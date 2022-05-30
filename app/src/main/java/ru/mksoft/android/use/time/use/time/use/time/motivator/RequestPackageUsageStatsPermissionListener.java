package ru.mksoft.android.use.time.use.time.use.time.motivator;

/**
 * Listener for request statistics processor result.
 *
 * @author Kirill
 * @since 06.05.2022
 */
public interface RequestPackageUsageStatsPermissionListener {
    /**
     * Action when permission requested
     *
     * @param isGranted true, if granted
     */
    void onPermissionGranted(boolean isGranted);
}
