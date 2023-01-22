package dev.hcr.hcf.factions.types;

import dev.hcr.hcf.HCF;
import dev.hcr.hcf.factions.Faction;
import dev.hcr.hcf.factions.claims.Claim;
import dev.hcr.hcf.factions.claims.cuboid.Cuboid;
import dev.hcr.hcf.utils.LocationUtils;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.io.*;
import java.util.*;

public class GlowStoneMountainFaction extends Faction implements SystemFaction {
    private File file;

    private final Collection<Location> glowStoneLocationCache = new HashSet<>();

    public GlowStoneMountainFaction() {
        super(UUID.randomUUID(), "GlowstoneMountain", true);
        initiate();
    }

    public GlowStoneMountainFaction(Document document) {
        super(document);
        load(document);
        initiate();
    }

    @Override
    public double getDTRMultiplier() {
        return 1.5;
    }

    void initiate() {
        glowStoneLocationCache.clear();
        File directory = new File(HCF.getPlugin().getDataFolder(), "glowstone");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        this.file = new File(directory, "mountain.txt");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
            try {
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                glowStoneLocationCache.add(LocationUtils.parseLocation(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startGlowStoneScanner() {
        initiate();
        if (getClaims() == null || getClaims().size() == 0) return;
        FileWriter writer = null;
        try {
            try {
                writer = new FileWriter(file);
                for (Claim claim : getClaims()) {
                    Cuboid cuboid = claim.getCuboid();
                    for (Iterator<Block> it = cuboid.blockList(); it.hasNext();) {
                        Block block = it.next();
                        if (block.getType() == Material.GLOWSTONE) {
                            System.out.println("Glowstone found: " + block.getLocation());
                            writer.write(LocationUtils.parseLocationToString(block.getLocation()));
                            writer.write("\n");
                            glowStoneLocationCache.add(block.getLocation());
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void regenerateGlowStone() {
        for (Location location : glowStoneLocationCache) {
            location.getWorld().getBlockAt(location).setType(Material.GLOWSTONE);
        }
    }

    public Collection<Location> getGlowStoneLocationCache() {
        return glowStoneLocationCache;
    }
}
