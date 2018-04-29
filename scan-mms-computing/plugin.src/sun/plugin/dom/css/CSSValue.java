package sun.plugin.dom.css;

import java.io.PrintStream;
import org.w3c.dom.DOMException;
import sun.plugin.dom.exception.InvalidStateException;
import sun.plugin.dom.exception.NoModificationAllowedException;

public abstract class CSSValue
  implements org.w3c.dom.css.CSSValue
{
  protected String cssText = null;
  private String propertyName = null;
  private CSSStyleDeclaration css = null;
  private CSSValue parentCSSValue = null;

  protected CSSValue(CSSStyleDeclaration paramCSSStyleDeclaration, String paramString)
  {
    this.css = paramCSSStyleDeclaration;
    this.propertyName = paramString;
  }

  protected CSSValue(CSSValue paramCSSValue)
  {
    this.parentCSSValue = paramCSSValue;
  }

  public String getCssText()
  {
    return this.cssText;
  }

  public void setCssText(String paramString)
    throws DOMException
  {
    CSSValue localCSSValue1 = null;
    if (this.parentCSSValue != null)
      localCSSValue1 = newCSSValue(this.parentCSSValue, paramString);
    else
      localCSSValue1 = newCSSValue(this.css, this.propertyName, paramString);
    if (isSameType(localCSSValue1))
    {
      copy(localCSSValue1);
      this.cssText = paramString;
      for (CSSValue localCSSValue2 = this; localCSSValue2.parentCSSValue != null; localCSSValue2 = localCSSValue2.parentCSSValue);
      localCSSValue2.updateProperty();
    }
    else
    {
      throw new NoModificationAllowedException("Can not set cssText: " + paramString);
    }
  }

  public String toString()
  {
    return this.cssText;
  }

  protected void updateProperty()
  {
    this.css.setProperty(this.propertyName, toString(), null);
  }

  protected abstract boolean isSameType(CSSValue paramCSSValue);

  protected void copy(CSSValue paramCSSValue)
  {
    this.cssText = paramCSSValue.cssText;
  }

  public static CSSValue newCSSValue(CSSValue paramCSSValue, String paramString)
    throws DOMException
  {
    String str = paramString.trim();
    if (hasMultipleValues(str))
      return CSSValueList.newCSSValueList(paramCSSValue, str);
    return CSSPrimitiveValue.newCSSPrimitiveValue(paramCSSValue, str);
  }

  public static CSSValue newCSSValue(CSSStyleDeclaration paramCSSStyleDeclaration, String paramString1, String paramString2)
    throws DOMException
  {
    String str = paramString2.trim();
    System.out.println("New CSSValue for " + paramString1 + " = " + paramString2);
    if (hasMultipleValues(str))
      return CSSValueList.newCSSValueList(paramCSSStyleDeclaration, paramString1, str);
    return CSSPrimitiveValue.newCSSPrimitiveValue(paramCSSStyleDeclaration, paramString1, str);
  }

  private static boolean hasMultipleValues(String paramString)
    throws DOMException
  {
    for (int j = 0; j < paramString.length(); j++)
    {
      int i = paramString.charAt(j);
      switch (i)
      {
      case 32:
        return true;
      case 40:
        j = paramString.indexOf(")", j + 1);
        if (j == -1)
          throw new InvalidStateException("Invalid cssText: " + paramString);
        break;
      case 34:
        j = paramString.indexOf("\"", j + 1);
        if (j == -1)
          throw new InvalidStateException("Invalid cssText: " + paramString);
        break;
      }
    }
    return false;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.css.CSSValue
 * JD-Core Version:    0.6.2
 */