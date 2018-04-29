package sun.plugin.dom.css;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.stylesheets.MediaList;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.DOMObjectHelper;

public final class CSSImportRule extends CSSRule
  implements org.w3c.dom.css.CSSImportRule
{
  public CSSImportRule(DOMObject paramDOMObject, Document paramDocument, Node paramNode, CSSStyleSheet paramCSSStyleSheet, org.w3c.dom.css.CSSRule paramCSSRule)
  {
    super(paramDOMObject, paramDocument, paramNode, paramCSSStyleSheet, paramCSSRule);
  }

  public short getType()
  {
    return 3;
  }

  public String getHref()
  {
    return DOMObjectHelper.getStringMember(this.obj, "href");
  }

  public MediaList getMedia()
  {
    try
    {
      Object localObject1 = this.obj.getMember("media");
      if ((localObject1 != null) && ((localObject1 instanceof DOMObject)))
      {
        Object localObject2 = DOMObjectFactory.createStyleSheetObject((DOMObject)localObject1, this.document, this.ownerNode);
        if ((localObject2 != null) && ((localObject2 instanceof MediaList)))
          return (MediaList)localObject2;
      }
    }
    catch (DOMException localDOMException)
    {
    }
    return null;
  }

  public CSSStyleSheet getStyleSheet()
  {
    Object localObject1 = this.obj.getMember("styleSheet");
    if ((localObject1 != null) && ((localObject1 instanceof DOMObject)))
    {
      Object localObject2 = DOMObjectFactory.createStyleSheetObject((DOMObject)localObject1, this.document, this.ownerNode);
      if ((localObject2 != null) && ((localObject2 instanceof CSSStyleSheet)))
        return (CSSStyleSheet)localObject2;
    }
    return null;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.css.CSSImportRule
 * JD-Core Version:    0.6.2
 */