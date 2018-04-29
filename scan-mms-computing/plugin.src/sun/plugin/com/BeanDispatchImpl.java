package sun.plugin.com;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

public class BeanDispatchImpl extends DispatchImpl
  implements AmbientProperty
{
  public BeanDispatchImpl(Object paramObject, int paramInt)
  {
    super(paramObject, paramInt);
    setBridge();
  }

  public Object invoke(int paramInt1, int paramInt2, Object[] paramArrayOfObject)
    throws Exception
  {
    Object localObject = invokeImpl(paramInt1, paramInt2, paramArrayOfObject);
    if ((localObject instanceof DispatchImpl))
      ((DispatchImpl)localObject).setBridge();
    return localObject;
  }

  public JavaClass getTargetClass()
  {
    if ((this.targetClass == null) && (this.targetObj != null))
      this.targetClass = new BeanClass(this.targetObj.getClass());
    return this.targetClass;
  }

  public void setBackground(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((this.targetObj instanceof Component))
    {
      Component localComponent = (Component)this.targetObj;
      localComponent.setBackground(new Color(paramInt1, paramInt2, paramInt3));
    }
  }

  public void setForeground(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((this.targetObj instanceof Component))
    {
      Component localComponent = (Component)this.targetObj;
      localComponent.setForeground(new Color(paramInt1, paramInt2, paramInt3));
    }
  }

  public void setFont(String paramString, int paramInt1, int paramInt2)
  {
    if ((this.targetObj instanceof Component))
    {
      Component localComponent = (Component)this.targetObj;
      localComponent.setFont(new Font(paramString, paramInt1, paramInt2));
    }
  }

  public int getBackground()
  {
    if ((this.targetObj instanceof Component))
    {
      Component localComponent = (Component)this.targetObj;
      return localComponent.getBackground().getRGB();
    }
    return 0;
  }

  public int getForeground()
  {
    if ((this.targetObj instanceof Component))
    {
      Component localComponent = (Component)this.targetObj;
      return localComponent.getForeground().getRGB();
    }
    return 0;
  }

  public Font getFont()
  {
    if ((this.targetObj instanceof Component))
    {
      Component localComponent = (Component)this.targetObj;
      return localComponent.getFont();
    }
    return null;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.com.BeanDispatchImpl
 * JD-Core Version:    0.6.2
 */