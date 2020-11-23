Samples for MealRestController testing
--------------------------------------
*getAll()*
`curl --location --request GET 'localhost:8080/topjava/rest/meals'`

*get()*
`curl --location --request GET 'localhost:8080/topjava/rest/meals/100005'`

*create()*
`curl --location --request POST 'localhost:8080/topjava/rest/meals' \
--header 'Content-Type: application/json' \
--data-raw '{
    "dateTime": "2020-01-30T20:15:00",
    "description": "Новая еда",
    "calories": 700
}'`

*update()*
`curl --location --request PUT 'localhost:8080/topjava/rest/meals/100007' \
--header 'Content-Type: application/json' \
--data-raw '{
    "dateTime": "2020-01-31T12:50:00",
    "description": "Недоеденный Обед",
    "calories": 800
}'`

*delete()*
`curl --location --request DELETE 'localhost:8080/topjava/rest/meals/100004'`

*getBetween()*
`curl --location --request GET 'localhost:8080/topjava/rest/meals/filter
?startDate=2020-01-30&startTime=10:15&endDate=2020-01-30&endTime=14:15'`

*getBetweenWithoutDates()*
`curl --location --request GET 'localhost:8080/topjava/rest/meals/filter
?startTime=10:15&endTime=14:15'`