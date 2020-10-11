package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryMealRepository.class);

    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public Meal save(Integer userId, Meal meal) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.put(meal.getId(), meal);
            log.info("save {}", meal);
            return meal;
        }
        // handle case: update, but not present in storage
        return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> {
            if (checkNotAuthorized(userId, oldMeal)) {
                return null;
            }
            log.info("update {} with id={}", meal, id);
            return meal;
        });
    }

    @Override
    public boolean delete(Integer userId, int id) {
        if (checkNotAuthorized(userId, repository.get(id))) {
            return false;
        }
        log.info("delete {}", id);
        return repository.remove(id) != null;
    }

    @Override
    public Meal get(Integer userId, int id) {
        if (checkNotAuthorized(userId, repository.get(id))) {
            return null;
        }
        log.info("get {}", id);
        return repository.get(id);
    }

    @Override
    public Collection<Meal> getAll(Integer userId) {
        log.info("getAll");
        return repository.values().stream()
                .filter(meal -> userId.equals(meal.getUserId()))
                .sorted(Comparator.comparing(Meal::getDate).reversed())
                .collect(Collectors.toList());
    }

    private boolean checkNotAuthorized(Integer userId, Meal meal) {
        if (meal == null) {
            return true;
        }
        return !userId.equals(meal.getUserId());
    }
}

