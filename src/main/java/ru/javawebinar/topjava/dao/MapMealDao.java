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
    public Meal insert(Meal meal) {
        int id = index.incrementAndGet();
        meal.setId(id);
        log.debug("inserting meal id={}", id);
        meals.put(id, meal);
        return meal;
    }

    @Override
    public Meal update(Meal meal) {
        int id = meal.getId();
        log.debug("updating meal id={}", id);
        return meals.computeIfPresent(id, (k, v) -> meal);
    }

    @Override
    public Meal getById(int id) {
        return meals.getOrDefault(id, null);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(meals.values());
    }

    @Override
    public void delete(int id) {
        log.debug("deleting meal id={}", id);
        meals.remove(id);
    }
}
