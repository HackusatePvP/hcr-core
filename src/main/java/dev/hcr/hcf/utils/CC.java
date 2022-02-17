package dev.hcr.hcf.utils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
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

    public static void main(String[] args) {
        String parse = "50.0%70%-50.0";
        String[] split = parse.split("%");
        double x = Double.parseDouble(split[0]);
        int y = Integer.parseInt(split[1]);
        double z = Double.parseDouble(split[2]);
        System.out.println(x + "," + y + "," + z);
    }
}
