package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealDao {
    void insert(Meal meal);

    void update(Meal meal);

    Meal getById(int id);

    List<Meal> getAll();

    void delete(int id);
}
