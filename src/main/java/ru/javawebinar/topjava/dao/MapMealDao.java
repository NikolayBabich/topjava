package ru.javawebinar.topjava.dao;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.slf4j.LoggerFactory.getLogger;

public class MapMealDao implements MealDao {
    private static final AtomicInteger index = new AtomicInteger();
    private static final Logger log = getLogger(MapMealDao.class);
    private final Map<Integer, Meal> meals = new ConcurrentHashMap<>();

    @Override
    public void insert(Meal meal) {
        int id = index.incrementAndGet();
        meal.setId(id);
        checkKeyNotExist(id);
        log.debug("inserting meal id={}", id);
        meals.put(id, meal);
    }

    @Override
    public void update(Meal meal) {
        int id = meal.getId();
        checkKeyExist(id);
        log.debug("updating meal id={}", id);
        meals.put(id, meal);
    }

    @Override
    public Meal getById(int id) {
        checkKeyExist(id);
        return meals.get(id);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(meals.values());
    }

    @Override
    public void delete(int id) {
        checkKeyExist(id);
        log.debug("deleting meal id={}", id);
        meals.remove(id);
    }

    private void checkKeyNotExist(int id) {
        if (meals.containsKey(id)) {
            throw new IllegalArgumentException("Meal id=" + id + "not exists");
        }
    }

    private void checkKeyExist(int id) {
        if (!meals.containsKey(id)) {
            throw new IllegalArgumentException();
        }
    }
}
