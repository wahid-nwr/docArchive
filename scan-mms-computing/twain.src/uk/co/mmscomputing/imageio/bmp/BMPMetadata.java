/*     */ package uk.co.mmscomputing.imageio.bmp;
/*     */ 
/*     */ import java.awt.image.IndexColorModel;
/*     */ import javax.imageio.metadata.IIOMetadata;
/*     */ import javax.imageio.metadata.IIOMetadataNode;
/*     */ import org.w3c.dom.Node;
/*     */ 
/*     */ public class BMPMetadata extends IIOMetadata
/*     */   implements BMPConstants
/*     */ {
/*     */   private static final String formatName = "javax_imageio_bmp_1.0";
/*     */   private int width;
/*     */   private int height;
/*     */   private int bitsPerPixel;
/*     */   private int compression;
/*     */   private int imageSize;
/*     */   private int xPixelsPerMeter;
/*     */   private int yPixelsPerMeter;
/*     */   private int colorsUsed;
/*     */   private int colorsImportant;
/*     */   private int redMask;
/*     */   private int greenMask;
/*     */   private int blueMask;
/*     */   private IndexColorModel icm;
/*     */ 
/*     */   public BMPMetadata()
/*     */   {
/*  20 */     super(true, "javax_imageio_bmp_1.0", "uk.co.mmscomputing.imageio.bmp.BMPMetadata", null, null);
/*  21 */     this.xPixelsPerMeter = 2953;
/*  22 */     this.yPixelsPerMeter = 2953;
/*     */   }
/*     */   public void setWidth(int paramInt) {
/*  25 */     this.width = paramInt; } 
/*  26 */   public void setHeight(int paramInt) { this.height = paramInt; } 
/*  27 */   public void setBitsPerPixel(int paramInt) { this.bitsPerPixel = paramInt; } 
/*  28 */   public void setCompression(int paramInt) { this.compression = paramInt; } 
/*  29 */   public void setImageSize(int paramInt) { this.imageSize = paramInt; } 
/*     */   public void setXPixelsPerMeter(int paramInt) {
/*  31 */     this.xPixelsPerMeter = paramInt; } 
/*  32 */   public void setYPixelsPerMeter(int paramInt) { this.yPixelsPerMeter = paramInt; }
/*     */ 
/*     */   public void setXDotsPerInch(int paramInt)
/*     */   {
/*  36 */     this.xPixelsPerMeter = ((int)Math.round(paramInt * 1000.0D / 25.399999999999999D)); } 
/*  37 */   public void setYDotsPerInch(int paramInt) { this.yPixelsPerMeter = ((int)Math.round(paramInt * 1000.0D / 25.399999999999999D)); } 
/*     */   public void setColorsUsed(int paramInt) {
/*  39 */     this.colorsUsed = paramInt; } 
/*  40 */   public void setColorsImportant(int paramInt) { this.colorsImportant = paramInt; } 
/*  41 */   public void setRedMask(int paramInt) { this.redMask = paramInt; } 
/*  42 */   public void setGreenMask(int paramInt) { this.greenMask = paramInt; } 
/*  43 */   public void setBlueMask(int paramInt) { this.blueMask = paramInt; }
/*     */ 
/*     */   public void setIndexColorModel(IndexColorModel paramIndexColorModel) {
/*  46 */     if (paramIndexColorModel != null) this.colorsUsed = paramIndexColorModel.getMapSize();
/*  47 */     this.icm = paramIndexColorModel;
/*     */   }
/*     */   public int getXPixelsPerMeter() {
/*  50 */     return this.xPixelsPerMeter; } 
/*  51 */   public int getYPixelsPerMeter() { return this.yPixelsPerMeter; }
/*     */ 
/*     */   public int getXDotsPerInch() {
/*  54 */     return (int)Math.round(this.xPixelsPerMeter * 25.399999999999999D / 1000.0D); } 
/*  55 */   public int getYDotsPerInch() { return (int)Math.round(this.yPixelsPerMeter * 25.399999999999999D / 1000.0D); } 
/*     */   public IndexColorModel getIndexColorModel() {
/*  57 */     return this.icm;
/*     */   }
/*  59 */   public boolean isReadOnly() { return false; }
/*     */ 
/*     */   public Node getAsTree(String paramString) {
/*  62 */     if (paramString.equals("javax_imageio_bmp_1.0"))
/*  63 */       return getNativeTree();
/*  64 */     if (paramString.equals("javax_imageio_1.0")) {
/*  65 */       return getStandardTree();
/*     */     }
/*  67 */     throw new IllegalArgumentException(getClass().getName() + ".mergeTree:\n\tUnknown format: " + paramString);
/*     */   }
/*     */ 
/*     */   public void mergeTree(String paramString, Node paramNode) {
/*  71 */     throw new IllegalStateException(getClass().getName() + ".mergeTree:\n\tFunction not supported.");
/*     */   }
/*     */ 
/*     */   public void reset() {
/*  75 */     throw new IllegalStateException(getClass().getName() + ".reset:\n\tFunction not supported.");
/*     */   }
/*     */ 
/*     */   private Node getNativeTree() {
/*  79 */     IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("javax_imageio_bmp_1.0");
/*  80 */     addChildNode(localIIOMetadataNode1, "BMPVersion", "BMP v. 3.x");
/*  81 */     addChildNode(localIIOMetadataNode1, "Width", new Integer(this.width));
/*  82 */     addChildNode(localIIOMetadataNode1, "Height", new Integer(this.height));
/*  83 */     addChildNode(localIIOMetadataNode1, "BitsPerPixel", new Integer(this.bitsPerPixel));
/*  84 */     addChildNode(localIIOMetadataNode1, "Compression", new Integer(this.compression));
/*  85 */     addChildNode(localIIOMetadataNode1, "ImageSize", new Integer(this.imageSize));
/*     */ 
/*  87 */     IIOMetadataNode localIIOMetadataNode2 = addChildNode(localIIOMetadataNode1, "PixelsPerMeter", null);
/*  88 */     addChildNode(localIIOMetadataNode2, "X", new Integer(this.xPixelsPerMeter));
/*  89 */     addChildNode(localIIOMetadataNode2, "Y", new Integer(this.yPixelsPerMeter));
/*     */ 
/*  91 */     addChildNode(localIIOMetadataNode1, "ColorsUsed", new Integer(this.colorsUsed));
/*  92 */     addChildNode(localIIOMetadataNode1, "ColorsImportant", new Integer(this.colorsImportant));
/*     */ 
/*  94 */     if (this.icm != null) {
/*  95 */       localIIOMetadataNode2 = addChildNode(localIIOMetadataNode1, "Palette", null);
/*  96 */       for (int i = 0; i < this.colorsUsed; i++) {
/*  97 */         IIOMetadataNode localIIOMetadataNode3 = addChildNode(localIIOMetadataNode2, "PaletteEntry", null);
/*  98 */         addChildNode(localIIOMetadataNode3, "Red", new Byte((byte)this.icm.getRed(i)));
/*  99 */         addChildNode(localIIOMetadataNode3, "Green", new Byte((byte)this.icm.getGreen(i)));
/* 100 */         addChildNode(localIIOMetadataNode3, "Blue", new Byte((byte)this.icm.getBlue(i)));
/*     */       }
/*     */     }
/* 103 */     return localIIOMetadataNode1;
/*     */   }
/*     */ 
/*     */   protected IIOMetadataNode getStandardChromaNode() {
/* 107 */     IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Chroma");
/* 108 */     if (this.icm != null) {
/* 109 */       IIOMetadataNode localIIOMetadataNode2 = new IIOMetadataNode("Palette");
/* 110 */       localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
/* 111 */       for (int i = 0; i < this.colorsUsed; i++) {
/* 112 */         IIOMetadataNode localIIOMetadataNode3 = new IIOMetadataNode("PaletteEntry");
/* 113 */         localIIOMetadataNode2.appendChild(localIIOMetadataNode3);
/* 114 */         localIIOMetadataNode3.setAttribute("index", "" + i);
/* 115 */         localIIOMetadataNode3.setAttribute("red", "" + this.icm.getRed(i));
/* 116 */         localIIOMetadataNode3.setAttribute("green", "" + this.icm.getGreen(i));
/* 117 */         localIIOMetadataNode3.setAttribute("blue", "" + this.icm.getBlue(i));
/* 118 */         localIIOMetadataNode3.setAttribute("alpha", "255");
/*     */       }
/*     */     }
/* 121 */     return localIIOMetadataNode1;
/*     */   }
/*     */ 
/*     */   protected IIOMetadataNode getStandardCompressionNode() {
/* 125 */     IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Compression");
/* 126 */     IIOMetadataNode localIIOMetadataNode2 = new IIOMetadataNode("CompressionTypeName");
/* 127 */     localIIOMetadataNode2.setAttribute("value", BMPConstants.compressionTypeNames[this.compression]);
/* 128 */     localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
/* 129 */     return localIIOMetadataNode1;
/*     */   }
/*     */ 
/*     */   private String countBits(int paramInt) {
/* 133 */     int i = 0;
/* 134 */     while (paramInt > 0) {
/* 135 */       if ((paramInt & 0x1) == 1) i++;
/* 136 */       paramInt >>>= 1;
/*     */     }
/* 138 */     return "" + i;
/*     */   }
/*     */ 
/*     */   protected IIOMetadataNode getStandardDataNode() {
/* 142 */     IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Data");
/* 143 */     String str = "";
/* 144 */     if (this.bitsPerPixel == 24)
/* 145 */       str = "8 8 8";
/* 146 */     else if ((this.bitsPerPixel == 16) || (this.bitsPerPixel == 32))
/* 147 */       str = str + countBits(this.redMask) + " " + countBits(this.greenMask) + " " + countBits(this.blueMask);
/* 148 */     else if (this.bitsPerPixel <= 8) {
/* 149 */       str = str + this.bitsPerPixel;
/*     */     }
/* 151 */     IIOMetadataNode localIIOMetadataNode2 = new IIOMetadataNode("BitsPerSample");
/* 152 */     localIIOMetadataNode2.setAttribute("value", str);
/* 153 */     localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
/*     */ 
/* 155 */     return localIIOMetadataNode1;
/*     */   }
/*     */ 
/*     */   protected IIOMetadataNode getStandardDimensionNode() {
/* 159 */     if ((this.yPixelsPerMeter > 0.0D) && (this.xPixelsPerMeter > 0.0D)) {
/* 160 */       IIOMetadataNode localIIOMetadataNode1 = new IIOMetadataNode("Dimension");
/*     */ 
/* 162 */       double d = this.yPixelsPerMeter / this.xPixelsPerMeter;
/* 163 */       IIOMetadataNode localIIOMetadataNode2 = new IIOMetadataNode("PixelAspectRatio");
/* 164 */       localIIOMetadataNode2.setAttribute("value", "" + d);
/* 165 */       localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
/*     */ 
/* 167 */       d = 1000.0D / this.xPixelsPerMeter;
/* 168 */       localIIOMetadataNode2 = new IIOMetadataNode("HorizontalPhysicalPixelSpacing");
/* 169 */       localIIOMetadataNode2.setAttribute("value", "" + d);
/* 170 */       localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
/*     */ 
/* 172 */       d = 1000.0D / this.yPixelsPerMeter;
/* 173 */       localIIOMetadataNode2 = new IIOMetadataNode("VerticalPhysicalPixelSpacing");
/* 174 */       localIIOMetadataNode2.setAttribute("value", "" + d);
/* 175 */       localIIOMetadataNode1.appendChild(localIIOMetadataNode2);
/*     */ 
/* 177 */       return localIIOMetadataNode1;
/*     */     }
/* 179 */     return null;
/*     */   }
/*     */ 
/*     */   private String objectToString(Object paramObject) {
/* 183 */     return paramObject.toString();
/*     */   }
/*     */ 
/*     */   private IIOMetadataNode addChildNode(IIOMetadataNode paramIIOMetadataNode, String paramString, Object paramObject) {
/* 187 */     IIOMetadataNode localIIOMetadataNode = new IIOMetadataNode(paramString);
/* 188 */     if (paramObject != null) {
/* 189 */       localIIOMetadataNode.setUserObject(paramObject);
/* 190 */       localIIOMetadataNode.setNodeValue(objectToString(paramObject));
/*     */     }
/* 192 */     paramIIOMetadataNode.appendChild(localIIOMetadataNode);
/* 193 */     return localIIOMetadataNode;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.imageio.bmp.BMPMetadata
 * JD-Core Version:    0.6.2
 */