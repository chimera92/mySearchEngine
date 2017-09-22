package Search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chimera on 9/11/17.
 */


@SuppressWarnings("DefaultFileTemplate")
class GlobalPosIndex {
    private final Map universalIndex;
    private ArrayList tempList;
    private ArrayList newList;

    public GlobalPosIndex()
    {
        universalIndex = Collections.synchronizedMap(new HashMap<String,ArrayList[]>());
        tempList =new ArrayList();
        newList =new ArrayList();

    }

    public void add(String key, Integer[] posArray)
    {
        synchronized (universalIndex)
        {
            if(universalIndex.containsKey(key))
            {
                tempList = (ArrayList) universalIndex.get(key);
//                tempList= this.binInsert((ArrayList) tempList.clone(), posArray.clone());
                this.binInsert(tempList, posArray);
//*                universalIndex.put(key, tempList.clone());
//                universalIndex.put(key, tempList);
            }
            else
            {
                newList.clear();
//                tempList.add(posArray.clone());
                newList.add(posArray);
                universalIndex.put(key, newList.clone());
            }

        }
    }

    private ArrayList binInsert(ArrayList tempListP, Integer[] postingArray)
    {
        Integer insertPos;
        if(tempListP.size()==1)
        {
            Integer[] firstPosting= (Integer[]) tempListP.get(0);
            Integer firstVal=firstPosting[0];
            if(firstVal>postingArray[0])
            {
                insertPos=0;
            }
            else
            {
                insertPos=1;
            }
//            System.out.println("!!!!!!"+firstVal+" "+postingArray[0]+" "+insertPos);
        }
        else {
            insertPos = searchSortedInsertPos(tempListP,0,tempListP.size()-1,postingArray);
        }


//        System.out.println();
//        System.out.println(postingArray[0]+" "+insertPos);
//        for (Object o : tempListP) {
//            Integer[] x= (Integer[]) o;
//            System.out.print(x[0]+" ");
//        }
//        System.out.println();
        tempListP.add(insertPos,postingArray);
        return tempListP;
    }

    private int searchSortedInsertPos(ArrayList tempListP, int startP, int endP, Integer[] elementP)
    {

        if(endP==startP+1)
        {
            if(((Integer[])tempListP.get(endP))[0]<elementP[0])
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
        if(midVal<elementP[0])
        {
            return searchSortedInsertPos(tempListP,midL,endP,elementP);
        }
        else
        {
            return searchSortedInsertPos(tempListP,startP,midL,elementP);
        }
    }
}
