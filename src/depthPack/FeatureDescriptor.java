/*     */ package depthPack;
/*     */ 
/*     */ import com.googlecode.javacv.cpp.opencv_core;
/*     */ import com.googlecode.javacv.cpp.opencv_core.CvMat;
/*     */ import com.googlecode.javacv.cpp.opencv_core.IplImage;
/*     */ import com.googlecode.javacv.cpp.opencv_highgui;
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public class FeatureDescriptor
/*     */ {
/*     */   private static final int WEIGHTSCALE = 1;
/*     */   private static final int RADIUS = 36;
/*     */   private static final int FI = 360;
/*     */   private static final int ANGLE_SIZE = 45;
/*     */   private static final int ANGLE_BIN_SIZE = 8;
/*     */   private static final int RADIUS_BIN_SIZE = 4;
/*     */   private static final int GRAD_BIN_SIZE_XY = 8;
/*     */   private static final int GRAD_BIN_SIZE_YZ = 5;
/*     */   private static final int GRAD_BIN_SIZE_XZ = 5;
/*     */   private static final int TRAINING_MAT_SIZE = 576;
/*     */   private static final int WIDTH = 320;
/*     */   private static final int HEIGHT = 240;
/*     */   private static final int CAP_WIDTH = 90;
/*     */   private static final int CAP_HEIGHT = 90;
/*     */   private static final int MAT_ARRAY_SIZE = 8100;
/*     */   private static final int PROJECTED_NORM_XY = 0;
/*     */   private static final int PROJECTED_NORM_YZ = 1;
/*     */   private static final int PROJECTED_NORM_XZ = 2;
/*     */   private static final int MANHATTAN_DISTANCE_MODE = 0;
/*     */   private static opencv_core.CvMat data;
/*     */   public static opencv_core.CvMat[] NormMatArr;
/*     */   private static double[][][] HistogramXY;
/*     */   private static double[][][] HistogramYZ;
/*     */   private static double[][][] HistogramXZ;
/*     */   private static opencv_core.IplImage hist;
/*     */ 
/*     */   public FeatureDescriptor()
/*     */   {
/*  53 */     NormMatArr = new opencv_core.CvMat[8100];
/*     */ 
/*  56 */     HistogramXY = new double[8][4][8];
/*  57 */     HistogramYZ = new double[8][4][5];
/*  58 */     HistogramXZ = new double[8][4][5];
/*     */ 
/*  60 */     for (int i = 0; i < 8; i++) {
/*  61 */       for (int j = 0; j < 4; j++) {
/*  62 */         for (int j2 = 0; j2 < 8; j2++) {
/*  63 */           HistogramXY[i][j][j2] = 0.0D;
/*     */         }
/*  65 */         for (int j2 = 0; j2 < 5; j2++) {
/*  66 */           HistogramYZ[i][j][j2] = 0.0D;
/*     */         }
/*  68 */         for (int j2 = 0; j2 < 5; j2++) {
/*  69 */           HistogramXZ[i][j][j2] = 0.0D;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  75 */     data = opencv_core.cvCreateMat(1, 576, opencv_core.CV_32FC1);
/*     */ 
/*  77 */     for (int i = 0; i < 8100; i++) {
/*  78 */       NormMatArr[i] = opencv_core.cvCreateMat(3, 1, opencv_core.CV_32FC1);
/*  79 */       opencv_core.cvSetZero(NormMatArr[i]);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void init()
/*     */   {
/*  92 */     for (int i = 0; i < 576; i++) {
/*  93 */       data.put(0, i, 0.0D);
/*     */     }
/*     */ 
/*  96 */     for (int i = 0; i < 8; i++)
/*  97 */       for (int j = 0; j < 4; j++) {
/*  98 */         for (int j2 = 0; j2 < 8; j2++) {
/*  99 */           HistogramXY[i][j][j2] = 0.0D;
/*     */         }
/* 101 */         for (int j2 = 0; j2 < 5; j2++) {
/* 102 */           HistogramYZ[i][j][j2] = 0.0D;
/*     */         }
/* 104 */         for (int j2 = 0; j2 < 5; j2++)
/* 105 */           HistogramXZ[i][j][j2] = 0.0D;
/*     */       }
/*     */   }
/*     */ 
/*     */   public opencv_core.CvMat MK(short[] depthMap, int x, int y, int width, int height)
/*     */   {
/* 114 */     init();
/* 115 */     makeNormMatArr(depthMap, x, y, width, height);
/*     */ 
/* 117 */     return get1DHistogram(NormMatArr, 0, 0, 0);
/*     */   }
/*     */ 
/*     */   public opencv_core.CvMat get1DHistogram(opencv_core.CvMat[] NormMatArr, int x, int y, int l)
/*     */   {
/* 123 */     fillHIST(NormMatArr, x, y);
/* 124 */     makeHistTo1DMat();
/*     */ 
/* 126 */     if (l == 1)
/* 127 */       showHIST();
/* 128 */     return data;
/*     */   }
/*     */ 
/*     */   private void showHIST() {
/* 132 */     hist = opencv_core.cvCreateImage(opencv_core.cvSize(640, 480), 8, 3);
/* 133 */     opencv_core.cvSetZero(hist);
/*     */ 
/* 136 */     for (int i = 0; i < 576; i++)
/*     */     {
/* 140 */       if (i < 256)
/*     */       {
/* 142 */         opencv_core.cvLine(hist, opencv_core.cvPoint(10 + i, 0), opencv_core.cvPoint(10 + i, (int)Math.round(data.get(0, i))), opencv_core.CV_RGB(0.0D, 255.0D, 0.0D), 1, 16, 0);
/*     */       }
/* 146 */       else if ((i >= 256) && (i < 416))
/*     */       {
/* 148 */         opencv_core.cvLine(hist, opencv_core.cvPoint(20 + i, 0), opencv_core.cvPoint(20 + i, (int)Math.round(data.get(0, i))), opencv_core.CV_RGB(255.0D, 0.0D, 0.0D), 1, 16, 0);
/*     */       }
/*     */       else
/*     */       {
/* 153 */         if (i < 416) {
/*     */           continue;
/*     */         }
/* 156 */         opencv_core.cvLine(hist, opencv_core.cvPoint(30 + i, 0), opencv_core.cvPoint(30 + i, (int)Math.round(data.get(0, i))), opencv_core.CV_RGB(0.0D, 0.0D, 255.0D), 1, 16, 0);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 162 */     opencv_highgui.cvShowImage("hist", hist);
/* 163 */     opencv_core.cvReleaseImage(hist);
/*     */   }
/*     */ 
/*     */   private void makeHistTo1DMat()
/*     */   {
/* 171 */     int idx = 0;
/* 172 */     for (int cnt = 0; cnt < 3; cnt++)
/* 173 */       for (int i = 0; i < 8; i++)
/* 174 */         for (int j = 0; j < 4; j++)
/*     */         {
/* 177 */           switch (cnt) {
/*     */           case 0:
/* 179 */             for (int k = 0; k < 8; k++) {
/* 180 */               data.put(0, idx++, HistogramXY[i][j][k]);
/*     */             }
/* 182 */             break;
/*     */           case 1:
/* 184 */             for (int k = 0; k < 5; k++) {
/* 185 */               data.put(0, idx++, HistogramYZ[i][j][k]);
/*     */             }
/* 187 */             break;
/*     */           case 2:
/* 189 */             int k = 0;
/*     */             while (true) { data.put(0, idx++, HistogramXZ[i][j][k]);
/*     */ 
/* 189 */               k++; if (k >= 5)
/*     */               {
/* 192 */                 break;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */   }
/*     */ 
/*     */   private void fillHIST(opencv_core.CvMat[] NormMatArr, int x, int y)
/*     */   {
/* 209 */     for (int j = 1; j < 89; j++)
/* 210 */       for (int k = 1; k < 89; k++)
/*     */       {
/* 212 */         int i = k + j * 90;
/*     */ 
/* 214 */         opencv_core.CvMat ProjectedNormXY = opencv_core.cvCreateMat(3, 1, opencv_core.CV_32FC1);
/* 215 */         ProjectedNormXY = getProjectVector(ProjectedNormXY, NormMatArr[i], 0);
/* 216 */         opencv_core.CvMat ProjectedNormYZ = opencv_core.cvCreateMat(3, 1, opencv_core.CV_32FC1);
/* 217 */         ProjectedNormYZ = getProjectVector(ProjectedNormYZ, NormMatArr[i], 1);
/* 218 */         opencv_core.CvMat ProjectedNormXZ = opencv_core.cvCreateMat(3, 1, opencv_core.CV_32FC1);
/* 219 */         ProjectedNormXZ = getProjectVector(ProjectedNormXZ, NormMatArr[i], 2);
/*     */ 
/* 222 */         double[] BinNumXY = getBinNum(ProjectedNormXY, 0);
/* 223 */         double[] BinNumYZ = getBinNum(ProjectedNormYZ, 1);
/* 224 */         double[] BinNumXZ = getBinNum(ProjectedNormXZ, 2);
/*     */ 
/* 227 */         double[] weightsXY = calcWeight(BinNumXY[0], BinNumXY[1], BinNumXY[2]);
/*     */ 
/* 229 */         double[] weightsYZ = calcWeight(BinNumYZ[0], BinNumYZ[1], BinNumYZ[2]);
/*     */ 
/* 231 */         double[] weightsXZ = calcWeight(BinNumXZ[0], BinNumXZ[1], BinNumXZ[2]);
/*     */         int idx_ANGLE;
/* 238 */         if ((idx_ANGLE = getAngleBin(k, j, x, y)) == -1)
/*     */         {
/* 240 */           opencv_core.cvReleaseMat(ProjectedNormXZ);
/* 241 */           opencv_core.cvReleaseMat(ProjectedNormXY);
/* 242 */           opencv_core.cvReleaseMat(ProjectedNormYZ);
/*     */         }
/*     */         else
/*     */         {
/*     */           int idx_RADIUS;
/* 247 */           if ((idx_RADIUS = getRadiusBin(k, j, x, y)) == -1)
/*     */           {
/* 249 */             opencv_core.cvReleaseMat(ProjectedNormXZ);
/* 250 */             opencv_core.cvReleaseMat(ProjectedNormXY);
/* 251 */             opencv_core.cvReleaseMat(ProjectedNormYZ);
/*     */           }
/*     */           else
/*     */           {
/* 257 */             if ((ProjectedNormXY.get(0, 0) != 0.0D) || (ProjectedNormXY.get(0, 1) != 0.0D)) {
/* 258 */               HistogramXY[idx_ANGLE][idx_RADIUS][(int)BinNumXY[0]] += weightsXY[0] * 1.0D;
/* 259 */               HistogramXY[idx_ANGLE][idx_RADIUS][(int)BinNumXY[1]] += weightsXY[1] * 1.0D;
/*     */             }
/* 261 */             if ((ProjectedNormYZ.get(0, 0) != 0.0D) || (ProjectedNormYZ.get(0, 1) != 0.0D)) {
/* 262 */               HistogramYZ[idx_ANGLE][idx_RADIUS][(int)BinNumYZ[0]] += weightsYZ[0] * 1.0D;
/* 263 */               HistogramYZ[idx_ANGLE][idx_RADIUS][(int)BinNumYZ[1]] += weightsYZ[1] * 1.0D;
/*     */             }
/* 265 */             if ((ProjectedNormXZ.get(0, 0) != 0.0D) || (ProjectedNormXZ.get(0, 1) != 0.0D)) {
/* 266 */               HistogramXZ[idx_ANGLE][idx_RADIUS][(int)BinNumXZ[0]] += weightsXZ[0] * 1.0D;
/* 267 */               HistogramXZ[idx_ANGLE][idx_RADIUS][(int)BinNumXZ[1]] += weightsXZ[1] * 1.0D;
/*     */             }
/*     */ 
/* 272 */             opencv_core.cvReleaseMat(ProjectedNormXZ);
/* 273 */             opencv_core.cvReleaseMat(ProjectedNormXY);
/* 274 */             opencv_core.cvReleaseMat(ProjectedNormYZ);
/*     */           }
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   private int getRadiusBin(int x, int y, int boxX, int boxY)
/*     */   {
/* 287 */     double distance = Math.sqrt(Math.pow(boxX + 45 - x, 2.0D) + Math.pow(boxY + 45 - y, 2.0D));
/*     */ 
/* 289 */     for (int i = 0; i < 4; i++)
/*     */     {
/* 291 */       if ((9 * i <= distance) && (distance < 9 * (i + 1))) {
/* 292 */         return i;
/*     */       }
/*     */     }
/* 295 */     return -1;
/*     */   }
/*     */ 
/*     */   private int getAngleBin(int x, int y, int boxX, int boxY)
/*     */   {
/* 302 */     double Angle = 0.0D;
/*     */ 
/* 304 */     x -= boxX + 45;
/* 305 */     y -= boxY + 45;
/*     */ 
/* 309 */     if ((Angle = Math.atan2(y, x) / 3.141592653589793D * 180.0D) < 0.0D) {
/* 310 */       Angle += 360.0D;
/*     */     }
/*     */ 
/* 314 */     for (int i = 0; i < 8; i++)
/*     */     {
/* 316 */       if ((45 * i <= Angle) && (Angle < 45 * (i + 1)))
/* 317 */         return i;
/*     */     }
/* 319 */     return -1;
/*     */   }
/*     */ 
/*     */   private double[] calcWeight(double num_bin0, double num_bin1, double angle)
/*     */   {
/* 325 */     double[] theta = new double[2];
/*     */ 
/* 328 */     theta[0] = (angle - 45.0D * num_bin0);
/* 329 */     theta[1] = (45.0D * num_bin1 - angle);
/*     */ 
/* 332 */     double[] weights = new double[2];
/*     */ 
/* 335 */     if ((weights[0] = Math.sin(theta[1] / 180.0D * 3.141592653589793D) / (Math.sin(theta[0] / 180.0D * 3.141592653589793D) + Math.sin(theta[1] / 180.0D * 3.141592653589793D))) < 0.0D)
/*     */     {
/* 337 */       System.out.println(weights[0]);
/* 338 */       System.err.println("weight0 cannot be a negative value");
/* 339 */       System.exit(1);
/*     */     }
/* 341 */     if ((weights[1] = Math.sin(theta[0] / 180.0D * 3.141592653589793D) / (Math.sin(theta[0] / 180.0D * 3.141592653589793D) + Math.sin(theta[1] / 180.0D * 3.141592653589793D))) < 0.0D)
/*     */     {
/* 343 */       System.out.println(weights[1]);
/* 344 */       System.err.println("weight1 cannot be a negative value");
/* 345 */       System.exit(1);
/*     */     }
/*     */ 
/* 349 */     return weights;
/*     */   }
/*     */ 
/*     */   private double[] getBinNum(opencv_core.CvMat vector, int mode)
/*     */   {
/* 355 */     int binSize = 0;
/* 356 */     double[] BinNum = new double[3];
/* 357 */     double x = 0.0D; double y = 0.0D; double theta = 0.0D;
/*     */ 
/* 359 */     switch (mode)
/*     */     {
/*     */     case 0:
/* 363 */       x = vector.get(0, 0);
/* 364 */       y = vector.get(1, 0);
/* 365 */       binSize = 8;
/* 366 */       break;
/*     */     case 1:
/* 369 */       x = vector.get(1, 0);
/* 370 */       y = vector.get(2, 0);
/* 371 */       binSize = 5;
/* 372 */       break;
/*     */     case 2:
/* 375 */       x = vector.get(0, 0);
/* 376 */       y = vector.get(2, 0);
/* 377 */       binSize = 5;
/* 378 */       break;
/*     */     default:
/* 380 */       System.err.println("getBinNum ERROR");
/*     */     }
/*     */ 
/* 387 */     if ((theta = Math.atan2(y, x) / 3.141592653589793D * 180.0D) < 0.0D) {
/* 388 */       theta += 360.0D;
/*     */     }
/* 390 */     if (theta == 360.0D) {
/* 391 */       theta = 0.0D;
/*     */     }
/*     */ 
/* 394 */     if ((binSize == 5) && (theta > 180.0D))
/*     */     {
/* 396 */       System.err.println("not possible angle in binSize=5 :" + theta + ", " + x + ", " + y);
/* 397 */       System.exit(1);
/*     */     }
/*     */ 
/* 404 */     for (int i = 0; i < binSize; i++)
/*     */     {
/* 406 */       if ((45 * i >= theta) || (theta > 45 * (i + 1)))
/*     */         continue;
/* 408 */       BinNum[0] = i;
/*     */ 
/* 410 */       if (i != 7) {
/* 411 */         BinNum[1] = (i + 1); break;
/*     */       }
/* 413 */       BinNum[1] = 0.0D;
/*     */ 
/* 415 */       break;
/*     */     }
/*     */ 
/* 421 */     BinNum[2] = theta;
/*     */ 
/* 426 */     return BinNum;
/*     */   }
/*     */ 
/*     */   private void makeNormMatArr(short[] depthMap, int x, int y, int width, int height) {
/* 430 */     int imageHeight = height;
/* 431 */     int imageWidth = width;
/*     */ 
/* 433 */     opencv_core.CvMat pt0 = opencv_core.cvCreateMat(3, 1, opencv_core.CV_32FC1);
/* 434 */     opencv_core.CvMat pt1 = opencv_core.cvCreateMat(3, 1, opencv_core.CV_32FC1);
/* 435 */     opencv_core.CvMat pt2 = opencv_core.cvCreateMat(3, 1, opencv_core.CV_32FC1);
/* 436 */     opencv_core.CvMat pt3 = opencv_core.cvCreateMat(3, 1, opencv_core.CV_32FC1);
/* 437 */     opencv_core.CvMat mat = opencv_core.cvCreateMat(3, 1, opencv_core.CV_32FC1);
/*     */ 
/* 439 */     opencv_core.CvMat v1 = opencv_core.cvCreateMat(3, 1, opencv_core.CV_32FC1);
/* 440 */     opencv_core.CvMat v2 = opencv_core.cvCreateMat(3, 1, opencv_core.CV_32FC1);
/* 441 */     opencv_core.CvMat normal = opencv_core.cvCreateMat(3, 1, opencv_core.CV_32FC1);
/*     */ 
/* 448 */     for (int i = 0; i < imageWidth; i++) {
/* 449 */       for (int j = 0; j < imageHeight; j++)
/*     */       {
/* 451 */         if ((i == 0) || (j == 0) || (i == imageWidth - 1) || (j == imageHeight - 1))
/*     */         {
/* 454 */           opencv_core.cvSetZero(mat);
/* 455 */           NormMatArr[(i + j * imageWidth)] = mat;
/*     */         }
/*     */         else
/*     */         {
/* 462 */           pt0.put(0, 0, i);
/* 463 */           pt1.put(0, 0, i);
/* 464 */           pt2.put(0, 0, i - 1);
/* 465 */           pt3.put(0, 0, i + 1);
/*     */ 
/* 469 */           pt0.put(1, 0, j - 1);
/* 470 */           pt1.put(1, 0, j + 1);
/* 471 */           pt2.put(1, 0, j);
/* 472 */           pt3.put(1, 0, j);
/*     */ 
/* 475 */           pt0.put(2, 0, depthMap[(i + (j - 1) * imageWidth)]);
/* 476 */           pt1.put(2, 0, depthMap[(i + (j + 1) * imageWidth)]);
/* 477 */           pt2.put(2, 0, depthMap[(i - 1 + j * imageWidth)]);
/* 478 */           pt3.put(2, 0, depthMap[(i + 1 + j * imageWidth)]);
/*     */ 
/* 480 */           opencv_core.cvSetZero(v1);
/* 481 */           opencv_core.cvSetZero(v2);
/*     */ 
/* 484 */           NormMatArr[(i + j * imageWidth)] = getFittedPlaneNorm(v1, v2, NormMatArr[(i + j * imageWidth)], pt0, pt1, pt2, pt3);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 497 */     opencv_core.cvReleaseMat(v1);
/* 498 */     opencv_core.cvReleaseMat(v2);
/*     */ 
/* 500 */     opencv_core.cvReleaseMat(pt0);
/* 501 */     opencv_core.cvReleaseMat(pt1);
/* 502 */     opencv_core.cvReleaseMat(pt2);
/* 503 */     opencv_core.cvReleaseMat(pt3);
/* 504 */     opencv_core.cvReleaseMat(mat);
/*     */   }
/*     */ 
/*     */   protected opencv_core.CvMat getFittedPlaneNorm(opencv_core.CvMat v1, opencv_core.CvMat v2, opencv_core.CvMat normal, opencv_core.CvMat pt0, opencv_core.CvMat pt1, opencv_core.CvMat pt2, opencv_core.CvMat pt3)
/*     */   {
/* 512 */     for (int i = 0; i < 3; i++)
/*     */     {
/* 515 */       v1.put(i, 0, pt1.get(i, 0) - pt0.get(i, 0));
/*     */ 
/* 517 */       v2.put(i, 0, pt3.get(i, 0) - pt2.get(i, 0));
/*     */     }
/*     */ 
/* 522 */     opencv_core.cvCrossProduct(v1, v2, normal);
/*     */ 
/* 524 */     if (normal.get(2, 0) < 0.0D)
/*     */     {
/* 526 */       normal.put(0, 0, normal.get(0, 0) * -1.0D);
/* 527 */       normal.put(1, 0, normal.get(1, 0) * -1.0D);
/* 528 */       normal.put(2, 0, normal.get(2, 0) * -1.0D);
/*     */     }
/*     */ 
/* 533 */     return normal;
/*     */   }
/*     */ 
/*     */   protected opencv_core.CvMat getProjectVector(opencv_core.CvMat vector, opencv_core.CvMat norm, int mode)
/*     */   {
/* 539 */     switch (mode)
/*     */     {
/*     */     case 0:
/* 543 */       vector.put(0, 0, norm.get(0, 0));
/* 544 */       vector.put(1, 0, norm.get(1, 0));
/* 545 */       vector.put(2, 0, 0.0D);
/*     */ 
/* 549 */       return vector;
/*     */     case 1:
/* 554 */       vector.put(0, 0, 0.0D);
/* 555 */       vector.put(1, 0, norm.get(1, 0));
/* 556 */       vector.put(2, 0, norm.get(2, 0));
/*     */ 
/* 558 */       return vector;
/*     */     case 2:
/* 564 */       vector.put(0, 0, norm.get(0, 0));
/* 565 */       vector.put(1, 0, 0.0D);
/* 566 */       vector.put(2, 0, norm.get(2, 0));
/*     */ 
/* 568 */       return vector;
/*     */     }
/*     */ 
/* 572 */     return null;
/*     */   }
/*     */ 
/*     */   public int getDominanAngleIdx()
/*     */   {
/* 582 */     double maximum = -10.0D;
/* 583 */     int maxIdx = -1;
/* 584 */     double sum = 0.0D;
/* 585 */     for (int k = 0; k < 8; k++) {
/* 586 */       sum = 0.0D;
/*     */ 
/* 588 */       for (int i = 0; i < 8; i++) {
/* 589 */         for (int j = 0; j < 4; j++) {
/* 590 */           sum += HistogramXY[i][j][k];
/*     */         }
/*     */       }
/*     */ 
/* 594 */       if (maximum >= sum)
/*     */         continue;
/* 596 */       maximum = sum;
/* 597 */       maxIdx = k;
/*     */     }
/*     */ 
/* 601 */     if (maxIdx < 4)
/* 602 */       maxIdx = 8 - maxIdx;
/* 603 */     return maxIdx;
/*     */   }
/*     */ }

/* Location:           C:\Web_java\eclipse\open_cv_workspace\CV_PROJECT_DEPTH\CV_PROJECT_DEPTH\bin\
 * Qualified Name:     depthPack.FeatureDescriptor
 * JD-Core Version:    0.6.0
 */