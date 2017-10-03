package Search;

import org.tartarus.snowball.ext.PorterStemmer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chimera on 9/25/17.
 */
public class QueryProcessor {


    private final GlobalPosIndex globalPosIndex;
    private final GlobalBiWordIndex globalBiWordIndex;
    private final PorterStemmer stemmer;

    public QueryProcessor(GlobalPosIndex globalPosIndex, GlobalBiWordIndex globalBiWordIndex)
    {
        this.globalPosIndex = globalPosIndex;
        this.globalBiWordIndex = globalBiWordIndex;
        this.stemmer = new PorterStemmer();
    }

    /**
     * Method will parse query and extract all literals and populate positiveLiterals, negativeLiterals,
     * positivePhraseQueries, and negativePhraseQuries.
     *
     * @param query
     */
    public String[] parseQuery(String query)
    {
        //split query on the basis of '+'
        //for each part then,
        //check if there is " present, find the closing quotes.
        //if there is closing quotes, ignore any symbol present in the phrase.
        //if there is no closing quotes, ignore opening quote.
        //populate all lists.
        //Keep ORing the results hence obtained.
//        System.out.println(query);
//        Pattern pattern = Pattern.compile("^(.*?)(\\([^\\(\\)]*\\))(.*?)$");
//        while(query.contains("("))
//        {
//            Matcher matcher = pattern.matcher(query);
//            matcher.find();
//            System.out.println(matcher.group(1)+".."+matcher.group(2)+".."+matcher.group(3));
//            break;
//        }




        String[] docList=null;
        String[] subQueries = query.split("\\s*\\+\\s*");
        ArrayList<ArrayList<Document>> toBeOrEdList=new ArrayList<ArrayList<Document>>();
        for(String subQuery:subQueries)
        {
//            System.out.println(subQuery);
            processOrLessQuery(subQuery);
        }

        return docList;

    }

    private ArrayList<Document> processOrLessQuery(String subQuery)
    {
        ArrayList<String> queryBuffer=new ArrayList<String>();
        ArrayList<String> AtomicQueries=new ArrayList<String>();
        boolean phraseBuffFlag=false;
        for(String subAtomicQuery:subQuery.split("\\s+"))
        {
//            System.out.println(subAtomicQuery);
            if (subAtomicQuery.matches("[^\"]*\"[^\"]*"))
            {
                if(phraseBuffFlag==false)
                {
                    phraseBuffFlag=true;
                }
                else
                {
                    phraseBuffFlag=false;
                    queryBuffer.add(subAtomicQuery);
                    AtomicQueries.add(0,String.join(" ",queryBuffer));
                    queryBuffer.clear();
                    continue;
                }
            }
            if (phraseBuffFlag==true)
            {
                queryBuffer.add(subAtomicQuery);
            }
            else
            {
                AtomicQueries.add(subAtomicQuery);
            }
        }
        for (String q:AtomicQueries)
        {
            System.out.println("!!"+q);
        }
        return new ArrayList<Document>();
    }

    private String stem(String input)
    {
        stemmer.setCurrent(input);
        stemmer.stem();
//            System.out.println(stemmer.getCurrent());
        return stemmer.getCurrent();
    }



//
//        System.out.println("docIds:");
//        for(int i=0; i<docIds.size();i++) {
//            System.out.print(docIds.get(i)[0]+" ");
//        }
//        System.out.println();
//        System.out.println("queryResults:");
//        for(int i=0; i<queryResults.size();i++) {
//            System.out.print(queryResults.get(i)[0]+" ");
//        }
//        System.out.println();



    public ArrayList<Integer[]> query(String token)
    {
        return globalPosIndex.get(token);
    }


