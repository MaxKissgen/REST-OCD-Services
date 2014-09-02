package i5.las2peer.services.ocd.algorithms;

import i5.las2peer.services.ocd.adapters.AdapterException;
import i5.las2peer.services.ocd.algorithms.RandomWalkLabelPropagationAlgorithm;
import i5.las2peer.services.ocd.algorithms.utils.OcdAlgorithmException;
import i5.las2peer.services.ocd.graph.Cover;
import i5.las2peer.services.ocd.graph.CustomGraph;
import i5.las2peer.services.ocd.metrics.ExtendedModularity;
import i5.las2peer.services.ocd.metrics.MetricException;
import i5.las2peer.services.ocd.metrics.StatisticalMeasure;
import i5.las2peer.services.ocd.testsUtil.OcdTestGraphFactory;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.Vector;

import y.base.Node;

/*
LDAV=ones(1,num_vertices(SparseGraph));
LDAV=LDAV./num_vertices(SparseGraph);
LDAVNext=LDAV;
NumOfIterations=0; % it shows number of iterations to converge...
diff=1;
while(diff>0.00001)
    temp=LDAV*NDATM;
    LDAV=LDAVNext;
    LDAVNext=temp;
    diff=norm(LDAVNext-LDAV);
    NumOfIterations=NumOfIterations+1
    
end

NDATM normalized DA matrix
LDAV DA vector
 */

/*
 * Test Class for the Random Walk Label Propagation Algorithm
 */
public class RandomWalkLabelPropagationAlgorithmTest {

	@Test
	public void testRandomWalkExecution() throws OcdAlgorithmException {
		Matrix transitionMatrix = new Basic2DMatrix(2, 2);
		transitionMatrix.set(0, 0, 0.9);
		transitionMatrix.set(0, 1, 0.5);
		transitionMatrix.set(1, 0, 0.1);
		transitionMatrix.set(1, 1, 0.5);
		System.out.println("Transition Matrix:");
		System.out.println(transitionMatrix.transpose());
		RandomWalkLabelPropagationAlgorithm algo = new RandomWalkLabelPropagationAlgorithm(0.05, 1000, 0.001);
		Vector vec = algo.executeRandomWalk(transitionMatrix);
		System.out.println("Steady State Vector:");
		System.out.println(vec);
	}
	
	@Test
	public void testWithKnownResults() throws OcdAlgorithmException {
		System.out.println("Known Result Test");
		CustomGraph graph = OcdTestGraphFactory.getAperiodicTwoCommunitiesGraph();
		RandomWalkLabelPropagationAlgorithm algo = new RandomWalkLabelPropagationAlgorithm(0.1, 1000, 0.001);
		Matrix disassortativityMatrix = algo.getTransposedDisassortativityMatrix(graph);
		System.out.println("DA M:\n" + disassortativityMatrix);
		Vector disassortativityVector = algo.executeRandomWalk(disassortativityMatrix);
		System.out.println("DA Vec:\n" + disassortativityVector);
		Vector leadershipVector = algo.getLeadershipValues(graph,
				disassortativityVector);
		System.out.println("LS Vec:\n" + leadershipVector);
		Map<Node, Double> followerMap = algo.getFollowerDegrees(graph,
				leadershipVector);
		System.out.println("Follower Degs:\n" + followerMap);
		List<Node> leaders = algo.getGlobalLeaders(followerMap);
		System.out.println("Leaders:\n" + leaders);
		Cover cover = algo.labelPropagationPhase(graph, leaders);
		System.out.println(cover);
	}
	
	@Ignore
	@Test
	public void testOnTwoCommunities() throws OcdAlgorithmException {
		CustomGraph graph = OcdTestGraphFactory.getTwoCommunitiesGraph();
		RandomWalkLabelPropagationAlgorithm algo = new RandomWalkLabelPropagationAlgorithm(0.2, 1000, 0.001);
		Cover cover = algo.detectOverlappingCommunities(graph);
		System.out.println(cover.toString());
	}
	
	@Ignore
	@Test
	public void testOnAperiodicTwoCommunities() throws OcdAlgorithmException {
		CustomGraph graph = OcdTestGraphFactory.getAperiodicTwoCommunitiesGraph();
		RandomWalkLabelPropagationAlgorithm algo = new RandomWalkLabelPropagationAlgorithm(0.2, 1000, 0.001);
		Cover cover = algo.detectOverlappingCommunities(graph);
		System.out.println(cover.toString());
	}
	
	@Test
	public void testOnSawmill() throws OcdAlgorithmException, AdapterException, FileNotFoundException {
		CustomGraph graph = OcdTestGraphFactory.getSawmillGraph();
		RandomWalkLabelPropagationAlgorithm algo = new RandomWalkLabelPropagationAlgorithm(0.2, 1000, 0.001);
		Cover cover = algo.detectOverlappingCommunities(graph);
		System.out.println(cover.toString());	
	}
	
	@Test
	public void testProfitabilityDelta() throws OcdAlgorithmException, AdapterException, FileNotFoundException, MetricException {
		System.out.println();
		System.out.println();
		System.out.println("Test Profitability Delta");
		double[] profitabililtyDeltas = new double[11];
		profitabililtyDeltas[0] = 0.050;
		profitabililtyDeltas[1] = 0.075;
		profitabililtyDeltas[2] = 0.100;
		profitabililtyDeltas[3] = 0.125;
		profitabililtyDeltas[4] = 0.150;
		profitabililtyDeltas[5] = 0.175;
		profitabililtyDeltas[6] = 0.200;
		profitabililtyDeltas[7] = 0.225;
		profitabililtyDeltas[8] = 0.250;
		profitabililtyDeltas[9] = 0.275;
		profitabililtyDeltas[10] = 0.300;
		StatisticalMeasure metric = new ExtendedModularity();
		CustomGraph graph = OcdTestGraphFactory.getSawmillGraph();
		for(int i=0; i < profitabililtyDeltas.length; i++) {
			System.out.println("Delta: " + profitabililtyDeltas[i]);
			RandomWalkLabelPropagationAlgorithm algo = new RandomWalkLabelPropagationAlgorithm(profitabililtyDeltas[i], 1000, 0.001);
			Cover cover = algo.detectOverlappingCommunities(graph);
			metric.measure(cover);
			System.out.println(cover.toString());
		}
	}

}