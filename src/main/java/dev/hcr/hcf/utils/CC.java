package dev.hcr.hcf.utils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class CC {

    public static String translate(String line) {
        return ChatColor.translateAlternateColorCodes('&', line);
    }

    public static List<String> translate(List<String> list) {
        List<String> toReturn = new ArrayList<>();
        list.forEach(s -> toReturn.add(CC.translate(s)));
        return toReturn;
    }

    /*public static void main(String[] args) {
        List<String> roles = new ArrayList<>();
        roles.add("Hackusate-Leader");
        roles.add("piitex-CoLeader");
        roles.add("third-member");
        for (String s : roles) {
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == '-') {
                    String sub = s.substring(i);
                    System.out.println(sub);
                }
            }
        }
    } */
    public static void main(String[] args) {
        List<String> claims = new ArrayList<>();
        claims.add("50*50*-50*-50");
        int count = 0;
        String full;
        for (String s : claims) {
            for (int i = 0; i < s.length(); i++) {
                double x = 0D, z = 0D, x2 = 0D, z2 = 0D;
                if (s.charAt(i) == '*') {
                    String sub = s.substring(i);
                    if (count == 0) {
                        x = Double.parseDouble(s.replace(sub, ""));
                        System.out.println("X: " + x);
                        s = s.replace(x + "*", "");
                    }
                    if (count == 1) {
                        System.out.println("Line: " + s);
                        System.out.println("Sub: " + s.replace(sub, ""));
                        z = Double.parseDouble(s.replace(sub, ""));
                        System.out.println("Z: " + z);
                    }
                    if (count == 2) {
                        x2 = Double.parseDouble(s.replace(sub, ""));
                    }
                    if (count == 3) {
                        z2 = Double.parseDouble(s.replace(sub, ""));
                    }
                    count++;
                    System.out.println("Line: " + s);
                }
            }
        }
    }
}
