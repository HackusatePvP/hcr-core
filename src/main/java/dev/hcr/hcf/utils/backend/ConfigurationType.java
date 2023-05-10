package dev.hcr.hcf.utils.backend;


import dev.hcr.hcf.HCF;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public abstract class ConfigurationType {
    private final File file;
    private final String fileName;

    private final HCF plugin = HCF.getPlugin();

    public ConfigurationType(String fileName) {
        this.fileName = fileName;
        this.file = new File(HCF.getPlugin().getDataFolder(), fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (file.exists()) {
            plugin.getLogger().info("File: \"" + fileName + "\" already exists. Loading...");
            return;
        }
        try {
            plugin.getLogger().warning("File: \"" + fileName + "\" does not exist. Creating and presetting the file...");
            if (!file.createNewFile()) {
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        InputStream inputStream = getClass().getResourceAsStream("/" + file.getName());
        if (inputStream == null) {
            plugin.getLogger().severe("Could not load \"" + fileName + "\". This will result in the sign shop not working.");
            return;
        }
        try {
            FileUtils.copyInputStreamToFile(inputStream, file);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public ConfigurationType(String fileName, String directory) {
        this.fileName = fileName;
        File fileDirectory = new File(HCF.getPlugin().getDataFolder(), directory);
        this.file = new File(fileDirectory, fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (file.exists()) {
            plugin.getLogger().info("File: \"" + fileName + "\" already exists. Loading...");
            return;
        }
        try {
            plugin.getLogger().warning("File: \"" + fileName + "\" does not exist. Creating and presetting the file...");
            file.createNewFile();
            file.mkdir();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        InputStream inputStream = getClass().getResourceAsStream("/" + directory + "/" + file.getName());
        if (inputStream == null) {
            plugin.getLogger().severe("Could not load \"" + fileName + "\". This will result in the sign shop not working.");
            return;
        }
        try {
            FileUtils.copyInputStreamToFile(inputStream, file);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public abstract String getString(String path);

    public abstract Boolean getBoolean(String path);

    public abstract Integer getInteger(String path);

    public abstract Double getDouble(String path);

    public abstract Float getFloat(String path);

    public abstract Long getLong(String path);

    public abstract Object get(String path);

    public abstract void write(String path, Object value);

    public abstract void preset(File file);

    public String getFileName() {
        return fileName;
    }

    public File getFile() {
        return file;
    }

}
