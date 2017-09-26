package Search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chimera on 9/25/17.
 */
public class GlobalBiWordIndex {
    private final Map universalIndex;
    private ArrayList tempList;
    private ArrayList newList;

    public GlobalBiWordIndex()
    {
        universalIndex = Collections.synchronizedMap(new HashMap<String,ArrayList[]>());
        tempList =new ArrayList();
        newList =new ArrayList();

    }

    public void add(String key, Integer docID)
    {
        synchronized (universalIndex)
        {
            if(universalIndex.containsKey(key))
            {
                tempList = (ArrayList) universalIndex.get(key);
                this.binInsert(tempList, docID);
            }
            else
            {
                newList.clear();
                newList.add(docID);
                universalIndex.put(key, newList.clone());
            }

        }
    }

    private ArrayList binInsert(ArrayList tempListP, Integer docID)
    {
        Integer insertPos;
        if(tempListP.size()==1)
        {
            Integer[] firstPosting= (Integer[]) tempListP.get(0);
            Integer firstVal=firstPosting[0];
            if(firstVal>docID)
            {
                insertPos=0;
            }
            else
            {
                insertPos=1;
            }
        }
        else {
            insertPos = searchSortedInsertPos(tempListP,0,tempListP.size()-1,docID);
        }


        tempListP.add(insertPos,docID);
        return tempListP;
    }

    private int searchSortedInsertPos(ArrayList tempListP, int startP, int endP, Integer docId)
    {

        if(endP==startP+1)
        {
            if(((Integer[])tempListP.get(endP))[0]<docId)
            {
                return endP+1;
            }
            else
            {
                return endP;
            }
        }

        int midL=(startP+endP)/2;
        Integer[] midPosting= (Integer[]) tempListP.get(midL);
        Integer midVal=midPosting[0];
        if(midVal<docId)
        {
            return searchSortedInsertPos(tempListP,midL,endP,docId);
        }
        else
        {
            return searchSortedInsertPos(tempListP,startP,midL,docId);
        }
    }
}
