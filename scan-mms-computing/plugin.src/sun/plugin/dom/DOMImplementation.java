package sun.plugin.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.DOMImplementationCSS;
import org.w3c.dom.html.HTMLDOMImplementation;
import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.exception.PluginNotSupportedException;

public class DOMImplementation
  implements org.w3c.dom.DOMImplementation, HTMLDOMImplementation, DOMImplementationCSS
{
  public boolean hasFeature(String paramString1, String paramString2)
  {
    if (paramString1 == null)
      return false;
    if (paramString2 == null)
      paramString2 = "2.0";
    return (paramString2.equals("2.0")) && ((paramString1.equalsIgnoreCase("dom")) || (paramString1.equalsIgnoreCase("xml")) || (paramString1.equalsIgnoreCase("html")) || (paramString1.equalsIgnoreCase("stylesheets")) || (paramString1.equalsIgnoreCase("views")) || (paramString1.equalsIgnoreCase("css")));
  }

  public DocumentType createDocumentType(String paramString1, String paramString2, String paramString3)
    throws DOMException
  {
    throw new PluginNotSupportedException("DOMImplementation.createDocumentType() is not supported");
  }

  public Document createDocument(String paramString1, String paramString2, DocumentType paramDocumentType)
    throws DOMException
  {
    throw new PluginNotSupportedException("DOMImplementation.createDocument() is not supported");
  }

  public HTMLDocument createHTMLDocument(String paramString)
  {
    throw new PluginNotSupportedException("HTMLDOMImplementation.createHTMLDocument() is not supported");
  }

  public CSSStyleSheet createCSSStyleSheet(String paramString1, String paramString2)
    throws DOMException
  {
    throw new PluginNotSupportedException("DOMImplementationCSS.createCSSStyleSheet() is not supported");
  }

  public Object getFeature(String paramString1, String paramString2)
  {
    throw new PluginNotSupportedException("DOMImplementation.getFeature() is not supported.");
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.DOMImplementation
 * JD-Core Version:    0.6.2
 */