import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by chimera on 9/11/17.
 */
public class DocumentWriter implements Runnable{
    private Document doc;
    DocumentWriter(Document doc)
    {
        this.doc=doc;
    }

    @Override
    public void run()
    {

        String data = "Test data";
        File file=new File("corpus/"+doc.getTitle().hashCode()+".txt");
        file.getParentFile().mkdirs();

        try {
            Files.write(Paths.get(file.toString()),data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
