package Search;

import org.tartarus.snowball.ext.PorterStemmer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chimera on 9/25/17.
 */
public class QueryProcessor {


    private final GlobalPosIndex globalPosIndex;
    private final GlobalBiWordIndex globalBiWordIndex;
    private final PorterStemmer stemmer;
    private HashMap<String, ArrayList<Integer[]>> queryMap;

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


    public ArrayList<Integer[]> parseCompoundQuery(String query)
    {
        Pattern pattern = Pattern.compile("^(.*?)\\(([^\\(\\)]*)\\)(.*?)$");
        queryMap=new HashMap<String, ArrayList<Integer[]>>();
        System.out.println(query);

        while(query.contains("("))
        {
            Matcher matcher = pattern.matcher(query);
            if(matcher.find()) {

                String extractedQuery = matcher.group(2);
//            System.out.println(matcher.group(1)+".."+matcher.group(2)+".."+matcher.group(3));
                queryMap.put("^_^" + extractedQuery.hashCode(), parseSimpleQuery(extractedQuery));
                query = matcher.group(1) + " ^_^" + extractedQuery.hashCode() + " " + matcher.group(3);
                System.out.println(query);
            }
            else
            {
                System.err.println("Invalid Query!!");
                break;
            }
        }

        System.out.println(query);
        return parseSimpleQuery(query);
    }

    public ArrayList<Integer[]> parseSimpleQuery(String query)
    {
        //split query on the basis of '+'
        //for each part then,
        //check if there is " present, find the closing quotes.
        //if there is closing quotes, ignore any symbol present in the phrase.
        //if there is no closing quotes, ignore opening quote.
        //populate all lists.
        //Keep ORing the results hence obtained.
//        System.out.println(query);


        ArrayList<Integer[]> docList;
        String[] subQueries = query.split("\\s*\\+\\s*");
        ArrayList<ArrayList<Integer[]>> toBeOrEdList=new ArrayList<>();
        for(String subQuery:subQueries)
        {
//            System.out.println(subQuery);
            toBeOrEdList.add(processOrLessQuery(subQuery));
        }

        docList=orOperation(toBeOrEdList);

        return docList;

    }

    private ArrayList<Integer[]> processOrLessQuery(String subQuery)
    {
        ArrayList<String> queryBuffer=new ArrayList<String>();
        ArrayList<String> AtomicQueries=new ArrayList<String>();
        boolean phraseBuffFlag=false;
        for(String subAtomicQuery:subQuery.trim().split("\\s+"))
        {
//            if (subAtomicQuery.startsWith("^_^"))
            subAtomicQuery=subAtomicQuery.trim();

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
//                if(subAtomicQuery.matches("^-?\\^_\\^.*"))
//                {
//                    AtomicQueries.add(subAtomicQuery);
//                }
//                else
//                {//remove stem at this level.
//                    subAtomicQuery=subAtomicQuery.toLowerCase().replaceAll("^\\W+|\\W+$|'","");
                    AtomicQueries.add(subAtomicQuery);

//                }
             }
        }

        ArrayList<ArrayList<Integer[]>> negQueriesPosList = new ArrayList();
        ArrayList<ArrayList<Integer[]>> posQueriesPosList = new ArrayList();

        for (String q:AtomicQueries)
        {
            if(q.startsWith("-"))
            {
                if(q.matches("^-?\\^_\\^.*"))
                {
                    negQueriesPosList.add(queryMap.get(q.substring(1)));
                }
                else
                {
//                    q=q.substring(1).replaceAll("^\\W+|\\W+$|'","");;
                    negQueriesPosList.add(query(q.substring(1)));
                }

            }
            else
            {
                if(q.startsWith("^_^"))
                {
                    posQueriesPosList.add(queryMap.get(q));
                }
                else
                {
//                    q=q.replaceAll("^\\W+|\\W+$|'","");
                    posQueriesPosList.add(query(q));
                }
            }
        }

