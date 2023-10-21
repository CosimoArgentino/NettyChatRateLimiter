package com.chat.server.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public final class Messages {
    private static final String BUNDLE_NAME = "Messages";
    private static final ResourceBundle bundle = getResourceBundle(Locale.getDefault());

    public static final String HELLO = "HELLO";
    public static final String SORRY_NO_MORE_NAMES_FOR_YOU = "SORRY_NO_MORE_NAMES_FOR_YOU";
    public static final String YOU_CANNOT_SEND_MESSAGES_OUT_OF_ROOM = "YOU_CANNOT_SEND_MESSAGES_OUT_OF_ROOM";
    public static final String WELCOME_TO_ROOM = "WELCOME_TO_ROOM";
    public static final String INVALID_COMMAND = "INVALID_COMMAND";
    public static final String LIMIT_OF_MESSAGES_PER_MINUTE_EXCEED = "LIMIT_OF_MESSAGES_PER_MINUTE_EXCEED";
    public static final String COULD_NOT_CHANGE = "COULD_NOT_CHANGE";
    public static final String LEAVING = "LEAVING";

    private Messages() {
    }

    public static String get(String key, Object... args) {
        return get(bundle, key, args);
    }

    public static String get(ResourceBundle bundle, String key, Object... args) {
        String pattern = bundle.getString(key);
        return MessageFormat.format(pattern, args);
    }

    public static String get(Locale locale, String key, String... args) {
        ResourceBundle bundle = getResourceBundle(locale);
        return get(bundle, key, args);
    }

    private static ResourceBundle getResourceBundle(Locale locale) {
        return ResourceBundle.getBundle(BUNDLE_NAME, locale, new UTF8Control());
    }

}
