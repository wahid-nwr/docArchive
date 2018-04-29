/*    */ package ij.plugin;
/*    */ 
/*    */ import ij.IJ;
/*    */ import ij.Prefs;
/*    */ import ij.gui.GenericDialog;
/*    */ import ij.util.Tools;
/*    */ import java.util.Properties;
/*    */ 
/*    */ public class ProxySettings
/*    */   implements PlugIn
/*    */ {
/* 15 */   private Properties props = System.getProperties();
/* 16 */   private String proxyhost = Prefs.get("proxy.server", "");
/* 17 */   private int proxyport = (int)Prefs.get("proxy.port", 8080.0D);
/*    */ 
/*    */   public void run(String arg) {
/* 20 */     if (IJ.getApplet() != null) return;
/* 21 */     String host = System.getProperty("http.proxyHost");
/* 22 */     if (host != null) this.proxyhost = host;
/* 23 */     String port = System.getProperty("http.proxyPort");
/* 24 */     if (port != null) {
/* 25 */       double portNumber = Tools.parseDouble(port);
/* 26 */       if (!Double.isNaN(portNumber))
/* 27 */         this.proxyport = ((int)portNumber);
/*    */     }
/* 29 */     if (!showDialog()) return;
/* 30 */     if (!this.proxyhost.equals(""))
/* 31 */       this.props.put("proxySet", "true");
/*    */     else
/* 33 */       this.props.put("proxySet", "false");
/* 34 */     this.props.put("http.proxyHost", this.proxyhost);
/* 35 */     this.props.put("http.proxyPort", "" + this.proxyport);
/* 36 */     Prefs.set("proxy.server", this.proxyhost);
/* 37 */     Prefs.set("proxy.port", this.proxyport);
/*    */     try {
/* 39 */       System.setProperty("java.net.useSystemProxies", Prefs.useSystemProxies ? "true" : "false"); } catch (Exception e) {
/*    */     }
/* 41 */     if (IJ.debugMode)
/* 42 */       logProperties();
/*    */   }
/*    */ 
/*    */   public void logProperties() {
/* 46 */     IJ.log("proxy set: " + System.getProperty("proxySet"));
/* 47 */     IJ.log("proxy host: " + System.getProperty("http.proxyHost"));
/* 48 */     IJ.log("proxy port: " + System.getProperty("http.proxyPort"));
/* 49 */     IJ.log("java.net.useSystemProxies: " + System.getProperty("java.net.useSystemProxies"));
/*    */   }
/*    */ 
/*    */   boolean showDialog() {
/* 53 */     GenericDialog gd = new GenericDialog("Proxy Settings");
/* 54 */     gd.addStringField("Proxy server:", this.proxyhost, 15);
/* 55 */     gd.addNumericField("Port:", this.proxyport, 0);
/* 56 */     gd.addCheckbox("Or, use system proxy settings", Prefs.useSystemProxies);
/* 57 */     gd.addHelp("http://imagej.nih.gov/ij/docs/menus/edit.html#proxy");
/* 58 */     gd.showDialog();
/* 59 */     if (gd.wasCanceled())
/* 60 */       return false;
/* 61 */     this.proxyhost = gd.getNextString();
/* 62 */     this.proxyport = ((int)gd.getNextNumber());
/* 63 */     Prefs.useSystemProxies = gd.getNextBoolean();
/* 64 */     return true;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.ProxySettings
 * JD-Core Version:    0.6.2
 */