package org.main;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Very basic threaded quicksort. The pivot selection, in particular, is very basic.
 */
public class ThreadQuickSort extends ThreadedSort {

    /**
     * Initial point for a threaded quicksort. Uses an alternative array equal in size to the array to sort for
     * temporary storage. On average, this is probably very spatially inefficient. However, it is "one-and-done", in
     * that it is always enough, even in worst case scenarios, and doesn't deal with size checking/resizing issues, or
     * regular reallocation.
     *
     * @param input The array to sort.
     * @param <E>   Comparable object.
     */
    public static <E extends Comparable<E>> void quickSort(E[] input) {
        if (input == null || input.length < 2) {
            return;
        }
        int size = input.length;
        final E[] altArray = java.util.Arrays.copyOf(input, size);
        try (ForkJoinPool pool = new ForkJoinPool()) {

            QuickTask<E> task = new QuickTask<E>(input, altArray, 0, size);
            pool.invoke(task);
        }
    }

    /**
     * Internal task for recursively sorting. Java really makes you jump through hoops for simple cases.
     *
     * @param <E> Comparable object to be sorted.
     */
    static class QuickTask<E extends Comparable<E>> extends RecursiveAction {

        private final E[] input;

        private final E[] altArray;

        private final int start;
        private final int end;

        /**
         * Constructor, because Java doesn't give a good option to just pass arguments to a function.
         *
         * @param input    The array to sort.
         * @param altArray An alternative array for temporary storage.
         * @param start    The start of the portion of the array to sort.
         * @param end      The end of the portion of the array to sort.
         */
        private QuickTask(E[] input, E[] altArray, int start, int end) {
            this.input = input;
            this.altArray = altArray;
            this.start = start;
            this.end = end;
        }

        /**
         * While there are a number of methods to finding a pivot based on concrete numbers, to be able to process
         * objects given comparable such as a user defined "color" class with an internal comparison, this attempts to
         * find the middle-most of three passed elements (the first, middle, and last elements of the portion to sort).
         * Correctly found the middle-most value in rudimentary testing, but may not exhaustively cover possibilities.
         * Worst case, a bad pivot is used, which is always a possibility.
         *
         * @param e1 The first element.
         * @param e2 The middle element.
         * @param e3 The last element.
         * @return The middle-most element.
         */
        private E findPivot(E e1, E e2, E e3) {
            int x = e1.compareTo(e2);
            int y = e3.compareTo(e2);
            if ((x < 0 && y > 0) || (x > 0 && y < 0) || x == y) {
                return e2;
            }
            int z = e1.compareTo(e3);
            if ((z < 0 && x < 0) || (x > 0 && z > 0)) {
                return e3;
            }
            return e1;
        }

        /**
         * Threaded recursive quicksort.
         */
        @Override
        protected void compute() {
            if (end - start <= 64) {
                sliceInsert(input, start, end);
            } else {
                int middle = (end + start) >> 1;

                E pivot = findPivot(input[start], input[middle], input[end - 1]);

                int left = 0;
                int right = 0;
                int mid = 0;

                for (int i = start; i < end; i++) {
                    if (input[i].compareTo(pivot) < 0) {
                        input[start + left++] = input[i];
                    } else if (input[i].compareTo(pivot) > 0) {
                        altArray[end - 1 - right++] = input[i];
                    } else {
                        altArray[start + mid++] = input[i];
                    }
                }
                int index = left;
                for (int i = 0; i < mid; i++) {
                    input[start + index++] = altArray[start + i];
                }
                for (int i = 0; i < right; i++) {
                    input[start + index++] = altArray[end - 1 - i];
                }

                //Effectively combines fork and join for a series of tasks
                invokeAll(new QuickTask<E>(input, altArray, start, start + left), new QuickTask<E>(input, altArray, end - right, end));
            }

        }
    }
}
