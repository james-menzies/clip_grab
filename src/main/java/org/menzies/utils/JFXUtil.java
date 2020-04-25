package org.menzies.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.menzies.view.ScreenController;
import org.menzies.view.View;

import java.io.IOException;

public class JFXUtil {

    public static Stage createStage(Parent root) {

        Stage stage = new Stage();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        return stage;
    }


    public static <T> Parent getRoot(T viewModel, String resource, ScreenController controller) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(JFXUtil.class.getResource(resource));

        Parent root = loader.load();
        View<T> view = loader.getController();
        view.setVM(viewModel);

        if (controller != null) {
            view.setScreenController(controller);
        }
        return root;
    }

    public static <T> Parent getRoot(T viewModel, String resource) throws IOException {

        return getRoot(viewModel, resource, null);
    }
}

