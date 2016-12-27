package cacm;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.FileSystems;

public class ReadIndex {
    public void authorWithMaxPublication() throws Exception {
        Directory d = FSDirectory.open(FileSystems.getDefault().getPath("index"));
        IndexReader ir = DirectoryReader.open(d);

        HighFreqTerms.DocFreqComparator cmp = new HighFreqTerms.DocFreqComparator();
        TermStats[] fields = HighFreqTerms.getHighFreqTerms(ir, 12, "author", cmp);
        for (TermStats ts: fields)
            System.out.println(ts.termtext.utf8ToString() + " => " + ts.docFreq);
    }

    public void titleTermeTop() throws Exception {
        Directory d = FSDirectory.open(FileSystems.getDefault().getPath("index"));
        IndexReader ir = DirectoryReader.open(d);

        HighFreqTerms.DocFreqComparator cmp = new HighFreqTerms.DocFreqComparator();
        TermStats[] fields = HighFreqTerms.getHighFreqTerms(ir, 10, "title", cmp);
        for (TermStats ts: fields)
            System.out.println(ts.termtext.utf8ToString() + " => " + ts.docFreq);
    }


}
