package realeity.core.mdg.elements;

import java.util.Iterator;

import realeity.core.mdg.AbstractModuleDependencyGraph;

/**
 * 
 * @author alvine
 * november 2014
 *
 */
public class MDGIterator implements Iterator<AbstractGraphNode>{
	
	/**
	 * Structure that shoulb be navigated using the iterator.
	 */
	private AbstractModuleDependencyGraph graph;
	
	/**
	 * Index of the current node on which the iterator is pointing.
	 */
	private int currentNodeIndex;

	
	public MDGIterator(AbstractModuleDependencyGraph graph) {
		this.graph = graph;
		//TODO  : in all the direct accesses of a graph, use an mdg iterator instead
	}

	@Override
	public boolean hasNext() {
		return currentNodeIndex < graph.getNodeList().size();
	}

	@Override
	public AbstractGraphNode next() {
		return graph.getNodeList().get(currentNodeIndex++);
	}
	
	public int getCurrentNodeIndex() {
		return currentNodeIndex;
	}
	
	public AbstractModuleDependencyGraph getGraph() {
		return graph;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}

}
