package org.menzies.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import org.menzies.model.library.Library;
import org.menzies.model.service.parsing.FailedParseException;
import org.menzies.utils.JFXUtil;
import org.menzies.viewmodel.BatchDownloadVM;
import org.menzies.viewmodel.ProjectSelectVM;

import java.io.IOException;

import static org.menzies.viewmodel.ProjectSelectVM.Selection;

public class ProjectSelectView implements View<ProjectSelectVM> {


    private ProjectSelectVM vm;
    private ToggleGroup group;
    private Alert failedRun;
    private DirectoryChooser chooser;

    @FXML
    private ListView savedProjects;

    @FXML
    private Button chooseFolder;

    @FXML
    private ChoiceBox<Library> libraryChoiceBox;

    @FXML
    private Button runButton;

    @FXML
    private RadioButton newDownload;

    @FXML
    private RadioButton existingDownload;

    public ProjectSelectView() {

        group = new ToggleGroup();

        newDownload.setToggleGroup(group);
        existingDownload.setToggleGroup(group);
        initializeFailedRun();

    }

    private void initializeFailedRun() {

        failedRun = new Alert(Alert.AlertType.ERROR);
        failedRun.setTitle("Could not begin project");
        failedRun.setHeaderText("Problem in creation of new project");
    }

    @Override
    public void setVM(ProjectSelectVM vm) {

        this.vm = vm;

        newDownload.setOnAction(e -> vm.selectionProperty().set(Selection.NEW));
        existingDownload.setOnAction(e -> vm.selectionProperty().set(Selection.EXISTING));

        runButton.setOnAction(e -> onRunAction());


    }

    private void onRunAction() {

        BatchDownloadVM vm;

        try {
            vm = this.vm.handleRun();
            JFXUtil.getRoot(vm, "/org/menzies/view/ProjectSelect.fxml");
        } catch (FailedParseException e) {
            failedRun.setContentText(e.getMessage());
        } catch (IOException e) {
            failedRun.setContentText("Internal problem rendering next screen");
        }
    }




}
