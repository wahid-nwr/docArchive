package sun.plugin.javascript.navig;

import netscape.javascript.JSException;

public class FrameArray extends Array
{
  protected FrameArray(int paramInt1, String paramString, int paramInt2)
  {
    super(paramInt1, paramString, paramInt2);
  }

  protected Object createObject(String paramString)
    throws JSException
  {
    return resolveObject("[object Window]", paramString);
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig.FrameArray
 * JD-Core Version:    0.6.2
 */