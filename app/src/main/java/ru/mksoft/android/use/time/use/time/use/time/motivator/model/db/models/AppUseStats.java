package ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Class for database table "APP_USE_STATS" representation.
 *
 * @author Kirill
 * @since 23.02.2022
 */

@DatabaseTable(tableName = "APP_USE_STATS")

@Getter
@Setter
public class AppUseStats {
    /**
     * USER_APP field name
     */
    public static final String FIELD_USER_APP = "USER_APP";
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * DATE field name
     */
    public static final String FIELD_DATE = "DATE";

    @SuppressWarnings("FieldNamingConvention")
    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(columnName = "USER_APP",
            foreign = true,
            foreignAutoRefresh = true,
            foreignAutoCreate = true,
            canBeNull = false)
    private UserApp userApp;

    @SuppressWarnings("FieldNamingConvention")
    @DatabaseField(columnName = "DATE",
            dataType = DataType.DATE_STRING,
            format = DATE_FORMAT,
            canBeNull = false)
    private Date date;

    @DatabaseField(columnName = "USAGE_TIME", canBeNull = false)
    private Long usageTime;
}
