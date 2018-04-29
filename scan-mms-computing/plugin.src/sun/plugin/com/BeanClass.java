package sun.plugin.com;

import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;
import sun.plugin.resources.ResourceHandler;

public class BeanClass extends JavaClass
{
  private BeanInfo bInfo = null;
  private boolean collected = false;
  private MethodDescriptor[] methods = null;
  private PropertyDescriptor[] props = null;
  private EventSetDescriptor[] eds = null;
  NameIDMap evtIDMap = new NameIDMap();
  Method[] evtMethods = null;

  public BeanClass(Class paramClass)
  {
    super(paramClass);
    try
    {
      this.bInfo = Introspector.getBeanInfo(this.wrappedClass);
    }
    catch (IntrospectionException localIntrospectionException)
    {
      localIntrospectionException.printStackTrace();
    }
  }

  private MethodDescriptor getMethodDescriptor(int paramInt)
  {
    return this.methods[(paramInt - 32768)];
  }

  private PropertyDescriptor getProperty(int paramInt)
  {
    return this.props[(paramInt - 4096)];
  }

  private Method getEventMethod(int paramInt)
  {
    return this.evtMethods[(paramInt - 16384)];
  }

  public BeanInfo getBeanInfo()
  {
    return this.bInfo;
  }

  private MethodDescriptor getMethodDescriptor1(int paramInt, Object[] paramArrayOfObject)
    throws InvocationTargetException
  {
    int i = -1;
    int j = 2147483647;
    int k = 0;
    String str = getMethodDescriptor(paramInt).getName();
    for (int m = this.methIDMap.get(str); m < this.methods.length; m++)
      if (this.methods[m].getName().equals(str))
      {
        Class[] arrayOfClass = this.methods[m].getMethod().getParameterTypes();
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
      throw new InvocationTargetException(new Exception(getMethodDescriptor(paramInt).getName() + ResourceHandler.getMessage("com.method.notexists")));
    return this.methods[i];
  }

  public Dispatcher getDispatcher(int paramInt1, int paramInt2, Object[] paramArrayOfObject)
    throws InvocationTargetException
  {
    collect();
    MethodDispatcher localMethodDispatcher = null;
    Object localObject;
    if ((0x8000 & paramInt2) > 0)
    {
      localObject = getMethodDescriptor1(paramInt2, paramArrayOfObject);
      if (localObject != null)
        localMethodDispatcher = new MethodDispatcher(((MethodDescriptor)localObject).getMethod());
    }
    if ((0x1000 & paramInt2) > 0)
    {
      localObject = getProperty(paramInt2);
      if (localObject != null)
        if ((paramInt1 & 0x2) > 0)
          localMethodDispatcher = new MethodDispatcher(((PropertyDescriptor)localObject).getReadMethod());
        else
          localMethodDispatcher = new MethodDispatcher(((PropertyDescriptor)localObject).getWriteMethod());
    }
    return localMethodDispatcher;
  }

  protected synchronized void collect()
  {
    if (!this.collected)
    {
      this.methods = this.bInfo.getMethodDescriptors();
      this.props = this.bInfo.getPropertyDescriptors();
      this.eds = this.bInfo.getEventSetDescriptors();
      Packager.sort(this.methods);
      Packager.sort(this.props);
      Vector localVector = new Vector();
      int k;
      for (int i = 0; i < this.eds.length; i++)
      {
        Method[] arrayOfMethod = this.eds[i].getListenerMethods();
        for (k = 0; k < arrayOfMethod.length; k++)
          localVector.addElement(arrayOfMethod[k]);
      }
      this.evtMethods = ((Method[])localVector.toArray(new Method[0]));
      sort(this.evtMethods);
      if (this.methods != null)
        for (i = 0; i < this.methods.length; i++)
        {
          int j = this.methIDMap.get(this.methods[i].getName());
          if (j == -1)
            this.methIDMap.put(this.methods[i].getName(), i);
        }
      if (this.props != null)
        for (i = 0; i < this.props.length; i++)
          this.fieldIDMap.put(this.props[i].getName(), i);
      if (this.eds != null)
        for (i = 0; i < this.evtMethods.length; i++)
        {
          String str = this.evtMethods[i].getName();
          k = this.evtIDMap.get(str);
          if (k == -1)
            this.evtIDMap.put(str, i);
        }
      this.collected = true;
    }
  }

  protected int getIdForName(String paramString)
    throws Exception
  {
    collect();
    int i = -1;
    i = this.methIDMap.get(paramString);
    if (i != -1)
      return i + 32768;
    i = getPropertyId(paramString);
    if (i == -1)
      i = getEventId(paramString);
    if (i == -1)
      throw new Exception(paramString + ResourceHandler.getMessage("com.notexists"));
    return i;
  }

  public int getEventId(String paramString)
  {
    collect();
    int i = this.evtIDMap.get(paramString);
    if (i != -1)
      return i + 16384;
    return -1;
  }

  public int getPropertyId(String paramString)
  {
    collect();
    int i = this.fieldIDMap.get(paramString);
    if (i != -1)
      return i + 4096;
    return -1;
  }

  protected int getReturnType(int paramInt)
  {
    Class localClass = null;
    if (paramInt >= 32768)
      localClass = this.methods[(paramInt - 32768)].getMethod().getReturnType();
    else if (paramInt >= 4096)
      localClass = this.props[(paramInt - 4096)].getPropertyType();
    return Utils.getType(localClass);
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.com.BeanClass
 * JD-Core Version:    0.6.2
 */