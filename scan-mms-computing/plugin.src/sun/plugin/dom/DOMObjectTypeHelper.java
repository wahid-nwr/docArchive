package sun.plugin.dom;

import com.sun.deploy.services.ServiceManager;
import sun.plugin.dom.core.Text;
import sun.plugin.services.BrowserService;

final class DOMObjectTypeHelper
{
  private static final String HTML_IMPLEMENTATION_PACKAGE = "sun.plugin.dom.html";
  private static final String SSL_IMPLEMENTATION_PACKAGE = "sun.plugin.dom.stylesheets";
  private static final String CSS_IMPLEMENTATION_PACKAGE = "sun.plugin.dom.css";
  private static final String DOM_IMPLEMENTATION_PACKAGE = "sun.plugin.dom.core";

  static Class getDOMCoreClass(DOMObject paramDOMObject)
  {
    Object localObject = getObjectClass(paramDOMObject, "sun.plugin.dom.core");
    if (localObject == null)
      localObject = Text.class;
    return localObject;
  }

  static Class getHTMLElementClass(DOMObject paramDOMObject)
  {
    return getObjectClass(paramDOMObject, "sun.plugin.dom.html");
  }

  static Class getCSSRuleClass(DOMObject paramDOMObject)
  {
    return getObjectClass(paramDOMObject, "sun.plugin.dom.css");
  }

  static Class getStyleSheetClass(DOMObject paramDOMObject)
  {
    Class localClass = getObjectClass(paramDOMObject, "sun.plugin.dom.css");
    if (null != localClass)
      return localClass;
    return getObjectClass(paramDOMObject, "sun.plugin.dom.stylesheets");
  }

  private static Class getObjectClass(DOMObject paramDOMObject, String paramString)
  {
    String str1 = paramDOMObject.toString();
    String str2 = getObjectType(str1);
    if (str2 == null)
      str2 = str1;
    BrowserService localBrowserService = (BrowserService)ServiceManager.getService();
    str2 = localBrowserService.mapBrowserElement(str2);
    if (str2 != null)
    {
      StringBuffer localStringBuffer = new StringBuffer(paramString);
      localStringBuffer.append('.');
      localStringBuffer.append(str2);
      try
      {
        return Class.forName(localStringBuffer.toString());
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
      }
    }
    return null;
  }

  private static String getObjectType(String paramString)
  {
    paramString = paramString.trim();
    if (!paramString.endsWith("]"))
      return null;
    int i = paramString.lastIndexOf(' ');
    if (i == -1)
      return null;
    return paramString.substring(i + 1, paramString.length() - 1);
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.DOMObjectTypeHelper
 * JD-Core Version:    0.6.2
 */