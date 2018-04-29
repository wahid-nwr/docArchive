package sun.plugin.javascript.navig;

import netscape.javascript.JSException;

public abstract interface JSObjectResolver
{
  public abstract Object resolveObject(JSObject paramJSObject, String paramString1, int paramInt, String paramString2, Object paramObject)
    throws JSException;
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.javascript.navig.JSObjectResolver
 * JD-Core Version:    0.6.2
 */