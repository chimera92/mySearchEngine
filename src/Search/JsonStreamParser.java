package Search;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by chimera on 9/11/17.
 */
@SuppressWarnings("DefaultFileTemplate")
class JsonStreamParser {

    private final GlobalPosIndex globalPosIndex;
    private final GlobalBiWordIndex globalBiWordIndex;
    private JsonReader reader = null;
    private final String ipFile;
    private static int docCount=0;





    public JsonStreamParser(GlobalPosIndex globalPosIndex, GlobalBiWordIndex globalBiWordIndex,String ipFile) throws  IOException {
        this.globalPosIndex = globalPosIndex;
        this.globalBiWordIndex = globalBiWordIndex;
        reader = new JsonReader(new InputStreamReader(new FileInputStream(new File(ipFile)), "UTF-8"));
        this.ipFile = ipFile;
    }

    private int generateDocId()
    {
        return docCount++;
    }
    private void submitJobs(Document doc)
    {
        Runnable indexJob = new IndexBuilderThread(globalPosIndex,globalBiWordIndex,doc);
        Runnable writeJob = new DocumentWriterThread(doc);
        Main.submitJob(indexJob);
        Main.submitJob(writeJob);
    }

    public void start() throws IOException
    {
        Gson gson = new GsonBuilder().create();
        // Read file in stream mode
        reader.beginObject();
        reader.nextName();
        reader.beginArray();


        while (reader.hasNext())
        {
            // Read data into object
            Document doc;
            doc = gson.fromJson(reader, Document.class);
            doc.setId(generateDocId());
            submitJobs(doc);
        }
        reader.close();
        }
    }

