package ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Category;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.UserApp;

import java.sql.SQLException;
import java.util.List;

/**
 * Application data access object.
 *
 * @author Kirill
 * @since 18.11.2021
 */
public class UserAppDAO extends BaseDaoImpl<UserApp, Long> {
    protected UserAppDAO(ConnectionSource connectionSource, Class<UserApp> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    /**
     * Returns tracked applications.
     *
     * @return tracked applications
     * @throws SQLException in case of incorrect work with database
     */
    public List<UserApp> getAllTrackedApps() throws SQLException {
        QueryBuilder<UserApp, Long> queryBuilder = queryBuilder();
        queryBuilder.where().eq(UserApp.FIELD_IS_TRACKED, true);
        PreparedQuery<UserApp> preparedQuery = queryBuilder.prepare();

        return query(preparedQuery);
    }

    /**
     * Return untracked applications.
     *
     * @return untracked applications
     * @throws SQLException in case of incorrect work with database
     */
    public List<UserApp> getAllUntrackedApps() throws SQLException {
        QueryBuilder<UserApp, Long> queryBuilder = queryBuilder();
        queryBuilder.where().eq(UserApp.FIELD_IS_TRACKED, false);
        PreparedQuery<UserApp> preparedQuery = queryBuilder.prepare();

        return query(preparedQuery);
    }

    /**
     * Returns the tracked applications with the given category.
     *
     * @param category category
     * @return tracked applications with the given category
     * @throws SQLException in case of incorrect work with database
     */
    public List<UserApp> getTrackedUserAppsForCategory(Category category) throws SQLException {
        QueryBuilder<UserApp, Long> queryBuilder = queryBuilder();
        queryBuilder.where()
                .eq(UserApp.FIELD_CATEGORY, category)
                .and()
                .eq(UserApp.FIELD_IS_TRACKED, true);
        PreparedQuery<UserApp> preparedQuery = queryBuilder.prepare();

        return query(preparedQuery);
    }
}
