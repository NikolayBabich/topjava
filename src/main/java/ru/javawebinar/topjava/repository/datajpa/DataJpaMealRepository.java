package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DataJpaMealRepository implements MealRepository {

    private final CrudMealRepository crudRepository;

    @Autowired
    private CrudUserRepository userRepository;

    public DataJpaMealRepository(CrudMealRepository crudRepository) {
        this.crudRepository = crudRepository;
    }

    @Override
    @Transactional
    public Meal save(Meal meal, int userId) {
        //noinspection ConstantConditions
        if (!meal.isNew() && get(meal.getId(), userId) == null) {
            return null;
        }
        meal.setUser(userRepository.getOne(userId));
        return crudRepository.save(meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        return crudRepository.delete(id, userId) != 0;
    }

    @Override
    @Transactional
    public Meal get(int id, int userId) {
        User user = userRepository.getOne(userId);
        return crudRepository.findByIdAndUser(id, user);
    }

    @Override
    @Transactional
    public List<Meal> getAll(int userId) {
        User user = userRepository.getOne(userId);
        return crudRepository.findAllByUser(user, Sort.by("dateTime").descending());
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return crudRepository.findAllBetweenInclusive(userId, startDateTime, endDateTime);
    }

    @Override
    public Meal getWith(int id, int userId) {
        return crudRepository.getWith(id, userId);
    }
}
