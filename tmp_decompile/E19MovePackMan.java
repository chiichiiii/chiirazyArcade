package com.smu8.game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class E19MovePackMan extends JFrame {
   private int width = 500;
   private int height = 500;
   private Color backColor = new Color(0, 0, 0);

   public E19MovePackMan() {
      super("팩맨");
      this.setContentPane(new MyPanel());
      this.setBounds(0, 0, this.width, this.height + 30);
      this.setVisible(true);
      this.setDefaultCloseOperation(3);
   }

   public static void main(String[] args) {
      SwingUtilities.invokeLater(() -> new E19MovePackMan());
   }

   class MyPanel extends JPanel {
      int x = 0;
      int y = 0;
      int width = 100;
      int height = 100;
      int startAngle = 20;
      int arcAngle = 320;
      Timer moveTimer;
      Timer angleTimer;
      Color bgColor = new Color(255, 205, 0);
      Status status;

      public MyPanel() {
         this.status = E19MovePackMan.MyPanel.Status.STOP;
         this.setFocusable(true);
         this.requestFocusInWindow();
         this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
               System.out.println(e.getKeyCode());
               System.out.println(e.getKeyChar());
               MyPanel var10000 = MyPanel.this;
               Status var10001;
               switch (e.getKeyCode()) {
                  case 32 -> var10001 = E19MovePackMan.MyPanel.Status.STOP;
                  case 65 -> var10001 = E19MovePackMan.MyPanel.Status.LEFT;
                  case 68 -> var10001 = E19MovePackMan.MyPanel.Status.RIGHT;
                  case 83 -> var10001 = E19MovePackMan.MyPanel.Status.BOTTOM;
                  case 87 -> var10001 = E19MovePackMan.MyPanel.Status.TOP;
                  default -> var10001 = E19MovePackMan.MyPanel.Status.STOP;
               }

               var10000.status = var10001;
            }
         });
         this.moveTimer = new Timer(10, (e) -> this.move());
         this.moveTimer.start();
         this.angleTimer = new Timer(200, (e) -> this.turn());
         this.angleTimer.start();
      }

      void move() {
         switch (this.status.ordinal()) {
            case 1:
               if (this.y > 0) {
                  --this.y;
               }
               break;
            case 2:
               if (this.y + this.height < E19MovePackMan.this.height) {
                  ++this.y;
               }
               break;
            case 3:
               if (this.x > 0) {
                  --this.x;
               }
               break;
            case 4:
               if (this.x + this.width < E19MovePackMan.this.width) {
                  ++this.x;
               }
         }

         this.repaint();
      }

      void turn() {
         switch (this.status.ordinal()) {
            case 1 -> this.startAngle = 120;
            case 2 -> this.startAngle = 300;
            case 3 -> this.startAngle = 210;
            case 4 -> this.startAngle = 30;
         }

         this.arcAngle = this.arcAngle != 360 ? 360 : 300;
         this.repaint();
      }

      protected void paintComponent(Graphics g) {
         this.setBackground(Color.white);
         g.setColor(new Color(255, 205, 0));
         g.fillArc(this.x, this.y, this.width, this.height, this.startAngle, this.arcAngle);
         g.setColor(Color.black);
      }

      static enum Status {
         STOP,
         TOP,
         BOTTOM,
         LEFT,
         RIGHT;

         // $FF: synthetic method
         private static Status[] $values() {
            return new Status[]{STOP, TOP, BOTTOM, LEFT, RIGHT};
         }
      }
   }
}
