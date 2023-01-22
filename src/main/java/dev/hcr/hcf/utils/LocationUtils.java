package dev.hcr.hcf.utils;

import dev.hcr.hcf.factions.claims.cuboid.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtils {

    public static Location parseLocation(String parse) {
        String[] split = parse.split("%");
        double x = Double.parseDouble(split[0]);
        double y = Double.parseDouble(split[1]);
        double z = Double.parseDouble(split[2]);
        String world = split[3];
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    public static String parseLocationToString(Location location) {
        return location.getBlockX() + "%" + location.getBlockY() + "%" + location.getBlockZ() + "%" + location.getWorld().getName();
    }

    public static Cuboid parseCuboid(String parse) {
        try {
            String[] split = parse.split("\\*");
            double x1 = Double.parseDouble(split[0]);
            double z1 = Double.parseDouble(split[1]);
            double x2 = Double.parseDouble(split[2]);
            double z2 = Double.parseDouble(split[3]);
            String world = split[4];
            return new Cuboid(world, x1, z1, x2, z2);
        } catch (Exception ignored) {
            throw new NullPointerException();
        }

    }
}
