package io.github.nopeless.project2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RotatingArrayList<E> implements IRotatingArrayList<E> {

    private static final int ARRAY_DEFAULT_SIZE = 4;
    private E[] data;
    private int size;
    private int offset;

    @SuppressWarnings("unchecked")
    public RotatingArrayList() {
        data = (E[]) (new Object[ARRAY_DEFAULT_SIZE]);
        size = 0;
        offset = 0;
    }

    /**
     * Easy way to copy another RotatingArrayList
     */
    public RotatingArrayList(RotatingArrayList<E> arr) {
        data = arr.data.clone();
        size = arr.size;
        offset = arr.offset;
    }

    @SuppressWarnings("unchecked cast")
    public RotatingArrayList(List<E> items) {
        data = (E[]) (new Object[_getMinimalPowerTwo(items.size())]);
        // copy data over
        for (int i = 0; i < items.size(); i++) {
            data[i] = items.get(i);
        }

        size = items.size();
        offset = 0;
    }

    private int _getMinimalPowerTwo(int size) {
        int res = 1;

        while (res < size) res <<= 1;

        return res;
    }

    public Stream<E> stream() {
        return Stream.concat(
                Arrays.stream(data, offset, size),
                Arrays.stream(data, 0, offset)
        );
    }

    public String toString() {
        return "[" + stream().map(o -> o == null ? "<null>" : o.toString()).collect(Collectors.joining(", ")) + "]";
    }

    /**
     * Only used for testing
     */
    public String __internalData() {
        var s = "";

        for (int i = 0; i < data.length; i++) {
            boolean inBounds = i < size;

            var c = inBounds ? data[i].toString() : "_";

            if (c.length() != 1) throw new RuntimeException(
                    "Cannot create internal representation of data due to non 1 character length elements"
            );

            s += c;
        }

        return size + "/" + data.length + " " + offset + ":" + s;
    }


    /**
     * Returns the size of this list (from the user's perspective).
     */
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        for (int i = 0; i < size; i++) {
            if (data[i].equals(o)) return true;
        }

        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return stream().iterator();
    }

    @Override
    public E[] toArray() {
        @SuppressWarnings("unchecked")
        var dataCopy = (E[]) new Object[size];

        System.arraycopy(data, offset, dataCopy, 0, size - offset);
        System.arraycopy(data, 0, dataCopy, size - offset, offset);

        return dataCopy;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new RuntimeException("Not implemented");
    }


    @Override
    public boolean add(E o) {
        add(size, o);

        return true;
    }

    @Override
    public void add(int index, E element) {
        addAll(index, List.of(element));
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return addAll(size, c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        splice(index, 0, List.copyOf(c));

        return true;
    }

    @Override
    public boolean remove(Object o) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void clear() {
        offset = 0;
        size = 0;
    }

    @Override
    public boolean retainAll(Collection c) {
        int ap = 0;
        for (int i = 0; i < size; i++) {
            if (c.contains(data[i])) {
                data[ap++] = data[i];
            }
        }

        size = ap;

        return true;
    }

    @Override
    public boolean removeAll(Collection c) {
        int ap = 0;
        for (int i = 0; i < size; i++) {
            if (!c.contains(data[i])) {
                data[ap++] = data[i];
            }
        }

        size = ap;

        return true;
    }

    @Override
    public boolean containsAll(Collection c) {
        for (var el : c) {
            if (!contains(el)) return false;
        }

        return true;
    }


    private int actualIndex(int pos) {
        int p = pos + offset;
        if (p >= size) p -= size;

        return p;
    }

    /**
     * Returns the element at index [pos] in the list.  You may assume pos
     * is a valid index in the list.
     */
    public E get(int pos) {
        return data[actualIndex(pos)];
    }

    @Override
    public E remove(int index) {
        E tmp = get(index);
        splice(index, 1);
        return tmp;
    }


    public E set(int pos, E value) {
        if (pos < 0 || pos >= size) throw new IndexOutOfBoundsException();

        return data[actualIndex(pos)] = value;
    }

    @Override
    public void splice(int start) {
        splice(start, 0);
    }

    @Override
    public void splice(int start, int deleteCount) {
        splice(start, deleteCount, List.of());
    }

    @Override
    public void splice(int start, int deleteCount, List<? extends E> items) {
        if (start + deleteCount > size) throw new IllegalArgumentException();

        // I am greatly disturbed that I do not have enough motivation or proper tools to
        // generate efficient code for this.
        // all these methods have overlapping logic and redundant copies, which theoretically can be solved by
        // an efficient logical algorithm that precisely calculates the exact operations to do
        // which cannot be written on a conventional language like Java (or at least the
        // logical computation itself will be slow enough to offset the benefits)
        // The following implementation is known to work and should be precise enough that
        // No extensive testing is required to prove its correctness.

        ensureDataLength(size - deleteCount + items.size());

        // array modification is particularly difficult to write an efficient algorithm
        // because when using a single method of modifying the array there are
        // different best/worst case scenario (again, bringing to the "smart algorithm" argument)
        // for my usecase, my algorithm adheres to the following rules:

        // while it is not an optimized routine, do not fragment the logic of copying sub-arrays
        //   as it gets complicated really quickly
        // the operations following now are guaranteed that the array has enough data length for all element additions
        // if it is possible to not move (arraycopy) the existing subarray, don't.
        // that includes filling the left side of the array first to avoid moving the existing subarray

        // algorithm:
        // @predicate data.length >= data.length - deleteCount + items.size();

        // 1. determine left delete (empty space on the left) (no size change yet)
        // 2. if left delete exists, determine if it can be filled using items
        //   3. if they can't fill, linearly list items, move entire remaining size once
        // (left and items adjusted)
        // 4. if left delete doesn't exist, linearly append at tend of list

        int itemsCap = items.size();

        // memory layout:
        // ... leftSpaceStart:leftSpaceEnd ... rightSpaceStart:rightSpaceEnd ... size
        // ^ fixed                         ^ try not to move this            ^ deterministic

        int leftSpaceStart = Math.max(offset + start - size, 0);
        int leftSpaceEnd = Math.max(offset + start + deleteCount - size, 0);

        int rightSpaceStart = Math.min(offset + start, size);
        int rightSpaceEnd = Math.min(offset + start + deleteCount, size);


        // determine if mid section requires moving
        int midMoveRight = leftSpaceStart + itemsCap - leftSpaceEnd;
        if (midMoveRight < 0 || leftSpaceStart > 0) {
            int midLength = rightSpaceStart - leftSpaceEnd;
            System.arraycopy(data, leftSpaceEnd, data, leftSpaceEnd + midMoveRight, midLength);

            leftSpaceEnd += midMoveRight;
            rightSpaceStart += midMoveRight;

            offset += midMoveRight;
        }

        // fill left space
        for (int di = leftSpaceStart, ii = itemsCap - (leftSpaceEnd - leftSpaceStart); di < leftSpaceEnd; di++, ii++) {
            data[di] = items.get(ii);
        }

        itemsCap -= leftSpaceEnd - leftSpaceStart;

        // determine if right subarray needs moving
        int rightMoveRight = itemsCap - rightSpaceEnd + rightSpaceStart;
        if (rightMoveRight != 0 && rightSpaceEnd < size) {
            int rightLength = size - rightSpaceEnd;
            System.arraycopy(data, rightSpaceEnd, data, rightSpaceEnd + rightMoveRight, rightLength);
        }

        size += rightMoveRight;

        // fill right space
        for (int di = rightSpaceStart, ii = 0; ii < itemsCap; di++, ii++) {
            data[di] = items.get(ii);
        }
    }

    public void rotateLeft() {
        offset++;
        if (offset >= size) offset = 0;
    }

    public void rotateRight() {
        offset--;
        if (offset < 0) offset = size - 1;
    }

    public boolean equals(List<E> other) {
        if (this.size() != other.size()) return false;

        for (int i = 0; i < this.size(); i++) {
            if (!Objects.equals(this.get(i), other.get(i))) return false;
        }

        return true;
    }

    public E removeFirst() {
        return remove(0);
    }

    public E removeLast() {
        return remove(size - 1);
    }


    /**
     * @param newSize if negative, -newSize ** 2 *  current. else ensure newSize exists
     */
    private void ensureDataLength(int newSize) {
        if (data.length >= newSize) return;
        E[] newData = _createNewArrayEnsuringSize(newSize);

        // copy low first (right)
        System.arraycopy(data, offset, newData, 0, size - offset);
        // copy high later (left)
        System.arraycopy(data, 0, newData, size - offset, offset);

        offset = 0;

        data = newData;
    }

    private E[] _createNewArrayEnsuringSize(int newSize) {
        int newDataLength = data.length;

        if (newSize < 0) {
            newDataLength <<= -newSize;
        } else {
            while (newDataLength < newSize) {
                newDataLength <<= 1;
            }
        }

        @SuppressWarnings("unchecked cast")
        E[] newData = (E[]) new Object[newDataLength];
        return newData;
    }

    // methods that I won't implement

    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    public int indexOf(Object o) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new RuntimeException("Not implemented");
    }

}