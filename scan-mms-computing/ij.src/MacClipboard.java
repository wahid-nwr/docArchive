/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.plugin.PlugIn;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Image;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.datatransfer.Clipboard;
/*     */ import java.awt.datatransfer.DataFlavor;
/*     */ import java.awt.datatransfer.Transferable;
/*     */ import java.awt.image.ImageProducer;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ public class MacClipboard extends ImagePlus
/*     */   implements PlugIn
/*     */ {
/*     */   static Clipboard clipboard;
/*     */ 
/*     */   public void run(String paramString)
/*     */   {
/*  15 */     Image localImage = showSystemClipboard();
/*  16 */     if (localImage != null) setImage(localImage); 
/*     */   }
/*     */ 
/*     */   Image showSystemClipboard()
/*     */   {
/*  20 */     Image localImage = null;
/*  21 */     if (clipboard == null)
/*  22 */       clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
/*     */     try {
/*  24 */       Transferable localTransferable = clipboard.getContents(null);
/*  25 */       localImage = displayMacImage(localTransferable);
/*     */     } catch (Throwable localThrowable) {
/*  27 */       IJ.handleException(localThrowable);
/*     */     }
/*  29 */     return localImage;
/*     */   }
/*     */ 
/*     */   Image displayMacImage(Transferable paramTransferable) {
/*  33 */     Image localImage = getMacImage(paramTransferable);
/*  34 */     if (localImage != null) {
/*  35 */       ij.WindowManager.checkForDuplicateName = true;
/*  36 */       new ImagePlus("Clipboard", localImage).show();
/*     */     }
/*  38 */     return localImage;
/*     */   }
/*     */ 
/*     */   Image getMacImage(Transferable paramTransferable)
/*     */   {
/*  45 */     if (!isQTJavaInstalled())
/*  46 */       return null;
/*  47 */     Image localImage = null;
/*  48 */     DataFlavor[] arrayOfDataFlavor = paramTransferable.getTransferDataFlavors();
/*  49 */     if ((arrayOfDataFlavor == null) || (arrayOfDataFlavor.length == 0))
/*  50 */       return null;
/*     */     try {
/*  52 */       Object localObject = paramTransferable.getTransferData(arrayOfDataFlavor[0]);
/*  53 */       if ((localObject == null) || (!(localObject instanceof InputStream)))
/*  54 */         return null;
/*  55 */       localImage = getImageFromPictStream((InputStream)localObject); } catch (Exception localException) {
/*     */     }
/*  57 */     return localImage;
/*     */   }
/*     */ 
/*     */   Image getImageFromPictStream(InputStream paramInputStream)
/*     */   {
/*     */     try
/*     */     {
/*  64 */       ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
/*     */ 
/*  68 */       byte[] arrayOfByte1 = new byte[512];
/*  69 */       byte[] arrayOfByte2 = new byte[4096];
/*  70 */       int i = 0; int j = 0;
/*  71 */       localByteArrayOutputStream.write(arrayOfByte1, 0, 512);
/*  72 */       while ((i = paramInputStream.read(arrayOfByte2, 0, 4096)) > 0)
/*  73 */         localByteArrayOutputStream.write(arrayOfByte2, 0, i);
/*  74 */       localByteArrayOutputStream.close();
/*  75 */       j = localByteArrayOutputStream.size();
/*     */ 
/*  77 */       if (j <= 0)
/*  78 */         return null;
/*  79 */       byte[] arrayOfByte3 = localByteArrayOutputStream.toByteArray();
/*     */ 
/* 106 */       Class localClass = Class.forName("quicktime.QTSession");
/* 107 */       Method localMethod = localClass.getMethod("isInitialized", null);
/* 108 */       Boolean localBoolean = (Boolean)localMethod.invoke(null, null);
/* 109 */       if (!localBoolean.booleanValue()) {
/* 110 */         localMethod = localClass.getMethod("open", null);
/* 111 */         localMethod.invoke(null, null);
/*     */       }
/* 113 */       localClass = Class.forName("quicktime.util.QTHandle");
/* 114 */       Constructor localConstructor = localClass.getConstructor(new Class[] { arrayOfByte3.getClass() });
/* 115 */       Object localObject1 = localConstructor.newInstance(new Object[] { arrayOfByte3 });
/* 116 */       String str = new String("PICT");
/* 117 */       localClass = Class.forName("quicktime.util.QTUtils");
/* 118 */       localMethod = localClass.getMethod("toOSType", new Class[] { str.getClass() });
/* 119 */       Integer localInteger1 = (Integer)localMethod.invoke(null, new Object[] { str });
/* 120 */       localClass = Class.forName("quicktime.std.image.GraphicsImporter");
/* 121 */       localConstructor = localClass.getConstructor(new Class[] { Integer.TYPE });
/* 122 */       Object localObject2 = localConstructor.newInstance(new Object[] { localInteger1 });
/* 123 */       localMethod = localClass.getMethod("setDataHandle", new Class[] { Class.forName("quicktime.util.QTHandleRef") });
/*     */ 
/* 125 */       localMethod.invoke(localObject2, new Object[] { localObject1 });
/* 126 */       localMethod = localClass.getMethod("getNaturalBounds", null);
/* 127 */       Object localObject3 = localMethod.invoke(localObject2, null);
/* 128 */       localClass = Class.forName("quicktime.app.view.GraphicsImporterDrawer");
/* 129 */       localConstructor = localClass.getConstructor(new Class[] { localObject2.getClass() });
/* 130 */       Object localObject4 = localConstructor.newInstance(new Object[] { localObject2 });
/* 131 */       localMethod = localObject3.getClass().getMethod("getWidth", null);
/* 132 */       Integer localInteger2 = (Integer)localMethod.invoke(localObject3, null);
/* 133 */       localMethod = localObject3.getClass().getMethod("getHeight", null);
/* 134 */       Integer localInteger3 = (Integer)localMethod.invoke(localObject3, null);
/* 135 */       Dimension localDimension = new Dimension(localInteger2.intValue(), localInteger3.intValue());
/* 136 */       localClass = Class.forName("quicktime.app.view.QTImageProducer");
/* 137 */       localConstructor = localClass.getConstructor(new Class[] { localObject4.getClass(), localDimension.getClass() });
/* 138 */       Object localObject5 = localConstructor.newInstance(new Object[] { localObject4, localDimension });
/* 139 */       if ((localObject5 instanceof ImageProducer))
/* 140 */         return Toolkit.getDefaultToolkit().createImage((ImageProducer)localObject5);
/*     */     } catch (Exception localException) {
/* 142 */       IJ.showStatus("" + localException);
/*     */     }
/* 144 */     return null;
/*     */   }
/*     */ 
/*     */   boolean isQTJavaInstalled()
/*     */   {
/* 150 */     boolean bool = false;
/*     */     try {
/* 152 */       Class localClass = Class.forName("quicktime.QTSession");
/* 153 */       bool = true; } catch (Exception localException) {
/*     */     }
/* 155 */     return bool;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     MacClipboard
 * JD-Core Version:    0.6.2
 */