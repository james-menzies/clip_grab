package org.menzies.viewmodel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import org.menzies.model.LazyList;
import org.menzies.model.ProjectDAO;
import org.menzies.model.library.Library;
import org.menzies.model.pojo.LibraryElement;
import org.menzies.model.pojo.Project;
import org.menzies.model.service.download.DownloadTask;
import org.menzies.model.service.parsing.FailedParseException;
import org.menzies.model.service.tagging.TaggingService;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProjectSelectVM {


    private ProjectDAO dao;
    private ObjectProperty<Library> selectedLibrary;
    private ObjectProperty<File> selectedRootDir;
    private ObjectProperty<Selection> selection;
    private ObjectProperty<Project> existingSelection;
    private ObservableList<Project> savedProjects;

    public ObjectProperty<Library> selectedLibraryProperty() {
        return selectedLibrary;
    }
    public ObjectProperty<File> selectedRootDirProperty() {
        return selectedRootDir;
    }
    public ObservableList<Project> savedProjectsProperty() {

        return savedProjects;
    }

    public void setSelection(Selection selection) {
        this.selection.set(selection);
    }

    public ObjectProperty<Project> existingSelectionProperty() {
        return existingSelection;
    }

    public enum Selection {NEW, EXISTING}

    public ProjectSelectVM(ProjectDAO dao) {
        this.dao = dao;
        selectedLibrary = new SimpleObjectProperty<>();
        selectedRootDir = new SimpleObjectProperty<>();
        selection = new SimpleObjectProperty<>();
        existingSelection = new SimpleObjectProperty<>();
        savedProjects = FXCollections.observableArrayList
                (Collections.unmodifiableList(dao.getProjects()));
    }

    public BatchDownloadVM<?> handleRun() throws FailedParseException {

        Project project = getSelectedProject();
        return initializeBatchDownloadVM(project);
    }

    private Project getSelectedProject() throws FailedParseException {

        if (selection.get() == Selection.NEW) {
            Project project = new Project(selectedLibrary.get(),
                    selectedRootDir.get().getPath());
            dao.add(project);
            return project;
        } else return existingSelection.get();
    }

    private BatchDownloadVM<?> initializeBatchDownloadVM(Project project) {


        List<LibraryElement> elements = dao.getRemainingDownloads(project);

        Function<Integer, DownloadTask[]> function = integer -> {
            return elements.stream()
                    .skip(integer)
                    .limit(20)
                    .map(element -> {
                        DownloadTask task = new DownloadTask(element);
                        task.addPostDownloadTask(file -> new TaggingService().tagFile(element), "Tagging file");
                        task.addPostDownloadTask(file -> dao.markElementComplete(element), "Updating database");
                        task.setOnSucceeded(this::handle);
                        return task;
                    })
                    .toArray(DownloadTask[]::new);
        };


        List<DownloadTask> tasks = new LazyList<DownloadTask>(elements.size(), function);

        var vm = new BatchDownloadVM<>(tasks);
        vm.setDownloadTotal((int) dao.getDownloadTotal(project));


        return vm;
    }


    private void handle(WorkerStateEvent event) {
        System.out.println("completed");
    }

}
