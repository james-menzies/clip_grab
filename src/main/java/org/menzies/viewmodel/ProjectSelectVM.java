package org.menzies.viewmodel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import org.menzies.model.pojo.Project;
import org.menzies.model.ProjectDAO;
import org.menzies.model.library.Library;
import org.menzies.model.service.parsing.FailedParseException;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ProjectSelectVM {


    private ProjectDAO dao;

    private ObjectProperty<Library> selectedLibrary;
    private ObjectProperty<File> selectedRootDir;
    private ObjectProperty<Selection> selection;
    private ObjectProperty<Project> existingSelection;
    private ObservableList<Project> savedProjects;

    public Library getSelectedLibrary() {
        return selectedLibrary.get();
    }

    public ObjectProperty<Library> selectedLibraryProperty() {
        return selectedLibrary;
    }


    public ObjectProperty<File> selectedRootDirProperty() {
        return selectedRootDir;
    }


    public ObjectProperty<Selection> selectionProperty() {
        return selection;
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

    public BatchDownloadVM handleRun() throws FailedParseException {

        ExecutorService service = new ThreadPoolExecutor(5, 5, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
                new ThreadPoolExecutor.DiscardPolicy()) {
            @Override
            public void shutdown() {
                super.shutdown();
                getQueue().clear();
            }
        };
        Project project;

        if (selection.get() == Selection.NEW) {

            project = new Project(selectedLibrary.get(), selectedRootDir.get().getPath());
        } else {
            project = existingSelection.get();
        }

        return new BatchDownloadVM(service, project);
    }


}
