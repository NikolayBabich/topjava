package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.MEAL_3;
import static ru.javawebinar.topjava.MealTestData.MEAL_7;
import static ru.javawebinar.topjava.MealTestData.assertMatchByAllFields;
import static ru.javawebinar.topjava.MealTestData.getNew;
import static ru.javawebinar.topjava.MealTestData.getUpdated;
import static ru.javawebinar.topjava.MealTestData.userMealsFilteredAndSorted;
import static ru.javawebinar.topjava.MealTestData.userMealsSorted;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void get() {
        Meal meal = service.get(MEAL_3.getId(), USER_ID);
        assertMatchByAllFields(meal, MEAL_3);
    }

    @Test
    public void getNotOwned() {
        assertThrows(NotFoundException.class, () -> service.get(MEAL_7.getId(), USER_ID));
    }

    @Test
    public void delete() {
        service.delete(MEAL_3.getId(), USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(MEAL_3.getId(), USER_ID));
    }

    @Test
    public void deleteNotOwned() {
        assertThrows(NotFoundException.class, () -> service.delete(MEAL_7.getId(), USER_ID));
    }

    @Test
    public void getBetweenInclusive() {
        List<Meal> mealsFiltered = service.getBetweenInclusive(LocalDate.of(2020, Month.JANUARY, 1),
                                                               LocalDate.of(2020, Month.JANUARY, 30),
                                                               USER_ID);
        assertMatchByAllFields(mealsFiltered, userMealsFilteredAndSorted);
    }

    @Test
    public void getAll() {
        List<Meal> all = service.getAll(USER_ID);
        assertMatchByAllFields(all, userMealsSorted);
    }

    @Test
    public void update() {
        Meal updated = getUpdated();
        service.update(updated, USER_ID);
        assertMatchByAllFields(service.get(updated.getId(), USER_ID), updated);
    }

    @Test
    public void updateNotOwned() {
        assertThrows(NotFoundException.class, () -> service.update(getUpdated(), ADMIN_ID));
    }

    @Test
    public void create() {
        Meal newMeal = getNew();
        Meal created = service.create(newMeal, USER_ID);
        Integer newId = created.getId();
        newMeal.setId(newId);
        assertMatchByAllFields(created, newMeal);
        assertMatchByAllFields(service.get(newId, USER_ID), newMeal);
    }
}
