package sun.plugin.com;

public abstract interface Dispatch
{
  public static final int METHOD = 1;
  public static final int PROPERTYGET = 2;
  public static final int PROPERTYPUT = 4;
  public static final int PROPERTYPUTREF = 8;
  public static final int propertyBase = 4096;
  public static final int eventBase = 16384;
  public static final int methodBase = 32768;
  public static final int JT_BOOL = 1;
  public static final int JT_BYTE = 2;
  public static final int JT_CHAR = 3;
  public static final int JT_SHORT = 4;
  public static final int JT_INT = 5;
  public static final int JT_LONG = 6;
  public static final int JT_FLOAT = 7;
  public static final int JT_DOUBLE = 8;
  public static final int JT_STRING = 9;
  public static final int JT_ARRAY = 10;
  public static final int JT_OBJECT = 11;

  public abstract Object invoke(int paramInt1, int paramInt2, Object[] paramArrayOfObject)
    throws Exception;

  public abstract int getIdForName(String paramString)
    throws Exception;

  public abstract int getReturnType(int paramInt1, int paramInt2, Object[] paramArrayOfObject);

  public abstract Object getWrappedObject();
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.com.Dispatch
 * JD-Core Version:    0.6.2
 */