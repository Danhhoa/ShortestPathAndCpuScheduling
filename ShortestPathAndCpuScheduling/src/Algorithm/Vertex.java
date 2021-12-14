package Algorithm;

import java.util.ArrayList;

public class Vertex  implements Comparable<Vertex> {
    private boolean visited;
    private String name;
    public ArrayList<Edge> connections; // canh ke
    public double minDistance = Double.POSITIVE_INFINITY;
    public Vertex previous;

    public Vertex(String name) {
        this.name = name;
        this.connections = new ArrayList<>();
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public ArrayList<Edge> getConnections() {
        return connections;
    }

    public void setConnections(ArrayList<Edge> connections) {
        this.connections = connections;
    }

    public void addNeighbour(Edge edge) {
        this.connections.add(edge);
    }

    public double getMinDistance() {
        return minDistance;
    }

    public void setMinDistance(double minDistance) {
        this.minDistance = minDistance;
    }

    public Vertex getPrevious() {
        return previous;
    }

    public void setPrevious(Vertex previous) {
        this.previous = previous;
    }

    public String toString() {
        return name;
    }

    // so sanh kh/cach giua 2 dinh
    public int compareTo(Vertex other) {
        return Double.compare(minDistance, other.minDistance);
    }
}
