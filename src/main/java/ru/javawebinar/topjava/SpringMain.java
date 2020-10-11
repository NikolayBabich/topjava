package ru.javawebinar.topjava;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.web.meal.MealRestController;

import java.time.LocalDateTime;

public class SpringMain {
    public static void main(String[] args) {
        // java 7 automatic resource management (ARM)
        try (ConfigurableApplicationContext appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml")) {
            MealRestController mealRestController = appCtx.getBean(MealRestController.class);

            mealRestController.create(new Meal(LocalDateTime.now().minusDays(1), "Вчерашняя еда", 500));
            mealRestController.create(new Meal(LocalDateTime.now().plusDays(1), "Завтрашняя еда", 1500));
            mealRestController.create(new Meal(LocalDateTime.now(), "Сегодняшняя еда", 300));
            System.out.println(mealRestController.getAll() + System.lineSeparator());

            Meal testMeal = new Meal(LocalDateTime.now(), "Абсолютно новая еда", 1000);
            testMeal.setId(1);
            mealRestController.update(testMeal, 1);
            System.out.println(mealRestController.getAll() + System.lineSeparator());

//            mealRestController.delete(4);
            mealRestController.delete(2);
            System.out.println(mealRestController.getAll() + System.lineSeparator());
        }
    }
}
