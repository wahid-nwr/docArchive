/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.Executer;
/*     */ import ij.IJ;
/*     */ import ij.Menus;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.util.StringSorter;
/*     */ import java.io.File;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class Hotkeys
/*     */   implements PlugIn
/*     */ {
/*     */   private static final String TITLE = "Hotkeys";
/*  13 */   private static String command = "";
/*  14 */   private static String shortcut = "";
/*     */ 
/*     */   public void run(String arg) {
/*  17 */     if (arg.equals("install")) {
/*  18 */       installHotkey();
/*  19 */     } else if (arg.equals("remove")) {
/*  20 */       removeHotkey();
/*     */     } else {
/*  22 */       Executer e = new Executer(arg);
/*  23 */       e.run();
/*     */     }
/*  25 */     IJ.register(Hotkeys.class);
/*     */   }
/*     */ 
/*     */   void installHotkey() {
/*  29 */     String[] commands = getAllCommands();
/*  30 */     GenericDialog gd = new GenericDialog("Create Shortcut");
/*  31 */     gd.addChoice("Command:", commands, command);
/*  32 */     gd.addStringField("Shortcut:", shortcut, 3);
/*  33 */     gd.showDialog();
/*  34 */     if (gd.wasCanceled())
/*  35 */       return;
/*  36 */     command = gd.getNextChoice();
/*  37 */     shortcut = gd.getNextString();
/*  38 */     if (shortcut.equals("")) {
/*  39 */       IJ.showMessage("Hotkeys", "Shortcut required");
/*  40 */       return;
/*     */     }
/*  42 */     if (shortcut.length() > 1)
/*  43 */       shortcut = shortcut.replace('f', 'F');
/*  44 */     String plugin = "ij.plugin.Hotkeys(\"" + command + "\")";
/*  45 */     int err = Menus.installPlugin(plugin, 'h', "*" + command, shortcut, IJ.getInstance());
/*  46 */     switch (err) {
/*     */     case -1:
/*  48 */       IJ.showMessage("Hotkeys", "The command \"" + command + "\" is already installed.");
/*  49 */       break;
/*     */     case -2:
/*  51 */       IJ.showMessage("Hotkeys", "The shortcut must be a single character or F1-F24.");
/*  52 */       break;
/*     */     case -3:
/*  54 */       IJ.showMessage("The \"" + shortcut + "\" shortcut is already being used.");
/*  55 */       break;
/*     */     default:
/*  57 */       shortcut = "";
/*     */     }
/*     */   }
/*     */ 
/*     */   void removeHotkey()
/*     */   {
/*  63 */     String[] commands = getInstalledCommands();
/*  64 */     if (commands == null) {
/*  65 */       IJ.showMessage("Remove...", "No installed commands found.");
/*  66 */       return;
/*     */     }
/*  68 */     GenericDialog gd = new GenericDialog("Remove");
/*  69 */     gd.addChoice("Command:", commands, "");
/*  70 */     gd.addMessage("The command is not removed\nuntil ImageJ is restarted.");
/*  71 */     gd.showDialog();
/*  72 */     if (gd.wasCanceled())
/*  73 */       return;
/*  74 */     command = gd.getNextChoice();
/*  75 */     int err = Menus.uninstallPlugin(command);
/*  76 */     boolean removed = true;
/*  77 */     if (err == -5)
/*  78 */       removed = deletePlugin(command);
/*  79 */     if (removed)
/*  80 */       IJ.showStatus("\"" + command + "\" removed; ImageJ restart required");
/*     */     else
/*  82 */       IJ.showStatus("\"" + command + "\" not removed");
/*     */   }
/*     */ 
/*     */   boolean deletePlugin(String command) {
/*  86 */     String plugin = (String)Menus.getCommands().get(command);
/*  87 */     String name = plugin + ".class";
/*  88 */     File file = new File(Menus.getPlugInsPath(), name);
/*  89 */     if ((file == null) || (!file.exists())) {
/*  90 */       return false;
/*     */     }
/*  92 */     return IJ.showMessageWithCancel("Delete Plugin?", "Permanently delete \"" + name + "\"?");
/*     */   }
/*     */ 
/*     */   String[] getAllCommands() {
/*  96 */     Vector v = new Vector();
/*  97 */     for (Enumeration en = Menus.getCommands().keys(); en.hasMoreElements(); ) {
/*  98 */       String cmd = (String)en.nextElement();
/*  99 */       if (!cmd.startsWith("*"))
/* 100 */         v.addElement(cmd);
/*     */     }
/* 102 */     String[] list = new String[v.size()];
/* 103 */     v.copyInto((String[])list);
/* 104 */     StringSorter.sort(list);
/* 105 */     return list;
/*     */   }
/*     */ 
/*     */   String[] getInstalledCommands() {
/* 109 */     Vector v = new Vector();
/* 110 */     Hashtable commandTable = Menus.getCommands();
/* 111 */     for (Enumeration en = commandTable.keys(); en.hasMoreElements(); ) {
/* 112 */       String cmd = (String)en.nextElement();
/* 113 */       if (cmd.startsWith("*")) {
/* 114 */         v.addElement(cmd);
/*     */       } else {
/* 116 */         String plugin = (String)commandTable.get(cmd);
/* 117 */         if ((plugin.indexOf("_") >= 0) && (!plugin.startsWith("ij.")))
/* 118 */           v.addElement(cmd);
/*     */       }
/*     */     }
/* 121 */     if (v.size() == 0)
/* 122 */       return null;
/* 123 */     String[] list = new String[v.size()];
/* 124 */     v.copyInto((String[])list);
/* 125 */     StringSorter.sort(list);
/* 126 */     return list;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.Hotkeys
 * JD-Core Version:    0.6.2
 */