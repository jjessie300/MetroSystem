import java.util.*;
import java.lang.Math.*;


public class McMetro {
    protected Track[] tracks;
    protected HashMap<BuildingID, Building> buildingTable = new HashMap<>();

    // You may initialize anything you need in the constructor
    McMetro(Track[] tracks, Building[] buildings) {
       this.tracks = tracks;

       // Populate buildings table
       for (Building building : buildings) {
           buildingTable.putIfAbsent(building.id(), building);
       }
    }

    // Helper class to build graph for bestMetroSystem
    private static class Graph{
        Track track;
        int weight;

        Graph(Track track, int weight) {
            this.track = track;
            this.weight = weight;
        }
    }

    /*
    // Build graph for maxPassengers
    private static class Graph2 {
        BuildingID endBuilding;
        int weight;

        Graph2(BuildingID end, int weight) {
            this.endBuilding = end;
            this.weight = weight;
        }
    }
    */

    // Maximum number of passengers that can be transported from start to end
    int maxPassengers(BuildingID start, BuildingID end) {
        // TODO: your implementation here
        // Edmonds Karp algorithm with BFS --> want to find the max flow
        Map<BuildingID, Map<BuildingID, Integer>> graph = new HashMap<BuildingID, Map<BuildingID, Integer>>();
        HashMap<BuildingID, BuildingID> parent = new HashMap<>();
        HashMap<BuildingID, BuildingID> path = new HashMap<>();
        int bottleneck = Integer.MAX_VALUE;
        int maxFlow = 0;

        // Build graph --> want to associate each building with its descendant and max capacity the track between can hold
        for (Track track : tracks) {
            int occupantA = buildingTable.get(track.startBuildingId()).occupants();
            int occupantB = buildingTable.get(track.startBuildingId()).occupants();
            int trackCapacity = track.capacity();
            int edgeCapacity = Math.min(Math.min(occupantA, occupantB), trackCapacity);
            graph.putIfAbsent(track.startBuildingId(), new HashMap<>());
            //System.out.println("key in big hash");
            //System.out.println(track.startBuildingId());
            graph.get(track.startBuildingId()).put(track.endBuildingId(), edgeCapacity);
            //System.out.println(graph.get(track.startBuildingId()));
            //System.out.println(graph.get(track.startBuildingId()).get(track.endBuildingId()));
            //System.out.println(graph);
        }
        // Start building and end building are the same, no transportation is required
        if (start.equals(end)) {
            return 0;
        }
        //System.out.println("check what is stored in key 1");
        //System.out.println(graph.get(start));

        // Edmonds Karp algorithm with BFS
        while(bfs(start, end, graph, parent) == true) { // find all possible paths from start to end (augmenting paths)
            // Backtrack through the parent (from end to start) to find the bottleneck and keep track of the path
            BuildingID target = end;
            BuildingID ancestor = parent.get(target);
            //System.out.println("ancestor");
            //System.out.println(ancestor);
            while(!ancestor.equals(start)){
                path.put(ancestor, target);
                //System.out.println("capacity");
                //System.out.println(graph.get(ancestor).get(target));
                if (graph.get(ancestor).get(target) < bottleneck) {
                    bottleneck = graph.get(ancestor).get(target); // update the bottleneck
                    //System.out.println(bottleneck);
                }
                target = ancestor;
                ancestor = parent.get(ancestor); // update ancestor (the grandparent)
            }
            /*
            System.out.println("ancestor is start node");
            System.out.println(ancestor);
            System.out.println(target);
            */
            path.put(ancestor, target);
            if (graph.get(ancestor).get(target) < bottleneck) {
                bottleneck = graph.get(ancestor).get(target);
                //System.out.println("final bottleneck");
                //System.out.println(bottleneck);
            }
            //System.out.println(path);
            // Follow the path and adjust the weight of the edges in the graph based on the bottleneck
            for(BuildingID augAncestor: path.keySet()){
                BuildingID augDescendant = path.get(augAncestor);
                int currentWeight = graph.get(augAncestor).get(augDescendant);
                //System.out.println("current weight");
                //System.out.println(currentWeight);
                graph.get(augAncestor).put(augDescendant, currentWeight - bottleneck); // subtract bottleneck from the current weight of the edge
            }
            path.clear();
            maxFlow += bottleneck; // add bottleneck to the max flow
        }
        return maxFlow;
        // return 0;
    }

    // Used https://www.programiz.com/dsa/graph-bfs as reference for BFS implementation
    private boolean bfs(BuildingID start, BuildingID end, Map<BuildingID, Map<BuildingID, Integer>> graph, HashMap<BuildingID, BuildingID> parent) {
        Queue<BuildingID> queue = new LinkedList<>();
        Set<BuildingID> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);
        parent.clear();

