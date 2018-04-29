/*     */ package ij.plugin.frame;
/*     */ 
/*     */ import ij.CompositeImage;
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GUI;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.plugin.PlugIn;
/*     */ import java.awt.Button;
/*     */ import java.awt.Checkbox;
/*     */ import java.awt.Choice;
/*     */ import java.awt.Frame;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.MenuItem;
/*     */ import java.awt.Point;
/*     */ import java.awt.PopupMenu;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ 
/*     */ public class Channels extends PlugInFrame
/*     */   implements PlugIn, ItemListener, ActionListener
/*     */ {
/*  11 */   private static String[] modes = { "Composite", "Color", "Grayscale" };
/*  12 */   private static String[] menuItems = { "Make Composite", "Convert to RGB", "Split Channels", "Merge Channels...", "Edit LUT...", "-", "Red", "Green", "Blue", "Cyan", "Magenta", "Yellow", "Grays" };
/*     */ 
/*  15 */   private static String moreLabel = "More »";
/*     */   private Choice choice;
/*     */   private Checkbox[] checkbox;
/*     */   private Button moreButton;
/*     */   private static Frame instance;
/*     */   private int id;
/*     */   private static Point location;
/*     */   private PopupMenu pm;
/*     */ 
/*     */   public Channels()
/*     */   {
/*  26 */     super("Channels");
/*  27 */     if (instance != null) {
/*  28 */       WindowManager.toFront(instance);
/*  29 */       return;
/*     */     }
/*  31 */     WindowManager.addWindow(this);
/*  32 */     instance = this;
/*  33 */     GridBagLayout gridbag = new GridBagLayout();
/*  34 */     GridBagConstraints c = new GridBagConstraints();
/*  35 */     setLayout(gridbag);
/*  36 */     int y = 0;
/*  37 */     c.gridx = 0;
/*  38 */     c.gridy = (y++);
/*  39 */     c.gridwidth = 1;
/*  40 */     c.fill = 1;
/*  41 */     c.anchor = 10;
/*  42 */     int margin = 32;
/*  43 */     if (IJ.isVista())
/*  44 */       margin = 40;
/*  45 */     else if (IJ.isMacOSX())
/*  46 */       margin = 18;
/*  47 */     c.insets = new Insets(10, margin, 10, margin);
/*  48 */     this.choice = new Choice();
/*  49 */     for (int i = 0; i < modes.length; i++)
/*  50 */       this.choice.addItem(modes[i]);
/*  51 */     this.choice.select(0);
/*  52 */     this.choice.addItemListener(this);
/*  53 */     add(this.choice, c);
/*     */ 
/*  55 */     CompositeImage ci = getImage();
/*  56 */     int nCheckBoxes = ci != null ? ci.getNChannels() : 3;
/*  57 */     if (nCheckBoxes > 7)
/*  58 */       nCheckBoxes = 7;
/*  59 */     this.checkbox = new Checkbox[nCheckBoxes];
/*  60 */     for (int i = 0; i < nCheckBoxes; i++) {
/*  61 */       this.checkbox[i] = new Checkbox("Channel " + (i + 1), true);
/*  62 */       c.insets = new Insets(0, 25, i < nCheckBoxes - 1 ? 0 : 10, 5);
/*  63 */       c.gridy = (y++);
/*  64 */       add(this.checkbox[i], c);
/*  65 */       this.checkbox[i].addItemListener(this);
/*     */     }
/*     */ 
/*  68 */     c.insets = new Insets(0, 15, 10, 15);
/*  69 */     c.fill = 0;
/*  70 */     c.gridy = (y++);
/*  71 */     this.moreButton = new Button(moreLabel);
/*  72 */     this.moreButton.addActionListener(this);
/*  73 */     add(this.moreButton, c);
/*  74 */     update();
/*     */ 
/*  76 */     this.pm = new PopupMenu();
/*  77 */     for (int i = 0; i < menuItems.length; i++)
/*  78 */       addPopupItem(menuItems[i]);
/*  79 */     add(this.pm);
/*     */ 
/*  81 */     addKeyListener(IJ.getInstance());
/*  82 */     setResizable(false);
/*  83 */     pack();
/*  84 */     if (location == null) {
/*  85 */       GUI.center(this);
/*  86 */       location = getLocation();
/*     */     } else {
/*  88 */       setLocation(location);
/*  89 */     }show();
/*     */   }
/*     */ 
/*     */   public void update() {
/*  93 */     CompositeImage ci = getImage();
/*  94 */     if (ci == null) return;
/*  95 */     int n = this.checkbox.length;
/*  96 */     int nChannels = ci.getNChannels();
/*  97 */     if ((nChannels != n) && (nChannels <= 7)) {
/*  98 */       instance = null;
/*  99 */       location = getLocation();
/* 100 */       close();
/* 101 */       new Channels();
/* 102 */       return;
/*     */     }
/* 104 */     boolean[] active = ci.getActiveChannels();
/* 105 */     for (int i = 0; i < this.checkbox.length; i++)
/* 106 */       this.checkbox[i].setState(active[i]);
/* 107 */     int index = 0;
/* 108 */     switch (ci.getMode()) { case 1:
/* 109 */       index = 0; break;
/*     */     case 2:
/* 110 */       index = 1; break;
/*     */     case 3:
/* 111 */       index = 2;
/*     */     }
/* 113 */     this.choice.select(index);
/*     */   }
/*     */ 
/*     */   void addPopupItem(String s) {
/* 117 */     MenuItem mi = new MenuItem(s);
/* 118 */     mi.addActionListener(this);
/* 119 */     this.pm.add(mi);
/*     */   }
/*     */ 
/*     */   CompositeImage getImage() {
/* 123 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 124 */     if ((imp == null) || (!imp.isComposite())) {
/* 125 */       return null;
/*     */     }
/* 127 */     return (CompositeImage)imp;
/*     */   }
/*     */ 
/*     */   public void itemStateChanged(ItemEvent e) {
/* 131 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 132 */     if (imp == null) return;
/* 133 */     if (!imp.isComposite()) {
/* 134 */       int channels = imp.getNChannels();
/* 135 */       if ((channels == 1) && (imp.getStackSize() <= 4))
/* 136 */         channels = imp.getStackSize();
/* 137 */       if ((imp.getBitDepth() == 24) || ((channels > 1) && (channels < 7))) {
/* 138 */         GenericDialog gd = new GenericDialog(imp.getTitle(), this);
/* 139 */         gd.addMessage("Convert to multi-channel composite image?");
/* 140 */         gd.showDialog();
/* 141 */         if (gd.wasCanceled()) {
/* 142 */           return;
/*     */         }
/* 144 */         IJ.doCommand("Make Composite");
/*     */       } else {
/* 146 */         IJ.error("Channels", "A composite image is required (e.g., " + moreLabel + " Open HeLa Cells),\nor create one using " + moreLabel + " Make Composite.");
/* 147 */         return;
/*     */       }
/*     */     }
/* 150 */     if (!imp.isComposite()) return;
/* 151 */     CompositeImage ci = (CompositeImage)imp;
/* 152 */     Object source = e.getSource();
/* 153 */     if (source == this.choice) {
/* 154 */       int index = ((Choice)source).getSelectedIndex();
/* 155 */       switch (index) { case 0:
/* 156 */         ci.setMode(1); break;
/*     */       case 1:
/* 157 */         ci.setMode(2); break;
/*     */       case 2:
/* 158 */         ci.setMode(3);
/*     */       }
/* 160 */       ci.updateAndDraw();
/* 161 */       if (Recorder.record) {
/* 162 */         String mode = null;
/* 163 */         switch (index) { case 0:
/* 164 */           mode = "composite"; break;
/*     */         case 1:
/* 165 */           mode = "color"; break;
/*     */         case 2:
/* 166 */           mode = "grayscale";
/*     */         }
/* 168 */         Recorder.record("Stack.setDisplayMode", mode);
/*     */       }
/* 170 */     } else if ((source instanceof Checkbox)) {
/* 171 */       for (int i = 0; i < this.checkbox.length; i++) {
/* 172 */         Checkbox cb = (Checkbox)source;
/* 173 */         if (cb == this.checkbox[i]) {
/* 174 */           if (ci.getMode() == 1) {
/* 175 */             boolean[] active = ci.getActiveChannels();
/* 176 */             active[i] = cb.getState();
/* 177 */             if (Recorder.record) {
/* 178 */               String str = "";
/* 179 */               for (int c = 0; c < ci.getNChannels(); c++)
/* 180 */                 str = str + (active[c] != 0 ? "1" : "0");
/* 181 */               Recorder.record("Stack.setActiveChannels", str);
/*     */             }
/*     */           } else {
/* 184 */             imp.setPosition(i + 1, imp.getSlice(), imp.getFrame());
/* 185 */             if (Recorder.record)
/* 186 */               Recorder.record("Stack.setChannel", i + 1);
/*     */           }
/* 188 */           ci.updateAndDraw();
/* 189 */           return;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e) {
/* 196 */     String command = e.getActionCommand();
/* 197 */     if (command == null) return;
/* 198 */     if (command.equals(moreLabel)) {
/* 199 */       Point bloc = this.moreButton.getLocation();
/* 200 */       this.pm.show(this, bloc.x, bloc.y);
/* 201 */     } else if (command.equals("Convert to RGB")) {
/* 202 */       IJ.doCommand("Stack to RGB");
/*     */     } else {
/* 204 */       IJ.doCommand(command);
/*     */     }
/*     */   }
/*     */ 
/* 208 */   public static Frame getInstance() { return instance; }
/*     */ 
/*     */   public void close()
/*     */   {
/* 212 */     super.close();
/* 213 */     instance = null;
/* 214 */     location = getLocation();
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.frame.Channels
 * JD-Core Version:    0.6.2
 */