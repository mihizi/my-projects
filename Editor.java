package editor;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.*;
import javafx.scene.text.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import java.util.ArrayList;
import java.io.*;
import java.util.List;


public class Editor extends Application {
    /** An EventHandler to handle keys that get pressed. */

    private static double WINDOW_WIDTH = 500;
    private static double WINDOW_HEIGHT = 500;
    private static final double LEFT_BOUND = 5;
    private static double RIGHT_BOUND = WINDOW_WIDTH - LEFT_BOUND;
    private int fontSize = 12;
    private String fontName = "Verdana";

    private final Rectangle movingCursor;
    public Group root = new Group();
    public Group rootP = new Group();
    ScrollBar scrollBar = new ScrollBar();
    double scrollBarWidth = scrollBar.getLayoutBounds().getWidth();


    public LinkedListDeque words = new LinkedListDeque();
    public ArrayList<undoredoholder> undo = new <undoredoholder>ArrayList();
    public ArrayList<undoredoholder> redo = new <undoredoholder>ArrayList();

    public Editor() {
        // Create a rectangle to surround the text that gets displayed.  Initialize it with a size
        // of 0, since there isn't any text yet.
        movingCursor = new Rectangle(5, 0);
    }

    public void render() {
        words.resetRenderer();
        Text height_text = new Text("");
        while (words.getRenderer() != words.getSentB()) {
            words.getRenderText().setFont(Font.font(fontName, fontSize));
            height_text.setFont(Font.font(fontName, fontSize));

            double textWidth = Math.round(words.getPriorText().getLayoutBounds().getWidth());
            double textHeight = Math.round(height_text.getLayoutBounds().getHeight());
            boolean shouldWordWrap = (words.isPriorSpace(words.getRenderer()) && (words.getPriorText().getX() + words.getPriorText().getLayoutBounds().getWidth() + words.getRenderText().getLayoutBounds().getWidth() > WINDOW_WIDTH - LEFT_BOUND - scrollBarWidth));
            if (!words.getRenderText().getText().equals(" ") && (words.getRenderText().getText().equals("\r\n") || (words.getPriorText().getX() + words.getPriorText().getLayoutBounds().getWidth() + words.getRenderText().getLayoutBounds().getWidth() > WINDOW_WIDTH - LEFT_BOUND - scrollBarWidth))){
                if (shouldWordWrap) {
                    words.rendererWrap(words.getRenderer());
                }
                words.getRenderText().setX(LEFT_BOUND);
                words.getRenderText().setY(words.getPriorText().getY() + textHeight);
                words.pointStart(words.getRenderer());
            } else {
                words.getRenderText().setX(words.getPriorText().getX() + textWidth);
                words.getRenderText().setY(words.getPriorText().getY());
            }
            words.moreRenderer();
        }

        scrollBar.setMax(words.getLastText().getY() + words.getLastText().getLayoutBounds().getHeight());

    }


    private void updateCursor() {
        double textWidth = Math.round(words.getCursorText().getLayoutBounds().getWidth());

        // Re-size and re-position the bounding box.
        movingCursor.setHeight(fontSize + 5);
        movingCursor.setWidth(1);

        // For rectangles, the position is the upper left hand corner.
        movingCursor.setX(words.getCursorText().getX() + textWidth);
        movingCursor.setY(words.getCursorText().getY());
    }


    private class MouseClickEventHandler implements EventHandler<MouseEvent> {
        /** A Text object that will be used to print the current mouse position. */
        Text positionText;

        MouseClickEventHandler(Group root) {
            // For now, since there's no mouse position yet, just create an empty Text object.
            positionText = new Text("");
            // We want the text to show up immediately above the position, so set the origin to be
            // VPos.BOTTOM (so the x-position we assign will be the position of the bottom of the
            // text).
            positionText.setTextOrigin(VPos.BOTTOM);

            // Add the positionText to root, so that it will be displayed on the screen.
            root.getChildren().add(positionText);
        }


        @Override
        public void handle(MouseEvent mouseEvent) {
            double mousePressedX = mouseEvent.getX();
            double mousePressedY = mouseEvent.getY();

            // Display text right above the click.
            positionText.setText("(" + mousePressedX + ", " + mousePressedY + ")");
            positionText.setX(mousePressedX);
            positionText.setY(mousePressedY);
            words.movecursorclick(mousePressedX, mousePressedY);
            render();
            updateCursor();

            //Helps to reset arraylist of new line pointers
            if (words.getNewLines().size() > 1) {
                words.getNewLines().remove(words.getNewLines().size() - 1);
                words.getNewLines().remove(words.getNewLines().size() - 1);
            }
        }

    }



    private class KeyEventHandler implements EventHandler<KeyEvent> {

