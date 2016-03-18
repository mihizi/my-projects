package editor;

import javafx.scene.Node;
import javafx.scene.text.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LinkedListDeque {

    //Class Variables
    private TextNode sentF;
    private int size;
    private TextNode sentB;
    public TextNode cursor;
    public TextNode renderer = null;
    TextNode start;
    TextNode end;
    private ArrayList<TextNode> newLines = new ArrayList<TextNode>(5000);


    //TextNode-base of LinkedListDeque

    //Constructors
    public LinkedListDeque() {
        size = 0;
        sentB = new TextNode(sentF, new Text(""), null);
        sentF = new TextNode(null, new Text(5, 0, ""), sentB);
        sentB.prev = sentF;
        cursor = sentF;
    }



    public void addChar(Text x) {
        TextNode newNode = new TextNode(cursor, x, cursor.next);
        cursor.next.prev = newNode;
        cursor.next = newNode;
        cursor = cursor.next;
        size += 1;
    }


    public boolean isEmpty() {
        return sentF.next.next == null;
    }

    public int size() {
        return size;
    }


    public Text removeChar() {
        if (isEmpty() || cursor == sentF) {
            return null;
        } else {
            TextNode removed = cursor;
            cursor.prev.next = cursor.next;
            cursor = cursor.prev;
            cursor.next.prev = cursor;
            size -= 1;
            newLines.remove(removed);
            return removed.text;
        }
    }



//    public TextNode getNode(int index) {
//        if (index > size() - 1) {
//            return null;
//        } else {
//            TextNode copyList = sentF.next;
//            while (index > 0) {
//                copyList = copyList.next;
//                index--;
//            }
//            return copyList;
//
//        }
//    }


    public Text getLastText() {
        return sentB.prev.text;
    }



    //Cursor Methods

    public Text getCursorText() {
        return cursor.text;
    }

    public TextNode getCursor(){
        return cursor;
    }

    public void movecursorleft() {
        if (cursor.prev == null) {
            return;
        }
        cursor = cursor.prev;
    }

    public void movecursorright() {
        if (cursor.next == sentB) {
            return;
        }
        cursor = cursor.next;
    }

    public void movecursorup(){
        TextNode ogCursor = cursor;
        double originalCursorX = getCursorText().getX() + Math.round(getCursorText().getLayoutBounds().getWidth());
        cursor = getPrevStart().prev;
        if (cursor == sentF.prev){
            cursor = ogCursor;
            return;
        }
        TextNode ending = getPrevStart();
        double minXDifference = 10000;

        //Loop Variables
        TextNode upNode = cursor;
        double curCursorX;
        double xDifference;
        while (cursor != ending.prev) {
            curCursorX = getCursorText().getX() + Math.round(getCursorText().getLayoutBounds().getWidth());
            xDifference = Math.abs(originalCursorX - curCursorX);
                if (xDifference < minXDifference){
                    minXDifference = xDifference;
                    upNode = cursor;
                }
            cursor = cursor.prev;
        }
        cursor = upNode;
    }

    private TextNode getPrevStart(){
        if (cursor == sentF){
            return cursor;
        }
        TextNode cursorCopy = cursor;
        while (cursorCopy.text.getX() != 5 ){
            if (cursorCopy.prev == null)
                return sentF.next;
            cursorCopy = cursorCopy.prev;
        }
        if (cursorCopy.prev.text.getX() == 5 && cursorCopy.prev.text.getY() == cursorCopy.text.getY()) {
            cursorCopy = cursorCopy.prev;
        }
        return cursorCopy;
    }

    public void movecursordown(){
        TextNode ogCursor = cursor;
        double originalCursorX = getCursorText().getX() + Math.round(getCursorText().getLayoutBounds().getWidth());
        cursor = getNextStart().next;
        if (cursor.equals(sentB)){ //check here
            cursor = ogCursor;
            return;
        }
        TextNode ending = getNextStart();
        double minXDifference = 10000;

        //Loop Variables
        TextNode downNode = cursor;
        double curCursorX;
        double xDifference;
        while (cursor.next != ending.next.next) {
            curCursorX = getCursorText().getX() + Math.round(getCursorText().getLayoutBounds().getWidth());
            xDifference = Math.abs(originalCursorX - curCursorX);
            if (xDifference < minXDifference){
                minXDifference = xDifference;
                downNode = cursor;
                if (minXDifference == 0){
                    return;
                }
            }
                cursor = cursor.next;
        }
        cursor = downNode;
    }



    private TextNode getNextStart(){
        TextNode cursorCopy = cursor;
        while (cursorCopy.text.getX() != 5){
            if (cursorCopy.next == null)
                return sentB.prev;
            cursorCopy = cursorCopy.next;
        }
        if (cursorCopy.next.text.getX() == 5) {
            cursorCopy = cursorCopy.next;
        }

        return cursorCopy;
    }

    //Adds to the arraylist a pointer to the start of a line
    public void pointStart(TextNode starter){
        if (!newLines.contains(starter) && starter.text.getY() != starter.prev.text.getY() && starter.text.getX() != starter.prev.text.getX()) {
            newLines.add(starter);
        }

    }

    public void movecursorclick(double xClick, double yClick){
        if (yClick < 0) {
            cursor = sentF;
            return;
        }
        if (yClick > getLastText().getY() + getLastText().getLayoutBounds().getHeight()) {
            cursor = sentB.prev;
            return;
        }
        setYstartend(yClick);

        TextNode chooseNode = start;
        double curStartX;
        double xDifference;
        double minXDifference = 999999;
        while (!start.equals(end)) {
            curStartX = start.text.getX() + Math.round(start.text.getLayoutBounds().getWidth());
            xDifference = Math.abs(xClick - curStartX);
            if (xDifference < minXDifference){
                minXDifference = xDifference;
                chooseNode = start;
                if (minXDifference == 0){
                    return;
                }
            }
            start = start.next;
        }

        cursor = chooseNode;
    }

    //set the start and end pointer to textnodes of the line that which is clicked
    public void setYstartend(double yClick){
        if (!newLines.contains(sentF)) {
            newLines.add(0, sentF);
        }
        newLines.add(sentB.prev);
        newLines.add(sentB.prev);
        Text height_text = new Text("");
        double charHeight = height_text.getLayoutBounds().getHeight();
        for (int i = 0; i < newLines.size(); i++){
            TextNode val = newLines.get(i);
            if (yClick - (val.text.getY() + charHeight) <= 0 && val.text.getX() == 5) {
                start = val;
                end = newLines.get(i + 1);
                return;
            }
        }
    }

    public void setCursor(TextNode where){
        cursor = where;
    }

    public ArrayList getNewLines(){
        return newLines;
    }


//Wrapping Methods

    //Gets the start letter of the word getting wordwrapped
    public TextNode getStartWord(TextNode endWord) {
        while (endWord.prev != null && endWord.prev.text.getX() != 5) {
            if (endWord.text.getText().equals(" ")){
                return endWord.next;
            }
            endWord = endWord.prev;
        }
        return cursor;
    }


//render methods

    public void resetRenderer(){
        renderer = sentF.next;
    }

    public TextNode getRenderer(){
        return renderer;
    }

    public void moreRenderer(){
        renderer = renderer.next;
    }

    public Text getRenderText(){
        return renderer.text;
    }

    public TextNode getSentB(){
        return sentB;
    }

    public Text getPriorText() {
        return renderer.prev.text;
    }



    public void rendererWrap(TextNode endWord){
        renderer = getStartWord(endWord);
    }

    //Finds out if should textwrap or word wrap by seeing if there is a space prior to the word thats getting wrapped
    public boolean isPriorSpace(TextNode renderer){
        return !getStartWord(renderer).equals(cursor);
    }


}


