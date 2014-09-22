/*     */ package depthPack;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ 
/*     */ public class TestSetMaker
/*     */ {
/*  19 */   private static FileOutputStream fos = null;
/*  20 */   private static BufferedOutputStream bos = null;
/*     */ 
/*  22 */   private static FileInputStream file = null;
/*  23 */   private static BufferedInputStream fis = null;
/*  24 */   private static int data_len = 0;
/*     */   private static String fname;
/*     */ 
/*     */   private static byte[] toBytes(Short input)
/*     */   {
/*  30 */     ByteBuffer buffer = ByteBuffer.allocate(2);
/*  31 */     buffer.putShort(input.shortValue());
/*  32 */     buffer.flip();
/*  33 */     return buffer.array();
/*     */   }
/*     */ 
/*     */   private static int bytesToShort(byte[] bytes)
/*     */   {
/*  38 */     int newValue = 0;
/*  39 */     newValue |= bytes[0] << 8 & 0xFF00;
/*  40 */     newValue |= bytes[1] & 0xFF;
/*     */ 
/*  42 */     return newValue;
/*     */   }
/*     */ 
/*     */   public static void createTestFile(String file)
/*     */   {
/*  47 */     fname = file;
/*  48 */     File f = new File(fname);
/*  49 */     if (!f.exists())
/*     */     {
/*     */       try
/*     */       {
/*  53 */         f.createNewFile();
/*     */       }
/*     */       catch (IOException e) {
/*  56 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void recordReady(int width, int height)
/*     */   {
/*     */     try {
/*  64 */       fos = new FileOutputStream(fname);
/*  65 */       bos = new BufferedOutputStream(fos, width);
/*     */ 
/*  67 */       int offset = 0;
/*  68 */       bos.write(toBytes(Short.valueOf((short)width)), offset, 2);
/*  69 */       bos.write(toBytes(Short.valueOf((short)height)), offset, 2);
/*     */     }
/*     */     catch (FileNotFoundException e)
/*     */     {
/*  73 */       e.printStackTrace();
/*     */     }
/*     */     catch (IOException e) {
/*  76 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void recordFinish()
/*     */   {
/*  84 */     if (fos != null)
/*     */       try
/*     */       {
/*  87 */         fos.close();
/*     */       }
/*     */       catch (IOException e) {
/*  90 */         e.printStackTrace();
/*     */       }
/*     */   }
/*     */ 
/*     */   public static void recordTestFrameSet(short[] data, int width, int height)
/*     */   {
/*  98 */     int offset = 0;
/*     */     try
/*     */     {
/* 101 */       for (int i = 0; i < width * height; i++)
/*     */       {
/* 104 */         bos.write(toBytes(Short.valueOf(data[i])), offset, 2);
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (FileNotFoundException e)
/*     */     {
/* 111 */       e.printStackTrace();
/*     */     }
/*     */     catch (IOException e) {
/* 114 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void loadReady(String name, int width)
/*     */   {
/* 122 */     byte[] temp = new byte[2];
/* 123 */     short[] data = null;
/* 124 */     int offset = 0;
/*     */     try {
/* 126 */       file = new FileInputStream(name);
/* 127 */       fis = new BufferedInputStream(file, width);
/*     */ 
/* 129 */       fis.read(temp, offset, 2);
/* 130 */       data_len = bytesToShort(temp);
/*     */ 
/* 132 */       temp = new byte[2];
/* 133 */       fis.read(temp, offset, 2);
/* 134 */       data_len *= bytesToShort(temp);
/*     */     }
/*     */     catch (FileNotFoundException e)
/*     */     {
/* 138 */       e.printStackTrace();
/*     */     }
/*     */     catch (IOException e) {
/* 141 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void loadFinish()
/*     */   {
/* 148 */     if (fis != null)
/*     */       try
/*     */       {
/* 151 */         fis.close();
/*     */       }
/*     */       catch (IOException e) {
/* 154 */         e.printStackTrace();
/*     */       }
/*     */   }
/*     */ 
/*     */   public static short[] loadTestFrameSet()
/*     */   {
/* 161 */     byte[] temp = new byte[2];
/* 162 */     short[] data = null;
/* 163 */     int offset = 0;
/*     */     try
/*     */     {
/* 166 */       data = new short[data_len];
/* 167 */       for (int i = 0; i < data_len; i++)
/*     */       {
/* 169 */         temp = new byte[2];
/* 170 */         if (fis.read(temp, offset, 2) == -1) {
/* 171 */           return null;
/*     */         }
/* 173 */         data[i] = (short)bytesToShort(temp);
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (FileNotFoundException e)
/*     */     {
/* 179 */       e.printStackTrace();
/*     */     }
/*     */     catch (IOException e) {
/* 182 */       e.printStackTrace();
/*     */     }
/* 184 */     return data;
/*     */   }
/*     */ }

