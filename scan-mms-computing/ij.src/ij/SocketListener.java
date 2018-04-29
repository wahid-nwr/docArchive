/*    */ package ij;
/*    */ 
/*    */ import ij.io.OpenDialog;
/*    */ import ij.io.Opener;
/*    */ import java.io.BufferedReader;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStreamReader;
/*    */ import java.io.PrintStream;
/*    */ import java.net.InetAddress;
/*    */ import java.net.ServerSocket;
/*    */ import java.net.Socket;
/*    */ 
/*    */ public class SocketListener
/*    */   implements Runnable
/*    */ {
/*    */   public SocketListener()
/*    */   {
/* 20 */     Thread thread = new Thread(this, "SocketListener");
/* 21 */     thread.start();
/*    */   }
/*    */ 
/*    */   public void run() {
/* 25 */     ServerSocket serverSocket = null;
/*    */     try
/*    */     {
/* 29 */       serverSocket = new ServerSocket(ImageJ.getPort());
/* 30 */       if (IJ.debugMode) IJ.log("SocketListener: new ServerSocket(" + ImageJ.getPort() + ")"); while (true)
/*    */       {
/* 32 */         Socket clientSocket = serverSocket.accept();
/* 33 */         InetAddress address = clientSocket.getInetAddress();
/* 34 */         boolean isLocal = (address != null) && (address.isLoopbackAddress());
/* 35 */         if (IJ.debugMode) IJ.log("SocketListener: client=" + address + "  " + isLocal); try
/*    */         {
/* 37 */           BufferedReader is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
/* 38 */           String cmd = is.readLine();
/* 39 */           if (IJ.debugMode) IJ.log("SocketListener: command=\"" + cmd + "\"");
/* 40 */           if (cmd.startsWith("open ")) {
/* 41 */             new Opener().openAndAddToRecent(cmd.substring(5));
/* 42 */           } else if ((isLocal) && (cmd.startsWith("macro "))) {
/* 43 */             String name = cmd.substring(6);
/* 44 */             String name2 = name;
/* 45 */             String arg = null;
/* 46 */             if (name2.endsWith(")")) {
/* 47 */               int index = name2.lastIndexOf("(");
/* 48 */               if (index > 0) {
/* 49 */                 name = name2.substring(0, index);
/* 50 */                 arg = name2.substring(index + 1, name2.length() - 1);
/*    */               }
/*    */             }
/* 53 */             IJ.runMacroFile(name, arg);
/* 54 */           } else if ((isLocal) && (cmd.startsWith("run "))) {
/* 55 */             IJ.run(cmd.substring(4));
/* 56 */           } else if ((isLocal) && (cmd.startsWith("eval "))) {
/* 57 */             String rtn = IJ.runMacro(cmd.substring(5));
/* 58 */             if (rtn != null)
/* 59 */               System.out.print(rtn);
/* 60 */           } else if (cmd.startsWith("user.dir ")) {
/* 61 */             OpenDialog.setDefaultDirectory(cmd.substring(9));
/*    */           } } catch (Throwable e) {  }
/*    */ 
/* 63 */         clientSocket.close();
/*    */       }
/*    */     }
/*    */     catch (IOException e)
/*    */     {
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.SocketListener
 * JD-Core Version:    0.6.2
 */