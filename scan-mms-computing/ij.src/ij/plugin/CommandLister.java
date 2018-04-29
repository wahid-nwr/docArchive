/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.Menus;
/*    */ import ij.text.TextWindow;
/*    */ import ij.util.StringSorter;
/*    */ import java.awt.event.KeyEvent;
/*    */ import java.util.Enumeration;
/*    */ import java.util.Hashtable;
/*    */ import java.util.Vector;
/*    */ 
/*    */ public class CommandLister
/*    */   implements PlugIn
/*    */ {
/*    */   public void run(String arg)
/*    */   {
/* 13 */     if (arg.equals("shortcuts"))
/* 14 */       listShortcuts();
/*    */     else
/* 16 */       listCommands();
/*    */   }
/*    */ 
/*    */   public void listCommands() {
/* 20 */     Hashtable commands = Menus.getCommands();
/* 21 */     Vector v = new Vector();
/* 22 */     for (Enumeration en = commands.keys(); en.hasMoreElements(); ) {
/* 23 */       String command = (String)en.nextElement();
/* 24 */       v.addElement(command + "\t" + (String)commands.get(command));
/*    */     }
/* 26 */     showList("Commands", "Command\tPlugin", v);
/*    */   }
/*    */ 
/*    */   public void listShortcuts() {
/* 30 */     Hashtable shortcuts = Menus.getShortcuts();
/* 31 */     Vector v = new Vector();
/* 32 */     addShortcutsToVector(shortcuts, v);
/* 33 */     Hashtable macroShortcuts = Menus.getMacroShortcuts();
/* 34 */     addShortcutsToVector(macroShortcuts, v);
/* 35 */     showList("Keyboard Shortcuts", "Hot Key\tCommand", v);
/*    */   }
/*    */ 
/*    */   void addShortcutsToVector(Hashtable shortcuts, Vector v) {
/* 39 */     for (Enumeration en = shortcuts.keys(); en.hasMoreElements(); ) {
/* 40 */       Integer key = (Integer)en.nextElement();
/* 41 */       int keyCode = key.intValue();
/* 42 */       boolean upperCase = false;
/* 43 */       if ((keyCode >= 265) && (keyCode <= 290)) {
/* 44 */         upperCase = true;
/* 45 */         keyCode -= 200;
/*    */       }
/* 47 */       String shortcut = KeyEvent.getKeyText(keyCode);
/* 48 */       if ((!upperCase) && (shortcut.length() == 1)) {
/* 49 */         char c = shortcut.charAt(0);
/* 50 */         if ((c >= 'A') && (c <= 'Z'))
/* 51 */           c = (char)(c + ' ');
/* 52 */         char[] chars = new char[1];
/* 53 */         chars[0] = c;
/* 54 */         shortcut = new String(chars);
/*    */       }
/* 56 */       if (shortcut.length() > 1)
/* 57 */         shortcut = " " + shortcut;
/* 58 */       v.addElement(shortcut + "\t" + (String)shortcuts.get(key));
/*    */     }
/*    */   }
/*    */ 
/*    */   void showList(String title, String headings, Vector v) {
/* 63 */     String[] list = new String[v.size()];
/* 64 */     v.copyInto((String[])list);
/* 65 */     StringSorter.sort(list);
/* 66 */     StringBuffer sb = new StringBuffer();
/* 67 */     for (int i = 0; i < list.length; i++) {
/* 68 */       sb.append(list[i]);
/* 69 */       sb.append("\n");
/*    */     }
/* 71 */     TextWindow tw = new TextWindow(title, headings, sb.toString(), 600, 500);
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.CommandLister
 * JD-Core Version:    0.6.2
 */