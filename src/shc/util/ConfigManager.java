package shc.util;

import java.io.File;

public class ConfigManager {

    private static final String APP_NAME = "Sufiyan Health Clinic";

    public static File getConfigDirectory() {

        String localAppData = System.getenv("LOCALAPPDATA");

        if (localAppData == null || localAppData.isBlank()) {

            localAppData = System.getProperty("user.home");

        }

        File dir = new File(localAppData, APP_NAME);

        if (!dir.exists()) {

            dir.mkdirs();

        }

        return dir;

    }

    public static File getConfigFile() {

        return new File(getConfigDirectory(), "db.properties");

    }

}