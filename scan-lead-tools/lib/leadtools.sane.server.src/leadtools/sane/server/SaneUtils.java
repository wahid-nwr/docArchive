/*    */ package leadtools.sane.server;
/*    */ 
/*    */ import com.fasterxml.jackson.databind.JsonNode;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import leadtools.LeadRect;
/*    */ import leadtools.RasterImage;
/*    */ import leadtools.RasterPaintAlignMode;
/*    */ import leadtools.RasterPaintSizeMode;
/*    */ import leadtools.RasterSizeFlags;
/*    */ import leadtools.imageprocessing.SizeCommand;
/*    */ 
/*    */ class SaneUtils
/*    */ {
/*    */   static File createTempFile(String prefix, String suffix)
/*    */     throws IOException
/*    */   {
/* 12 */     File tempFile = File.createTempFile(prefix, suffix);
/* 13 */     tempFile.deleteOnExit();
/* 14 */     return tempFile;
/*    */   }
/*    */ 
/*    */   static void resizeImage(RasterImage image, int width, int height)
/*    */   {
/* 24 */     if ((image.getXResolution() != 0) && (image.getYResolution() != 0) && (Math.abs(image.getXResolution() - image.getYResolution()) > 2))
/*    */     {
/*    */       int resizeHeight;
/*    */       int resizeHeight;
/*    */       int resizeWidth;
/* 25 */       if (image.getXResolution() > image.getYResolution()) {
/* 26 */         int resizeWidth = image.getImageWidth();
/* 27 */         resizeHeight = (int)(image.getImageHeight() * image.getXResolution() / image.getYResolution());
/*    */       }
/*    */       else {
/* 30 */         resizeHeight = image.getImageHeight();
/* 31 */         resizeWidth = (int)(image.getImageWidth() * image.getYResolution() / image.getXResolution());
/*    */       }
/*    */ 
/* 34 */       SizeCommand sizeCommand = new SizeCommand(resizeWidth, resizeHeight, RasterSizeFlags.RESAMPLE.getValue() | RasterSizeFlags.SCALE_TO_GRAY.getValue());
/* 35 */       sizeCommand.run(image);
/*    */ 
/* 37 */       image.setXResolution(Math.max(image.getXResolution(), image.getYResolution()));
/* 38 */       image.setYResolution(image.getXResolution());
/*    */     }
/*    */ 
/* 42 */     if (((width == 0) && (height == 0)) || ((image.getImageWidth() <= width) && (image.getImageHeight() <= height))) {
/* 43 */       return;
/*    */     }
/*    */ 
/* 46 */     int resizeWidth = width;
/* 47 */     int resizeHeight = height;
/*    */ 
/* 51 */     if (resizeHeight == 0) {
/* 52 */       resizeHeight = (int)(image.getImageHeight() * resizeWidth / image.getImageWidth() + 0.5D);
/*    */     }
/* 54 */     else if (resizeWidth == 0) {
/* 55 */       resizeWidth = (int)(image.getImageWidth() * resizeHeight / image.getImageHeight() + 0.5D);
/*    */     }
/*    */ 
/* 59 */     LeadRect rc = new LeadRect(0, 0, resizeWidth, resizeHeight);
/* 60 */     rc = RasterImage.calculatePaintModeRectangle(image.getImageWidth(), image.getImageHeight(), rc, RasterPaintSizeMode.FIT, RasterPaintAlignMode.NEAR, RasterPaintAlignMode.NEAR);
/*    */ 
/* 63 */     SizeCommand sizeCommand = new SizeCommand(rc.getWidth(), rc.getHeight(), RasterSizeFlags.RESAMPLE.getValue() | RasterSizeFlags.SCALE_TO_GRAY.getValue());
/* 64 */     sizeCommand.run(image);
/*    */ 
/* 69 */     image.setXResolution(96);
/* 70 */     image.setYResolution(96);
/*    */   }
/*    */ 
/*    */   static String getStringFromJson(JsonNode node, String key) {
/* 74 */     String value = node.get(key).toString();
/* 75 */     value = validateJsonString(value);
/* 76 */     return value;
/*    */   }
/*    */ 
/*    */   static int getIntegerFromJson(JsonNode node, String Key) {
/* 80 */     return node.get(Key).asInt();
/*    */   }
/*    */ 
/*    */   static String validateJsonString(String jsonString) {
/* 84 */     if ((jsonString.startsWith("\"")) && (jsonString.endsWith("\""))) {
/* 85 */       jsonString = jsonString.substring(1, jsonString.length() - 1);
/*    */     }
/* 87 */     if (jsonString.contains("\\\"")) {
/* 88 */       jsonString = jsonString.replace("\\", "");
/*    */     }
/* 90 */     if (jsonString.contains("\"{")) {
/* 91 */       jsonString = jsonString.replace("\"{", "{");
/*    */     }
/* 93 */     if (jsonString.contains("}\"")) {
/* 94 */       jsonString = jsonString.replace("}\"", "}");
/*    */     }
/* 96 */     return jsonString;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.server.jar
 * Qualified Name:     leadtools.sane.server.SaneUtils
 * JD-Core Version:    0.6.2
 */