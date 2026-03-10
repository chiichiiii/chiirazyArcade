package com.smu8.game;

import java.awt.Color;
import java.awt.Graphics;

class Missile {
   double x;
   double y;
   double speed = (double)8.0F;

   public Missile(double x, double y) {
      this.x = x;
      this.y = y;
   }

   public void move() {
      this.y -= this.speed;
   }

   public void draw(Graphics g) {
      g.setColor(Color.YELLOW);
      g.fillRect((int)this.x, (int)this.y, 5, 10);
   }
}
