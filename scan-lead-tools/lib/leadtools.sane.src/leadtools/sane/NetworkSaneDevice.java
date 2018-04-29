/*     */ package leadtools.sane;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.Socket;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import leadtools.InvalidOperationException;
/*     */ import leadtools.L_ERROR;
/*     */ import leadtools.RasterException;
/*     */ import leadtools.RasterExceptionCode;
/*     */ import leadtools.RasterImage;
/*     */ import leadtools.RasterSupport;
/*     */ import leadtools.RasterSupportType;
/*     */ import leadtools.ltkrn;
/*     */ 
/*     */ class NetworkSaneDevice extends SaneDevice
/*     */ {
/*     */   private InetAddress C;
/*     */   private int h;
/*     */   private Socket k;
/*  14 */   private int l = this.a = 0;
/*     */   private int a;
/*     */   private DeviceInfo L;
/*     */   private NetworkStream D;
/*  14 */   private String I = "";
/*     */ 
/*     */   private ControlOptionDataResponse l()
/*     */   {
/*     */     NetworkSaneDevice tmp10_8 = this; int a = tmp10_8.D.readInt();
/*     */ 
/* 527 */     SaneValueType a = SaneValueType.forValue(
/* 746 */       this.D.readInt());
/*     */ 
/* 466 */     int a = 
/* 746 */       tmp10_8.D.readInt();
/*     */ 
/* 686 */     int a = 
/* 746 */       this.D.readInt();
/*     */ 
/* 465 */     Object localObject = null;
/*     */ 
/* 476 */     this.D.readStatus(true);
/*     */     byte[] a;
/* 579 */     if (a != 0)
/*     */     {
/* 437 */       a = new byte[a];
/*     */ 
/* 520 */       if (this.D.readBytes(a) != a)
/*     */       {
/* 640 */         throw new RasterException("Error reading value");
/*     */       }
/*     */     }
/*     */     String a;
/* 471 */     if (!
/* 753 */       (a = this.D.readString()).isEmpty())
/*     */     {
/* 530 */       j(a);
/*     */       NetworkSaneDevice tmp112_110 = this; a = tmp112_110.D.readInt();
/*     */ 
/* 428 */       a = SaneValueType.forValue(
/* 613 */         this.D.readInt());
/*     */ 
/* 502 */       a = 
/* 613 */         tmp112_110.D.readInt();
/*     */ 
/* 721 */       a = this.D
/* 721 */         .readInt();
/*     */ 
/* 615 */       a = null;
/*     */ 
/* 530 */       this.D.readStatus(true);
/*     */ 
/* 702 */       if (a != 0)
/*     */       {
/* 561 */         a = new byte[a];
/*     */ 
/* 461 */         if (this.D.readBytes(a) != a)
/*     */         {
/* 583 */           throw new RasterException("Error reading value");
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 597 */     ControlOptionDataResponse localControlOptionDataResponse1 = new ControlOptionDataResponse();
/*     */     ControlOptionDataResponse a;
/*     */     ControlOptionDataResponse tmp196_194 = a;
/*     */     ControlOptionDataResponse tmp197_196 = tmp196_194;
/*     */     ControlOptionDataResponse tmp202_200 = a; tmp202_200.info = a; tmp202_200.type = a; tmp197_196.valueSize = a; tmp196_194.value = a;
/*     */ 
/* 446 */     return 
/* 610 */       tmp197_196;
/*     */   }
/*     */ 
/*     */   public int read(byte[] data, int offset)
/*     */   {
/*     */     try
/*     */     {
/* 121 */       InputStream a = this.k.getInputStream();
/*     */       DataInputStream a;
/*  88 */       if ((a = new DataInputStream(a))
/* 103 */         .readInt() == -1)
/*     */       {
/* 109 */         a.readInt();
/*     */       }
/*     */ 
/* 122 */       int a = 0;
/*     */ 
/* 128 */       int a = offset;
/*     */ 
/*  12 */       int a = data.length - a;
/*     */ 
/*  43 */       if (
/* 140 */         (a = a.read(data, a, a)) != -1) {
/*  26 */         a += a;
/*     */       }
/*     */ 
/* 151 */       a = data.length - a;
/*     */ 
/* 148 */       return a;
/*     */     }
/*     */     catch (Exception a)
/*     */     {
/*     */     }
/*  17 */     throw new InvalidOperationException("Error Reading data");
/*     */   }
/*     */ 
/*     */   public SaneOptionDescriptor getOptionDescriptor(int optionIndex)
/*     */   {
/*     */     SaneOptionDescriptor[] a;
/* 240 */     if ((a = getOptionDescriptors()).length > 
/* 240 */       optionIndex)
/*     */     {
/* 378 */       return a[optionIndex];
/*     */     }
/* 317 */     return null;
/*     */   }
/*     */ 
/*     */   RasterImage saneBufferToBitmapHandle(byte[] data, SaneParameters parameters)
/*     */   {
/* 223 */     int a = L_ERROR.SUCCESS.getValue();
/*     */ 
/* 315 */     long a = 0L;
/*     */     try
/*     */     {
/* 329 */       int a = 1;
/*     */ 
/* 236 */       int a = 0;
/*     */ 
/* 308 */       switch (parameters.getFormat())
/*     */       {
/*     */       case GRAY:
/*     */         while (true)
/* 250 */           if (0 == 0) { a = 2;
/*     */ 
/* 255 */             a = 1;
/*     */ 
/* 385 */             break;
/*     */           }
/*     */       case ???:
/* 235 */         a = 0;
/*     */ 
/* 335 */         a = 3;
/*     */ 
/* 271 */         break;
/*     */       default:
/*     */         throw new RasterException(RasterExceptionCode.FEATURE_NOT_SUPPORTED);
/*     */       }
/* 259 */       if (
/* 297 */         (a = ltkrn.AllocBitmapHandle()) == 0L)
/* 233 */         throw new RasterException(RasterExceptionCode.NO_MEMORY);
/*     */        tmp135_134 = null;
/*     */ 
/* 347 */       RasterException.checkErrorCode(a = ltkrn.CreateBitmap(a, ltkrn.BITMAPHANDLE_getSizeOf(), 1, parameters.getWidth(), parameters.getHeight(), parameters.getDepth() * a, a, tmp135_134, 0, tmp135_134, 0L));
/*     */       byte[] tmp149_148 = data;
/*     */ 
/* 234 */       RasterException.checkErrorCode(
/* 267 */         a = ltkrn.SetBitmapDataPointer(a, tmp149_148, 0, tmp149_148.length));
/*     */ 
/* 275 */       return RasterImage.createFromBitmapHandle(a);
/*     */     }
/*     */     finally
/*     */     {
/* 292 */       if (a != 0L)
/*     */       {
/* 336 */         ltkrn.FreeBitmapHandle(a);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   NetworkSaneDevice(InetAddress arg1, NetworkStream stream, String arg3, String arg4, String arg5, String arg6)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_2
/*     */     //   1: aload_0
/*     */     //   2: dup_x1
/*     */     //   3: dup_x2
/*     */     //   4: aload_1
/*     */     //   5: ldc 5
/*     */     //   7: aload_0
/*     */     //   8: dup_x1
/*     */     //   9: iconst_0
/*     */     //   10: dup
/*     */     //   11: aload_0
/*     */     //   12: dup_x1
/*     */     //   13: invokespecial 2	leadtools/sane/SaneDevice:<init>	()V
/*     */     //   16: putfield 3	leadtools/sane/NetworkSaneDevice:a	I
/*     */     //   19: putfield 4	leadtools/sane/NetworkSaneDevice:l	I
/*     */     //   22: putfield 6	leadtools/sane/NetworkSaneDevice:I	Ljava/lang/String;
/*     */     //   25: putfield 7	leadtools/sane/NetworkSaneDevice:C	Ljava/net/InetAddress;
/*     */     //   28: putfield 8	leadtools/sane/NetworkSaneDevice:D	Lleadtools/sane/NetworkStream;
/*     */     //   31: new 9	leadtools/sane/DeviceInfo
/*     */     //   34: dup
/*     */     //   35: aload_3
/*     */     //   36: aload 4
/*     */     //   38: aload 5
/*     */     //   40: aload 6
/*     */     //   42: invokespecial 10	leadtools/sane/DeviceInfo:<init>	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
/*     */     //   45: putfield 1	leadtools/sane/NetworkSaneDevice:L	Lleadtools/sane/DeviceInfo;
/*     */     //   48: return
/*     */   }
/*     */ 
/*     */   public void cancel()
/*     */   {
/* 188 */     if (this.h == 0)
/*     */       return;
/*     */     NetworkSaneDevice tmp10_8 = this; tmp10_8.D.writeOperationCode(SaneRpcCode.CANCEL); this.D.writeHandle(this.h);
/*     */ 
/* 190 */     tmp10_8.D.readDummy();
/*     */   }
/*     */ 
/*     */   private boolean j(String optionName, String compareName)
/*     */   {
/* 751 */     if ((optionName.isEmpty()) || (compareName.isEmpty())) {
/* 623 */       return false;
/*     */     }
/*     */ 
/* 771 */     return optionName.compareTo(compareName) == 0;
/*     */   }
/*     */ 
/*     */   private static int j(InputStream stream, OutputStream destination)
/*     */     throws IOException
/*     */   {
/*     */     DataInputStream a;
/*     */     int a;
/*     */     int a;
/* 165 */     if (((a = 
/*  85 */       (a = new DataInputStream(stream)).readInt()) == 
/* 165 */       -1) && (
/*  90 */       (a = a.read()) != -1))
/*     */     {
/*  76 */       return -1;
/*     */     }
/*     */ 
/* 172 */     byte[] a = new byte[a];
/*     */     int a;
/* 262 */     destination.write(a, 0, a);
/*     */ 
/* 311 */     return a;
/*     */   }
/*     */ 
/*     */   public String getOptionValue(String optionName)
/*     */   {
/* 459 */     SaneOptionDescriptor[] a = getOptionDescriptors();
/*     */     int a;
/* 679 */     if (j(optionName, a[a].getName()))
/*     */     {
/*     */       NetworkSaneDevice tmp30_28 = this;
/*     */       NetworkSaneDevice tmp31_30 = tmp30_28; tmp31_30.D.writeOperationCode(SaneRpcCode.CONTROL_OPTION); this.D
/* 695 */         .writeHandle(this.h); this.D
/* 745 */         .writeInt(a);
/*     */ 
/* 535 */       tmp31_30.D
/* 696 */         .writeInt(SaneOptionActions.GET.getValue());
/*     */ 
/* 535 */       this.D
/* 619 */         .writeInt(a[a].getType().getValue());
/*     */ 
/* 535 */       tmp30_28.D.writeInt(a[a].getSize());
/*     */ 
/* 627 */       int a = 
/* 679 */         0;
/*     */ 
/* 602 */       switch (1.$SwitchMap$leadtools$sane$SaneValueType[a[a].getType().ordinal()])
/*     */       {
/*     */       case 1:
/*     */       case 2:
/*     */       case 3:
/*     */         while (true)
/*     */         {
/* 664 */           a = a[a].getSize() / 4;
/*     */         }
/*     */ 
/*     */       case 4:
/* 754 */         a = a[a].getSize(); tmpTernaryOp = this;
/*     */ 
/* 538 */         break;
/*     */       }
/*     */ 
/* 664 */       (0 == 0 ? this : this).D.writeInt(a);
/*     */ 
/* 551 */       byte[] a = new byte[a[a].getSize()];
/*     */ 
/* 467 */       this.D.writeBytes(a);
/*     */ 
/* 586 */       String a = null;
/*     */ 
/* 534 */       ControlOptionDataResponse a = l();
/*     */ 
/* 738 */       switch (1.$SwitchMap$leadtools$sane$SaneValueType[a.type.ordinal()])
/*     */       {
/*     */       case 3:
/* 658 */         a = this.D.covertToInt(a.value) == 1 ? "true" : "false";
/*     */ 
/* 682 */         return a;
/*     */       case 1:
/*     */       case 2:
/*     */         while (true)
/* 611 */           if (0 == 0)
/* 452 */             return 
/* 611 */               a = Integer.toString(this.D.covertToInt(a.value));
/*     */       case 4:
/* 607 */         return a = this.D.covertToString(a.value);
/*     */       }
/*     */ 
/* 741 */       return a;
/*     */     }
/*     */ 
/* 772 */     a++;
/*     */ 
/* 724 */     return null;
/*     */   }
/*     */ 
/*     */   public void start()
/*     */   {
/*     */     NetworkSaneDevice tmp2_0 = this;
/*     */     NetworkSaneDevice tmp3_2 = tmp2_0;
/*     */     NetworkSaneDevice tmp4_3 = tmp3_2;
/*     */     NetworkSaneDevice tmp5_4 = tmp4_3; tmp5_4.D.writeOperationCode(SaneRpcCode.START); this.D.writeHandle(this.h);
/*     */ 
/* 204 */     int a = this.D
/* 204 */       .readStatus(false);
/*     */ 
/* 146 */     this.a = tmp5_4.D
/* 160 */       .readInt();
/*     */ 
/* 146 */     this.l = tmp4_3.D.readInt();
/*     */ 
/* 146 */     this.I = tmp3_2.D.readString();
/*     */ 
/* 106 */     RasterException.checkErrorCode(a);
/*     */ 
/*  83 */     if (!
/* 146 */       tmp2_0.I.isEmpty())
/*     */     {
/*     */       NetworkSaneDevice tmp76_74 = this; j(tmp76_74.I);
/*     */       NetworkSaneDevice tmp92_90 = this;
/*     */       NetworkSaneDevice tmp93_92 = tmp92_90; this.a = tmp93_92.D.readInt(); this.l = tmp93_92.D
/* 156 */         .readInt();
/*     */ 
/*  67 */       this.I = tmp92_90.D
/*  69 */         .readString();
/*     */ 
/*  40 */       tmp76_74.D
/* 125 */         .readStatus(true);
/*     */     }
/*     */     NetworkSaneDevice tmp124_122 = this; tmp124_122.k = SaneUtils.createSocket(tmp124_122.C, this.a);
/*     */   }
/*     */ 
/*     */   byte[] acquireData(SaneParameters parameters)
/*     */   {
/* 219 */     start();
/*     */ 
/* 340 */     InputStream a = SaneUtils.getInputStream(this.k);
/*     */     try
/*     */     {
/* 260 */       return processFrame(a, parameters, this.l);
/*     */     }
/*     */     catch (Exception a)
/*     */     {
/* 341 */       throw new RasterException(a.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public SaneParameters getParameters()
/*     */   {
/*     */     NetworkSaneDevice tmp2_0 = this; tmp2_0.D.writeOperationCode(SaneRpcCode.GET_PARAMETERS); this.D.writeHandle(this.h);
/*     */ 
/* 383 */     int a = this.D.readInt();
/*     */ 
/* 316 */     this.D.readStatus(true);
/*     */ 
/* 384 */     int a = tmp2_0.D
/* 384 */       .readInt() == 1 ? 1 : 0;
/*     */     NetworkSaneDevice tmp57_55 = this; int a = tmp57_55.D.readInt();
/*     */ 
/* 270 */     int a = this.D
/* 270 */       .readInt();
/*     */ 
/* 334 */     int a = tmp57_55.D
/* 334 */       .readInt();
/*     */ 
/* 345 */     int a = this.D
/* 345 */       .readInt();
/*     */ 
/* 268 */     return new SaneParameters(a, a, a, a, a, a);
/*     */   }
/*     */ 
/*     */   private SaneOptionDescriptor j()
/*     */   {
/*     */     NetworkSaneDevice tmp9_7 = this;
/*     */     NetworkSaneDevice tmp10_9 = tmp9_7;
/*     */     NetworkSaneDevice tmp11_10 = tmp10_9; String a = tmp11_10.D.readString();
/*     */ 
/* 355 */     String a = 
/* 361 */       this.D.readString();
/*     */ 
/* 283 */     String a = 
/* 361 */       tmp11_10.D.readString();
/*     */ 
/* 348 */     SaneValueType a = SaneValueType.forValue(
/* 361 */       this.D.readInt());
/*     */ 
/* 287 */     SaneUnit a = SaneUnit.forValue(
/* 361 */       tmp10_9.D.readInt());
/*     */ 
/* 254 */     int a = 
/* 361 */       this.D.readInt();
/*     */ 
/* 394 */     int a = tmp9_7.D
/* 394 */       .readInt();
/*     */ 
/* 253 */     SaneConstraintType a = SaneConstraintType.forValue(
/* 361 */       this.D.readInt());
/*     */ 
/* 298 */     int a = 0;
/*     */ 
/* 282 */     Object localObject1 = null;
/*     */ 
/* 389 */     this.D.readUnusedPointer();
/*     */     Object a;
/* 214 */     switch (1.$SwitchMap$leadtools$sane$SaneConstraintType[a.ordinal()])
/*     */     {
/*     */     case 1:
/* 332 */       break;
/*     */     case 2:
/*     */     case 3:
/*     */       while (true)
/* 273 */         if (0 == 0)
/*     */         {
/*     */           NetworkSaneDevice tmp144_142 = this; int a = tmp144_142.D.readInt();
/*     */ 
/* 213 */           int a = 
/* 393 */             this.D.readInt();
/*     */ 
/* 343 */           int i = 
/* 393 */             tmp144_142.D.readInt();
/*     */ 
/* 273 */           this.D.readUnusedPointer();
/*     */           int a;
/* 321 */           switch (a)
/*     */           {
/*     */           case INT:
/*     */           case FIXED:
/* 286 */             while (0 != 0);
/* 286 */             a = new SaneRangeConstraint(a, a, a);
/*     */ 
/* 245 */             break;
/*     */           default:
/* 377 */             break;
/*     */ 
/* 367 */             int[] a = new int[a - 1];
/*     */ 
/* 390 */             a = this.D.readInt();
/*     */             int a;
/* 373 */             int a = this.D.readInt();
/*     */ 
/* 396 */             if (a != 0) {
/* 360 */               a[(a - 1)] = a;
/*     */             }
/*     */ 
/* 369 */             a++;
/*     */ 
/* 243 */             a = new SaneWordListConstraint(a);
/* 244 */             break;
/*     */           }
/*     */         }
/*     */     case 4:
/* 228 */       List a = new ArrayList();
/*     */ 
/* 272 */       a = this.D.readInt();
/*     */       int a;
/* 400 */       String a = this.D.readString();
/*     */ 
/* 312 */       if (a != a - 1)
/* 300 */         a.add(a);
/* 211 */       a++;
/*     */ 
/* 278 */       a = new SaneStringListConstraint((String[])a.toArray(new String[0]));
/*     */     }
/*     */ 
/* 408 */     return new SaneOptionDescriptor(a, a, a, a, a, a, a, (SaneConstraint)a);
/*     */   }
/*     */ 
/*     */   public DeviceInfo getInfo()
/*     */   {
/*  23 */     return this.L; } 
/*     */   public void setOptionValue(String optionName, String value) { // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokevirtual 84	leadtools/sane/NetworkSaneDevice:getOptionDescriptors	()[Lleadtools/sane/SaneOptionDescriptor;
/*     */     //   4: astore_3
/*     */     //   5: iconst_0
/*     */     //   6: dup
/*     */     //   7: istore 4
/*     */     //   9: aload_3
/*     */     //   10: arraylength
/*     */     //   11: if_icmpge +306 -> 317
/*     */     //   14: aload_0
/*     */     //   15: aload_1
/*     */     //   16: aload_3
/*     */     //   17: iload 4
/*     */     //   19: aaload
/*     */     //   20: invokevirtual 118	leadtools/sane/SaneOptionDescriptor:getName	()Ljava/lang/String;
/*     */     //   23: invokespecial 119	leadtools/sane/NetworkSaneDevice:j	(Ljava/lang/String;Ljava/lang/String;)Z
/*     */     //   26: ifeq +283 -> 309
/*     */     //   29: aload_0
/*     */     //   30: dup
/*     */     //   31: dup2
/*     */     //   32: getfield 8	leadtools/sane/NetworkSaneDevice:D	Lleadtools/sane/NetworkStream;
/*     */     //   35: getstatic 120	leadtools/sane/SaneRpcCode:CONTROL_OPTION	Lleadtools/sane/SaneRpcCode;
/*     */     //   38: invokevirtual 12	leadtools/sane/NetworkStream:writeOperationCode	(Lleadtools/sane/SaneRpcCode;)V
/*     */     //   41: getfield 8	leadtools/sane/NetworkSaneDevice:D	Lleadtools/sane/NetworkStream;
/*     */     //   44: aload_0
/*     */     //   45: dup_x1
/*     */     //   46: getfield 17	leadtools/sane/NetworkSaneDevice:h	I
/*     */     //   49: invokevirtual 23	leadtools/sane/NetworkStream:writeHandle	(I)V
/*     */     //   52: getfield 8	leadtools/sane/NetworkSaneDevice:D	Lleadtools/sane/NetworkStream;
/*     */     //   55: iload 4
/*     */     //   57: invokevirtual 121	leadtools/sane/NetworkStream:writeInt	(I)V
/*     */     //   60: getfield 8	leadtools/sane/NetworkSaneDevice:D	Lleadtools/sane/NetworkStream;
/*     */     //   63: getstatic 134	leadtools/sane/SaneOptionActions:SET	Lleadtools/sane/SaneOptionActions;
/*     */     //   66: invokevirtual 123	leadtools/sane/SaneOptionActions:getValue	()I
/*     */     //   69: invokevirtual 121	leadtools/sane/NetworkStream:writeInt	(I)V
/*     */     //   72: getfield 8	leadtools/sane/NetworkSaneDevice:D	Lleadtools/sane/NetworkStream;
/*     */     //   75: aload_3
/*     */     //   76: iload 4
/*     */     //   78: aaload
/*     */     //   79: invokevirtual 124	leadtools/sane/SaneOptionDescriptor:getType	()Lleadtools/sane/SaneValueType;
/*     */     //   82: invokevirtual 125	leadtools/sane/SaneValueType:getValue	()I
/*     */     //   85: invokevirtual 121	leadtools/sane/NetworkStream:writeInt	(I)V
/*     */     //   88: getstatic 94	leadtools/sane/NetworkSaneDevice$1:$SwitchMap$leadtools$sane$SaneValueType	[I
/*     */     //   91: aload_3
/*     */     //   92: iload 4
/*     */     //   94: aaload
/*     */     //   95: invokevirtual 124	leadtools/sane/SaneOptionDescriptor:getType	()Lleadtools/sane/SaneValueType;
/*     */     //   98: invokevirtual 95	leadtools/sane/SaneValueType:ordinal	()I
/*     */     //   101: iaload
/*     */     //   102: tableswitch	default:+202 -> 304, 1:+68->170, 2:+68->170, 3:+30->132, 4:+177->279
/*     */     //   133: dup
/*     */     //   134: getfield 8	leadtools/sane/NetworkSaneDevice:D	Lleadtools/sane/NetworkStream;
/*     */     //   137: aload_3
/*     */     //   138: iload 4
/*     */     //   140: aaload
/*     */     //   141: invokevirtual 126	leadtools/sane/SaneOptionDescriptor:getSize	()I
/*     */     //   144: invokevirtual 121	leadtools/sane/NetworkStream:writeInt	(I)V
/*     */     //   147: getfield 8	leadtools/sane/NetworkSaneDevice:D	Lleadtools/sane/NetworkStream;
/*     */     //   150: aload_2
/*     */     //   151: invokestatic 135	java/lang/Boolean:parseBoolean	(Ljava/lang/String;)Z
/*     */     //   154: iconst_1
/*     */     //   155: if_icmpne +7 -> 162
/*     */     //   158: iconst_1
/*     */     //   159: goto +4 -> 163
/*     */     //   162: iconst_0
/*     */     //   163: invokevirtual 121	leadtools/sane/NetworkStream:writeInt	(I)V
/*     */     //   166: aload_0
/*     */     //   167: goto +138 -> 305
/*     */     //   170: iconst_1
/*     */     //   171: iconst_0
/*     */     //   172: ifne +104 -> 276
/*     */     //   175: anewarray 104	java/lang/String
/*     */     //   178: dup
/*     */     //   179: iconst_0
/*     */     //   180: aload_2
/*     */     //   181: aastore
/*     */     //   182: astore 5
/*     */     //   184: aload_2
/*     */     //   185: ldc 136
/*     */     //   187: invokevirtual 137	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
/*     */     //   190: ifeq +11 -> 201
/*     */     //   193: aload_2
/*     */     //   194: ldc 136
/*     */     //   196: invokevirtual 138	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
/*     */     //   199: astore 5
/*     */     //   201: aload_0
/*     */     //   202: getfield 8	leadtools/sane/NetworkSaneDevice:D	Lleadtools/sane/NetworkStream;
/*     */     //   205: aload_3
/*     */     //   206: iload 4
/*     */     //   208: aaload
/*     */     //   209: invokevirtual 126	leadtools/sane/SaneOptionDescriptor:getSize	()I
/*     */     //   212: aload 5
/*     */     //   214: arraylength
/*     */     //   215: imul
/*     */     //   216: invokevirtual 121	leadtools/sane/NetworkStream:writeInt	(I)V
/*     */     //   219: aload_0
/*     */     //   220: getfield 8	leadtools/sane/NetworkSaneDevice:D	Lleadtools/sane/NetworkStream;
/*     */     //   223: aload 5
/*     */     //   225: arraylength
/*     */     //   226: invokevirtual 121	leadtools/sane/NetworkStream:writeInt	(I)V
/*     */     //   229: aload 5
/*     */     //   231: dup
/*     */     //   232: astore 6
/*     */     //   234: arraylength
/*     */     //   235: istore 7
/*     */     //   237: iconst_0
/*     */     //   238: dup
/*     */     //   239: istore 8
/*     */     //   241: iload 7
/*     */     //   243: if_icmpge +61 -> 304
/*     */     //   246: aload 6
/*     */     //   248: iload 8
/*     */     //   250: iinc 8 1
/*     */     //   253: aaload
/*     */     //   254: astore 9
/*     */     //   256: aload_0
/*     */     //   257: getfield 8	leadtools/sane/NetworkSaneDevice:D	Lleadtools/sane/NetworkStream;
/*     */     //   260: aload 9
/*     */     //   262: invokestatic 139	java/lang/Integer:parseInt	(Ljava/lang/String;)I
/*     */     //   265: invokevirtual 121	leadtools/sane/NetworkStream:writeInt	(I)V
/*     */     //   268: iload 8
/*     */     //   270: goto -29 -> 241
/*     */     //   273: goto +31 -> 304
/*     */     //   276: goto -105 -> 171
/*     */     //   279: aload_0
/*     */     //   280: dup
/*     */     //   281: dup_x1
/*     */     //   282: getfield 8	leadtools/sane/NetworkSaneDevice:D	Lleadtools/sane/NetworkStream;
/*     */     //   285: aload_2
/*     */     //   286: invokevirtual 140	java/lang/String:length	()I
/*     */     //   289: iconst_1
/*     */     //   290: iadd
/*     */     //   291: invokevirtual 121	leadtools/sane/NetworkStream:writeInt	(I)V
/*     */     //   294: getfield 8	leadtools/sane/NetworkSaneDevice:D	Lleadtools/sane/NetworkStream;
/*     */     //   297: aload_2
/*     */     //   298: invokevirtual 14	leadtools/sane/NetworkStream:writeString	(Ljava/lang/String;)V
/*     */     //   301: goto +4 -> 305
/*     */     //   304: aload_0
/*     */     //   305: invokespecial 128	leadtools/sane/NetworkSaneDevice:l	()Lleadtools/sane/NetworkSaneDevice$ControlOptionDataResponse;
/*     */     //   308: pop
/*     */     //   309: iinc 4 1
/*     */     //   312: iload 4
/*     */     //   314: goto -305 -> 9
/*     */     //   317: return } 
/* 189 */   public void open() { ???.D.writeOperationCode(SaneRpcCode.OPEN);
/*     */     NetworkSaneDevice this;
/* 189 */     ???.D.writeString(this.L.getName());
/*     */     NetworkSaneDevice tmp34_32 = this; this.h = tmp34_32.D.readHandle();
/*     */ 
/* 120 */     String str1 = tmp34_32.D
/* 120 */       .readString();
/*     */ 
/* 182 */     this.D.readStatus(true);
/*     */     String a;
/* 197 */     if (!a.isEmpty())
/*     */     {
/* 192 */       j(a);
/*     */       NetworkSaneDevice tmp74_72 = this; this.h = tmp74_72.D.readInt();
/*     */ 
/* 181 */       a = tmp74_72.D
/* 181 */         .readString();
/*     */ 
/* 192 */       this.D.readStatus(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   public SaneOptionDescriptor[] getOptionDescriptors()
/*     */   {
/* 399 */     ???.D.writeOperationCode(SaneRpcCode.GET_OPTION_DESCRIPTORS);
/*     */     NetworkSaneDevice this;
/* 399 */     ???.D.writeHandle(this.h);
/*     */     int a;
/* 225 */     if (
/* 365 */       (a = this.D
/* 365 */       .readInt() - 1) <= 0)
/* 242 */       return new SaneOptionDescriptor[0];
/* 215 */     SaneOptionDescriptor[] a = new SaneOptionDescriptor[a + 1];
/*     */     int a;
/* 226 */     a[j()] = (a++);
/*     */ 
/* 238 */     return a;
/*     */   }
/*     */ 
/*     */   public static byte[] processFrame(InputStream stream, SaneParameters params, int byteOrder)
/*     */     throws IOException
/*     */   {
/*     */     SaneParameters tmp1_0 = params; int a = tmp1_0.getBytesPerLine();
/*     */ 
/*  45 */     int a = 
/* 162 */       tmp1_0.getHeight();
/*     */ 
/* 175 */     int a = a * a;
/*     */     ByteArrayOutputStream a;
/* 198 */     if (a > 0)
/*     */     {
/*  61 */       ByteArrayOutputStream localByteArrayOutputStream1 = 
/* 198 */         new ByteArrayOutputStream(a); tmpTernaryOp = stream;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void j(String resource)
/*     */   {
/*     */     NetworkSaneDevice tmp4_3 = this; this.D.writeOperationCode(SaneRpcCode.AUTHORIZE); tmp4_3.D
/* 158 */       .writeString(resource);
/*     */ 
/* 141 */     AuthorizationInfo a = SaneSession.invokeAuthorizationCallback(
/* 145 */       resource); this.D
/* 203 */       .writeString(a._userName);
/*     */ 
/* 145 */     tmp4_3.D.writeString(a._password);
/*     */ 
/* 145 */     this.D.readDummy();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*  51 */     if (this.h == 0)
/*     */       return;
/*     */     NetworkSaneDevice tmp10_8 = this; tmp10_8.D.writeOperationCode(SaneRpcCode.CLOSE); this.D.writeHandle(this.h);
/*     */ 
/* 130 */     this.h = 0;
/*     */ 
/* 169 */     tmp10_8.D.readDummy();
/*     */   }
/*     */ 
/*     */   public RasterImage acquireImage()
/*     */   {
/* 362 */     if (RasterSupport.isLocked(RasterSupportType.DOCUMENT))
/* 218 */       throw new RasterException(RasterExceptionCode.DOCUMENT_NOT_ENABLED);
/*     */     SaneParameters a;
/*     */     NetworkSaneDevice tmp22_20 = this;
/*     */ 
/* 342 */     return 
/* 386 */       tmp22_20.saneBufferToBitmapHandle(
/* 353 */       acquireData(
/* 386 */       a = tmp22_20.getParameters()), a);
/*     */   }
/*     */ 
/*     */   class ControlOptionDataResponse
/*     */   {
/*     */     public int valueSize;
/*     */     public SaneValueType type;
/*     */     public byte[] value;
/*     */     public int info;
/*     */ 
/*     */     ControlOptionDataResponse()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.jar
 * Qualified Name:     leadtools.sane.NetworkSaneDevice
 * JD-Core Version:    0.6.2
 */