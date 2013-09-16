package org.jcote.domino.checkable.tag;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jcote.domino.checkable.Checkable;
import org.jcote.domino.checkable.NodeCallback;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author jordancote
 * 
 */
public class TagMatch extends Checkable {
	HashMap<String, HashMap<String,String>> matchTags;  // tag name -> (attr name -> attr value)
	
	/**
	 * 
	 */
	public TagMatch() {
		super();
		matchTags = new HashMap<>();
	}
	
	public TagMatch(NodeCallback callback) {
		super(callback);
		matchTags = new HashMap<>();
	}
	
	public TagMatch(String tagName, String attrName, String attrValue, NodeCallback callback) {
		this(callback);
		addCriteria(tagName, attrName, attrValue);
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
	public void addCriteria(String tagName, String attrName, String attrValue) {
		tagName = tagName.toUpperCase();
		if (matchTags.containsKey(tagName)) {
			// tag already exists, add criteria to matchTags entry
			matchTags.get(tagName).put(attrName, attrValue);
		} else {
			// tag does not exist, create new matchTags entry
			HashMap<String, String> attrMap = new HashMap<>();
			attrMap.put(attrName, attrValue);
			matchTags.put(tagName, attrMap);
		}
	}
	
	/**checks criteria on single tag
	 * @param node
	 */
	public boolean checkImpl(Node node) {
		String nodeName = node.getNodeName();
    	if (!matchTags.containsKey(nodeName)) {
    		return false;
    	}
    	if (node.getAttributes() == null) {
    		return false;
    	}
		// tag found, scan for all specified match attributes
		return checkAttrs(node);
	}
	
	/**
	 * Checks that all attributes are contained on the given node
	 * @param node
	 * @return
	 */
	public boolean checkAttrs(Node node) {
		NamedNodeMap attrs = node.getAttributes();
		if (attrs.getLength() == 0) {
			return false;
		}
		HashMap<String,String> matchAttrMap = matchTags.get(node.getNodeName());
		for (Entry<String, String> matchAttrEntry : matchAttrMap.entrySet()) {
			Node attrNode = attrs.getNamedItem(matchAttrEntry.getKey());
			if (!matchAttrEntry.getValue().equalsIgnoreCase(attrNode.getNodeValue())) {
				return false;
			}
		}
		return true;
	}
	
}