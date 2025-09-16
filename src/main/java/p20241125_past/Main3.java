package p20241125_past;

import java.util.Scanner;

public class Main3 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // 注意 hasNext 和 hasNextLine 的区别
        int n = in.nextInt();
        if(n < 4){
            System.out.println(0);
            return;
        }
        long sum = 1;
        for (int i = n; i > n-3; i--) {
            sum *= n;
        }

        System.out.println(2*(n+1));
    }
}