    public ArrayList<Integer[]> queryPhrase(String phrase,int nearK)
    {

        String[] phraseTokens = phrase.replaceAll("^\"*|\"*$", "").split("\\s+");

        for(int i=0;i<phraseTokens.length;i++)
        {
            String temp=phraseTokens[i].replaceAll("^\\W*|\\W*$","");
            phraseTokens[i]=this.stem(temp);
        }

        HashSet<Integer> docsToBeScanned = new HashSet<>();

        ArrayList<Integer[]> andResult = query(phraseTokens[0]);
        for (int i = 1; i < phraseTokens.length; i++) {
            andResult = andOperation(andResult, phraseTokens[i]);
        }

        if (andResult.size() == 0) {
            return null;
        }
        for (Integer[] posting : andResult) {
            docsToBeScanned.add(posting[0]);
        }

        ArrayList<ArrayList<Integer[]>> queryTokenIndex = new ArrayList<>(phraseTokens.length);


        for (int i = 0; i < phraseTokens.length; i++) {
            ArrayList<Integer[]> andPosList=new ArrayList<>();
            for(Integer[] posting:query(phraseTokens[i]))
            {
                if(docsToBeScanned.contains(posting[0]))
                {
                    andPosList.add(posting);
                }
            }
            queryTokenIndex.add(andPosList);
        }


        int andDocsSize=queryTokenIndex.get(0).size();


        ArrayList<Integer[]> result=new ArrayList<>();
        for(int docNo=0;docNo<andDocsSize;docNo++)
        {
            ArrayList<Integer[]> allTermDocs=new ArrayList<>(queryTokenIndex.size());
            for(ArrayList<Integer[]> phraseToken :queryTokenIndex)
            {
                allTermDocs.add(phraseToken.get(docNo));
            }
            Integer[] basePositions=allTermDocs.get(0);
            for(int basePosIndex=1;basePosIndex<basePositions.length;basePosIndex++)
            {
                int basePos=basePositions[basePosIndex];

                int offset=0;
                boolean phraseFound=true;
                for (Integer[] termDoc:allTermDocs)
                {
                    boolean found=false;
                    for(int x=1;x<termDoc.length;x++)
                    {
                        for(int k=0;k<nearK;k++)
                        {

                            if(termDoc[x]==basePos+offset+k)
                            {
                                found=true;
                                break;
                            }
                        }

                    }
//                    List<Integer> pos =new ArrayList<Integer>(Arrays.asList(termDoc));
//                    pos=pos.subList(1,pos.size()-1);
                    phraseFound=found && phraseFound;
                    offset++;
                }
                if(phraseFound)
                {
                    result.add(basePositions);
                    break;
                }
            }


        }
        return result;
    }

    public ArrayList<Integer[]> orOperation(ArrayList<Integer[]> docIds, String token) {
        ArrayList<Integer[]> queryResults = query(token);
        ArrayList<Integer[]> output = new ArrayList<Integer[]>();
        int index1=0, index2 = 0;
        while(index1 < queryResults.size() && index2 < docIds.size()) {
            if(queryResults.get(index1)[0].equals(docIds.get(index2)[0])) {
                output.add(queryResults.get(index1));
                index1++;
                index2++;
            } else if(queryResults.get(index1)[0].intValue() < docIds.get(index2)[0].intValue()) {
                output.add(queryResults.get(index1));
                index1++;
            } else {
                output.add(docIds.get(index2));
                index2++;
            }
        }

        while(index1< docIds.size()) {
            output.add(docIds.get(index1));
            index1++;
        }
        while(index2 < queryResults.size()) {
            output.add(queryResults.get(index1));
            index2++;
        }

        return output;
    }


    public ArrayList<Integer[]> notOperation(ArrayList<Integer[]> docIds, String token) {
        ArrayList<Integer[]> queryResults = query(token);
        ArrayList<Integer[]> output = new ArrayList<Integer[]>();
        int index1 = 0, index2 = 0;
        while(index1 < docIds.size()) {
            if(docIds.get(index1).equals(queryResults.get(index2))) {
                index1++;
                index2++;
            } else if (docIds.get(index1)[0].intValue() < queryResults.get(index2)[0].intValue()) {
                output.add(docIds.get(index1));
                index1++;
            } else {
                index2++;
            }
        }
        return output;
    }


    public ArrayList<Integer[]> andOperation(ArrayList<Integer[]> docIds, String token) {
        ArrayList<Integer[]> queryResults = query(token);
        ArrayList<Integer[]> output = new ArrayList<Integer[]>();
        int index1=0, index2 = 0;


        if (queryResults==null || docIds==null)
        {
            return output;
        }


        while(index1 < queryResults.size() && index2 < docIds.size()) {
            if(queryResults.get(index1)[0].equals(docIds.get(index2)[0])) {
                output.add(queryResults.get(index1));
                index1++;
                index2++;
            } else if(queryResults.get(index1)[0].intValue() < docIds.get(index2)[0].intValue()) {
                index1++;
            } else {
                index2++;
            }
        }
        return output;
    }


}
