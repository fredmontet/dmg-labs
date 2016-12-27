import cacm.CustomSimilarity;
import cacm.SearchEngine;

public class Main {

    public static void main(String[] args) throws Exception {

        String index_path = "index";
        String file_path = "asset/cacm_v2.txt";
        String analyzer_type = "english";

        SearchEngine cacm = new SearchEngine(file_path, index_path);

        cacm.index(analyzer_type);

        // TODO YOLO!

    }
}
