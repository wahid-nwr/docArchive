package sun.plugin.dom.stylesheets;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.stylesheets.MediaList;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.DOMObjectHelper;

public class StyleSheet
  implements org.w3c.dom.stylesheets.StyleSheet
{
  protected DOMObject obj;
  protected Document doc;
  protected Node owner;

  public StyleSheet(DOMObject paramDOMObject, Document paramDocument, Node paramNode)
  {
    this.obj = paramDOMObject;
    this.doc = paramDocument;
    this.owner = paramNode;
  }

  public String getType()
  {
    return DOMObjectHelper.getStringMemberNoEx(this.obj, "type");
  }

  public boolean getDisabled()
  {
    return DOMObjectHelper.getBooleanMemberNoEx(this.obj, "disabled");
  }

  public void setDisabled(boolean paramBoolean)
  {
    DOMObjectHelper.setBooleanMemberNoEx(this.obj, "disabled", paramBoolean);
  }

  public Node getOwnerNode()
  {
    if (this.owner != null)
      return this.owner;
    try
    {
      Object localObject = this.obj.getMember("owningElement");
      if ((localObject != null) && ((localObject instanceof DOMObject)))
        return DOMObjectFactory.createHTMLElement((DOMObject)localObject, (HTMLDocument)this.doc);
    }
    catch (DOMException localDOMException)
    {
    }
    return null;
  }

  public org.w3c.dom.stylesheets.StyleSheet getParentStyleSheet()
  {
    try
    {
      Object localObject = this.obj.getMember("parentStyleSheet");
      if ((localObject != null) && ((localObject instanceof DOMObject)))
        return DOMObjectFactory.createStyleSheet((DOMObject)localObject, this.doc, getOwnerNode());
    }
    catch (DOMException localDOMException)
    {
    }
    return null;
  }

  public String getHref()
  {
    return DOMObjectHelper.getStringMemberNoEx(this.obj, "href");
  }

  public String getTitle()
  {
    return DOMObjectHelper.getStringMemberNoEx(this.obj, "title");
  }

  public MediaList getMedia()
  {
    try
    {
      Object localObject1 = this.obj.getMember("media");
      if ((localObject1 != null) && ((localObject1 instanceof DOMObject)))
      {
        Object localObject2 = DOMObjectFactory.createStyleSheetObject((DOMObject)localObject1, this.doc, this.owner);
        if ((localObject2 != null) && ((localObject2 instanceof MediaList)))
          return (MediaList)localObject2;
      }
    }
    catch (DOMException localDOMException)
    {
    }
    return null;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.stylesheets.StyleSheet
 * JD-Core Version:    0.6.2
 */