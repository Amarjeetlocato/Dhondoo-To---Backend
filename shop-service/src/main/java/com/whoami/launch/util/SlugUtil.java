package com.whoami.launch.util;

import java.util.UUID;

public final class SlugUtil {

    private SlugUtil() {
    }

    public static String generate(String businessName) {

        String base = businessName
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