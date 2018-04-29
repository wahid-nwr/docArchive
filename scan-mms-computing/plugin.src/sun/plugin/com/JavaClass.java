package sun.plugin.com;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import sun.plugin.javascript.ReflectUtil;
import sun.plugin.resources.ResourceHandler;

public class JavaClass
{
  private static final boolean writeDebug = false;
  protected Class wrappedClass = null;
  private boolean collected = false;
  protected Method[] methods = null;
  protected Field[] fields = null;
  NameIDMap methIDMap = new NameIDMap();
  NameIDMap fieldIDMap = new NameIDMap();

  public JavaClass(Class paramClass)
  {
    this.wrappedClass = paramClass;
  }

  public String getName()
  {
    return this.wrappedClass.getName();
  }

  public Method getMethod(int paramInt)
  {
    return this.methods[(paramInt - 32768)];
  }

  public Method getMethod1(int paramInt, Object[] paramArrayOfObject)
    throws InvocationTargetException
  {
    int i = -1;
    int j = 2147483647;
    int k = 0;
    String str = getMethod(paramInt).getName();
    for (int m = this.methIDMap.get(str); m < this.methods.length; m++)
      if (this.methods[m].getName().equals(str))
      {
        Class[] arrayOfClass = this.methods[m].getParameterTypes();
        if (((paramArrayOfObject == null) || (paramArrayOfObject.length == 0)) && (arrayOfClass.length == 0))
          return this.methods[m];
        ParameterListCorrelator localParameterListCorrelator = new ParameterListCorrelator(arrayOfClass, paramArrayOfObject);
        if (localParameterListCorrelator.parametersCorrelateToClasses())
        {
          int n = localParameterListCorrelator.numberOfConversionsNeeded();
          if (n < j)
          {
            i = m;
            j = n;
            k = 0;
            if (n == 0)
              break;
          }
          else if (n == j)
          {
            k = 1;
          }
        }
      }
    if (k == 1)
      throw new InvocationTargetException(new Exception(ResourceHandler.getMessage("com.method.ambiguous")));
    if (j == 2147483647)
      throw new InvocationTargetException(new Exception(getMethod(paramInt).getName() + ResourceHandler.getMessage("com.method.notexists")));
    return this.methods[i];
  }

  public Field getField(int paramInt)
  {
    return this.fields[(paramInt - 4096)];
  }

  public Dispatcher getDispatcher(int paramInt1, int paramInt2, Object[] paramArrayOfObject)
    throws InvocationTargetException
  {
    Object localObject1 = null;
    Object localObject2;
    if ((0x8000 & paramInt2) > 0)
    {
      localObject2 = getMethod1(paramInt2, paramArrayOfObject);
      if (localObject2 != null)
        localObject1 = new MethodDispatcher((Method)localObject2);
    }
    if ((0x1000 & paramInt2) > 0)
    {
      localObject2 = getField(paramInt2);
      if (localObject2 != null)
        if ((paramInt1 & 0x2) > 0)
          localObject1 = new PropertyGetDispatcher((Field)localObject2);
        else
          localObject1 = new PropertySetDispatcher((Field)localObject2);
    }
    return localObject1;
  }

  protected synchronized void collect()
  {
    if (!this.collected)
    {
      this.methods = ReflectUtil.getJScriptMethods(this.wrappedClass);
      this.fields = ReflectUtil.getJScriptFields(this.wrappedClass);
      sort(this.methods);
      sort(this.fields);
      int i;
      if (this.methods != null)
        for (i = 0; i < this.methods.length; i++)
        {
          int j = this.methIDMap.get(this.methods[i].getName());
          if (j == -1)
            this.methIDMap.put(this.methods[i].getName(), i);
        }
      if (this.fields != null)
        for (i = 0; i < this.fields.length; i++)
          this.fieldIDMap.put(this.fields[i].getName(), i);
      this.collected = true;
    }
  }

  static void sort(Object[] paramArrayOfObject)
  {
    Arrays.sort(paramArrayOfObject, new Comparator()
    {
      public int compare(Object paramAnonymousObject1, Object paramAnonymousObject2)
      {
        String str1 = ((Member)paramAnonymousObject1).getName();
        String str2 = ((Member)paramAnonymousObject2).getName();
        return str1.compareTo(str2);
      }
    });
  }

  protected int getIdForName(String paramString)
    throws Exception
  {
    collect();
    int i = this.methIDMap.get(paramString);
    if (i != -1)
      return i + 32768;
    i = this.fieldIDMap.get(paramString);
    if (i != -1)
      return i + 4096;
    throw new Exception(paramString + ResourceHandler.getMessage("com.notexists"));
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.com.JavaClass
 * JD-Core Version:    0.6.2
 */