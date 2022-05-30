package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.applist;

import android.graphics.drawable.Drawable;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 06.01.2022
 */
public class AppCardInfo {
    String label;
    @SuppressWarnings("FieldNamingConvention")
    Drawable icon;
    String category;

    /**
     * Constructor
     *
     * @param label label of the application
     * @param icon icon of the application
     * @param category category of the application
     */
    public AppCardInfo(String label, Drawable icon, String category) {
        this.label = label;
        this.icon = icon;
        this.category = category;
    }
}
