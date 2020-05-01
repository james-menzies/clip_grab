package org.menzies.model;

import javafx.concurrent.Task;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.menzies.model.pojo.LibraryElement;
import org.menzies.model.pojo.Project;

import javax.persistence.NamedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ProjectDAO {




    private static final SessionFactory DEFAULT_FACTORY;
    private final SessionFactory factory;
    private final List<LibraryElement> elements;


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
        elements = new ArrayList<>();
    }


    public boolean add(Project project) {

        Session session = factory.openSession();
        session.beginTransaction();

        session.persist(project);
        session.getTransaction().commit();

        session.close();
        return true;
    }

    public int getDownloadTotal(Project project) {

        Session session = factory.openSession();
        Long total = 0L;

        try {
            total = session.createQuery("select count(*) " +
                    "from Project p " +
                    "join p.elements" +
                    " where p = :project", Long.class)
                    .setParameter("project", project)
                    .getSingleResult();
        } catch (Exception e) {
            System.out.println("Error processing query in retrieving download total");
        } finally {
            session.close();
        }
        return total.intValue();
    }

    public List<LibraryElement> getRemainingDownloads(Project project) {


        Session session = factory.openSession();
        session.beginTransaction();

        List<LibraryElement> list = session.createQuery(
                "select le " +
                "from Project p " +
                "join p.elements le " +
                "with p = :project " +
                "and le.completed = false", LibraryElement.class)
                .setParameter("project", project)
                .list();

        session.getTransaction().commit();
        session.close();

        return list;
    }

    public List<Project> getProjects() {

        Session session = factory.openSession();
        session.beginTransaction();

        List<Project> list = session.createQuery("from Project", Project.class).list();

        session.close();

        return list;
    }

    public void delete(Project project) {

        Session session = factory.openSession();
        session.beginTransaction();

        try {
            session.remove(project);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
    }


    public Task<Void> updateProject(Project project) {

        /*
        Get the element already attached to the given project's id. Create a new task that
        passes both lists to a transfer service. Then lock the project until the task returns.
        Then return the task instance.
         */

        return null;
    }

    public void markElementComplete(LibraryElement element) {

        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();

        try {

            session.createQuery("update LibraryElement le set completed = true where le = :le")
                    .setParameter("le", element)
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

}