        while(queue.size() != 0) {
            BuildingID node = queue.poll(); // dequeue
            //System.out.println("break -1 ");
            //System.out.println(node);
            // if node is not part of the graph (does not have outgoing edges to another node or is just not part of it) then there is no path from start to end
            if (!graph.containsKey(node)) {
                return false;
            }
            Map<BuildingID, Integer> edges = graph.get(node);
            //System.out.println("print descendants of node 1");
            //System.out.println(edges);
            // Go through all the neighbors of that node
            for (Map.Entry<BuildingID, Integer> edge : edges.entrySet()) {
                BuildingID neighbor = edge.getKey();
                int weight = edge.getValue();
                if (!visited.contains(neighbor) && weight > 0) { // check if neighbor has been visited and if its weight is bigger than 0
                    //System.out.println("Break 0 ");
                    //System.out.println(neighbor);
                    queue.add(neighbor);
                    visited.add(neighbor);
                    parent.put(neighbor, node);
                    //System.out.println("parent");
                    //System.out.println(parent.get(neighbor));
                    if (neighbor.equals(end)) { // there is a path from start to end
                        //System.out.println("returned true");
                        return true;
                    }
                    //System.out.println("child");
                    //System.out.println(neighbor);
                    //System.out.println("parent");
                    //System.out.println(node);
                }
            }
        }
        return false;
    }


    // Returns a list of trackIDs that connect to every building maximizing total network capacity taking cost into account
    TrackID[] bestMetroSystem() {
        // TODO: your implementation here
        // Minimum spanning tree (instead want to maximize here) with Kruskal's algorithm
        // Make graph, want to associate goodness with its corresponding track
        ArrayList<Graph> graph = new ArrayList<>();
        for (Track track : this.tracks) {
            int occupantA = buildingTable.get(track.startBuildingId()).occupants(); // num of occupants in start building
            int occupantB = buildingTable.get(track.endBuildingId()).occupants(); // num of occupants in end building
            int trackCapacity = track.capacity();
            int maxPassengers = Math.min(Math.min(occupantA, occupantB), trackCapacity); // find max passengers track can hold
            int trackWorth = maxPassengers / track.cost(); // calculate "goodness" of the track
            graph.add(new Graph(track, trackWorth));
        }
        // Sort in decreasing order --> maximize num of people on the track while also considering its cost
        graph.sort((a, b) -> Integer.compare(b.weight, a.weight));
        /*
        for (int i=0; i<graph.size(); i++) {
            System.out.println(graph.get(i).weight);
            System.out.println(graph.get(i).track);
        }
        */
        NaiveDisjointSet<BuildingID> sets = new NaiveDisjointSet<>();
        for (BuildingID ID: buildingTable.keySet()) {
            sets.add(ID); // add each building into disjoint set as individual sets
            sets.size.put(ID, 1); // set size of each set to 1
        }
        ArrayList<TrackID> MST = new ArrayList<>();
        // Kruskal's algorithm
        for (int i = 0; i < graph.size(); i++) {
            Graph edge = graph.get(i);
            Track track = edge.track;
            BuildingID buildingA = sets.find(track.startBuildingId());
            // System.out.println(buildingA);
            BuildingID buildingB = sets.find(track.endBuildingId());
            // System.out.println(buildingB);
            // Check if buildings are part of the same set
            if (!buildingA.equals(buildingB)) {
                sets.union(buildingA, buildingB); // merge sets if they are not part of the same set
                MST.add(track.id());
            }
        }
        TrackID[] arrayMST = new TrackID[MST.size()];
        // Transfer track IDs from array list into an array
        for (int i = 0; i < arrayMST.length; i++) {
            arrayMST[i] = MST.get(i);
            // System.out.println(arrayMST[i]);
        }
        return arrayMST;
        // return new TrackID[0];
    }

    // Adds a passenger to the system
    void addPassenger(String name) {
        // TODO: your implementation here
    }

    // Do not change this
    void addPassengers(String[] names) {
        for (String s : names) {
            addPassenger(s);
        }
    }

    // Returns all passengers in the system whose names start with firstLetters
    ArrayList<String> searchForPassengers(String firstLetters) {
        // TODO: your implementation here
        return new ArrayList<>();
    }

    // Return how many ticket checkers will be hired
    static int hireTicketCheckers(int[][] schedule) {
        // TODO: your implementation here
        // Greedy algorithm (same as interval scheduling problem) --> greedy choice: pick schedule with earliest end time
        int numTicketCheckers = 0;
        int currentEnd = -100000;
        // Sort based on earliest end time
        Arrays.sort(schedule, (a, b) -> Integer.compare(a[1], b[1]));

        for (int[] sched: schedule) {
            int startTime = sched[0];
            int endTime = sched[1];
            // System.out.println(startTime);
            // System.out.println(endTime);
            // Determine if we pick that ticket checker
            if(startTime >= currentEnd) { // check if start time of the schedule in question overlaps with the current end time
                currentEnd = endTime; // updating current end time since a schedule has been added
                numTicketCheckers += 1;
            }
        }
        return numTicketCheckers;
    }
}
