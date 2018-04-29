package sun.plugin.dom.css;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.stylesheets.MediaList;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.DOMObjectHelper;

public final class CSSMediaRule extends CSSRule
  implements org.w3c.dom.css.CSSMediaRule
{
  public CSSMediaRule(DOMObject paramDOMObject, Document paramDocument, Node paramNode, CSSStyleSheet paramCSSStyleSheet, org.w3c.dom.css.CSSRule paramCSSRule)
  {
    super(paramDOMObject, paramDocument, paramNode, paramCSSStyleSheet, paramCSSRule);
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

  public CSSRuleList getCssRules()
  {
    Object localObject1 = null;
    try
    {
      localObject1 = this.obj.getMember("cssRules");
    }
    catch (DOMException localDOMException1)
    {
      try
      {
        localObject1 = this.obj.getMember("rules");
      }
      catch (DOMException localDOMException2)
      {
      }
    }
    if ((localObject1 != null) && ((localObject1 instanceof DOMObject)))
    {
      Object localObject2 = DOMObjectFactory.createCSSObject((DOMObject)localObject1, this.document, this.ownerNode, this.parentStyleSheet, this.parentRule);
      if ((localObject2 != null) && ((localObject2 instanceof CSSRuleList)))
        return (CSSRuleList)localObject2;
    }
    return null;
  }

  public int insertRule(String paramString, int paramInt)
    throws DOMException
  {
    String str = null;
    try
    {
      str = DOMObjectHelper.callStringMethod(this.obj, "addRule", new Object[] { new Integer(paramInt) });
    }
    catch (DOMException localDOMException)
    {
      str = DOMObjectHelper.callStringMethod(this.obj, "insertRule", new Object[] { new Integer(paramInt) });
    }
    if (str != null)
      return Integer.parseInt(str);
    return 0;
  }

  public void deleteRule(int paramInt)
    throws DOMException
  {
    try
    {
      this.obj.call("removeRule", new Object[] { new Integer(paramInt) });
    }
    catch (DOMException localDOMException)
    {
      this.obj.call("deleteRule", new Object[] { new Integer(paramInt) });
    }
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.css.CSSMediaRule
 * JD-Core Version:    0.6.2
 */