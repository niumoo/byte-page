package com.wdbyte.bytepage.module;

import java.util.ArrayList;
import java.util.List;

/**
 * 树结构
 *
 * @param <T>
 */
public class TreeNode<T> {
    private String name;
    private List<TreeNode<T>> children;
    private TreeNode<T> parent;
    private T data;

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public TreeNode(String name, T data, TreeNode<T> parent) {
        this.name = name;
        this.data = data;
        this.children = new ArrayList<>(8);
        this.parent = parent;
    }

    public void addChild(TreeNode<T> child) {
        this.children.add(child);
    }

    public void removeChild(TreeNode<T> child) {
        this.children.remove(child);
    }

    public T getData() {
        return this.data;
    }

    public List<TreeNode<T>> getChildren() {
        return this.children;
    }

    public String getName() {
        return name;
    }

    public TreeNode<T> getParent() {
        return parent;
    }
}