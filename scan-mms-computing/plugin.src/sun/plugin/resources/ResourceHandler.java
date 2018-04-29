package sun.plugin.resources;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ResourceHandler
{
  private static ResourceBundle rb = ResourceBundle.getBundle("sun.plugin.resources.Activator");

  public static String getMessage(String paramString)
  {
    try
    {
      return rb.getString(paramString);
    }
    catch (MissingResourceException localMissingResourceException)
    {
    }
    return paramString;
  }

  public static String getFormattedMessage(String paramString, Object[] paramArrayOfObject)
  {
    try
    {
      MessageFormat localMessageFormat = new MessageFormat(rb.getString(paramString));
      return localMessageFormat.format(paramArrayOfObject);
    }
    catch (MissingResourceException localMissingResourceException)
    {
    }
    return paramString;
  }

  public static String[] getMessageArray(String paramString)
  {
    // Byte code:
    //   0: getstatic 61	sun/plugin/resources/ResourceHandler:rb	Ljava/util/ResourceBundle;
    //   3: aload_0
    //   4: invokevirtual 71	java/util/ResourceBundle:getStringArray	(Ljava/lang/String;)[Ljava/lang/String;
    //   7: areturn
    //   8: astore_1
    //   9: iconst_1
    //   10: anewarray 33	java/lang/String
    //   13: dup
    //   14: iconst_0
    //   15: aload_0
    //   16: aastore
    //   17: areturn
    //
    // Exception table:
    //   from	to	target	type
    //   0	7	8	java/util/MissingResourceException
  }

  public static int getAcceleratorKey(String paramString)
  {
    Integer localInteger = (Integer)rb.getObject(paramString + ".acceleratorKey");
    return localInteger.intValue();
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.resources.ResourceHandler
 * JD-Core Version:    0.6.2
 */