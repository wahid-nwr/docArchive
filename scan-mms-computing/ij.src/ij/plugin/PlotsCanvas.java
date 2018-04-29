/*     */ package ij.plugin;
/*     */ 
/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.Undo;
/*     */ import ij.gui.ImageCanvas;
/*     */ import ij.gui.Roi;
/*     */ import ij.gui.Toolbar;
/*     */ import ij.measure.Calibration;
/*     */ import ij.measure.ResultsTable;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ImageStatistics;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.MouseEvent;
/*     */ 
/*     */ class PlotsCanvas extends ImageCanvas
/*     */ {
/*     */   public static final int MAX_PEAKS = 200;
/* 496 */   double[] actual = { 428566.0D, 351368.0D, 233977.0D, 99413.0D, 60057.0D, 31382.0D, 14531.0D, 7843.0D, 2146.0D, 752.0D, 367.0D };
/*     */ 
/* 498 */   double[] measured = new double['È'];
/* 499 */   Rectangle[] rect = new Rectangle['È'];
/*     */   int counter;
/*     */   ResultsTable rt;
/*     */ 
/*     */   public PlotsCanvas(ImagePlus imp)
/*     */   {
/* 504 */     super(imp);
/*     */   }
/*     */ 
/*     */   public void mousePressed(MouseEvent e) {
/* 508 */     super.mousePressed(e);
/* 509 */     Roi roi = this.imp.getRoi();
/* 510 */     if (roi == null)
/* 511 */       return;
/* 512 */     if (roi.getType() == 5)
/* 513 */       Roi.setColor(Color.blue);
/*     */     else
/* 515 */       Roi.setColor(Color.yellow);
/* 516 */     if ((Toolbar.getToolId() != 8) || (IJ.spaceBarDown()))
/* 517 */       return;
/* 518 */     if (IJ.shiftKeyDown()) {
/* 519 */       IJ.showMessage("Gel Analyzer", "Unable to measure area because shift key is down.");
/* 520 */       this.imp.killRoi();
/* 521 */       this.counter = 0;
/* 522 */       return;
/*     */     }
/* 524 */     ImageStatistics s = this.imp.getStatistics();
/* 525 */     if (this.counter == 0) {
/* 526 */       this.rt = ResultsTable.getResultsTable();
/* 527 */       this.rt.reset();
/*     */     }
/*     */ 
/* 530 */     double perimeter = roi.getLength();
/* 531 */     String error = "";
/* 532 */     double circularity = 12.566370614359172D * (s.pixelCount / (perimeter * perimeter));
/* 533 */     if (circularity < 0.025D)
/* 534 */       error = " (error?)";
/* 535 */     double area = s.pixelCount + perimeter / 2.0D;
/* 536 */     Calibration cal = this.imp.getCalibration();
/* 537 */     area = area * cal.pixelWidth * cal.pixelHeight;
/* 538 */     this.rect[this.counter] = roi.getBounds();
/*     */ 
/* 543 */     int places = cal.scaled() ? 3 : 0;
/* 544 */     this.rt.incrementCounter();
/* 545 */     this.rt.addValue("Area", area);
/* 546 */     this.rt.show("Results");
/*     */ 
/* 548 */     this.measured[this.counter] = area;
/* 549 */     if (this.counter < 200)
/* 550 */       this.counter += 1;
/*     */   }
/*     */ 
/*     */   public void mouseReleased(MouseEvent e) {
/* 554 */     super.mouseReleased(e);
/* 555 */     Roi roi = this.imp.getRoi();
/* 556 */     if ((roi != null) && (roi.getType() == 5)) {
/* 557 */       Undo.setup(1, this.imp);
/* 558 */       this.imp.getProcessor().snapshot();
/* 559 */       roi.drawPixels();
/* 560 */       this.imp.updateAndDraw();
/* 561 */       this.imp.killRoi();
/*     */     }
/*     */   }
/*     */ 
/*     */   void reset() {
/* 566 */     this.counter = 0;
/*     */   }
/*     */ 
/*     */   void labelPeaks() {
/* 570 */     this.imp.killRoi();
/* 571 */     double total = 0.0D;
/* 572 */     for (int i = 0; i < this.counter; i++)
/* 573 */       total += this.measured[i];
/* 574 */     ImageProcessor ip = this.imp.getProcessor();
/* 575 */     ip.setFont(new Font("SansSerif", 0, 9));
/* 576 */     for (int i = 0; i < this.counter; i++) {
/* 577 */       Rectangle r = this.rect[i];
/*     */       String s;
/*     */       String s;
/* 579 */       if (GelAnalyzer.labelWithPercentages)
/* 580 */         s = IJ.d2s(this.measured[i] / total * 100.0D, 2);
/*     */       else
/* 582 */         s = IJ.d2s(this.measured[i], 0);
/* 583 */       int swidth = ip.getStringWidth(s);
/* 584 */       int x = r.x + r.width / 2 - swidth / 2;
/* 585 */       int y = r.y + r.height * 3 / 4 + 9;
/* 586 */       int[] data = new int[swidth];
/* 587 */       ip.getRow(x, y, data, swidth);
/* 588 */       boolean fits = true;
/* 589 */       for (int j = 0; j < swidth; j++)
/* 590 */         if (data[j] != 255) {
/* 591 */           fits = false;
/* 592 */           break;
/*     */         }
/* 594 */       fits = (fits) && (this.measured[i] > 500.0D);
/* 595 */       if (r.height >= GelAnalyzer.plotHeight - 11)
/* 596 */         fits = true;
/* 597 */       if (!fits)
/* 598 */         y = r.y - 2;
/* 599 */       ip.drawString(s, x, y);
/*     */     }
/*     */ 
/* 602 */     this.imp.updateAndDraw();
/* 603 */     displayPercentages();
/*     */ 
/* 605 */     reset();
/*     */   }
/*     */ 
/*     */   void displayPercentages() {
/* 609 */     ResultsTable rt = ResultsTable.getResultsTable();
/* 610 */     rt.reset();
/*     */ 
/* 612 */     double total = 0.0D;
/* 613 */     for (int i = 0; i < this.counter; i++)
/* 614 */       total += this.measured[i];
/* 615 */     if ((IJ.debugMode) && (this.counter == this.actual.length)) {
/* 616 */       debug();
/* 617 */       return;
/*     */     }
/* 619 */     for (int i = 0; i < this.counter; i++) {
/* 620 */       double percent = this.measured[i] / total * 100.0D;
/* 621 */       rt.incrementCounter();
/* 622 */       rt.addValue("Area", this.measured[i]);
/* 623 */       rt.addValue("Percent", percent);
/*     */     }
/*     */ 
/* 626 */     rt.show("Results");
/*     */   }
/*     */ 
/*     */   void debug() {
/* 630 */     for (int i = 0; i < this.counter; i++) {
/* 631 */       double a = this.actual[i] / this.actual[0] * 100.0D;
/* 632 */       double m = this.measured[i] / this.measured[0] * 100.0D;
/* 633 */       IJ.write(IJ.d2s(a, 4) + " " + IJ.d2s(m, 4) + " " + IJ.d2s((m - a) / m * 100.0D, 4));
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.plugin.PlotsCanvas
 * JD-Core Version:    0.6.2
 */