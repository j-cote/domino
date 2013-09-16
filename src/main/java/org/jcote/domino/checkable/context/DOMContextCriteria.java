package org.jcote.domino.checkable.context;

import org.jcote.domino.checkable.tag.TagMatch;
import org.jcote.domino.exception.DOMContextException;
import org.jcote.domino.exception.DOMSearchException;
import org.w3c.dom.Node;

/**
 * DOMContextCriteria
 * Specifies several criteria that a node chain can be checked to match.
 * Examples include Tag which checks tag name, TagCriteria checks tag attributes, 
 * Root checks for html tag, and some wildcard types will skip over a specific number of nodes
 * until the next criteria is matched.
 * @author jordancote
 *
 */
public class DOMContextCriteria {
	public static enum Type {
		Tag,			// match one tag with specified name
		TagCriteria, 	// match one tag with specified criteria
		Root,			// match root node (node which has no parent)
		ExactlyN,		// match exactly N nodes
		AtMostN,		// match at most N nodes until specified next criteria  -- WILL CONSUME NEXT CRITERIA AS WELL!
		Any,			// match any number of nodes until specified next criteria  -- WILL CONSUME NEXT CRITERIA AS WELL!
		Custom,			// match whatever the user wants
	}
	
	private Type type;
	private String tagName;  				// used if Type is {Tag, TagCriteria}
	private DOMContextCriteria nextCriteria;	// used if Type is {AtMostN, Any}
	private int n;						// used if Type is {AtMostN, ExactlyN}
	private TagMatch tagMatch;	// used if Type is {TagCriteria}
	private CustomContext customContext; // used if Type is {Custom}
	
	
	public DOMContextCriteria setTypeAsRoot() {
		this.type = Type.Root;
		return this;
	}

	public DOMContextCriteria setTypeAsTag(String tagName) {
		this.type = Type.Tag;
		this.tagName = tagName;
		return this;
	}
	
	public DOMContextCriteria setTypeAsTagCriteria(String tagName, String attrName, String attrValue) {
		this.type = Type.TagCriteria;
		this.tagName = tagName;
		this.tagMatch = new TagMatch();
		this.tagMatch.addCriteria(tagName, attrName, attrValue);
		return this;
	}
		
	public DOMContextCriteria setTypeAsAtMostN(Type type, DOMContextCriteria nextCriteria, int n) {
		this.type = Type.AtMostN;
		this.nextCriteria = nextCriteria;
		this.n = n;
		return this;
	}
	
	public DOMContextCriteria setTypeAsExactlyN(int n) {
		this.type = Type.ExactlyN;
		this.n = n;
		return this;
	}
	
	public DOMContextCriteria setTypeAsAny(DOMContextCriteria nextCriteria) {
		this.type = Type.Any;
		this.nextCriteria = nextCriteria;
		return this;
	}
	
	public DOMContextCriteria setTypeAsCustom(CustomContext customContext) {
		this.type = Type.Custom;
		this.customContext = customContext;
		return this;
	}
	
	
	/**
	 * See if the given node satisfies this DOMContextCriteria.
	 * @param node
	 * @return Returns number of nodes consumed by this object's criteria
	 * @throws DOMSearchException 
	 */
	public int checkContext(Node node) throws DOMSearchException {
		int i;
		Node nextNode;
		switch(this.type) {
			case Tag:
				if (node.getNodeName().equalsIgnoreCase(tagName)) {
					return 1;
				}
				return 0;
			case TagCriteria:
				if (tagMatch.checkImpl(node)) {
					return 1;
				}
				return 0;
			case Root:
				if (node.getNodeName().equalsIgnoreCase("HTML")) {
					return 1;
				}
				return 0;
			case ExactlyN:
				i = 0;
				nextNode = node.getParentNode();
				while (nextNode != null) {
					if (i == n) {
						return n;
					}
					i++;
					nextNode = nextNode.getParentNode();
				}
				return 0;
			case AtMostN:
				i = 0;
				nextNode = node.getParentNode();
				while (nextNode != null) {
					i++;
					int consumedNext = nextCriteria.checkContext(nextNode);
					if (consumedNext > 0) {
						// was able to be consumed from nextNode
						// return total count of nodes consumed
						return i + consumedNext;
					}
					// did not meet criteria this time
					if (i == n) {
						// we hit the limit (failed to meet criteria)
						return 0;
					}
					nextNode = nextNode.getParentNode();
				}
				// end of node parent chain
				return 0;
			case Any:
				i = 0;
				nextNode = node.getParentNode();
				while (nextNode != null) {
					i++;
					int consumedNext = nextCriteria.checkContext(nextNode);
					if (consumedNext > 0) {
						// was able to be consumed from nextNode
						// return total count of nodes consumed
						return i + consumedNext;
					}
					// did not meet criteria this time
					nextNode = nextNode.getParentNode();
				}
				// end of node chain
				return 0;
			case Custom:
				return this.customContext.checkContext(node);
		}
		// failed to meet criteria
		throw new DOMContextException("DOMContextCriteria has unknown Type: " + this.type);
	}
}