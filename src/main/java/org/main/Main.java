package org.main;

import java.util.Arrays;
import java.util.Random;

/**
 * Managing class. If I learned one thing from this project, it's that Java makes threads more convoluted to use for
 * simple cases.
 */
public class Main {

    /**
     * Main function. Creates two arrays of 1000 elements to pass to sorting algorithms. Prints the before and after
     * states of the arrays for testing/verification.
     *
     * @param args Unused argument parameter.
     */
    public static void main(String[] args) {
//        Testing size
//        int size = 1 << 25;
        int size = 1000;
//        I took this line straight from what you said.
        Integer[] numbers = new Integer[size];
        Random dice = new Random();
        for (int i = 0; i < size; i++) {
            numbers[i] = dice.nextInt();
        }
//        Timing testing
//        long startTime;
//        long endTime;
        System.out.println("Before QuickSort: \n" + Arrays.toString(numbers));
//        startTime = System.nanoTime();
        ThreadQuickSort.quickSort(numbers);
//        endTime = System.nanoTime();
        System.out.println("After QuickSort: \n" + Arrays.toString(numbers));
        for (int i = 0; i < size; i++) {
            numbers[i] = dice.nextInt();
        }
        System.out.println("Before MergeSort: \n" + Arrays.toString(numbers));
        ThreadMergeSort.mergeSort(numbers);
        System.out.println("After MergeSort: \n" + Arrays.toString(numbers));
//        System.out.println(endTime - startTime);
    }
}