        /** The Text to display on the screen. */

        public KeyEventHandler(final Group root, int windowWidth, int windowHeight) {
        }


        @Override
        public void handle(KeyEvent keyEvent) {
            if (keyEvent.isShortcutDown()) {
                if (keyEvent.getCode() == KeyCode.P) {
                    int x = (int) (Math.round(words.getCursorText().getX()) + (int)Math.round(words.getCursorText().getLayoutBounds().getWidth()));
                    int y = (int) words.getCursorText().getY();
                    System.out.println(x + ", " + y);

                }
                else if (keyEvent.getCode() == KeyCode.EQUALS){
                    fontSize += 4;
                    render();
                    updateCursor();
                }
                else if (keyEvent.getCode() == KeyCode.MINUS){
                    if(fontSize > 4)
                        fontSize -= 4;

                    render();
                    updateCursor();
                } else if (keyEvent.getCode() == KeyCode.Z){
                    if (undo.size() > 0){
                       undoredoholder actionToUndo = undo.remove(undo.size() -1);
                        if (actionToUndo.isInsert == true){
                            words.setCursor(actionToUndo.where);
                            Text removed = words.removeChar();
                            redo.add(new undoredoholder(words.getCursor(), false, removed));
                            root.getChildren().remove(removed);
                            render();
                            updateCursor();
                        } else {
                            words.setCursor(actionToUndo.where);
                            words.addChar(actionToUndo.text);
                            redo.add(new undoredoholder(words.getCursor(), true, actionToUndo.text));
                            root.getChildren().add(actionToUndo.text);
                            render();
                            updateCursor();


                        }
                        }


                } else if (keyEvent.getCode() == KeyCode.Y){
                    if (redo.size() > 0){
                        undoredoholder actionToUndo = redo.remove(redo.size() -1);
                        if (actionToUndo.isInsert == true){
                            words.setCursor(actionToUndo.where);
                            Text removed = words.removeChar();
                            undo.add(new undoredoholder(words.getCursor(), false, removed));
                            root.getChildren().remove(removed);
                            render();
                            updateCursor();
                        } else {
                            words.setCursor(actionToUndo.where);
                            words.addChar(actionToUndo.text);
                            undo.add(new undoredoholder(words.getCursor(), true, actionToUndo.text));
                            root.getChildren().add(actionToUndo.text);
                            render();
                            updateCursor();


                        }
                    }


                }



            } else if (keyEvent.getEventType() == KeyEvent.KEY_TYPED) {
                // Use the KEY_TYPED event rather than KEY_PRESSED for letter keys, because with
                // the KEY_TYPED event, javafx handles the "Shift" key and associated
                // capitalization.
                String characterTyped = keyEvent.getCharacter();
                Text charText = new Text(keyEvent.getCharacter());
                charText.setFont(Font.font(fontName, fontSize));
                double textWidth = Math.round(charText.getLayoutBounds().getWidth());
                charText.setTextOrigin(VPos.TOP);

                if (characterTyped.length() > 0 && characterTyped.charAt(0) != 8) {
                    if (characterTyped.equals(" ") && movingCursor.getX() + movingCursor.getLayoutBounds().getWidth() + charText.getLayoutBounds().getWidth() > WINDOW_WIDTH - LEFT_BOUND - scrollBarWidth) {
                            words.addChar(charText);
                    } else if(characterTyped.equals("\r")){ //handle enter
                        Text emptyLine = new Text("\r\n");
                        words.addChar(emptyLine);

                        render();
                        updateCursor();
                        undo.add(new undoredoholder(words.getCursor(), true, emptyLine));
                        redo.clear();
                        if (undo.size() >= 100){
                            undo.remove(0);
                        }

                    } else {

                        words.addChar(charText);
                        root.getChildren().add(charText);

                        render();

                        keyEvent.consume();
                        updateCursor();
                        undo.add(new undoredoholder(words.getCursor(), true, charText));
                        redo.clear();
                        if (undo.size() >= 100){
                            undo.remove(0);
                        }
                    }

                }


            } else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                KeyCode code = keyEvent.getCode();
                if (code == KeyCode.BACK_SPACE) {
                    Text removed = words.removeChar();
                    root.getChildren().remove(removed);
                    render();
                    updateCursor();
                    undo.add(new undoredoholder(words.getCursor(), false, removed));
                    redo.clear();
                    if (undo.size() >= 100) {
                        undo.remove(0);
                    }
                } else if (code == KeyCode.LEFT){
                    words.movecursorleft();
                    render();
                    updateCursor();
                } else if (code == KeyCode.RIGHT){
                    words.movecursorright();
                    render();
                    updateCursor();
                } else if (code == KeyCode.UP){
                    words.movecursorup();
                    render();
                    updateCursor();
                } else if (code == KeyCode.DOWN){
                    words.movecursordown();
                    render();
                    updateCursor();
                }

            }
        }

    }

    /** An EventHandler to handle changing the color of the rectangle. */
    private class RectangleBlinkEventHandler implements EventHandler<ActionEvent> {
        private int currentColorIndex = 0;
        private Color[] boxColors =
                {Color.BLACK, Color.WHITE};

        RectangleBlinkEventHandler() {
            // Set the color to be the first color in the list.
            changeColor();
        }

        private void changeColor() {
            movingCursor.setFill(boxColors[currentColorIndex]);
            currentColorIndex = (currentColorIndex + 1) % boxColors.length;
        }

        @Override
        public void handle(ActionEvent event) {
            changeColor();
        }
    }

    /** Makes the text bounding box change color periodically. */
    public void makeRectangleColorChange() {
        // Create a Timeline that will call the "handle" function of RectangleBlinkEventHandler
        // every 1 second.
        final Timeline timeline = new Timeline();
        // The rectangle should continue blinking forever.
        timeline.setCycleCount(Timeline.INDEFINITE);
        RectangleBlinkEventHandler cursorChange = new RectangleBlinkEventHandler();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), cursorChange);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    public void readFile(List givenArgs) {
        if (givenArgs.isEmpty()) {
            return;
        }

        String fileName = (String) givenArgs.get(0);
        try {
            File inputFile = new File(fileName);

            // Check to make sure that the input file exists!
            if (!inputFile.exists()) {
                inputFile.createNewFile();
                System.out.println("Successfully created file " + fileName);
            }
            FileReader reader = new FileReader(inputFile);
            BufferedReader bufferedReader = new BufferedReader(reader);

            int intRead = -1;

            // Keep reading from the file input read() returns -1, which means the end of the file
            // was reached.
            while ((intRead = bufferedReader.read()) != -1) {
                // The integer read can be cast to a char, because we're assuming ASCII.
                char charRead = (char) intRead;
                String charStr = Character.toString(charRead);
                Text charStrText = new Text(charStr);
                words.addChar(charStrText);
                root.getChildren().add(charStrText);
            }

            System.out.println("Successfully opened file " + fileName);

            // Close the reader
            bufferedReader.close();
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File not found! Exception was: " + fileNotFoundException);
        } catch (IOException ioException) {
            System.out.println("Error when copying; exception was: " + ioException);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        Parameters param = getParameters();
        List paramStr = param.getRaw();
        readFile(paramStr);

        rootP.getChildren().add(root);

        int windowWidth = 500;
        int windowHeight = 500;
        Scene scene = new Scene(rootP, windowWidth, windowHeight, Color.WHITE);

        // To get information about what keys the user is pressing, create an EventHandler.
        // EventHandler subclasses must override the "handle" function, which will be called
        // by javafx.
        EventHandler<KeyEvent> keyEventHandler =
                new KeyEventHandler(root, windowWidth, windowHeight);
        // Register the event handler to be called for all KEY_PRESSED and KEY_TYPED events.
        scene.setOnKeyTyped(keyEventHandler);
        scene.setOnKeyPressed(keyEventHandler);
        scene.setOnMouseClicked(new MouseClickEventHandler(root));

        //set up the scrollbar
        scrollBar.setOrientation(Orientation.VERTICAL);
        scrollBar.setMin(0);
        scrollBar.setMax(words.getLastText().getY() + words.getLastText().getLayoutBounds().getHeight());
        scrollBar.setPrefHeight(WINDOW_HEIGHT);
        scrollBar.setLayoutX(WINDOW_WIDTH - scrollBar.getLayoutBounds().getWidth());
        root.getChildren().add(scrollBar);

        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldScreenWidth,
                    Number newScreenWidth) {
                WINDOW_WIDTH = (Double) newScreenWidth;
                scrollBar.setLayoutX(WINDOW_WIDTH - scrollBar.getLayoutBounds().getWidth());
                render();
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>(){
            @Override public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldScreenHeight,
                    Number newScreenHeight) {
                WINDOW_HEIGHT = (Double) newScreenHeight;
                scrollBar.setPrefHeight(WINDOW_HEIGHT);
                    render();
            }
        });


        //render the given file
        if (!paramStr.isEmpty()){
            render();
        }

        // All new Nodes need to be added to the root in order to be displayed.
        root.getChildren().add(movingCursor);
        makeRectangleColorChange();

        primaryStage.setTitle("Mihir's Editor");

        // This is boilerplate, necessary to setup the window where things are displayed.
        primaryStage.setScene(scene);
        primaryStage.show();
    }




    public static void main(String[] args) {
        launch(args);
    }
}
