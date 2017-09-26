package Search;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Main {

    private static ExecutorService executor=null;

    public static void submitJob(Runnable job)
    {
        executor.execute(job);
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException
    {

        QueryProcessor qp=new QueryProcessor();
        System.out.println("Enter an input");
        Scanner reader = new Scanner(System.in);
        boolean moreInput=true;
        String in;
        do
        {
            in = reader.nextLine();
            System.out.println("Enter an input");
            if(in.matches("\\s*\\:q\\s*"))
            {

                System.out.println("Quit!!");
                moreInput=false;
                continue;
            }
            else
            {
                qp.processQuery(in);
            }
            if(!reader.hasNextLine())
            {
                moreInput=false;
            }
        }while(moreInput);

        long startTime = System.currentTimeMillis();
        System.out.println("Start time = "+startTime);
        JsonStreamParser parser = new JsonStreamParser(args[0]);
        executor = Executors.newFixedThreadPool(200);
        GlobalPosIndex globalPosIndex=new GlobalPosIndex();
        parser.start(globalPosIndex);

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MICROSECONDS);
        long endTime = System.currentTimeMillis();
        System.out.println(endTime-startTime);



    }
}
