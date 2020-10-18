package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final int MEAL_START_ID = START_SEQ + 2;

    public static final List<Meal> userMeals = Arrays.asList(
            new Meal(MEAL_START_ID + 0, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
            new Meal(MEAL_START_ID + 1, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
            new Meal(MEAL_START_ID + 2, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
            new Meal(MEAL_START_ID + 3, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
            new Meal(MEAL_START_ID + 4, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
            new Meal(MEAL_START_ID + 5, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
            new Meal(MEAL_START_ID + 6, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
    );

    public static final List<Meal> adminMeals = Arrays.asList(
            new Meal(MEAL_START_ID + 7, LocalDateTime.of(2015, Month.JUNE, 1, 14, 0), "Админ ланч", 410),
            new Meal(MEAL_START_ID + 8, LocalDateTime.of(2015, Month.JUNE, 1, 21, 0), "Админ ужин", 1500)
    );

    public static Meal getNew() {
        return new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 23, 0), "Поздний ужин", 150);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(userMeals.get(0));
        updated.setDescription("Ранний завтрак");
        updated.setDateTime(updated.getDateTime().minusHours(1));
        return updated;
    }

    public static List<Meal> getFilteredByDate() {
        return filterByPredicate(meal -> meal.getDate().equals(LocalDate.of(2020, Month.JANUARY, 30)));
    }

    public static List<Meal> getAllSorted() {
        return filterByPredicate(meal -> true);
    }

    private static List<Meal> filterByPredicate(Predicate<Meal> filter) {
        return userMeals.stream()
                .filter(filter)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    public static <T> void assertMatchByAllFields(T actual, T expected) {
        assertThat(actual).usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .isEqualTo(expected);
    }
}
