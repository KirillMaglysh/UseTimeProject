package ru.mksoft.android.use.time.use.time.use.time.motivator.model.db;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Category;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 18.05.2022
 */
@AllArgsConstructor
@Getter
public class CategoryStatsBin {
    private Category category;
    private Long usedTime;
}
