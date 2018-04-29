package sun.plugin.viewer;

import com.sun.deploy.perf.DeployPerfUtil;

public class IExplorerPluginContext
{
  static IExplorerPluginObject createPluginObject(boolean paramBoolean, String[] paramArrayOfString1, String[] paramArrayOfString2, int paramInt)
  {
    DeployPerfUtil.put("START - Java   - ENV - create browser plugin object (IE)");
    IExplorerPluginObject localIExplorerPluginObject = new IExplorerPluginObject(paramInt, paramBoolean, LifeCycleManager.getIdentifier(paramArrayOfString1, paramArrayOfString2));
    for (int i = 0; i < paramArrayOfString1.length; i++)
      if (paramArrayOfString1[i] != null)
        localIExplorerPluginObject.setParameter(paramArrayOfString1[i], paramArrayOfString2[i]);
    localIExplorerPluginObject.setBoxColors();
    DeployPerfUtil.put("END   - Java   - ENV - create browser plugin object (IE)");
    return localIExplorerPluginObject;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.viewer.IExplorerPluginContext
 * JD-Core Version:    0.6.2
 */