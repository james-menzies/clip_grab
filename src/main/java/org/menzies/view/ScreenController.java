package org.menzies.view;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class ScreenController {

    private BorderPane root;
    private Stage stage;

    public ScreenController(MenuBar menuBar, Stage stage) {

        root = new BorderPane();
        root.setTop(menuBar);

        this.stage = stage;
        stage.setScene(new Scene(root));
    }


    public void changeView(Node node) {
        root.setCenter(node);
        stage.sizeToScene();
    }

    public Stage getStage() {
        return stage;
    }
}
