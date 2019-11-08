package com.anthonygosme.loadbalancer;

import com.anthonygosme.loadbalancer.config.LoadingException;
import com.anthonygosme.loadbalancer.config.pojo.Config;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;

import static com.anthonygosme.loadbalancer.config.YamlLoader.loadConfigFromFile;
import static com.anthonygosme.loadbalancer.config.YamlLoader.loadObjectFromFile;
import static org.junit.jupiter.api.Assertions.*;

class YamlLoaderTest {

    @Test
        // can't find the file
    void loadFromFileNoFile() {
        LoadingException thrownNotFound =
                Assertions.assertThrows(
                        LoadingException.class,
                        () -> loadObjectFromFile("./src/main/resources/notfound.yaml"));
        assertTrue(thrownNotFound.getMessage().contains("can't parse"));
    }

    @Test
        // simple Yaml
    void loadFromFileSimpleYaml() throws LoadingException {
        System.out.println(System.getProperty("user.dir"));
        Map<String, Object> obj = loadObjectFromFile("./src/main/resources/simple.yaml");
        assertNotNull(obj);
        assertEquals(obj.get("String"), "simple String");
    }

    @Test
        //  nested yaml
    void loadFromFileNestedYaml() throws LoadingException {
        Map<String, Object> obj = loadObjectFromFile("./src/main/resources/nested.yaml");
        assertEquals(((ArrayList) obj.get("foods")).get(1), "Orange");
    }

    @Test
        //  Malformed yaml
    void loadFromFileMalformedYaml() {
        LoadingException thrownNotFound =
                Assertions.assertThrows(
                        LoadingException.class,
                        () -> loadObjectFromFile("./src/main/resources/failure.yaml"));
        assertTrue(thrownNotFound.getMessage().contains("can't parse"));
    }

    @Test
        //  Target yaml
    void loadFromFileTargetYaml() throws LoadingException {
        Map<String, Object> obj = loadObjectFromFile("./src/main/resources/target.yaml");
        assertNotNull(obj);
    }

    @Test
    void loadConfigFromFileTest() throws LoadingException {
        String jarPath = System.getProperty("user.dir");
        Config config = loadConfigFromFile(jarPath + "/conf.d/proxy.yaml");
        assertNotNull(config);
        assertEquals(config.getProxy().getListen().getAddress(), "127.0.0.1");
    }
}
