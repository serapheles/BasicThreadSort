package org.main;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
//        System.out.println("Hello world!");
        int size = 1 << 20;
        Integer[] numbers = new Integer[size];
        Random dice = new Random();
        for(int i = 0;i < size;i++){
            numbers[i] = dice.nextInt(1000);
        }
        long startTime = System.nanoTime();
//        NewMerge.newSort(numbers);
        ThreadMergeSort.mergeSort(numbers);
        long endTime = System.nanoTime();
        System.out.println(endTime - startTime);
//        System.out.println(Arrays.toString(numbers));

    }
}