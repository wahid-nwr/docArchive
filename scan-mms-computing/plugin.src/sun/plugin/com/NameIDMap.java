package sun.plugin.com;

import java.util.HashMap;

class NameIDMap extends HashMap
{
  public int get(String paramString)
  {
    Integer localInteger = (Integer)super.get(paramString.toLowerCase());
    if (localInteger != null)
      return localInteger.intValue();
    return -1;
  }

  public void put(String paramString, int paramInt)
  {
    super.put(paramString.toLowerCase(), new Integer(paramInt));
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.com.NameIDMap
 * JD-Core Version:    0.6.2
 */