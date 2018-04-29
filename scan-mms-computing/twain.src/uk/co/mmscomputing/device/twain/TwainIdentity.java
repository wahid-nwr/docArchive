/*     */ package uk.co.mmscomputing.device.twain;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class TwainIdentity
/*     */   implements TwainConstants
/*     */ {
/*     */   private TwainSourceManager manager;
/*     */   protected byte[] identity;
/*     */ 
/*     */   TwainIdentity(TwainSourceManager paramTwainSourceManager)
/*     */   {
/*  35 */     this.manager = paramTwainSourceManager;
/*  36 */     this.identity = new byte[''];
/*     */   }
/*     */ 
/*     */   TwainIdentity(TwainSourceManager paramTwainSourceManager, byte[] paramArrayOfByte) {
/*  40 */     this.manager = paramTwainSourceManager;
/*  41 */     this.identity = paramArrayOfByte;
/*     */   }
/*     */ 
/*     */   boolean isTwain20Source() {
/*  45 */     int i = jtwain.getINT32(this.identity, 50);
/*  46 */     return (i & 0x40000000) == 1073741824;
/*     */   }
/*     */ 
/*     */   void maskTwain20Source() {
/*  50 */     int i = jtwain.getINT32(this.identity, 50);
/*  51 */     jtwain.setINT32(this.identity, 50, i & 0xBFFFFFFF);
/*     */   }
/*     */ 
/*     */   void getDefault()
/*     */   {
/*     */     try
/*     */     {
/*  59 */       this.manager.call(1, 3, 3, this.identity);
/*     */     }
/*     */     catch (TwainIOException localTwainIOException) {
/*     */     }
/*     */   }
/*     */ 
/*     */   void userSelect() throws TwainIOException {
/*  66 */     this.manager.call(1, 3, 1027, this.identity);
/*     */   }
/*     */ 
/*     */   void open() throws TwainIOException {
/*  70 */     this.manager.call(1, 3, 1025, this.identity);
/*     */   }
/*     */ 
/*     */   void getFirst() throws TwainIOException {
/*  74 */     this.manager.call(1, 3, 4, this.identity);
/*     */   }
/*     */ 
/*     */   void getNext() throws TwainIOException {
/*  78 */     this.manager.call(1, 3, 5, this.identity);
/*     */   }
/*     */   public int getId() {
/*  81 */     return jtwain.getINT32(this.identity, 0); } 
/*  82 */   public int getMajorNum() { return jtwain.getINT16(this.identity, 4); } 
/*  83 */   public int getMinorNum() { return jtwain.getINT16(this.identity, 6); } 
/*  84 */   public int getLanguage() { return jtwain.getINT16(this.identity, 8); } 
/*  85 */   public int getCountry() { return jtwain.getINT16(this.identity, 10); }
/*     */ 
/*     */   public String getInfo() {
/*  88 */     String str = "";
/*  89 */     for (int i = 12; (this.identity[i] != 0) && (i < 46); i++) {
/*  90 */       str = str + (char)this.identity[i];
/*     */     }
/*  92 */     return str;
/*     */   }
/*     */   public int getProtocolMajor() {
/*  95 */     return jtwain.getINT16(this.identity, 46); } 
/*  96 */   public int getProtocolMinor() { return jtwain.getINT16(this.identity, 48); } 
/*  97 */   public int getSupportedGroups() { return jtwain.getINT32(this.identity, 50); }
/*     */ 
/*     */   public String getManufacturer() {
/* 100 */     String str = "";
/* 101 */     for (int i = 54; (this.identity[i] != 0) && (i < 88); i++) {
/* 102 */       str = str + (char)this.identity[i];
/*     */     }
/* 104 */     return str;
/*     */   }
/*     */ 
/*     */   public String getProductFamily() {
/* 108 */     String str = "";
/* 109 */     for (int i = 88; (this.identity[i] != 0) && (i < 122); i++) {
/* 110 */       str = str + (char)this.identity[i];
/*     */     }
/* 112 */     return str;
/*     */   }
/*     */ 
/*     */   public String getProductName() {
/* 116 */     String str = "";
/* 117 */     for (int i = 122; (this.identity[i] != 0) && (i < 156); i++) {
/* 118 */       str = str + (char)this.identity[i];
/*     */     }
/* 120 */     return str;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 124 */     String str = "TwainIdentity\n";
/* 125 */     str = str + "\tid               = 0x" + Integer.toHexString(getId()) + "\n";
/* 126 */     str = str + "\tmajorNum         = 0x" + Integer.toHexString(getMajorNum()) + "\n";
/* 127 */     str = str + "\tminorNum         = 0x" + Integer.toHexString(getMinorNum()) + "\n";
/* 128 */     str = str + "\tlanguage         = 0x" + Integer.toHexString(getLanguage()) + "\n";
/* 129 */     str = str + "\tcountry          = 0x" + Integer.toHexString(getCountry()) + "\n";
/* 130 */     str = str + "\tinfo             = " + getInfo() + "\n";
/* 131 */     str = str + "\tprotocol major   = 0x" + Integer.toHexString(getProtocolMajor()) + "\n";
/* 132 */     str = str + "\tprotocol minor   = 0x" + Integer.toHexString(getProtocolMinor()) + "\n";
/* 133 */     str = str + "\tsupported groups = 0x" + Integer.toHexString(getSupportedGroups()) + "\n";
/* 134 */     str = str + "\tmanufacturer     = " + getManufacturer() + "\n";
/* 135 */     str = str + "\tproduct family   = " + getProductFamily() + "\n";
/* 136 */     str = str + "\tproduct name     = " + getProductName() + "\n";
/*     */ 
/* 138 */     str = str + "\ttwain 2.0 source = " + isTwain20Source() + "\n";
/* 139 */     return str;
/*     */   }
/*     */ 
/*     */   public static TwainIdentity[] getIdentities() throws TwainIOException {
/* 143 */     TwainSourceManager localTwainSourceManager = jtwain.getSourceManager();
/* 144 */     Vector localVector = new Vector();
/*     */     try {
/* 146 */       TwainIdentity localTwainIdentity = new TwainIdentity(localTwainSourceManager);
/* 147 */       localTwainIdentity.getFirst();
/* 148 */       localVector.add(localTwainIdentity);
/*     */       while (true) {
/* 150 */         localTwainIdentity = new TwainIdentity(localTwainSourceManager);
/* 151 */         localTwainIdentity.getNext();
/* 152 */         localVector.add(localTwainIdentity);
/*     */       }
/*     */     } catch (TwainResultException.EndOfList localEndOfList) {
/*     */     } catch (TwainIOException localTwainIOException) {
/* 156 */       System.out.println("uk.co.mmscomputing.device.twain.TwainIdentity.getIdentities:\n\t" + localTwainIOException);
/*     */     }
/* 158 */     return (TwainIdentity[])localVector.toArray(new TwainIdentity[0]);
/*     */   }
/*     */ 
/*     */   public static String[] getProductNames() throws TwainIOException {
/* 162 */     TwainSourceManager localTwainSourceManager = jtwain.getSourceManager();
/* 163 */     Vector localVector = new Vector();
/*     */     try {
/* 165 */       TwainIdentity localTwainIdentity = new TwainIdentity(localTwainSourceManager);
/* 166 */       localTwainIdentity.getFirst();
/* 167 */       localVector.add(localTwainIdentity.getProductName());
/*     */       while (true) {
/* 169 */         localTwainIdentity = new TwainIdentity(localTwainSourceManager);
/* 170 */         localTwainIdentity.getNext();
/* 171 */         localVector.add(localTwainIdentity.getProductName());
/*     */       }
/*     */     } catch (TwainResultException.EndOfList localEndOfList) {
/*     */     } catch (TwainIOException localTwainIOException) {
/* 175 */       System.out.println("uk.co.mmscomputing.device.twain.TwainIdentity.getProductNames:\n\t" + localTwainIOException);
/*     */     }
/* 177 */     return (String[])localVector.toArray(new String[0]);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.twain.TwainIdentity
 * JD-Core Version:    0.6.2
 */