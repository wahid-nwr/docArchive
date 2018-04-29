package sun.plugin.viewer;

public class AxBridgeContext
{
  static AxBridgeObject createBeansObject(boolean paramBoolean, String[] paramArrayOfString1, String[] paramArrayOfString2, int paramInt)
  {
    AxBridgeObject localAxBridgeObject = new AxBridgeObject(paramInt, paramBoolean, LifeCycleManager.getIdentifier(paramArrayOfString1, paramArrayOfString2));
    for (int i = 0; i < paramArrayOfString1.length; i++)
      if (paramArrayOfString1[i] != null)
        localAxBridgeObject.setParameter(paramArrayOfString1[i], paramArrayOfString2[i]);
    localAxBridgeObject.setBoxColors();
    return localAxBridgeObject;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.viewer.AxBridgeContext
 * JD-Core Version:    0.6.2
 */