import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.menzies.model.ProjectDAO;
import org.menzies.utils.JFXUtil;
import org.menzies.view.ScreenController;
import org.menzies.viewmodel.ProjectSelectVM;


public class MainApp extends Application {


    public static void main(String... args) {

        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("BBC Sounds Batch Download");

        Image image = new Image(MainApp.class.getResourceAsStream("/AppIcon.png"));
        primaryStage.getIcons().add(image);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(new Menu("File"));

        ScreenController controller = new ScreenController(menuBar, primaryStage);

        ProjectDAO dao = new ProjectDAO();
        ProjectSelectVM vm = new ProjectSelectVM(dao);

        Parent root = JFXUtil.getRoot(vm, "/org/menzies/view/ProjectSelect.fxml", controller);
        controller.changeView(root);

        primaryStage.show();
    }
}
