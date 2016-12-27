package cacm;

/**
 * Created by fredmontet on 27.12.16.
 */
public class QueryItem {
    public String id;
    public String query;

    public QueryItem(String line) {

        String[] splitted = line.split("\t");

        this.id = splitted[0];
        this.query = splitted[1];
    }
}
