/*     */ package leadtools;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public final class LeadStream
/*     */   implements ILeadStream
/*     */ {
/*  31 */   private boolean _isDisposed = false;
/*     */   private int _openCount;
/*     */   private boolean _disposeStream;
/*     */   private InputStream _inputStream;
/*  82 */   private ArrayList<Long> _originalPositions = new ArrayList();
/*     */   private long _originalPosition;
/*     */   private long _currentPosition;
/*     */   private RandomAccessFile _randomAccessFile;
/*     */   private OutputStream _outputStream;
/*     */   private boolean _isStarted;
/*     */ 
/*     */   public LeadStream(InputStream stream, boolean freeStream)
/*     */   {
/*  11 */     if (stream == null) {
/*  12 */       throw new ArgumentNullException("stream");
/*     */     }
/*  14 */     initialize(stream, null, null, freeStream);
/*     */   }
/*     */ 
/*     */   public LeadStream(OutputStream stream, boolean freeStream) {
/*  18 */     if (stream == null) {
/*  19 */       throw new ArgumentNullException("stream");
/*     */     }
/*  21 */     initialize(null, stream, null, freeStream);
/*     */   }
/*     */ 
/*     */   public LeadStream(RandomAccessFile file, boolean freeStream) {
/*  25 */     if (file == null) {
/*  26 */       throw new ArgumentNullException("file");
/*     */     }
/*  28 */     initialize(null, null, file, freeStream);
/*     */   }
/*     */ 
/*     */   protected void finalize()
/*     */   {
/*     */     try {
/*  34 */       dispose();
/*     */     } catch (Throwable e) {
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void dispose() {
/*  40 */     if (this._isDisposed)
/*  41 */       return;
/*  42 */     this._isDisposed = true;
/*     */ 
/*  44 */     if (this._isStarted == true) {
/*  45 */       stop(true);
/*     */     }
/*  47 */     if (this._disposeStream == true)
/*     */       try {
/*  49 */         if (this._inputStream != null)
/*  50 */           this._inputStream.close();
/*  51 */         else if (this._outputStream != null)
/*  52 */           this._outputStream.close();
/*  53 */         else if (this._randomAccessFile != null)
/*  54 */           this._randomAccessFile.close();
/*     */       }
/*     */       catch (Exception ex)
/*     */       {
/*     */       }
/*  59 */     this._inputStream = null;
/*  60 */     this._outputStream = null;
/*  61 */     this._randomAccessFile = null;
/*     */   }
/*     */ 
/*     */   private void initialize(InputStream inputStream, OutputStream outputStream, RandomAccessFile file, boolean disposeStream) {
/*  65 */     this._inputStream = inputStream;
/*  66 */     this._outputStream = outputStream;
/*  67 */     this._randomAccessFile = file;
/*     */ 
/*  69 */     this._disposeStream = disposeStream;
/*  70 */     this._isStarted = false;
/*  71 */     this._openCount = 0;
/*     */   }
/*     */ 
/*     */   public InputStream getInputStream()
/*     */   {
/*  79 */     return this._inputStream;
/*     */   }
/*     */ 
/*     */   public RandomAccessFile getRandomAccessFile()
/*     */   {
/*  87 */     return this._randomAccessFile;
/*     */   }
/*     */ 
/*     */   public OutputStream getOutputStream()
/*     */   {
/*  93 */     return this._outputStream;
/*     */   }
/*     */ 
/*     */   public String getFileName()
/*     */   {
/*  99 */     return "Stream";
/*     */   }
/*     */ 
/*     */   public boolean canSeek() {
/* 103 */     return this._randomAccessFile != null;
/*     */   }
/*     */ 
/*     */   public boolean canRead() {
/* 107 */     return (this._inputStream != null) || (this._randomAccessFile != null);
/*     */   }
/*     */ 
/*     */   public boolean canWrite() {
/* 111 */     return (this._outputStream != null) || (this._randomAccessFile != null);
/*     */   }
/*     */ 
/*     */   public boolean isStarted() {
/* 115 */     return this._isStarted;
/*     */   }
/*     */ 
/*     */   public boolean start()
/*     */   {
/* 120 */     if (this._randomAccessFile != null)
/*     */       try {
/* 122 */         this._originalPosition = this._randomAccessFile.getFilePointer();
/*     */       } catch (IOException e) {
/* 124 */         this._originalPosition = 0L;
/*     */       }
/*     */     else {
/* 127 */       this._originalPosition = 0L;
/*     */     }
/* 129 */     this._currentPosition = this._originalPosition;
/*     */ 
/* 131 */     this._isStarted = true;
/* 132 */     return true;
/*     */   }
/*     */ 
/*     */   public void stop(boolean resetPosition) {
/* 136 */     if (!this._isStarted) {
/* 137 */       return;
/*     */     }
/* 139 */     if (this._randomAccessFile != null)
/*     */     {
/* 141 */       if (resetPosition) {
/*     */         try
/*     */         {
/* 144 */           this._randomAccessFile.seek(this._originalPosition);
/*     */         } catch (IOException e) {
/* 146 */           throw new RuntimeException(e);
/*     */         }
/*     */       }
/*     */ 
/* 150 */       this._currentPosition = this._originalPosition;
/*     */     }
/*     */ 
/* 153 */     this._isStarted = false;
/*     */   }
/*     */ 
/*     */   public boolean openFile(String fileName, LeadStreamMode mode, LeadStreamAccess access, LeadStreamShare share) {
/* 157 */     boolean ret = false;
/*     */ 
/* 159 */     this._openCount += 1;
/* 160 */     this._originalPositions.add(Long.valueOf(this._currentPosition));
/* 161 */     this._originalPosition = 0L;
/*     */ 
/* 163 */     if (this._openCount > 1) {
/* 164 */       if (this._randomAccessFile == null) {
/* 165 */         return false;
/*     */       }
/*     */ 
/* 169 */       seek(LeadSeekOrigin.BEGIN, 0L);
/* 170 */       return true;
/*     */     }
/*     */ 
/* 173 */     if (this._randomAccessFile != null) {
/*     */       try
/*     */       {
/* 176 */         this._randomAccessFile.seek(this._originalPosition);
/* 177 */         this._currentPosition = this._originalPosition;
/* 178 */         ret = true;
/*     */       } catch (Exception ex) {
/* 180 */         ret = false;
/*     */       }
/*     */     }
/* 183 */     else if (this._inputStream != null)
/*     */     {
/* 185 */       ret = (mode == LeadStreamMode.OPEN) && (access == LeadStreamAccess.READ);
/* 186 */     } else if (this._outputStream != null)
/*     */     {
/* 188 */       ret = ((mode == LeadStreamMode.CREATE) || (mode == LeadStreamMode.TRUNCATE)) && (access == LeadStreamAccess.WRITE);
/*     */     }
/*     */ 
/* 191 */     return ret;
/*     */   }
/*     */ 
/*     */   public int read(byte[] buffer, int count) {
/* 195 */     if ((this._inputStream == null) && (this._randomAccessFile == null)) {
/* 196 */       return 0;
/*     */     }
/* 198 */     if (count == 0) {
/* 199 */       return 0;
/*     */     }
/* 201 */     int bytes = 0;
/*     */     try {
/* 203 */       if (this._inputStream != null)
/* 204 */         bytes = this._inputStream.read(buffer, 0, count);
/*     */       else {
/* 206 */         bytes = this._randomAccessFile.read(buffer, 0, count);
/*     */       }
/* 208 */       this._currentPosition += bytes;
/*     */     }
/*     */     catch (Exception e) {
/* 211 */       e.printStackTrace();
/*     */     }
/*     */ 
/* 214 */     return bytes;
/*     */   }
/*     */ 
/*     */   public int write(byte[] buffer, int count) {
/* 218 */     if ((this._outputStream == null) && (this._randomAccessFile == null)) {
/* 219 */       return 0;
/*     */     }
/* 221 */     if (count == 0)
/* 222 */       return 0;
/*     */     try
/*     */     {
/* 225 */       if (this._outputStream != null)
/* 226 */         this._outputStream.write(buffer, 0, count);
/*     */       else {
/* 228 */         this._randomAccessFile.write(buffer, 0, count);
/*     */       }
/* 230 */       this._currentPosition += count;
/*     */ 
/* 232 */       return count;
/*     */     } catch (Exception e) {
/* 234 */       e.printStackTrace();
/* 235 */     }return L_ERROR.FAILURE.getValue();
/*     */   }
/*     */ 
/*     */   public long seek(LeadSeekOrigin origin, long offset)
/*     */   {
/* 241 */     if (this._randomAccessFile == null)
/* 242 */       return L_ERROR.ERROR_FILE_LSEEK.getValue();
/*     */     try
/*     */     {
/* 245 */       long size = this._randomAccessFile.length();
/*     */       long position;
/* 247 */       switch (1.$SwitchMap$leadtools$LeadSeekOrigin[origin.ordinal()]) {
/*     */       case 1:
/* 249 */         position = this._currentPosition + offset;
/* 250 */         break;
/*     */       case 2:
/* 253 */         position = this._originalPosition + size + offset;
/* 254 */         break;
/*     */       case 3:
/*     */       default:
/* 258 */         position = this._originalPosition + offset;
/*     */       }
/*     */ 
/* 262 */       if (position < this._originalPosition) {
/* 263 */         position = this._originalPosition;
/*     */       }
/*     */ 
/* 267 */       this._randomAccessFile.seek(position);
/* 268 */       if (position != this._randomAccessFile.getFilePointer()) {
/* 269 */         return -1L;
/*     */       }
/*     */ 
/* 272 */       this._currentPosition = position;
/*     */ 
/* 274 */       return this._currentPosition - this._originalPosition; } catch (Exception ex) {
/*     */     }
/* 276 */     return L_ERROR.ERROR_FILE_LSEEK.getValue();
/*     */   }
/*     */ 
/*     */   public void closeFile()
/*     */   {
/* 282 */     this._openCount -= 1;
/*     */ 
/* 284 */     int index = this._originalPositions.size() - 1;
/* 285 */     long temp = ((Long)this._originalPositions.get(index)).longValue();
/* 286 */     this._originalPositions.remove(index);
/* 287 */     this._originalPosition = 0L;
/*     */ 
/* 289 */     if (this._randomAccessFile != null)
/*     */     {
/* 291 */       seek(LeadSeekOrigin.BEGIN, temp);
/*     */ 
/* 293 */       if ((this._openCount <= 0) && (this._disposeStream)) {
/*     */         try {
/* 295 */           this._randomAccessFile.close();
/*     */         } catch (IOException e) {
/* 297 */           e.printStackTrace();
/*     */         }
/* 299 */         this._randomAccessFile = null;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 304 */     if (this._disposeStream)
/*     */       try {
/* 306 */         if (this._inputStream != null) {
/* 307 */           this._inputStream.close();
/*     */         }
/* 309 */         if (this._outputStream != null)
/* 310 */           this._outputStream.close();
/*     */       }
/*     */       catch (IOException e) {
/* 313 */         e.printStackTrace();
/*     */       }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.LeadStream
 * JD-Core Version:    0.6.2
 */