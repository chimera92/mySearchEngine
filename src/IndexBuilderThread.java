/**
 * Created by chimera on 9/11/17.
 */
public class IndexBuilderThread implements Runnable{

        private Document doc;

        public IndexBuilderThread(Document doc){
            this.doc=doc;
        }

        @Override
        public void run() {
            System.out.println(doc.toString());
        }
    }

