/*    */ package leadtools.internal;
/*    */ 
/*    */ import java.util.Calendar;
/*    */ import java.util.Date;
/*    */ import java.util.TimeZone;
/*    */ 
/*    */ public final class DateTools
/*    */ {
/*  8 */   public static final TimeZone utcTZ = TimeZone.getTimeZone("UTC");
/*    */ 
/*    */   public static long toLocalTime(long time, TimeZone to) {
/* 11 */     return convertTime(time, utcTZ, to);
/*    */   }
/*    */ 
/*    */   public static long toUTC(long time, TimeZone from) {
/* 15 */     return convertTime(time, from, utcTZ);
/*    */   }
/*    */ 
/*    */   public static long convertTime(long time, TimeZone from, TimeZone to) {
/* 19 */     return time + getTimeZoneOffset(time, from, to);
/*    */   }
/*    */ 
/*    */   public static Date toUniversalTime(Date date) {
/* 23 */     Calendar cal = Calendar.getInstance();
/* 24 */     cal.setTimeInMillis(toUTC(date.getTime(), TimeZone.getDefault()));
/* 25 */     Date newDate = cal.getTime();
/* 26 */     return newDate;
/*    */   }
/*    */ 
/*    */   public static Date toLocalTime(Date date) {
/* 30 */     Calendar cal = Calendar.getInstance();
/* 31 */     cal.setTimeInMillis(toLocalTime(date.getTime(), TimeZone.getDefault()));
/* 32 */     Date newDate = cal.getTime();
/* 33 */     return newDate;
/*    */   }
/*    */ 
/*    */   private static long getTimeZoneOffset(long time, TimeZone from, TimeZone to) {
/* 37 */     int fromOffset = from.getOffset(time);
/* 38 */     int toOffset = to.getOffset(time);
/* 39 */     int diff = 0;
/*    */ 
/* 41 */     if (fromOffset >= 0) {
/* 42 */       if (toOffset > 0) {
/* 43 */         toOffset = -1 * toOffset;
/*    */       }
/*    */       else {
/* 46 */         toOffset = Math.abs(toOffset);
/*    */       }
/* 48 */       diff = (fromOffset + toOffset) * -1;
/*    */     }
/*    */     else {
/* 51 */       if (toOffset <= 0) {
/* 52 */         toOffset = -1 * Math.abs(toOffset);
/*    */       }
/* 54 */       diff = Math.abs(fromOffset) + toOffset;
/*    */     }
/* 56 */     return diff;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.internal.DateTools
 * JD-Core Version:    0.6.2
 */