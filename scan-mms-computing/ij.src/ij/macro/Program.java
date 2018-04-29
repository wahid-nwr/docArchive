/*     */ package ij.macro;
/*     */ 
/*     */ import ij.IJ;
/*     */ import java.util.Hashtable;
/*     */ 
/*     */ public class Program
/*     */   implements MacroConstants
/*     */ {
/*   8 */   private int maxSymbols = 800;
/*   9 */   private int maxProgramSize = 2000;
/*  10 */   private int pc = -1;
/*     */ 
/*  12 */   int stLoc = -1;
/*     */   int symTabLoc;
/*  14 */   Symbol[] table = new Symbol[this.maxSymbols];
/*     */   static Symbol[] systemTable;
/*  16 */   int[] code = new int[this.maxProgramSize];
/*  17 */   int[] lineNumbers = new int[this.maxProgramSize];
/*     */   Variable[] globals;
/*     */   boolean hasVars;
/*     */   boolean hasFunctions;
/*     */   int macroCount;
/*     */   Hashtable menus;
/*     */   boolean queueCommands;
/*     */   Hashtable extensionRegistry;
/*     */ 
/*     */   public Program()
/*     */   {
/*  28 */     if (systemTable != null) {
/*  29 */       this.stLoc = (systemTable.length - 1);
/*  30 */       for (int i = 0; i <= this.stLoc; i++)
/*  31 */         this.table[i] = systemTable[i];
/*     */     }
/*     */     else {
/*  34 */       addKeywords();
/*  35 */       addFunctions();
/*  36 */       addNumericFunctions();
/*  37 */       addStringFunctions();
/*  38 */       addArrayFunctions();
/*  39 */       systemTable = new Symbol[this.stLoc + 1];
/*  40 */       for (int i = 0; i <= this.stLoc; i++)
/*  41 */         systemTable[i] = this.table[i];
/*  42 */       IJ.register(Program.class);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int[] getCode() {
/*  47 */     return this.code;
/*     */   }
/*     */ 
/*     */   public Symbol[] getSymbolTable() {
/*  51 */     return this.table;
/*     */   }
/*     */ 
/*     */   void addKeywords() {
/*  55 */     for (int i = 0; i < keywords.length; i++)
/*  56 */       addSymbol(new Symbol(keywordIDs[i], keywords[i]));
/*     */   }
/*     */ 
/*     */   void addFunctions() {
/*  60 */     for (int i = 0; i < functions.length; i++)
/*  61 */       addSymbol(new Symbol(functionIDs[i], functions[i]));
/*     */   }
/*     */ 
/*     */   void addNumericFunctions() {
/*  65 */     for (int i = 0; i < numericFunctions.length; i++)
/*  66 */       addSymbol(new Symbol(numericFunctionIDs[i], numericFunctions[i]));
/*     */   }
/*     */ 
/*     */   void addStringFunctions() {
/*  70 */     for (int i = 0; i < stringFunctions.length; i++)
/*  71 */       addSymbol(new Symbol(stringFunctionIDs[i], stringFunctions[i]));
/*     */   }
/*     */ 
/*     */   void addArrayFunctions() {
/*  75 */     for (int i = 0; i < arrayFunctions.length; i++)
/*  76 */       addSymbol(new Symbol(arrayFunctionIDs[i], arrayFunctions[i]));
/*     */   }
/*     */ 
/*     */   void addSymbol(Symbol sym) {
/*  80 */     this.stLoc += 1;
/*  81 */     if (this.stLoc == this.table.length) {
/*  82 */       Symbol[] tmp = new Symbol[this.maxSymbols * 2];
/*  83 */       System.arraycopy(this.table, 0, tmp, 0, this.maxSymbols);
/*  84 */       this.table = tmp;
/*  85 */       this.maxSymbols *= 2;
/*     */     }
/*  87 */     this.table[this.stLoc] = sym;
/*     */   }
/*     */ 
/*     */   void addToken(int tok, int lineNumber) {
/*  91 */     this.pc += 1;
/*  92 */     if (this.pc == this.code.length) {
/*  93 */       int[] tmp = new int[this.maxProgramSize * 2];
/*  94 */       System.arraycopy(this.code, 0, tmp, 0, this.maxProgramSize);
/*  95 */       this.code = tmp;
/*     */ 
/*  97 */       tmp = new int[this.maxProgramSize * 2];
/*  98 */       System.arraycopy(this.lineNumbers, 0, tmp, 0, this.maxProgramSize);
/*  99 */       this.lineNumbers = tmp;
/*     */ 
/* 101 */       this.maxProgramSize *= 2;
/*     */     }
/* 103 */     this.code[this.pc] = tok;
/* 104 */     this.lineNumbers[this.pc] = lineNumber;
/*     */   }
/*     */ 
/*     */   Symbol lookupWord(String str)
/*     */   {
/* 112 */     for (int i = 0; i <= this.stLoc; i++) {
/* 113 */       Symbol symbol = this.table[i];
/* 114 */       if ((symbol.type != 133) && (str.equals(symbol.str))) {
/* 115 */         this.symTabLoc = i;
/* 116 */         return symbol;
/*     */       }
/*     */     }
/* 119 */     return null;
/*     */   }
/*     */ 
/*     */   void saveGlobals(Interpreter interp)
/*     */   {
/* 124 */     if (interp.topOfStack == -1)
/* 125 */       return;
/* 126 */     int n = interp.topOfStack + 1;
/* 127 */     this.globals = new Variable[n];
/* 128 */     for (int i = 0; i < n; i++)
/* 129 */       this.globals[i] = interp.stack[i];
/*     */   }
/*     */ 
/*     */   public void dumpSymbolTable() {
/* 133 */     IJ.log("");
/* 134 */     IJ.log("Symbol Table");
/* 135 */     for (int i = 0; i <= this.maxSymbols; i++) {
/* 136 */       Symbol symbol = this.table[i];
/* 137 */       if (symbol == null)
/*     */         break;
/* 139 */       IJ.log(i + " " + symbol);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void dumpProgram() {
/* 144 */     IJ.log("");
/* 145 */     IJ.log("Tokenized Program");
/*     */ 
/* 148 */     for (int i = 0; i <= this.pc; i++)
/* 149 */       IJ.log(i + "\t " + this.lineNumbers[i] + "   " + (this.code[i] & 0xFFF) + "   " + decodeToken(this.code[i]));
/*     */   }
/*     */ 
/*     */   public Variable[] getGlobals() {
/* 153 */     return this.globals;
/*     */   }
/*     */ 
/*     */   public boolean hasVars() {
/* 157 */     return this.hasVars;
/*     */   }
/*     */ 
/*     */   public int macroCount() {
/* 161 */     return this.macroCount;
/*     */   }
/*     */ 
/*     */   public String decodeToken(int token) {
/* 165 */     return decodeToken(token & 0xFFF, token >> 12);
/*     */   }
/*     */ 
/*     */   String decodeToken(int token, int address)
/*     */   {
/*     */     String str;
/*     */     String str;
/*     */     String str;
/* 170 */     switch (token) {
/*     */     case 129:
/*     */     case 134:
/*     */     case 135:
/*     */     case 136:
/*     */     case 137:
/*     */     case 138:
/* 177 */       str = this.table[address].str;
/* 178 */       break;
/*     */     case 133:
/* 180 */       str = "\"" + this.table[address].str + "\"";
/* 181 */       break;
/*     */     case 130:
/* 183 */       double v = this.table[address].value;
/* 184 */       if ((int)v == v)
/* 185 */         str = IJ.d2s(v, 0);
/*     */       else
/* 187 */         str = "" + v;
/* 188 */       break;
/*     */     case 128:
/* 190 */       str = "EOF";
/* 191 */       break;
/*     */     case 131:
/*     */     case 132:
/*     */     default:
/* 193 */       if (token < 32) {
/* 194 */         switch (token) {
/*     */         case 1:
/* 196 */           str = "++";
/* 197 */           break;
/*     */         case 2:
/* 199 */           str = "--";
/* 200 */           break;
/*     */         case 9:
/* 202 */           str = "+=";
/* 203 */           break;
/*     */         case 10:
/* 205 */           str = "-=";
/* 206 */           break;
/*     */         case 11:
/* 208 */           str = "*=";
/* 209 */           break;
/*     */         case 12:
/* 211 */           str = "/=";
/* 212 */           break;
/*     */         case 13:
/* 214 */           str = "&&";
/* 215 */           break;
/*     */         case 14:
/* 217 */           str = "||";
/* 218 */           break;
/*     */         case 3:
/* 220 */           str = "==";
/* 221 */           break;
/*     */         case 4:
/* 223 */           str = "!=";
/* 224 */           break;
/*     */         case 5:
/* 226 */           str = ">";
/* 227 */           break;
/*     */         case 6:
/* 229 */           str = ">=";
/* 230 */           break;
/*     */         case 7:
/* 232 */           str = "<";
/* 233 */           break;
/*     */         case 8:
/* 235 */           str = "<=";
/* 236 */           break;
/*     */         default:
/* 238 */           str = "";
/* 239 */           break;
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/*     */         String str;
/* 241 */         if (token >= 200) {
/* 242 */           str = this.table[address].str;
/*     */         } else {
/* 244 */           char[] s = new char[1];
/* 245 */           s[0] = ((char)token);
/* 246 */           str = new String(s);
/*     */         }
/*     */       }
/*     */       break;
/*     */     }
/* 250 */     return str;
/*     */   }
/*     */ 
/*     */   public Hashtable getMenus() {
/* 254 */     return this.menus;
/*     */   }
/*     */ 
/*     */   public boolean hasWord(String word)
/*     */   {
/* 260 */     for (int i = 0; i < this.code.length; i++) {
/* 261 */       int token = this.code[i];
/* 262 */       if (token > 127) {
/* 263 */         if (token == 128) return false;
/* 264 */         int tokenAddress = token >> 12;
/* 265 */         String str = this.table[tokenAddress].str;
/* 266 */         if ((str != null) && (str.equals(word))) return true; 
/*     */       }
/*     */     }
/* 268 */     return false;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.macro.Program
 * JD-Core Version:    0.6.2
 */