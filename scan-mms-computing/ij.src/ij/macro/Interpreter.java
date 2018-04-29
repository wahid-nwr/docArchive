/*      */ package ij.macro;
/*      */ 
/*      */ import ij.IJ;
/*      */ import ij.ImagePlus;
/*      */ import ij.WindowManager;
/*      */ import ij.gui.GenericDialog;
/*      */ import ij.gui.Plot;
/*      */ import ij.measure.ResultsTable;
/*      */ import ij.plugin.frame.Editor;
/*      */ import ij.plugin.frame.RoiManager;
/*      */ import ij.process.ColorProcessor;
/*      */ import ij.text.TextPanel;
/*      */ import ij.text.TextWindow;
/*      */ import ij.util.Tools;
/*      */ import java.awt.Frame;
/*      */ import java.io.PrintWriter;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Vector;
/*      */ 
/*      */ public class Interpreter
/*      */   implements MacroConstants
/*      */ {
/*      */   public static final int NONE = 0;
/*      */   public static final int STEP = 1;
/*      */   public static final int TRACE = 2;
/*      */   public static final int FAST_TRACE = 3;
/*      */   public static final int RUN = 4;
/*      */   public static final int RUN_TO_CARET = 5;
/*      */   static final int STACK_SIZE = 1000;
/*      */   static final int MAX_ARGS = 20;
/*      */   int pc;
/*      */   int token;
/*      */   int tokenAddress;
/*      */   double tokenValue;
/*      */   String tokenString;
/*   27 */   boolean looseSyntax = true;
/*      */   int lineNumber;
/*      */   boolean statusUpdated;
/*      */   boolean showingProgress;
/*      */   boolean keysSet;
/*      */   boolean checkingType;
/*      */   int prefixValue;
/*      */   Variable[] stack;
/*   36 */   int topOfStack = -1;
/*   37 */   int topOfGlobals = -1;
/*   38 */   int startOfLocals = 0;
/*      */   static Interpreter instance;
/*      */   static Interpreter previousInstance;
/*      */   public static boolean batchMode;
/*      */   static Vector imageTable;
/*      */   boolean done;
/*      */   Program pgm;
/*      */   Functions func;
/*      */   boolean inFunction;
/*      */   String macroName;
/*      */   String argument;
/*      */   String returnValue;
/*      */   boolean calledMacro;
/*      */   double[] rgbWeights;
/*      */   boolean inPrint;
/*      */   static String additionalFunctions;
/*      */   Editor editor;
/*   55 */   int debugMode = 0;
/*      */   boolean showDebugFunctions;
/*      */   static boolean showVariables;
/*      */   boolean wasError;
/*      */   ReturnException returnException;
/*      */ 
/*      */   public void run(String macro)
/*      */   {
/*   62 */     if (additionalFunctions != null) {
/*   63 */       if ((!macro.endsWith("\n")) && (!additionalFunctions.startsWith("\n")))
/*   64 */         macro = macro + "\n" + additionalFunctions;
/*      */       else
/*   66 */         macro = macro + additionalFunctions;
/*      */     }
/*   68 */     Tokenizer tok = new Tokenizer();
/*   69 */     Program pgm = tok.tokenize(macro);
/*   70 */     if ((pgm.hasVars) && (pgm.hasFunctions))
/*   71 */       saveGlobals2(pgm);
/*   72 */     run(pgm);
/*      */   }
/*      */ 
/*      */   public String run(String macro, String arg)
/*      */   {
/*   78 */     this.argument = arg;
/*   79 */     this.calledMacro = true;
/*   80 */     if (IJ.getInstance() == null)
/*   81 */       setBatchMode(true);
/*   82 */     Interpreter saveInstance = instance;
/*   83 */     run(macro);
/*   84 */     instance = saveInstance;
/*   85 */     return this.returnValue;
/*      */   }
/*      */ 
/*      */   public void run(Program pgm)
/*      */   {
/*   90 */     this.pgm = pgm;
/*   91 */     this.pc = -1;
/*   92 */     instance = this;
/*   93 */     if (!this.calledMacro) {
/*   94 */       batchMode = false;
/*   95 */       imageTable = null;
/*      */     }
/*   97 */     pushGlobals();
/*   98 */     if (this.func == null)
/*   99 */       this.func = new Functions(this, pgm);
/*  100 */     this.func.plot = null;
/*      */ 
/*  102 */     doStatements();
/*  103 */     finishUp();
/*      */   }
/*      */ 
/*      */   public void run(int location)
/*      */   {
/*  108 */     this.topOfStack = this.topOfGlobals;
/*  109 */     this.done = false;
/*  110 */     this.pc = (location - 1);
/*  111 */     doStatements();
/*      */   }
/*      */ 
/*      */   public void runMacro(Program pgm, int macroLoc, String macroName)
/*      */   {
/*  116 */     this.calledMacro = true;
/*  117 */     this.pgm = pgm;
/*  118 */     this.macroName = macroName;
/*  119 */     this.pc = (macroLoc - 1);
/*  120 */     previousInstance = instance;
/*  121 */     instance = this;
/*      */ 
/*  123 */     pushGlobals();
/*  124 */     if (this.func == null)
/*  125 */       this.func = new Functions(this, pgm);
/*  126 */     this.func.plot = null;
/*  127 */     if (macroLoc == 0)
/*  128 */       doStatements();
/*      */     else
/*  130 */       doBlock();
/*  131 */     finishUp();
/*  132 */     ij.plugin.frame.Recorder.recordInMacros = false;
/*      */   }
/*      */ 
/*      */   public void saveGlobals(Program pgm)
/*      */   {
/*  137 */     saveGlobals2(pgm);
/*      */   }
/*      */ 
/*      */   void saveGlobals2(Program pgm) {
/*  141 */     this.pgm = pgm;
/*  142 */     this.pc = -1;
/*  143 */     instance = this;
/*  144 */     this.func = new Functions(this, pgm);
/*  145 */     while (!this.done) {
/*  146 */       getToken();
/*  147 */       switch (this.token) { case 201:
/*  148 */         doVar(); break;
/*      */       case 200:
/*  149 */         skipMacro(); break;
/*      */       case 207:
/*  150 */         skipFunction();
/*      */       }
/*      */     }
/*      */ 
/*  154 */     instance = null;
/*  155 */     pgm.saveGlobals(this);
/*  156 */     this.pc = -1;
/*  157 */     this.topOfStack = -1;
/*  158 */     this.done = false;
/*      */   }
/*      */ 
/*      */   final void getToken() {
/*  162 */     if (this.done) return;
/*  163 */     this.token = this.pgm.code[(++this.pc)];
/*      */ 
/*  165 */     if (this.token <= 127)
/*  166 */       return;
/*  167 */     this.tokenAddress = (this.token >> 12);
/*  168 */     this.token &= 4095;
/*  169 */     Symbol sym = this.pgm.table[this.tokenAddress];
/*  170 */     this.tokenString = sym.str;
/*  171 */     this.tokenValue = sym.value;
/*  172 */     this.done = (this.token == 128);
/*      */   }
/*      */ 
/*      */   final int nextToken() {
/*  176 */     return this.pgm.code[(this.pc + 1)] & 0xFFF;
/*      */   }
/*      */ 
/*      */   final int nextNextToken() {
/*  180 */     return this.pgm.code[(this.pc + 2)] & 0xFFF;
/*      */   }
/*      */ 
/*      */   final void putTokenBack() {
/*  184 */     this.pc -= 1;
/*  185 */     if (this.pc < 0)
/*  186 */       this.pc = -1;
/*      */   }
/*      */ 
/*      */   void doStatements() {
/*  190 */     while (!this.done)
/*  191 */       doStatement();
/*      */   }
/*      */ 
/*      */   final void doStatement() {
/*  195 */     getToken();
/*  196 */     if ((this.debugMode != 0) && (this.editor != null) && (!this.done) && (this.token != 59) && (this.token != 207))
/*  197 */       this.editor.debug(this, this.debugMode);
/*  198 */     switch (this.token) {
/*      */     case 201:
/*  200 */       doVar();
/*  201 */       break;
/*      */     case 134:
/*  203 */       this.func.doFunction(this.pgm.table[this.tokenAddress].type);
/*  204 */       break;
/*      */     case 138:
/*  206 */       runUserFunction();
/*  207 */       break;
/*      */     case 208:
/*  209 */       doReturn();
/*  210 */       break;
/*      */     case 129:
/*  212 */       doAssignment();
/*  213 */       break;
/*      */     case 202:
/*  215 */       doIf();
/*  216 */       return;
/*      */     case 203:
/*  218 */       error("Else without if");
/*  219 */       return;
/*      */     case 206:
/*  221 */       doFor();
/*  222 */       return;
/*      */     case 204:
/*  224 */       doWhile();
/*  225 */       return;
/*      */     case 205:
/*  227 */       doDo();
/*  228 */       return;
/*      */     case 200:
/*  230 */       runFirstMacro();
/*  231 */       return;
/*      */     case 207:
/*  233 */       skipFunction();
/*  234 */       return;
/*      */     case 59:
/*  236 */       return;
/*      */     case 123:
/*  238 */       putTokenBack();
/*  239 */       doBlock();
/*  240 */       return;
/*      */     case 40:
/*      */     case 130:
/*      */     case 133:
/*      */     case 135:
/*      */     case 136:
/*  246 */       putTokenBack();
/*  247 */       this.inPrint = true;
/*  248 */       String s = getString();
/*  249 */       this.inPrint = false;
/*  250 */       if ((s != null) && (s.length() > 0) && (!s.equals("NaN")) && (!s.equals("[aborted]")))
/*  251 */         IJ.log(s);
/*  252 */       return;
/*      */     case 137:
/*  253 */       this.func.getArrayFunction(this.pgm.table[this.tokenAddress].type); break;
/*      */     case 128:
/*  254 */       break;
/*      */     default:
/*  256 */       error("Statement cannot begin with '" + this.pgm.decodeToken(this.token, this.tokenAddress) + "'");
/*      */     }
/*  258 */     if (!this.looseSyntax) {
/*  259 */       getToken();
/*  260 */       if ((this.token != 59) && (!this.done))
/*  261 */         error("';' expected");
/*      */     }
/*      */   }
/*      */ 
/*      */   Variable runUserFunction() {
/*  266 */     int newPC = (int)this.tokenValue;
/*  267 */     int saveStartOfLocals = this.startOfLocals;
/*  268 */     this.startOfLocals = (this.topOfStack + 1);
/*  269 */     int saveTOS = this.topOfStack;
/*  270 */     int nArgs = pushArgs();
/*  271 */     int savePC = this.pc;
/*  272 */     Variable value = null;
/*  273 */     this.pc = newPC;
/*  274 */     setupArgs(nArgs);
/*  275 */     boolean saveInFunction = this.inFunction;
/*  276 */     this.inFunction = true;
/*      */     try {
/*  278 */       doBlock();
/*      */     } catch (ReturnException e) {
/*  280 */       value = new Variable(0, e.value, e.str, e.array);
/*  281 */       if ((value.getArray() != null) && (e.arraySize != 0))
/*  282 */         value.setArraySize(e.arraySize);
/*      */     }
/*  284 */     this.inFunction = saveInFunction;
/*  285 */     this.pc = savePC;
/*  286 */     trimStack(saveTOS, saveStartOfLocals);
/*  287 */     return value;
/*      */   }
/*      */ 
/*      */   int pushArgs()
/*      */   {
/*  292 */     getLeftParen();
/*  293 */     int count = 0;
/*  294 */     Variable[] args = new Variable[20];
/*      */ 
/*  296 */     if (nextToken() != 41) {
/*      */       do {
/*  298 */         if (count == 20)
/*  299 */           error("Too many arguments");
/*  300 */         int next = nextToken();
/*  301 */         int nextPlus = this.pgm.code[(this.pc + 2)] & 0xFF;
/*  302 */         if ((next == 133) || (next == 136)) {
/*  303 */           args[count] = new Variable(0, 0.0D, getString());
/*  304 */         } else if (next == 138) {
/*  305 */           int savePC = this.pc;
/*  306 */           getToken();
/*  307 */           boolean simpleFunctionCall = isSimpleFunctionCall(false);
/*  308 */           this.pc = savePC;
/*  309 */           if (simpleFunctionCall) {
/*  310 */             getToken();
/*  311 */             Variable v2 = runUserFunction();
/*  312 */             if (v2 == null)
/*  313 */               error("No return value");
/*  314 */             args[count] = v2;
/*      */           } else {
/*  316 */             args[count] = new Variable(0, getExpression(), null);
/*      */           } } else if ((next == 129) && ((nextPlus == 44) || (nextPlus == 41))) {
/*  318 */           double value = 0.0D;
/*  319 */           Variable[] array = null;
/*  320 */           int arraySize = 0;
/*  321 */           String str = null;
/*  322 */           getToken();
/*  323 */           Variable v = lookupVariable();
/*  324 */           if (v != null) {
/*  325 */             int type = v.getType();
/*  326 */             if (type == 0) {
/*  327 */               value = v.getValue();
/*  328 */             } else if (type == 1) {
/*  329 */               array = v.getArray();
/*  330 */               arraySize = v.getArraySize();
/*      */             } else {
/*  332 */               str = v.getString();
/*      */             }
/*      */           }
/*  334 */           args[count] = new Variable(0, value, str, array);
/*  335 */           if (array != null) args[count].setArraySize(arraySize); 
/*      */         }
/*  336 */         else if ((next == 129) && (nextPlus == 91)) {
/*  337 */           int savePC = this.pc;
/*  338 */           getToken();
/*  339 */           Variable v = lookupVariable();
/*  340 */           v = getArrayElement(v);
/*  341 */           if (v.getString() != null) {
/*  342 */             args[count] = new Variable(0, 0.0D, v.getString(), null);
/*      */           } else {
/*  344 */             this.pc = savePC;
/*  345 */             args[count] = new Variable(0, getExpression(), null);
/*      */           }
/*      */         } else {
/*  348 */           args[count] = new Variable(0, getExpression(), null);
/*  349 */         }count++;
/*  350 */         getToken();
/*  351 */       }while (this.token == 44);
/*  352 */       putTokenBack();
/*      */     }
/*  354 */     int nArgs = count;
/*  355 */     while (count > 0)
/*  356 */       push(args[(--count)], this);
/*  357 */     getRightParen();
/*  358 */     return nArgs;
/*      */   }
/*      */ 
/*      */   void setupArgs(int nArgs) {
/*  362 */     getLeftParen();
/*  363 */     int i = this.topOfStack;
/*  364 */     int count = nArgs;
/*  365 */     if (nextToken() != 41) {
/*      */       do {
/*  367 */         getToken();
/*  368 */         if (i >= 0)
/*  369 */           this.stack[i].symTabIndex = this.tokenAddress;
/*  370 */         i--;
/*  371 */         count--;
/*  372 */         getToken();
/*  373 */       }while (this.token == 44);
/*  374 */       putTokenBack();
/*      */     }
/*  376 */     if (count != 0)
/*  377 */       error(nArgs + " argument" + (nArgs == 1 ? "" : "s") + " expected");
/*  378 */     getRightParen();
/*      */   }
/*      */ 
/*      */   void doReturn()
/*      */   {
/*  386 */     double value = 0.0D;
/*  387 */     String str = null;
/*  388 */     Variable[] array = null;
/*  389 */     int arraySize = 0;
/*  390 */     getToken();
/*  391 */     if (this.token != 59) {
/*  392 */       boolean isString = (this.token == 133) || (this.token == 136);
/*  393 */       boolean isArrayFunction = this.token == 137;
/*  394 */       if (this.token == 129) {
/*  395 */         Variable v = lookupLocalVariable(this.tokenAddress);
/*  396 */         if ((v != null) && (nextToken() == 59)) {
/*  397 */           array = v.getArray();
/*  398 */           if (array != null) arraySize = v.getArraySize();
/*  399 */           isString = v.getString() != null;
/*      */         }
/*      */       }
/*      */ 
/*  403 */       putTokenBack();
/*  404 */       if (isString) {
/*  405 */         str = getString();
/*  406 */       } else if (isArrayFunction) {
/*  407 */         getToken();
/*  408 */         array = this.func.getArrayFunction(this.pgm.table[this.tokenAddress].type);
/*  409 */       } else if (array == null) {
/*  410 */         value = getExpression();
/*      */       }
/*      */     }
/*  412 */     if (this.inFunction) {
/*  413 */       if (this.returnException == null)
/*  414 */         this.returnException = new ReturnException();
/*  415 */       this.returnException.value = value;
/*  416 */       this.returnException.str = str;
/*  417 */       this.returnException.array = array;
/*  418 */       this.returnException.arraySize = arraySize;
/*      */ 
/*  420 */       throw this.returnException;
/*      */     }
/*  422 */     finishUp();
/*  423 */     if ((value != 0.0D) || (array != null))
/*  424 */       error("Macros can only return strings");
/*  425 */     this.returnValue = str;
/*  426 */     this.done = true;
/*      */   }
/*      */ 
/*      */   void doFor()
/*      */   {
/*  431 */     boolean saveLooseSyntax = this.looseSyntax;
/*  432 */     this.looseSyntax = false;
/*  433 */     getToken();
/*  434 */     if (this.token != 40)
/*  435 */       error("'(' expected");
/*  436 */     getToken();
/*  437 */     if (this.token != 201)
/*  438 */       putTokenBack();
/*      */     do {
/*  440 */       if (nextToken() != 59)
/*  441 */         getAssignmentExpression();
/*  442 */       getToken();
/*  443 */     }while (this.token == 44);
/*      */ 
/*  445 */     if (this.token != 59)
/*  446 */       error("';' expected");
/*  447 */     int condPC = this.pc;
/*  448 */     int startPC = 0;
/*  449 */     double cond = 1.0D;
/*      */     while (true) {
/*  451 */       if (this.pgm.code[(this.pc + 1)] != 59)
/*  452 */         cond = getLogicalExpression();
/*  453 */       if (startPC == 0)
/*  454 */         checkBoolean(cond);
/*  455 */       getToken();
/*  456 */       if (this.token != 59)
/*  457 */         error("';' expected");
/*  458 */       int incPC = this.pc;
/*      */ 
/*  460 */       if (startPC != 0)
/*  461 */         this.pc = startPC;
/*      */       else {
/*  463 */         while (this.token != 41) {
/*  464 */           getToken();
/*      */ 
/*  466 */           if ((this.token == 123) || (this.token == 59) || (this.token == 40) || (this.done))
/*  467 */             error("')' expected");
/*      */         }
/*      */       }
/*  470 */       startPC = this.pc;
/*  471 */       if (cond == 1.0D) {
/*  472 */         doStatement();
/*      */       } else {
/*  474 */         skipStatement();
/*  475 */         break;
/*      */       }
/*  477 */       this.pc = incPC;
/*      */       do {
/*  479 */         if (nextToken() != 41)
/*  480 */           getAssignmentExpression();
/*  481 */         getToken();
/*  482 */       }while (this.token == 44);
/*  483 */       this.pc = condPC;
/*      */     }
/*  485 */     this.looseSyntax = saveLooseSyntax;
/*      */   }
/*  489 */   void doWhile() { this.looseSyntax = false;
/*  490 */     int savePC = this.pc;
/*      */     boolean isTrue;
/*      */     do {
/*  493 */       this.pc = savePC;
/*  494 */       isTrue = getBoolean();
/*  495 */       if (isTrue)
/*  496 */         doStatement();
/*      */       else
/*  498 */         skipStatement(); 
/*      */     }
/*  499 */     while ((isTrue) && (!this.done)); } 
/*      */   void doDo() {
/*  503 */     this.looseSyntax = false;
/*  504 */     int savePC = this.pc;
/*      */     boolean isTrue;
/*      */     do {
/*  507 */       doStatement();
/*  508 */       getToken();
/*  509 */       if (this.token != 204)
/*  510 */         error("'while' expected");
/*  511 */       isTrue = getBoolean();
/*  512 */       if (isTrue)
/*  513 */         this.pc = savePC; 
/*      */     }
/*  514 */     while ((isTrue) && (!this.done));
/*      */   }
/*      */ 
/*      */   final void doBlock() {
/*  518 */     getToken();
/*  519 */     if (this.token != 123)
/*  520 */       error("'{' expected");
/*  521 */     while (!this.done) {
/*  522 */       getToken();
/*  523 */       if (this.token == 125)
/*      */         break;
/*  525 */       putTokenBack();
/*  526 */       doStatement();
/*      */     }
/*  528 */     if (this.token != 125)
/*  529 */       error("'}' expected");
/*      */   }
/*      */ 
/*      */   final void skipStatement() {
/*  533 */     getToken();
/*  534 */     switch (this.token) { case 1:
/*      */     case 40:
/*      */     case 129:
/*      */     case 134:
/*      */     case 135:
/*      */     case 136:
/*      */     case 138:
/*      */     case 201:
/*      */     case 208:
/*  538 */       skipSimpleStatement();
/*  539 */       break;
/*      */     case 202:
/*  541 */       skipParens();
/*  542 */       skipStatement();
/*  543 */       getToken();
/*  544 */       if (this.token == 203)
/*  545 */         skipStatement();
/*      */       else
/*  547 */         putTokenBack();
/*  548 */       break;
/*      */     case 206:
/*  550 */       skipParens();
/*  551 */       skipStatement();
/*  552 */       break;
/*      */     case 204:
/*  554 */       skipParens();
/*  555 */       skipStatement();
/*  556 */       break;
/*      */     case 205:
/*  558 */       skipStatement();
/*  559 */       getToken();
/*  560 */       skipParens();
/*  561 */       break;
/*      */     case 59:
/*  563 */       break;
/*      */     case 123:
/*  565 */       putTokenBack();
/*  566 */       skipBlock();
/*  567 */       break;
/*      */     default:
/*  569 */       error("Skipped statement cannot begin with '" + this.pgm.decodeToken(this.token, this.tokenAddress) + "'"); }
/*      */   }
/*      */ 
/*      */   final void skipBlock()
/*      */   {
/*  574 */     int count = 0;
/*      */     do {
/*  576 */       getToken();
/*  577 */       if (this.token == 123) {
/*  578 */         count++;
/*  579 */       } else if (this.token == 125) {
/*  580 */         count--;
/*  581 */       } else if (this.done) {
/*  582 */         error("'}' expected");
/*  583 */         return;
/*      */       }
/*      */     }
/*  585 */     while (count > 0);
/*      */   }
/*      */ 
/*      */   final void skipParens() {
/*  589 */     int count = 0;
/*      */     do {
/*  591 */       getToken();
/*  592 */       if (this.token == 40) {
/*  593 */         count++;
/*  594 */       } else if (this.token == 41) {
/*  595 */         count--;
/*  596 */       } else if (this.done) {
/*  597 */         error("')' expected");
/*  598 */         return;
/*      */       }
/*      */     }
/*  600 */     while (count > 0);
/*      */   }
/*      */ 
/*      */   final void skipSimpleStatement() {
/*  604 */     boolean finished = this.done;
/*  605 */     getToken();
/*  606 */     while ((!finished) && (!this.done))
/*  607 */       if (this.token == 59)
/*  608 */         finished = true;
/*  609 */       else if ((this.token == 203) || ((this.token == 134) && (this.pgm.code[(this.pc - 1)] != 46)))
/*  610 */         error("';' expected");
/*      */       else
/*  612 */         getToken();
/*      */   }
/*      */ 
/*      */   void skipFunction()
/*      */   {
/*  618 */     getToken();
/*  619 */     skipParens();
/*  620 */     skipBlock();
/*      */   }
/*      */ 
/*      */   void runFirstMacro() {
/*  624 */     getToken();
/*  625 */     doBlock();
/*  626 */     this.done = true;
/*  627 */     finishUp();
/*      */   }
/*      */ 
/*      */   void skipMacro() {
/*  631 */     getToken();
/*  632 */     skipBlock();
/*      */   }
/*      */ 
/*      */   final void doAssignment() {
/*  636 */     int next = this.pgm.code[(this.pc + 1)] & 0xFF;
/*  637 */     if (next == 91) {
/*  638 */       doArrayElementAssignment();
/*  639 */       return;
/*      */     }
/*  641 */     int type = getExpressionType();
/*  642 */     switch (type) { case 2:
/*  643 */       doStringAssignment(); break;
/*      */     case 1:
/*  644 */       doArrayAssignment(); break;
/*      */     case 138:
/*  645 */       doUserFunctionAssignment(); break;
/*      */     case 136:
/*  646 */       doNumericStringAssignment(); break;
/*      */     default:
/*  648 */       putTokenBack();
/*  649 */       getAssignmentExpression(); }
/*      */   }
/*      */ 
/*      */   int getExpressionType()
/*      */   {
/*  654 */     int rightSideToken = this.pgm.code[(this.pc + 2)];
/*  655 */     int tok = rightSideToken & 0xFF;
/*  656 */     if (tok == 133)
/*  657 */       return 2;
/*  658 */     if (tok == 136) {
/*  659 */       int address = rightSideToken >> 12;
/*  660 */       int type = this.pgm.table[address].type;
/*  661 */       if (type == 2017) {
/*  662 */         int token2 = this.pgm.code[(this.pc + 4)];
/*  663 */         String name = this.pgm.table[(token2 >> 12)].str;
/*  664 */         if ((name.equals("getNumber")) || (name.equals("getCheckbox")))
/*  665 */           return 136;
/*  666 */       } else if (type == 2019) {
/*  667 */         int token2 = this.pgm.code[(this.pc + 4)];
/*  668 */         String name = this.pgm.table[(token2 >> 12)].str;
/*  669 */         if ((name.equals("exists")) || (name.equals("isDirectory")) || (name.equals("length")) || (name.equals("getLength")) || (name.equals("rename")) || (name.equals("delete")))
/*      */         {
/*  671 */           return 136;
/*      */         } } else if (type == 2027) {
/*  673 */         int token2 = this.pgm.code[(this.pc + 4)];
/*  674 */         String name = this.pgm.table[(token2 >> 12)].str;
/*  675 */         if (name.equals("getValue")) return 136;
/*      */       }
/*  677 */       return 2;
/*      */     }
/*  679 */     if (tok == 137)
/*  680 */       return 1;
/*  681 */     if (tok == 138)
/*  682 */       return 138;
/*  683 */     if (tok != 129)
/*  684 */       return 0;
/*  685 */     Variable v = lookupVariable(rightSideToken >> 12);
/*  686 */     if (v == null)
/*  687 */       return 0;
/*  688 */     int type = v.getType();
/*  689 */     if (type != 1)
/*  690 */       return type;
/*  691 */     if (this.pgm.code[(this.pc + 3)] == 46)
/*  692 */       return 0;
/*  693 */     if (this.pgm.code[(this.pc + 3)] != 91)
/*  694 */       return 1;
/*  695 */     int savePC = this.pc;
/*  696 */     getToken();
/*  697 */     getToken();
/*  698 */     this.checkingType = true;
/*  699 */     int index = getIndex();
/*  700 */     this.checkingType = false;
/*  701 */     this.pc = (savePC - 1);
/*  702 */     getToken();
/*  703 */     Variable[] array = v.getArray();
/*  704 */     if ((index < 0) || (index >= array.length))
/*  705 */       return 0;
/*  706 */     return array[index].getType();
/*      */   }
/*      */ 
/*      */   final void doNumericStringAssignment()
/*      */   {
/*  711 */     putTokenBack();
/*  712 */     getToken();
/*  713 */     Variable v = lookupLocalVariable(this.tokenAddress);
/*  714 */     if (v == null) v = push(this.tokenAddress, 0.0D, null, this);
/*  715 */     getToken();
/*  716 */     if (this.token != 61) error("'=' expected");
/*  717 */     v.setValue(getExpression());
/*      */   }
/*      */ 
/*      */   final void doArrayElementAssignment() {
/*  721 */     Variable v = lookupLocalVariable(this.tokenAddress);
/*  722 */     if (v == null)
/*  723 */       error("Undefined identifier");
/*  724 */     if ((this.pgm.code[(this.pc + 5)] == 59) && ((this.pgm.code[(this.pc + 4)] == 1) || (this.pgm.code[(this.pc + 4)] == 2))) {
/*  725 */       putTokenBack(); getFactor(); return;
/*  726 */     }int index = getIndex();
/*  727 */     int expressionType = getExpressionType();
/*  728 */     if (expressionType == 1)
/*  729 */       error("Arrays of arrays not supported");
/*  730 */     getToken();
/*  731 */     int op = this.token;
/*  732 */     if ((op != 61) && (op != 9) && (op != 10) && (op != 11) && (op != 12)) {
/*  733 */       error("'=', '+=', '-=', '*=' or '/=' expected"); return;
/*  734 */     }if ((op != 61) && ((expressionType == 2) || (expressionType == 1))) {
/*  735 */       error("'=' expected"); return;
/*  736 */     }Variable[] array = v.getArray();
/*  737 */     if (array == null)
/*  738 */       error("Array expected");
/*  739 */     if (index < 0)
/*  740 */       error("Negative index");
/*  741 */     if (index >= array.length) {
/*  742 */       if (!this.func.expandableArrays)
/*  743 */         error("Index (" + index + ") out of range");
/*  744 */       Variable[] array2 = new Variable[index + array.length / 2 + 1];
/*      */ 
/*  746 */       boolean strings = (array.length > 0) && (array[0].getString() != null);
/*  747 */       for (int i = 0; i < array2.length; i++) {
/*  748 */         if (i < array.length) {
/*  749 */           array2[i] = array[i];
/*      */         } else {
/*  751 */           array2[i] = new Variable((0.0D / 0.0D));
/*  752 */           if (strings)
/*  753 */             array2[i].setString("undefined");
/*      */         }
/*      */       }
/*  756 */       v.setArray(array2);
/*  757 */       v.setArraySize(index + 1);
/*  758 */       array = v.getArray();
/*      */     }
/*  760 */     int size = v.getArraySize();
/*  761 */     if (index + 1 > size)
/*  762 */       v.setArraySize(index + 1);
/*  763 */     int next = nextToken();
/*  764 */     switch (expressionType) {
/*      */     case 2:
/*  766 */       array[index].setString(getString());
/*  767 */       break;
/*      */     case 1:
/*  769 */       getToken();
/*  770 */       if (this.token == 137)
/*  771 */         array[index].setArray(this.func.getArrayFunction(this.pgm.table[this.tokenAddress].type)); break;
/*      */     default:
/*  774 */       switch (op) { case 61:
/*  775 */         array[index].setValue(getExpression()); break;
/*      */       case 9:
/*  776 */         array[index].setValue(array[index].getValue() + getExpression()); break;
/*      */       case 10:
/*  777 */         array[index].setValue(array[index].getValue() - getExpression()); break;
/*      */       case 11:
/*  778 */         array[index].setValue(array[index].getValue() * getExpression()); break;
/*      */       case 12:
/*  779 */         array[index].setValue(array[index].getValue() / getExpression());
/*      */       }
/*      */       break;
/*      */     }
/*      */   }
/*      */ 
/*      */   final void doUserFunctionAssignment()
/*      */   {
/*  787 */     putTokenBack();
/*  788 */     int savePC = this.pc;
/*  789 */     getToken();
/*  790 */     getToken();
/*  791 */     getToken();
/*  792 */     boolean simpleAssignment = isSimpleFunctionCall(true);
/*  793 */     this.pc = savePC;
/*  794 */     if (!simpleAssignment) {
/*  795 */       getAssignmentExpression();
/*      */     } else {
/*  797 */       getToken();
/*  798 */       Variable v1 = lookupLocalVariable(this.tokenAddress);
/*  799 */       if (v1 == null)
/*  800 */         v1 = push(this.tokenAddress, 0.0D, null, this);
/*  801 */       getToken();
/*  802 */       if (this.token != 61)
/*  803 */         error("'=' expected");
/*  804 */       getToken();
/*  805 */       Variable v2 = runUserFunction();
/*  806 */       if (v2 == null)
/*  807 */         error("No return value");
/*  808 */       if (this.done) return;
/*  809 */       int type = v2.getType();
/*  810 */       if (type == 0) {
/*  811 */         v1.setValue(v2.getValue());
/*  812 */       } else if (type == 1) {
/*  813 */         v1.setArray(v2.getArray());
/*  814 */         v1.setArraySize(v2.getArraySize());
/*      */       } else {
/*  816 */         v1.setString(v2.getString());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*  821 */   boolean isSimpleFunctionCall(boolean assignment) { int count = 0;
/*      */     do {
/*  823 */       getToken();
/*      */ 
/*  825 */       if (this.token == 40)
/*  826 */         count++;
/*  827 */       else if (this.token == 41)
/*  828 */         count--;
/*  829 */       else if (this.done)
/*  830 */         error("')' expected"); 
/*      */     }
/*  831 */     while (count > 0);
/*  832 */     getToken();
/*  833 */     if (assignment) {
/*  834 */       return this.token == 59;
/*      */     }
/*  836 */     return (this.token == 44) || (this.token == 41); }
/*      */ 
/*      */   final void doStringAssignment()
/*      */   {
/*  840 */     Variable v = lookupLocalVariable(this.tokenAddress);
/*  841 */     if (v == null) {
/*  842 */       if (nextToken() == 61)
/*  843 */         v = push(this.tokenAddress, 0.0D, null, this);
/*      */       else
/*  845 */         error("Undefined identifier");
/*      */     }
/*  847 */     getToken();
/*  848 */     if (this.token == 61)
/*  849 */       v.setString(getString());
/*  850 */     else if (this.token == 9)
/*  851 */       v.setString(v.getString() + getString());
/*      */     else
/*  853 */       error("'=' or '+=' expected");
/*      */   }
/*      */ 
/*      */   final void doArrayAssignment() {
/*  857 */     Variable v = lookupLocalVariable(this.tokenAddress);
/*  858 */     if (v == null) {
/*  859 */       if (nextToken() == 61)
/*  860 */         v = push(this.tokenAddress, 0.0D, null, this);
/*      */       else
/*  862 */         error("Undefined identifier");
/*      */     }
/*  864 */     getToken();
/*  865 */     if (this.token != 61) {
/*  866 */       error("'=' expected");
/*  867 */       return;
/*      */     }
/*  869 */     getToken();
/*  870 */     if (this.token == 137) {
/*  871 */       v.setArray(this.func.getArrayFunction(this.pgm.table[this.tokenAddress].type));
/*  872 */     } else if (this.token == 129) {
/*  873 */       Variable v2 = lookupVariable();
/*  874 */       v.setArray(v2.getArray());
/*      */     } else {
/*  876 */       error("Array expected");
/*      */     }
/*      */   }
/*      */ 
/*  880 */   final void doIf() { this.looseSyntax = false;
/*  881 */     boolean b = getBoolean();
/*  882 */     if (b)
/*  883 */       doStatement();
/*      */     else
/*  885 */       skipStatement();
/*  886 */     int next = nextToken();
/*  887 */     if (next == 59) {
/*  888 */       getToken();
/*  889 */       next = nextToken();
/*      */     }
/*  891 */     if (next == 203) {
/*  892 */       getToken();
/*  893 */       if (b)
/*  894 */         skipStatement();
/*      */       else
/*  896 */         doStatement();
/*      */     } }
/*      */ 
/*      */   final boolean getBoolean()
/*      */   {
/*  901 */     getLeftParen();
/*  902 */     double value = getLogicalExpression();
/*  903 */     checkBoolean(value);
/*  904 */     getRightParen();
/*  905 */     return value != 0.0D;
/*      */   }
/*      */ 
/*      */   final double getLogicalExpression() {
/*  909 */     double v1 = getBooleanExpression();
/*  910 */     int next = nextToken();
/*  911 */     if ((next != 13) && (next != 14))
/*  912 */       return v1;
/*  913 */     checkBoolean(v1);
/*  914 */     getToken();
/*  915 */     int op = this.token;
/*  916 */     double v2 = getLogicalExpression();
/*  917 */     checkBoolean(v2);
/*  918 */     if (op == 13)
/*  919 */       return (int)v1 & (int)v2;
/*  920 */     if (op == 14)
/*  921 */       return (int)v1 | (int)v2;
/*  922 */     return v1;
/*      */   }
/*      */ 
/*      */   final double getBooleanExpression() {
/*  926 */     double v1 = 0.0D;
/*  927 */     String s1 = null;
/*  928 */     int next = this.pgm.code[(this.pc + 1)];
/*  929 */     int tok = next & 0xFFF;
/*  930 */     if ((tok == 133) || (tok == 136) || (isString(next)))
/*  931 */       s1 = getString();
/*      */     else
/*  933 */       v1 = getExpression();
/*  934 */     next = nextToken();
/*  935 */     if ((next >= 3) && (next <= 8)) {
/*  936 */       getToken();
/*  937 */       int op = this.token;
/*  938 */       if (s1 != null)
/*  939 */         return compareStrings(s1, getString(), op);
/*  940 */       double v2 = getExpression();
/*  941 */       switch (op) {
/*      */       case 3:
/*  943 */         v1 = v1 == v2 ? 1.0D : 0.0D;
/*  944 */         break;
/*      */       case 4:
/*  946 */         v1 = v1 != v2 ? 1.0D : 0.0D;
/*  947 */         break;
/*      */       case 5:
/*  949 */         v1 = v1 > v2 ? 1.0D : 0.0D;
/*  950 */         break;
/*      */       case 6:
/*  952 */         v1 = v1 >= v2 ? 1.0D : 0.0D;
/*  953 */         break;
/*      */       case 7:
/*  955 */         v1 = v1 < v2 ? 1.0D : 0.0D;
/*  956 */         break;
/*      */       case 8:
/*  958 */         v1 = v1 <= v2 ? 1.0D : 0.0D;
/*      */       }
/*      */     }
/*  961 */     else if (s1 != null) {
/*  962 */       v1 = Tools.parseDouble(s1, 0.0D);
/*  963 */     }return v1;
/*      */   }
/*      */ 
/*      */   boolean isString(int token)
/*      */   {
/*  968 */     if ((token & 0xFFF) != 129) return false;
/*  969 */     Variable v = lookupVariable(token >> 12);
/*  970 */     if (v == null) return false;
/*  971 */     if (this.pgm.code[(this.pc + 2)] == 91) {
/*  972 */       Variable[] array = v.getArray();
/*  973 */       if ((array != null) && (array.length > 0))
/*  974 */         return array[0].getType() == 2;
/*      */     }
/*  976 */     return v.getType() == 2;
/*      */   }
/*      */ 
/*      */   double compareStrings(String s1, String s2, int op)
/*      */   {
/*  981 */     int result = s1.compareToIgnoreCase(s2);
/*  982 */     double v1 = 0.0D;
/*  983 */     switch (op) {
/*      */     case 3:
/*  985 */       v1 = result == 0 ? 1.0D : 0.0D;
/*  986 */       break;
/*      */     case 4:
/*  988 */       v1 = result != 0 ? 1.0D : 0.0D;
/*  989 */       break;
/*      */     case 5:
/*  991 */       v1 = result > 0 ? 1.0D : 0.0D;
/*  992 */       break;
/*      */     case 6:
/*  994 */       v1 = result >= 0 ? 1.0D : 0.0D;
/*  995 */       break;
/*      */     case 7:
/*  997 */       v1 = result < 0 ? 1.0D : 0.0D;
/*  998 */       break;
/*      */     case 8:
/* 1000 */       v1 = result <= 0 ? 1.0D : 0.0D;
/*      */     }
/*      */ 
/* 1003 */     return v1;
/*      */   }
/*      */ 
/*      */   final double getAssignmentExpression() {
/* 1007 */     int tokPlus2 = this.pgm.code[(this.pc + 2)];
/* 1008 */     if (((this.pgm.code[(this.pc + 1)] & 0xFF) == 129) && ((tokPlus2 == 61) || (tokPlus2 == 9) || (tokPlus2 == 10) || (tokPlus2 == 11) || (tokPlus2 == 12)))
/*      */     {
/* 1010 */       getToken();
/* 1011 */       Variable v = lookupLocalVariable(this.tokenAddress);
/* 1012 */       if (v == null)
/* 1013 */         v = push(this.tokenAddress, 0.0D, null, this);
/* 1014 */       getToken();
/* 1015 */       double value = 0.0D;
/* 1016 */       if (this.token == 61) {
/* 1017 */         value = getAssignmentExpression();
/*      */       } else {
/* 1019 */         value = v.getValue();
/* 1020 */         switch (this.token) { case 9:
/* 1021 */           value += getAssignmentExpression(); break;
/*      */         case 10:
/* 1022 */           value -= getAssignmentExpression(); break;
/*      */         case 11:
/* 1023 */           value *= getAssignmentExpression(); break;
/*      */         case 12:
/* 1024 */           value /= getAssignmentExpression();
/*      */         }
/*      */       }
/* 1027 */       v.setValue(value);
/* 1028 */       return value;
/*      */     }
/* 1030 */     return getLogicalExpression();
/*      */   }
/*      */ 
/*      */   final void checkBoolean(double value) {
/* 1034 */     if ((value != 0.0D) && (value != 1.0D))
/* 1035 */       error("Boolean expression expected");
/*      */   }
/*      */ 
/*      */   void doVar() {
/* 1039 */     getToken();
/* 1040 */     while (this.token == 129) {
/* 1041 */       if (nextToken() == 61) {
/* 1042 */         doAssignment();
/*      */       } else {
/* 1044 */         Variable v = lookupVariable(this.tokenAddress);
/* 1045 */         if (v == null)
/* 1046 */           push(this.tokenAddress, 0.0D, null, this);
/*      */       }
/* 1048 */       getToken();
/* 1049 */       if (this.token == 44)
/* 1050 */         getToken();
/*      */       else
/* 1052 */         putTokenBack();
/*      */     }
/*      */   }
/*      */ 
/*      */   final void getLeftParen()
/*      */   {
/* 1059 */     getToken();
/* 1060 */     if (this.token != 40)
/* 1061 */       error("'(' expected");
/*      */   }
/*      */ 
/*      */   final void getRightParen() {
/* 1065 */     getToken();
/* 1066 */     if (this.token != 41)
/* 1067 */       error("')' expected");
/*      */   }
/*      */ 
/*      */   final void getParens() {
/* 1071 */     if (nextToken() == 40) {
/* 1072 */       getLeftParen();
/* 1073 */       getRightParen();
/*      */     }
/*      */   }
/*      */ 
/*      */   final void getComma() {
/* 1078 */     getToken();
/* 1079 */     if (this.token != 44)
/* 1080 */       if (this.looseSyntax)
/* 1081 */         putTokenBack();
/*      */       else
/* 1083 */         error("',' expected");
/*      */   }
/*      */ 
/*      */   void error(String message)
/*      */   {
/* 1088 */     boolean showMessage = !this.done;
/* 1089 */     String[] variables = showMessage ? getVariables() : null;
/* 1090 */     this.token = 128;
/* 1091 */     this.tokenString = "";
/* 1092 */     IJ.showStatus("");
/* 1093 */     IJ.showProgress(0, 0);
/* 1094 */     batchMode = false;
/* 1095 */     imageTable = null;
/* 1096 */     WindowManager.setTempCurrentImage(null);
/* 1097 */     this.wasError = true;
/* 1098 */     instance = null;
/* 1099 */     if (showMessage) {
/* 1100 */       String line = getErrorLine();
/* 1101 */       this.done = true;
/* 1102 */       if (line.length() > 120)
/* 1103 */         line = line.substring(0, 119) + "...";
/* 1104 */       showError("Macro Error", message + " in line " + this.lineNumber + ".\n \n" + line, variables);
/* 1105 */       throw new RuntimeException("Macro canceled");
/*      */     }
/* 1107 */     this.done = true;
/*      */   }
/*      */ 
/*      */   void showError(String title, String msg, String[] variables) {
/* 1111 */     GenericDialog gd = new GenericDialog(title);
/* 1112 */     gd.setInsets(6, 5, 0);
/* 1113 */     gd.addMessage(msg);
/* 1114 */     gd.setInsets(15, 30, 5);
/* 1115 */     gd.addCheckbox("Show \"Debug\" Window", showVariables);
/* 1116 */     gd.hideCancelButton();
/* 1117 */     gd.showDialog();
/* 1118 */     showVariables = gd.getNextBoolean();
/* 1119 */     if ((!gd.wasCanceled()) && (showVariables))
/* 1120 */       updateDebugWindow(variables, null);
/*      */   }
/*      */ 
/*      */   public TextWindow updateDebugWindow(String[] variables, TextWindow debugWindow) {
/* 1124 */     if (debugWindow == null) {
/* 1125 */       Frame f = WindowManager.getFrame("Debug");
/* 1126 */       if ((f != null) && ((f instanceof TextWindow))) {
/* 1127 */         debugWindow = (TextWindow)f;
/* 1128 */         debugWindow.toFront();
/*      */       }
/*      */     }
/* 1131 */     if (debugWindow == null)
/* 1132 */       debugWindow = new TextWindow("Debug", "Name\tValue", "", 300, 400);
/* 1133 */     TextPanel panel = debugWindow.getTextPanel();
/* 1134 */     int n = variables.length;
/* 1135 */     if (n == 0) {
/* 1136 */       panel.clear();
/* 1137 */       return debugWindow;
/*      */     }
/* 1139 */     int lines = panel.getLineCount();
/* 1140 */     for (int i = 0; i < lines; i++) {
/* 1141 */       if (i < n)
/* 1142 */         panel.setLine(i, variables[i]);
/*      */       else
/* 1144 */         panel.setLine(i, "");
/*      */     }
/* 1146 */     for (int i = lines; i < n; i++)
/* 1147 */       debugWindow.append(variables[i]);
/* 1148 */     return debugWindow;
/*      */   }
/*      */ 
/*      */   String getErrorLine()
/*      */   {
/* 1153 */     int savePC = this.pc;
/* 1154 */     this.lineNumber = this.pgm.lineNumbers[this.pc];
/* 1155 */     while ((this.pc >= 0) && (this.lineNumber == this.pgm.lineNumbers[this.pc]))
/* 1156 */       this.pc -= 1;
/* 1157 */     if (this.lineNumber <= 1)
/* 1158 */       this.pc = -1;
/* 1159 */     String line = "";
/* 1160 */     getToken();
/* 1161 */     while ((!this.done) && (this.lineNumber == this.pgm.lineNumbers[this.pc])) {
/* 1162 */       String str = this.pgm.decodeToken(this.token, this.tokenAddress);
/* 1163 */       if (this.pc == savePC)
/* 1164 */         str = "<" + str + ">";
/* 1165 */       line = line + str + " ";
/* 1166 */       getToken();
/*      */     }
/* 1168 */     return line;
/*      */   }
/*      */ 
/*      */   final String getString() {
/* 1172 */     String str = getStringTerm();
/*      */     while (true) {
/* 1174 */       getToken();
/* 1175 */       if (this.token != 43) break;
/* 1176 */       str = str + getStringTerm();
/*      */     }
/* 1178 */     putTokenBack();
/*      */ 
/* 1182 */     return str;
/*      */   }
/*      */ 
/*      */   final String getStringTerm()
/*      */   {
/* 1187 */     getToken();
/*      */     String str;
/*      */     String str;
/* 1188 */     switch (this.token) {
/*      */     case 133:
/* 1190 */       str = this.tokenString;
/* 1191 */       break;
/*      */     case 136:
/* 1193 */       str = this.func.getStringFunction(this.pgm.table[this.tokenAddress].type);
/* 1194 */       break;
/*      */     case 138:
/* 1196 */       Variable v = runUserFunction();
/* 1197 */       if (v == null)
/* 1198 */         error("No return value");
/* 1199 */       str = v.getString();
/* 1200 */       if (str == null) {
/* 1201 */         double value = v.getValue();
/* 1202 */         if ((int)value == value)
/* 1203 */           str = IJ.d2s(value, 0);
/*      */         else
/* 1205 */           str = "" + value; 
/*      */       }
/* 1206 */       break;
/*      */     case 129:
/* 1209 */       str = lookupStringVariable();
/* 1210 */       if (str != null) break; case 130:
/*      */     case 131:
/*      */     case 132:
/*      */     case 134:
/*      */     case 135:
/*      */     case 137:
/*      */     default:
/* 1214 */       putTokenBack();
/* 1215 */       double value = getStringExpression();
/* 1216 */       if ((int)value == value) {
/* 1217 */         str = IJ.d2s(value, 0);
/*      */       } else {
/* 1219 */         str = "" + value;
/* 1220 */         if ((this.inPrint) && (value != (1.0D / 0.0D)) && (value != (-1.0D / 0.0D)) && (value != (0.0D / 0.0D)) && (str.length() - str.indexOf('.') > 6) && (str.indexOf('E') == -1))
/*      */         {
/* 1222 */           str = IJ.d2s(value, 4); } 
/*      */       }break;
/*      */     }
/* 1225 */     return str;
/*      */   }
/*      */ 
/*      */   final boolean isStringFunction() {
/* 1229 */     Symbol symbol = this.pgm.table[this.tokenAddress];
/* 1230 */     return symbol.type == 2000;
/*      */   }
/*      */ 
/*      */   final double getExpression() {
/* 1234 */     double value = getTerm();
/*      */     while (true)
/*      */     {
/* 1237 */       int next = nextToken();
/* 1238 */       if (next == 43) {
/* 1239 */         getToken();
/* 1240 */         value += getTerm(); } else {
/* 1241 */         if (next != 45) break;
/* 1242 */         getToken();
/* 1243 */         value -= getTerm();
/*      */       }
/*      */     }
/*      */ 
/* 1247 */     return value;
/*      */   }
/*      */ 
/*      */   final double getTerm() {
/* 1251 */     double value = getFactor();
/* 1252 */     boolean done = false;
/*      */ 
/* 1254 */     while (!done) {
/* 1255 */       int next = nextToken();
/* 1256 */       switch (next) { case 42:
/* 1257 */         getToken(); value *= getFactor(); break;
/*      */       case 47:
/* 1258 */         getToken(); value /= getFactor(); break;
/*      */       case 37:
/* 1259 */         getToken(); value %= getFactor(); break;
/*      */       case 38:
/* 1260 */         getToken(); value = (int)value & (int)getFactor(); break;
/*      */       case 124:
/* 1261 */         getToken(); value = (int)value | (int)getFactor(); break;
/*      */       case 94:
/* 1262 */         getToken(); value = (int)value ^ (int)getFactor(); break;
/*      */       case 15:
/* 1263 */         getToken(); value = (int)value >> (int)getFactor(); break;
/*      */       case 16:
/* 1264 */         getToken(); value = (int)value << (int)getFactor(); break;
/*      */       default:
/* 1265 */         done = true;
/*      */       }
/*      */     }
/* 1268 */     return value;
/*      */   }
/*      */ 
/*      */   final double getFactor() {
/* 1272 */     double value = 0.0D;
/* 1273 */     Variable v = null;
/* 1274 */     getToken();
/* 1275 */     switch (this.token) {
/*      */     case 130:
/* 1277 */       value = this.tokenValue;
/* 1278 */       break;
/*      */     case 135:
/* 1280 */       value = this.func.getFunctionValue(this.pgm.table[this.tokenAddress].type);
/* 1281 */       break;
/*      */     case 136:
/* 1283 */       String str = this.func.getStringFunction(this.pgm.table[this.tokenAddress].type);
/* 1284 */       value = Tools.parseDouble(str);
/* 1285 */       if ("NaN".equals(str))
/* 1286 */         value = (0.0D / 0.0D);
/* 1287 */       else if (Double.isNaN(value))
/* 1288 */         error("Numeric value expected"); break;
/*      */     case 138:
/* 1291 */       v = runUserFunction();
/* 1292 */       if (v == null)
/* 1293 */         error("No return value");
/* 1294 */       if (this.done) {
/* 1295 */         value = 0.0D;
/*      */       }
/* 1297 */       else if (v.getString() != null)
/* 1298 */         error("Numeric return value expected");
/*      */       else {
/* 1300 */         value = v.getValue();
/*      */       }
/* 1302 */       break;
/*      */     case 209:
/* 1303 */       value = 1.0D; break;
/*      */     case 210:
/* 1304 */       value = 0.0D; break;
/*      */     case 211:
/* 1305 */       value = 3.141592653589793D; break;
/*      */     case 212:
/* 1306 */       value = (0.0D / 0.0D); break;
/*      */     case 129:
/* 1308 */       v = lookupVariable();
/* 1309 */       if (v == null)
/* 1310 */         return 0.0D;
/* 1311 */       int next = nextToken();
/* 1312 */       if (next == 91) {
/* 1313 */         v = getArrayElement(v);
/* 1314 */         value = v.getValue();
/* 1315 */         next = nextToken();
/* 1316 */       } else if (next == 46) {
/* 1317 */         value = getArrayLength(v);
/* 1318 */         next = nextToken();
/*      */       } else {
/* 1320 */         if ((this.prefixValue != 0) && (!this.checkingType)) {
/* 1321 */           v.setValue(v.getValue() + this.prefixValue);
/* 1322 */           this.prefixValue = 0;
/*      */         }
/* 1324 */         value = v.getValue();
/*      */       }
/* 1326 */       if ((next == 1) || (next == 2))
/*      */       {
/* 1328 */         getToken();
/* 1329 */         if (this.token == 1)
/* 1330 */           v.setValue(v.getValue() + (this.checkingType ? 0 : 1));
/*      */         else
/* 1332 */           v.setValue(v.getValue() - (this.checkingType ? 0 : 1)); 
/*      */       }
/* 1333 */       break;
/*      */     case 40:
/* 1335 */       value = getLogicalExpression();
/* 1336 */       getRightParen();
/* 1337 */       break;
/*      */     case 1:
/* 1339 */       this.prefixValue = 1;
/* 1340 */       value = getFactor();
/* 1341 */       break;
/*      */     case 2:
/* 1343 */       this.prefixValue = -1;
/* 1344 */       value = getFactor();
/* 1345 */       break;
/*      */     case 33:
/* 1347 */       value = getFactor();
/* 1348 */       if ((value == 0.0D) || (value == 1.0D))
/* 1349 */         value = value == 0.0D ? 1.0D : 0.0D;
/*      */       else
/* 1351 */         error("Boolean expected");
/* 1352 */       break;
/*      */     case 45:
/* 1354 */       value = -getFactor();
/* 1355 */       break;
/*      */     case 126:
/* 1357 */       value = (int)getFactor() ^ 0xFFFFFFFF;
/* 1358 */       break;
/*      */     default:
/* 1360 */       error("Number or numeric function expected");
/*      */     }
/*      */ 
/* 1363 */     return value;
/*      */   }
/*      */ 
/*      */   final Variable getArrayElement(Variable v) {
/* 1367 */     int index = getIndex();
/* 1368 */     Variable[] array = v.getArray();
/* 1369 */     if (array == null)
/* 1370 */       error("Array expected");
/* 1371 */     if ((index < 0) || (index >= array.length))
/* 1372 */       error("Index (" + index + ") out of 0-" + (array.length - 1) + " range");
/* 1373 */     return array[index];
/*      */   }
/*      */ 
/*      */   final double getArrayLength(Variable v) {
/* 1377 */     getToken();
/* 1378 */     getToken();
/* 1379 */     if ((this.token != 129) || (!this.tokenString.equals("length")))
/* 1380 */       error("'length' expected");
/* 1381 */     if (v.getArray() == null)
/* 1382 */       error("Array expected");
/* 1383 */     return v.getArraySize();
/*      */   }
/*      */ 
/*      */   final double getStringExpression() {
/* 1387 */     double value = getTerm();
/*      */     while (true) {
/* 1389 */       getToken();
/* 1390 */       if (this.token == 43) {
/* 1391 */         getToken();
/* 1392 */         if ((this.token == 133) || (this.token == 136)) {
/* 1393 */           putTokenBack();
/* 1394 */           putTokenBack();
/* 1395 */           break label134;
/*      */         }
/* 1397 */         if (this.token == 129) {
/* 1398 */           Variable v = lookupVariable(this.tokenAddress);
/* 1399 */           if ((v != null) && (v.getString() != null)) {
/* 1400 */             putTokenBack();
/* 1401 */             putTokenBack();
/* 1402 */             break label134;
/*      */           }
/*      */         }
/* 1405 */         putTokenBack();
/* 1406 */         value += getTerm(); } else {
/* 1407 */         if (this.token != 45) break;
/* 1408 */         value -= getTerm();
/*      */       }
/*      */     }
/* 1410 */     putTokenBack();
/*      */ 
/* 1414 */     label134: return value;
/*      */   }
/*      */ 
/*      */   final Variable lookupLocalVariable(int symTabAddress)
/*      */   {
/* 1421 */     Variable v = null;
/* 1422 */     for (int i = this.topOfStack; i >= this.startOfLocals; i--) {
/* 1423 */       if (this.stack[i].symTabIndex == symTabAddress) {
/* 1424 */         v = this.stack[i];
/* 1425 */         break;
/*      */       }
/*      */     }
/* 1428 */     if (v == null) {
/* 1429 */       for (int i = this.topOfGlobals; i >= 0; i--) {
/* 1430 */         if (this.stack[i].symTabIndex == symTabAddress) {
/* 1431 */           v = this.stack[i];
/* 1432 */           break;
/*      */         }
/*      */       }
/*      */     }
/* 1436 */     return v;
/*      */   }
/*      */ 
/*      */   final Variable lookupVariable(int symTabAddress)
/*      */   {
/* 1441 */     Variable v = null;
/* 1442 */     for (int i = this.topOfStack; i >= 0; i--) {
/* 1443 */       if (this.stack[i].symTabIndex == symTabAddress) {
/* 1444 */         v = this.stack[i];
/* 1445 */         break;
/*      */       }
/*      */     }
/* 1448 */     return v;
/*      */   }
/*      */ 
/*      */   Variable push(Variable var, Interpreter interp) {
/* 1452 */     if (this.stack == null)
/* 1453 */       this.stack = new Variable[1000];
/* 1454 */     if (this.topOfStack >= 998)
/* 1455 */       interp.error("Stack overflow");
/*      */     else
/* 1457 */       this.topOfStack += 1;
/* 1458 */     this.stack[this.topOfStack] = var;
/* 1459 */     return var;
/*      */   }
/*      */ 
/*      */   void pushGlobals() {
/* 1463 */     if (this.pgm.globals == null)
/* 1464 */       return;
/* 1465 */     if (this.stack == null)
/* 1466 */       this.stack = new Variable[1000];
/* 1467 */     for (int i = 0; i < this.pgm.globals.length; i++) {
/* 1468 */       this.topOfStack += 1;
/* 1469 */       this.stack[this.topOfStack] = this.pgm.globals[i];
/*      */     }
/* 1471 */     this.topOfGlobals = this.topOfStack;
/*      */   }
/*      */ 
/*      */   Variable push(int symTabLoc, double value, String str, Interpreter interp)
/*      */   {
/* 1476 */     Variable var = new Variable(symTabLoc, value, str);
/* 1477 */     if (this.stack == null)
/* 1478 */       this.stack = new Variable[1000];
/* 1479 */     if (this.topOfStack >= 998)
/* 1480 */       interp.error("Stack overflow");
/*      */     else
/* 1482 */       this.topOfStack += 1;
/* 1483 */     this.stack[this.topOfStack] = var;
/* 1484 */     return var;
/*      */   }
/*      */ 
/*      */   void trimStack(int previousTOS, int previousStartOfLocals) {
/* 1488 */     for (int i = previousTOS + 1; i <= this.topOfStack; i++)
/* 1489 */       this.stack[i] = null;
/* 1490 */     this.topOfStack = previousTOS;
/* 1491 */     this.startOfLocals = previousStartOfLocals;
/*      */   }
/*      */ 
/*      */   final Variable lookupVariable()
/*      */   {
/* 1498 */     Variable v = null;
/* 1499 */     if (this.stack == null) {
/* 1500 */       undefined();
/* 1501 */       return v;
/*      */     }
/* 1503 */     boolean found = false;
/* 1504 */     for (int i = this.topOfStack; i >= 0; i--) {
/* 1505 */       v = this.stack[i];
/*      */ 
/* 1507 */       if (v.symTabIndex == this.tokenAddress) {
/* 1508 */         found = true;
/* 1509 */         break;
/*      */       }
/*      */     }
/* 1512 */     if (!found)
/* 1513 */       undefined();
/* 1514 */     return v;
/*      */   }
/*      */ 
/*      */   final String lookupStringVariable() {
/* 1518 */     if (this.stack == null) {
/* 1519 */       undefined();
/* 1520 */       return "";
/*      */     }
/* 1522 */     boolean found = false;
/* 1523 */     String str = null;
/* 1524 */     for (int i = this.topOfStack; i >= 0; i--) {
/* 1525 */       if (this.stack[i].symTabIndex == this.tokenAddress) {
/* 1526 */         Variable v = this.stack[i];
/* 1527 */         found = true;
/* 1528 */         int next = nextToken();
/* 1529 */         if (next == 91) {
/* 1530 */           int savePC = this.pc;
/* 1531 */           int index = getIndex();
/* 1532 */           Variable[] array = v.getArray();
/* 1533 */           if (array == null)
/* 1534 */             error("Array expected");
/* 1535 */           if ((index < 0) || (index >= array.length))
/* 1536 */             error("Index (" + index + ") out of 0-" + (array.length - 1) + " range");
/* 1537 */           str = array[index].getString();
/* 1538 */           if (str == null) {
/* 1539 */             this.pc = (savePC - 1);
/* 1540 */             getToken();
/*      */           }
/* 1542 */           break; } if (next == 46) {
/* 1543 */           str = null; break;
/*      */         }
/* 1545 */         if (v.getArray() != null) {
/* 1546 */           getToken(); error("'[' or '.' expected");
/* 1547 */         }str = v.getString();
/*      */ 
/* 1549 */         break;
/*      */       }
/*      */     }
/* 1552 */     if (!found)
/* 1553 */       undefined();
/* 1554 */     return str;
/*      */   }
/*      */ 
/*      */   int getIndex() {
/* 1558 */     getToken();
/* 1559 */     if (this.token != 91)
/* 1560 */       error("'['expected");
/* 1561 */     int index = (int)getExpression();
/* 1562 */     getToken();
/* 1563 */     if (this.token != 93)
/* 1564 */       error("']' expected");
/* 1565 */     return index;
/*      */   }
/*      */ 
/*      */   void undefined() {
/* 1569 */     if (nextToken() == 40)
/* 1570 */       error("Undefined identifier");
/*      */     else
/* 1572 */       error("Undefined variable");
/*      */   }
/*      */ 
/*      */   void dump() {
/* 1576 */     getParens();
/* 1577 */     if (!this.done) {
/* 1578 */       this.pgm.dumpSymbolTable();
/* 1579 */       this.pgm.dumpProgram();
/* 1580 */       dumpStack();
/*      */     }
/*      */   }
/*      */ 
/*      */   void dumpStack() {
/* 1585 */     IJ.log("");
/* 1586 */     IJ.log("Stack");
/* 1587 */     if (this.stack != null)
/* 1588 */       for (int i = this.topOfStack; i >= 0; i--)
/* 1589 */         IJ.log(i + " " + this.pgm.table[this.stack[i].symTabIndex].str + " " + this.stack[i]);
/*      */   }
/*      */ 
/*      */   void finishUp() {
/* 1593 */     this.func.updateDisplay();
/* 1594 */     instance = null;
/* 1595 */     if (!this.calledMacro) {
/* 1596 */       if (batchMode) this.showingProgress = true;
/* 1597 */       batchMode = false;
/* 1598 */       imageTable = null;
/* 1599 */       WindowManager.setTempCurrentImage(null);
/*      */     }
/* 1601 */     if (this.func.plot != null) {
/* 1602 */       this.func.plot.show();
/* 1603 */       this.func.plot = null;
/*      */     }
/* 1605 */     if (this.showingProgress)
/* 1606 */       IJ.showProgress(0, 0);
/* 1607 */     if (this.keysSet) {
/* 1608 */       IJ.setKeyUp(18);
/* 1609 */       IJ.setKeyUp(16);
/* 1610 */       IJ.setKeyUp(32);
/*      */     }
/* 1612 */     if (this.rgbWeights != null)
/* 1613 */       ColorProcessor.setWeightingFactors(this.rgbWeights[0], this.rgbWeights[1], this.rgbWeights[2]);
/* 1614 */     if (this.func.writer != null) this.func.writer.close();
/* 1615 */     this.func.roiManager = null;
/* 1616 */     if (this.func.resultsPending) {
/* 1617 */       ResultsTable rt = ResultsTable.getResultsTable();
/* 1618 */       if ((rt != null) && (rt.getCounter() > 0)) rt.show("Results");
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void abort()
/*      */   {
/* 1624 */     if (instance != null)
/* 1625 */       instance.abortMacro();
/*      */   }
/*      */ 
/*      */   static void abortPrevious()
/*      */   {
/* 1630 */     if (previousInstance != null) {
/* 1631 */       previousInstance.abortMacro();
/* 1632 */       IJ.beep();
/* 1633 */       previousInstance = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void abort(Interpreter interp)
/*      */   {
/* 1639 */     if (interp != null)
/* 1640 */       interp.abortMacro();
/*      */   }
/*      */ 
/*      */   public void abortMacro()
/*      */   {
/* 1645 */     if (!this.calledMacro) {
/* 1646 */       batchMode = false;
/* 1647 */       imageTable = null;
/*      */     }
/* 1649 */     this.done = true;
/* 1650 */     if ((this.func != null) && ((this.macroName == null) || (this.macroName.indexOf(" Tool") == -1)))
/* 1651 */       this.func.abortDialog();
/* 1652 */     IJ.showStatus("Macro aborted");
/*      */   }
/*      */ 
/*      */   public static Interpreter getInstance() {
/* 1656 */     return instance;
/*      */   }
/*      */ 
/*      */   static void setBatchMode(boolean b)
/*      */   {
/* 1664 */     batchMode = b;
/* 1665 */     if (!b) imageTable = null; 
/*      */   }
/*      */ 
/*      */   public static boolean isBatchMode()
/*      */   {
/* 1669 */     return batchMode;
/*      */   }
/*      */ 
/*      */   public static void addBatchModeImage(ImagePlus imp) {
/* 1673 */     if ((!batchMode) || (imp == null)) return;
/* 1674 */     if (imageTable == null) {
/* 1675 */       imageTable = new Vector();
/*      */     }
/* 1677 */     imageTable.addElement(imp);
/*      */   }
/*      */ 
/*      */   public static void removeBatchModeImage(ImagePlus imp) {
/* 1681 */     if ((imageTable != null) && (imp != null)) {
/* 1682 */       int index = imageTable.indexOf(imp);
/*      */ 
/* 1684 */       if (index != -1)
/* 1685 */         imageTable.removeElementAt(index);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static int[] getBatchModeImageIDs() {
/* 1690 */     if ((!batchMode) || (imageTable == null))
/* 1691 */       return new int[0];
/* 1692 */     int n = imageTable.size();
/* 1693 */     int[] imageIDs = new int[n];
/* 1694 */     for (int i = 0; i < n; i++) {
/* 1695 */       ImagePlus imp = (ImagePlus)imageTable.elementAt(i);
/* 1696 */       imageIDs[i] = imp.getID();
/*      */     }
/* 1698 */     return imageIDs;
/*      */   }
/*      */ 
/*      */   public static int getBatchModeImageCount() {
/* 1702 */     if ((!batchMode) || (imageTable == null)) {
/* 1703 */       return 0;
/*      */     }
/* 1705 */     return imageTable.size();
/*      */   }
/*      */ 
/*      */   public static ImagePlus getBatchModeImage(int id) {
/* 1709 */     if ((!batchMode) || (imageTable == null))
/* 1710 */       return null;
/* 1711 */     for (Enumeration en = imageTable.elements(); en.hasMoreElements(); ) {
/* 1712 */       ImagePlus imp = (ImagePlus)en.nextElement();
/* 1713 */       if (id == imp.getID())
/* 1714 */         return imp;
/*      */     }
/* 1716 */     return null;
/*      */   }
/*      */ 
/*      */   public static ImagePlus getLastBatchModeImage() {
/* 1720 */     if ((!batchMode) || (imageTable == null)) return null;
/* 1721 */     int size = imageTable.size();
/* 1722 */     if (size == 0) return null;
/* 1723 */     return (ImagePlus)imageTable.elementAt(size - 1);
/*      */   }
/*      */ 
/*      */   public static void setAdditionalFunctions(String functions)
/*      */   {
/* 1728 */     additionalFunctions = functions;
/*      */   }
/*      */ 
/*      */   public static String getAdditionalFunctions() {
/* 1732 */     return additionalFunctions;
/*      */   }
/*      */ 
/*      */   public static RoiManager getBatchModeRoiManager()
/*      */   {
/* 1737 */     Interpreter interp = getInstance();
/* 1738 */     if ((interp != null) && (isBatchMode()) && (RoiManager.getInstance() == null)) {
/* 1739 */       if (interp.func.roiManager == null)
/* 1740 */         interp.func.roiManager = new RoiManager(true);
/* 1741 */       return interp.func.roiManager;
/*      */     }
/* 1743 */     return null;
/*      */   }
/*      */ 
/*      */   public static boolean isBatchModeRoiManager()
/*      */   {
/* 1748 */     Interpreter interp = getInstance();
/* 1749 */     return (interp != null) && (isBatchMode()) && (interp.func.roiManager != null);
/*      */   }
/*      */ 
/*      */   public void setEditor(Editor ed) {
/* 1753 */     if ((ed != null) && (this.editor == null))
/* 1754 */       ed.fixLineEndings();
/* 1755 */     this.editor = ed;
/* 1756 */     if (ed != null)
/* 1757 */       this.debugMode = 1;
/*      */     else
/* 1759 */       this.debugMode = 0;
/*      */   }
/*      */ 
/*      */   public void setDebugMode(int mode) {
/* 1763 */     this.debugMode = mode;
/*      */   }
/*      */ 
/*      */   public int getLineNumber() {
/* 1767 */     return this.pgm.lineNumbers[this.pc];
/*      */   }
/*      */ 
/*      */   public String[] getVariables() {
/* 1771 */     int nImages = WindowManager.getImageCount();
/* 1772 */     if (nImages > 0) this.showDebugFunctions = true;
/* 1773 */     int nFunctions = this.showDebugFunctions ? 3 : 0;
/* 1774 */     String[] variables = new String[this.topOfStack + 1 + nFunctions];
/* 1775 */     if (this.showDebugFunctions) {
/* 1776 */       String title = null;
/* 1777 */       if (nImages > 0) {
/* 1778 */         ImagePlus imp = WindowManager.getCurrentImage();
/* 1779 */         if (imp != null) title = imp.getTitle();
/*      */       }
/* 1781 */       if (this.debugMode == 1) System.gc();
/* 1782 */       variables[0] = ("FreeMemory()\t" + IJ.freeMemory());
/* 1783 */       variables[1] = ("nImages()\t" + nImages);
/* 1784 */       variables[2] = ("getTitle()\t" + (title != null ? "\"" + title + "\"" : ""));
/*      */     }
/*      */ 
/* 1787 */     int index = nFunctions;
/* 1788 */     for (int i = 0; i <= this.topOfStack; i++) {
/* 1789 */       String name = this.pgm.table[this.stack[i].symTabIndex].str;
/* 1790 */       if (i <= this.topOfGlobals)
/* 1791 */         name = name + " (g)";
/* 1792 */       variables[(index++)] = (name + "\t" + this.stack[i]);
/*      */     }
/* 1794 */     return variables;
/*      */   }
/*      */ 
/*      */   public boolean done()
/*      */   {
/* 1799 */     return this.done;
/*      */   }
/*      */ 
/*      */   public Editor getEditor()
/*      */   {
/* 1804 */     return this.editor;
/*      */   }
/*      */ 
/*      */   public boolean wasError()
/*      */   {
/* 1809 */     return this.wasError;
/*      */   }
/*      */ 
/*      */   public void setVariable(String name, double value)
/*      */   {
/* 1814 */     for (int i = 0; i <= this.topOfStack; i++) {
/* 1815 */       int index = this.stack[i].symTabIndex;
/* 1816 */       if (this.pgm.table[index].str.equals(name)) {
/* 1817 */         this.stack[i].setValue(value);
/* 1818 */         break;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public double getVariable(String name)
/*      */   {
/* 1825 */     for (int i = 0; i <= this.topOfStack; i++) {
/* 1826 */       int index = this.stack[i].symTabIndex;
/* 1827 */       if (this.pgm.table[index].str.equals(name))
/* 1828 */         return this.stack[i].getValue();
/*      */     }
/* 1830 */     return (0.0D / 0.0D);
/*      */   }
/*      */ 
/*      */   public double getVariable2(String name)
/*      */   {
/* 1835 */     for (int i = this.topOfStack; i >= 0; i--) {
/* 1836 */       int index = this.stack[i].symTabIndex;
/* 1837 */       if (this.pgm.table[index].str.equals(name))
/* 1838 */         return this.stack[i].getValue();
/*      */     }
/* 1840 */     return (0.0D / 0.0D);
/*      */   }
/*      */ 
/*      */   public String getStringVariable(String name)
/*      */   {
/* 1845 */     for (int i = this.topOfStack; i >= 0; i--) {
/* 1846 */       int index = this.stack[i].symTabIndex;
/* 1847 */       if (this.pgm.table[index].str.equals(name))
/* 1848 */         return this.stack[i].getString();
/*      */     }
/* 1850 */     return null;
/*      */   }
/*      */ 
/*      */   public String getVariableAsString(String name) {
/* 1854 */     String s = getStringVariable(name);
/* 1855 */     if (s == null) {
/* 1856 */       double value = getVariable2(name);
/* 1857 */       if (!Double.isNaN(value)) s = "" + value;
/*      */     }
/* 1859 */     return s;
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.macro.Interpreter
 * JD-Core Version:    0.6.2
 */