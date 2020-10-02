<%--suppress ELValidationInJSP --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="ru">
<head>
    <title>Meal</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Edit meal</h2>
<form method="post" action="meals">
    <input type="hidden" name="id" value="${meal.id}"/>
    DateTime: <input type="datetime-local" name="dateTime" value="${meal.dateTime}"/>
    <br>
    Description: <input type="text" name="description" size="20" value="${meal.description}"/>
    <br>
    Calories: <input type="number" name="calories" min="1" value="${meal.calories}"/>
    <br>
    <input type="submit" value="Save"/>
</form>
<button onclick="window.history.back()">Cancel</button>
</body>
</html>
