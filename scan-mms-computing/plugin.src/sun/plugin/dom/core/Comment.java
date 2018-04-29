package sun.plugin.dom.core;

import org.w3c.dom.Document;
import sun.plugin.dom.DOMObject;

public final class Comment extends CharacterData
  implements org.w3c.dom.Comment
{
  private static final String NODE_NAME = "#comment";

  public Comment(DOMObject paramDOMObject, Document paramDocument)
  {
    super(paramDOMObject, paramDocument);
  }

  public String getNodeName()
  {
    return "#comment";
  }

  public short getNodeType()
  {
    return 8;
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.core.Comment
 * JD-Core Version:    0.6.2
 */