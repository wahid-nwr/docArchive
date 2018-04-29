/*     */ package leadtools.sane.host;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Frame;
/*     */ import java.awt.Panel;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.awt.event.WindowListener;
/*     */ import java.io.PrintStream;
/*     */ import leadtools.LTLibrary;
/*     */ import leadtools.Platform;
/*     */ import leadtools.demos.DemoUtilities;
/*     */ import leadtools.sane.server.ClientConnectionEvent;
/*     */ import leadtools.sane.server.ClientListener;
/*     */ import leadtools.sane.server.ImageProcessingEvent;
/*     */ import leadtools.sane.server.ImageProcessingListener;
/*     */ import leadtools.sane.server.PageImageProcessingEvent;
/*     */ import leadtools.sane.server.SaneScanningService;
/*     */ import leadtools.sane.server.SaneServer;
/*     */ 
/*     */ public class HostApplication extends Frame
/*     */   implements WindowListener, ClientListener, ImageProcessingListener
/*     */ {
/*     */   private static final long serialVersionUID = -1410998960220601099L;
/*     */   private static final int saneServerPortNumber = 50000;
/*  25 */   private DemoData demoData = null;
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*  22 */     new HostApplication().run();
/*     */   }
/*     */ 
/*     */   private void run()
/*     */   {
/*     */     try
/*     */     {
/*  30 */       String platform = "/usr/share/leadtools/web scanning application/lib/";
/*  31 */       if (Platform.is64Bit())
/*  32 */         platform = platform + "x64/";
/*     */       else {
/*  34 */         platform = platform + "x86/";
/*     */       }
/*     */ 
/*  37 */       Platform.setLibPath(platform);
/*     */ 
/*  39 */       Platform.loadLibrary(LTLibrary.LEADTOOLS);
/*  40 */       Platform.loadLibrary(LTLibrary.CODECS);
/*  41 */       Platform.loadLibrary(LTLibrary.IMAGE_PROCESSING_CORE);
/*  42 */       Platform.loadLibrary(LTLibrary.SANE);
/*     */ 
/*  44 */       if (!DemoUtilities.setLicense()) {
/*  45 */         System.err.println("Please set your runtime license");
/*  46 */         return;
/*     */       }
/*     */ 
/*  49 */       this.demoData = new DemoData();
/*     */ 
/*  52 */       this.demoData.saneService = new SaneScanningService();
/*  53 */       this.demoData.saneService.addImageProcessingListener(this);
/*  54 */       this.demoData.saneService.addClientListener(this);
/*  55 */       this.demoData.saneServer = new SaneServer(50000, this.demoData.saneService);
/*  56 */       this.demoData.saneServer.start();
/*  57 */       return;
/*     */     } catch (Exception e) {
/*  59 */       System.err.println(e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public HostApplication()
/*     */   {
/*  66 */     addWindowListener(this);
/*  67 */     createGUI();
/*     */   }
/*     */ 
/*     */   private void createGUI()
/*     */   {
/*  72 */     pack();
/*  73 */     setVisible(true);
/*  74 */     add(new Panel(new BorderLayout()));
/*  75 */     setTitle("Leadtools Web Scanning");
/*     */   }
/*     */ 
/*     */   public void begin(ImageProcessingEvent event)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void process(PageImageProcessingEvent event)
/*     */   {
/*     */     try {
/*  85 */       ImageProcessing.run(event);
/*     */     } catch (Exception ex) {
/*  87 */       ex.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void end(ImageProcessingEvent event)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void clientStopped(ClientConnectionEvent event)
/*     */   {
/*  98 */     this.demoData.clientsCount -= 1;
/*  99 */     if (this.demoData.clientsCount == 0) {
/* 100 */       this.demoData.saneServer.stop();
/* 101 */       System.exit(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void clientStarted(ClientConnectionEvent event)
/*     */   {
/* 107 */     this.demoData.clientsCount += 1;
/*     */   }
/*     */ 
/*     */   public void windowActivated(WindowEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void windowClosed(WindowEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void windowClosing(WindowEvent e)
/*     */   {
/* 121 */     this.demoData.saneServer.stop();
/* 122 */     System.exit(0);
/*     */   }
/*     */ 
/*     */   public void windowDeactivated(WindowEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void windowDeiconified(WindowEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void windowIconified(WindowEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void windowOpened(WindowEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   class DemoData
/*     */   {
/*     */     protected SaneScanningService saneService;
/*     */     protected int clientsCount;
/*     */     protected SaneServer saneServer;
/*     */ 
/*     */     DemoData()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.host.jar
 * Qualified Name:     leadtools.sane.host.HostApplication
 * JD-Core Version:    0.6.2
 */