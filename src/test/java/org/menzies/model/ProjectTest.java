package org.menzies.model;

import org.junit.Test;
import org.menzies.model.library.Library;
import org.menzies.model.service.parsing.FailedParseException;

import java.io.File;

import static org.junit.Assert.*;

public class ProjectTest {


    @Test
    public void generateDefaultBBC()  {




        Project project;

        try {
            project = new Project(Library.BBC, "C:/");

            System.out.println(project.getElements());
        } catch (FailedParseException e) {
            e.printStackTrace();
        }



    }

}