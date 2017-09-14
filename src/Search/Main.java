package Search;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Main {

    private static ExecutorService executor=null;

    public static void submitJob(Runnable job)
    {
        executor.execute(job);
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException
    {
        JsonStreamParser parser = new JsonStreamParser(args[0]);
        executor = Executors.newFixedThreadPool(150);
        GlobalPosIndex globalPosIndex=new GlobalPosIndex();
        parser.start(globalPosIndex);
        executor.shutdown();
    }
}
