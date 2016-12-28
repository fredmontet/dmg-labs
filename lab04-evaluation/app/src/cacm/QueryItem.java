package cacm;

public class QueryItem {

    public int id;
    public String query;

    public QueryItem(String line) {

        String[] splitted = line.split("\t");

        this.id = Integer.parseInt(splitted[0]);
        this.query = splitted[1];
    }
}
