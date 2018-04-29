package sun.plugin.dom;

import com.sun.java.browser.dom.DOMUnsupportedException;
import java.applet.Applet;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import org.w3c.dom.Document;
import sun.plugin.dom.html.HTMLDocument;

public class DOMServiceProvider extends com.sun.java.browser.dom.DOMServiceProvider
{
  public boolean canHandle(Object paramObject)
  {
    return (paramObject != null) && ((paramObject instanceof Applet));
  }

  public Document getDocument(Object paramObject)
    throws DOMUnsupportedException
  {
    try
    {
      if (canHandle(paramObject))
      {
        JSObject localJSObject1 = JSObject.getWindow((Applet)paramObject);
        if (localJSObject1 == null)
          throw new JSException("Unable to obtain Window object");
        JSObject localJSObject2 = (JSObject)localJSObject1.getMember("document");
        if (localJSObject2 == null)
          throw new JSException("Unable to obtain Document object");
        return new HTMLDocument(new DOMObject(localJSObject2), null);
      }
    }
    catch (JSException localJSException)
    {
      localJSException.printStackTrace();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    throw new DOMUnsupportedException();
  }

  public org.w3c.dom.DOMImplementation getDOMImplementation()
  {
    return new DOMImplementation();
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.DOMServiceProvider
 * JD-Core Version:    0.6.2
 */