package com.kyrylov.nure.ua.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Class to work with resources
 *
 * @author Dmitrii
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResourceHelper {

    private static String getResource(String resourceName, String bundleLocation) {
        String resource = null;

        ResourceBundle bundle = ResourceBundle.getBundle(bundleLocation, Locale.getDefault(), getClassLoader());

        if (bundle != null) {
            try {
                resource = bundle.getString(resourceName);
            } catch (MissingResourceException ex) {
                ex.printStackTrace();
            }
        }

        return resource != null ? resource : "???" + resourceName + "???";
    }

    public static PropertiesWrapper getProperties() throws IOException {
        Properties projectProperties = new Properties();
        try {
            InputStream is = getClassLoader().getResourceAsStream("project.properties");
            projectProperties.load(is);
        } catch (IOException e) {
            throw new IOException("Properties hasn't been read", e);
        }
        return new PropertiesWrapper(projectProperties);
    }

    private static ClassLoader getClassLoader() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        return loader;
    }

    @AllArgsConstructor
    @Getter
    public enum ProjectProperties {
        MAKEUP_SITE("makeupSite");

        private String value;
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class PropertiesWrapper {

        private Properties properties;

        public String getProperty(ProjectProperties property) {
            return this.properties.getProperty(property.getValue());
        }
    }
}
