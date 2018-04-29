package sun.plugin.dom.css;

import java.util.HashMap;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.Counter;
import sun.plugin.dom.exception.InvalidAccessException;
import sun.plugin.dom.exception.InvalidStateException;
import sun.plugin.dom.exception.NoModificationAllowedException;

public final class CSSPrimitiveValue extends CSSValue
  implements org.w3c.dom.css.CSSPrimitiveValue
{
  private static final String RGB = "rgb";
  private static final String RECT = "rect";
  private static final String COLOR = "#";
  private short primitiveType = 0;
  private Object value = null;
  private static HashMap unit2Type = null;
  private static HashMap type2Unit = null;

  protected CSSPrimitiveValue(CSSStyleDeclaration paramCSSStyleDeclaration, String paramString)
  {
    super(paramCSSStyleDeclaration, paramString);
  }

  protected CSSPrimitiveValue(CSSValue paramCSSValue)
  {
    super(paramCSSValue);
  }

  public short getCssValueType()
  {
    return 1;
  }

  public short getPrimitiveType()
  {
    return this.primitiveType;
  }

  public void setFloatValue(short paramShort, float paramFloat)
    throws DOMException
  {
    if ((paramShort == this.primitiveType) && (isFloatType(paramShort)))
    {
      this.value = new Float(paramFloat);
      updateProperty();
    }
    else
    {
      throw new NoModificationAllowedException("Can not set: " + paramFloat);
    }
  }

  public float getFloatValue(short paramShort)
    throws DOMException
  {
    if (isFloatType(this.primitiveType))
      return ((Float)this.value).floatValue();
    throw new InvalidAccessException("Not a Float value");
  }

  private boolean isFloatType(short paramShort)
  {
    return (paramShort == 1) || (paramShort == 2) || (paramShort == 3) || (paramShort == 4) || (paramShort == 5) || (paramShort == 6) || (paramShort == 7) || (paramShort == 8) || (paramShort == 9) || (paramShort == 10) || (paramShort == 11) || (paramShort == 12) || (paramShort == 13) || (paramShort == 14) || (paramShort == 15) || (paramShort == 16) || (paramShort == 17) || (paramShort == 18);
  }

  public void setStringValue(short paramShort, String paramString)
    throws DOMException
  {
    if ((this.primitiveType == paramShort) && (isStringType(paramShort)))
    {
      this.value = paramString;
      updateProperty();
    }
    else
    {
      throw new NoModificationAllowedException("Can not set: " + paramString);
    }
  }

  public String getStringValue()
    throws DOMException
  {
    if (isStringType(this.primitiveType))
      return (String)this.value;
    throw new InvalidAccessException("Not a String value");
  }

  private boolean isStringType(short paramShort)
  {
    return (19 == paramShort) || (20 == paramShort) || (21 == paramShort) || (22 == paramShort);
  }

  public Counter getCounterValue()
    throws DOMException
  {
    if (23 == this.primitiveType)
      return (Counter)this.value;
    throw new InvalidAccessException("Not a Counter value");
  }

  public org.w3c.dom.css.Rect getRectValue()
    throws DOMException
  {
    if (24 == this.primitiveType)
      return (org.w3c.dom.css.Rect)this.value;
    throw new InvalidAccessException("Not a Rect value");
  }

  public org.w3c.dom.css.RGBColor getRGBColorValue()
    throws DOMException
  {
    if (25 == this.primitiveType)
      return (org.w3c.dom.css.RGBColor)this.value;
    throw new InvalidAccessException("Not a RGBColor value");
  }

  public String toString()
  {
    if (isStringType(this.primitiveType))
      return (String)this.value;
    if (isFloatType(this.primitiveType))
    {
      String str = (String)getType2UnitMap().get(new Short(this.primitiveType));
      if (str == null)
        return ((Float)this.value).toString();
      return ((Float)this.value).toString() + str;
    }
    if ((this.primitiveType == 24) || (this.primitiveType == 25))
      return this.value.toString();
    if (this.primitiveType == 23);
    return null;
  }

  protected boolean isSameType(CSSValue paramCSSValue)
  {
    return (getCssValueType() == paramCSSValue.getCssValueType()) && (((CSSPrimitiveValue)paramCSSValue).getPrimitiveType() == getPrimitiveType());
  }

  protected void copy(CSSValue paramCSSValue)
  {
    CSSPrimitiveValue localCSSPrimitiveValue = (CSSPrimitiveValue)paramCSSValue;
    this.value = localCSSPrimitiveValue.value;
  }

  public static CSSPrimitiveValue newCSSPrimitiveValue(CSSStyleDeclaration paramCSSStyleDeclaration, String paramString1, String paramString2)
    throws DOMException
  {
    CSSPrimitiveValue localCSSPrimitiveValue = new CSSPrimitiveValue(paramCSSStyleDeclaration, paramString1);
    return newCSSPrimitiveValue(localCSSPrimitiveValue, paramString2);
  }

  public static CSSPrimitiveValue newCSSPrimitiveValue(CSSValue paramCSSValue, String paramString)
    throws DOMException
  {
    CSSPrimitiveValue localCSSPrimitiveValue = new CSSPrimitiveValue(paramCSSValue);
    return newCSSPrimitiveValue(localCSSPrimitiveValue, paramString);
  }

  private static CSSPrimitiveValue newCSSPrimitiveValue(CSSPrimitiveValue paramCSSPrimitiveValue, String paramString)
    throws DOMException
  {
    Object localObject;
    if ((paramString.startsWith("#")) || (paramString.startsWith("rgb")))
    {
      localObject = RGBColor.newRGBColor(paramCSSPrimitiveValue, paramString);
      paramCSSPrimitiveValue.value = localObject;
      paramCSSPrimitiveValue.primitiveType = 25;
      return paramCSSPrimitiveValue;
    }
    if (paramString.startsWith("rect"))
    {
      localObject = Rect.newRect(paramCSSPrimitiveValue, paramString);
      paramCSSPrimitiveValue.value = localObject;
      paramCSSPrimitiveValue.primitiveType = 24;
      return paramCSSPrimitiveValue;
    }
    int i = splitCssText(paramString);
    if (i == -1)
      throw new InvalidStateException("Invalid cssText: " + paramString);
    if (i == 0)
    {
      paramCSSPrimitiveValue.value = paramString;
      paramCSSPrimitiveValue.primitiveType = 19;
      return paramCSSPrimitiveValue;
    }
    if (i == paramString.length())
    {
      paramCSSPrimitiveValue.value = new Float(paramString);
      paramCSSPrimitiveValue.primitiveType = 1;
      return paramCSSPrimitiveValue;
    }
    String str1 = paramString.substring(0, i);
    String str2 = paramString.substring(i);
    paramCSSPrimitiveValue.value = new Float(str1);
    Short localShort = (Short)getUnit2TypeMap().get(str2.toLowerCase());
    if (localShort == null)
      paramCSSPrimitiveValue.primitiveType = 0;
    else
      paramCSSPrimitiveValue.primitiveType = localShort.shortValue();
    return paramCSSPrimitiveValue;
  }

  private static int splitCssText(String paramString)
  {
    int j = 0;
    for (int i = 0; i < paramString.length(); i++)
    {
      char c = paramString.charAt(i);
      if (!Character.isDigit(c))
      {
        if (c != '.')
          break;
        if (j != 0)
          return -1;
        j = 1;
      }
    }
    return i;
  }

  private static synchronized HashMap getUnit2TypeMap()
  {
    if (unit2Type == null)
    {
      unit2Type = new HashMap();
      unit2Type.put("%", new Short((short)2));
      unit2Type.put("ems", new Short((short)3));
      unit2Type.put("exs", new Short((short)4));
      unit2Type.put("px", new Short((short)5));
      unit2Type.put("cm", new Short((short)6));
      unit2Type.put("mm", new Short((short)7));
      unit2Type.put("in", new Short((short)8));
      unit2Type.put("pt", new Short((short)9));
      unit2Type.put("pc", new Short((short)10));
      unit2Type.put("deg", new Short((short)11));
      unit2Type.put("rad", new Short((short)12));
      unit2Type.put("grad", new Short((short)13));
      unit2Type.put("ms", new Short((short)14));
      unit2Type.put("s", new Short((short)15));
      unit2Type.put("hz", new Short((short)16));
      unit2Type.put("khz", new Short((short)17));
    }
    return unit2Type;
  }

  private static synchronized HashMap getType2UnitMap()
  {
    if (type2Unit == null)
    {
      type2Unit = new HashMap();
      type2Unit.put(new Short((short)2), "%");
      type2Unit.put(new Short((short)3), "ems");
      type2Unit.put(new Short((short)4), "exs");
      type2Unit.put(new Short((short)5), "px");
      type2Unit.put(new Short((short)6), "cm");
      type2Unit.put(new Short((short)7), "mm");
      type2Unit.put(new Short((short)8), "in");
      type2Unit.put(new Short((short)9), "pt");
      type2Unit.put(new Short((short)10), "pc");
      type2Unit.put(new Short((short)11), "deg");
      type2Unit.put(new Short((short)12), "rad");
      type2Unit.put(new Short((short)13), "grad");
      type2Unit.put(new Short((short)14), "ms");
      type2Unit.put(new Short((short)15), "s");
      type2Unit.put(new Short((short)16), "hz");
      type2Unit.put(new Short((short)17), "khz");
    }
    return type2Unit;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.css.CSSPrimitiveValue
 * JD-Core Version:    0.6.2
 */