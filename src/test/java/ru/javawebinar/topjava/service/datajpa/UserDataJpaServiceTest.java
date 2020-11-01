package ru.javawebinar.topjava.service.datajpa;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.service.UserServiceTest;

@ActiveProfiles(profiles = Profiles.DATAJPA)
public class UserDataJpaServiceTest extends UserServiceTest {
/*
    @Override
    @Transactional
    @Test
    public void get() {
        User actual = service.get(USER_ID);
        USER_MATCHER.assertMatch(actual, user);
        MEAL_MATCHER.assertMatch(actual.getMeals(), meals);
    }*/
}