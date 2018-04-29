/*     */ package leadtools.sane;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.net.Socket;
/*     */ 
/*     */ class NetworkSaneSession extends SaneSession
/*     */ {
/*     */   private NetworkStream D;
/*     */   private Socket I;
/*     */ 
/*     */   Socket getSocket()
/*     */   {
/* 154 */     return this.I;
/*     */   }
/*     */ 
/*     */   public void start()
/*     */   {
/*  82 */     String a = System.getProperty("user.name");
/*     */     NetworkSaneSession tmp8_6 = this; tmp8_6.D.writeOperationCode(SaneRpcCode.INIT); this.D.writeInt(16777219);
/*     */ 
/* 183 */     tmp8_6.D.writeString(a);
/*     */ 
/*  94 */     this.D.readInt();
/*     */   }
/*     */ 
/*     */   public SaneDevice[] getDevices()
/*     */   {
/* 176 */     this.D.writeOperationCode(SaneRpcCode.GET_DEVICES);
/*     */ 
/*  52 */     int i = this.D.readInt() - 1;
/*     */ 
/* 176 */     this.D.readStatus(true);
/*     */     int a;
/* 163 */     if (a <= 0) {
/*  51 */       return new NetworkSaneDevice[0];
/*     */     }
/*     */ 
/* 105 */     NetworkSaneDevice[] a = new NetworkSaneDevice[a];
/*     */     int a;
/*     */     NetworkSaneSession tmp59_57 = this; String a = tmp59_57.D.readString();
/*     */ 
/* 127 */     String a = this.D
/* 127 */       .readString();
/*     */ 
/* 194 */     String a = tmp59_57.D
/* 194 */       .readString();
/*     */ 
/* 130 */     String a = this.D
/* 130 */       .readString();
/*     */ 
/* 171 */     a[new NetworkSaneDevice(this.I.getInetAddress(), this.D, a, a, a, a)] = (a++);
/*     */ 
/* 157 */     this.D.readUnusedPointer();
/*     */ 
/*  46 */     return a;
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */   {
/*  98 */     this.D.writeOperationCode(SaneRpcCode.EXIT);
/*     */   }
/*     */ 
/*     */   NetworkStream getStream()
/*     */   {
/* 155 */     return this.D;
/*     */   }
/*     */ 
/*     */   private void j(InetAddress arg1, int arg2)
/*     */   {
/*     */     int i;
/* 110 */     ???.I = SaneUtils.createSaneSocket(???, i);
/*     */     int port;
/*     */     InetAddress address;
/*  23 */     this.D = new NetworkStream(SaneUtils.getOutputStream(this.I), SaneUtils.getInputStream(this.I));
/*     */   }
/*     */ 
/*     */   public NetworkSaneSession(InetAddress ???, int arg2)
/*     */   {
/*  96 */     j(???, port);
/*     */   }
/*     */ 
/*     */   public SaneDevice getDevice(String deviceName)
/*     */   {
/*     */      tmp17_16 = null;
/*     */      tmp18_17 = tmp17_16; return new NetworkSaneDevice(this.I.getInetAddress(), this.D, deviceName, tmp18_17, tmp17_16, tmp18_17);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.jar
 * Qualified Name:     leadtools.sane.NetworkSaneSession
 * JD-Core Version:    0.6.2
 */