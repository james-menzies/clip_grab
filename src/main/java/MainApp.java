import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.menzies.model.library.Library;
import org.menzies.model.library.LibraryConfig;

import java.io.File;


public class MainApp extends Application {


    public static void main(String... args) {

        Application.launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("BBC Sounds Batch Download");
        BorderPane root = new BorderPane();
        BorderPane dynamicScene = new BorderPane();
        root.setCenter(dynamicScene);


        primaryStage.setScene(new Scene(root));
        primaryStage.show();




    }
}
