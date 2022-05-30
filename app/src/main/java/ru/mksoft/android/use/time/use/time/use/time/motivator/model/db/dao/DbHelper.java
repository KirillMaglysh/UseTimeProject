package ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.jetbrains.annotations.NotNull;
import ru.mksoft.android.use.time.use.time.use.time.motivator.R;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.DatabaseException;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.*;

import java.sql.SQLException;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 18.11.2021
 */
public class DbHelper extends OrmLiteSqliteOpenHelper {
    private static final String LOG_TAG = DbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "use_time_motivator.db";
    private static final int DATABASE_VERSION = 1;

    /**
     * Идентификатор записи, создаваемой по умолчанию.
     */
    public static final long PREDEFINED_ID = 1L;

    private static String noCategoryName;
    private static String noLimitRuleName;

    private CategoryDAO categoryDAO = null;
    private RuleDAO ruleDAO = null;
    private UserAppDAO userAppDAO = null;
    private AppUseStatsDAO appUseStatsDao = null;
    private PropertyDAO propertyDAO = null;

    /**
     * Constructor
     *
     * @param context application context
     */
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        noCategoryName = context.getString(R.string.no_category_name);
        noLimitRuleName = context.getString(R.string.no_limit_rile_name);
    }

    @Override
    public void onOpen(SQLiteDatabase database) {
        super.onOpen(database);
        // Without this line, update and delete data restrictions in related tables do not work.
        database.execSQL("PRAGMA foreign_keys = ON;");
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Rule.class);
            TableUtils.createTable(connectionSource, Category.class);
            TableUtils.createTable(connectionSource, UserApp.class);
            TableUtils.createTable(connectionSource, AppUseStats.class);
            TableUtils.createTable(connectionSource, Property.class);

            initializeDataBase();
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Error creating DB " + DATABASE_NAME);
            throw new DatabaseException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "upgrade database");
    }

    /**
     * Return the category data access object.
     *
     * @return category data access object
     * @throws SQLException in case of incorrect work with database
     */
    public CategoryDAO getCategoryDAO() throws SQLException {
        if (categoryDAO == null) {
            categoryDAO = new CategoryDAO(getConnectionSource(), Category.class);
        }

        return categoryDAO;
    }

    /**
     * Return the application data access object.
     *
     * @return application data access object
     * @throws SQLException in case of incorrect work with database
     */
    public UserAppDAO getUserAppDAO() throws SQLException {
        if (userAppDAO == null) {
            userAppDAO = new UserAppDAO(getConnectionSource(), UserApp.class);
        }

        return userAppDAO;
    }

    /**
     * Return the rule data access object.
     *
     * @return rule data access object
     * @throws SQLException in case of incorrect work with database
     */
    public RuleDAO getRuleDAO() throws SQLException {
        if (ruleDAO == null) {
            ruleDAO = new RuleDAO(getConnectionSource(), Rule.class);
        }

        return ruleDAO;
    }

    /**
     * Return the application usage statistics data access object.
     *
     * @return application usage statistics data access object
     * @throws SQLException in case of incorrect work with database
     */
    public AppUseStatsDAO getAppUseStatsDao() throws SQLException {
        if (appUseStatsDao == null) {
            appUseStatsDao = new AppUseStatsDAO(getConnectionSource(), AppUseStats.class);
        }

        return appUseStatsDao;
    }

    /**
     * Return property data access object.
     *
     * @return property data access object
     * @throws SQLException in case of incorrect work with database
     */
    public PropertyDAO getPropertyDAO() throws SQLException {
        if (propertyDAO == null) {
            propertyDAO = new PropertyDAO(getConnectionSource(), Property.class);
        }

        return propertyDAO;
    }

    private static void initializeDataBase() throws SQLException {
        RuleDAO ruleDAO = DbHelperFactory.getHelper().getRuleDAO();
        Rule noLimitRule = ruleDAO.queryForId(PREDEFINED_ID);
        if (noLimitRule == null) {
            noLimitRule = new Rule();
            noLimitRule.setId(PREDEFINED_ID);
            noLimitRule.setName(noLimitRuleName);
            ruleDAO.createOrUpdate(noLimitRule);
        }

        CategoryDAO categoryDAO = DbHelperFactory.getHelper().getCategoryDAO();
        Category noCategory = categoryDAO.queryForId(PREDEFINED_ID);
        if (noCategory == null) {
            noCategory = new Category();
            noCategory.setId(PREDEFINED_ID);
            noCategory.setName(noCategoryName);
            noCategory.setRule(noLimitRule);
            categoryDAO.createOrUpdate(noCategory);
        }

        initializeProperties();
    }

    private static void initializeProperties() throws SQLException {
        PropertyDAO propertyDAO = DbHelperFactory.getHelper().getPropertyDAO();
        createNewEmptyProperty(propertyDAO, Property.STRIKE_FIELD_ID);
        createNewEmptyProperty(propertyDAO, Property.USER_LEVEL_FIELD_ID);
        createNewEmptyProperty(propertyDAO, Property.YESTERDAY_USED_TIME_FIELD_ID);
        createNewEmptyProperty(propertyDAO, Property.YESTERDAY_FAILED_GOALS_NUMBER_FIELD_ID);
    }

    @NotNull
    private static void createNewEmptyProperty(PropertyDAO propertyDAO, Long strikeFieldId) throws SQLException {
        Property strikeProperty = propertyDAO.queryForId(strikeFieldId);
        if (strikeProperty == null) {
            strikeProperty = new Property();
            strikeProperty.setId(strikeFieldId);
            strikeProperty.setValue(0L);
        }

        DbHelperFactory.getHelper().getPropertyDAO().create(strikeProperty);
    }
}
