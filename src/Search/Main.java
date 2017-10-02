package Search;

import org.tartarus.snowball.ext.PorterStemmer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Main {

    private static ExecutorService executor=null;
    private static PorterStemmer stemmer=new PorterStemmer();


    public static void submitJob(Runnable job)
    {
        executor.execute(job);
    }

//    public static String stem(String input)
//    {
//        stemmer.setCurrent(input);
//        stemmer.stem();
////            System.out.println(stemmer.getCurrent());
//        return stemmer.getCurrent();
//    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException
    {


        GlobalPosIndex globalPosIndex=new GlobalPosIndex();
        GlobalBiWordIndex globalBiWordIndex=new GlobalBiWordIndex();
        QueryProcessor qp=new QueryProcessor(globalPosIndex,globalBiWordIndex);
        System.out.println("Enter an input");
        Scanner reader = new Scanner(System.in);
        boolean moreInput=true;
        String in;
        do
        {
            in = reader.nextLine();
            System.out.println("Enter an input");
            System.out.println(in);
            if(in.matches("\\s*\\:q\\s*"))
            {

                System.out.println("Quit!!");
                moreInput=false;
                continue;
            }
            else
            {
                System.out.println("Process");
                qp.parseQuery(in);
            }
            if(!reader.hasNextLine())
            {
                moreInput=false;
            }
        }while(moreInput);
//        System.out.println(stem("top"));

        long startTime = System.currentTimeMillis();
        System.out.println("Start time = "+startTime);
        JsonStreamParser parser = new JsonStreamParser(args[0]);
        executor = Executors.newFixedThreadPool(200);

        parser.start(globalPosIndex,globalBiWordIndex);

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MICROSECONDS);
        long endTime = System.currentTimeMillis();
        System.out.println(endTime-startTime);
        globalPosIndex.printLen();

//        ArrayList<Integer[]> x =qp.queryPhrase("ready to be");
//        ArrayList<Integer[]> y=qp.query("readi");
//        ArrayList<Integer[]> aa = qp.andOperation(y,"to");

//        System.out.println(x.toString());
    }

}
