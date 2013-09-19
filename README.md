Domino
======

Searches a DOM tree structure relative to a given node.

 * TagMatch criteria allows you to specify particular attribute key/value pairs to look for: `<tag key="value">blah</tag>`
 * DOMContext criteria allows you to restrict matching tags to have a particular pattern of parent nodes: `<parent><child /></parent>`
 * Nodes will be traversed (and matched) in Depth-first manner. This means it will follow the order they appear in HTML source.
 * Criteria will be checked on each node in the order which they were added to DOMSearch.
 * A match count limit will cause criteria to stop being checked on nodes once the limit is reached.
 * Similar mechanisms (ex, ignore first N) can be implemented by the user. Just have your NodeCallback be a closure that ignores the first N executions.  It must be a closure in order to keep state.
 * Along these lines, your closure may ignore executions until after a different TagMatch's closure has been called
 * This gives you the flexibility to specify positioning constraints in addition to contextual constraints.


### Example 1
Finds all nodes in the entire tree of the document that look like: 
`<font face="arial" ...>contents here</font>`

```
Document doc = ...;
TagMatch tm = new TagMatch("font", "face", "arial", new NodeCallback() {
	@Override
	public void perform(Node node) {
	  System.out.println("found one!");
	}
});

DOMSearch ds = new DOMSearch();
ds.addCheckable(tm);
ds.execute(doc);
```
 
###Example 2
Finds all nodes in the subtree of nodeToSearch that look like:
`<p class="summer"><?><font face="arial">contents here</font></?></p>`
where `<?>` is any tag

```
final Wrapper<String> matched = new Wrapper<>(null);

DOMContext dc = new DOMContext(new NodeCallback() {
	@Override
	public void perform(Node node) {
		matched.set(node.getTextContent());
	}
});
dc.addCriteria(new DOMContextCriteria().setTypeAsTagCriteria("font", "face", "arial"));
dc.addCriteria(new DOMContextCriteria().setTypeAsExactlyN(1));
dc.addCriteria(new DOMContextCriteria().setTypeAsTagCriteria("p", "class", "summer"));

DOMSearch ds = new DOMSearch();
ds.addCheckable(dc);
ds.execute(nodeToSearch);

System.out.println("contents: " + matched.get());
```
