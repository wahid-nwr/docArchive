/*     */ package ij.macro;
/*     */ 
/*     */ public class ExtensionDescriptor
/*     */ {
/*     */   public String name;
/*     */   public int[] argTypes;
/*     */   public MacroExtension handler;
/*     */ 
/*     */   public ExtensionDescriptor(String theName, int[] theArgTypes, MacroExtension theHandler)
/*     */   {
/*  11 */     this.name = theName;
/*  12 */     this.argTypes = theArgTypes;
/*  13 */     this.handler = theHandler;
/*     */   }
/*     */ 
/*     */   public static ExtensionDescriptor newDescriptor(String theName, MacroExtension theHandler, int[] types) {
/*  17 */     int[] argTypes = new int[types.length];
/*  18 */     for (int i = 0; i < types.length; i++) {
/*  19 */       argTypes[i] = types[i];
/*     */     }
/*     */ 
/*  22 */     return new ExtensionDescriptor(theName, argTypes, theHandler);
/*     */   }
/*     */ 
/*     */   public static ExtensionDescriptor newDescriptor(String theName, MacroExtension theHandler) {
/*  26 */     return newDescriptor(theName, theHandler, new int[0]);
/*     */   }
/*     */ 
/*     */   public static ExtensionDescriptor newDescriptor(String theName, MacroExtension theHandler, int type) {
/*  30 */     return newDescriptor(theName, theHandler, new int[] { type });
/*     */   }
/*     */ 
/*     */   public static ExtensionDescriptor newDescriptor(String theName, MacroExtension theHandler, int t1, int t2) {
/*  34 */     return newDescriptor(theName, theHandler, new int[] { t1, t2 });
/*     */   }
/*     */ 
/*     */   public static ExtensionDescriptor newDescriptor(String theName, MacroExtension theHandler, int t1, int t2, int t3) {
/*  38 */     return newDescriptor(theName, theHandler, new int[] { t1, t2, t3 });
/*     */   }
/*     */ 
/*     */   public static ExtensionDescriptor newDescriptor(String theName, MacroExtension theHandler, int t1, int t2, int t3, int t4) {
/*  42 */     return newDescriptor(theName, theHandler, new int[] { t1, t2, t3, t4 });
/*     */   }
/*     */ 
/*     */   public static ExtensionDescriptor newDescriptor(String theName, MacroExtension theHandler, Integer[] types) {
/*  46 */     int[] argTypes = new int[types.length];
/*  47 */     for (int i = 0; i < types.length; i++) {
/*  48 */       argTypes[i] = types[i].intValue();
/*     */     }
/*     */ 
/*  51 */     return new ExtensionDescriptor(theName, argTypes, theHandler);
/*     */   }
/*     */ 
/*     */   public static boolean isOptionalArg(int argType) {
/*  55 */     return (argType & 0x20) == 32;
/*     */   }
/*     */ 
/*     */   public static boolean isOutputArg(int argType) {
/*  59 */     return (argType & 0x10) == 16;
/*     */   }
/*     */ 
/*     */   public static int getRawType(int argType) {
/*  63 */     return argType & 0xFFFFFFCF;
/*     */   }
/*     */ 
/*     */   public boolean checkArguments(Object[] args) {
/*  67 */     for (int i = 0; i < this.argTypes.length; i++) {
/*  68 */       boolean optional = isOptionalArg(this.argTypes[i]);
/*  69 */       boolean output = isOutputArg(this.argTypes[i]);
/*     */ 
/*  71 */       int rawType = getRawType(this.argTypes[i]);
/*     */ 
/*  73 */       if (args.length < i) {
/*  74 */         return optional;
/*     */       }
/*  76 */       switch (rawType) {
/*     */       case 1:
/*  78 */         if (output) {
/*  79 */           if (!(args[i] instanceof String[])) return false;
/*     */         }
/*  81 */         else if (!(args[i] instanceof String)) return false;
/*     */ 
/*     */       case 2:
/*  84 */         if (output) {
/*  85 */           if (!(args[i] instanceof Double[])) return false;
/*     */         }
/*  87 */         else if (!(args[i] instanceof Double)) return false;
/*     */ 
/*     */       case 4:
/*  90 */         if (!(args[i] instanceof Object[])) return false; break;
/*     */       case 3:
/*     */       }
/*     */     }
/*  94 */     return true;
/*     */   }
/*     */ 
/*     */   public static String getTypeName(int argType) {
/*  98 */     switch (getRawType(argType)) {
/*     */     case 1:
/* 100 */       return "string";
/*     */     case 2:
/* 102 */       return "number";
/*     */     case 4:
/* 104 */       return "array";
/*     */     case 3:
/* 106 */     }return "unknown";
/*     */   }
/*     */ 
/*     */   private static String getVariableTypename(int type)
/*     */   {
/* 111 */     switch (type) {
/*     */     case 2:
/* 113 */       return "string";
/*     */     case 130:
/* 115 */       return "number";
/*     */     case 1:
/* 117 */       return "array";
/*     */     }
/* 119 */     return "unknown";
/*     */   }
/*     */ 
/*     */   private static Object[] convertArray(Variable[] array)
/*     */   {
/* 125 */     Object[] oArray = new Object[array.length];
/* 126 */     for (int i = 0; i < array.length; i++) {
/* 127 */       switch (array[i].getType()) {
/*     */       case 2:
/* 129 */         oArray[i] = array[i].getString();
/* 130 */         break;
/*     */       case 0:
/* 132 */         oArray[i] = new Double(array[i].getValue());
/* 133 */         break;
/*     */       case 1:
/* 135 */         oArray[i] = convertArray(array[i].getArray());
/* 136 */         break;
/*     */       default:
/* 138 */         oArray[i] = null;
/*     */       }
/*     */     }
/*     */ 
/* 142 */     return oArray;
/*     */   }
/*     */ 
/*     */   Variable[] parseArgumentList(Functions func) {
/* 146 */     Interpreter interp = func.interp;
/*     */ 
/* 148 */     Variable[] vArray = new Variable[this.argTypes.length];
/* 149 */     int i = 0;
/*     */     do {
/* 151 */       if (i >= this.argTypes.length) {
/* 152 */         interp.error("too many arguments (expected " + this.argTypes.length + ")");
/* 153 */         return null;
/*     */       }
/* 155 */       boolean output = isOutputArg(this.argTypes[i]);
/*     */       Variable v;
/*     */       Variable v;
/* 158 */       if (output) {
/* 159 */         v = func.getVariable();
/*     */       } else {
/* 161 */         v = new Variable();
/* 162 */         switch (getRawType(this.argTypes[i])) {
/*     */         case 1:
/* 164 */           v.setString(func.getString());
/* 165 */           break;
/*     */         case 2:
/* 167 */           v.setValue(interp.getExpression());
/* 168 */           break;
/*     */         case 4:
/* 170 */           v.setArray(func.getArray());
/*     */         case 3:
/*     */         }
/*     */       }
/* 174 */       vArray[i] = v;
/* 175 */       i++;
/* 176 */       interp.getToken();
/* 177 */     }while (interp.token == 44);
/*     */ 
/* 179 */     if (interp.token != 41) {
/* 180 */       interp.error("')' expected");
/*     */     }
/* 182 */     if ((i < this.argTypes.length) && (!isOptionalArg(this.argTypes[i]))) {
/* 183 */       interp.error("too few arguments, expected " + this.argTypes.length + " but found " + i);
/*     */     }
/*     */ 
/* 186 */     return vArray;
/*     */   }
/*     */ 
/*     */   public static Object convertVariable(Interpreter interp, int rawType, Variable var)
/*     */   {
/* 191 */     int type = getRawType(rawType);
/* 192 */     boolean output = isOutputArg(rawType);
/*     */ 
/* 194 */     if (var == null) {
/* 195 */       return null;
/*     */     }
/* 197 */     switch (type) {
/*     */     case 1:
/* 199 */       if ((!output) && (var.getType() != 2)) {
/* 200 */         interp.error("Expected string, but variable type is " + getVariableTypename(var.getType()));
/* 201 */         return null;
/*     */       }
/* 203 */       if (output) {
/* 204 */         String s = var.getString();
/* 205 */         if (s == null) s = "";
/* 206 */         return new String[] { s };
/*     */       }
/* 208 */       return var.getString();
/*     */     case 2:
/* 211 */       if (var.getType() != 0) {
/* 212 */         interp.error("Expected number, but variable type is " + getVariableTypename(var.getType()));
/* 213 */         return null;
/*     */       }
/* 215 */       if (output) {
/* 216 */         return new Double[] { new Double(var.getValue()) };
/*     */       }
/* 218 */       return new Double(var.getValue());
/*     */     case 4:
/* 221 */       if (var.getType() != 1) {
/* 222 */         interp.error("Expected array, but variable type is " + getVariableTypename(var.getType()));
/* 223 */         return null;
/*     */       }
/* 225 */       return convertArray(var.getArray());
/*     */     case 3:
/* 227 */     }interp.error("Unknown descriptor type " + type + " (" + rawType + ")");
/* 228 */     return null;
/*     */   }
/*     */ 
/*     */   public static void convertOutputType(Variable variable, Object object)
/*     */   {
/* 233 */     if ((object instanceof String[])) {
/* 234 */       String[] str = (String[])object;
/* 235 */       variable.setString(str[0]);
/* 236 */     } else if ((object instanceof Double[])) {
/* 237 */       Double[] dbl = (Double[])object;
/* 238 */       variable.setValue(dbl[0].doubleValue());
/* 239 */     } else if ((object instanceof Object[])) {
/* 240 */       Object[] arr = (Object[])object;
/* 241 */       Variable[] vArr = new Variable[arr.length];
/* 242 */       for (int i = 0; i < arr.length; i++) {
/* 243 */         vArr[i] = new Variable();
/* 244 */         convertOutputType(vArr[i], arr[i]);
/*     */       }
/* 246 */       variable.setArray(vArr);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String dispatch(Functions func) {
/* 251 */     Interpreter interp = func.interp;
/*     */ 
/* 253 */     if (this.argTypes.length == 0) {
/* 254 */       interp.getParens();
/* 255 */       return this.handler.handleExtension(this.name, null);
/*     */     }
/* 257 */     interp.getLeftParen();
/*     */ 
/* 259 */     Variable[] vArgs = null;
/* 260 */     int next = interp.nextToken();
/* 261 */     if (next != 41) {
/* 262 */       vArgs = parseArgumentList(func);
/*     */     }
/*     */ 
/* 270 */     Object[] args = new Object[this.argTypes.length];
/*     */ 
/* 272 */     for (int i = 0; i < this.argTypes.length; i++) {
/* 273 */       if (i >= vArgs.length) {
/* 274 */         if (isOptionalArg(this.argTypes[i])) break;
/* 275 */         interp.error("expected argument " + (i + 1) + " of type " + getTypeName(this.argTypes[i]));
/* 276 */         return null;
/*     */       }
/*     */ 
/* 281 */       args[i] = convertVariable(interp, this.argTypes[i], vArgs[i]);
/*     */     }
/*     */ 
/* 284 */     String retVal = this.handler.handleExtension(this.name, args);
/* 285 */     for (int i = 0; (i < this.argTypes.length) && 
/* 286 */       (i < vArgs.length); i++)
/*     */     {
/* 287 */       if (isOutputArg(this.argTypes[i])) {
/* 288 */         convertOutputType(vArgs[i], args[i]);
/*     */       }
/*     */     }
/*     */ 
/* 292 */     return retVal;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.macro.ExtensionDescriptor
 * JD-Core Version:    0.6.2
 */