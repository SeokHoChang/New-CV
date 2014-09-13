/*    */ package depthPack;
/*    */ 
/*    */ import com.googlecode.javacv.cpp.opencv_core;
/*    */ import com.googlecode.javacv.cpp.opencv_core.CvMat;
/*    */ import com.googlecode.javacv.cpp.opencv_core.CvPoint2D32f;
/*    */ import com.googlecode.javacv.cpp.opencv_core.IplImage;
/*    */ import com.googlecode.javacv.cpp.opencv_imgproc;
/*    */ 
/*    */ public class ImageController
/*    */ {
/*    */   private static opencv_core.IplImage Canvas;
/*    */   private static opencv_core.IplImage Image;
/*    */ 
/*    */   public ImageController()
/*    */   {
/* 17 */     Canvas = opencv_core.cvCreateImage(opencv_core.cvSize(640, 480), 32, 3);
/*    */   }
/*    */ 
/*    */   public opencv_core.IplImage setImageOrientation(opencv_core.IplImage img, double angle)
/*    */   {
/* 23 */     opencv_core.IplImage dst = opencv_core.cvCloneImage(img);
/* 24 */     double scale = 1.0D;
/*    */ 
/* 26 */     opencv_core.CvPoint2D32f center = new opencv_core.CvPoint2D32f(new double[] { img.width() / 2, img.height() / 2 });
/* 27 */     opencv_core.CvMat rotM = opencv_core.cvCreateMat(2, 3, opencv_core.CV_32FC1);
/* 28 */     opencv_imgproc.cv2DRotationMatrix(center, angle, scale, rotM);
/* 29 */     opencv_imgproc.cvWarpAffine(img, dst, rotM);
/* 30 */     return dst;
/*    */   }
/*    */ }

/* Location:           C:\Web_java\eclipse\open_cv_workspace\CV_PROJECT_DEPTH\CV_PROJECT_DEPTH\bin\
 * Qualified Name:     depthPack.ImageController
 * JD-Core Version:    0.6.0
 */