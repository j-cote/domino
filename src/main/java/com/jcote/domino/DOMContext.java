package com.jcote.domino;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Node;


public class DOMContext implements Checkable {
	public LinkedList<DOMContextCriteria> contextCriteriaChain;

	public DOMContext() {
		this.contextCriteriaChain = new LinkedList<DOMContextCriteria>();
	}
	
	/**
	 * @param criteria
	 */
	public void addCriteria(DOMContextCriteria criteria) {
		contextCriteriaChain.add(criteria);
	}
	
	/**
	 * 
	 */
	public void removeLastCriteria() {
		contextCriteriaChain.removeLast();
	}
	
	/**
	 * 
	 */
	public void removeFirstCriteria() {
		contextCriteriaChain.removeFirst();
	}
	
	/**
	 * Check if the parent node chain (context) of a given node matches the pattern of this instance.
	 * @param node
	 * @return True if node is within specified context, False otherwise.
	 * @throws DOMSearchException 
	 */
	public boolean check(Node node) throws DOMSearchException {
		DOMContextCriteria contextCriteria;
		int consumed = 0;
		
		for (int i = 0; i < contextCriteriaChain.size(); i++) {
			contextCriteria = contextCriteriaChain.get(i);
			// try to consume some nodes with the next criteria
			try {
				consumed += contextCriteria.checkContext(node);
			} catch (DOMContextException e) {
				throw new DOMSearchException(e);
			}
			// see if criteria was met
			if (consumed == 0) {
				// chain broken (criteria not met)
				return false;
			}
			// skip consumed nodes
			for (int j = 0; j < consumed; j++) {
				// DOMContextCriteria::checkContext() assures these nodes will not be null
				node = node.getParentNode();
			}
		}
		// successfully traversed criteria chain
		return true;
	}
}
