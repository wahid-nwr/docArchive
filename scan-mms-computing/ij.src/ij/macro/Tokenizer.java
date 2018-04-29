/*     */ package ij.macro;
/*     */ 
/*     */ import java.io.StreamTokenizer;
/*     */ import java.io.StringReader;
/*     */ 
/*     */ public class Tokenizer
/*     */   implements MacroConstants
/*     */ {
/*     */   private StreamTokenizer st;
/*     */   private int token;
/*     */   private String tokenString;
/*     */   private double tokenValue;
/*     */   private Program pgm;
/*     */   private int lineNumber;
/*     */ 
/*     */   public Program tokenize(String program)
/*     */   {
/*  18 */     this.st = new StreamTokenizer(new StringReader(program));
/*     */ 
/*  20 */     this.st.ordinaryChar(45);
/*  21 */     this.st.ordinaryChar(47);
/*  22 */     this.st.ordinaryChar(46);
/*  23 */     this.st.wordChars(95, 95);
/*  24 */     this.st.whitespaceChars(128, 255);
/*  25 */     this.st.slashStarComments(true);
/*  26 */     this.st.slashSlashComments(true);
/*  27 */     this.pgm = new Program();
/*     */     do {
/*  29 */       getToken();
/*  30 */       addToken();
/*  31 */     }while (this.token != 128);
/*  32 */     if (this.pgm.hasFunctions)
/*  33 */       addUserFunctions();
/*  34 */     return this.pgm;
/*     */   }
/*     */ 
/*     */   final void getToken() {
/*     */     try {
/*  39 */       this.token = this.st.nextToken();
/*  40 */       this.lineNumber = this.st.lineno();
/*  41 */       String ret = null;
/*     */       int nextToken;
/*  43 */       switch (this.st.ttype) {
/*     */       case -1:
/*  45 */         ret = "EOF";
/*  46 */         this.token = 128;
/*  47 */         break;
/*     */       case -3:
/*  49 */         ret = this.st.sval;
/*  50 */         this.token = 129;
/*  51 */         break;
/*     */       case -2:
/*  53 */         ret = "" + this.st.nval;
/*  54 */         this.tokenValue = this.st.nval;
/*  55 */         if (this.tokenValue == 0.0D)
/*  56 */           this.tokenValue = getHexConstant();
/*  57 */         else if (tryScientificNotation())
/*  58 */           ret = ret + this.st.sval;
/*  59 */         this.token = 130;
/*  60 */         break;
/*     */       case 34:
/*     */       case 39:
/*  62 */         ret = "" + this.st.sval;
/*  63 */         this.token = 133;
/*  64 */         break;
/*     */       case 43:
/*  66 */         nextToken = this.st.nextToken();
/*  67 */         if (nextToken == 43)
/*  68 */           this.token = 1;
/*  69 */         else if (nextToken == 61)
/*  70 */           this.token = 9;
/*     */         else
/*  72 */           this.st.pushBack();
/*  73 */         break;
/*     */       case 45:
/*  75 */         nextToken = this.st.nextToken();
/*  76 */         if (nextToken == 45)
/*  77 */           this.token = 2;
/*  78 */         else if (nextToken == 61)
/*  79 */           this.token = 10;
/*     */         else
/*  81 */           this.st.pushBack();
/*  82 */         break;
/*     */       case 42:
/*  84 */         nextToken = this.st.nextToken();
/*  85 */         if (nextToken == 61)
/*  86 */           this.token = 11;
/*     */         else
/*  88 */           this.st.pushBack();
/*  89 */         break;
/*     */       case 47:
/*  91 */         nextToken = this.st.nextToken();
/*  92 */         if (nextToken == 61)
/*  93 */           this.token = 12;
/*     */         else
/*  95 */           this.st.pushBack();
/*  96 */         break;
/*     */       case 61:
/*  98 */         nextToken = this.st.nextToken();
/*  99 */         if (nextToken == 61)
/* 100 */           this.token = 3;
/*     */         else
/* 102 */           this.st.pushBack();
/* 103 */         break;
/*     */       case 33:
/* 105 */         nextToken = this.st.nextToken();
/* 106 */         if (nextToken == 61)
/* 107 */           this.token = 4;
/*     */         else
/* 109 */           this.st.pushBack();
/* 110 */         break;
/*     */       case 62:
/* 112 */         nextToken = this.st.nextToken();
/* 113 */         if (nextToken == 61) {
/* 114 */           this.token = 6;
/* 115 */         } else if (nextToken == 62) {
/* 116 */           this.token = 15;
/*     */         } else {
/* 118 */           this.st.pushBack();
/* 119 */           this.token = 5;
/*     */         }
/* 121 */         break;
/*     */       case 60:
/* 123 */         nextToken = this.st.nextToken();
/* 124 */         if (nextToken == 61) {
/* 125 */           this.token = 8;
/* 126 */         } else if (nextToken == 60) {
/* 127 */           this.token = 16;
/*     */         } else {
/* 129 */           this.st.pushBack();
/* 130 */           this.token = 7;
/*     */         }
/* 132 */         break;
/*     */       case 38:
/* 134 */         nextToken = this.st.nextToken();
/* 135 */         if (nextToken == 38)
/* 136 */           this.token = 13;
/*     */         else
/* 138 */           this.st.pushBack();
/* 139 */         break;
/*     */       case 124:
/* 141 */         nextToken = this.st.nextToken();
/* 142 */         if (nextToken == 124)
/* 143 */           this.token = 14;
/*     */         else
/* 145 */           this.st.pushBack();
/* 146 */         break;
/*     */       }
/*     */ 
/* 152 */       this.tokenString = ret;
/*     */     }
/*     */     catch (Exception e) {
/*     */     }
/*     */   }
/*     */ 
/*     */   final void addToken() {
/* 159 */     int tok = this.token;
/* 160 */     switch (this.token) {
/*     */     case 129:
/* 162 */       Symbol symbol = this.pgm.lookupWord(this.tokenString);
/* 163 */       if (symbol != null) {
/* 164 */         int type = symbol.getFunctionType();
/* 165 */         if (type == 0) {
/* 166 */           tok = symbol.type;
/* 167 */           switch (tok) { case 207:
/* 168 */             this.pgm.hasFunctions = true; break;
/*     */           case 201:
/* 169 */             this.pgm.hasVars = true; break;
/*     */           case 200:
/* 170 */             this.pgm.macroCount += 1; }
/*     */         }
/*     */         else {
/* 173 */           tok = type;
/* 174 */         }tok += (this.pgm.symTabLoc << 12);
/*     */       } else {
/* 176 */         this.pgm.addSymbol(new Symbol(this.token, this.tokenString));
/* 177 */         tok += (this.pgm.stLoc << 12);
/*     */       }
/* 179 */       break;
/*     */     case 133:
/* 181 */       this.pgm.addSymbol(new Symbol(this.token, this.tokenString));
/* 182 */       tok += (this.pgm.stLoc << 12);
/* 183 */       break;
/*     */     case 130:
/* 185 */       this.pgm.addSymbol(new Symbol(this.tokenValue));
/* 186 */       tok += (this.pgm.stLoc << 12);
/* 187 */       break;
/*     */     case 131:
/*     */     case 132:
/*     */     }
/* 191 */     this.pgm.addToken(tok, this.lineNumber);
/*     */   }
/*     */ 
/*     */   double getHexConstant() {
/*     */     try {
/* 196 */       this.token = this.st.nextToken();
/*     */     } catch (Exception e) {
/* 198 */       return 0.0D;
/*     */     }
/* 200 */     if (this.st.ttype != -3) {
/* 201 */       this.st.pushBack();
/* 202 */       return 0.0D;
/*     */     }
/* 204 */     if (!this.st.sval.startsWith("x")) {
/* 205 */       this.st.pushBack();
/* 206 */       return 0.0D;
/*     */     }
/* 208 */     String s = this.st.sval.substring(1, this.st.sval.length());
/* 209 */     double n = 0.0D;
/*     */     try {
/* 211 */       n = Integer.parseInt(s, 16);
/*     */     } catch (NumberFormatException e) {
/* 213 */       this.st.pushBack();
/* 214 */       n = 0.0D;
/*     */     }
/* 216 */     return n;
/*     */   }
/*     */ 
/*     */   boolean tryScientificNotation() {
/* 220 */     String sval = "" + this.tokenValue;
/*     */     try {
/* 222 */       int next = this.st.nextToken();
/* 223 */       String sval2 = this.st.sval;
/* 224 */       if ((this.st.ttype == -3) && ((sval2.startsWith("e")) || (sval2.startsWith("E"))))
/*     */       {
/* 226 */         if (sval2.equalsIgnoreCase("e"))
/*     */         {
/* 228 */           next = this.st.nextToken();
/* 229 */           if (next == 45)
/* 230 */             sval2 = sval2 + "-";
/* 231 */           else if (next != 43)
/* 232 */             throw new Exception();
/* 233 */           if (this.st.nextToken() != -2)
/* 234 */             throw new Exception();
/* 235 */           sval2 = sval2 + this.st.nval;
/*     */         }
/* 237 */         if (sval2.endsWith(".0"))
/* 238 */           sval2 = sval2.substring(0, sval2.length() - 2);
/* 239 */         this.tokenValue = Double.parseDouble(sval + sval2);
/* 240 */         return true;
/*     */       }
/* 242 */       this.st.pushBack(); } catch (Exception e) {
/*     */     }
/* 244 */     return false;
/*     */   }
/*     */ 
/*     */   void addUserFunctions()
/*     */   {
/* 249 */     int[] code = this.pgm.getCode();
/*     */ 
/* 251 */     for (int i = 0; i < code.length; i++) {
/* 252 */       this.token = (code[i] & 0xFFF);
/* 253 */       if (this.token == 207) {
/* 254 */         int nextToken = code[(i + 1)] & 0xFFF;
/* 255 */         if ((nextToken == 129) || ((nextToken >= 134) && (nextToken <= 137)))
/*     */         {
/*     */           int address2;
/* 256 */           int address = address2 = code[(i + 1)] >> 12;
/* 257 */           if (nextToken != 129) {
/* 258 */             this.pgm.addSymbol(new Symbol(129, this.pgm.getSymbolTable()[address].str));
/* 259 */             address2 = this.pgm.stLoc;
/* 260 */             code[(i + 1)] = (129 + (address2 << 12));
/*     */           }
/* 262 */           Symbol sym = this.pgm.getSymbolTable()[address2];
/* 263 */           sym.type = 138;
/* 264 */           sym.value = (i + 1);
/* 265 */           for (int j = 0; j < code.length; j++) {
/* 266 */             this.token = (code[j] & 0xFFF);
/* 267 */             if (((this.token == 129) || ((this.token >= 134) && (this.token <= 137))) && (code[j] >> 12 == address) && ((j == 0) || ((code[(j - 1)] & 0xFFF) != 207)))
/*     */             {
/* 269 */               code[j] = 138;
/* 270 */               code[j] += (address2 << 12);
/*     */             } else {
/* 272 */               if (this.token == 128) break;
/*     */             }
/*     */           }
/*     */         }
/*     */       } else {
/* 277 */         if (this.token == 128)
/*     */           break;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.macro.Tokenizer
 * JD-Core Version:    0.6.2
 */