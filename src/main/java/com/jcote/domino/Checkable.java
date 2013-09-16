package com.jcote.domino;

import org.w3c.dom.Node;

public interface Checkable {
	public boolean check(Node node) throws DOMSearchException;
}
