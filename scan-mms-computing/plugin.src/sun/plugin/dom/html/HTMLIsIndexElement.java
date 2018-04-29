package sun.plugin.dom.html;

import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLFormElement;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;

public final class HTMLIsIndexElement extends HTMLElement
  implements org.w3c.dom.html.HTMLIsIndexElement
{
  public HTMLIsIndexElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public HTMLFormElement getForm()
  {
    Object localObject1 = this.obj.getMember("form");
    if ((localObject1 != null) && ((localObject1 instanceof DOMObject)))
    {
      Object localObject2 = DOMObjectFactory.createHTMLObject((DOMObject)localObject1, (HTMLDocument)getOwnerDocument());
      if ((localObject2 != null) && ((localObject2 instanceof HTMLFormElement)))
        return (HTMLFormElement)localObject2;
    }
    return null;
  }

  public String getPrompt()
  {
    return getAttribute("prompt");
  }

  public void setPrompt(String paramString)
  {
    setAttribute("prompt", paramString);
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLIsIndexElement
 * JD-Core Version:    0.6.2
 */