package dev.hcr.hcf.utils;

import dev.hcr.hcf.factions.claims.cuboid.Cuboid;
import org.bukkit.Location;

public class LocationUtils {

    public static Location parseLocation(String parse) {
        return null; // TODO: 2/1/2022
    }

    public static Cuboid parseCuboid(String parse) {
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
                                        System.out.println("X1: " + x);
                                        System.out.println("X2: " + x1);
                                        System.out.println("Z1: " + z);
                                        System.out.println("Z2: " + z1);
                                        System.out.println("World: " + world);
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
    }
}
