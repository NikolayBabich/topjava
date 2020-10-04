package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.MapMealDao;
import ru.javawebinar.topjava.dao.MealDao;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

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
    private static final String INSERT_OR_EDIT = "editMeal.jsp";
    private static final String LIST = "meals.jsp";
    private static final String SERVLET = "meals";
    private static final Logger log = getLogger(MealServlet.class);

    private MealDao dao;

    @Override
    public void init() {
        dao = new MapMealDao();
        MealsUtil.getTestMeals().forEach(dao::insert);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("edit".equalsIgnoreCase(action)) {
            int id = parseId(request);
            if (id > 0) {
                request.setAttribute("meal", dao.getById(id));
            }
            log.debug("forwarding to {}}", INSERT_OR_EDIT);
            request.getRequestDispatcher(INSERT_OR_EDIT).forward(request, response);
        } else if ("delete".equalsIgnoreCase(action)) {
            dao.delete(parseId(request));
            log.debug("redirecting to {}}", SERVLET);
            response.sendRedirect(SERVLET);
        } else {
            setAttributeAllUnfiltered(request);
            request.getRequestDispatcher(LIST).forward(request, response);
        }
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
                meal.setId(parseId(request));
                dao.update(meal);
            }
        }

        setAttributeAllUnfiltered(request);
        request.getRequestDispatcher(LIST).forward(request, response);
    }

    private void setAttributeAllUnfiltered(HttpServletRequest request) {
        request.setAttribute("meals", filteredByStreams(dao.getAll(), LocalTime.MIN, LocalTime.MAX,
                                                        MealsUtil.getCaloriesPerDay()));
    }

    private int parseId(HttpServletRequest request) {
        return Integer.parseInt(request.getParameter("id"));
    }
}
