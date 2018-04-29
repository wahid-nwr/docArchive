package sun.plugin.dom.css;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleSheet;
import sun.plugin.dom.DOMObject;

public final class CSSUnknownRule extends CSSRule
  implements org.w3c.dom.css.CSSUnknownRule
{
  public CSSUnknownRule(DOMObject paramDOMObject, Document paramDocument, Node paramNode, CSSStyleSheet paramCSSStyleSheet, org.w3c.dom.css.CSSRule paramCSSRule)
  {
    super(paramDOMObject, paramDocument, paramNode, paramCSSStyleSheet, paramCSSRule);
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.css.CSSUnknownRule
 * JD-Core Version:    0.6.2
 */