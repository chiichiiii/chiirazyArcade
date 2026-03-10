package com.smu8.game;

import java.awt.Color;
import java.awt.Graphics;

class Player {
   int x;
   int y;
   int speed = 20;
   int dx = 0;
   int dy = 0;

   public Player(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public void keyPressed(int keyCode) {
      switch (keyCode) {
         case 37:
         case 65:
            this.dx = -this.speed;
            break;
         case 38:
         case 87:
            this.dy = -this.speed;
            break;
         case 39:
         case 68:
            this.dx = this.speed;
            break;
         case 40:
         case 83:
            this.dy = this.speed;
      }

   }

   public void move(int width, int height) {
      this.x += this.dx;
      this.y += this.dy;
      if (this.x < 0) {
         this.x = 0;
      }

      if (this.x > width - 30) {
         this.x = width - 30;
      }

      if (this.y < 0) {
         this.y = 0;
      }

      if (this.y > height - 20) {
         this.y = height - 20;
      }

      this.dx = 0;
      this.dy = 0;
   }

   public void draw(Graphics g) {
      g.setColor(Color.GREEN);
      g.fillRect(this.x, this.y, 30, 20);
   }
}
