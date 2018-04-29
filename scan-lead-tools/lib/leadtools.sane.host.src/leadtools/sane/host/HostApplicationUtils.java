/*    */ package leadtools.sane.host;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JsonProcessingException;
/*    */ import com.fasterxml.jackson.databind.JsonNode;
/*    */ import com.fasterxml.jackson.databind.ObjectMapper;
/*    */ import java.io.IOException;
/*    */ import java.io.StringReader;
/*    */ import leadtools.LeadRect;
/*    */ import leadtools.RasterColor;
/*    */ import leadtools.RasterImage;
/*    */ import leadtools.RasterPaintAlignMode;
/*    */ import leadtools.RasterPaintSizeMode;
/*    */ import leadtools.RasterSizeFlags;
/*    */ import leadtools.imageprocessing.SizeCommand;
/*    */ 
/*    */ public class HostApplicationUtils
/*    */ {
/*    */   public static void resizeImage(RasterImage image, int width, int height)
/*    */   {
/* 19 */     if ((image.getXResolution() != 0) && (image.getYResolution() != 0) && (Math.abs(image.getXResolution() - image.getYResolution()) > 2))
/*    */     {
/*    */       int resizeHeight;
/*    */       int resizeHeight;
/*    */       int resizeWidth;
/* 20 */       if (image.getXResolution() > image.getYResolution()) {
/* 21 */         int resizeWidth = image.getImageWidth();
/* 22 */         resizeHeight = (int)(image.getImageHeight() * image.getXResolution() / image.getYResolution());
/*    */       }
/*    */       else {
/* 25 */         resizeHeight = image.getImageHeight();
/* 26 */         resizeWidth = (int)(image.getImageWidth() * image.getYResolution() / image.getXResolution());
/*    */       }
/*    */ 
/* 29 */       SizeCommand sizeCommand = new SizeCommand(resizeWidth, resizeHeight, RasterSizeFlags.RESAMPLE.getValue() | RasterSizeFlags.SCALE_TO_GRAY.getValue());
/* 30 */       sizeCommand.run(image);
/*    */ 
/* 32 */       image.setXResolution(Math.max(image.getXResolution(), image.getYResolution()));
/* 33 */       image.setYResolution(image.getXResolution());
/*    */     }
/*    */ 
/* 37 */     if (((width == 0) && (height == 0)) || ((image.getImageWidth() <= width) && (image.getImageHeight() <= height))) {
/* 38 */       return;
/*    */     }
/*    */ 
/* 41 */     int resizeWidth = width;
/* 42 */     int resizeHeight = height;
/*    */ 
/* 46 */     if (resizeHeight == 0) {
/* 47 */       resizeHeight = (int)(image.getImageHeight() * resizeWidth / image.getImageWidth() + 0.5D);
/*    */     }
/* 49 */     else if (resizeWidth == 0) {
/* 50 */       resizeWidth = (int)(image.getImageWidth() * resizeHeight / image.getImageHeight() + 0.5D);
/*    */     }
/*    */ 
/* 54 */     LeadRect rc = new LeadRect(0, 0, resizeWidth, resizeHeight);
/* 55 */     rc = RasterImage.calculatePaintModeRectangle(image.getImageWidth(), image.getImageHeight(), rc, RasterPaintSizeMode.FIT, RasterPaintAlignMode.NEAR, RasterPaintAlignMode.NEAR);
/*    */ 
/* 58 */     SizeCommand sizeCommand = new SizeCommand(rc.getWidth(), rc.getHeight(), RasterSizeFlags.RESAMPLE.getValue() | RasterSizeFlags.SCALE_TO_GRAY.getValue());
/* 59 */     sizeCommand.run(image);
/*    */ 
/* 65 */     image.setXResolution(96);
/* 66 */     image.setYResolution(96);
/*    */   }
/*    */ 
/*    */   public static int jsonStringToInteger(String jsonString, String key) throws JsonProcessingException, IOException {
/* 70 */     ObjectMapper mapper = new ObjectMapper();
/* 71 */     StringReader reader = new StringReader(jsonString);
/* 72 */     JsonNode node = mapper.readTree(reader).get(key);
/* 73 */     return node.asInt();
/*    */   }
/*    */ 
/*    */   public static boolean jsonStringToBoolean(String jsonString, String key) throws JsonProcessingException, IOException {
/* 77 */     ObjectMapper mapper = new ObjectMapper();
/* 78 */     StringReader reader = new StringReader(jsonString);
/* 79 */     JsonNode node = mapper.readTree(reader).get(key);
/* 80 */     return node.asBoolean();
/*    */   }
/*    */ 
/*    */   public static RasterColor jsonStringToRasterColor(String jsonString, String key) throws JsonProcessingException, IOException {
/* 84 */     ObjectMapper mapper = new ObjectMapper();
/* 85 */     StringReader reader = new StringReader(jsonString);
/* 86 */     JsonNode node = mapper.readTree(reader).get(key);
/* 87 */     RasterColor rasterColor = new RasterColor(node.path("A").asInt(), node.path("R").asInt(), node.path("G").asInt(), node.path("B").asInt());
/* 88 */     return rasterColor;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.host.jar
 * Qualified Name:     leadtools.sane.host.HostApplicationUtils
 * JD-Core Version:    0.6.2
 */