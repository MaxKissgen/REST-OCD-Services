package i5.las2peer.services.ocd.graphs.properties;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import i5.las2peer.services.ocd.graphs.CustomGraph;
import y.base.Node;

@RunWith(MockitoJUnitRunner.class)
public class ClusteringCoefficientTest {
	
	@Spy
	ClusteringCoefficient property;
	
	@Test
	public void localUndirected() {

		ClusteringCoefficient property = new ClusteringCoefficient();
		double result;

		result = property.localUndirected(1, 4);
		assertEquals(0.1666666, result, 0.00001);

		result = property.localUndirected(2, 5);
		assertEquals(0.2, result, 0.00001);
		
		result = property.localUndirected(4, 6);
		assertEquals(0.266666, result, 0.00001);
		
		result = property.localUndirected(3, 3);
		assertEquals(1.0, result, 0.00001);
		
		result = property.localUndirected(1, 2);
		assertEquals(1.0, result, 0.00001);

		result = property.localUndirected(0, 3);
		assertEquals(0.0, result, 0.00001);

		result = property.localUndirected(4, 0);
		assertEquals(0.0, result, 0.00001);

	}
	
	@Test
	public void localDirected() {

		ClusteringCoefficient property = new ClusteringCoefficient();
		double result;

		result = property.localDirected(1, 4);
		assertEquals(0.083333, result, 0.00001);

		result = property.localDirected(2, 5);
		assertEquals(0.1, result, 0.00001);
		
		result = property.localDirected(4, 6);
		assertEquals(0.1333333, result, 0.00001);
		
		result = property.localDirected(3, 3);
		assertEquals(0.5, result, 0.00001);

		result = property.localDirected(0, 3);
		assertEquals(0.0, result, 0.00001);

		result = property.localDirected(4, 0);
		assertEquals(0.0, result, 0.00001);

	}
	
	@Test
	public void calculateNodeLocalUndirected() {

		CustomGraph graph = new CustomGraph();
		Node n1 = graph.createNode();
		Node n2 = graph.createNode();
		Node n3 = graph.createNode();
		Node n4 = graph.createNode();
		Node n5 = graph.createNode();
		Node n6 = graph.createNode();
		
		graph.createEdge(n1, n2);
		graph.createEdge(n2, n1);		
		graph.createEdge(n1, n3);
		graph.createEdge(n3, n1);		
		graph.createEdge(n1, n4);
		graph.createEdge(n4, n1);
		graph.createEdge(n1, n5);
		graph.createEdge(n5, n1);
		graph.createEdge(n1, n6);
		graph.createEdge(n6, n1);
		
		graph.createEdge(n2, n3);
		graph.createEdge(n3, n2);		
		graph.createEdge(n5, n4);
		graph.createEdge(n4, n5);		
		
		property.calculateLocal(n1, graph);
		Mockito.verify(property, Mockito.times(1)).localUndirected(2, 5);

	}	
	
	@Test
	public void calculateNodeLocal() {

		CustomGraph graph = new CustomGraph();
		Node n1 = graph.createNode();
		Node n2 = graph.createNode();
		Node n3 = graph.createNode();
		Node n4 = graph.createNode();
		graph.createEdge(n1, n2);
		graph.createEdge(n2, n1);
		
		graph.createEdge(n1, n3);
		graph.createEdge(n3, n1);
		
		graph.createEdge(n1, n4);
		graph.createEdge(n4, n1);
		
		graph.createEdge(n2, n3);
		graph.createEdge(n3, n2);
		
		graph.createEdge(n3, n4);
		graph.createEdge(n4, n3);		
		
		property.calculateLocal(n1, graph);
		Mockito.verify(property, Mockito.times(1)).localUndirected(2, 3);

	}
	
	@Test
	public void initialize() {

		CustomGraph graph = new CustomGraph();
		Node n1 = graph.createNode();
		Node n2 = graph.createNode();
		Node n3 = graph.createNode();
		Node n4 = graph.createNode();
		graph.createEdge(n1, n2);
		graph.createEdge(n2, n1);
		
		graph.createEdge(n1, n3);
		graph.createEdge(n3, n1);
		
		graph.createEdge(n2, n3);
		graph.createEdge(n3, n2);
		
		graph.createEdge(n3, n4);
		graph.createEdge(n4, n3);
				
		property.calculate(graph);
		Mockito.verify(property, Mockito.times(2)).localUndirected(1, 2);
		Mockito.verify(property, Mockito.times(1)).localUndirected(1, 3);
		Mockito.verify(property, Mockito.times(1)).localUndirected(0, 1);
		
	}
}
