/*     */ package leadtools.sane;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import leadtools.InvalidOperationException;
/*     */ import leadtools.L_ERROR;
/*     */ import leadtools.RasterException;
/*     */ 
/*     */ class NetworkStream
/*     */ {
/*  63 */   private static Charset l = Charset.forName("ISO-8859-1");
/*     */   private static final String a = "ISO-8859-1";
/*     */   private InputStream L;
/*     */   private static final int D = 4;
/*     */   private OutputStream I;
/*     */ 
/*     */   public void writeInt(int n)
/*     */   {
/*     */     try
/*     */     {
/* 138 */       ByteArrayOutputStream a = new ByteArrayOutputStream(4);
/*     */ 
/* 176 */       new DataOutputStream(a)
/* 181 */         .writeInt(n);
/*     */ 
/*  16 */       writeBytes(a.toByteArray());
/*     */ 
/*  52 */       return;
/*     */     }
/*     */     catch (Exception a)
/*     */     {
/* 152 */       j();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeOperationCode(SaneRpcCode code) {
/* 157 */     writeInt(code.getValue());
/*     */   }
/*     */ 
/*     */   public int readHandle()
/*     */   {
/*  78 */     return readInt();
/*     */   }
/*     */ 
/*     */   int covertToInt(byte[] b)
/*     */   {
/*     */     try
/*     */     {
/*   9 */       return new DataInputStream(new ByteArrayInputStream(b)).readInt();
/*     */     }
/*     */     catch (Exception a)
/*     */     {
/* 160 */       l();
/*     */     }
/*  55 */     return 
/* 159 */       0;
/*     */   }
/*     */ 
/*     */   public void writeByte(byte b)
/*     */   {
/* 189 */     writeBytes(new byte[] { b });
/*     */   }
/*     */ 
/*     */   public int readInt()
/*     */   {
/*     */     byte[] a = new byte[4];
/*  69 */     if (
/* 156 */       readBytes(a) != 4) {
/*   4 */       l();
/*     */     }
/*     */ 
/*  29 */     return covertToInt(a);
/*     */   }
/*     */ 
/*     */   String covertToString(byte[] b)
/*     */   {
/*  40 */     return j(b).toString();
/*     */   }
/*     */ 
/*     */   public int readStatus(boolean throwException)
/*     */   {
/* 124 */     SaneStatus a = SaneStatus.forValue(
/* 145 */       readInt());
/*     */ 
/* 158 */     L_ERROR a = L_ERROR.SUCCESS;
/*     */ 
/* 185 */     switch (1.$SwitchMap$leadtools$sane$SaneStatus[a.ordinal()])
/*     */     {
/*     */     case 1:
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/*     */     case 5:
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/*     */     case 9:
/*     */     case 10:
/*     */     case 11:
/*     */       while (true)
/*     */       {
/* 203 */         a = L_ERROR.SUCCESS; tmpTernaryOp = throwException;
/*     */ 
/*  72 */         break;
/*     */ 
/*  57 */         a = L_ERROR.ERROR_SANE_CANCELLED; tmpTernaryOp = throwException;
/*     */ 
/* 174 */         break;
/*     */ 
/*  97 */         a = L_ERROR.ERROR_SANE_UNSUPPORTED; tmpTernaryOp = throwException;
/*     */ 
/* 200 */         break;
/*     */ 
/* 188 */         a = L_ERROR.ERROR_SANE_DEVICE_BUSY; tmpTernaryOp = throwException;
/*     */ 
/*  10 */         break;
/*     */ 
/* 190 */         a = L_ERROR.ERROR_SANE_INVAL; tmpTernaryOp = throwException;
/*     */ 
/*  24 */         break;
/*     */ 
/* 170 */         a = L_ERROR.ERROR_SANE_JAMMED; tmpTernaryOp = throwException;
/*     */ 
/*  81 */         break;
/*     */ 
/*  87 */         a = L_ERROR.ERROR_SANE_NO_DOCS; tmpTernaryOp = throwException;
/*     */ 
/* 111 */         break;
/*     */ 
/*  18 */         a = L_ERROR.ERROR_SANE_COVER_OPEN; tmpTernaryOp = throwException;
/*     */ 
/* 121 */         break;
/*     */ 
/* 103 */         a = L_ERROR.ERROR_SANE_IO_ERROR; tmpTernaryOp = throwException;
/*     */ 
/*  80 */         break;
/*     */ 
/* 109 */         a = L_ERROR.ERROR_NO_MEMORY;
/*     */       }
/*     */ 
/*     */     case 12:
/* 122 */       a = L_ERROR.ERROR_SANE_ACCESS_DENIED; tmpTernaryOp = throwException;
/*     */ 
/* 128 */       break;
/*     */     default:
/* 173 */       a = L_ERROR.FAILURE;
/*     */     }
/*  26 */     if (
/* 203 */       (0 == 0 ? throwException : throwException) != 0)
/*     */     {
/*  65 */       RasterException.checkErrorCode(a.getValue());
/*     */     }
/*     */ 
/* 151 */     return a.getValue();
/*     */   }
/*     */ 
/*     */   private static CharBuffer j(byte[] b)
/*     */   {
/* 368 */     return l.decode(ByteBuffer.wrap(b));
/*     */   }
/*     */ 
/*     */   private static byte[] j(char[] charArray)
/*     */   {
/* 288 */     return l.encode(CharBuffer.wrap(charArray)).array();
/*     */   }
/*     */ 
/*     */   public int readUnusedPointer()
/*     */   {
/* 262 */     return readHandle();
/*     */   }
/*     */ 
/*     */   private void l()
/*     */   {
/* 223 */     throw new InvalidOperationException("Error Reading data");
/*     */   }
/*     */ 
/*     */   public void writeHandle(int handle)
/*     */   {
/* 146 */     writeInt(handle);
/*     */   }
/*     */ 
/*     */   public int readBytes(byte[] b)
/*     */   {
/*     */     try
/*     */     {
/*     */       byte[] tmp6_5 = b; return this.L.read(tmp6_5, 0, tmp6_5.length);
/*     */     }
/*     */     catch (Exception a)
/*     */     {
/*  35 */       l();
/*     */     }
/*     */ 
/* 132 */     return 0;
/*     */   }
/*     */ 
/*     */   private void j()
/*     */   {
/* 376 */     throw new InvalidOperationException("Error writing data");
/*     */   }
/*     */ 
/*     */   protected void finalize()
/*     */   {
/*  89 */     dispose();
/*     */   }
/*     */ 
/*     */   public synchronized void dispose()
/*     */   {
/*     */     try
/*     */     {
/* 183 */       this.I.close(); this.L
/* 186 */         .close();
/*     */     }
/*     */     catch (Exception a)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public String readString()
/*     */   {
/*     */     int a;
/* 143 */     if (
/* 168 */       (a = readInt()) == 0)
/*     */     {
/* 201 */       return "";
/*     */     }
/*  27 */     byte[] a = new byte[a - 1];
/*     */ 
/*  90 */     readByte();
/*     */ 
/* 164 */     return j(
/* 165 */       readBytes(a)).toString();
/*     */   }
/*     */ 
/*     */   public void writeBytes(byte[] b)
/*     */   {
/*     */     try
/*     */     {
/*  25 */       this.I.write(b);
/*     */     }
/*     */     catch (Exception a)
/*     */     {
/* 115 */       j();
/*     */     }
/*     */   }
/*     */ 
/*     */   public byte readByte()
/*     */   {
/*     */     try
/*     */     {
/* 131 */       byte[] a = new byte[1];
/*     */       byte[] tmp10_9 = a; this.L.read(tmp10_9, 0, tmp10_9.length);
/*     */ 
/* 123 */       return a[0];
/*     */     }
/*     */     catch (Exception a)
/*     */     {
/*  45 */       l();
/*     */     }
/*     */ 
/* 129 */     return 
/* 162 */       0;
/*     */   }
/*     */ 
/*     */   public void writeString(String str)
/*     */   {
/*     */     char[] a;
/*  91 */     if (
/* 169 */       (a = str.toCharArray()).length > 0)
/*     */     {
/*  68 */       byte[] a = j(a);
/*     */ 
/*  31 */       writeInt(a.length + 1);
/*     */ 
/*   1 */       writeBytes(a);
/*     */     }
/*     */ 
/* 130 */     writeInt(0);
/*     */   }
/*     */ 
/*     */   public NetworkStream(OutputStream arg1, InputStream inputStream)
/*     */   {
/* 116 */     this.I = outputStream; this.L = inputStream;
/*     */   }
/*     */ 
/*     */   public int readDummy()
/*     */   {
/* 351 */     return readInt();
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.jar
 * Qualified Name:     leadtools.sane.NetworkStream
 * JD-Core Version:    0.6.2
 */