package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;

public final class HTMLMapElement extends HTMLElement
  implements org.w3c.dom.html.HTMLMapElement
{
  public HTMLMapElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public HTMLCollection getAreas()
  {
    Object localObject1 = this.obj.getMember("areas");
    if ((localObject1 != null) && ((localObject1 instanceof DOMObject)))
    {
      Object localObject2 = DOMObjectFactory.createHTMLObject((DOMObject)localObject1, (HTMLDocument)getOwnerDocument());
      if ((localObject2 instanceof HTMLCollection))
        return (HTMLCollection)localObject2;
    }
    return null;
  }

  public String getName()
  {
    return getAttribute("name");
  }

  public void setName(String paramString)
  {
    setAttribute("name", paramString);
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLMapElement
 * JD-Core Version:    0.6.2
 */