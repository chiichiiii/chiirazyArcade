package com.smu8.game;

import java.awt.Color;
import java.awt.Graphics;

class Enemy {
   double x;
   double y;
   double speed = (double)1.5F;

   public Enemy(int x, int y) {
      this.x = (double)x;
      this.y = (double)y;
   }

   public void chase(Player player) {
      double dx = (double)player.x - this.x;
      double dy = (double)player.y - this.y;
      double distance = Math.sqrt(dx * dx + dy * dy);
      if (distance != (double)0.0F) {
         this.x += dx / distance * this.speed;
         this.y += dy / distance * this.speed;
      }

   }

   public void draw(Graphics g) {
      g.setColor(Color.RED);
      g.fillOval((int)this.x, (int)this.y, 25, 25);
   }
}
