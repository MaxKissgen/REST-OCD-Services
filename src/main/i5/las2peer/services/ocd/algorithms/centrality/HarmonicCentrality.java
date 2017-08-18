package i5.las2peer.services.ocd.algorithms.centrality;

import java.util.HashSet;
import java.util.Set;

import i5.las2peer.services.ocd.graphs.CentralityCreationLog;
import i5.las2peer.services.ocd.graphs.CentralityCreationType;
import i5.las2peer.services.ocd.graphs.CentralityMap;
import i5.las2peer.services.ocd.graphs.CustomGraph;
import i5.las2peer.services.ocd.graphs.GraphType;
import y.algo.ShortestPaths;
import y.base.Node;
import y.base.NodeCursor;

public class HarmonicCentrality implements CentralityAlgorithm {
	
	public CentralityMap getValues(CustomGraph graph) {
		NodeCursor nc = graph.nodes();
		
		CentralityMap res = new CentralityMap(graph);
		res.setCreationMethod(new CentralityCreationLog(CentralityCreationType.HARMONIC_CENTRALITY, this.compatibleGraphTypes()));
		
		double[] edgeWeights = graph.getEdgeWeights();
		while(nc.ok()) {
			Node node = nc.node();
			double[] dist = new double[graph.nodeCount()];
			ShortestPaths.dijkstra(graph, node, true, edgeWeights, dist);
			double inverseDistSum = 0.0;
			for(double d : dist) {
				if(d != 0.0) {
					inverseDistSum += 1.0/d;
				}
			}
			res.setNodeValue(node, 1.0/(graph.nodeCount()-1.0)*inverseDistSum);
			nc.next();
		}
		
		return res;
	}

	@Override
	public Set<GraphType> compatibleGraphTypes() {
		Set<GraphType> compatibleTypes = new HashSet<GraphType>();
		compatibleTypes.add(GraphType.WEIGHTED);
		return compatibleTypes;
	}

	@Override
	public CentralityCreationType getAlgorithmType() {
		return CentralityCreationType.HARMONIC_CENTRALITY;
	}
}