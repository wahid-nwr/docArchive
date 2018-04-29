package sun.plugin.com;

import java.lang.reflect.InvocationTargetException;
import netscape.javascript.JSObject;
import sun.plugin.resources.ResourceHandler;

public class ParameterListCorrelator
{
  private static final int BOOL_CODE = 0;
  private static final int BYTE_CODE = 1;
  private static final int SHORT_CODE = 2;
  private static final int INT_CODE = 3;
  private static final int LONG_CODE = 4;
  private static final int FLOAT_CODE = 5;
  private static final int DOUBLE_CODE = 6;
  private static final int CHAR_CODE = 7;
  private static final int NOT_PRIMITIVE_CODE = 8;
  private Class[] expectedClasses;
  private Object[] givenParameters;
  private boolean analysisIsDone;
  private boolean parametersCorrelateToClasses;
  private int numberOfConversionsNeeded;

  ParameterListCorrelator(Class[] paramArrayOfClass, Object[] paramArrayOfObject)
  {
    this.expectedClasses = paramArrayOfClass;
    if (this.expectedClasses == null)
      this.expectedClasses = new Class[0];
    this.givenParameters = paramArrayOfObject;
    if (this.givenParameters == null)
      this.givenParameters = new Object[0];
    this.analysisIsDone = false;
    this.numberOfConversionsNeeded = 0;
    this.parametersCorrelateToClasses = false;
  }

  boolean parametersCorrelateToClasses()
  {
    analyze();
    return this.parametersCorrelateToClasses;
  }

  int numberOfConversionsNeeded()
    throws InvocationTargetException
  {
    analyze();
    if (!this.parametersCorrelateToClasses)
    {
      IllegalArgumentException localIllegalArgumentException = new IllegalArgumentException(ResourceHandler.getMessage("com.method.argsTypeInvalid"));
      throw new InvocationTargetException(localIllegalArgumentException);
    }
    return this.numberOfConversionsNeeded;
  }

  private void analyze()
  {
    if (!this.analysisIsDone)
    {
      this.parametersCorrelateToClasses = true;
      if (this.expectedClasses.length != this.givenParameters.length)
        reportParametersDoNotCorrelate();
      for (int i = 0; (!this.analysisIsDone) && (i < this.givenParameters.length); i++)
        analyzeParameter(this.expectedClasses[i], this.givenParameters[i]);
      this.analysisIsDone = true;
    }
  }

  private void analyzeParameter(Class paramClass, Object paramObject)
  {
    if (paramObject != null)
      analyzeParameter(paramClass, paramObject.getClass());
  }

  private void analyzeParameter(Class paramClass1, Class paramClass2)
  {
    if (paramClass2 == null)
      return;
    if (paramClass1.equals(paramClass2))
      return;
    if (paramClass1.isAssignableFrom(paramClass2))
      this.numberOfConversionsNeeded += 1;
    else if (paramClass1 == String.class)
      this.numberOfConversionsNeeded += 1;
    else if (((paramClass1.isPrimitive()) || (Number.class.isAssignableFrom(paramClass1)) || (paramClass1 == Character.class) || (paramClass1 == Boolean.class)) && (paramClass2 == String.class))
      this.numberOfConversionsNeeded += 1;
    else if (paramClass1.isArray())
    {
      if (!paramClass2.isArray())
      {
        reportParametersDoNotCorrelate();
      }
      else
      {
        Class localClass1 = paramClass1.getComponentType();
        Class localClass2 = paramClass2.getComponentType();
        if (!localClass2.equals(Object.class))
          analyzeParameter(localClass1, localClass2);
      }
    }
    else if (paramClass1.equals(JSObject.class))
      this.numberOfConversionsNeeded += 1;
    else if (paramClass1.equals(DispatchImpl.class))
      this.numberOfConversionsNeeded += 1;
    else if (!typesAreEquivalentPrimitives(paramClass1, paramClass2))
      reportParametersDoNotCorrelate();
  }

  private boolean typesAreEquivalentPrimitives(Class paramClass1, Class paramClass2)
  {
    int i = typeCodeFromTypeName(paramClass1.getName());
    if (i != 8)
    {
      int j = typeCodeFromTypeName(paramClass2.getName());
      if (j == 8)
        return false;
      if (i != j)
        this.numberOfConversionsNeeded += 1;
      return true;
    }
    return false;
  }

  private static int typeCodeFromTypeName(String paramString)
  {
    if ((paramString.equals("boolean")) || (paramString.equals("java.lang.Boolean")))
      return 0;
    if ((paramString.equals("byte")) || (paramString.equals("java.lang.Byte")))
      return 1;
    if ((paramString.equals("short")) || (paramString.equals("java.lang.Short")))
      return 2;
    if ((paramString.equals("int")) || (paramString.equals("java.lang.Integer")))
      return 3;
    if ((paramString.equals("long")) || (paramString.equals("java.lang.Long")))
      return 4;
    if ((paramString.equals("float")) || (paramString.equals("java.lang.Float")))
      return 5;
    if ((paramString.equals("double")) || (paramString.equals("java.lang.Double")))
      return 6;
    if ((paramString.equals("char")) || (paramString.equals("java.lang.Character")))
      return 7;
    return 8;
  }

  private void reportParametersDoNotCorrelate()
  {
    this.parametersCorrelateToClasses = false;
    this.analysisIsDone = true;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.com.ParameterListCorrelator
 * JD-Core Version:    0.6.2
 */