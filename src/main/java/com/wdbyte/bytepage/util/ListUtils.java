package com.wdbyte.bytepage.util;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author niulang
 * @date 2021/03/04
 */
public class ListUtils {

    /**
     * 比较排序
     *
     * @param list       待排序列表
     * @param comparator 排序字段
     * @param asc        是否升序
     * @param <T>
     * @return
     */
    public static <T> List<T> sort(List<T> list, Comparator<? super T> comparator, boolean asc) {
        if (!asc) {
            comparator = comparator.reversed();
        }
        return list.stream().sorted(comparator).collect(Collectors.toList());
    }
}
