package org.main;

import java.util.concurrent.*;

public class ThreadQuickSort extends ThreadedSort{

    public static <E extends Comparable<E>> void quickSort(E[] input){
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

    static class QuickTask<E extends Comparable<E>> extends RecursiveAction{

        private final E[] input;

        private final E[] altArray;

        private final int start;
        private final int end;

        private QuickTask(E[] input, E[] altArray, int start, int end) {
            this.input = input;
            this.altArray = altArray;
            this.start = start;
            this.end = end;
        }

        private E findPivot(E e1, E e2, E e3){
            int x = e1.compareTo(e2);
            int y = e3.compareTo(e2);
            if((x < 0 && y > 0) || (x > 0 && y < 0) || x == y){
                return e2;
            }
            int z = e1.compareTo(e3);
            if((z < 0 && x < 0) || (x > 0 && z > 0)){
                return e3;
            }
            return e1;
        }

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

                for(int i = start; i < end;i++){
                    if(input[i].compareTo(pivot) < 0){
                        input[start + left++] = input[i];
                    } else if (input[i].compareTo(pivot) > 0) {
                        altArray[end - 1 - right++] = input[i];
                    } else {
                        altArray[start + mid++] = input[i];
                    }
                }
                int index = left;
                for(int i = 0; i < mid;i++){
                    input[start + index++] = altArray[start + i];
                }
                for(int i = 0; i < right;i++){
                    input[start + index++] = altArray[end - 1 - i];
                }

                invokeAll(new QuickTask<E>(input, altArray, start, start + left),
                        new QuickTask<E>(input, altArray, end - right, end));
//                QuickTask<E> leftTask = new QuickTask<E>(input, altArray, start, start + left);
//                QuickTask<E> rightTask = new QuickTask<E>(input, altArray, end - right, end);
//
//                leftTask.fork();
//                rightTask.fork();
//                leftTask.join();
//                rightTask.join();
            }

        }
    }
}
