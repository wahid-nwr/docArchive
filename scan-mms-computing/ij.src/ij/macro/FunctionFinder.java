/*     */ package ij.macro;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImageJ;
/*     */ import ij.WindowManager;
/*     */ import ij.plugin.frame.Editor;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Dialog;
/*     */ import java.awt.Frame;
/*     */ import java.awt.List;
/*     */ import java.awt.Panel;
/*     */ import java.awt.Point;
/*     */ import java.awt.TextArea;
/*     */ import java.awt.TextField;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.KeyListener;
/*     */ import java.awt.event.TextEvent;
/*     */ import java.awt.event.TextListener;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.awt.event.WindowListener;
/*     */ 
/*     */ public class FunctionFinder
/*     */   implements TextListener, WindowListener, KeyListener, ItemListener
/*     */ {
/*     */   Dialog d;
/*     */   TextField prompt;
/*     */   List completions;
/*     */   String[] commands;
/*     */ 
/*     */   public FunctionFinder()
/*     */   {
/*  24 */     String exists = IJ.runMacro("return File.exists(getDirectory('macros')+'functions.html');");
/*  25 */     if (exists == "0") {
/*  26 */       String installLocalMacroFunctionsFile = "functions = File.openUrlAsString('http://imagej.nih.gov/ij/developer/macro/functions.html');\nf = File.open(getDirectory('macros')+'functions.html');\nprint (f, functions);\nFile.close(f);";
/*     */       try
/*     */       {
/*  30 */         IJ.runMacro(installLocalMacroFunctionsFile); } catch (Throwable e) {
/*  31 */         IJ.error("Problem downloading functions.html"); return;
/*     */       }
/*     */     }
/*  33 */     String f = IJ.runMacro("return File.openAsString(getDirectory('macros')+'functions.html');");
/*  34 */     String[] l = f.split("\n");
/*  35 */     this.commands = new String[l.length];
/*  36 */     int c = 0;
/*  37 */     for (int i = 0; i < l.length; i++) {
/*  38 */       String line = l[i];
/*  39 */       if (line.startsWith("<b>")) {
/*  40 */         this.commands[c] = line.substring(line.indexOf("<b>") + 3, line.indexOf("</b>"));
/*  41 */         c++;
/*     */       }
/*     */     }
/*  44 */     if (c == 0) {
/*  45 */       IJ.error("ImageJ/macros/functions.html is corrupted");
/*  46 */       return;
/*     */     }
/*     */ 
/*  49 */     ImageJ imageJ = IJ.getInstance();
/*  50 */     this.d = new Dialog(imageJ, "Built-in Functions");
/*  51 */     this.d.setLayout(new BorderLayout());
/*  52 */     this.d.addWindowListener(this);
/*  53 */     Panel northPanel = new Panel();
/*  54 */     this.prompt = new TextField("", 30);
/*  55 */     this.prompt.addTextListener(this);
/*  56 */     this.prompt.addKeyListener(this);
/*  57 */     northPanel.add(this.prompt);
/*  58 */     this.d.add(northPanel, "North");
/*  59 */     this.completions = new List(12);
/*  60 */     this.completions.addKeyListener(this);
/*  61 */     populateList("");
/*  62 */     this.d.add(this.completions, "Center");
/*  63 */     this.d.pack();
/*     */ 
/*  65 */     Frame frame = WindowManager.getFrontWindow();
/*  66 */     if (frame == null) return;
/*  67 */     Point posi = frame.getLocationOnScreen();
/*  68 */     int initialX = (int)posi.getX() + 38;
/*  69 */     int initialY = (int)posi.getY() + 84;
/*  70 */     this.d.setLocation(initialX, initialY);
/*  71 */     this.d.setVisible(true);
/*  72 */     this.d.toFront();
/*     */   }
/*     */ 
/*     */   public void populateList(String matchingSubstring) {
/*  76 */     String substring = matchingSubstring.toLowerCase();
/*  77 */     this.completions.removeAll();
/*     */     try {
/*  79 */       for (int i = 0; i < this.commands.length; i++) {
/*  80 */         String commandName = this.commands[i];
/*  81 */         if (commandName.length() != 0)
/*     */         {
/*  83 */           String lowerCommandName = commandName.toLowerCase();
/*  84 */           if (lowerCommandName.indexOf(substring) >= 0)
/*  85 */             this.completions.add(this.commands[i]); 
/*     */         }
/*     */       }
/*     */     } catch (Exception e) {
/*     */     }
/*     */   }
/*     */ 
/*  92 */   public void edPaste(String arg) { Frame frame = WindowManager.getFrontWindow();
/*     */     try {
/*  94 */       TextArea ta = ((Editor)frame).getTextArea();
/*  95 */       int start = ta.getSelectionStart();
/*  96 */       int end = ta.getSelectionEnd();
/*     */       try {
/*  98 */         ta.replaceRange(arg.substring(0, arg.length()), start, end); } catch (Exception e) {
/*     */       }
/* 100 */       if (IJ.isMacOSX())
/* 101 */         ta.setCaretPosition(start + arg.length());
/*     */     } catch (Exception e) {
/*     */     } }
/*     */ 
/*     */   public void itemStateChanged(ItemEvent ie) {
/* 106 */     populateList(this.prompt.getText());
/*     */   }
/*     */ 
/*     */   protected void runFromLabel(String listLabel) {
/* 110 */     edPaste(listLabel);
/* 111 */     this.d.dispose();
/*     */   }
/*     */ 
/*     */   public void keyPressed(KeyEvent ke) {
/* 115 */     int key = ke.getKeyCode();
/* 116 */     int items = this.completions.getItemCount();
/* 117 */     Object source = ke.getSource();
/* 118 */     if (source == this.prompt) {
/* 119 */       if (key == 10) {
/* 120 */         if (1 == items) {
/* 121 */           String selected = this.completions.getItem(0);
/* 122 */           runFromLabel(selected);
/*     */         }
/* 124 */       } else if (key == 38) {
/* 125 */         this.completions.requestFocus();
/* 126 */         if (items > 0)
/* 127 */           this.completions.select(this.completions.getItemCount() - 1);
/* 128 */       } else if (key == 27) {
/* 129 */         this.d.dispose();
/* 130 */       } else if (key == 40) {
/* 131 */         this.completions.requestFocus();
/* 132 */         if (items > 0)
/* 133 */           this.completions.select(0);
/*     */       }
/* 135 */     } else if ((source == this.completions) && 
/* 136 */       (key == 10)) {
/* 137 */       String selected = this.completions.getSelectedItem();
/* 138 */       if (selected != null)
/* 139 */         runFromLabel(selected); 
/*     */     }
/*     */   }
/*     */ 
/*     */   public void keyReleased(KeyEvent ke) {
/*     */   }
/*     */ 
/*     */   public void keyTyped(KeyEvent ke) {
/*     */   }
/*     */ 
/* 149 */   public void textValueChanged(TextEvent te) { populateList(this.prompt.getText()); }
/*     */ 
/*     */   public void windowClosing(WindowEvent e)
/*     */   {
/* 153 */     this.d.dispose();
/*     */   }
/*     */ 
/*     */   public void windowActivated(WindowEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void windowDeactivated(WindowEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void windowClosed(WindowEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void windowOpened(WindowEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void windowIconified(WindowEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void windowDeiconified(WindowEvent e)
/*     */   {
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.macro.FunctionFinder
 * JD-Core Version:    0.6.2
 */