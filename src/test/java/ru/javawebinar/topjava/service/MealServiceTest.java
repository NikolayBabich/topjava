package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.NOT_FOUND;
import static ru.javawebinar.topjava.MealTestData.adminMeal0;
import static ru.javawebinar.topjava.MealTestData.adminMeal1;
import static ru.javawebinar.topjava.MealTestData.assertMatchByAllFields;
import static ru.javawebinar.topjava.MealTestData.getNew;
import static ru.javawebinar.topjava.MealTestData.getUpdated;
import static ru.javawebinar.topjava.MealTestData.userMeal3;
import static ru.javawebinar.topjava.MealTestData.userMealsFilteredBetweenDates;
import static ru.javawebinar.topjava.MealTestData.userMealsFilteredFromDate;
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
        Meal meal = service.get(userMeal3.getId(), USER_ID);
        assertMatchByAllFields(meal, userMeal3);
    }

    @Test
    public void getNotOwned() {
        assertThrows(NotFoundException.class, () -> service.get(adminMeal0.getId(), USER_ID));
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND, USER_ID));
    }

    @Test
    public void delete() {
        service.delete(userMeal3.getId(), USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(userMeal3.getId(), USER_ID));
    }

    @Test
    public void deleteNotOwned() {
        assertThrows(NotFoundException.class, () -> service.delete(adminMeal0.getId(), USER_ID));
    }

    @Test
    public void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND, USER_ID));
    }

    @Test
    public void getBetweenInclusive() {
        List<Meal> mealsFiltered = service.getBetweenInclusive(LocalDate.of(2020, Month.JANUARY, 1),
                                                               LocalDate.of(2020, Month.JANUARY, 30),
                                                               USER_ID);
        assertMatchByAllFields(mealsFiltered, userMealsFilteredBetweenDates);
    }

    @Test
    public void getBetweenInclusiveFromDate() {
        List<Meal> mealsFiltered = service.getBetweenInclusive(LocalDate.of(2020, Month.JANUARY, 31),
                                                               null,
                                                               USER_ID);
        assertMatchByAllFields(mealsFiltered, userMealsFilteredFromDate);
    }

    @Test
    public void getBetweenInclusiveToDate() {
        List<Meal> mealsFiltered = service.getBetweenInclusive(null,
                                                               LocalDate.of(2020, Month.JANUARY, 29),
                                                               USER_ID);
        assertThat(mealsFiltered).isEmpty();
    }

    @Test
    public void getBetweenInclusiveNoBounds() {
        List<Meal> mealsFiltered = service.getBetweenInclusive(null, null, USER_ID);
        assertMatchByAllFields(mealsFiltered, userMealsSorted);
    }

    @Test
    public void getAll() {
        List<Meal> all = service.getAll(USER_ID);
        assertMatchByAllFields(all, userMealsSorted);
    }

    @Test
    public void update() {
        Meal updated = getUpdated();
        int id = updated.getId();
        service.update(updated, USER_ID);
        assertMatchByAllFields(service.get(id, USER_ID), getUpdated());
    }

    @Test
    public void updateNotOwned() {
        assertThrows(NotFoundException.class, () -> service.update(getUpdated(), ADMIN_ID));
    }

    @Test
    public void updateNotFound() {
        assertThrows(NotFoundException.class, () ->
                service.update(new Meal(NOT_FOUND, LocalDateTime.now(), "Not found", 100), ADMIN_ID));
    }

    @Test
    public void create() {
        Meal created = service.create(getNew(), USER_ID);
        Integer newId = created.getId();
        Meal newMeal = getNew();
        newMeal.setId(newId);
        assertMatchByAllFields(created, newMeal);
        assertMatchByAllFields(service.get(newId, USER_ID), newMeal);
    }

    @Test
    public void duplicateDateTimeCreate() {
        assertThrows(DataAccessException.class, () ->
                service.create(new Meal(adminMeal1.getDateTime(), "Duplicate", 1200), ADMIN_ID));
    }
}
