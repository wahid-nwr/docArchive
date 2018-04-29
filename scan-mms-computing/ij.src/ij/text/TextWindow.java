/*     */ package ij.text;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImageJ;
/*     */ import ij.Menus;
/*     */ import ij.Prefs;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GUI;
/*     */ import ij.gui.YesNoCancelDialog;
/*     */ import ij.io.OpenDialog;
/*     */ import ij.macro.Interpreter;
/*     */ import ij.measure.ResultsTable;
/*     */ import ij.plugin.filter.Analyzer;
/*     */ import java.awt.CheckboxMenuItem;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.Frame;
/*     */ import java.awt.Image;
/*     */ import java.awt.Menu;
/*     */ import java.awt.MenuBar;
/*     */ import java.awt.MenuItem;
/*     */ import java.awt.MenuShortcut;
/*     */ import java.awt.Point;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.awt.event.FocusListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class TextWindow extends Frame
/*     */   implements ActionListener, FocusListener, ItemListener
/*     */ {
/*     */   public static final String LOC_KEY = "results.loc";
/*     */   public static final String WIDTH_KEY = "results.width";
/*     */   public static final String HEIGHT_KEY = "results.height";
/*     */   public static final String LOG_LOC_KEY = "log.loc";
/*     */   public static final String DEBUG_LOC_KEY = "debug.loc";
/*     */   static final String FONT_SIZE = "tw.font.size";
/*     */   static final String FONT_ANTI = "tw.font.anti";
/*     */   TextPanel textPanel;
/*     */   CheckboxMenuItem antialiased;
/*  27 */   int[] sizes = { 9, 10, 11, 12, 13, 14, 16, 18, 20, 24, 36, 48, 60, 72 };
/*  28 */   int fontSize = (int)Prefs.get("tw.font.size", 5.0D);
/*     */   MenuBar mb;
/*     */ 
/*     */   public TextWindow(String title, String data, int width, int height)
/*     */   {
/*  39 */     this(title, "", data, width, height);
/*     */   }
/*     */ 
/*     */   public TextWindow(String title, String headings, String data, int width, int height)
/*     */   {
/*  51 */     super(title);
/*  52 */     enableEvents(64L);
/*  53 */     if (IJ.isLinux()) setBackground(ImageJ.backgroundColor);
/*  54 */     this.textPanel = new TextPanel(title);
/*  55 */     this.textPanel.setTitle(title);
/*  56 */     add("Center", this.textPanel);
/*  57 */     this.textPanel.setColumnHeadings(headings);
/*  58 */     if ((data != null) && (!data.equals("")))
/*  59 */       this.textPanel.append(data);
/*  60 */     addKeyListener(this.textPanel);
/*  61 */     ImageJ ij = IJ.getInstance();
/*  62 */     if (ij != null) {
/*  63 */       this.textPanel.addKeyListener(ij);
/*  64 */       Image img = ij.getIconImage();
/*  65 */       if (img != null) try {
/*  66 */           setIconImage(img); } catch (Exception e) {  }
/*     */  
/*     */     }
/*  68 */     addFocusListener(this);
/*  69 */     addMenuBar();
/*  70 */     setFont();
/*  71 */     WindowManager.addWindow(this);
/*     */ 
/*  73 */     Point loc = null;
/*  74 */     int w = 0; int h = 0;
/*  75 */     if (title.equals("Results")) {
/*  76 */       loc = Prefs.getLocation("results.loc");
/*  77 */       w = (int)Prefs.get("results.width", 0.0D);
/*  78 */       h = (int)Prefs.get("results.height", 0.0D);
/*  79 */     } else if (title.equals("Log")) {
/*  80 */       loc = Prefs.getLocation("log.loc");
/*  81 */       w = width;
/*  82 */       h = height;
/*  83 */     } else if (title.equals("Debug")) {
/*  84 */       loc = Prefs.getLocation("debug.loc");
/*  85 */       w = width;
/*  86 */       h = height;
/*     */     }
/*  88 */     if ((loc != null) && (w > 0) && (h > 0)) {
/*  89 */       setSize(w, h);
/*  90 */       setLocation(loc);
/*     */     } else {
/*  92 */       setSize(width, height);
/*  93 */       GUI.center(this);
/*     */     }
/*  95 */     show();
/*     */   }
/*     */ 
/*     */   public TextWindow(String path, int width, int height)
/*     */   {
/* 106 */     super("");
/* 107 */     enableEvents(64L);
/* 108 */     this.textPanel = new TextPanel();
/* 109 */     this.textPanel.addKeyListener(IJ.getInstance());
/* 110 */     add("Center", this.textPanel);
/* 111 */     if (openFile(path)) {
/* 112 */       WindowManager.addWindow(this);
/* 113 */       setSize(width, height);
/* 114 */       show();
/*     */     } else {
/* 116 */       dispose();
/*     */     }
/*     */   }
/*     */ 
/* 120 */   void addMenuBar() { this.mb = new MenuBar();
/* 121 */     if (Menus.getFontSize() != 0)
/* 122 */       this.mb.setFont(Menus.getFont());
/* 123 */     Menu m = new Menu("File");
/* 124 */     m.add(new MenuItem("Save As...", new MenuShortcut(83)));
/* 125 */     if (getTitle().equals("Results")) {
/* 126 */       m.add(new MenuItem("Rename..."));
/* 127 */       m.add(new MenuItem("Duplicate..."));
/*     */     }
/* 129 */     m.addActionListener(this);
/* 130 */     this.mb.add(m);
/* 131 */     m = new Menu("Edit");
/* 132 */     m.add(new MenuItem("Cut", new MenuShortcut(88)));
/* 133 */     m.add(new MenuItem("Copy", new MenuShortcut(67)));
/* 134 */     m.add(new MenuItem("Clear"));
/* 135 */     m.add(new MenuItem("Select All", new MenuShortcut(65)));
/* 136 */     m.addActionListener(this);
/* 137 */     this.mb.add(m);
/* 138 */     m = new Menu("Font");
/* 139 */     m.add(new MenuItem("Make Text Smaller"));
/* 140 */     m.add(new MenuItem("Make Text Larger"));
/* 141 */     m.addSeparator();
/* 142 */     this.antialiased = new CheckboxMenuItem("Antialiased", Prefs.get("tw.font.anti", IJ.isMacOSX()));
/* 143 */     this.antialiased.addItemListener(this);
/* 144 */     m.add(this.antialiased);
/* 145 */     m.add(new MenuItem("Save Settings"));
/* 146 */     m.addActionListener(this);
/* 147 */     this.mb.add(m);
/* 148 */     if (getTitle().equals("Results")) {
/* 149 */       m = new Menu("Results");
/* 150 */       m.add(new MenuItem("Clear Results"));
/* 151 */       m.add(new MenuItem("Summarize"));
/* 152 */       m.add(new MenuItem("Distribution..."));
/* 153 */       m.add(new MenuItem("Set Measurements..."));
/* 154 */       m.add(new MenuItem("Options..."));
/* 155 */       m.addActionListener(this);
/* 156 */       this.mb.add(m);
/*     */     }
/* 158 */     setMenuBar(this.mb);
/*     */   }
/*     */ 
/*     */   public void append(String text)
/*     */   {
/* 167 */     this.textPanel.append(text);
/*     */   }
/*     */ 
/*     */   void setFont() {
/* 171 */     this.textPanel.setFont(new Font("SanSerif", 0, this.sizes[this.fontSize]), this.antialiased.getState());
/*     */   }
/*     */ 
/*     */   boolean openFile(String path) {
/* 175 */     OpenDialog od = new OpenDialog("Open Text File...", path);
/* 176 */     String directory = od.getDirectory();
/* 177 */     String name = od.getFileName();
/* 178 */     if (name == null)
/* 179 */       return false;
/* 180 */     path = directory + name;
/*     */ 
/* 182 */     IJ.showStatus("Opening: " + path);
/*     */     try {
/* 184 */       BufferedReader r = new BufferedReader(new FileReader(directory + name));
/* 185 */       load(r);
/* 186 */       r.close();
/*     */     }
/*     */     catch (Exception e) {
/* 189 */       IJ.error(e.getMessage());
/* 190 */       return true;
/*     */     }
/* 192 */     this.textPanel.setTitle(name);
/* 193 */     setTitle(name);
/* 194 */     IJ.showStatus("");
/* 195 */     return true;
/*     */   }
/*     */ 
/*     */   public TextPanel getTextPanel()
/*     */   {
/* 200 */     return this.textPanel;
/*     */   }
/*     */ 
/*     */   public void load(BufferedReader in) throws IOException
/*     */   {
/* 205 */     int count = 0;
/*     */     while (true) {
/* 207 */       String s = in.readLine();
/* 208 */       if (s == null) break;
/* 209 */       this.textPanel.appendLine(s);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent evt) {
/* 214 */     String cmd = evt.getActionCommand();
/* 215 */     if (cmd.equals("Make Text Larger"))
/* 216 */       changeFontSize(true);
/* 217 */     else if (cmd.equals("Make Text Smaller"))
/* 218 */       changeFontSize(false);
/* 219 */     else if (cmd.equals("Save Settings"))
/* 220 */       saveSettings();
/*     */     else
/* 222 */       this.textPanel.doCommand(cmd);
/*     */   }
/*     */ 
/*     */   public void processWindowEvent(WindowEvent e) {
/* 226 */     super.processWindowEvent(e);
/* 227 */     int id = e.getID();
/* 228 */     if (id == 201)
/* 229 */       close();
/* 230 */     else if (id == 205)
/* 231 */       WindowManager.setWindow(this);
/*     */   }
/*     */ 
/*     */   public void itemStateChanged(ItemEvent e) {
/* 235 */     setFont();
/*     */   }
/*     */ 
/*     */   public void close() {
/* 239 */     close(true);
/*     */   }
/*     */ 
/*     */   public void close(boolean showDialog)
/*     */   {
/* 245 */     if (getTitle().equals("Results")) {
/* 246 */       if ((showDialog) && (!Analyzer.resetCounter()))
/* 247 */         return;
/* 248 */       IJ.setTextPanel(null);
/* 249 */       Prefs.saveLocation("results.loc", getLocation());
/* 250 */       Dimension d = getSize();
/* 251 */       Prefs.set("results.width", d.width);
/* 252 */       Prefs.set("results.height", d.height);
/* 253 */     } else if (getTitle().equals("Log")) {
/* 254 */       Prefs.saveLocation("log.loc", getLocation());
/* 255 */       IJ.debugMode = false;
/* 256 */       IJ.log("\\Closed");
/* 257 */       IJ.notifyEventListeners(3);
/* 258 */     } else if (getTitle().equals("Debug")) {
/* 259 */       Prefs.saveLocation("debug.loc", getLocation());
/* 260 */     } else if ((this.textPanel != null) && (this.textPanel.rt != null) && 
/* 261 */       (!saveContents())) { return; }
/*     */ 
/*     */ 
/* 264 */     dispose();
/* 265 */     WindowManager.removeWindow(this);
/* 266 */     this.textPanel.flush();
/*     */   }
/*     */ 
/*     */   public void rename(String title) {
/* 270 */     this.textPanel.rename(title);
/*     */   }
/*     */ 
/*     */   boolean saveContents() {
/* 274 */     int lineCount = this.textPanel.getLineCount();
/* 275 */     if (!this.textPanel.unsavedLines) lineCount = 0;
/* 276 */     ImageJ ij = IJ.getInstance();
/* 277 */     boolean macro = (IJ.macroRunning()) || (Interpreter.isBatchMode());
/* 278 */     if ((lineCount > 0) && (!macro) && (ij != null) && (!ij.quitting())) {
/* 279 */       YesNoCancelDialog d = new YesNoCancelDialog(this, getTitle(), "Save " + lineCount + " measurements?");
/* 280 */       if (d.cancelPressed())
/* 281 */         return false;
/* 282 */       if ((d.yesPressed()) && 
/* 283 */         (!this.textPanel.saveAs(""))) {
/* 284 */         return false;
/*     */       }
/*     */     }
/* 287 */     this.textPanel.rt.reset();
/* 288 */     return true;
/*     */   }
/*     */ 
/*     */   void changeFontSize(boolean larger) {
/* 292 */     int in = this.fontSize;
/* 293 */     if (larger) {
/* 294 */       this.fontSize += 1;
/* 295 */       if (this.fontSize == this.sizes.length)
/* 296 */         this.fontSize = (this.sizes.length - 1);
/*     */     } else {
/* 298 */       this.fontSize -= 1;
/* 299 */       if (this.fontSize < 0)
/* 300 */         this.fontSize = 0;
/*     */     }
/* 302 */     IJ.showStatus(this.sizes[this.fontSize] + " point");
/* 303 */     setFont();
/*     */   }
/*     */ 
/*     */   void saveSettings() {
/* 307 */     Prefs.set("tw.font.size", this.fontSize);
/* 308 */     Prefs.set("tw.font.anti", this.antialiased.getState());
/* 309 */     IJ.showStatus("Font settings saved (size=" + this.sizes[this.fontSize] + ", antialiased=" + this.antialiased.getState() + ")");
/*     */   }
/*     */ 
/*     */   public void focusGained(FocusEvent e) {
/* 313 */     WindowManager.setWindow(this);
/*     */   }
/*     */ 
/*     */   public void focusLost(FocusEvent e)
/*     */   {
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.text.TextWindow
 * JD-Core Version:    0.6.2
 */