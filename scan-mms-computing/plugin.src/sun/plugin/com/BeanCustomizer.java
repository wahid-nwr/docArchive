package sun.plugin.com;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.Customizer;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import sun.awt.windows.WEmbeddedFrame;
import sun.plugin.viewer.AxBridgeObject;

public class BeanCustomizer
{
  private boolean dirty = false;
  Customizer customizer = null;
  PropertyEditor propEditor = null;
  Component comp;
  WEmbeddedFrame frame;
  BeanInfo bInfo;

  public BeanCustomizer(BeanInfo paramBeanInfo)
  {
    this.bInfo = paramBeanInfo;
  }

  public boolean open(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    boolean bool = false;
    if (this.comp != null)
    {
      this.frame = new WEmbeddedFrame(paramInt1);
      if (this.frame != null)
      {
        this.frame.setLayout(new BorderLayout());
        Color localColor = new Color(paramInt2, paramInt3, paramInt4);
        this.frame.setBackground(localColor);
        this.comp.setBackground(localColor);
        this.comp.setSize(this.comp.getPreferredSize());
        this.frame.setSize(this.comp.getPreferredSize());
        this.frame.add(this.comp);
        this.frame.validate();
        bool = true;
      }
    }
    return bool;
  }

  public void show(boolean paramBoolean)
  {
    this.frame.setVisible(paramBoolean);
    this.comp.repaint();
  }

  public void move(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.frame.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
  }

  public void setObject(Object paramObject)
  {
    Object localObject = ((AxBridgeObject)paramObject).getJavaObject();
    try
    {
      Class localClass = this.bInfo.getBeanDescriptor().getCustomizerClass();
      if (localClass != null)
      {
        this.customizer = ((Customizer)localClass.newInstance());
        this.comp = ((Component)this.customizer);
        this.customizer.setObject(localObject);
      }
      if (this.comp == null)
      {
        PropertyDescriptor[] arrayOfPropertyDescriptor = this.bInfo.getPropertyDescriptors();
        for (int i = 0; i < arrayOfPropertyDescriptor.length; i++)
        {
          localClass = arrayOfPropertyDescriptor[i].getPropertyEditorClass();
          if (localClass != null)
          {
            this.propEditor = ((PropertyEditor)localClass.newInstance());
            if (this.propEditor.supportsCustomEditor())
            {
              this.comp = this.propEditor.getCustomEditor();
              this.propEditor.setValue(localObject);
              break;
            }
          }
        }
      }
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.com.BeanCustomizer
 * JD-Core Version:    0.6.2
 */