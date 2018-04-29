package ij;

public abstract interface IJEventListener
{
  public static final int FOREGROUND_COLOR_CHANGED = 0;
  public static final int BACKGROUND_COLOR_CHANGED = 1;
  public static final int COLOR_PICKER_CLOSED = 2;
  public static final int LOG_WINDOW_CLOSED = 3;
  public static final int TOOL_CHANGED = 4;

  public abstract void eventOccurred(int paramInt);
}

/* Location:           /home/wahid/Downloads/webscanning/ij.jar
 * Qualified Name:     ij.IJEventListener
 * JD-Core Version:    0.6.2
 */