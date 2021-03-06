package i5.las2peer.services.ocd.centrality.measures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import i5.las2peer.services.ocd.centrality.data.CentralityCreationLog;
import i5.las2peer.services.ocd.centrality.data.CentralityCreationType;
import i5.las2peer.services.ocd.centrality.data.CentralityMeasureType;
import i5.las2peer.services.ocd.centrality.utils.CentralityAlgorithm;
import i5.las2peer.services.ocd.centrality.data.CentralityMap;
import i5.las2peer.services.ocd.graphs.CustomGraph;
import i5.las2peer.services.ocd.graphs.GraphType;
import y.algo.NetworkFlows;
import y.base.DataProvider;
import y.base.Edge;
import y.base.EdgeCursor;
import y.base.EdgeMap;
import y.base.Node;
import y.base.NodeCursor;
import y.util.Maps;

/**
 * Implementation of Flow Centrality.
 * See: Freeman, Linton C and Borgatti, Stephen P and White, Douglas R. 1991. Centrality in valued graphs: A measure of betweenness based on network flow. 
 * @author Tobias
 *
 */
public class FlowBetweenness implements CentralityAlgorithm {
	
	public CentralityMap getValues(CustomGraph graph) throws InterruptedException {
		CentralityMap res = new CentralityMap(graph);
		res.setCreationMethod(new CentralityCreationLog(CentralityMeasureType.FLOW_BETWEENNESS, CentralityCreationType.CENTRALITY_MEASURE, this.getParameters(), this.compatibleGraphTypes()));
		
		Node[] nodeArray = graph.getNodeArray();
		// The flow capacities are given by the edge weights, only integers are supported
		double[] weights = graph.getEdgeWeights();
		int[] intWeights = new int[weights.length];
		for(int i = 0; i < graph.getEdgeWeights().length; i++) {
			intWeights[i] = (int) weights[i];
		}
		DataProvider capacities = Maps.createIndexEdgeMap(intWeights);
		
		// Set initial values to 0
		NodeCursor nc = graph.nodes();
		while(nc.ok()) {
			res.setNodeValue(nc.node(), 0.0);
			nc.next();
		}
		
		// For each pair (i,j) of nodes calculate the maximum flow and add flows through the individual nodes to their centrality values
		for(int i = 0; i < nodeArray.length; i++) {
			if(Thread.interrupted()) {
				throw new InterruptedException();
			}
			Node source = nodeArray[i];
			for(int j = 0; j < nodeArray.length; j++) {
				if(i != j) {
					Node sink = nodeArray[j];
					
					// Instantiate data structures
					Map<Edge, Integer> flowMap = new HashMap<Edge, Integer>();
					EdgeMap flowEdgeMap = Maps.createEdgeMap(flowMap);
					
					// Calculate maximum flows with given source and sink
					int maximumFlow = NetworkFlows.calcMaxFlow(graph, source, sink, capacities, flowEdgeMap);
					
					// Measure flow through all the nodes
					nc = graph.nodes();
					while(nc.ok()) {
						Node node = nc.node();
						if(node != source && node != sink && maximumFlow != 0) {
							// Calculate flow through node
							int maximumFlowThroughNode = 0;
							EdgeCursor inEdges = node.inEdges();
							while(inEdges.ok()) {
								maximumFlowThroughNode += flowEdgeMap.getInt(inEdges.edge());
								inEdges.next();
							}
							res.setNodeValue(node, res.getNodeValue(node) + maximumFlowThroughNode/maximumFlow);
						}
						nc.next();
					}	
				}
			}	
		}
		return res;
	}

	@Override
	public Set<GraphType> compatibleGraphTypes() {
		Set<GraphType> compatibleTypes = new HashSet<GraphType>();
		compatibleTypes.add(GraphType.DIRECTED);
		compatibleTypes.add(GraphType.WEIGHTED);
		return compatibleTypes;
	}

	@Override
	public CentralityMeasureType getCentralityMeasureType() {
		return CentralityMeasureType.FLOW_BETWEENNESS;
	}
	
	@Override
	public HashMap<String, String> getParameters() {
		return new HashMap<String, String>();
	}
	
	@Override
	public void setParameters(Map<String, String> parameters) throws IllegalArgumentException {
		if(parameters.size() > 0) {
			throw new IllegalArgumentException();
		}
	}
}
