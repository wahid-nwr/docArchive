package sun.plugin.dom;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.html.HTMLDocument;
import sun.plugin.dom.exception.PluginNotSupportedException;
import sun.plugin.dom.html.HTMLAnchorElement;
import sun.plugin.dom.html.HTMLAppletElement;
import sun.plugin.dom.html.HTMLAreaElement;
import sun.plugin.dom.html.HTMLBRElement;
import sun.plugin.dom.html.HTMLBaseElement;
import sun.plugin.dom.html.HTMLBodyElement;
import sun.plugin.dom.html.HTMLButtonElement;
import sun.plugin.dom.html.HTMLDListElement;
import sun.plugin.dom.html.HTMLDirectoryElement;
import sun.plugin.dom.html.HTMLDivElement;
import sun.plugin.dom.html.HTMLFieldSetElement;
import sun.plugin.dom.html.HTMLFontElement;
import sun.plugin.dom.html.HTMLFormElement;
import sun.plugin.dom.html.HTMLFrameElement;
import sun.plugin.dom.html.HTMLFrameSetElement;
import sun.plugin.dom.html.HTMLHRElement;
import sun.plugin.dom.html.HTMLHeadElement;
import sun.plugin.dom.html.HTMLHeadingElement;
import sun.plugin.dom.html.HTMLHtmlElement;
import sun.plugin.dom.html.HTMLIFrameElement;
import sun.plugin.dom.html.HTMLImageElement;
import sun.plugin.dom.html.HTMLInputElement;
import sun.plugin.dom.html.HTMLIsIndexElement;
import sun.plugin.dom.html.HTMLLIElement;
import sun.plugin.dom.html.HTMLLabelElement;
import sun.plugin.dom.html.HTMLLegendElement;
import sun.plugin.dom.html.HTMLLinkElement;
import sun.plugin.dom.html.HTMLMapElement;
import sun.plugin.dom.html.HTMLMenuElement;
import sun.plugin.dom.html.HTMLMetaElement;
import sun.plugin.dom.html.HTMLModElement;
import sun.plugin.dom.html.HTMLOListElement;
import sun.plugin.dom.html.HTMLObjectElement;
import sun.plugin.dom.html.HTMLOptGroupElement;
import sun.plugin.dom.html.HTMLOptionElement;
import sun.plugin.dom.html.HTMLParagraphElement;
import sun.plugin.dom.html.HTMLParamElement;
import sun.plugin.dom.html.HTMLPreElement;
import sun.plugin.dom.html.HTMLQuoteElement;
import sun.plugin.dom.html.HTMLScriptElement;
import sun.plugin.dom.html.HTMLSelectElement;
import sun.plugin.dom.html.HTMLStyleElement;
import sun.plugin.dom.html.HTMLTableCaptionElement;
import sun.plugin.dom.html.HTMLTableCellElement;
import sun.plugin.dom.html.HTMLTableColElement;
import sun.plugin.dom.html.HTMLTableElement;
import sun.plugin.dom.html.HTMLTableRowElement;
import sun.plugin.dom.html.HTMLTableSectionElement;
import sun.plugin.dom.html.HTMLTextAreaElement;
import sun.plugin.dom.html.HTMLTitleElement;
import sun.plugin.dom.html.HTMLUListElement;

public class DOMObjectFactory
{
  private static final String HTML_TAGNAME = "tagName";
  private static final String ATTR_TYPE = "type";
  private static HashMap elmTagClassMap = null;
  private static HashMap elmTypeClassMap = null;

