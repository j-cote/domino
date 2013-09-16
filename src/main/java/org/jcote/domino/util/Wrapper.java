package org.jcote.domino.util;

public class Wrapper<E> {
	private E object;
	public Wrapper(E object) {
		this.set(object);
	}
	public E get() {
		return object;
	}
	public void set(E object) {
		this.object = object;
	}	
}