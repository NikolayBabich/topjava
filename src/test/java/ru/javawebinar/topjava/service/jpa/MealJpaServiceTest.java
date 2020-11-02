package ru.javawebinar.topjava.service.jpa;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.service.MealServiceTest;

@ActiveProfiles(profiles = Profiles.JPA)
public class MealJpaServiceTest extends MealServiceTest {
/*
    @Test
    public void getWith() {
        service.setRepository(AopTestUtils.getTargetObject(service.getRepository()));
        Meal actual = service.getWith(MEAL1_ID, USER_ID);
        MEAL_MATCHER.assertMatch(actual, meal1);
        USER_MATCHER.assertMatch(actual.getUser(), user);
    }

    @Test
    public void getWithNotOwned() {
        assertThrows(NotFoundException.class, () -> service.getWith(MEAL1_ID, ADMIN_ID));
    }

    @Test
    public void getWithNotFound() {
        assertThrows(NotFoundException.class, () -> service.getWith(NOT_FOUND, USER_ID));
    }
*/
}