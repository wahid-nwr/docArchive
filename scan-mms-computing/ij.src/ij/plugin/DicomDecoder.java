/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.Prefs;
/*     */ import ij.io.FileInfo;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.util.Properties;
/*     */ 
/*     */ class DicomDecoder
/*     */ {
/*     */   private static final int PIXEL_REPRESENTATION = 2621699;
/*     */   private static final int TRANSFER_SYNTAX_UID = 131088;
/*     */   private static final int MODALITY = 524384;
/*     */   private static final int SLICE_THICKNESS = 1572944;
/*     */   private static final int SLICE_SPACING = 1573000;
/*     */   private static final int IMAGER_PIXEL_SPACING = 1577316;
/*     */   private static final int SAMPLES_PER_PIXEL = 2621442;
/*     */   private static final int PHOTOMETRIC_INTERPRETATION = 2621444;
/*     */   private static final int PLANAR_CONFIGURATION = 2621446;
/*     */   private static final int NUMBER_OF_FRAMES = 2621448;
/*     */   private static final int ROWS = 2621456;
/*     */   private static final int COLUMNS = 2621457;
/*     */   private static final int PIXEL_SPACING = 2621488;
/*     */   private static final int BITS_ALLOCATED = 2621696;
/*     */   private static final int WINDOW_CENTER = 2625616;
/*     */   private static final int WINDOW_WIDTH = 2625617;
/*     */   private static final int RESCALE_INTERCEPT = 2625618;
/*     */   private static final int RESCALE_SLOPE = 2625619;
/*     */   private static final int RED_PALETTE = 2626049;
/*     */   private static final int GREEN_PALETTE = 2626050;
/*     */   private static final int BLUE_PALETTE = 2626051;
/*     */   private static final int ICON_IMAGE_SEQUENCE = 8913408;
/*     */   private static final int ITEM = -73728;
/*     */   private static final int ITEM_DELIMINATION = -73715;
/*     */   private static final int SEQUENCE_DELIMINATION = -73507;
/*     */   private static final int PIXEL_DATA = 2145386512;
/*     */   private static final int AE = 16709;
/*     */   private static final int AS = 16723;
/*     */   private static final int AT = 16724;
/*     */   private static final int CS = 17235;
/*     */   private static final int DA = 17473;
/*     */   private static final int DS = 17491;
/*     */   private static final int DT = 17492;
/*     */   private static final int FD = 17988;
/*     */   private static final int FL = 17996;
/*     */   private static final int IS = 18771;
/*     */   private static final int LO = 19535;
/*     */   private static final int LT = 19540;
/*     */   private static final int PN = 20558;
/*     */   private static final int SH = 21320;
/*     */   private static final int SL = 21324;
/*     */   private static final int SS = 21331;
/*     */   private static final int ST = 21332;
/*     */   private static final int TM = 21581;
/*     */   private static final int UI = 21833;
/*     */   private static final int UL = 21836;
/*     */   private static final int US = 21843;
/*     */   private static final int UT = 21844;
/*     */   private static final int OB = 20290;
/*     */   private static final int OW = 20311;
/*     */   private static final int SQ = 21329;
/*     */   private static final int UN = 21838;
/*     */   private static final int QQ = 16191;
/*     */   private static Properties dictionary;
/*     */   private String directory;
/*     */   private String fileName;
/*     */   private static final int ID_OFFSET = 128;
/*     */   private static final String DICM = "DICM";
/*     */   private BufferedInputStream f;
/* 249 */   private int location = 0;
/* 250 */   private boolean littleEndian = true;
/*     */   private int elementLength;
/*     */   private int vr;
/*     */   private static final int IMPLICIT_VR = 11565;
/* 255 */   private byte[] vrLetters = new byte[2];
/*     */   private int previousGroup;
/*     */   private String previousInfo;
/* 258 */   private StringBuffer dicomInfo = new StringBuffer(1000);
/*     */   private boolean dicmFound;
/*     */   private boolean oddLocations;
/* 261 */   private boolean bigEndianTransferSyntax = false;
/*     */   double windowCenter;
/*     */   double windowWidth;
/*     */   double rescaleIntercept;
/* 263 */   double rescaleSlope = 1.0D;
/*     */   boolean inSequence;
/*     */   BufferedInputStream inputStream;
/*     */   String modality;
/* 802 */   static char[] buf8 = new char[8];
/*     */   char[] buf10;
/*     */ 
/*     */   public DicomDecoder(String directory, String fileName)
/*     */   {
/* 269 */     this.directory = directory;
/* 270 */     this.fileName = fileName;
/* 271 */     String path = null;
/* 272 */     if ((dictionary == null) && (IJ.getApplet() == null)) {
/* 273 */       path = Prefs.getHomeDir() + File.separator + "DICOM_Dictionary.txt";
/* 274 */       File f = new File(path);
/* 275 */       if (f.exists()) try {
/* 276 */           dictionary = new Properties();
/* 277 */           InputStream is = new BufferedInputStream(new FileInputStream(f));
/* 278 */           dictionary.load(is);
/* 279 */           is.close();
/* 280 */           if (IJ.debugMode) IJ.log("DicomDecoder: using " + dictionary.size() + " tag dictionary at " + path); 
/*     */         }
/* 282 */         catch (Exception e) { dictionary = null; }
/*     */ 
/*     */     }
/* 285 */     if (dictionary == null) {
/* 286 */       DicomDictionary d = new DicomDictionary();
/* 287 */       dictionary = d.getDictionary();
/* 288 */       if (IJ.debugMode) IJ.log("DicomDecoder: " + path + " not found; using " + dictionary.size() + " tag built in dictionary"); 
/*     */     }
/*     */   }
/*     */ 
/*     */   String getString(int length) throws IOException {
/* 293 */     byte[] buf = new byte[length];
/* 294 */     int pos = 0;
/* 295 */     while (pos < length) {
/* 296 */       int count = this.f.read(buf, pos, length - pos);
/* 297 */       pos += count;
/*     */     }
/* 299 */     this.location += length;
/* 300 */     return new String(buf);
/*     */   }
/*     */ 
/*     */   int getByte() throws IOException {
/* 304 */     int b = this.f.read();
/* 305 */     if (b == -1) throw new IOException("unexpected EOF");
/* 306 */     this.location += 1;
/* 307 */     return b;
/*     */   }
/*     */ 
/*     */   int getShort() throws IOException {
/* 311 */     int b0 = getByte();
/* 312 */     int b1 = getByte();
/* 313 */     if (this.littleEndian) {
/* 314 */       return (b1 << 8) + b0;
/*     */     }
/* 316 */     return (b0 << 8) + b1;
/*     */   }
/*     */ 
/*     */   final int getInt() throws IOException {
/* 320 */     int b0 = getByte();
/* 321 */     int b1 = getByte();
/* 322 */     int b2 = getByte();
/* 323 */     int b3 = getByte();
/* 324 */     if (this.littleEndian) {
/* 325 */       return (b3 << 24) + (b2 << 16) + (b1 << 8) + b0;
/*     */     }
/* 327 */     return (b0 << 24) + (b1 << 16) + (b2 << 8) + b3;
/*     */   }
/*     */ 
/*     */   double getDouble() throws IOException {
/* 331 */     int b0 = getByte();
/* 332 */     int b1 = getByte();
/* 333 */     int b2 = getByte();
/* 334 */     int b3 = getByte();
/* 335 */     int b4 = getByte();
/* 336 */     int b5 = getByte();
/* 337 */     int b6 = getByte();
/* 338 */     int b7 = getByte();
/* 339 */     long res = 0L;
/* 340 */     if (this.littleEndian) {
/* 341 */       res += b0;
/* 342 */       res += (b1 << 8);
/* 343 */       res += (b2 << 16);
/* 344 */       res += (b3 << 24);
/* 345 */       res += (b4 << 32);
/* 346 */       res += (b5 << 40);
/* 347 */       res += (b6 << 48);
/* 348 */       res += (b7 << 56);
/*     */     } else {
/* 350 */       res += b7;
/* 351 */       res += (b6 << 8);
/* 352 */       res += (b5 << 16);
/* 353 */       res += (b4 << 24);
/* 354 */       res += (b3 << 32);
/* 355 */       res += (b2 << 40);
/* 356 */       res += (b1 << 48);
/* 357 */       res += (b0 << 56);
/*     */     }
/* 359 */     return Double.longBitsToDouble(res);
/*     */   }
/*     */ 
/*     */   float getFloat() throws IOException {
/* 363 */     int b0 = getByte();
/* 364 */     int b1 = getByte();
/* 365 */     int b2 = getByte();
/* 366 */     int b3 = getByte();
/* 367 */     int res = 0;
/* 368 */     if (this.littleEndian) {
/* 369 */       res += b0;
/* 370 */       res = (int)(res + (b1 << 8));
/* 371 */       res = (int)(res + (b2 << 16));
/* 372 */       res = (int)(res + (b3 << 24));
/*     */     } else {
/* 374 */       res += b3;
/* 375 */       res = (int)(res + (b2 << 8));
/* 376 */       res = (int)(res + (b1 << 16));
/* 377 */       res = (int)(res + (b0 << 24));
/*     */     }
/* 379 */     return Float.intBitsToFloat(res);
/*     */   }
/*     */ 
/*     */   byte[] getLut(int length) throws IOException {
/* 383 */     if ((length & 0x1) != 0) {
/* 384 */       String dummy = getString(length);
/* 385 */       return null;
/*     */     }
/* 387 */     length /= 2;
/* 388 */     byte[] lut = new byte[length];
/* 389 */     for (int i = 0; i < length; i++)
/* 390 */       lut[i] = ((byte)(getShort() >>> 8));
/* 391 */     return lut;
/*     */   }
/*     */ 
/*     */   int getLength() throws IOException {
/* 395 */     int b0 = getByte();
/* 396 */     int b1 = getByte();
/* 397 */     int b2 = getByte();
/* 398 */     int b3 = getByte();
/*     */ 
/* 408 */     this.vr = ((b0 << 8) + b1);
/*     */ 
/* 410 */     switch (this.vr) { case 20290:
/*     */     case 20311:
/*     */     case 21329:
/*     */     case 21838:
/*     */     case 21844:
/* 413 */       if ((b2 == 0) || (b3 == 0)) return getInt();
/*     */ 
/* 415 */       this.vr = 11565;
/* 416 */       if (this.littleEndian) {
/* 417 */         return (b3 << 24) + (b2 << 16) + (b1 << 8) + b0;
/*     */       }
/* 419 */       return (b0 << 24) + (b1 << 16) + (b2 << 8) + b3;
/*     */     case 16191:
/*     */     case 16709:
/*     */     case 16723:
/*     */     case 16724:
/*     */     case 17235:
/*     */     case 17473:
/*     */     case 17491:
/*     */     case 17492:
/*     */     case 17988:
/*     */     case 17996:
/*     */     case 18771:
/*     */     case 19535:
/*     */     case 19540:
/*     */     case 20558:
/*     */     case 21320:
/*     */     case 21324:
/*     */     case 21331:
/*     */     case 21332:
/*     */     case 21581:
/*     */     case 21833:
/*     */     case 21836:
/*     */     case 21843:
/* 424 */       if (this.littleEndian) {
/* 425 */         return (b3 << 8) + b2;
/*     */       }
/* 427 */       return (b2 << 8) + b3;
/*     */     }
/*     */ 
/* 430 */     this.vr = 11565;
/* 431 */     if (this.littleEndian) {
/* 432 */       return (b3 << 24) + (b2 << 16) + (b1 << 8) + b0;
/*     */     }
/* 434 */     return (b0 << 24) + (b1 << 16) + (b2 << 8) + b3;
/*     */   }
/*     */ 
/*     */   int getNextTag() throws IOException
/*     */   {
/* 439 */     int groupWord = getShort();
/* 440 */     if ((groupWord == 2048) && (this.bigEndianTransferSyntax)) {
/* 441 */       this.littleEndian = false;
/* 442 */       groupWord = 8;
/*     */     }
/* 444 */     int elementWord = getShort();
/* 445 */     int tag = groupWord << 16 | elementWord;
/* 446 */     this.elementLength = getLength();
/*     */ 
/* 450 */     if ((this.elementLength == 13) && (!this.oddLocations)) this.elementLength = 10;
/*     */ 
/* 454 */     if (this.elementLength == -1) {
/* 455 */       this.elementLength = 0;
/* 456 */       this.inSequence = true;
/*     */     }
/*     */ 
/* 459 */     return tag;
/*     */   }
/*     */ 
/*     */   FileInfo getFileInfo() throws IOException
/*     */   {
/* 464 */     FileInfo fi = new FileInfo();
/* 465 */     int bitsAllocated = 16;
/* 466 */     fi.fileFormat = 1;
/* 467 */     fi.fileName = this.fileName;
/* 468 */     if (this.directory.indexOf("://") > 0) {
/* 469 */       URL u = new URL(this.directory + this.fileName);
/* 470 */       this.inputStream = new BufferedInputStream(u.openStream());
/* 471 */       fi.inputStream = this.inputStream;
/* 472 */     } else if (this.inputStream != null) {
/* 473 */       fi.inputStream = this.inputStream;
/*     */     } else {
/* 475 */       fi.directory = this.directory;
/* 476 */     }fi.width = 0;
/* 477 */     fi.height = 0;
/* 478 */     fi.offset = 0;
/* 479 */     fi.intelByteOrder = true;
/* 480 */     fi.fileType = 2;
/* 481 */     fi.fileFormat = 6;
/* 482 */     int samplesPerPixel = 1;
/* 483 */     int planarConfiguration = 0;
/* 484 */     String photoInterpretation = "";
/*     */ 
/* 486 */     if (this.inputStream != null)
/*     */     {
/* 488 */       this.f = this.inputStream;
/* 489 */       this.f.mark(400000);
/*     */     } else {
/* 491 */       this.f = new BufferedInputStream(new FileInputStream(this.directory + this.fileName));
/* 492 */     }if (IJ.debugMode) {
/* 493 */       IJ.log("");
/* 494 */       IJ.log("DicomDecoder: decoding " + this.fileName);
/*     */     }
/*     */ 
/* 497 */     long skipCount = 128L;
/* 498 */     while (skipCount > 0L) skipCount -= this.f.skip(skipCount);
/* 499 */     this.location += 128;
/*     */ 
/* 501 */     if (!getString(4).equals("DICM")) {
/* 502 */       if (this.inputStream == null) this.f.close();
/* 503 */       if (this.inputStream != null)
/* 504 */         this.f.reset();
/*     */       else
/* 506 */         this.f = new BufferedInputStream(new FileInputStream(this.directory + this.fileName));
/* 507 */       this.location = 0;
/* 508 */       if (IJ.debugMode) IJ.log("DICM not found at offset 128; reseting to offset 0"); 
/*     */     }
/* 510 */     else { this.dicmFound = true;
/* 511 */       if (IJ.debugMode) IJ.log("DICM found at offset 128");
/*     */     }
/*     */ 
/* 514 */     boolean decodingTags = true;
/* 515 */     boolean signed = false;
/*     */ 
/* 517 */     while (decodingTags) {
/* 518 */       int tag = getNextTag();
/* 519 */       if ((this.location & 0x1) != 0)
/* 520 */         this.oddLocations = true;
/* 521 */       if (this.inSequence) {
/* 522 */         addInfo(tag, null);
/*     */       }
/*     */       else
/*     */       {
/*     */         String s;
/*     */         int index;
/* 526 */         switch (tag) {
/*     */         case 131088:
/* 528 */           s = getString(this.elementLength);
/* 529 */           addInfo(tag, s);
/* 530 */           if ((s.indexOf("1.2.4") > -1) || (s.indexOf("1.2.5") > -1)) {
/* 531 */             this.f.close();
/* 532 */             String msg = "ImageJ cannot open compressed DICOM images.\n \n";
/* 533 */             msg = msg + "Transfer Syntax UID = " + s;
/* 534 */             throw new IOException(msg);
/*     */           }
/* 536 */           if (s.indexOf("1.2.840.10008.1.2.2") >= 0)
/* 537 */             this.bigEndianTransferSyntax = true; break;
/*     */         case 524384:
/* 540 */           this.modality = getString(this.elementLength);
/* 541 */           addInfo(tag, this.modality);
/* 542 */           break;
/*     */         case 2621448:
/* 544 */           s = getString(this.elementLength);
/* 545 */           addInfo(tag, s);
/* 546 */           double frames = s2d(s);
/* 547 */           if (frames > 1.0D)
/* 548 */             fi.nImages = ((int)frames); break;
/*     */         case 2621442:
/* 551 */           samplesPerPixel = getShort();
/* 552 */           addInfo(tag, samplesPerPixel);
/* 553 */           break;
/*     */         case 2621444:
/* 555 */           photoInterpretation = getString(this.elementLength);
/* 556 */           addInfo(tag, photoInterpretation);
/* 557 */           break;
/*     */         case 2621446:
/* 559 */           planarConfiguration = getShort();
/* 560 */           addInfo(tag, planarConfiguration);
/* 561 */           break;
/*     */         case 2621456:
/* 563 */           fi.height = getShort();
/* 564 */           addInfo(tag, fi.height);
/* 565 */           break;
/*     */         case 2621457:
/* 567 */           fi.width = getShort();
/* 568 */           addInfo(tag, fi.width);
/* 569 */           break;
/*     */         case 1577316:
/*     */         case 2621488:
/* 571 */           String scale = getString(this.elementLength);
/* 572 */           getSpatialScale(fi, scale);
/* 573 */           addInfo(tag, scale);
/* 574 */           break;
/*     */         case 1572944:
/*     */         case 1573000:
/* 576 */           String spacing = getString(this.elementLength);
/* 577 */           fi.pixelDepth = s2d(spacing);
/* 578 */           addInfo(tag, spacing);
/* 579 */           break;
/*     */         case 2621696:
/* 581 */           bitsAllocated = getShort();
/* 582 */           if (bitsAllocated == 8)
/* 583 */             fi.fileType = 0;
/* 584 */           else if (bitsAllocated == 32)
/* 585 */             fi.fileType = 11;
/* 586 */           addInfo(tag, bitsAllocated);
/* 587 */           break;
/*     */         case 2621699:
/* 589 */           int pixelRepresentation = getShort();
/* 590 */           if (pixelRepresentation == 1) {
/* 591 */             fi.fileType = 1;
/* 592 */             signed = true;
/*     */           }
/* 594 */           addInfo(tag, pixelRepresentation);
/* 595 */           break;
/*     */         case 2625616:
/* 597 */           String center = getString(this.elementLength);
/* 598 */           index = center.indexOf('\\');
/* 599 */           if (index != -1) center = center.substring(index + 1);
/* 600 */           this.windowCenter = s2d(center);
/* 601 */           addInfo(tag, center);
/* 602 */           break;
/*     */         case 2625617:
/* 604 */           String width = getString(this.elementLength);
/* 605 */           index = width.indexOf('\\');
/* 606 */           if (index != -1) width = width.substring(index + 1);
/* 607 */           this.windowWidth = s2d(width);
/* 608 */           addInfo(tag, width);
/* 609 */           break;
/*     */         case 2625618:
/* 611 */           String intercept = getString(this.elementLength);
/* 612 */           this.rescaleIntercept = s2d(intercept);
/* 613 */           addInfo(tag, intercept);
/* 614 */           break;
/*     */         case 2625619:
/* 616 */           String slop = getString(this.elementLength);
/* 617 */           this.rescaleSlope = s2d(slop);
/* 618 */           addInfo(tag, slop);
/* 619 */           break;
/*     */         case 2626049:
/* 621 */           fi.reds = getLut(this.elementLength);
/* 622 */           addInfo(tag, this.elementLength / 2);
/* 623 */           break;
/*     */         case 2626050:
/* 625 */           fi.greens = getLut(this.elementLength);
/* 626 */           addInfo(tag, this.elementLength / 2);
/* 627 */           break;
/*     */         case 2626051:
/* 629 */           fi.blues = getLut(this.elementLength);
/* 630 */           addInfo(tag, this.elementLength / 2);
/* 631 */           break;
/*     */         case 2145386512:
/* 634 */           if (this.elementLength != 0) {
/* 635 */             fi.offset = this.location;
/* 636 */             addInfo(tag, this.location);
/* 637 */             decodingTags = false;
/*     */           } else {
/* 639 */             addInfo(tag, null);
/* 640 */           }break;
/*     */         case 2139619344:
/* 643 */           if (this.elementLength != 0) {
/* 644 */             fi.offset = (this.location + 4);
/* 645 */             decodingTags = false; } break;
/*     */         default:
/* 650 */           addInfo(tag, null);
/*     */         }
/*     */       }
/*     */     }
/* 654 */     if ((fi.fileType == 0) && 
/* 655 */       (fi.reds != null) && (fi.greens != null) && (fi.blues != null) && (fi.reds.length == fi.greens.length) && (fi.reds.length == fi.blues.length))
/*     */     {
/* 658 */       fi.fileType = 5;
/* 659 */       fi.lutSize = fi.reds.length;
/*     */     }
/*     */ 
/* 664 */     if ((fi.fileType == 11) && (signed)) {
/* 665 */       fi.fileType = 3;
/*     */     }
/* 667 */     if ((samplesPerPixel == 3) && (photoInterpretation.startsWith("RGB"))) {
/* 668 */       if (planarConfiguration == 0)
/* 669 */         fi.fileType = 6;
/* 670 */       else if (planarConfiguration == 1)
/* 671 */         fi.fileType = 7;
/* 672 */     } else if (photoInterpretation.endsWith("1 ")) {
/* 673 */       fi.whiteIsZero = true;
/*     */     }
/* 675 */     if (!this.littleEndian) {
/* 676 */       fi.intelByteOrder = false;
/*     */     }
/* 678 */     if (IJ.debugMode) {
/* 679 */       IJ.log("width: " + fi.width);
/* 680 */       IJ.log("height: " + fi.height);
/* 681 */       IJ.log("images: " + fi.nImages);
/* 682 */       IJ.log("bits allocated: " + bitsAllocated);
/* 683 */       IJ.log("offset: " + fi.offset);
/*     */     }
/*     */ 
/* 686 */     if (this.inputStream != null)
/* 687 */       this.f.reset();
/*     */     else
/* 689 */       this.f.close();
/* 690 */     return fi;
/*     */   }
/*     */ 
/*     */   String getDicomInfo() {
/* 694 */     String s = new String(this.dicomInfo);
/* 695 */     char[] chars = new char[s.length()];
/* 696 */     s.getChars(0, s.length(), chars, 0);
/* 697 */     for (int i = 0; i < chars.length; i++) {
/* 698 */       if ((chars[i] < ' ') && (chars[i] != '\n')) chars[i] = ' ';
/*     */     }
/* 700 */     return new String(chars);
/*     */   }
/*     */ 
/*     */   void addInfo(int tag, String value) throws IOException {
/* 704 */     String info = getHeaderInfo(tag, value);
/* 705 */     if ((this.inSequence) && (info != null) && (this.vr != 21329)) info = ">" + info;
/* 706 */     if ((info != null) && (tag != -73728)) {
/* 707 */       int group = tag >>> 16;
/*     */ 
/* 710 */       this.previousGroup = group;
/* 711 */       this.previousInfo = info;
/* 712 */       this.dicomInfo.append(tag2hex(tag) + info + "\n");
/*     */     }
/* 714 */     if (IJ.debugMode) {
/* 715 */       if (info == null) info = "";
/* 716 */       this.vrLetters[0] = ((byte)(this.vr >> 8));
/* 717 */       this.vrLetters[1] = ((byte)(this.vr & 0xFF));
/* 718 */       String VR = new String(this.vrLetters);
/* 719 */       IJ.log("(" + tag2hex(tag) + VR + " " + this.elementLength + " bytes from " + (this.location - this.elementLength) + ") " + info);
/*     */     }
/*     */   }
/*     */ 
/*     */   void addInfo(int tag, int value)
/*     */     throws IOException
/*     */   {
/* 728 */     addInfo(tag, Integer.toString(value));
/*     */   }
/*     */ 
/*     */   String getHeaderInfo(int tag, String value) throws IOException {
/* 732 */     if ((tag == -73715) || (tag == -73507)) {
/* 733 */       this.inSequence = false;
/* 734 */       if (!IJ.debugMode) return null;
/*     */     }
/* 736 */     String key = i2hex(tag);
/*     */ 
/* 739 */     String id = (String)dictionary.get(key);
/* 740 */     if (id != null) {
/* 741 */       if ((this.vr == 11565) && (id != null))
/* 742 */         this.vr = ((id.charAt(0) << '\b') + id.charAt(1));
/* 743 */       id = id.substring(2);
/*     */     }
/* 745 */     if (tag == -73728)
/* 746 */       return id != null ? id + ":" : null;
/* 747 */     if (value != null)
/* 748 */       return id + ": " + value;
/* 749 */     switch (this.vr) {
/*     */     case 17988:
/* 751 */       if (this.elementLength == 8)
/* 752 */         value = Double.toString(getDouble());
/*     */       else
/* 754 */         for (int i = 0; i < this.elementLength; i++) getByte();
/* 755 */       break;
/*     */     case 17996:
/* 757 */       if (this.elementLength == 4)
/* 758 */         value = Float.toString(getFloat());
/*     */       else
/* 760 */         for (int i = 0; i < this.elementLength; i++) getByte();
/* 761 */       break;
/*     */     case 16709:
/*     */     case 16723:
/*     */     case 16724:
/*     */     case 17235:
/*     */     case 17473:
/*     */     case 17491:
/*     */     case 17492:
/*     */     case 18771:
/*     */     case 19535:
/*     */     case 19540:
/*     */     case 20558:
/*     */     case 21320:
/*     */     case 21332:
/*     */     case 21581:
/*     */     case 21833:
/* 766 */       value = getString(this.elementLength);
/* 767 */       break;
/*     */     case 21843:
/* 769 */       if (this.elementLength == 2) {
/* 770 */         value = Integer.toString(getShort());
/*     */       } else {
/* 772 */         value = "";
/* 773 */         int n = this.elementLength / 2;
/* 774 */         for (int i = 0; i < n; i++)
/* 775 */           value = value + Integer.toString(getShort()) + " ";
/*     */       }
/* 777 */       break;
/*     */     case 11565:
/* 779 */       value = getString(this.elementLength);
/* 780 */       if (this.elementLength > 44) value = null; break;
/*     */     case 21329:
/* 783 */       value = "";
/* 784 */       boolean privateTag = (tag >> 16 & 0x1) != 0;
/* 785 */       if ((tag != 8913408) && (!privateTag)) {
/*     */         break;
/*     */       }
/*     */     default:
/* 789 */       long skipCount = this.elementLength;
/* 790 */       while (skipCount > 0L) skipCount -= this.f.skip(skipCount);
/* 791 */       this.location += this.elementLength;
/* 792 */       value = "";
/*     */     }
/* 794 */     if ((value != null) && (id == null) && (!value.equals("")))
/* 795 */       return "---: " + value;
/* 796 */     if (id == null) {
/* 797 */       return null;
/*     */     }
/* 799 */     return id + ": " + value;
/*     */   }
/*     */ 
/*     */   String i2hex(int i)
/*     */   {
/* 806 */     for (int pos = 7; pos >= 0; pos--) {
/* 807 */       buf8[pos] = ij.util.Tools.hexDigits[(i & 0xF)];
/* 808 */       i >>>= 4;
/*     */     }
/* 810 */     return new String(buf8);
/*     */   }
/*     */ 
/*     */   String tag2hex(int tag)
/*     */   {
/* 816 */     if (this.buf10 == null) {
/* 817 */       this.buf10 = new char[11];
/* 818 */       this.buf10[4] = ',';
/* 819 */       this.buf10[9] = ' ';
/*     */     }
/* 821 */     int pos = 8;
/* 822 */     while (pos >= 0) {
/* 823 */       this.buf10[pos] = ij.util.Tools.hexDigits[(tag & 0xF)];
/* 824 */       tag >>>= 4;
/* 825 */       pos--;
/* 826 */       if (pos == 4) pos--;
/*     */     }
/* 828 */     return new String(this.buf10);
/*     */   }
/*     */ 
/*     */   double s2d(String s) {
/* 832 */     if (s == null) return 0.0D;
/* 833 */     if (s.startsWith("\\"))
/* 834 */       s = s.substring(1); Double d;
/*     */     try {
/* 836 */       d = new Double(s); } catch (NumberFormatException e) {
/* 837 */       d = null;
/* 838 */     }if (d != null) {
/* 839 */       return d.doubleValue();
/*     */     }
/* 841 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   void getSpatialScale(FileInfo fi, String scale) {
/* 845 */     double xscale = 0.0D; double yscale = 0.0D;
/* 846 */     int i = scale.indexOf('\\');
/* 847 */     if (i > 0) {
/* 848 */       yscale = s2d(scale.substring(0, i));
/* 849 */       xscale = s2d(scale.substring(i + 1));
/*     */     }
/* 851 */     if ((xscale != 0.0D) && (yscale != 0.0D)) {
/* 852 */       fi.pixelWidth = xscale;
/* 853 */       fi.pixelHeight = yscale;
/* 854 */       fi.unit = "mm";
/*     */     }
/*     */   }
/*     */ 
/*     */   boolean dicmFound() {
/* 859 */     return this.dicmFound;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.DicomDecoder
 * JD-Core Version:    0.6.2
 */