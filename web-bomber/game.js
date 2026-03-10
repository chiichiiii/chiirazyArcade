(() => {
  // 타일/화면 기본 크기 설정값
  const TILE = 40;
  const MAP_W = 15;
  const MAP_H = 13;
  const HUD_W = 260;
  const WIDTH = MAP_W * TILE + HUD_W;
  const HEIGHT = MAP_H * TILE;

  // 게임 밸런스 관련 상수(속도, 폭탄 시간, 승리 조건)
  const BASE_SPEED = 4.0;
  const BOMB_FUSE = 2.0;
  const EXPLOSION_TIME = 0.35;
  const POWERUP_DROP = 0.25;
  const WIN_TARGET = 3;

  // 게임 진행 상태(시작/플레이/일시정지/라운드 종료/게임오버)
  const Phase = { START: "START", PLAYING: "PLAYING", PAUSED: "PAUSED", ROUND_OVER: "ROUND_OVER", GAME_OVER: "GAME_OVER" };
  const Mode = { P1_VS_P2: "P1_VS_P2", P1_VS_AI: "P1_VS_AI" };

  // 맵 타일/파워업/방향 타입 정의
  const Tile = { EMPTY: 0, SOLID: 1, BREAKABLE: 2, POWERUP: 3 };
  const Power = { BOMB_CAPACITY: "BOMB_CAPACITY", BOMB_RANGE: "BOMB_RANGE", SPEED: "SPEED" };
  const Facing = { DOWN: "DOWN", UP: "UP", LEFT: "LEFT", RIGHT: "RIGHT" };

  // Canvas 초기화: 여기서 화면에 모든 요소를 그린다
  const canvas = document.getElementById("game");
  const ctx = canvas.getContext("2d");
  canvas.width = WIDTH;
  canvas.height = HEIGHT;

  // 작은 유틸 함수들: 랜덤 정수, 좌표 키 문자열
  function randInt(n) { return Math.floor(Math.random() * n); }
  function key(x, y) { return `${x},${y}`; }

  // 이미지 후보 경로를 순서대로 시도하는 로더
  function loadImageSeq(paths) {
    const img = new Image();
    let i = 0;
    const tryNext = () => {
      if (i >= paths.length) return;
      img.src = paths[i++];
    };
    img.onerror = tryNext;
    tryNext();
    return img;
  }

  // 이미지가 정상 로드되었는지 체크
  function imageReady(img) {
    return img && img.complete && img.naturalWidth > 0;
  }

  // 캐릭터 8개 스프라이트(방향/걷기) 묶음을 생성
  function spriteSet(namePrefix) {
    return {
      downIdle: loadImageSeq([`assets/${namePrefix}.png`]),
      downWalk: loadImageSeq([`assets/${namePrefix}濾?png`]),
      upIdle: loadImageSeq([`assets/${namePrefix}??png`]),
      upWalk: loadImageSeq([`assets/${namePrefix}??룰퀗??png`]),
      leftIdle: loadImageSeq([`assets/${namePrefix}??png`]),
      leftWalk: loadImageSeq([`assets/${namePrefix}???ｋ뜪.png`]),
      rightIdle: loadImageSeq([`assets/${namePrefix}??png`]),
      rightWalk: loadImageSeq([`assets/${namePrefix}??釉뚮뜪.png`]),
    };
  }

  // 현재 방향/이동 상태에 맞는 스프라이트 프레임 선택
  function getSprite(set, facing, moving, animTime) {
    const walk = moving && (Math.floor(animTime * 8) % 2 === 1);
    if (facing === Facing.UP) return walk ? set.upWalk : set.upIdle;
    if (facing === Facing.LEFT) return walk ? set.leftWalk : set.leftIdle;
    if (facing === Facing.RIGHT) return walk ? set.rightWalk : set.rightIdle;
    return walk ? set.downWalk : set.downIdle;
  }

  // 실제 사용할 이미지 리소스 목록
  const sprites = {
    p1: spriteSet("??????怨룹꽑1"),
    p2: spriteSet("??????怨룹꽑2"),
    bot: spriteSet("AI"),
    bomb1: loadImageSeq(["assets/??繹?.png"]),
    bomb2: loadImageSeq(["assets/??繹?.png"]),
    explosion: loadImageSeq(["assets/??繹??⑥ъ뗀.png"]),
  };

  // 플레이어 기본 상태 객체 생성 함수(사람/봇 공통)
  function makePlayer(name, color, tx, ty, isBot = false) {
    return {
      name, color,
      spawnTx: tx, spawnTy: ty,
      x: tx + 0.5, y: ty + 0.5,
      speed: BASE_SPEED,
      bombCapacity: 1,
      bombRange: 1,
      lives: 3,
      wins: 0,
      alive: true,
      activeBombs: 0,
      invincible: 0,
      passableBombIds: new Set(),
      facing: Facing.DOWN,
      moving: false,
      animTime: 0,
      stepMoving: false,
      stepProgress: 0,
      fromX: tx + 0.5,
      fromY: ty + 0.5,
      toX: tx + 0.5,
      toY: ty + 0.5,
      isBot,
      thinkCooldown: 0,
      bombCooldown: 0,
      path: [],
    };
  }

  // 게임의 모든 실시간 상태를 모아두는 중앙 저장소
  const state = {
    map: Array.from({ length: MAP_H }, () => Array.from({ length: MAP_W }, () => Tile.EMPTY)),
    phase: Phase.START,
    mode: Mode.P1_VS_P2,
    menuIndex: 0,
    round: 1,
    winner: null,
    bombs: [],
    explosions: [],
    powerUps: [],
    nextBombId: 1,
    p1: makePlayer("P1", "#4884ff", 1, 1),
    p2: makePlayer("P2", "#ff5050", MAP_W - 2, MAP_H - 2),
    bot: makePlayer("BOT", "#46c55a", MAP_W - 2, 1, true),
  };

  // 키 입력 상태 버퍼(눌림 상태를 프레임 업데이트에서 사용)
  const input = {
    p1Up: false, p1Down: false, p1Left: false, p1Right: false,
    p2Up: false, p2Down: false, p2Left: false, p2Right: false,
    p1Bomb: false, p2Bomb: false,
  };

  // 현재 모드에 따라 실제 참가자 목록 반환
  function allPlayers() {
    return state.mode === Mode.P1_VS_P2 ? [state.p1, state.p2] : [state.p1, state.bot];
  }

  // 라운드 시작 시 플레이어 상태 초기화
  function resetPlayer(p) {
    p.x = p.spawnTx + 0.5;
    p.y = p.spawnTy + 0.5;
    p.speed = BASE_SPEED;
    p.bombCapacity = 1;
    p.bombRange = 1;
    p.lives = 3;
    p.alive = true;
    p.activeBombs = 0;
    p.invincible = 0;
    p.passableBombIds.clear();
    p.facing = Facing.DOWN;
    p.moving = false;
    p.animTime = 0;
    p.stepMoving = false;
    p.stepProgress = 0;
    p.fromX = p.x;
    p.fromY = p.y;
    p.toX = p.x;
    p.toY = p.y;
    p.path = [];
    p.thinkCooldown = 0;
    p.bombCooldown = 0;
  }

  // 맵 접근 유틸: 범위 검사 / 조회 / 설정
  function inBounds(x, y) { return x >= 0 && y >= 0 && x < MAP_W && y < MAP_H; }
  function tileAt(x, y) { return inBounds(x, y) ? state.map[y][x] : Tile.SOLID; }
  function setTile(x, y, v) { if (inBounds(x, y)) state.map[y][x] = v; }

  function tileXY(p) {
    return { x: Math.floor(p.x), y: Math.floor(p.y) };
  }

  // 랜덤 맵 생성: 고정벽 + 브레이커블 + 스폰 안전지대
  function generateMap() {
    const safe = new Set();
    const spawns = [state.p1, state.p2, state.bot];
    for (const s of spawns) {
      for (let y = 0; y < MAP_H; y++) {
        for (let x = 0; x < MAP_W; x++) {
          if (Math.abs(x - s.spawnTx) + Math.abs(y - s.spawnTy) <= 2) safe.add(key(x, y));
        }
      }
    }

    for (let y = 0; y < MAP_H; y++) {
      for (let x = 0; x < MAP_W; x++) {
        if (x === 0 || y === 0 || x === MAP_W - 1 || y === MAP_H - 1) { setTile(x, y, Tile.SOLID); continue; }
        if (x % 2 === 0 && y % 2 === 0) { setTile(x, y, Tile.SOLID); continue; }
        setTile(x, y, safe.has(key(x, y)) ? Tile.EMPTY : (Math.random() < 0.62 ? Tile.BREAKABLE : Tile.EMPTY));
      }
    }
  }

  // 새 매치 시작(승수 초기화)
  function startMatch() {
    state.p1.wins = 0;
    state.p2.wins = 0;
    state.bot.wins = 0;
    state.round = 1;
    startRound();
  }

  // 새 라운드 시작(오브젝트/플레이어 상태 리셋)
  function startRound() {
    state.bombs.length = 0;
    state.explosions.length = 0;
    state.powerUps.length = 0;
    state.nextBombId = 1;
    state.winner = null;
    generateMap();

    resetPlayer(state.p1);
    resetPlayer(state.p2);
    resetPlayer(state.bot);

    if (state.mode === Mode.P1_VS_P2) state.bot.alive = false;
    else state.p2.alive = false;

    state.phase = Phase.PLAYING;
  }

  // 특정 타일의 폭탄 조회
  function bombAt(tx, ty) {
    return state.bombs.find(b => b.tx === tx && b.ty === ty) || null;
  }

  // 플레이어가 해당 타일로 이동 가능한지 판정
  function canEnterTile(player, tx, ty) {
    if (!inBounds(tx, ty)) return false;
    const t = tileAt(tx, ty);
    if (t === Tile.SOLID || t === Tile.BREAKABLE) return false;
    const b = bombAt(tx, ty);
    return !b || player.passableBombIds.has(b.id);
  }

  // 플레이어 충돌 박스가 폭탄 타일과 겹치는지 검사
  function overlapsBombTile(player, tx, ty) {
    const half = 0.26;
    const minX = player.x - half, maxX = player.x + half;
    const minY = player.y - half, maxY = player.y + half;
    return minX < tx + 1 && maxX > tx && minY < ty + 1 && maxY > ty;
  }

  // 폭탄 통과 예외 규칙 갱신
  function updateBombPass(player) {
    for (const id of [...player.passableBombIds]) {
      const b = state.bombs.find(x => x.id === id);
      if (!b || !overlapsBombTile(player, b.tx, b.ty)) player.passableBombIds.delete(id);
    }
  }

  // 타일 단위 이동: 한 칸 목표를 정하고 보간으로 이동
  function tryMove(player, dirX, dirY, dt) {
    if (!player.alive) return;

    if (player.stepMoving) {
      player.animTime += dt;
      player.stepProgress += player.speed * dt;
      if (player.stepProgress >= 1) {
        player.stepProgress = 1;
        player.x = player.toX;
        player.y = player.toY;
        player.stepMoving = false;
      } else {
        player.x = player.fromX + (player.toX - player.fromX) * player.stepProgress;
        player.y = player.fromY + (player.toY - player.fromY) * player.stepProgress;
      }
      return;
    }

    let sx = 0, sy = 0;
    if (Math.abs(dirX) > Math.abs(dirY)) sx = dirX > 0.1 ? 1 : (dirX < -0.1 ? -1 : 0);
    else sy = dirY > 0.1 ? 1 : (dirY < -0.1 ? -1 : 0);

    if (sx === 0 && sy === 0) {
      player.moving = false;
      player.animTime = 0;
      return;
    }

    if (sx < 0) player.facing = Facing.LEFT;
    else if (sx > 0) player.facing = Facing.RIGHT;
    else if (sy < 0) player.facing = Facing.UP;
    else player.facing = Facing.DOWN;

    const cur = tileXY(player);
    const nx = cur.x + sx;
    const ny = cur.y + sy;
    if (!canEnterTile(player, nx, ny)) {
      player.moving = false;
      player.animTime = 0;
      return;
    }

    player.stepMoving = true;
    player.stepProgress = 0;
    player.fromX = player.x;
    player.fromY = player.y;
    player.toX = nx + 0.5;
    player.toY = ny + 0.5;
    player.moving = true;

    tryMove(player, 0, 0, dt);
  }

  // 폭탄 설치 처리(용량/타일/중복 검사 포함)
  function placeBomb(player, tx, ty) {
    if (state.phase !== Phase.PLAYING || !player.alive) return false;
    if (player.activeBombs >= player.bombCapacity) return false;
    if (!inBounds(tx, ty)) return false;
    const t = tileAt(tx, ty);
    if (t !== Tile.EMPTY && t !== Tile.POWERUP) return false;
    if (bombAt(tx, ty)) return false;

    const b = { id: state.nextBombId++, tx, ty, owner: player, fuse: BOMB_FUSE };
    state.bombs.push(b);
    player.activeBombs++;
    player.passableBombIds.add(b.id);
    return true;
  }

  // 파워업 생성(강제 타입 또는 랜덤 타입)
  function spawnPowerUp(tx, ty, forced = null) {
    removePowerUpAt(tx, ty);
    const r = Math.random();
    const type = forced || (r < 0.34 ? Power.BOMB_CAPACITY : (r < 0.67 ? Power.BOMB_RANGE : Power.SPEED));
    setTile(tx, ty, Tile.POWERUP);
    state.powerUps.push({ tx, ty, type });
  }

  // 특정 위치 파워업 제거
  function removePowerUpAt(tx, ty) {
    const i = state.powerUps.findIndex(p => p.tx === tx && p.ty === ty);
    if (i >= 0) state.powerUps.splice(i, 1);
  }

  // 폭발 전파 셀 계산: 중심 + 4방향
  function calcExplosionCells(ox, oy, range) {
    const cells = [{ x: ox, y: oy }];
    const dirs = [[1,0],[-1,0],[0,1],[0,-1]];
    for (const [dx, dy] of dirs) {
      for (let i = 1; i <= range; i++) {
        const nx = ox + dx * i;
        const ny = oy + dy * i;
        const t = tileAt(nx, ny);
        if (t === Tile.SOLID) break;
        cells.push({ x: nx, y: ny });
        if (t === Tile.BREAKABLE) break;
      }
    }
    return cells;
  }

  // 폭탄 폭발 처리: 연쇄 폭발, 블록 파괴, 파워업 처리
  function detonateBomb(bomb, visited = new Set()) {
    if (visited.has(bomb.id)) return;
    visited.add(bomb.id);

    const idx = state.bombs.indexOf(bomb);
    if (idx < 0) return;
    state.bombs.splice(idx, 1);
    bomb.owner.activeBombs = Math.max(0, bomb.owner.activeBombs - 1);

    const cells = calcExplosionCells(bomb.tx, bomb.ty, bomb.owner.bombRange);
    state.explosions.push({ cells, time: EXPLOSION_TIME });

    for (const c of cells) {
      const t = tileAt(c.x, c.y);
      if (t === Tile.BREAKABLE) {
        setTile(c.x, c.y, Tile.EMPTY);
        if (Math.random() < POWERUP_DROP) spawnPowerUp(c.x, c.y);
      } else if (t === Tile.POWERUP) {
        removePowerUpAt(c.x, c.y);
        setTile(c.x, c.y, Tile.EMPTY);
      }

      const other = bombAt(c.x, c.y);
      if (other) detonateBomb(other, visited);
    }
  }

  // 위험 타일 집합 계산(현재 폭발 + 예정 폭발)
  function getDangerCells(extra = []) {
    const s = new Set();
    for (const e of state.explosions) for (const c of e.cells) s.add(key(c.x, c.y));
    for (const b of state.bombs) for (const c of calcExplosionCells(b.tx, b.ty, b.owner.bombRange)) s.add(key(c.x, c.y));
    for (const c of extra) s.add(key(c.x, c.y));
    return s;
  }

  // BFS 최단경로 탐색 유틸
  function bfsPath(start, goalFn, passableFn) {
    const q = [start];
    const vis = new Set([key(start.x, start.y)]);
    const prev = new Map();

    while (q.length) {
      const cur = q.shift();
      if (goalFn(cur.x, cur.y)) {
        const path = [];
        let k = key(cur.x, cur.y);
        while (k) {
          const [sx, sy] = k.split(",").map(Number);
          path.unshift({ x: sx, y: sy });
          k = prev.get(k) || null;
        }
        return path;
      }
      const dirs = [[1,0],[-1,0],[0,1],[0,-1]];
      for (const [dx, dy] of dirs) {
        const nx = cur.x + dx, ny = cur.y + dy;
        const nk = key(nx, ny);
        if (vis.has(nk) || !passableFn(nx, ny)) continue;
        vis.add(nk);
        prev.set(nk, key(cur.x, cur.y));
        q.push({ x: nx, y: ny });
      }
    }
    return [];
  }

  // 봇이 지금 폭탄 설치해도 탈출 가능한지 미리 검사
  function canEscapeIfPlaceBomb(bot, tx, ty) {
    const virtual = calcExplosionCells(tx, ty, bot.bombRange);
    const danger = getDangerCells(virtual);
    const st = tileXY(bot);
    const p = bfsPath(
      st,
      (x, y) => !danger.has(key(x, y)) && (tileAt(x, y) === Tile.EMPTY || tileAt(x, y) === Tile.POWERUP),
      (x, y) => {
        if (!inBounds(x, y)) return false;
        const t = tileAt(x, y);
        if (t === Tile.SOLID || t === Tile.BREAKABLE) return false;
        const b = bombAt(x, y);
        if (!b) return true;
        if (x === tx && y === ty) return true;
        return bot.passableBombIds.has(b.id);
      }
    );
    return p.length > 1;
  }

  // 가장 가까운 적 탐색
  function nearestEnemy(from) {
    let best = null;
    let dist = 1e9;
    for (const p of allPlayers()) {
      if (p === from || !p.alive) continue;
      const a = tileXY(from);
      const b = tileXY(p);
      const d = Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
      if (d < dist) { dist = d; best = p; }
    }
    return best;
  }

  // 두 좌표가 같은 행/열일 때 중간 장애물 확인
  function isLineClear(sx, sy, ex, ey) {
    if (sx !== ex && sy !== ey) return false;
    const dx = Math.sign(ex - sx), dy = Math.sign(ey - sy);
    let x = sx + dx, y = sy + dy;
    while (x !== ex || y !== ey) {
      const t = tileAt(x, y);
      if (t === Tile.SOLID || t === Tile.BREAKABLE) return false;
      x += dx; y += dy;
    }
    return true;
  }

  // 주변 1칸에 브레이커블 벽이 있는지 검사
  function hasAdjacentBreakable(tx, ty) {
    return [[1,0],[-1,0],[0,1],[0,-1]].some(([dx,dy]) => tileAt(tx + dx, ty + dy) === Tile.BREAKABLE);
  }

  // 봇 AI 메인 루프: 회피 -> 공격 판단 -> 이동
  function botUpdate(bot, dt) {
    if (!bot.alive) return;

    bot.thinkCooldown -= dt;
    bot.bombCooldown -= dt;
    const me = tileXY(bot);
    const danger = getDangerCells();

    if (danger.has(key(me.x, me.y))) {
      bot.path = bfsPath(
        me,
        (x, y) => !danger.has(key(x, y)) && (tileAt(x, y) === Tile.EMPTY || tileAt(x, y) === Tile.POWERUP),
        (x, y) => canEnterTile(bot, x, y)
      );
    } else if (bot.bombCooldown <= 0 && bot.activeBombs < bot.bombCapacity) {
      const enemy = nearestEnemy(bot);
      if (enemy) {
        const e = tileXY(enemy);
        const attack = ((me.x === e.x && Math.abs(me.y - e.y) <= bot.bombRange && isLineClear(me.x, me.y, e.x, e.y)) ||
                       (me.y === e.y && Math.abs(me.x - e.x) <= bot.bombRange && isLineClear(me.x, me.y, e.x, e.y)) ||
                       hasAdjacentBreakable(me.x, me.y));
        if (attack && canEscapeIfPlaceBomb(bot, me.x, me.y) && placeBomb(bot, me.x, me.y)) {
          bot.bombCooldown = 0.7;
        }
      }
    }

    if (bot.thinkCooldown <= 0 || bot.path.length <= 1) {
      const enemy = nearestEnemy(bot);
      if (enemy) {
        const e = tileXY(enemy);
        bot.path = bfsPath(me, (x, y) => x === e.x && y === e.y, (x, y) => canEnterTile(bot, x, y) && !danger.has(key(x, y)));
      }
      if (bot.path.length <= 1) {
        bot.path = bfsPath(me, (x, y) => Math.random() < 0.05 && (tileAt(x, y) === Tile.EMPTY || tileAt(x, y) === Tile.POWERUP), (x, y) => canEnterTile(bot, x, y));
      }
      bot.thinkCooldown = 0.25;
    }

    if (bot.path.length > 1) {
      if (me.x === bot.path[1].x && me.y === bot.path[1].y) bot.path.shift();
      if (bot.path.length > 1) {
        const n = bot.path[1];
        tryMove(bot, Math.sign(n.x - me.x), Math.sign(n.y - me.y), dt);
      }
    }
  }

  // 탈락 시 먹었던 강화 아이템을 무작위 위치에 다시 드랍
  function dropItemsOnDeath(player) {
    const drops = [];
    const extraCap = Math.max(0, player.bombCapacity - 1);
    const extraRange = Math.max(0, player.bombRange - 1);
    const extraSpeed = Math.max(0, Math.round((player.speed - BASE_SPEED) / 0.55));

    for (let i = 0; i < extraCap; i++) drops.push(Power.BOMB_CAPACITY);
    for (let i = 0; i < extraRange; i++) drops.push(Power.BOMB_RANGE);
    for (let i = 0; i < extraSpeed; i++) drops.push(Power.SPEED);

    for (let i = drops.length - 1; i > 0; i--) {
      const j = randInt(i + 1);
      [drops[i], drops[j]] = [drops[j], drops[i]];
    }

    for (const type of drops) {
      const spots = [];
      for (let y = 1; y < MAP_H - 1; y++) {
        for (let x = 1; x < MAP_W - 1; x++) {
          if (tileAt(x, y) !== Tile.EMPTY) continue;
          if (bombAt(x, y)) continue;
          if (state.powerUps.some(p => p.tx === x && p.ty === y)) continue;
          const hot = state.explosions.some(e => e.cells.some(c => c.x === x && c.y === y));
          if (!hot) spots.push({ x, y });
        }
      }
      if (!spots.length) break;
      const p = spots[randInt(spots.length)];
      spawnPowerUp(p.x, p.y, type);
    }
  }

  // 플레이어 피격 처리(무적/리스폰/탈락)
  function hitPlayer(player) {
    if (!player.alive || player.invincible > 0) return;
    player.lives -= 1;
    if (player.lives <= 0) {
      player.alive = false;
      dropItemsOnDeath(player);
      return;
    }
    player.invincible = 1.2;
    player.x = player.spawnTx + 0.5;
    player.y = player.spawnTy + 0.5;
    player.stepMoving = false;
  }

  // 매 프레임 게임 로직 업데이트
  function update(dt) {
    if (state.phase !== Phase.PLAYING) return;

    for (const p of allPlayers()) {
      p.invincible = Math.max(0, p.invincible - dt);
      updateBombPass(p);
    }

    tryMove(state.p1, (input.p1Right ? 1 : 0) - (input.p1Left ? 1 : 0), (input.p1Down ? 1 : 0) - (input.p1Up ? 1 : 0), dt);

    if (state.mode === Mode.P1_VS_P2) {
      tryMove(state.p2, (input.p2Right ? 1 : 0) - (input.p2Left ? 1 : 0), (input.p2Down ? 1 : 0) - (input.p2Up ? 1 : 0), dt);
    } else {
      botUpdate(state.bot, dt);
    }

    if (input.p1Bomb) {
      const t = tileXY(state.p1);
      placeBomb(state.p1, t.x, t.y);
      input.p1Bomb = false;
    }
    if (input.p2Bomb && state.mode === Mode.P1_VS_P2) {
      const t = tileXY(state.p2);
      placeBomb(state.p2, t.x, t.y);
      input.p2Bomb = false;
    }

    const toExplode = [];
    for (const b of state.bombs) {
      b.fuse -= dt;
      if (b.fuse <= 0) toExplode.push(b);
    }
    for (const b of toExplode) if (state.bombs.includes(b)) detonateBomb(b);

    for (let i = state.explosions.length - 1; i >= 0; i--) {
      state.explosions[i].time -= dt;
      if (state.explosions[i].time <= 0) state.explosions.splice(i, 1);
    }

    for (const p of allPlayers()) {
      if (!p.alive || p.invincible > 0) continue;
      const t = tileXY(p);
      if (state.explosions.some(e => e.cells.some(c => c.x === t.x && c.y === t.y))) hitPlayer(p);
    }

    for (const p of allPlayers()) {
      if (!p.alive) continue;
      const t = tileXY(p);
      const idx = state.powerUps.findIndex(u => u.tx === t.x && u.ty === t.y);
      if (idx >= 0) {
        const u = state.powerUps[idx];
        if (u.type === Power.BOMB_CAPACITY) p.bombCapacity = Math.min(5, p.bombCapacity + 1);
        else if (u.type === Power.BOMB_RANGE) p.bombRange = Math.min(8, p.bombRange + 1);
        else p.speed = Math.min(7, p.speed + 0.55);
        state.powerUps.splice(idx, 1);
        setTile(t.x, t.y, Tile.EMPTY);
      }
    }

    const alive = allPlayers().filter(p => p.alive);
    if (alive.length <= 1) {
      state.winner = alive[0] || null;
      if (state.winner) state.winner.wins += 1;
      if (state.p1.wins >= WIN_TARGET || state.p2.wins >= WIN_TARGET || state.bot.wins >= WIN_TARGET) state.phase = Phase.GAME_OVER;
      else state.phase = Phase.ROUND_OVER;
    }
  }

  // 렌더링: 타일 한 칸 그리기
  function drawTile(x, y, t) {
    if (t === Tile.EMPTY || t === Tile.POWERUP) ctx.fillStyle = "#e1e6d8";
    else if (t === Tile.SOLID) ctx.fillStyle = "#505050";
    else ctx.fillStyle = "#96643c";
    ctx.fillRect(x * TILE, y * TILE, TILE, TILE);
    ctx.strokeStyle = "rgba(0,0,0,.12)";
    ctx.strokeRect(x * TILE, y * TILE, TILE, TILE);
  }

  // 렌더링: 맵 전체
  function drawMap() {
    for (let y = 0; y < MAP_H; y++) for (let x = 0; x < MAP_W; x++) drawTile(x, y, tileAt(x, y));
  }

  // 렌더링: 파워업
  function drawPowerUps() {
    ctx.font = "bold 14px Segoe UI";
    for (const p of state.powerUps) {
      const px = p.tx * TILE, py = p.ty * TILE;
      ctx.fillStyle = p.type === Power.BOMB_CAPACITY ? "#53a3f7" : (p.type === Power.BOMB_RANGE ? "#ffa640" : "#57ce65");
      ctx.beginPath();
      ctx.arc(px + TILE / 2, py + TILE / 2, TILE * 0.32, 0, Math.PI * 2);
      ctx.fill();
      ctx.fillStyle = "#111";
      const txt = p.type === Power.BOMB_CAPACITY ? "B+" : (p.type === Power.BOMB_RANGE ? "R+" : "S+");
      ctx.fillText(txt, px + 11, py + TILE - 12);
    }
  }

  // 렌더링: 폭탄(이미지 없으면 도형 대체)
  function drawBombs() {
    for (const b of state.bombs) {
      const px = b.tx * TILE, py = b.ty * TILE;
      const frame = (Math.floor((BOMB_FUSE - Math.max(0, b.fuse)) * 8) % 2 === 0) ? sprites.bomb1 : sprites.bomb2;
      if (imageReady(frame)) {
        const size = Math.floor(TILE * 0.84);
        const dx = px + (TILE - size) / 2;
        const dy = py + (TILE - size) / 2;
        ctx.drawImage(frame, dx, dy, size, size);
      } else {
        ctx.fillStyle = "#000";
        ctx.beginPath();
        ctx.arc(px + TILE / 2, py + TILE / 2, TILE * 0.34, 0, Math.PI * 2);
        ctx.fill();
        ctx.fillStyle = "#fff";
        ctx.beginPath();
        ctx.arc(px + TILE * 0.45, py + TILE * 0.33, 3, 0, Math.PI * 2);
        ctx.fill();
      }
    }
  }

  // 렌더링: 폭발(타일당 1장)
  function drawExplosions() {
    for (const e of state.explosions) {
      for (const c of e.cells) {
        const px = c.x * TILE, py = c.y * TILE;
        if (imageReady(sprites.explosion)) {
          ctx.drawImage(sprites.explosion, px, py, TILE, TILE);
        } else {
          ctx.fillStyle = "rgba(255,140,35,.82)";
          ctx.fillRect(px + 2, py + 2, TILE - 4, TILE - 4);
          ctx.fillStyle = "rgba(255,228,110,.82)";
          ctx.fillRect(px + 10, py + 10, TILE - 20, TILE - 20);
        }
      }
    }
  }

  // 렌더링: 플레이어(무적 깜빡임 포함)
  function drawPlayer(p, spriteSetObj) {
    if (!p.alive) return;
    if (p.invincible > 0 && Math.floor(performance.now() / 90) % 2 === 1) return;

    const cx = Math.round(p.x * TILE);
    const cy = Math.round(p.y * TILE);
    const sprite = getSprite(spriteSetObj, p.facing, p.moving, p.animTime);

    if (imageReady(sprite)) {
      const sw = sprite.naturalWidth;
      const sh = sprite.naturalHeight;
      const h = Math.floor(TILE * 1.45);
      const w = Math.max(TILE, Math.min(Math.floor(TILE * 1.9), Math.round(sw / sh * h)));
      ctx.drawImage(sprite, cx - w / 2, cy - h / 2, w, h);
    } else {
      const r = TILE / 2 - 6;
      ctx.fillStyle = p.color;
      ctx.beginPath();
      ctx.arc(cx, cy, r, 0, Math.PI * 2);
      ctx.fill();
      ctx.strokeStyle = "#111";
      ctx.stroke();
    }
  }

  // 렌더링: HUD의 플레이어 정보 블록
  function drawHudPlayer(p, x, y) {
    ctx.fillStyle = p.color;
    ctx.fillRect(x, y - 16, 16, 16);
    ctx.fillStyle = "#fff";
    ctx.font = "bold 15px Segoe UI";
    ctx.fillText(p.name, x + 24, y - 2);
    ctx.font = "14px Segoe UI";
    ctx.fillText(`Lives: ${p.lives}   Wins: ${p.wins}`, x, y + 22);
    ctx.fillText(`Bomb: ${p.bombCapacity}  Range: ${p.bombRange}`, x, y + 42);
    ctx.fillText(`Speed: ${p.speed.toFixed(1)}`, x, y + 62);
  }

  // 렌더링: 우측 HUD 전체
  function drawHud() {
    const left = MAP_W * TILE;
    ctx.fillStyle = "#202534";
    ctx.fillRect(left, 0, HUD_W, HEIGHT);
    ctx.strokeStyle = "#50648c";
    ctx.lineWidth = 2;
    ctx.beginPath();
    ctx.moveTo(left, 0);
    ctx.lineTo(left, HEIGHT);
    ctx.stroke();

    ctx.fillStyle = "#f0f5ff";
    ctx.font = "bold 20px Segoe UI";
    ctx.fillText("Arcade Bomber", left + 24, 34);

    ctx.font = "15px Segoe UI";
    ctx.fillText(`Round: ${state.round}`, left + 24, 66);
    ctx.fillText(`Mode: ${state.mode === Mode.P1_VS_P2 ? "P1 vs P2" : "P1 vs AI"}`, left + 24, 88);
    ctx.fillText(`State: ${state.phase}`, left + 24, 110);

    drawHudPlayer(state.p1, left + 24, 155);
    drawHudPlayer(state.mode === Mode.P1_VS_P2 ? state.p2 : state.bot, left + 24, 260);

    ctx.fillStyle = "#cdd7e8";
    ctx.font = "13px Segoe UI";
    ctx.fillText("P1: WASD + SPACE", left + 24, HEIGHT - 88);
    ctx.fillText("P2: ARROW + ENTER", left + 24, HEIGHT - 68);
    ctx.fillText("P: pause, R: restart", left + 24, HEIGHT - 48);
    ctx.fillText("ENTER: next/start", left + 24, HEIGHT - 28);
  }

  // 렌더링: 시작/일시정지/라운드 종료 오버레이
  function drawOverlay() {
    let lines = [];
    if (state.phase === Phase.START) {
      lines = [
        "Arcade Bomber (Web)",
        `${state.menuIndex === 0 ? ">" : " "} 1. P1 vs P2`,
        `${state.menuIndex === 1 ? ">" : " "} 2. P1 vs AI`,
        "ENTER: ??戮곗굚 / ESC: ?貫?껆뵳??
      ];
    } else if (state.phase === Phase.PAUSED) {
      lines = ["Paused", "P: Resume"]; 
    } else if (state.phase === Phase.ROUND_OVER) {
      lines = [state.winner ? `Winner: ${state.winner.name}` : "Draw", "ENTER: ???깅쾳 ??源녿뮧??, "R: ?????]; 
    } else if (state.phase === Phase.GAME_OVER) {
      let champ = state.p1;
      if (state.p2.wins > champ.wins) champ = state.p2;
      if (state.bot.wins > champ.wins) champ = state.bot;
      lines = [`Champion: ${champ.name}`, "R: ??嶺뚮씞???, "ENTER: ??嶺뚮씞???]; 
    }

    if (!lines.length) return;
    const w = MAP_W * TILE;
    const h = MAP_H * TILE;
    ctx.fillStyle = "rgba(0,0,0,.55)";
    ctx.fillRect(0, 0, w, h);
    ctx.fillStyle = "#fff";
    ctx.font = "bold 30px Segoe UI";
    ctx.textAlign = "center";
    ctx.fillText(lines[0], w / 2, h / 2 - 50);
    ctx.font = "22px Segoe UI";
    if (lines[1]) ctx.fillText(lines[1], w / 2, h / 2 - 10);
    if (lines[2]) ctx.fillText(lines[2], w / 2, h / 2 + 24);
    if (lines[3]) ctx.fillText(lines[3], w / 2, h / 2 + 58);
    ctx.textAlign = "start";
  }

  // 한 프레임 렌더링 순서 제어
  function render() {
    ctx.clearRect(0, 0, WIDTH, HEIGHT);
    drawMap();
    drawPowerUps();
    drawBombs();
    drawExplosions();
    drawPlayer(state.p1, sprites.p1);
    if (state.mode === Mode.P1_VS_P2) drawPlayer(state.p2, sprites.p2);
    else drawPlayer(state.bot, sprites.bot);
    drawHud();
    drawOverlay();
  }

  // 일시정지 토글
  function togglePause() {
    if (state.phase === Phase.PLAYING) state.phase = Phase.PAUSED;
    else if (state.phase === Phase.PAUSED) state.phase = Phase.PLAYING;
  }

  // ENTER 키 동작 처리
  function onEnter() {
    if (state.phase === Phase.START) {
      state.mode = state.menuIndex === 0 ? Mode.P1_VS_P2 : Mode.P1_VS_AI;
      startMatch();
      return;
    }
    if (state.phase === Phase.ROUND_OVER) {
      state.round += 1;
      startRound();
      return;
    }
    if (state.phase === Phase.GAME_OVER) {
      startMatch();
      return;
    }
    if (state.phase === Phase.PLAYING && state.mode === Mode.P1_VS_P2) {
      input.p2Bomb = true;
    }
  }

  // R 키 재시작 처리
  function onRestart() {
    if (state.phase === Phase.START) return;
    startMatch();
  }

  // 키다운/키업 이벤트를 입력 버퍼에 반영
  function mapKeyDown(e, down) {
    const k = e.key.toLowerCase();

    if (["arrowup", "arrowdown", "arrowleft", "arrowright", " ", "enter"].includes(k)) e.preventDefault();

    if (!down) {
      if (k === "w") input.p1Up = false;
      else if (k === "s") input.p1Down = false;
      else if (k === "a") input.p1Left = false;
      else if (k === "d") input.p1Right = false;
      else if (k === "arrowup") input.p2Up = false;
      else if (k === "arrowdown") input.p2Down = false;
      else if (k === "arrowleft") input.p2Left = false;
      else if (k === "arrowright") input.p2Right = false;
      return;
    }

    if (k === "w") input.p1Up = true;
    else if (k === "s") input.p1Down = true;
    else if (k === "a") input.p1Left = true;
    else if (k === "d") input.p1Right = true;
    else if (k === "arrowup") input.p2Up = true;
    else if (k === "arrowdown") input.p2Down = true;
    else if (k === "arrowleft") input.p2Left = true;
    else if (k === "arrowright") input.p2Right = true;
    else if (k === " ") input.p1Bomb = true;
    else if (k === "enter") onEnter();
    else if (k === "p") togglePause();
    else if (k === "r") onRestart();
    else if (k === "1" && state.phase === Phase.START) state.menuIndex = 0;
    else if (k === "2" && state.phase === Phase.START) state.menuIndex = 1;
    else if (k === "escape") {
      state.phase = Phase.START;
      state.menuIndex = 0;
    }
  }

  window.addEventListener("keydown", e => mapKeyDown(e, true));
  window.addEventListener("keyup", e => mapKeyDown(e, false));

  let last = performance.now();
  // requestAnimationFrame 메인 루프
  function loop(now) {
    let dt = (now - last) / 1000;
    if (dt > 0.05) dt = 0.05;
    last = now;

    update(dt);
    render();
    requestAnimationFrame(loop);
  }

  requestAnimationFrame(loop);
})();
