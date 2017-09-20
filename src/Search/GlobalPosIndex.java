package Search;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chimera on 9/11/17.
 */


@SuppressWarnings("DefaultFileTemplate")
class GlobalPosIndex {
    private final Map universalIndex;
    public GlobalPosIndex()
    {
        universalIndex = Collections.synchronizedMap(new HashMap<String,HashMap<Integer,Integer[]>>());
    }




    public void add(String key, Integer docId, Integer[] posArray)
    {
        synchronized (universalIndex)
        {
            HashMap<Integer,Integer[]> docId_wordPositions;
            if(universalIndex.containsKey(key))
            {
                 docId_wordPositions = (HashMap<Integer, Integer[]>) universalIndex.get(key);
                 docId_wordPositions.put(docId,posArray);
                 universalIndex.put(key,docId_wordPositions);
            }
            else
            {
                docId_wordPositions= new HashMap<>();
                docId_wordPositions.put(docId,posArray);
                universalIndex.put(key,docId_wordPositions);
            }

        }
    }

//
//    public Object get(String key)
//    {
//        synchronized(universalIndex)
//        {
//            return  universalIndex.get(key);
//        }
//    }

}
