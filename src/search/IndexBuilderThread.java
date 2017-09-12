package search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chimera on 9/11/17.
 */
public class IndexBuilderThread implements Runnable{

        private Document doc;

        public IndexBuilderThread(Document doc){
            this.doc=doc;
        }

        @Override
        public void run()
        {
            Map posIndex=new HashMap<String,ArrayList<Integer>>();
            String[] tokens = doc.getBody().split(" ");
            for(int i=0;i<tokens.length;i++)
            {
                String token=tokens[i].replaceAll("^\\W+|\\W+$|'"," ");
                
                if(posIndex.containsKey(token))
                {
                    ArrayList temp = (ArrayList) posIndex.get(token);
                    temp.add(i);
                    posIndex.put(token,temp);
                }
                else {
                    posIndex.put(token,new ArrayList<Integer>(i));
                }
            }
        }
    }

