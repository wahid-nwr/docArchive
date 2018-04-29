package sun.plugin.util;

public abstract interface TraceFilter
{
  public static final int OUTPUT_MASK = 15;
  public static final int STATUS_BAR_ONLY = 1;
  public static final int JAVA_CONSOLE_ONLY = 2;
  public static final int DEFAULT = 2;
  public static final int LEVEL_MASK = 4080;
  public static final int LEVEL_BASIC = 16;
  public static final int LEVEL_NET = 32;
  public static final int LEVEL_SECURITY = 64;
  public static final int LEVEL_EXT = 128;
  public static final int LEVEL_LIVECONNECT = 256;
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.util.TraceFilter
 * JD-Core Version:    0.6.2
 */