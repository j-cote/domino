package org.jcote.domino.checkable.tag;

import org.jcote.domino.checkable.NodeCallback;
import org.w3c.dom.Node;

public class TagDontMatch extends TagMatch {

	public boolean checkImpl(Node node) {
		return !super.checkImpl(node);
	}
}
