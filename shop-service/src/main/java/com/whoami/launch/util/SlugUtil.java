package com.whoami.launch.util;
import java.util.UUID;

public class SlugUtil {

    public static String generate(String shopName) {

        String base = shopName
                .toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");

        String suffix = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 6);
        return base + "-" + suffix;
    }
}