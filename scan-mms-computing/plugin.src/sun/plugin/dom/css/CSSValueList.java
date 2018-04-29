package sun.plugin.dom.css;

import java.io.PrintStream;
import java.util.ArrayList;
import org.w3c.dom.DOMException;
import sun.plugin.dom.exception.InvalidStateException;

public final class CSSValueList extends CSSValue
  implements org.w3c.dom.css.CSSValueList
{
  private ArrayList values = new ArrayList();

  protected CSSValueList(CSSStyleDeclaration paramCSSStyleDeclaration, String paramString)
  {
    super(paramCSSStyleDeclaration, paramString);
  }

  protected CSSValueList(CSSValue paramCSSValue)
  {
    super(paramCSSValue);
  }

  public short getCssValueType()
  {
    return 2;
  }

  public int getLength()
  {
    return this.values.size();
  }

  public org.w3c.dom.css.CSSValue item(int paramInt)
  {
    return (CSSValue)this.values.get(paramInt);
  }

  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = getLength();
    for (int j = 0; j < getLength(); j++)
    {
      localStringBuffer.append(item(j).toString());
      if (j < i - 1)
        localStringBuffer.append(' ');
    }
    return localStringBuffer.toString();
  }

  protected boolean isSameType(CSSValue paramCSSValue)
  {
    if (paramCSSValue.getCssValueType() != getCssValueType())
      return false;
    CSSValueList localCSSValueList = (CSSValueList)paramCSSValue;
    if (localCSSValueList.getLength() != getLength())
      return false;
    for (int i = 0; i < localCSSValueList.getLength(); i++)
    {
      CSSValue localCSSValue2 = (CSSValue)localCSSValueList.item(i);
      CSSValue localCSSValue1 = (CSSValue)item(i);
      if (!localCSSValue1.isSameType(localCSSValue2))
        return false;
    }
    return true;
  }

  protected void copy(CSSValue paramCSSValue)
  {
    super.copy(paramCSSValue);
    CSSValueList localCSSValueList = (CSSValueList)paramCSSValue;
    this.values.clear();
    for (int i = 0; i < localCSSValueList.getLength(); i++)
      addValue((CSSValue)localCSSValueList.item(i));
  }

  private void addValue(CSSValue paramCSSValue)
  {
    this.values.add(paramCSSValue);
  }

  public static CSSValueList newCSSValueList(CSSValue paramCSSValue, String paramString)
    throws DOMException
  {
    CSSValueList localCSSValueList = new CSSValueList(paramCSSValue);
    return newCSSValueList(localCSSValueList, paramString);
  }

  public static CSSValueList newCSSValueList(CSSStyleDeclaration paramCSSStyleDeclaration, String paramString1, String paramString2)
    throws DOMException
  {
    CSSValueList localCSSValueList = new CSSValueList(paramCSSStyleDeclaration, paramString1);
    return newCSSValueList(localCSSValueList, paramString2);
  }

  private static CSSValueList newCSSValueList(CSSValueList paramCSSValueList, String paramString)
    throws DOMException
  {
    String str1 = paramString.trim();
    int i;
    while ((i = getNextCssText(str1)) > 0)
    {
      System.out.println("Return index: " + i);
      if (str1.charAt(i) != ' ')
        i++;
      String str2 = str1.substring(0, i);
      System.out.println("Return cssText: " + str2);
      paramCSSValueList.addValue(CSSValue.newCSSValue(paramCSSValueList, str2));
      if (i >= str1.length())
        break;
      str1 = str1.substring(i + 1);
      System.out.println("Rest cssText: " + str1);
    }
    paramCSSValueList.cssText = paramString;
    return paramCSSValueList;
  }

  private static int getNextCssText(String paramString)
    throws DOMException
  {
    for (int j = 0; j < paramString.length(); j++)
    {
      int i = paramString.charAt(j);
      switch (i)
      {
      case 32:
        return j;
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
    return j - 1;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.css.CSSValueList
 * JD-Core Version:    0.6.2
 */