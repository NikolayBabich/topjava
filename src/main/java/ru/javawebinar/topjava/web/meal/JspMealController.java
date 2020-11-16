package ru.javawebinar.topjava.web.meal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.javawebinar.topjava.model.Meal;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

@Controller
@RequestMapping("/meals")
public class JspMealController extends AbstractMealController {

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("meals", super.getAll());
        return "/meals";
    }

    @PostMapping
    public String update(HttpServletRequest request, Model model) {
        String idParam = request.getParameter("id");
        int id = (idParam == null) ? 0 : Integer.parseInt(idParam);
        final Meal meal = (id == 0) ? new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "---", 1000)
                                    : super.get(id);
        model.addAttribute("meal", meal);
        return "/mealForm";
    }

    @PostMapping("/mealForm")
    public String update(HttpServletRequest request) {
        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));
        String paramId = request.getParameter("id");
        if (StringUtils.hasText(paramId)) {
            super.update(meal, Integer.parseInt(paramId));
        } else {
            super.create(meal);
        }
        return "redirect:/meals";
    }

    @GetMapping("/delete")
    public String delete(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        super.delete(Integer.parseInt(paramId));
        return "redirect:/meals";
    }

    @GetMapping("/filter")
    public String filter(HttpServletRequest request, Model model) {
        LocalDate startDate = parseLocalDate(request.getParameter("startDate"));
        LocalDate endDate = parseLocalDate(request.getParameter("endDate"));
        LocalTime startTime = parseLocalTime(request.getParameter("startTime"));
        LocalTime endTime = parseLocalTime(request.getParameter("endTime"));
        model.addAttribute("meals", super.getBetween(startDate, startTime, endDate, endTime));
        return "/meals";
    }
}
