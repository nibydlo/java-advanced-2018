package ru.ifmo.rain.krylov.arrayset;

import java.util.*;

public class ArraySet<E> extends AbstractSet<E> implements SortedSet<E> {

    private List<E> arrayList;
    private Comparator<? super E> comparator;

    public ArraySet() {
        this((Comparator<? super E>) null);
    }

    public ArraySet(Comparator<? super E> comparator) {
        this.arrayList = new ArrayList<>();
        this.comparator = comparator;
    }

    public ArraySet(Collection<? extends E> arrayList) {
        this(arrayList, null);
    }

    public ArraySet(Collection<? extends E> arr, Comparator<? super E> comparator) {

        TreeSet<E> ts = new TreeSet<>(comparator);
        ts.addAll(arr);
        this.arrayList = new ArrayList<>(ts);
        this.comparator = ts.comparator();
    }

    public ArraySet(ArraySet<E> navigableSet) {

        this.comparator = navigableSet.comparator();
        this.arrayList = new ArrayList<>(navigableSet);
    }

    public ArraySet(List<E> arrayList, Comparator<? super E> comparator) {
        this.comparator = comparator;
        this.arrayList = arrayList;
    }

    private E lowerFloorCeilingHigher(E e, boolean less, boolean include) {

        int pos = bin(e, less, include);
        return (pos >= 0 && pos < arrayList.size()) ? arrayList.get(pos) : null;
    }

   /* @Override
    public E lower(E e) {
        return lowerFloorCeilingHigher(e, true, false);
    }

    @Override
    public E floor(E e) {
        return lowerFloorCeilingHigher(e, true, true);
    }

    @Override
    public E ceiling(E e) {
        return lowerFloorCeilingHigher(e, false, true);
    }

    @Override
    public E higher(E e) {
        return lowerFloorCeilingHigher(e, false, false);
    }

    @Override
    public E pollFirst() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E pollLast() {
        throw new UnsupportedOperationException();
    }*/

    @Override
    public int size() {
        return this.arrayList == null ? 0 : this.arrayList.size();
    }

    @Override
    //@SuppressWarnings("unchecked")
    public boolean contains(Object o) {

        try {
            return Collections.binarySearch(this.arrayList, (E) o, this.comparator()) >= 0;
        } catch (ClassCastException e) {
            System.err.println("argument has unexpected type");
            return false;
        }
    }

    @Override
    public Iterator<E> iterator() {
        return Collections.unmodifiableList(arrayList).iterator();
    }

    /*@Override
    public NavigableSet<E> descendingSet() {
        return new ArraySet<>(arrayList, Collections.reverseOrder(comparator));
    }

    @Override
    public Iterator<E> descendingIterator() {
        return descendingSet().iterator();
    }*/

    private int bin(E element, boolean to, boolean inclusive) {

        int pos = Collections.binarySearch(arrayList, element, comparator);
        if (pos < 0) {
            pos = ~pos - (to ? 1 : 0);
        } else if (!inclusive) {
            pos += (to ? -1 : 1);
        }
        return pos;
    }

    /*
    @Override
    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {

        int from = bin(fromElement, false, fromInclusive);
        int to = bin(toElement, true, toInclusive) + 1;
        to = Integer.max(from, to);
        return new ArraySet<>(arrayList.subList(from, to), comparator);
    }

    @Override
    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        return new ArraySet<>(arrayList.subList(0, bin(toElement, true, inclusive) + 1), comparator);
    }

    @Override
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        return new ArraySet<>(arrayList.subList(bin(fromElement, false, inclusive), arrayList.size()), comparator);
    }*/

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        return headSet(toElement).tailSet(fromElement);
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        return new ArraySet<>(arrayList.subList(0, bin(toElement, true, false) + 1), comparator);
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        return new ArraySet<>(arrayList.subList(bin(fromElement, false, true), arrayList.size()), comparator);
    }

    @Override
    public E first() {
        try {
            return this.arrayList.get(0);
        } catch (IndexOutOfBoundsException e) {
            throw new NoSuchElementException();
        }
    }

    @Override
    public E last() {
        try {
            return this.arrayList.get(arrayList.size() - 1);
        } catch (IndexOutOfBoundsException e) {
            throw new NoSuchElementException();
        }
    }
}
