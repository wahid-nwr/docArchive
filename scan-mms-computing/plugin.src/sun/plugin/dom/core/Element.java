package sun.plugin.dom.core;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectHelper;
import sun.plugin.dom.exception.PluginNotSupportedException;

public abstract class Element extends Node
  implements org.w3c.dom.Element
{
  private static final String ATTR_TAGNAME = "tagName";

  protected Element(DOMObject paramDOMObject, Document paramDocument)
  {
    super(paramDOMObject, paramDocument);
  }

  public String getTagName()
  {
    return getAttribute("tagName");
  }

  public String getAttribute(String paramString)
  {
    return DOMObjectHelper.getStringMemberNoEx(this.obj, paramString);
  }

  public void setAttribute(String paramString1, String paramString2)
    throws DOMException
  {
    DOMObjectHelper.setStringMember(this.obj, paramString1, paramString2);
  }

  public void removeAttribute(String paramString)
    throws DOMException
  {
    throw new PluginNotSupportedException("Element.removeAttribute() is not supported");
  }

  public Attr getAttributeNode(String paramString)
  {
    throw new PluginNotSupportedException("Element.getAttributeNode() is not supported");
  }

  public Attr setAttributeNode(Attr paramAttr)
    throws DOMException
  {
    throw new PluginNotSupportedException("Element.setAttributeNode() is not supported");
  }

  public Attr removeAttributeNode(Attr paramAttr)
    throws DOMException
  {
    throw new PluginNotSupportedException("Element.removeAttributeNode() is not supported");
  }

  public NodeList getElementsByTagName(String paramString)
  {
    throw new PluginNotSupportedException("Element.getElementsByTagName() is not supported");
  }

  public String getAttributeNS(String paramString1, String paramString2)
  {
    throw new PluginNotSupportedException("Element.getAttributeNS() is not supported");
  }

  public void setAttributeNS(String paramString1, String paramString2, String paramString3)
    throws DOMException
  {
    throw new PluginNotSupportedException("Element.setAttributeNS() is not supported");
  }

  public void removeAttributeNS(String paramString1, String paramString2)
    throws DOMException
  {
    throw new PluginNotSupportedException("Element.removeAttributeNS() is not supported");
  }

  public Attr getAttributeNodeNS(String paramString1, String paramString2)
  {
    throw new PluginNotSupportedException("Element.getAttributeNodeNS() is not supported");
  }

  public Attr setAttributeNodeNS(Attr paramAttr)
    throws DOMException
  {
    throw new PluginNotSupportedException("Element.setAttributeNodeNS() is not supported");
  }

  public NodeList getElementsByTagNameNS(String paramString1, String paramString2)
  {
    throw new PluginNotSupportedException("Element.getElementsByTagNameNS() is not supported");
  }

  public boolean hasAttribute(String paramString)
  {
    return getAttribute(paramString) != null;
  }

  public boolean hasAttributeNS(String paramString1, String paramString2)
  {
    throw new PluginNotSupportedException("Element.hasAttributeNS() is not supported");
  }

  public String getNodeName()
  {
    return getTagName();
  }

  public String getNodeValue()
    throws DOMException
  {
    throw new PluginNotSupportedException("Element.getNodeValue() is not supported");
  }

  public void setNodeValue(String paramString)
    throws DOMException
  {
    throw new PluginNotSupportedException("Element.setNodeValue() is not supported");
  }

  public short getNodeType()
  {
    return 1;
  }

  public void setIdAttribute(String paramString, boolean paramBoolean)
    throws DOMException
  {
    throw new PluginNotSupportedException("Element.setIdAttribute() is not supported");
  }

  public void setIdAttributeNS(String paramString1, String paramString2, boolean paramBoolean)
    throws DOMException
  {
    throw new PluginNotSupportedException("Element.setIdAttributeNS() is not supported");
  }

  public void setIdAttributeNode(Attr paramAttr, boolean paramBoolean)
    throws DOMException
  {
    throw new PluginNotSupportedException("Element.setIdAttributeNode() is not supported");
  }

  public TypeInfo getSchemaTypeInfo()
  {
    throw new PluginNotSupportedException("Element.getSchemaTypeInfo is not supported");
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.core.Element
 * JD-Core Version:    0.6.2
 */