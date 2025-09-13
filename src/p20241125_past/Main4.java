package p20241125_past;

public class Main4 {
    public static void main(String[] args) {
        int[] nums = new int[]{1,2,3,4,4,5};
        int target = 4;
        int left = 0;
        int right = nums.length - 1;
        int mid = 0;
        while(left <= right){
            mid = (left + right) / 2;
            if(nums[mid] == target){
                break;
            }else{
                if(nums[mid] > target){
                    right = mid - 1;
                }else{
                    left = mid + 1;
                }
            }
        }
        if(left <= right){
            int leftBound = mid;
            int rightBound = mid;
            if(leftBound >0 && nums[leftBound - 1] == target){
                leftBound--;
            }
            if(rightBound < nums.length-1 && nums[rightBound + 1] == target){
                rightBound++;
            }
            System.out.println(mid);
            System.out.println(leftBound);
            System.out.println(rightBound);
        }else{
            System.out.println(-1);
        }

    }
}
