package org.menzies.model;

import java.util.AbstractList;
import java.util.function.Function;

public class LazyList<E> extends AbstractList<E> {

    /*
    This class is designed to handle the lazy loading of elements as they come through
    from the database. It is intended that this class be used with an iterator, random
    access would be very slow.
     */

    private final int size;
    E[] loadedInstances;
    int startOfLoadedIndex;
    int endOfLoadedIndex;
    private final Function<Integer, E[]> retrieval;

    public LazyList(int size, Function<Integer, E[]> retrieval) {
        this.size = size;
        this.retrieval = retrieval;

        //load the first x instances
       refreshLoaded(0);
    }


    @Override
    public E get(int index) {
        if (index >= size) {
            return null;
        }


        if (index > endOfLoadedIndex || index < startOfLoadedIndex) {
            refreshLoaded(index);
        }

        return loadedInstances[index - startOfLoadedIndex];
    }

    @Override
    public int size() {
        return size;
    }

    private void refreshLoaded(int startOfLoadedIndex) {

        loadedInstances = retrieval.apply(startOfLoadedIndex);
        System.out.println("Refreshing list");
        this.startOfLoadedIndex = startOfLoadedIndex;
        endOfLoadedIndex = loadedInstances.length + startOfLoadedIndex - 1;

    }
}
