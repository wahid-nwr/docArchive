/*     */ package ij.plugin.frame;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.Prefs;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GUI;
/*     */ import ij.gui.TextRoi;
/*     */ import ij.plugin.PlugIn;
/*     */ import java.awt.Checkbox;
/*     */ import java.awt.Choice;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.Frame;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.Panel;
/*     */ import java.awt.Point;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ 
/*     */ public class Fonts extends PlugInFrame
/*     */   implements PlugIn, ItemListener
/*     */ {
/*     */   public static final String LOC_KEY = "fonts.loc";
/*  13 */   private static String[] sizes = { "8", "9", "10", "12", "14", "18", "24", "28", "36", "48", "60", "72", "100", "150", "225", "350" };
/*  14 */   private static int[] isizes = { 8, 9, 10, 12, 14, 18, 24, 28, 36, 48, 60, 72, 100, 150, 225, 350 };
/*     */   private Panel panel;
/*     */   private Choice font;
/*     */   private Choice size;
/*     */   private Choice style;
/*     */   private Checkbox checkbox;
/*     */   private static Frame instance;
/*     */ 
/*     */   public Fonts()
/*     */   {
/*  23 */     super("Fonts");
/*  24 */     if (instance != null) {
/*  25 */       WindowManager.toFront(instance);
/*  26 */       return;
/*     */     }
/*  28 */     WindowManager.addWindow(this);
/*  29 */     instance = this;
/*  30 */     setLayout(new FlowLayout(1, 10, 5));
/*     */ 
/*  32 */     this.font = new Choice();
/*  33 */     GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
/*  34 */     String[] fonts = ge.getAvailableFontFamilyNames();
/*  35 */     this.font.add("SansSerif");
/*  36 */     this.font.add("Serif");
/*  37 */     this.font.add("Monospaced");
/*  38 */     for (int i = 0; i < fonts.length; i++) {
/*  39 */       String f = fonts[i];
/*  40 */       if ((!f.equals("SansSerif")) && (!f.equals("Serif")) && (!f.equals("Monospaced")))
/*  41 */         this.font.add(f);
/*     */     }
/*  43 */     this.font.select(TextRoi.getFont());
/*  44 */     this.font.addItemListener(this);
/*  45 */     add(this.font);
/*     */ 
/*  47 */     this.size = new Choice();
/*  48 */     for (int i = 0; i < sizes.length; i++)
/*  49 */       this.size.add(sizes[i]);
/*  50 */     this.size.select(getSizeIndex());
/*  51 */     this.size.addItemListener(this);
/*  52 */     add(this.size);
/*     */ 
/*  54 */     this.style = new Choice();
/*  55 */     this.style.add("Plain");
/*  56 */     this.style.add("Bold");
/*  57 */     this.style.add("Italic");
/*  58 */     this.style.add("Bold+Italic");
/*  59 */     this.style.add("Center");
/*  60 */     this.style.add("Right");
/*  61 */     this.style.add("Center+Bold");
/*  62 */     this.style.add("Right+Bold");
/*  63 */     int i = TextRoi.getStyle();
/*  64 */     int justificaton = TextRoi.getGlobalJustification();
/*  65 */     String s = "Plain";
/*  66 */     if (i == 1) {
/*  67 */       if (justificaton == 1)
/*  68 */         s = "Center+Bold";
/*  69 */       else if (justificaton == 2)
/*  70 */         s = "Right+Bold";
/*     */       else
/*  72 */         s = "Bold";
/*  73 */     } else if (i == 2)
/*  74 */       s = "Italic";
/*  75 */     else if (i == 3)
/*  76 */       s = "Bold+Italic";
/*  77 */     else if (i == 0) {
/*  78 */       if (justificaton == 1)
/*  79 */         s = "Center";
/*  80 */       else if (justificaton == 2)
/*  81 */         s = "Right";
/*     */     }
/*  83 */     this.style.select(s);
/*  84 */     this.style.addItemListener(this);
/*  85 */     add(this.style);
/*     */ 
/*  87 */     this.checkbox = new Checkbox("Smooth", TextRoi.isAntialiased());
/*  88 */     add(this.checkbox);
/*  89 */     this.checkbox.addItemListener(this);
/*     */ 
/*  91 */     pack();
/*  92 */     Point loc = Prefs.getLocation("fonts.loc");
/*  93 */     if (loc != null)
/*  94 */       setLocation(loc);
/*     */     else
/*  96 */       GUI.center(this);
/*  97 */     show();
/*  98 */     IJ.register(Fonts.class);
/*     */   }
/*     */ 
/*     */   int getSizeIndex() {
/* 102 */     int size = TextRoi.getSize();
/* 103 */     int index = 0;
/* 104 */     for (int i = 0; i < isizes.length; i++) {
/* 105 */       if (size >= isizes[i])
/* 106 */         index = i;
/*     */     }
/* 108 */     return index;
/*     */   }
/*     */ 
/*     */   public void itemStateChanged(ItemEvent e) {
/* 112 */     String fontName = this.font.getSelectedItem();
/* 113 */     int fontSize = Integer.parseInt(this.size.getSelectedItem());
/* 114 */     String styleName = this.style.getSelectedItem();
/* 115 */     int fontStyle = 0;
/* 116 */     int justification = 0;
/* 117 */     if (styleName.endsWith("Bold"))
/* 118 */       fontStyle = 1;
/* 119 */     else if (styleName.equals("Italic"))
/* 120 */       fontStyle = 2;
/* 121 */     else if (styleName.equals("Bold+Italic"))
/* 122 */       fontStyle = 3;
/* 123 */     if (styleName.startsWith("Center"))
/* 124 */       justification = 1;
/* 125 */     else if (styleName.startsWith("Right"))
/* 126 */       justification = 2;
/* 127 */     TextRoi.setFont(fontName, fontSize, fontStyle, this.checkbox.getState());
/* 128 */     TextRoi.setGlobalJustification(justification);
/* 129 */     IJ.showStatus(fontSize + " point " + fontName + " " + styleName);
/*     */   }
/*     */ 
/*     */   public void close() {
/* 133 */     super.close();
/* 134 */     instance = null;
/* 135 */     Prefs.saveLocation("fonts.loc", getLocation());
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.frame.Fonts
 * JD-Core Version:    0.6.2
 */