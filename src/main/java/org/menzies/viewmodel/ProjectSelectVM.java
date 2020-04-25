package org.menzies.viewmodel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import org.menzies.model.ProjectDAO;
import org.menzies.model.library.Library;
import org.menzies.model.pojo.Project;
import org.menzies.model.service.download.DownloadTask;
import org.menzies.model.service.parsing.FailedParseException;
import org.menzies.model.service.tagging.TaggingService;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
        savedProjects = dao.getProjects();
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
        }
        else return existingSelection.get();
    }

    private BatchDownloadVM<?> initializeBatchDownloadVM(Project project) {


        ExecutorService executorService = initializeExecutorService();

        Set<DownloadTask> tasks = project.getElements()
                .stream()
                .filter(libraryElement -> !libraryElement.isCompleted())
                .map( libraryElement -> {
                    DownloadTask task = new DownloadTask(libraryElement);
                    task.addPostDownloadTask(file -> new TaggingService().tagFile(libraryElement));
                    task.setOnSucceeded(v -> libraryElement.setCompleted(true));
                    return task;
                })
                .collect(Collectors.toSet());

        var vm = new BatchDownloadVM<>(executorService, tasks);
        vm.runningProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                dao.saveProject(project);
            }
        });

        return vm;
    }

    private ExecutorService initializeExecutorService() {

        return new ThreadPoolExecutor(5, 5, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
                new ThreadPoolExecutor.DiscardPolicy()) {
            @Override
            public void shutdown() {
                super.shutdown();
                getQueue().clear();
            }
        };
    }
}
