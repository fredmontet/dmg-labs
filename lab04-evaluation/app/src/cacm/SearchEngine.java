package cacm;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchEngine {

    private String index_path;
    private String file_path;
    private Analyzer index_analyzer;
    private Analyzer query_analyzer;

    public SearchEngine(String file_path, String index_path) throws IOException {
        setFilePath(file_path);
        setIndexPath(index_path);
    }

    public void index(String analyzer_type) throws IOException {
        index(analyzer_type, null);
    }

    public void index(String analyzer_type, ClassicSimilarity similarity) throws IOException {

        this.index_analyzer = getAnalyzerByName(analyzer_type);

        // Configure indexer
        IndexWriterConfig iwc = new IndexWriterConfig(index_analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        iwc.setUseCompoundFile(false);
        if (similarity != null)
            iwc.setSimilarity(similarity);

        // Create index writer
        Path path = FileSystems.getDefault().getPath("index");
        Directory dir = FSDirectory.open(path);
        IndexWriter indexWriter = new IndexWriter(dir, iwc);

        // Index files
        try (BufferedReader br = new BufferedReader(new FileReader(this.file_path))) {
            String line;
            while ((line = br.readLine()) != null) {

                // Create document
                Document doc = new Document();
                CacmItem item = new CacmItem(line);

                // Create field
                FieldType fieldType = new FieldType();
                fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
                fieldType.setTokenized(true);
                fieldType.setStored(true);
                fieldType.setStoreTermVectors(true);
                fieldType.setStoreTermVectorOffsets(true);
                fieldType.setStoreTermVectorPayloads(true);
                fieldType.setStoreTermVectorPositions(true);
                fieldType.freeze();

                // Add fields to document
                if (item.id != null)
                    doc.add(new Field("id", item.id, fieldType));

                if (item.content != null)
                    doc.add(new Field("content", item.content, fieldType));

                // Add document to index
                indexWriter.addDocument(doc);
            }
        }

        // Terminate indexing
        indexWriter.close();
        dir.close();
    }

    public void query(String fieldStr, String queryStr, String analyzer_type) throws ParseException, IOException {
        query(fieldStr, queryStr, analyzer_type, null);
    }

    public ArrayList<Integer> query(String fieldStr, String queryStr, String analyzer_type, ClassicSimilarity similarity) throws ParseException, IOException {

        //System.out.println("Start search");
        this.query_analyzer = getAnalyzerByName(analyzer_type);

        // Setup tools
        ArrayList<Integer> results = new ArrayList<>();
        Path path = FileSystems.getDefault().getPath(this.index_path);
        Directory dir = FSDirectory.open(path);
        IndexReader indexReader = DirectoryReader.open(dir);
        QueryParser parser = new QueryParser(fieldStr, this.query_analyzer);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        if (similarity != null) {
            indexSearcher.setSimilarity(similarity);
        }

        // Search the index with a query
        Query query = parser.parse(QueryParser.escape(queryStr));
        ScoreDoc[] hits = indexSearcher.search(query, Integer.MAX_VALUE).scoreDocs;

        // Retrieve results
        //System.out.println("Results found: " + hits.length);
        //System.out.println("Query is: " + query.toString());
        int i = 0;
        for (ScoreDoc hit : hits) {
            Document doc = indexSearcher.doc(hit.doc);
            results.add(Integer.parseInt(doc.get("id")));
            //System.out.println(doc.get("id") + ": " + doc.get("content") + " (" + hit.score + ")");
            i++;
        }

        // Close index reader
        indexReader.close();
        dir.close();

        //System.out.println("Search is done !");
        return results;
    }

    public Results batchQuery(String fieldStr, QueryItems queries, String analyzer_type) throws ParseException, IOException {

        // Setup the tools
        ArrayList<Integer> queryResults = new ArrayList<>();
        Results results = new Results();

        // Loop and search the index with each query
        for (QueryItem item: queries.items) {
            queryResults = query(fieldStr, item.query, analyzer_type, null);
            results.results.put(item.id, queryResults);
        }

        return results;
    }

    private Analyzer getAnalyzerByName(String analyzer_type) throws java.io.IOException {

        //System.out.println("Analyzer type : " + analyzer_type);
        Analyzer analyzer;

        switch (analyzer_type) {
            case "standard":
                return analyzer = new StandardAnalyzer();
            case "whitespace":
                return analyzer = new WhitespaceAnalyzer();
            case "english":
                return analyzer = new EnglishAnalyzer();
            case "english_custom":
                FileReader stopwords = new FileReader("asset/common_words_v2.txt");
                return analyzer = new EnglishAnalyzer(new CharArraySet(stopwords.read(), true));
            case "shingle_2":
                return analyzer = new ShingleAnalyzerWrapper(2, 2);
            case "shingle_3":
                return analyzer = new ShingleAnalyzerWrapper(3, 3);
            case "stop":
                try {
                    return analyzer = new StopAnalyzer(new FileReader("asset/common_words.txt"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            default:
                System.out.println(analyzer_type + " analyzer is not existing, StandardAnalyzer is used instead");
                return analyzer = new StandardAnalyzer();
        }
    }

    public String getFilePath() {
        return this.file_path;
    }

    public void setFilePath(String file_path) {
        this.file_path = file_path;
    }

    public String getIndexPath() {
        return index_path;
    }

    public void setIndexPath(String index_path) {
        this.index_path = index_path;
    }
}
