package sun.plugin.dom.html;

import java.util.ArrayList;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.DocumentCSS;
import org.w3c.dom.css.ElementCSSInlineStyle;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.stylesheets.DocumentStyle;
import org.w3c.dom.stylesheets.StyleSheetList;
import org.w3c.dom.views.AbstractView;
import org.w3c.dom.views.DocumentView;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.DOMObjectHelper;
import sun.plugin.dom.core.Document;
import sun.plugin.dom.css.ViewCSS;
import sun.plugin.dom.exception.PluginNotSupportedException;

public class HTMLDocument extends Document
  implements org.w3c.dom.html.HTMLDocument, DocumentView, DocumentStyle, DocumentCSS
{
  private static final String TAG_HTML = "HTML";
  private static ArrayList list = new ArrayList();

  public HTMLDocument(DOMObject paramDOMObject, org.w3c.dom.html.HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public String getTitle()
  {
    return getAttribute("title");
  }

  public void setTitle(String paramString)
  {
    setAttribute("title", paramString);
  }

  public String getReferrer()
  {
    return getAttribute("referrer");
  }

  public String getDomain()
  {
    return getAttribute("domain");
  }

  public String getURL()
  {
    return getAttribute("URL");
  }

  public HTMLElement getBody()
  {
    Object localObject1 = this.obj.getMember("body");
    if ((localObject1 != null) && ((localObject1 instanceof DOMObject)))
    {
      Object localObject2 = DOMObjectFactory.createHTMLObject((DOMObject)localObject1, this);
      if ((localObject2 != null) && ((localObject2 instanceof HTMLElement)))
        return (HTMLElement)localObject2;
    }
    return null;
  }

  public void setBody(HTMLElement paramHTMLElement)
  {
    throw new PluginNotSupportedException("HTMLDocument.setBody() is not supported");
  }

  public HTMLCollection getImages()
  {
    Object localObject1 = this.obj.getMember("images");
    if ((localObject1 != null) && ((localObject1 instanceof DOMObject)))
    {
      Object localObject2 = DOMObjectFactory.createHTMLObject((DOMObject)localObject1, this);
      if ((localObject2 != null) && ((localObject2 instanceof HTMLCollection)))
        return (HTMLCollection)localObject2;
    }
    return null;
  }

  public HTMLCollection getApplets()
  {
    Object localObject1 = this.obj.getMember("applets");
    if ((localObject1 != null) && ((localObject1 instanceof DOMObject)))
    {
      Object localObject2 = DOMObjectFactory.createHTMLObject((DOMObject)localObject1, this);
      if ((localObject2 != null) && ((localObject2 instanceof HTMLCollection)))
        return (HTMLCollection)localObject2;
    }
    return null;
  }

  public HTMLCollection getLinks()
  {
    Object localObject1 = this.obj.getMember("links");
    if ((localObject1 != null) && ((localObject1 instanceof DOMObject)))
    {
      Object localObject2 = DOMObjectFactory.createHTMLObject((DOMObject)localObject1, this);
      if ((localObject2 != null) && ((localObject2 instanceof HTMLCollection)))
        return (HTMLCollection)localObject2;
    }
    return null;
  }

  public HTMLCollection getForms()
  {
    Object localObject1 = this.obj.getMember("forms");
    if ((localObject1 != null) && ((localObject1 instanceof DOMObject)))
    {
      Object localObject2 = DOMObjectFactory.createHTMLObject((DOMObject)localObject1, this);
      if ((localObject2 != null) && ((localObject2 instanceof HTMLCollection)))
        return (HTMLCollection)localObject2;
    }
    return null;
  }

  public HTMLCollection getAnchors()
  {
    Object localObject1 = this.obj.getMember("anchors");
    if ((localObject1 != null) && ((localObject1 instanceof DOMObject)))
    {
      Object localObject2 = DOMObjectFactory.createHTMLObject((DOMObject)localObject1, this);
      if ((localObject2 != null) && ((localObject2 instanceof HTMLCollection)))
        return (HTMLCollection)localObject2;
    }
    return null;
  }

  public String getCookie()
  {
    return getAttribute("cookie");
  }

  public void setCookie(String paramString)
  {
    setAttribute("cookie", paramString);
  }

  public void open()
  {
    throw new PluginNotSupportedException("HTMLDocument.open() is not supported");
  }

  public void close()
  {
    throw new PluginNotSupportedException("HTMLDocument.close() is not supported");
  }

  public void write(String paramString)
  {
    throw new PluginNotSupportedException("HTMLDocument.write() is not supported");
  }

  public void writeln(String paramString)
  {
    throw new PluginNotSupportedException("HTMLDocument.writeln() is not supported");
  }

  public NodeList getElementsByName(String paramString)
  {
    Object localObject1 = this.obj.call("getElementsByName", new Object[] { paramString });
    if (localObject1 == null)
      return null;
    if ((localObject1 instanceof DOMObject))
    {
      Object localObject2 = DOMObjectFactory.createHTMLObject((DOMObject)localObject1, this);
      if ((localObject2 != null) && ((localObject2 instanceof NodeList)))
        return (NodeList)localObject2;
    }
    return null;
  }

  public NodeList getElementsByTagName(String paramString)
  {
    Object localObject1 = this.obj.call("getElementsByTagName", new Object[] { paramString });
    if (localObject1 == null)
      return null;
    if ((localObject1 instanceof DOMObject))
    {
      Object localObject2 = DOMObjectFactory.createHTMLObject((DOMObject)localObject1, this);
      if ((localObject2 != null) && ((localObject2 instanceof NodeList)))
        return (NodeList)localObject2;
    }
    return null;
  }

  public Element getDocumentElement()
  {
    Object localObject = this.obj.getMember("documentElement");
    if (localObject == null)
      return null;
    if ((localObject instanceof DOMObject))
    {
      HTMLElement localHTMLElement = DOMObjectFactory.createHTMLElement((DOMObject)localObject, this);
      if ((localHTMLElement != null) && ((localHTMLElement instanceof Element)))
        return (Element)localHTMLElement;
    }
    return null;
  }

  public Element createElement(String paramString)
    throws DOMException
  {
    Object localObject = this.obj.call("createElement", new Object[] { paramString });
    if ((localObject != null) && ((localObject instanceof DOMObject)))
    {
      HTMLElement localHTMLElement = DOMObjectFactory.createHTMLElement((DOMObject)localObject, this);
      if ((localHTMLElement != null) && ((localHTMLElement instanceof Element)))
      {
        list.add(localHTMLElement);
        return (Element)localHTMLElement;
      }
    }
    return null;
  }

  public Element getElementById(String paramString)
  {
    Object localObject = this.obj.call("getElementById", new Object[] { paramString });
    if (localObject == null)
      return null;
    if ((localObject instanceof DOMObject))
    {
      HTMLElement localHTMLElement = DOMObjectFactory.createHTMLElement((DOMObject)localObject, this);
      return (Element)localHTMLElement;
    }
    return null;
  }

  private String getAttribute(String paramString)
  {
    return DOMObjectHelper.getStringMember(this.obj, paramString);
  }

  private void setAttribute(String paramString1, String paramString2)
  {
    DOMObjectHelper.setStringMember(this.obj, paramString1, paramString2);
  }

  public AbstractView getDefaultView()
  {
    return new ViewCSS(this);
  }

  public StyleSheetList getStyleSheets()
  {
    Object localObject1 = this.obj.getMember("styleSheets");
    if (localObject1 == null)
      return null;
    if ((localObject1 instanceof DOMObject))
    {
      Object localObject2 = DOMObjectFactory.createStyleSheetObject((DOMObject)localObject1, this, null);
      if ((localObject2 != null) && ((localObject2 instanceof StyleSheetList)))
        return (StyleSheetList)localObject2;
    }
    return null;
  }

  public CSSStyleDeclaration getOverrideStyle(Element paramElement, String paramString)
  {
    if ((paramElement instanceof ElementCSSInlineStyle))
    {
      ElementCSSInlineStyle localElementCSSInlineStyle = (ElementCSSInlineStyle)paramElement;
      return localElementCSSInlineStyle.getStyle();
    }
    return null;
  }

  public NodeList getChildNodes()
  {
    return getElementsByTagName("HTML");
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLDocument
 * JD-Core Version:    0.6.2
 */