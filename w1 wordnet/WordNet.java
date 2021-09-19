import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WordNet {


    private Map<String, Set<Integer>> nounKeyList; // if set<integer> change to arraylist, will run out of memory
    private Map<Integer,String> idKeyList;
    private DirectedCycle dc;
    private Digraph G;
    private SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms){
        if(synsets == null || hypernyms == null ) throw new IllegalArgumentException();

        readSynsets(synsets);
        readHypernyms(hypernyms);
        this.sap = new SAP(this.G);
        this.dc = new DirectedCycle(this.G);
        if(!isDAG()) throw new IllegalArgumentException();
    }

    private boolean isDAG(){
        // check has exactly one root
        int rootCount = 0;
        for(int i=0;i<this.G.V();i++){
            if(this.G.outdegree(i) == 0) {
                rootCount++;
            }
            if(rootCount > 1) return false;
        }

        if(rootCount == 0) return false; // if no root

        return !dc.hasCycle();
    }

    private void readSynsets(String synsets){
        In inSyn = new In(synsets);
        this.nounKeyList = new HashMap<>();
        this.idKeyList = new HashMap<>();

        while (!inSyn.isEmpty()){
            String line = inSyn.readLine();
            String[] str = line.split(","); // 0 synset id, 1 synset, 2 def
            String[] nouns = str[1].split(" ");
            int id = Integer.parseInt(str[0]);
            this.idKeyList.put(id, str[1]);

            for(int i=0;i<nouns.length;i++){
                if(!this.nounKeyList.containsKey(nouns[i])){
                    this.nounKeyList.put(nouns[i], new HashSet<Integer>());
                }
                this.nounKeyList.get(nouns[i]).add(id);
            }
        }
    }

    private void readHypernyms(String hypernyms){
        In inHyp = new In(hypernyms);
        this.G = new Digraph(this.idKeyList.size());

        while (!inHyp.isEmpty()) {
            String line = inHyp.readLine();
            String[] str = line.split(",");
            for(int i=1;i<str.length;i++){
                this.G.addEdge(Integer.parseInt(str[0]),Integer.parseInt(str[i])); // 0 is root of this line
            }
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns(){
        return this.nounKeyList.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word){
        if(word == null) throw new IllegalArgumentException();
        return this.nounKeyList.get(word) != null;
    }

    // distance between nounA and nounB
    public int distance(String nounA, String nounB){
        if(!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException();
        Iterable<Integer> v = this.nounKeyList.get(nounA);
        Iterable<Integer> w = this.nounKeyList.get(nounB);
        return this.sap.length(v,w);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB in a shortest ancestral path
    public String sap(String nounA, String nounB){
        if(!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException();
        Iterable<Integer> v = this.nounKeyList.get(nounA);
        Iterable<Integer> w = this.nounKeyList.get(nounB);
        int ancestor = this.sap.ancestor(v,w);
        return this.idKeyList.get(ancestor);
    }
}
