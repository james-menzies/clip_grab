package org.menzies.model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.menzies.model.pojo.LibraryElement;

import java.util.List;

public class ProjectDAOTest {


    private SessionFactory factory;
    private Session session;
    private Transaction tx;

    @Before
    public void before()  {

        factory = new Configuration()
                .configure()
                .buildSessionFactory();

        session = factory.openSession();
        tx = session.beginTransaction();

    }

    @Test
    public void runCustomTest() {


    }


    @After
    public void after() {

        session.close();
    }
}