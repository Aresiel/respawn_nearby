package io.github.nopeless.project2;

import java.util.List;

public interface IRotatingArrayList<E> extends List<E> {
    // rotating array list specific
    void rotateLeft();

    void rotateRight();

    // extra challenge features

    /**
     * Delete everything from start
     */
    void splice(int start);

    void splice(int start, int deleteCount);

    void splice(int start, int deleteCount, List<? extends E> items);
}