package sqlite;

/**
 * Created by user on 2016/11/15.
 */
public class Root {
    int r_id;
    String root;

    public Root(int r_id,String root){
        this.r_id = r_id;
        this.root = root;
    }

    public Root(){}

    public int getR_id(){return r_id;}
    public String getRoot() {return root;}
    public void setRoot_id(int root_id){this.r_id = root_id;}
    public void setRoot(String root){this.root = root;}
}
