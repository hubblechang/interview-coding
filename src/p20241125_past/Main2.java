package p20241125_past;

import java.util.Scanner;

public class Main2 {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // 注意 hasNext 和 hasNextLine 的区别
        String s = in.nextLine();
        char[] arr = new char[3 * s.length()];
        char[] rel = new char[3 * s.length()];
        for (int i = 0; i < s.length(); i++) {
            arr[i] = s.charAt(i);
            rel[i] = '1';
        }

        int cur = 0;
        for (int i = 0; i < s.length(); i++) {
            arr[cur + s.length()] = arr[cur];
            rel[cur + s.length()] = '1';
            rel[cur] = ' ';
            arr[cur] = ' ';
            cur += 2;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if(rel[i] == '1'){
                sb.append(arr[i]);
                if(sb.length() == s.length()){
                    break;
                }
            }
        }

        System.out.println(sb.toString());
    }

}
