package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectHelper;

public final class HTMLDListElement extends HTMLElement
  implements org.w3c.dom.html.HTMLDListElement
{
  public HTMLDListElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public boolean getCompact()
  {
    return DOMObjectHelper.getBooleanMember(this.obj, "compact");
  }

  public void setCompact(boolean paramBoolean)
  {
    DOMObjectHelper.setBooleanMember(this.obj, "compact", paramBoolean);
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLDListElement
 * JD-Core Version:    0.6.2
 */