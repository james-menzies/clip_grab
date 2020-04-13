package org.menzies.model;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class ProjectDAO {

    // TODO: 13/04/2020 fill out implementation of class 
    public void add(Project project) {

    }

    public void delete(Project project) {


    }

    public ObservableList<Project> getProjects() {

        return null;
    }

    /*
    This method will block all other methods until the task that is returned
    is complete. This is because an update will potentially alter the file
    directory, and it could take an arbitrarily long time. This gives the
    UI a chance to observe the progress of the task, whilst allowing runtime safety.
     */

    public Task<Void> updateProject(Project) {
        return null;
    }



}
