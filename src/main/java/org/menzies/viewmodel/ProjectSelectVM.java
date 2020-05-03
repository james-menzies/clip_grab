package org.menzies.viewmodel;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProjectSelectVM {


    private ProjectDAO dao;
    private ObjectProperty<Library> selectedLibrary;
    private ObjectProperty<File> selectedRootDir;
    private ObjectProperty<Selection> selection;
    private ObjectProperty<Project> existingSelection;
    private ObservableList<Project> savedProjects;
    private ReadOnlyStringWrapper libraryDescription;

    public ObjectProperty<Library> selectedLibraryProperty() {
        return selectedLibrary;
    }
    public ObjectProperty<File> selectedRootDirProperty() {
        return selectedRootDir;
    }
    public ObservableList<Project> savedProjectsProperty() {

        return savedProjects;
    }

    public ReadOnlyStringProperty libraryDescriptionProperty() {
        return libraryDescription.getReadOnlyProperty();
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

        libraryDescription = new ReadOnlyStringWrapper();
        libraryDescription.bind(Bindings.createStringBinding( () -> {

            if (selectedLibrary.get() != null) {
                return selectedLibrary.get().getConfig().getDescription();
            } else return "";

        }, selectedLibrary));
    }

    public Task<BatchDownloadVM<?>> handleRun()  {

        Task<BatchDownloadVM<?>> task =  new Task<BatchDownloadVM<?>>() {

            @Override
            protected BatchDownloadVM<?> call() throws Exception {


                Project project = null;
                try {
                    project = getSelectedProject();
                } catch (FailedParseException e) {

                    updateMessage(e.getMessage());
                    throw new Exception();

                } catch (NullPointerException e) {
                    updateMessage("Not all variables set.");
                    throw new Exception();
                }
                return initializeBatchDownloadVM(project);
            }
        };

        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(task);
        service.shutdown();

        return task;
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
                        return task;
                    })
                    .toArray(DownloadTask[]::new);
        };

        List<DownloadTask> tasks = new LazyList<DownloadTask>(elements.size(), function);

        var vm = new BatchDownloadVM<>(tasks);
        vm.setDownloadTotal((int) dao.getDownloadTotal(project));
        return vm;
    }

    public void handleDelete() {

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Project project = existingSelection.get();
                savedProjects.remove(project);
                dao.delete(project);
                return null;
            }
        };

        ExecutorService service = Executors.newSingleThreadExecutor();

        service.submit(task);
        service.shutdown();
    }


}
