package Search;

import org.tartarus.snowball.ext.PorterStemmer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.*;

class Main {

    private static ExecutorService executor=null;
    private static PorterStemmer stemmer=new PorterStemmer();
    public static CountDownLatch latch;



    public static void submitJob(Runnable job)
    {
        executor.execute(job);
    }

    public static String stem(String input)
    {
        stemmer.setCurrent(input);
        stemmer.stem();
//            System.out.println(stemmer.getCurrent());
        return stemmer.getCurrent();
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException
    {


        GlobalPosIndex globalPosIndex=new GlobalPosIndex();
        GlobalBiWordIndex globalBiWordIndex=new GlobalBiWordIndex();
        QueryProcessor qp=new QueryProcessor(globalPosIndex,globalBiWordIndex);
//        System.out.println("Enter an input");
        Scanner reader = new Scanner(System.in);
        boolean moreInput=true;
        String in;




        System.out.println("...MY SEARCH ENGINE...");
        System.out.println(":q");
        System.out.println(":stem <token>");
        System.out.println(":index <dir_path>");
        System.out.println(":vocab");
        System.out.println("<query>");
        System.out.println(":corpusFromJSON <json filepath>");
        do
        {

            System.out.println("Enter an input :");
            System.out.println();

            in = reader.nextLine().trim();

            String[] command = in.split("\\s+",2);

            switch(command[0].toLowerCase()) {
                case ":q": moreInput=false;
                    break;
                case ":stem":
                    if(command.length<2)
                    {
                        System.err.println("No word found to Stem..!!");
                        break;
                    }
                    System.out.println("Stem Value:\n"+stem(command[1]));
                    break;

                case ":index":
                    if(command.length>1) {
                        executor = Executors.newFixedThreadPool(200);
                        CorpusFromDirectory corpusFromDirectory = new CorpusFromDirectory(globalPosIndex, globalBiWordIndex, command[1]);
                        corpusFromDirectory.start();
                        executor.shutdown();
                        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MICROSECONDS);
                    }
                    else
                        System.err.println("Missing filepath parameter!!");
                    break;
                case ":corpusfromjson":
                    if(command.length<2)
                    {
                        System.err.println("Missing filepath parameter!!");
                        break;
                    }
//                    !!!!!!!!!!!!!!flag!!
                    executor = Executors.newFixedThreadPool(200);
                    JsonStreamParser parser = new JsonStreamParser(globalPosIndex,globalBiWordIndex,args[0]);
                    parser.start();
                    executor.shutdown();
                    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MICROSECONDS);
                    break;
                case ":vocab":
                    globalPosIndex.showVocab();
                    break;

                default:
                    ArrayList<Integer[]> result = qp.parseCompoundQuery(in.toLowerCase());
                    System.out.println(result);
                    for(Integer[] x : result) {
                        System.out.println(x[0].intValue());
                    }
                    break;
            }
        }while(moreInput);

        long startTime = System.currentTimeMillis();
//        System.out.println("Start time = "+startTime);

        long endTime = System.currentTimeMillis();
//        System.out.println(endTime-startTime);
        globalPosIndex.printLen();

//        ArrayList<Integer[]> x =qp.queryPhrase("preserv park",4);
//        ArrayList<Integer[]> y=qp.query("readi");
//        ArrayList<Integer[]> aa = qp.andOperation(y,"to");
//        System.out.println(x.toString());

    }

}
//flag
//null in and or phrase
//change corpus path
