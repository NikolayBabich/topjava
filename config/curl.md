## Examples of REST API processing meals data for authorized user

##### Receive all meals list for current user (with an indicator of daily calories excess)
`curl --location --request GET 'localhost:8080/topjava/rest/meals'`

##### Receive the meal #100005
`curl --location --request GET 'localhost:8080/topjava/rest/meals/100005'`

##### Create a new meal
`curl --location --request POST 'localhost:8080/topjava/rest/meals' \
--header 'Content-Type: application/json' \
--data-raw '{
    "dateTime": "2020-01-30T20:15:00",
    "description": "Новая еда",
    "calories": 700
}'`

##### Update the meal #100007 (if exists) 
`curl --location --request PUT 'localhost:8080/topjava/rest/meals/100007' \
--header 'Content-Type: application/json' \
--data-raw '{
    "dateTime": "2020-01-31T12:50:00",
    "description": "Недоеденный Обед",
    "calories": 800
}'`

##### Delete the meal #100004 (if exists)
`curl --location --request DELETE 'localhost:8080/topjava/rest/meals/100004'`

##### Receive meals list filtered from 30.01.2020 to 30.01.2020 and from 10:15 to 14:15 (with an indicator of daily calories excess)
`curl --location --request GET 'localhost:8080/topjava/rest/meals/filter
?startDate=2020-01-30&startTime=10:15&endDate=2020-01-30&endTime=14:15'`

##### Receive meals list filtered by time from 10:15 to 14:15 (with an indicator of daily calories excess)
`curl --location --request GET 'localhost:8080/topjava/rest/meals/filter
?startTime=10:15&endTime=14:15'`