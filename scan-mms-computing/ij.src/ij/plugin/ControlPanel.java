/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.Menus;
/*     */ import ij.Prefs;
/*     */ import ij.util.Java2;
/*     */ import ij.util.StringSorter;
/*     */ import java.awt.Menu;
/*     */ import java.awt.MenuBar;
/*     */ import java.awt.MenuItem;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.util.Arrays;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Properties;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.tree.DefaultMutableTreeNode;
/*     */ import javax.swing.tree.TreePath;
/*     */ 
/*     */ public class ControlPanel
/*     */   implements PlugIn
/*     */ {
/*  28 */   private static final String fileSeparator = System.getProperty("file.separator");
/*     */ 
/*  31 */   private static final char sep = fileSeparator.charAt(0);
/*     */ 
/*  33 */   private Hashtable panels = new Hashtable();
/*  34 */   private Vector visiblePanels = new Vector();
/*  35 */   private Vector expandedNodes = new Vector();
/*  36 */   private String defaultArg = "";
/*     */ 
/*  38 */   private boolean savePropsUponClose = true;
/*  39 */   private boolean propertiesChanged = true;
/*  40 */   private boolean closeChildPanelOnExpand = true;
/*     */   private boolean requireDoubleClick;
/*  42 */   private boolean quitting = true;
/*     */ 
/*  44 */   Vector menus = new Vector();
/*  45 */   Vector allMenus = new Vector();
/*  46 */   Hashtable commands = new Hashtable();
/*  47 */   Hashtable menuCommands = new Hashtable();
/*     */   String[] pluginsArray;
/*  49 */   Hashtable treeCommands = new Hashtable();
/*     */   int argLength;
/*  52 */   private String path = null;
/*     */   private DefaultMutableTreeNode root;
/*  55 */   MenuItem reloadMI = null;
/*     */ 
/*     */   public ControlPanel()
/*     */   {
/*  59 */     Java2.setSystemLookAndFeel();
/*     */   }
/*     */ 
/*     */   public void run(String arg)
/*     */   {
/*  65 */     load();
/*     */   }
/*     */ 
/*     */   synchronized void load()
/*     */   {
/*  74 */     this.commands = Menus.getCommands();
/*  75 */     this.pluginsArray = Menus.getPlugins();
/*  76 */     this.root = doRootFromMenus();
/*  77 */     if ((this.root == null) || (this.root.getChildCount() == 0)) return;
/*  78 */     loadProperties();
/*  79 */     restoreVisiblePanels();
/*  80 */     if (this.panels.isEmpty())
/*  81 */       newPanel(this.root);
/*     */   }
/*     */ 
/*     */   private synchronized DefaultMutableTreeNode doRootFromMenus()
/*     */   {
/*  90 */     DefaultMutableTreeNode node = new DefaultMutableTreeNode("ImageJ Menus");
/*  91 */     if (this.argLength == 0) node.setUserObject("Control Panel");
/*  92 */     MenuBar menuBar = Menus.getMenuBar();
/*  93 */     for (int i = 0; i < menuBar.getMenuCount(); i++) {
/*  94 */       Menu menu = menuBar.getMenu(i);
/*  95 */       DefaultMutableTreeNode menuNode = new DefaultMutableTreeNode(menu.getLabel());
/*  96 */       recurseSubMenu(menu, menuNode);
/*  97 */       node.add(menuNode);
/*     */     }
/*  99 */     return node;
/*     */   }
/*     */ 
/*     */   private void recurseSubMenu(Menu menu, DefaultMutableTreeNode node)
/*     */   {
/* 116 */     int items = menu.getItemCount();
/* 117 */     if (items == 0) return;
/* 118 */     for (int i = 0; i < items; i++) {
/* 119 */       MenuItem mItem = menu.getItem(i);
/* 120 */       String label = mItem.getLabel();
/* 121 */       if ((mItem instanceof Menu)) {
/* 122 */         DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(label);
/* 123 */         recurseSubMenu((Menu)mItem, subNode);
/* 124 */         node.add(subNode);
/* 125 */       } else if (((mItem instanceof MenuItem)) && 
/* 126 */         (!label.equals("-"))) {
/* 127 */         DefaultMutableTreeNode leaf = new DefaultMutableTreeNode(label);
/* 128 */         node.add(leaf);
/* 129 */         if (this.treeCommands == null) this.treeCommands = new Hashtable();
/* 130 */         if (label.equals("Reload Plugins")) {
/* 131 */           this.reloadMI = mItem;
/* 132 */           this.treeCommands.put(label, "Reload Plugins From Panel");
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void populateNode(Hashtable collection, DefaultMutableTreeNode node)
/*     */   {
/* 149 */     Vector labelVector = new Vector();
/* 150 */     for (Enumeration e = collection.keys(); e.hasMoreElements(); ) {
/* 151 */       String key = (String)e.nextElement();
/* 152 */       labelVector.addElement(key);
/*     */     }
/* 154 */     String[] labels = new String[labelVector.size()];
/* 155 */     String[] items = new String[labelVector.size()];
/* 156 */     labelVector.copyInto((String[])labels);
/* 157 */     StringSorter.sort(labels);
/* 158 */     for (int i = 0; i < labels.length; i++) {
/* 159 */       items[i] = ((String)collection.get(labels[i]));
/*     */     }
/* 161 */     populateNode(items, labels, node);
/*     */   }
/*     */ 
/*     */   private void populateNode(String[] items, String[] labels, DefaultMutableTreeNode node)
/*     */   {
/* 173 */     if ((items.length == 0) || (items.length != labels.length)) return;
/* 174 */     String label = null;
/* 175 */     for (int i = 0; i < items.length; i++) {
/* 176 */       if ((labels != null) && (i < labels.length))
/* 177 */         label = labels[i];
/* 178 */       buildTreePath(items[i], label, node);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void buildTreePath(String source, String label, DefaultMutableTreeNode topNode)
/*     */   {
/* 188 */     buildTreePath(source, label, null, topNode);
/*     */   }
/*     */ 
/*     */   private void buildTreePath(String source, String label, String command, DefaultMutableTreeNode topNode)
/*     */   {
/* 201 */     String local = source;
/* 202 */     String argument = "";
/* 203 */     String delimiter = fileSeparator;
/*     */ 
/* 208 */     int leftParen = source.indexOf('(');
/* 209 */     int rightParen = source.indexOf(')');
/* 210 */     if ((leftParen > -1) && (rightParen > leftParen)) {
/* 211 */       argument = source.substring(leftParen + 1, rightParen);
/* 212 */       local = source.substring(0, leftParen);
/*     */     }
/*     */ 
/* 216 */     String pluginsPath = Menus.getPlugInsPath();
/* 217 */     if (local.startsWith(pluginsPath)) {
/* 218 */       local = local.substring(pluginsPath.length(), local.length());
/*     */     }
/*     */ 
/* 221 */     local = local.replace('.', delimiter.charAt(0));
/*     */ 
/* 224 */     if (argument.length() > 0) {
/* 225 */       local = local.concat(fileSeparator).concat(argument);
/*     */     }
/* 227 */     DefaultMutableTreeNode node = null;
/*     */ 
/* 238 */     StringTokenizer pathParser = new StringTokenizer(local, delimiter);
/* 239 */     int tokens = pathParser.countTokens();
/* 240 */     while (pathParser.hasMoreTokens()) {
/* 241 */       String token = pathParser.nextToken();
/* 242 */       tokens--;
/* 243 */       if ((topNode.isLeaf()) && (topNode.getAllowsChildren())) {
/* 244 */         if (token.indexOf("ControlPanel") == -1)
/*     */         {
/* 247 */           if (tokens == 0) {
/* 248 */             if (label != null) token = label;
/* 249 */             token = token.replace('_', ' ');
/* 250 */             if (token.endsWith(".class")) {
/* 251 */               token = token.substring(0, token.length() - 6);
/*     */             }
/*     */           }
/* 254 */           node = new DefaultMutableTreeNode(token);
/*     */ 
/* 258 */           if (tokens == 0) {
/* 259 */             String cmd = command == null ? token : command;
/* 260 */             if (this.treeCommands == null) this.treeCommands = new Hashtable();
/* 261 */             if (!this.treeCommands.containsKey(token)) this.treeCommands.put(token, cmd);
/*     */           }
/*     */ 
/* 264 */           topNode.add(node);
/* 265 */           topNode = node;
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 272 */         boolean hasTokenAsNode = false;
/* 273 */         Enumeration nodes = topNode.children();
/* 274 */         while (nodes.hasMoreElements()) {
/* 275 */           node = (DefaultMutableTreeNode)nodes.nextElement();
/* 276 */           if (((String)node.getUserObject()).equals(token)) {
/* 277 */             hasTokenAsNode = true;
/* 278 */             topNode = node;
/*     */           }
/*     */         }
/*     */ 
/* 282 */         if ((!hasTokenAsNode) && 
/* 283 */           (token.indexOf("ControlPanel") == -1)) {
/* 284 */           if (tokens == 0) {
/* 285 */             if (label != null) token = label;
/* 286 */             token = token.replace('_', ' ');
/* 287 */             if (token.endsWith(".class"))
/* 288 */               token = token.substring(0, token.length() - 6);
/*     */           }
/* 290 */           node = new DefaultMutableTreeNode(token);
/* 291 */           topNode.add(node);
/* 292 */           topNode = node;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   TreePanel newPanel(DefaultMutableTreeNode node)
/*     */   {
/* 307 */     boolean main = node.getUserObject().equals(this.root.getUserObject());
/* 308 */     TreePanel panel = new TreePanel(node, this, main);
/* 309 */     return panel;
/*     */   }
/*     */ 
/*     */   TreePanel newPanel(DefaultMutableTreeNode node, Point location) {
/* 313 */     boolean main = node.getUserObject().equals(this.root.getUserObject());
/* 314 */     TreePanel panel = new TreePanel(node, this, main, location);
/* 315 */     return panel;
/*     */   }
/*     */ 
/*     */   TreePanel newPanel(String path)
/*     */   {
/* 324 */     if (path.equals("Control_Panel.@Main")) path = "Control_Panel";
/* 325 */     path = key2pStr(path);
/* 326 */     TreePanel pnl = null;
/* 327 */     for (Enumeration e = this.root.breadthFirstEnumeration(); e.hasMoreElements(); ) {
/* 328 */       DefaultMutableTreeNode n = (DefaultMutableTreeNode)e.nextElement();
/* 329 */       TreePath p = new TreePath(n.getPath());
/* 330 */       if (p.toString().equals(path))
/* 331 */         pnl = newPanel(n);
/*     */     }
/* 333 */     return pnl;
/*     */   }
/*     */ 
/*     */   boolean requiresDoubleClick()
/*     */   {
/* 340 */     return this.requireDoubleClick;
/*     */   }
/* 342 */   void setDoubleClick(boolean dc) { this.requireDoubleClick = dc; }
/*     */ 
/*     */   boolean hasPanelForNode(DefaultMutableTreeNode node) {
/* 345 */     TreePath path = new TreePath(node.getPath());
/* 346 */     return this.panels.containsKey(pStr2Key(path.toString()));
/*     */   }
/*     */ 
/*     */   TreePanel getPanelForNode(DefaultMutableTreeNode node) {
/* 350 */     TreePath path = new TreePath(node.getPath());
/* 351 */     String pathString = path.toString();
/* 352 */     if (this.panels.containsKey(pStr2Key(pathString))) {
/* 353 */       return (TreePanel)this.panels.get(pStr2Key(pathString));
/*     */     }
/* 355 */     return null;
/*     */   }
/*     */   public DefaultMutableTreeNode getRoot() {
/* 358 */     return this.root;
/*     */   }
/* 360 */   Hashtable getPanels() { return this.panels; }
/*     */ 
/*     */   Hashtable getTreeCommands() {
/* 363 */     return this.treeCommands;
/*     */   }
/*     */ 
/*     */   boolean hasVisiblePanels() {
/* 367 */     return this.visiblePanels.size() > 0;
/*     */   }
/*     */   int getVisiblePanelsCount() {
/* 370 */     return this.visiblePanels.size();
/*     */   }
/*     */ 
/*     */   void registerPanel(TreePanel panel)
/*     */   {
/* 379 */     String key = pStr2Key(panel.getRootPath().toString());
/* 380 */     this.panels.put(key, panel);
/* 381 */     setPanelShowingProperty(panel.getRootPath().toString());
/* 382 */     this.propertiesChanged = true;
/*     */   }
/*     */ 
/*     */   void loadProperties()
/*     */   {
/* 401 */     if (IJ.debugMode) IJ.log("CP.loadProperties");
/* 402 */     this.visiblePanels.removeAllElements();
/* 403 */     this.expandedNodes.removeAllElements();
/* 404 */     this.panels.clear();
/* 405 */     Properties properties = Prefs.getControlPanelProperties();
/* 406 */     for (Enumeration e = properties.keys(); e.hasMoreElements(); ) {
/* 407 */       String key = (String)e.nextElement();
/* 408 */       if (key.startsWith(".Control_Panel.")) {
/* 409 */         key = key.substring(1, key.length());
/* 410 */         String val = Prefs.get(key, null);
/* 411 */         if (IJ.debugMode) IJ.log("  " + key + ": " + val);
/* 412 */         if (Character.isDigit(val.charAt(0)))
/* 413 */           this.visiblePanels.addElement(key);
/* 414 */         else if (val.equals("expand"))
/* 415 */           this.expandedNodes.addElement(key);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void saveProperties() {
/* 421 */     if (IJ.debugMode) IJ.log("CP.saveProperties: " + this.propertiesChanged);
/*     */     Enumeration e;
/* 422 */     if (this.propertiesChanged) {
/* 423 */       clearProperties();
/* 424 */       for (Enumeration e = this.visiblePanels.elements(); e.hasMoreElements(); ) {
/* 425 */         String s = (String)e.nextElement();
/* 426 */         TreePanel p = (TreePanel)this.panels.get(s);
/* 427 */         if (p != null) recordGeometry(p);
/*     */       }
/* 429 */       for (e = this.expandedNodes.elements(); e.hasMoreElements(); )
/* 430 */         Prefs.set((String)e.nextElement(), "expand");
/*     */     }
/* 432 */     this.propertiesChanged = false;
/*     */   }
/*     */ 
/*     */   void clearProperties() {
/* 436 */     Properties properties = Prefs.getControlPanelProperties();
/* 437 */     for (Enumeration e = properties.keys(); e.hasMoreElements(); ) {
/* 438 */       String key = (String)e.nextElement();
/* 439 */       if (key.startsWith(".Control_Panel."))
/* 440 */         properties.remove(key);
/*     */     }
/*     */   }
/*     */ 
/*     */   void setExpandedStateProperty(String item) {
/* 445 */     String s = pStr2Key(item);
/* 446 */     this.expandedNodes.addElement(s);
/* 447 */     this.propertiesChanged = true;
/*     */   }
/*     */ 
/*     */   boolean hasExpandedStateProperty(String item) {
/* 451 */     String s = pStr2Key(item);
/* 452 */     return this.expandedNodes.contains(s);
/*     */   }
/*     */ 
/*     */   void unsetExpandedStateProperty(String item) {
/* 456 */     String s = pStr2Key(item);
/* 457 */     this.expandedNodes.remove(s);
/* 458 */     this.propertiesChanged = true;
/*     */   }
/*     */ 
/*     */   void setPanelShowingProperty(String item) {
/* 462 */     String s = pStr2Key(item);
/* 463 */     if (!this.visiblePanels.contains(s))
/* 464 */       this.visiblePanels.addElement(s);
/* 465 */     this.propertiesChanged = true;
/*     */   }
/*     */ 
/*     */   void unsetPanelShowingProperty(String item) {
/* 469 */     String s = pStr2Key(item);
/* 470 */     if (this.visiblePanels.remove(s));
/*     */   }
/*     */ 
/*     */   boolean hasPanelShowingProperty(String item)
/*     */   {
/* 478 */     String s = pStr2Key(item);
/* 479 */     return this.visiblePanels.contains(s);
/*     */   }
/*     */ 
/*     */   void restoreVisiblePanels() {
/* 483 */     String[] visPanls = new String[this.visiblePanels.size()];
/* 484 */     this.visiblePanels.toArray(visPanls);
/* 485 */     Arrays.sort(visPanls);
/*     */     TreePanel p;
/* 486 */     for (int i = 0; i < visPanls.length; i++)
/* 487 */       if (!this.panels.containsKey(visPanls[i]))
/* 488 */         p = newPanel(visPanls[i]);
/*     */   }
/*     */ 
/*     */   void recordGeometry(TreePanel panel)
/*     */   {
/* 494 */     String pTitle = panel.getRootPath().toString();
/* 495 */     pTitle = pStr2Key(pTitle);
/* 496 */     JFrame frame = panel.getFrame();
/* 497 */     if (frame != null) {
/* 498 */       Rectangle rect = frame.getBounds();
/* 499 */       String xCoord = new Integer(rect.x).toString();
/* 500 */       String yCoord = new Integer(rect.y).toString();
/* 501 */       String width = new Integer(rect.width).toString();
/* 502 */       String height = new Integer(rect.height).toString();
/* 503 */       if (pTitle.equals("Control_Panel")) pTitle = "Control_Panel.@Main";
/* 504 */       String geometry = xCoord + " " + yCoord + " " + width + " " + height;
/* 505 */       if (IJ.debugMode) IJ.log("CP.recordGeometry: " + pTitle + " " + geometry);
/* 506 */       Prefs.set(pTitle, geometry);
/*     */     }
/*     */   }
/*     */ 
/*     */   void restoreGeometry(TreePanel panel) {
/* 511 */     String pTitle = panel.getRootPath().toString();
/* 512 */     pTitle = pStr2Key(pTitle);
/* 513 */     if (pTitle.equals("Control_Panel")) pTitle = "Control_Panel.@Main";
/* 514 */     if (IJ.debugMode) IJ.log("CP.restoreGeometry: " + pTitle);
/* 515 */     String geom = Prefs.get(pTitle, null);
/* 516 */     if (geom != null) {
/* 517 */       int[] coords = s2ints(geom);
/* 518 */       if ((coords != null) && (coords.length == 4)) {
/* 519 */         panel.setBounds(coords[0], coords[1], coords[2], coords[3]);
/*     */       } else {
/* 521 */         Point pnt = panel.getDefaultLocation();
/* 522 */         if (pnt != null)
/* 523 */           panel.getFrame().setLocation((int)pnt.getX(), (int)pnt.getY());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void closeAll(boolean die) {
/* 529 */     this.quitting = die;
/* 530 */     if (!this.visiblePanels.isEmpty()) {
/* 531 */       this.propertiesChanged = true;
/* 532 */       saveProperties();
/*     */     }
/* 534 */     for (Enumeration e = this.panels.elements(); e.hasMoreElements(); ) {
/* 535 */       TreePanel p = (TreePanel)e.nextElement();
/* 536 */       p.close();
/*     */     }
/*     */ 
/* 539 */     this.quitting = true;
/*     */   }
/*     */ 
/*     */   void showHelp()
/*     */   {
/* 560 */     IJ.showMessage("About Control Panel...", "This plugin displays a panel with ImageJ commands in a hierarchical tree structure.\n \nUsage:\n \n     Click on a leaf node to launch the corresponding ImageJ command (or plugin)\n     (double-click on X Window Systems)\n \n     Double-click on a tree branch node (folder) to expand or collapse it\n \n     Click and drag on a tree branch node (folder) to display its descendants,\n     in a separate (child) panel (\"tear-off\" mock-up)\n \n     In a child panel, use the \"Show Parent\" menu item to re-open the parent panel\n     if it was accidentally closed\n \nAuthor: Cezar M. Tigaret (c.tigaret@ucl.ac.uk)\nThis code is in the public domain.");
/*     */   }
/*     */ 
/*     */   String pStr2Key(String pathString)
/*     */   {
/* 580 */     String keyword = pathString;
/* 581 */     if (keyword.startsWith("["))
/* 582 */       keyword = keyword.substring(keyword.indexOf("[") + 1, keyword.length());
/* 583 */     if (keyword.endsWith("]"))
/* 584 */       keyword = keyword.substring(0, keyword.lastIndexOf("]"));
/* 585 */     StringTokenizer st = new StringTokenizer(keyword, ",");
/* 586 */     String result = "";
/* 587 */     while (st.hasMoreTokens()) {
/* 588 */       String token = st.nextToken();
/* 589 */       if (token.startsWith(" ")) token = token.substring(1, token.length());
/* 590 */       result = result + token + ".";
/*     */     }
/* 592 */     result = result.substring(0, result.length() - 1);
/* 593 */     result = result.replace(' ', '_');
/* 594 */     return result;
/*     */   }
/*     */ 
/*     */   String key2pStr(String keyword)
/*     */   {
/* 599 */     StringTokenizer st = new StringTokenizer(keyword, ".");
/* 600 */     String result = "";
/* 601 */     while (st.hasMoreTokens()) {
/* 602 */       String token = st.nextToken();
/* 603 */       result = result + token + ", ";
/*     */     }
/* 605 */     result = result.substring(0, result.length() - 2);
/* 606 */     result = "[" + result + "]";
/* 607 */     result = result.replace('_', ' ');
/* 608 */     return result;
/*     */   }
/*     */ 
/*     */   public int[] s2ints(String s)
/*     */   {
/* 616 */     StringTokenizer st = new StringTokenizer(s, ", \t");
/* 617 */     int nInts = st.countTokens();
/* 618 */     if (nInts == 0) return null;
/* 619 */     int[] ints = new int[nInts];
/* 620 */     for (int i = 0; i < nInts; i++) try {
/* 621 */         ints[i] = Integer.parseInt(st.nextToken()); } catch (NumberFormatException e) {
/* 622 */         return null;
/*     */       }
/* 624 */     return ints;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.ControlPanel
 * JD-Core Version:    0.6.2
 */