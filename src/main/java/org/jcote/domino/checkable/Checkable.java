package org.jcote.domino.checkable;

import org.jcote.domino.exception.DOMSearchException;
import org.w3c.dom.Node;

public abstract class Checkable {
	private NodeCallback callback;
	
	public Checkable() {
		
	}
	
	public Checkable(NodeCallback callback) {
		this.callback = callback;
	}
	
	public boolean check(Node node) throws DOMSearchException {
		if (this.checkImpl(node)) {
			if (callback != null) {
				callback.perform(node);
			}
			return true;
		}
		return false;
	}
	
	abstract public boolean checkImpl(Node node) throws DOMSearchException;
}
