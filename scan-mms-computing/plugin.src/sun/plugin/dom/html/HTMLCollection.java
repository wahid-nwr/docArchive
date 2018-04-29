package sun.plugin.dom.html;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.DOMObjectHelper;

public class HTMLCollection
  implements org.w3c.dom.html.HTMLCollection, NodeList
{
  protected DOMObject obj;
  protected HTMLDocument doc;

  public HTMLCollection(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    this.obj = paramDOMObject;
    this.doc = paramHTMLDocument;
  }

  public int getLength()
  {
    return DOMObjectHelper.getIntMember(this.obj, "length");
  }

  public Node item(int paramInt)
  {
    Object localObject1 = this.obj.getSlot(paramInt);
    if ((localObject1 != null) && ((localObject1 instanceof DOMObject)))
    {
      Object localObject2 = DOMObjectFactory.createCommonDOMObject((DOMObject)localObject1, this.doc);
      if ((localObject2 != null) && ((localObject2 instanceof Node)))
        return (Node)localObject2;
    }
    return null;
  }

  public Node namedItem(String paramString)
  {
    Object localObject1 = this.obj.getMember(paramString);
    if ((localObject1 != null) && ((localObject1 instanceof DOMObject)))
    {
      Object localObject2 = DOMObjectFactory.createCommonDOMObject((DOMObject)localObject1, this.doc);
      if ((localObject2 != null) && ((localObject2 instanceof Node)))
        return (Node)localObject2;
    }
    return null;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLCollection
 * JD-Core Version:    0.6.2
 */