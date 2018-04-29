package netscape.javascript;

public class JSException extends RuntimeException
{
  public static final int EXCEPTION_TYPE_EMPTY = -1;
  public static final int EXCEPTION_TYPE_VOID = 0;
  public static final int EXCEPTION_TYPE_OBJECT = 1;
  public static final int EXCEPTION_TYPE_FUNCTION = 2;
  public static final int EXCEPTION_TYPE_STRING = 3;
  public static final int EXCEPTION_TYPE_NUMBER = 4;
  public static final int EXCEPTION_TYPE_BOOLEAN = 5;
  public static final int EXCEPTION_TYPE_ERROR = 6;
  protected String message = null;
  protected String filename = null;
  protected int lineno = -1;
  protected String source = null;
  protected int tokenIndex = -1;
  private int wrappedExceptionType = -1;
  private Object wrappedException = null;

  public JSException()
  {
    this(null);
  }

  public JSException(String paramString)
  {
    this(paramString, null, -1, null, -1);
  }

  public JSException(String paramString1, String paramString2, int paramInt1, String paramString3, int paramInt2)
  {
    super(paramString1);
    this.message = paramString1;
    this.filename = paramString2;
    this.lineno = paramInt1;
    this.source = paramString3;
    this.tokenIndex = paramInt2;
    this.wrappedExceptionType = -1;
  }

  public JSException(int paramInt, Object paramObject)
  {
    this();
    this.wrappedExceptionType = paramInt;
    this.wrappedException = paramObject;
  }

  public int getWrappedExceptionType()
  {
    return this.wrappedExceptionType;
  }

  public Object getWrappedException()
  {
    return this.wrappedException;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     netscape.javascript.JSException
 * JD-Core Version:    0.6.2
 */