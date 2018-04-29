package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectHelper;

public class HTMLFormElement extends HTMLElement
  implements org.w3c.dom.html.HTMLFormElement
{
  public HTMLFormElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public org.w3c.dom.html.HTMLCollection getElements()
  {
    Object localObject = this.obj.getMember("elements");
    if ((localObject != null) && ((localObject instanceof DOMObject)))
      return new HTMLCollection((DOMObject)localObject, (HTMLDocument)getOwnerDocument());
    return null;
  }

  public int getLength()
  {
    return DOMObjectHelper.getIntMember(this.obj, "length");
  }

  public String getName()
  {
    return getAttribute("name");
  }

  public void setName(String paramString)
  {
    setAttribute("name", paramString);
  }

  public String getAcceptCharset()
  {
    return getAttribute("acceptCharset");
  }

  public void setAcceptCharset(String paramString)
  {
    setAttribute("acceptCharset", paramString);
  }

  public String getAction()
  {
    return getAttribute("action");
  }

  public void setAction(String paramString)
  {
    setAttribute("action", paramString);
  }

  public String getEnctype()
  {
    return getAttribute("enctype");
  }

  public void setEnctype(String paramString)
  {
    setAttribute("enctype", paramString);
  }

  public String getMethod()
  {
    return getAttribute("method");
  }

  public void setMethod(String paramString)
  {
    setAttribute("method", paramString);
  }

  public String getTarget()
  {
    return getAttribute("target");
  }

  public void setTarget(String paramString)
  {
    setAttribute("target", paramString);
  }

  public void submit()
  {
    this.obj.call("submit", new Object[0]);
  }

  public void reset()
  {
    this.obj.call("reset", new Object[0]);
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLFormElement
 * JD-Core Version:    0.6.2
 */