package org.menzies.utils;

import java.util.ArrayList;
import java.util.List;

public class AutomaticDummyWorkerFactory {

    public static List<AutomaticDummyWorker> get(int number) {

        List<AutomaticDummyWorker> list = new ArrayList<>();

        for (int i = 0; i < number; i++) {

            String title = "File #" + (i + 1);
            long length = (long) (Math.random() * 10000L);

            boolean fail = Math.random() < 0.5;

            var worker = new AutomaticDummyWorker(title, length, fail);

            list.add(worker);
        }

        return list;
    }
}
