package org.menzies.view;

public interface View<T> {

    void setVM(T vm);

    default void setScreenController(ScreenController controller) {}

}
