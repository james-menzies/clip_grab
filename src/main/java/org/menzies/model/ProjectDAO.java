package org.menzies.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.menzies.model.pojo.Project;

public class ProjectDAO {


    private static final SessionFactory DEFAULT_FACTORY;
    private final SessionFactory factory;


    static {
        try {
            Configuration config = new Configuration();
            DEFAULT_FACTORY = config.configure().buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public ProjectDAO() {

        this(DEFAULT_FACTORY);
    }

    public ProjectDAO(SessionFactory factory) {

        this.factory = factory;
    }


    // TODO: 13/04/2020 fill out implementation of class
    public boolean add(Project project) {

        Session session = factory.openSession();
        session.beginTransaction();


return true;

    }

    public void delete(Project project) {


    }

    public ObservableList<Project> getProjects() {

        return FXCollections.emptyObservableList();
    }

    /*
    This method will block all other methods until the task that is returned
    is complete. This is because an update will potentially alter the file
    directory, and it could take an arbitrarily long time. This gives the
    UI a chance to observe the progress of the task, whilst allowing runtime safety.
     */

    public Task<Void> overwriteProject(Project project) {

        return null;
    }

    public void saveProject(Project project) {
    }
}
