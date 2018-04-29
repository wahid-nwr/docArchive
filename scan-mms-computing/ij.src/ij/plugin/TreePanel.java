/*      */ package ij.plugin;
/*      */ 
/*      */ import ij.IJ;
/*      */ import ij.ImageJ;
/*      */ import ij.WindowManager;
/*      */ import ij.gui.GUI;
/*      */ import java.awt.AWTEventMulticaster;
/*      */ import java.awt.Component;
/*      */ import java.awt.Container;
/*      */ import java.awt.Cursor;
/*      */ import java.awt.Insets;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.ComponentAdapter;
/*      */ import java.awt.event.ComponentEvent;
/*      */ import java.awt.event.MouseAdapter;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.MouseMotionAdapter;
/*      */ import java.awt.event.WindowEvent;
/*      */ import java.awt.event.WindowListener;
/*      */ import java.awt.image.IndexColorModel;
/*      */ import java.awt.image.MemoryImageSource;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Hashtable;
/*      */ import javax.swing.ImageIcon;
/*      */ import javax.swing.JCheckBoxMenuItem;
/*      */ import javax.swing.JFrame;
/*      */ import javax.swing.JMenuBar;
/*      */ import javax.swing.JMenuItem;
/*      */ import javax.swing.JScrollPane;
/*      */ import javax.swing.JTree;
/*      */ import javax.swing.SwingUtilities;
/*      */ import javax.swing.event.TreeExpansionEvent;
/*      */ import javax.swing.event.TreeExpansionListener;
/*      */ import javax.swing.event.TreeWillExpandListener;
/*      */ import javax.swing.tree.DefaultMutableTreeNode;
/*      */ import javax.swing.tree.DefaultTreeModel;
/*      */ import javax.swing.tree.TreeNode;
/*      */ import javax.swing.tree.TreePath;
/*      */ import javax.swing.tree.TreeSelectionModel;
/*      */ 
/*      */ class TreePanel
/*      */   implements ActionListener, WindowListener, TreeExpansionListener, TreeWillExpandListener
/*      */ {
/*      */   ControlPanel pcp;
/*      */   boolean isMainPanel;
/*      */   String title;
/*  654 */   boolean isDragging = false;
/*      */   Point defaultLocation;
/*      */   private JTree pTree;
/*      */   private JMenuBar pMenuBar;
/*      */   private DefaultMutableTreeNode root;
/*  661 */   private DefaultMutableTreeNode draggingNode = null;
/*      */   private DefaultTreeModel pTreeModel;
/*      */   private ActionListener listener;
/*      */   private JFrame pFrame;
/*      */   private JCheckBoxMenuItem pMenu_saveOnClose;
/*      */   private JCheckBoxMenuItem pMenu_noClutter;
/*      */   private TreePath rootPath;
/*  669 */   private static final int[] _uparrow1_data = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13, 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13, 1, 1, 13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 14, 2, 1, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 14, 4, 5, 6, 1, 7, 0, 0, 0, 0, 0, 0, 0, 0, 1, 8, 4, 9, 14, 2, 6, 1, 0, 0, 0, 0, 0, 0, 0, 2, 8, 4, 9, 14, 14, 14, 2, 6, 1, 0, 0, 0, 0, 0, 8, 8, 4, 9, 14, 14, 14, 14, 14, 2, 6, 2, 0, 0, 0, 8, 10, 14, 8, 10, 11, 11, 12, 12, 12, 12, 12, 6, 2, 0, 14, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*      */ 
/*  691 */   private static final int[] _uparrow1_ctable = { 33, -16777216, -13619152, -5592406, -1, -12829636, -14342875, -4802890, -10987432, -3947581, -14540254, -13948117, -13750738, -6250336, -8355712 };
/*      */ 
/*  696 */   private static IndexColorModel iconCM = new IndexColorModel(8, _uparrow1_ctable.length, _uparrow1_ctable, 0, true, 255, 0);
/*  697 */   private static final ImageIcon upIcon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(16, 16, iconCM, _uparrow1_data, 0, 16)));
/*      */ 
/*      */   public TreePanel(DefaultMutableTreeNode root, ControlPanel pcp, boolean isMainPanel) {
/*  700 */     new TreePanel(root, pcp, isMainPanel, null);
/*      */   }
/*      */ 
/*      */   public TreePanel(DefaultMutableTreeNode root, ControlPanel pcp, boolean isMainPanel, Point location) {
/*  704 */     this.root = root;
/*  705 */     this.pcp = pcp;
/*  706 */     this.isMainPanel = isMainPanel;
/*  707 */     this.defaultLocation = location;
/*  708 */     this.rootPath = new TreePath(root.getPath());
/*  709 */     this.title = ((String)root.getUserObject());
/*  710 */     buildTreePanel();
/*  711 */     pcp.registerPanel(this);
/*      */   }
/*      */ 
/*      */   public void buildTreePanel()
/*      */   {
/*  719 */     this.pFrame = new JFrame(this.title);
/*  720 */     this.pFrame.setDefaultCloseOperation(2);
/*  721 */     this.pTreeModel = new DefaultTreeModel(this.root);
/*  722 */     this.pTree = new JTree(this.pTreeModel);
/*  723 */     this.pTree.setEditable(false);
/*  724 */     this.pTree.putClientProperty("JTree.lineStyle", "Angled");
/*  725 */     this.pTree.getSelectionModel().setSelectionMode(1);
/*  726 */     this.pTree.setRootVisible(false);
/*  727 */     this.pTree.setShowsRootHandles(true);
/*      */ 
/*  729 */     JScrollPane ptView = new JScrollPane(this.pTree);
/*  730 */     addMenu();
/*  731 */     this.pFrame.getContentPane().add(ptView, "Center");
/*  732 */     addListeners();
/*  733 */     this.pFrame.pack();
/*  734 */     if (this.defaultLocation != null) {
/*  735 */       if (IJ.debugMode) IJ.log("CP.buildTreePanel: " + this.defaultLocation);
/*  736 */       this.pFrame.setLocation(this.defaultLocation.x, this.defaultLocation.y);
/*      */     } else {
/*  738 */       this.pcp.restoreGeometry(this);
/*      */     }
/*  740 */     if (this.pFrame.getLocation().x == 0)
/*  741 */       GUI.center(this.pFrame);
/*  742 */     setVisible();
/*  743 */     ImageJ ij = IJ.getInstance();
/*  744 */     ij.addWindowListener(this);
/*  745 */     this.pFrame.addKeyListener(ij);
/*  746 */     this.pTree.addKeyListener(ij);
/*      */   }
/*      */ 
/*      */   void addMenu() {
/*  750 */     this.pMenuBar = new JMenuBar();
/*  751 */     Insets ins = new Insets(0, 0, 0, 10);
/*  752 */     this.pMenuBar.setMargin(ins);
/*  753 */     if (this.isMainPanel) {
/*  754 */       JMenuItem helpMI = new JMenuItem("Help");
/*  755 */       helpMI.addActionListener(this);
/*  756 */       helpMI.setActionCommand("Help");
/*  757 */       this.pMenuBar.add(helpMI);
/*      */     }
/*      */     else
/*      */     {
/*  765 */       JMenuItem spMI = new JMenuItem("Show Parent", upIcon);
/*  766 */       spMI.addActionListener(this);
/*  767 */       spMI.setActionCommand("Show Parent");
/*  768 */       this.pMenuBar.add(spMI);
/*      */     }
/*  770 */     this.pFrame.setJMenuBar(this.pMenuBar);
/*      */   }
/*      */ 
/*      */   void addListeners() {
/*  774 */     addActionListener(this);
/*  775 */     this.pFrame.addWindowListener(this);
/*  776 */     this.pFrame.addComponentListener(new ComponentAdapter() {
/*      */       public void componentMoved(ComponentEvent e) {
/*  778 */         Rectangle r = e.getComponent().getBounds();
/*  779 */         if (IJ.debugMode) IJ.log("CP.componentMoved: " + r);
/*  780 */         if (r.x > 0) {
/*  781 */           TreePanel.this.defaultLocation = new Point(r.x, r.y);
/*  782 */           TreePanel.this.recordGeometry();
/*      */         }
/*      */       }
/*      */     });
/*  786 */     this.pTree.addMouseListener(new MouseAdapter() {
/*      */       public void mouseClicked(MouseEvent e) {
/*  788 */         TreePanel.this.isDragging = false;
/*  789 */         if ((TreePanel.this.pcp.requiresDoubleClick()) && (e.getClickCount() != 2)) return;
/*  790 */         int selRow = TreePanel.this.pTree.getRowForLocation(e.getX(), e.getY());
/*  791 */         if (selRow != -1) TreePanel.this.toAction(); 
/*      */       }
/*      */ 
/*      */       public void mouseReleased(MouseEvent e)
/*      */       {
/*  795 */         if (TreePanel.this.isDragging) {
/*  796 */           Point pnt = new Point(e.getX(), e.getY());
/*  797 */           SwingUtilities.convertPointToScreen(pnt, TreePanel.this.pTree);
/*  798 */           TreePanel.this.tearOff(null, pnt);
/*      */         }
/*  800 */         TreePanel.this.isDragging = false;
/*      */       }
/*      */     });
/*  803 */     this.pTree.addMouseMotionListener(new MouseMotionAdapter()
/*      */     {
/*      */       public void mouseDragged(MouseEvent e)
/*      */       {
/*  807 */         int selRow = TreePanel.this.pTree.getRowForLocation(e.getX(), e.getY());
/*  808 */         if (selRow != -1)
/*      */         {
/*  810 */           if (((DefaultMutableTreeNode)TreePanel.this.pTree.getLastSelectedPathComponent()).isLeaf()) return;
/*  811 */           TreePanel.this.pFrame.setCursor(new Cursor(13));
/*  812 */           TreePanel.this.isDragging = true;
/*      */         }
/*      */       }
/*      */     });
/*  816 */     this.pTree.addTreeExpansionListener(this);
/*  817 */     this.pTree.addTreeWillExpandListener(this);
/*      */   }
/*      */ 
/*      */   public String getTitle()
/*      */   {
/*  824 */     return this.title;
/*      */   }
/*  826 */   public TreePath getRootPath() { return this.rootPath; } 
/*      */   public boolean isTheMainPanel() {
/*  828 */     return this.isMainPanel;
/*      */   }
/*  830 */   public JFrame getFrame() { return this.pFrame; } 
/*      */   public JTree getTree() {
/*  832 */     return this.pTree;
/*      */   }
/*  834 */   public DefaultMutableTreeNode getRootNode() { return this.root; } 
/*      */   public Point getDefaultLocation() {
/*  836 */     return this.defaultLocation;
/*      */   }
/*      */ 
/*      */   boolean isVisible()
/*      */   {
/*  843 */     return this.pFrame.isVisible();
/*      */   }
/*      */ 
/*      */   void setBounds(int x, int y, int w, int h) {
/*  847 */     this.pFrame.setBounds(new Rectangle(x, y, w, h));
/*  848 */     this.defaultLocation = new Point(x, y);
/*      */   }
/*      */ 
/*      */   void setAutoSaveProps(boolean autoSave) {
/*  852 */     if (isTheMainPanel()) this.pMenu_saveOnClose.setSelected(autoSave); 
/*      */   }
/*      */ 
/*  855 */   boolean getAutoSaveProps() { return this.pMenu_saveOnClose.isSelected(); }
/*      */ 
/*      */   void restoreExpandedNodes() {
/*  858 */     if ((this.pTree == null) || (this.root == null))
/*  859 */       return;
/*  860 */     this.pTree.removeTreeExpansionListener(this);
/*  861 */     TreeNode[] rootPath = this.root.getPath();
/*  862 */     for (Enumeration e = this.root.breadthFirstEnumeration(); e.hasMoreElements(); )
/*      */     {
/*  865 */       DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.nextElement();
/*  866 */       if ((!node.isLeaf()) && (node != this.root))
/*      */       {
/*  868 */         TreeNode[] nodePath = node.getPath();
/*  869 */         TreePath nTreePath = new TreePath(nodePath);
/*  870 */         String npS = nTreePath.toString();
/*      */ 
/*  875 */         DefaultMutableTreeNode[] localPath = new DefaultMutableTreeNode[nodePath.length - rootPath.length + 1];
/*  876 */         for (int i = 0; i < localPath.length; i++)
/*      */         {
/*  878 */           localPath[i] = ((DefaultMutableTreeNode)nodePath[(i + rootPath.length - 1)]);
/*      */         }
/*  880 */         TreePath newPath = new TreePath(localPath);
/*  881 */         if ((this.pcp.hasExpandedStateProperty(npS)) && (!this.pcp.hasPanelShowingProperty(npS)))
/*      */         {
/*  883 */           if (newPath != null)
/*      */           {
/*      */             try
/*      */             {
/*  887 */               this.pTree.expandPath(newPath);
/*      */             } catch (Throwable t) {
/*      */             }
/*      */           }
/*  891 */         } else if (((this.pcp.hasExpandedStateProperty(npS)) || (this.pTree.isExpanded(newPath))) && (this.pcp.hasPanelShowingProperty(npS)))
/*      */         {
/*  893 */           this.pTree.collapsePath(newPath);
/*  894 */           this.pcp.unsetExpandedStateProperty(npS);
/*      */         }
/*      */       }
/*      */     }
/*  898 */     this.pTree.addTreeExpansionListener(this);
/*      */   }
/*      */ 
/*      */   public void processEvent(ActionEvent e)
/*      */   {
/*  906 */     if (this.listener != null) this.listener.actionPerformed(e); 
/*      */   }
/*      */ 
/*      */   public void addActionListener(ActionListener al)
/*      */   {
/*  910 */     this.listener = AWTEventMulticaster.add(this.listener, al);
/*      */   }
/*      */ 
/*      */   public void removeActionListener(ActionListener al) {
/*  914 */     this.listener = AWTEventMulticaster.remove(this.listener, al);
/*      */   }
/*      */ 
/*      */   public void actionPerformed(ActionEvent e) {
/*  918 */     String cmd = e.getActionCommand();
/*      */ 
/*  920 */     if (cmd == null) return;
/*  921 */     if (cmd.equals("Help")) {
/*  922 */       showHelp();
/*  923 */       return;
/*      */     }
/*  925 */     if (cmd.equals("Show Parent")) {
/*  926 */       DefaultMutableTreeNode parent = (DefaultMutableTreeNode)this.root.getParent();
/*  927 */       if (parent != null)
/*      */       {
/*  929 */         TreePanel panel = this.pcp.getPanelForNode(parent);
/*  930 */         if (panel == null) panel = this.pcp.newPanel(parent);
/*  931 */         if (panel != null) panel.setVisible();
/*      */       }
/*  933 */       return;
/*      */     }
/*  935 */     if (cmd.equals("Reload Plugins From Panel")) {
/*  936 */       this.pcp.closeAll(false);
/*  937 */       IJ.doCommand("Reload Plugins");
/*      */     }
/*      */     else {
/*  940 */       if (cmd.equals("Reload Plugins"))
/*  941 */         this.pcp.closeAll(false);
/*      */       else
/*  943 */         IJ.doCommand(cmd);
/*  944 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void windowClosing(WindowEvent e)
/*      */   {
/*  955 */     if (IJ.debugMode) IJ.log("CP.windowClosing: " + this.isMainPanel);
/*  956 */     if (this.isMainPanel)
/*  957 */       this.pcp.saveProperties();
/*  958 */     this.pcp.unsetPanelShowingProperty(getRootPath().toString());
/*      */   }
/*      */ 
/*      */   public void windowActivated(WindowEvent e) {
/*  962 */     WindowManager.setWindow(getFrame());
/*      */   }
/*      */   public void windowClosed(WindowEvent e) {
/*      */   }
/*      */   public void windowDeactivated(WindowEvent e) {
/*      */   }
/*      */   public void windowDeiconified(WindowEvent e) {
/*      */   }
/*      */   public void windowIconified(WindowEvent e) {
/*      */   }
/*      */   public void windowOpened(WindowEvent e) {  } 
/*  973 */   public void treeCollapsed(TreeExpansionEvent ev) { String evPathString = ev.getPath().toString();
/*  974 */     evPathString = evPathString.substring(evPathString.indexOf("[") + 1, evPathString.lastIndexOf("]"));
/*  975 */     evPathString = evPathString.substring(getTitle().length() + 2, evPathString.length());
/*  976 */     String rootPath = getRootPath().toString();
/*  977 */     rootPath = rootPath.substring(rootPath.indexOf("[") + 1, rootPath.lastIndexOf("]"));
/*  978 */     String path = "[" + rootPath + ", " + evPathString + "]";
/*      */ 
/*  980 */     this.pcp.unsetExpandedStateProperty(path); }
/*      */ 
/*      */   public void treeExpanded(TreeExpansionEvent ev)
/*      */   {
/*  984 */     TreePath evPath = ev.getPath();
/*      */ 
/*  986 */     String evPathString = ev.getPath().toString();
/*  987 */     evPathString = this.pcp.pStr2Key(evPathString);
/*  988 */     evPathString = evPathString.substring(getTitle().length() + 1, evPathString.length());
/*  989 */     String rootPath = getRootPath().toString();
/*  990 */     rootPath = this.pcp.pStr2Key(rootPath);
/*      */ 
/*  992 */     String path = rootPath + "." + evPathString;
/*  993 */     if (this.pcp.hasPanelShowingProperty(path)) {
/*  994 */       Hashtable panels = this.pcp.getPanels();
/*  995 */       TreePanel p = (TreePanel)panels.get(path);
/*  996 */       if (p != null) p.close();
/*      */     }
/*      */ 
/*  999 */     this.pcp.setExpandedStateProperty(path);
/*      */   }
/*      */   public void treeWillExpand(TreeExpansionEvent ev) {
/*      */   }
/*      */   public void treeWillCollapse(TreeExpansionEvent ev) {
/*      */   }
/*      */ 
/*      */   void recordGeometry() {
/* 1007 */     this.pcp.recordGeometry(this);
/*      */   }
/*      */ 
/*      */   void refreshTree()
/*      */   {
/* 1014 */     this.pTreeModel.reload();
/*      */   }
/*      */ 
/*      */   void tearOff() {
/* 1018 */     tearOff(null);
/*      */   }
/*      */ 
/*      */   void tearOff(DefaultMutableTreeNode node) {
/* 1022 */     tearOff(node, null);
/*      */   }
/*      */ 
/*      */   void tearOff(DefaultMutableTreeNode node, Point pnt) {
/* 1026 */     this.isDragging = false;
/* 1027 */     this.pFrame.setCursor(Cursor.getDefaultCursor());
/* 1028 */     if (node == null)
/* 1029 */       node = (DefaultMutableTreeNode)this.pTree.getLastSelectedPathComponent();
/* 1030 */     if (node.isLeaf()) return;
/* 1031 */     TreeNode[] nPath = node.getPath();
/* 1032 */     TreeNode[] rPath = this.root.getPath();
/* 1033 */     DefaultMutableTreeNode[] tPath = new DefaultMutableTreeNode[nPath.length - rPath.length + 1];
/* 1034 */     for (int i = 0; i < tPath.length; i++)
/* 1035 */       tPath[i] = ((DefaultMutableTreeNode)nPath[(i + rPath.length - 1)]);
/* 1036 */     TreePath path = new TreePath(nPath);
/* 1037 */     TreePath localPath = new TreePath(tPath);
/* 1038 */     String pathString = localPath.toString();
/*      */ 
/* 1040 */     TreePanel p = this.pcp.getPanelForNode(node);
/* 1041 */     if (p == null) {
/* 1042 */       if (pnt != null)
/* 1043 */         p = this.pcp.newPanel(node, pnt);
/*      */       else
/* 1045 */         p = this.pcp.newPanel(node);
/* 1046 */       this.pTree.collapsePath(localPath);
/*      */     } else {
/* 1048 */       if (pnt != null)
/* 1049 */         p.setLocation(pnt);
/* 1050 */       p.setVisible();
/* 1051 */       this.pTree.collapsePath(localPath);
/*      */     }
/*      */   }
/*      */ 
/*      */   void toAction()
/*      */   {
/* 1057 */     DefaultMutableTreeNode nde = (DefaultMutableTreeNode)this.pTree.getLastSelectedPathComponent();
/*      */ 
/* 1059 */     if (nde.getChildCount() > 0) return;
/* 1060 */     String aCmd = nde.toString();
/* 1061 */     String cmd = aCmd;
/* 1062 */     if (this.pcp.treeCommands.containsKey(aCmd))
/* 1063 */       cmd = (String)this.pcp.treeCommands.get(aCmd);
/* 1064 */     processEvent(new ActionEvent(this, 1001, cmd));
/*      */   }
/*      */ 
/*      */   void setVisible()
/*      */   {
/* 1069 */     if ((this.pFrame != null) && (!this.pFrame.isVisible())) {
/* 1070 */       restoreExpandedNodes();
/* 1071 */       if (this.defaultLocation != null) this.pFrame.setLocation(this.defaultLocation);
/* 1072 */       this.pFrame.setVisible(true);
/*      */ 
/* 1074 */       DefaultMutableTreeNode parent = (DefaultMutableTreeNode)this.root.getParent();
/* 1075 */       if (parent != null) {
/* 1076 */         TreePanel pnl = this.pcp.getPanelForNode(parent);
/* 1077 */         if ((pnl != null) && (pnl.isVisible())) {
/* 1078 */           TreeNode[] rPath = this.root.getPath();
/* 1079 */           TreeNode[] pPath = pnl.getRootNode().getPath();
/* 1080 */           DefaultMutableTreeNode[] tPath = new DefaultMutableTreeNode[rPath.length - pPath.length + 1];
/* 1081 */           for (int i = 0; i < tPath.length; i++) {
/* 1082 */             tPath[i] = ((DefaultMutableTreeNode)rPath[(i + pPath.length - 1)]);
/*      */           }
/* 1084 */           TreePath localPath = new TreePath(tPath);
/*      */ 
/* 1087 */           pnl.getTree().collapsePath(localPath);
/*      */         }
/*      */       }
/*      */     }
/* 1091 */     if (this.pcp != null) this.pcp.setPanelShowingProperty(getRootPath().toString()); 
/*      */   }
/*      */ 
/*      */   void setLocation(Point p)
/*      */   {
/* 1095 */     if (p != null) this.defaultLocation = p; 
/*      */   }
/*      */ 
/*      */   void close()
/*      */   {
/* 1099 */     this.pFrame.dispatchEvent(new WindowEvent(this.pFrame, 201));
/* 1100 */     this.pcp.unsetPanelShowingProperty(getRootPath().toString());
/*      */   }
/*      */   private void showHelp() {
/* 1103 */     this.pcp.showHelp();
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.TreePanel
 * JD-Core Version:    0.6.2
 */