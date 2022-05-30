package ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 08.03.2022
 */
@DatabaseTable(tableName = "PROPERTY")
@Getter
@Setter
public class Property {
    /**
     * ID of property database field STRIKE
     */
    public static final Long STRIKE_FIELD_ID = 1L;

    /**
     * ID of property database field USER_LEVEL
     */
    public static final Long USER_LEVEL_FIELD_ID = 2L;

    /**
     * ID of property database field YESTERDAY_FAILED_GOALS_NUMBER
     */
    public static final Long YESTERDAY_FAILED_GOALS_NUMBER_FIELD_ID = 3L;
    /**
     * ID of property database field YESTERDAY_USED_TIME
     */
    public static final Long YESTERDAY_USED_TIME_FIELD_ID = 4L;
    /**
     * Max value which field strike can have
     */
    public static final Integer MAX_STRIKE = 7;
    /**
     * Maximum specific level, which user can achieve.
     */
    public static final Integer MAX_SPECIFIC_LEVEL = 7;

    @SuppressWarnings("FieldNamingConvention")
    @DatabaseField(id = true)
    private Long id;

    @DatabaseField(columnName = "VALUE", canBeNull = false)
    private Long value;
}
