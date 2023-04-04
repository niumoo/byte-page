package com.wdbyte.bytepage.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author niulang
 * @date 2023/04/04
 */
public class FileUtil {

    public static List<Path> listFiles(String path, String endsWith) throws IOException {
        Path start = Paths.get(path);
        Stream<Path> paths = Files.walk(start);
        return paths.filter(Files::isRegularFile)
            .filter(path1 -> path1.toString().endsWith(endsWith))
            .collect(Collectors.toList());
    }

    public static List<Path> listDir(Path path) throws IOException {
        Stream<Path> pathStream = Files.list(path);
        List<Path> dirList = pathStream
            .filter(Files::isDirectory)
            .sorted(Comparator.comparing(Path::getFileName))
            .collect(Collectors.toList());
        pathStream.close();
        return dirList;
    }

    public static List<Path> listDirAndMdFile(Path path) throws IOException {
        if (!Files.isDirectory(path)){
            return new ArrayList<>(0);
        }
        Stream<Path> pathStream = Files.list(path);
        List<Path> dirList = pathStream
            .filter(pathTemp -> Files.isDirectory(pathTemp) || pathTemp.toString().endsWith(".md"))
            .sorted(Comparator.comparing(Path::getFileName))
            .collect(Collectors.toList());
        pathStream.close();
        return dirList;
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
