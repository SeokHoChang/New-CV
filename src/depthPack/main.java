/*      */ package depthPack;
/*      */ 
/*      */ import com.googlecode.javacpp.Loader;
/*      */ import com.googlecode.javacv.cpp.opencv_core;
/*      */ import com.googlecode.javacv.cpp.opencv_core.CvContour;
/*      */ import com.googlecode.javacv.cpp.opencv_core.CvFont;
/*      */ import com.googlecode.javacv.cpp.opencv_core.CvMat;
/*      */ import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
/*      */ import com.googlecode.javacv.cpp.opencv_core.CvRect;
/*      */ import com.googlecode.javacv.cpp.opencv_core.CvSeq;
/*      */ import com.googlecode.javacv.cpp.opencv_core.IplImage;
/*      */ import com.googlecode.javacv.cpp.opencv_highgui;
/*      */ import com.googlecode.javacv.cpp.opencv_highgui.CvCapture;
/*      */ import com.googlecode.javacv.cpp.opencv_imgproc;
/*      */ import com.googlecode.javacv.cpp.opencv_ml.CvSVM;
/*      */ import intel.pcsdk.PXCUPipeline;

import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ 
/*      */ public class main
/*      */ {
/*      */   private static final int REALTIME_MODE = 0;
/*      */   private static final int TEST_MODE = 1;
/*      */   private static final int CAPTURE_MODE = 2;
/*      */   private static final int SVMSIZE = 11;
/*      */   public static final int MATSIZE = 76800;
/*      */   private static final int DATSIZE = 576;
/*      */   private static final int testNum = 600;
/*      */   private static final int MAX_DIST = 1000;
/*      */   private static final int MIN_SEARCH_RANGE = 270;
/*      */   private static final int MAX_SEARCH_RANGE = 500;
/*      */   private static final int RANGE = 50;
/*      */   private static final int CaptureBox_X = 10;
/*      */   private static final int CaptureBox_Y = 10;
/*      */   public static final int CaptureBox_WIDTH = 90;
/*      */   public static final int CaptureBox_HEIGHT = 90;
/*      */   private static final int CaptureBox_MAT_SIZE = 8100;
/*   84 */   public static int cnt = 0;
/*      */   private static opencv_core.CvMat dataTest;
/*      */   private static opencv_core.CvMat[] dataSets;
/*      */   private static short[] depthmap_V;
/*      */   private static short[] depthmap_R;
/*      */   private static short[] depthmap_G;
/*      */   private static short[] depthmap_B;
/*      */   private static short[] captureArr;
/*      */   private static int[] Size;
/*      */   private static opencv_core.IplImage RgbMap;
/*      */   private static opencv_core.IplImage DepthMap;
/*      */   private static opencv_core.IplImage testMap;
/*      */   private static opencv_core.IplImage capture;
/*      */   private static opencv_core.IplImage DepthMap_3C;
/*      */   private static opencv_core.IplImage DepthMap_R;
/*      */   private static opencv_core.IplImage DepthMap_G;
/*      */   private static opencv_core.IplImage DepthMap_B;
/*      */   private static opencv_core.IplImage img;
/*      */   private static opencv_core.IplImage MSK;
/*      */   private static PXCUPipeline pp;
/*      */   private static TestSetMaker testSets;
/*      */   private static Classifier classifier;
/*      */   private static Converter cvt;
/*      */   private static CvSVM[] SVMs;
/*      */   private static opencv_core.CvFont font;
/*  109 */   private static int drawX = 0; private static int drawY = 0;
/*  110 */   private static int ClosestX = 0; private static int ClosestY = 0;
/*  111 */   private static int SecondX = 0; private static int SecondY = 0;
/*      */   private static opencv_core.IplImage DepthImg;
/*      */   private static short[][] confusionMat;
/*      */   private static final int confusionMat_row = 12;
/*      */   private static final int confusionMat_col = 12;
/*      */ 
/*      */   public static void init()
/*      */   {
/*  126 */     confusionMat = new short[12][12];
/*      */ 
/*  128 */     SVMs = new CvSVM[11];
/*  129 */     for (int i = 0; i < 11; i++) {
/*  130 */       Classifier svm = new Classifier();
/*  131 */       Classifier.getSVM().load("SVM_TRAINED" + i, "_0218");
/*  132 */       SVMs[i] = Classifier.getSVM();
/*      */     }
/*      */ 
/*  135 */     cvt = new Converter();
/*  136 */     dataSets = new opencv_core.CvMat[2];
/*  137 */     dataTest = opencv_core.cvCreateMat(1, 576, opencv_core.CV_32FC1);
/*      */ 
/*  139 */     for (int j = 0; j < 2; j++) {
/*  140 */       dataSets[j] = opencv_core.cvCreateMat(600, 576, opencv_core.CV_32FC1);
/*      */     }
/*  142 */     font = new opencv_core.CvFont();
/*  143 */     opencv_core.cvInitFont(font, 3, 0.7D, 0.7D, 0.0D, 0, 0);
/*      */ 
/*  145 */     classifier = new Classifier();
/*  146 */     opencv_core.cvRect(10, 10, 90, 90);
/*  147 */     opencv_core.cvRect(0, 0, 90, 90);
/*      */ 
/*  149 */     captureArr = new short[8100];
/*  150 */     depthmap_V = new short[76800];
/*  151 */     depthmap_R = new short[76800];
/*  152 */     depthmap_G = new short[76800];
/*  153 */     depthmap_B = new short[76800];
/*  154 */     Size = new int[2];
/*  155 */     DepthImg = opencv_core.cvCreateImage(opencv_core.cvSize(320, 240), 8, 1);
/*  156 */     DepthMap = opencv_core.cvCreateImage(opencv_core.cvSize(320, 240), 8, 1);
/*  157 */     MSK = opencv_core.cvCreateImage(opencv_core.cvSize(320, 240), 8, 1);
/*  158 */     DepthMap_R = opencv_core.cvCreateImage(opencv_core.cvSize(320, 240), 8, 1);
/*  159 */     DepthMap_G = opencv_core.cvCreateImage(opencv_core.cvSize(320, 240), 8, 1);
/*  160 */     DepthMap_B = opencv_core.cvCreateImage(opencv_core.cvSize(320, 240), 8, 1);
/*      */ 
/*  162 */     testMap = opencv_core.cvCreateImage(opencv_core.cvSize(320, 240), 8, 1);
/*  163 */     capture = opencv_core.cvCreateImage(opencv_core.cvSize(90, 90), 
/*  164 */       8, 1);
/*  165 */     DepthMap_3C = opencv_core.cvCreateImage(opencv_core.cvSize(320, 240), 8, 3);
/*  166 */     img = opencv_core.cvCreateImage(opencv_core.cvSize(90, 90), 
/*  167 */       8, 1);
/*      */ 
/*  169 */     opencv_core.cvSetZero(DepthMap);
/*  170 */     opencv_core.cvSetZero(testMap);
/*  171 */     opencv_core.cvSetZero(capture);
/*      */ 

/*  173 */     pp = new PXCUPipeline();
/*      */ 
/*  175 */     if (!pp.Init(PXCUPipeline.GESTURE))
/*      */     {
/*  177 */       System.out.print("Failed to initialize PXCUPipeline\n");
/*      */ 
/*  179 */       System.exit(3);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void testSet_init()
/*      */   {
/*  185 */     testSets = new TestSetMaker();
/*  186 */     TestSetMaker.createTestFile("negative8.dat");
/*  187 */     TestSetMaker.recordReady(90, 90);
/*      */   }
/*      */ 
/*      */   public static void makeTestSet(TestSetMaker ts, String file)
/*      */   {
/*  194 */     TestSetMaker.createTestFile(file);
/*  195 */     TestSetMaker.recordReady(320, 240);
/*      */ 
/*  197 */     for (int k = 0; k < 1000; k++)
/*      */     {
/*  200 */       if (!pp.AcquireFrame(true))
/*      */         break;
/*  202 */       if (!pp.QueryDepthMap(depthmap_V))
/*      */         break;
/*  204 */       pp.QueryDepthMapSize(Size);
/*      */ 
/*  208 */       TestSetMaker.recordTestFrameSet(depthmap_V, 320, 240);
/*      */ 
/*  211 */       for (int i = 0; i < depthmap_V.length; i++) {
/*  212 */         if (depthmap_V[i] < 1000) {
/*  213 */           depthmap_V[i] = (short)(int)(depthmap_V[i] / 1000.0D * 255.0D);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  218 */       DepthMap = cvt.CvtArr2Img(DepthMap, depthmap_V, 320, 240);
/*  219 */       opencv_core.cvNot(DepthMap, DepthMap);
/*      */ 
/*  222 */       opencv_highgui.cvShowImage("depth", DepthMap);
/*      */ 
/*  224 */       pp.ReleaseFrame();
/*      */ 
/*  227 */       if ((opencv_highgui.cvWaitKey(1) == 99) || (opencv_highgui.cvWaitKey(1) == 67))
/*      */       {
/*      */         break;
/*      */       }
/*      */     }
/*  232 */     TestSetMaker.recordFinish();
/*      */   }
/*      */ 
/*      */   public static void loadTestSet(String fname, int width, int height, int classnum)
/*      */   {
/*  241 */     TestSetMaker ts1 = new TestSetMaker();
/*  242 */     TestSetMaker ts2 = new TestSetMaker();
/*  243 */     TestSetMaker.loadReady(fname, width);
/*      */ 
/*  245 */     opencv_core.IplImage result = opencv_core.cvCreateImage(opencv_core.cvSize(width, height), 8, 1);
/*  246 */     short[] depthData = new short[width * height];
/*      */ 
/*  249 */     FeatureDescriptor fd0 = new FeatureDescriptor();
/*      */ 
/*  252 */     opencv_core.CvFont font = new opencv_core.CvFont();
/*      */ 
/*  254 */     opencv_core.cvInitFont(font, 3, 0.5D, 0.5D, 0.0D, 0, 0);
/*  255 */     int n = 0;
/*      */     while (true)
/*      */     {
/*  263 */       if (n < 300)
/*      */       {
/*  265 */         depthData = TestSetMaker.loadTestFrameSet();
/*      */ 
/*  268 */         depthData = Smoothing(img, depthData, 90, 
/*  269 */           90);
/*      */ 
/*  272 */         opencv_core.CvMat data0 = fd0.MK(depthData, 0, 0, 90, 90);
/*      */ 
/*  275 */         for (int j = 0; j < 576; j++) {
/*  276 */           dataSets[0].put(n, j, (float)data0.get(0, j));
/*  277 */           if (dataSets[0].get(n, j) < 0.0D) {
/*  278 */             dataSets[0].put(n, j, 0.0D);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*  284 */       else if ((n >= 300) && (n < 600)) {
/*  285 */         if (n == 300) {
/*  286 */           TestSetMaker.loadReady("negative" + classnum + ".dat", width);
/*      */         }
/*      */ 
/*  289 */         depthData = TestSetMaker.loadTestFrameSet();
/*      */ 
/*  292 */         depthData = Smoothing(img, depthData, 90, 
/*  293 */           90);
/*      */ 
/*  297 */         opencv_core.CvMat data0 = fd0.MK(depthData, 0, 0, 90, 90);
/*      */ 
/*  300 */         for (int j = 0; j < 576; j++) {
/*  301 */           dataSets[0].put(n, j, (float)data0.get(0, j));
/*  302 */           if (dataSets[0].get(n, j) >= 0.0D)
/*      */             continue;
/*  304 */           dataSets[0].put(n, j, 0.0D);
/*      */         }
/*      */ 
/*      */       }
/*  312 */       else if (n == 600) {
/*  313 */         classifier.trainSVM(dataSets[0], 600, classnum);
/*      */       }
/*      */ 
/*  318 */       if (n == 601) {
/*  319 */         int prob = 0;
/*  320 */         for (int i = 0; i < 600; i++) {
/*  321 */           for (int j = 0; j < 576; j++) {
/*  322 */             dataTest.put(0, j, (float)dataSets[0].get(i, j));
/*      */           }
/*      */ 
/*  325 */           if ((i < 300) && 
/*  326 */             (Classifier.classifySVM(dataTest) == classnum))
/*  327 */             prob++;
/*  328 */           System.out.println(i + " : " + 
/*  329 */             Classifier.classifySVM(dataTest));
/*      */         }
/*      */ 
/*  333 */         System.out.println("Hand Rate=" + prob / 600.0D);
/*  334 */         System.out.println("negative Rate=" + (1.0D - prob / 600.0D));
/*      */ 
/*  337 */         Classifier.getSVM().save("SVM_TRAINED" + classnum, "_0218");
/*      */       }
/*      */ 
/*  341 */       if (n > 601) {
/*  342 */         opencv_highgui.cvShowImage("datasets", dataSets[0]);
/*  343 */         break;
/*      */       }
/*      */ 
/*  347 */       result = cvt.CvtArr2Img(img, depthData, width, height);
/*  348 */       opencv_core.cvNot(result, result);
/*  349 */       opencv_core.cvPutText(result, "Cnt:" + n++, opencv_core.cvPoint(10, 20), font, 
/*  350 */         opencv_core.CV_RGB(0.0D, 0.0D, 255.0D));
/*  351 */       opencv_highgui.cvShowImage("depth", result);
/*  352 */       opencv_highgui.cvWaitKey(1);
/*      */     }
/*      */ 
/*  355 */     TestSetMaker.loadFinish();
/*  356 */     TestSetMaker.loadFinish();
/*      */   }
/*      */ 
/*      */   public static int matchHand(opencv_core.CvMat data)
/*      */   {
/*  362 */     int index = -1;
/*  363 */     float minDist = 100.0F;
/*  364 */     float dist = 0.0F;
/*      */ 
/*  366 */     for (int i = 0; i < 11; i++)
/*      */     {
/*  368 */       if ((dist = SVMs[i].predict(data, true)) < minDist) {
/*  369 */         cnt += 1;
/*  370 */         minDist = dist;
/*  371 */         index = i;
/*      */       }
/*      */     }
/*      */ 
/*  375 */     if (minDist >= 0.0F) {
/*  376 */       index = -1;
/*      */     }
/*      */ 
/*  380 */     return index;
/*      */   }
/*      */ 
/*      */   public static short[] Smoothing(opencv_core.IplImage img, short[] depthData, int width, int height)
/*      */   {
/*  387 */     img = cvt.CvtArr2Img(img, depthData, width, height);
/*      */ 
/*  390 */     opencv_imgproc.cvSmooth(img, img, 2, 3);
/*      */ 
/*  393 */     return cvt.CvtImg2Arr(img);
/*      */   }
/*      */ 
/*      */   public static void drawCaputeBox(int x, int y)
/*      */   {
/*  399 */     opencv_core.cvDrawCircle(DepthMap_3C, opencv_core.cvPoint(drawX, drawY), 3, 
/*  400 */       opencv_core.cvScalar(255.0D, 255.0D, 255.0D, 0.0D), 2, 1, 0);
/*      */ 
/*  402 */     x = x < 0 ? 0 : x;
/*  403 */     x = x > 229 ? 229 : x;
/*  404 */     y = y < 0 ? 0 : y;
/*  405 */     y = y > 149 ? 149 : y;
/*      */ 
/*  407 */     opencv_core.cvDrawRect(DepthMap_3C, opencv_core.cvPoint(x, y), opencv_core.cvPoint(90 + x, 90 + y), 
/*  408 */       opencv_core.cvScalar(0.0D, 255.0D, 0.0D, 0.0D), 2, 0, 0);
/*  409 */     opencv_core.cvDrawCircle(DepthMap_3C, opencv_core.cvPoint(45 + x, 45 + y), 2, 
/*  410 */       opencv_core.cvScalar(0.0D, 255.0D, 0.0D, 0.0D), 2, 0, 0);
/*      */ 
/*  412 */     opencv_core.cvDrawCircle(DepthMap_3C, opencv_core.cvPoint(45 + x, 45 + y), 36, 
/*  413 */       opencv_core.cvScalar(0.0D, 255.0D, 0.0D, 0.0D), 1, 0, 0);
/*  414 */     opencv_core.cvDrawCircle(DepthMap_3C, opencv_core.cvPoint(45 + x, 45 + y), 27, 
/*  415 */       opencv_core.cvScalar(0.0D, 255.0D, 0.0D, 0.0D), 1, 0, 0);
/*  416 */     opencv_core.cvDrawCircle(DepthMap_3C, opencv_core.cvPoint(45 + x, 45 + y), 18, 
/*  417 */       opencv_core.cvScalar(0.0D, 255.0D, 0.0D, 0.0D), 1, 0, 0);
/*  418 */     opencv_core.cvDrawCircle(DepthMap_3C, opencv_core.cvPoint(45 + x, 45 + y), 9, 
/*  419 */       opencv_core.cvScalar(0.0D, 255.0D, 0.0D, 0.0D), 1, 0, 0);
/*  420 */     for (int i = 0; i < 8; i++)
/*      */     {
/*  422 */       opencv_core.cvLine(DepthMap_3C, 
/*  423 */         opencv_core.cvPoint(45 + x, 45 + y), 
/*  424 */         opencv_core.cvPoint((int)(36.0D * Math.cos(i * 45 / 180.0D * 3.141592653589793D)) + 
/*  425 */         45 + x, 
/*  426 */         (int)(36.0D * Math.sin(i * 45 / 180.0D * 3.141592653589793D)) + 
/*  427 */         45 + y), opencv_core.cvScalar(0.0D, 255.0D, 0.0D, 0.0D), 1, 0, 0);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static opencv_core.CvMat correctNAN(opencv_core.CvMat data, opencv_core.CvMat dataTest)
/*      */   {
/*  434 */     for (int j = 0; j < 576; j++)
/*      */     {
/*  436 */       dataTest.put(0, j, (float)data.get(0, j));
/*      */ 
/*  438 */       if (dataTest.get(0, j) >= 0.0D)
/*      */         continue;
/*  440 */       dataTest.put(0, j, 0.0D);
/*      */     }
/*      */ 
/*  444 */     return dataTest;
/*      */   }
/*      */ 
/*      */   public static short[] findClosestArea(short[] map, short[] depthMap, int mode)
/*      */   {
/*  449 */     int ClosestValue = 1000;
/*  450 */     int ClosestIdx = -1;
/*      */ 
/*  452 */     for (int i = 0; i < depthMap.length; i++)
/*      */     {
/*  454 */       if (mode == 0) {
/*  455 */         if (depthMap[i] < ClosestValue) {
/*  456 */           ClosestValue = depthMap[i];
/*  457 */           ClosestIdx = i;
/*      */         }
/*      */       } else {
/*  460 */         if (((i % 320 >= ClosestX - 45) && 
/*  461 */           (i % 320 < ClosestX - 45 + 90) && 
/*  462 */           (i / 320 >= ClosestY - 45) && 
/*  463 */           (i / 320 < ClosestY - 45 + 
/*  463 */           90)) || 
/*  464 */           (depthMap[i] >= ClosestValue)) continue;
/*  465 */         ClosestValue = depthMap[i];
/*  466 */         ClosestIdx = i;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  475 */     if (mode == 0)
/*      */     {
/*  477 */       if (Math.abs(depthMap[(ClosestX + ClosestY * 320)] - 
/*  477 */         depthMap[(ClosestIdx % 320 + ClosestIdx / 320 * 320)]) > 7)
/*      */       {
/*  479 */         if (Math.sqrt(Math.pow(ClosestX - ClosestIdx % 320, 2.0D) + 
/*  479 */           Math.pow(ClosestY - ClosestIdx / 320, 2.0D)) > 10.0D) {
/*  480 */           ClosestX = ClosestIdx % 320;
/*  481 */           ClosestY = ClosestIdx / 320;
/*      */         }
/*      */       }
/*      */     } else {
/*  485 */       SecondX = ClosestIdx % 320;
/*  486 */       SecondY = ClosestIdx / 320;
/*      */     }
/*      */ 
/*  490 */     int min = ClosestValue;
/*  491 */     int max = min + 33;
/*      */ 
/*  493 */     for (int i = 0; i < depthMap.length; i++) {
/*  494 */       int X = i % 320;
/*  495 */       int Y = i / 320;
/*  496 */       double r = 0.0D;
/*  497 */       if (mode == 0)
/*  498 */         r = Math.sqrt(Math.pow(X - ClosestX, 2.0D) + 
/*  499 */           Math.pow(Y - ClosestY, 2.0D));
/*      */       else {
/*  501 */         r = Math.sqrt(Math.pow(X - SecondX, 2.0D) + 
/*  502 */           Math.pow(Y - SecondY, 2.0D));
/*      */       }
/*  504 */       if ((depthMap[i] < max) && (depthMap[i] > min) && (r < 95.0D)) {
/*  505 */         short v = depthMap[i];
/*  506 */         map[i] = v;
/*      */       } else {
/*  508 */         map[i] = 255;
/*      */       }
/*      */     }
/*  511 */     return map;
/*      */   }
/*      */ 
/*      */   public static void MEANShift(short[] depthMap, int originX, int originY, int mode)
/*      */   {
/*  517 */     double d = 1000.0D;
/*  518 */     int Xsum = 0;
/*  519 */     int Ysum = 0;
/*  520 */     int n = 1;
/*      */ 
/*  522 */     int meanX = 0;
/*  523 */     int meanY = 0;
/*      */ 
/*  525 */     while (d >= 5.0D) {
/*  526 */       for (int i = 0; i < depthMap.length; i++)
/*      */       {
/*  528 */         if (depthMap[i] != 255) {
/*  529 */           n++;
/*  530 */           Xsum += i % 320;
/*  531 */           Ysum += i / 320;
/*      */         }
/*      */       }
/*      */ 
/*  535 */       meanX = Xsum / n;
/*  536 */       meanY = Ysum / n;
/*      */ 
/*  538 */       d = Math.sqrt(Math.pow(meanX - originX, 2.0D) + 
/*  539 */         Math.pow(meanY - originY, 2.0D));
/*  540 */       originX = meanX;
/*  541 */       originY = meanY;
/*  542 */       Xsum = 0;
/*  543 */       Ysum = 0;
/*  544 */       n = 1;
/*      */     }
/*      */ 
/*  548 */     if (mode == 0) {
/*  549 */       ClosestX = meanX;
/*  550 */       ClosestY = meanY;
/*      */     } else {
/*  552 */       SecondX = meanX;
/*  553 */       SecondY = meanY;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static opencv_core.CvSeq findBiggestContour(opencv_core.CvSeq contours)
/*      */   {
/*  559 */     opencv_core.CvSeq MaxContourPtr = null;
/*  560 */     opencv_core.CvSeq MaxContourPtr1 = null;
/*  561 */     opencv_core.CvRect contourBox = null;
/*  562 */     opencv_core.CvRect contourBox1 = null;
/*  563 */     int boxArea = 0;
/*  564 */     int maxArea = -1;
/*      */ 
/*  566 */     for (opencv_core.CvSeq ptr = contours; ptr != null; ptr = ptr.h_next())
/*      */     {
/*  568 */       contourBox = opencv_imgproc.cvBoundingRect(ptr, 1);
/*      */ 
/*  570 */       boxArea = contourBox.width() * contourBox.height();
/*      */ 
/*  572 */       if (boxArea < 2025)
/*      */       {
/*      */         continue;
/*      */       }
/*  576 */       if (boxArea <= maxArea)
/*      */       {
/*      */         continue;
/*      */       }
/*      */ 
/*  583 */       maxArea = boxArea;
/*  584 */       MaxContourPtr = ptr;
/*      */     }
/*      */ 
/*  595 */     return MaxContourPtr;
/*      */   }
/*      */ 
/*      */   public static void findHand(opencv_core.IplImage DepthImg)
/*      */   {
/*  600 */     opencv_imgproc.cvThreshold(DepthImg, MSK, 0.0D, 255.0D, 0);
/*      */ 
/*  602 */     opencv_core.CvMemStorage mem = opencv_core.cvCreateMemStorage(0);
/*  603 */     opencv_core.CvSeq contours = new opencv_core.CvSeq();
/*      */ 
/*  606 */     opencv_highgui.cvShowImage("MSK", MSK);
/*  607 */     int contourNum = opencv_imgproc.cvFindContours(MSK, mem, contours, 
/*  608 */       Loader.sizeof(opencv_core.CvContour.class), 1, 
/*  609 */       2, opencv_core.cvPoint(0, 0));
/*      */ 
/*  611 */     if (contourNum > 0) {
/*  612 */       opencv_core.CvSeq Maxcontour = findBiggestContour(contours);
/*      */ 
/*  614 */       if (Maxcontour != null)
/*      */       {
/*  616 */         opencv_core.CvMemStorage storage = opencv_core.cvCreateMemStorage(0);
/*      */ 
/*  618 */         opencv_core.CvSeq poly = opencv_imgproc.cvApproxPoly(Maxcontour, 
/*  619 */           Loader.sizeof(opencv_core.CvContour.class), storage, 
/*  620 */           0, 3.0D, 1);
/*      */ 
/*  622 */         opencv_core.cvDrawContours(DepthMap_3C, poly, opencv_core.cvScalar(255.0D, 0.0D, 0.0D, 0.0D), 
/*  623 */           opencv_core.cvScalar(0.0D, 0.0D, 0.0D, 0.0D), 1, 3, 8);
/*      */ 
/*  625 */         opencv_core.cvReleaseMemStorage(storage);
/*      */       }
/*      */     }
/*      */ 
/*  629 */     opencv_core.cvReleaseMemStorage(mem);
/*      */   }
/*      */ 
/*      */   public static short[] maskDepthMap(short[] depthmap, int centerX, int centerY)
/*      */   {
/*  635 */     int minA = depthmap[(ClosestX + ClosestY * 320)];
/*  636 */     int maxA = minA + 38;
/*      */ 
/*  638 */     int minB = depthmap[(SecondX + SecondY * 320)];
/*  639 */     int maxB = minB + 33;
/*      */ 
/*  641 */     for (int i = 0; i < depthmap.length; i++) {
/*  642 */       boolean ishand1 = (i % 320 >= centerX) && 
/*  643 */         (i % 320 < centerX + 90) && 
/*  644 */         (i / 320 >= centerY) && (
/*  645 */         i / 320 < centerY + 90);
/*      */ 
/*  656 */       if (!ishand1) {
/*  657 */         depthmap[i] = 255;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  662 */     return depthmap;
/*      */   }
/*      */ 
/*      */   public static void realTimeShow(int mode, String testFrame) {
/*  666 */     opencv_highgui.CvCapture cap1 = opencv_highgui.cvCreateCameraCapture(0);
/*      */ 
/*  668 */     FeatureDescriptor fd = new FeatureDescriptor();
/*  669 */     ImageController imgCont = new ImageController();
/*  670 */     GUI gui = new GUI();
/*  671 */     boolean Chkmatch = false;
/*  672 */     boolean hold = true;
/*      */ 
/*  674 */     int cnt = 0;
/*  675 */     boolean Fin = false;
/*      */ 
/*  677 */     TestSetMaker test = new TestSetMaker();
/*      */ 
/*  680 */     if (mode == 1) {
/*  681 */       TestSetMaker.loadReady(testFrame, 320);
/*      */     }
/*      */ 
/*  684 */     while (!Fin) {
/*  685 */       RgbMap = opencv_highgui.cvQueryFrame(cap1);
/*      */ 
/*  687 */       if (mode == 0) {
/*  688 */         if (!pp.AcquireFrame(true)) {
/*      */           break;
/*      */         }
/*  691 */         pp.QueryDepthMapSize(Size);
/*      */ 
/*  693 */         if (!pp.QueryDepthMap(depthmap_V))
/*  694 */           break;
/*      */       }
/*  696 */       else if (mode == 1) {
/*  697 */         depthmap_V = TestSetMaker.loadTestFrameSet();
/*  698 */         if (depthmap_V == null)
/*      */           break;
/*  700 */         Chkmatch = true;
/*  701 */       } else if (mode == 2)
/*      */       {
/*  703 */         if (!pp.AcquireFrame(true)) {
/*      */           break;
/*      */         }
/*  706 */         pp.QueryDepthMapSize(Size);
/*      */ 
/*  708 */         if (!pp.QueryDepthMap(depthmap_V))
/*      */           break;
/*  710 */         Chkmatch = false;
/*      */       }
/*      */       else {
/*  713 */         System.out.println("Wrong MODE NUMBER");
/*      */ 
/*  715 */         break;
/*      */       }
/*      */ 
/*  719 */       for (int i = 0; i < depthmap_V.length; i++)
/*      */       {
/*  721 */         depthmap_B[i] = 0;
/*  722 */         depthmap_G[i] = 0;
/*  723 */         depthmap_R[i] = 0;
/*      */ 
/*  725 */         if (depthmap_V[i] < 1000) {
/*  726 */           depthmap_V[i] = 
/*  727 */             (short)(int)Math.round(depthmap_V[i] / 1000.0D * 255.0D);
/*      */ 
/*  729 */           if (depthmap_V[i] < 130) {
/*  730 */             depthmap_R[i] = (short)(int)(depthmap_V[i] / 130.0D * 255.0D);
/*      */           }
/*  732 */           else if ((130 < depthmap_V[i]) && (depthmap_V[i] < 200))
/*  733 */             depthmap_G[i] = (short)(int)(depthmap_V[i] / 200.0D * 255.0D);
/*  734 */           else if ((200 < depthmap_V[i]) && (depthmap_V[i] < 255))
/*  735 */             depthmap_B[i] = (short)(int)(depthmap_V[i] / 255.0D * 255.0D);
/*      */         }
/*      */         else {
/*  738 */           depthmap_V[i] = 255;
/*      */         }
/*      */       }
/*  740 */       DepthMap_R = cvt.CvtArr2Img(DepthMap_R, depthmap_R, 320, 240);
/*  741 */       DepthMap_G = cvt.CvtArr2Img(DepthMap_G, depthmap_G, 320, 240);
/*  742 */       DepthMap_B = cvt.CvtArr2Img(DepthMap_B, depthmap_B, 320, 240);
/*  743 */       DepthMap = cvt.CvtArr2Img(DepthMap, depthmap_V, 320, 240);
/*      */ 
/*  745 */       opencv_core.cvNot(DepthMap, DepthMap);
/*  746 */       opencv_core.cvMerge(DepthMap_B, DepthMap_G, DepthMap_R, null, DepthMap_3C);
/*      */ 
/*  748 */       short[] map = new short[76800];
/*  749 */       map = findClosestArea(map, depthmap_V, 0);
/*      */ 
/*  751 */       drawX = ClosestX;
/*  752 */       drawY = ClosestY;
/*      */ 
/*  754 */       MEANShift(map, ClosestX, ClosestY, 0);
/*  755 */       drawCaputeBox(ClosestX - 45, ClosestY - 55);
/*      */ 
/*  764 */       map = maskDepthMap(map, ClosestX - 45, ClosestY - 55);
/*  765 */       DepthImg = cvt.CvtArr2Img(DepthImg, map, 320, 240);
/*  766 */       opencv_core.cvNot(DepthImg, DepthImg);
/*      */ 
/*  771 */       int handNum = -1;
/*  772 */       if (Chkmatch) {
/*  773 */         captureArr = captureBoxImage(DepthMap, capture, ClosestX - 45, 
/*  774 */           ClosestY - 55, 0);
/*  775 */         captureArr = Smoothing(img, captureArr, 90, 90);
/*      */ 
/*  777 */         dataTest = correctNAN(fd.MK(captureArr, 0, 0, 90, 90), dataTest);
/*      */ 
/*  794 */         opencv_highgui.cvShowImage("CaptureBox", capture);
/*      */ 
/*  796 */         handNum = matchHand(dataTest);
/*      */ 
/*  800 */         if (mode != 1) {
/*  801 */           gui.setDrawPoint(320 - drawX, drawY);
/*  802 */           gui.setHandPosition(320 - ClosestX, ClosestY - 10);
/*  803 */           gui.countMoving();
/*      */ 
/*  805 */           gui.getHandNum(handNum);
/*  806 */           opencv_core.cvPutText(DepthMap_3C, gui.getState(), opencv_core.cvPoint(15, 15), font, 
/*  807 */             opencv_core.CV_RGB(0.0D, 180.0D, 0.0D));
/*  808 */           gui.paint();
/*      */         }
/*      */ 
/*  812 */         if (handNum != -1) {
/*  813 */           opencv_core.cvPutText(DepthMap_3C, Integer.toString(handNum), opencv_core.cvPoint(45, 45), font, 
/*  814 */             opencv_core.CV_RGB(255.0D, 0.0D, 0.0D));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  820 */       switch (opencv_highgui.cvWaitKey(1))
/*      */       {
/*      */       case 71:
/*      */       case 103:
/*  824 */         Chkmatch = !Chkmatch;
/*  825 */         break;
/*      */       case 67:
/*      */       case 99:
/*  828 */         System.out.println(cnt++);
/*  829 */         captureBoxImage(DepthMap, capture, ClosestX - 45, 
/*  830 */           ClosestY - 55, 1);
/*  831 */         opencv_highgui.cvShowImage("CaptureBox", capture);
/*  832 */         break;
/*      */       case 83:
/*      */       case 115:
/*  835 */         TestSetMaker.recordFinish();
/*  836 */         System.out.println("record finish");
/*  837 */         System.exit(1);
/*  838 */         break;
/*      */       case 27:
/*  841 */         Fin = true;
/*      */       }
/*      */ 
/*  845 */       opencv_highgui.cvShowImage("depth3C", DepthMap_3C);
/*  846 */       opencv_highgui.cvShowImage("depth1C", DepthMap);
/*  847 */       opencv_highgui.cvShowImage("RGB", RgbMap);
/*      */ 
/*  850 */       if (mode == 1)
/*      */       {
/*  852 */         chkTF(handNum);
/*      */       }
/*      */ 
/*  856 */       pp.ReleaseFrame();
/*      */     }
/*      */ 
/*  860 */     if (mode == 1) {
/*  861 */       TestSetMaker.loadFinish();
/*  862 */       printConfusionMat();
/*      */     }
/*      */ 
/*  865 */     opencv_core.cvReleaseImage(DepthMap);
/*  866 */     opencv_core.cvReleaseImage(DepthMap_3C);
/*  867 */     opencv_core.cvReleaseImage(RgbMap);
/*  868 */     opencv_core.cvReleaseImage(MSK);
/*      */   }
/*      */ 
/*      */   public static short[] captureBoxImage(opencv_core.IplImage DepthMap, opencv_core.IplImage capture, int x, int y, int mode)
/*      */   {
/*  875 */     x = x < 0 ? 0 : x;
/*  876 */     x = x > 229 ? 229 : x;
/*  877 */     y = y < 0 ? 0 : y;
/*  878 */     y = y > 149 ? 149 : y;
/*      */ 
/*  880 */     opencv_core.cvRect(x, y, 90, 90);
/*      */ 
/*  882 */     opencv_core.cvSetImageROI(DepthMap, 
/*  883 */       opencv_core.cvRect(x, y, 90, 90));
/*  884 */     opencv_core.cvCopy(DepthMap, capture);
/*  885 */     opencv_core.cvResetImageROI(DepthMap);
/*      */ 
/*  887 */     int i = 0; for (int j = 0; i < 76800; i++) {
/*  888 */       if ((i % 320 < x) || (i % 320 >= 90 + x) || 
/*  889 */         (i / 320 < y) || (i / 320 >= 90 + y)) continue;
/*  890 */       captureArr[(j++)] = depthmap_V[i];
/*      */     }
/*      */ 
/*  895 */     if (mode == 1) {
/*  896 */       TestSetMaker.recordTestFrameSet(captureArr, 90, 
/*  897 */         90);
/*      */     }
/*  899 */     return captureArr;
/*      */   }
/*      */ 
/*      */   public static void chkTF(int handNum)
/*      */   {
/*  905 */     int ans = opencv_highgui.cvWaitKey(10000);
/*      */ 
/*  907 */     switch (ans)
/*      */     {
/*      */     case 48:
/*  910 */       ans = 0;
/*  911 */       break;
/*      */     case 49:
/*  913 */       ans = 1;
/*  914 */       break;
/*      */     case 50:
/*  916 */       ans = 2;
/*  917 */       break;
/*      */     case 51:
/*  919 */       ans = 3;
/*  920 */       break;
/*      */     case 52:
/*  922 */       ans = 4;
/*  923 */       break;
/*      */     case 53:
/*  925 */       ans = 5;
/*  926 */       break;
/*      */     case 54:
/*  928 */       ans = 6;
/*  929 */       break;
/*      */     case 55:
/*  931 */       ans = 7;
/*  932 */       break;
/*      */     case 56:
/*  934 */       ans = 8;
/*  935 */       break;
/*      */     case 57:
/*  937 */       ans = 9;
/*  938 */       break;
/*      */     case 88:
/*  940 */       ans = 10;
/*  941 */       break;
/*      */     case 102:
/*  945 */       ans = -1;
/*  946 */       break;
/*      */     default:
/*  948 */       return;
/*      */     }
/*      */ 
/*  951 */     System.out.println("Answer: " + ans);
/*  952 */     System.out.println("Machin said :" + handNum);
/*      */ 
/*  954 */     int i = handNum; int j = ans;
/*  955 */     if (ans == -1)
/*  956 */       j = 11;
/*  957 */     if (handNum == -1)
/*  958 */       i = 11;
/*      */     int tmp254_253 = j;
/*      */     short[] tmp254_252 = confusionMat[i]; tmp254_252[tmp254_253] = (short)(tmp254_252[tmp254_253] + 1);
/*      */   }
/*      */ 
/*      */   public static void printConfusionMat()
/*      */   {
/*  967 */     for (int i = 0; i < 12; i++) {
/*  968 */       for (int j = 0; j < 12; j++) {
/*  969 */         System.out.print(confusionMat[i][j] + " ");
/*      */       }
/*  971 */       System.out.println();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void main(String[] args) {
/*  976 */     init();
///*      */ 	PPT2IMG p2i= new PPT2IMG("Final.pptx", "Final");
//			try {
//				p2i.converter();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
/* 1003 */     realTimeShow(0, null);
/*      */ 
/* 1008 */     pp.Close();
/*      */ 
/* 1010 */     System.exit(0);
/*      */   }
/*      */ }

/* Location:           C:\Web_java\eclipse\open_cv_workspace\CV_PROJECT_DEPTH\CV_PROJECT_DEPTH\bin\
 * Qualified Name:     depthPack.main
 * JD-Core Version:    0.6.0
 */