/*     */ package uk.co.mmscomputing.device.twain;
/*     */ 
/*     */ import java.awt.geom.Rectangle2D.Double;
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public abstract class TwainContainer
/*     */   implements TwainConstants
/*     */ {
/*     */   protected int cap;
/*     */   protected int type;
/* 228 */   static String[] hexs = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
/*     */ 
/*     */   TwainContainer(int paramInt, byte[] paramArrayOfByte)
/*     */   {
/*  11 */     this.cap = paramInt;
/*  12 */     this.type = jtwain.getINT16(paramArrayOfByte, 0);
/*     */   }
/*     */ 
/*     */   TwainContainer(int paramInt1, int paramInt2) {
/*  16 */     this.cap = paramInt1;
/*  17 */     this.type = paramInt2;
/*     */   }
/*     */   public int getCapabilityId() {
/*  20 */     return this.cap; } 
/*     */   abstract int getType();
/*     */ 
/*     */   abstract byte[] getBytes();
/*     */ 
/*  25 */   public int getItemType() { return this.type; }
/*     */ 
/*     */   public abstract Object[] getItems();
/*     */ 
/*     */   private boolean booleanValue(Object paramObject) throws TwainIOException {
/*  30 */     if ((paramObject instanceof Number))
/*  31 */       return ((Number)paramObject).intValue() != 0;
/*  32 */     if ((paramObject instanceof Boolean))
/*  33 */       return ((Boolean)paramObject).booleanValue();
/*  34 */     if ((paramObject instanceof String)) {
/*  35 */       return Boolean.valueOf((String)paramObject).booleanValue();
/*     */     }
/*  37 */     throw new TwainIOException(getClass().getName() + ".booleanValue:\n\tUnsupported data type: " + paramObject.getClass().getName());
/*     */   }
/*     */ 
/*     */   private int intValue(Object paramObject) throws TwainIOException {
/*  41 */     if ((paramObject instanceof Number))
/*  42 */       return ((Number)paramObject).intValue();
/*  43 */     if ((paramObject instanceof Boolean))
/*  44 */       return ((Boolean)paramObject).booleanValue() ? 1 : 0;
/*  45 */     if ((paramObject instanceof String)) {
/*  46 */       String str = (String)paramObject;
/*     */       try {
/*  48 */         return Integer.parseInt(str);
/*     */       } catch (Exception localException) {
/*  50 */         throw new TwainIOException(getClass().getName() + ".intValue:\n\tCannot convert string [\"" + str + "\"] to int.");
/*     */       }
/*     */     }
/*  53 */     throw new TwainIOException(getClass().getName() + ".intValue:\n\tUnsupported data type: " + paramObject.getClass().getName());
/*     */   }
/*     */ 
/*     */   private double doubleValue(Object paramObject) throws TwainIOException {
/*  57 */     if ((paramObject instanceof Number))
/*  58 */       return ((Number)paramObject).doubleValue();
/*  59 */     if ((paramObject instanceof Boolean))
/*  60 */       return ((Boolean)paramObject).booleanValue() ? 1 : 0;
/*  61 */     if ((paramObject instanceof String)) {
/*  62 */       String str = (String)paramObject;
/*     */       try {
/*  64 */         return Double.parseDouble(str);
/*     */       } catch (Exception localException) {
/*  66 */         throw new TwainIOException(getClass().getName() + ".doubleValue:\n\tCannot convert string [\"" + str + "\"] to double.");
/*     */       }
/*     */     }
/*  69 */     throw new TwainIOException(getClass().getName() + ".doubleValue:\n\tUnsupported data type: " + paramObject.getClass().getName());
/*     */   }
/*     */ 
/*     */   public abstract Object getCurrentValue() throws TwainIOException;
/*     */ 
/*     */   public boolean booleanValue() throws TwainIOException
/*     */   {
/*  76 */     return booleanValue(getCurrentValue()); } 
/*  77 */   public int intValue() throws TwainIOException { return intValue(getCurrentValue()); } 
/*  78 */   public double doubleValue() throws TwainIOException { return doubleValue(getCurrentValue()); } 
/*     */   public abstract void setCurrentValue(Object paramObject) throws TwainIOException;
/*     */ 
/*     */   public void setCurrentValue(boolean paramBoolean) throws TwainIOException {
/*  82 */     setCurrentValue(new Boolean(paramBoolean)); } 
/*  83 */   public void setCurrentValue(int paramInt) throws TwainIOException { setCurrentValue(new Integer(paramInt)); } 
/*  84 */   public void setCurrentValue(double paramDouble) throws TwainIOException { setCurrentValue(new Double(paramDouble)); }
/*     */ 
/*     */   public abstract Object getDefaultValue() throws TwainIOException;
/*     */ 
/*     */   public boolean booleanDefaultValue() throws TwainIOException
/*     */   {
/*  90 */     return booleanValue(getDefaultValue()); } 
/*  91 */   public int intDefaultValue() throws TwainIOException { return intValue(getDefaultValue()); } 
/*  92 */   public double doubleDefaultValue() throws TwainIOException { return doubleValue(getDefaultValue()); } 
/*     */   public abstract void setDefaultValue(Object paramObject) throws TwainIOException;
/*     */ 
/*     */   public void setDefaultValue(boolean paramBoolean) throws TwainIOException {
/*  96 */     setDefaultValue(new Boolean(paramBoolean)); } 
/*  97 */   public void setDefaultValue(int paramInt) throws TwainIOException { setDefaultValue(new Integer(paramInt)); } 
/*  98 */   public void setDefaultValue(double paramDouble) throws TwainIOException { setDefaultValue(new Double(paramDouble)); }
/*     */ 
/*     */ 
/*     */   protected Object get32BitObjectAt(byte[] paramArrayOfByte, int paramInt)
/*     */   {
/* 103 */     switch (this.type) { case 0:
/*     */     case 1:
/*     */     case 2:
/* 106 */       return new Integer(jtwain.getINT32(paramArrayOfByte, paramInt));
/*     */     case 3:
/* 107 */       return new Integer(jtwain.getINT32(paramArrayOfByte, paramInt) & 0xFF);
/*     */     case 4:
/* 108 */       return new Integer(jtwain.getINT32(paramArrayOfByte, paramInt) & 0xFFFF);
/*     */     case 5:
/* 109 */       return new Long(jtwain.getINT32(paramArrayOfByte, paramInt) & 0xFFFFFFFF);
/*     */     case 6:
/* 110 */       return new Boolean(jtwain.getINT32(paramArrayOfByte, paramInt) != 0);
/*     */     case 7:
/* 111 */       return new Double(jtwain.getFIX32(paramArrayOfByte, paramInt));
/*     */     case 8:
/*     */     case 9:
/*     */     case 10:
/*     */     case 11:
/*     */     case 12:
/*     */     case 13:
/*     */     case 14:
/* 122 */       return new Integer(jtwain.getINT32(paramArrayOfByte, paramInt));
/*     */     }
/*     */ 
/* 125 */     new TwainIOException(getClass().getName() + ".get32BitObjectAt:\n\tUnsupported type = " + this.type).printStackTrace();
/*     */ 
/* 127 */     return null;
/*     */   }
/*     */ 
/*     */   protected void set32BitObjectAt(byte[] paramArrayOfByte, int paramInt, Object paramObject) {
/* 131 */     int i = 0;
/* 132 */     if ((paramObject instanceof Integer)) {
/* 133 */       int j = ((Integer)paramObject).intValue();
/* 134 */       if (this.type == 7)
/* 135 */         jtwain.setFIX32(paramArrayOfByte, paramInt, j);
/* 136 */       else if (this.type == 6)
/* 137 */         jtwain.setINT32(paramArrayOfByte, paramInt, j == 0 ? 0 : 1);
/*     */       else
/* 139 */         jtwain.setINT32(paramArrayOfByte, paramInt, j);
/*     */     }
/* 141 */     else if ((paramObject instanceof Double)) {
/* 142 */       double d = ((Double)paramObject).doubleValue();
/* 143 */       if (this.type == 7)
/* 144 */         jtwain.setFIX32(paramArrayOfByte, paramInt, d);
/* 145 */       else if (this.type == 6)
/* 146 */         jtwain.setINT32(paramArrayOfByte, paramInt, d == 0.0D ? 0 : 1);
/*     */       else
/* 148 */         jtwain.setINT32(paramArrayOfByte, paramInt, (int)d);
/*     */     }
/* 150 */     else if ((paramObject instanceof Boolean)) {
/* 151 */       int k = ((Boolean)paramObject).booleanValue() ? 1 : 0;
/* 152 */       if (this.type == 7)
/* 153 */         jtwain.setFIX32(paramArrayOfByte, paramInt, k);
/*     */       else
/* 155 */         jtwain.setINT32(paramArrayOfByte, paramInt, k);
/*     */     }
/* 157 */     else if ((paramObject instanceof String)) {
/* 158 */       if (this.type == 7)
/* 159 */         set32BitObjectAt(paramArrayOfByte, paramInt, new Double((String)paramObject));
/*     */       else
/* 161 */         set32BitObjectAt(paramArrayOfByte, paramInt, new Integer((String)paramObject));
/*     */     }
/*     */     else {
/* 164 */       System.out.println(getClass().getName() + ".set32BitObjectAt:\n\tUnsupported type = " + this.type);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Object getObjectAt(byte[] paramArrayOfByte, int paramInt) {
/* 169 */     switch (this.type) { case 0:
/* 170 */       return new Integer(paramArrayOfByte[paramInt]);
/*     */     case 1:
/* 171 */       return new Integer(jtwain.getINT16(paramArrayOfByte, paramInt));
/*     */     case 2:
/* 172 */       return new Integer(jtwain.getINT32(paramArrayOfByte, paramInt));
/*     */     case 3:
/* 173 */       return new Integer(paramArrayOfByte[paramInt] & 0xFF);
/*     */     case 4:
/* 174 */       return new Integer(jtwain.getINT16(paramArrayOfByte, paramInt) & 0xFFFF);
/*     */     case 5:
/* 175 */       return new Long(jtwain.getINT32(paramArrayOfByte, paramInt) & 0xFFFFFFFF);
/*     */     case 6:
/* 176 */       return new Boolean(jtwain.getINT16(paramArrayOfByte, paramInt) != 0);
/*     */     case 7:
/* 177 */       return new Double(jtwain.getFIX32(paramArrayOfByte, paramInt));
/*     */     case 8:
/* 179 */       double d1 = jtwain.getFIX32(paramArrayOfByte, paramInt);
/* 180 */       double d2 = jtwain.getFIX32(paramArrayOfByte, paramInt + 4);
/* 181 */       double d3 = jtwain.getFIX32(paramArrayOfByte, paramInt + 8) - d1;
/* 182 */       double d4 = jtwain.getFIX32(paramArrayOfByte, paramInt + 12) - d2;
/* 183 */       return new Rectangle2D.Double(d1, d2, d3, d4);
/*     */     case 9:
/*     */     case 10:
/*     */     case 11:
/*     */     case 12:
/* 188 */       String str = "";
/* 189 */       for (int i = 0; (paramArrayOfByte[(paramInt + i)] != 0) && (i < TwainConstants.typeSizes[this.type]); i++) {
/* 190 */         str = str + (char)paramArrayOfByte[(paramInt + i)];
/*     */       }
/* 192 */       return str; }
/* 193 */     System.out.println(getClass().getName() + ".getObjectAt:\n\tUnsupported type = " + this.type);
/*     */ 
/* 195 */     return null;
/*     */   }
/*     */ 
/*     */   private void set16BitObjectAt(byte[] paramArrayOfByte, int paramInt, Object paramObject)
/*     */   {
/*     */     int i;
/* 199 */     if ((paramObject instanceof Number)) {
/* 200 */       i = ((Number)paramObject).intValue();
/* 201 */       jtwain.setINT16(paramArrayOfByte, paramInt, i);
/* 202 */     } else if ((paramObject instanceof Boolean)) {
/* 203 */       i = ((Boolean)paramObject).booleanValue() ? 1 : 0;
/* 204 */       jtwain.setINT16(paramArrayOfByte, paramInt, i);
/*     */     } else {
/* 206 */       System.out.println("3\b" + getClass().getName() + ".set16BitObjectAt:\n\tUnsupported type = " + paramObject.getClass().getName());
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void setObjectAt(byte[] paramArrayOfByte, int paramInt, Object paramObject) {
/* 211 */     switch (this.type) { case 1:
/*     */     case 4:
/* 213 */       set16BitObjectAt(paramArrayOfByte, paramInt, paramObject); break;
/*     */     case 2:
/*     */     case 5:
/*     */     case 7:
/* 216 */       set32BitObjectAt(paramArrayOfByte, paramInt, paramObject); break;
/*     */     case 3:
/*     */     case 6:
/*     */     default:
/* 217 */       System.out.println("3\b" + getClass().getName() + ".setObjectAt:\n\tUnsupported type = " + this.type); }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 222 */     String str = getClass().getName() + "\n";
/* 223 */     str = str + "cap          = 0x" + Integer.toHexString(this.cap) + "\n";
/* 224 */     str = str + "type         = 0x" + Integer.toHexString(this.type) + "\n";
/* 225 */     return str;
/*     */   }
/*     */ 
/*     */   public static String toString(byte[] paramArrayOfByte)
/*     */   {
/* 232 */     String str = "\n";
/* 233 */     int i = 0;
/* 234 */     while (i < paramArrayOfByte.length) {
/* 235 */       str = str + " ";
/* 236 */       str = str + hexs[(paramArrayOfByte[i] >> 4 & 0xF)];
/* 237 */       str = str + hexs[(paramArrayOfByte[i] & 0xF)];
/* 238 */       if ((i + 1) % 8 == 0) str = str + "\n";
/* 239 */       i++;
/*     */     }
/* 241 */     if ((i + 1) % 8 != 0) str = str + "\n";
/* 242 */     return str;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.device.twain.TwainContainer
 * JD-Core Version:    0.6.2
 */