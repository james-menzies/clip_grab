package org.menzies.view;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.menzies.model.library.Library;
import org.menzies.model.pojo.Project;
import org.menzies.model.service.parsing.FailedParseException;
import org.menzies.utils.JFXUtil;
import org.menzies.viewmodel.BatchDownloadVM;
import org.menzies.viewmodel.ProjectSelectVM;

import java.io.File;
import java.io.IOException;

import static org.menzies.viewmodel.ProjectSelectVM.Selection;

public class ProjectSelectView implements View<ProjectSelectVM> {

    private ProjectSelectVM vm;
    private ToggleGroup group;
    private Alert failedRun;
    private ScreenController controller;

    @FXML
    private ListView<Project> savedProjects;

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

    @FXML
    private Label chosenDirectory;

    @FXML
    private TextArea libraryDescription;

    public ProjectSelectView() {

        group = new ToggleGroup();
        initializeFailedRun();
    }

    @FXML
    public void initialize() {
        newDownload.setToggleGroup(group);
        existingDownload.setToggleGroup(group);
        libraryChoiceBox.setItems(FXCollections.observableArrayList(Library.values()));
        libraryDescription.setEditable(false);
    }

    private void initializeFailedRun() {

        failedRun = new Alert(Alert.AlertType.ERROR);
    }

    @Override
    public void setVM(ProjectSelectVM vm) {

        this.vm = vm;

        newDownload.setOnAction(e -> {
            vm.setSelection(Selection.NEW);
            savedProjects.setDisable(true);
            chooseFolder.setDisable(false);
            libraryChoiceBox.setDisable(false);
            libraryDescription.setDisable(false);
        });
        existingDownload.setOnAction(e -> {
            vm.setSelection(Selection.EXISTING);
            savedProjects.setDisable(false);
            chooseFolder.setDisable(true);
            libraryChoiceBox.setDisable(true);
            libraryDescription.setDisable(true);

        });
        savedProjects.setItems(vm.savedProjectsProperty());

        runButton.setOnAction(e -> onRunAction());

        chooseFolder.setOnAction(e -> {
            DirectoryChooser chooser = new DirectoryChooser();

            chooser.setTitle("Choose Root Directory for Download");
            chooser.setInitialDirectory( new File(System.getProperty("user.home")));

            Window window = chooseFolder.getScene().getWindow();
            File selection = chooser.showDialog(window);

            chosenDirectory.setText(selection.getPath());
            vm.selectedRootDirProperty().set(selection);
        });


        vm.selectedLibraryProperty().bind(libraryChoiceBox.valueProperty());
        vm.existingSelectionProperty().bind(savedProjects.getSelectionModel().selectedItemProperty());

        if (savedProjects.getItems().size() > 0) {
            existingDownload.fire();
            savedProjects.getSelectionModel().select(0);
        }
        else {
            newDownload.fire();
            libraryChoiceBox.getSelectionModel().select(0);
        }

    }

    @Override
    public void setScreenController(ScreenController controller) {
        this.controller = controller;
    }

    private void onRunAction() {

        BatchDownloadVM vm;

        try {
            vm = this.vm.handleRun();

            Node node = JFXUtil.getRoot(vm, "/org/menzies/view/BatchDownload.fxml");
            controller.getStage().setOnCloseRequest(e -> vm.handleHardShutDown());
            controller.changeView(node);

        } catch (FailedParseException e) {
            failedRun.setContentText(e.getMessage());
            failedRun.showAndWait();
        } catch (IOException | NullPointerException e) {
            failedRun.setTitle("Program Error");
            failedRun.setHeaderText("An internal problem has occurred.");
            failedRun.setContentText("Contact the developer as.");
            failedRun.showAndWait();
            e.printStackTrace();
        }
    }
}
