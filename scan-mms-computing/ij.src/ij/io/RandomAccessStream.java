/*     */ package ij.io;
/*     */ 
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public final class RandomAccessStream extends InputStream
/*     */ {
/*     */   private static final int BLOCK_SIZE = 512;
/*     */   private static final int BLOCK_MASK = 511;
/*     */   private static final int BLOCK_SHIFT = 9;
/*     */   private InputStream src;
/*     */   private RandomAccessFile ras;
/*     */   private long pointer;
/*     */   private Vector data;
/*     */   private int length;
/*     */   private boolean foundEOS;
/*     */ 
/*     */   public RandomAccessStream(InputStream inputstream)
/*     */   {
/*  28 */     this.pointer = 0L;
/*  29 */     this.data = new Vector();
/*  30 */     this.length = 0;
/*  31 */     this.foundEOS = false;
/*  32 */     this.src = inputstream;
/*     */   }
/*     */ 
/*     */   public RandomAccessStream(RandomAccessFile ras)
/*     */   {
/*  37 */     this.ras = ras;
/*     */   }
/*     */ 
/*     */   public int getFilePointer() throws IOException {
/*  41 */     if (this.ras != null) {
/*  42 */       return (int)this.ras.getFilePointer();
/*     */     }
/*  44 */     return (int)this.pointer;
/*     */   }
/*     */ 
/*     */   public long getLongFilePointer() throws IOException {
/*  48 */     if (this.ras != null) {
/*  49 */       return this.ras.getFilePointer();
/*     */     }
/*  51 */     return this.pointer;
/*     */   }
/*     */ 
/*     */   public int read() throws IOException {
/*  55 */     if (this.ras != null)
/*  56 */       return this.ras.read();
/*  57 */     long l = this.pointer + 1L;
/*  58 */     long l1 = readUntil(l);
/*  59 */     if (l1 >= l) {
/*  60 */       byte[] abyte0 = (byte[])this.data.elementAt((int)(this.pointer >> 9));
/*  61 */       return abyte0[((int)(this.pointer++ & 0x1FF))] & 0xFF;
/*     */     }
/*  63 */     return -1;
/*     */   }
/*     */ 
/*     */   public int read(byte[] bytes, int off, int len) throws IOException {
/*  67 */     if (bytes == null)
/*  68 */       throw new NullPointerException();
/*  69 */     if (this.ras != null)
/*  70 */       return this.ras.read(bytes, off, len);
/*  71 */     if ((off < 0) || (len < 0) || (off + len > bytes.length))
/*  72 */       throw new IndexOutOfBoundsException();
/*  73 */     if (len == 0)
/*  74 */       return 0;
/*  75 */     long l = readUntil(this.pointer + len);
/*  76 */     if (l <= this.pointer) {
/*  77 */       return -1;
/*     */     }
/*  79 */     byte[] abyte1 = (byte[])this.data.elementAt((int)(this.pointer >> 9));
/*  80 */     int k = Math.min(len, 512 - (int)(this.pointer & 0x1FF));
/*  81 */     System.arraycopy(abyte1, (int)(this.pointer & 0x1FF), bytes, off, k);
/*  82 */     this.pointer += k;
/*  83 */     return k;
/*     */   }
/*     */ 
/*     */   public final void readFully(byte[] bytes) throws IOException
/*     */   {
/*  88 */     readFully(bytes, bytes.length);
/*     */   }
/*     */ 
/*     */   public final void readFully(byte[] bytes, int len) throws IOException {
/*  92 */     int read = 0;
/*     */     do {
/*  94 */       int l = read(bytes, read, len - read);
/*  95 */       if (l < 0) break;
/*  96 */       read += l;
/*  97 */     }while (read < len);
/*     */   }
/*     */ 
/*     */   private long readUntil(long l) throws IOException {
/* 101 */     if (l < this.length)
/* 102 */       return l;
/* 103 */     if (this.foundEOS)
/* 104 */       return this.length;
/* 105 */     int i = (int)(l >> 9);
/* 106 */     int j = this.length >> 9;
/* 107 */     for (int k = j; k <= i; k++) {
/* 108 */       byte[] abyte0 = new byte[512];
/* 109 */       this.data.addElement(abyte0);
/* 110 */       int i1 = 512;
/* 111 */       int j1 = 0;
/* 112 */       while (i1 > 0) {
/* 113 */         int k1 = this.src.read(abyte0, j1, i1);
/* 114 */         if (k1 == -1) {
/* 115 */           this.foundEOS = true;
/* 116 */           return this.length;
/*     */         }
/* 118 */         j1 += k1;
/* 119 */         i1 -= k1;
/* 120 */         this.length += k1;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 125 */     return this.length;
/*     */   }
/*     */ 
/*     */   public void seek(long loc) throws IOException
/*     */   {
/* 130 */     if (this.ras != null) {
/* 131 */       this.ras.seek(loc); return;
/* 132 */     }if (loc < 0L)
/* 133 */       this.pointer = 0L;
/*     */     else
/* 135 */       this.pointer = loc;
/*     */   }
/*     */ 
/*     */   public void seek(int loc) throws IOException {
/* 139 */     long lloc = loc & 0xFFFFFFFF;
/*     */ 
/* 141 */     if (this.ras != null) {
/* 142 */       this.ras.seek(lloc);
/* 143 */       return;
/*     */     }
/* 145 */     if (lloc < 0L)
/* 146 */       this.pointer = 0L;
/*     */     else
/* 148 */       this.pointer = lloc;
/*     */   }
/*     */ 
/*     */   public final int readInt() throws IOException {
/* 152 */     int i = read();
/* 153 */     int j = read();
/* 154 */     int k = read();
/* 155 */     int l = read();
/* 156 */     if ((i | j | k | l) < 0) {
/* 157 */       throw new EOFException();
/*     */     }
/* 159 */     return (i << 24) + (j << 16) + (k << 8) + l;
/*     */   }
/*     */ 
/*     */   public final long readLong() throws IOException {
/* 163 */     return (readInt() << 32) + (readInt() & 0xFFFFFFFF);
/*     */   }
/*     */ 
/*     */   public final double readDouble() throws IOException {
/* 167 */     return Double.longBitsToDouble(readLong());
/*     */   }
/*     */ 
/*     */   public final short readShort() throws IOException {
/* 171 */     int i = read();
/* 172 */     int j = read();
/* 173 */     if ((i | j) < 0) {
/* 174 */       throw new EOFException();
/*     */     }
/* 176 */     return (short)((i << 8) + j);
/*     */   }
/*     */ 
/*     */   public final float readFloat() throws IOException {
/* 180 */     return Float.intBitsToFloat(readInt());
/*     */   }
/*     */ 
/*     */   public void close() throws IOException
/*     */   {
/* 185 */     if (this.ras != null) {
/* 186 */       this.ras.close();
/*     */     } else {
/* 188 */       this.data.removeAllElements();
/* 189 */       this.src.close();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.io.RandomAccessStream
 * JD-Core Version:    0.6.2
 */