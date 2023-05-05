package org.main;

public class ThreadedSort {

    /**
     * Internal Insertion Sort for lowest level sort.
     *
     * @param input The array to sort.
     * @param start The starting value to sort from.
     * @param end   The ending value to sort to.
     */
    protected static <E extends Comparable<E>> void sliceInsert(E[] input, int start, int end) {
        for (int i = start + 1; i < end; i++) {
            E value = input[i];
            int j = i;
            for (; j > start && value.compareTo(input[j - 1]) < 0; j--) {
                input[j] = input[j - 1];
            }
            input[j] = value;
        }
    }

    protected static <E extends Comparable<E>> boolean arrayCheck(E[] input){
        return true;
    }
}
