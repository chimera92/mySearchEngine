package Search;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chimera on 9/25/17.
 */
public class QueryProcessor {

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
        String[] docList=null;
        String[] subQueries = query.split("\\s*\\+\\s*");
        ArrayList<String> subAtomicQueries=new ArrayList<String>();
        for(String subQuery:subQueries)
        {
            ArrayList<String> queryBuffer=new ArrayList<String>();
            boolean phraseBuffFlag=false;
            for(String subAtomicQuery:subQuery.split("\\s+"))
            {
                if (subAtomicQuery.matches(".*?\".*"))
                {
                    if(phraseBuffFlag==false)
                    {
                        phraseBuffFlag=true;
                    }
                    else
                    {
                        phraseBuffFlag=false;
                        queryBuffer.add(subAtomicQuery);
                        subAtomicQueries.add(0,String.join(" ",queryBuffer));
                        queryBuffer.clear();
                    }
                }
                if (phraseBuffFlag)
                {
                    queryBuffer.add(subAtomicQuery);
                }
                else
                {
                    subAtomicQueries.add(subAtomicQuery);
                }
            }
        }
//        for sub
        
        return docList;

    }
//
//    private Object[] extractPhraseQuery(String subQuery)
//    {
//        int startIndex=subQuery.indexOf("\"");
//        int endIndex=subQuery.indexOf("\"",startIndex+1);
//        System.out.println(startIndex+" "+endIndex);
////        return new Object[] {subQuery.substring(startIndex, endIndex+1), startIndex,endIndex};
//    }

}
