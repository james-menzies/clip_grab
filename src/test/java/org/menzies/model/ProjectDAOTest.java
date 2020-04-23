package org.menzies.model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.menzies.model.library.Library;
import org.menzies.model.pojo.Project;
import org.menzies.model.service.parsing.FailedParseException;

import static org.junit.Assert.*;

public class ProjectDAOTest {


    private SessionFactory factory;

    @Before
    public void before() throws FailedParseException {

        factory = new Configuration()
                .configure()
                .buildSessionFactory();

        Session session = factory.openSession();

        session.beginTransaction();



        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void checkBefore() {

        System.out.println("Reached test");
    }
}