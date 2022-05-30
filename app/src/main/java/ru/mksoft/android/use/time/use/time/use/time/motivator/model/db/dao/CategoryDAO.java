package ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Category;

import java.sql.SQLException;
import java.util.List;

import static ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.dao.DbHelper.PREDEFINED_ID;

/**
 * Application category data access object.
 *
 * @author Kirill
 * @since 18.11.2021
 */
public class CategoryDAO extends BaseDaoImpl<Category, Long> {
    protected CategoryDAO(ConnectionSource connectionSource, Class<Category> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    /**
     * Returns a list of all categories, excluding the default one
     *
     * @return list of all categories
     * @throws SQLException in case of incorrect work with database
     */
    public List<Category> getAllCategoriesWoDefault() throws SQLException {
        QueryBuilder<Category, Long> queryBuilder = queryBuilder();
        queryBuilder.where().ne("id", PREDEFINED_ID);
        return query(queryBuilder.prepare());
    }

    /**
     * Returns the number of all categories, excluding the default one
     *
     * @return number of all categories
     * @throws SQLException in case of incorrect work with database
     */
    public long getCategoriesWoDefaultCount() throws SQLException {
        return queryBuilder().where().ne("id", PREDEFINED_ID).countOf();
    }

    @Override
    public Category queryForId(@SuppressWarnings("MethodParameterNamingConvention") Long id) throws SQLException {
        return super.queryForId(id);
    }

    /**
     * Returns the default category.
     *
     * @return default category
     * @throws SQLException in case of incorrect work with database
     */
    public Category getDefaultCategory() throws SQLException {
        return queryForId(PREDEFINED_ID);
    }

    /**
     * Returns count of all user categories.
     *
     * @return count of all user categories
     * @throws SQLException in case of incorrect work with database
     */
    public long countOfUserCategories() throws SQLException {
        return countOf() - 1;
    }
}
