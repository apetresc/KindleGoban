package com.amazon.kindle.app.go.model.sgf;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SGFIterator {

	private SGFGameTree tree;
	private SGFSequence currentSequence;
	private Iterator inner;
	
	SGFIterator(SGFGameTree tree) {
		this.tree = tree;
		this.currentSequence = tree.getSequence();
		inner = currentSequence.getNodes().iterator();
	}
	
	public SGFNode next() throws NoSuchElementException {
		try {
			if (inner.hasNext()) {
				return (SGFNode) inner.next();
			} else if (this.tree.getSubtrees().length > 0) {
				this.tree = this.tree.getSubtrees()[0];
				this.currentSequence = tree.getSequence();
				inner = currentSequence.getNodes().iterator();
				return next();
			}
		} catch (NullPointerException npe) {
			throw new NoSuchElementException();
		}
		throw new NoSuchElementException();
	}
	
	public boolean hasNext() {
		if (inner.hasNext()) {
			return true;
		} else if (this.tree.getSubtrees().length > 0 
				&& this.tree.getSubtrees()[0].getSequence().getNodes().iterator().hasNext()) {
			return true;
		}
		return false;
	}
	
	public boolean hasVariation() {
		return (!inner.hasNext() && this.tree.getSubtrees().length > 1);
	}
}
