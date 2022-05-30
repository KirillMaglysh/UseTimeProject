package ru.mksoft.android.use.time.use.time.use.time.motivator.utils;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 26.05.2022
 */
public class StringUtils {
    public static String makeStringShort(String src, int length) {
        if (src.length() <= length) {
            return src;
        }

        return src.substring(0, Math.max(0, length - 3)) + "..";
    }
}
