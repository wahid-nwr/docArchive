/*     */ package ij.macro;
/*     */ 
/*     */ import ij.IJ;
/*     */ 
/*     */ class Variable
/*     */   implements MacroConstants, Cloneable
/*     */ {
/*     */   static final int VALUE = 0;
/*     */   static final int ARRAY = 1;
/*     */   static final int STRING = 2;
/*     */   int symTabIndex;
/*     */   private double value;
/*     */   private String str;
/*     */   private Variable[] array;
/*     */   private int arraySize;
/*     */ 
/*     */   Variable()
/*     */   {
/*     */   }
/*     */ 
/*     */   Variable(double value)
/*     */   {
/*  15 */     this.value = value;
/*     */   }
/*     */ 
/*     */   Variable(int symTabIndex, double value, String str) {
/*  19 */     this.symTabIndex = symTabIndex;
/*  20 */     this.value = value;
/*  21 */     this.str = str;
/*     */   }
/*     */ 
/*     */   Variable(int symTabIndex, double value, String str, Variable[] array) {
/*  25 */     this.symTabIndex = symTabIndex;
/*  26 */     this.value = value;
/*  27 */     this.str = str;
/*  28 */     this.array = array;
/*     */   }
/*     */ 
/*     */   Variable(byte[] array) {
/*  32 */     this.array = new Variable[array.length];
/*  33 */     for (int i = 0; i < array.length; i++)
/*  34 */       this.array[i] = new Variable(array[i] & 0xFF);
/*     */   }
/*     */ 
/*     */   Variable(int[] array) {
/*  38 */     this.array = new Variable[array.length];
/*  39 */     for (int i = 0; i < array.length; i++)
/*  40 */       this.array[i] = new Variable(array[i]);
/*     */   }
/*     */ 
/*     */   Variable(double[] array) {
/*  44 */     this.array = new Variable[array.length];
/*  45 */     for (int i = 0; i < array.length; i++)
/*  46 */       this.array[i] = new Variable(array[i]);
/*     */   }
/*     */ 
/*     */   double getValue() {
/*  50 */     if (this.str != null) {
/*  51 */       return convertToDouble();
/*     */     }
/*  53 */     return this.value;
/*     */   }
/*     */ 
/*     */   double convertToDouble() {
/*     */     try {
/*  58 */       Double d = new Double(this.str);
/*  59 */       return d.doubleValue(); } catch (NumberFormatException e) {
/*     */     }
/*  61 */     return (0.0D / 0.0D);
/*     */   }
/*     */ 
/*     */   void setValue(double value)
/*     */   {
/*  66 */     this.value = value;
/*  67 */     this.str = null;
/*  68 */     this.array = null;
/*     */   }
/*     */ 
/*     */   String getString() {
/*  72 */     return this.str;
/*     */   }
/*     */ 
/*     */   void setString(String str) {
/*  76 */     this.str = str;
/*  77 */     this.value = 0.0D;
/*  78 */     this.array = null;
/*     */   }
/*     */ 
/*     */   Variable[] getArray() {
/*  82 */     return this.array;
/*     */   }
/*     */ 
/*     */   void setArray(Variable[] array) {
/*  86 */     this.array = array;
/*  87 */     this.value = 0.0D;
/*  88 */     this.str = null;
/*  89 */     this.arraySize = 0;
/*     */   }
/*     */ 
/*     */   void setArraySize(int size) {
/*  93 */     if (this.array == null)
/*  94 */       size = 0;
/*  95 */     else if (size > this.array.length)
/*  96 */       size = this.array.length;
/*  97 */     this.arraySize = size;
/*     */   }
/*     */ 
/*     */   int getArraySize() {
/* 101 */     int size = this.array != null ? this.array.length : 0;
/* 102 */     if (this.arraySize > 0) size = this.arraySize;
/* 103 */     return size;
/*     */   }
/*     */ 
/*     */   int getType() {
/* 107 */     if (this.array != null)
/* 108 */       return 1;
/* 109 */     if (this.str != null) {
/* 110 */       return 2;
/*     */     }
/* 112 */     return 0;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 116 */     String s = "";
/* 117 */     if (this.array != null) {
/* 118 */       s = s + "array[" + this.array.length + "]";
/* 119 */     } else if (this.str != null) {
/* 120 */       s = this.str;
/* 121 */       if (s.length() > 80)
/* 122 */         s = s.substring(0, 80) + "...";
/* 123 */       s = s.replaceAll("\n", " | ");
/* 124 */       s = "\"" + s + "\"";
/*     */     }
/* 126 */     else if (this.value == (int)this.value) {
/* 127 */       s = s + (int)this.value;
/*     */     } else {
/* 129 */       s = s + IJ.d2s(this.value, 4);
/*     */     }
/* 131 */     return s;
/*     */   }
/*     */   public synchronized Object clone() {
/*     */     try {
/* 135 */       return super.clone(); } catch (CloneNotSupportedException e) {
/* 136 */     }return null;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.macro.Variable
 * JD-Core Version:    0.6.2
 */