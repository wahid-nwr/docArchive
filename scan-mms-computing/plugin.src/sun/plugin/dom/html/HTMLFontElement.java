package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;

public final class HTMLFontElement extends HTMLElement
  implements org.w3c.dom.html.HTMLFontElement
{
  public HTMLFontElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public String getColor()
  {
    return getAttribute("color");
  }

  public void setColor(String paramString)
  {
    setAttribute("color", paramString);
  }

  public String getFace()
  {
    return getAttribute("face");
  }

  public void setFace(String paramString)
  {
    setAttribute("face", paramString);
  }

  public String getSize()
  {
    return getAttribute("size");
  }

  public void setSize(String paramString)
  {
    setAttribute("size", paramString);
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLFontElement
 * JD-Core Version:    0.6.2
 */