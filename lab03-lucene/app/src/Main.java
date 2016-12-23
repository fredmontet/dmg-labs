import cacm.CustomSimilarity;
import cacm.SearchEngine;

public class Main {

    public static void main(String[] args) throws Exception {

        String index_path = "index";
        String file_path = "asset/cacm.txt";
        String analyzer_type = "english";

        SearchEngine cacm = new SearchEngine(file_path, index_path);

        long start_time = System.nanoTime();

        CustomSimilarity customSimilarity = new CustomSimilarity();

        /*/
        cacm.index(analyzer_type);
        /*/
        cacm.index(analyzer_type, customSimilarity);
        /**/

        long end_time = System.nanoTime();
        System.out.println("Time of indexing (ms) : " + (end_time - start_time) / 1e6);

        cacm.query("summary", "Information Retrieval", analyzer_type, 10);
        cacm.query("summary", "Information Retrieval", analyzer_type, 10, customSimilarity);

        /*
        cacm.query("summary", "Information Retrieval", analyzer_type, 10);
        cacm.query("summary", "Information AND Retrieval", analyzer_type, 10);
        cacm.query("summary", "+Retrieval Information -Database", analyzer_type, 10);
        cacm.query("summary", "Info*", analyzer_type, 10);
        cacm.query("summary", "'Information Retrieval'~5", analyzer_type, 10);

        ReadIndex readIndex = new ReadIndex();
        readIndex.authorWithMaxPublication();
        readIndex.titleTermeTop();
        */
    }
}
