package ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import org.jetbrains.annotations.NotNull;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.AppStatsBin;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.CategoryStatsBin;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.AppUseStats;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Category;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.UserApp;
import ru.mksoft.android.use.time.use.time.use.time.motivator.utils.DateTimeUtils;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Application usage statistics data access object.
 *
 * @author Kirill
 * @since 23.02.2022
 */
public class AppUseStatsDAO extends BaseDaoImpl<AppUseStats, Long> {

    private SimpleDateFormat dateRawFormat = new SimpleDateFormat(AppUseStats.DATE_FORMAT, Locale.getDefault());

    protected AppUseStatsDAO(ConnectionSource connectionSource, Class<AppUseStats> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    @Override
    public AppUseStats queryForId(Long id) throws SQLException {
        return super.queryForId(id);
    }

    public List<AppUseStats> getAppStatsForPeriod(UserApp userApp, Date start, Date end) throws SQLException {
        QueryBuilder<AppUseStats, Long> queryBuilder = queryBuilder();
        queryBuilder.where()
                .eq(AppUseStats.FIELD_USER_APP, userApp)
                .and()
                .between(AppUseStats.FIELD_DATE, start, end);
        PreparedQuery<AppUseStats> preparedQuery = queryBuilder.prepare();

        return query(preparedQuery);
    }

    public List<Long> getCategorySumSuffixTimeStats(Category category, int dayNum) throws SQLException {
        String query = "select Date, sum(USAGE_TIME) as TIME \n" +
                "from APP_USE_STATS \n" +
                "where USER_APP in ( \n" +
                "    select id \n" +
                "    from USER_APP \n" +
                "    where CATEGORY = " + category.getId() + " \n" +
                ") \n" +
                "and DATE between '" +
                dateRawFormat.format(DateTimeUtils.getDateOtherDayBegin(-dayNum + 1)) +
                "' and '" +
                dateRawFormat.format(DateTimeUtils.getDateOtherDayBegin(0)) +
                "' \n" +
                "GROUP BY Date";

        GenericRawResults<String[]> results = this.queryRaw(query);
        return convertWeekGenericResults(results);
    }

    @NotNull
    private static List<Long> convertWeekGenericResults(GenericRawResults<String[]> results) throws SQLException {
        List<Long> numberStats = new ArrayList<>();
        for (String[] dateResult : results) {
            numberStats.add(Long.parseLong(dateResult[1]));
        }

        int duty = 7 - numberStats.size();
        List<Long> zeros = new ArrayList<>();
        for (int i = 0; i < duty; i++) {
            zeros.add(0L);
        }

        numberStats.addAll(0, zeros);
        return numberStats;
    }

    /**
     * Removes AppUseStats stats for userApp for date.
     *
     * @param userApp application for which you want to remove stats
     * @param date    date, for which you want to remove stats
     * @return number of removed field in database
     * @throws SQLException in case of incorrect work with database
     */
    public int removeAppStatsPerDay(UserApp userApp, Date date) throws SQLException {
        DeleteBuilder<AppUseStats, Long> deleteBuilder = deleteBuilder();
        deleteBuilder.where()
                .eq(AppUseStats.FIELD_USER_APP, userApp)
                .and()
                .eq(AppUseStats.FIELD_DATE, date);

        PreparedDelete<AppUseStats> preparedQuery = deleteBuilder.prepare();
        return delete(preparedQuery);
    }

    /**
     * Returns summary today time of using category's applications.
     *
     * @param category category, for which you want to get stats
     * @return summary time today of using category's applications
     * @throws SQLException in case of incorrect work with database
     */
    public Long getCategoryTodaySumStats(Category category) throws SQLException {
        return getCategorySumStatsByDate(category, DateTimeUtils.getDateOfCurrentDayBegin());
    }

    public List<CategoryStatsBin> getAllCategoriesSumStatsBinWoDefaultByDate(Date date) throws SQLException {
        List<Category> categories = DbHelperFactory.getHelper().getCategoryDAO().getAllCategoriesWoDefault();
        List<CategoryStatsBin> categoryStatsBins = new ArrayList<>(categories.size());
        for (Category category : categories) {
            categoryStatsBins.add(new CategoryStatsBin(category, getCategorySumStatsByDate(category, date)));
        }

        return categoryStatsBins;
    }

    public List<CategoryStatsBin> getAllCategoriesSumStatsBinWoDefaultByLastWeek() throws SQLException {
        List<Category> categories = DbHelperFactory.getHelper().getCategoryDAO().getAllCategoriesWoDefault();
        List<CategoryStatsBin> categoryStatsBins = new ArrayList<>(categories.size());
        for (Category category : categories) {
            categoryStatsBins.add(new CategoryStatsBin(category, getCategorySumStatsByLastWeek(category)));
        }

        return categoryStatsBins;
    }

    public List<AppStatsBin> getStatsForAllCategoryAppsByDate(Category category, Date date) throws SQLException {
        String query = "SELECT PACKAGE_NAME, Time FROM\n " +
                "(select USER_APP as APP_ID, sum(USAGE_TIME) as Time\n" +
                "from APP_USE_STATS\n" +
                "where USER_APP in (\n" +
                "    select id\n" +
                "    from USER_APP\n" +
                "    where CATEGORY = " + category.getId() + "\n" +
                ")\n" +
                "    and DATE = '" +
                dateRawFormat.format(date) + "' GROUP BY USER_APP) JOIN USER_APP ON APP_ID = ID" +
                " ORDER BY Time DESC";

        return parseUserAppTimeQuery(this.queryRaw(query));
    }

    private static List<AppStatsBin> parseUserAppTimeQuery(GenericRawResults<String[]> results) {
        List<AppStatsBin> parsedResult = new ArrayList<>();
        for (String[] row : results) {
            parsedResult.add(new AppStatsBin(row[0], Long.parseLong(row[1])));
        }

        return parsedResult;
    }

    /**
     * Returns summary today time of using category's applications.
     *
     * @param category category, for which you want to get stats
     * @param date     date, for which you want to get stats
     * @return summary time today of using category's applications
     * @throws SQLException in case of incorrect work with database
     */
    public Long getCategorySumStatsByDate(Category category, Date date) throws SQLException {
        String query = "select sum(USAGE_TIME)\n" +
                "from APP_USE_STATS\n" +
                "where USER_APP in (\n" +
                "    select id\n" +
                "    from USER_APP\n" +
                "    where CATEGORY = " + category.getId() + "\n" +
                "\n" +
                ")\n" +
                "    and DATE = '" +
                dateRawFormat.format(date) + "'";

        List<String[]> results = queryRaw(query).getResults();
        return results.get(0)[0] == null ? 0L : Long.parseLong(results.get(0)[0]);
    }

    public Long getCategorySumStatsByLastWeek(Category category) throws SQLException {
        String query = "select sum(USAGE_TIME)\n" +
                "from APP_USE_STATS\n" +
                "where USER_APP in (\n" +
                "    select id\n" +
                "    from USER_APP\n" +
                "    where CATEGORY = " + category.getId() + "\n" +
                ")\n" +
                "and DATE between '" +
                dateRawFormat.format(DateTimeUtils.getDateOtherDayBegin(-6)) +
                "' and '" +
                dateRawFormat.format(DateTimeUtils.getDateOtherDayBegin(0)) + "'";

        List<String[]> results = queryRaw(query).getResults();
        return results.get(0)[0] == null ? 0L : Long.parseLong(results.get(0)[0]);
    }

    public List<AppStatsBin> getStatsForAllCategoryAppsByLastWeek(Category category) throws SQLException {
        String query = "SELECT PACKAGE_NAME, Time FROM\n " +
                "(select USER_APP as APP_ID, sum(USAGE_TIME) as Time\n" +
                "from APP_USE_STATS\n" +
                "where USER_APP in (\n" +
                "    select id\n" +
                "    from USER_APP\n" +
                "    where CATEGORY = " + category.getId() + "\n" +
                ")\n" +
                "and DATE between '" +
                dateRawFormat.format(DateTimeUtils.getDateOtherDayBegin(-6)) +
                "' and '" +
                dateRawFormat.format(DateTimeUtils.getDateOtherDayBegin(0)) +
                "' GROUP BY USER_APP)" +
                " JOIN USER_APP ON APP_ID = ID" +
                " ORDER BY Time DESC";

        return parseUserAppTimeQuery(this.queryRaw(query));
    }


    /**
     * Clears application usage statistics.
     *
     * @param userApp application
     * @throws SQLException in case of incorrect work with database
     */
    public void removeAppAllStats(UserApp userApp) throws SQLException {
        DeleteBuilder<AppUseStats, Long> queryBuilder = deleteBuilder();
        deleteBuilder().where()
                .eq(AppUseStats.FIELD_USER_APP, userApp);

        PreparedDelete<AppUseStats> preparedQuery = queryBuilder.prepare();
        delete(preparedQuery);
    }
}
