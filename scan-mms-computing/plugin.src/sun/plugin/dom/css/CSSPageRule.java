package sun.plugin.dom.css;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleSheet;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.DOMObjectHelper;

public final class CSSPageRule extends CSSRule
  implements org.w3c.dom.css.CSSPageRule
{
  public CSSPageRule(DOMObject paramDOMObject, Document paramDocument, Node paramNode, CSSStyleSheet paramCSSStyleSheet, org.w3c.dom.css.CSSRule paramCSSRule)
  {
    super(paramDOMObject, paramDocument, paramNode, paramCSSStyleSheet, paramCSSRule);
  }

  public String getSelectorText()
  {
    return DOMObjectHelper.getStringMemberNoEx(this.obj, "selectorText");
  }

  public void setSelectorText(String paramString)
    throws DOMException
  {
    DOMObjectHelper.setStringMemberNoEx(this.obj, "selectorText", paramString);
  }

  public CSSStyleDeclaration getStyle()
  {
    Object localObject1 = this.obj.getMember("style");
    if ((localObject1 != null) && ((localObject1 instanceof DOMObject)))
    {
      Object localObject2 = DOMObjectFactory.createCSSObject((DOMObject)localObject1, this.document, this.ownerNode, this.parentStyleSheet, this.parentRule);
      if ((localObject2 != null) && ((localObject2 instanceof CSSStyleDeclaration)))
        return (CSSStyleDeclaration)localObject2;
    }
    return null;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.css.CSSPageRule
 * JD-Core Version:    0.6.2
 */