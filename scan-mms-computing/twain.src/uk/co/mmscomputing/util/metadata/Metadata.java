/*    */ package uk.co.mmscomputing.util.metadata;
/*    */ 
/*    */ import java.util.Enumeration;
/*    */ import java.util.Vector;
/*    */ import uk.co.mmscomputing.util.configuration.ConfigurationMap;
/*    */ 
/*    */ public class Metadata extends ConfigurationMap
/*    */ {
/*  8 */   private Vector listeners = new Vector();
/*    */ 
/*    */   public Metadata()
/*    */   {
/*    */   }
/*    */ 
/*    */   public Metadata(Class paramClass) {
/* 15 */     super(paramClass);
/*    */   }
/*    */ 
/*    */   public void putString(String paramString1, String paramString2) {
/* 19 */     put(paramString1, paramString2);
/*    */   }
/*    */ 
/*    */   public void putBoolean(String paramString, boolean paramBoolean) {
/* 23 */     put(paramString, new String(Boolean.toString(paramBoolean)));
/*    */   }
/*    */ 
/*    */   public boolean getBoolean(String paramString) {
/* 27 */     String str = getString(paramString);
/* 28 */     return Boolean.getBoolean(str);
/*    */   }
/*    */ 
/*    */   public void putInt(String paramString, int paramInt) {
/* 32 */     put(paramString, new String(Integer.toString(paramInt)));
/*    */   }
/*    */ 
/*    */   public void addListener(MetadataListener paramMetadataListener) {
/* 36 */     this.listeners.add(paramMetadataListener);
/*    */   }
/*    */ 
/*    */   public void addListener(int paramInt, MetadataListener paramMetadataListener) {
/* 40 */     this.listeners.add(paramInt, paramMetadataListener);
/*    */   }
/*    */ 
/*    */   public void removeListener(MetadataListener paramMetadataListener)
/*    */   {
/* 48 */     this.listeners.remove(paramMetadataListener);
/*    */   }
/*    */ 
/*    */   public void fireListenerUpdate(Object paramObject) {
/* 52 */     for (Enumeration localEnumeration = this.listeners.elements(); localEnumeration.hasMoreElements(); ) {
/* 53 */       MetadataListener localMetadataListener = (MetadataListener)localEnumeration.nextElement();
/* 54 */       localMetadataListener.update(paramObject, this);
/*    */     }
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 59 */     String str = getClass().getName() + "\n";
/* 60 */     for (Enumeration localEnumeration = this.listeners.elements(); localEnumeration.hasMoreElements(); ) {
/* 61 */       MetadataListener localMetadataListener = (MetadataListener)localEnumeration.nextElement();
/* 62 */       str = str + localMetadataListener + "\n";
/*    */     }
/* 64 */     return str;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/twain.jar
 * Qualified Name:     uk.co.mmscomputing.util.metadata.Metadata
 * JD-Core Version:    0.6.2
 */