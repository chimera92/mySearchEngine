package Search;

import org.tartarus.martin.Stemmer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by chimera on 9/11/17.
 */
@SuppressWarnings("DefaultFileTemplate")
class IndexBuilderThread implements Runnable{

        private final Document doc;
        private final GlobalPosIndex globalPosIndex;
        private final int docId;

    public IndexBuilderThread(GlobalPosIndex globalPosIndex, Document doc){
            this.doc=doc;
            this.docId=doc.getId();
            this.globalPosIndex=globalPosIndex;
        }

        @Override
        public void run()
        {

            Map posIndex=new HashMap<String,ArrayList<Integer>>();

            String[] tokens = doc.getBody().split("\\s+");
            String[] token_arr;
            ArrayList temp=new ArrayList<Integer>();
            String token;

            Integer i=0;

            for (String tempToken:tokens)
            {
                if(!tempToken.matches(".*?\\w.*"))
                {
//                    System.out.println(tempToken+"!!!!!!!!!!!!!!");
                    continue;
                }
                token_arr =tempToken.replaceAll("^\\W+|\\W+$|'","").split("\\s*-\\s*");
                if(token_arr.length>1)
                {
                    for(int j = 0; j< token_arr.length; j++)
                    {
                        if(posIndex.containsKey(token_arr[j]))
                        {
                            temp= (ArrayList) posIndex.get(token_arr[j]);
                            temp.add(i+j);
                            posIndex.put(token_arr[j],temp.clone());
                        }
                        else
                        {
                            temp.clear();
                            temp.add(docId);
                            temp.add(i+j);
                            posIndex.put(token_arr[j],temp.clone());
                        }
                    }
                }
                token=String.join("",token_arr);

//                System.out.println(i+token);
                if(posIndex.containsKey(token))
                {
                    temp = (ArrayList) posIndex.get(token);
                    temp.add(i);
                    posIndex.put(token,temp.clone());
                }
                else {
                    temp.clear();
                    temp.add(docId);
                    temp.add(i);
                    posIndex.put(token,temp.clone());
                }
                i++;
            }

            Map.Entry key_val;
            for (Object o : posIndex.entrySet()) {
                key_val = (Map.Entry) o;
                temp = (ArrayList) key_val.getValue();

//                Debug
//                System.out.println(key_val.getKey());
//                for(int j=0;j<temp.size();j++)
//                {
//                    System.out.print(temp.get(j)+" ");
//                }
//                System.out.println();
                globalPosIndex.add((String) key_val.getKey(),(Integer[]) temp.toArray(new Integer[temp.size()]));
            }
        }
    }