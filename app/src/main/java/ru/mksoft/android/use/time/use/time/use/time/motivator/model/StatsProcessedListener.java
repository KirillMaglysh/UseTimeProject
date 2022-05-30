package ru.mksoft.android.use.time.use.time.use.time.motivator.model;

/**
 * UI element, which need to react on stats processing finish.
 *
 * @author Kirill
 * @since 06.05.2022
 */
public interface StatsProcessedListener {
    /**
     * Calls when statistics is processed.
     */
    void processStatsUpdated();
}
