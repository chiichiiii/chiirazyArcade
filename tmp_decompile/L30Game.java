package com.smu8.game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class L30Game {
   public static void main(String[] args) {
      SwingUtilities.invokeLater(() -> {
         JFrame frame = new JFrame("Arcade Bomber");
         frame.setDefaultCloseOperation(3);
         frame.setResizable(false);
         frame.setContentPane(new GamePanel());
         frame.pack();
         frame.setLocationRelativeTo((Component)null);
         frame.setVisible(true);
      });
   }

   static enum TileType {
      EMPTY,
      SOLID,
      BREAKABLE,
      POWERUP;

      // $FF: synthetic method
      private static TileType[] $values() {
         return new TileType[]{EMPTY, SOLID, BREAKABLE, POWERUP};
      }
   }

   static enum PowerUpType {
      BOMB_CAPACITY,
      BOMB_RANGE,
      SPEED;

      // $FF: synthetic method
      private static PowerUpType[] $values() {
         return new PowerUpType[]{BOMB_CAPACITY, BOMB_RANGE, SPEED};
      }
   }

   static enum Facing {
      DOWN,
      UP,
      LEFT,
      RIGHT;

      // $FF: synthetic method
      private static Facing[] $values() {
         return new Facing[]{DOWN, UP, LEFT, RIGHT};
      }
   }

   static class Bomb {
      final int id;
      final int tx;
      final int ty;
      final Player owner;
      double fuse;

      Bomb(int id, int tx, int ty, Player owner, double fuse) {
         this.id = id;
         this.tx = tx;
         this.ty = ty;
         this.owner = owner;
         this.fuse = fuse;
      }
   }

   static class Explosion {
      final Set cells = new HashSet();
      double timeLeft;

      Explosion(double duration) {
         this.timeLeft = duration;
      }

      boolean contains(int tx, int ty) {
         return this.cells.contains(new Point(tx, ty));
      }
   }

   static class PowerUp {
      final int tx;
      final int ty;
      final PowerUpType type;

      PowerUp(int tx, int ty, PowerUpType type) {
         this.tx = tx;
         this.ty = ty;
         this.type = type;
      }

      Color color() {
         Color var10000;
         switch (this.type.ordinal()) {
            case 0 -> var10000 = new Color(83, 163, 247);
            case 1 -> var10000 = new Color(255, 166, 64);
            case 2 -> var10000 = new Color(87, 206, 101);
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      String shortName() {
         String var10000;
         switch (this.type.ordinal()) {
            case 0 -> var10000 = "B+";
            case 1 -> var10000 = "R+";
            case 2 -> var10000 = "S+";
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }
   }

   static class SpriteSet {
      final Image downIdle;
      final Image downWalk;
      final Image upIdle;
      final Image upWalk;
      final Image leftIdle;
      final Image leftWalk;
      final Image rightIdle;
      final Image rightWalk;

      SpriteSet(Image downIdle, Image downWalk, Image upIdle, Image upWalk, Image leftIdle, Image leftWalk, Image rightIdle, Image rightWalk) {
         this.downIdle = downIdle;
         this.downWalk = downWalk;
         this.upIdle = upIdle;
         this.upWalk = upWalk;
         this.leftIdle = leftIdle;
         this.leftWalk = leftWalk;
         this.rightIdle = rightIdle;
         this.rightWalk = rightWalk;
      }

      Image get(Facing facing, boolean moving, double animTime) {
         boolean walkFrame = moving && (int)(animTime * (double)8.0F) % 2 == 1;
         Image var10000;
         switch (facing.ordinal()) {
            case 0 -> var10000 = walkFrame && this.downWalk != null ? this.downWalk : this.downIdle;
            case 1 -> var10000 = walkFrame && this.upWalk != null ? this.upWalk : this.upIdle;
            case 2 -> var10000 = walkFrame && this.leftWalk != null ? this.leftWalk : this.leftIdle;
            case 3 -> var10000 = walkFrame && this.rightWalk != null ? this.rightWalk : this.rightIdle;
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }
   }

   static class TileMap {
      final int width;
      final int height;
      final TileType[][] tiles;
      final int[][] breakableVariants;

      TileMap(int width, int height) {
         this.width = width;
         this.height = height;
         this.tiles = new TileType[height][width];
         this.breakableVariants = new int[height][width];

         for(int y = 0; y < height; ++y) {
            Arrays.fill(this.breakableVariants[y], -1);
         }

      }

      void generate(Random random, Point... safeSpawns) {
         Set<Point> safe = new HashSet();

         for(Point spawn : safeSpawns) {
            for(int y = 0; y < this.height; ++y) {
               for(int x = 0; x < this.width; ++x) {
                  if (Math.abs(x - spawn.x) + Math.abs(y - spawn.y) <= 2) {
                     safe.add(new Point(x, y));
                  }
               }
            }
         }

         for(int y = 0; y < this.height; ++y) {
            for(int x = 0; x < this.width; ++x) {
               if (x != 0 && y != 0 && x != this.width - 1 && y != this.height - 1) {
                  if (x % 2 == 0 && y % 2 == 0) {
                     this.breakableVariants[y][x] = -1;
                     this.tiles[y][x] = L30Game.TileType.SOLID;
                  } else if (safe.contains(new Point(x, y))) {
                     this.tiles[y][x] = L30Game.TileType.EMPTY;
                     this.breakableVariants[y][x] = -1;
                  } else if (random.nextDouble() < 0.62) {
                     this.tiles[y][x] = L30Game.TileType.BREAKABLE;
                     this.breakableVariants[y][x] = random.nextInt(5);
                  } else {
                     this.tiles[y][x] = L30Game.TileType.EMPTY;
                     this.breakableVariants[y][x] = -1;
                  }
               } else {
                  this.breakableVariants[y][x] = -1;
                  this.tiles[y][x] = L30Game.TileType.SOLID;
               }
            }
         }

         for(Point spawn : safeSpawns) {
            if (this.inBounds(spawn.x, spawn.y)) {
               this.tiles[spawn.y][spawn.x] = L30Game.TileType.EMPTY;
               this.breakableVariants[spawn.y][spawn.x] = -1;
            }
         }

      }

      boolean inBounds(int x, int y) {
         return x >= 0 && y >= 0 && x < this.width && y < this.height;
      }

      TileType get(int x, int y) {
         if (!this.inBounds(x, y)) {
            return L30Game.TileType.SOLID;
         } else {
            TileType t = this.tiles[y][x];
            return t == null ? L30Game.TileType.EMPTY : t;
         }
      }

      void set(int x, int y, TileType type) {
         if (this.inBounds(x, y)) {
            this.tiles[y][x] = type;
            if (type != L30Game.TileType.BREAKABLE) {
               this.breakableVariants[y][x] = -1;
            }
         }

      }

      int getBreakableVariant(int x, int y) {
         return !this.inBounds(x, y) ? -1 : this.breakableVariants[y][x];
      }

      boolean isWalkableTile(int x, int y) {
         TileType t = this.get(x, y);
         return t == L30Game.TileType.EMPTY || t == L30Game.TileType.POWERUP;
      }
   }

   abstract static class Player {
      final String name;
      final Color color;
      final int spawnTx;
      final int spawnTy;
      double x;
      double y;
      double speed;
      double invincibleTime;
      int bombCapacity;
      int bombRange;
      int lives;
      int wins;
      int activeBombs;
      boolean alive;
      boolean stepMoving;
      double stepFromX;
      double stepFromY;
      double stepToX;
      double stepToY;
      double stepProgress;
      Facing facing;
      boolean moving;
      double animTime;
      final Set passableBombIds;

      Player(String name, Color color, int spawnTx, int spawnTy) {
         this.facing = L30Game.Facing.DOWN;
         this.passableBombIds = new HashSet();
         this.name = name;
         this.color = color;
         this.spawnTx = spawnTx;
         this.spawnTy = spawnTy;
         this.resetForRound();
      }

      void resetForRound() {
         this.x = (double)this.spawnTx + (double)0.5F;
         this.y = (double)this.spawnTy + (double)0.5F;
         this.speed = (double)4.0F;
         this.bombCapacity = 1;
         this.bombRange = 1;
         this.lives = 3;
         this.alive = true;
         this.invincibleTime = (double)0.0F;
         this.activeBombs = 0;
         this.passableBombIds.clear();
         this.facing = L30Game.Facing.DOWN;
         this.moving = false;
         this.animTime = (double)0.0F;
         this.stepMoving = false;
         this.stepProgress = (double)0.0F;
         this.stepFromX = this.x;
         this.stepFromY = this.y;
         this.stepToX = this.x;
         this.stepToY = this.y;
      }

      void updateInvincibility(double dt) {
         this.invincibleTime = Math.max((double)0.0F, this.invincibleTime - dt);
      }

      boolean isInvincible() {
         return this.invincibleTime > (double)0.0F;
      }

      int tileX() {
         return (int)Math.floor(this.x);
      }

      int tileY() {
         return (int)Math.floor(this.y);
      }

      void tryMove(double dirX, double dirY, double dt, GameState state) {
         if (this.alive) {
            if (this.stepMoving) {
               this.advanceStep(dt);
            } else {
               int sx = 0;
               int sy = 0;
               if (Math.abs(dirX) > Math.abs(dirY)) {
                  if (dirX > 0.1) {
                     sx = 1;
                  } else if (dirX < -0.1) {
                     sx = -1;
                  }
               } else if (dirY > 0.1) {
                  sy = 1;
               } else if (dirY < -0.1) {
                  sy = -1;
               }

               if (sx == 0 && sy == 0) {
                  this.moving = false;
                  this.animTime = (double)0.0F;
               } else {
                  if (sx < 0) {
                     this.facing = L30Game.Facing.LEFT;
                  } else if (sx > 0) {
                     this.facing = L30Game.Facing.RIGHT;
                  } else if (sy < 0) {
                     this.facing = L30Game.Facing.UP;
                  } else {
                     this.facing = L30Game.Facing.DOWN;
                  }

                  int nx = this.tileX() + sx;
                  int ny = this.tileY() + sy;
                  if (!state.canEnterTile(this, nx, ny)) {
                     this.moving = false;
                     this.animTime = (double)0.0F;
                  } else {
                     this.stepMoving = true;
                     this.stepProgress = (double)0.0F;
                     this.stepFromX = this.x;
                     this.stepFromY = this.y;
                     this.stepToX = (double)nx + (double)0.5F;
                     this.stepToY = (double)ny + (double)0.5F;
                     this.moving = true;
                     this.advanceStep(dt);
                  }
               }
            }
         }
      }

      private void advanceStep(double dt) {
         this.animTime += dt;
         this.stepProgress += this.speed * dt;
         if (this.stepProgress >= (double)1.0F) {
            this.stepProgress = (double)1.0F;
            this.x = this.stepToX;
            this.y = this.stepToY;
            this.stepMoving = false;
         } else {
            this.x = this.stepFromX + (this.stepToX - this.stepFromX) * this.stepProgress;
            this.y = this.stepFromY + (this.stepToY - this.stepFromY) * this.stepProgress;
         }
      }

      boolean canPlaceBomb() {
         return this.alive && this.activeBombs < this.bombCapacity;
      }

      void onBombPlaced(int bombId) {
         ++this.activeBombs;
         this.passableBombIds.add(bombId);
      }

      void onBombRemoved() {
         if (this.activeBombs > 0) {
            --this.activeBombs;
         }

      }

      void updateBombPassThrough(GameState state) {
         this.passableBombIds.removeIf((id) -> {
            Bomb b = state.findBombById(id);
            if (b == null) {
               return true;
            } else {
               return !this.overlapsBombTile(b.tx, b.ty);
            }
         });
      }

      private boolean overlapsBombTile(int bombTx, int bombTy) {
         double half = 0.26;
         double minX = this.x - half;
         double maxX = this.x + half;
         double minY = this.y - half;
         double maxY = this.y + half;
         double tileMinX = (double)bombTx;
         double tileMaxX = (double)bombTx + (double)1.0F;
         double tileMinY = (double)bombTy;
         double tileMaxY = (double)bombTy + (double)1.0F;
         return minX < tileMaxX && maxX > tileMinX && minY < tileMaxY && maxY > tileMinY;
      }

      void applyPowerUp(PowerUpType type) {
         switch (type.ordinal()) {
            case 0 -> this.bombCapacity = Math.min(5, this.bombCapacity + 1);
            case 1 -> this.bombRange = Math.min(8, this.bombRange + 1);
            case 2 -> this.speed = Math.min((double)7.0F, this.speed + 0.55);
         }

      }

      void hit() {
         if (this.alive && !this.isInvincible()) {
            --this.lives;
            if (this.lives <= 0) {
               this.alive = false;
            } else {
               this.invincibleTime = 1.2;
               this.x = (double)this.spawnTx + (double)0.5F;
               this.y = (double)this.spawnTy + (double)0.5F;
            }
         }
      }

      boolean visibleForRender(long millis) {
         return !this.isInvincible() || millis / 90L % 2L == 0L;
      }
   }

   static class HumanPlayer extends Player {
      HumanPlayer(String name, Color color, int tx, int ty) {
         super(name, color, tx, ty);
      }
   }

   static class BotPlayer extends Player {
      private double thinkCooldown = (double)0.0F;
      private double bombCooldown = (double)0.0F;
      private final List path = new ArrayList();

      BotPlayer(String name, Color color, int tx, int ty) {
         super(name, color, tx, ty);
      }

      void updateAI(double dt, GameState state) {
         if (this.alive) {
            this.thinkCooldown -= dt;
            this.bombCooldown -= dt;
            Point me = new Point(this.tileX(), this.tileY());
            Set<Point> danger = state.getDangerCells();
            if (danger.contains(me)) {
               this.refreshEscapePath(state, me, danger, true);
               this.moveByPath(dt, state);
            } else {
               if (this.bombCooldown <= (double)0.0F && this.canPlaceBomb() && this.shouldPlantBomb(state) && state.placeBomb(this, this.tileX(), this.tileY())) {
                  this.bombCooldown = 0.7;
                  this.thinkCooldown = 0.15;
                  Set<Point> afterPlantDanger = state.getDangerCells();
                  this.refreshEscapePath(state, me, afterPlantDanger, false);
               }

               if (this.thinkCooldown <= (double)0.0F || this.path.size() <= 1) {
                  this.refreshRoamPath(state, me, danger);
                  this.thinkCooldown = (double)0.25F;
               }

               this.moveByPath(dt, state);
            }
         }
      }

      private void refreshEscapePath(GameState state, Point me, Set danger, boolean strictSafe) {
         List<Point> newPath = L30Game.GridUtil.bfsPathToPredicate(me, (p) -> !danger.contains(p) && state.isSafeStandingTile(p.x, p.y), (x, y) -> state.isPassableForPath(this, x, y));
         if (newPath.isEmpty() && !strictSafe) {
            newPath = L30Game.GridUtil.bfsPathToPredicate(me, (p) -> state.isSafeStandingTile(p.x, p.y), (x, y) -> state.isPassableForPath(this, x, y));
         }

         this.path.clear();
         this.path.addAll(newPath);
      }

      private void refreshRoamPath(GameState state, Point me, Set danger) {
         Player target = state.findNearestEnemy(this);
         List<Point> newPath = List.of();
         if (target != null) {
            Point tp = new Point(target.tileX(), target.tileY());
            newPath = L30Game.GridUtil.bfsPath(me, tp, (x, y) -> state.isPassableForPath(this, x, y) && !danger.contains(new Point(x, y)));
         }

         if (newPath.isEmpty()) {
            newPath = L30Game.GridUtil.bfsPathToPredicate(me, (p) -> state.random.nextDouble() < 0.08 && state.isSafeStandingTile(p.x, p.y), (x, y) -> state.isPassableForPath(this, x, y) && !danger.contains(new Point(x, y)));
         }

         if (newPath.isEmpty()) {
            newPath = L30Game.GridUtil.bfsPathToPredicate(me, (p) -> state.isSafeStandingTile(p.x, p.y), (x, y) -> state.isPassableForPath(this, x, y));
         }

         this.path.clear();
         this.path.addAll(newPath);
      }

      private void moveByPath(double dt, GameState state) {
         if (this.path.size() > 1) {
            Point next = (Point)this.path.get(1);
            if (this.tileX() == next.x && this.tileY() == next.y) {
               this.path.remove(0);
               if (this.path.size() <= 1) {
                  return;
               }

               next = (Point)this.path.get(1);
            }

            int dx = Integer.compare(next.x, this.tileX());
            int dy = Integer.compare(next.y, this.tileY());
            this.tryMove((double)dx, (double)dy, dt, state);
         }
      }

      private boolean shouldPlantBomb(GameState state) {
         Player nearest = state.findNearestEnemy(this);
         if (nearest == null) {
            return false;
         } else {
            int tx = this.tileX();
            int ty = this.tileY();
            int ex = nearest.tileX();
            int ey = nearest.tileY();
            boolean attackChance = false;
            if (tx == ex) {
               int d = Math.abs(ty - ey);
               if (d <= this.bombRange && state.isLineThreatClear(tx, ty, ex, ey)) {
                  attackChance = true;
               }
            }

            if (ty == ey) {
               int d = Math.abs(tx - ex);
               if (d <= this.bombRange && state.isLineThreatClear(tx, ty, ex, ey)) {
                  attackChance = true;
               }
            }

            if (!attackChance) {
               attackChance = state.hasAdjacentBreakable(tx, ty);
            }

            return !attackChance ? false : state.canEscapeIfPlaceBomb(this, tx, ty);
         }
      }
   }

   static class GridUtil {
      private static final int[] DX = new int[]{1, -1, 0, 0};
      private static final int[] DY = new int[]{0, 0, 1, -1};

      static List bfsPath(Point start, Point goal, CellPassable passable) {
         return bfsPathToPredicate(start, (p) -> p.equals(goal), passable);
      }

      static List bfsPathToPredicate(Point start, Predicate goalPredicate, CellPassable passable) {
         ArrayDeque<Point> q = new ArrayDeque();
         Map<Point, Point> prev = new HashMap();
         Set<Point> visited = new HashSet();
         Point startCopy = new Point(start);
         q.add(startCopy);
         visited.add(startCopy);
         Point found = null;

         while(!q.isEmpty()) {
            Point cur = (Point)q.poll();
            if (goalPredicate.test(cur)) {
               found = cur;
               break;
            }

            for(int i = 0; i < 4; ++i) {
               int nx = cur.x + DX[i];
               int ny = cur.y + DY[i];
               Point next = new Point(nx, ny);
               if (!visited.contains(next) && passable.test(nx, ny)) {
                  visited.add(next);
                  prev.put(next, cur);
                  q.add(next);
               }
            }
         }

         if (found == null) {
            return List.of();
         } else {
            List<Point> path = new ArrayList();

            for(Point cur = found; cur != null; cur = (Point)prev.get(cur)) {
               path.add(0, cur);
            }

            return path;
         }
      }

      interface CellPassable {
         boolean test(int var1, int var2);
      }
   }

   static class GameState {
      static final int MAP_W = 15;
      static final int MAP_H = 13;
      static final int TILE_SIZE = 40;
      static final int HUD_W = 260;
      static final double BASE_SPEED = (double)4.0F;
      private static final double BOMB_FUSE = (double)2.0F;
      private static final double EXPLOSION_TIME = 0.35;
      private static final double POWERUP_DROP_CHANCE = (double)0.25F;
      private static final int WIN_TARGET = 3;
      final Random random = new Random();
      final TileMap map = new TileMap(15, 13);
      final List bombs = new ArrayList();
      final List explosions = new ArrayList();
      final List powerUps = new ArrayList();
      final HumanPlayer p1 = new HumanPlayer("P1", new Color(72, 132, 255), 1, 1);
      final HumanPlayer p2 = new HumanPlayer("P2", new Color(255, 80, 80), 13, 11);
      final BotPlayer bot = new BotPlayer("BOT", new Color(70, 197, 90), 13, 1);
      Phase phase;
      Mode mode;
      int round;
      Player roundWinner;
      private int nextBombId;

      GameState() {
         this.phase = L30Game.GameState.Phase.START;
         this.mode = L30Game.GameState.Mode.P1_VS_P2;
         this.round = 1;
         this.nextBombId = 1;
      }

      boolean isP2Mode() {
         return this.mode == L30Game.GameState.Mode.P1_VS_P2;
      }

      boolean isAiMode() {
         return this.mode == L30Game.GameState.Mode.P1_VS_AI;
      }

      void startNewMatch() {
         this.p1.wins = 0;
         this.p2.wins = 0;
         this.bot.wins = 0;
         this.round = 1;
         this.startRound();
      }

      void startRound() {
         this.bombs.clear();
         this.explosions.clear();
         this.powerUps.clear();
         this.nextBombId = 1;
         this.roundWinner = null;
         this.map.generate(this.random, new Point(this.p1.spawnTx, this.p1.spawnTy), new Point(this.p2.spawnTx, this.p2.spawnTy), new Point(this.bot.spawnTx, this.bot.spawnTy));
         this.p1.resetForRound();
         this.p2.resetForRound();
         this.bot.resetForRound();
         if (this.isP2Mode()) {
            this.bot.alive = false;
         } else {
            this.p2.alive = false;
         }

         this.phase = L30Game.GameState.Phase.PLAYING;
      }

      void togglePause() {
         if (this.phase == L30Game.GameState.Phase.PLAYING) {
            this.phase = L30Game.GameState.Phase.PAUSED;
         } else if (this.phase == L30Game.GameState.Phase.PAUSED) {
            this.phase = L30Game.GameState.Phase.PLAYING;
         }

      }

      void onEnterPressed(Mode selectedMode) {
         if (this.phase == L30Game.GameState.Phase.START) {
            this.mode = selectedMode;
            this.startNewMatch();
         } else if (this.phase != L30Game.GameState.Phase.ROUND_OVER) {
            if (this.phase == L30Game.GameState.Phase.GAME_OVER) {
               this.startNewMatch();
            }

         } else {
            if (this.p1.wins < 3 && this.p2.wins < 3 && this.bot.wins < 3) {
               ++this.round;
               this.startRound();
            } else {
               this.phase = L30Game.GameState.Phase.GAME_OVER;
            }

         }
      }

      void onRestartPressed() {
         this.startNewMatch();
      }

      void update(double dt, boolean p1Up, boolean p1Down, boolean p1Left, boolean p1Right, boolean p2Up, boolean p2Down, boolean p2Left, boolean p2Right, boolean p1Bomb, boolean p2Bomb) {
         if (this.phase == L30Game.GameState.Phase.PLAYING) {
            this.p1.updateInvincibility(dt);
            this.p2.updateInvincibility(dt);
            this.bot.updateInvincibility(dt);
            this.p1.updateBombPassThrough(this);
            this.p2.updateBombPassThrough(this);
            this.bot.updateBombPassThrough(this);
            this.p1.tryMove((double)((p1Right ? 1 : 0) - (p1Left ? 1 : 0)), (double)((p1Down ? 1 : 0) - (p1Up ? 1 : 0)), dt, this);
            if (this.isP2Mode()) {
               this.p2.tryMove((double)((p2Right ? 1 : 0) - (p2Left ? 1 : 0)), (double)((p2Down ? 1 : 0) - (p2Up ? 1 : 0)), dt, this);
            } else {
               this.bot.updateAI(dt, this);
            }

            if (p1Bomb) {
               this.placeBomb(this.p1, this.p1.tileX(), this.p1.tileY());
            }

            if (this.isP2Mode() && p2Bomb) {
               this.placeBomb(this.p2, this.p2.tileX(), this.p2.tileY());
            }

            this.updateBombs(dt);
            this.updateExplosions(dt);
            this.handlePowerUpPickup(this.p1);
            if (this.isP2Mode()) {
               this.handlePowerUpPickup(this.p2);
            } else {
               this.handlePowerUpPickup(this.bot);
            }

            this.applyExplosionDamage();
            this.checkRoundEnd();
         }
      }

      private void updateBombs(double dt) {
         List<Bomb> toExplode = new ArrayList();

         for(Bomb b : this.bombs) {
            b.fuse -= dt;
            if (b.fuse <= (double)0.0F) {
               toExplode.add(b);
            }
         }

         for(Bomb b : toExplode) {
            if (this.bombs.contains(b)) {
               this.detonateBomb(b, new HashSet());
            }
         }

      }

      private void updateExplosions(double dt) {
         Iterator<Explosion> it = this.explosions.iterator();

         while(it.hasNext()) {
            Explosion e = (Explosion)it.next();
            e.timeLeft -= dt;
            if (e.timeLeft <= (double)0.0F) {
               it.remove();
            }
         }

      }

      private void applyExplosionDamage() {
         for(Player p : this.allPlayers()) {
            if (p.alive && !p.isInvincible()) {
               int tx = p.tileX();
               int ty = p.tileY();

               for(Explosion e : this.explosions) {
                  if (e.contains(tx, ty)) {
                     int beforeLives = p.lives;
                     p.hit();
                     if (beforeLives > 0 && !p.alive) {
                        this.scatterDroppedPowerUps(p);
                     }
                     break;
                  }
               }
            }
         }

      }

      private void handlePowerUpPickup(Player player) {
         if (player.alive) {
            int tx = player.tileX();
            int ty = player.tileY();
            Iterator<PowerUp> it = this.powerUps.iterator();

            while(it.hasNext()) {
               PowerUp p = (PowerUp)it.next();
               if (p.tx == tx && p.ty == ty) {
                  player.applyPowerUp(p.type);
                  this.map.set(tx, ty, L30Game.TileType.EMPTY);
                  it.remove();
                  break;
               }
            }

         }
      }

      private void checkRoundEnd() {
         List<Player> alive = new ArrayList();
         if (this.p1.alive) {
            alive.add(this.p1);
         }

         if (this.isP2Mode() && this.p2.alive) {
            alive.add(this.p2);
         }

         if (this.isAiMode() && this.bot.alive) {
            alive.add(this.bot);
         }

         if (alive.size() <= 1) {
            this.roundWinner = alive.isEmpty() ? null : (Player)alive.get(0);
            if (this.roundWinner != null) {
               ++this.roundWinner.wins;
            }

            this.phase = this.p1.wins < 3 && this.p2.wins < 3 && this.bot.wins < 3 ? L30Game.GameState.Phase.ROUND_OVER : L30Game.GameState.Phase.GAME_OVER;
         }

      }

      boolean placeBomb(Player owner, int tx, int ty) {
         if (this.phase == L30Game.GameState.Phase.PLAYING && owner.canPlaceBomb()) {
            if (this.map.inBounds(tx, ty) && this.map.isWalkableTile(tx, ty)) {
               if (this.bombAt(tx, ty) != null) {
                  return false;
               } else {
                  Bomb bomb = new Bomb(this.nextBombId++, tx, ty, owner, (double)2.0F);
                  this.bombs.add(bomb);
                  owner.onBombPlaced(bomb.id);
                  return true;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }

      Bomb bombAt(int tx, int ty) {
         for(Bomb b : this.bombs) {
            if (b.tx == tx && b.ty == ty) {
               return b;
            }
         }

         return null;
      }

      Bomb findBombById(int id) {
         for(Bomb b : this.bombs) {
            if (b.id == id) {
               return b;
            }
         }

         return null;
      }

      private void detonateBomb(Bomb bomb, Set chainVisited) {
         if (chainVisited.add(bomb.id)) {
            if (this.bombs.remove(bomb)) {
               bomb.owner.onBombRemoved();
               Explosion explosion = new Explosion(0.35);
               List<Point> cells = this.calcExplosionCells(bomb.tx, bomb.ty, bomb.owner.bombRange);
               explosion.cells.addAll(cells);
               this.explosions.add(explosion);

               for(Point c : cells) {
                  TileType t = this.map.get(c.x, c.y);
                  if (t == L30Game.TileType.BREAKABLE) {
                     this.map.set(c.x, c.y, L30Game.TileType.EMPTY);
                     if (this.random.nextDouble() < (double)0.25F) {
                        this.spawnPowerUp(c.x, c.y);
                     }
                  } else if (t == L30Game.TileType.POWERUP) {
                     this.removePowerUpAt(c.x, c.y);
                     this.map.set(c.x, c.y, L30Game.TileType.EMPTY);
                  }

                  Bomb other = this.bombAt(c.x, c.y);
                  if (other != null) {
                     this.detonateBomb(other, chainVisited);
                  }
               }

            }
         }
      }

      private void spawnPowerUp(int tx, int ty) {
         double r = this.random.nextDouble();
         PowerUpType type = r < 0.34 ? L30Game.PowerUpType.BOMB_CAPACITY : (r < 0.67 ? L30Game.PowerUpType.BOMB_RANGE : L30Game.PowerUpType.SPEED);
         this.spawnPowerUp(tx, ty, type);
      }

      private void spawnPowerUp(int tx, int ty, PowerUpType type) {
         this.removePowerUpAt(tx, ty);
         this.map.set(tx, ty, L30Game.TileType.POWERUP);
         this.powerUps.add(new PowerUp(tx, ty, type));
      }

      private void removePowerUpAt(int tx, int ty) {
         this.powerUps.removeIf((p) -> p.tx == tx && p.ty == ty);
      }

      private boolean hasPowerUpAt(int tx, int ty) {
         return this.powerUps.stream().anyMatch((p) -> p.tx == tx && p.ty == ty);
      }

      private void scatterDroppedPowerUps(Player player) {
         List<PowerUpType> drops = new ArrayList();
         int extraCapacity = Math.max(0, player.bombCapacity - 1);
         int extraRange = Math.max(0, player.bombRange - 1);
         int extraSpeed = Math.max(0, (int)Math.round((player.speed - (double)4.0F) / 0.55));

         for(int i = 0; i < extraCapacity; ++i) {
            drops.add(L30Game.PowerUpType.BOMB_CAPACITY);
         }

         for(int i = 0; i < extraRange; ++i) {
            drops.add(L30Game.PowerUpType.BOMB_RANGE);
         }

         for(int i = 0; i < extraSpeed; ++i) {
            drops.add(L30Game.PowerUpType.SPEED);
         }

         if (!drops.isEmpty()) {
            Collections.shuffle(drops, this.random);

            for(PowerUpType type : drops) {
               Point pos = this.findRandomDropTile();
               if (pos == null) {
                  break;
               }

               this.spawnPowerUp(pos.x, pos.y, type);
            }

         }
      }

      private Point findRandomDropTile() {
         List<Point> candidates = new ArrayList();

         for(int y = 0; y < 13; ++y) {
            for(int x = 0; x < 15; ++x) {
               if (this.map.get(x, y) == L30Game.TileType.EMPTY && this.bombAt(x, y) == null && !this.hasPowerUpAt(x, y)) {
                  boolean inExplosion = false;

                  for(Explosion e : this.explosions) {
                     if (e.contains(x, y)) {
                        inExplosion = true;
                        break;
                     }
                  }

                  if (!inExplosion) {
                     candidates.add(new Point(x, y));
                  }
               }
            }
         }

         if (candidates.isEmpty()) {
            return null;
         } else {
            return (Point)candidates.get(this.random.nextInt(candidates.size()));
         }
      }

      private List calcExplosionCells(int ox, int oy, int range) {
         List<Point> cells = new ArrayList();
         cells.add(new Point(ox, oy));
         int[] dx = new int[]{1, -1, 0, 0};
         int[] dy = new int[]{0, 0, 1, -1};

         for(int d = 0; d < 4; ++d) {
            for(int i = 1; i <= range; ++i) {
               int nx = ox + dx[d] * i;
               int ny = oy + dy[d] * i;
               TileType t = this.map.get(nx, ny);
               if (t == L30Game.TileType.SOLID) {
                  break;
               }

               cells.add(new Point(nx, ny));
               if (t == L30Game.TileType.BREAKABLE) {
                  break;
               }
            }
         }

         return cells;
      }

      Set getDangerCells() {
         Set<Point> danger = new HashSet();

         for(Explosion e : this.explosions) {
            danger.addAll(e.cells);
         }

         for(Bomb b : this.bombs) {
            danger.addAll(this.calcExplosionCells(b.tx, b.ty, b.owner.bombRange));
         }

         return danger;
      }

      boolean canEscapeIfPlaceBomb(Player owner, int bombTx, int bombTy) {
         Set<Point> danger = this.getDangerCells();
         danger.addAll(this.calcExplosionCells(bombTx, bombTy, owner.bombRange));
         Point start = new Point(owner.tileX(), owner.tileY());
         List<Point> path = L30Game.GridUtil.bfsPathToPredicate(start, (p) -> !danger.contains(p) && this.isSafeStandingTile(p.x, p.y), (x, y) -> this.isPassableForPathWithVirtualBomb(owner, x, y, bombTx, bombTy));
         return path.size() > 1;
      }

      private boolean isPassableForPathWithVirtualBomb(Player player, int x, int y, int virtualBombTx, int virtualBombTy) {
         if (!this.map.inBounds(x, y)) {
            return false;
         } else {
            TileType t = this.map.get(x, y);
            if (t != L30Game.TileType.SOLID && t != L30Game.TileType.BREAKABLE) {
               if (x == virtualBombTx && y == virtualBombTy) {
                  return true;
               } else {
                  Bomb b = this.bombAt(x, y);
                  return b == null || player.passableBombIds.contains(b.id);
               }
            } else {
               return false;
            }
         }
      }

      boolean canEnterTile(Player player, int tx, int ty) {
         if (!this.map.inBounds(tx, ty)) {
            return false;
         } else {
            TileType t = this.map.get(tx, ty);
            if (t != L30Game.TileType.SOLID && t != L30Game.TileType.BREAKABLE) {
               Bomb b = this.bombAt(tx, ty);
               return b == null || player.passableBombIds.contains(b.id);
            } else {
               return false;
            }
         }
      }

      boolean isBlockedForPlayer(Player player, double nx, double ny) {
         double half = 0.26;
         double[] xs = new double[]{nx - half, nx + half};
         double[] ys = new double[]{ny - half, ny + half};

         for(double x : xs) {
            for(double y : ys) {
               int tx = (int)Math.floor(x);
               int ty = (int)Math.floor(y);
               if (!this.map.inBounds(tx, ty)) {
                  return true;
               }

               TileType t = this.map.get(tx, ty);
               if (t == L30Game.TileType.SOLID || t == L30Game.TileType.BREAKABLE) {
                  return true;
               }

               Bomb b = this.bombAt(tx, ty);
               if (b != null && !player.passableBombIds.contains(b.id)) {
                  return true;
               }
            }
         }

         return false;
      }

      boolean isPassableForPath(Player player, int x, int y) {
         if (!this.map.inBounds(x, y)) {
            return false;
         } else {
            TileType t = this.map.get(x, y);
            if (t != L30Game.TileType.SOLID && t != L30Game.TileType.BREAKABLE) {
               Bomb b = this.bombAt(x, y);
               return b == null || player.passableBombIds.contains(b.id);
            } else {
               return false;
            }
         }
      }

      boolean isSafeStandingTile(int x, int y) {
         if (!this.map.inBounds(x, y)) {
            return false;
         } else {
            TileType t = this.map.get(x, y);
            return t == L30Game.TileType.EMPTY || t == L30Game.TileType.POWERUP;
         }
      }

      boolean hasAdjacentBreakable(int tx, int ty) {
         int[] dx = new int[]{1, -1, 0, 0};
         int[] dy = new int[]{0, 0, 1, -1};

         for(int i = 0; i < 4; ++i) {
            if (this.map.get(tx + dx[i], ty + dy[i]) == L30Game.TileType.BREAKABLE) {
               return true;
            }
         }

         return false;
      }

      Player findNearestEnemy(Player from) {
         Player nearest = null;
         int best = Integer.MAX_VALUE;

         for(Player p : this.allPlayers()) {
            if (p != from && p.alive) {
               int d = Math.abs(from.tileX() - p.tileX()) + Math.abs(from.tileY() - p.tileY());
               if (d < best) {
                  best = d;
                  nearest = p;
               }
            }
         }

         return nearest;
      }

      boolean isLineThreatClear(int sx, int sy, int ex, int ey) {
         if (sx != ex && sy != ey) {
            return false;
         } else {
            int dx = Integer.compare(ex, sx);
            int dy = Integer.compare(ey, sy);
            int x = sx + dx;

            for(int y = sy + dy; x != ex || y != ey; y += dy) {
               TileType t = this.map.get(x, y);
               if (t == L30Game.TileType.SOLID || t == L30Game.TileType.BREAKABLE) {
                  return false;
               }

               x += dx;
            }

            return true;
         }
      }

      List allPlayers() {
         return this.isP2Mode() ? Arrays.asList(this.p1, this.p2) : Arrays.asList(this.p1, this.bot);
      }

      String modeText() {
         return this.isP2Mode() ? "P1 vs P2" : "P1 vs AI";
      }

      String winnerText() {
         return this.roundWinner == null ? "Draw" : "Winner: " + this.roundWinner.name;
      }

      String championText() {
         Player champ = this.p1;
         if (this.p2.wins > champ.wins) {
            champ = this.p2;
         }

         if (this.bot.wins > champ.wins) {
            champ = this.bot;
         }

         return "Champion: " + champ.name;
      }

      static enum Phase {
         START,
         PLAYING,
         PAUSED,
         ROUND_OVER,
         GAME_OVER;

         // $FF: synthetic method
         private static Phase[] $values() {
            return new Phase[]{START, PLAYING, PAUSED, ROUND_OVER, GAME_OVER};
         }
      }

      static enum Mode {
         P1_VS_P2,
         P1_VS_AI;

         // $FF: synthetic method
         private static Mode[] $values() {
            return new Mode[]{P1_VS_P2, P1_VS_AI};
         }
      }
   }

   static class GamePanel extends JPanel {
      private final GameState state = new GameState();
      private final Timer timer;
      private final SpriteSet p1Sprites;
      private final SpriteSet p2Sprites;
      private final SpriteSet aiSprites;
      private final Image bombImg1;
      private final Image bombImg2;
      private final Image explosionImg;
      private final Image solidBlockImg;
      private final Image[] breakableBlockImgs;
      private boolean p1Up;
      private boolean p1Down;
      private boolean p1Left;
      private boolean p1Right;
      private boolean p2Up;
      private boolean p2Down;
      private boolean p2Left;
      private boolean p2Right;
      private boolean p1BombQueued;
      private boolean p2BombQueued;
      private int startMenuIndex = 0;
      private long lastNanos;

      GamePanel() {
         int w = 860;
         int h = 520;
         this.setPreferredSize(new Dimension(w, h));
         this.setFocusable(true);
         this.setBackground(new Color(30, 30, 30));
         this.p1Sprites = new SpriteSet(this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\플레이어1.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\플레이어1걷.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\플레이어1뒷.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\플레이어1뒷걷.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\플레이어1왼.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\플레이어1왼걷.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\플레이어1오.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\플레이어1오걷.png"));
         this.p2Sprites = new SpriteSet(this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\플레이어2.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\플레이어2걷.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\플레이어2뒷.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\플레이어2뒷걷.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\플레이어2왼.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\플레이어2왼걷.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\플레이어2오.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\플레이어2오걷.png"));
         this.aiSprites = new SpriteSet(this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\AI.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\AI걷.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\AI뒷.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\AI뒷걷.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\AI왼.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\AI왼걷.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\AI오.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\AI오걷.png"));
         this.bombImg1 = this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\폭탄1.png");
         this.solidBlockImg = this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\안부서지는블럭.png");
         this.breakableBlockImgs = new Image[]{this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\빨간블럭.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\노란블럭.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\주황블럭.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\초록블럭.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\파란블럭.png")};
         this.bombImg2 = this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\폭탄2.png");
         this.explosionImg = this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\폭탄터짐.png");
         this.setupKeyBindings();
         this.lastNanos = System.nanoTime();
         this.timer = new Timer(16, (e) -> this.onTick());
         this.timer.start();
      }

      private void onTick() {
         long now = System.nanoTime();
         double dt = (double)(now - this.lastNanos) / (double)1.0E9F;
         this.lastNanos = now;
         if (dt > 0.05) {
            dt = 0.05;
         }

         this.state.update(dt, this.p1Up, this.p1Down, this.p1Left, this.p1Right, this.p2Up, this.p2Down, this.p2Left, this.p2Right, this.p1BombQueued, this.p2BombQueued);
         this.p1BombQueued = false;
         this.p2BombQueued = false;
         this.repaint();
      }

      private void setupKeyBindings() {
         InputMap im = this.getInputMap(2);
         ActionMap am = this.getActionMap();
         this.bindHold(im, am, "W", () -> this.p1Up = true, () -> this.p1Up = false);
         this.bindHold(im, am, "S", () -> this.p1Down = true, () -> this.p1Down = false);
         this.bindHold(im, am, "A_KEY", "A", () -> this.p1Left = true, () -> this.p1Left = false);
         this.bindHold(im, am, "D", () -> this.p1Right = true, () -> this.p1Right = false);
         this.bindHold(im, am, "UP", "UP", () -> this.p2Up = true, () -> this.p2Up = false);
         this.bindHold(im, am, "DOWN", "DOWN", () -> this.p2Down = true, () -> this.p2Down = false);
         this.bindHold(im, am, "LEFT", "LEFT", () -> this.p2Left = true, () -> this.p2Left = false);
         this.bindHold(im, am, "RIGHT", "RIGHT", () -> this.p2Right = true, () -> this.p2Right = false);
         this.bindPress(im, am, "SPACE", "SPACE", () -> {
            if (this.state.phase == L30Game.GameState.Phase.PLAYING) {
               this.p1BombQueued = true;
            }

         });
         this.bindPress(im, am, "ENTER_KEY", "ENTER", () -> {
            if (this.state.phase == L30Game.GameState.Phase.PLAYING && this.state.isP2Mode()) {
               this.p2BombQueued = true;
            } else {
               this.state.onEnterPressed(this.startMenuIndex == 0 ? L30Game.GameState.Mode.P1_VS_P2 : L30Game.GameState.Mode.P1_VS_AI);
            }

         });
         this.bindPress(im, am, "MODE_1", "1", () -> {
            if (this.state.phase == L30Game.GameState.Phase.START) {
               this.startMenuIndex = 0;
            }

         });
         this.bindPress(im, am, "MODE_2", "2", () -> {
            if (this.state.phase == L30Game.GameState.Phase.START) {
               this.startMenuIndex = 1;
            }

         });
         GameState var10005 = this.state;
         Objects.requireNonNull(var10005);
         this.bindPress(im, am, "PAUSE", "P", var10005::togglePause);
         var10005 = this.state;
         Objects.requireNonNull(var10005);
         this.bindPress(im, am, "RESTART", "R", var10005::onRestartPressed);
         this.bindPress(im, am, "ESC", "ESCAPE", () -> System.exit(0));
      }

      private void bindPress(InputMap im, ActionMap am, String id, String key, final Runnable action) {
         im.put(KeyStroke.getKeyStroke(key), id);
         am.put(id, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
               action.run();
            }
         });
      }

      private void bindHold(InputMap im, ActionMap am, String key, Runnable onPress, Runnable onRelease) {
         this.bindHold(im, am, key, key, onPress, onRelease);
      }

      private void bindHold(InputMap im, ActionMap am, String id, String key, final Runnable onPress, final Runnable onRelease) {
         String pressId = id + "_PRESS";
         String releaseId = id + "_RELEASE";
         im.put(KeyStroke.getKeyStroke("pressed " + key), pressId);
         im.put(KeyStroke.getKeyStroke("released " + key), releaseId);
         am.put(pressId, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
               onPress.run();
            }
         });
         am.put(releaseId, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
               onRelease.run();
            }
         });
      }

      protected void paintComponent(Graphics g) {
         super.paintComponent(g);
         Graphics2D g2 = (Graphics2D)g.create();
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         this.drawMap(g2);
         this.drawPowerUps(g2);
         this.drawBombs(g2);
         this.drawExplosions(g2);
         this.drawPlayers(g2);
         this.drawHud(g2);
         this.drawOverlay(g2);
         g2.dispose();
      }

      private void drawMap(Graphics2D g2) {
         int ts = 40;

         for(int y = 0; y < 13; ++y) {
            for(int x = 0; x < 15; ++x) {
               TileType t = this.state.map.get(x, y);
               int px = x * ts;
               int py = y * ts;
               if (t == L30Game.TileType.SOLID) {
                  if (this.solidBlockImg != null) {
                     g2.drawImage(this.solidBlockImg, px, py, ts, ts, (ImageObserver)null);
                  } else {
                     g2.setColor(new Color(80, 80, 80));
                     g2.fillRect(px, py, ts, ts);
                  }
               } else if (t == L30Game.TileType.BREAKABLE) {
                  int variant = this.state.map.getBreakableVariant(x, y);
                  Image blockImg = variant >= 0 && variant < this.breakableBlockImgs.length ? this.breakableBlockImgs[variant] : null;
                  if (blockImg != null) {
                     g2.drawImage(blockImg, px, py, ts, ts, (ImageObserver)null);
                  } else {
                     g2.setColor(new Color(150, 100, 60));
                     g2.fillRect(px, py, ts, ts);
                  }
               } else {
                  g2.setColor(new Color(225, 230, 216));
                  g2.fillRect(px, py, ts, ts);
               }

               g2.setColor(new Color(0, 0, 0, 28));
               g2.drawRect(px, py, ts, ts);
            }
         }

      }

      private void drawPowerUps(Graphics2D g2) {
         int ts = 40;
         g2.setFont(new Font("Dialog", 1, 14));

         for(PowerUp p : this.state.powerUps) {
            int px = p.tx * ts;
            int py = p.ty * ts;
            g2.setColor(p.color());
            g2.fillOval(px + 8, py + 8, ts - 16, ts - 16);
            g2.setColor(Color.BLACK);
            g2.drawString(p.shortName(), px + 12, py + ts - 12);
         }

      }

      private void drawBombs(Graphics2D g2) {
         int ts = 40;

         for(Bomb b : this.state.bombs) {
            int px = b.tx * ts;
            int py = b.ty * ts;
            Image frame = (int)Math.floor(((double)2.0F - Math.max((double)0.0F, b.fuse)) * (double)8.0F) % 2 == 0 ? this.bombImg1 : this.bombImg2;
            if (frame != null) {
               int size = (int)((double)ts * 0.84);
               int dx = px + (ts - size) / 2;
               int dy = py + (ts - size) / 2;
               g2.drawImage(frame, dx, dy, size, size, (ImageObserver)null);
            } else {
               g2.setColor(Color.BLACK);
               g2.fillOval(px + 7, py + 7, ts - 14, ts - 14);
               g2.setColor(Color.WHITE);
               g2.fillOval(px + 14, py + 10, 6, 6);
            }
         }

      }

      private void drawExplosions(Graphics2D g2) {
         int ts = 40;

         for(Explosion e : this.state.explosions) {
            for(Point p : e.cells) {
               int px = p.x * ts;
               int py = p.y * ts;
               if (this.explosionImg != null) {
                  g2.drawImage(this.explosionImg, px, py, ts, ts, (ImageObserver)null);
               } else {
                  g2.setColor(new Color(255, 140, 35, 210));
                  g2.fillRect(px + 2, py + 2, ts - 4, ts - 4);
                  g2.setColor(new Color(255, 228, 110, 210));
                  g2.fillRect(px + 10, py + 10, ts - 20, ts - 20);
               }
            }
         }

      }

      private void drawPlayers(Graphics2D g2) {
         int ts = 40;
         long ms = System.currentTimeMillis();

         for(Player p : this.state.allPlayers()) {
            if (p.alive && p.visibleForRender(ms)) {
               int cx = (int)Math.round(p.x * (double)ts);
               int cy = (int)Math.round(p.y * (double)ts);
               int r = ts / 2 - 6;
               Image sprite = null;
               if (p == this.state.p1 && this.p1Sprites != null) {
                  sprite = this.p1Sprites.get(p.facing, p.moving, p.animTime);
               } else if (p == this.state.p2 && this.p2Sprites != null) {
                  sprite = this.p2Sprites.get(p.facing, p.moving, p.animTime);
               } else if (p == this.state.bot && this.aiSprites != null) {
                  sprite = this.aiSprites.get(p.facing, p.moving, p.animTime);
               }

               if (sprite != null) {
                  int sw = Math.max(1, sprite.getWidth((ImageObserver)null));
                  int sh = Math.max(1, sprite.getHeight((ImageObserver)null));
                  int h = (int)((double)ts * 1.45);
                  int w = (int)Math.round((double)sw / (double)sh * (double)h);
                  w = Math.max(ts, Math.min((int)((double)ts * 1.9), w));
                  int drawX = cx - w / 2;
                  int drawY = cy - h / 2;
                  g2.drawImage(sprite, drawX, drawY, w, h, (ImageObserver)null);
                  g2.setColor(Color.BLACK);
                  g2.setFont(new Font("Dialog", 1, 12));
               } else {
                  g2.setColor(p.color);
                  g2.fillOval(cx - r, cy - r, r * 2, r * 2);
                  g2.setColor(Color.BLACK);
                  g2.drawOval(cx - r, cy - r, r * 2, r * 2);
                  g2.setFont(new Font("Dialog", 1, 12));
               }
            }
         }

      }

      private Image loadImage(String path) {
         try {
            BufferedImage raw = ImageIO.read(new File(path));
            return raw == null ? null : this.trimTransparent(raw);
         } catch (Exception var3) {
            return null;
         }
      }

      private BufferedImage trimTransparent(BufferedImage src) {
         int w = src.getWidth();
         int h = src.getHeight();
         int minX = w;
         int minY = h;
         int maxX = -1;
         int maxY = -1;

         for(int y = 0; y < h; ++y) {
            for(int x = 0; x < w; ++x) {
               int a = src.getRGB(x, y) >>> 24 & 255;
               if (a > 8) {
                  if (x < minX) {
                     minX = x;
                  }

                  if (y < minY) {
                     minY = y;
                  }

                  if (x > maxX) {
                     maxX = x;
                  }

                  if (y > maxY) {
                     maxY = y;
                  }
               }
            }
         }

         if (maxX >= minX && maxY >= minY) {
            return src.getSubimage(minX, minY, maxX - minX + 1, maxY - minY + 1);
         } else {
            return src;
         }
      }

      private void drawHud(Graphics2D g2) {
         int left = 600;
         int h = 520;
         g2.setColor(new Color(32, 37, 52));
         g2.fillRect(left, 0, 260, h);
         g2.setColor(new Color(80, 100, 140));
         g2.setStroke(new BasicStroke(2.0F));
         g2.drawLine(left, 0, left, h);
         g2.setColor(new Color(240, 245, 255));
         g2.setFont(new Font("Dialog", 1, 20));
         g2.drawString("Arcade Bomber", left + 24, 34);
         g2.setFont(new Font("Dialog", 0, 15));
         g2.drawString("Round: " + this.state.round, left + 24, 66);
         g2.drawString("Mode: " + this.state.modeText(), left + 24, 88);
         g2.drawString("State: " + String.valueOf(this.state.phase), left + 24, 110);
         this.drawPlayerHud(g2, this.state.p1, left + 24, 155);
         if (this.state.isP2Mode()) {
            this.drawPlayerHud(g2, this.state.p2, left + 24, 260);
         } else {
            this.drawPlayerHud(g2, this.state.bot, left + 24, 260);
         }

         g2.setFont(new Font("Dialog", 0, 13));
         g2.setColor(new Color(205, 215, 232));
         g2.drawString("P1: WASD + SPACE", left + 24, h - 88);
         g2.drawString("P2 mode: ARROW + ENTER", left + 24, h - 68);
         g2.drawString("P pause, R restart", left + 24, h - 48);
         g2.drawString("ENTER next/start, ESC exit", left + 24, h - 28);
      }

      private void drawPlayerHud(Graphics2D g2, Player p, int x, int y) {
         g2.setColor(p.color);
         g2.fillRect(x, y - 16, 16, 16);
         g2.setColor(Color.WHITE);
         g2.setFont(new Font("Dialog", 1, 15));
         g2.setFont(new Font("Dialog", 0, 14));
         g2.drawString("Lives: " + p.lives + "   Wins: " + p.wins, x, y + 22);
         g2.drawString("Bomb: " + p.bombCapacity + "  Range: " + p.bombRange, x, y + 42);
         g2.drawString(String.format("Speed: %.1f", p.speed), x, y + 62);
      }

      private void drawOverlay(Graphics2D g2) {
         String line1 = null;
         String line2 = null;
         String line3 = null;
         String line4 = null;
         if (this.state.phase == L30Game.GameState.Phase.START) {
            line1 = "Arcade Bomber";
            String var10000 = this.startMenuIndex == 0 ? "> " : "  ";
            line2 = var10000 + "P1 vs P2";
            line3 = (this.startMenuIndex == 1 ? "> " : "  ") + "P1 vs AI";
            line4 = "1/2 select, ENTER start, ESC exit";
         } else if (this.state.phase == L30Game.GameState.Phase.PAUSED) {
            line1 = "Paused";
            line2 = "P: Resume";
         } else if (this.state.phase == L30Game.GameState.Phase.ROUND_OVER) {
            line1 = this.state.winnerText();
            line2 = "R: Restart Match / ENTER: Next Round / ESC: Exit";
         } else if (this.state.phase == L30Game.GameState.Phase.GAME_OVER) {
            line1 = this.state.championText();
            line2 = "R: Restart Match / ENTER: New Match / ESC: Exit";
            line3 = "First to 3 wins";
         }

         if (line1 != null) {
            int mapW = 600;
            int mapH = 520;
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, mapW, mapH);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Dialog", 1, 34));
            this.drawCentered(g2, line1, mapW / 2, mapH / 2 - 42);
            g2.setFont(new Font("Dialog", 0, 22));
            this.drawCentered(g2, line2, mapW / 2, mapH / 2 + 2);
            if (line3 != null) {
               g2.setFont(new Font("Dialog", 0, 22));
               this.drawCentered(g2, line3, mapW / 2, mapH / 2 + 34);
            }

            if (line4 != null) {
               g2.setFont(new Font("Dialog", 0, 15));
               this.drawCentered(g2, line4, mapW / 2, mapH / 2 + 70);
            }

         }
      }

      private void drawCentered(Graphics2D g2, String text, int cx, int y) {
         FontMetrics fm = g2.getFontMetrics();
         int x = cx - fm.stringWidth(text) / 2;
         g2.drawString(text, x, y);
      }
   }
}
