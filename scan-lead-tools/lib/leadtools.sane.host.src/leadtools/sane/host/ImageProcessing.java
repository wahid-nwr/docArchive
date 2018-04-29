/*     */ package leadtools.sane.host;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonProcessingException;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import leadtools.RasterByteOrder;
/*     */ import leadtools.RasterColor;
/*     */ import leadtools.RasterDitheringMethod;
/*     */ import leadtools.RasterImage;
/*     */ import leadtools.imageprocessing.ColorResolutionCommand;
/*     */ import leadtools.imageprocessing.ColorResolutionCommandMode;
/*     */ import leadtools.imageprocessing.ColorResolutionCommandPaletteFlags;
/*     */ import leadtools.imageprocessing.core.BorderRemoveBorderFlags;
/*     */ import leadtools.imageprocessing.core.BorderRemoveCommand;
/*     */ import leadtools.imageprocessing.core.DeskewCommand;
/*     */ import leadtools.sane.server.PageImageProcessingEvent;
/*     */ 
/*     */ class ImageProcessing
/*     */ {
/*  39 */   private static IIPFunction filpImage = new IIPFunction()
/*     */   {
/*     */     public void invoke(RasterImage image, String arguments) throws JsonProcessingException, IOException
/*     */     {
/*  43 */       boolean horizontal = HostApplicationUtils.jsonStringToBoolean(arguments, "horizontal");
/*  44 */       image.flipViewPerspective(horizontal);
/*     */     }
/*  39 */   };
/*     */ 
/*  48 */   private static IIPFunction rotateImage = new IIPFunction()
/*     */   {
/*     */     public void invoke(RasterImage image, String arguments) throws JsonProcessingException, IOException
/*     */     {
/*  52 */       int angle = HostApplicationUtils.jsonStringToInteger(arguments, "angle");
/*  53 */       while (angle > 360) angle -= 360;
/*  54 */       while (angle < 0) angle += 360;
/*     */ 
/*  56 */       if ((Math.abs(angle) != 90) && (Math.abs(angle) != 180) && (Math.abs(angle) != 270) && (Math.abs(angle) != 360)) {
/*  57 */         throw new IllegalArgumentException("Invalid angle");
/*     */       }
/*     */ 
/*  60 */       image.rotateViewPerspective(angle);
/*     */     }
/*  48 */   };
/*     */ 
/*  64 */   private static IIPFunction deskewImage = new IIPFunction()
/*     */   {
/*     */     public void invoke(RasterImage image, String arguments) throws JsonProcessingException, IOException
/*     */     {
/*  68 */       int angleRange = HostApplicationUtils.jsonStringToInteger(arguments, "angleRange");
/*  69 */       int angleResolution = HostApplicationUtils.jsonStringToInteger(arguments, "angleResolution");
/*  70 */       RasterColor fillColor = HostApplicationUtils.jsonStringToRasterColor(arguments, "fillColor");
/*  71 */       int flags = HostApplicationUtils.jsonStringToInteger(arguments, "flags");
/*     */ 
/*  73 */       DeskewCommand cmd = new DeskewCommand(fillColor, flags);
/*  74 */       cmd.setAngleRange(angleRange);
/*  75 */       cmd.setAngleResolution(angleResolution);
/*     */ 
/*  77 */       cmd.run(image);
/*     */     }
/*  64 */   };
/*     */ 
/*  81 */   private static IIPFunction borderRemove = new IIPFunction()
/*     */   {
/*     */     public void invoke(RasterImage image, String arguments) throws JsonProcessingException, IOException
/*     */     {
/*  85 */       int flags = HostApplicationUtils.jsonStringToInteger(arguments, "flags");
/*  86 */       int border = HostApplicationUtils.jsonStringToInteger(arguments, "border");
/*  87 */       int percent = HostApplicationUtils.jsonStringToInteger(arguments, "percent");
/*  88 */       int whiteNoiseLength = HostApplicationUtils.jsonStringToInteger(arguments, "whiteNoiseLength");
/*  89 */       int variance = HostApplicationUtils.jsonStringToInteger(arguments, "variance");
/*     */ 
/*  91 */       if (image.getBitsPerPixel() != 1) {
/*  92 */         ColorResolutionCommand cmdColorRes = new ColorResolutionCommand(ColorResolutionCommandMode.IN_PLACE, 1, RasterByteOrder.BGR, RasterDitheringMethod.NONE, ColorResolutionCommandPaletteFlags.FIXED.getValue(), null);
/*  93 */         cmdColorRes.run(image);
/*     */       }
/*     */ 
/*  96 */       if (border == BorderRemoveBorderFlags.NONE.getValue()) {
/*  97 */         border = new BorderRemoveCommand().getBorder();
/*     */       }
/*     */ 
/* 100 */       BorderRemoveCommand cmd = new BorderRemoveCommand(flags, border, percent, whiteNoiseLength, variance);
/*     */ 
/* 102 */       cmd.run(image);
/*     */     }
/*  81 */   };
/*     */ 
/* 106 */   private static final HashMap<String, IIPFunction> _ipCommands = new HashMap();
/*     */ 
/*     */   static {
/* 109 */     _ipCommands.put("Flip", filpImage);
/* 110 */     _ipCommands.put("Rotate", rotateImage);
/* 111 */     _ipCommands.put("Deskew", deskewImage);
/* 112 */     _ipCommands.put("BorderRemove", borderRemove);
/*     */   }
/*     */ 
/*     */   public static void run(PageImageProcessingEvent e)
/*     */     throws Exception
/*     */   {
/*  20 */     String commandName = e.getCommandName();
/*  21 */     String arguments = e.getArguments();
/*  22 */     RasterImage image = e.getImage();
/*     */ 
/*  24 */     if (e.isPreview()) {
/*  25 */       HostApplicationUtils.resizeImage(image, e.getPreviewWidth(), e.getPreviewHeight());
/*     */     }
/*     */ 
/*  28 */     IIPFunction ipFunction = null;
/*     */ 
/*  30 */     if (_ipCommands.containsKey(commandName)) {
/*  31 */       ipFunction = (IIPFunction)_ipCommands.get(commandName);
/*     */     }
/*     */ 
/*  34 */     if (ipFunction != null)
/*  35 */       ipFunction.invoke(image, arguments);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.host.jar
 * Qualified Name:     leadtools.sane.host.ImageProcessing
 * JD-Core Version:    0.6.2
 */