package sun.plugin.dom.css;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleSheet;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.DOMObjectHelper;

public final class CSSRuleList
  implements org.w3c.dom.css.CSSRuleList
{
  private DOMObject obj;
  private CSSStyleSheet parentStyleSheet;
  private CSSRule parentRule;
  private Document document;
  private Node ownerNode;

  public CSSRuleList(DOMObject paramDOMObject, Document paramDocument, Node paramNode, CSSStyleSheet paramCSSStyleSheet, CSSRule paramCSSRule)
  {
    this.obj = paramDOMObject;
    this.document = paramDocument;
    this.ownerNode = paramNode;
    this.parentStyleSheet = paramCSSStyleSheet;
    this.parentRule = paramCSSRule;
  }

  public int getLength()
  {
    return DOMObjectHelper.getIntMemberNoEx(this.obj, "length");
  }

  public CSSRule item(int paramInt)
  {
    Object localObject1 = this.obj.getSlot(paramInt);
    if ((localObject1 != null) && ((localObject1 instanceof DOMObject)))
    {
      Object localObject2 = DOMObjectFactory.createCSSObject((DOMObject)localObject1, this.document, this.ownerNode, this.parentStyleSheet, this.parentRule);
      if ((localObject2 != null) && ((localObject2 instanceof CSSRule)))
        return (CSSRule)localObject2;
    }
    return null;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.css.CSSRuleList
 * JD-Core Version:    0.6.2
 */