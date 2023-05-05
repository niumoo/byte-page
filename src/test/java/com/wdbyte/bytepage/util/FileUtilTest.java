package com.wdbyte.bytepage.util;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

/**
 * @author niulang
 * @date 2023/05/05
 */
class FileUtilTest {

    @org.junit.jupiter.api.Test
    void listFiles() {
    }

    @org.junit.jupiter.api.Test
    void listDirAndMdFile() {
    }

    @Test
    void getPathNameByIndex() {
        String path = "/Users/darcy/git/byte-notes/md/01.Java 基础/01.Java 基础";
        String pathNameByIndex = FileUtil.getPathNameByIndex(Paths.get(path), 1);
        System.out.println(pathNameByIndex);
    }
}