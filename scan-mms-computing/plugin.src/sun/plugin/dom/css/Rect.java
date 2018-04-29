package sun.plugin.dom.css;

import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import org.w3c.dom.DOMException;
import sun.plugin.dom.exception.InvalidStateException;

public final class Rect
  implements org.w3c.dom.css.Rect
{
  private org.w3c.dom.css.CSSPrimitiveValue top;
  private org.w3c.dom.css.CSSPrimitiveValue right;
  private org.w3c.dom.css.CSSPrimitiveValue bottom;
  private org.w3c.dom.css.CSSPrimitiveValue left;

  protected Rect(CSSPrimitiveValue paramCSSPrimitiveValue1, CSSPrimitiveValue paramCSSPrimitiveValue2, CSSPrimitiveValue paramCSSPrimitiveValue3, CSSPrimitiveValue paramCSSPrimitiveValue4)
  {
    this.top = paramCSSPrimitiveValue1;
    this.left = paramCSSPrimitiveValue4;
    this.bottom = paramCSSPrimitiveValue3;
    this.right = paramCSSPrimitiveValue2;
  }

  public org.w3c.dom.css.CSSPrimitiveValue getTop()
  {
    return this.top;
  }

  public org.w3c.dom.css.CSSPrimitiveValue getRight()
  {
    return this.right;
  }

  public org.w3c.dom.css.CSSPrimitiveValue getBottom()
  {
    return this.bottom;
  }

  public org.w3c.dom.css.CSSPrimitiveValue getLeft()
  {
    return this.left;
  }

  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("rect(");
    localStringBuffer.append(this.top.toString());
    localStringBuffer.append(' ');
    localStringBuffer.append(this.right.toString());
    localStringBuffer.append(' ');
    localStringBuffer.append(this.bottom.toString());
    localStringBuffer.append(' ');
    localStringBuffer.append(this.left.toString());
    localStringBuffer.append(')');
    return localStringBuffer.toString();
  }

  public static Rect newRect(CSSValue paramCSSValue, String paramString)
    throws DOMException
  {
    int i = paramString.indexOf('(');
    int j = paramString.indexOf(')');
    if ((i == -1) || (j == -1) || (i >= j))
      throw new InvalidStateException("Invalid cssText: " + paramString);
    String str1 = paramString.substring(i + 1, j);
    str1 = str1.trim();
    StringTokenizer localStringTokenizer = new StringTokenizer(str1);
    String str2;
    String str3;
    String str4;
    String str5;
    try
    {
      str2 = localStringTokenizer.nextToken();
      str3 = localStringTokenizer.nextToken();
      str4 = localStringTokenizer.nextToken();
      str5 = localStringTokenizer.nextToken();
      System.out.println("top: " + str2 + " right: " + str3 + " bottom: " + str4 + " left: " + str5);
    }
    catch (NoSuchElementException localNoSuchElementException)
    {
      throw new InvalidStateException("Invalid cssText: " + paramString);
    }
    if (localStringTokenizer.hasMoreTokens())
      throw new InvalidStateException("Invalid cssText: " + paramString);
    return new Rect(CSSPrimitiveValue.newCSSPrimitiveValue(paramCSSValue, str2), CSSPrimitiveValue.newCSSPrimitiveValue(paramCSSValue, str3), CSSPrimitiveValue.newCSSPrimitiveValue(paramCSSValue, str4), CSSPrimitiveValue.newCSSPrimitiveValue(paramCSSValue, str5));
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.css.Rect
 * JD-Core Version:    0.6.2
 */