package Search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by chimera on 9/25/17.
 */
public class QueryProcessor {

    List<String> positiveLiterals;
    List<String> negativeLiterals;
    List<String> positivePhraseQueries;
    List<String> negativePhraseQueries;

    public QueryProcessor() {
        positiveLiterals = new ArrayList<String>();
        negativeLiterals = new ArrayList<String>();
        positivePhraseQueries = new ArrayList<String>();
        negativePhraseQueries = new ArrayList<String>();
    }
//
//    /**
//     * Method will parse query and extract all literals and populate positiveLiterals, negativeLiterals,
//     * positivePhraseQueries, and negativePhraseQuries.
//     *
//     * @param query
//     */
//    public String[] parseQuery(String query) {
//        //split query on the basis of '+'
//        //for each part then,
//        //check if there is " present, find the closing quotes.
//        //if there is closing quotes, ignore any symbol present in the phrase.
//        //if there is no closing quotes, ignore opening quote.
//        //populate all lists.
//        //Keep ORing the results hence obtained.
//        String[] docList=null;
//
//        String[] subQueries = query.split("+");
//        StringBuilder updatedQuery = new StringBuilder(query);
//        for(int i=0; i<subQueries.length; i++) {
//            //extract positive and negative phrase queries.
//            updatedQuery = extractPhrase(positivePhraseQueries, negativePhraseQueries, query);
//            //now there are no more "s in the updatedQuery.
//            //extract positive and negative literals.
//            extractLiterals(positiveLiterals, positiveLiterals, updatedQuery.toString());
//        }
//        return docList;
//
//    }
//
//    private void extractLiterals(List<String> positiveLiterals, List<String> negativeLiterals, String query) {
//        String[] subQueries = query.split(" ");
//        for(String thisSubQuery : subQueries) {
//            if(thisSubQuery.charAt(0) == '-') {//here we are assuming that there would be no space between '-' and literal(or phrase)
//                negativeLiterals.add(thisSubQuery.substring(1));
//            } else {
//                positiveLiterals.add(thisSubQuery);
//            }
//        }
//    }
//
//    /**
//     * method to extract positive and negative phrases out of query, and populate positivePhrases, and negativePhrases.
//     *
//     * @param positivePhrases
//     * @param negativePhrases
//     * @param query
//     * @return query with all positive and negative phrases removed.
//     */
//    private StringBuilder extractPhrase(List<String> positivePhrases, List<String> negativePhrases, String query) {
//        int startIndex = -1;
//        int endIndex = -1;
//        StringBuilder prunedQuery = new StringBuilder(query);
//        boolean isPositivePhrase = false;
//        boolean isNegativePhrase = false;
//        for(int i=0; i<query.length(); i++) {
//            if(query.charAt(i) == '"') {
//                startIndex = i;
//                if(i==0) {
//                    isPositivePhrase = true;
//                } else if(query.charAt(i-1) == '-') {
//                    isNegativePhrase = true;
//                } else {
//                    isPositivePhrase = true;
//                }
//                for(int j=i; j<query.length(); j++) {
//                    if(query.charAt(j) == '"') {
//                        endIndex = j;
//                        if(isPositivePhrase) {
//                            //insert this phrase into positivePhrases.
//                            positivePhrases.add(query.substring(startIndex+1, endIndex));
//                        } else {
//                            negativePhrases.add(query.substring(startIndex+1, endIndex));
//                        }
//                        isPositivePhrase = false;
//                        isNegativePhrase = false;
//                        prunedQuery = prunedQuery.delete(startIndex, endIndex);
//                        startIndex = -1;
//                        endIndex = -1;
//                        i = j;
//                        break;
//                    }
//                }
//                if(startIndex != -1) {//means query has opening '"', but not closing '"', remove opening '"' in such cases.
//                    prunedQuery = prunedQuery.deleteCharAt(startIndex);
//                }
//            }
//        }
//        return prunedQuery;
//    }
//
//    /***
//     * Method to query corpus for a single Token
//     * @param token
//     * @return list of document Ids containing the word token.
//     */
//    public List<Integer> query(String token) {
//        //get global Map here.
//        List<Integer> docIds = new ArrayList<Integer>();
//        Map<String, ArrayList[]> globalMap = new GlobalPosIndex().getUniversalIndex();
//        ArrayList[] postings = globalMap.get(token);
//        for(int i=0; i<postings.length; i++) {
//            docIds.add((Integer) postings[i].get(0));
//        }
//        return docIds;
//    }
//
//    public List<Integer> andOperation(Map globalMap, String token1, String token2) {
//        //get Global Map here.
//        List<Integer> docIds = new ArrayList<Integer>();
//        //Map<String, ArrayList<Integer>[]> globalMap = new GlobalPosIndex().getUniversalIndex();
//        List<Integer> result1 = new ArrayList<Integer>();
//        List<Integer> result2 = new ArrayList<Integer>();
//        ArrayList postings1 = (ArrayList) globalMap.get(token1);
//        ArrayList postings2 = (ArrayList) globalMap.get(token2);
//        for(int i=0; i<postings1.size(); i++) {
//            Integer[] temp = (Integer[]) postings1.get(i);
//            result1.add(temp[0]);
//        }
//        for(int i=0; i<postings2.size(); i++) {
//            Integer[] temp = (Integer[]) postings2.get(i);
//            result2.add(temp[0]);
//        }
//        int index1=0, index2 = 0;
//        while(index1 < result1.size() && index2 < result2.size()) {
//            if(result1.get(index1) == result2.get(index2)) {
//                docIds.add(result1.get(index1));
//                index1++;
//                index2++;
//            } else if(result1.get(index1) < result2.get(index2)) {
//                index1++;
//            } else {
//                index2++;
//            }
//        }
//        return docIds;
//    }
//    public List<Integer> phraseQuery(String phrase) {
//        //get Global Map here.
//        List<Integer> docIds = new ArrayList<Integer>();
//        Map<String, ArrayList> globalMap = new GlobalPosIndex().getUniversalIndex();
//
//        return null;
//    }

}
