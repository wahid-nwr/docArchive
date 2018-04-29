/*     */ package ij.plugin;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ class LZWEncoder
/*     */ {
/*     */   private static final int EOF = -1;
/*     */   private int imgW;
/*     */   private int imgH;
/*     */   private byte[] pixAry;
/*     */   private int initCodeSize;
/*     */   private int remaining;
/*     */   private int curPixel;
/*     */   static final int BITS = 12;
/*     */   static final int HSIZE = 5003;
/*     */   int n_bits;
/* 568 */   int maxbits = 12;
/*     */   int maxcode;
/* 570 */   int maxmaxcode = 4096;
/*     */ 
/* 572 */   int[] htab = new int[5003];
/* 573 */   int[] codetab = new int[5003];
/*     */ 
/* 575 */   int hsize = 5003;
/*     */ 
/* 577 */   int free_ent = 0;
/*     */ 
/* 581 */   boolean clear_flg = false;
/*     */   int g_init_bits;
/*     */   int ClearCode;
/*     */   int EOFCode;
/* 615 */   int cur_accum = 0;
/* 616 */   int cur_bits = 0;
/*     */ 
/* 618 */   int[] masks = { 0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535 };
/*     */   int a_count;
/* 627 */   byte[] accum = new byte[256];
/*     */ 
/*     */   LZWEncoder(int width, int height, byte[] pixels, int color_depth)
/*     */   {
/* 632 */     this.imgW = width;
/* 633 */     this.imgH = height;
/* 634 */     this.pixAry = pixels;
/* 635 */     this.initCodeSize = Math.max(2, color_depth);
/*     */   }
/*     */ 
/*     */   void char_out(byte c, OutputStream outs)
/*     */     throws IOException
/*     */   {
/* 643 */     this.accum[(this.a_count++)] = c;
/* 644 */     if (this.a_count >= 254)
/* 645 */       flush_char(outs);
/*     */   }
/*     */ 
/*     */   void cl_block(OutputStream outs)
/*     */     throws IOException
/*     */   {
/* 654 */     cl_hash(this.hsize);
/* 655 */     this.free_ent = (this.ClearCode + 2);
/* 656 */     this.clear_flg = true;
/*     */ 
/* 658 */     output(this.ClearCode, outs);
/*     */   }
/*     */ 
/*     */   void cl_hash(int hsize)
/*     */   {
/* 665 */     for (int i = 0; i < hsize; i++)
/* 666 */       this.htab[i] = -1;
/*     */   }
/*     */ 
/*     */   void compress(int init_bits, OutputStream outs)
/*     */     throws IOException
/*     */   {
/* 681 */     this.g_init_bits = init_bits;
/*     */ 
/* 684 */     this.clear_flg = false;
/* 685 */     this.n_bits = this.g_init_bits;
/* 686 */     this.maxcode = MAXCODE(this.n_bits);
/*     */ 
/* 688 */     this.ClearCode = (1 << init_bits - 1);
/* 689 */     this.EOFCode = (this.ClearCode + 1);
/* 690 */     this.free_ent = (this.ClearCode + 2);
/*     */ 
/* 692 */     this.a_count = 0;
/*     */ 
/* 694 */     int ent = nextPixel();
/*     */ 
/* 696 */     int hshift = 0;
/* 697 */     for (int fcode = this.hsize; fcode < 65536; fcode *= 2)
/* 698 */       hshift++;
/* 699 */     hshift = 8 - hshift;
/*     */ 
/* 701 */     int hsize_reg = this.hsize;
/* 702 */     cl_hash(hsize_reg);
/*     */ 
/* 704 */     output(this.ClearCode, outs);
/*     */     int c;
/* 707 */     while ((c = nextPixel()) != -1)
/*     */     {
/* 709 */       fcode = (c << this.maxbits) + ent;
/* 710 */       int i = c << hshift ^ ent;
/*     */ 
/* 712 */       if (this.htab[i] == fcode)
/*     */       {
/* 714 */         ent = this.codetab[i];
/*     */       }
/*     */       else {
/* 717 */         if (this.htab[i] >= 0)
/*     */         {
/* 719 */           int disp = hsize_reg - i;
/* 720 */           if (i == 0)
/* 721 */             disp = 1;
/*     */           do
/*     */           {
/* 724 */             if (i -= disp < 0) {
/* 725 */               i += hsize_reg;
/*     */             }
/* 727 */             if (this.htab[i] == fcode)
/*     */             {
/* 729 */               ent = this.codetab[i];
/* 730 */               break;
/*     */             }
/*     */           }
/* 733 */           while (this.htab[i] >= 0);
/*     */         }
/* 735 */         output(ent, outs);
/* 736 */         ent = c;
/* 737 */         if (this.free_ent < this.maxmaxcode)
/*     */         {
/* 739 */           this.codetab[i] = (this.free_ent++);
/* 740 */           this.htab[i] = fcode;
/*     */         }
/*     */         else {
/* 743 */           cl_block(outs);
/*     */         }
/*     */       }
/*     */     }
/* 746 */     output(ent, outs);
/* 747 */     output(this.EOFCode, outs);
/*     */   }
/*     */ 
/*     */   void encode(OutputStream os)
/*     */     throws IOException
/*     */   {
/* 754 */     os.write(this.initCodeSize);
/*     */ 
/* 756 */     this.remaining = (this.imgW * this.imgH);
/* 757 */     this.curPixel = 0;
/*     */ 
/* 759 */     compress(this.initCodeSize + 1, os);
/*     */ 
/* 761 */     os.write(0);
/*     */   }
/*     */ 
/*     */   void flush_char(OutputStream outs)
/*     */     throws IOException
/*     */   {
/* 768 */     if (this.a_count > 0)
/*     */     {
/* 770 */       outs.write(this.a_count);
/* 771 */       outs.write(this.accum, 0, this.a_count);
/* 772 */       this.a_count = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   final int MAXCODE(int n_bits)
/*     */   {
/* 779 */     return (1 << n_bits) - 1;
/*     */   }
/*     */ 
/*     */   private int nextPixel()
/*     */   {
/* 788 */     if (this.remaining == 0) {
/* 789 */       return -1;
/*     */     }
/* 791 */     this.remaining -= 1;
/*     */ 
/* 793 */     byte pix = this.pixAry[(this.curPixel++)];
/*     */ 
/* 795 */     return pix & 0xFF;
/*     */   }
/*     */ 
/*     */   void output(int code, OutputStream outs)
/*     */     throws IOException
/*     */   {
/* 801 */     this.cur_accum &= this.masks[this.cur_bits];
/*     */ 
/* 803 */     if (this.cur_bits > 0)
/* 804 */       this.cur_accum |= code << this.cur_bits;
/*     */     else {
/* 806 */       this.cur_accum = code;
/*     */     }
/* 808 */     this.cur_bits += this.n_bits;
/*     */ 
/* 810 */     while (this.cur_bits >= 8)
/*     */     {
/* 812 */       char_out((byte)(this.cur_accum & 0xFF), outs);
/* 813 */       this.cur_accum >>= 8;
/* 814 */       this.cur_bits -= 8;
/*     */     }
/*     */ 
/* 819 */     if ((this.free_ent > this.maxcode) || (this.clear_flg))
/*     */     {
/* 821 */       if (this.clear_flg)
/*     */       {
/* 823 */         this.maxcode = MAXCODE(this.n_bits = this.g_init_bits);
/* 824 */         this.clear_flg = false;
/*     */       }
/*     */       else
/*     */       {
/* 828 */         this.n_bits += 1;
/* 829 */         if (this.n_bits == this.maxbits)
/* 830 */           this.maxcode = this.maxmaxcode;
/*     */         else {
/* 832 */           this.maxcode = MAXCODE(this.n_bits);
/*     */         }
/*     */       }
/*     */     }
/* 836 */     if (code == this.EOFCode)
/*     */     {
/* 839 */       while (this.cur_bits > 0)
/*     */       {
/* 841 */         char_out((byte)(this.cur_accum & 0xFF), outs);
/* 842 */         this.cur_accum >>= 8;
/* 843 */         this.cur_bits -= 8;
/*     */       }
/*     */ 
/* 846 */       flush_char(outs);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.LZWEncoder
 * JD-Core Version:    0.6.2
 */