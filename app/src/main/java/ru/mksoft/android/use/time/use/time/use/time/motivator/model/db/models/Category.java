package ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models;

import androidx.annotation.Nullable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;
import ru.mksoft.android.use.time.use.time.use.time.motivator.utils.DateTimeUtils;

import java.util.Date;
import java.util.Objects;

/**
 * Class for database table "CATEGORIES" representation.
 *
 * @author Kirill
 * @since 18.11.2021
 */
@Getter
@Setter
@DatabaseTable(tableName = "CATEGORIES")
public class Category {
    /**
     * CATEGORY_NAME field name
     */
    public static final String FIELD_CATEGORY_NAME = "CATEGORY_NAME";

    @SuppressWarnings("FieldNamingConvention")
    @DatabaseField(generatedId = true)
    private Long id;

    @SuppressWarnings("FieldNamingConvention")
    @DatabaseField(columnName = "RULE_ID",
            foreign = true,
            foreignAutoRefresh = true,
            columnDefinition = "integer references RULES(id) on delete restrict",
            index = true,
            canBeNull = false)
    private Rule rule;

    @SuppressWarnings("FieldNamingConvention")
    @DatabaseField(columnName = FIELD_CATEGORY_NAME,
            unique = true,
            width = 32,
            index = true,
            canBeNull = false)
    private String name;

    /**
     * Returns true if the goal is completed and false otherwise.
     *
     * @param date     date for which goal is checking
     * @param usedTime used time for date
     * @return true if the goal is completed and false otherwise
     */
    public boolean isDayGoalCompleted(Date date, Long usedTime) {
        return rule.getTime(Rule.DayOfWeek.values()[(DateTimeUtils.getDayOfWeek(date))]) >= (usedTime / DateTimeUtils.MILLIS_IN_MINUTE);
    }

    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object other) {
        if (!(other instanceof Category)) {
            return false;
        }

        return this.id.equals(((Category) other).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
