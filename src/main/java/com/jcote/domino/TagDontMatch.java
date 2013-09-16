package com.jcote.domino;

import org.w3c.dom.Node;

public class TagDontMatch extends TagMatch {

	public TagDontMatch() {
		super();
	}

	public TagDontMatch(String tagName, String attrName, String attrValue,
			NodeCallback callback) {
		super(tagName, attrName, attrValue, callback);
	}

	public TagDontMatch(String tagName, String attrName, String attrValue) {
		super(tagName, attrName, attrValue);
	}

	public boolean check(Node node) {
		return !super.check(node);
	}
}
