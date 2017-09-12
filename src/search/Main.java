package search;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static ExecutorService executor=null;
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException
    {
        JsonStreamParser parser = new JsonStreamParser(args[0]);
        executor = Executors.newFixedThreadPool(1000);

        parser.start();
        executor.shutdown();

    }
}
