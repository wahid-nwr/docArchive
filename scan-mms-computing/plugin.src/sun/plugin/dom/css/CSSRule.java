package sun.plugin.dom.css;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleSheet;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectHelper;

public class CSSRule
  implements org.w3c.dom.css.CSSRule
{
  protected DOMObject obj;
  protected Document document;
  protected Node ownerNode;
  protected CSSStyleSheet parentStyleSheet;
  protected org.w3c.dom.css.CSSRule parentRule;

  public CSSRule(DOMObject paramDOMObject, Document paramDocument, Node paramNode, CSSStyleSheet paramCSSStyleSheet, org.w3c.dom.css.CSSRule paramCSSRule)
  {
    this.obj = paramDOMObject;
    this.document = paramDocument;
    this.ownerNode = paramNode;
    this.parentStyleSheet = paramCSSStyleSheet;
    this.parentRule = paramCSSRule;
  }

  public short getType()
  {
    return 0;
  }

  public String getCssText()
  {
    return DOMObjectHelper.getStringMemberNoEx(this.obj, "cssText");
  }

  public void setCssText(String paramString)
    throws DOMException
  {
    DOMObjectHelper.setStringMember(this.obj, "cssText", paramString);
  }

  public CSSStyleSheet getParentStyleSheet()
  {
    return this.parentStyleSheet;
  }

  public org.w3c.dom.css.CSSRule getParentRule()
  {
    return this.parentRule;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.css.CSSRule
 * JD-Core Version:    0.6.2
 */