/*     */ package uk.co.mmscomputing.device.twain;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class TwainCapability
/*     */   implements TwainConstants
/*     */ {
/*     */   protected TwainSource source;
/*     */   protected int cap;
/*  18 */   protected byte[] buf = new byte[jtwain.getPtrSize() == 4 ? 8 : 12];
/*     */   protected TwainContainer container;
/* 259 */   private static Hashtable caps = new Hashtable();
/*     */ 
/*     */   TwainCapability(TwainSource paramTwainSource, int paramInt)
/*     */     throws TwainIOException
/*     */   {
/*  22 */     this.source = paramTwainSource;
/*  23 */     this.cap = paramInt;
/*  24 */     this.container = null;
/*  25 */     this.container = get();
/*     */   }
/*     */ 
/*     */   TwainCapability(TwainSource paramTwainSource, int paramInt1, int paramInt2) throws TwainIOException {
/*  29 */     this.source = paramTwainSource;
/*  30 */     this.cap = paramInt1;
/*  31 */     this.container = null;
/*  32 */     switch (paramInt2) { case 1:
/*  33 */       this.container = get(); break;
/*     */     case 2:
/*  34 */       this.container = getCurrent(); break;
/*     */     case 3:
/*  35 */       this.container = getDefault(); break;
/*     */     default:
/*  36 */       this.container = get(); }
/*     */   }
/*     */ 
/*     */   private TwainContainer get(int paramInt1, int paramInt2) throws TwainIOException
/*     */   {
/*  41 */     jtwain.setINT16(this.buf, 0, this.cap);
/*  42 */     jtwain.setINT16(this.buf, 2, paramInt2);
/*     */ 
/*  45 */     this.source.call(1, 1, paramInt1, this.buf);
/*     */ 
/*  47 */     int i = jtwain.getINT16(this.buf, 2);
/*     */ 
/*  53 */     byte[] arrayOfByte = jtwain.ngetContainer(this.buf);
/*     */ 
/*  55 */     switch (i) { case 3:
/*  56 */       return new TwainArray(this.cap, arrayOfByte);
/*     */     case 4:
/*  57 */       return new TwainEnumeration(this.cap, arrayOfByte);
/*     */     case 5:
/*  58 */       return new TwainOneValue(this.cap, arrayOfByte);
/*     */     case 6:
/*  59 */       return new TwainRange(this.cap, arrayOfByte); }
/*  60 */     throw new TwainIOException(getClass().getName() + ".get:\n\tUnknown container type.");
/*     */   }
/*     */ 
/*     */   private TwainContainer get(int paramInt) throws TwainIOException
/*     */   {
/*  65 */     return get(paramInt, -1);
/*     */   }
/*     */ 
/*     */   public TwainContainer get() throws TwainIOException {
/*  69 */     this.container = get(1);
/*  70 */     return this.container;
/*     */   }
/*     */ 
/*     */   public TwainContainer getCurrent() throws TwainIOException {
/*  74 */     return get(2);
/*     */   }
/*     */ 
/*     */   public TwainContainer getDefault() throws TwainIOException {
/*  78 */     return get(3);
/*     */   }
/*     */ 
/*     */   public int querySupport() throws TwainIOException {
/*  82 */     return get(8, 5).intValue();
/*     */   }
/*     */ 
/*     */   public boolean querySupport(int paramInt) {
/*     */     try {
/*  87 */       int i = get(8, 5).intValue();
/*  88 */       return (i & paramInt) != 0;
/*     */     } catch (TwainIOException localTwainIOException) {
/*  90 */       System.out.println("3\b" + getClass().getName() + ".querySupport:\n\t" + localTwainIOException.getMessage());
/*  91 */     }return false;
/*     */   }
/*     */ 
/*     */   public TwainContainer reset() throws TwainIOException
/*     */   {
/*  96 */     this.container = get(7);
/*  97 */     return this.container;
/*     */   }
/*     */ 
/*     */   public TwainContainer set() throws TwainIOException {
/* 101 */     this.container = set(this.container);
/* 102 */     return this.container;
/*     */   }
/*     */ 
/*     */   private TwainContainer set(TwainContainer paramTwainContainer) throws TwainIOException {
/* 106 */     int i = paramTwainContainer.getType();
/* 107 */     byte[] arrayOfByte = paramTwainContainer.getBytes();
/*     */     try
/*     */     {
/* 113 */       jtwain.setINT16(this.buf, 0, this.cap);
/* 114 */       jtwain.setINT16(this.buf, 2, i);
/*     */ 
/* 116 */       jtwain.nsetContainer(this.buf, arrayOfByte);
/*     */ 
/* 124 */       this.source.call(1, 1, 6, this.buf);
/*     */     } catch (TwainResultException.CheckStatus localCheckStatus) {
/* 126 */       paramTwainContainer = get();
/*     */     } finally {
/* 128 */       jtwain.nfreeContainer(this.buf);
/*     */     }
/* 130 */     return paramTwainContainer;
/*     */   }
/*     */   public Object[] getItems() {
/* 133 */     return this.container.getItems();
/*     */   }
/* 135 */   public boolean booleanValue() throws TwainIOException { return getCurrent().booleanValue(); } 
/* 136 */   public int intValue() throws TwainIOException { return getCurrent().intValue(); } 
/* 137 */   public double doubleValue() throws TwainIOException { return getCurrent().doubleValue(); } 
/*     */   public void setCurrentValue(boolean paramBoolean) throws TwainIOException {
/* 139 */     setCurrentValue(new Boolean(paramBoolean)); } 
/* 140 */   public void setCurrentValue(int paramInt) throws TwainIOException { setCurrentValue(new Integer(paramInt)); } 
/* 141 */   public void setCurrentValue(double paramDouble) throws TwainIOException { setCurrentValue(new Double(paramDouble)); }
/*     */ 
/*     */   public void setCurrentValue(Object paramObject) throws TwainIOException {
/* 144 */     this.container.setCurrentValue(paramObject); set();
/*     */   }
/*     */   public boolean booleanDefaultValue() throws TwainIOException {
/* 147 */     return getDefault().booleanDefaultValue(); } 
/* 148 */   public int intDefaultValue() throws TwainIOException { return getDefault().intDefaultValue(); } 
/* 149 */   public double doubleDefaultValue() throws TwainIOException { return getDefault().doubleDefaultValue(); } 
/*     */   public void setDefaultValue(boolean paramBoolean) throws TwainIOException {
/* 151 */     setDefaultValue(new Boolean(paramBoolean)); } 
/* 152 */   public void setDefaultValue(int paramInt) throws TwainIOException { setDefaultValue(new Integer(paramInt)); } 
/* 153 */   public void setDefaultValue(double paramDouble) throws TwainIOException { setDefaultValue(new Double(paramDouble)); }
/*     */ 
/*     */   public void setDefaultValue(Object paramObject) throws TwainIOException {
/* 156 */     this.container.setDefaultValue(paramObject); set();
/*     */   }
/*     */ 
/*     */   protected String toString(String[] paramArrayOfString) {
/* 160 */     String str = getClass().getName() + "\n";
/*     */ 
/* 162 */     Object[] arrayOfObject = this.container.getItems();
/* 163 */     for (int i = 0; i < arrayOfObject.length; i++) {
/* 164 */       int j = ((Number)arrayOfObject[i]).intValue();
/* 165 */       str = str + paramArrayOfString[j] + "\n";
/*     */     }
/* 167 */     str = str + "\n" + this.container.toString();
/* 168 */     return str;
/*     */   }
/*     */ 
/*     */   public String getName() {
/* 172 */     String str = getCapName(this.cap);
/* 173 */     if (str == null) {
/* 174 */       return "Cap_0x" + Integer.toHexString(this.cap);
/*     */     }
/* 176 */     return str;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 180 */     String str = getClass().getName() + "\n";
/* 181 */     str = str + "name         = " + getName() + "\n";
/* 182 */     str = str + this.container.toString();
/* 183 */     return str;
/*     */   }
/*     */ 
/*     */   public static TwainCapability[] getCapabilities(TwainSource paramTwainSource) throws TwainIOException {
/* 187 */     TwainCapability localTwainCapability = paramTwainSource.getCapability(4101);
/* 188 */     Object[] arrayOfObject = localTwainCapability.getItems();
/* 189 */     Vector localVector = new Vector();
/*     */ 
/* 191 */     for (int i = 0; i < arrayOfObject.length; i++) {
/* 192 */       int j = ((Number)arrayOfObject[i]).intValue();
/*     */       try {
/* 194 */         switch (j) { case 256:
/* 195 */           localVector.add(new Compression(paramTwainSource)); break;
/*     */         case 259:
/* 196 */           localVector.add(new XferMech(paramTwainSource)); break;
/*     */         case 4364:
/* 197 */           localVector.add(new ImageFileFormat(paramTwainSource)); break;
/*     */         default:
/* 198 */           localVector.add(new TwainCapability(paramTwainSource, j)); }
/*     */       } catch (TwainFailureException.BadCap localBadCap) {
/*     */       } catch (TwainFailureException.CapUnsupported localCapUnsupported) {
/*     */       }
/*     */       catch (TwainIOException localTwainIOException) {
/* 203 */         String str = getCapName(j);
/* 204 */         if (str == null) str = "Cap_0x" + Integer.toHexString(j) + "[" + Integer.toString(j) + "]";
/* 205 */         System.out.println("3\b\n" + str + "\n\t" + localTwainIOException.toString());
/*     */       }
/*     */     }
/* 208 */     return (TwainCapability[])localVector.toArray(new TwainCapability[0]);
/*     */   }
/*     */ 
/*     */   public static String getCapName(int paramInt)
/*     */   {
/* 255 */     return (String)caps.get(new Integer(paramInt));
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 260 */     caps.put(new Integer(1), "CAP_XFERCOUNT");
/* 261 */     caps.put(new Integer(256), "ICAP_COMPRESSION");
/* 262 */     caps.put(new Integer(257), "ICAP_PIXELTYPE");
/* 263 */     caps.put(new Integer(258), "ICAP_UNITS");
/* 264 */     caps.put(new Integer(259), "ICAP_XFERMECH");
/*     */ 
/* 266 */     caps.put(new Integer(4096), "CAP_AUTHOR");
/* 267 */     caps.put(new Integer(4097), "CAP_CAPTION");
/* 268 */     caps.put(new Integer(4098), "CAP_FEEDERENABLED");
/* 269 */     caps.put(new Integer(4099), "CAP_FEEDERLOADED");
/* 270 */     caps.put(new Integer(4100), "CAP_TIMEDATE");
/* 271 */     caps.put(new Integer(4101), "CAP_SUPPORTEDCAPS");
/* 272 */     caps.put(new Integer(4102), "CAP_EXTENDEDCAPS");
/* 273 */     caps.put(new Integer(4103), "CAP_AUTOFEED");
/* 274 */     caps.put(new Integer(4104), "CAP_CLEARPAGE");
/* 275 */     caps.put(new Integer(4105), "CAP_FEEDPAGE");
/* 276 */     caps.put(new Integer(4106), "CAP_REWINDPAGE");
/* 277 */     caps.put(new Integer(4107), "CAP_INDICATORS");
/* 278 */     caps.put(new Integer(4108), "CAP_SUPPORTEDCAPSEXT");
/* 279 */     caps.put(new Integer(4109), "CAP_PAPERDETECTABLE");
/* 280 */     caps.put(new Integer(4110), "CAP_UICONTROLLABLE");
/* 281 */     caps.put(new Integer(4111), "CAP_DEVICEONLINE");
/*     */ 
/* 283 */     caps.put(new Integer(4112), "CAP_AUTOSCAN");
/* 284 */     caps.put(new Integer(4113), "CAP_THUMBNAILSENABLED");
/* 285 */     caps.put(new Integer(4114), "CAP_DUPLEX");
/* 286 */     caps.put(new Integer(4115), "CAP_DUPLEXENABLED");
/* 287 */     caps.put(new Integer(4116), "CAP_ENABLEDSUIONLY");
/* 288 */     caps.put(new Integer(4117), "CAP_CUSTOMDSDATA");
/* 289 */     caps.put(new Integer(4118), "CAP_ENDORSER");
/* 290 */     caps.put(new Integer(4119), "CAP_JOBCONTROL");
/* 291 */     caps.put(new Integer(4120), "CAP_ALARMS");
/* 292 */     caps.put(new Integer(4121), "CAP_ALARMVOLUME");
/* 293 */     caps.put(new Integer(4122), "CAP_AUTOMATICCAPTURE");
/* 294 */     caps.put(new Integer(4123), "CAP_TIMEBEFOREFIRSTCAPTURE");
/* 295 */     caps.put(new Integer(4124), "CAP_TIMEBETWEENCAPTURES");
/* 296 */     caps.put(new Integer(4125), "CAP_CLEARBUFFERS");
/* 297 */     caps.put(new Integer(4126), "CAP_MAXBATCHBUFFERS");
/* 298 */     caps.put(new Integer(4127), "CAP_DEVICETIMEDATE");
/*     */ 
/* 300 */     caps.put(new Integer(4128), "CAP_POWERSUPPLY");
/* 301 */     caps.put(new Integer(4129), "CAP_CAMERAPREVIEWUI");
/* 302 */     caps.put(new Integer(4130), "CAP_DEVICEEVENT");
/* 303 */     caps.put(new Integer(4131), "CAP_PAGEMULTIPLEACQUIRE");
/* 304 */     caps.put(new Integer(4132), "CAP_SERIALNUMBER");
/* 305 */     caps.put(new Integer(4133), "CAP_FILESYSTEM");
/* 306 */     caps.put(new Integer(4134), "CAP_PRINTER");
/* 307 */     caps.put(new Integer(4135), "CAP_PRINTERENABLED");
/* 308 */     caps.put(new Integer(4136), "CAP_PRINTERINDEX");
/* 309 */     caps.put(new Integer(4137), "CAP_PRINTERMODE");
/* 310 */     caps.put(new Integer(4138), "CAP_PRINTERSTRING");
/* 311 */     caps.put(new Integer(4139), "CAP_PRINTERSUFFIX");
/* 312 */     caps.put(new Integer(4140), "CAP_LANGUAGE");
/* 313 */     caps.put(new Integer(4141), "CAP_FEEDERALIGNMENT");
/* 314 */     caps.put(new Integer(4142), "CAP_FEEDERORDER");
/* 315 */     caps.put(new Integer(4143), "CAP_PAPERBINDING");
/*     */ 
/* 317 */     caps.put(new Integer(4144), "CAP_REACQUIREALLOWED");
/* 318 */     caps.put(new Integer(4145), "CAP_PASSTHRU");
/* 319 */     caps.put(new Integer(4146), "CAP_BATTERYMINUTES");
/* 320 */     caps.put(new Integer(4147), "CAP_BATTERYPERCENTAGE");
/* 321 */     caps.put(new Integer(4148), "CAP_POWERDOWNTIME");
/*     */ 
/* 323 */     caps.put(new Integer(4352), "ICAP_AUTOBRIGHT");
/* 324 */     caps.put(new Integer(4353), "ICAP_BRIGHTNESS");
/*     */ 
/* 326 */     caps.put(new Integer(4355), "ICAP_CONTRAST");
/* 327 */     caps.put(new Integer(4356), "ICAP_CUSTHALFTONE");
/* 328 */     caps.put(new Integer(4357), "ICAP_EXPOSURETIME");
/* 329 */     caps.put(new Integer(4358), "ICAP_FILTER");
/* 330 */     caps.put(new Integer(4359), "ICAP_FLASHUSED");
/* 331 */     caps.put(new Integer(4360), "ICAP_GAMMA");
/* 332 */     caps.put(new Integer(4361), "ICAP_HALFTONES");
/* 333 */     caps.put(new Integer(4362), "ICAP_HIGHLIGHT");
/*     */ 
/* 335 */     caps.put(new Integer(4364), "ICAP_IMAGEFILEFORMAT");
/* 336 */     caps.put(new Integer(4365), "ICAP_LAMPSTATE");
/* 337 */     caps.put(new Integer(4366), "ICAP_LIGHTSOURCE");
/*     */ 
/* 340 */     caps.put(new Integer(4368), "ICAP_ORIENTATION");
/* 341 */     caps.put(new Integer(4369), "ICAP_PHYSICALWIDTH");
/* 342 */     caps.put(new Integer(4370), "ICAP_PHYSICALHEIGHT");
/* 343 */     caps.put(new Integer(4371), "ICAP_SHADOW");
/* 344 */     caps.put(new Integer(4372), "ICAP_FRAMES");
/*     */ 
/* 346 */     caps.put(new Integer(4374), "ICAP_XNATIVERESOLUTION");
/* 347 */     caps.put(new Integer(4375), "ICAP_YNATIVERESOLUTION");
/* 348 */     caps.put(new Integer(4376), "ICAP_XRESOLUTION");
/* 349 */     caps.put(new Integer(4377), "ICAP_YRESOLUTION");
/* 350 */     caps.put(new Integer(4378), "ICAP_MAXFRAMES");
/* 351 */     caps.put(new Integer(4379), "ICAP_TILES");
/* 352 */     caps.put(new Integer(4380), "ICAP_BITORDER");
/* 353 */     caps.put(new Integer(4381), "ICAP_CCITTKFACTOR");
/* 354 */     caps.put(new Integer(4382), "ICAP_LIGHTPATH");
/* 355 */     caps.put(new Integer(4383), "ICAP_PIXELFLAVOR");
/*     */ 
/* 357 */     caps.put(new Integer(4384), "ICAP_PLANARCHUNKY");
/* 358 */     caps.put(new Integer(4385), "ICAP_ROTATION");
/* 359 */     caps.put(new Integer(4386), "ICAP_SUPPORTEDSIZES");
/* 360 */     caps.put(new Integer(4387), "ICAP_THRESHOLD");
/* 361 */     caps.put(new Integer(4388), "ICAP_XSCALING");
/* 362 */     caps.put(new Integer(4389), "ICAP_YSCALING");
/* 363 */     caps.put(new Integer(4390), "ICAP_BITORDERCODES");
/* 364 */     caps.put(new Integer(4391), "ICAP_PIXELFLAVORCODES");
/* 365 */     caps.put(new Integer(4392), "ICAP_JPEGPIXELTYPE");
/*     */ 
/* 367 */     caps.put(new Integer(4394), "ICAP_TIMEFILL");
/* 368 */     caps.put(new Integer(4395), "ICAP_BITDEPTH");
/* 369 */     caps.put(new Integer(4396), "ICAP_BITDEPTHREDUCTION");
/* 370 */     caps.put(new Integer(4397), "ICAP_UNDEFINEDIMAGESIZE");
/* 371 */     caps.put(new Integer(4398), "ICAP_IMAGEDATASET");
/* 372 */     caps.put(new Integer(4399), "ICAP_EXTIMAGEINFO");
/*     */ 
/* 374 */     caps.put(new Integer(4400), "ICAP_MINIMUMHEIGHT");
/* 375 */     caps.put(new Integer(4401), "ICAP_MINIMUMWIDTH");
/*     */ 
/* 378 */     caps.put(new Integer(4404), "ICAP_AUTODISCARDBLANKPAGES");
/*     */ 
/* 380 */     caps.put(new Integer(4406), "ICAP_FLIPROTATION");
/* 381 */     caps.put(new Integer(4407), "ICAP_BARCODEDETECTIONENABLED");
/* 382 */     caps.put(new Integer(4408), "ICAP_SUPPORTEDBARCODETYPES");
/* 383 */     caps.put(new Integer(4409), "ICAP_BARCODEMAXSEARCHPRIORITIES");
/* 384 */     caps.put(new Integer(4410), "ICAP_BARCODESEARCHPRIORITIES");
/* 385 */     caps.put(new Integer(4411), "ICAP_BARCODESEARCHMODE");
/* 386 */     caps.put(new Integer(4412), "ICAP_BARCODEMAXRETRIES");
/* 387 */     caps.put(new Integer(4413), "ICAP_BARCODETIMEOUT");
/* 388 */     caps.put(new Integer(4414), "ICAP_ZOOMFACTOR");
/* 389 */     caps.put(new Integer(4415), "ICAP_PATCHCODEDETECTIONENABLED");
/*     */ 
/* 391 */     caps.put(new Integer(4416), "ICAP_SUPPORTEDPATCHCODETYPES");
/* 392 */     caps.put(new Integer(4417), "ICAP_PATCHCODEMAXSEARCHPRIORITIES");
/* 393 */     caps.put(new Integer(4418), "ICAP_PATCHCODESEARCHPRIORITIES");
/* 394 */     caps.put(new Integer(4419), "ICAP_PATCHCODESEARCHMODE");
/* 395 */     caps.put(new Integer(4420), "ICAP_PATCHCODEMAXRETRIES");
/* 396 */     caps.put(new Integer(4421), "ICAP_PATCHCODETIMEOUT");
/* 397 */     caps.put(new Integer(4422), "ICAP_FLASHUSED2");
/* 398 */     caps.put(new Integer(4423), "ICAP_IMAGEFILTER");
/* 399 */     caps.put(new Integer(4424), "ICAP_NOISEFILTER");
/* 400 */     caps.put(new Integer(4425), "ICAP_OVERSCAN");
/*     */ 
/* 402 */     caps.put(new Integer(4432), "ICAP_AUTOMATICBORDERDETECTION");
/* 403 */     caps.put(new Integer(4433), "ICAP_AUTOMATICDESKEW");
/* 404 */     caps.put(new Integer(4434), "ICAP_AUTOMATICROTATE");
/*     */   }
/*     */ 
/*     */   public static class ImageFileFormat extends TwainCapability
/*     */   {
/*     */     ImageFileFormat(TwainSource paramTwainSource)
/*     */       throws TwainIOException
/*     */     {
/* 244 */       super(4364);
/*     */     }
/*     */     public String toString() {
/* 247 */       return toString(TwainConstants.ImageFileFormatStrings);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class XferMech extends TwainCapability
/*     */   {
/*     */     XferMech(TwainSource paramTwainSource)
/*     */       throws TwainIOException
/*     */     {
/* 225 */       super(259);
/*     */     }
/*     */     public String toString() {
/* 228 */       return toString(TwainConstants.XferMechStrings);
/*     */     }
/*     */     public int intValue() {
/*     */       try {
/* 232 */         return super.intValue();
/*     */       } catch (Exception localException) {
/* 234 */         System.err.println(getClass().getName() + ".intValue:\n\t" + localException);
/* 235 */       }return 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Compression extends TwainCapability
/*     */   {
/*     */     Compression(TwainSource paramTwainSource)
/*     */       throws TwainIOException
/*     */     {
/* 215 */       super(256);
/*     */     }
/*     */     public String toString() {
/* 218 */       return toString(TwainConstants.CompressionStrings);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.twain.TwainCapability
 * JD-Core Version:    0.6.2
 */