package cacm;

/**
 * Created by fredmontet on 27.12.16.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class QueryItems {

    public ArrayList<QueryItem> items;

    public QueryItems() {
        this.items = new ArrayList<QueryItem>();
    }

    public void load(String filepath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            System.out.println("Loading the QueryItems from "+filepath);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() > 1) {
                    this.items.add(new QueryItem(line));
                }
            }
        }
    }
}
