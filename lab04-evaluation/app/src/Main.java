import cacm.QueryItems;
import cacm.Results;
import cacm.SearchEngine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;

public class Main {

    public static void main(String[] args) throws Exception {

        // Path etc.
        String index_path = "index";
        String file_path = "asset/cacm_v2.txt";
        String queries_path = "asset/query.txt";
        String relevant_results = "asset/qrels.txt";
        String common_words = "asset/common_words.txt";

        // Setup the tools
        SearchEngine cacm = new SearchEngine(file_path, index_path);
        Results results;

        System.out.println("Lab 04 - Evaluation");
        System.out.println("===================\n");

        // Load all the queries
        System.out.println("# Load all the queries\n");
        QueryItems queries = new QueryItems();
        System.out.println("\tLoading the QueryItems from "+queries_path);
        queries.load(queries_path);
        System.out.println("\t-> Queries loaded\n");

        System.out.println("\tNumber of queries: "+queries.items.size());

        int id = 0; // Testing
        System.out.println("\tTest to make a request on the query id: "+id);
        System.out.println("\t-> Query is: "+queries.items.get(id).query); // Testing
        System.out.println("\n");

        // Select the Analyzers to evaluate
        System.out.println("# Select the Analyzers\n");
        String[] analyzer_types = {"whitespace","standard","english","english_custom"};
        for (String analyzer: analyzer_types) {
            System.out.println("\t- "+analyzer);
        }
        System.out.println("\n");

        // For each Analyzer
        System.out.println("# Build the indexes and query them\n");

        for (String analyzer_type : analyzer_types) {

            System.out.println("\t"+analyzer_type);

            System.out.print("\t-> Indexing...");
            cacm.index(analyzer_type);
            System.out.print("\r\t-> Indexing\tDONE");

            System.out.print("\n\t-> Querying...");
            results = cacm.batchQuery("content", queries, analyzer_type);
            System.out.print("\r\t-> Querying\tDONE");

            System.out.print("\n\t-> Export...");
            results.export(analyzer_type);
            System.out.print("\r\t-> Export\tDONE");
            System.out.print("\n\t-> Output in results/qrels_"+analyzer_type+".txt");

            System.out.println("\n");
        }
    }
}
