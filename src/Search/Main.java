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
        long startTime = System.currentTimeMillis();
        JsonStreamParser parser = new JsonStreamParser(args[0]);
        executor = Executors.newFixedThreadPool(200);
        GlobalPosIndex globalPosIndex=new GlobalPosIndex();
        parser.start(globalPosIndex);

        executor.shutdown();
        boolean shutdown = false;
        while(shutdown==false)
        {
            Thread.sleep(1000);
            shutdown=executor.isTerminated();
        }
        long endTime = System.currentTimeMillis();

        System.out.println(endTime-startTime);
    }
}
