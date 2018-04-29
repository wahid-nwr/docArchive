/*     */ package leadtools.codecs;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.transform.Transformer;
/*     */ import javax.xml.transform.TransformerFactory;
/*     */ import javax.xml.transform.dom.DOMSource;
/*     */ import javax.xml.transform.stream.StreamResult;
/*     */ import javax.xml.xpath.XPath;
/*     */ import javax.xml.xpath.XPathConstants;
/*     */ import javax.xml.xpath.XPathExpression;
/*     */ import javax.xml.xpath.XPathFactory;
/*     */ import leadtools.LeadRect;
/*     */ import leadtools.LeadSize;
/*     */ import leadtools.RasterColor;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ 
/*     */ final class CodecsOptionsSerializer
/*     */ {
/*     */   public static void save(String fileName, OutputStream stream, CodecsOptions options)
/*     */     throws Exception
/*     */   {
/*  23 */     DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
/*  24 */     DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
/*  25 */     Document doc = docBuilder.newDocument();
/*  26 */     Element rootElement = doc.createElement("leadtools_raster_codecs");
/*  27 */     doc.appendChild(rootElement);
/*  28 */     doc.setXmlStandalone(true);
/*     */ 
/*  30 */     saveOptions(rootElement, options);
/*     */ 
/*  32 */     TransformerFactory transformerFactory = TransformerFactory.newInstance();
/*  33 */     Transformer transformer = transformerFactory.newTransformer();
/*  34 */     transformer.setOutputProperty("encoding", "UTF-8");
/*  35 */     transformer.setOutputProperty("indent", "yes");
/*  36 */     transformer.setOutputProperty("standalone", "yes");
/*  37 */     transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
/*  38 */     DOMSource source = new DOMSource(doc);
/*  39 */     StreamResult result = createWriter(fileName, stream);
/*     */ 
/*  44 */     transformer.transform(source, result);
/*     */   }
/*     */ 
/*     */   private static StreamResult createWriter(String fileName, OutputStream stream)
/*     */   {
/*     */     StreamResult writer;
/*     */     StreamResult writer;
/*  50 */     if (fileName != null)
/*  51 */       writer = new StreamResult(new File(fileName));
/*     */     else {
/*  53 */       writer = new StreamResult(stream);
/*     */     }
/*  55 */     return writer;
/*     */   }
/*     */ 
/*     */   private static void saveOptions(Element rootElement, CodecsOptions options) {
/*  59 */     Map dic = new HashMap();
/*  60 */     options.writeXml(dic);
/*  61 */     Document doc = rootElement.getOwnerDocument();
/*     */ 
/*  63 */     Element optionsElement = doc.createElement("options");
/*     */ 
/*  65 */     for (Map.Entry item : dic.entrySet()) {
/*  66 */       Element element = doc.createElement("option");
/*  67 */       element.setAttribute((String)item.getKey(), (String)item.getValue());
/*  68 */       optionsElement.appendChild(element);
/*     */     }
/*     */ 
/*  71 */     rootElement.appendChild(optionsElement);
/*     */   }
/*     */ 
/*     */   private static String getOption(Map<String, String> dic, String key) {
/*  75 */     if ((dic.size() > 0) && (dic.containsKey(key))) {
/*  76 */       String value = (String)dic.get(key);
/*  77 */       dic.remove(key);
/*  78 */       return value;
/*     */     }
/*     */ 
/*  81 */     return null;
/*     */   }
/*     */ 
/*     */   public static void writeOption(Map<String, String> dic, String key, boolean value) {
/*  85 */     dic.put(key, value ? "true" : "false");
/*     */   }
/*     */ 
/*     */   public static boolean readOption(Map<String, String> dic, String key, boolean defaultValue) {
/*  89 */     String value = getOption(dic, key);
/*  90 */     if (value != null) {
/*  91 */       return (value != null) && ((value.compareToIgnoreCase("true") == 0) || (value.compareToIgnoreCase("1") == 0));
/*     */     }
/*     */ 
/*  94 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public static void writeOption(Map<String, String> dic, String key, int value) {
/*  98 */     dic.put(key, Integer.toString(value));
/*     */   }
/*     */ 
/*     */   private static boolean tryParseInteger(String value, ParseResult result)
/*     */   {
/*     */     try
/*     */     {
/* 109 */       result.value = Integer.valueOf(Integer.parseInt(value));
/* 110 */       result.ok = true;
/*     */     }
/*     */     catch (NumberFormatException e) {
/* 113 */       result.ok = false;
/*     */     }
/*     */ 
/* 116 */     return result.ok;
/*     */   }
/*     */ 
/*     */   private static boolean tryParseLong(String value, ParseResult result) {
/*     */     try {
/* 121 */       result.value = Long.valueOf(Long.parseLong(value));
/* 122 */       result.ok = true;
/*     */     }
/*     */     catch (NumberFormatException e) {
/* 125 */       result.ok = false;
/*     */     }
/*     */ 
/* 128 */     return result.ok;
/*     */   }
/*     */ 
/*     */   private static boolean tryParseFloat(String value, ParseResult result) {
/*     */     try {
/* 133 */       result.value = Float.valueOf(Float.parseFloat(value));
/* 134 */       result.ok = true;
/*     */     }
/*     */     catch (NumberFormatException e) {
/* 137 */       result.ok = false;
/*     */     }
/*     */ 
/* 140 */     return result.ok;
/*     */   }
/*     */ 
/*     */   private static boolean tryParseDouble(String value, ParseResult result) {
/*     */     try {
/* 145 */       result.value = Double.valueOf(Double.parseDouble(value));
/* 146 */       result.ok = true;
/*     */     }
/*     */     catch (NumberFormatException e) {
/* 149 */       result.ok = false;
/*     */     }
/*     */ 
/* 152 */     return result.ok;
/*     */   }
/*     */ 
/*     */   public static int readOption(Map<String, String> dic, String key, int defaultValue) {
/* 156 */     int ret = defaultValue;
/* 157 */     String value = getOption(dic, key);
/* 158 */     if (value != null) {
/* 159 */       ParseResult result = new ParseResult();
/* 160 */       if (tryParseInteger(value, result)) {
/* 161 */         return ((Integer)result.value).intValue();
/*     */       }
/*     */     }
/* 164 */     return ret;
/*     */   }
/*     */ 
/*     */   public static void writeOption(Map<String, String> dic, String key, String value) {
/* 168 */     dic.put(key, value != null ? value : "");
/*     */   }
/*     */ 
/*     */   public static String readOption(Map<String, String> dic, String key, String defaultValue) {
/* 172 */     String value = getOption(dic, key);
/* 173 */     if (value != null) {
/* 174 */       return value;
/*     */     }
/* 176 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public static void writeOption(Map<String, String> dic, String key, LeadSize value) {
/* 180 */     dic.put(key, String.format("%d,%d", new Object[] { Integer.valueOf(value.getWidth()), Integer.valueOf(value.getHeight()) }));
/*     */   }
/*     */ 
/*     */   public static LeadSize readOption(Map<String, String> dic, String key, LeadSize defaultValue) {
/* 184 */     String value = getOption(dic, key);
/* 185 */     if (value != null) {
/* 186 */       String[] strs = value.split(",");
/* 187 */       if (strs.length == 2) {
/* 188 */         ParseResult width = new ParseResult();
/* 189 */         ParseResult height = new ParseResult();
/* 190 */         if ((tryParseInteger(strs[0].trim(), width)) && (tryParseInteger(strs[1].trim(), height))) {
/* 191 */           return new LeadSize(((Integer)width.value).intValue(), ((Integer)height.value).intValue());
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 196 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public static void writeOption(Map<String, String> dic, String key, LeadRect value) {
/* 200 */     dic.put(key, String.format("%d,%d,%d,%d", new Object[] { Integer.valueOf(value.getX()), Integer.valueOf(value.getY()), Integer.valueOf(value.getWidth()), Integer.valueOf(value.getHeight()) }));
/*     */   }
/*     */ 
/*     */   public static LeadRect readOption(Map<String, String> dic, String key, LeadRect defaultValue) {
/* 204 */     String value = getOption(dic, key);
/* 205 */     if (value != null) {
/* 206 */       String[] strs = value.split(",");
/* 207 */       if (strs.length == 4) {
/* 208 */         ParseResult x = new ParseResult();
/* 209 */         ParseResult y = new ParseResult();
/* 210 */         ParseResult width = new ParseResult();
/* 211 */         ParseResult height = new ParseResult();
/* 212 */         if ((tryParseInteger(strs[0].trim(), x)) && (tryParseInteger(strs[1].trim(), y)) && (tryParseInteger(strs[2].trim(), width)) && (tryParseInteger(strs[3].trim(), height)))
/*     */         {
/* 216 */           return new LeadRect(((Integer)x.value).intValue(), ((Integer)y.value).intValue(), ((Integer)width.value).intValue(), ((Integer)height.value).intValue());
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 221 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public static void writeOption(Map<String, String> dic, String key, RasterColor value) {
/* 225 */     String sb = String.format("#%02X%02X%02X", new Object[] { Byte.valueOf(value.getR()), Byte.valueOf(value.getG()), Byte.valueOf(value.getB()) });
/* 226 */     dic.put(key, sb);
/*     */   }
/*     */ 
/*     */   public static RasterColor toRasterColor(String str) {
/* 230 */     return new RasterColor(Integer.valueOf(str.substring(1, 3), 16).intValue(), Integer.valueOf(str.substring(3, 5), 16).intValue(), Integer.valueOf(str.substring(5, 7), 16).intValue());
/*     */   }
/*     */ 
/*     */   public static RasterColor readOption(Map<String, String> dic, String key, RasterColor defaultValue)
/*     */   {
/* 237 */     String value = getOption(dic, key);
/* 238 */     if (value != null) {
/* 239 */       return toRasterColor(value);
/*     */     }
/*     */ 
/* 242 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public static void writeOption(Map<String, String> dic, String key, float value) {
/* 246 */     dic.put(key, Float.toString(value));
/*     */   }
/*     */ 
/*     */   public static float readOption(Map<String, String> dic, String key, float defaultValue) {
/* 250 */     float ret = defaultValue;
/*     */ 
/* 252 */     String value = getOption(dic, key);
/* 253 */     if (value != null) {
/* 254 */       ParseResult result = new ParseResult();
/* 255 */       if (tryParseFloat(value, result)) {
/* 256 */         return ((Float)result.value).floatValue();
/*     */       }
/*     */     }
/*     */ 
/* 260 */     return ret;
/*     */   }
/*     */ 
/*     */   public static void writeOption(Map<String, String> dic, String key, double value) {
/* 264 */     dic.put(key, Double.toString(value));
/*     */   }
/*     */ 
/*     */   public static double readOption(Map<String, String> dic, String key, double defaultValue) {
/* 268 */     double ret = defaultValue;
/*     */ 
/* 270 */     String value = getOption(dic, key);
/* 271 */     if (value != null) {
/* 272 */       ParseResult result = new ParseResult();
/* 273 */       if (tryParseDouble(value, result)) {
/* 274 */         return ((Double)result.value).doubleValue();
/*     */       }
/*     */     }
/*     */ 
/* 278 */     return ret;
/*     */   }
/*     */ 
/*     */   public static void writeOption(Map<String, String> dic, String key, long value) {
/* 282 */     dic.put(key, Long.toString(value));
/*     */   }
/*     */ 
/*     */   public static long readOption(Map<String, String> dic, String key, long defaultValue) {
/* 286 */     long ret = defaultValue;
/*     */ 
/* 288 */     String value = getOption(dic, key);
/* 289 */     if (value != null) {
/* 290 */       ParseResult result = new ParseResult();
/* 291 */       if (tryParseLong(value, result)) {
/* 292 */         return ((Long)result.value).longValue();
/*     */       }
/*     */     }
/*     */ 
/* 296 */     return ret;
/*     */   }
/*     */ 
/*     */   public static void writeOption(Map<String, String> dic, String key, int[] value) {
/* 300 */     StringBuilder sb = new StringBuilder();
/* 301 */     int length = value != null ? value.length : 0;
/* 302 */     for (int i = 0; i < length; i++) {
/* 303 */       sb.append(value[i]);
/* 304 */       if (i != length - 1)
/* 305 */         sb.append(",");
/*     */     }
/* 307 */     dic.put(key, sb.toString());
/*     */   }
/*     */ 
/*     */   public static int[] readOption(Map<String, String> dic, String key, int[] defaultValue) {
/* 311 */     String value = getOption(dic, key);
/* 312 */     if (value != null) {
/* 313 */       String[] strs = value.split(",");
/* 314 */       int length = strs.length;
/* 315 */       int[] ret = new int[length];
/* 316 */       ParseResult val = new ParseResult();
/* 317 */       for (int i = 0; i < length; i++) {
/* 318 */         if (tryParseInteger(strs[i].trim(), val)) {
/* 319 */           ret[i] = ((Integer)val.value).intValue();
/*     */         }
/*     */       }
/* 322 */       return ret;
/*     */     }
/*     */ 
/* 325 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public static CodecsOptions load(String fileName, InputStream stream) throws Exception {
/* 329 */     DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
/* 330 */     DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
/* 331 */     Document doc = null;
/*     */ 
/* 333 */     if (fileName != null)
/* 334 */       doc = docBuilder.parse(new File(fileName));
/*     */     else {
/* 336 */       doc = docBuilder.parse(stream);
/*     */     }
/* 338 */     XPathFactory xpathFactory = XPathFactory.newInstance();
/* 339 */     XPath xpath = xpathFactory.newXPath();
/*     */ 
/* 341 */     CodecsOptions options = new CodecsOptions();
/*     */ 
/* 343 */     Node rootNav = (Node)xpath.compile("leadtools_raster_codecs").evaluate(doc, XPathConstants.NODE);
/* 344 */     if (rootNav != null) {
/* 345 */       loadOptions(xpath, rootNav, options);
/*     */     }
/*     */ 
/* 348 */     return options;
/*     */   }
/*     */ 
/*     */   private static void loadOptions(XPath xpath, Node rootNav, CodecsOptions options) throws Exception {
/* 352 */     Map dic = new HashMap();
/*     */ 
/* 354 */     NodeList optionsIter = (NodeList)xpath.compile("//options/option").evaluate(rootNav.getOwnerDocument(), XPathConstants.NODESET);
/* 355 */     if (optionsIter != null) {
/* 356 */       int length = optionsIter.getLength();
/* 357 */       for (int i = 0; i < length; i++) {
/* 358 */         Node optionNav = optionsIter.item(i);
/* 359 */         NamedNodeMap attrs = optionNav.getAttributes();
/* 360 */         if ((attrs != null) && (attrs.getLength() > 0)) {
/* 361 */           Node node = attrs.item(0);
/* 362 */           String name = node.getNodeName();
/* 363 */           if ((name != null) && (name.length() > 0)) {
/* 364 */             dic.put(name, node.getNodeValue());
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 370 */     options.readXml(dic);
/*     */   }
/*     */ 
/*     */   static class ParseResult
/*     */   {
/*     */     public Object value;
/*     */     public boolean ok;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.codecs.jar
 * Qualified Name:     leadtools.codecs.CodecsOptionsSerializer
 * JD-Core Version:    0.6.2
 */