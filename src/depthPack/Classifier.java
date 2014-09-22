/*    */ package depthPack;
/*    */ 
/*    */ import static com.googlecode.javacv.cpp.opencv_core.*;

import com.googlecode.javacv.cpp.opencv_core;
/*    */ import com.googlecode.javacv.cpp.opencv_core.CvMat;
/*    */ import com.googlecode.javacv.cpp.opencv_core.CvTermCriteria;
/*    */ import com.googlecode.javacv.cpp.opencv_ml.CvSVM;
/*    */ import com.googlecode.javacv.cpp.opencv_ml.CvSVMParams;
/*    */ 
/*    */ public class Classifier
/*    */ {
/*    */   private static final int DATSIZE =576;
/*    */   private static CvSVM svm;
/*    */   private static CvSVMParams param_svm;
/*    */   private static CvMat temp;
/*    */   private static CvMat temp_cls;
/*    */ 
/*    */   public Classifier()
/*    */   {
/* 23 */     initSVM();
/*    */   }
/*    */ 
/*    */   public void initSVM()
/*    */   {
/* 28 */     svm = new CvSVM();
/* 29 */    	CvTermCriteria criteria = cvTermCriteria(2, 1000, 1.0E-006D);
/*    */ 
/* 31 */     param_svm = 
/* 32 */       new CvSVMParams();
/* 33 */     param_svm.svm_type(CvSVM.C_SVC);
/* 34 */     param_svm.kernel_type(CvSVM.LINEAR);
/* 35 */     param_svm.degree(9.0);
/* 36 */     param_svm.gamma(8.0);
/* 37 */     param_svm.coef0(0.0);
/* 38 */     param_svm.C(100);
/* 39 */     param_svm.nu(0.9);
/* 40 */     param_svm.p(0.0D);
/* 41 */     param_svm.class_weights(null);
/* 42 */     param_svm.term_crit(criteria);
/*    */ 
/* 44 */     
/* 45 */     
/*    */   }
/*    */ 
/*    */   public static CvSVM getSVM()
/*    */   {
/* 51 */     return svm;
/*    */   }
/*    */ 
/*    */   public void trainSVM(CvMat Data, int EntrySize, int classnum)
/*    */   {
/* 59 */     CvMat temp_cls = cvCreateMat(EntrySize, 1, CV_32SC1);
/*    */ 
/* 61 */     for (int j = 0; j < EntrySize; j++) {
/* 62 */       if (j < EntrySize / 2)
/* 63 */         temp_cls.put(j, 0, classnum);
/*    */       else {
/* 65 */         temp_cls.put(j, 0, -1);
/*    */       }
			   //System.out.println(Data.get(j, 0));
/*    */     }
/*    */ 
/* 69 */     svm.train(Data, temp_cls, null, null, param_svm);
/*    */ 
/* 71 */  
/* 72 */    cvReleaseMat(temp_cls);
/*    */   }
/*    */ 
/*    */   public static float classifySVM(CvMat Data)
/*    */   {
/* 78 */     float response = svm.predict(Data, false);
/*    */ 
/* 80 */     return response;
/*    */   }
/*    */ }

