package org.main;

import java.util.concurrent.*;

public class ThreadMergeSort {

    static class MergeTask<E extends Comparable<E>> extends RecursiveAction {

        private final E[] input;

        private final E[] altArray;

        private final int start;
        private final int end;

        private MergeTask(E[] input, E[] altArray, int start, int end) {
            this.input = input;
            this.altArray = altArray;
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            if (end - start <= 64) {
                sliceInsert(altArray, start, end);
            } else {
                int middle = (start + end) / 2;

//                invokeAll(new MergeTask(altArray, input, start, middle), new MergeTask(altArray, input, middle, end));
                MergeTask<E> leftTask = new MergeTask<E>(altArray, input, start, middle);
                MergeTask<E> rightTask = new MergeTask<E>(altArray, input, middle, end);

                leftTask.fork();
                rightTask.fork();
                leftTask.join();
                rightTask.join();

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

    public static <E extends Comparable<E>> void mergeSort(E[] input) {
        if (input == null || input.length < 2) {
            return;
        }
        int size = input.length;
        if (size <= 64) {
            sliceInsert(input, 0, size);
        }
        final E[] altArray = java.util.Arrays.copyOf(input, size);
//        ExecutorService pool = Executors.newCachedThreadPool();
        ForkJoinPool pool = new ForkJoinPool();

        MergeTask<E> task = new MergeTask<E>(altArray, input, 0, size);
        pool.invoke(task);
    }

    /**
     * Internal Insertion Sort for lowest level sort.
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
