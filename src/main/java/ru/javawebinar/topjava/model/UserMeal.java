package ru.javawebinar.topjava.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class UserMeal {
    private final LocalDateTime dateTime;

    private final String description;

    private final int calories;

    public UserMeal(LocalDateTime dateTime, String description, int calories) {
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDescription() {
        return description;
    }

    public int getCalories() {
        return calories;
    }

    public LocalDate getDate() {
        return getDateTime().toLocalDate();
    }

    public LocalTime getTime() {
        return getDateTime().toLocalTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserMeal userMeal = (UserMeal) o;

        if (calories != userMeal.calories) return false;
        if (!dateTime.equals(userMeal.dateTime)) return false;
        return description.equals(userMeal.description);
    }

    @Override
    public int hashCode() {
        int result = dateTime.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + calories;
        return result;
    }
}
