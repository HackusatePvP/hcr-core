package dev.hcr.hcf.utils;

import dev.hcr.hcf.factions.claims.cuboid.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtils {

    public static Location parseLocation(String parse) {
        String[] split = parse.split("%");
        double x = Double.parseDouble(split[0]);
        //int y = Integer.parseInt(split[1]);
        double y = Double.parseDouble(split[1]);
        double z = Double.parseDouble(split[2]);
        String world = split[3];
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    public static Cuboid parseCuboid(String parse) {
        // -56.0 * 309.0 * -50.0 * 321.0 * world
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

 /*   public static Cuboid parseCuboid(String parse) {
        // Dont even ask LMAOOO
        for (int s1 = 0; s1 < parse.length(); s1++) {
            if (parse.charAt(s1) == '*') {
                String part = parse.substring(s1);
                double x = Double.parseDouble(parse.replace(part, ""));
                if (part.startsWith("*")) {
                    part = part.substring(1);
                }
                for (int s2 = 0; s2 < part.length(); s2++) {
                    if (part.charAt(s2) == '*') {
                        String part2 = part.substring(s2);
                        double z = Double.parseDouble(part.replace(part2, ""));
                        if (part2.startsWith("*")) {
                            part2 = part2.substring(1);
                        }
                        for (int s3 = 0; s3 < part2.length(); s3++) {
                            if (part2.charAt(s3) == '*') {
                                String part3 = part2.substring(s3);
                                double x1 = Double.parseDouble(part2.replace(part3, ""));
                                if (part3.startsWith("*")){
                                    part3 = part3.substring(1);
                                }
                                for (int s4 = 0; s4 < part3.length(); s4++) {
                                    if (part3.charAt(s4) == '*') {
                                        String part4 = part3.substring(s4);
                                        double z1 = Double.parseDouble(part3.replace(part4, ""));
                                        String part5 = part3.substring(1);
                                        String world = part4.replace(part5, "").replace("*", "");
                                        return new Cuboid(world, x, z, x1, z1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    } */
}
