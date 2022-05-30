package ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Property;

import java.sql.SQLException;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 08.03.2022
 */
public class PropertyDAO extends BaseDaoImpl<Property, Long> {
    protected PropertyDAO(ConnectionSource connectionSource, Class<Property> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    /**
     * Updates strike and level, if strike value is more than max possible value.
     *
     * @param strikeChange number of strike change
     * @throws SQLException in case of incorrect work with database
     */
    public void updateStrike(int strikeChange) throws SQLException {
        Property strike = queryForId(Property.STRIKE_FIELD_ID);
        Long strikeValue = strike.getValue();
        strikeValue += strikeChange;
        if (Math.abs(strikeValue) > Property.MAX_STRIKE) {
            long levelChange = strikeValue / 7;
            updateLevel(levelChange);
            strikeValue -= levelChange * 7;
        }

        strike.setValue(strikeValue);
        update(strike);
    }

    private void updateLevel(Long levelChange) throws SQLException {
        Property level = queryForId(Property.USER_LEVEL_FIELD_ID);
        level.setValue(Math.max(level.getValue() + levelChange, 0));
        update(level);
    }
}
