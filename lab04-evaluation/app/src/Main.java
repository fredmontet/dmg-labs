import cacm.CustomSimilarity;
import cacm.QueryItem;
import cacm.QueryItems;
import cacm.SearchEngine;
import org.apache.lucene.queryparser.classic.QueryParser;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws Exception {

        // Path etc.
        String index_path = "index";
        String file_path = "asset/cacm_v2.txt";
        String queries_path = "asset/query.txt";
        String relevant_results = "asset/qrels.txt";
        String common_words = "asset/common_words.txt";

        SearchEngine cacm = new SearchEngine(file_path, index_path);

        // Load all the queries
        QueryItems queries = new QueryItems();
        queries.load(queries_path);
        int id = 0; // Just for testing
        System.out.println("Query id "+id+" is: "+queries.items.get(id).query);
        
        // Select the Analyzers to evaluate
        //String[] analyzer_types = {"whitespace","standard","english","english_custom"};
        String[] analyzer_types = {"standard"}; // For testing
        
        // For each Analyzer
        for (String analyzer_type : analyzer_types) {
            // Build an index
            cacm.index(analyzer_type);
            // Execute all the queries
            String queryString = "What articles exist which deal with TSS (Time Sharing System), an operating system for IBM computers?";
            cacm.query("content", QueryParser.escape(queryString), analyzer_type, 10);
            /*
            for (QueryItem item: queries.items) {
                cacm.query("content", QueryParser.escape(item.query), analyzer_type, 10);
            }
            */
        }


        // TODO YOLO!

    }
}
