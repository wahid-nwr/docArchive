package sun.plugin.com;

class TypeMap
{
  static Object[][] map = { { Long.TYPE, "long" }, { Integer.TYPE, "int" }, { Double.TYPE, "double" }, { Float.TYPE, "float" }, { Short.TYPE, "short" }, { String.class, "BSTR" }, { Boolean.TYPE, "VARIANT_BOOL" }, { Character.TYPE, "short" }, { Byte.TYPE, "BYTE" } };

  static String getCOMType(Class paramClass)
  {
    if (paramClass.toString().equals("void"))
      return "void";
    if (paramClass.isArray())
      return "VARIANT";
    for (int i = 0; i < map.length; i++)
      if (map[i][0] == paramClass)
        return (String)map[i][1];
    return "IDispatch*";
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.com.TypeMap
 * JD-Core Version:    0.6.2
 */