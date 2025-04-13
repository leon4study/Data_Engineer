package com.fastcampus.de.java.clip10_5_1_quiz;

class ArrayCalculation {

    int[] arr = { 0, 1, 2, 3, 4 };

    public int divide(int denominatorIndex, int numeratorIndex){
        return arr[denominatorIndex] / arr[numeratorIndex];
    }
}

public class Main {
    public static void main(String[] args) {
        ArrayCalculation arrayCalculation = new ArrayCalculation();

        try {
            //System.out.println("2 / 1 = " + arrayCalculation.divide(2, 1));
            //System.out.println("1 / 0 = " + arrayCalculation.divide(1, 0)); // java.lang.ArithmeticException: "/ by zero"
            System.out.println("Try to divide using out of index element = "
                    + arrayCalculation.divide(5, 0)); // java.lang.ArrayIndexOutOfBoundsException: 5
        } catch (ArithmeticException e) {
            System.out.println(e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println(e);
            System.out.println("arr index range : "+ arrayCalculation.arr[0]+" to "+ arrayCalculation.arr[arrayCalculation.arr.length-1]);
        }

    }
}