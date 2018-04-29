/*    */ import ij.IJ;
/*    */ import ij.plugin.PlugIn;
/*    */ import javax.script.ScriptEngine;
/*    */ import javax.script.ScriptEngineManager;
/*    */ 
/*    */ public class JavaScriptEvaluator
/*    */   implements PlugIn, Runnable
/*    */ {
/*    */   Thread thread;
/*    */   String script;
/*    */ 
/*    */   public void run(String paramString)
/*    */   {
/* 19 */     if (paramString.equals("")) return;
/* 20 */     if (!IJ.isJava16()) {
/* 21 */       IJ.error("Java 1.6 or later required"); return;
/* 22 */     }this.script = paramString;
/* 23 */     this.thread = new Thread(this, "JavaScript");
/* 24 */     this.thread.setPriority(Math.max(this.thread.getPriority() - 2, 1));
/* 25 */     this.thread.start();
/*    */   }
/*    */ 
/*    */   public String run(String paramString1, String paramString2)
/*    */   {
/* 30 */     this.script = paramString1;
/* 31 */     run();
/* 32 */     return null;
/*    */   }
/*    */ 
/*    */   public void run() {
/*    */     try {
/* 37 */       ScriptEngineManager localScriptEngineManager = new ScriptEngineManager();
/* 38 */       ScriptEngine localObject = localScriptEngineManager.getEngineByName("ECMAScript");
/* 39 */       if (localObject == null) {
/* 40 */         IJ.error("Could not find JavaScript engine"); return;
/* 41 */       }((ScriptEngine)localObject).eval(this.script);
/*    */     } catch (Throwable localThrowable) {
/* 43 */       Object localObject = localThrowable.getMessage();
/* 44 */       if (((String)localObject).startsWith("sun.org.mozilla.javascript.internal.EcmaError: "))
/* 45 */         localObject = ((String)localObject).substring(47, ((String)localObject).length());
/* 46 */       if (((String)localObject).startsWith("sun.org.mozilla.javascript.internal.EvaluatorException"))
/* 47 */         localObject = "Error" + ((String)localObject).substring(54, ((String)localObject).length());
/* 48 */       if (((String)localObject).indexOf("Macro canceled") == -1)
/* 49 */         IJ.log((String)localObject);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     JavaScriptEvaluator
 * JD-Core Version:    0.6.2
 */