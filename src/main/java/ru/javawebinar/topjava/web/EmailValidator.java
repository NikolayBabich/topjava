package ru.javawebinar.topjava.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.javawebinar.topjava.HasId;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;
import ru.javawebinar.topjava.to.UserTo;

@Component
public class EmailValidator implements Validator {

    @Autowired
    private UserRepository repository;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserTo.class.equals(clazz) || User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        HasId user = (HasId) target;
        User oldUser = user.getClass().equals(UserTo.class)
                       ? repository.getByEmail(((UserTo) user).getEmail())
                       : repository.getByEmail(((User) user).getEmail());
        if (oldUser != null && !oldUser.getId().equals(user.getId())) {
            errors.rejectValue("email", "user.email.duplicate", "User with this email already exists");
        }
    }
}
