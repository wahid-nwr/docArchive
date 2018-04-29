/*     */ package leadtools;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class RasterCollection<T>
/*     */   implements List<T>
/*     */ {
/*     */   private int _ignoreEventsCounter;
/*     */   public ArrayList<T> _list;
/*  95 */   private Vector<RasterCollectionEventListener<T>> _ItemAdded = new Vector();
/*  96 */   private Vector<RasterCollectionEventListener<T>> _ItemRemoved = new Vector();
/*     */ 
/*     */   public Object getElement(int i)
/*     */   {
/*  17 */     return this._list.get(i % this._list.size());
/*     */   }
/*     */ 
/*     */   public synchronized void addItemAddedListener(RasterCollectionEventListener<T> listener)
/*     */   {
/*  99 */     if (this._ItemAdded.contains(listener)) {
/* 100 */       return;
/*     */     }
/* 102 */     this._ItemAdded.addElement(listener);
/*     */   }
/*     */ 
/*     */   public synchronized void removeItemAddedListener(RasterCollectionEventListener<T> listener) {
/* 106 */     if (!this._ItemAdded.contains(listener)) {
/* 107 */       return;
/*     */     }
/* 109 */     this._ItemAdded.removeElement(listener);
/*     */   }
/*     */ 
/*     */   private synchronized void fireItemAdded(RasterCollectionEvent<T> event) {
/* 113 */     for (RasterCollectionEventListener listener : this._ItemAdded)
/* 114 */       listener.onRasterCollectionChanged(event);
/*     */   }
/*     */ 
/*     */   public synchronized void addItemRemovedListener(RasterCollectionEventListener<T> listener)
/*     */   {
/* 119 */     if (this._ItemRemoved.contains(listener)) {
/* 120 */       return;
/*     */     }
/* 122 */     this._ItemRemoved.addElement(listener);
/*     */   }
/*     */ 
/*     */   public synchronized void removeItemRemovedListener(RasterCollectionEventListener<T> listener) {
/* 126 */     if (!this._ItemRemoved.contains(listener)) {
/* 127 */       return;
/*     */     }
/* 129 */     this._ItemAdded.removeElement(listener);
/*     */   }
/*     */ 
/*     */   private synchronized void fireItemRemoved(RasterCollectionEvent<T> event) {
/* 133 */     for (RasterCollectionEventListener listener : this._ItemRemoved)
/* 134 */       listener.onRasterCollectionChanged(event);
/*     */   }
/*     */ 
/*     */   protected void onItemAdded(RasterCollectionEvent<T> event)
/*     */   {
/* 139 */     if (this._ignoreEventsCounter == 0)
/*     */     {
/* 141 */       event.setCollection(this);
/*     */ 
/* 143 */       fireItemAdded(event);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void onItemRemoved(RasterCollectionEvent<T> event) {
/* 148 */     if (this._ignoreEventsCounter == 0)
/*     */     {
/* 150 */       event.setCollection(this);
/*     */ 
/* 152 */       fireItemRemoved(event);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void onItemAdded(T item)
/*     */   {
/* 158 */     onItemAdded(new RasterCollectionEvent(this, item));
/*     */   }
/*     */ 
/*     */   private void onItemRemoved(T item) {
/* 162 */     onItemRemoved(new RasterCollectionEvent(this, item));
/*     */   }
/*     */ 
/*     */   public RasterCollection()
/*     */   {
/* 167 */     this._list = new ArrayList();
/* 168 */     this._ignoreEventsCounter = 0;
/*     */   }
/*     */ 
/*     */   public final void disableEvents()
/*     */   {
/* 173 */     this._ignoreEventsCounter += 1;
/*     */   }
/*     */ 
/*     */   public final void enableEvents() {
/* 177 */     if (this._ignoreEventsCounter <= 0) {
/* 178 */       throw new InvalidOperationException("EnableEvents is called without a pairing DisableEvents");
/*     */     }
/* 180 */     this._ignoreEventsCounter -= 1;
/*     */   }
/*     */ 
/*     */   public void insert(int index, T Item)
/*     */   {
/* 191 */     this._list.add(index, Item);
/* 192 */     onItemAdded(Item);
/*     */   }
/*     */ 
/*     */   public void removeAt(int index) {
/* 196 */     Object Item = this._list.get(index);
/* 197 */     this._list.remove(index);
/* 198 */     onItemRemoved(Item);
/*     */   }
/*     */ 
/*     */   public T getItem(int index)
/*     */   {
/* 203 */     return this._list.get(index);
/*     */   }
/*     */ 
/*     */   public void setItem(int index, T value)
/*     */   {
/* 208 */     this._list.set(index, value);
/*     */   }
/*     */ 
/*     */   public int getCount()
/*     */   {
/* 214 */     return this._list.size();
/*     */   }
/*     */ 
/*     */   public boolean isReadOnly()
/*     */   {
/* 219 */     return false;
/*     */   }
/*     */ 
/*     */   public void removeAll()
/*     */   {
/* 229 */     while (getCount() > 0)
/*     */     {
/* 231 */       Object Item = this._list.get(0);
/* 232 */       this._list.remove(0);
/* 233 */       onItemRemoved(Item);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Iterator<T> iterator()
/*     */   {
/* 254 */     return this._list.iterator();
/*     */   }
/*     */ 
/*     */   public void addRange(Collection<T> collection)
/*     */   {
/* 259 */     if (collection != null)
/*     */     {
/* 261 */       Iterator enumer = collection.iterator();
/* 262 */       while (enumer.hasNext())
/*     */       {
/* 264 */         Object Item = enumer.next();
/* 265 */         this._list.add(Item);
/* 266 */         onItemAdded(Item);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void sendToBack(T Item, boolean last) {
/* 272 */     int index = indexOf(Item);
/* 273 */     if (index >= 0)
/*     */     {
/* 275 */       remove(Item);
/* 276 */       int newIndex = last ? getCount() : index + 1;
/* 277 */       if (newIndex < getCount()) {
/* 278 */         insert(newIndex, Item);
/*     */       }
/*     */       else
/* 281 */         add(Item);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void bringToFront(T Item, boolean first) {
/* 286 */     int index = indexOf(Item);
/* 287 */     if ((index != -1) && (index <= getCount() - 1))
/*     */     {
/* 289 */       remove(Item);
/* 290 */       int newIndex = first ? 0 : index - 1;
/* 291 */       if (newIndex > 0)
/* 292 */         insert(newIndex, Item);
/*     */       else
/* 294 */         insert(0, Item);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean add(T e)
/*     */   {
/* 300 */     boolean bRet = this._list.add(e);
/* 301 */     onItemAdded(e);
/* 302 */     return bRet;
/*     */   }
/*     */ 
/*     */   public void add(int index, T element)
/*     */   {
/* 307 */     this._list.add(index, element);
/* 308 */     onItemAdded(element);
/*     */   }
/*     */ 
/*     */   public boolean addAll(Collection<? extends T> c)
/*     */   {
/* 313 */     boolean bRet = true;
/* 314 */     for (Iterator i$ = c.iterator(); i$.hasNext(); ) { Object item = i$.next();
/* 315 */       this._list.add(item);
/* 316 */       onItemAdded(item);
/*     */     }
/* 318 */     return bRet;
/*     */   }
/*     */ 
/*     */   public boolean addAll(int index, Collection<? extends T> c)
/*     */   {
/* 323 */     boolean bRet = this._list.addAll(index, c);
/* 324 */     for (Iterator i$ = c.iterator(); i$.hasNext(); ) { Object item = i$.next();
/* 325 */       onItemAdded(item); }
/* 326 */     return bRet;
/*     */   }
/*     */ 
/*     */   public void clear() {
/* 330 */     removeAll();
/*     */   }
/*     */ 
/*     */   public boolean contains(Object o) {
/* 334 */     return this._list.contains(o);
/*     */   }
/*     */ 
/*     */   public boolean containsAll(Collection<?> c) {
/* 338 */     return this._list.containsAll(c);
/*     */   }
/*     */ 
/*     */   public T get(int index) {
/* 342 */     return this._list.get(index);
/*     */   }
/*     */ 
/*     */   public int indexOf(Object o) {
/* 346 */     return this._list.indexOf(o);
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/* 350 */     return this._list.isEmpty();
/*     */   }
/*     */ 
/*     */   public int lastIndexOf(Object o) {
/* 354 */     return this._list.lastIndexOf(o);
/*     */   }
/*     */ 
/*     */   public ListIterator<T> listIterator() {
/* 358 */     return this._list.listIterator();
/*     */   }
/*     */ 
/*     */   public ListIterator<T> listIterator(int index) {
/* 362 */     return this._list.listIterator(index);
/*     */   }
/*     */ 
/*     */   public boolean remove(Object o)
/*     */   {
/* 367 */     int index = this._list.indexOf(o);
/* 368 */     Object item = null;
/* 369 */     if (index >= 0) {
/* 370 */       item = this._list.get(index);
/*     */     }
/* 372 */     boolean bRet = this._list.remove(o);
/*     */ 
/* 374 */     if (item != null) {
/* 375 */       onItemRemoved(item);
/*     */     }
/* 377 */     return bRet;
/*     */   }
/*     */ 
/*     */   public T remove(int index) {
/* 381 */     Object item = this._list.remove(index);
/* 382 */     onItemRemoved(item);
/* 383 */     return item;
/*     */   }
/*     */ 
/*     */   public boolean retainAll(Collection<?> c) {
/* 387 */     boolean bRet = this._list.removeAll(c);
/* 388 */     for (Iterator i$ = c.iterator(); i$.hasNext(); ) { Object item = i$.next();
/*     */ 
/* 390 */       Object temp = item;
/* 391 */       onItemRemoved(temp);
/*     */     }
/*     */ 
/* 394 */     return bRet;
/*     */   }
/*     */ 
/*     */   public T set(int index, T element) {
/* 398 */     return this._list.set(index, element);
/*     */   }
/*     */ 
/*     */   public int size() {
/* 402 */     return this._list.size();
/*     */   }
/*     */ 
/*     */   public List<T> subList(int fromIndex, int toIndex) {
/* 406 */     return this._list.subList(fromIndex, toIndex);
/*     */   }
/*     */ 
/*     */   public Object[] toArray() {
/* 410 */     return this._list.toArray();
/*     */   }
/*     */ 
/*     */   public <T> T[] toArray(T[] a)
/*     */   {
/* 415 */     return this._list.toArray(a);
/*     */   }
/*     */ 
/*     */   public boolean removeAll(Collection<?> c)
/*     */   {
/* 420 */     boolean bRet = this._list.removeAll(c);
/* 421 */     for (Iterator i$ = c.iterator(); i$.hasNext(); ) { Object item = i$.next();
/*     */ 
/* 423 */       Object temp = item;
/* 424 */       onItemRemoved(temp);
/*     */     }
/*     */ 
/* 427 */     return bRet;
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.RasterCollection
 * JD-Core Version:    0.6.2
 */