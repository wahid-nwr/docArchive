/*     */ package ij;
/*     */ 
/*     */ import ij.io.OpenDialog;
/*     */ import ij.plugin.MacroInstaller;
/*     */ import ij.plugin.frame.Recorder;
/*     */ import ij.text.TextWindow;
/*     */ import ij.util.Tools;
/*     */ import java.awt.Menu;
/*     */ import java.awt.MenuItem;
/*     */ import java.io.CharArrayWriter;
/*     */ import java.io.File;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class Executer
/*     */   implements Runnable
/*     */ {
/*     */   private static String previousCommand;
/*     */   private static CommandListener listener;
/*  18 */   private static Vector listeners = new Vector();
/*     */   private String command;
/*     */   private Thread thread;
/*     */ 
/*     */   public Executer(String cmd)
/*     */   {
/*  26 */     this.command = cmd;
/*     */   }
/*     */ 
/*     */   public Executer(String cmd, ImagePlus imp)
/*     */   {
/*  33 */     if (cmd.startsWith("Repeat")) {
/*  34 */       this.command = previousCommand;
/*  35 */       IJ.setKeyUp(16);
/*     */     } else {
/*  37 */       this.command = cmd;
/*  38 */       if ((!cmd.equals("Undo")) && (!cmd.equals("Close")))
/*  39 */         previousCommand = cmd;
/*     */     }
/*  41 */     IJ.resetEscape();
/*  42 */     this.thread = new Thread(this, cmd);
/*  43 */     this.thread.setPriority(Math.max(this.thread.getPriority() - 2, 1));
/*  44 */     if (imp != null)
/*  45 */       WindowManager.setTempCurrentImage(this.thread, imp);
/*  46 */     this.thread.start();
/*     */   }
/*     */ 
/*     */   public void run() {
/*  50 */     if (this.command == null) return;
/*  51 */     if (listeners.size() > 0) synchronized (listeners) {
/*  52 */         for (int i = 0; i < listeners.size(); i++) {
/*  53 */           CommandListener listener = (CommandListener)listeners.elementAt(i);
/*  54 */           this.command = listener.commandExecuting(this.command);
/*  55 */           if (this.command == null) return; 
/*     */         }
/*     */       }
/*     */     try
/*     */     {
/*  59 */       if (Recorder.record) {
/*  60 */         Recorder.setCommand(this.command);
/*  61 */         runCommand(this.command);
/*  62 */         Recorder.saveCommand();
/*     */       } else {
/*  64 */         runCommand(this.command);
/*  65 */       }int len = this.command.length();
/*  66 */       if (this.command.charAt(len - 1) != ']')
/*  67 */         IJ.setKeyUp(-1);
/*     */     } catch (Throwable e) {
/*  69 */       IJ.showStatus("");
/*  70 */       IJ.showProgress(1, 1);
/*  71 */       ImagePlus imp = WindowManager.getCurrentImage();
/*  72 */       if (imp != null) imp.unlock();
/*  73 */       String msg = e.getMessage();
/*  74 */       if ((e instanceof OutOfMemoryError)) {
/*  75 */         IJ.outOfMemory(this.command);
/*  76 */       } else if ((!(e instanceof RuntimeException)) || (msg == null) || (!msg.equals("Macro canceled")))
/*     */       {
/*  79 */         CharArrayWriter caw = new CharArrayWriter();
/*  80 */         PrintWriter pw = new PrintWriter(caw);
/*  81 */         e.printStackTrace(pw);
/*  82 */         String s = caw.toString();
/*  83 */         if (IJ.isMacintosh()) {
/*  84 */           if (s.indexOf("ThreadDeath") > 0)
/*  85 */             return;
/*  86 */           s = Tools.fixNewLines(s);
/*     */         }
/*  88 */         int w = 500; int h = 300;
/*  89 */         if (s.indexOf("UnsupportedClassVersionError") != -1) {
/*  90 */           if (s.indexOf("version 49.0") != -1) {
/*  91 */             s = e + "\n \nThis plugin requires Java 1.5 or later.";
/*  92 */             w = 700; h = 150;
/*     */           }
/*  94 */           if (s.indexOf("version 50.0") != -1) {
/*  95 */             s = e + "\n \nThis plugin requires Java 1.6 or later.";
/*  96 */             w = 700; h = 150;
/*     */           }
/*  98 */           if (s.indexOf("version 51.0") != -1) {
/*  99 */             s = e + "\n \nThis plugin requires Java 1.7 or later.";
/* 100 */             w = 700; h = 150;
/*     */           }
/*     */         }
/* 103 */         if (IJ.getInstance() != null)
/* 104 */           new TextWindow("Exception", s, w, h);
/*     */         else
/* 106 */           IJ.log(s);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void runCommand(String cmd) {
/* 112 */     Hashtable table = Menus.getCommands();
/* 113 */     String className = (String)table.get(cmd);
/* 114 */     if (className != null) {
/* 115 */       String arg = "";
/* 116 */       if (className.endsWith("\")"))
/*     */       {
/* 118 */         int argStart = className.lastIndexOf("(\"");
/* 119 */         if (argStart > 0) {
/* 120 */           arg = className.substring(argStart + 2, className.length() - 2);
/* 121 */           className = className.substring(0, argStart);
/*     */         }
/*     */       }
/* 124 */       if ((IJ.shiftKeyDown()) && (className.startsWith("ij.plugin.Macro_Runner")) && (!Menus.getShortcuts().contains("*" + cmd)))
/* 125 */         IJ.open(IJ.getDirectory("plugins") + arg);
/*     */       else
/* 127 */         IJ.runPlugIn(cmd, className, arg);
/*     */     }
/*     */     else {
/* 130 */       if (MacroInstaller.runMacroCommand(cmd)) {
/* 131 */         return;
/*     */       }
/* 133 */       String path = IJ.getDirectory("luts") + cmd + ".lut";
/* 134 */       File f = new File(path);
/* 135 */       if (f.exists()) {
/* 136 */         String dir = OpenDialog.getLastDirectory();
/* 137 */         IJ.open(path);
/* 138 */         OpenDialog.setLastDirectory(dir);
/* 139 */       } else if (!openRecent(cmd)) {
/* 140 */         IJ.error("Unrecognized command: " + cmd);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   boolean openRecent(String cmd)
/*     */   {
/* 147 */     Menu menu = Menus.openRecentMenu;
/* 148 */     if (menu == null) return false;
/* 149 */     for (int i = 0; i < menu.getItemCount(); i++) {
/* 150 */       if (menu.getItem(i).getLabel().equals(cmd)) {
/* 151 */         IJ.open(cmd);
/* 152 */         return true;
/*     */       }
/*     */     }
/* 155 */     return false;
/*     */   }
/*     */ 
/*     */   public static String getCommand()
/*     */   {
/* 161 */     return previousCommand;
/*     */   }
/*     */ 
/*     */   public static void addCommandListener(CommandListener listener)
/*     */   {
/* 166 */     listeners.addElement(listener);
/*     */   }
/*     */ 
/*     */   public static void removeCommandListener(CommandListener listener)
/*     */   {
/* 171 */     listeners.removeElement(listener);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.Executer
 * JD-Core Version:    0.6.2
 */