/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.Menus;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.io.PluginClassLoader;
/*     */ import ij.util.StringSorter;
/*     */ import java.io.File;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class Installer
/*     */   implements PlugIn
/*     */ {
/*  14 */   private static String[] menus = { "Shortcuts", "Plugins", "Import", "Save As", "Filters", "Tools", "Utilities" };
/*     */   private static final String TITLE = "Installer";
/*  17 */   private static String command = "";
/*  18 */   private static String shortcut = "";
/*  19 */   private static String defaultPlugin = "";
/*  20 */   private static String menuStr = menus[0];
/*     */ 
/*     */   public void run(String arg) {
/*  23 */     installPlugin();
/*     */   }
/*     */ 
/*     */   void installPlugin() {
/*  27 */     String[] plugins = getPlugins();
/*  28 */     if ((plugins == null) || (plugins.length == 0)) {
/*  29 */       IJ.error("No plugins found");
/*  30 */       return;
/*     */     }
/*  32 */     GenericDialog gd = new GenericDialog("Install Plugin", IJ.getInstance());
/*  33 */     gd.addChoice("Plugin:", plugins, defaultPlugin);
/*  34 */     gd.addChoice("Menu:", menus, menuStr);
/*  35 */     gd.addStringField("Command:", command, 16);
/*  36 */     gd.addStringField("Shortcut:", shortcut, 3);
/*  37 */     gd.addStringField("Argument:", "", 12);
/*  38 */     gd.showDialog();
/*  39 */     if (gd.wasCanceled())
/*  40 */       return;
/*  41 */     String plugin = gd.getNextChoice();
/*  42 */     menuStr = gd.getNextChoice();
/*  43 */     command = gd.getNextString();
/*  44 */     shortcut = gd.getNextString();
/*  45 */     String argument = gd.getNextString();
/*  46 */     IJ.register(Installer.class);
/*  47 */     defaultPlugin = plugin;
/*  48 */     if (command.equals("")) {
/*  49 */       IJ.showMessage("Installer", "Command required");
/*  50 */       return;
/*     */     }
/*  52 */     if (shortcut.length() > 1)
/*  53 */       shortcut = shortcut.replace('f', 'F');
/*  54 */     char menu = ' ';
/*  55 */     if (menuStr.equals(menus[0]))
/*  56 */       menu = 'h';
/*  57 */     else if (menuStr.equals(menus[1]))
/*  58 */       menu = 'p';
/*  59 */     else if (menuStr.equals(menus[2]))
/*  60 */       menu = 'i';
/*  61 */     else if (menuStr.equals(menus[3]))
/*  62 */       menu = 's';
/*  63 */     else if (menuStr.equals(menus[4]))
/*  64 */       menu = 'f';
/*  65 */     else if (menuStr.equals(menus[5]))
/*  66 */       menu = 't';
/*  67 */     else if (menuStr.equals(menus[6]))
/*  68 */       menu = 'u';
/*  69 */     if (!argument.equals(""))
/*  70 */       plugin = plugin + "(\"" + argument + "\")";
/*  71 */     int err = Menus.installPlugin(plugin, menu, command, shortcut, IJ.getInstance());
/*  72 */     switch (err) {
/*     */     case -1:
/*  74 */       IJ.showMessage("Installer", "The command \"" + command + "\" \nis already being used.");
/*  75 */       return;
/*     */     case -2:
/*  77 */       IJ.showMessage("Installer", "The shortcut must be a single character or \"F1\"-\"F12\".");
/*  78 */       return;
/*     */     case -3:
/*  80 */       IJ.showMessage("The \"" + shortcut + "\" shortcut is already being used.");
/*  81 */       return;
/*     */     }
/*  83 */     command = "";
/*  84 */     shortcut = "";
/*     */ 
/*  87 */     if (!plugin.endsWith(")"))
/*  88 */       installAbout(plugin);
/*     */   }
/*     */ 
/*     */   void installAbout(String plugin) {
/*  92 */     boolean hasShowAboutMethod = false;
/*  93 */     PluginClassLoader loader = new PluginClassLoader(Menus.getPlugInsPath());
/*     */     try {
/*  95 */       Class c = loader.loadClass(plugin);
/*  96 */       Method m = c.getDeclaredMethod("showAbout", new Class[0]);
/*  97 */       if (m != null)
/*  98 */         hasShowAboutMethod = true;
/*     */     }
/*     */     catch (Exception e) {
/*     */     }
/* 102 */     if (hasShowAboutMethod)
/* 103 */       Menus.installPlugin(plugin + "(\"about\")", 'a', plugin + "...", "", IJ.getInstance());
/*     */   }
/*     */ 
/*     */   String[] getPlugins() {
/* 107 */     String path = Menus.getPlugInsPath();
/* 108 */     if (path == null)
/* 109 */       return null;
/* 110 */     File f = new File(path);
/* 111 */     String[] list = f.list();
/* 112 */     if (list == null) return null;
/* 113 */     Vector v = new Vector();
/* 114 */     for (int i = 0; i < list.length; i++) {
/* 115 */       String className = list[i];
/* 116 */       boolean isClassFile = className.endsWith(".class");
/* 117 */       if ((className.indexOf('_') >= 0) && (isClassFile) && (className.indexOf('$') < 0)) {
/* 118 */         className = className.substring(0, className.length() - 6);
/* 119 */         v.addElement(className);
/*     */       }
/* 121 */       else if (!isClassFile) {
/* 122 */         getSubdirectoryPlugins(path, className, v);
/*     */       }
/*     */     }
/* 125 */     list = new String[v.size()];
/* 126 */     v.copyInto((String[])list);
/* 127 */     StringSorter.sort(list);
/* 128 */     return list;
/*     */   }
/*     */ 
/*     */   void getSubdirectoryPlugins(String path, String dir, Vector v)
/*     */   {
/* 134 */     if (dir.endsWith(".java"))
/* 135 */       return;
/* 136 */     File f = new File(path, dir);
/* 137 */     if (!f.isDirectory())
/* 138 */       return;
/* 139 */     String[] list = f.list();
/* 140 */     if (list == null)
/* 141 */       return;
/* 142 */     dir = dir + "/";
/* 143 */     for (int i = 0; i < list.length; i++) {
/* 144 */       String name = list[i];
/* 145 */       if ((name.indexOf('_') >= 0) && (name.endsWith(".class")) && (name.indexOf('$') < 0)) {
/* 146 */         name = name.substring(0, name.length() - 6);
/* 147 */         v.addElement(name);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.Installer
 * JD-Core Version:    0.6.2
 */