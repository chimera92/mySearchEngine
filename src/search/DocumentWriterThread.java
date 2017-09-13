package search;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by chimera on 9/11/17.
 */
public class DocumentWriterThread implements Runnable{
    private Document doc;
    DocumentWriterThread(Document doc)
    {
        this.doc=doc;
    }

    @Override
    public void run()
    {

        File file=new File("/home/chimera/corpus/"+doc.getTitle().hashCode()+".txt");
        file.getParentFile().mkdirs();

        try {
            Files.write(Paths.get(file.toString()),doc.getBody().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
