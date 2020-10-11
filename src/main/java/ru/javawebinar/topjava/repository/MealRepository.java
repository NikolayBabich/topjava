package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.util.Collection;

public interface MealRepository {
    // null if not found, when updated
    Meal save(Integer userId, Meal meal);

    // false if not found
    boolean delete(Integer userId, int id);

    // null if not found
    Meal get(Integer userId, int id);

    Collection<Meal> getAll(Integer userId);
}
