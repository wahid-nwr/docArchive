/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.Macro;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.io.SaveDialog;
/*     */ import ij.plugin.frame.Editor;
/*     */ import ij.text.TextWindow;
/*     */ 
/*     */ public class NewPlugin
/*     */   implements PlugIn
/*     */ {
/*     */   public static final int MACRO = 0;
/*     */   public static final int JAVASCRIPT = 1;
/*     */   public static final int PLUGIN = 2;
/*     */   public static final int PLUGIN_FILTER = 3;
/*     */   public static final int PLUGIN_FRAME = 4;
/*     */   public static final int TEXT_FILE = 5;
/*     */   public static final int TABLE = 6;
/*  13 */   private static int rows = 16;
/*  14 */   private static int columns = 60;
/*  15 */   private static int tableWidth = 350;
/*  16 */   private static int tableHeight = 250;
/*  17 */   private int type = 0;
/*  18 */   private String name = "Macro.txt";
/*     */   private boolean monospaced;
/*  20 */   private boolean menuBar = true;
/*     */   private Editor ed;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  24 */     this.type = -1;
/*  25 */     if ((arg.startsWith("text")) || (arg.equals(""))) {
/*  26 */       this.type = 5;
/*  27 */       this.name = "Untitled.txt";
/*  28 */     } else if (arg.equals("macro")) {
/*  29 */       this.type = 0;
/*  30 */       this.name = "Macro.txt";
/*  31 */     } else if (arg.equals("javascript")) {
/*  32 */       this.type = 1;
/*  33 */       this.name = "Script.js";
/*  34 */     } else if (arg.equals("plugin")) {
/*  35 */       this.type = 2;
/*  36 */       this.name = "My_Plugin.java";
/*  37 */     } else if (arg.equals("frame")) {
/*  38 */       this.type = 4;
/*  39 */       this.name = "Plugin_Frame.java";
/*  40 */     } else if (arg.equals("filter")) {
/*  41 */       this.type = 3;
/*  42 */       this.name = "Filter_Plugin.java";
/*  43 */     } else if (arg.equals("table")) {
/*  44 */       String options = Macro.getOptions();
/*  45 */       if ((IJ.isMacro()) && (options != null) && (options.indexOf("[Text File]") != -1)) {
/*  46 */         this.type = 5;
/*  47 */         this.name = "Untitled.txt";
/*  48 */         arg = "text+dialog";
/*     */       } else {
/*  50 */         this.type = 6;
/*  51 */         this.name = "Table";
/*     */       }
/*     */     }
/*  54 */     this.menuBar = true;
/*  55 */     if (((arg.equals("text+dialog")) || (this.type == 6)) && 
/*  56 */       (!showDialog())) return;
/*     */ 
/*  58 */     if (this.type == -1) {
/*  59 */       createPlugin("Converted_Macro.java", 2, arg);
/*  60 */     } else if ((this.type == 0) || (this.type == 5) || (this.type == 1)) {
/*  61 */       if ((this.type == 5) && (this.name.equals("Macro")))
/*  62 */         this.name = "Untitled.txt";
/*  63 */       createMacro(this.name);
/*  64 */     } else if (this.type == 6) {
/*  65 */       createTable();
/*     */     } else {
/*  67 */       createPlugin(this.name, this.type, arg);
/*     */     }
/*     */   }
/*     */ 
/*  71 */   public void createMacro(String name) { int options = (this.monospaced ? 1 : 0) + (this.menuBar ? 2 : 0);
/*  72 */     this.ed = new Editor(rows, columns, 0, options);
/*  73 */     if ((this.type == 0) && (!name.endsWith(".txt"))) {
/*  74 */       name = SaveDialog.setExtension(name, ".txt");
/*  75 */     } else if ((this.type == 1) && (!name.endsWith(".js"))) {
/*  76 */       if (name.equals("Macro")) name = "script";
/*  77 */       name = SaveDialog.setExtension(name, ".js");
/*     */     }
/*  79 */     this.ed.create(name, ""); }
/*     */ 
/*     */   void createTable()
/*     */   {
/*  83 */     new TextWindow(this.name, "", tableWidth, tableHeight);
/*     */   }
/*     */ 
/*     */   public void createPlugin(String name, int type, String methods) {
/*  87 */     this.ed = ((Editor)IJ.runPlugIn("ij.plugin.frame.Editor", ""));
/*  88 */     if (this.ed == null) return;
/*  89 */     String pluginName = name;
/*  90 */     if ((!name.endsWith(".java")) && (!name.endsWith(".JAVA")))
/*  91 */       name = SaveDialog.setExtension(name, ".java");
/*  92 */     String className = pluginName.substring(0, pluginName.length() - 5);
/*  93 */     String text = "";
/*  94 */     text = text + "import ij.*;\n";
/*  95 */     text = text + "import ij.process.*;\n";
/*  96 */     text = text + "import ij.gui.*;\n";
/*  97 */     text = text + "import java.awt.*;\n";
/*  98 */     switch (type) {
/*     */     case 2:
/* 100 */       text = text + "import ij.plugin.*;\n";
/* 101 */       text = text + "import ij.plugin.frame.*;\n";
/* 102 */       text = text + "\n";
/* 103 */       text = text + "public class " + className + " implements PlugIn {\n";
/* 104 */       text = text + "\n";
/* 105 */       text = text + "\tpublic void run(String arg) {\n";
/* 106 */       if (methods.equals("plugin"))
/* 107 */         text = text + "\t\tIJ.showMessage(\"" + className + "\",\"Hello world!\");\n";
/*     */       else
/* 109 */         text = text + methods;
/* 110 */       text = text + "\t}\n";
/* 111 */       break;
/*     */     case 3:
/* 113 */       text = text + "import ij.plugin.filter.*;\n";
/* 114 */       text = text + "\n";
/* 115 */       text = text + "public class " + className + " implements PlugInFilter {\n";
/* 116 */       text = text + "\tImagePlus imp;\n";
/* 117 */       text = text + "\n";
/* 118 */       text = text + "\tpublic int setup(String arg, ImagePlus imp) {\n";
/* 119 */       text = text + "\t\tthis.imp = imp;\n";
/* 120 */       text = text + "\t\treturn DOES_ALL;\n";
/* 121 */       text = text + "\t}\n";
/* 122 */       text = text + "\n";
/* 123 */       text = text + "\tpublic void run(ImageProcessor ip) {\n";
/* 124 */       text = text + "\t\tip.invert();\n";
/* 125 */       text = text + "\t\timp.updateAndDraw();\n";
/* 126 */       text = text + "\t\tIJ.wait(500);\n";
/* 127 */       text = text + "\t\tip.invert();\n";
/* 128 */       text = text + "\t\timp.updateAndDraw();\n";
/* 129 */       text = text + "\t}\n";
/* 130 */       break;
/*     */     case 4:
/* 132 */       text = text + "import ij.plugin.frame.*;\n";
/* 133 */       text = text + "\n";
/* 134 */       text = text + "public class " + className + " extends PlugInFrame {\n";
/* 135 */       text = text + "\n";
/* 136 */       text = text + "\tpublic " + className + "() {\n";
/* 137 */       text = text + "\t\tsuper(\"" + className + "\");\n";
/* 138 */       text = text + "\t\tTextArea ta = new TextArea(15, 50);\n";
/* 139 */       text = text + "\t\tadd(ta);\n";
/* 140 */       text = text + "\t\tpack();\n";
/* 141 */       text = text + "\t\tGUI.center(this);\n";
/* 142 */       text = text + "\t\tshow();\n";
/* 143 */       text = text + "\t}\n";
/*     */     }
/*     */ 
/* 146 */     text = text + "\n";
/* 147 */     text = text + "}\n";
/* 148 */     this.ed.create(pluginName, text);
/*     */   }
/*     */ 
/*     */   public boolean showDialog()
/*     */   {
/*     */     String heightUnit;
/*     */     String title;
/*     */     String widthUnit;
/*     */     String heightUnit;
/* 155 */     if (this.type == 6) {
/* 156 */       String title = "New Table";
/* 157 */       this.name = "Table";
/* 158 */       int width = tableWidth;
/* 159 */       int height = tableHeight;
/* 160 */       String widthUnit = "pixels";
/* 161 */       heightUnit = "pixels";
/*     */     } else {
/* 163 */       title = "New Text Window";
/* 164 */       this.name = "Untitled";
/* 165 */       width = columns;
/* 166 */       height = rows;
/* 167 */       widthUnit = "characters";
/* 168 */       heightUnit = "lines";
/*     */     }
/* 170 */     GenericDialog gd = new GenericDialog(title);
/* 171 */     gd.addStringField("Name:", this.name, 16);
/* 172 */     gd.addMessage("");
/* 173 */     gd.addNumericField("Width:", width, 0, 3, widthUnit);
/* 174 */     gd.addNumericField("Height:", height, 0, 3, heightUnit);
/* 175 */     if (this.type != 6) {
/* 176 */       gd.setInsets(5, 30, 0);
/* 177 */       gd.addCheckbox("Menu Bar", true);
/* 178 */       gd.setInsets(0, 30, 0);
/* 179 */       gd.addCheckbox("Monospaced Font", this.monospaced);
/*     */     }
/* 181 */     gd.showDialog();
/* 182 */     if (gd.wasCanceled())
/* 183 */       return false;
/* 184 */     this.name = gd.getNextString();
/* 185 */     int width = (int)gd.getNextNumber();
/* 186 */     int height = (int)gd.getNextNumber();
/* 187 */     if (width < 1) width = 1;
/* 188 */     if (height < 1) height = 1;
/* 189 */     if (this.type != 6) {
/* 190 */       this.menuBar = gd.getNextBoolean();
/* 191 */       this.monospaced = gd.getNextBoolean();
/* 192 */       columns = width;
/* 193 */       rows = height;
/* 194 */       if (rows > 100) rows = 100;
/* 195 */       if (columns > 200) columns = 200; 
/*     */     }
/* 197 */     else { tableWidth = width;
/* 198 */       tableHeight = height;
/* 199 */       if (tableWidth < 128) tableWidth = 128;
/* 200 */       if (tableHeight < 75) tableHeight = 75;
/*     */     }
/* 202 */     return true;
/*     */   }
/*     */ 
/*     */   public Editor getEditor()
/*     */   {
/* 207 */     return this.ed;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.NewPlugin
 * JD-Core Version:    0.6.2
 */