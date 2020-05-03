package org.menzies;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.menzies.model.ProjectDAO;
import org.menzies.utils.JFXUtil;
import org.menzies.view.ScreenController;
import org.menzies.viewmodel.ProjectSelectVM;


public class MainApp extends Application {


    private static Stage stage;

    public static void main(String... args) {

        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("BBC Sounds Batch Download");

        Image image = new Image(MainApp.class.getResourceAsStream("/AppIcon.png"));
        primaryStage.getIcons().add(image);

        MenuBar menuBar = new MenuBar();

        Menu menu = new Menu("Help");
        MenuItem item = new MenuItem("About");
        menuBar.getMenus().add(menu);
        menu.getItems().add(item);

        item.setOnAction(e -> showAboutInfo());

        ScreenController controller = new ScreenController(menuBar, primaryStage);

        ProjectDAO dao = new ProjectDAO();
        ProjectSelectVM vm = new ProjectSelectVM(dao);

        Parent root = JFXUtil.getRoot(vm, "/org/menzies/view/ProjectSelect.fxml", controller);
        controller.changeView(root);

        primaryStage.show();
    }

    private static void showAboutInfo() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(stage);
        alert.setTitle("About");
        alert.setHeaderText("How to use this program.");
        alert.setContentText("To start a new batch download, select a library and root directory" +
                ", making sure there is enough room in the folder for total estimated size. The program" +
                " will track your downloaded files automatically. To continue with a previous download simply " +
                "select a previous configuration and run. \n\n" +
                "Deleting a saved project will remove all saved information, however if you wish to remove " +
                "the downloaded files this will have to be done manually.\n\n" +
                "If you would like to stop downloading files but complete downloads that have already begun, " +
                "click \"Shutdown\" otherwise closing the window or clicking \"Hard Shutdown\" will terminate the" +
                " download immediately, deleting any partially downloaded files. Happy downloading");
        alert.showAndWait();
    }
}
