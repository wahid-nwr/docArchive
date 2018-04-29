/*    */ package ij.macro;
/*    */ 
/*    */ public class Symbol
/*    */   implements MacroConstants
/*    */ {
/*    */   public int type;
/*    */   public double value;
/*    */   public String str;
/*    */ 
/*    */   Symbol(int token, String str)
/*    */   {
/* 10 */     this.type = (token & 0xFFFF);
/* 11 */     this.str = str;
/*    */   }
/*    */ 
/*    */   Symbol(double value) {
/* 15 */     this.value = value;
/*    */   }
/*    */ 
/*    */   int getFunctionType() {
/* 19 */     int t = 0;
/* 20 */     if ((this.type >= 300) && (this.type < 1000))
/* 21 */       t = 134;
/* 22 */     else if ((this.type >= 1000) && (this.type < 2000))
/* 23 */       t = 135;
/* 24 */     else if ((this.type >= 2000) && (this.type < 3000))
/* 25 */       t = 136;
/* 26 */     else if ((this.type >= 3000) && (this.type < 4000))
/* 27 */       t = 137;
/* 28 */     return t;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 32 */     return this.type + " " + this.value + " " + this.str;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.macro.Symbol
 * JD-Core Version:    0.6.2
 */