package search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chimera on 9/11/17.
 */


public class reversePosIndex {
    private ConcurrentHashMap universalIndex;
    public reversePosIndex()
    {
        universalIndex=new ConcurrentHashMap<String,ConcurrentHashMap<String,ArrayList>>();
    }
}
