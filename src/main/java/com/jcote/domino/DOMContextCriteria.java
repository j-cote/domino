package com.jcote.domino;

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
	}
	
	private Type type;
	private String tagName;  				// used if Type is {Tag, TagCriteria}
	private DOMContextCriteria nextCriteria;	// used if Type is {AtMostN, Any}
	private int n;						// used if Type is {AtMostN, ExactlyN}
	private TagMatch tagMatch;	// used if Type is {TagCriteria}
	
	
	public DOMContextCriteria(Type type) throws DOMContextException {
		if (!(type == Type.Root)) {
			throw new DOMContextException("Constructor called with () parameter, but Type is not one of: {Root}");
		}
		this.type = type;
	}

	public DOMContextCriteria(Type type, String tagName) throws DOMContextException {
		if (!(type == Type.Tag)) {
			throw new DOMContextException("Constructor called with (tagName) parameter, but Type is not one of: {Tag}");
		}
		this.type = type;
		this.tagName = tagName;
	}
	
	public DOMContextCriteria(Type type, String tagName, String attrName, String attrValue) throws DOMContextException {
		if (!(type == Type.TagCriteria)) {
			throw new DOMContextException("Constructor called with (tagName, attrName, attrValue) parameters, but Type is not one of: {TagCriteria}");
		}
		this.type = type;
		this.tagName = tagName;
		this.tagMatch = new TagMatch();
		this.tagMatch.addCriteria(tagName, attrName, attrValue);
	}
	
	public DOMContextCriteria(Type type, TagMatch tagMatch) throws DOMContextException {
		if (!(type == Type.TagCriteria)) {
			throw new DOMContextException("Constructor called with (tagMatch) parameters, but Type is not one of: {TagCriteria}");
		}
		if (tagMatch == null) {
			throw new DOMContextException("tagMatch must not be null.");
		}
		this.type = type;
		this.tagMatch = tagMatch;
	}
	
	public DOMContextCriteria(Type type, DOMContextCriteria nextCriteria, int n) throws DOMContextException {
		if (!(type != Type.AtMostN)) {
			throw new DOMContextException("Constructor called with (next, n) parameters, but Type is not one of: {AtMostN}");
		}
		if (nextCriteria == null) {
			throw new DOMContextException("nextCriteria must not be null.");
		}
		if (n < 1) {
			throw new DOMContextException("N must be at least 1.");
		}
		this.type = type;
		this.nextCriteria = nextCriteria;
		this.n = n;
	}
	
	public DOMContextCriteria(Type type, int n) throws DOMContextException {
		if (!(type == Type.ExactlyN)) {
			throw new DOMContextException("Constructor called with (n) parameter, but Type is not one of: {ExactlyN}");
		}
		if (n < 1) {
			throw new DOMContextException("N must be at least 1.");
		}
		this.type = type;
		this.n = n;
	}
	
	public DOMContextCriteria(Type type, DOMContextCriteria nextCriteria) throws DOMContextException {
		if (!(type != Type.Any)) {
			throw new DOMContextException("Constructor called with (next) parameter, but Type is not one of: {Any}");
		}
		if (nextCriteria == null) {
			throw new DOMContextException("nextCriteria must not be null.");
		}
		this.type = type;
		this.nextCriteria = nextCriteria;
	}
	
	
	/**
	 * See if the given node satisfies this DOMContextCriteria.
	 * @param node
	 * @return Returns number of nodes consumed by this object's criteria
	 * @throws DOMContextException 
	 */
	public int checkContext(Node node) throws DOMContextException {
		int i, j;
		Node nextNode;
		switch(this.type) {
			case Tag:
				if (node.getNodeName().equalsIgnoreCase(tagName)) {
					return 1;
				}
				return 0;
			case TagCriteria:
				if (tagMatch.check(node)) {
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
					i++;
					nextNode = nextNode.getParentNode();
				}
				if (i == n) {
					return n;
				}
				return 0;
			case AtMostN:
				i = 0;
				nextNode = node.getParentNode();
				while (nextNode != null) {
					i++;
					j = nextCriteria.checkContext(nextNode);
					if (j > 0) {
						// was able to be consumed from nextNode
						// return total count of nodes consumed
						return i + j;
					}
					// did not meet criteria this time
					if (i == n) {
						// we hit the limit (failed to meet criteria)
						return 0;
					}
					nextNode = nextNode.getParentNode();
				}
				// got a null nextNode, or exhausted N nodes (failed to meet criteria)
				return 0;
			case Any:
				i = 0;
				nextNode = node.getParentNode();
				while (nextNode != null) {
					i++;
					j = nextCriteria.checkContext(nextNode);
					if (j > 0) {
						// was able to be consumed from nextNode
						// return total count of nodes consumed
						return i + j;
					}
					// did not meet criteria this time
					nextNode = nextNode.getParentNode();
				}
				// got a null nextNode (failed to meet criteria)
				return 0;
		}
		// failed to meet criteria
		throw new DOMContextException("DOMContextCriteria has unknown Type: " + this.type);
	}
}