import java.util.*;

public class NaiveDisjointSet<T> {
    HashMap<T, T> parentMap = new HashMap<>();
    HashMap<T, Integer> size = new HashMap<>();

    void add(T element) {
        parentMap.put(element, element);
    }

    // TODO: Implement path compression
    T find(T a) {
        T node = parentMap.get(a);
        if (node.equals(a)) {
            return node;
        } else {
            parentMap.put(a, find(node)); // find the ultimate parent of node a
            return parentMap.get(a);
        }
    }

    // TODO: Implement union by size or union by rank
    // Used https://takeuforward.org/data-structure/disjoint-set-union-by-rank-union-by-size-path-compression-g-46/ for union by size
    void union(T a, T b) {
        // parentMap.put(find(a), find(b));
        if (size.get(find(a)) < size.get(find(b))) {
            // merge tree with smaller number of nodes into tree with larger number of nodes
            parentMap.put(find(a), find(b));
            // update size (number of nodes) of root of the tree with more nodes
            size.put(find(b), size.get(find(b)) + size.get(find(a)));
        }
        else {
            parentMap.put(find(b), find(a));
            size.put(find(a), size.get(find(a)) + size.get(find(b)));
        }

    }
}