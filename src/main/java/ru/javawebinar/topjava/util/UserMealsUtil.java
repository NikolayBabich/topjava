package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.util.TimeUtil.isBetweenHalfOpen;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = optionalFilteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println();
        System.out.println(optionalFilteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals,
                                                            LocalTime startTime, LocalTime endTime,
                                                            int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesByDate = new HashMap<>();
        for (UserMeal meal : meals) {
            caloriesByDate.merge(meal.getDate(), meal.getCalories(), Integer::sum);
        }
        List<UserMealWithExcess> result = new ArrayList<>();
        for (UserMeal meal : meals) {
            final LocalDateTime mealDateTime = meal.getDateTime();
            if (isBetweenHalfOpen(meal.getTime(), startTime, endTime)) {
                result.add(new UserMealWithExcess(mealDateTime, meal.getDescription(), meal.getCalories(),
                                                  caloriesByDate.get(meal.getDate()) > caloriesPerDay));
            }
        }
        return result;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals,
                                                             LocalTime startTime, LocalTime endTime,
                                                             int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesByDate = meals.stream()
                .collect(Collectors.toMap(UserMeal::getDate, UserMeal::getCalories, Integer::sum));

        return meals.stream()
                .filter(meal -> isBetweenHalfOpen(meal.getTime(), startTime, endTime))
                .map(meal -> new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(),
                                                    caloriesByDate.get(meal.getDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> optionalFilteredByCycles(List<UserMeal> meals,
                                                                    LocalTime startTime, LocalTime endTime,
                                                                    int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesByDate = new HashMap<>();
        Map<LocalDate, AtomicBoolean> excessByDate = new HashMap<>();
        List<UserMealWithExcess> result = new ArrayList<>();
        for (UserMeal meal : meals) {
            LocalDate date = meal.getDate();
            excessByDate.computeIfAbsent(date, d -> new AtomicBoolean());
            Integer totalDayCalories = caloriesByDate.merge(date, meal.getCalories(), Integer::sum);
            if (totalDayCalories > caloriesPerDay) {
                excessByDate.get(date).set(true);
            }
            if (isBetweenHalfOpen(meal.getTime(), startTime, endTime)) {
                result.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(),
                                                  excessByDate.get(date)));
            }
        }
        return result;
    }

    public static List<UserMealWithExcess> optionalFilteredByStreams(List<UserMeal> meals,
                                                                     LocalTime startTime, LocalTime endTime,
                                                                     int caloriesPerDay) {
        return meals.stream()
                .collect(Collectors.groupingBy(UserMeal::getDate))
                .values().stream()
                .flatMap(dayMeals -> {
                    boolean isExceeded = dayMeals.stream()
                            .mapToInt(UserMeal::getCalories)
                            .sum() > caloriesPerDay;
                    return dayMeals.stream()
                            .filter(meal -> isBetweenHalfOpen(meal.getTime(), startTime, endTime))
                            .map(meal -> new UserMealWithExcess(meal.getDateTime(), meal.getDescription(),
                                                                meal.getCalories(), isExceeded));
                })
                .collect(Collectors.toList());
    }
}
