package com.txkj.tool.util;

import com.alibaba.fastjson2.JSON;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;

public class YmlUtil {

    public static Map<String, Object> toMap(String content) {
        Map<String, Object> codeMap;
        Yaml yaml = new Yaml();
        codeMap = yaml.load(content);
        return codeMap;
    }

    public static Map<String, Object> toMap(Path ymlPath) {
        Map<String, Object> codeMap;
        try (InputStream is = new FileInputStream(ymlPath.toFile())) {
            Yaml yaml = new Yaml();
            codeMap = yaml.load(is);
            return codeMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String ymlToJson(Path ymlPath) {
        Map<String, Object> codeMap;
        try (InputStream is = new FileInputStream(ymlPath.toFile())) {
            Yaml yaml = new Yaml();
            codeMap = yaml.load(is);
            return JSON.toJSONString(codeMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String ymlToJson(String content) {
        Map<String, Object> codeMap;
        Yaml yaml = new Yaml();
        codeMap = yaml.load(content);
        return JSON.toJSONString(codeMap);
    }

}
