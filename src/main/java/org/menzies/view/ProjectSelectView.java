package org.menzies.view;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Popup;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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

    @FXML
    private Button deleteProject;

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
        libraryDescription.setWrapText(true);
        deleteProject.disableProperty().bind(Bindings.or(newDownload.selectedProperty(),
                Bindings.isNull(savedProjects.getSelectionModel().selectedItemProperty())));
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

        libraryDescription.textProperty().bind(vm.libraryDescriptionProperty());

        runButton.setOnAction(e -> onRunAction());

        deleteProject.setOnAction(e -> vm.handleDelete());

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
        controller.getStage().setResizable(false);

    }

    private void onRunAction() {

        Task<BatchDownloadVM<?>> task;

        BorderPane pane = new BorderPane();

        pane.setPrefHeight(150);
        pane.setPrefWidth(250);
        pane.setCenter(new Label("Fetching downloads..."));
        pane.setStyle("-fx-background-color: white;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 2;");


        ProgressBar bar = new ProgressBar();
        bar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        bar.setPrefWidth(230);
        pane.setPadding(new Insets(20));
        BorderPane.setAlignment(bar, Pos.CENTER);

        pane.setBottom(bar);

        Popup popup = new Popup();
        popup.getContent().add(pane);
        popup.show(controller.getStage());


        task = vm.handleRun();
        task.setOnSucceeded(e -> {

            popup.hide();
            BatchDownloadVM<?> vm;

            try {
               vm = task.get();
                Node node = JFXUtil.getRoot(vm, "/org/menzies/view/BatchDownload.fxml");
                controller.getStage().setOnCloseRequest(e1 -> vm.handleHardShutDown());
                controller.changeView(node);
            } catch (InterruptedException |ExecutionException | IOException ex) {
                showInternalError(ex);
            }
        });

        task.setOnFailed( e -> {

            popup.hide();
            failedRun.setTitle("Error: Cannot Proceed with Download");
            failedRun.setHeaderText("Please check the following:");
            failedRun.setContentText(task.getMessage());
            failedRun.showAndWait();
        });
    }

    private void showInternalError(Throwable ex) {
        failedRun.setTitle("Program Error");
        failedRun.setHeaderText("An internal problem has occurred.");
        failedRun.setContentText("Contact the developer.");
        ex.printStackTrace();
        failedRun.showAndWait();
    }
}
