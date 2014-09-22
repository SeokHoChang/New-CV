/*    */ package depthPack;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
/*    */ import java.awt.geom.Rectangle2D.Float;
/*    */ import java.awt.image.BufferedImage;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import javax.imageio.ImageIO;
/*    */ import org.apache.poi.xslf.usermodel.XMLSlideShow;
/*    */ import org.apache.poi.xslf.usermodel.XSLFSlide;
/*    */ 
/*    */ public class PPT2IMG
/*    */ {
/*    */   private String pptFile;
/*    */   public String cvtImgFile;
/*    */ 
/*    */   public PPT2IMG(String pptFile, String cvtImgFile)
/*    */   {
/* 24 */     this.pptFile = pptFile;
/* 25 */     this.cvtImgFile = cvtImgFile;
/*    */   }
/*    */ 
/*    */   public void converter() throws IOException {
/* 29 */     FileInputStream is = new FileInputStream(this.pptFile);
/* 30 */     XMLSlideShow ppt = new XMLSlideShow(is);
/* 31 */     is.close();
/*    */ 
/* 33 */     double zoom = 2.0D;
/* 34 */     AffineTransform at = new AffineTransform();
/* 35 */     at.setToScale(zoom, zoom);
/*    */ 
/* 37 */     Dimension pgsize = ppt.getPageSize();
/*    */ 
/* 39 */     XSLFSlide[] slide = ppt.getSlides();
/* 40 */     for (int i = 0; i < slide.length; i++) {
/* 41 */       BufferedImage img = new BufferedImage((int)Math.ceil(pgsize.width * zoom), (int)Math.ceil(pgsize.height * zoom), 1);
/* 42 */       Graphics2D graphics = img.createGraphics();
/* 43 */       graphics.setTransform(at);
/*    */ 
/* 45 */       graphics.setPaint(Color.white);
/* 46 */       graphics.fill(new Rectangle2D.Float(0.0F, 0.0F, pgsize.width, pgsize.height));
/* 47 */       slide[i].draw(graphics);
/* 48 */       FileOutputStream out = new FileOutputStream(this.cvtImgFile + "-" + (i + 1) + ".png");
/* 49 */       ImageIO.write(img, "png", out);
/* 50 */       out.close();
/*    */     }
/*    */   }
/*    */ }

