package sun.plugin.com;

import sun.plugin.javascript.ocx.JSObject;

public class DispatchClient
{
  private static final int EQUALS_FALSE = 0;
  private static final int EQUALS_TRUE = 1;
  private static final int EQUALS_ERROR = -1;
  private int dispPtr = 0;
  private int threadId = 0;

  public DispatchClient(int paramInt1, int paramInt2)
  {
    this.dispPtr = paramInt1;
    this.threadId = paramInt2;
  }

  public Object invoke(int paramInt1, String paramString, int paramInt2, Object[] paramArrayOfObject)
  {
    Object[] arrayOfObject = null;
    if (paramArrayOfObject != null)
      arrayOfObject = Utils.convertArgs(paramArrayOfObject, paramInt1);
    Object localObject2 = nativeInvoke(paramInt1, paramString, paramInt2, arrayOfObject, this.dispPtr, this.threadId);
    Object localObject1;
    if ((localObject2 != null) && ((localObject2 instanceof DispatchClient)))
      localObject1 = new JSObject((DispatchClient)localObject2);
    else
      localObject1 = localObject2;
    return localObject1;
  }

  public void release(int paramInt)
  {
    nativeRelease(paramInt, this.dispPtr);
  }

  public boolean equals(int paramInt, Object paramObject)
  {
    if (!(paramObject instanceof DispatchClient))
      return false;
    DispatchClient localDispatchClient = (DispatchClient)paramObject;
    int i = nativeEquals(paramInt, this.dispPtr, localDispatchClient.dispPtr);
    if (-1 == i)
      throw new RuntimeException("Unexpected native error");
    return 1 == i;
  }

  public int getDispatchWrapper()
  {
    return this.dispPtr;
  }

  public String getDispType(int paramInt)
  {
    return nativeGetDispType(paramInt, this.dispPtr);
  }

  private native int nativeEquals(int paramInt1, int paramInt2, int paramInt3);

  private native void nativeRelease(int paramInt1, int paramInt2);

  private native Object nativeInvoke(int paramInt1, String paramString, int paramInt2, Object[] paramArrayOfObject, int paramInt3, int paramInt4);

  private native String nativeGetDispType(int paramInt1, int paramInt2);
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.com.DispatchClient
 * JD-Core Version:    0.6.2
 */