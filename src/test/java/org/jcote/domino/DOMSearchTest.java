package org.jcote.domino;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.cyberneko.html.parsers.DOMParser;
import org.jcote.domino.DOMSearch;
import org.jcote.domino.checkable.NodeCallback;
import org.jcote.domino.checkable.context.DOMContext;
import org.jcote.domino.checkable.context.DOMContextCriteria;
import org.jcote.domino.checkable.context.DOMContextCriteria.Type;
import org.jcote.domino.checkable.tag.TagMatch;
import org.jcote.domino.exception.DOMSearchException;
import org.jcote.domino.util.Wrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


@RunWith(JUnit4.class)
public class DOMSearchTest {

	public DOMSearchTest() {
		
	}
	
	public Document createTestDoc() throws SAXException, IOException {
		InputStream is = getClass().getResourceAsStream("/test.html");
		DOMParser dp = new DOMParser();
        dp.parse(new InputSource(is));
        return dp.getDocument();
	}

	@Test
	public void tagMatchTest() throws SAXException, IOException, DOMSearchException {
		Document testDoc = createTestDoc();
		DOMSearch ds = new DOMSearch();

		final Wrapper<ArrayList<String>> matched1 = new Wrapper<>(new ArrayList<String>());
		TagMatch tm1 = new TagMatch("font", "face", "arial", new NodeCallback() {
			
			@Override
			public void perform(Node node) {
				matched1.get().add(node.getTextContent());
			}
		});
		
		ds.addCheckable(tm1);
		ds.execute(testDoc);
		

		List expected1 = new ArrayList<String>(Arrays.asList(
				new String[] {"normal", "italicized", "nineinchnails", "trent", "reznor"}));
		assertEquals(expected1, matched1.get());
	}
	
	@Test
	public void contextTest() throws SAXException, IOException, DOMSearchException {
		Document testDoc = createTestDoc();
		DOMSearch ds = new DOMSearch();

		final Wrapper<ArrayList<String>> matched2 = new Wrapper<>(new ArrayList<String>());

		DOMContext dc = new DOMContext(new NodeCallback() {
			
			@Override
			public void perform(Node node) {
				matched2.get().add(node.getTextContent());
			}
		});
		dc.addCriteria(new DOMContextCriteria().setTypeAsTagCriteria("font", "face", "arial"));
		dc.addCriteria(new DOMContextCriteria().setTypeAsExactlyN(1));
		dc.addCriteria(new DOMContextCriteria().setTypeAsTagCriteria("ul", "class", "listy"));
		
		ds.addCheckable(dc);
		ds.execute(testDoc);

		List expected2 = new ArrayList<String>(Arrays.asList(
				new String[] {"trent","reznor"}));
		assertEquals(expected2, matched2.get());
	}
}
