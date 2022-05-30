package ru.mksoft.android.use.time.use.time.use.time.motivator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.UserApp;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 08.05.2022
 */
@AllArgsConstructor
@Getter
public class AppStatsBin {
    private String packageName;
    private Long usedTime;
}
