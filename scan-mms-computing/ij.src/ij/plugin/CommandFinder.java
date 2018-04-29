/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImageJ;
/*     */ import ij.Menus;
/*     */ import ij.Prefs;
/*     */ import ij.WindowManager;
/*     */ import ij.text.TextWindow;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Menu;
/*     */ import java.awt.MenuBar;
/*     */ import java.awt.MenuItem;
/*     */ import java.awt.Point;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.KeyListener;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.awt.event.WindowListener;
/*     */ import java.util.Arrays;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import javax.swing.DefaultListModel;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.event.DocumentEvent;
/*     */ import javax.swing.event.DocumentListener;
/*     */ import javax.swing.text.Document;
/*     */ 
/*     */ public class CommandFinder
/*     */   implements PlugIn, ActionListener, WindowListener, KeyListener, ItemListener, MouseListener
/*     */ {
/*     */   int multiClickInterval;
/*  94 */   long lastClickTime = -9223372036854775808L;
/*     */   String lastClickedItem;
/*     */   JFrame d;
/*     */   JTextField prompt;
/*     */   JList completions;
/*     */   JScrollPane scrollPane;
/*     */   DefaultListModel completionsModel;
/*     */   JButton runButton;
/*     */   JButton closeButton;
/*     */   JButton exportButton;
/*     */   JCheckBox fullInfoCheckBox;
/*     */   JCheckBox closeCheckBox;
/*     */   Hashtable commandsHash;
/*     */   String[] commands;
/*     */   Hashtable listLabelToCommand;
/* 106 */   static boolean closeWhenRunning = Prefs.get("command-finder.close", true);
/*     */ 
/*     */   public CommandFinder()
/*     */   {
/*  69 */     Toolkit toolkit = Toolkit.getDefaultToolkit();
/*  70 */     Integer interval = (Integer)toolkit.getDesktopProperty("awt.multiClickInterval");
/*  71 */     if (interval == null)
/*     */     {
/*  74 */       this.multiClickInterval = 300;
/*     */     }
/*  76 */     else this.multiClickInterval = interval.intValue();
/*     */   }
/*     */ 
/*     */   protected String makeListLabel(String command, CommandAction ca, boolean fullInfo)
/*     */   {
/* 110 */     if (fullInfo) {
/* 111 */       String result = command;
/* 112 */       if (ca.menuLocation != null)
/* 113 */         result = result + " (in " + ca.menuLocation + ")";
/* 114 */       if (ca.classCommand != null)
/* 115 */         result = result + " [" + ca.classCommand + "]";
/* 116 */       String jarFile = Menus.getJarFileForMenuEntry(command);
/* 117 */       if (jarFile != null)
/* 118 */         result = result + " {from " + jarFile + "}";
/* 119 */       return result;
/*     */     }
/* 121 */     return command;
/*     */   }
/*     */ 
/*     */   protected void populateList(String matchingSubstring)
/*     */   {
/* 126 */     boolean fullInfo = this.fullInfoCheckBox.isSelected();
/* 127 */     String substring = matchingSubstring.toLowerCase();
/* 128 */     this.completionsModel.removeAllElements();
/* 129 */     for (int i = 0; i < this.commands.length; i++) {
/* 130 */       String commandName = this.commands[i];
/* 131 */       if (commandName.length() != 0)
/*     */       {
/* 133 */         String lowerCommandName = commandName.toLowerCase();
/* 134 */         if (lowerCommandName.indexOf(substring) >= 0) {
/* 135 */           CommandAction ca = (CommandAction)this.commandsHash.get(commandName);
/* 136 */           String listLabel = makeListLabel(commandName, ca, fullInfo);
/* 137 */           this.completionsModel.addElement(listLabel);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent ae)
/*     */   {
/* 156 */     Object source = ae.getSource();
/* 157 */     if (source == this.runButton) {
/* 158 */       String selected = (String)this.completions.getSelectedValue();
/* 159 */       if (selected == null) {
/* 160 */         IJ.error("Please select a command to run");
/* 161 */         return;
/*     */       }
/* 163 */       runFromLabel(selected);
/* 164 */     } else if (source == this.exportButton) {
/* 165 */       export();
/* 166 */     } else if (source == this.closeButton) {
/* 167 */       this.d.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void itemStateChanged(ItemEvent ie) {
/* 172 */     populateList(this.prompt.getText());
/*     */   }
/*     */ 
/*     */   public void mouseClicked(MouseEvent e) {
/* 176 */     long now = System.currentTimeMillis();
/* 177 */     String justClickedItem = (String)this.completions.getSelectedValue();
/*     */ 
/* 179 */     long thisClickInterval = now - this.lastClickTime;
/* 180 */     if ((thisClickInterval < this.multiClickInterval) && 
/* 181 */       (justClickedItem != null) && (this.lastClickedItem != null) && (justClickedItem.equals(this.lastClickedItem)))
/*     */     {
/* 184 */       runFromLabel(justClickedItem);
/*     */     }
/*     */ 
/* 187 */     this.lastClickTime = now;
/* 188 */     this.lastClickedItem = justClickedItem;
/*     */   }
/*     */   public void mousePressed(MouseEvent e) {
/*     */   }
/*     */   public void mouseReleased(MouseEvent e) {
/*     */   }
/*     */   public void mouseEntered(MouseEvent e) {
/*     */   }
/*     */   public void mouseExited(MouseEvent e) {  } 
/* 197 */   void export() { StringBuffer sb = new StringBuffer(5000);
/* 198 */     for (int i = 0; i < this.completionsModel.size(); i++) {
/* 199 */       sb.append(i);
/* 200 */       sb.append("\t");
/* 201 */       sb.append((String)this.completionsModel.elementAt(i));
/* 202 */       sb.append("\n");
/*     */     }
/* 204 */     TextWindow tw = new TextWindow("ImageJ Menu Commands", " \tCommand", sb.toString(), 600, 500); }
/*     */ 
/*     */   protected void runFromLabel(String listLabel)
/*     */   {
/* 208 */     String command = (String)this.listLabelToCommand.get(listLabel);
/* 209 */     CommandAction ca = (CommandAction)this.commandsHash.get(command);
/*     */ 
/* 211 */     IJ.showStatus("Running command " + ca.classCommand);
/* 212 */     IJ.doCommand(command);
/*     */ 
/* 223 */     closeWhenRunning = this.closeCheckBox.isSelected();
/* 224 */     if (closeWhenRunning)
/* 225 */       this.d.dispose();
/*     */   }
/*     */ 
/*     */   public void keyPressed(KeyEvent ke) {
/* 229 */     int key = ke.getKeyCode();
/* 230 */     int items = this.completionsModel.getSize();
/* 231 */     Object source = ke.getSource();
/* 232 */     if (key == 27) {
/* 233 */       this.d.dispose();
/* 234 */     } else if (source == this.prompt)
/*     */     {
/* 238 */       if ((key == 10) && 
/* 239 */         (1 == items)) {
/* 240 */         String selected = (String)this.completionsModel.elementAt(0);
/* 241 */         runFromLabel(selected);
/*     */       }
/*     */ 
/* 248 */       int index = -1;
/* 249 */       if (key == 38) {
/* 250 */         index = this.completions.getSelectedIndex() - 1;
/* 251 */         if (index < 0)
/* 252 */           index = items - 1;
/*     */       }
/* 254 */       else if (key == 40) {
/* 255 */         index = this.completions.getSelectedIndex() + 1;
/* 256 */         if (index >= items)
/* 257 */           index = Math.min(items - 1, 0);
/*     */       }
/* 259 */       else if (key == 34) {
/* 260 */         index = this.completions.getLastVisibleIndex();
/* 261 */       }if (index >= 0) {
/* 262 */         this.completions.requestFocus();
/* 263 */         this.completions.ensureIndexIsVisible(index);
/* 264 */         this.completions.setSelectedIndex(index);
/*     */       }
/* 266 */     } else if (key == 8)
/*     */     {
/* 270 */       this.prompt.requestFocus();
/* 271 */     } else if (source == this.completions)
/*     */     {
/* 275 */       if (key == 10) {
/* 276 */         String selected = (String)this.completions.getSelectedValue();
/* 277 */         if (selected != null)
/* 278 */           runFromLabel(selected);
/*     */       }
/* 280 */       else if (key == 38) {
/* 281 */         if (this.completions.getSelectedIndex() <= 0) {
/* 282 */           this.completions.clearSelection();
/* 283 */           this.prompt.requestFocus();
/*     */         }
/*     */       }
/* 286 */       else if ((key == 40) && 
/* 287 */         (this.completions.getSelectedIndex() == items - 1)) {
/* 288 */         this.completions.clearSelection();
/* 289 */         this.prompt.requestFocus();
/*     */       }
/*     */     }
/* 292 */     else if (source == this.runButton) {
/* 293 */       if (key == 10) {
/* 294 */         String selected = (String)this.completions.getSelectedValue();
/* 295 */         if (selected != null)
/* 296 */           runFromLabel(selected);
/*     */       }
/* 298 */     } else if ((source == this.closeButton) && 
/* 299 */       (key == 10)) {
/* 300 */       this.d.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void keyReleased(KeyEvent ke)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void keyTyped(KeyEvent ke)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void parseMenu(String path, Menu menu)
/*     */   {
/* 325 */     int n = menu.getItemCount();
/*     */     CommandAction caAfter;
/* 326 */     for (int i = 0; i < n; i++) {
/* 327 */       MenuItem m = menu.getItem(i);
/* 328 */       String label = m.getLabel();
/* 329 */       if ((m instanceof Menu)) {
/* 330 */         Menu subMenu = (Menu)m;
/* 331 */         parseMenu(path + " > " + label, subMenu);
/*     */       } else {
/* 333 */         String trimmedLabel = label.trim();
/* 334 */         if ((trimmedLabel.length() != 0) && (!trimmedLabel.equals("-")))
/*     */         {
/* 336 */           CommandAction ca = (CommandAction)this.commandsHash.get(label);
/* 337 */           if (ca == null) {
/* 338 */             this.commandsHash.put(label, new CommandAction(null, m, path));
/*     */           } else {
/* 340 */             ca.menuItem = m;
/* 341 */             ca.menuLocation = path;
/*     */           }
/* 343 */           caAfter = (CommandAction)this.commandsHash.get(label);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void findAllMenuItems()
/*     */   {
/* 352 */     MenuBar menuBar = Menus.getMenuBar();
/* 353 */     int topLevelMenus = menuBar.getMenuCount();
/* 354 */     for (int i = 0; i < topLevelMenus; i++) {
/* 355 */       Menu topLevelMenu = menuBar.getMenu(i);
/* 356 */       parseMenu(topLevelMenu.getLabel(), topLevelMenu);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void run(String ignored)
/*     */   {
/* 362 */     this.commandsHash = new Hashtable();
/*     */ 
/* 367 */     Hashtable realCommandsHash = (Hashtable)Menus.getCommands().clone();
/*     */ 
/* 369 */     Set realCommandSet = realCommandsHash.keySet();
/*     */ 
/* 371 */     Iterator i = realCommandSet.iterator();
/* 372 */     while (i.hasNext()) {
/* 373 */       String command = (String)i.next();
/*     */ 
/* 375 */       String trimmedCommand = command.trim();
/* 376 */       if ((trimmedCommand.length() > 0) && (!trimmedCommand.equals("-"))) {
/* 377 */         this.commandsHash.put(command, new CommandAction((String)realCommandsHash.get(command), null, null));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 388 */     findAllMenuItems();
/*     */ 
/* 393 */     this.commands = ((String[])this.commandsHash.keySet().toArray(new String[0]));
/* 394 */     Arrays.sort(this.commands);
/*     */ 
/* 396 */     this.listLabelToCommand = new Hashtable();
/*     */ 
/* 398 */     for (int i = 0; i < this.commands.length; i++) {
/* 399 */       CommandAction ca = (CommandAction)this.commandsHash.get(this.commands[i]);
/* 400 */       this.listLabelToCommand.put(makeListLabel(this.commands[i], ca, true), this.commands[i]);
/* 401 */       this.listLabelToCommand.put(makeListLabel(this.commands[i], ca, false), this.commands[i]);
/*     */     }
/*     */ 
/* 406 */     ImageJ imageJ = IJ.getInstance();
/*     */ 
/* 408 */     this.d = new JFrame("Command Finder") {
/*     */       public void setVisible(boolean visible) {
/* 410 */         if (visible)
/* 411 */           WindowManager.addWindow(this);
/* 412 */         super.setVisible(visible);
/*     */       }
/*     */ 
/*     */       public void dispose() {
/* 416 */         WindowManager.removeWindow(this);
/* 417 */         super.dispose();
/*     */       }
/*     */     };
/* 420 */     Container contentPane = this.d.getContentPane();
/* 421 */     contentPane.setLayout(new BorderLayout());
/* 422 */     this.d.addWindowListener(this);
/*     */ 
/* 424 */     this.fullInfoCheckBox = new JCheckBox("Show full information", false);
/* 425 */     this.fullInfoCheckBox.addItemListener(this);
/* 426 */     this.closeCheckBox = new JCheckBox("Close when running", closeWhenRunning);
/* 427 */     this.closeCheckBox.addItemListener(this);
/*     */ 
/* 429 */     JPanel northPanel = new JPanel();
/*     */ 
/* 431 */     northPanel.add(new JLabel("Type part of a command:"));
/*     */ 
/* 433 */     this.prompt = new JTextField("", 30);
/* 434 */     this.prompt.getDocument().addDocumentListener(new PromptDocumentListener());
/* 435 */     this.prompt.addKeyListener(this);
/*     */ 
/* 437 */     northPanel.add(this.prompt);
/*     */ 
/* 439 */     contentPane.add(northPanel, "North");
/*     */ 
/* 441 */     this.completionsModel = new DefaultListModel();
/* 442 */     this.completions = new JList(this.completionsModel);
/* 443 */     this.scrollPane = new JScrollPane(this.completions);
/*     */ 
/* 445 */     this.completions.setSelectionMode(0);
/* 446 */     this.completions.setLayoutOrientation(0);
/*     */ 
/* 448 */     this.completions.setVisibleRowCount(20);
/* 449 */     this.completions.addKeyListener(this);
/* 450 */     populateList("");
/*     */ 
/* 452 */     contentPane.add(this.scrollPane, "Center");
/*     */ 
/* 454 */     this.completions.addMouseListener(this);
/*     */ 
/* 456 */     this.runButton = new JButton("Run");
/* 457 */     this.exportButton = new JButton("Export");
/* 458 */     this.closeButton = new JButton("Close");
/*     */ 
/* 460 */     this.runButton.addActionListener(this);
/* 461 */     this.exportButton.addActionListener(this);
/* 462 */     this.closeButton.addActionListener(this);
/* 463 */     this.runButton.addKeyListener(this);
/* 464 */     this.closeButton.addKeyListener(this);
/*     */ 
/* 466 */     JPanel southPanel = new JPanel();
/* 467 */     southPanel.setLayout(new BorderLayout());
/*     */ 
/* 469 */     JPanel optionsPanel = new JPanel();
/* 470 */     optionsPanel.add(this.fullInfoCheckBox);
/* 471 */     optionsPanel.add(this.closeCheckBox);
/*     */ 
/* 473 */     JPanel buttonsPanel = new JPanel();
/* 474 */     buttonsPanel.add(this.runButton);
/* 475 */     buttonsPanel.add(this.exportButton);
/* 476 */     buttonsPanel.add(this.closeButton);
/*     */ 
/* 478 */     southPanel.add(optionsPanel, "Center");
/* 479 */     southPanel.add(buttonsPanel, "South");
/*     */ 
/* 481 */     contentPane.add(southPanel, "South");
/*     */ 
/* 483 */     Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
/*     */ 
/* 485 */     this.d.pack();
/*     */ 
/* 487 */     int dialogWidth = this.d.getWidth();
/* 488 */     int dialogHeight = this.d.getHeight();
/*     */ 
/* 490 */     int screenWidth = (int)screenSize.getWidth();
/* 491 */     int screenHeight = (int)screenSize.getHeight();
/*     */ 
/* 493 */     Point pos = imageJ.getLocationOnScreen();
/*     */ 
/* 500 */     int initialX = (int)pos.getX() + 38;
/* 501 */     int initialY = (int)pos.getY() + 84;
/*     */ 
/* 503 */     if (initialX + dialogWidth > screenWidth)
/* 504 */       initialX = screenWidth - dialogWidth;
/* 505 */     if (initialX < 0)
/* 506 */       initialX = 0;
/* 507 */     if (initialY + dialogHeight > screenHeight)
/* 508 */       initialY = screenHeight - dialogHeight;
/* 509 */     if (initialY < 0) {
/* 510 */       initialY = 0;
/*     */     }
/* 512 */     this.d.setLocation(initialX, initialY);
/*     */ 
/* 514 */     this.d.setVisible(true);
/* 515 */     this.d.toFront();
/*     */   }
/*     */ 
/*     */   public void windowClosing(WindowEvent e)
/*     */   {
/* 521 */     this.d.dispose();
/* 522 */     Prefs.set("command-finder.close", closeWhenRunning);
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
/*     */ 
/*     */   class PromptDocumentListener
/*     */     implements DocumentListener
/*     */   {
/*     */     PromptDocumentListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void insertUpdate(DocumentEvent e)
/*     */     {
/* 310 */       CommandFinder.this.populateList(CommandFinder.this.prompt.getText());
/*     */     }
/*     */     public void removeUpdate(DocumentEvent e) {
/* 313 */       CommandFinder.this.populateList(CommandFinder.this.prompt.getText());
/*     */     }
/*     */     public void changedUpdate(DocumentEvent e) {
/* 316 */       CommandFinder.this.populateList(CommandFinder.this.prompt.getText());
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class LevenshteinPair
/*     */     implements Comparable
/*     */   {
/*     */     int index;
/*     */     int cost;
/*     */ 
/*     */     LevenshteinPair(int index, int cost)
/*     */     {
/* 146 */       this.index = index;
/* 147 */       this.cost = cost;
/*     */     }
/*     */ 
/*     */     public int compareTo(Object o) {
/* 151 */       return this.cost - ((LevenshteinPair)o).cost;
/*     */     }
/*     */   }
/*     */ 
/*     */   class CommandAction
/*     */   {
/*     */     String classCommand;
/*     */     MenuItem menuItem;
/*     */     String menuLocation;
/*     */ 
/*     */     CommandAction(String classCommand, MenuItem menuItem, String menuLocation)
/*     */     {
/*  81 */       this.classCommand = classCommand;
/*  82 */       this.menuItem = menuItem;
/*  83 */       this.menuLocation = menuLocation;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/*  89 */       return "classCommand: " + this.classCommand + ", menuItem: " + this.menuItem + ", menuLocation: " + this.menuLocation;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.CommandFinder
 * JD-Core Version:    0.6.2
 */