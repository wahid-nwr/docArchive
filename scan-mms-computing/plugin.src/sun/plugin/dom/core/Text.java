package sun.plugin.dom.core;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.exception.PluginNotSupportedException;

public final class Text extends CharacterData
  implements org.w3c.dom.Text
{
  private static final String FUNC_SPLIT_TEXT = "splitText";
  private static final String NODE_NAME = "#text";

  public Text(DOMObject paramDOMObject, Document paramDocument)
  {
    super(paramDOMObject, paramDocument);
  }

  public org.w3c.dom.Text splitText(int paramInt)
    throws DOMException
  {
    Object[] arrayOfObject = { new Integer(paramInt) };
    Object localObject = this.obj.call("splitText", arrayOfObject);
    if ((localObject != null) && ((localObject instanceof DOMObject)))
      return new Text((DOMObject)localObject, getOwnerDocument());
    return null;
  }

  public String getNodeName()
  {
    return "#text";
  }

  public short getNodeType()
  {
    return 3;
  }

  public boolean isElementContentWhitespace()
  {
    throw new PluginNotSupportedException("Node.isElementContentWhitespace() is not supported.");
  }

  public String getWholeText()
  {
    throw new PluginNotSupportedException("Node.getWholeText() is not supported.");
  }

  public org.w3c.dom.Text replaceWholeText(String paramString)
    throws DOMException
  {
    throw new PluginNotSupportedException("Node.replaceWholeText() is not supported.");
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.core.Text
 * JD-Core Version:    0.6.2
 */