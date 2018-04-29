/*     */ package uk.co.mmscomputing.util;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Container;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Properties;
/*     */ import javax.swing.JApplet;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTabbedPane;
/*     */ import javax.swing.SwingUtilities;
/*     */ import uk.co.mmscomputing.util.log.LogBook;
/*     */ 
/*     */ public abstract class UtilMainApp extends JApplet
/*     */ {
/*  14 */   private Properties properties = new Properties();
/*     */   private File propertiesFile;
/*  16 */   private JFrame frame = null;
/*     */ 
/*     */   public UtilMainApp()
/*     */   {
/*  20 */     this.frame = null;
/*     */   }
/*     */ 
/*     */   public UtilMainApp(String paramString, String[] paramArrayOfString) {
/*  24 */     JFrame.setDefaultLookAndFeelDecorated(true);
/*     */ 
/*  26 */     this.frame = new JFrame(paramString);
/*     */ 
/*  29 */     this.frame.addWindowListener(new WindowAdapter() {
/*     */       public void windowClosing(WindowEvent paramAnonymousWindowEvent) {
/*  31 */         UtilMainApp.this.stop(); System.exit(0);
/*     */       }
/*     */     });
/*  35 */     init();
/*  36 */     start();
/*     */   }
/*     */   protected boolean isApplet() {
/*  39 */     return this.frame == null;
/*     */   }
/*     */   protected void setFrameSize(JFrame paramJFrame, Rectangle paramRectangle) {
/*  42 */     paramJFrame.setSize(paramRectangle.width * 4 / 5, paramRectangle.height * 4 / 5);
/*     */   }
/*     */ 
/*     */   protected abstract JPanel getCenterPanel(Properties paramProperties)
/*     */     throws Exception;
/*     */ 
/*     */   public void createGUI()
/*     */   {
/*     */     try
/*     */     {
/*  59 */       LogBook localLogBook = new LogBook(false);
/*     */ 
/*  61 */       Runtime localRuntime = Runtime.getRuntime();
/*  62 */       System.out.println("Runtime Total Memory: " + localRuntime.totalMemory() / 1048576L + " MB");
/*  63 */       System.out.println("Runtime Max   Memory: " + localRuntime.maxMemory() / 1048576L + " MB");
/*     */ 
/*  65 */       String str1 = System.getProperty("java.home");
/*  66 */       System.out.println("java directory: " + str1);
/*     */ 
/*  68 */       String str2 = getClass().getName();
/*  69 */       String str3 = str2.substring(0, str2.lastIndexOf('.')) + ".properties.txt";
/*     */ 
/*  71 */       String str4 = System.getProperty("user.dir");
/*  72 */       System.out.println("current directory: " + str4);
/*     */ 
/*  74 */       String str5 = System.getProperty("user.home");
/*  75 */       System.out.println("user directory: " + str5);
/*     */ 
/*  77 */       File localFile = new File(str5, "mmsc");
/*     */       try {
/*  79 */         localFile.mkdirs();
/*  80 */         this.propertiesFile = new File(localFile.getAbsolutePath(), str3);
/*     */       } catch (Exception localException2) {
/*  82 */         System.out.println("9\bCould not create directory:\n\t" + localFile.getAbsolutePath() + "\n\t" + localException2);
/*  83 */         this.propertiesFile = new File(str3);
/*     */       }
/*     */ 
/*  86 */       System.out.println("properties file: " + this.propertiesFile.getAbsolutePath());
/*     */ 
/*  88 */       if (this.propertiesFile.exists()) this.properties.load(new FileInputStream(this.propertiesFile));
/*     */ 
/*  91 */       JTabbedPane localJTabbedPane = new JTabbedPane();
/*     */ 
/*  93 */       String str6 = this.properties.getProperty(getClass().getName() + ".mainapp.title");
/*  94 */       if (str6 == null) str6 = "MainApp";
/*  95 */       JPanel localJPanel = getCenterPanel(this.properties);
/*  96 */       localJTabbedPane.addTab(str6, localJPanel);
/*     */ 
/*  98 */       String str7 = this.properties.getProperty(getClass().getName() + ".log.title");
/*  99 */       if (str7 == null) str7 = "Log";
/* 100 */       localJTabbedPane.addTab("Log", localLogBook);
/*     */ 
/* 102 */       Container localContainer = getContentPane();
/* 103 */       localContainer.setLayout(new BorderLayout());
/* 104 */       localContainer.add(localJTabbedPane, "Center");
/*     */ 
/* 106 */       if (this.frame != null) {
/* 107 */         this.frame.getContentPane().add(this);
/*     */ 
/* 109 */         GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
/* 110 */         setFrameSize(this.frame, localGraphicsEnvironment.getMaximumWindowBounds());
/*     */ 
/* 112 */         this.frame.setLocationRelativeTo(null);
/* 113 */         this.frame.setVisible(true);
/*     */       }
/*     */     }
/*     */     catch (Exception localException1) {
/* 117 */       System.out.println("9\b" + getClass().getName() + ".createGUI:\n\tCould not create GUI\n\t" + localException1.getMessage());
/* 118 */       localException1.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void init()
/*     */   {
/*     */     try {
/* 125 */       SwingUtilities.invokeLater(new Runnable()
/*     */       {
/*     */         public void run() {
/* 128 */           UtilMainApp.this.createGUI();
/*     */         }
/*     */       });
/*     */     }
/*     */     catch (Exception localException) {
/* 133 */       System.out.println("9\b" + getClass().getName() + ".init:\n\tCould not create GUI\n\t" + localException.getMessage());
/* 134 */       localException.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void stop() {
/*     */     try {
/* 140 */       this.properties.store(new FileOutputStream(this.propertiesFile), this.propertiesFile.getAbsolutePath());
/*     */     } catch (Exception localException) {
/* 142 */       System.out.println("9\b" + getClass().getName() + ".stop:\n\tCould not save properties\n\t" + localException.getMessage());
/* 143 */       localException.printStackTrace();
/*     */     }
/* 145 */     super.stop();
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.util.UtilMainApp
 * JD-Core Version:    0.6.2
 */