package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealDao {
    Meal insert(Meal meal);

    Meal update(Meal meal);

    Meal getById(int id);

    List<Meal> getAll();

    void delete(int id);
}
