package org.main;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Very basic threaded mergesort.
 * Based on some relatively casual testing/timing, is actually only about as fast as my single threaded versions. When I
 * have time I'll check to see how the java source version does this to see how they optimized the threading portions. I
 * would love to have tried adding some of the odd improvements to mergesort I've tinkered with previously, but I can't
 * think of a clean way to add them in a threaded context without adding a lot of extra stuff.
 */
public class ThreadMergeSort extends ThreadedSort {

    /**
     * Initial point for a threaded mergesort; sets up the storage array, checks the size, et cetera.
     *
     * @param input The array to sort.
     * @param <E>   Comparable object.
     */
    public static <E extends Comparable<E>> void mergeSort(E[] input) {
        if (input == null || input.length < 2) {
            return;
        }
        int size = input.length;
        final E[] altArray = java.util.Arrays.copyOf(input, size);
        try (ForkJoinPool pool = new ForkJoinPool()) {
            MergeTask<E> task = new MergeTask<E>(altArray, input, 0, size);
            pool.invoke(task);
        }
    }

    /**
     * Internal task for recursively sorting. Java really makes you jump through hoops for simple cases.
     *
     * @param <E> Comparable object to be sorted.
     */
    static class MergeTask<E extends Comparable<E>> extends RecursiveAction {

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
        private MergeTask(E[] input, E[] altArray, int start, int end) {
            this.input = input;
            this.altArray = altArray;
            this.start = start;
            this.end = end;
        }

        /**
         * Threaded recursive mergesort.
         */
        @Override
        protected void compute() {
//            Elementary sort step. The best cutoff here is machine dependant, but in my experience 64 is a good general
//            option.
            if (end - start <= 64) {
                sliceInsert(altArray, start, end);
            } else {
                int middle = (start + end) >> 1;

                //Effectively combines fork and join for a series of tasks
                invokeAll(new MergeTask<E>(altArray, input, start, middle), new MergeTask<E>(altArray, input, middle, end));

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
                if (left < middle) {
                    System.arraycopy(input, left, altArray, start + index, middle - left);
                } else if (right < end) {
                    System.arraycopy(input, right, altArray, start + index, end - right);
                }

            }
        }
    }
}
