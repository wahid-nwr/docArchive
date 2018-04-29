package ij.macro;

public abstract interface MacroExtension
{
  public static final int ARG_STRING = 1;
  public static final int ARG_NUMBER = 2;
  public static final int ARG_ARRAY = 4;
  public static final int ARG_OUTPUT = 16;
  public static final int ARG_OPTIONAL = 32;

  public abstract String handleExtension(String paramString, Object[] paramArrayOfObject);

  public abstract ExtensionDescriptor[] getExtensionFunctions();
}

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.macro.MacroExtension
 * JD-Core Version:    0.6.2
 */