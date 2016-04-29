package org.javaneat.visualization;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Graphs;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.util.PredicatedParallelEdgeIndexFunction;
import org.apache.commons.math3.util.FastMath;
import org.javaneat.genome.ConnectionGene;
import org.javaneat.genome.NEATGenome;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * Created by Mitchell on 4/14/2016.
 */
public final class Visualizer {

    private static final int nodeSpacingScale = 4;

    private Visualizer() {
    }

    public static BufferedImage getImage(boolean squareInputs, int squareLength, int outputNum, NEATGenome genome) {

        Graph<Integer, Edge> graph = Graphs.synchronizedDirectedGraph(new DirectedSparseMultigraph<>());

        genome.getNeuronGeneList().stream().forEach(neuronGene -> graph.addVertex(neuronGene.getNeuronID()));
        genome.getConnectionGeneList().stream()
                .filter(ConnectionGene::getEnabled)
                .forEach(connectionGene -> graph.addEdge(new Edge(connectionGene), connectionGene.getFromNode(), connectionGene.getToNode()));

        FRLayout<Integer, Edge> layout = new FRLayout<>(graph);
        layout.setSize(new Dimension(400, 200));

        layout.setRepulsionMultiplier(.5);
        layout.setAttractionMultiplier(5);

        if (squareInputs) {
            int currentNode = 0;

            layout.setLocation(currentNode, new Point2D.Double(squareLength * nodeSpacingScale, squareLength * nodeSpacingScale));
            layout.lock(currentNode, true);
            currentNode++;

            for (int row = 0; row < squareLength; row++) {
                for (int column = 0; column < squareLength; column++) {
                    layout.setLocation(currentNode, new Point2D.Double(column * nodeSpacingScale, row * nodeSpacingScale));
                    layout.lock(currentNode, true);
                    currentNode++;
                }
            }

            for (int row = 0; row < outputNum; row++) {
                layout.setLocation(currentNode, new Point2D.Double(squareLength * 32, row * 32));
                layout.lock(currentNode, true);
                currentNode++;
            }
        }

        layout.initialize();

        VisualizationViewer<Integer, Edge> vv = new VisualizationViewer<>(layout, new Dimension(400, 200));
        PredicatedParallelEdgeIndexFunction<Integer, Edge> predicatedParallelEdgeIndexFunction = PredicatedParallelEdgeIndexFunction.getInstance();
        predicatedParallelEdgeIndexFunction.setPredicate(input -> true);
        vv.getRenderContext().setParallelEdgeIndexFunction(predicatedParallelEdgeIndexFunction);
        vv.getRenderContext().setEdgeStrokeTransformer(input -> new BasicStroke(FastMath.abs((float) (3 * input.weight))));
        vv.getRenderContext().setEdgeArrowStrokeTransformer(input -> new BasicStroke(3));
        vv.getRenderContext().setEdgeDrawPaintTransformer(input -> {
            if (input.weight >= 0)
                return Color.BLACK;
            else
                return Color.RED;
        });
        vv.getRenderContext().setVertexFillPaintTransformer(input -> {
            if (input == 0) { // Bias
                return Color.GREEN;
            }
            if (input < squareLength * squareLength + 1) { // Inputs
                return Color.BLUE;
            }
            if (input < squareLength * squareLength + 1 + outputNum) { // Outputs
                return Color.RED;
            } else { // Hidden
                return Color.GRAY;
            }
        });
        //vv.getModel().getRelaxer().setSleepTime(10);
        vv.setGraphMouse(new DefaultModalGraphMouse(.9f, 1 / .9f));

        JFrame frame = new JFrame("Simple Graph View");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);


        return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    }

    private static class Edge {
        final double weight;

        Edge(ConnectionGene connectionGene) {
            this.weight = connectionGene.getWeight();
        }

        @Override
        public String toString() {
            return String.format("[%.4f]", weight);
        }
    }
}
