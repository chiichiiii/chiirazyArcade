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
import java.awt.Stroke;
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
      PUSHABLE,
      POWERUP;

      // $FF: synthetic method
      private static TileType[] $values() {
         return new TileType[]{EMPTY, SOLID, BREAKABLE, PUSHABLE, POWERUP};
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
      final Set<Point> cells = new HashSet<>();
      double timeLeft;
      boolean hurtsStoryBoss = true;

      Explosion(double duration) {
         this.timeLeft = duration;
      }

      boolean contains(int tx, int ty) {
         return this.cells.contains(new Point(tx, ty));
      }
   }
   static class BossSkillWarning {
      final Set<Point> cells = new HashSet<>();
      double timeLeft;

      BossSkillWarning(double duration) {
         this.timeLeft = duration;
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
                     this.breakableVariants[y][x] = random.nextInt(2);
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

      void setBreakable(int x, int y, int variant) {
         if (this.inBounds(x, y)) {
            this.tiles[y][x] = L30Game.TileType.BREAKABLE;
            this.breakableVariants[y][x] = Math.floorMod(variant, 2);
         }

      }

      void setPushable(int x, int y) {
         if (this.inBounds(x, y)) {
            this.tiles[y][x] = L30Game.TileType.PUSHABLE;
            this.breakableVariants[y][x] = -1;
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
      double bubbleTimeLeft;
      int bombCapacity;
      int bombRange;
      int lives;
      int wins;
      int activeBombs;
      boolean alive;
      boolean bubbleTrapped;
      boolean stepMoving;
      double stepFromX;
      double stepFromY;
      double stepToX;
      double stepToY;
      double stepProgress;
      Facing facing;
      boolean moving;
      double animTime;
      final Set<Integer> passableBombIds;

      Player(String name, Color color, int spawnTx, int spawnTy) {
         this.facing = L30Game.Facing.DOWN;
         this.passableBombIds = new HashSet<>();
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
         this.bubbleTimeLeft = (double)0.0F;
         this.bubbleTrapped = false;
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

      void updateBubble(double dt) {
         if (this.bubbleTrapped) {
            this.bubbleTimeLeft = Math.max((double)0.0F, this.bubbleTimeLeft - dt);
         }
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
         if (this.alive && !this.bubbleTrapped) {
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
                  boolean canStepIntoTarget = state.canEnterTile(this, nx, ny) && !state.isBlockedForPlayer(this, (double)nx + (double)0.5F, (double)ny + (double)0.5F);
                  if (!canStepIntoTarget && (!(this instanceof HumanPlayer) || !state.tryPushBox(this, nx, ny, sx, sy))) {
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
         return this.alive && !this.bubbleTrapped && this.activeBombs < this.bombCapacity;
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
         this.passableBombIds.removeIf((Integer id) -> {
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

      void trapInBubble(double duration) {
         if (this.alive && !this.isInvincible() && !this.bubbleTrapped) {
            this.bubbleTrapped = true;
            this.bubbleTimeLeft = duration;
            this.moving = false;
            this.stepMoving = false;
            this.animTime = (double)0.0F;
            this.x = (double)this.tileX() + (double)0.5F;
            this.y = (double)this.tileY() + (double)0.5F;
         }
      }

      void resetCollectedPowerUps() {
         this.bombCapacity = 1;
         this.bombRange = 1;
         this.speed = (double)4.0F;
      }

      void burstBubble() {
         if (this.alive && this.bubbleTrapped) {
            this.bubbleTrapped = false;
            this.bubbleTimeLeft = (double)0.0F;
            --this.lives;
            if (this.lives <= 0) {
               this.alive = false;
            } else {
               this.resetCollectedPowerUps();
               this.invincibleTime = 1.2;
               this.x = (double)this.spawnTx + (double)0.5F;
               this.y = (double)this.spawnTy + (double)0.5F;
               this.stepMoving = false;
               this.moving = false;
               this.stepProgress = (double)0.0F;
               this.stepFromX = this.x;
               this.stepFromY = this.y;
               this.stepToX = this.x;
               this.stepToY = this.y;
            }
         }
      }

      boolean visibleForRender(long millis) {
         return this.bubbleTrapped || !this.isInvincible() || millis / 90L % 2L == 0L;
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
      private final List<Point> path = new ArrayList<>();

      BotPlayer(String name, Color color, int tx, int ty) {
         super(name, color, tx, ty);
      }





      void updateAI(double dt, GameState state) {
         if (this.alive && !this.bubbleTrapped) {
            this.thinkCooldown -= dt;
            this.bombCooldown -= dt;
            Point me = new Point(this.tileX(), this.tileY());
            Set<Point> danger = state.getDangerCells();
            if (state.isStoryMode()) {
               if (this.thinkCooldown <= (double)0.0F || this.path.size() <= 1) {
                  this.refreshStoryRoamPath(state, me);
                  this.thinkCooldown = 0.35;
               }

               this.moveByPath(dt, state);
            } else if (danger.contains(me)) {
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



      private void refreshEscapePath
(GameState state, Point me, Set<Point> danger, boolean strictSafe) {
         List<Point> newPath = L30Game.GridUtil.bfsPathToPredicate(me, (Point p) -> !danger.contains(p) && state.isSafeStandingTile(p.x, p.y), (x, y) -> state.isPassableForPath(this, x, y));
         if (newPath.isEmpty() && !strictSafe) {
            newPath = L30Game.GridUtil.bfsPathToPredicate(me, (Point p) -> state.isSafeStandingTile(p.x, p.y), (x, y) -> state.isPassableForPath(this, x, y));
         }

         this.path.clear();
         this.path.addAll(newPath);
      }



      private void refreshRoamPath(GameState state, Point me, Set<Point> danger) {
         Player target = state.findNearestEnemy(this);
         List<Point> newPath = List.of();
         if (target != null) {
            Point tp = new Point(target.tileX(), target.tileY());
            newPath = L30Game.GridUtil.bfsPath(me, tp, (x, y) -> state.isPassableForPath(this, x, y) && !danger.contains(new Point(x, y)));
         }

         if (newPath.isEmpty()) {
            newPath = L30Game.GridUtil.bfsPathToPredicate(me, (Point p) -> state.random.nextDouble() < 0.08 && state.isSafeStandingTile(p.x, p.y), (x, y) -> state.isPassableForPath(this, x, y) && !danger.contains(new Point(x, y)));
         }

         if (newPath.isEmpty()) {
            newPath = L30Game.GridUtil.bfsPathToPredicate(me, (Point p) -> state.isSafeStandingTile(p.x, p.y), (x, y) -> state.isPassableForPath(this, x, y));
         }

         this.path.clear();
         this.path.addAll(newPath);
      }



      private void refreshStoryRoamPath(GameState state, Point me) {
         List<Point> newPath = List.of();
         Player target = state.findNearestEnemy(this);
         if (target != null) {
            int distance = Math.abs(me.x - target.tileX()) + Math.abs(me.y - target.tileY());
            if (distance <= 4) {
               Point tp = new Point(target.tileX(), target.tileY());
               newPath = L30Game.GridUtil.bfsPath(me, tp, (x, y) -> state.isPassableForPath(this, x, y));
            }
         }

         if (newPath.isEmpty()) {
            newPath = L30Game.GridUtil.bfsPathToPredicate(me, (Point p) -> state.random.nextDouble() < 0.12 && state.isSafeStandingTile(p.x, p.y), (x, y) -> state.isPassableForPath(this, x, y));
         }

         if (newPath.isEmpty()) {
            newPath = L30Game.GridUtil.bfsPathToPredicate(me, (Point p) -> state.isSafeStandingTile(p.x, p.y), (x, y) -> state.isPassableForPath(this, x, y));
         }

         this.path.clear();
         this.path.addAll(newPath);
      }



      private void moveByPath
(double dt, GameState state) {
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

      static List<Point> bfsPath(Point start, Point goal, CellPassable passable) {
         return bfsPathToPredicate(start, (p) -> p.equals(goal), passable);
      }

      static List<Point> bfsPathToPredicate(Point start, Predicate<Point> goalPredicate, CellPassable passable) {
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
            List<Point> path = new ArrayList<>();

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
      private static final double BUBBLE_DURATION = (double)5.0F;
      private static final int WIN_TARGET = 3;
      final Random random = new Random();
      final TileMap map = new TileMap(15, 13);
      final List<Bomb> bombs = new ArrayList<>();
      final List<Explosion> explosions = new ArrayList<>();
      final List<PowerUp> powerUps = new ArrayList<>();
      final List<BossSkillWarning> bossSkillWarnings = new ArrayList<>();
      final HumanPlayer p1 = new HumanPlayer("P1", new Color(72, 132, 255), 1, 1);
      final HumanPlayer p2 = new HumanPlayer("P2", new Color(255, 80, 80), 13, 11);
      final BotPlayer bot = new BotPlayer("BOT", new Color(70, 197, 90), 13, 1);
      final BotPlayer bot2 = new BotPlayer("BOT2", new Color(88, 207, 114), 11, 10);
      final BotPlayer bot3 = new BotPlayer("BOT3", new Color(96, 220, 128), 3, 10);
      final BotPlayer bot4 = new BotPlayer("BOT4", new Color(120, 232, 146), 7, 8);
      final BotPlayer bot5 = new BotPlayer("BOT5", new Color(134, 236, 158), 5, 10);
      final BotPlayer bot6 = new BotPlayer("BOT6", new Color(148, 240, 170), 9, 10);
      final BotPlayer bot7 = new BotPlayer("BOT7", new Color(160, 242, 176), 5, 6);
      final BotPlayer bot8 = new BotPlayer("BOT8", new Color(172, 244, 182), 9, 6);
      final BotPlayer bot9 = new BotPlayer("BOT9", new Color(184, 246, 188), 11, 6);
      final BotPlayer bot10 = new BotPlayer("BOT10", new Color(196, 248, 194), 7, 10);
      final BotPlayer bot11 = new BotPlayer("BOT11", new Color(208, 250, 200), 5, 8);
      final BotPlayer bot12 = new BotPlayer("BOT12", new Color(220, 252, 206), 9, 8);
      final BotPlayer bot13 = new BotPlayer("BOT13", new Color(232, 254, 212), 11, 8);
      final BotPlayer bot14 = new BotPlayer("BOT14", new Color(244, 255, 218), 3, 8);
      final BotPlayer bot15 = new BotPlayer("BOT15", new Color(156, 230, 180), 13, 8);
      final BotPlayer bot16 = new BotPlayer("BOT16", new Color(168, 232, 186), 5, 4);
      final BotPlayer bot17 = new BotPlayer("BOT17", new Color(180, 234, 192), 7, 4);
      final BotPlayer bot18 = new BotPlayer("BOT18", new Color(192, 236, 198), 9, 4);
      final BotPlayer bot19 = new BotPlayer("BOT19", new Color(104, 245, 186), 11, 4);
      final BotPlayer bot20 = new BotPlayer("BOT20", new Color(118, 255, 192), 9, 6);
      Phase phase;
      Mode mode;
      int round;
      int stage;
      boolean storyCleared;
      Player roundWinner;
      private int nextBombId;
      int storyBossHp;
      int storyBossMaxHp;
      double storyBossSkillCooldown;
      private boolean pushAnimActive;
      private int pushAnimFromTx;
      private int pushAnimFromTy;
      private int pushAnimToTx;
      private int pushAnimToTy;
      private long pushAnimStartNanos;

      GameState() {
         this.phase = L30Game.GameState.Phase.START;
         this.mode = L30Game.GameState.Mode.P1_VS_P2;
         this.round = 1;
         this.stage = 1;
         this.storyCleared = false;
         this.nextBombId = 1;
         this.storyBossHp = 0;
         this.storyBossMaxHp = 0;
         this.storyBossSkillCooldown = 0.0;
      }

      boolean isP2Mode() {
         return this.mode == L30Game.GameState.Mode.P1_VS_P2;
      }

      boolean isAiMode() {
         return this.mode == L30Game.GameState.Mode.P1_VS_AI;
      }

      boolean isStoryMode() {
         return this.mode == L30Game.GameState.Mode.STORY;
      }

      boolean isStoryBoss(Player player) {
         return this.isStoryMode() && this.stage == 3 && player == this.bot;
      }

      void startNewMatch() {
         this.p1.wins = 0;
         this.p2.wins = 0;
         this.bot.wins = 0;
         this.bot2.wins = 0;
         this.bot3.wins = 0;
         this.bot4.wins = 0;
         this.bot5.wins = 0;
         this.bot6.wins = 0;
         this.bot7.wins = 0;
         this.bot8.wins = 0;
         this.bot9.wins = 0;
         this.bot10.wins = 0;
         this.bot11.wins = 0;
         this.bot12.wins = 0;
         this.bot13.wins = 0;
         this.bot14.wins = 0;
         this.bot15.wins = 0;
         this.bot16.wins = 0;
         this.bot17.wins = 0;
         this.bot18.wins = 0;
         this.bot19.wins = 0;
         this.bot20.wins = 0;
         this.round = 1;
         this.stage = 1;
         this.storyCleared = false;
         this.startRound();
      }









      void startRound() {
         this.bombs.clear();
         this.explosions.clear();
         this.bossSkillWarnings.clear();
         this.powerUps.clear();
         this.storyBossSkillCooldown = 0.0;
         this.nextBombId = 1;
         this.roundWinner = null;
         this.p1.resetForRound();
         this.p2.resetForRound();
         this.bot.resetForRound();
         this.bot2.resetForRound();
         this.bot3.resetForRound();
         this.bot4.resetForRound();
         this.bot5.resetForRound();
         this.bot6.resetForRound();
         this.bot7.resetForRound();
         this.bot8.resetForRound();
         this.bot9.resetForRound();
         this.bot10.resetForRound();
         this.bot11.resetForRound();
         this.bot12.resetForRound();
         this.bot13.resetForRound();
         this.bot14.resetForRound();
         this.bot15.resetForRound();
         this.bot16.resetForRound();
         this.bot17.resetForRound();
         this.bot18.resetForRound();
         this.bot19.resetForRound();
         this.bot20.resetForRound();
         if (this.isP2Mode()) {
            this.map.generate(this.random, new Point(this.p1.spawnTx, this.p1.spawnTy), new Point(this.p2.spawnTx, this.p2.spawnTy));
            this.deactivateStoryBots();
         } else {
            if (this.isStoryMode() && this.stage == 1) {
               this.generateStoryStageOneMap();
               this.activateStoryBots(4, (double)2.0F);
               this.placeStoryStageOneBots();
            } else if (this.isStoryMode() && this.stage == 2) {
               this.generateStoryStageTwoMap();
               this.activateStoryBots(6, (double)2.0F);
               this.placeStoryStageTwoBots();
            } else if (this.isStoryMode() && this.stage == 3) {
               this.generateStoryStageThreeMap();
               this.activateStoryBots(5, (double)2.0F);
               this.placeStoryStageThreeBots();
                this.storyBossSkillCooldown = this.nextStoryBossSkillDelay();
               this.storyBossMaxHp = 10;
               this.storyBossHp = this.storyBossMaxHp;
               this.p1.x = (double)(this.map.width / 2) + (double)0.5F;
               this.p1.y = (double)(this.map.height - 2) + (double)0.5F;
               this.p1.stepMoving = false;
               this.p1.moving = false;
               this.p1.stepProgress = (double)0.0F;
               this.p1.stepFromX = this.p1.x;
               this.p1.stepFromY = this.p1.y;
               this.p1.stepToX = this.p1.x;
               this.p1.stepToY = this.p1.y;
            } else {
               this.map.generate(this.random, new Point(this.p1.spawnTx, this.p1.spawnTy), new Point(this.bot.spawnTx, this.bot.spawnTy));
               this.deactivateStoryBots();
               this.bot.alive = true;
            }

            this.p2.alive = false;
            if (this.isStoryMode()) {
               if (this.stage == 1) {
                  this.storyBossHp = 0;
                  this.storyBossMaxHp = 0;
                  for(BotPlayer storyBot : this.storyBots()) {
                     storyBot.lives = 1;
                     storyBot.bombCapacity = 1;
                     storyBot.bombRange = 1;
                     storyBot.speed = (double)2.0F;
                  }
               } else if (this.stage == 2) {
                  this.storyBossHp = 0;
                  this.storyBossMaxHp = 0;
                  for(BotPlayer storyBot : this.storyBots()) {
                     storyBot.lives = 1;
                     storyBot.bombCapacity = 1;
                     storyBot.bombRange = 1;
                     storyBot.speed = (double)2.0F;
                  }
               } else {
                  for(BotPlayer storyBot : this.storyBots()) {
                     storyBot.lives = 1;
                     storyBot.bombCapacity = 1;
                     storyBot.bombRange = 1;
                     storyBot.speed = (double)2.0F;
                  }
                  this.storyBossMaxHp = 10;
                  this.storyBossHp = this.storyBossMaxHp;
                  this.bot.lives = 7;
                  this.bot.bombCapacity = 3;
                  this.bot.bombRange = 3;
                  this.bot.speed = (double)1.0F;
                   this.storyBossSkillCooldown = this.nextStoryBossSkillDelay();
               }
            }
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
         } else if (this.phase == L30Game.GameState.Phase.ROUND_OVER) {
            if (this.isStoryMode()) {
               this.stage = Math.min(3, this.stage + 1);
               this.startRound();
            } else if (this.p1.wins < 3 && this.p2.wins < 3 && this.bot.wins < 3) {
               ++this.round;
               this.startRound();
            } else {
               this.phase = L30Game.GameState.Phase.GAME_OVER;
            }
         } else if (this.phase == L30Game.GameState.Phase.GAME_OVER) {
            this.startNewMatch();
         }
      }





      void onRestartPressed() {
         this.startNewMatch();
      }

      boolean tryPushBox(Player player, int boxTx, int boxTy, int dirX, int dirY) {
         if (this.map.get(boxTx, boxTy) != L30Game.TileType.PUSHABLE) {
            return false;
         } else {
            int nextTx = boxTx + dirX;
            int nextTy = boxTy + dirY;
            if (!this.map.inBounds(nextTx, nextTy) || !this.map.isWalkableTile(nextTx, nextTy) || this.bombAt(nextTx, nextTy) != null || this.hasLivingPlayerAt(nextTx, nextTy, player)) {
               return false;
            } else {
               this.pushAnimActive = true;
               this.pushAnimFromTx = boxTx;
               this.pushAnimFromTy = boxTy;
               this.pushAnimToTx = nextTx;
               this.pushAnimToTy = nextTy;
               this.pushAnimStartNanos = System.nanoTime();
               this.map.setPushable(nextTx, nextTy);
               this.map.set(boxTx, boxTy, L30Game.TileType.EMPTY);
               return true;
            }
         }
      }

      boolean isPushAnimationActive(long nowNanos) {
         if (!this.pushAnimActive) {
            return false;
         } else if ((double)(nowNanos - this.pushAnimStartNanos) / (double)1.0E9F >= 0.12) {
            this.pushAnimActive = false;
            return false;
         } else {
            return true;
         }
      }

      double pushAnimationProgress(long nowNanos) {
         return Math.min(1.0, Math.max(0.0, (double)(nowNanos - this.pushAnimStartNanos) / (double)1.2E8F));
      }


      private boolean hasLivingPlayerAt(int tx, int ty, Player ignore) {
         for(Player player : this.allPlayers()) {
            if (player != ignore && player.alive && player.tileX() == tx && player.tileY() == ty) {
               return true;
            }
         }

         return false;
      }

      void debugAdvanceStoryStage() {
         if (!this.isStoryMode() || this.phase != L30Game.GameState.Phase.PLAYING) {
            return;
         }

         if (this.stage < 3) {
            ++this.stage;
            this.startRound();
         } else {
            this.storyCleared = true;
            this.phase = L30Game.GameState.Phase.GAME_OVER;
         }
      }


      private List<BotPlayer> storyBots() {
         return Arrays.asList(this.bot, this.bot2, this.bot3, this.bot4, this.bot5, this.bot6, this.bot7, this.bot8, this.bot9, this.bot10, this.bot11, this.bot12, this.bot13, this.bot14, this.bot15, this.bot16, this.bot17, this.bot18, this.bot19, this.bot20);
      }

      private void deactivateStoryBots() {
         for(BotPlayer storyBot : this.storyBots()) {
            storyBot.alive = false;
            storyBot.bubbleTrapped = false;
            storyBot.bubbleTimeLeft = (double)0.0F;
            storyBot.activeBombs = 0;
            storyBot.passableBombIds.clear();
         }
      }

      private void activateStoryBots(int count, double speed) {
         this.deactivateStoryBots();
         List<BotPlayer> storyBots = this.storyBots();

         for(int i = 0; i < count && i < storyBots.size(); ++i) {
            BotPlayer storyBot = (BotPlayer)storyBots.get(i);
            storyBot.alive = true;
            storyBot.bubbleTrapped = false;
            storyBot.bubbleTimeLeft = (double)0.0F;
            storyBot.activeBombs = 0;
            storyBot.passableBombIds.clear();
            storyBot.speed = speed;
         }
      }





      private void generateStoryStageOneMap() {
         int gapStartX = this.map.width / 2 - 1;

         for(int y = 0; y < this.map.height; ++y) {
            for(int x = 0; x < this.map.width; ++x) {
               boolean border = x == 0 || y == 0 || x == this.map.width - 1 || y == this.map.height - 1;
               boolean fourthRowWall = y == 3 && (x < gapStartX || x > gapStartX + 1);
               this.map.set(x, y, border || fourthRowWall ? L30Game.TileType.SOLID : L30Game.TileType.EMPTY);
            }
         }

         for(int y = 4; y < this.map.height - 1; ++y) {
            this.map.setBreakable(2, y, 0);
            this.map.setBreakable(4, y, 1);
            this.map.setBreakable(8, y, 0);
            this.map.setBreakable(10, y, 1);
            this.map.setBreakable(12, y, 0);
         }
      }

      private void generateStoryStageTwoMap() {
         for(int y = 0; y < this.map.height; ++y) {
            for(int x = 0; x < this.map.width; ++x) {
               boolean border = x == 0 || y == 0 || x == this.map.width - 1 || y == this.map.height - 1;
               boolean thirdColumnWall = x == 2 && y != this.map.height / 2;
               this.map.set(x, y, border || thirdColumnWall ? L30Game.TileType.SOLID : L30Game.TileType.EMPTY);
            }
         }

         int[][] heartTiles = new int[][]{{5, 1}, {6, 1}, {7, 1}, {9, 1}, {10, 1}, {11, 1}, {4, 2}, {8, 2}, {12, 2}, {3, 3}, {13, 3}, {3, 4}, {13, 4}, {4, 5}, {12, 5}, {5, 6}, {11, 6}, {6, 7}, {10, 7}, {7, 8}, {9, 8}, {8, 9}};

         for(int i = 0; i < heartTiles.length; ++i) {
            int[] tile = heartTiles[i];
            int patternIndex = i % 3;
            if (patternIndex == 2) {
               this.map.setPushable(tile[0], tile[1]);
            } else {
               this.map.setBreakable(tile[0], tile[1], patternIndex);
            }
         }
      }


      private void generateStoryStageThreeMap() {
         int gapStartX = this.map.width / 2 - 1;
         int lowerWallY = this.map.height - 4;

         for(int y = 0; y < this.map.height; ++y) {
            for(int x = 0; x < this.map.width; ++x) {
               boolean border = x == 0 || y == 0 || x == this.map.width - 1 || y == this.map.height - 1;
               boolean lowerWall = y == lowerWallY && (x < gapStartX || x > gapStartX + 1);
               this.map.set(x, y, border || lowerWall ? L30Game.TileType.SOLID : L30Game.TileType.EMPTY);
            }
         }

         this.map.setPushable(gapStartX, lowerWallY);
         this.map.setPushable(gapStartX + 1, lowerWallY);

         Point[] stageThreeX = new Point[]{new Point(3, 2), new Point(4, 3), new Point(5, 4), new Point(6, 5), new Point(8, 5), new Point(9, 4), new Point(10, 3), new Point(11, 2), new Point(3, 10), new Point(4, 9), new Point(5, 8), new Point(6, 7), new Point(8, 7), new Point(9, 8), new Point(10, 9), new Point(11, 10)};

         for(int i = 0; i < stageThreeX.length; ++i) {
            Point block = stageThreeX[i];
            this.map.setBreakable(block.x, block.y, i);
         }
      }

      private void placeStoryStageOneBots() {
         List<Point> candidates = new ArrayList<>();


         for(int y = 4; y < this.map.height - 1; ++y) {
            for(int x = 1; x < this.map.width - 1; ++x) {
               if (this.map.get(x, y) == L30Game.TileType.EMPTY && (x != this.p1.spawnTx || y != this.p1.spawnTy)) {
                  candidates.add(new Point(x, y));
               }
            }
         }

         Collections.shuffle(candidates, this.random);
         List<BotPlayer> storyBots = this.storyBots();

         for(int i = 0; i < storyBots.size() && i < candidates.size(); ++i) {
            BotPlayer storyBot = (BotPlayer)storyBots.get(i);
            Point spawn = (Point)candidates.get(i);
            this.placeStoryBotAt(storyBot, spawn);
         }
      }



      private void placeStoryStageTwoBots() {
         List<Point> candidates = new ArrayList<>(Arrays.asList(new Point(6, 3), new Point(7, 3), new Point(9, 3), new Point(10, 3), new Point(5, 4), new Point(6, 4), new Point(7, 4), new Point(8, 4), new Point(9, 4), new Point(10, 4), new Point(11, 4), new Point(6, 5), new Point(7, 5), new Point(8, 5), new Point(9, 5), new Point(10, 5), new Point(7, 6), new Point(8, 6), new Point(9, 6)));
         candidates.removeIf((point) -> this.map.get(point.x, point.y) != L30Game.TileType.EMPTY || point.x == this.p1.spawnTx && point.y == this.p1.spawnTy);
         Collections.shuffle(candidates, this.random);
         int candidateIndex = 0;

         for(BotPlayer storyBot : this.storyBots()) {
            if (!storyBot.alive) {
               continue;
            }

            if (candidateIndex >= candidates.size()) {
               storyBot.alive = false;
               continue;
            }

            Point spawn = (Point)candidates.get(candidateIndex++);
            this.placeStoryBotAt(storyBot, spawn);
         }
      }

      private void placeStoryStageThreeBots() {
         this.placeStoryBotAt(this.bot, new Point(7, 6));
         List<Point> candidates = new ArrayList<>(Arrays.asList(new Point(2, 2), new Point(12, 2), new Point(2, 5), new Point(12, 5), new Point(2, 8), new Point(12, 8), new Point(3, 7), new Point(11, 7)));
         candidates.removeIf((point) -> this.map.get(point.x, point.y) != L30Game.TileType.EMPTY || point.x == this.p1.spawnTx && point.y == this.p1.spawnTy || point.x == this.bot.tileX() && point.y == this.bot.tileY());
         Collections.shuffle(candidates, this.random);
         List<BotPlayer> minions = Arrays.asList(this.bot2, this.bot3, this.bot4, this.bot5);
         int candidateIndex = 0;

         for(BotPlayer storyBot : minions) {
            storyBot.alive = candidateIndex < candidates.size();
            if (storyBot.alive) {
               this.placeStoryBotAt(storyBot, (Point)candidates.get(candidateIndex++));
            }
         }

         for(BotPlayer storyBot : Arrays.asList(this.bot6, this.bot7, this.bot8, this.bot9, this.bot10, this.bot11, this.bot12, this.bot13, this.bot14, this.bot15, this.bot16, this.bot17, this.bot18, this.bot19, this.bot20)) {
            storyBot.alive = false;
         }
      }


      private void placeStoryBotAt(BotPlayer storyBot, Point spawn) {
         storyBot.x = (double)spawn.x + (double)0.5F;
         storyBot.y = (double)spawn.y + (double)0.5F;
         storyBot.stepMoving = false;
         storyBot.moving = false;
         storyBot.stepProgress = (double)0.0F;
         storyBot.stepFromX = storyBot.x;
         storyBot.stepFromY = storyBot.y;
         storyBot.stepToX = storyBot.x;
         storyBot.stepToY = storyBot.y;
      }

      private BotPlayer findInactiveStoryBot(BotPlayer exclude) {
         for(BotPlayer storyBot : this.storyBots()) {
            if (storyBot != exclude && !storyBot.alive) {
               return storyBot;
            }
         }

         return null;
      }

      private boolean isStorySpawnTileAvailable(int x, int y, Player ignore) {
         if (!this.map.isWalkableTile(x, y)) {
            return false;
         }

         if (this.p1.alive && this.p1 != ignore && this.p1.tileX() == x && this.p1.tileY() == y) {
            return false;
         }

         for(BotPlayer storyBot : this.storyBots()) {
            if (storyBot != ignore && storyBot.alive && storyBot.tileX() == x && storyBot.tileY() == y) {
               return false;
            }
         }

         return true;
      }

      private void spawnStoryStageTwoSplit(BotPlayer source) {
         BotPlayer clone = this.findInactiveStoryBot(source);
         if (clone == null) {
            return;
         }

         List<Point> candidates = new ArrayList<>();
         int baseX = source.tileX();
         int baseY = source.tileY();
         int[][] directions = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

         for(int[] direction : directions) {
            int x = baseX + direction[0];
            int y = baseY + direction[1];
            if (x >= 3 && this.isStorySpawnTileAvailable(x, y, source)) {
               candidates.add(new Point(x, y));
            }
         }

         if (candidates.isEmpty()) {
            for(int y = 1; y < this.map.height - 1; ++y) {
               for(int x = 3; x < this.map.width - 1; ++x) {
                  if (this.isStorySpawnTileAvailable(x, y, source)) {
                     candidates.add(new Point(x, y));
                  }
               }
            }
         }

         if (!candidates.isEmpty()) {
            Point spawn = (Point)candidates.get(this.random.nextInt(candidates.size()));
            clone.alive = true;
            clone.bubbleTrapped = false;
            clone.bubbleTimeLeft = (double)0.0F;
            clone.lives = 1;
            clone.bombCapacity = 1;
            clone.bombRange = 1;
            clone.speed = (double)2.0F;
            clone.activeBombs = 0;
            clone.passableBombIds.clear();
            this.placeStoryBotAt(clone, spawn);
         }
      }

      private void placeStoryStageOneBot() {
         List<Point> candidates = new ArrayList<>();

         for(int y = 4; y < this.map.height - 1; ++y) {
            for(int x = 1; x < this.map.width - 1; ++x) {
               if (this.map.get(x, y) == L30Game.TileType.EMPTY && (x != this.p1.spawnTx || y != this.p1.spawnTy)) {
                  candidates.add(new Point(x, y));
               }
            }
         }

         if (!candidates.isEmpty()) {
            Point spawn = (Point)candidates.get(this.random.nextInt(candidates.size()));
            this.bot.x = (double)spawn.x + (double)0.5F;
            this.bot.y = (double)spawn.y + (double)0.5F;
            this.bot.stepMoving = false;
            this.bot.moving = false;
            this.bot.stepProgress = (double)0.0F;
            this.bot.stepFromX = this.bot.x;
            this.bot.stepFromY = this.bot.y;
            this.bot.stepToX = this.bot.x;
            this.bot.stepToY = this.bot.y;
         }
      }







      void update(double dt, boolean p1Up, boolean p1Down, boolean p1Left, boolean p1Right, boolean p2Up, boolean p2Down, boolean p2Left, boolean p2Right, boolean p1Bomb, boolean p2Bomb) {
         if (this.phase == L30Game.GameState.Phase.PLAYING) {
            for(Player player : this.allPlayers()) {
               player.updateInvincibility(dt);
               player.updateBubble(dt);
               player.updateBombPassThrough(this);
            }

            this.p1.tryMove((double)((p1Right ? 1 : 0) - (p1Left ? 1 : 0)), (double)((p1Down ? 1 : 0) - (p1Up ? 1 : 0)), dt, this);
            if (this.isP2Mode()) {
               this.p2.tryMove((double)((p2Right ? 1 : 0) - (p2Left ? 1 : 0)), (double)((p2Down ? 1 : 0) - (p2Up ? 1 : 0)), dt, this);
            } else if (this.isStoryMode()) {
               for(BotPlayer storyBot : this.storyBots()) {
                  if (storyBot.alive) {
                     storyBot.updateAI(dt, this);
                  }
               }
            } else if (this.bot.alive) {
               this.bot.updateAI(dt, this);
            }

            if (p1Bomb) {
               this.placeBomb(this.p1, this.p1.tileX(), this.p1.tileY());
            }

             this.updateStoryBossSkill(dt);
            if (this.isP2Mode() && p2Bomb) {
               this.placeBomb(this.p2, this.p2.tileX(), this.p2.tileY());
            }

            this.updateBombs(dt);
            this.updateExplosions(dt);
            for(Player player : this.allPlayers()) {
               this.handlePowerUpPickup(player);
            }

            this.applyExplosionDamage();
            this.updateBubbleInteractions();
            this.checkRoundEnd();
         }
      }



      private void updateBombs(double dt) {
         List<Bomb> toExplode = new ArrayList<>();

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

      private double nextStoryBossSkillDelay() {
         return 2.8 + this.random.nextDouble() * 2.4;
      }

      private void updateStoryBossSkill(double dt) {
         if (this.isStoryMode() && this.stage == 3 && this.bot.alive && !this.bot.bubbleTrapped && this.storyBossHp > 0) {
            this.storyBossSkillCooldown -= dt;
            if (this.storyBossSkillCooldown <= (double)0.0F) {
               this.spawnStoryBossSkillWarning();
               this.storyBossSkillCooldown = this.nextStoryBossSkillDelay();
            }
         }

         Iterator<BossSkillWarning> warningIt = this.bossSkillWarnings.iterator();

         while(warningIt.hasNext()) {
            BossSkillWarning warning = (BossSkillWarning)warningIt.next();
            warning.timeLeft -= dt;
            if (warning.timeLeft <= (double)0.0F) {
               this.detonateStoryBossSkill(warning);
               warningIt.remove();
            }
         }
      }

      private void spawnStoryBossSkillWarning() {
         List<Point> candidates = new ArrayList<>();
         int bossTx = this.bot.tileX();
         int bossTy = this.bot.tileY();

         for(int dy = -2; dy <= 2; ++dy) {
            for(int dx = -2; dx <= 2; ++dx) {
               if (Math.max(Math.abs(dx), Math.abs(dy)) != 2) {
                  continue;
               }

               int centerX = bossTx + dx;
               int centerY = bossTy + dy;
               if (!this.canPlaceStoryBossSkillAt(centerX, centerY)) {
                  continue;
               }

               candidates.add(new Point(centerX, centerY));
            }
         }

         if (candidates.isEmpty()) {
            return;
         }

         Point center = (Point)candidates.get(this.random.nextInt(candidates.size()));
         BossSkillWarning warning = new BossSkillWarning(0.9);

         for(int y = center.y - 1; y <= center.y + 1; ++y) {
            for(int x = center.x - 1; x <= center.x + 1; ++x) {
               warning.cells.add(new Point(x, y));
            }
         }

         this.bossSkillWarnings.add(warning);
      }

      private boolean canPlaceStoryBossSkillAt(int centerX, int centerY) {
         for(int y = centerY - 1; y <= centerY + 1; ++y) {
            for(int x = centerX - 1; x <= centerX + 1; ++x) {
               if (!this.map.inBounds(x, y)) {
                  return false;
               }

               if (this.map.get(x, y) == L30Game.TileType.SOLID) {
                  return false;
               }
            }
         }

         return true;
      }

      private void detonateStoryBossSkill(BossSkillWarning warning) {
         Explosion explosion = new Explosion(0.4);
         explosion.hurtsStoryBoss = false;
         explosion.cells.addAll(warning.cells);
         this.explosions.add(explosion);
         Set<Integer> chainVisited = new HashSet<>();

         for(Point c : warning.cells) {
            TileType t = this.map.get(c.x, c.y);
            if (t == L30Game.TileType.BREAKABLE || t == L30Game.TileType.PUSHABLE) {
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





      private void damageStoryBossByExplosion() {
         if (!this.bot.alive || this.bot.bubbleTrapped || this.bot.isInvincible()) {
            return;
         }

         if (this.storyBossHp > 1) {
            --this.storyBossHp;
            this.bot.invincibleTime = 0.65;
         } else if (this.storyBossHp == 1) {
            this.storyBossHp = 0;
            this.bot.trapInBubble(BUBBLE_DURATION);
         }
      }


      private boolean touchesExplosion(Player player, Explosion explosion) {
         if (this.isStoryBoss(player)) {
            double reach = this.contactRadius(player) + 0.5;
            double reachSq = reach * reach;

            for(Point cell : explosion.cells) {
               double dx = player.x - ((double)cell.x + (double)0.5F);
               double dy = player.y - ((double)cell.y + (double)0.5F);
               if (dx * dx + dy * dy <= reachSq) {
                  return true;
               }
            }

            return false;
         }

         return explosion.contains(player.tileX(), player.tileY());
      }


      private void applyExplosionDamage() {
         if (this.isStoryMode()) {
            for(Explosion e : this.explosions) {
               if (this.p1.alive && !this.p1.bubbleTrapped && e.contains(this.p1.tileX(), this.p1.tileY())) {
                  this.p1.trapInBubble(BUBBLE_DURATION);
               }

                for(BotPlayer storyBot : this.storyBots()) {
                   if (storyBot.alive && this.touchesExplosion(storyBot, e)) {
                      if (this.isStoryBoss(storyBot)) {
                         if (!e.hurtsStoryBoss) {
                            continue;
                         }
                         this.damageStoryBossByExplosion();
                      } else if (!storyBot.bubbleTrapped) {
                         storyBot.trapInBubble(BUBBLE_DURATION);
                      }
                   }
               }
            }

            return;
         }

         for(Player p : this.allPlayers()) {
            if (p.alive && !p.isInvincible() && !p.bubbleTrapped) {
               int tx = p.tileX();
               int ty = p.tileY();

               for(Explosion e : this.explosions) {
                  if (e.contains(tx, ty)) {
                     p.trapInBubble(BUBBLE_DURATION);
                     break;
                  }
               }
            }
         }

      }





      private void handlePowerUpPickup(Player player) {
         if (player.alive && !player.bubbleTrapped) {
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





      private void updateBubbleInteractions() {
         if (this.isStoryMode()) {
            if (this.p1.alive && this.p1.bubbleTrapped) {
               if (this.p1.bubbleTimeLeft <= (double)0.0F) {
                  this.popBubble(this.p1);
               } else {
                  for(BotPlayer storyBot : this.storyBots()) {
                     if (storyBot.alive && !storyBot.bubbleTrapped && this.touchesBubble(storyBot, this.p1)) {
                        this.popBubble(this.p1);
                        break;
                     }
                  }
               }
            }

            this.handleStoryContactRules();

            for(BotPlayer storyBot : this.storyBots()) {
               if (storyBot.alive && storyBot.bubbleTrapped && storyBot.bubbleTimeLeft <= (double)0.0F) {
                  if (this.isStoryBoss(storyBot)) {
                     this.defeatStoryBossCompletely();
                  } else {
                     this.releaseStoryEnemyBubble(storyBot);
                  }
               }
            }

            return;
         }

         for(Player p : this.allPlayers()) {
            if (p.alive && p.bubbleTrapped) {
               if (p.bubbleTimeLeft <= (double)0.0F) {
                  this.popBubble(p);
               } else {
                  for(Player other : this.allPlayers()) {
                     if (other != p && other.alive && !other.bubbleTrapped && this.touchesBubble(other, p)) {
                        this.popBubble(p);
                        break;
                     }
                  }
               }
            }
         }

      }





      private boolean touchesBubble(Player mover, Player trapped) {
         double dx = mover.x - trapped.x;
         double dy = mover.y - trapped.y;
         double reach = this.contactRadius(mover) + this.contactRadius(trapped);
         return dx * dx + dy * dy <= reach * reach;
      }

      private double contactRadius(Player player) {
         if (this.isStoryBoss(player)) {
            return player.bubbleTrapped ? 0.95 : 1.05;
         }

         return player.bubbleTrapped ? 0.42 : 0.35;
      }
      private void popBubble(Player player) {
         if (player.alive && player.bubbleTrapped) {
            this.scatterDroppedPowerUps(player);
            player.burstBubble();
         }
      }





      private void releaseStoryEnemyBubble(BotPlayer player) {
         player.bubbleTrapped = false;
         player.bubbleTimeLeft = (double)0.0F;
         player.moving = false;
         player.stepMoving = false;
         player.stepProgress = (double)0.0F;
         player.stepFromX = player.x;
         player.stepFromY = player.y;
         player.stepToX = player.x;
         player.stepToY = player.y;
         if (this.stage == 2) {
            this.spawnStoryStageTwoSplit(player);
         }
      }
      private void defeatStoryBossCompletely() {
         this.storyBossHp = 0;
         this.bot.alive = false;
         this.bot.bubbleTrapped = false;
         this.bot.bubbleTimeLeft = (double)0.0F;
         this.bot.moving = false;
         this.bot.stepMoving = false;
         this.bot.stepProgress = (double)0.0F;
      }

      private void damageStoryBoss() {
         if (this.storyBossHp > 0) {
            --this.storyBossHp;
         }

         if (this.storyBossHp <= 0) {
            this.defeatStoryBossCompletely();
         } else {
            this.releaseStoryEnemyBubble(this.bot);
         }
      }


      private void defeatStoryPlayer() {
         this.p1.alive = false;
         this.p1.bubbleTrapped = false;
         this.p1.bubbleTimeLeft = (double)0.0F;
         this.p1.lives = 0;
      }



      private void handleStoryContactRules() {
         if (!this.isStoryMode() || !this.p1.alive || this.p1.bubbleTrapped) {
            return;
         }

         for(BotPlayer storyBot : this.storyBots()) {
            if (!storyBot.alive) {
               continue;
            }

            if (storyBot.bubbleTrapped) {
               if (this.touchesBubble(this.p1, storyBot)) {
                  if (this.isStoryBoss(storyBot)) {
                     this.damageStoryBoss();
                  } else {
                     storyBot.alive = false;
                     storyBot.bubbleTrapped = false;
                     storyBot.bubbleTimeLeft = (double)0.0F;
                  }
               }
            } else if (this.touchesBubble(this.p1, storyBot)) {
               this.defeatStoryPlayer();
               return;
            }
         }
      }


      private void checkRoundEnd() {
         if (this.isStoryMode()) {
            if (!this.p1.alive) {
               this.storyCleared = false;
               this.roundWinner = null;
               this.phase = L30Game.GameState.Phase.GAME_OVER;
               return;
            }

            boolean storyEnemiesAlive = false;
            for(BotPlayer storyBot : this.storyBots()) {
               if (storyBot.alive) {
                  storyEnemiesAlive = true;
                  break;
               }
            }

            if (!storyEnemiesAlive) {
               this.roundWinner = this.p1;
               if (this.stage < 3) {
                  this.phase = L30Game.GameState.Phase.ROUND_OVER;
               } else {
                  this.storyCleared = true;
                  this.phase = L30Game.GameState.Phase.GAME_OVER;
               }
            }

            return;
         }

         List<Player> alive = new ArrayList<>();
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
                  if (t == L30Game.TileType.BREAKABLE || t == L30Game.TileType.PUSHABLE) {
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

         long nowNanos = System.nanoTime();

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
               if (t == L30Game.TileType.BREAKABLE || t == L30Game.TileType.PUSHABLE) {
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
         List<Point> path = L30Game.GridUtil.bfsPathToPredicate(start, (Point p) -> !danger.contains(p) && this.isSafeStandingTile(p.x, p.y), (x, y) -> this.isPassableForPathWithVirtualBomb(owner, x, y, bombTx, bombTy));
         return path.size() > 1;
      }

      private boolean isPassableForPathWithVirtualBomb(Player player, int x, int y, int virtualBombTx, int virtualBombTy) {
         if (!this.map.inBounds(x, y)) {
            return false;
         } else {
            TileType t = this.map.get(x, y);
            if (t != L30Game.TileType.SOLID && t != L30Game.TileType.BREAKABLE && t != L30Game.TileType.PUSHABLE) {
               if (!this.hasLargeBodyClearance(player, x, y)) {
                  return false;
               }
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
            if (t != L30Game.TileType.SOLID && t != L30Game.TileType.BREAKABLE && t != L30Game.TileType.PUSHABLE) {
               if (!this.hasLargeBodyClearance(player, tx, ty)) {
                  return false;
               }
               Bomb b = this.bombAt(tx, ty);
               return b == null || player.passableBombIds.contains(b.id);
            } else {
               return false;
            }
         }
      }

      boolean isBlockedForPlayer(Player player, double nx, double ny) {
         double half = this.collisionHalf(player);
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
               if (t == L30Game.TileType.SOLID || t == L30Game.TileType.BREAKABLE || t == L30Game.TileType.PUSHABLE) {
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

      private boolean hasLargeBodyClearance(Player player, int tx, int ty) {
         return true;
      }

      boolean isPassableForPath(Player player, int x, int y) {
         if (!this.map.inBounds(x, y)) {
            return false;
         } else {
            TileType t = this.map.get(x, y);
            if (t != L30Game.TileType.SOLID && t != L30Game.TileType.BREAKABLE && t != L30Game.TileType.PUSHABLE) {
               if (!this.hasLargeBodyClearance(player, x, y)) {
                  return false;
               }
               Bomb b = this.bombAt(x, y);
               return b == null || player.passableBombIds.contains(b.id);
            } else {
               return false;
            }
         }
      }

      private double collisionHalf(Player player) {
         if (this.isStoryBoss(player)) {
            return player.bubbleTrapped ? 0.34 : 0.38;
         }

         return 0.26;
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
            TileType adjacent = this.map.get(tx + dx[i], ty + dy[i]);
            if (adjacent == L30Game.TileType.BREAKABLE || adjacent == L30Game.TileType.PUSHABLE) {
               return true;
            }
         }

         return false;
      }



      Player findNearestEnemy(Player from) {
         if (this.isStoryMode()) {
            if (from == this.p1) {
               Player nearest = null;
               int best = Integer.MAX_VALUE;

               for(BotPlayer storyBot : this.storyBots()) {
                  if (storyBot.alive) {
                     int d = Math.abs(from.tileX() - storyBot.tileX()) + Math.abs(from.tileY() - storyBot.tileY());
                     if (d < best) {
                        best = d;
                        nearest = storyBot;
                     }
                  }
               }

               return nearest;
            }

            return this.p1.alive ? this.p1 : null;
         }

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





      List<Player> allPlayers() {
         if (this.isP2Mode()) {
            return Arrays.asList(this.p1, this.p2);
         } else if (this.isStoryMode()) {
            List<Player> players = new ArrayList<>();
            players.add(this.p1);
            players.addAll(this.storyBots());
            return players;
         } else {
            return Arrays.asList(this.p1, this.bot);
         }
      }



      String modeText() {
         return this.isP2Mode() ? "P1 vs P2" : (this.isStoryMode() ? "Story Mode" : "P1 vs AI");
      }

      String progressText() {
         return this.isStoryMode() ? "Stage: " + this.stage + "/3" : "Round: " + this.round;
      }

      String winnerText() {
         return this.isStoryMode() ? "Stage " + this.stage + " Clear!" : (this.roundWinner == null ? "Draw" : "Winner: " + this.roundWinner.name);
      }

      String championText() {
         if (this.isStoryMode()) {
            return this.storyCleared ? "Story Cleared!" : "P1 Defeated";
         }

         Player champ = this.p1;
         if (this.p2.wins > champ.wins) {
            champ = this.p2;
         }

         if (this.bot.wins > champ.wins) {
            champ = this.bot;
         }

         return champ.name + " is Champion!";
      }

      static enum Phase {


         START,
         PLAYING,
         PAUSED,
         ROUND_OVER,
         GAME_OVER;
      }

      static enum Mode {
         P1_VS_P2,
         P1_VS_AI,
         STORY;
      }
   }

   static class GamePanel extends JPanel {
      private final GameState state = new GameState();
      private final Timer timer;
      private final SpriteSet p1Sprites;
      private final SpriteSet p2Sprites;
      private final SpriteSet aiSprites;
      private final Image storyBotIdleImg;
      private final Image storyBotJumpImg;
      private final Image storyBotLandImg;
      private final Image kingStoryBotIdleImg;
      private final Image kingStoryBotJumpImg;
      private final Image storyBotBubbleImg1;
      private final Image storyBotBubbleImg2;
      private final Image kingStoryBotBubbleImg1;
      private final Image kingStoryBotBubbleImg2;
      private final Image bombImg1;
      private final Image bombImg2;
      private final Image explosionImg;
      private final Image floorBgImg;
      private final Image solidBlockImg;
      private final Image[] breakableBlockImgs;
      private final Image pushableBoxImg;
      private final Image p1BubbleImg;
      private final Image p2BubbleImg;
      private final Image aiBubbleImg;
      private final Image bombCountItemImg;
      private final Image bombRangeItemImg;
      private final Image speedItemImg;
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
         this.p1Sprites = new SpriteSet(this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uD50C\uB808\uC774\uC5B41.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uD50C\uB808\uC774\uC5B41\uAC77.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uD50C\uB808\uC774\uC5B41\uB4B7.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uD50C\uB808\uC774\uC5B41\uB4B7\uAC77.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uD50C\uB808\uC774\uC5B41\uC67C.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uD50C\uB808\uC774\uC5B41\uC67C\uAC77.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uD50C\uB808\uC774\uC5B41\uC624.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uD50C\uB808\uC774\uC5B41\uC624\uAC77.png"));
         this.p2Sprites = new SpriteSet(this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uD50C\uB808\uC774\uC5B42.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uD50C\uB808\uC774\uC5B42\uAC77.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uD50C\uB808\uC774\uC5B42\uB4B7.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uD50C\uB808\uC774\uC5B42\uB4B7\uAC77.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uD50C\uB808\uC774\uC5B42\uC67C.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uD50C\uB808\uC774\uC5B42\uC67C\uAC77.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uD50C\uB808\uC774\uC5B42\uC624.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uD50C\uB808\uC774\uC5B42\uC624\uAC77.png"));
         this.aiSprites = new SpriteSet(this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\AI.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\AI\uAC77.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\AI\uB4B7.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\AI\uB4B7\uAC77.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\AI\uC67C.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\AI\uC67C\uAC77.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\AI\uC624.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\AI\uC624\uAC77.png"));
         this.storyBotIdleImg = this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uAF2C\uBB3C\uC774\uAE30\uBCF8.png");
         this.storyBotBubbleImg1 = this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\꼬물이갇힘.png");
         this.storyBotBubbleImg2 = this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\꼬물이갇힘2.png");
         this.storyBotJumpImg = this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uAF2C\uBB3C\uC774\uB6F0\uC5B4.png");
         this.storyBotLandImg = this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uAF2C\uBB3C\uC774\uCC29\uC9C0.png");
         this.kingStoryBotBubbleImg1 = this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\왕꼬물이갇힘.png");
         this.kingStoryBotBubbleImg2 = this.loadImage("C:\\Users\\KOSMO\\Desktop\\크아\\왕꼬물이갇힘2.png");
         this.kingStoryBotIdleImg = this.loadImage("C:\\Users\\KOSMO\\Desktop\\\ud06c\uc544\\\uc655\uaf2c\ubb3c\uc774\uae30\ubcf8\u002e\u0070\u006e\u0067");
         this.kingStoryBotJumpImg = this.loadImage("C:\\Users\\KOSMO\\Desktop\\\ud06c\uc544\\\uc655\uaf2c\ubb3c\uc774\uca5c\uc5b4\u002e\u0070\u006e\u0067");
         this.bombImg1 = this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uD3ED\uD0C41.png");
         this.solidBlockImg = this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uC548\uBD80\uC11C\uC9C0\uB294\uBE14\uB7ED.png");
         this.breakableBlockImgs = new Image[]{this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uBE68\uAC04\uBE14\uB7ED.png"), this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uC8FC\uD669\uBE14\uB7ED.png")};
         this.pushableBoxImg = this.loadImage("C:\\Users\\KOSMO\\Desktop\\\ud06c\uc544\\\ub098\ubb34\ubc15\uc2a4\u002e\u0070\u006e\u0067");
         this.bombImg2 = this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uD3ED\uD0C42.png");
         this.explosionImg = this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uD3ED\uD0C4\uD130\uC9D0.png");
         this.floorBgImg = this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uBAA8\uB798\uC0AC\uC7A5.png");
         this.p1BubbleImg = this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\P1\uBC84\uBE14\uC774\uBBF8\uC9C0.png");
         this.p2BubbleImg = this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\P2\uBC84\uBE14\uC774\uBBF8\uC9C0.png");
         this.aiBubbleImg = this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\AI\uBC84\uBE14\uC774\uBBF8\uC9C0.png");
         this.bombCountItemImg = this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uAC1C\uC218.png");
         this.bombRangeItemImg = this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uC904\uAE30.png");
         this.speedItemImg = this.loadImage("C:\\Users\\KOSMO\\Desktop\\\uD06C\uC544\\\uC18D\uB3C4.png");
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
               this.state.onEnterPressed(this.startMenuIndex == 0 ? L30Game.GameState.Mode.P1_VS_P2 : (this.startMenuIndex == 1 ? L30Game.GameState.Mode.P1_VS_AI : L30Game.GameState.Mode.STORY));
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
         this.bindPress(im, am, "MODE_3", "3", () -> {
            if (this.state.phase == L30Game.GameState.Phase.START) {
               this.startMenuIndex = 2;
            }

         });
         GameState var10005 = this.state;
         Objects.requireNonNull(var10005);
         this.bindPress(im, am, "PAUSE", "P", var10005::togglePause);
         var10005 = this.state;
         Objects.requireNonNull(var10005);
         this.bindPress(im, am, "RESTART", "R", var10005::onRestartPressed);
         this.bindPress(im, am, "ESC", "ESCAPE", () -> System.exit(0));
          this.bindPress(im, am, "DEBUG_NEXT_STAGE", "N", () -> this.state.debugAdvanceStoryStage());
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
          this.drawBossSkillWarnings(g2);
         this.drawExplosions(g2);
         this.drawPlayers(g2);
          this.drawStoryBossHpBar(g2);
         this.drawHud(g2);
         this.drawOverlay(g2);
         g2.dispose();
      }
      private void drawMap(Graphics2D g2) {
         int ts = 40;
         int floorTileSize = 180;
         int mapPixelWidth = 15 * ts;
         int mapPixelHeight = 13 * ts;
         long nowNanos = System.nanoTime();
         boolean drawPushAnimation = this.state.isPushAnimationActive(nowNanos);
         if (this.floorBgImg != null) {
            for(int y = 0; y < mapPixelHeight; y += floorTileSize) {
               for(int x = 0; x < mapPixelWidth; x += floorTileSize) {
                  g2.drawImage(this.floorBgImg, x, y, floorTileSize, floorTileSize, (ImageObserver)null);
               }
            }
         } else {
            g2.setColor(new Color(225, 230, 216));
            g2.fillRect(0, 0, mapPixelWidth, mapPixelHeight);
         }

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
               } else if (t == L30Game.TileType.PUSHABLE) {
                  if (drawPushAnimation && x == this.state.pushAnimToTx && y == this.state.pushAnimToTy) {
                     continue;
                  }
                  if (this.pushableBoxImg != null) {
                     g2.drawImage(this.pushableBoxImg, px, py, ts, ts, (ImageObserver)null);
                  } else {
                     g2.setColor(new Color(156, 112, 68));
                     g2.fillRect(px, py, ts, ts);
                  }
               }
            }
         }

         if (drawPushAnimation) {
            double progress = this.state.pushAnimationProgress(nowNanos);
            int fromPx = this.state.pushAnimFromTx * ts;
            int fromPy = this.state.pushAnimFromTy * ts;
            int toPx = this.state.pushAnimToTx * ts;
            int toPy = this.state.pushAnimToTy * ts;
            int drawX = (int)Math.round((double)fromPx + (double)(toPx - fromPx) * progress);
            int drawY = (int)Math.round((double)fromPy + (double)(toPy - fromPy) * progress);
            if (this.pushableBoxImg != null) {
               g2.drawImage(this.pushableBoxImg, drawX, drawY, ts, ts, (ImageObserver)null);
            } else {
               g2.setColor(new Color(156, 112, 68));
               g2.fillRect(drawX, drawY, ts, ts);
            }
         }

      }
      private void drawPowerUps(Graphics2D g2) {
         int ts = 40;

         for(PowerUp p : this.state.powerUps) {
            int px = p.tx * ts;
            int py = p.ty * ts;
            Image itemImg = this.getPowerUpImage(p.type);
            if (itemImg != null) {
               int size = (int)((double)ts * 0.72);
               int dx = px + (ts - size) / 2;
               int dy = py + (ts - size) / 2;
               g2.drawImage(itemImg, dx, dy, size, size, (ImageObserver)null);
            } else {
               g2.setColor(p.color());
               g2.fillOval(px + 8, py + 8, ts - 16, ts - 16);
               g2.setColor(Color.BLACK);
               g2.drawString(p.shortName(), px + 12, py + ts - 12);
            }
         }

      }

      private Image getPowerUpImage(PowerUpType type) {
         return switch (type.ordinal()) {
            case 0 -> this.bombCountItemImg;
            case 1 -> this.bombRangeItemImg;
            case 2 -> this.speedItemImg;
            default -> null;
         };
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
      private void drawBossSkillWarnings(Graphics2D g2) {
         if (this.state.bossSkillWarnings.isEmpty()) {
            return;
         }

         int ts = 40;
         Color fill = new Color(76, 188, 255, 95);
         Color stroke = new Color(220, 248, 255, 210);
         Stroke oldStroke = g2.getStroke();

         for(BossSkillWarning warning : this.state.bossSkillWarnings) {
            for(Point p : warning.cells) {
               int px = p.x * ts;
               int py = p.y * ts;
               g2.setColor(fill);
               g2.fillRect(px + 2, py + 2, ts - 4, ts - 4);
               g2.setColor(stroke);
               g2.setStroke(new BasicStroke(2.0F));
               g2.drawRect(px + 3, py + 3, ts - 6, ts - 6);
            }
         }

         g2.setStroke(oldStroke);
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
               Image sprite;
               if (p.bubbleTrapped) {
                  sprite = this.getBubbleImage(p);
               } else {
                  sprite = null;
                  if (p == this.state.p1 && this.p1Sprites != null) {
                     sprite = this.p1Sprites.get(p.facing, p.moving, p.animTime);
                  } else if (p == this.state.p2 && this.p2Sprites != null) {
                     sprite = this.p2Sprites.get(p.facing, p.moving, p.animTime);
                  } else if (p instanceof BotPlayer) {
                     if (this.state.isStoryMode()) {
                        sprite = this.getStoryBotSprite(p);
                     } else if (this.aiSprites != null) {
                        sprite = this.aiSprites.get(p.facing, p.moving, p.animTime);
                     }
                  }
               }

               if (sprite != null) {
                  int sw = Math.max(1, sprite.getWidth((ImageObserver)null));
                  int sh = Math.max(1, sprite.getHeight((ImageObserver)null));
                  int h = p.bubbleTrapped ? (int)((double)ts * 2.0) : (int)((double)ts * 1.45);
                  if (this.state.isStoryMode() && p instanceof BotPlayer && !this.state.isStoryBoss(p)) {
                     h = p.bubbleTrapped ? (int)((double)ts * 1.75) : (int)((double)ts * 1.2);
                  }
                  if (this.state.isStoryBoss(p)) {
                     h = p.bubbleTrapped ? (int)((double)ts * 4.7) : (int)((double)ts * 3.9);
                  }

                  int w = (int)Math.round((double)sw / (double)sh * (double)h);
                  w = Math.max((this.state.isStoryMode() && p instanceof BotPlayer && !this.state.isStoryBoss(p)) ? (int)((double)ts * 0.85) : ts, Math.min(this.state.isStoryBoss(p) ? (int)((double)ts * 4.8) : (this.state.isStoryMode() && p instanceof BotPlayer ? (int)((double)ts * 1.55) : (int)((double)ts * 1.9)), w));
                  int drawX = cx - w / 2;
                  int drawY = cy - h / 2;
                  g2.drawImage(sprite, drawX, drawY, w, h, (ImageObserver)null);
               } else {
                  g2.setColor(p.bubbleTrapped ? new Color(120, 200, 255, 210) : p.color);
                  g2.fillOval(cx - r, cy - r, r * 2, r * 2);
                  g2.setColor(Color.BLACK);
                  g2.drawOval(cx - r, cy - r, r * 2, r * 2);
               }
            }
         }

      }





      private Image getStoryBotSprite(Player player) {
         if (this.state.isStoryBoss(player)) {
            if (this.kingStoryBotIdleImg == null) {
               return this.storyBotIdleImg;
            }
            if (!player.moving) {
               return this.kingStoryBotIdleImg;
            }
            return (int)(player.animTime * (double)2.4F) % 2 == 0 ? this.kingStoryBotIdleImg : (this.kingStoryBotJumpImg != null ? this.kingStoryBotJumpImg : this.kingStoryBotIdleImg);
         }
         if (this.storyBotIdleImg == null) {
            return this.aiSprites == null ? null : this.aiSprites.get(player.facing, player.moving, player.animTime);
         } else if (!player.moving && !player.stepMoving) {
            return this.storyBotIdleImg;
         } else {
            int frame = (int)(player.animTime * (double)6.0F) % 3;
            return switch (frame) {
               case 1 -> this.storyBotJumpImg != null ? this.storyBotJumpImg : this.storyBotIdleImg;
               case 2 -> this.storyBotLandImg != null ? this.storyBotLandImg : this.storyBotIdleImg;
               default -> this.storyBotIdleImg;
            };
         }
      }

      private Image getBubbleImage(Player player) {
         if (player == this.state.p1) {
            return this.p1BubbleImg;
         } else if (player == this.state.p2) {
            return this.p2BubbleImg;
         } else if (this.state.isStoryBoss(player)) {
            if (this.kingStoryBotBubbleImg1 == null) {
               return this.aiBubbleImg;
            } else {
               return System.currentTimeMillis() / 220L % 2L == 0L ? this.kingStoryBotBubbleImg1 : (this.kingStoryBotBubbleImg2 != null ? this.kingStoryBotBubbleImg2 : this.kingStoryBotBubbleImg1);
            }
         } else if (this.state.isStoryMode() && player instanceof BotPlayer) {
            if (this.storyBotBubbleImg1 == null) {
               return this.aiBubbleImg;
            } else {
               return System.currentTimeMillis() / 220L % 2L == 0L ? this.storyBotBubbleImg1 : (this.storyBotBubbleImg2 != null ? this.storyBotBubbleImg2 : this.storyBotBubbleImg1);
            }
         } else {
            return player == this.state.bot ? this.aiBubbleImg : null;
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

      private void drawStoryBossHpBar(Graphics2D g2) {
         if (!this.state.isStoryMode() || this.state.stage != 3 || !this.state.bot.alive || this.state.storyBossMaxHp <= 0) {
            return;
         }

         int x = 24;
         int y = 14;
         int w = 552;
         int h = 18;
         double ratio = Math.max(0.0, Math.min(1.0, (double)this.state.storyBossHp / (double)this.state.storyBossMaxHp));
         g2.setColor(new Color(30, 18, 18, 220));
         g2.fillRoundRect(x, y, w, h, 12, 12);
         g2.setColor(new Color(255, 110, 110, 235));
         g2.fillRoundRect(x, y, (int)Math.round((double)w * ratio), h, 12, 12);
         g2.setColor(Color.WHITE);
         g2.setFont(new Font("Dialog", 1, 13));
         g2.drawString("Boss HP", x, y - 4);
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
         g2.drawString(this.state.progressText(), left + 24, 66);
         g2.drawString("Mode: " + this.state.modeText(), left + 24, 88);
         g2.drawString("State: " + String.valueOf(this.state.phase), left + 24, 110);
         this.drawPlayerHud(g2, this.state.p1, left + 24, 155);
         if (this.state.isP2Mode()) {
            this.drawPlayerHud(g2, this.state.p2, left + 24, 260);
         } else if (this.state.isStoryMode()) {
            g2.drawString("Enemies Left: " + this.state.storyBots().stream().filter((bot) -> bot.alive).count(), left + 24, 260);
            g2.drawString("Story AI Speed: 2.0", left + 24, 282);
            g2.drawString("Aggro Range: 4 tiles", left + 24, 304);
         } else {
            this.drawPlayerHud(g2, this.state.bot, left + 24, 260);
         }

         g2.setFont(new Font("Dialog", 0, 13));
         g2.setColor(new Color(205, 215, 232));
         g2.drawString("P1: WASD + SPACE", left + 24, h - 88);
          g2.drawString("P pause, R restart, N next stage", left + 24, h - 48);
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
         String line5 = null;
         if (this.state.phase == L30Game.GameState.Phase.START) {
            line1 = "Arcade Bomber";
            line2 = (this.startMenuIndex == 0 ? "> " : "  ") + "친구와 대전";
            line3 = (this.startMenuIndex == 1 ? "> " : "  ") + "봇전";
            line4 = (this.startMenuIndex == 2 ? "> " : "  ") + "꼬물이 모드";
            line5 = "1/2/3 select, ENTER start, ESC exit";
         } else if (this.state.phase == L30Game.GameState.Phase.PAUSED) {
            line1 = "Paused";
            line2 = "P: Resume";
         } else if (this.state.phase == L30Game.GameState.Phase.ROUND_OVER) {
            line1 = this.state.winnerText();
            line2 = this.state.isStoryMode() ? "R: Restart Story / ENTER: Next Stage / ESC: Exit" : "R: Restart Match / ENTER: Next Round / ESC: Exit";
         } else if (this.state.phase == L30Game.GameState.Phase.GAME_OVER) {
            line1 = this.state.championText();
            line2 = this.state.isStoryMode() ? "R: Restart Story / ENTER: New Story / ESC: Exit" : "R: Restart Match / ENTER: New Match / ESC: Exit";
            line3 = this.state.isStoryMode() ? (this.state.storyCleared ? "Stage 3 boss defeated" : "Try the story again") : "First to 3 wins";
         }

         if (line1 != null) {
            int mapW = 600;
            int mapH = 520;
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, mapW, mapH);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Dialog", 1, 34));
            this.drawCentered(g2, line1, mapW / 2, mapH / 2 - 52);
            if (line2 != null) {
               g2.setFont(new Font("Dialog", 0, 22));
               this.drawCentered(g2, line2, mapW / 2, mapH / 2 - 8);
            }

            if (line3 != null) {
               g2.setFont(new Font("Dialog", 0, 22));
               this.drawCentered(g2, line3, mapW / 2, mapH / 2 + 26);
            }

            if (line4 != null) {
               g2.setFont(new Font("Dialog", 0, 22));
               this.drawCentered(g2, line4, mapW / 2, mapH / 2 + 60);
            }

            if (line5 != null) {
               g2.setFont(new Font("Dialog", 0, 15));
               this.drawCentered(g2, line5, mapW / 2, mapH / 2 + 96);
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










































