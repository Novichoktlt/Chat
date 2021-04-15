package lesson3_6.online;

import java.util.Arrays;

public class Main {
    public  static void main(String[] args){
        int [] arrays = new int[] {1,5,65,5,4,5,65,4,1,5};
        System.out.println(Arrays.toString(knife(arrays)));
        System.out.println(checkArr(new int[] {4,4,4,4,1,1,4,4,}));
        System.out.println(checkArr(new int[] {4,4,4,4}));
        System.out.println(checkArr(new int[] {1,1,1,1}));
        System.out.println(checkArr(new int[] {1,1,1,1,9,4,4,4,4}));

    }


    public static int[] knife (int[] arrays){
        for (int i = arrays.length - 1; i >= 0; i--) {
            if (arrays[i] == 4) return Arrays.copyOfRange(arrays, i + 1, arrays.length);
        }
        throw new  RuntimeException ("В массиве нет четверок");
    }

    public  static boolean checkArr(int[] arr){

        boolean one = false;
        boolean four = false;


        for (int i = 0; i < arr.length; i++){
            if (arr[i] == 1) one = true;
            else if (arr[i] == 4) four =  true;
            else return false;
        }
        return one && four;
    }

}
