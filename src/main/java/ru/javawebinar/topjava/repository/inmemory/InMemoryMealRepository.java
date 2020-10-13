package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryMealRepository.class);

    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public Meal save(int userId, Meal meal) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.computeIfAbsent(userId, userid -> new ConcurrentHashMap<>());
            getUserMeals(userId).put(meal.getId(), meal);
            log.info("save {}", meal);
            return meal;
        }

        Map<Integer, Meal> userMeals = getUserMeals(userId);
        if (userMeals == null) {
            return null;
        }
        return userMeals.computeIfPresent(meal.getId(), (id, oldMeal) -> {
            log.info("update {} to {} with id={}", oldMeal, meal, id);
            return meal;
        });
    }

    @Override
    public boolean delete(int userId, int id) {
        Map<Integer, Meal> userMeals = getUserMeals(userId);
        if (userMeals == null) {
            return false;
        }
        log.info("delete {}", id);
        return userMeals.remove(id) != null;
    }

    @Override
    public Meal get(int userId, int id) {
        Map<Integer, Meal> userMeals = getUserMeals(userId);
        if (userMeals == null) {
            return null;
        }
        log.info("get {}", id);
        return userMeals.get(id);
    }

    @Override
    public List<Meal> getAll(int userId) {
        Map<Integer, Meal> userMeals = getUserMeals(userId);
        if (userMeals == null) {
            return Collections.emptyList();
        }
        log.info("getAll");
        return userMeals.values().stream()
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Meal> getFilteredByDates(int userId, LocalDate startDate, LocalDate endDate) {
        Map<Integer, Meal> userMeals = getUserMeals(userId);
        if (userMeals == null) {
            return Collections.emptyList();
        }
        log.info("getFiltered from {} to {}", startDate, endDate);
        return userMeals.values().stream()
                .filter(meal -> DateTimeUtil.isInside(meal.getDate(), startDate, endDate))
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    private Map<Integer, Meal> getUserMeals(int userId) {
        return repository.get(userId);
    }
}

