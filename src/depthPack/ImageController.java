/*    */ package depthPack;
/*    */ 
/*    */ import static com.googlecode.javacv.cpp.opencv_core.*;
/*    */ import static com.googlecode.javacv.cpp.opencv_imgproc.*;
/*    */ 
/*    */ public class ImageController
/*    */ {
/*    */   private static IplImage Canvas;
/*    */   private static IplImage Image;
/*    */ 
/*    */   public ImageController()
/*    */   {
/* 17 */     Canvas = cvCreateImage(cvSize(640, 480), 32, 3);
/*    */   }
/*    */ 
/*    */   public IplImage setImageOrientation(IplImage img, double angle)
/*    */   {
/* 23 */    IplImage dst = cvCloneImage(img);
/* 24 */     double scale = 1.0D;
/*    */ 
/* 26 */     CvPoint2D32f center = new CvPoint2D32f( img.width() / 2, img.height() / 2 );
/* 27 */     CvMat rotM = cvCreateMat(2, 3, CV_32FC1);
/* 28 */    cv2DRotationMatrix(center, angle, scale, rotM);
/* 29 */     cvWarpAffine(img, dst, rotM);
/* 30 */     return dst;
/*    */   }
/*    */ }

