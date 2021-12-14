package Algorithm;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.io.InputStreamReader;
import java.util.ArrayList;



public class Map {
    public static Vertex[] map = null;

    public static ArrayList<String> listNodes;
    public ArrayList<String> listVertAdded;

    public Map(String filename) {
        map = createMap(filename);
    }

    public boolean contains(String name) {
        for (Vertex v : map) {
            if (v != null && v.toString().compareTo(name) == 0)
                return true;
        }
        return false;
    }

    public Vertex getVertexWithName(String name) {
        for (Vertex v : map) {
            if (v != null && v.toString().compareTo(name) == 0)
                return v;
        }
        return null;
    }

    public static Vertex[] addVert(int n, Vertex arr[], Vertex x) {
        int i;
        Vertex newMap[] = new Vertex[n + 1];
        for (i = 0; i < n; i++) {
            newMap[i] = arr[i];
            newMap[n] = x;
        }
        return newMap;

    }

    private Vertex[] createMap(String mapFile) {
        try {
            listVertAdded = new ArrayList<>();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(mapFile)));
            int vertices = Integer.valueOf(br.readLine());
            if (vertices <= 0) {
                System.out.println("Vertex amount from map file is less than or equal to 0. Must have two or more "
                        + "points to construct a map.");
                return null;
            }
            String line;
            int i = 0; // counter used to add vertices to map array
            map = new Vertex[vertices]; // initialize new empty map with length using vertex count

            while (!(line = br.readLine()).equals("-1")) {
                String[] tokens = line.split("\\s+");
//				System.out.println("XÉT " + line);
                // add a vertex for each city only once each
                if (!contains(tokens[0])) {
                    // if vertex doesn't exist in map, add it
                    map[i] = new Vertex(tokens[0]);
                    listVertAdded.add(tokens[0]);
                    // add an edge to new vertex
                    Vertex edge = getVertexWithName(tokens[1]);

                    // if edge vertex doesn't exist in map yet, create the edge vertex and connect
                    // it to vertex
                    if (edge == null) {
                        // add edges to each vertex using second and third value from file
                        Vertex newEdge = new Vertex(tokens[1]);
                        if (newEdge!=null) {
                            map = addVert(map.length, map, newEdge);
                        }

                        // add each vertex to map
                        map[i].addNeighbour(new Edge(newEdge, Double.valueOf(tokens[2])));
                        listVertAdded.add(tokens[1]);


                    } else {
                        map[i].addNeighbour(new Edge(edge, Double.valueOf(tokens[2])));

                    }
                    // increment i only after vertex is done being added to map
                    i++;
                } else {
                    Vertex vertex = getVertexWithName(tokens[0]);
                    // try to get vertex matching edge to be added
                    Vertex edge = getVertexWithName(tokens[1]);
                    // if edge vertex doesn't exist in map yet, create the edge vertex and connect
                    // it to vertex
                    if (edge == null) {
                        Vertex newEdge = new Vertex(tokens[1]);
                        if (newEdge != null) {
                            map = addVert(map.length, map, newEdge);
                        }

                        vertex.addNeighbour(new Edge(newEdge, Double.valueOf(tokens[2])));

                        if (newEdge != null) {
                            listVertAdded.add(tokens[1]);
                        }

                    } else {
                        vertex.addNeighbour(new Edge(edge, Double.valueOf(tokens[2])));

                    }
                }
                if (i > vertices) {
                    System.out.println("Additional vertices were found in the map file that were counted in the total"
                            + "amount of vertices. Please check the value from line in the map file "
                            + "and try again or the path finding may not be accurate.");
                    return null;
                }
            }

        } catch (Exception e) {
            if (e instanceof FileNotFoundException) {
                System.out.println("Map file with name " + mapFile + " could not be found. Please make sure file "
                        + "is in the same directory as program or check file name again.");
                return null;
            } else if (e instanceof NumberFormatException) {
                System.out.println("The first value in the map file must be an integer specifying the amount of"
                        + " vertices in the map. Please check map file line 1 and try again.");
                return null;
            }
        }
        return map;
    }
}

