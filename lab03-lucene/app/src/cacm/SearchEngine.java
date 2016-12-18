package cacm;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class SearchEngine {

    private String index_path;
    private String file_path;
    private Analyzer index_analyzer;
    private Analyzer query_analyzer;

    public SearchEngine(String file_path, String index_path) throws IOException{
        setFilePath(file_path);
        setIndexPath(index_path);
    }

    public void index(String analyzer_type) throws IOException {
        System.out.println("Start indexing CACM collection");
        this.index_analyzer = getAnalyzerByName(analyzer_type);

        // Configure indexer
        IndexWriterConfig iwc = new IndexWriterConfig(index_analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        iwc.setUseCompoundFile(false);

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
                if(item.id != null)
                    doc.add(new Field("id", item.id, fieldType));

                if(item.authors != null)
                    for (String authorName : item.authors)
                        doc.add(new StringField("author", authorName, Field.Store.YES));

                if(item.title != null)
                    doc.add(new Field("title", item.title, fieldType));

                if(item.summary != null)
                    doc.add(new Field("summary", item.summary, fieldType));

                // Add document to index
                indexWriter.addDocument(doc);
            }
        }

        // Terminate indexing
        indexWriter.close();
        dir.close();
        System.out.println("Indexing done");
    }

    public void query(String fieldStr, String queryStr, String analyzer_type) throws ParseException, IOException{
        System.out.println("Start search");
        this.query_analyzer = getAnalyzerByName(analyzer_type);

        // Create query parser
        QueryParser parser = new QueryParser(fieldStr, this.query_analyzer);

        // Parse query
        Query query = parser.parse(queryStr);

        // Create index reader
        Path path = FileSystems.getDefault().getPath(this.index_path);
        Directory dir = FSDirectory.open(path);
        IndexReader indexReader = DirectoryReader.open(dir);

        // Create index searcher
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //indexSearcher.setSimilarity(new CustomSimilarity());

        // Search query
        ScoreDoc[] hits = indexSearcher.search(query, 1000).scoreDocs;

        // Retrieve results
        System.out.println("Results found: " + hits.length);
        System.out.println("Query is: " + query.toString());
        for (ScoreDoc hit: hits) {
            Document doc = indexSearcher.doc(hit.doc);
            System.out.println(doc.get("id") + ": " + doc.get("title") + " (" + hit.score + ")");
        }

        // Close index reader
        indexReader.close();
        dir.close();

        System.out.println("Search is done !");
    }

    private Analyzer getAnalyzerByName(String analyzer_type) {

        System.out.println("Analyzer type : "+analyzer_type);
        Analyzer analyzer;

        switch(analyzer_type) {
            case "standard":
                return analyzer = new StandardAnalyzer();
            /*
            case "whitespace":
                return analyzer = new WhitespaceAnalyzer();
            case "english":
                return analyzer = new EnglishAnalyzer();
            case "shingle_2":
                return analyzer = new ShingleAnalyzerWrapper(2, 2);
            case "shingle_3":
                return analyzer = new ShingleAnalyzerWrapper(3, 3);
            case "stop":
                return analyzer = new StopAnalyzer(new FileReader("data/common_words.txt"));
            */
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
