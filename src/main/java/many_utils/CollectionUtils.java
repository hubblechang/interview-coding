package many_utils;

import com.alibaba.fastjson.JSON;

import java.util.*;

public class CollectionUtils {
    public static <T> void intersection(Collection<T> source, Collection<T> dest) {
        source.removeIf(t -> !dest.contains(t));
    }

    public static void main(String[] args) {
        List<String> source =  new ArrayList<>(Arrays.asList("a", "b", "c"));
        List<String> dest =  new ArrayList<>(Arrays.asList("f", "e", "c"));
        intersection(source, dest);
        System.out.println(JSON.toJSONString(source));
    }

}