package sun.plugin.dom.stylesheets;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.stylesheets.StyleSheet;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.DOMObjectHelper;

public class StyleSheetList
  implements org.w3c.dom.stylesheets.StyleSheetList
{
  private DOMObject obj;
  private Document doc;
  private Node owner;

  public StyleSheetList(DOMObject paramDOMObject, Document paramDocument, Node paramNode)
  {
    this.obj = paramDOMObject;
    this.doc = paramDocument;
    this.owner = paramNode;
  }

  public int getLength()
  {
    return DOMObjectHelper.getIntMemberNoEx(this.obj, "length");
  }

  public StyleSheet item(int paramInt)
  {
    try
    {
      Object localObject = this.obj.getSlot(paramInt);
      if ((localObject != null) && ((localObject instanceof DOMObject)))
      {
        StyleSheet localStyleSheet = DOMObjectFactory.createStyleSheet((DOMObject)localObject, this.doc, this.owner);
        if ((localStyleSheet != null) && ((localStyleSheet instanceof StyleSheet)))
          return (StyleSheet)localStyleSheet;
      }
    }
    catch (DOMException localDOMException)
    {
    }
    return null;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.stylesheets.StyleSheetList
 * JD-Core Version:    0.6.2
 */