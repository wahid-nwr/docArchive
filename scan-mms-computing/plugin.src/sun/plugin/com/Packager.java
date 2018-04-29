package sun.plugin.com;

import java.awt.Component;
import java.beans.BeanInfo;
import java.beans.Beans;
import java.beans.EventSetDescriptor;
import java.beans.FeatureDescriptor;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.ParameterDescriptor;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

public class Packager
{
  private PrintStream ps = null;
  private File idlFile = null;
  private boolean isGUI = false;
  public static final int NONGUI_EXITCODE = 101;
  private String[] javaProps = { "background", "font", "foreground" };
  private String[] notAllowedMethods = { "enable", "disable", "class", "minimumSize", "preferredSize", "warning" };
  private String[] notAllowedProps = { "layout", "action" };
  private String[] comProps = { "[id(0xfffffe0b)]\nOLE_COLOR BackColor;", "[id(0xfffffe00)]\nFont* Font;", "[id(0xfffffdff)]\nOLE_COLOR ForeColor;" };
  private HashMap methodMap = new HashMap();
  private HashMap eventMap = new HashMap();
  private HashMap overLoadedMap = new HashMap();

  Packager(String paramString1, String paramString2)
  {
    try
    {
      String str = paramString1.substring(paramString1.lastIndexOf('.') + 1);
      this.idlFile = new File(paramString2 + File.separator + str + ".idl");
      this.ps = new PrintStream(new FileOutputStream(this.idlFile));
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
      if (this.idlFile != null)
        this.idlFile.delete();
    }
  }

  public static void main(String[] paramArrayOfString)
  {
    Packager localPackager = new Packager(paramArrayOfString[0], paramArrayOfString[1]);
    localPackager.generate(paramArrayOfString[0], paramArrayOfString[2], paramArrayOfString[3], paramArrayOfString[4], paramArrayOfString[5]);
    int i = 0;
    if (!localPackager.isGUI)
      i = 101;
    Runtime.getRuntime().exit(i);
  }

  public void generate(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    try
    {
      ClassLoader localClassLoader = getClass().getClassLoader();
      Object localObject = Beans.instantiate(localClassLoader, paramString1);
      if ((localObject instanceof Component))
        this.isGUI = true;
      BeanInfo localBeanInfo = Introspector.getBeanInfo(localObject.getClass());
      int i = paramString1.lastIndexOf('.');
      String str = paramString1.substring(i + 1);
      this.ps.println("[");
      this.ps.println("uuid(" + paramString3.substring(1, paramString3.length() - 1) + "),");
      this.ps.println("version(1.0)");
      this.ps.println("]");
      this.ps.println("library " + str);
      this.ps.println("{");
      this.ps.println("importlib(\"Stdole2.tlb\");");
      this.ps.println("dispinterface " + str + "Source;");
      this.ps.println("dispinterface " + str + "Dispatch;");
      this.ps.println("[");
      this.ps.println("uuid(" + paramString4.substring(1, paramString4.length() - 1) + "),");
      this.ps.println("version(1.0)");
      this.ps.println("]");
      this.ps.println("dispinterface " + str + "Source {");
      this.ps.println("properties:");
      this.ps.println("methods:");
      printEvents(localBeanInfo.getEventSetDescriptors());
      this.ps.println("};");
      this.ps.println("[");
      this.ps.println("uuid(" + paramString5.substring(1, paramString5.length() - 1) + "),");
      this.ps.println("version(1.0)");
      this.ps.println("]");
      this.ps.println("dispinterface " + str + "Dispatch {");
      this.ps.println("properties:");
      printProperties(localBeanInfo, true);
      this.ps.println("methods:");
      printProperties(localBeanInfo, false);
      printMethods(localBeanInfo.getMethodDescriptors(), 32768, this.methodMap);
      this.ps.println("};");
      this.ps.println("[");
      this.ps.println("uuid(" + paramString2.substring(1, paramString2.length() - 1) + "),");
      this.ps.println("version(1.0)");
      this.ps.println("]");
      this.ps.println("coclass " + str + " {");
      this.ps.println("[default, source] dispinterface " + str + "Source;");
      this.ps.println("[default] dispinterface " + str + "Dispatch;");
      this.ps.println("};");
      this.ps.println("};");
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      if (this.idlFile != null)
        this.idlFile.delete();
    }
  }

