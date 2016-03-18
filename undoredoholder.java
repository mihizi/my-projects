package editor;


import javafx.scene.text.Text;

/**
 * Created by mihirchitalia on 3/7/16.
 */
public class undoredoholder {
    TextNode where;
    boolean isInsert;
    Text text;
    public undoredoholder(TextNode where, boolean isInsert, Text text){
        this.where = where;
        this.isInsert = isInsert;
        this.text = text;
    }
}
