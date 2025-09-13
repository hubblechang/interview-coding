package p202411;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;


public class BackTracking {
    public static ArrayList<ArrayList<Integer>> combine(int n, int k){
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        ArrayList<Integer> cur = new ArrayList<>();
        back_tracking(result, cur, n, k, -1);
        return result;
    }

    private static void back_tracking(ArrayList<ArrayList<Integer>> result, ArrayList<Integer> cur, int n, int k, int idx) {
        if(cur.size() == k){
            result.add(new ArrayList<>(cur));
            return;
        }
        for(int i = idx + 1; i < n; i++){
            cur.add(i);
            back_tracking(result, cur, n, k, idx + 1);
            cur.removeLast();
        }
    }

    public static void main(String[] args) {
        ArrayList<ArrayList<Integer>> res = combine(10, 3);
        System.out.println(JSON.toJSONString(res));
    }
}
