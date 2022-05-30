package ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Rule;

import java.sql.SQLException;
import java.util.List;

/**
 * Rule data access object.
 *
 * @author Kirill
 * @since 11.02.2022
 */
public class RuleDAO extends BaseDaoImpl<Rule, Long> {
    protected RuleDAO(ConnectionSource connectionSource, Class<Rule> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    /**
     * Returns all rules.
     *
     * @return all rules
     * @throws SQLException in case of incorrect work with database
     */
    public List<Rule> getAllRules() throws SQLException {
        return this.queryForAll();
    }

    @Override
    public Rule queryForId(@SuppressWarnings("MethodParameterNamingConvention") Long id) throws SQLException {
        return super.queryForId(id);
    }
}
