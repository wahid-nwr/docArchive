/*      */ package ij.gui;
/*      */ 
/*      */ import ij.CompositeImage;
/*      */ import ij.IJ;
/*      */ import ij.ImagePlus;
/*      */ import ij.Macro;
/*      */ import ij.Prefs;
/*      */ import ij.WindowManager;
/*      */ import ij.macro.Interpreter;
/*      */ import ij.macro.MacroRunner;
/*      */ import ij.plugin.ScreenGrabber;
/*      */ import ij.plugin.filter.PlugInFilterRunner;
/*      */ import ij.plugin.frame.Recorder;
/*      */ import ij.util.Tools;
/*      */ import java.awt.AWTEvent;
/*      */ import java.awt.BorderLayout;
/*      */ import java.awt.Button;
/*      */ import java.awt.Checkbox;
/*      */ import java.awt.Choice;
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Dialog;
/*      */ import java.awt.FlowLayout;
/*      */ import java.awt.Font;
/*      */ import java.awt.Frame;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.GridBagConstraints;
/*      */ import java.awt.GridBagLayout;
/*      */ import java.awt.GridLayout;
/*      */ import java.awt.Insets;
/*      */ import java.awt.Label;
/*      */ import java.awt.Panel;
/*      */ import java.awt.Scrollbar;
/*      */ import java.awt.SystemColor;
/*      */ import java.awt.TextArea;
/*      */ import java.awt.TextField;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.AdjustmentEvent;
/*      */ import java.awt.event.AdjustmentListener;
/*      */ import java.awt.event.FocusEvent;
/*      */ import java.awt.event.FocusListener;
/*      */ import java.awt.event.ItemEvent;
/*      */ import java.awt.event.ItemListener;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.KeyListener;
/*      */ import java.awt.event.TextEvent;
/*      */ import java.awt.event.TextListener;
/*      */ import java.awt.event.WindowEvent;
/*      */ import java.awt.event.WindowListener;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Locale;
/*      */ import java.util.Vector;
/*      */ 
/*      */ public class GenericDialog extends Dialog
/*      */   implements ActionListener, TextListener, FocusListener, ItemListener, KeyListener, AdjustmentListener, WindowListener
/*      */ {
/*      */   public static final int MAX_SLIDERS = 25;
/*      */   protected Vector numberField;
/*      */   protected Vector stringField;
/*      */   protected Vector checkbox;
/*      */   protected Vector choice;
/*      */   protected Vector slider;
/*      */   protected TextArea textArea1;
/*      */   protected TextArea textArea2;
/*      */   protected Vector defaultValues;
/*      */   protected Vector defaultText;
/*      */   protected Component theLabel;
/*      */   private Button cancel;
/*      */   private Button okay;
/*      */   private Button no;
/*      */   private Button help;
/*   49 */   private String okLabel = "  OK  ";
/*   50 */   private String helpLabel = "Help";
/*      */   private boolean wasCanceled;
/*      */   private boolean wasOKed;
/*      */   private int y;
/*      */   private int nfIndex;
/*      */   private int sfIndex;
/*      */   private int cbIndex;
/*      */   private int choiceIndex;
/*      */   private int textAreaIndex;
/*      */   private GridBagLayout grid;
/*      */   private GridBagConstraints c;
/*   56 */   private boolean firstNumericField = true;
/*   57 */   private boolean firstSlider = true;
/*      */   private boolean invalidNumber;
/*      */   private String errorMessage;
/*   60 */   private boolean firstPaint = true;
/*      */   private Hashtable labels;
/*      */   private boolean macro;
/*      */   private String macroOptions;
/*      */   private int topInset;
/*      */   private int leftInset;
/*      */   private int bottomInset;
/*      */   private boolean customInsets;
/*      */   private int[] sliderIndexes;
/*      */   private double[] sliderScales;
/*      */   private Checkbox previewCheckbox;
/*      */   private Vector dialogListeners;
/*      */   private PlugInFilterRunner pfr;
/*   71 */   private String previewLabel = " Preview";
/*      */   private static final String previewRunning = "wait...";
/*      */   private boolean recorderOn;
/*      */   private boolean yesNoCancel;
/*      */   private char echoChar;
/*      */   private boolean hideCancelButton;
/*   77 */   private boolean centerDialog = true;
/*      */   private String helpURL;
/*      */   private String yesLabel;
/*      */   private String noLabel;
/*      */ 
/*      */   public GenericDialog(String title)
/*      */   {
/*   86 */     this(title, IJ.getInstance() != null ? IJ.getInstance() : WindowManager.getCurrentImage() != null ? WindowManager.getCurrentImage().getWindow() : new Frame());
/*      */   }
/*      */ 
/*      */   public GenericDialog(String title, Frame parent)
/*      */   {
/*   92 */     super(parent == null ? new Frame() : parent, title, true);
/*   93 */     if (Prefs.blackCanvas) {
/*   94 */       setForeground(SystemColor.controlText);
/*   95 */       setBackground(SystemColor.control);
/*      */     }
/*      */ 
/*   99 */     this.grid = new GridBagLayout();
/*  100 */     this.c = new GridBagConstraints();
/*  101 */     setLayout(this.grid);
/*  102 */     this.macroOptions = Macro.getOptions();
/*  103 */     this.macro = (this.macroOptions != null);
/*  104 */     addKeyListener(this);
/*  105 */     addWindowListener(this);
/*      */   }
/*      */ 
/*      */   public void addNumericField(String label, double defaultValue, int digits)
/*      */   {
/*  123 */     addNumericField(label, defaultValue, digits, 6, null);
/*      */   }
/*      */ 
/*      */   public void addNumericField(String label, double defaultValue, int digits, int columns, String units)
/*      */   {
/*  135 */     String label2 = label;
/*  136 */     if (label2.indexOf('_') != -1)
/*  137 */       label2 = label2.replace('_', ' ');
/*  138 */     Label theLabel = makeLabel(label2);
/*  139 */     this.c.gridx = 0; this.c.gridy = this.y;
/*  140 */     this.c.anchor = 13;
/*  141 */     this.c.gridwidth = 1;
/*  142 */     if (this.firstNumericField)
/*  143 */       this.c.insets = getInsets(5, 0, 3, 0);
/*      */     else
/*  145 */       this.c.insets = getInsets(0, 0, 3, 0);
/*  146 */     this.grid.setConstraints(theLabel, this.c);
/*  147 */     add(theLabel);
/*  148 */     if (this.numberField == null) {
/*  149 */       this.numberField = new Vector(5);
/*  150 */       this.defaultValues = new Vector(5);
/*  151 */       this.defaultText = new Vector(5);
/*      */     }
/*  153 */     if (IJ.isWindows()) columns -= 2;
/*  154 */     if (columns < 1) columns = 1;
/*  155 */     TextField tf = new TextField(IJ.d2s(defaultValue, digits), columns);
/*  156 */     if (IJ.isLinux()) tf.setBackground(Color.white);
/*  157 */     tf.addActionListener(this);
/*  158 */     tf.addTextListener(this);
/*  159 */     tf.addFocusListener(this);
/*  160 */     tf.addKeyListener(this);
/*  161 */     this.numberField.addElement(tf);
/*  162 */     this.defaultValues.addElement(new Double(defaultValue));
/*  163 */     this.defaultText.addElement(tf.getText());
/*  164 */     this.c.gridx = 1; this.c.gridy = this.y;
/*  165 */     this.c.anchor = 17;
/*  166 */     tf.setEditable(true);
/*      */ 
/*  168 */     this.firstNumericField = false;
/*  169 */     if ((units == null) || (units.equals(""))) {
/*  170 */       this.grid.setConstraints(tf, this.c);
/*  171 */       add(tf);
/*      */     } else {
/*  173 */       Panel panel = new Panel();
/*  174 */       panel.setLayout(new FlowLayout(0, 0, 0));
/*  175 */       panel.add(tf);
/*  176 */       panel.add(new Label(" " + units));
/*  177 */       this.grid.setConstraints(panel, this.c);
/*  178 */       add(panel);
/*      */     }
/*  180 */     if ((Recorder.record) || (this.macro))
/*  181 */       saveLabel(tf, label);
/*  182 */     this.y += 1;
/*      */   }
/*      */ 
/*      */   private Label makeLabel(String label) {
/*  186 */     if (IJ.isMacintosh())
/*  187 */       label = label + " ";
/*  188 */     return new Label(label);
/*      */   }
/*      */ 
/*      */   private void saveLabel(Component component, String label) {
/*  192 */     if (this.labels == null)
/*  193 */       this.labels = new Hashtable();
/*  194 */     if (label.length() > 0) {
/*  195 */       if (label.charAt(0) == ' ')
/*  196 */         label = label.trim();
/*  197 */       this.labels.put(component, label);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addStringField(String label, String defaultText)
/*      */   {
/*  206 */     addStringField(label, defaultText, 8);
/*      */   }
/*      */ 
/*      */   public void addStringField(String label, String defaultText, int columns)
/*      */   {
/*  215 */     String label2 = label;
/*  216 */     if (label2.indexOf('_') != -1)
/*  217 */       label2 = label2.replace('_', ' ');
/*  218 */     Label theLabel = makeLabel(label2);
/*  219 */     this.c.gridx = 0; this.c.gridy = this.y;
/*  220 */     this.c.anchor = 13;
/*  221 */     this.c.gridwidth = 1;
/*  222 */     boolean custom = this.customInsets;
/*  223 */     if (this.stringField == null) {
/*  224 */       this.stringField = new Vector(4);
/*  225 */       this.c.insets = getInsets(5, 0, 5, 0);
/*      */     } else {
/*  227 */       this.c.insets = getInsets(0, 0, 5, 0);
/*  228 */     }this.grid.setConstraints(theLabel, this.c);
/*  229 */     add(theLabel);
/*  230 */     if (custom) {
/*  231 */       if (this.stringField.size() == 0)
/*  232 */         this.c.insets = getInsets(5, 0, 5, 0);
/*      */       else
/*  234 */         this.c.insets = getInsets(0, 0, 5, 0);
/*      */     }
/*  236 */     TextField tf = new TextField(defaultText, columns);
/*  237 */     if (IJ.isLinux()) tf.setBackground(Color.white);
/*  238 */     tf.setEchoChar(this.echoChar);
/*  239 */     this.echoChar = '\000';
/*  240 */     tf.addActionListener(this);
/*  241 */     tf.addTextListener(this);
/*  242 */     tf.addFocusListener(this);
/*  243 */     tf.addKeyListener(this);
/*  244 */     this.c.gridx = 1; this.c.gridy = this.y;
/*  245 */     this.c.anchor = 17;
/*  246 */     this.grid.setConstraints(tf, this.c);
/*  247 */     tf.setEditable(true);
/*  248 */     add(tf);
/*  249 */     this.stringField.addElement(tf);
/*  250 */     if ((Recorder.record) || (this.macro))
/*  251 */       saveLabel(tf, label);
/*  252 */     this.y += 1;
/*      */   }
/*      */ 
/*      */   public void setEchoChar(char echoChar)
/*      */   {
/*  257 */     this.echoChar = echoChar;
/*      */   }
/*      */ 
/*      */   public void addCheckbox(String label, boolean defaultValue)
/*      */   {
/*  265 */     addCheckbox(label, defaultValue, false);
/*      */   }
/*      */ 
/*      */   private void addCheckbox(String label, boolean defaultValue, boolean isPreview)
/*      */   {
/*  273 */     String label2 = label;
/*  274 */     if (label2.indexOf('_') != -1)
/*  275 */       label2 = label2.replace('_', ' ');
/*  276 */     if (this.checkbox == null) {
/*  277 */       this.checkbox = new Vector(4);
/*  278 */       this.c.insets = getInsets(15, 20, 0, 0);
/*      */     } else {
/*  280 */       this.c.insets = getInsets(0, 20, 0, 0);
/*  281 */     }this.c.gridx = 0; this.c.gridy = this.y;
/*  282 */     this.c.gridwidth = 2;
/*  283 */     this.c.anchor = 17;
/*  284 */     Checkbox cb = new Checkbox(label2);
/*  285 */     this.grid.setConstraints(cb, this.c);
/*  286 */     cb.setState(defaultValue);
/*  287 */     cb.addItemListener(this);
/*  288 */     cb.addKeyListener(this);
/*  289 */     add(cb);
/*  290 */     this.checkbox.addElement(cb);
/*      */ 
/*  292 */     if ((!isPreview) && ((Recorder.record) || (this.macro)))
/*  293 */       saveLabel(cb, label);
/*  294 */     if (isPreview) this.previewCheckbox = cb;
/*  295 */     this.y += 1;
/*      */   }
/*      */ 
/*      */   public void addPreviewCheckbox(PlugInFilterRunner pfr)
/*      */   {
/*  316 */     if (this.previewCheckbox != null)
/*  317 */       return;
/*  318 */     ImagePlus imp = WindowManager.getCurrentImage();
/*  319 */     if ((imp != null) && (imp.isComposite()) && (((CompositeImage)imp).getMode() == 1))
/*  320 */       return;
/*  321 */     this.pfr = pfr;
/*  322 */     addCheckbox(this.previewLabel, false, true);
/*      */   }
/*      */ 
/*      */   public void addPreviewCheckbox(PlugInFilterRunner pfr, String label)
/*      */   {
/*  332 */     if (this.previewCheckbox != null) {
/*  333 */       return;
/*      */     }
/*      */ 
/*  337 */     this.previewLabel = label;
/*  338 */     this.pfr = pfr;
/*  339 */     addCheckbox(this.previewLabel, false, true);
/*      */   }
/*      */ 
/*      */   public void addCheckboxGroup(int rows, int columns, String[] labels, boolean[] defaultValues)
/*      */   {
/*  349 */     addCheckboxGroup(rows, columns, labels, defaultValues, null);
/*      */   }
/*      */ 
/*      */   public void addCheckboxGroup(int rows, int columns, String[] labels, boolean[] defaultValues, String[] headings)
/*      */   {
/*  361 */     Panel panel = new Panel();
/*  362 */     int nRows = headings != null ? rows + 1 : rows;
/*  363 */     panel.setLayout(new GridLayout(nRows, columns, 6, 0));
/*  364 */     int startCBIndex = this.cbIndex;
/*  365 */     if (this.checkbox == null)
/*  366 */       this.checkbox = new Vector(12);
/*  367 */     if (headings != null) {
/*  368 */       Font font = new Font("SansSerif", 1, 12);
/*  369 */       for (int i = 0; i < columns; i++) {
/*  370 */         if ((i > headings.length - 1) || (headings[i] == null)) {
/*  371 */           panel.add(new Label(""));
/*      */         } else {
/*  373 */           Label label = new Label(headings[i]);
/*  374 */           label.setFont(font);
/*  375 */           panel.add(label);
/*      */         }
/*      */       }
/*      */     }
/*  379 */     int i1 = 0;
/*  380 */     int[] index = new int[labels.length];
/*  381 */     for (int row = 0; row < rows; row++)
/*  382 */       for (int col = 0; col < columns; col++) {
/*  383 */         int i2 = col * rows + row;
/*  384 */         if (i2 >= labels.length) break;
/*  385 */         index[i1] = i2;
/*  386 */         String label = labels[i1];
/*  387 */         if ((label == null) || (label.length() == 0)) {
/*  388 */           Label lbl = new Label("");
/*  389 */           panel.add(lbl);
/*  390 */           i1++;
/*      */         }
/*      */         else {
/*  393 */           if (label.indexOf('_') != -1)
/*  394 */             label = label.replace('_', ' ');
/*  395 */           Checkbox cb = new Checkbox(label);
/*  396 */           this.checkbox.addElement(cb);
/*  397 */           cb.setState(defaultValues[i1]);
/*  398 */           cb.addItemListener(this);
/*  399 */           if ((Recorder.record) || (this.macro))
/*  400 */             saveLabel(cb, labels[i1]);
/*  401 */           if (IJ.isLinux()) {
/*  402 */             Panel panel2 = new Panel();
/*  403 */             panel2.setLayout(new BorderLayout());
/*  404 */             panel2.add("West", cb);
/*  405 */             panel.add(panel2);
/*      */           } else {
/*  407 */             panel.add(cb);
/*  408 */           }i1++;
/*      */         }
/*      */       }
/*  411 */     this.c.gridx = 0; this.c.gridy = this.y;
/*  412 */     this.c.gridwidth = 2;
/*  413 */     this.c.anchor = 17;
/*  414 */     this.c.insets = getInsets(10, 0, 0, 0);
/*  415 */     this.grid.setConstraints(panel, this.c);
/*  416 */     add(panel);
/*  417 */     this.y += 1;
/*      */   }
/*      */ 
/*      */   public void addChoice(String label, String[] items, String defaultItem)
/*      */   {
/*  426 */     String label2 = label;
/*  427 */     if (label2.indexOf('_') != -1)
/*  428 */       label2 = label2.replace('_', ' ');
/*  429 */     Label theLabel = makeLabel(label2);
/*  430 */     this.c.gridx = 0; this.c.gridy = this.y;
/*  431 */     this.c.anchor = 13;
/*  432 */     this.c.gridwidth = 1;
/*  433 */     if (this.choice == null) {
/*  434 */       this.choice = new Vector(4);
/*  435 */       this.c.insets = getInsets(5, 0, 5, 0);
/*      */     } else {
/*  437 */       this.c.insets = getInsets(0, 0, 5, 0);
/*  438 */     }this.grid.setConstraints(theLabel, this.c);
/*  439 */     add(theLabel);
/*  440 */     Choice thisChoice = new Choice();
/*  441 */     thisChoice.addKeyListener(this);
/*  442 */     thisChoice.addItemListener(this);
/*  443 */     for (int i = 0; i < items.length; i++)
/*  444 */       thisChoice.addItem(items[i]);
/*  445 */     thisChoice.select(defaultItem);
/*  446 */     this.c.gridx = 1; this.c.gridy = this.y;
/*  447 */     this.c.anchor = 17;
/*  448 */     this.grid.setConstraints(thisChoice, this.c);
/*  449 */     add(thisChoice);
/*  450 */     this.choice.addElement(thisChoice);
/*  451 */     if ((Recorder.record) || (this.macro))
/*  452 */       saveLabel(thisChoice, label);
/*  453 */     this.y += 1;
/*      */   }
/*      */ 
/*      */   public void addMessage(String text)
/*      */   {
/*  458 */     addMessage(text, null);
/*      */   }
/*      */ 
/*      */   public void addMessage(String text, Font font)
/*      */   {
/*  464 */     this.theLabel = null;
/*  465 */     if (text.indexOf('\n') >= 0)
/*  466 */       this.theLabel = new MultiLineLabel(text);
/*      */     else {
/*  468 */       this.theLabel = new Label(text);
/*      */     }
/*  470 */     this.c.gridx = 0; this.c.gridy = this.y;
/*  471 */     this.c.gridwidth = 2;
/*  472 */     this.c.anchor = 17;
/*  473 */     this.c.insets = getInsets(text.equals("") ? 0 : 10, 20, 0, 0);
/*  474 */     this.c.fill = 2;
/*  475 */     this.grid.setConstraints(this.theLabel, this.c);
/*  476 */     if (font != null)
/*  477 */       this.theLabel.setFont(font);
/*  478 */     add(this.theLabel);
/*  479 */     this.c.fill = 0;
/*  480 */     this.y += 1;
/*      */   }
/*      */ 
/*      */   public void addTextAreas(String text1, String text2, int rows, int columns)
/*      */   {
/*  490 */     if (this.textArea1 != null) return;
/*  491 */     Panel panel = new Panel();
/*  492 */     this.textArea1 = new TextArea(text1, rows, columns, 3);
/*  493 */     if (IJ.isLinux()) this.textArea1.setBackground(Color.white);
/*  494 */     this.textArea1.addTextListener(this);
/*  495 */     panel.add(this.textArea1);
/*  496 */     if (text2 != null) {
/*  497 */       this.textArea2 = new TextArea(text2, rows, columns, 3);
/*  498 */       if (IJ.isLinux()) this.textArea2.setBackground(Color.white);
/*  499 */       panel.add(this.textArea2);
/*      */     }
/*  501 */     this.c.gridx = 0; this.c.gridy = this.y;
/*  502 */     this.c.gridwidth = 2;
/*  503 */     this.c.anchor = 17;
/*  504 */     this.c.insets = getInsets(15, 20, 0, 0);
/*  505 */     this.grid.setConstraints(panel, this.c);
/*  506 */     add(panel);
/*  507 */     this.y += 1;
/*      */   }
/*      */ 
/*      */   public void addSlider(String label, double minValue, double maxValue, double defaultValue)
/*      */   {
/*  520 */     int columns = 4;
/*  521 */     int digits = 0;
/*  522 */     double scale = 1.0D;
/*  523 */     if ((maxValue - minValue <= 5.0D) && ((minValue != (int)minValue) || (maxValue != (int)maxValue) || (defaultValue != (int)defaultValue))) {
/*  524 */       scale = 20.0D;
/*  525 */       minValue *= scale;
/*  526 */       maxValue *= scale;
/*  527 */       defaultValue *= scale;
/*  528 */       digits = 2;
/*      */     }
/*  530 */     String label2 = label;
/*  531 */     if (label2.indexOf('_') != -1)
/*  532 */       label2 = label2.replace('_', ' ');
/*  533 */     Label theLabel = makeLabel(label2);
/*  534 */     this.c.gridx = 0; this.c.gridy = this.y;
/*  535 */     this.c.anchor = 13;
/*  536 */     this.c.gridwidth = 1;
/*  537 */     this.c.insets = new Insets(0, 0, 3, 0);
/*  538 */     this.grid.setConstraints(theLabel, this.c);
/*  539 */     add(theLabel);
/*      */ 
/*  541 */     if (this.slider == null) {
/*  542 */       this.slider = new Vector(5);
/*  543 */       this.sliderIndexes = new int[25];
/*  544 */       this.sliderScales = new double[25];
/*      */     }
/*  546 */     Scrollbar s = new Scrollbar(0, (int)defaultValue, 1, (int)minValue, (int)maxValue + 1);
/*  547 */     this.slider.addElement(s);
/*  548 */     s.addAdjustmentListener(this);
/*  549 */     s.setUnitIncrement(1);
/*      */ 
/*  551 */     if (this.numberField == null) {
/*  552 */       this.numberField = new Vector(5);
/*  553 */       this.defaultValues = new Vector(5);
/*  554 */       this.defaultText = new Vector(5);
/*      */     }
/*  556 */     if (IJ.isWindows()) columns -= 2;
/*  557 */     if (columns < 1) columns = 1;
/*  558 */     TextField tf = new TextField(IJ.d2s(defaultValue / scale, digits), columns);
/*  559 */     if (IJ.isLinux()) tf.setBackground(Color.white);
/*  560 */     tf.addActionListener(this);
/*  561 */     tf.addTextListener(this);
/*  562 */     tf.addFocusListener(this);
/*  563 */     tf.addKeyListener(this);
/*  564 */     this.numberField.addElement(tf);
/*  565 */     this.sliderIndexes[(this.slider.size() - 1)] = (this.numberField.size() - 1);
/*  566 */     this.sliderScales[(this.slider.size() - 1)] = scale;
/*  567 */     this.defaultValues.addElement(new Double(defaultValue / scale));
/*  568 */     this.defaultText.addElement(tf.getText());
/*  569 */     tf.setEditable(true);
/*      */ 
/*  571 */     this.firstSlider = false;
/*      */ 
/*  573 */     Panel panel = new Panel();
/*  574 */     GridBagLayout pgrid = new GridBagLayout();
/*  575 */     GridBagConstraints pc = new GridBagConstraints();
/*  576 */     panel.setLayout(pgrid);
/*      */ 
/*  585 */     pc.gridx = 0; pc.gridy = 0;
/*  586 */     pc.gridwidth = 1;
/*  587 */     pc.ipadx = 75;
/*  588 */     pc.anchor = 17;
/*  589 */     pgrid.setConstraints(s, pc);
/*  590 */     panel.add(s);
/*  591 */     pc.ipadx = 0;
/*      */ 
/*  593 */     pc.gridx = 1;
/*  594 */     pc.insets = new Insets(5, 5, 0, 0);
/*  595 */     pc.anchor = 13;
/*  596 */     pgrid.setConstraints(tf, pc);
/*  597 */     panel.add(tf);
/*      */ 
/*  599 */     this.grid.setConstraints(panel, this.c);
/*  600 */     this.c.gridx = 1; this.c.gridy = this.y;
/*  601 */     this.c.gridwidth = 1;
/*  602 */     this.c.anchor = 17;
/*  603 */     this.c.insets = new Insets(0, 0, 0, 0);
/*  604 */     this.grid.setConstraints(panel, this.c);
/*  605 */     add(panel);
/*  606 */     this.y += 1;
/*  607 */     if ((Recorder.record) || (this.macro))
/*  608 */       saveLabel(tf, label);
/*      */   }
/*      */ 
/*      */   public void addPanel(Panel panel)
/*      */   {
/*  613 */     addPanel(panel, 17, new Insets(5, 0, 0, 0));
/*      */   }
/*      */ 
/*      */   public void addPanel(Panel panel, int contraints, Insets insets)
/*      */   {
/*  620 */     this.c.gridx = 0; this.c.gridy = this.y;
/*  621 */     this.c.gridwidth = 2;
/*  622 */     this.c.anchor = contraints;
/*  623 */     this.c.insets = insets;
/*  624 */     this.grid.setConstraints(panel, this.c);
/*  625 */     add(panel);
/*  626 */     this.y += 1;
/*      */   }
/*      */ 
/*      */   public void setInsets(int top, int left, int bottom)
/*      */   {
/*  642 */     this.topInset = top;
/*  643 */     this.leftInset = left;
/*  644 */     this.bottomInset = bottom;
/*  645 */     this.customInsets = true;
/*      */   }
/*      */ 
/*      */   public void setOKLabel(String label)
/*      */   {
/*  650 */     this.okLabel = label;
/*      */   }
/*      */ 
/*      */   public void setHelpLabel(String label)
/*      */   {
/*  655 */     this.helpLabel = label;
/*      */   }
/*      */ 
/*      */   public void enableYesNoCancel()
/*      */   {
/*  660 */     enableYesNoCancel(" Yes ", " No ");
/*      */   }
/*      */ 
/*      */   public void enableYesNoCancel(String yesLabel, String noLabel)
/*      */   {
/*  678 */     this.yesLabel = yesLabel;
/*  679 */     this.noLabel = noLabel;
/*  680 */     this.yesNoCancel = true;
/*      */   }
/*      */ 
/*      */   public void hideCancelButton()
/*      */   {
/*  685 */     this.hideCancelButton = true;
/*      */   }
/*      */ 
/*      */   Insets getInsets(int top, int left, int bottom, int right) {
/*  689 */     if (this.customInsets) {
/*  690 */       this.customInsets = false;
/*  691 */       return new Insets(this.topInset, this.leftInset, this.bottomInset, 0);
/*      */     }
/*  693 */     return new Insets(top, left, bottom, right);
/*      */   }
/*      */ 
/*      */   public void addDialogListener(DialogListener dl)
/*      */   {
/*  706 */     if (this.dialogListeners == null)
/*  707 */       this.dialogListeners = new Vector();
/*  708 */     this.dialogListeners.addElement(dl);
/*  709 */     if (IJ.debugMode) IJ.log("GenericDialog: Listener added: " + dl);
/*      */   }
/*      */ 
/*      */   public boolean wasCanceled()
/*      */   {
/*  714 */     if (this.wasCanceled)
/*  715 */       Macro.abort();
/*  716 */     return this.wasCanceled;
/*      */   }
/*      */ 
/*      */   public boolean wasOKed()
/*      */   {
/*  721 */     return (this.wasOKed) || (this.macro);
/*      */   }
/*      */ 
/*      */   public double getNextNumber()
/*      */   {
/*  727 */     if (this.numberField == null)
/*  728 */       return -1.0D;
/*  729 */     TextField tf = (TextField)this.numberField.elementAt(this.nfIndex);
/*  730 */     String theText = tf.getText();
/*  731 */     String label = null;
/*  732 */     if (this.macro) {
/*  733 */       label = (String)this.labels.get(tf);
/*  734 */       theText = Macro.getValue(this.macroOptions, label, theText);
/*      */     }
/*      */ 
/*  737 */     String originalText = (String)this.defaultText.elementAt(this.nfIndex);
/*  738 */     double defaultValue = ((Double)this.defaultValues.elementAt(this.nfIndex)).doubleValue();
/*      */     double value;
/*      */     double value;
/*  740 */     if (theText.equals(originalText)) {
/*  741 */       value = defaultValue;
/*      */     } else {
/*  743 */       Double d = getValue(theText);
/*      */       double value;
/*  744 */       if (d != null) {
/*  745 */         value = d.doubleValue();
/*      */       }
/*      */       else {
/*  748 */         if (theText.startsWith("&")) theText = theText.substring(1);
/*  749 */         Interpreter interp = Interpreter.getInstance();
/*  750 */         value = interp != null ? interp.getVariable2(theText) : (0.0D / 0.0D);
/*  751 */         if (Double.isNaN(value)) {
/*  752 */           this.invalidNumber = true;
/*  753 */           this.errorMessage = ("\"" + theText + "\" is an invalid number");
/*  754 */           value = (0.0D / 0.0D);
/*  755 */           if (this.macro) {
/*  756 */             IJ.error("Macro Error", "Numeric value expected in run() function\n \n   Dialog box title: \"" + getTitle() + "\"\n" + "   Key: \"" + label.toLowerCase(Locale.US) + "\"\n" + "   Value or variable name: \"" + theText + "\"");
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  764 */     if (this.recorderOn)
/*  765 */       recordOption(tf, trim(theText));
/*  766 */     this.nfIndex += 1;
/*  767 */     return value;
/*      */   }
/*      */ 
/*      */   private String trim(String value) {
/*  771 */     if (value.endsWith(".0"))
/*  772 */       value = value.substring(0, value.length() - 2);
/*  773 */     if (value.endsWith(".00"))
/*  774 */       value = value.substring(0, value.length() - 3);
/*  775 */     return value;
/*      */   }
/*      */ 
/*      */   private void recordOption(Component component, String value) {
/*  779 */     String label = (String)this.labels.get(component);
/*  780 */     if (value.equals("")) value = "[]";
/*  781 */     Recorder.recordOption(label, value);
/*      */   }
/*      */ 
/*      */   private void recordCheckboxOption(Checkbox cb) {
/*  785 */     String label = (String)this.labels.get(cb);
/*  786 */     if (label != null)
/*  787 */       if (cb.getState())
/*  788 */         Recorder.recordOption(label);
/*  789 */       else if (Recorder.getCommandOptions() == null)
/*  790 */         Recorder.recordOption(" ");
/*      */   }
/*      */ 
/*      */   protected Double getValue(String text) {
/*      */     Double d;
/*      */     try {
/*  796 */       d = new Double(text);
/*      */     } catch (NumberFormatException e) {
/*  798 */       d = null;
/*      */     }
/*  800 */     return d;
/*      */   }
/*      */ 
/*      */   public double parseDouble(String s) {
/*  804 */     if (s == null) return (0.0D / 0.0D);
/*  805 */     double value = Tools.parseDouble(s);
/*  806 */     if (Double.isNaN(value)) {
/*  807 */       if (s.startsWith("&")) s = s.substring(1);
/*  808 */       Interpreter interp = Interpreter.getInstance();
/*  809 */       value = interp != null ? interp.getVariable2(s) : (0.0D / 0.0D);
/*      */     }
/*  811 */     return value;
/*      */   }
/*      */ 
/*      */   public boolean invalidNumber()
/*      */   {
/*  817 */     boolean wasInvalid = this.invalidNumber;
/*  818 */     this.invalidNumber = false;
/*  819 */     return wasInvalid;
/*      */   }
/*      */ 
/*      */   public String getErrorMessage()
/*      */   {
/*  825 */     return this.errorMessage;
/*      */   }
/*      */ 
/*      */   public String getNextString()
/*      */   {
/*  831 */     if (this.stringField == null)
/*  832 */       return "";
/*  833 */     TextField tf = (TextField)this.stringField.elementAt(this.sfIndex);
/*  834 */     String theText = tf.getText();
/*  835 */     if (this.macro) {
/*  836 */       String label = (String)this.labels.get(tf);
/*  837 */       theText = Macro.getValue(this.macroOptions, label, theText);
/*  838 */       if ((theText != null) && ((theText.startsWith("&")) || (label.toLowerCase(Locale.US).startsWith(theText))))
/*      */       {
/*  840 */         if (theText.startsWith("&")) theText = theText.substring(1);
/*  841 */         Interpreter interp = Interpreter.getInstance();
/*  842 */         String s = interp != null ? interp.getVariableAsString(theText) : null;
/*  843 */         if (s != null) theText = s;
/*      */       }
/*      */     }
/*  846 */     if (this.recorderOn)
/*  847 */       recordOption(tf, theText);
/*  848 */     this.sfIndex += 1;
/*  849 */     return theText;
/*      */   }
/*      */ 
/*      */   public boolean getNextBoolean()
/*      */   {
/*  854 */     if (this.checkbox == null)
/*  855 */       return false;
/*  856 */     Checkbox cb = (Checkbox)this.checkbox.elementAt(this.cbIndex);
/*  857 */     if (this.recorderOn)
/*  858 */       recordCheckboxOption(cb);
/*  859 */     boolean state = cb.getState();
/*  860 */     if (this.macro) {
/*  861 */       String label = (String)this.labels.get(cb);
/*  862 */       String key = Macro.trimKey(label);
/*  863 */       state = isMatch(this.macroOptions, key + " ");
/*      */     }
/*  865 */     this.cbIndex += 1;
/*  866 */     return state;
/*      */   }
/*      */ 
/*      */   boolean isMatch(String s1, String s2)
/*      */   {
/*  871 */     if (s1.startsWith(s2))
/*  872 */       return true;
/*  873 */     s2 = " " + s2;
/*  874 */     int len1 = s1.length();
/*  875 */     int len2 = s2.length();
/*  876 */     boolean inLiteral = false;
/*      */ 
/*  878 */     for (int i = 0; i < len1 - len2 + 1; i++) {
/*  879 */       char c = s1.charAt(i);
/*  880 */       if ((inLiteral) && (c == ']'))
/*  881 */         inLiteral = false;
/*  882 */       else if (c == '[')
/*  883 */         inLiteral = true;
/*  884 */       if ((c == s2.charAt(0)) && (!inLiteral) && ((i <= 1) || (s1.charAt(i - 1) != '=')))
/*      */       {
/*  886 */         boolean match = true;
/*  887 */         for (int j = 0; j < len2; j++)
/*  888 */           if (s2.charAt(j) != s1.charAt(i + j)) {
/*  889 */             match = false; break;
/*      */           }
/*  891 */         if (match) return true; 
/*      */       }
/*      */     }
/*  893 */     return false;
/*      */   }
/*      */ 
/*      */   public String getNextChoice()
/*      */   {
/*  898 */     if (this.choice == null)
/*  899 */       return "";
/*  900 */     Choice thisChoice = (Choice)this.choice.elementAt(this.choiceIndex);
/*  901 */     String item = thisChoice.getSelectedItem();
/*  902 */     if (this.macro) {
/*  903 */       String label = (String)this.labels.get(thisChoice);
/*  904 */       item = Macro.getValue(this.macroOptions, label, item);
/*  905 */       if ((item != null) && (item.startsWith("&")))
/*  906 */         item = getChoiceVariable(item);
/*      */     }
/*  908 */     if (this.recorderOn)
/*  909 */       recordOption(thisChoice, item);
/*  910 */     this.choiceIndex += 1;
/*  911 */     return item;
/*      */   }
/*      */ 
/*      */   public int getNextChoiceIndex()
/*      */   {
/*  916 */     if (this.choice == null)
/*  917 */       return -1;
/*  918 */     Choice thisChoice = (Choice)this.choice.elementAt(this.choiceIndex);
/*  919 */     int index = thisChoice.getSelectedIndex();
/*  920 */     if (this.macro) {
/*  921 */       String label = (String)this.labels.get(thisChoice);
/*  922 */       String oldItem = thisChoice.getSelectedItem();
/*  923 */       int oldIndex = thisChoice.getSelectedIndex();
/*  924 */       String item = Macro.getValue(this.macroOptions, label, oldItem);
/*  925 */       if ((item != null) && (item.startsWith("&")))
/*  926 */         item = getChoiceVariable(item);
/*  927 */       thisChoice.select(item);
/*  928 */       index = thisChoice.getSelectedIndex();
/*  929 */       if ((index == oldIndex) && (!item.equals(oldItem)))
/*      */       {
/*  931 */         Interpreter interp = Interpreter.getInstance();
/*  932 */         String s = interp != null ? interp.getStringVariable(item) : null;
/*  933 */         if (s == null)
/*  934 */           IJ.error(getTitle(), "\"" + item + "\" is not a valid choice for \"" + label + "\"");
/*      */         else
/*  936 */           item = s;
/*      */       }
/*      */     }
/*  939 */     if (this.recorderOn)
/*  940 */       recordOption(thisChoice, thisChoice.getSelectedItem());
/*  941 */     this.choiceIndex += 1;
/*  942 */     return index;
/*      */   }
/*      */ 
/*      */   private String getChoiceVariable(String item) {
/*  946 */     item = item.substring(1);
/*  947 */     Interpreter interp = Interpreter.getInstance();
/*  948 */     String s = interp != null ? interp.getStringVariable(item) : null;
/*  949 */     if (s != null) item = s;
/*  950 */     return item;
/*      */   }
/*      */ 
/*      */   public String getNextText()
/*      */   {
/*      */     String text;
/*  956 */     if ((this.textAreaIndex == 0) && (this.textArea1 != null))
/*      */     {
/*  958 */       String text = this.textArea1.getText();
/*  959 */       this.textAreaIndex += 1;
/*  960 */       if (this.macro)
/*  961 */         text = Macro.getValue(this.macroOptions, "text1", text);
/*  962 */       if (this.recorderOn) {
/*  963 */         String text2 = text;
/*  964 */         String cmd = Recorder.getCommand();
/*  965 */         if ((cmd != null) && (cmd.equals("Convolve..."))) {
/*  966 */           text2 = text.replaceAll("\n", "\\\\n");
/*  967 */           if (!text.endsWith("\n")) text2 = text2 + "\\n"; 
/*      */         }
/*  969 */         else { text2 = text.replace('\n', ' '); }
/*  970 */         Recorder.recordOption("text1", text2);
/*      */       }
/*  972 */     } else if ((this.textAreaIndex == 1) && (this.textArea2 != null)) {
/*  973 */       this.textArea2.selectAll();
/*  974 */       String text = this.textArea2.getText();
/*  975 */       this.textAreaIndex += 1;
/*  976 */       if (this.macro)
/*  977 */         text = Macro.getValue(this.macroOptions, "text2", text);
/*  978 */       if (this.recorderOn)
/*  979 */         Recorder.recordOption("text2", text.replace('\n', ' '));
/*      */     } else {
/*  981 */       text = null;
/*  982 */     }return text;
/*      */   }
/*      */ 
/*      */   public void showDialog()
/*      */   {
/*  987 */     if (this.macro) {
/*  988 */       dispose();
/*  989 */       this.recorderOn = ((Recorder.record) && (Recorder.recordInMacros));
/*      */     } else {
/*  991 */       if (this.pfr != null)
/*  992 */         this.pfr.setDialog(this);
/*  993 */       Panel buttons = new Panel();
/*  994 */       buttons.setLayout(new FlowLayout(1, 5, 0));
/*  995 */       this.cancel = new Button("Cancel");
/*  996 */       this.cancel.addActionListener(this);
/*  997 */       this.cancel.addKeyListener(this);
/*  998 */       if (this.yesNoCancel) {
/*  999 */         this.okLabel = this.yesLabel;
/* 1000 */         this.no = new Button(this.noLabel);
/* 1001 */         this.no.addActionListener(this);
/* 1002 */         this.no.addKeyListener(this);
/*      */       }
/* 1004 */       this.okay = new Button(this.okLabel);
/* 1005 */       this.okay.addActionListener(this);
/* 1006 */       this.okay.addKeyListener(this);
/* 1007 */       boolean addHelp = this.helpURL != null;
/* 1008 */       if (addHelp) {
/* 1009 */         this.help = new Button(this.helpLabel);
/* 1010 */         this.help.addActionListener(this);
/* 1011 */         this.help.addKeyListener(this);
/*      */       }
/* 1013 */       if (IJ.isMacintosh()) {
/* 1014 */         if (addHelp) buttons.add(this.help);
/* 1015 */         if (this.yesNoCancel) buttons.add(this.no);
/* 1016 */         if (!this.hideCancelButton) buttons.add(this.cancel);
/* 1017 */         buttons.add(this.okay);
/*      */       } else {
/* 1019 */         buttons.add(this.okay);
/* 1020 */         if (this.yesNoCancel) buttons.add(this.no);
/* 1021 */         if (!this.hideCancelButton)
/* 1022 */           buttons.add(this.cancel);
/* 1023 */         if (addHelp) buttons.add(this.help);
/*      */       }
/* 1025 */       this.c.gridx = 0; this.c.gridy = this.y;
/* 1026 */       this.c.anchor = 13;
/* 1027 */       this.c.gridwidth = 2;
/* 1028 */       this.c.insets = new Insets(15, 0, 0, 0);
/* 1029 */       this.grid.setConstraints(buttons, this.c);
/* 1030 */       add(buttons);
/* 1031 */       if (IJ.isMacintosh())
/* 1032 */         setResizable(false);
/* 1033 */       pack();
/* 1034 */       setup();
/* 1035 */       if (this.centerDialog) GUI.center(this);
/* 1036 */       setVisible(true);
/* 1037 */       this.recorderOn = Recorder.record;
/* 1038 */       IJ.wait(50);
/*      */     }
/*      */ 
/* 1041 */     if ((!this.wasCanceled) && (this.dialogListeners != null) && (this.dialogListeners.size() > 0)) {
/* 1042 */       resetCounters();
/* 1043 */       ((DialogListener)this.dialogListeners.elementAt(0)).dialogItemChanged(this, null);
/* 1044 */       this.recorderOn = false;
/*      */     }
/* 1046 */     resetCounters();
/*      */   }
/*      */ 
/*      */   private void resetCounters()
/*      */   {
/* 1051 */     this.nfIndex = 0;
/* 1052 */     this.sfIndex = 0;
/* 1053 */     this.cbIndex = 0;
/* 1054 */     this.choiceIndex = 0;
/* 1055 */     this.textAreaIndex = 0;
/* 1056 */     this.invalidNumber = false;
/*      */   }
/*      */ 
/*      */   public Vector getNumericFields()
/*      */   {
/* 1061 */     return this.numberField;
/*      */   }
/*      */ 
/*      */   public Vector getStringFields()
/*      */   {
/* 1066 */     return this.stringField;
/*      */   }
/*      */ 
/*      */   public Vector getCheckboxes()
/*      */   {
/* 1071 */     return this.checkbox;
/*      */   }
/*      */ 
/*      */   public Vector getChoices()
/*      */   {
/* 1076 */     return this.choice;
/*      */   }
/*      */ 
/*      */   public Vector getSliders()
/*      */   {
/* 1081 */     return this.slider;
/*      */   }
/*      */ 
/*      */   public TextArea getTextArea1()
/*      */   {
/* 1086 */     return this.textArea1;
/*      */   }
/*      */ 
/*      */   public TextArea getTextArea2()
/*      */   {
/* 1091 */     return this.textArea2;
/*      */   }
/*      */ 
/*      */   public Component getMessage()
/*      */   {
/* 1097 */     return this.theLabel;
/*      */   }
/*      */ 
/*      */   public Checkbox getPreviewCheckbox()
/*      */   {
/* 1102 */     return this.previewCheckbox;
/*      */   }
/*      */ 
/*      */   public Button[] getButtons()
/*      */   {
/* 1108 */     Button[] buttons = new Button[3];
/* 1109 */     buttons[0] = this.okay;
/* 1110 */     buttons[1] = this.cancel;
/* 1111 */     buttons[2] = this.no;
/* 1112 */     return buttons;
/*      */   }
/*      */ 
/*      */   public void previewRunning(boolean isRunning)
/*      */   {
/* 1119 */     if (this.previewCheckbox != null) {
/* 1120 */       this.previewCheckbox.setLabel(isRunning ? "wait..." : this.previewLabel);
/* 1121 */       if (IJ.isMacOSX()) repaint();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void centerDialog(boolean b)
/*      */   {
/* 1127 */     this.centerDialog = b;
/*      */   }
/*      */ 
/*      */   protected void setup() {
/*      */   }
/*      */ 
/*      */   public void actionPerformed(ActionEvent e) {
/* 1134 */     Object source = e.getSource();
/* 1135 */     if (source != this.okay) { if (((source == this.cancel ? 1 : 0) | (source == this.no ? 1 : 0)) == 0); } else {
/* 1136 */       this.wasCanceled = (source == this.cancel);
/* 1137 */       this.wasOKed = (source == this.okay);
/* 1138 */       dispose(); return;
/* 1139 */     }if (source == this.help) {
/* 1140 */       if (this.hideCancelButton) {
/* 1141 */         this.wasOKed = true;
/* 1142 */         dispose();
/*      */       }
/* 1144 */       showHelp();
/*      */     } else {
/* 1146 */       notifyListeners(e);
/*      */     }
/*      */   }
/*      */ 
/* 1150 */   public void textValueChanged(TextEvent e) { notifyListeners(e);
/* 1151 */     if (this.slider == null) return;
/* 1152 */     Object source = e.getSource();
/* 1153 */     for (int i = 0; i < this.slider.size(); i++) {
/* 1154 */       int index = this.sliderIndexes[i];
/* 1155 */       if (source == this.numberField.elementAt(index)) {
/* 1156 */         TextField tf = (TextField)this.numberField.elementAt(index);
/* 1157 */         double value = Tools.parseDouble(tf.getText());
/* 1158 */         if (!Double.isNaN(value)) {
/* 1159 */           Scrollbar sb = (Scrollbar)this.slider.elementAt(i);
/* 1160 */           sb.setValue((int)(value * this.sliderScales[i]));
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void itemStateChanged(ItemEvent e)
/*      */   {
/* 1168 */     notifyListeners(e);
/*      */   }
/*      */ 
/*      */   public void focusGained(FocusEvent e) {
/* 1172 */     Component c = e.getComponent();
/* 1173 */     if ((c instanceof TextField))
/* 1174 */       ((TextField)c).selectAll();
/*      */   }
/*      */ 
/*      */   public void focusLost(FocusEvent e) {
/* 1178 */     Component c = e.getComponent();
/* 1179 */     if ((c instanceof TextField))
/* 1180 */       ((TextField)c).select(0, 0);
/*      */   }
/*      */ 
/*      */   public void keyPressed(KeyEvent e) {
/* 1184 */     int keyCode = e.getKeyCode();
/* 1185 */     IJ.setKeyDown(keyCode);
/* 1186 */     if ((keyCode == 10) && (this.textArea1 == null)) {
/* 1187 */       this.wasOKed = true;
/* 1188 */       if ((IJ.isMacOSX()) && (IJ.isJava15()))
/* 1189 */         accessTextFields();
/* 1190 */       dispose();
/* 1191 */     } else if (keyCode == 27) {
/* 1192 */       this.wasCanceled = true;
/* 1193 */       dispose();
/* 1194 */       IJ.resetEscape();
/* 1195 */     } else if ((keyCode == 87) && ((e.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0)) {
/* 1196 */       this.wasCanceled = true;
/* 1197 */       dispose();
/*      */     }
/*      */   }
/*      */ 
/*      */   void accessTextFields()
/*      */   {
/* 1203 */     if (this.stringField != null) {
/* 1204 */       for (int i = 0; i < this.stringField.size(); i++)
/* 1205 */         ((TextField)this.stringField.elementAt(i)).getText();
/*      */     }
/* 1207 */     if (this.numberField != null)
/* 1208 */       for (int i = 0; i < this.numberField.size(); i++)
/* 1209 */         ((TextField)this.numberField.elementAt(i)).getText();
/*      */   }
/*      */ 
/*      */   public void keyReleased(KeyEvent e)
/*      */   {
/* 1214 */     int keyCode = e.getKeyCode();
/* 1215 */     IJ.setKeyUp(keyCode);
/* 1216 */     int flags = e.getModifiers();
/* 1217 */     boolean control = (flags & 0x2) != 0;
/* 1218 */     boolean meta = (flags & 0x4) != 0;
/* 1219 */     boolean shift = (flags & 0x1) != 0;
/* 1220 */     if ((keyCode == 71) && (shift) && ((control) || (meta)))
/* 1221 */       new ScreenGrabber().run(""); 
/*      */   }
/*      */ 
/*      */   public void keyTyped(KeyEvent e) {
/*      */   }
/*      */ 
/* 1227 */   public Insets getInsets() { Insets i = super.getInsets();
/* 1228 */     return new Insets(i.top + 10, i.left + 10, i.bottom + 10, i.right + 10); }
/*      */ 
/*      */   public synchronized void adjustmentValueChanged(AdjustmentEvent e)
/*      */   {
/* 1232 */     Object source = e.getSource();
/* 1233 */     for (int i = 0; i < this.slider.size(); i++)
/* 1234 */       if (source == this.slider.elementAt(i)) {
/* 1235 */         Scrollbar sb = (Scrollbar)source;
/* 1236 */         TextField tf = (TextField)this.numberField.elementAt(this.sliderIndexes[i]);
/* 1237 */         int digits = this.sliderScales[i] == 1.0D ? 0 : 2;
/* 1238 */         tf.setText("" + IJ.d2s(sb.getValue() / this.sliderScales[i], digits));
/*      */       }
/*      */   }
/*      */ 
/*      */   private void notifyListeners(AWTEvent e)
/*      */   {
/* 1252 */     if (this.dialogListeners == null) return;
/* 1253 */     boolean everythingOk = true;
/* 1254 */     for (int i = 0; (everythingOk) && (i < this.dialogListeners.size()); i++) {
/*      */       try {
/* 1256 */         resetCounters();
/* 1257 */         if (!((DialogListener)this.dialogListeners.elementAt(i)).dialogItemChanged(this, e))
/* 1258 */           everythingOk = false;
/*      */       } catch (Exception err) {
/* 1260 */         IJ.beep();
/* 1261 */         IJ.log("ERROR: " + err + "\nin DialogListener of " + this.dialogListeners.elementAt(i) + "\nat " + err.getStackTrace()[0] + "\nfrom " + err.getStackTrace()[1]);
/*      */       }
/*      */     }
/* 1264 */     boolean workaroundOSXbug = (IJ.isMacOSX()) && (!this.okay.isEnabled()) && (everythingOk);
/* 1265 */     if (this.previewCheckbox != null)
/* 1266 */       this.previewCheckbox.setEnabled(everythingOk);
/* 1267 */     this.okay.setEnabled(everythingOk);
/* 1268 */     if (workaroundOSXbug) repaint(); 
/*      */   }
/*      */ 
/*      */   public void paint(Graphics g)
/*      */   {
/* 1272 */     super.paint(g);
/* 1273 */     if (this.firstPaint) {
/* 1274 */       if ((this.numberField != null) && (IJ.isMacOSX()))
/*      */       {
/* 1276 */         TextField tf = (TextField)this.numberField.elementAt(0);
/* 1277 */         tf.setEditable(false);
/* 1278 */         tf.setEditable(true);
/*      */       }
/* 1280 */       if ((this.numberField == null) && (this.stringField == null))
/* 1281 */         this.okay.requestFocus();
/* 1282 */       this.firstPaint = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void windowClosing(WindowEvent e) {
/* 1287 */     this.wasCanceled = true;
/* 1288 */     dispose();
/*      */   }
/*      */ 
/*      */   public void addHelp(String url) {
/* 1292 */     this.helpURL = url;
/*      */   }
/*      */ 
/*      */   void showHelp() {
/* 1296 */     String macro = "run('URL...', 'url=" + this.helpURL + "');";
/* 1297 */     new MacroRunner(macro);
/*      */   }
/*      */ 
/*      */   public void windowActivated(WindowEvent e)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void windowOpened(WindowEvent e)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void windowClosed(WindowEvent e)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void windowIconified(WindowEvent e)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void windowDeiconified(WindowEvent e)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void windowDeactivated(WindowEvent e)
/*      */   {
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.gui.GenericDialog
 * JD-Core Version:    0.6.2
 */