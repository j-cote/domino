package com.jcote.domino;

import java.util.HashMap;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author jordancote
 * 
 */
public class TagMatch implements Checkable {
	HashMap<String, TagMatchCriteria> matchTags;
	
	/**
	 * 
	 */
	public TagMatch() {
		matchTags = new HashMap<String, TagMatchCriteria>();
	}
	
	public TagMatch(String tagName, String attrName, String attrValue, NodeCallback callback) {
		this();
		addCriteria(tagName, attrName, attrValue, callback);
	}
	
	public TagMatch(String tagName, String attrName, String attrValue) {
		this();
		addCriteria(tagName, attrName, attrValue);
	}
	
	/**
	 * @param tagName
	 * @param attrName
	 * @param attrValue
	 * @param callback
	 */
	public void addCriteria(String tagName, String attrName, String attrValue, NodeCallback callback) {
		tagName = tagName.toUpperCase();
		if (matchTags.containsKey(tagName)) {
			// tag already exists, add criteria to matchTags entry
			matchTags.get(tagName).addCriteria(attrName, attrValue, callback);
		} else {
			// tag does not exist, create new matchTags entry
			TagMatchCriteria tagCriteria = new TagMatchCriteria();
			tagCriteria.addCriteria(attrName, attrValue, callback);
			matchTags.put(tagName, tagCriteria);
		}
	}
	
	public void addCriteria(String tagName, String attrName, String attrValue) {
		addCriteria(tagName, attrName, attrValue, null);
	}

	
	/**checks criteria on single node
	 * @param node
	 */
	public boolean check(Node node) {
		// check tag
		String nodeName = node.getNodeName();
    	if (matchTags.containsKey(nodeName)) {
    		if (node.getAttributes() != null) {
    			// tag found, scan all tag's attributes
    			return checkAttr(node);
    		}
    	}
    	return false;
	}
	
	// checks attribute list against criteria
	public boolean checkAttr(Node node) {
		NamedNodeMap attrs = node.getAttributes();
		boolean found = false;
		TagMatchCriteria tagCriteria = matchTags.get(node.getNodeName());
		for (int i=0; i< attrs.getLength(); i++) {
			Node attr = attrs.item(i);
			String attrName = attr.getNodeName();
			if (tagCriteria.containsAttrName(attrName)) {
				// attribute name found, check this attribute value
				if (checkAttrValue(tagCriteria, node, attr)) {
					found = true;
				}
			}
		}
		return found;
	}
	
	// checks for possible matching values for one attribute
	public boolean checkAttrValue(TagMatchCriteria tagCriteria, Node node, Node attr) {
		String attrName = attr.getNodeName();
		String attrValue = attr.getNodeValue();
		if (tagCriteria.containsAttrValue(attrName, attrValue)) {
			// criteria matched
			// execute callback on this node, if it exists
			NodeCallback callback = tagCriteria.getCallback(attrName, attrValue);
			if (callback != null) {
				callback.perform(node);
			}
			return true;
		}
		return false;
	}
}