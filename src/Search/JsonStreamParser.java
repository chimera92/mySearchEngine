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

    private JsonReader reader = null;


    public JsonStreamParser(String ipFile) throws  IOException {
            reader = new JsonReader(new InputStreamReader(new FileInputStream(new File(ipFile)), "UTF-8"));
    }

    public void start(GlobalPosIndex globalPosIndex) throws IOException
    {
        Gson gson = new GsonBuilder().create();
        // Read file in stream mode
        reader.beginObject();
        reader.nextName();
        reader.beginArray();

        Document doc;
        Runnable indexJob;
        Runnable writeJob;
        while (reader.hasNext())
        {
            // Read data into object
            doc = gson.fromJson(reader, Document.class);
            indexJob = new IndexBuilderThread(globalPosIndex,doc);
            writeJob = new DocumentWriterThread(doc);
            Main.submitJob(indexJob);
            Main.submitJob(writeJob);
        }
        reader.close();
        }
    }

