package com.wdbyte.bytepage.module;

import java.util.List;

/**
 * @author niulang
 * @date 2023/04/04
 */
public class MenuInfo {
    // 一级分类
    private String one;
    // 二级分类
    private String two;
    // 文件路径
    private List<PostInfo> postInfoList;

    public String getOne() {
        return one;
    }

    public void setOne(String one) {
        this.one = one;
    }

    public String getTwo() {
        return two;
    }

    public void setTwo(String two) {
        this.two = two;
    }

    public List<PostInfo> getPostInfoList() {
        return postInfoList;
    }

    public void setPostInfoList(List<PostInfo> postInfoList) {
        this.postInfoList = postInfoList;
    }
}