  private void printMethods(MethodDescriptor[] paramArrayOfMethodDescriptor, int paramInt, HashMap paramHashMap)
  {
    HashMap localHashMap = new HashMap();
    Method localMethod1;
    for (int i = 0; i < paramArrayOfMethodDescriptor.length; i++)
    {
      localMethod1 = paramArrayOfMethodDescriptor[i].getMethod();
      Method localMethod2 = (Method)localHashMap.get(localMethod1.getName());
      if (localMethod2 != null)
        if (localMethod2.getParameterTypes().length < localMethod1.getParameterTypes().length)
          this.overLoadedMap.put(localMethod1.getName(), localMethod1);
        else
          this.overLoadedMap.put(localMethod1.getName(), localMethod2);
      localHashMap.put(localMethod1.getName(), localMethod1);
    }
    sort(paramArrayOfMethodDescriptor);
    for (i = 0; i < paramArrayOfMethodDescriptor.length; i++)
    {
      localMethod1 = paramArrayOfMethodDescriptor[i].getMethod();
      int j = 0;
      for (int k = 0; k < this.notAllowedMethods.length; k++)
        if (localMethod1.getName().equals(this.notAllowedMethods[k]))
        {
          j = 1;
          break;
        }
      if (j != 1)
      {
        Method localMethod3 = (Method)this.overLoadedMap.get(localMethod1.getName());
        if ((localMethod3 == null) || (localMethod1.equals(localMethod3)))
        {
          paramHashMap.put(localMethod1.getName(), localMethod1);
          if (localMethod3 != null)
            printOverLoadedMethod(paramArrayOfMethodDescriptor[i], paramInt + i);
          else
            printMethod(paramArrayOfMethodDescriptor[i], paramInt + i);
        }
      }
    }
  }

  private void printOverLoadedMethod(MethodDescriptor paramMethodDescriptor, int paramInt)
  {
    Method localMethod = paramMethodDescriptor.getMethod();
    ParameterDescriptor[] arrayOfParameterDescriptor = paramMethodDescriptor.getParameterDescriptors();
    Class[] arrayOfClass = localMethod.getParameterTypes();
    StringBuffer localStringBuffer = new StringBuffer("[id(" + paramInt + ")]\n");
    localStringBuffer.append("VARIANT " + localMethod.getName() + "(");
    String str = ", ";
    for (int i = 0; i < arrayOfClass.length; i++)
    {
      localStringBuffer.append("[optional] VARIANT ");
      if (i == arrayOfClass.length - 1)
        str = ");";
      if ((arrayOfParameterDescriptor != null) && (arrayOfParameterDescriptor[i] != null))
        localStringBuffer.append(arrayOfParameterDescriptor[i].getName() + str);
      else
        localStringBuffer.append("var" + i + str);
    }
    if (arrayOfClass.length == 0)
      localStringBuffer.append(");");
    this.ps.println(localStringBuffer);
  }

  private void printMethod(MethodDescriptor paramMethodDescriptor, int paramInt)
  {
    Method localMethod = paramMethodDescriptor.getMethod();
    StringBuffer localStringBuffer = new StringBuffer("[id(" + paramInt + ")]\n");
    String str1 = TypeMap.getCOMType(localMethod.getReturnType());
    localStringBuffer.append(str1 + " " + localMethod.getName() + "(");
    Class[] arrayOfClass = localMethod.getParameterTypes();
    ParameterDescriptor[] arrayOfParameterDescriptor = paramMethodDescriptor.getParameterDescriptors();
    String str2 = ", ";
    for (int i = 0; i < arrayOfClass.length; i++)
    {
      localStringBuffer.append(TypeMap.getCOMType(arrayOfClass[i]) + " ");
      if (i == arrayOfClass.length - 1)
        str2 = ");";
      if ((arrayOfParameterDescriptor != null) && (arrayOfParameterDescriptor[i] != null))
        localStringBuffer.append(arrayOfParameterDescriptor[i].getName() + str2);
      else
        localStringBuffer.append("arg" + i + str2);
    }
    if (arrayOfClass.length == 0)
      localStringBuffer.append(");");
    this.ps.println(localStringBuffer);
  }

  private void printEventMethods(MethodDescriptor[] paramArrayOfMethodDescriptor, int paramInt, HashMap paramHashMap)
  {
    for (int i = 0; i < paramArrayOfMethodDescriptor.length; i++)
    {
      Method localMethod1 = paramArrayOfMethodDescriptor[i].getMethod();
      Method localMethod2 = (Method)paramHashMap.get(localMethod1.getName());
      if (localMethod2 == null)
      {
        paramHashMap.put(localMethod1.getName(), localMethod1);
        printMethod(paramArrayOfMethodDescriptor[i], paramInt + i);
      }
    }
  }

  private void printEvents(EventSetDescriptor[] paramArrayOfEventSetDescriptor)
  {
    if (paramArrayOfEventSetDescriptor == null)
      return;
    MethodDescriptor[] arrayOfMethodDescriptor = null;
    Vector localVector = new Vector();
    for (int i = 0; i < paramArrayOfEventSetDescriptor.length; i++)
    {
      arrayOfMethodDescriptor = paramArrayOfEventSetDescriptor[i].getListenerMethodDescriptors();
      for (int j = 0; j < arrayOfMethodDescriptor.length; j++)
        localVector.addElement(arrayOfMethodDescriptor[j]);
    }
    arrayOfMethodDescriptor = (MethodDescriptor[])localVector.toArray(new MethodDescriptor[0]);
    sort(arrayOfMethodDescriptor);
    printEventMethods(arrayOfMethodDescriptor, 16384, this.eventMap);
  }

