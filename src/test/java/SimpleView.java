import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.menzies.view.View;

public class SimpleView implements View<StringProperty> {

    @FXML
    private Label label;

    @Override
    public void setVM(StringProperty vm) {
        label.textProperty().bind(vm);
    }
}
