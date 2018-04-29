/*    */ package leadtools.codecs;
/*    */ 
/*    */ public final class CodecsCodecInformation
/*    */ {
/*    */   private String _name;
/*    */   private String _extensionList;
/*    */   private int _flags;
/*    */ 
/*    */   public String getName()
/*    */   {
/* 16 */     return this._name;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 22 */     return getName();
/*    */   }
/*    */ 
/*    */   public String getExtensionList()
/*    */   {
/* 27 */     return this._extensionList;
/*    */   }
/*    */ 
/*    */   public void setExtensionList(String value) {
/* 31 */     this._extensionList = value;
/*    */   }
/*    */ 
/*    */   public boolean isPresent()
/*    */   {
/* 36 */     return Tools.isFlagged(this._flags, 4);
/*    */   }
/*    */ 
/*    */   public CodecsCodecLoadMode getLoadMode()
/*    */   {
/* 41 */     if (Tools.isFlagged(this._flags, 1))
/* 42 */       return CodecsCodecLoadMode.IGNORED;
/* 43 */     if (Tools.isFlagged(this._flags, 2)) {
/* 44 */       return CodecsCodecLoadMode.FIXED;
/*    */     }
/* 46 */     return CodecsCodecLoadMode.DYNAMIC;
/*    */   }
/*    */ 
/*    */   public void setLoadMode(CodecsCodecLoadMode value) {
/* 50 */     this._flags = Tools.setFlag1(this._flags, 1, false);
/* 51 */     this._flags = Tools.setFlag1(this._flags, 2, false);
/*    */ 
/* 53 */     switch (1.$SwitchMap$leadtools$codecs$CodecsCodecLoadMode[value.ordinal()])
/*    */     {
/*    */     case 1:
/* 56 */       this._flags = Tools.setFlag1(this._flags, 1, true);
/* 57 */       break;
/*    */     case 2:
/* 60 */       this._flags = Tools.setFlag1(this._flags, 2, true);
/* 61 */       break;
/*    */     }
/*    */   }
/*    */ 
/*    */   public boolean getCheckedByInformation()
/*    */   {
/* 70 */     return Tools.isFlagged(this._flags, 8);
/*    */   }
/*    */ 
/*    */   public void setCheckedByInformation(boolean value) {
/* 74 */     this._flags = Tools.setFlag1(this._flags, 8, value);
/*    */   }
/*    */ 
/*    */   public boolean isSlowInformation()
/*    */   {
/* 79 */     return Tools.isFlagged(this._flags, 16);
/*    */   }
/*    */ 
/*    */   public CodecsCodecInformation clone()
/*    */   {
/* 84 */     CodecsCodecInformation varCopy = new CodecsCodecInformation();
/*    */ 
/* 86 */     varCopy._name = this._name;
/* 87 */     varCopy._extensionList = this._extensionList;
/* 88 */     varCopy._flags = this._flags;
/*    */ 
/* 90 */     return varCopy;
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsCodecInformation
 * JD-Core Version:    0.6.2
 */