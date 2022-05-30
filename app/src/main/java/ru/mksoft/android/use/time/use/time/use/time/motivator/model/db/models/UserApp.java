package ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Class for database table "USER_APP" representation.
 *
 * @author Kirill
 * @since 18.11.2021
 */

@Getter
@Setter
@DatabaseTable(tableName = "USER_APP")
public class UserApp {
    /**
     * Name of column IS_TRACKED in dp.
     */
    public static final String FIELD_IS_TRACKED = "IS_TRACKED";

    /**
     * Name of column CATEGORY in dp.
     */
    public static final String FIELD_CATEGORY = "CATEGORY";

    @SuppressWarnings("FieldNamingConvention")
    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(columnName = "SYS_ID",
            width = 512,
            canBeNull = false)
    private Integer systemId;

    @DatabaseField(columnName = "PACKAGE_NAME",
            width = 32,
            index = true,
            canBeNull = false)
    private String packageName;

    @DatabaseField(columnName = "CATEGORY",
            foreign = true,
            foreignAutoRefresh = true,
            columnDefinition = "integer references CATEGORIES(id) on delete restrict",
            index = true,
            canBeNull = false)
    private Category category;

    @DatabaseField(columnName = "IS_TRACKED",
            index = true,
            canBeNull = false)
    private Boolean isTracked = false;

    @DatabaseField(columnName = "LAST_UPDATE_DATE",
            dataType = DataType.DATE_STRING,
            format = "yyyy-MM-dd",
            canBeNull = false)
    private Date lastUpdateDate;
}
