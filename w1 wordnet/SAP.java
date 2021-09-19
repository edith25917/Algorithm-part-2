import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;

public class SAP {

    private Digraph G;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G){
        this.G = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w){
        if(!validateVertex(v) || !validateVertex(w)) throw new IllegalArgumentException();
        int[] result = shortest(v,w);

        return result[1];
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w){
        if(!validateVertex(v) || !validateVertex(w)) throw new IllegalArgumentException();
        int[] result = shortest(v,w);

        return result[0];
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w){
        if(v == null || w == null) throw new IllegalArgumentException();
        if(!validateVertex(v) || !validateVertex(w)) throw new IllegalArgumentException();
        int[] results = shortest(v,w);

        return results[1];
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w){
        if(v == null || w == null) throw new IllegalArgumentException();
        if(!validateVertex(v) || !validateVertex(w)) throw new IllegalArgumentException();
        int[] results = shortest(v,w);

        return results[0];
    }


    private int[] shortest(int v, int w){
        BreadthFirstDirectedPaths vBfs = new BreadthFirstDirectedPaths(this.G, v);
        BreadthFirstDirectedPaths wBfs = new BreadthFirstDirectedPaths(this.G, w);

        return getAncAndLength(vBfs,wBfs);
    }

    private int[] shortest(Iterable<Integer> v, Iterable<Integer> w){
        BreadthFirstDirectedPaths vBfs = new BreadthFirstDirectedPaths(this.G,v);
        BreadthFirstDirectedPaths wBfs = new BreadthFirstDirectedPaths(this.G,w);

        return getAncAndLength(vBfs,wBfs);
    }

    private int[] getAncAndLength(BreadthFirstDirectedPaths vBfs , BreadthFirstDirectedPaths wBfs ){
        int[] results = new int[2];

        int anc = -1;
        int minLength = Integer.MAX_VALUE;

        for(int i = 0; i< this.G.V();i++){
            if(vBfs.hasPathTo(i) && wBfs.hasPathTo(i)){
                int length = vBfs.distTo(i)+wBfs.distTo(i);
                if(minLength > length){
                    minLength = length;
                    anc = i;
                }
            }
        }
        results[0] = anc;
        results[1] = anc == -1 ? -1 : minLength;

        return results;
    }


    private boolean validateVertex(int v){
        return v >= 0 && v <= this.G.V();
    }

    private boolean validateVertex(Iterable<Integer> vertices){
        int count = 0;
        for (Integer v : vertices) {
            count++;
            if (v == null) {
                throw new IllegalArgumentException();
            }
            validateVertex(v);
        }
        if (count == 0) {
            throw new IllegalArgumentException();
        }
        return true;
    }

}
