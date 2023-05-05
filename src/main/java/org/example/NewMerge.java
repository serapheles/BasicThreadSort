package org.example;

import java.util.Arrays;

public class NewMerge {

    /**
     * Basic merge sort.
     *
     * @param input The array to sort.
     */
    public static <E extends Comparable<E>> void newSort(E[] input) {
        if (input == null || input.length < 2) {
            return;
        }
        int size = input.length;
        E[] altArray = Arrays.copyOf(input, size);
        bounceMerge(altArray, input, 0, size);
    }

    /**
     * Private recursive step of top-down merge sort.
     *
     * @param input    Passed array to sort.
     * @param altArray Passed array to sort into.
     * @param start    The index of the first value in the given recursive
     *                 step.
     * @param end      The index of the final value in the given recursive
     *                 step.
     */
    public static <E extends Comparable<E>> void bounceMerge(E[] input, E[] altArray, int start, int end) {
        //Insertion sort for lowest level sorting.
        if (end - start <= 128) {
            sliceInsert(altArray, start, end);
            return;
        }

        int middle = (start + end) / 2;

        bounceMerge(altArray, input, start, middle);
        bounceMerge(altArray, input, middle, end);

        int left = start;
        int right = middle;
        int index = 0;

        while (left < middle && right < end) {
            if (input[left].compareTo(input[right]) < 0) {
                altArray[start + index++] = input[left++];
                continue;
            }
            altArray[start + index++] = input[right++];
        }
        if(left < middle){
            System.arraycopy(input, left, altArray, start + index, middle - left);
        } else if (right < end) {
            System.arraycopy(input, right, altArray, start + index, end - right);
        }
    }

    /**
     * Internal Insertion Sort for lowest level sort. Saves a tremendous amount
     * of function calls for otherwise trivial cases.
     *
     * @param input The array to sort.
     * @param start The starting value to sort from.
     * @param end   The ending value to sort to.
     */
    private static <E extends Comparable<E>> void sliceInsert(E[] input, int start, int end) {
        for (int i = start + 1; i < end; i++) {
            E value = input[i];
            int j = i;
            for (; j > start && value.compareTo(input[j - 1]) < 0; j--) {
                input[j] = input[j - 1];
            }
            input[j] = value;
        }
    }
}