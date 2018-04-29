package sun.plugin.viewer;

import com.sun.deploy.perf.DeployPerfUtil;

public class WNetscapePluginContext
{
  private static String PLUGIN_UNIQUE_ID = "A8F70EB5-AAEF-11d6-95A4-0050BAAC8BD3";

  static WNetscapePluginObject createPluginObject(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2, int paramInt)
  {
    DeployPerfUtil.put("START - Java   - ENV - create browser plugin object (Mozilla:Windows)");
    boolean bool = paramString.indexOf("application/x-java-bean") >= 0;
    WNetscapePluginObject localWNetscapePluginObject = new WNetscapePluginObject(paramInt, bool, LifeCycleManager.getIdentifier(paramArrayOfString1, paramArrayOfString2));
    for (int i = 0; i < paramArrayOfString1.length; i++)
      if ((paramArrayOfString1[i] != null) && (!PLUGIN_UNIQUE_ID.equals(paramArrayOfString1[i])))
        localWNetscapePluginObject.setParameter(paramArrayOfString1[i], paramArrayOfString2[i]);
    localWNetscapePluginObject.setBoxColors();
    DeployPerfUtil.put("END   - Java   - ENV - create browser plugin object (Mozilla:Windows)");
    return localWNetscapePluginObject;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.viewer.WNetscapePluginContext
 * JD-Core Version:    0.6.2
 */