        if(posQueriesPosList.size()<1)
        {
            System.err.println("!! Invalid Query !!");
            return new ArrayList<>();
        }
        ArrayList<Integer[]> posuniou=andOperation(posQueriesPosList);
        ArrayList<Integer[]> notunion =orOperation(negQueriesPosList);
        return notOperation(posuniou,notunion);
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
        if(token.startsWith("\""))
        {
            return queryPhrase(token);
        }
        else
        {
            token=token.replaceAll("^\\W+|\\W+$|'","");
            return globalPosIndex.get(stem(token));
        }
    }


    public ArrayList<Integer[]> queryPhrase(String phrase)
    {

        int nearK=1;


        Pattern pattern = Pattern.compile("^(.*?)\\bnear/(\\d+)\\b(.*?)$");
        Matcher matcher = pattern.matcher(phrase);
        if(matcher.find())
        {
            nearK = Integer.parseInt(matcher.group(2));
//            System.out.println(matcher.group(2));
            phrase =matcher.group(1)+" "+matcher.group(3);
        }



        String[] phraseTokens = phrase.replaceAll("^\"*|\"*$", "").split("\\s+");




        for(int i=0;i<phraseTokens.length;i++)
        {
            String temp=phraseTokens[i].toLowerCase().replaceAll("^\\W*|\\W*$","");
            phraseTokens[i]=this.stem(temp);
        }

        if(phraseTokens.length==2 && nearK==1)
        {
            String biWordKey=String.join(" ",phraseTokens);
            System.out.println("!!!!!!!!!!");
            return globalBiWordIndex.getBiwordPosting(biWordKey);
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
                        if(found)
                        {
                            break;
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

    public ArrayList<Integer[]> orOperation(String token1, String token2) {
        return orOperation(query(token1), query(token2));
    }

    public ArrayList<Integer[]> orOperation(ArrayList<Integer[]> docIds1, ArrayList<Integer[]> docIds2) {
//        ArrayList<Integer[]> queryResults = query(token);
        ArrayList<Integer[]> output = new ArrayList<Integer[]>();
        int index1=0, index2 = 0;
        if(docIds1 == null && docIds2 == null) {
            return output;
        } else if(docIds1 == null) {
            return docIds2;
        } else if(docIds2 == null) {
            return docIds1;
        }
        while(index1 < docIds1.size() && index2 < docIds2.size()) {
            if(docIds1.get(index1)[0].equals(docIds2.get(index2)[0])) {
                output.add(docIds1.get(index1));
                index1++;
                index2++;
            } else if(docIds1.get(index1)[0].intValue() < docIds2.get(index2)[0].intValue()) {
                output.add(docIds1.get(index1));
                index1++;
            } else {
                output.add(docIds2.get(index2));
                index2++;
            }
        }

        while(index2< docIds2.size()) {
            output.add(docIds2.get(index2));
            index2++;
        }
        while(index1 < docIds1.size()) {
            output.add(docIds1.get(index1));
            index1++;
        }

        return output;
    }

    public ArrayList<Integer[]> orOperation(ArrayList<Integer[]> docIds, String token) {
        ArrayList<Integer[]> queryResults = query(token);
        return orOperation(docIds, queryResults);
    }

    public ArrayList<Integer[]> orOperation(ArrayList<ArrayList<Integer[]>> postingsList) {
        ArrayList<Integer[]> output = new ArrayList<Integer[]>();
        if(null == postingsList || postingsList.size() == 0) {
            return output;
        } else {
            for(ArrayList<Integer[]> thisPosting : postingsList) {
                output = orOperation(output, thisPosting);
            }
        }
        return output;
    }


    public ArrayList<Integer[]> notOperation(ArrayList<Integer[]> docIds1, ArrayList<Integer[]> docIds2) {
        ArrayList<Integer[]> output = new ArrayList<Integer[]>();
        int index1 = 0, index2 = 0;
        if(docIds2==null || docIds2.size()<1)
        {
            return docIds1;
        }

        while(index1 < docIds1.size() && index2<docIds2.size()) {
            if(docIds1.get(index1)[0].equals(docIds2.get(index2)[0])) {
                index1++;
                index2++;
            } else if (docIds1.get(index1)[0].intValue() < docIds2.get(index2)[0].intValue()) {
                output.add(docIds1.get(index1));
                index1++;
            } else {
                index2++;
            }
        }

        while(index1 < docIds1.size())
        {
            output.add(docIds1.get(index1));
            index1++;
        }
        return output;
    }

    public ArrayList<Integer[]> notOperation(ArrayList<Integer[]> docIds, String token) {
        return notOperation(docIds, query(token));
    }

    public ArrayList<Integer[]> andOperation(String token1, String token2) {
        return andOperation(query(token1), query(token2));
    }

    public ArrayList<Integer[]> andOperation(ArrayList<ArrayList<Integer[]>> postingsList) {
        ArrayList<Integer[]> output = postingsList.get(0);
        //Do nothing for andOperation on single token.
        if(null == postingsList || postingsList.size() <= 1) {
            return output;
        } else {
            for(int i=1;i<postingsList.size();i++) {
                output = andOperation(output, postingsList.get(i));
            }
        }
        return output;
    }

    public ArrayList<Integer[]> andOperation(ArrayList<Integer[]> docIds1, ArrayList<Integer[]> docIds2) {
        ArrayList<Integer[]> output = new ArrayList<Integer[]>();
        int index1=0, index2 = 0;


        if (docIds1==null || docIds2==null)
        {
            return output;
        }


        while(index1 < docIds1.size() && index2 < docIds2.size()) {
            if(docIds1.get(index1)[0].equals(docIds2.get(index2)[0])) {
                output.add(docIds1.get(index1));
                index1++;
                index2++;
            } else if(docIds1.get(index1)[0].intValue() < docIds2.get(index2)[0].intValue()) {
                index1++;
            } else {
                index2++;
            }
        }
        return output;
    }

    public ArrayList<Integer[]> andOperation(ArrayList<Integer[]> docIds, String token) {
        ArrayList<Integer[]> queryResults = query(token);
        return andOperation(docIds, queryResults);
    }


}
