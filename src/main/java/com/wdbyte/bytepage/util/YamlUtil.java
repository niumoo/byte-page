package com.wdbyte.bytepage.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import com.wdbyte.bytepage.module.PostInfo;
import org.yaml.snakeyaml.Yaml;

/**
 * @author niulang
 * @date 2023/03/31
 */
public class YamlUtil {
    public static PostInfo parseYaml(String yamlContent) {
        Yaml yaml = new Yaml();
        PostInfo postInfo = yaml.loadAs(yamlContent, PostInfo.class);
        return postInfo;
    }
}
