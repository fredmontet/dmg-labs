import cacm.SearchEngine;

public class Main {

    public static void main(String[] args) throws Exception{

        String index_path = "index";
        String file_path = "asset/cacm.txt";
        String analyzer_type = "standard";

        SearchEngine cacm = new SearchEngine(file_path, index_path);
        //cacm.index(analyzer_type);

        cacm.query("summary", "Information Retrieval", analyzer_type);

    }

}
