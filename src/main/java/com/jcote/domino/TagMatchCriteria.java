package com.jcote.domino;

import java.util.HashMap;

import org.w3c.dom.Node;

interface NodeCallback {
	public void perform(Node node);
}

// matches a tag based on type, and by a single attribute name=value being present
public class TagMatchCriteria {
	// TODO: replace with multimap?
	protected HashMap<String,HashMap<String, NodeCallback>> criteria;  // attribute key->[value,callback] mapping
	
	public TagMatchCriteria() {
		criteria = new HashMap<String,HashMap<String, NodeCallback>>();
	}
	
	public void addCriteria(String key, String value, NodeCallback callback) {
		key = key.toUpperCase();
		value = value.toUpperCase();
		if (criteria.containsKey(key)) {
			criteria.get(key).put(value, callback);
		} else {
			HashMap<String, NodeCallback> map = new HashMap<String, NodeCallback>();
			map.put(value, callback);
			criteria.put(key, map);
		}
	}
	
	public boolean containsAttrName(String name) {
		name = name.toUpperCase();
		return criteria.containsKey(name);
	}
	
	public boolean containsAttrValue(String name, String value) {
		name = name.toUpperCase();
		value = value.toUpperCase();
		return criteria.get(name).containsKey(value);
	}
	
	public NodeCallback getCallback(String name, String value) {
		name = name.toUpperCase();
		value = value.toUpperCase();
		return criteria.get(name).get(value);
	}
}
