package pathfinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Pathfinding {

    public static ArrayList<Node> aStar(Node[][] nodes, Node start, Node target) {

        HashMap<Node, Node> parents = new HashMap<>();
        HashMap<Node, Double> gCost = new HashMap<>();

        // Add the start cost to the map
        gCost.put(start, 0.0);

        //A PriorityQueue that sorts the smallest d2Heuristic node to the front
        PriorityQueue<Node> open = new PriorityQueue<>(1,
                (a, b) -> (int) Math.signum((gCost.get(a) + d2Heuristic(a, target)) - (gCost.get(b) + d2Heuristic(b, target))));
        open.add(start);
        ArrayList<Node> closed = new ArrayList<>();

        Node current = null;
        while (!open.isEmpty() && !target.equals(open.peek())) {
            current = open.poll();
            closed.add(current);

            ArrayList<Node> neighbors = new ArrayList<>();
            if (current != null) {
                neighbors = neighbors(nodes, current);
            }

            for (Node neighbor : neighbors) {
                double cost = gCost.get(current) + d2Heuristic(current, neighbor); //g of neighbor + movement cost
                if (open.contains(neighbor) && gCost.containsKey(neighbor) && cost < gCost.get(neighbor)) {
                    open.remove(neighbor);
                } else if (closed.contains(neighbor) && gCost.containsKey(neighbor) && cost < gCost.get(neighbor)) {
                    closed.remove(neighbor);
                } else if (!(open.contains(neighbor) || closed.contains(neighbor))) {
                    gCost.put(neighbor, cost);
                    open.add(neighbor);
                    parents.put(neighbor, current);
                }
            }
        }
        ArrayList<Node> path = new ArrayList<>();
        path.add(current);
        while (parents.containsKey(current)) {
            path.add(0, parents.get(current));
            current = parents.get(current);
        }
        return path;
    }

    // Distance squared
    public static double d2Heuristic(Node a, Node b) {
        return Math.pow(b.getI() - a.getI(), 2) + Math.pow(b.getJ() - a.getJ(), 2);
    }

    public static ArrayList<Node> neighbors(Node[][] nodes, Node current) {
        //Add all of the neighbors to a list to iterate over
        ArrayList<Node> neighbors = new ArrayList<>();
        if (current.getI() > 0 && nodes[current.getI() - 1][current.getJ()].isTraversable()) {
            neighbors.add(nodes[current.getI() - 1][current.getJ()]); //left
            if (current.getJ() > 0 && nodes[current.getI() - 1][current.getJ() - 1].isTraversable()) {
                neighbors.add(nodes[current.getI() - 1][current.getJ() - 1]); //top left corner
            }
            if (current.getJ() < nodes.length - 1 && nodes[current.getI() - 1][current.getJ() + 1].isTraversable()) {
                neighbors.add(nodes[current.getI() - 1][current.getJ() + 1]); //bottom left corner
            }
        }
        if (current.getJ() > 0 && nodes[current.getI()][current.getJ() - 1].isTraversable()) {
            neighbors.add(nodes[current.getI()][current.getJ() - 1]); //top
        }
        if (current.getI() < nodes.length - 1 && nodes[current.getI() + 1][current.getJ()].isTraversable()) {
            neighbors.add(nodes[current.getI() + 1][current.getJ()]); //right
            if (current.getJ() > 0 && nodes[current.getI() + 1][current.getJ() - 1].isTraversable()) {
                neighbors.add(nodes[current.getI() + 1][current.getJ() - 1]); //top right corner
            }
            if (current.getJ() < nodes.length - 1 && nodes[current.getI() + 1][current.getJ() + 1].isTraversable()) {
                neighbors.add(nodes[current.getI() + 1][current.getJ() + 1]); //bottom right corner
            }
        }
        if (current.getJ() < nodes[0].length - 1 && nodes[current.getI()][current.getJ() + 1].isTraversable()) {
            neighbors.add(nodes[current.getI()][current.getJ() + 1]); //bottom
        }
        return neighbors;
    }
}
