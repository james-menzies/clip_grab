package org.menzies.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.menzies.view.View;

import java.io.IOException;

public class JFXUtil {

    public static Stage createStage(Parent root) {

        Stage stage = new Stage();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        return stage;
    }


    public static <T> Parent getRoot(T viewModel, String resourceLocation) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(JFXUtil.class.getResource(resourceLocation));

        Parent root = loader.load();
        View<T> view = loader.getController();
        view.setVM(viewModel);
        return root;


    }
}
