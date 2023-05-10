package dev.hcr.hcf.utils.backend;

import dev.hcr.hcf.HCF;
import org.apache.commons.io.FileUtils;
import org.bukkit.Material;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ItemDatabase {
    private final File file;
    private static final Map<String, Material> items = new HashMap<>();

    public ItemDatabase() {
        this.file = new File(HCF.getPlugin().getDataFolder(), "items.txt");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        InputStream inputStream = getClass().getResourceAsStream("/items.txt");
        if (inputStream == null) {
            HCF.getPlugin().getLogger().severe("Could not load \"items.txt\". This will result in the sign shop not working.");
            return;
        }
        try {
            if (!file.exists()) {
                FileUtils.copyInputStreamToFile(inputStream, file);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        init();
    }

    public void init() {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split("&");
                String item = split[0];
                Material material = Material.getMaterial(Integer.parseInt(split[1]));
                items.put(item, material);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Material getItem(String alias) {
        return items.get(alias);
    }
}
