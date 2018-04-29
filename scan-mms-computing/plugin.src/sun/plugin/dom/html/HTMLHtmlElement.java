package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;

public final class HTMLHtmlElement extends HTMLElement
  implements org.w3c.dom.html.HTMLHtmlElement
{
  public HTMLHtmlElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public String getVersion()
  {
    return getAttribute("version");
  }

  public void setVersion(String paramString)
  {
    setAttribute("version", paramString);
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLHtmlElement
 * JD-Core Version:    0.6.2
 */