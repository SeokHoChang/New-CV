/*    */ package depthPack;
/*    */ 
/*    */ import com.googlecode.javacv.cpp.opencv_core;
/*    */ import com.googlecode.javacv.cpp.opencv_core.CvMat;
/*    */ import com.googlecode.javacv.cpp.opencv_core.IplImage;
/*    */ 
/*    */ public class Converter
/*    */ {
/*    */   private short[] depthmap;
/*    */   private static opencv_core.CvMat depth_mat;
/*    */ 
/*    */   public opencv_core.IplImage CvtArr2Img(opencv_core.IplImage DepthMap, short[] depthArr, int width, int height)
/*    */   {
/* 21 */     for (int i = 0; i < depthArr.length; i++)
/*    */     {
/* 23 */       int x = i % width;
/* 24 */       int y = i / width;
/*    */ 
/* 26 */       opencv_core.cvSetReal2D(DepthMap, y, x, depthArr[i]);
/*    */     }
/*    */ 
/* 29 */     return DepthMap;
/*    */   }
/*    */ 
/*    */   public short[] CvtImg2Arr(opencv_core.IplImage img)
/*    */   {
/* 34 */     this.depthmap = new short[img.height() * img.width()];
/*    */ 
/* 36 */     for (int i = 0; i < img.width(); i++) {
/* 37 */       for (int j = 0; j < img.height(); j++)
/*    */       {
/* 39 */         this.depthmap[(i + j * img.width())] = (short)(int)opencv_core.cvGetReal2D(img, j, i);
/*    */       }
/*    */ 
/*    */     }
/*    */ 
/* 44 */     return this.depthmap;
/*    */   }
/*    */ }
