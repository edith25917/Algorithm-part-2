public class Outcast {

    private WordNet wn;
    // constructor takes a WordNet object
    public Outcast(WordNet wordnet){
        this.wn = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns)   {
        int maxDis = 0;
        String outcast = "";

        for(int i=0;i<nouns.length;i++){
            int dis = 0;
            for(int j=0;j<nouns.length;j++){
                dis += this.wn.distance(nouns[i],nouns[j]);
            }
            if(maxDis < dis){
                maxDis = dis;
                outcast = nouns[i];
            }
        }

        return outcast;
    }

}
