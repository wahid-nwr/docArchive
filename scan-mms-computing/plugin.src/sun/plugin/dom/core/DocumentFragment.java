package sun.plugin.dom.core;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.exception.PluginNotSupportedException;

final class DocumentFragment extends Node
  implements org.w3c.dom.DocumentFragment
{
  private static final String NODE_NAME = "#document-fragment";

  public DocumentFragment(DOMObject paramDOMObject, Document paramDocument)
  {
    super(paramDOMObject, paramDocument);
  }

  public String getNodeName()
  {
    return "#document-fragment";
  }

  public String getNodeValue()
    throws DOMException
  {
    throw new PluginNotSupportedException("DocumentFragment.getNodeValue() is not supported");
  }

  public void setNodeValue(String paramString)
    throws DOMException
  {
    throw new PluginNotSupportedException("DocumentFragment.setNodeValue() is not supported");
  }

  public short getNodeType()
  {
    return 11;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.core.DocumentFragment
 * JD-Core Version:    0.6.2
 */