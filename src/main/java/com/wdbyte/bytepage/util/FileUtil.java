package com.wdbyte.bytepage.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author niulang
 * @date 2023/04/04
 */
public class FileUtil {

    /**
     * 列出指定后缀的文件
     *
     * @param path
     * @param endsWith
     * @return
     * @throws IOException
     */
    public static List<Path> listFiles(String path, String endsWith) throws IOException {
        Path start = Paths.get(path);
        try (Stream<Path> paths = Files.walk(start);) {
            return paths.filter(Files::isRegularFile)
                .filter(path1 -> path1.toString().endsWith(endsWith))
                .collect(Collectors.toList());
        }
    }

    /**
     * 列出指定路径中的文件夹和指定后缀的文件
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static List<Path> listDirAndMdFile(Path path, String endsWith) throws IOException {
        if (!Files.isDirectory(path)) {
            return new ArrayList<>(0);
        }

        try (Stream<Path> pathStream = Files.list(path);) {
            List<Path> dirList = pathStream
                .filter(pathTemp -> Files.isDirectory(pathTemp) || pathTemp.toString().endsWith(endsWith))
                .sorted(Comparator.comparingInt(pathTemp -> Integer.parseInt(pathTemp.getFileName().toString().split("\\.")[0])))
                .collect(Collectors.toList());
            pathStream.close();
            if (dirList.get(0).getFileName().toString().contains(endsWith)){
                Collections.reverse(dirList);
            }
            return dirList;
        }
    }

    /**
     * 获取路径索引
     *
     * @param path
     * @param index
     * @return
     */
    public static String getPathNameByIndex(Path path, Integer index) {
        return path.getName(path.getNameCount() - index).toString();
    }
}
