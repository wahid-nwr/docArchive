/*     */ package leadtools.sane.server;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonGenerationException;
/*     */ import com.fasterxml.jackson.databind.JsonMappingException;
/*     */ import com.fasterxml.jackson.databind.JsonNode;
/*     */ import com.fasterxml.jackson.databind.ObjectMapper;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.StringReader;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.Socket;
/*     */ import java.net.URL;
/*     */ import java.net.URLDecoder;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import leadtools.RasterImageFormat;
/*     */ import leadtools.sane.DeviceInfo;
/*     */ 
/*     */ class ThreadSocket extends Thread
/*     */ {
/*     */   private Socket _clientSocket;
/*     */   private ISaneScanningService _saneScanningService;
/*     */ 
/*     */   ThreadSocket(Socket clientSocket, ISaneScanningService saneScanningService)
/*     */   {
/*  82 */     this._clientSocket = clientSocket;
/*  83 */     this._saneScanningService = saneScanningService;
/*  84 */     start();
/*     */   }
/*     */ 
/*     */   public void run() {
/*     */     try {
/*  89 */       InputStream is = this._clientSocket.getInputStream();
/*  90 */       OutputStream os = this._clientSocket.getOutputStream();
/*     */ 
/*  92 */       InputStreamReader isReader = new InputStreamReader(is);
/*  93 */       BufferedReader br = new BufferedReader(isReader);
/*     */ 
/*  95 */       HttpResponse httpResponse = new HttpResponse();
/*     */ 
/*  97 */       String requestLine = br.readLine();
/*  98 */       if ((requestLine != null) && ((requestLine.startsWith("POST")) || (requestLine.startsWith("GET")))) {
/*  99 */         String methodName = "";
/* 100 */         boolean isPost = requestLine.startsWith("POST");
/*     */ 
/* 102 */         readHttpRequestHeader(br);
/*     */ 
/* 105 */         String postData = "";
/* 106 */         StringBuilder payload = new StringBuilder();
/* 107 */         if (isPost) {
/* 108 */           methodName = requestLine.substring(requestLine.indexOf("/") + 1);
/* 109 */           methodName = methodName.substring(0, methodName.indexOf(" "));
/* 110 */           while (br.ready()) {
/* 111 */             payload.append((char)br.read());
/*     */           }
/* 113 */           postData = payload.toString();
/*     */         }
/*     */         else {
/* 116 */           methodName = requestLine.substring(requestLine.indexOf("/") + 1, requestLine.indexOf("?"));
/*     */         }
/*     */ 
/* 120 */         if (isPost) {
/* 121 */           httpResponse = processPostRequest(methodName, new ObjectMapper().readTree(new StringReader(postData)));
/*     */         }
/*     */         else
/*     */         {
/* 125 */           httpResponse = processGetRequest(methodName, requestLine.substring(requestLine.indexOf("?") + 1).trim());
/*     */         }
/*     */       }
/*     */ 
/* 129 */       writeHttpRequestResponse(os, httpResponse);
/*     */ 
/* 132 */       this._clientSocket.close();
/*     */     }
/*     */     catch (IOException e) {
/* 135 */       System.err.println(e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   private static HashMap<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
/* 140 */     HashMap queryPairs = new LinkedHashMap();
/* 141 */     String query = url.getQuery();
/* 142 */     String[] pairs = query.split("&");
/* 143 */     for (String pair : pairs) {
/* 144 */       int idx = pair.indexOf("=");
/* 145 */       queryPairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8").toLowerCase(), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
/*     */     }
/* 147 */     return queryPairs;
/*     */   }
/*     */ 
/*     */   private void readHttpRequestHeader(BufferedReader br) throws IOException
/*     */   {
/* 152 */     while (br.readLine().length() != 0);
/*     */   }
/*     */ 
/*     */   private void writeHttpRequestResponse(OutputStream os, HttpResponse httpResponse)
/*     */     throws IOException
/*     */   {
/* 160 */     os.write("HTTP/1.0 200 OK\r\n".getBytes());
/* 161 */     os.write("Access-Control-Allow-Origin: *\r\n".getBytes());
/* 162 */     os.write("Access-Control-Allow-Headers: Origin, X-Requested-With, Content-Type, Accept\r\n".getBytes());
/* 163 */     os.write("Access-Control-Allow-Methods: GET, POST\r\n".getBytes());
/* 164 */     os.write(new StringBuilder().append("Content-Type: ").append(httpResponse.getReponseType()).append("\r\n").toString().getBytes());
/* 165 */     os.write("Server: SaneScanningServer\r\n".getBytes());
/*     */ 
/* 168 */     os.write("\r\n".getBytes());
/*     */ 
/* 171 */     os.write(httpResponse.getReponse());
/* 172 */     os.flush();
/* 173 */     os.close();
/*     */   }
/*     */ 
/*     */   private HttpResponse processGetRequest(String methodName, String getUriPramaters) throws UnsupportedEncodingException, MalformedURLException {
/* 177 */     HttpResponse httpResponse = new HttpResponse();
/* 178 */     HashMap getUriParametersDictionary = splitQuery(new URL(new StringBuilder().append("http://localhost/").append(methodName).append("?").append(getUriPramaters).toString()));
/* 179 */     switch (methodName.toLowerCase()) {
/*     */     case "runcommand":
/* 181 */       String id = ""; String commandName = ""; String arguments = ""; String userData = "";
/*     */ 
/* 183 */       if (getUriParametersDictionary.containsKey("id")) {
/* 184 */         id = (String)getUriParametersDictionary.get("id");
/*     */       }
/* 186 */       if (getUriParametersDictionary.containsKey("commandname")) {
/* 187 */         commandName = (String)getUriParametersDictionary.get("commandname");
/*     */       }
/* 189 */       if (getUriParametersDictionary.containsKey("arguments")) {
/* 190 */         arguments = SaneUtils.validateJsonString((String)getUriParametersDictionary.get("arguments"));
/*     */       }
/* 192 */       if (getUriParametersDictionary.containsKey("userdata")) {
/* 193 */         userData = (String)getUriParametersDictionary.get("userdata");
/*     */       }
/*     */ 
/* 196 */       httpResponse.setResponse(this._saneScanningService.run(id, commandName, arguments, userData).toByteArray());
/* 197 */       httpResponse.setResponseType("image/png");
/* 198 */       break;
/*     */     case "getpage":
/* 201 */       String id = ""; String userData = "";
/* 202 */       int pageNumber = 0; int bpp = 0; int width = 0; int height = 0;
/* 203 */       RasterImageFormat format = RasterImageFormat.PNG;
/*     */ 
/* 205 */       if (getUriParametersDictionary.containsKey("id")) {
/* 206 */         id = (String)getUriParametersDictionary.get("id");
/*     */       }
/* 208 */       if (getUriParametersDictionary.containsKey("pagenumber")) {
/* 209 */         pageNumber = Integer.parseInt((String)getUriParametersDictionary.get("pagenumber"));
/*     */       }
/* 211 */       if (getUriParametersDictionary.containsKey("format")) {
/* 212 */         format = RasterImageFormat.values()[Integer.parseInt((String)getUriParametersDictionary.get("format"))];
/*     */       }
/* 214 */       if (getUriParametersDictionary.containsKey("bpp")) {
/* 215 */         bpp = Integer.parseInt((String)getUriParametersDictionary.get("bpp"));
/*     */       }
/* 217 */       if (getUriParametersDictionary.containsKey("width")) {
/* 218 */         width = Integer.parseInt((String)getUriParametersDictionary.get("width"));
/*     */       }
/* 220 */       if (getUriParametersDictionary.containsKey("height")) {
/* 221 */         height = Integer.parseInt((String)getUriParametersDictionary.get("height"));
/*     */       }
/* 223 */       if (getUriParametersDictionary.containsKey("userdata")) {
/* 224 */         userData = (String)getUriParametersDictionary.get("userdata");
/*     */       }
/*     */ 
/* 227 */       httpResponse.setResponse(this._saneScanningService.getPage(id, pageNumber, format, bpp, width, height, userData).toByteArray());
/* 228 */       httpResponse.setResponseType("image/png");
/* 229 */       break;
/*     */     case "preview":
/* 232 */       String id = ""; String commandName = ""; String arguments = ""; String userData = "";
/* 233 */       int pageNumber = 0; int bpp = 0; int width = 0; int height = 0;
/* 234 */       RasterImageFormat format = RasterImageFormat.PNG;
/*     */ 
/* 236 */       if (getUriParametersDictionary.containsKey("id")) {
/* 237 */         id = (String)getUriParametersDictionary.get("id");
/*     */       }
/* 239 */       if (getUriParametersDictionary.containsKey("pagenumber")) {
/* 240 */         pageNumber = Integer.parseInt((String)getUriParametersDictionary.get("pagenumber"));
/*     */       }
/* 242 */       if (getUriParametersDictionary.containsKey("commandname")) {
/* 243 */         commandName = (String)getUriParametersDictionary.get("commandname");
/*     */       }
/* 245 */       if (getUriParametersDictionary.containsKey("arguments")) {
/* 246 */         arguments = SaneUtils.validateJsonString((String)getUriParametersDictionary.get("arguments"));
/*     */       }
/* 248 */       if (getUriParametersDictionary.containsKey("format")) {
/* 249 */         format = RasterImageFormat.values()[Integer.parseInt((String)getUriParametersDictionary.get("format"))];
/*     */       }
/* 251 */       if (getUriParametersDictionary.containsKey("bpp")) {
/* 252 */         bpp = Integer.parseInt((String)getUriParametersDictionary.get("bpp"));
/*     */       }
/* 254 */       if (getUriParametersDictionary.containsKey("width")) {
/* 255 */         width = Integer.parseInt((String)getUriParametersDictionary.get("width"));
/*     */       }
/* 257 */       if (getUriParametersDictionary.containsKey("height")) {
/* 258 */         height = Integer.parseInt((String)getUriParametersDictionary.get("height"));
/*     */       }
/* 260 */       if (getUriParametersDictionary.containsKey("userdata")) {
/* 261 */         userData = (String)getUriParametersDictionary.get("userdata");
/*     */       }
/* 263 */       httpResponse.setResponse(this._saneScanningService.preview(id, pageNumber, commandName, arguments, width, height, format, bpp, userData).toByteArray());
/* 264 */       httpResponse.setResponseType("image/png");
/* 265 */       break;
/*     */     }
/*     */ 
/* 270 */     return httpResponse;
/*     */   }
/*     */ 
/*     */   private HttpResponse processPostRequest(String methodName, JsonNode httpPostRequestData) throws JsonGenerationException, JsonMappingException, IOException {
/* 274 */     HttpResponse httpResponse = new HttpResponse();
/*     */ 
/* 277 */     switch (methodName.toLowerCase()) {
/*     */     case "init":
/* 279 */       String userData = "";
/*     */ 
/* 281 */       if (httpPostRequestData.has("userData")) {
/* 282 */         userData = SaneUtils.getStringFromJson(httpPostRequestData, "userData");
/*     */       }
/*     */ 
/* 285 */       String id = this._saneScanningService.init(userData);
/* 286 */       httpResponse.setResponse(id.getBytes());
/* 287 */       break;
/*     */     case "start":
/* 291 */       String id = ""; String userData = "";
/*     */ 
/* 293 */       if (httpPostRequestData.has("id")) {
/* 294 */         id = httpPostRequestData.get("id").asText();
/*     */       }
/* 296 */       if (httpPostRequestData.has("userData")) {
/* 297 */         userData = httpPostRequestData.get("userData").toString();
/*     */       }
/*     */ 
/* 300 */       this._saneScanningService.start(id, userData);
/* 301 */       break;
/*     */     case "getstatus":
/* 305 */       String id = ""; String userData = "";
/*     */ 
/* 307 */       if (httpPostRequestData.has("id")) {
/* 308 */         id = SaneUtils.getStringFromJson(httpPostRequestData, "id");
/*     */       }
/* 310 */       if (httpPostRequestData.has("userData")) {
/* 311 */         userData = SaneUtils.getStringFromJson(httpPostRequestData, "userData");
/*     */       }
/*     */ 
/* 315 */       SaneStatus status = this._saneScanningService.getStatus(id, userData);
/* 316 */       ObjectMapper mapper = new ObjectMapper();
/* 317 */       httpResponse.setResponse(mapper.writeValueAsString(status).getBytes());
/* 318 */       httpResponse.setResponseType("application/json");
/* 319 */       break;
/*     */     case "getoptionvalue":
/* 323 */       String id = ""; String optionName = ""; String userData = "";
/*     */ 
/* 325 */       if (httpPostRequestData.has("id")) {
/* 326 */         id = SaneUtils.getStringFromJson(httpPostRequestData, "id");
/*     */       }
/* 328 */       if (httpPostRequestData.has("optionName")) {
/* 329 */         optionName = SaneUtils.getStringFromJson(httpPostRequestData, "optionName");
/*     */       }
/* 331 */       if (httpPostRequestData.has("userData")) {
/* 332 */         userData = SaneUtils.getStringFromJson(httpPostRequestData, "userData");
/*     */       }
/*     */ 
/* 335 */       httpResponse.setResponse(this._saneScanningService.getOptionValue(id, optionName, userData).getBytes());
/* 336 */       break;
/*     */     case "setoptionvalue":
/* 340 */       String id = ""; String optionName = ""; String value = ""; String userData = "";
/*     */ 
/* 342 */       if (httpPostRequestData.has("id")) {
/* 343 */         id = SaneUtils.getStringFromJson(httpPostRequestData, "id");
/*     */       }
/* 345 */       if (httpPostRequestData.has("optionName")) {
/* 346 */         optionName = SaneUtils.getStringFromJson(httpPostRequestData, "optionName");
/*     */       }
/* 348 */       if (httpPostRequestData.has("value")) {
/* 349 */         value = SaneUtils.getStringFromJson(httpPostRequestData, "value");
/*     */       }
/* 351 */       if (httpPostRequestData.has("userData")) {
/* 352 */         userData = SaneUtils.getStringFromJson(httpPostRequestData, "userData");
/*     */       }
/*     */ 
/* 355 */       this._saneScanningService.setOptionValue(id, optionName, value, userData);
/* 356 */       break;
/*     */     case "getsources":
/* 360 */       String id = ""; String userData = "";
/*     */ 
/* 362 */       if (httpPostRequestData.has("id")) {
/* 363 */         id = httpPostRequestData.get("id").asText();
/*     */       }
/* 365 */       if (httpPostRequestData.has("userData")) {
/* 366 */         userData = httpPostRequestData.get("userData").toString();
/*     */       }
/*     */ 
/* 370 */       DeviceInfo[] sources = this._saneScanningService.getSources(id, userData);
/* 371 */       if (sources != null) {
/* 372 */         ObjectMapper mapper = new ObjectMapper();
/* 373 */         httpResponse.setResponse(mapper.writeValueAsString(sources).getBytes());
/* 374 */         httpResponse.setResponseType("application/json");
/* 375 */       }break;
/*     */     case "selectsource":
/* 380 */       String id = ""; String sourceName = ""; String userData = "";
/*     */ 
/* 382 */       if (httpPostRequestData.has("id")) {
/* 383 */         id = httpPostRequestData.get("id").asText();
/*     */       }
/* 385 */       if (httpPostRequestData.has("sourceName")) {
/* 386 */         sourceName = httpPostRequestData.get("sourceName").asText();
/*     */       }
/* 388 */       if (httpPostRequestData.has("userData")) {
/* 389 */         userData = httpPostRequestData.get("userData").toString();
/*     */       }
/*     */ 
/* 392 */       this._saneScanningService.selectSource(id, sourceName, userData);
/* 393 */       break;
/*     */     case "acquire":
/* 397 */       String id = ""; String userData = "";
/*     */ 
/* 399 */       if (httpPostRequestData.has("id")) {
/* 400 */         id = httpPostRequestData.get("id").asText();
/*     */       }
/* 402 */       if (httpPostRequestData.has("userData")) {
/* 403 */         userData = httpPostRequestData.get("userData").toString();
/*     */       }
/* 405 */       this._saneScanningService.acquire(id, userData);
/* 406 */       break;
/*     */     case "deletepage":
/* 410 */       String id = ""; String userData = "";
/* 411 */       int pageNumber = 0;
/*     */ 
/* 413 */       if (httpPostRequestData.has("id")) {
/* 414 */         id = httpPostRequestData.get("id").asText();
/*     */       }
/* 416 */       if (httpPostRequestData.has("pageNumber")) {
/* 417 */         pageNumber = httpPostRequestData.get("pageNumber").asInt();
/*     */       }
/* 419 */       if (httpPostRequestData.has("userData")) {
/* 420 */         userData = httpPostRequestData.get("userData").toString();
/*     */       }
/*     */ 
/* 423 */       this._saneScanningService.deletePage(id, pageNumber, userData);
/* 424 */       break;
/*     */     case "stop":
/* 428 */       String id = ""; String userData = "";
/*     */ 
/* 430 */       if (httpPostRequestData.has("id")) {
/* 431 */         id = httpPostRequestData.get("id").asText();
/*     */       }
/* 433 */       if (httpPostRequestData.has("userData")) {
/* 434 */         userData = httpPostRequestData.get("userData").toString();
/*     */       }
/*     */ 
/* 437 */       this._saneScanningService.stop(id, userData);
/* 438 */       break;
/*     */     case "run":
/* 442 */       String id = ""; String userData = ""; String commandName = ""; String arguments = "";
/* 443 */       int firstPageNumber = 0; int lastPageNumber = 0;
/*     */ 
/* 445 */       if (httpPostRequestData.has("id")) {
/* 446 */         id = SaneUtils.getStringFromJson(httpPostRequestData, "id");
/*     */       }
/* 448 */       if (httpPostRequestData.has("firstPageNumber")) {
/* 449 */         firstPageNumber = SaneUtils.getIntegerFromJson(httpPostRequestData, "firstPageNumber");
/*     */       }
/* 451 */       if (httpPostRequestData.has("lastPageNumber")) {
/* 452 */         lastPageNumber = SaneUtils.getIntegerFromJson(httpPostRequestData, "lastPageNumber");
/*     */       }
/* 454 */       if (httpPostRequestData.has("commandName")) {
/* 455 */         commandName = SaneUtils.getStringFromJson(httpPostRequestData, "commandName");
/*     */       }
/* 457 */       if (httpPostRequestData.has("arguments")) {
/* 458 */         arguments = SaneUtils.getStringFromJson(httpPostRequestData, "arguments");
/*     */       }
/* 460 */       if (httpPostRequestData.has("userData")) {
/* 461 */         userData = SaneUtils.getStringFromJson(httpPostRequestData, "userData");
/*     */       }
/*     */ 
/* 464 */       this._saneScanningService.run(id, firstPageNumber, lastPageNumber, commandName, arguments, userData);
/* 465 */       break;
/*     */     }
/*     */ 
/* 471 */     return httpResponse;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.server.jar
 * Qualified Name:     leadtools.sane.server.ThreadSocket
 * JD-Core Version:    0.6.2
 */