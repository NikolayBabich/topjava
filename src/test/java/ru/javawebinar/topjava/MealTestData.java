package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final int NOT_FOUND = 10;
    public static final int MEAL_START_ID = START_SEQ + 2;

    public static final Meal userMeal0 =
            new Meal(MEAL_START_ID + 0, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500);
    public static final Meal userMeal1 =
            new Meal(MEAL_START_ID + 1, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000);
    public static final Meal userMeal2 =
            new Meal(MEAL_START_ID + 2, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500);
    public static final Meal userMeal3 =
            new Meal(MEAL_START_ID + 3, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100);
    public static final Meal userMeal4 =
            new Meal(MEAL_START_ID + 4, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000);
    public static final Meal userMeal5 =
            new Meal(MEAL_START_ID + 5, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500);
    public static final Meal userMeal6 =
            new Meal(MEAL_START_ID + 6, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410);
    public static final Meal adminMeal0 =
            new Meal(MEAL_START_ID + 7, LocalDateTime.of(2015, Month.JUNE, 1, 14, 0), "Админ ланч", 410);
    public static final Meal adminMeal1 =
            new Meal(MEAL_START_ID + 8, LocalDateTime.of(2015, Month.JUNE, 1, 21, 0), "Админ ужин", 1500);

    public static final List<Meal> userMealsSorted = Arrays.asList(userMeal6, userMeal5, userMeal4, userMeal3,
                                                                   userMeal2, userMeal1, userMeal0);
    public static final List<Meal> userMealsFilteredFromDate = Arrays.asList(userMeal6, userMeal5,
                                                                             userMeal4, userMeal3);
    public static final List<Meal> userMealsFilteredBetweenDates = Arrays.asList(userMeal2, userMeal1, userMeal0);

    public static Meal getNew() {
        return new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 23, 0), "Поздний ужин", 150);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(userMeal1);
        updated.setDescription("Ранний завтрак");
        updated.setDateTime(LocalDateTime.of(2020, Month.JANUARY, 30, 9, 0));
        updated.setCalories(750);
        return updated;
    }

    public static <T> void assertMatchByAllFields(T actual, T expected) {
        assertThat(actual).usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .isEqualTo(expected);
    }
}
