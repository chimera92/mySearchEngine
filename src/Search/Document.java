package Search;

/**
 * Created by chimera on 9/9/17.
 */
@SuppressWarnings({"DefaultFileTemplate", "unused"})
class Document {
    private String body;
    private String title;
    private int id;
    private String uri;


    @Override
    public String toString() {
        return "\"title\" = \"" + title+"\" ::: \"body\" = \""+ body+"\"}";
    }

    public  String getTitle() {
        return title;
    }
    public  String getBody() {
        return body;
    }

    public void setId(int id)
    {
        this.id=id;
    }
    public int getId(){
        return id;
    }

}
