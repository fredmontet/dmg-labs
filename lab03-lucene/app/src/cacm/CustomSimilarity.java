package cacm;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.similarities.ClassicSimilarity;

public class CustomSimilarity extends ClassicSimilarity {

    @Override
    public float tf(float freq) {
        return (float) (1 + Math.log((double) freq));
    }

    @Override
    public float idf(long docFreq, long numDocs) {
        return (float) Math.log(numDocs / docFreq);
    }

    @Override
    public float coord(int overlap, int maxOverlap) {
        return (float) Math.sqrt(overlap / maxOverlap);
    }

    @Override
    public float lengthNorm(FieldInvertState state) {
        return 1f;
    }
}
