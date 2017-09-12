import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Main {
    public static Object lock=new Object();

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException
    {
        JsonStreamParser parser = new JsonStreamParser(args[0]);
        parser.start();
    }
}
