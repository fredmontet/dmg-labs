package cacm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Created by fredmontet on 28.12.16.
 */
public class Results {

    public HashMap<Integer, ArrayList<Integer>> results;
    public HashMap<Integer, Integer> nbResults;

    public Results (){
        this.results = new HashMap<>();
    }

    public HashMap<Integer, Integer> computeNbResults() {
        this.nbResults = new HashMap<>();
        for (Map.Entry entry : this.results.entrySet()) {
            int queryId = Integer.parseInt(entry.getKey().toString());
            ArrayList queryResult = (ArrayList) entry.getValue();
            this.nbResults.put(queryId, queryResult.size());
        }
        return nbResults;
    }

    public void print(HashMap<Integer,Integer> hm){
        for (Map.Entry entry : hm.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }

    public void print(){
        for (Map.Entry entry : this.results.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }

    public void export(String filename) throws UnsupportedEncodingException {

        String folder = "results";
        File file = new File(folder);

        // Create the directory if it doesn't exist.
        if (!file.exists()) {
            System.out.println("Creating directory : " + folder);
            boolean res = false;
            try{
                file.mkdir();
                res = true;
            } catch(SecurityException se) {
                se.printStackTrace();
            }
            if(res) {
                System.out.println("Directory "+ folder+" created");
            }
        }

        // Write the result file
        try {
            PrintWriter writer = new PrintWriter(folder+"/qrels_"+filename+".txt", "UTF-8");

            for(Map.Entry entry : this.results.entrySet()) {
                int queryId = Integer.parseInt(entry.getKey().toString());
                ArrayList queryResult = (ArrayList) entry.getValue();

                for (Object result : queryResult) {
                    writer.println(queryId+" "+result);
                }
            }

            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
