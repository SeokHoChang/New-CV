/*     */ package depthPack;
/*     */ 
/*     */ import java.awt.Canvas;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Image;
/*     */ import java.awt.Point;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.image.BufferStrategy;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.util.LinkedList;
/*     */ import javax.swing.JFrame;
/*     */ 
/*     */ public class GUI
/*     */ {
/*     */   private JFrame frame;
/*     */   private Canvas canvas;
/*     */   private BufferStrategy bs;
/*     */   private BufferStrategy bs1;
/*  28 */   private static int ImpMarkWidth = 0;
/*  29 */   private static int ImpMarkHeight = 0;
/*     */ 
/*  31 */   private static int importantDrawX = 0;
/*  32 */   private static int importantDrawY = 0;
/*     */ 
/*  34 */   private static int curDrawX = 0;
/*  35 */   private static int curDrawY = 0;
/*  36 */   private static int preDrawX = 0;
/*  37 */   private static int preDrawY = 0;
/*     */ 
/*  39 */   private static int curHandX = 0;
/*  40 */   private static int curHandY = 0;
/*  41 */   private static int preHandX = 0;
/*  42 */   private static int preHandY = 0;
/*     */   private static Toolkit tk;
/*     */   private static Image hand;
/*     */   private static Image off;
/*     */   private static Image ImpMark;
/*     */   private static int ImageWidth;
/*     */   private static int ImageHeight;
/*     */   private static final int BGWidth = 1440;
/*     */   private static final int BGHeight = 1080;
/*     */   private static int HandNUM;
/*  50 */   private static int SlideCount = 1;
/*  51 */   private static int MovingFrameCount = 0;
/*     */ 
/*  53 */   private static boolean isWaiting = false;
/*  54 */   private static boolean isDrawing = false;
/*  55 */   private static boolean isReady4Draw = false;
/*     */ 
/*  57 */   private static boolean isImportant = false;
/*  58 */   private static boolean isMarked = false;
/*     */ 
/*  60 */   private static int WaitingFrameCount = 0;
/*  61 */   private static int frameCount = 0;
/*     */   private static final int MAX_MOVING_CNT = 3;
/*     */   private static final int MAX_WF_CNT = 4;
/*     */   private static final int HAND_STATE_0 = 0;
/*     */   private static final int HAND_STATE_1 = 1;
/*     */   private static final int HAND_STATE_2 = 2;
/*     */   private static final int HAND_STATE_3 = 3;
/*     */   private static final int HAND_STATE_4 = 4;
/*     */   private static final int HAND_STATE_5 = 5;
/*     */   private static final int HAND_STATE_6 = 6;
/*     */   private static final int HAND_STATE_7 = 7;
/*     */   private static final int HAND_STATE_8 = 8;
/*     */   private static final int HAND_STATE_9 = 9;
/*     */   private static final int HAND_STATE_10 = 10;
/*     */   private static final int MOVING_STATE_Stay = -1;
/*     */   private static final int MOVING_STATE_Right = 0;
/*     */   private static final int MOVING_STATE_Left = 1;
/*     */   private static final int MOVING_STATE_Up = 2;
/*     */   private static final int MOVING_STATE_Down = 3;
/*  84 */   private static int FullPages = -1;
/*     */   private static int currentHandState;
/*     */   private static int previoustHandState;
/*     */   private static int currentMovingXState;
/*     */   private static int previousMovingXState;
/*     */   private static Point vec;
/*     */   private static LinkedList<markNslide> impMarkPts;
/*     */   private static LinkedList<StatePair> StateList;
/*     */ 
/*     */   public GUI()
/*     */   {
/* 121 */     this.frame = new JFrame("Canvas");
/* 122 */     this.frame.setDefaultCloseOperation(3);
/* 123 */     this.frame.setPreferredSize(new Dimension(1440, 1080));
/* 124 */     this.canvas = new Canvas();
/* 125 */     this.canvas.setBackground(Color.white);
/* 126 */     this.canvas.setSize(1440, 1080);
/* 127 */     this.frame.add(this.canvas);
/*     */ 
/* 129 */     this.canvas.createBufferStrategy(1);
/* 130 */     this.bs = this.canvas.getBufferStrategy();
/*     */ 
/* 132 */     this.frame.pack();
/* 133 */     this.frame.setLocationRelativeTo(null);
/* 134 */     this.frame.setVisible(true);
/*     */ 
/* 136 */     tk = Toolkit.getDefaultToolkit();
/* 137 */     hand = tk.getImage("hand.jpg");
/* 138 */     ImageHeight = hand.getHeight(this.canvas);
/* 139 */     ImageWidth = hand.getWidth(this.canvas);
/*     */ 
/* 141 */     ImpMark = tk.getImage("ImportantMark.png");
/* 142 */     ImpMarkHeight = ImpMark.getHeight(this.canvas);
/* 143 */     ImpMarkWidth = ImpMark.getWidth(this.canvas);
/*     */ 
/* 145 */     impMarkPts = new LinkedList();
/*     */ 
/* 147 */     StateList = new LinkedList();
/*     */   }
/*     */ 
/*     */   private Image getSlideIMG(String Slides, String type, int num)
/*     */   {
/* 153 */     if (FullPages == -1) { int n = 0;
/*     */       File f;
/*     */       do { n++;
/* 158 */         f = new File(Slides + "-" + n + "." + type); }
/* 159 */       while (f.isFile());
/*     */ 
/* 161 */       FullPages = n;
/*     */     }
/*     */ 
/* 165 */     return tk.getImage(Slides + "-" + num + "." + type);
/*     */   }
/*     */ 
/*     */   public int getCurrentState() {
/* 169 */     return currentHandState;
/*     */   }
/*     */ 
/*     */   public Point getMovinDir() {
/* 173 */     return vec;
/*     */   }
/*     */ 
/*     */   private void setHandMovingDir() {
/* 177 */     int dirX = curHandX - preHandX;
/* 178 */     int dirY = curHandY - preHandY;
/*     */ 
/* 180 */     vec = new Point(dirX, dirY);
/*     */   }
/*     */ 
/*     */   private int isMovingX()
/*     */   {
/* 185 */     if (vec.x < -15)
/* 186 */       return 1;
/* 187 */     if (vec.x > 15) {
/* 188 */       return 0;
/*     */     }
/* 190 */     return -1;
/*     */   }
/*     */ 
/*     */   private int isMovingY() {
/* 194 */     if (vec.y < -10)
/* 195 */       return 3;
/* 196 */     if (vec.y > 10) {
/* 197 */       return 2;
/*     */     }
/* 199 */     return -1;
/*     */   }
/*     */ 
/*     */   public boolean isContinued()
/*     */   {
/* 204 */     return previoustHandState == currentHandState;
/*     */   }
/*     */ 
/*     */   public void countMoving()
/*     */   {
/* 211 */     if (StateList.size() == 3) {
/* 212 */       StateList.pop();
/*     */     }
/* 214 */     StateList.push(new StatePair(currentHandState, currentMovingXState));
/* 215 */     if (currentMovingXState == 0) {
/* 216 */       if (currentMovingXState == previousMovingXState)
/* 217 */         MovingFrameCount += 1;
/*     */       else
/* 219 */         MovingFrameCount = 0;
/*     */     }
/* 221 */     else if (currentMovingXState == 1) {
/* 222 */       if (currentMovingXState == previousMovingXState)
/* 223 */         MovingFrameCount -= 1;
/*     */       else
/* 225 */         MovingFrameCount = 0;
/*     */     }
/*     */     else
/* 228 */       MovingFrameCount += 0;
/*     */   }
/*     */ 
/*     */   public int getHandNum(int num)
/*     */   {
/* 234 */     if (!isWaiting) {
/* 235 */       previoustHandState = currentHandState;
/* 236 */       currentHandState = num;
/* 237 */       hand = tk.getImage("hand" + num + ".jpg");
/* 238 */       switch (num) {
/*     */       case 0:
/* 240 */         if ((!isDrawing) && (!isReady4Draw)) {
/* 241 */           isWaiting = true;
/* 242 */           StateList.clear();
/*     */         }
/* 244 */         return num;
/*     */       case 1:
/* 246 */         if (isReady4Draw) {
/* 247 */           isDrawing = true;
/*     */         }
/* 249 */         return num;
/*     */       case 2:
/* 252 */         isReady4Draw = true;
/* 253 */         return num;
/*     */       case 3:
/* 256 */         isDrawing = false;
/* 257 */         isReady4Draw = false;
/*     */ 
/* 259 */         isImportant = false;
/* 260 */         isMarked = false;
/*     */ 
/* 263 */         return num;
/*     */       case 4:
/* 266 */         if ((!isDrawing) && (!isReady4Draw))
/*     */         {
/* 268 */           if (!isImportant)
/* 269 */             isWaiting = true;
/*     */         }
/*     */         else {
/* 272 */           return -1;
/*     */         }
/* 274 */         return num;
/*     */       case 5:
/* 276 */         if ((!isDrawing) && (!isReady4Draw)) {
/* 277 */           if (isImportant) {
/* 278 */             isMarked = true;
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 283 */           return -1;
/* 284 */         }return num;
/*     */       case 6:
/*     */       case 7:
/*     */       case 8:
/*     */       case 9:
/*     */       case 10:
/* 290 */         return num;
/*     */       }
/* 292 */       return -1;
/*     */     }
/*     */ 
/* 297 */     if ((!isDrawing) && 
/* 298 */       (WaitingFrameCount++ == 4)) {
/* 299 */       isWaiting = false;
/* 300 */       WaitingFrameCount = 0;
/*     */ 
/* 302 */       if (currentHandState == 0) {
/* 303 */         boolean pass = false;
/* 304 */         double prob = getStateProb(StateList, 0, 
/* 305 */           0);
/*     */ 
/* 307 */         if (2.0D < prob)
/*     */         {
/* 309 */           if (SlideCount <= FullPages) {
/* 310 */             SlideCount += 1;
/* 311 */             pass = true;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 316 */         if (!pass) {
/* 317 */           prob = getStateProb(StateList, 0, 
/* 318 */             1);
/*     */ 
/* 320 */           if (2.0D < prob)
/*     */           {
/* 322 */             if (SlideCount > 1) {
/* 323 */               SlideCount -= 1;
/*     */             }
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 329 */         StateList.clear();
/* 330 */         MovingFrameCount = 0;
/* 331 */       } else if (currentHandState == 4) {
/* 332 */         double prob = getStateProb(StateList, 4, 
/* 333 */           -1);
/*     */ 
/* 335 */         if (2.0D < prob) {
/* 336 */           isImportant = true;
/* 337 */           importantDrawX = curHandX;
/* 338 */           importantDrawY = curHandY;
/*     */         }
/*     */       }
/* 341 */       StateList.clear();
/* 342 */       MovingFrameCount = 0;
/*     */     }
/* 344 */     return -1;
/*     */   }
/*     */ 
/*     */   private static double getStateProb(LinkedList<StatePair> StateList, int handNum, int dir)
/*     */   {
/* 351 */     double prob = 0.0D;
/* 352 */     for (int i = 0; i < StateList.size(); i++) {
/* 353 */       StatePair pair = (StatePair)StateList.get(i);
/*     */ 
/* 355 */       System.out.println(pair.Hand + "," + pair.Move);
/* 356 */       if ((pair.Hand == handNum) && (dir == pair.Move)) {
/* 357 */         prob += 1.0D;
/*     */       }
/* 361 */       else if ((pair.Hand == handNum) && (pair.Move == -1)) {
/* 362 */         prob += 0.5D;
/*     */       }
/* 366 */       else if ((pair.Hand != handNum) && (dir == pair.Move)) {
/* 367 */         prob += 1.0D;
/*     */       }
/* 370 */       else if ((pair.Hand != handNum) && (pair.Move != dir)) {
/* 371 */         prob += 0.0D;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 376 */     return prob;
/*     */   }
/*     */ 
/*     */   public void setHandPosition(int x, int y) {
/* 380 */     preHandX = curHandX;
/* 381 */     preHandY = curHandY;
/* 382 */     curHandX = (int)(x * 1440.0D / 320.0D);
/* 383 */     curHandY = (int)(y * 1080.0D / 240.0D);
/*     */ 
/* 385 */     setHandMovingDir();
/* 386 */     previousMovingXState = currentMovingXState;
/* 387 */     currentMovingXState = isMovingX();
/*     */   }
/*     */ 
/*     */   public void setDrawPoint(int x, int y)
/*     */   {
/* 392 */     if (isDrawing) {
/* 393 */       preDrawX = curDrawX;
/* 394 */       preDrawY = curDrawY;
/* 395 */       curDrawX = (int)(x * 1440.0D / 320.0D);
/* 396 */       curDrawY = (int)(y * 1080.0D / 240.0D);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getState()
/*     */   {
/* 406 */     String x = new String(); String y = new String();
/*     */ 
/* 408 */     switch (isMovingX()) {
/*     */     case -1:
/* 410 */       x = "stay";
/* 411 */       break;
/*     */     case 0:
/* 413 */       x = "Right";
/* 414 */       break;
/*     */     case 1:
/* 416 */       x = "Left";
/*     */     }
/*     */ 
/* 419 */     switch (isMovingY()) {
/*     */     case -1:
/* 421 */       y = "stay";
/* 422 */       break;
/*     */     case 2:
/* 424 */       y = "Up";
/* 425 */       break;
/*     */     case 3:
/* 427 */       y = "Down";
/*     */     case 0:
/*     */     case 1:
/*     */     }
/*     */ 
/* 432 */     return "(" + currentHandState + "," + MovingFrameCount + "," + x + "," + 
/* 433 */       y + ")";
/*     */   }
/*     */ 
/*     */   public void paint()
/*     */   {
/* 439 */     BufferedImage img = new BufferedImage(1440, 1080, 
/* 440 */       5);
/*     */ 
/* 442 */     Graphics g = this.bs.getDrawGraphics();
/* 443 */     Graphics G = img.getGraphics();
/*     */ 
/* 445 */     int term = 7;
/* 446 */     Image slide = getSlideIMG("Final","png", SlideCount);
/* 447 */     Image impMark = tk.getImage("ImportantMark.png");
/* 448 */     for (int j = 0; j < term; j++) {
/* 449 */       G.fillRect(0, 0, 1440, 1080);
/*     */ 
/* 451 */       G.drawImage(slide, 0, 0, this.canvas);
/* 452 */       G.setColor(Color.white);
/* 453 */       G.drawString(SlideCount + "/" + FullPages, 10, 10);
/*     */ 
/* 455 */       double difX = curHandX - preHandX;
/* 456 */       double difY = curHandY - preHandY;
/*     */ 
/* 458 */       G.setColor(null);
/*     */ 
/* 460 */       if (isMarked) {
/* 461 */         impMarkPts.add(
/* 462 */           new markNslide(SlideCount, 
/* 462 */           new Point(importantDrawX, importantDrawY)));
/* 463 */         isImportant = false;
/* 464 */         isMarked = false;
/*     */       }
/*     */ 
/* 467 */       for (int i = 0; i < impMarkPts.size(); i++)
/*     */       {
/* 469 */         if (SlideCount == ((markNslide)impMarkPts.get(i)).slideNum) {
/* 470 */           G.drawImage(
/* 471 */             impMark, 
/* 472 */             (int)(((markNslide)impMarkPts.get(i)).pt.x - ImpMarkWidth / 2.0D), 
/* 473 */             (int)(((markNslide)impMarkPts.get(i)).pt.y - ImpMarkHeight / 2.0D), 
/* 474 */             this.canvas);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 481 */       if (!isDrawing)
/*     */       {
/* 483 */         if (!isImportant)
/* 484 */           G.drawImage(
/* 485 */             hand, 
/* 486 */             (int)(preHandX + difX / term * j - ImageWidth / 2.0D), 
/* 487 */             (int)(preHandY + difY / term * j - ImageHeight / 2.0D), 
/* 488 */             this.canvas);
/*     */         else {
/* 490 */           G.drawImage(
/* 491 */             impMark, 
/* 492 */             (int)(preHandX + difX / term * j - ImpMarkWidth / 2.0D), 
/* 493 */             (int)(preHandY + difY / term * j - ImpMarkHeight / 2.0D), 
/* 494 */             this.canvas);
/*     */         }
/*     */       }
/*     */ 
/* 498 */       if (isDrawing) {
/* 499 */         G.setColor(Color.RED);
/*     */ 
/* 501 */         difX = curDrawX - preDrawX;
/* 502 */         difY = curDrawY - preDrawY;
/* 503 */         G.drawOval((int)(preDrawX + difX / term * j), 
/* 504 */           (int)(preDrawY + difY / term * j), 10, 10);
/*     */       }
/*     */ 
/* 507 */       g.drawImage(img, 0, 0, this.canvas);
/*     */ 
/* 509 */       if (!this.bs.contentsLost())
/* 510 */         this.bs.show();
/*     */     }
/*     */   }
/*     */ 
/*     */   class StatePair
/*     */   {
/*     */     int Hand;
/*     */     int Move;
/*     */ 
/*     */     public StatePair(int hand, int move)
/*     */     {
/* 102 */       this.Hand = hand;
/* 103 */       this.Move = move;
/*     */     }
/*     */   }
/*     */ 
/*     */   class markNslide {
/*     */     int slideNum;
/*     */     Point pt;
/*     */ 
/*     */     public markNslide(int num, Point ipt) {
/* 114 */       this.slideNum = num;
/* 115 */       this.pt = ipt;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Web_java\eclipse\open_cv_workspace\CV_PROJECT_DEPTH\CV_PROJECT_DEPTH\bin\
 * Qualified Name:     depthPack.GUI
 * JD-Core Version:    0.6.0
 */