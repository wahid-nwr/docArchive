package sun.plugin.dom.css;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import org.w3c.dom.DOMException;
import sun.plugin.dom.exception.InvalidStateException;

public final class RGBColor
  implements org.w3c.dom.css.RGBColor
{
  private org.w3c.dom.css.CSSPrimitiveValue red;
  private org.w3c.dom.css.CSSPrimitiveValue green;
  private org.w3c.dom.css.CSSPrimitiveValue blue;

  protected RGBColor(org.w3c.dom.css.CSSPrimitiveValue paramCSSPrimitiveValue1, org.w3c.dom.css.CSSPrimitiveValue paramCSSPrimitiveValue2, org.w3c.dom.css.CSSPrimitiveValue paramCSSPrimitiveValue3)
  {
    this.red = paramCSSPrimitiveValue1;
    this.green = paramCSSPrimitiveValue2;
    this.blue = paramCSSPrimitiveValue3;
  }

  public org.w3c.dom.css.CSSPrimitiveValue getRed()
  {
    return this.red;
  }

  public org.w3c.dom.css.CSSPrimitiveValue getGreen()
  {
    return this.green;
  }

  public org.w3c.dom.css.CSSPrimitiveValue getBlue()
  {
    return this.blue;
  }

  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("rgb(");
    int i = (int)this.red.getFloatValue((short)1);
    localStringBuffer.append(i);
    localStringBuffer.append(',');
    i = (int)this.green.getFloatValue((short)1);
    localStringBuffer.append(i);
    localStringBuffer.append(',');
    i = (int)this.blue.getFloatValue((short)1);
    localStringBuffer.append(i);
    localStringBuffer.append(')');
    return localStringBuffer.toString();
  }

  public static RGBColor newRGBColor(CSSValue paramCSSValue, String paramString)
    throws DOMException
  {
    String str1;
    String str2;
    String str3;
    if (paramString.charAt(0) == '#')
    {
      try
      {
        str1 = paramString.substring(1, 3);
        str2 = paramString.substring(3, 5);
        str3 = paramString.substring(5, 7);
        str1 = Integer.valueOf(str1, 16).toString();
        str2 = Integer.valueOf(str2, 16).toString();
        str3 = Integer.valueOf(str3, 16).toString();
      }
      catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
      {
        throw new InvalidStateException("Invalid cssText: " + paramString);
      }
    }
    else
    {
      int i = paramString.indexOf('(');
      int j = paramString.indexOf(')');
      if ((i == -1) || (j == -1) || (i >= j))
        throw new InvalidStateException("Invalid cssText: " + paramString);
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString.substring(i + 1, j - 1), ",");
      try
      {
        str1 = localStringTokenizer.nextToken();
        str2 = localStringTokenizer.nextToken();
        str3 = localStringTokenizer.nextToken();
      }
      catch (NoSuchElementException localNoSuchElementException)
      {
        throw new InvalidStateException("Invalid cssText: " + paramString);
      }
    }
    return new RGBColor(CSSPrimitiveValue.newCSSPrimitiveValue(paramCSSValue, str1), CSSPrimitiveValue.newCSSPrimitiveValue(paramCSSValue, str2), CSSPrimitiveValue.newCSSPrimitiveValue(paramCSSValue, str3));
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.css.RGBColor
 * JD-Core Version:    0.6.2
 */