  public static org.w3c.dom.html.HTMLElement createHTMLElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    Object localObject = createHTMLObject(paramDOMObject, paramHTMLDocument);
    if ((localObject instanceof org.w3c.dom.html.HTMLElement))
      return (org.w3c.dom.html.HTMLElement)localObject;
    return null;
  }

  public static Object createCommonDOMObject(DOMObject paramDOMObject, Document paramDocument)
  {
    if (paramDOMObject == null)
      return null;
    Class localClass = DOMObjectTypeHelper.getHTMLElementClass(paramDOMObject);
    if (localClass == null)
      localClass = getRealClassByTagName(paramDOMObject);
    if (localClass != null)
      return createHTMLObject(localClass, paramDOMObject, (HTMLDocument)paramDocument);
    localClass = DOMObjectTypeHelper.getDOMCoreClass(paramDOMObject);
    return createDOMCoreObject(localClass, paramDOMObject, paramDocument);
  }

  private static Object createDOMCoreObject(Class paramClass, DOMObject paramDOMObject, Document paramDocument)
  {
    try
    {
      Class[] arrayOfClass = { DOMObject.class, Document.class };
      Constructor localConstructor = paramClass.getConstructor(arrayOfClass);
      Object[] arrayOfObject = { paramDOMObject, paramDocument };
      return localConstructor.newInstance(arrayOfObject);
    }
    catch (Exception localException)
    {
    }
    throw new PluginNotSupportedException("DOMObjectFactory::createDOMCoreObject() cannot wrap " + paramDOMObject);
  }

  public static Object createHTMLObject(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    if (paramDOMObject == null)
      return null;
    Object localObject = DOMObjectTypeHelper.getHTMLElementClass(paramDOMObject);
    if (localObject == null)
      localObject = sun.plugin.dom.html.HTMLElement.class;
    return createHTMLObject((Class)localObject, paramDOMObject, paramHTMLDocument);
  }

  private static Object createHTMLObject(Class paramClass, DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    if ((paramClass != null) && ((paramClass.equals(sun.plugin.dom.html.HTMLElement.class)) || (paramClass.equals(HTMLObjectElement.class)) || (paramClass.equals(HTMLUListElement.class)) || (paramClass.equals(HTMLButtonElement.class)) || (paramClass.equals(HTMLOptionElement.class)) || (paramClass.equals(HTMLInputElement.class)) || (paramClass.equals(HTMLQuoteElement.class))))
    {
      Class localClass = getRealClassByTagName(paramDOMObject);
      if (localClass != null)
        paramClass = localClass;
    }
    try
    {
      Class[] arrayOfClass = { DOMObject.class, HTMLDocument.class };
      Constructor localConstructor = paramClass.getConstructor(arrayOfClass);
      Object[] arrayOfObject = { paramDOMObject, paramHTMLDocument };
      return localConstructor.newInstance(arrayOfObject);
    }
    catch (Exception localException)
    {
    }
    throw new PluginNotSupportedException("DOMObjectFactory::createHTMLElement() cannot wrap " + paramDOMObject);
  }

  public static Object createStyleSheetObject(DOMObject paramDOMObject, Document paramDocument, Node paramNode)
  {
    if (paramDOMObject == null)
      return null;
    Object localObject1 = DOMObjectTypeHelper.getStyleSheetClass(paramDOMObject);
    if (localObject1 != null)
    {
      Object localObject2;
      if (localObject1.equals(sun.plugin.dom.stylesheets.StyleSheet.class))
      {
        localObject2 = getRealClassByType(paramDOMObject);
        if (localObject2 != null)
          localObject1 = localObject2;
      }
      try
      {
        localObject2 = new Class[] { DOMObject.class, Document.class, Node.class };
        Constructor localConstructor = ((Class)localObject1).getConstructor((Class[])localObject2);
        Object[] arrayOfObject = { paramDOMObject, paramDocument, paramNode };
        return localConstructor.newInstance(arrayOfObject);
      }
      catch (Exception localException)
      {
      }
    }
    throw new PluginNotSupportedException("DOMObjectFactory::createStyleSheet() cannot wrap " + paramDOMObject);
  }

  public static org.w3c.dom.stylesheets.StyleSheet createStyleSheet(DOMObject paramDOMObject, Document paramDocument, Node paramNode)
  {
    Object localObject = createStyleSheetObject(paramDOMObject, paramDocument, paramNode);
    if ((localObject != null) && ((localObject instanceof org.w3c.dom.stylesheets.StyleSheet)))
      return (org.w3c.dom.stylesheets.StyleSheet)localObject;
    return null;
  }

  public static Object createCSSObject(DOMObject paramDOMObject, Document paramDocument, Node paramNode, org.w3c.dom.css.CSSStyleSheet paramCSSStyleSheet, CSSRule paramCSSRule)
  {
    if (paramDOMObject == null)
      return null;
    Class localClass = DOMObjectTypeHelper.getCSSRuleClass(paramDOMObject);
    if (localClass != null)
    {
      if (org.w3c.dom.stylesheets.StyleSheet.class.isAssignableFrom(localClass))
        return createStyleSheet(paramDOMObject, paramDocument, paramNode);
      try
      {
        Class[] arrayOfClass = { DOMObject.class, Document.class, Node.class, org.w3c.dom.css.CSSStyleSheet.class, CSSRule.class };
        Constructor localConstructor = localClass.getConstructor(arrayOfClass);
        Object[] arrayOfObject = { paramDOMObject, paramDocument, paramNode, paramCSSStyleSheet, paramCSSRule };
        return localConstructor.newInstance(arrayOfObject);
      }
      catch (Exception localException)
      {
      }
    }
    throw new PluginNotSupportedException("DOMObjectFactory::createCSSRuleObject() cannot wrap " + paramDOMObject);
  }

  private static Class getRealClassByType(DOMObject paramDOMObject)
  {
    try
    {
      Object localObject = paramDOMObject.getMember("type");
      if (localObject != null)
        return (Class)getElmTypeClassMap().get(localObject);
    }
    catch (DOMException localDOMException)
    {
    }
    return null;
  }

  private static Class getRealClassByTagName(DOMObject paramDOMObject)
  {
    try
    {
      Object localObject = paramDOMObject.getMember("tagName");
      if (localObject != null)
        return (Class)getElmTagClassMap().get(localObject);
    }
    catch (DOMException localDOMException)
    {
    }
    return null;
  }

  private static synchronized HashMap getElmTagClassMap()
  {
    if (elmTagClassMap == null)
    {
      elmTagClassMap = new HashMap();
      elmTagClassMap.put("A", HTMLAnchorElement.class);
      elmTagClassMap.put("APPLET", HTMLAppletElement.class);
      elmTagClassMap.put("AREA", HTMLAreaElement.class);
      elmTagClassMap.put("BASE", HTMLBaseElement.class);
      elmTagClassMap.put("BLOCKQUOTE", HTMLQuoteElement.class);
      elmTagClassMap.put("BODY", HTMLBodyElement.class);
      elmTagClassMap.put("BR", HTMLBRElement.class);
      elmTagClassMap.put("CAPTION", HTMLTableCaptionElement.class);
      elmTagClassMap.put("COL", HTMLTableColElement.class);
      elmTagClassMap.put("DEL", HTMLModElement.class);
      elmTagClassMap.put("DIR", HTMLDirectoryElement.class);
      elmTagClassMap.put("DIV", HTMLDivElement.class);
      elmTagClassMap.put("DL", HTMLDListElement.class);
      elmTagClassMap.put("FIELDSET", HTMLFieldSetElement.class);
      elmTagClassMap.put("FONT", HTMLFontElement.class);
      elmTagClassMap.put("FORM", HTMLFormElement.class);
      elmTagClassMap.put("FRAME", HTMLFrameElement.class);
      elmTagClassMap.put("FRAMESET", HTMLFrameSetElement.class);
      elmTagClassMap.put("HEAD", HTMLHeadElement.class);
      elmTagClassMap.put("H1", HTMLHeadingElement.class);
      elmTagClassMap.put("H2", HTMLHeadingElement.class);
      elmTagClassMap.put("H3", HTMLHeadingElement.class);
      elmTagClassMap.put("H4", HTMLHeadingElement.class);
      elmTagClassMap.put("H5", HTMLHeadingElement.class);
      elmTagClassMap.put("H6", HTMLHeadingElement.class);
      elmTagClassMap.put("HR", HTMLHRElement.class);
      elmTagClassMap.put("HTML", HTMLHtmlElement.class);
      elmTagClassMap.put("IFRAME", HTMLIFrameElement.class);
      elmTagClassMap.put("IMAGE", HTMLImageElement.class);
      elmTagClassMap.put("INPUT", HTMLInputElement.class);
      elmTagClassMap.put("INS", HTMLModElement.class);
      elmTagClassMap.put("ISINDEX", HTMLIsIndexElement.class);
      elmTagClassMap.put("LABEL", HTMLLabelElement.class);
      elmTagClassMap.put("LEGEND", HTMLLegendElement.class);
      elmTagClassMap.put("LI", HTMLLIElement.class);
      elmTagClassMap.put("LINK", HTMLLinkElement.class);
      elmTagClassMap.put("MAP", HTMLMapElement.class);
      elmTagClassMap.put("MENU", HTMLMenuElement.class);
      elmTagClassMap.put("META", HTMLMetaElement.class);
      elmTagClassMap.put("MOD", HTMLModElement.class);
      elmTagClassMap.put("OBJECT", HTMLObjectElement.class);
      elmTagClassMap.put("OL", HTMLOListElement.class);
      elmTagClassMap.put("OPTGROUP", HTMLOptGroupElement.class);
      elmTagClassMap.put("OPTION", HTMLOptionElement.class);
      elmTagClassMap.put("P", HTMLParagraphElement.class);
      elmTagClassMap.put("PARAM", HTMLParamElement.class);
      elmTagClassMap.put("PRE", HTMLPreElement.class);
      elmTagClassMap.put("Q", HTMLQuoteElement.class);
      elmTagClassMap.put("SCRIPT", HTMLScriptElement.class);
      elmTagClassMap.put("SELECT", HTMLSelectElement.class);
      elmTagClassMap.put("STYLE", HTMLStyleElement.class);
      elmTagClassMap.put("TABLE", HTMLTableElement.class);
      elmTagClassMap.put("TBODY", HTMLTableSectionElement.class);
      elmTagClassMap.put("TD", HTMLTableCellElement.class);
      elmTagClassMap.put("TFOOT", HTMLTableSectionElement.class);
      elmTagClassMap.put("TH", HTMLTableCellElement.class);
      elmTagClassMap.put("THEAD", HTMLTableSectionElement.class);
      elmTagClassMap.put("TR", HTMLTableRowElement.class);
      elmTagClassMap.put("TEXTARRA", HTMLTextAreaElement.class);
      elmTagClassMap.put("TITLE", HTMLTitleElement.class);
      elmTagClassMap.put("UL", HTMLUListElement.class);
    }
    return elmTagClassMap;
  }

  private static synchronized HashMap getElmTypeClassMap()
  {
    if (elmTypeClassMap == null)
    {
      elmTypeClassMap = new HashMap();
      elmTypeClassMap.put("text/css", sun.plugin.dom.css.CSSStyleSheet.class);
    }
    return elmTypeClassMap;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.DOMObjectFactory
 * JD-Core Version:    0.6.2
 */