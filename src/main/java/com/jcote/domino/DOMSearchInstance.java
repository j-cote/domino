package com.jcote.domino;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

/**
 * DOMSearchInstance
 * A one-shot class used for executing an instance of a DOMSearch
 * 
 * @author jordancote
 *
 */
public class DOMSearchInstance {
	private List<Checkable> checkableChain;
	private Map<Checkable, Integer> checkableLimits;
	
	public DOMSearchInstance(List<Checkable> chain, Map<Checkable, Integer> limits) {
		this.checkableChain = new LinkedList<>(chain);
		this.checkableLimits = new HashMap<>(limits);
	}
	
	/**
	 * Execute the search
	 * Run each checkable in the chain on the entire node tree
	 * If there is a limit imposed on a check, it is respected globally (ie, within children)
	 * This method will modify the instance variables for chain and limit
	 * @param node
	 * @throws DOMSearchException 
	 */
	public void execute(Node node) throws DOMSearchException {
		// Run all checkable criteria, in order, on current node
		int i = 0;
		Iterator<Checkable> iter = checkableChain.iterator();
		while (iter.hasNext()) {
			Checkable checkable = iter.next();
			int limit = checkableLimits.get(checkable);
			if (checkable.check(node)) {
				if (limit > 1) {
					// decrement the limit
					checkableLimits.put(checkable, limit - 1);
				} else if (limit == 1) {
					// limit exhausted, remove TagMatch
					checkableLimits.remove(checkable);
					checkableChain.remove(i);  // use i to avoid comparison on list elements
				} else {
					// (limit < 1) : no limit, no action
				}
			}
			i++;
		}
		
		// Recur on each child node, in order
        Node child = node.getFirstChild();
        while (child != null) {
            //check for criteria match on node
        	execute(child);
            child = child.getNextSibling();
        }
	}
}
