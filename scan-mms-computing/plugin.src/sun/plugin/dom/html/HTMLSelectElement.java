package sun.plugin.dom.html;

import org.w3c.dom.DOMException;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLFormElement;
import org.w3c.dom.html.HTMLOptionElement;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.DOMObjectHelper;

public class HTMLSelectElement extends HTMLElement
  implements org.w3c.dom.html.HTMLSelectElement
{
  public HTMLSelectElement(DOMObject paramDOMObject, HTMLDocument paramHTMLDocument)
  {
    super(paramDOMObject, paramHTMLDocument);
  }

  public String getType()
  {
    return getAttribute("type");
  }

  public int getSelectedIndex()
  {
    return DOMObjectHelper.getIntMember(this.obj, "selectedIndex");
  }

  public void setSelectedIndex(int paramInt)
  {
    DOMObjectHelper.setIntMember(this.obj, "selectedIndex", paramInt);
  }

  public String getValue()
  {
    return getAttribute("value");
  }

  public void setValue(String paramString)
  {
    setAttribute("value", paramString);
  }

  public int getLength()
  {
    return DOMObjectHelper.getIntMember(this.obj, "length");
  }

  public HTMLFormElement getForm()
  {
    Object localObject1 = this.obj.getMember("form");
    if ((localObject1 != null) && ((localObject1 instanceof DOMObject)))
    {
      Object localObject2 = DOMObjectFactory.createHTMLObject((DOMObject)localObject1, (HTMLDocument)getOwnerDocument());
      if ((localObject2 != null) && ((localObject2 instanceof HTMLFormElement)))
        return (HTMLFormElement)localObject2;
    }
    return null;
  }

  public org.w3c.dom.html.HTMLCollection getOptions()
  {
    Object localObject1 = this.obj.getMember("options");
    if ((localObject1 != null) && ((localObject1 instanceof DOMObject)))
    {
      Object localObject2 = DOMObjectFactory.createHTMLObject((DOMObject)localObject1, (HTMLDocument)getOwnerDocument());
      if (localObject2 != null)
      {
        if ((localObject2 instanceof org.w3c.dom.html.HTMLCollection))
          return (org.w3c.dom.html.HTMLCollection)localObject2;
        if ((localObject2 instanceof org.w3c.dom.html.HTMLSelectElement))
        {
          sun.plugin.dom.html.common.HTMLCollection localHTMLCollection = new sun.plugin.dom.html.common.HTMLCollection();
          for (int i = 0; i < getLength(); i++)
          {
            HTMLOptionElement localHTMLOptionElement = getOptionItem(i);
            if (localHTMLOptionElement != null)
              localHTMLCollection.add(localHTMLOptionElement);
          }
          return localHTMLCollection;
        }
      }
    }
    return null;
  }

  private HTMLOptionElement getOptionItem(int paramInt)
  {
    Object localObject1 = this.obj.call("item", new Object[] { new Integer(paramInt) });
    if ((localObject1 != null) && ((localObject1 instanceof DOMObject)))
    {
      Object localObject2 = DOMObjectFactory.createHTMLObject((DOMObject)localObject1, (HTMLDocument)getOwnerDocument());
      if ((localObject2 != null) && ((localObject2 instanceof HTMLOptionElement)))
        return (HTMLOptionElement)localObject2;
    }
    return null;
  }

  public boolean getDisabled()
  {
    return DOMObjectHelper.getBooleanMember(this.obj, "disabled");
  }

  public void setDisabled(boolean paramBoolean)
  {
    DOMObjectHelper.setBooleanMember(this.obj, "disabled", paramBoolean);
  }

  public boolean getMultiple()
  {
    return DOMObjectHelper.getBooleanMember(this.obj, "multiple");
  }

  public void setMultiple(boolean paramBoolean)
  {
    DOMObjectHelper.setBooleanMember(this.obj, "multiple", paramBoolean);
  }

  public String getName()
  {
    return getAttribute("name");
  }

  public void setName(String paramString)
  {
    setAttribute("name", paramString);
  }

  public int getSize()
  {
    return DOMObjectHelper.getIntMember(this.obj, "size");
  }

  public void setSize(int paramInt)
  {
    DOMObjectHelper.setIntMember(this.obj, "size", paramInt);
  }

  public int getTabIndex()
  {
    return DOMObjectHelper.getIntMember(this.obj, "tabIndex");
  }

  public void setTabIndex(int paramInt)
  {
    DOMObjectHelper.setIntMember(this.obj, "tabIndex", paramInt);
  }

  public void add(org.w3c.dom.html.HTMLElement paramHTMLElement1, org.w3c.dom.html.HTMLElement paramHTMLElement2)
    throws DOMException
  {
  }

  public void remove(int paramInt)
  {
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = new Integer(paramInt);
    this.obj.call("remove", arrayOfObject);
  }

  public void blur()
  {
    this.obj.call("blur", null);
  }

  public void focus()
  {
    this.obj.call("focus", null);
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.html.HTMLSelectElement
 * JD-Core Version:    0.6.2
 */