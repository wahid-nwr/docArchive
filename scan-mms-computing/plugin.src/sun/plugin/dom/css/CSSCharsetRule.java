package sun.plugin.dom.css;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleSheet;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectHelper;

public final class CSSCharsetRule extends CSSRule
  implements org.w3c.dom.css.CSSCharsetRule
{
  public CSSCharsetRule(DOMObject paramDOMObject, Document paramDocument, Node paramNode, CSSStyleSheet paramCSSStyleSheet, org.w3c.dom.css.CSSRule paramCSSRule)
  {
    super(paramDOMObject, paramDocument, paramNode, paramCSSStyleSheet, paramCSSRule);
  }

  public short getType()
  {
    return 2;
  }

  public String getEncoding()
  {
    return DOMObjectHelper.getStringMemberNoEx(this.obj, "encoding");
  }

  public void setEncoding(String paramString)
    throws DOMException
  {
    DOMObjectHelper.setStringMemberNoEx(this.obj, "encoding", paramString);
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.css.CSSCharsetRule
 * JD-Core Version:    0.6.2
 */