package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.TestMealData;
import ru.javawebinar.topjava.dao.MapMealDao;
import ru.javawebinar.topjava.dao.MealDao;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.DateTimeUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.javawebinar.topjava.util.MealsUtil.filteredByStreams;

public class MealServlet extends HttpServlet {
    private static final String INSERT_OR_EDIT = "/editMeal.jsp";
    private static final String LIST = "/meals.jsp";
    private static final Logger log = getLogger(MealServlet.class);
    private static final MealDao dao = new MapMealDao();

    @Override
    public void init() {
        TestMealData.getTestMeals().forEach(dao::insert);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String forward = LIST;
        String action = request.getParameter("action");
        if (action != null) {
            switch (action.toLowerCase()) {
                case "delete":
                    dao.delete(Integer.parseInt(request.getParameter("id")));
                    listAllUnfiltered(request);
                    break;
                case "edit":
                    int id = Integer.parseInt(request.getParameter("id"));
                    request.setAttribute("meal", dao.getById(id));
                case "insert":
                    forward = INSERT_OR_EDIT;
                    break;
                default:
                    listAllUnfiltered(request);
            }
        }
        log.debug("forwarding to {}}", forward);
        request.getRequestDispatcher(forward).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        LocalDateTime dateTime = DateTimeUtil.parse(request.getParameter("dateTime"));
        String description = request.getParameter("description");
        String caloriesParameter = request.getParameter("calories");
        int calories = caloriesParameter.isEmpty() ? 0 : Integer.parseInt(caloriesParameter);
        if (dateTime != null && calories > 0) {
            Meal meal = new Meal(dateTime, description, calories);

            String idParameter = request.getParameter("id");
            if (idParameter == null || idParameter.isEmpty()) {
                dao.insert(meal);
            } else {
                meal.setId(Integer.parseInt(idParameter));
                dao.update(meal);
            }
        }

        listAllUnfiltered(request);
        request.getRequestDispatcher(LIST).forward(request, response);
    }

    private void listAllUnfiltered(HttpServletRequest request) {
        request.setAttribute("meals", filteredByStreams(dao.getAll(), LocalTime.MIN, LocalTime.MAX,
                                                        TestMealData.getCaloriesPerDay()));
    }
}
