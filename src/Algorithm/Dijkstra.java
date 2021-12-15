package Algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class Dijkstra {
    public static void computePaths(Vertex source) {
        source.minDistance = 0.;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<>();
        vertexQueue.add(source);
        source.setVisited(true);
//		System.out.println("Queue:" + vertexQueue);

        while (!vertexQueue.isEmpty()) {
            Vertex currentVertex = vertexQueue.poll();
//			System.out.println("currentVertex: "+currentVertex);
            for (Edge edge : currentVertex.getConnections()) {
                Vertex v = edge.getTargetVertex();
//				System.out.println("target: " + v);

                if (!v.isVisited()) {
                    double newDistance = currentVertex.getMinDistance() + edge.getEdgeWeight();

                    if (newDistance < v.getMinDistance()) {
                        vertexQueue.remove(v);
                        v.setMinDistance(newDistance);
                        v.setPrevious(currentVertex);
                        vertexQueue.add(v);
                    }

                }
            }
            currentVertex.setVisited(true);
        }
    }

    public static List<Vertex> getShortestPathTo(Vertex target) {
        List<Vertex> path = new ArrayList<Vertex>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.getPrevious())
            path.add(vertex);

        Collections.reverse(path);
        return path;
    }
}
