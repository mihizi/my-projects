package editor;

import javafx.scene.text.Text;

//TextNode-base of LinkedListDeque
//Made this class public for undo/redo
public class TextNode {
    public TextNode prev; /* previous TextNode */
    public Text text;
    public TextNode next;

    public TextNode(TextNode p, Text i, TextNode h) {
        prev = p;
        text = i;
        next = h;
    }

}

