import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;



import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by chimera on 9/11/17.
 */
public class JsonStreamParser {

    private FileInputStream fis = null;
    private JsonReader reader = null;
    private Gson gson = null;
    private ExecutorService executor=null;
//    private ArrayList<Document> docList=new ArrayList<Document>();


    public JsonStreamParser(String ipFile) throws  IOException {
            fis = new FileInputStream(new File(ipFile));
            reader = new JsonReader(new InputStreamReader(fis, "UTF-8"));
    }

    public void start() throws IOException
    {
        gson= new GsonBuilder().create();
        executor = Executors.newFixedThreadPool(100);

        // Read file in stream mode
        reader.beginObject();
        reader.nextName();
        reader.beginArray();

        while (reader.hasNext())
        {
            // Read data into object model
//            Document doc = gson.fromJson(reader, Document.class);
            Runnable indexJob = new IndexBuilderThread(gson.fromJson(reader, Document.class));
            executor.execute(indexJob);
        }
        executor.shutdown();
        reader.close();
        }
    }

