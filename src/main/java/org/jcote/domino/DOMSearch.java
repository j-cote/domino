package org.jcote.domino;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jcote.domino.checkable.Checkable;
import org.jcote.domino.checkable.context.DOMContextCriteria;
import org.jcote.domino.exception.DOMSearchException;
import org.w3c.dom.Node;


/**
 * @author jordancote
 * Searches a DOM structure starting from given node.
 * (However, it does allow context searches extending beyond the starting node - this may change in a future version)
 * Looks for tags matching discrete sets of TagMatch criteria.  
 * Each TagMatch criteria may also include a DOMContextCriteria.
 * TagMatches will be performed on each node in the order which they were added to DOMSearch.
 * A TagMatch match count limit will cause the TagMatch to stop being performed on nodes once the limit is reached.
 * A TagMatch ignore count can be implemented by the user. 
 * Just have your TagMatch callback be a closure that ignores the first N executions.  It must be a closure to keep state.
 * Similarly, your closure may ignore executions until after a different TagMatch's closure has been called.
 * This gives you the flexibility to specify positioning constraints in addition to contextual constraints.
 *  
 * @see TagMatchCriteria
 * @see DOMContextCriteria
 */
public class DOMSearch {
	private LinkedList<Checkable> checkableChain;
	private HashMap<Checkable, Integer> checkableLimits;
	
	public DOMSearch() {
		checkableChain = new LinkedList<Checkable>();
		checkableLimits = new HashMap<Checkable, Integer>();
	}

	public DOMSearch(Checkable checkable, int limit) throws DOMSearchException {
		this();
		addCheckable(checkable, limit);
	}

	/**
	 * Add a checkable with no limit
	 * @param checkable
	 * @throws DOMSearchException
	 */
	public void addCheckable(Checkable checkable) throws DOMSearchException {
		addCheckable(checkable, -1);
	}
	
	/**
	 * Add a checkable with specified limitation on the number of matches that are allowed in a DOM crawl.
	 * The limit is enforced in accordance with the way the DOM is searched (Depth-First).
	 * The result of the operation is the 
	 * @param checkable
	 * @param limit
	 * @throws DOMSearchException
	 */
	public void addCheckable(Checkable checkable, int limit) throws DOMSearchException {
		if (limit == 0) {
			throw new DOMSearchException("DOMSearch: Limit must be positive (or negative to indicate no limit)");
		}
		if (checkable == null) {
			throw new DOMSearchException("DOMSearch: checkable must not be NULL");
		}
		checkableLimits.put(checkable, limit);
		checkableChain.add(checkable);
	}
	
	/**
	 * Run the DOMSearch.
	 * Uses a mutable copy of the Checkables assembled so far, so that this instance may be
	 * re-used for multiple searches.
	 * @param node
	 * @throws DOMSearchException
	 */
	public void execute(Node node) throws DOMSearchException {
		DOMSearchInstance instance = new DOMSearchInstance(this.checkableChain, this.checkableLimits);
		instance.execute(node);
	}
}

