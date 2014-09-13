/*    */ package depthPack;
/*    */ 
/*    */ import com.googlecode.javacv.cpp.opencv_core;
/*    */ import com.googlecode.javacv.cpp.opencv_core.CvMat;
/*    */ import com.googlecode.javacv.cpp.opencv_core.CvTermCriteria;
/*    */ import com.googlecode.javacv.cpp.opencv_ml.CvSVM;
/*    */ import com.googlecode.javacv.cpp.opencv_ml.CvSVMParams;
/*    */ 
/*    */ public class Classifier
/*    */ {
/*    */   private static final int DATSIZE = 576;
/*    */   private static CvSVM svm;
/*    */   private static CvSVMParams param_svm;
/*    */   private static opencv_core.CvMat temp;
/*    */   private static opencv_core.CvMat temp_cls;
/*    */ 
/*    */   public Classifier()
/*    */   {
/* 23 */     initSVM();
/*    */   }
/*    */ 
/*    */   public void initSVM()
/*    */   {
/* 28 */     svm = new CvSVM();
/* 29 */     opencv_core.CvTermCriteria criteria = opencv_core.cvTermCriteria(2, 1000, 1.0E-006D);
/*    */ 
/* 31 */     param_svm = 
/* 32 */       new CvSVMParams();
/* 33 */     param_svm.svm_type(100);
/* 34 */     param_svm.kernel_type(0);
/* 35 */     param_svm.degree(9.0D);
/* 36 */     param_svm.gamma(8.0D);
/* 37 */     param_svm.coef0(0.0D);
/* 38 */     param_svm.C(100.0D);
/* 39 */     param_svm.nu(0.9D);
/* 40 */     param_svm.p(0.0D);
/* 41 */     param_svm.class_weights(null);
/* 42 */     param_svm.term_crit(criteria);
/*    */ 
/* 44 */     temp = opencv_core.cvCreateMat(2, 576, opencv_core.CV_32FC1);
/* 45 */     temp_cls = opencv_core.cvCreateMat(2, 1, opencv_core.CV_32SC1);
/*    */   }
/*    */ 
/*    */   public static CvSVM getSVM()
/*    */   {
/* 51 */     return svm;
/*    */   }
/*    */ 
/*    */   public void trainSVM(opencv_core.CvMat Data, int EntrySize, int classnum)
/*    */   {
/* 59 */     opencv_core.CvMat temp_cls = opencv_core.cvCreateMat(EntrySize, 1, opencv_core.CV_32SC1);
/*    */ 
/* 61 */     for (int j = 0; j < EntrySize; j++) {
/* 62 */       if (j < EntrySize / 2)
/* 63 */         temp_cls.put(j, 0, classnum);
/*    */       else {
/* 65 */         temp_cls.put(j, 0, -1.0D);
/*    */       }
/*    */     }
/*    */ 
/* 69 */     svm.train(Data, temp_cls, null, null, param_svm);
/*    */ 
/* 71 */     opencv_core.cvReleaseMat(temp);
/* 72 */     opencv_core.cvReleaseMat(temp_cls);
/*    */   }
/*    */ 
/*    */   public static float classifySVM(opencv_core.CvMat Data)
/*    */   {
/* 78 */     float response = svm.predict(Data, false);
/*    */ 
/* 80 */     return response;
/*    */   }
/*    */ }

/* Location:           C:\Web_java\eclipse\open_cv_workspace\CV_PROJECT_DEPTH\CV_PROJECT_DEPTH\bin\
 * Qualified Name:     depthPack.Classifier
 * JD-Core Version:    0.6.0
 */