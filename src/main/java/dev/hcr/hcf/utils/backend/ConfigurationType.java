package dev.hcr.hcf.utils.backend;


import dev.hcr.hcf.HCF;

import java.io.File;
import java.io.IOException;

public abstract class ConfigurationType {
    private final File file;
    private final String fileName;

    public ConfigurationType(String fileName) {
        this.fileName = fileName;
        this.file = new File(HCF.getPlugin().getDataFolder(), fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            if (file.createNewFile()) {
                preset(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ConfigurationType(String fileName, String directory) {
        this.fileName = fileName;
        File fileDirectory = new File(HCF.getPlugin().getDataFolder(), directory);
        fileDirectory.mkdirs();
        this.file = new File(fileDirectory, fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
           if (file.createNewFile()) {
               preset(file);
           }
        } catch (IOException e) {
            e.printStackTrace();
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
