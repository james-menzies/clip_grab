package org.menzies.view;

import de.saxsys.javafx.test.JfxRunner;
import de.saxsys.javafx.test.TestInJfxThread;
import javafx.concurrent.Task;
import org.hibernate.annotations.Type;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JfxRunner.class)
public class TestTask {


    @Test
    @TestInJfxThread
    public void test() {

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                if (true) {
                    throw new Exception();
                }
                return null;
            }


            @Override
            protected void failed() {
                super.failed();
                System.out.println("Failed.");
            }
        };


        task.run();





    }


}
