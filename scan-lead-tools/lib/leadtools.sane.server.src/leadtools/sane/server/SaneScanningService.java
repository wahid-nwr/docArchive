/*     */ package leadtools.sane.server;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Paths;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.UUID;
/*     */ import leadtools.LeadStreamFactory;
/*     */ import leadtools.RasterException;
/*     */ import leadtools.RasterExceptionCode;
/*     */ import leadtools.RasterImage;
/*     */ import leadtools.RasterImageFormat;
/*     */ import leadtools.codecs.RasterCodecs;
/*     */ import leadtools.sane.DeviceInfo;
/*     */ import leadtools.sane.SaneDevice;
/*     */ import leadtools.sane.SaneFactory;
/*     */ import leadtools.sane.SaneSession;
/*     */ 
/*     */ public class SaneScanningService
/*     */   implements ISaneScanningService
/*     */ {
/* 131 */   private ArrayList<ClientListener> clientListeners = new ArrayList();
/*     */ 
/* 149 */   private ArrayList<ImageProcessingListener> imageProcessingListeners = new ArrayList();
/*     */ 
/* 172 */   HashMap<String, SessionData> _sessionsData = new HashMap();
/*     */ 
/* 702 */   private ICommandCallBack _runCommandCallBack = null;
/*     */ 
/*     */   public void addClientListener(ClientListener clientListener)
/*     */   {
/* 133 */     this.clientListeners.add(clientListener);
/*     */   }
/*     */   public void removeClientListener(ClientListener clientListener) {
/* 136 */     this.clientListeners.remove(clientListener);
/*     */   }
/*     */ 
/*     */   void clientStarted(ClientConnectionEvent event) {
/* 140 */     for (ClientListener listener : this.clientListeners)
/* 141 */       listener.clientStarted(event);
/*     */   }
/*     */ 
/*     */   void clientStopped(ClientConnectionEvent event) {
/* 145 */     for (ClientListener listener : this.clientListeners)
/* 146 */       listener.clientStopped(event);
/*     */   }
/*     */ 
/*     */   public void addImageProcessingListener(ImageProcessingListener imageProcessingListener)
/*     */   {
/* 151 */     this.imageProcessingListeners.add(imageProcessingListener);
/*     */   }
/*     */   public void removeImageProcessingListener(ImageProcessingListener imageProcessingListener) {
/* 154 */     this.imageProcessingListeners.remove(imageProcessingListener);
/*     */   }
/*     */ 
/*     */   void begin(ImageProcessingEvent event) {
/* 158 */     for (ImageProcessingListener listener : this.imageProcessingListeners)
/* 159 */       listener.begin(event);
/*     */   }
/*     */ 
/*     */   void process(PageImageProcessingEvent event) {
/* 163 */     for (ImageProcessingListener listener : this.imageProcessingListeners)
/* 164 */       listener.process(event);
/*     */   }
/*     */ 
/*     */   void end(ImageProcessingEvent event) {
/* 168 */     for (ImageProcessingListener listener : this.imageProcessingListeners)
/* 169 */       listener.end(event);
/*     */   }
/*     */ 
/*     */   private SessionData getSessionData(String id)
/*     */   {
/* 175 */     if (this._sessionsData.containsKey(id)) {
/* 176 */       return (SessionData)this._sessionsData.get(id);
/*     */     }
/* 178 */     return null;
/*     */   }
/*     */ 
/*     */   private void startSaneSession(SessionData data) {
/* 182 */     if (!data.getClosed())
/* 183 */       return;
/*     */     try
/*     */     {
/* 186 */       data.setSession(SaneFactory.createSession());
/* 187 */       data.getSession().start();
/* 188 */       data.setClosed(false);
/*     */     } catch (Exception e) {
/* 190 */       data.setLastErrorCode(RasterExceptionCode.FAILURE.getValue());
/* 191 */       data.setLastErrorMessage(e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   private RasterImage loadImage(String id, String fileName, Object userData) {
/* 196 */     RasterCodecs codecs = null;
/* 197 */     RasterImage image = null;
/*     */     try {
/* 199 */       codecs = createRasterCodecs(id, userData);
/* 200 */       image = codecs.load(fileName);
/*     */     } finally {
/* 202 */       if (codecs != null) {
/* 203 */         codecs.dispose();
/*     */       }
/*     */     }
/* 206 */     return image;
/*     */   }
/*     */ 
/*     */   protected RasterCodecs createRasterCodecs(String id, Object userData) {
/* 210 */     return new RasterCodecs();
/*     */   }
/*     */ 
/*     */   private void saveImage(String id, RasterImage image, String fileName, RasterImageFormat format, int bpp, Object userData) {
/* 214 */     RasterCodecs codecs = null;
/*     */     try {
/* 216 */       codecs = createRasterCodecs(id, userData);
/* 217 */       codecs.save(image, fileName, format, bpp);
/*     */     } finally {
/* 219 */       if (codecs != null)
/* 220 */         codecs.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void saveImage(String id, RasterImage image, ByteArrayOutputStream stream, RasterImageFormat format, int bpp, Object userData)
/*     */   {
/* 226 */     RasterCodecs codecs = null;
/*     */     try {
/* 228 */       codecs = createRasterCodecs(id, userData);
/* 229 */       codecs.save(image, LeadStreamFactory.create(stream, false), format, bpp);
/*     */     } finally {
/* 231 */       if (codecs != null)
/* 232 */         codecs.dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   public RasterImage getImage(String id, int pageNumber)
/*     */   {
/* 238 */     if ((id == null) || (id.isEmpty())) {
/* 239 */       throw new NullPointerException("id");
/*     */     }
/*     */ 
/* 242 */     SessionData data = getSessionData(id);
/* 243 */     if (data == null) {
/* 244 */       throw new IllegalArgumentException("Must be a valid Id");
/*     */     }
/*     */ 
/* 247 */     if (pageNumber == 0) {
/* 248 */       pageNumber = 1;
/*     */     }
/* 250 */     if ((pageNumber < 1) || (pageNumber > data.getSaveData().getFilesList().size())) {
/* 251 */       throw new IllegalArgumentException("Invalid page number");
/*     */     }
/*     */ 
/* 254 */     String imageFileName = (String)data.getSaveData().getFilesList().get(pageNumber - 1);
/* 255 */     return loadImage(id, imageFileName, null);
/*     */   }
/*     */ 
/*     */   public void setImage(String id, RasterImage image, int pageNumber) {
/* 259 */     if ((id == null) || (id == "")) {
/* 260 */       throw new IllegalArgumentException("id");
/*     */     }
/*     */ 
/* 263 */     SessionData data = getSessionData(id);
/* 264 */     if (data == null) {
/* 265 */       throw new IllegalArgumentException("Must be a valid Id");
/*     */     }
/*     */ 
/* 268 */     if (pageNumber == 0) {
/* 269 */       pageNumber = 1;
/*     */     }
/* 271 */     if ((pageNumber < 1) || (pageNumber > data.getSaveData().getFilesList().size())) {
/* 272 */       throw new IllegalArgumentException("Invalid page number");
/*     */     }
/*     */     try
/*     */     {
/* 276 */       saveImage(id, image, SaneUtils.createTempFile(Integer.toString(data.getSaveData().getFilesList().size()), ".png").getAbsolutePath(), RasterImageFormat.PNG, 0, null);
/*     */     } catch (IOException e) {
/* 278 */       data.setLastErrorCode(RasterExceptionCode.FAILURE.getValue());
/* 279 */       data.setLastErrorMessage(e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String generateID(Object userData) {
/* 284 */     return UUID.randomUUID().toString().replace("-", "");
/*     */   }
/*     */ 
/*     */   public String init(Object userData)
/*     */   {
/* 289 */     String id = generateID(userData);
/* 290 */     SessionData data = new SessionData(id);
/* 291 */     this._sessionsData.put(data.getId(), data);
/* 292 */     return data.getId();
/*     */   }
/*     */ 
/*     */   public void start(String id, Object userData)
/*     */   {
/* 297 */     SessionData data = getSessionData(id);
/*     */ 
/* 299 */     startSaneSession(data);
/*     */ 
/* 301 */     ClientConnectionEvent event = new ClientConnectionEvent();
/* 302 */     event.setId(id);
/* 303 */     event.setUserData(userData);
/* 304 */     clientStarted(event);
/*     */   }
/*     */ 
/*     */   public DeviceInfo[] getSources(String id, Object userData)
/*     */   {
/* 309 */     SessionData data = getSessionData(id);
/* 310 */     if (!data.isScanning()) {
/* 311 */       SaneSession session = data.getSession();
/* 312 */       if (session != null) {
/*     */         try {
/* 314 */           SaneDevice[] devices = session.getDevices();
/* 315 */           DeviceInfo[] devicesInfo = new DeviceInfo[devices.length];
/* 316 */           for (int i = 0; i < devices.length; i++)
/* 317 */             devicesInfo[i] = devices[i].getInfo();
/* 318 */           return devicesInfo;
/*     */         }
/*     */         catch (Exception e) {
/* 321 */           data.setLastErrorCode(RasterExceptionCode.FAILURE.getValue());
/* 322 */           data.setLastErrorMessage(e.getMessage());
/* 323 */           return null;
/*     */         }
/*     */       }
/*     */     }
/* 327 */     return null;
/*     */   }
/*     */ 
/*     */   public SaneStatus getStatus(String id, Object userData)
/*     */   {
/* 332 */     SessionData data = getSessionData(id);
/* 333 */     SaneStatus status = new SaneStatus();
/* 334 */     status.setScanning(data.isScanning());
/* 335 */     SaneError saneError = new SaneError();
/* 336 */     saneError.setCode(data.getLastErrorCode());
/* 337 */     saneError.setMessage(data.getLastErrorMessage());
/* 338 */     status.setError(saneError);
/* 339 */     status.setPageCount(data.getSaveData().getFilesList().size());
/* 340 */     status.setCurrentPage(0);
/* 341 */     status.setSelectedSource(data.getSourceName());
/*     */ 
/* 343 */     data.setLastErrorCode(RasterExceptionCode.SUCCESS.getValue());
/* 344 */     data.setLastErrorMessage("");
/*     */ 
/* 346 */     return status;
/*     */   }
/*     */ 
/*     */   public void selectSource(String id, String sourceName, Object userData)
/*     */   {
/* 351 */     SessionData data = getSessionData(id);
/* 352 */     if (!data.isScanning()) {
/* 353 */       SaneSession session = data.getSession();
/* 354 */       if (session != null)
/*     */         try {
/* 356 */           if ((sourceName != null) && (sourceName != "")) {
/* 357 */             SaneDevice device = session.getDevice(sourceName);
/* 358 */             device.open();
/* 359 */             device.setOptionValue("mode", "Color");
/* 360 */             device.setOptionValue("resolution", "150");
/* 361 */             device.close();
/* 362 */             data.setActiveDevice(device);
/*     */           }
/*     */           else {
/* 365 */             throw new IllegalArgumentException("sourceName");
/*     */           }
/*     */ 
/* 368 */           data.setSourceName(sourceName);
/*     */         } catch (Exception e) {
/* 370 */           data.setLastErrorCode(RasterExceptionCode.FAILURE.getValue());
/* 371 */           data.setLastErrorMessage(e.getMessage());
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void acquire(String id, Object userData)
/*     */   {
/* 379 */     SessionData data = getSessionData(id);
/* 380 */     if (!data.isScanning()) {
/* 381 */       data.setScanning(true);
/* 382 */       SaneDevice activeDevice = data.getActiveDevice();
/* 383 */       activeDevice.open();
/*     */       try {
/*     */         while (true)
/* 386 */           acquirePage(data);
/*     */       } catch (Exception e) {
/* 388 */         if ((e instanceof RasterException)) {
/* 389 */           RasterException rasterException = (RasterException)e;
/* 390 */           data.setLastErrorCode(rasterException.getCode().getValue());
/* 391 */           data.setLastErrorMessage(rasterException.getMessage());
/*     */         } else {
/* 393 */           data.setLastErrorCode(RasterExceptionCode.FAILURE.getValue());
/* 394 */           data.setLastErrorMessage(e.getMessage());
/*     */         }
/*     */ 
/* 399 */         if (activeDevice != null) {
/* 400 */           activeDevice.close();
/*     */         }
/* 402 */         data.setScanning(false);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/* 407 */   private void acquirePage(SessionData data) throws Exception { File tempFile = SaneUtils.createTempFile(data.getId() + Integer.toString(data.getSaveData().getFilesList().size() + 1), data.getSaveData().getExt());
/* 408 */     String fileName = tempFile.getAbsolutePath();
/* 409 */     RasterImage image = data.getActiveDevice().acquireImage();
/* 410 */     saveImage(data.getId(), image, fileName, RasterImageFormat.PNG, 0, null);
/* 411 */     data.getSaveData().getFilesList().add(fileName);
/* 412 */     image.dispose();
/*     */   }
/*     */ 
/*     */   public void setOptionValue(String id, String optionName, String value, Object userData)
/*     */   {
/* 418 */     SessionData data = getSessionData(id);
/*     */     try {
/* 420 */       data.getActiveDevice().setOptionValue(optionName, value);
/*     */     } catch (Exception e) {
/* 422 */       data.setLastErrorCode(RasterExceptionCode.FAILURE.getValue());
/* 423 */       data.setLastErrorMessage(e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getOptionValue(String id, String optionName, Object userData)
/*     */   {
/* 429 */     SessionData data = getSessionData(id);
/* 430 */     String optionValue = null;
/*     */     try {
/* 432 */       optionValue = data.getActiveDevice().getOptionValue(optionName);
/*     */     }
/*     */     catch (Exception e) {
/* 435 */       data.setLastErrorCode(RasterExceptionCode.FAILURE.getValue());
/* 436 */       data.setLastErrorMessage(e.getMessage());
/*     */     }
/* 438 */     return optionValue;
/*     */   }
/*     */ 
/*     */   public ByteArrayOutputStream getPage(String id, int pageNumber, RasterImageFormat format, int bpp, int width, int height, Object userData)
/*     */   {
/* 443 */     SessionData data = getSessionData(id);
/*     */ 
/* 445 */     if ((pageNumber < 0) || (pageNumber > data.getSaveData().getFilesList().size())) {
/* 446 */       data.setLastErrorCode(RasterExceptionCode.INVALID_PARAMETER.getValue());
/* 447 */       data.setLastErrorMessage("'pageNumber' must be a value between 1 and total pages number");
/* 448 */       return null;
/*     */     }
/*     */ 
/* 452 */     if (pageNumber == 0) {
/* 453 */       pageNumber = 1;
/*     */     }
/*     */ 
/* 456 */     if ((width < 0) || (height < 0)) {
/* 457 */       data.setLastErrorCode(RasterExceptionCode.INVALID_PARAMETER.getValue());
/* 458 */       data.setLastErrorMessage("'width' and 'height' must be value greater than or equal to 0");
/* 459 */       return null;
/*     */     }
/*     */ 
/* 462 */     String fileName = (String)data.getSaveData().getFilesList().get(pageNumber - 1);
/* 463 */     RasterImage image = loadImage(id, fileName, userData);
/*     */ 
/* 465 */     SaneUtils.resizeImage(image, width, height);
/*     */ 
/* 467 */     if (format == RasterImageFormat.UNKNOWN) {
/* 468 */       format = RasterImageFormat.PNG;
/*     */     }
/*     */ 
/* 471 */     ByteArrayOutputStream stream = new ByteArrayOutputStream();
/* 472 */     saveImage(id, image, stream, format, bpp, userData);
/* 473 */     image.dispose();
/*     */ 
/* 475 */     return stream;
/*     */   }
/*     */ 
/*     */   public void deletePage(String id, int pageNumber, Object userData)
/*     */   {
/* 480 */     SessionData data = getSessionData(id);
/*     */     try {
/* 482 */       SaveData saveData = data.getSaveData();
/* 483 */       Files.delete(Paths.get((String)saveData.getFilesList().get(pageNumber - 1), new String[0]));
/* 484 */       saveData.getFilesList().remove(pageNumber - 1);
/*     */     }
/*     */     catch (Exception e) {
/* 487 */       data.setLastErrorCode(RasterExceptionCode.INVALID_PARAMETER.getValue());
/* 488 */       data.setLastErrorMessage(e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void close(SessionData data) {
/* 493 */     SaveData saveData = data.getSaveData();
/*     */ 
/* 495 */     data.setLastErrorCode(RasterExceptionCode.SUCCESS.getValue());
/* 496 */     data.setLastErrorMessage("");
/* 497 */     data.setSourceName("");
/*     */ 
/* 499 */     saveData.getFilesList().clear();
/*     */ 
/* 501 */     if (!data.getClosed()) {
/* 502 */       data.setClosed(true);
/* 503 */       data.getSession().stop();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void stop(String id, Object userData)
/*     */   {
/* 509 */     SessionData data = getSessionData(id);
/* 510 */     close(data);
/*     */ 
/* 512 */     ClientConnectionEvent event = new ClientConnectionEvent();
/* 513 */     event.setId(id);
/* 514 */     event.setUserData(userData);
/* 515 */     clientStopped(event);
/*     */   }
/*     */ 
/*     */   public void run(String id, int firstPageNumber, int lastPageNumber, String commandName, String arguments, Object userData)
/*     */   {
/* 520 */     SessionData data = getSessionData(id);
/* 521 */     int pageCount = data.getSaveData().getFilesList().size();
/*     */ 
/* 524 */     if (firstPageNumber == 0) {
/* 525 */       firstPageNumber = 1;
/*     */     }
/* 527 */     if ((lastPageNumber == 0) || (lastPageNumber == -1)) {
/* 528 */       lastPageNumber = pageCount;
/*     */     }
/*     */ 
/* 531 */     if ((firstPageNumber < 1) || (firstPageNumber > lastPageNumber)) {
/* 532 */       throw new IllegalArgumentException("Invalid first page number");
/*     */     }
/* 534 */     if ((lastPageNumber < 1) || (lastPageNumber > pageCount)) {
/* 535 */       throw new IllegalArgumentException("Invalid last page number");
/*     */     }
/*     */ 
/* 538 */     boolean isCanceled = false;
/*     */ 
/* 540 */     if (this.imageProcessingListeners.size() > 0) {
/* 541 */       ImageProcessingEvent event = new ImageProcessingEvent();
/* 542 */       event.setId(id);
/* 543 */       event.setFirstPageNumber(firstPageNumber);
/* 544 */       event.setLastPageNumber(lastPageNumber);
/* 545 */       event.setCommandName(commandName);
/* 546 */       event.setArguments(arguments);
/* 547 */       event.setUserData(userData);
/*     */ 
/* 549 */       begin(event);
/*     */ 
/* 551 */       isCanceled = event.isCanceled();
/*     */     }
/*     */ 
/* 554 */     if (isCanceled) {
/* 555 */       return;
/*     */     }
/*     */ 
/* 558 */     RasterCodecs codecs = null;
/*     */     try {
/* 560 */       codecs = createRasterCodecs(id, userData);
/*     */ 
/* 563 */       for (int pageNumber = firstPageNumber; (pageNumber <= lastPageNumber) && (!isCanceled); pageNumber++) {
/* 564 */         String fileName = (String)data.getSaveData().getFilesList().get(pageNumber - 1);
/* 565 */         RasterImage image = codecs.load(fileName, 1);
/*     */ 
/* 567 */         if (this.imageProcessingListeners.size() > 0) {
/* 568 */           PageImageProcessingEvent event = new PageImageProcessingEvent();
/* 569 */           event.setId(id);
/* 570 */           event.setCommandName(commandName);
/* 571 */           event.setArguments(arguments);
/* 572 */           event.setUserData(userData);
/* 573 */           event.setPageNumber(pageNumber);
/* 574 */           event.setImage(image);
/*     */ 
/* 577 */           process(event);
/*     */ 
/* 579 */           isCanceled = event.isCanceled();
/*     */ 
/* 581 */           if ((!isCanceled) && (!event.getSkip()))
/* 582 */             codecs.save(event.getImage(), fileName, data.getSaveData().getFormat(), 32);
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 587 */       if (codecs != null) {
/* 588 */         codecs.dispose();
/*     */       }
/*     */     }
/* 591 */     if (this.imageProcessingListeners.size() > 0) {
/* 592 */       ImageProcessingEvent event = new ImageProcessingEvent();
/* 593 */       event.setId(id);
/* 594 */       event.setFirstPageNumber(firstPageNumber);
/* 595 */       event.setLastPageNumber(lastPageNumber);
/* 596 */       event.setCommandName(commandName);
/* 597 */       event.setArguments(arguments);
/* 598 */       event.setUserData(userData);
/* 599 */       event.setCanceled(isCanceled);
/*     */ 
/* 601 */       end(event);
/*     */ 
/* 603 */       if ((!isCanceled) && (event.isCanceled()))
/* 604 */         isCanceled = event.isCanceled();
/*     */     }
/*     */   }
/*     */ 
/*     */   public ByteArrayOutputStream preview(String id, int pageNumber, String commandName, String arguments, int width, int height, RasterImageFormat format, int bpp, Object userData)
/*     */   {
/* 611 */     SessionData data = getSessionData(id);
/* 612 */     int pagesCount = data.getSaveData().getFilesList().size();
/*     */ 
/* 615 */     if (pageNumber == 0) {
/* 616 */       pageNumber = 1;
/*     */     }
/* 618 */     if ((pageNumber < 1) || (pageNumber > pagesCount)) {
/* 619 */       throw new IllegalArgumentException("Invalid page number");
/*     */     }
/*     */ 
/* 622 */     if (format == RasterImageFormat.UNKNOWN) {
/* 623 */       format = RasterImageFormat.PNG;
/*     */     }
/*     */ 
/* 626 */     boolean isCanceled = false;
/* 627 */     Object sessionUserData = null;
/* 628 */     ByteArrayOutputStream resultstream = null;
/*     */ 
/* 630 */     if (!this.imageProcessingListeners.isEmpty()) {
/* 631 */       ImageProcessingEvent event = new ImageProcessingEvent();
/* 632 */       event.setId(id);
/* 633 */       event.setFirstPageNumber(pageNumber);
/* 634 */       event.setLastPageNumber(pageNumber);
/* 635 */       event.setCommandName(commandName);
/* 636 */       event.setArguments(arguments);
/* 637 */       event.setIsPreview(true);
/* 638 */       event.setPreviewWidth(width);
/* 639 */       event.setPreviewHeight(height);
/* 640 */       event.setUserData(sessionUserData);
/* 641 */       begin(event);
/* 642 */       isCanceled = event.isCanceled();
/*     */     }
/* 644 */     RasterCodecs codecs = null;
/* 645 */     RasterImage image = null;
/*     */     try {
/* 647 */       codecs = createRasterCodecs(id, userData);
/* 648 */       String fileName = (String)data.getSaveData().getFilesList().get(pageNumber - 1);
/* 649 */       image = codecs.load(fileName, 1);
/*     */ 
/* 651 */       if (!this.imageProcessingListeners.isEmpty()) {
/* 652 */         PageImageProcessingEvent event = new PageImageProcessingEvent();
/* 653 */         event.setId(id);
/* 654 */         event.setCommandName(commandName);
/* 655 */         event.setArguments(arguments);
/* 656 */         event.setUserData(sessionUserData);
/* 657 */         event.setPageNumber(pageNumber);
/* 658 */         event.setIsPreview(true);
/* 659 */         event.setPreviewWidth(width);
/* 660 */         event.setPreviewHeight(height);
/* 661 */         event.setImage(image);
/*     */ 
/* 664 */         process(event);
/* 665 */         isCanceled = event.isCanceled();
/*     */ 
/* 667 */         if ((!isCanceled) && (!event.getSkip())) {
/* 668 */           resultstream = new ByteArrayOutputStream();
/* 669 */           codecs.save(image, LeadStreamFactory.create(resultstream, false), format, bpp);
/*     */         }
/*     */       }
/*     */     } finally {
/* 673 */       if (codecs != null) {
/* 674 */         codecs.dispose();
/*     */       }
/* 676 */       if (image != null) {
/* 677 */         image.dispose();
/*     */       }
/*     */     }
/*     */ 
/* 681 */     if (!this.imageProcessingListeners.isEmpty()) {
/* 682 */       ImageProcessingEvent event = new ImageProcessingEvent();
/* 683 */       event.setId(id);
/* 684 */       event.setFirstPageNumber(pageNumber);
/* 685 */       event.setLastPageNumber(pageNumber);
/* 686 */       event.setCommandName(commandName);
/* 687 */       event.setArguments(arguments);
/* 688 */       event.setIsPreview(true);
/* 689 */       event.setPreviewWidth(width);
/* 690 */       event.setPreviewHeight(height);
/* 691 */       event.setCanceled(isCanceled);
/*     */ 
/* 693 */       end(event);
/*     */ 
/* 695 */       if ((!isCanceled) && (event.isCanceled())) {
/* 696 */         isCanceled = event.isCanceled();
/*     */       }
/*     */     }
/* 699 */     return resultstream;
/*     */   }
/*     */ 
/*     */   public ICommandCallBack getRunCommandCallBack()
/*     */   {
/* 704 */     return this._runCommandCallBack;
/*     */   }
/*     */   public void setRunCommandCallBack(ICommandCallBack newValue) {
/* 707 */     this._runCommandCallBack = newValue;
/*     */   }
/*     */ 
/*     */   public ByteArrayOutputStream run(String id, String commandName, String arguments, Object userData)
/*     */   {
/* 712 */     if (getRunCommandCallBack() == null) {
/* 713 */       return null;
/*     */     }
/*     */ 
/* 716 */     CommandEvent event = new CommandEvent();
/* 717 */     event.setId(id);
/* 718 */     event.setCommandName(commandName);
/* 719 */     event.setArguments(arguments);
/* 720 */     event.setUserData(userData);
/*     */ 
/* 722 */     return getRunCommandCallBack().invoke(event);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.sane.server.jar
 * Qualified Name:     leadtools.sane.server.SaneScanningService
 * JD-Core Version:    0.6.2
 */