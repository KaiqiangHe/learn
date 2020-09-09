package com.kaiqiang.learn.seckill;

import java.util.function.Predicate;

/**
 * @Author kaiqiang
 * @Date 2020/09/09
 */
public class CheckUtil {

    public static void checkParam(boolean condition, String message) {
        if(condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <T> void checkParam(T param, Predicate<T> predicate,  String message) {
        if(predicate.test(param)) {
            throw new IllegalArgumentException(message);
        }
    }

}
