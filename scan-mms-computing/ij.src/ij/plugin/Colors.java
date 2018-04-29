/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.ImagePlus;
/*     */ import ij.Prefs;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.Roi;
/*     */ import ij.gui.Toolbar;
/*     */ import ij.plugin.filter.Analyzer;
/*     */ import java.awt.Choice;
/*     */ import java.awt.Color;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class Colors
/*     */   implements PlugIn, ItemListener
/*     */ {
/*  13 */   public static final String[] colors = { "red", "green", "blue", "magenta", "cyan", "yellow", "orange", "black", "white" };
/*     */   private Choice fchoice;
/*     */   private Choice bchoice;
/*     */   private Choice schoice;
/*     */   private Color fc2;
/*     */   private Color bc2;
/*     */   private Color sc2;
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  18 */     if (arg.equals("point"))
/*  19 */       pointToolOptions();
/*     */     else
/*  21 */       showDialog();
/*     */   }
/*     */ 
/*     */   void showDialog() {
/*  25 */     Color fc = Toolbar.getForegroundColor();
/*  26 */     String fname = getColorName(fc, "black");
/*  27 */     Color bc = Toolbar.getBackgroundColor();
/*  28 */     String bname = getColorName(bc, "white");
/*  29 */     Color sc = Roi.getColor();
/*  30 */     String sname = getColorName(sc, "yellow");
/*  31 */     GenericDialog gd = new GenericDialog("Colors");
/*  32 */     gd.addChoice("Foreground:", colors, fname);
/*  33 */     gd.addChoice("Background:", colors, bname);
/*  34 */     gd.addChoice("Selection:", colors, sname);
/*  35 */     Vector choices = gd.getChoices();
/*  36 */     this.fchoice = ((Choice)choices.elementAt(0));
/*  37 */     this.bchoice = ((Choice)choices.elementAt(1));
/*  38 */     this.schoice = ((Choice)choices.elementAt(2));
/*  39 */     this.fchoice.addItemListener(this);
/*  40 */     this.bchoice.addItemListener(this);
/*  41 */     this.schoice.addItemListener(this);
/*     */ 
/*  43 */     gd.showDialog();
/*  44 */     if (gd.wasCanceled()) {
/*  45 */       if (this.fc2 != fc) Toolbar.setForegroundColor(fc);
/*  46 */       if (this.bc2 != bc) Toolbar.setBackgroundColor(bc);
/*  47 */       if (this.sc2 != sc) {
/*  48 */         Roi.setColor(sc);
/*  49 */         ImagePlus imp = WindowManager.getCurrentImage();
/*  50 */         if ((imp != null) && (imp.getRoi() != null)) imp.draw();
/*     */       }
/*  52 */       return;
/*     */     }
/*  54 */     fname = gd.getNextChoice();
/*  55 */     bname = gd.getNextChoice();
/*  56 */     sname = gd.getNextChoice();
/*  57 */     this.fc2 = getColor(fname, Color.black);
/*  58 */     this.bc2 = getColor(bname, Color.white);
/*  59 */     this.sc2 = getColor(sname, Color.yellow);
/*  60 */     if (this.fc2 != fc) Toolbar.setForegroundColor(this.fc2);
/*  61 */     if (this.bc2 != bc) Toolbar.setBackgroundColor(this.bc2);
/*  62 */     if (this.sc2 != sc) {
/*  63 */       Roi.setColor(this.sc2);
/*  64 */       ImagePlus imp = WindowManager.getCurrentImage();
/*  65 */       if (imp != null) imp.draw();
/*  66 */       Toolbar.getInstance().repaint();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String getColorName(Color c, String defaultName) {
/*  71 */     if (c == null) return defaultName;
/*  72 */     String name = defaultName;
/*  73 */     if (c.equals(Color.red)) name = colors[0];
/*  74 */     else if (c.equals(Color.green)) name = colors[1];
/*  75 */     else if (c.equals(Color.blue)) name = colors[2];
/*  76 */     else if (c.equals(Color.magenta)) name = colors[3];
/*  77 */     else if (c.equals(Color.cyan)) name = colors[4];
/*  78 */     else if (c.equals(Color.yellow)) name = colors[5];
/*  79 */     else if (c.equals(Color.orange)) name = colors[6];
/*  80 */     else if (c.equals(Color.black)) name = colors[7];
/*  81 */     else if (c.equals(Color.white)) name = colors[8];
/*  82 */     return name;
/*     */   }
/*     */ 
/*     */   public static Color getColor(String name, Color defaultColor) {
/*  86 */     if (name == null) return defaultColor;
/*  87 */     Color c = defaultColor;
/*  88 */     if (name.equals(colors[0])) c = Color.red;
/*  89 */     else if (name.equals(colors[1])) c = Color.green;
/*  90 */     else if (name.equals(colors[2])) c = Color.blue;
/*  91 */     else if (name.equals(colors[3])) c = Color.magenta;
/*  92 */     else if (name.equals(colors[4])) c = Color.cyan;
/*  93 */     else if (name.equals(colors[5])) c = Color.yellow;
/*  94 */     else if (name.equals(colors[6])) c = Color.orange;
/*  95 */     else if (name.equals(colors[7])) c = Color.black;
/*  96 */     else if (name.equals(colors[8])) c = Color.white;
/*  97 */     return c;
/*     */   }
/*     */ 
/*     */   public static Color decode(String hexColor, Color defaultColor) {
/* 101 */     Color color = getColor(hexColor, Color.gray);
/* 102 */     if (color == Color.gray) {
/* 103 */       if (hexColor.startsWith("#"))
/* 104 */         hexColor = hexColor.substring(1);
/* 105 */       int len = hexColor.length();
/* 106 */       if ((len != 6) && (len != 8))
/* 107 */         return defaultColor;
/* 108 */       float alpha = len == 8 ? parseHex(hexColor.substring(0, 2)) : 1.0F;
/* 109 */       if (len == 8)
/* 110 */         hexColor = hexColor.substring(2);
/* 111 */       float red = parseHex(hexColor.substring(0, 2));
/* 112 */       float green = parseHex(hexColor.substring(2, 4));
/* 113 */       float blue = parseHex(hexColor.substring(4, 6));
/* 114 */       color = new Color(red, green, blue, alpha);
/*     */     }
/* 116 */     return color;
/*     */   }
/*     */ 
/*     */   public static String hexToColor(String hex)
/*     */   {
/* 122 */     if (hex == null) return null;
/* 123 */     if (hex.startsWith("#"))
/* 124 */       hex = hex.substring(1);
/* 125 */     String color = null;
/* 126 */     if (hex.equals("ff0000")) color = "red";
/* 127 */     else if (hex.equals("00ff00")) color = "green";
/* 128 */     else if (hex.equals("0000ff")) color = "blue";
/* 129 */     else if (hex.equals("000000")) color = "black";
/* 130 */     else if (hex.equals("ffffff")) color = "white";
/* 131 */     else if (hex.equals("ffff00")) color = "yellow";
/* 132 */     else if (hex.equals("00ffff")) color = "cyan";
/* 133 */     else if (hex.equals("ff00ff")) color = "magenta";
/* 134 */     return color;
/*     */   }
/*     */ 
/*     */   public static String colorToString(Color color)
/*     */   {
/* 139 */     String str = color != null ? "#" + Integer.toHexString(color.getRGB()) : "none";
/* 140 */     if ((str.length() == 9) && (str.startsWith("#ff")))
/* 141 */       str = "#" + str.substring(3);
/* 142 */     String str2 = hexToColor(str);
/* 143 */     return str2 != null ? str2 : str;
/*     */   }
/*     */ 
/*     */   private static float parseHex(String hex) {
/* 147 */     float value = 0.0F;
/*     */     try { value = Integer.parseInt(hex, 16); } catch (Exception e) {
/*     */     }
/* 150 */     return value / 255.0F;
/*     */   }
/*     */ 
/*     */   public void itemStateChanged(ItemEvent e) {
/* 154 */     Choice choice = (Choice)e.getSource();
/* 155 */     String item = choice.getSelectedItem();
/* 156 */     Color color = getColor(item, Color.black);
/* 157 */     if (choice == this.fchoice) {
/* 158 */       Toolbar.setForegroundColor(color);
/* 159 */     } else if (choice == this.bchoice) {
/* 160 */       Toolbar.setBackgroundColor(color);
/* 161 */     } else if (choice == this.schoice) {
/* 162 */       Roi.setColor(color);
/* 163 */       ImagePlus imp = WindowManager.getCurrentImage();
/* 164 */       if ((imp != null) && (imp.getRoi() != null)) imp.draw();
/* 165 */       Toolbar.getInstance().repaint();
/*     */     }
/*     */   }
/*     */ 
/*     */   void pointToolOptions()
/*     */   {
/* 171 */     boolean saveNoPointLabels = Prefs.noPointLabels;
/* 172 */     Color sc = Roi.getColor();
/* 173 */     String sname = getColorName(sc, "yellow");
/* 174 */     GenericDialog gd = new GenericDialog("Point Tool");
/* 175 */     gd.addNumericField("Mark Width:", Analyzer.markWidth, 0, 2, "pixels");
/* 176 */     gd.addCheckbox("Auto-Measure", Prefs.pointAutoMeasure);
/* 177 */     gd.addCheckbox("Auto-Next Slice", Prefs.pointAutoNextSlice);
/* 178 */     gd.addCheckbox("Add to ROI Manager", Prefs.pointAddToManager);
/* 179 */     gd.addCheckbox("Label Points", !Prefs.noPointLabels);
/* 180 */     gd.addChoice("Selection Color:", colors, sname);
/* 181 */     Vector choices = gd.getChoices();
/* 182 */     this.schoice = ((Choice)choices.elementAt(0));
/* 183 */     this.schoice.addItemListener(this);
/* 184 */     gd.showDialog();
/* 185 */     if (gd.wasCanceled()) {
/* 186 */       if (this.sc2 != sc) {
/* 187 */         Roi.setColor(sc);
/* 188 */         ImagePlus imp = WindowManager.getCurrentImage();
/* 189 */         if ((imp != null) && (imp.getRoi() != null)) imp.draw();
/* 190 */         Toolbar.getInstance().repaint();
/*     */       }
/* 192 */       return;
/*     */     }
/* 194 */     int width = (int)gd.getNextNumber();
/* 195 */     if (width < 0) width = 0;
/* 196 */     Analyzer.markWidth = width;
/* 197 */     Prefs.pointAutoMeasure = gd.getNextBoolean();
/* 198 */     Prefs.pointAutoNextSlice = gd.getNextBoolean();
/* 199 */     Prefs.pointAddToManager = gd.getNextBoolean();
/* 200 */     Prefs.noPointLabels = !gd.getNextBoolean();
/* 201 */     sname = gd.getNextChoice();
/* 202 */     this.sc2 = getColor(sname, Color.yellow);
/* 203 */     if ((Prefs.pointAutoNextSlice) && (!Prefs.pointAddToManager))
/* 204 */       Prefs.pointAutoMeasure = true;
/* 205 */     if (Prefs.noPointLabels != saveNoPointLabels) {
/* 206 */       ImagePlus imp = WindowManager.getCurrentImage();
/* 207 */       if (imp != null) imp.draw();
/*     */     }
/* 209 */     if (this.sc2 != sc) {
/* 210 */       Roi.setColor(this.sc2);
/* 211 */       ImagePlus imp = WindowManager.getCurrentImage();
/* 212 */       if (imp != null) imp.draw();
/* 213 */       Toolbar.getInstance().repaint();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.Colors
 * JD-Core Version:    0.6.2
 */