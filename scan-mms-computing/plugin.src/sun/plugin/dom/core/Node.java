package sun.plugin.dom.core;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;
import sun.plugin.dom.DOMObject;
import sun.plugin.dom.DOMObjectFactory;
import sun.plugin.dom.DOMObjectHelper;
import sun.plugin.dom.exception.NoModificationAllowedException;
import sun.plugin.dom.exception.PluginNotSupportedException;

public abstract class Node
  implements org.w3c.dom.Node
{
  private static final String ATTR_CHILDREN = "children";
  private static final String ATTR_CHILD_NODES = "childNodes";
  private static final String ATTR_PARENT_NODE = "parentNode";
  private static final String ATTR_FIRST_CHILD = "firstChild";
  private static final String ATTR_LAST_CHILD = "lastChild";
  private static final String ATTR_PREVIOUS_SIBLING = "previousSibling";
  private static final String ATTR_NEXT_SIBLING = "nextSibling";
  private static final String ATTR_HAS_CHILD_NODES = "hasChildNodes";
  private static final String FUNC_APPEND_CHILD = "appendChild";
  private static final String FUNC_CLONE_NODE = "cloneNode";
  private static final String FUNC_INSERT_BEFORE = "insertBefore";
  private static final String FUNC_REMOVE_CHILD = "removeChild";
  private static final String FUNC_REPLACE_CHILD = "replaceChild";
  private static final String FUNC_REPLACE_NODE = "replaceNode";
  protected DOMObject obj;
  private Document doc;

  protected Node(DOMObject paramDOMObject, Document paramDocument)
  {
    this.obj = paramDOMObject;
    this.doc = paramDocument;
  }

  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof Node))
      return false;
    return this.obj.equals(((Node)paramObject).obj);
  }

  public int hashCode()
  {
    StringBuffer localStringBuffer = new StringBuffer(getClass().getName());
    localStringBuffer.append(toString());
    return localStringBuffer.toString().hashCode();
  }

  public String toString()
  {
    if (this.obj != null)
      return this.obj.toString();
    return super.toString();
  }

  public abstract String getNodeName();

  public abstract String getNodeValue()
    throws DOMException;

  public abstract void setNodeValue(String paramString)
    throws DOMException;

  public abstract short getNodeType();

  public NodeList getChildNodes()
  {
    Object localObject1 = null;
    try
    {
      localObject1 = this.obj.getMember("children");
      if (!(localObject1 instanceof DOMObject))
        localObject1 = null;
    }
    catch (DOMException localDOMException)
    {
    }
    if (localObject1 == null)
      localObject1 = this.obj.getMember("childNodes");
    if ((localObject1 != null) && ((localObject1 instanceof DOMObject)))
    {
      Object localObject2 = DOMObjectFactory.createCommonDOMObject((DOMObject)localObject1, getOwnerDocument());
      if ((localObject2 != null) && ((localObject2 instanceof NodeList)))
        return (NodeList)localObject2;
    }
    return null;
  }

  public org.w3c.dom.Node getParentNode()
  {
    Object localObject1 = this.obj.getMember("parentNode");
    if ((localObject1 != null) && ((localObject1 instanceof DOMObject)))
    {
      Object localObject2 = DOMObjectFactory.createCommonDOMObject((DOMObject)localObject1, getOwnerDocument());
      if ((localObject2 != null) && ((localObject2 instanceof org.w3c.dom.Node)))
        return (org.w3c.dom.Node)localObject2;
    }
    return null;
  }

  public org.w3c.dom.Node getFirstChild()
  {
    NodeList localNodeList = getChildNodes();
    if ((localNodeList != null) && (localNodeList.getLength() > 0))
      return localNodeList.item(0);
    return null;
  }

  public org.w3c.dom.Node getLastChild()
  {
    NodeList localNodeList = getChildNodes();
    int i;
    if ((localNodeList != null) && ((i = localNodeList.getLength()) > 0))
      return localNodeList.item(i - 1);
    return null;
  }

  public org.w3c.dom.Node getPreviousSibling()
  {
    Object localObject1 = this.obj.getMember("previousSibling");
    if ((localObject1 != null) && ((localObject1 instanceof DOMObject)))
    {
      Object localObject2 = DOMObjectFactory.createCommonDOMObject((DOMObject)localObject1, getOwnerDocument());
      if ((localObject2 != null) && ((localObject2 instanceof org.w3c.dom.Node)))
        return (org.w3c.dom.Node)localObject2;
    }
    return null;
  }

  public org.w3c.dom.Node getNextSibling()
  {
    Object localObject1 = this.obj.getMember("nextSibling");
    if ((localObject1 != null) && ((localObject1 instanceof DOMObject)))
    {
      Object localObject2 = DOMObjectFactory.createCommonDOMObject((DOMObject)localObject1, getOwnerDocument());
      if ((localObject2 != null) && ((localObject2 instanceof org.w3c.dom.Node)))
        return (org.w3c.dom.Node)localObject2;
    }
    return null;
  }

  public NamedNodeMap getAttributes()
  {
    throw new PluginNotSupportedException("Node.getAttributes() is not supported.");
  }

  public Document getOwnerDocument()
  {
    return this.doc;
  }

  public org.w3c.dom.Node insertBefore(org.w3c.dom.Node paramNode1, org.w3c.dom.Node paramNode2)
    throws DOMException
  {
    if ((paramNode1 != null) && (paramNode2 != null))
      if (((paramNode1 instanceof Node)) && ((paramNode2 instanceof Node)))
      {
        Node localNode1 = (Node)paramNode1;
        Node localNode2 = (Node)paramNode2;
        Object localObject1 = this.obj.call("insertBefore", new Object[] { localNode1.obj.getJSObject(), localNode2.obj.getJSObject() });
        if ((localObject1 != null) && ((localObject1 instanceof DOMObject)))
        {
          Object localObject2 = DOMObjectFactory.createCommonDOMObject((DOMObject)localObject1, getOwnerDocument());
          if ((localObject2 != null) && ((localObject2 instanceof org.w3c.dom.Node)))
            return (org.w3c.dom.Node)localObject2;
        }
      }
      else
      {
        throw new PluginNotSupportedException("Node.insertBefore() does not support node type: (" + paramNode1.getClass().getName() + ", " + paramNode2.getClass().getName() + ")");
      }
    return null;
  }

  public org.w3c.dom.Node replaceChild(org.w3c.dom.Node paramNode1, org.w3c.dom.Node paramNode2)
    throws DOMException
  {
    if ((paramNode1 != null) && (paramNode2 != null))
      if (((paramNode1 instanceof Node)) && ((paramNode2 instanceof Node)))
      {
        Node localNode1 = (Node)paramNode1;
        Node localNode2 = (Node)paramNode2;
        Object localObject1 = this.obj.call("replaceChild", new Object[] { localNode1.obj.getJSObject(), localNode2.obj.getJSObject() });
        if ((localObject1 != null) && ((localObject1 instanceof DOMObject)))
        {
          Object localObject2 = DOMObjectFactory.createCommonDOMObject((DOMObject)localObject1, getOwnerDocument());
          if ((localObject2 != null) && ((localObject2 instanceof org.w3c.dom.Node)))
            return (org.w3c.dom.Node)localObject2;
        }
      }
      else
      {
        throw new PluginNotSupportedException("Node.replaceChild() does not support node type: (" + paramNode1.getClass().getName() + ", " + paramNode2.getClass().getName() + ")");
      }
    return null;
  }

  public org.w3c.dom.Node removeChild(org.w3c.dom.Node paramNode)
    throws DOMException
  {
    if (paramNode != null)
      if ((paramNode instanceof Node))
      {
        Node localNode = (Node)paramNode;
        Object localObject1 = this.obj.call("removeChild", new Object[] { localNode.obj.getJSObject() });
        if ((localObject1 != null) && ((localObject1 instanceof DOMObject)))
        {
          Object localObject2 = DOMObjectFactory.createCommonDOMObject((DOMObject)localObject1, getOwnerDocument());
          if ((localObject2 != null) && ((localObject2 instanceof org.w3c.dom.Node)))
            return (org.w3c.dom.Node)localObject2;
        }
      }
      else
      {
        throw new PluginNotSupportedException("Node.removeChild() does not support node type: " + paramNode.getClass().getName());
      }
    return null;
  }

  public org.w3c.dom.Node appendChild(org.w3c.dom.Node paramNode)
    throws DOMException
  {
    if (paramNode != null)
      if ((paramNode instanceof Node))
      {
        Node localNode = (Node)paramNode;
        Object localObject1 = this.obj.call("appendChild", new Object[] { localNode.obj.getJSObject() });
        if ((localObject1 != null) && ((localObject1 instanceof DOMObject)))
        {
          Object localObject2 = DOMObjectFactory.createCommonDOMObject((DOMObject)localObject1, getOwnerDocument());
          if ((localObject2 != null) && ((localObject2 instanceof org.w3c.dom.Node)))
            return (org.w3c.dom.Node)localObject2;
        }
      }
      else
      {
        throw new PluginNotSupportedException("Node.appendChild() does not support node type: " + paramNode.getClass().getName());
      }
    return null;
  }

  public boolean hasChildNodes()
  {
    return DOMObjectHelper.getBooleanMember(this.obj, "hasChildNodes");
  }

  public org.w3c.dom.Node cloneNode(boolean paramBoolean)
  {
    Object localObject1 = this.obj.call("cloneNode", new Object[] { new Boolean(paramBoolean) });
    if ((localObject1 != null) && ((localObject1 instanceof DOMObject)))
    {
      Object localObject2 = DOMObjectFactory.createCommonDOMObject((DOMObject)localObject1, getOwnerDocument());
      if ((localObject2 != null) && ((localObject2 instanceof org.w3c.dom.Node)))
      {
        ((DOMObject)localObject1).lock();
        return (org.w3c.dom.Node)localObject2;
      }
    }
    return null;
  }

  public void normalize()
  {
    throw new PluginNotSupportedException("Node.normalize() is not supported.");
  }

  public boolean isSupported(String paramString1, String paramString2)
  {
    sun.plugin.dom.DOMImplementation localDOMImplementation = new sun.plugin.dom.DOMImplementation();
    return localDOMImplementation.hasFeature(paramString1, paramString2);
  }

  public String getNamespaceURI()
  {
    throw new PluginNotSupportedException("Node.getNamespaceURI() is not supported.");
  }

  public String getPrefix()
  {
    throw new PluginNotSupportedException("Node.getPrefix() is not supported.");
  }

  public void setPrefix(String paramString)
    throws DOMException
  {
    throw new NoModificationAllowedException("Node.setPrefix() is not supported");
  }

  public String getLocalName()
  {
    throw new PluginNotSupportedException("Node.getLocalName() is not supported.");
  }

  public boolean hasAttributes()
  {
    throw new PluginNotSupportedException("Node.hasAttributes() is not supported.");
  }

  public Object setUserData(String paramString, Object paramObject, UserDataHandler paramUserDataHandler)
  {
    throw new PluginNotSupportedException("Node.setUserData() is not supported.");
  }

  public Object getUserData(String paramString)
  {
    throw new PluginNotSupportedException("Node.getUserData() is not supported.");
  }

  public Object getFeature(String paramString1, String paramString2)
  {
    throw new PluginNotSupportedException("Node.getFeature() is not supported.");
  }

  public boolean isSameNode(org.w3c.dom.Node paramNode)
  {
    throw new PluginNotSupportedException("Node.isSameNode() is not supported.");
  }

  public boolean isEqualNode(org.w3c.dom.Node paramNode)
  {
    throw new PluginNotSupportedException("Node.isEqualNode() is not supported.");
  }

  public String lookupNamespaceURI(String paramString)
  {
    throw new PluginNotSupportedException("Node.lookupNamespaceURI() is not supported.");
  }

  public boolean isDefaultNamespace(String paramString)
  {
    throw new PluginNotSupportedException("Node.isDefaultNamespace() is not supported.");
  }

  public String lookupPrefix(String paramString)
  {
    throw new PluginNotSupportedException("Node.lookupPrefix() is not supported.");
  }

  public String getTextContent()
    throws DOMException
  {
    throw new PluginNotSupportedException("Node.getTextContent() is not supported.");
  }

  public void setTextContent(String paramString)
    throws DOMException
  {
    throw new PluginNotSupportedException("Node.setTextContent() is not supported.");
  }

  public short compareDocumentPosition(org.w3c.dom.Node paramNode)
    throws DOMException
  {
    throw new PluginNotSupportedException("Node.compareDocumentPosition() is not supported.");
  }

  public String getBaseURI()
  {
    throw new PluginNotSupportedException("Node.getBaseURI() is not supported.");
  }
}

/* Location:           /home/wahid/Downloads/webscanning/plugin.jar
 * Qualified Name:     sun.plugin.dom.core.Node
 * JD-Core Version:    0.6.2
 */