  private void printProperties(BeanInfo paramBeanInfo, boolean paramBoolean)
  {
    PropertyDescriptor[] arrayOfPropertyDescriptor = paramBeanInfo.getPropertyDescriptors();
    sort(arrayOfPropertyDescriptor);
    int i = paramBeanInfo.getDefaultPropertyIndex();
    for (int j = 0; j < arrayOfPropertyDescriptor.length; j++)
    {
      Method localMethod1 = arrayOfPropertyDescriptor[j].getReadMethod();
      Method localMethod2 = arrayOfPropertyDescriptor[j].getWriteMethod();
      if ((localMethod1 != null) && (localMethod2 != null) && (paramBoolean))
        printProperty(arrayOfPropertyDescriptor[j], j, j == i);
      if ((!paramBoolean) && (this.methodMap.get(arrayOfPropertyDescriptor[j].getName()) == null))
      {
        int k = 0;
        for (int m = 0; m < this.notAllowedMethods.length; m++)
          if (arrayOfPropertyDescriptor[j].getName().equals(this.notAllowedMethods[m]))
            k = 1;
        if ((k = 1) == 0)
          if (localMethod1 != null)
            printGetProperty(arrayOfPropertyDescriptor[j], j, j == i);
          else if (localMethod2 != null)
            printPutProperty(arrayOfPropertyDescriptor[j], j, j == i);
      }
    }
  }

  private void printPropertyAttrib(PropertyDescriptor paramPropertyDescriptor, int paramInt, String paramString)
  {
    int i = 4096 + paramInt;
    StringBuffer localStringBuffer = new StringBuffer("[id(" + i + ")");
    if (paramString != null)
      localStringBuffer.append(", " + paramString);
    if (paramPropertyDescriptor.isBound() == true)
      localStringBuffer.append(", bindable");
    if (paramPropertyDescriptor.isConstrained() == true)
      localStringBuffer.append(", requestedit");
    localStringBuffer.append("]");
    this.ps.println(localStringBuffer);
  }

  private void printProperty(PropertyDescriptor paramPropertyDescriptor, int paramInt, boolean paramBoolean)
  {
    for (int i = 0; i < this.javaProps.length; i++)
      if (paramPropertyDescriptor.getName().equals(this.javaProps[i]))
      {
        this.ps.println(this.comProps[i]);
        return;
      }
    for (i = 0; i < this.notAllowedProps.length; i++)
      if (paramPropertyDescriptor.getName().equals(this.notAllowedProps[i]))
        return;
    printPropertyAttrib(paramPropertyDescriptor, paramInt, null);
    StringBuffer localStringBuffer = new StringBuffer();
    String str = TypeMap.getCOMType(paramPropertyDescriptor.getPropertyType());
    localStringBuffer.append(str + " " + paramPropertyDescriptor.getName() + ";");
    this.ps.println(localStringBuffer);
  }

  private void printGetProperty(PropertyDescriptor paramPropertyDescriptor, int paramInt, boolean paramBoolean)
  {
    Method localMethod = paramPropertyDescriptor.getReadMethod();
    this.methodMap.put(paramPropertyDescriptor.getName(), localMethod);
    printPropertyAttrib(paramPropertyDescriptor, paramInt, "propget");
    StringBuffer localStringBuffer = new StringBuffer();
    String str = TypeMap.getCOMType(paramPropertyDescriptor.getPropertyType());
    localStringBuffer.append(str + " " + paramPropertyDescriptor.getName() + "();");
    this.ps.println(localStringBuffer);
  }

  private void printPutProperty(PropertyDescriptor paramPropertyDescriptor, int paramInt, boolean paramBoolean)
  {
    Method localMethod = paramPropertyDescriptor.getReadMethod();
    this.methodMap.put(paramPropertyDescriptor.getName(), localMethod);
    printPropertyAttrib(paramPropertyDescriptor, paramInt, "propput");
    StringBuffer localStringBuffer = new StringBuffer();
    String str = TypeMap.getCOMType(paramPropertyDescriptor.getPropertyType());
    localStringBuffer.append("void " + paramPropertyDescriptor.getName() + "(" + str + ");");
    this.ps.println(localStringBuffer);
  }

  static void sort(Object[] paramArrayOfObject)
  {
    Arrays.sort(paramArrayOfObject, new Comparator()
    {
      public int compare(Object paramAnonymousObject1, Object paramAnonymousObject2)
      {
        String str1 = ((FeatureDescriptor)paramAnonymousObject1).getName();
        String str2 = ((FeatureDescriptor)paramAnonymousObject2).getName();
        return str1.compareTo(str2);
      }
    });
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.com.Packager
 * JD-Core Version:    0.6.2
 */