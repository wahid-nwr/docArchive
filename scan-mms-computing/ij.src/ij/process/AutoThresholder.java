/*      */ package ij.process;
/*      */ 
/*      */ import ij.IJ;
/*      */ 
/*      */ public class AutoThresholder
/*      */ {
/*      */   private static String[] mStrings;
/*      */ 
/*      */   public static String[] getMethods()
/*      */   {
/*   31 */     if (mStrings == null) {
/*   32 */       Method[] mVals = Method.values();
/*   33 */       mStrings = new String[mVals.length];
/*   34 */       for (int i = 0; i < mVals.length; i++)
/*   35 */         mStrings[i] = mVals[i].name();
/*      */     }
/*   37 */     return mStrings;
/*      */   }
/*      */ 
/*      */   public int getThreshold(Method method, int[] histogram) {
/*   41 */     int threshold = 0;
/*   42 */     switch (1.$SwitchMap$ij$process$AutoThresholder$Method[method.ordinal()]) { case 1:
/*   43 */       threshold = defaultIsoData(histogram); break;
/*      */     case 2:
/*   44 */       threshold = IJIsoData(histogram); break;
/*      */     case 3:
/*   45 */       threshold = Huang(histogram); break;
/*      */     case 4:
/*   46 */       threshold = Intermodes(histogram); break;
/*      */     case 5:
/*   47 */       threshold = IsoData(histogram); break;
/*      */     case 6:
/*   48 */       threshold = Li(histogram); break;
/*      */     case 7:
/*   49 */       threshold = MaxEntropy(histogram); break;
/*      */     case 8:
/*   50 */       threshold = Mean(histogram); break;
/*      */     case 9:
/*   51 */       threshold = MinErrorI(histogram); break;
/*      */     case 10:
/*   52 */       threshold = Minimum(histogram); break;
/*      */     case 11:
/*   53 */       threshold = Moments(histogram); break;
/*      */     case 12:
/*   54 */       threshold = Otsu(histogram); break;
/*      */     case 13:
/*   55 */       threshold = Percentile(histogram); break;
/*      */     case 14:
/*   56 */       threshold = RenyiEntropy(histogram); break;
/*      */     case 15:
/*   57 */       threshold = Shanbhag(histogram); break;
/*      */     case 16:
/*   58 */       threshold = Triangle(histogram); break;
/*      */     case 17:
/*   59 */       threshold = Yen(histogram);
/*      */     }
/*   61 */     if (threshold == -1) threshold = 0;
/*   62 */     return threshold;
/*      */   }
/*      */ 
/*      */   public int getThreshold(String mString, int[] histogram)
/*      */   {
/*   67 */     int index = mString.indexOf(" ");
/*   68 */     if (index != -1)
/*   69 */       mString = mString.substring(0, index);
/*   70 */     Method method = (Method)Method.valueOf(Method.class, mString);
/*   71 */     return getThreshold(method, histogram);
/*      */   }
/*      */ 
/*      */   int Huang(int[] data)
/*      */   {
/*   81 */     int threshold = -1;
/*      */ 
/*   93 */     int first_bin = 0;
/*   94 */     for (int ih = 0; ih < 256; ih++) {
/*   95 */       if (data[ih] != 0) {
/*   96 */         first_bin = ih;
/*   97 */         break;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  102 */     int last_bin = 255;
/*  103 */     for (ih = 255; ih >= first_bin; ih--) {
/*  104 */       if (data[ih] != 0) {
/*  105 */         last_bin = ih;
/*  106 */         break;
/*      */       }
/*      */     }
/*  109 */     double term = 1.0D / (last_bin - first_bin);
/*  110 */     double[] mu_0 = new double[256];
/*      */     double num_pix;
/*  111 */     double sum_pix = num_pix = 0.0D;
/*  112 */     for (ih = first_bin; ih < 256; ih++) {
/*  113 */       sum_pix += ih * data[ih];
/*  114 */       num_pix += data[ih];
/*      */ 
/*  116 */       mu_0[ih] = (sum_pix / num_pix);
/*      */     }
/*      */ 
/*  119 */     double[] mu_1 = new double[256];
/*  120 */     sum_pix = num_pix = 0.0D;
/*  121 */     for (ih = last_bin; ih > 0; ih--) {
/*  122 */       sum_pix += ih * data[ih];
/*  123 */       num_pix += data[ih];
/*      */ 
/*  125 */       mu_1[(ih - 1)] = (sum_pix / num_pix);
/*      */     }
/*      */ 
/*  129 */     threshold = -1;
/*  130 */     double min_ent = 1.7976931348623157E+308D;
/*  131 */     for (int it = 0; it < 256; it++) {
/*  132 */       double ent = 0.0D;
/*  133 */       for (ih = 0; ih <= it; ih++)
/*      */       {
/*  135 */         double mu_x = 1.0D / (1.0D + term * Math.abs(ih - mu_0[it]));
/*  136 */         if ((mu_x >= 1.0E-06D) && (mu_x <= 0.999999D))
/*      */         {
/*  138 */           ent += data[ih] * (-mu_x * Math.log(mu_x) - (1.0D - mu_x) * Math.log(1.0D - mu_x));
/*      */         }
/*      */       }
/*      */ 
/*  142 */       for (ih = it + 1; ih < 256; ih++)
/*      */       {
/*  144 */         double mu_x = 1.0D / (1.0D + term * Math.abs(ih - mu_1[it]));
/*  145 */         if ((mu_x >= 1.0E-06D) && (mu_x <= 0.999999D))
/*      */         {
/*  147 */           ent += data[ih] * (-mu_x * Math.log(mu_x) - (1.0D - mu_x) * Math.log(1.0D - mu_x));
/*      */         }
/*      */       }
/*      */ 
/*  151 */       if (ent < min_ent) {
/*  152 */         min_ent = ent;
/*  153 */         threshold = it;
/*      */       }
/*      */     }
/*  156 */     return threshold;
/*      */   }
/*      */ 
/*      */   boolean bimodalTest(double[] y) {
/*  160 */     int len = y.length;
/*  161 */     boolean b = false;
/*  162 */     int modes = 0;
/*      */ 
/*  164 */     for (int k = 1; k < len - 1; k++) {
/*  165 */       if ((y[(k - 1)] < y[k]) && (y[(k + 1)] < y[k])) {
/*  166 */         modes++;
/*  167 */         if (modes > 2)
/*  168 */           return false;
/*      */       }
/*      */     }
/*  171 */     if (modes == 2)
/*  172 */       b = true;
/*  173 */     return b;
/*      */   }
/*      */ 
/*      */   int Intermodes(int[] data)
/*      */   {
/*  190 */     double[] iHisto = new double[256];
/*  191 */     int iter = 0;
/*  192 */     int threshold = -1;
/*  193 */     for (int i = 0; i < 256; i++) {
/*  194 */       iHisto[i] = data[i];
/*      */     }
/*  196 */     double[] tHisto = iHisto;
/*      */ 
/*  198 */     while (!bimodalTest(iHisto))
/*      */     {
/*  200 */       for (int i = 1; i < 255; i++)
/*  201 */         tHisto[i] = ((iHisto[(i - 1)] + iHisto[i] + iHisto[(i + 1)]) / 3.0D);
/*  202 */       tHisto[0] = ((iHisto[0] + iHisto[1]) / 3.0D);
/*  203 */       tHisto['ÿ'] = ((iHisto['þ'] + iHisto['ÿ']) / 3.0D);
/*  204 */       iHisto = tHisto;
/*  205 */       iter++;
/*  206 */       if (iter > 10000) {
/*  207 */         threshold = -1;
/*  208 */         IJ.log("Intermodes: threshold not found after 10000 iterations.");
/*  209 */         return threshold;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  214 */     int tt = 0;
/*  215 */     for (int i = 1; i < 255; i++) {
/*  216 */       if ((iHisto[(i - 1)] < iHisto[i]) && (iHisto[(i + 1)] < iHisto[i])) {
/*  217 */         tt += i;
/*      */       }
/*      */     }
/*      */ 
/*  221 */     threshold = (int)Math.floor(tt / 2.0D);
/*  222 */     return threshold;
/*      */   }
/*      */ 
/*      */   int IsoData(int[] data)
/*      */   {
/*  251 */     int g = 0;
/*      */ 
/*  253 */     for (int i = 1; i < 256; i++)
/*  254 */       if (data[i] > 0) {
/*  255 */         g = i + 1;
/*  256 */         break;
/*      */       }
/*      */     do
/*      */     {
/*  260 */       int l = 0;
/*  261 */       int totl = 0;
/*  262 */       for (i = 0; i < g; i++) {
/*  263 */         totl += data[i];
/*  264 */         l += data[i] * i;
/*      */       }
/*  266 */       double h = 0.0D;
/*  267 */       double toth = 0.0D;
/*  268 */       for (i = g + 1; i < 256; i++) {
/*  269 */         toth += data[i];
/*  270 */         h += data[i] * i;
/*      */       }
/*  272 */       if ((totl > 0) && (toth > 0.0D)) {
/*  273 */         l /= totl;
/*  274 */         h /= toth;
/*  275 */         if (g == (int)Math.round((l + h) / 2.0D))
/*      */           break;
/*      */       }
/*  278 */       g++;
/*  279 */     }while (g <= 254);
/*  280 */     return -1;
/*      */ 
/*  282 */     return g;
/*      */   }
/*      */ 
/*      */   int defaultIsoData(int[] data)
/*      */   {
/*  287 */     int n = data.length;
/*  288 */     int[] data2 = new int[n];
/*  289 */     int mode = 0; int maxCount = 0;
/*  290 */     for (int i = 0; i < n; i++) {
/*  291 */       int count = data[i];
/*  292 */       data2[i] = data[i];
/*  293 */       if (data2[i] > maxCount) {
/*  294 */         maxCount = data2[i];
/*  295 */         mode = i;
/*      */       }
/*      */     }
/*  298 */     int maxCount2 = 0;
/*  299 */     for (int i = 0; i < n; i++) {
/*  300 */       if ((data2[i] > maxCount2) && (i != mode))
/*  301 */         maxCount2 = data2[i];
/*      */     }
/*  303 */     int hmax = maxCount;
/*  304 */     if ((hmax > maxCount2 * 2) && (maxCount2 != 0)) {
/*  305 */       hmax = (int)(maxCount2 * 1.5D);
/*  306 */       data2[mode] = hmax;
/*      */     }
/*  308 */     return IJIsoData(data2);
/*      */   }
/*      */ 
/*      */   int IJIsoData(int[] data)
/*      */   {
/*  314 */     int maxValue = data.length - 1;
/*      */ 
/*  316 */     int count0 = data[0];
/*  317 */     data[0] = 0;
/*  318 */     int countMax = data[maxValue];
/*  319 */     data[maxValue] = 0;
/*  320 */     int min = 0;
/*  321 */     while ((data[min] == 0) && (min < maxValue))
/*  322 */       min++;
/*  323 */     int max = maxValue;
/*  324 */     while ((data[max] == 0) && (max > 0))
/*  325 */       max--;
/*  326 */     if (min >= max) {
/*  327 */       data[0] = count0; data[maxValue] = countMax;
/*  328 */       int level = data.length / 2;
/*  329 */       return level;
/*      */     }
/*  331 */     int movingIndex = min;
/*  332 */     int inc = Math.max(max / 40, 1);
/*      */     double result;
/*      */     do
/*      */     {
/*      */       double sum4;
/*      */       double sum3;
/*      */       double sum2;
/*  334 */       double sum1 = sum2 = sum3 = sum4 = 0.0D;
/*  335 */       for (int i = min; i <= movingIndex; i++) {
/*  336 */         sum1 += i * data[i];
/*  337 */         sum2 += data[i];
/*      */       }
/*  339 */       for (int i = movingIndex + 1; i <= max; i++) {
/*  340 */         sum3 += i * data[i];
/*  341 */         sum4 += data[i];
/*      */       }
/*  343 */       result = (sum1 / sum2 + sum3 / sum4) / 2.0D;
/*  344 */       movingIndex++;
/*  345 */     }while ((movingIndex + 1 <= result) && (movingIndex < max - 1));
/*  346 */     data[0] = count0; data[maxValue] = countMax;
/*  347 */     int level = (int)Math.round(result);
/*  348 */     return level;
/*      */   }
/*      */ 
/*      */   int Li(int[] data)
/*      */   {
/*  378 */     double tolerance = 0.5D;
/*  379 */     double num_pixels = 0.0D;
/*  380 */     for (int ih = 0; ih < 256; ih++) {
/*  381 */       num_pixels += data[ih];
/*      */     }
/*      */ 
/*  384 */     double mean = 0.0D;
/*  385 */     for (int ih = 1; ih < 256; ih++)
/*  386 */       mean += ih * data[ih]; mean /= num_pixels;
/*      */ 
/*  389 */     double new_thresh = mean;
/*      */     double old_thresh;
/*      */     int threshold;
/*      */     do { old_thresh = new_thresh;
/*  393 */       threshold = (int)(old_thresh + 0.5D);
/*      */ 
/*  396 */       double sum_back = 0.0D;
/*  397 */       double num_back = 0.0D;
/*  398 */       for (int ih = 0; ih <= threshold; ih++) {
/*  399 */         sum_back += ih * data[ih];
/*  400 */         num_back += data[ih];
/*      */       }
/*  402 */       double mean_back = num_back == 0.0D ? 0.0D : sum_back / num_back;
/*      */ 
/*  404 */       double sum_obj = 0.0D;
/*  405 */       double num_obj = 0.0D;
/*  406 */       for (int ih = threshold + 1; ih < 256; ih++) {
/*  407 */         sum_obj += ih * data[ih];
/*  408 */         num_obj += data[ih];
/*      */       }
/*  410 */       double mean_obj = num_obj == 0.0D ? 0.0D : sum_obj / num_obj;
/*      */ 
/*  420 */       double temp = (mean_back - mean_obj) / (Math.log(mean_back) - Math.log(mean_obj));
/*      */ 
/*  422 */       if (temp < -2.220446049250313E-16D)
/*  423 */         new_thresh = (int)(temp - 0.5D);
/*      */       else {
/*  425 */         new_thresh = (int)(temp + 0.5D);
/*      */       }
/*      */     }
/*      */ 
/*  429 */     while (Math.abs(new_thresh - old_thresh) > tolerance);
/*  430 */     return threshold;
/*      */   }
/*      */ 
/*      */   int MaxEntropy(int[] data)
/*      */   {
/*  441 */     int threshold = -1;
/*      */ 
/*  449 */     double[] norm_histo = new double[256];
/*  450 */     double[] P1 = new double[256];
/*  451 */     double[] P2 = new double[256];
/*      */ 
/*  453 */     double total = 0.0D;
/*  454 */     for (int ih = 0; ih < 256; ih++) {
/*  455 */       total += data[ih];
/*      */     }
/*  457 */     for (ih = 0; ih < 256; ih++) {
/*  458 */       norm_histo[ih] = (data[ih] / total);
/*      */     }
/*  460 */     P1[0] = norm_histo[0];
/*  461 */     P2[0] = (1.0D - P1[0]);
/*  462 */     for (ih = 1; ih < 256; ih++) {
/*  463 */       P1[ih] = (P1[(ih - 1)] + norm_histo[ih]);
/*  464 */       P2[ih] = (1.0D - P1[ih]);
/*      */     }
/*      */ 
/*  468 */     int first_bin = 0;
/*  469 */     for (ih = 0; ih < 256; ih++) {
/*  470 */       if (Math.abs(P1[ih]) >= 2.220446049250313E-16D) {
/*  471 */         first_bin = ih;
/*  472 */         break;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  477 */     int last_bin = 255;
/*  478 */     for (ih = 255; ih >= first_bin; ih--) {
/*  479 */       if (Math.abs(P2[ih]) >= 2.220446049250313E-16D) {
/*  480 */         last_bin = ih;
/*  481 */         break;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  487 */     double max_ent = 4.9E-324D;
/*      */ 
/*  489 */     for (int it = first_bin; it <= last_bin; it++)
/*      */     {
/*  491 */       double ent_back = 0.0D;
/*  492 */       for (ih = 0; ih <= it; ih++) {
/*  493 */         if (data[ih] != 0) {
/*  494 */           ent_back -= norm_histo[ih] / P1[it] * Math.log(norm_histo[ih] / P1[it]);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  499 */       double ent_obj = 0.0D;
/*  500 */       for (ih = it + 1; ih < 256; ih++) {
/*  501 */         if (data[ih] != 0) {
/*  502 */           ent_obj -= norm_histo[ih] / P2[it] * Math.log(norm_histo[ih] / P2[it]);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  507 */       double tot_ent = ent_back + ent_obj;
/*      */ 
/*  510 */       if (max_ent < tot_ent) {
/*  511 */         max_ent = tot_ent;
/*  512 */         threshold = it;
/*      */       }
/*      */     }
/*  515 */     return threshold;
/*      */   }
/*      */ 
/*      */   int Mean(int[] data)
/*      */   {
/*  523 */     int threshold = -1;
/*  524 */     double tot = 0.0D; double sum = 0.0D;
/*  525 */     for (int i = 0; i < 256; i++) {
/*  526 */       tot += data[i];
/*  527 */       sum += i * data[i];
/*      */     }
/*  529 */     threshold = (int)Math.floor(sum / tot);
/*  530 */     return threshold;
/*      */   }
/*      */ 
/*      */   int MinErrorI(int[] data)
/*      */   {
/*  541 */     int threshold = Mean(data);
/*  542 */     int Tprev = -2;
/*      */ 
/*  545 */     while (threshold != Tprev)
/*      */     {
/*  547 */       double mu = B(data, threshold) / A(data, threshold);
/*  548 */       double nu = (B(data, 255) - B(data, threshold)) / (A(data, 255) - A(data, threshold));
/*  549 */       double p = A(data, threshold) / A(data, 255);
/*  550 */       double q = (A(data, 255) - A(data, threshold)) / A(data, 255);
/*  551 */       double sigma2 = C(data, threshold) / A(data, threshold) - mu * mu;
/*  552 */       double tau2 = (C(data, 255) - C(data, threshold)) / (A(data, 255) - A(data, threshold)) - nu * nu;
/*      */ 
/*  555 */       double w0 = 1.0D / sigma2 - 1.0D / tau2;
/*  556 */       double w1 = mu / sigma2 - nu / tau2;
/*  557 */       double w2 = mu * mu / sigma2 - nu * nu / tau2 + Math.log10(sigma2 * (q * q) / (tau2 * (p * p)));
/*      */ 
/*  560 */       double sqterm = w1 * w1 - w0 * w2;
/*  561 */       if (sqterm < 0.0D) {
/*  562 */         IJ.log("MinError(I): not converging.");
/*  563 */         return threshold;
/*      */       }
/*      */ 
/*  567 */       Tprev = threshold;
/*  568 */       double temp = (w1 + Math.sqrt(sqterm)) / w0;
/*      */ 
/*  570 */       if (Double.isNaN(temp)) {
/*  571 */         IJ.log("MinError(I): NaN, not converging.");
/*  572 */         threshold = Tprev;
/*      */       }
/*      */       else {
/*  575 */         threshold = (int)Math.floor(temp);
/*      */       }
/*      */     }
/*  578 */     return threshold;
/*      */   }
/*      */ 
/*      */   double A(int[] y, int j) {
/*  582 */     double x = 0.0D;
/*  583 */     for (int i = 0; i <= j; i++)
/*  584 */       x += y[i];
/*  585 */     return x;
/*      */   }
/*      */ 
/*      */   double B(int[] y, int j) {
/*  589 */     double x = 0.0D;
/*  590 */     for (int i = 0; i <= j; i++)
/*  591 */       x += i * y[i];
/*  592 */     return x;
/*      */   }
/*      */ 
/*      */   double C(int[] y, int j) {
/*  596 */     double x = 0.0D;
/*  597 */     for (int i = 0; i <= j; i++)
/*  598 */       x += i * i * y[i];
/*  599 */     return x;
/*      */   }
/*      */ 
/*      */   int Minimum(int[] data)
/*      */   {
/*  615 */     int iter = 0;
/*  616 */     int threshold = -1;
/*  617 */     double[] iHisto = new double[256];
/*      */ 
/*  619 */     for (int i = 0; i < 256; i++) {
/*  620 */       iHisto[i] = data[i];
/*      */     }
/*  622 */     double[] tHisto = iHisto;
/*      */ 
/*  624 */     while (!bimodalTest(iHisto))
/*      */     {
/*  626 */       for (int i = 1; i < 255; i++)
/*  627 */         tHisto[i] = ((iHisto[(i - 1)] + iHisto[i] + iHisto[(i + 1)]) / 3.0D);
/*  628 */       tHisto[0] = ((iHisto[0] + iHisto[1]) / 3.0D);
/*  629 */       tHisto['ÿ'] = ((iHisto['þ'] + iHisto['ÿ']) / 3.0D);
/*  630 */       iHisto = tHisto;
/*  631 */       iter++;
/*  632 */       if (iter > 10000) {
/*  633 */         threshold = -1;
/*  634 */         IJ.log("Minimum: threshold not found after 10000 iterations.");
/*  635 */         return threshold;
/*      */       }
/*      */     }
/*      */ 
/*  639 */     for (int i = 1; i < 255; i++)
/*      */     {
/*  641 */       if ((iHisto[(i - 1)] > iHisto[i]) && (iHisto[(i + 1)] >= iHisto[i]))
/*  642 */         threshold = i;
/*      */     }
/*  644 */     return threshold;
/*      */   }
/*      */ 
/*      */   int Moments(int[] data)
/*      */   {
/*  655 */     double total = 0.0D;
/*  656 */     double m0 = 1.0D; double m1 = 0.0D; double m2 = 0.0D; double m3 = 0.0D; double sum = 0.0D; double p0 = 0.0D;
/*      */ 
/*  658 */     int threshold = -1;
/*      */ 
/*  660 */     double[] histo = new double[256];
/*      */ 
/*  662 */     for (int i = 0; i < 256; i++) {
/*  663 */       total += data[i];
/*      */     }
/*  665 */     for (int i = 0; i < 256; i++) {
/*  666 */       histo[i] = (data[i] / total);
/*      */     }
/*      */ 
/*  669 */     for (int i = 0; i < 256; i++) {
/*  670 */       double di = i;
/*  671 */       m1 += di * histo[i];
/*  672 */       m2 += di * di * histo[i];
/*  673 */       m3 += di * di * di * histo[i];
/*      */     }
/*      */ 
/*  680 */     double cd = m0 * m2 - m1 * m1;
/*  681 */     double c0 = (-m2 * m2 + m1 * m3) / cd;
/*  682 */     double c1 = (m0 * -m3 + m2 * m1) / cd;
/*  683 */     double z0 = 0.5D * (-c1 - Math.sqrt(c1 * c1 - 4.0D * c0));
/*  684 */     double z1 = 0.5D * (-c1 + Math.sqrt(c1 * c1 - 4.0D * c0));
/*  685 */     p0 = (z1 - m1) / (z1 - z0);
/*      */ 
/*  689 */     sum = 0.0D;
/*  690 */     for (int i = 0; i < 256; i++) {
/*  691 */       sum += histo[i];
/*  692 */       if (sum > p0) {
/*  693 */         threshold = i;
/*  694 */         break;
/*      */       }
/*      */     }
/*  697 */     return threshold;
/*      */   }
/*      */ 
/*      */   int Otsu(int[] data)
/*      */   {
/*  709 */     double L = 256.0D;
/*      */     double N;
/*  712 */     double S = N = 0.0D;
/*  713 */     for (int k = 0; k < L; k++) {
/*  714 */       S += k * data[k];
/*  715 */       N += data[k];
/*      */     }
/*      */ 
/*  718 */     double Sk = 0.0D;
/*  719 */     double N1 = data[0];
/*  720 */     double BCV = 0.0D;
/*  721 */     double BCVmax = 0.0D;
/*  722 */     int kStar = 0;
/*      */ 
/*  726 */     for (k = 1; k < L - 1.0D; k++) {
/*  727 */       Sk += k * data[k];
/*  728 */       N1 += data[k];
/*      */ 
/*  732 */       double denom = N1 * (N - N1);
/*      */ 
/*  734 */       if (denom != 0.0D)
/*      */       {
/*  736 */         double num = N1 / N * S - Sk;
/*  737 */         BCV = num * num / denom;
/*      */       }
/*      */       else {
/*  740 */         BCV = 0.0D;
/*      */       }
/*  742 */       if (BCV >= BCVmax) {
/*  743 */         BCVmax = BCV;
/*  744 */         kStar = k;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  749 */     return kStar;
/*      */   }
/*      */ 
/*      */   int Percentile(int[] data)
/*      */   {
/*  761 */     int iter = 0;
/*  762 */     int threshold = -1;
/*  763 */     double ptile = 0.5D;
/*  764 */     double[] avec = new double[256];
/*      */ 
/*  766 */     for (int i = 0; i < 256; i++) {
/*  767 */       avec[i] = 0.0D;
/*      */     }
/*  769 */     double total = partialSum(data, 255);
/*  770 */     double temp = 1.0D;
/*  771 */     for (int i = 0; i < 256; i++) {
/*  772 */       avec[i] = Math.abs(partialSum(data, i) / total - ptile);
/*      */ 
/*  774 */       if (avec[i] < temp) {
/*  775 */         temp = avec[i];
/*  776 */         threshold = i;
/*      */       }
/*      */     }
/*  779 */     return threshold;
/*      */   }
/*      */ 
/*      */   double partialSum(int[] y, int j)
/*      */   {
/*  784 */     double x = 0.0D;
/*  785 */     for (int i = 0; i <= j; i++)
/*  786 */       x += y[i];
/*  787 */     return x;
/*      */   }
/*      */ 
/*      */   int RenyiEntropy(int[] data)
/*      */   {
/*  815 */     double[] norm_histo = new double[256];
/*  816 */     double[] P1 = new double[256];
/*  817 */     double[] P2 = new double[256];
/*      */ 
/*  819 */     double total = 0.0D;
/*  820 */     for (int ih = 0; ih < 256; ih++) {
/*  821 */       total += data[ih];
/*      */     }
/*  823 */     for (ih = 0; ih < 256; ih++) {
/*  824 */       norm_histo[ih] = (data[ih] / total);
/*      */     }
/*  826 */     P1[0] = norm_histo[0];
/*  827 */     P2[0] = (1.0D - P1[0]);
/*  828 */     for (ih = 1; ih < 256; ih++) {
/*  829 */       P1[ih] = (P1[(ih - 1)] + norm_histo[ih]);
/*  830 */       P2[ih] = (1.0D - P1[ih]);
/*      */     }
/*      */ 
/*  834 */     int first_bin = 0;
/*  835 */     for (ih = 0; ih < 256; ih++) {
/*  836 */       if (Math.abs(P1[ih]) >= 2.220446049250313E-16D) {
/*  837 */         first_bin = ih;
/*  838 */         break;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  843 */     int last_bin = 255;
/*  844 */     for (ih = 255; ih >= first_bin; ih--) {
/*  845 */       if (Math.abs(P2[ih]) >= 2.220446049250313E-16D) {
/*  846 */         last_bin = ih;
/*  847 */         break;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  856 */     int threshold = 0;
/*  857 */     double max_ent = 0.0D;
/*      */ 
/*  859 */     for (int it = first_bin; it <= last_bin; it++)
/*      */     {
/*  861 */       double ent_back = 0.0D;
/*  862 */       for (ih = 0; ih <= it; ih++) {
/*  863 */         if (data[ih] != 0) {
/*  864 */           ent_back -= norm_histo[ih] / P1[it] * Math.log(norm_histo[ih] / P1[it]);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  869 */       double ent_obj = 0.0D;
/*  870 */       for (ih = it + 1; ih < 256; ih++) {
/*  871 */         if (data[ih] != 0) {
/*  872 */           ent_obj -= norm_histo[ih] / P2[it] * Math.log(norm_histo[ih] / P2[it]);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  877 */       double tot_ent = ent_back + ent_obj;
/*      */ 
/*  881 */       if (max_ent < tot_ent) {
/*  882 */         max_ent = tot_ent;
/*  883 */         threshold = it;
/*      */       }
/*      */     }
/*  886 */     int t_star2 = threshold;
/*      */ 
/*  889 */     threshold = 0;
/*  890 */     max_ent = 0.0D;
/*  891 */     double alpha = 0.5D;
/*  892 */     double term = 1.0D / (1.0D - alpha);
/*  893 */     for (it = first_bin; it <= last_bin; it++)
/*      */     {
/*  895 */       double ent_back = 0.0D;
/*  896 */       for (ih = 0; ih <= it; ih++) {
/*  897 */         ent_back += Math.sqrt(norm_histo[ih] / P1[it]);
/*      */       }
/*      */ 
/*  900 */       double ent_obj = 0.0D;
/*  901 */       for (ih = it + 1; ih < 256; ih++) {
/*  902 */         ent_obj += Math.sqrt(norm_histo[ih] / P2[it]);
/*      */       }
/*      */ 
/*  905 */       double tot_ent = term * (ent_back * ent_obj > 0.0D ? Math.log(ent_back * ent_obj) : 0.0D);
/*      */ 
/*  907 */       if (tot_ent > max_ent) {
/*  908 */         max_ent = tot_ent;
/*  909 */         threshold = it;
/*      */       }
/*      */     }
/*      */ 
/*  913 */     int t_star1 = threshold;
/*      */ 
/*  915 */     threshold = 0;
/*  916 */     max_ent = 0.0D;
/*  917 */     alpha = 2.0D;
/*  918 */     term = 1.0D / (1.0D - alpha);
/*  919 */     for (it = first_bin; it <= last_bin; it++)
/*      */     {
/*  921 */       double ent_back = 0.0D;
/*  922 */       for (ih = 0; ih <= it; ih++) {
/*  923 */         ent_back += norm_histo[ih] * norm_histo[ih] / (P1[it] * P1[it]);
/*      */       }
/*      */ 
/*  926 */       double ent_obj = 0.0D;
/*  927 */       for (ih = it + 1; ih < 256; ih++) {
/*  928 */         ent_obj += norm_histo[ih] * norm_histo[ih] / (P2[it] * P2[it]);
/*      */       }
/*      */ 
/*  931 */       double tot_ent = term * (ent_back * ent_obj > 0.0D ? Math.log(ent_back * ent_obj) : 0.0D);
/*      */ 
/*  933 */       if (tot_ent > max_ent) {
/*  934 */         max_ent = tot_ent;
/*  935 */         threshold = it;
/*      */       }
/*      */     }
/*      */ 
/*  939 */     int t_star3 = threshold;
/*      */ 
/*  942 */     if (t_star2 < t_star1) {
/*  943 */       int tmp_var = t_star1;
/*  944 */       t_star1 = t_star2;
/*  945 */       t_star2 = tmp_var;
/*      */     }
/*  947 */     if (t_star3 < t_star2) {
/*  948 */       int tmp_var = t_star2;
/*  949 */       t_star2 = t_star3;
/*  950 */       t_star3 = tmp_var;
/*      */     }
/*  952 */     if (t_star2 < t_star1) {
/*  953 */       int tmp_var = t_star1;
/*  954 */       t_star1 = t_star2;
/*  955 */       t_star2 = tmp_var;
/*      */     }
/*      */     int beta3;
/*      */     int beta1;
/*      */     int beta2;
/*      */     int beta3;
/*  959 */     if (Math.abs(t_star1 - t_star2) <= 5)
/*      */     {
/*      */       int beta3;
/*  960 */       if (Math.abs(t_star2 - t_star3) <= 5) {
/*  961 */         int beta1 = 1;
/*  962 */         int beta2 = 2;
/*  963 */         beta3 = 1;
/*      */       }
/*      */       else {
/*  966 */         int beta1 = 0;
/*  967 */         int beta2 = 1;
/*  968 */         beta3 = 3;
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*      */       int beta3;
/*  972 */       if (Math.abs(t_star2 - t_star3) <= 5) {
/*  973 */         int beta1 = 3;
/*  974 */         int beta2 = 1;
/*  975 */         beta3 = 0;
/*      */       }
/*      */       else {
/*  978 */         beta1 = 1;
/*  979 */         beta2 = 2;
/*  980 */         beta3 = 1;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  985 */     double omega = P1[t_star3] - P1[t_star1];
/*  986 */     int opt_threshold = (int)(t_star1 * (P1[t_star1] + 0.25D * omega * beta1) + 0.25D * t_star2 * omega * beta2 + t_star3 * (P2[t_star3] + 0.25D * omega * beta3));
/*      */ 
/*  988 */     return opt_threshold;
/*      */   }
/*      */ 
/*      */   int Shanbhag(int[] data)
/*      */   {
/* 1005 */     double[] norm_histo = new double[256];
/* 1006 */     double[] P1 = new double[256];
/* 1007 */     double[] P2 = new double[256];
/*      */ 
/* 1009 */     double total = 0.0D;
/* 1010 */     for (int ih = 0; ih < 256; ih++) {
/* 1011 */       total += data[ih];
/*      */     }
/* 1013 */     for (ih = 0; ih < 256; ih++) {
/* 1014 */       norm_histo[ih] = (data[ih] / total);
/*      */     }
/* 1016 */     P1[0] = norm_histo[0];
/* 1017 */     P2[0] = (1.0D - P1[0]);
/* 1018 */     for (ih = 1; ih < 256; ih++) {
/* 1019 */       P1[ih] = (P1[(ih - 1)] + norm_histo[ih]);
/* 1020 */       P2[ih] = (1.0D - P1[ih]);
/*      */     }
/*      */ 
/* 1024 */     int first_bin = 0;
/* 1025 */     for (ih = 0; ih < 256; ih++) {
/* 1026 */       if (Math.abs(P1[ih]) >= 2.220446049250313E-16D) {
/* 1027 */         first_bin = ih;
/* 1028 */         break;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1033 */     int last_bin = 255;
/* 1034 */     for (ih = 255; ih >= first_bin; ih--) {
/* 1035 */       if (Math.abs(P2[ih]) >= 2.220446049250313E-16D) {
/* 1036 */         last_bin = ih;
/* 1037 */         break;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1043 */     int threshold = -1;
/* 1044 */     double min_ent = 1.7976931348623157E+308D;
/*      */ 
/* 1046 */     for (int it = first_bin; it <= last_bin; it++)
/*      */     {
/* 1048 */       double ent_back = 0.0D;
/* 1049 */       double term = 0.5D / P1[it];
/* 1050 */       for (ih = 1; ih <= it; ih++) {
/* 1051 */         ent_back -= norm_histo[ih] * Math.log(1.0D - term * P1[(ih - 1)]);
/*      */       }
/* 1053 */       ent_back *= term;
/*      */ 
/* 1056 */       double ent_obj = 0.0D;
/* 1057 */       term = 0.5D / P2[it];
/* 1058 */       for (ih = it + 1; ih < 256; ih++) {
/* 1059 */         ent_obj -= norm_histo[ih] * Math.log(1.0D - term * P2[ih]);
/*      */       }
/* 1061 */       ent_obj *= term;
/*      */ 
/* 1064 */       double tot_ent = Math.abs(ent_back - ent_obj);
/*      */ 
/* 1066 */       if (tot_ent < min_ent) {
/* 1067 */         min_ent = tot_ent;
/* 1068 */         threshold = it;
/*      */       }
/*      */     }
/* 1071 */     return threshold;
/*      */   }
/*      */ 
/*      */   int Triangle(int[] data)
/*      */   {
/* 1083 */     int min = 0; int dmax = 0; int max = 0; int min2 = 0;
/* 1084 */     for (int i = 0; i < data.length; i++) {
/* 1085 */       if (data[i] > 0) {
/* 1086 */         min = i;
/* 1087 */         break;
/*      */       }
/*      */     }
/* 1090 */     if (min > 0) min--;
/*      */ 
/* 1097 */     for (int i = 255; i > 0; i--) {
/* 1098 */       if (data[i] > 0) {
/* 1099 */         min2 = i;
/* 1100 */         break;
/*      */       }
/*      */     }
/* 1103 */     if (min2 < 255) min2++;
/*      */ 
/* 1105 */     for (int i = 0; i < 256; i++) {
/* 1106 */       if (data[i] > dmax) {
/* 1107 */         max = i;
/* 1108 */         dmax = data[i];
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1113 */     boolean inverted = false;
/* 1114 */     if (max - min < min2 - max)
/*      */     {
/* 1117 */       inverted = true;
/* 1118 */       int left = 0;
/* 1119 */       int right = 255;
/* 1120 */       while (left < right)
/*      */       {
/* 1122 */         int temp = data[left];
/* 1123 */         data[left] = data[right];
/* 1124 */         data[right] = temp;
/*      */ 
/* 1126 */         left++;
/* 1127 */         right--;
/*      */       }
/* 1129 */       min = 255 - min2;
/* 1130 */       max = 255 - max;
/*      */     }
/*      */ 
/* 1133 */     if (min == max)
/*      */     {
/* 1135 */       return min;
/*      */     }
/*      */ 
/* 1141 */     double nx = data[max];
/* 1142 */     double ny = min - max;
/* 1143 */     double d = Math.sqrt(nx * nx + ny * ny);
/* 1144 */     nx /= d;
/* 1145 */     ny /= d;
/* 1146 */     d = nx * min + ny * data[min];
/*      */ 
/* 1149 */     int split = min;
/* 1150 */     double splitDistance = 0.0D;
/* 1151 */     for (int i = min + 1; i <= max; i++) {
/* 1152 */       double newDistance = nx * i + ny * data[i] - d;
/* 1153 */       if (newDistance > splitDistance) {
/* 1154 */         split = i;
/* 1155 */         splitDistance = newDistance;
/*      */       }
/*      */     }
/* 1158 */     split--;
/*      */ 
/* 1160 */     if (inverted)
/*      */     {
/* 1162 */       int left = 0;
/* 1163 */       int right = 255;
/* 1164 */       while (left < right) {
/* 1165 */         int temp = data[left];
/* 1166 */         data[left] = data[right];
/* 1167 */         data[right] = temp;
/* 1168 */         left++;
/* 1169 */         right--;
/*      */       }
/* 1171 */       return 255 - split;
/*      */     }
/*      */ 
/* 1174 */     return split;
/*      */   }
/*      */ 
/*      */   int Yen(int[] data)
/*      */   {
/* 1195 */     double[] norm_histo = new double[256];
/* 1196 */     double[] P1 = new double[256];
/* 1197 */     double[] P1_sq = new double[256];
/* 1198 */     double[] P2_sq = new double[256];
/*      */ 
/* 1200 */     double total = 0.0D;
/* 1201 */     for (int ih = 0; ih < 256; ih++) {
/* 1202 */       total += data[ih];
/*      */     }
/* 1204 */     for (ih = 0; ih < 256; ih++) {
/* 1205 */       norm_histo[ih] = (data[ih] / total);
/*      */     }
/* 1207 */     P1[0] = norm_histo[0];
/* 1208 */     for (ih = 1; ih < 256; ih++) {
/* 1209 */       P1[ih] = (P1[(ih - 1)] + norm_histo[ih]);
/*      */     }
/* 1211 */     norm_histo[0] *= norm_histo[0];
/* 1212 */     for (ih = 1; ih < 256; ih++) {
/* 1213 */       P1_sq[ih] = (P1_sq[(ih - 1)] + norm_histo[ih] * norm_histo[ih]);
/*      */     }
/* 1215 */     P2_sq['ÿ'] = 0.0D;
/* 1216 */     for (ih = 254; ih >= 0; ih--) {
/* 1217 */       P2_sq[ih] = (P2_sq[(ih + 1)] + norm_histo[(ih + 1)] * norm_histo[(ih + 1)]);
/*      */     }
/*      */ 
/* 1220 */     int threshold = -1;
/* 1221 */     double max_crit = 4.9E-324D;
/* 1222 */     for (int it = 0; it < 256; it++) {
/* 1223 */       double crit = -1.0D * (P1_sq[it] * P2_sq[it] > 0.0D ? Math.log(P1_sq[it] * P2_sq[it]) : 0.0D) + 2.0D * (P1[it] * (1.0D - P1[it]) > 0.0D ? Math.log(P1[it] * (1.0D - P1[it])) : 0.0D);
/* 1224 */       if (crit > max_crit) {
/* 1225 */         max_crit = crit;
/* 1226 */         threshold = it;
/*      */       }
/*      */     }
/* 1229 */     return threshold;
/*      */   }
/*      */ 
/*      */   public static enum Method
/*      */   {
/*   11 */     Default, 
/*   12 */     Huang, 
/*   13 */     Intermodes, 
/*   14 */     IsoData, 
/*   15 */     IJ_IsoData, 
/*   16 */     Li, 
/*   17 */     MaxEntropy, 
/*   18 */     Mean, 
/*   19 */     MinError, 
/*   20 */     Minimum, 
/*   21 */     Moments, 
/*   22 */     Otsu, 
/*   23 */     Percentile, 
/*   24 */     RenyiEntropy, 
/*   25 */     Shanbhag, 
/*   26 */     Triangle, 
/*   27 */     Yen;
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.process.AutoThresholder
 * JD-Core Version:    0.6.2
 */