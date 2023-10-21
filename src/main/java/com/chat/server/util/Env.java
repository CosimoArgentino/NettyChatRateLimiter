package com.chat.server.util;

public final class Env {

    private Env() {
    }

    public static Integer getPort() {
        return envNVL("PORT", 5222L).intValue();
    }

    public static Long getSeed() {
        return envNVL("SEED", System.currentTimeMillis());
    }

    public static Long envNVL(String envKey, Long defaultValue) {
        try {
            final String envValue = System.getenv(envKey) != null
                                    ? System.getenv(envKey)
                                    : System.getProperty(envKey);
            return Long.parseLong(envValue);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

}
