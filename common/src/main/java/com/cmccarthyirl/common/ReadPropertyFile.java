package com.cmccarthyirl.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ReadPropertyFile {

    /**
     * This method loads the property file
     */
    public Properties loadProperties(String filePath) throws IOException {
        Properties properties = null;
        InputStream inputStream = null;
        try {
            inputStream = loadResource(filePath);
            ;
            if (inputStream != null) {
                properties = new Properties();
                properties.load(inputStream);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return properties;
    }

    /**
     * This method returns the InputStream of given file
     */
    public InputStream loadResource(String filePath) throws IOException {
        ClassLoader classLoader = ReadPropertyFile.class.getClassLoader();
        InputStream inputStream = null;
        if (classLoader != null) {
            inputStream = classLoader.getResourceAsStream(filePath);
        }
        if (inputStream == null) {
            classLoader = ClassLoader.getSystemClassLoader();
            if (classLoader != null) {
                inputStream = classLoader.getResourceAsStream(filePath);
            }
        }
        if (inputStream == null) {
            File file = new File(filePath);
            if (file.exists()) {
                inputStream = new FileInputStream(file);
            }
        }
        return inputStream;
    }
}