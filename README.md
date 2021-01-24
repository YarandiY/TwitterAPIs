- POST users/signup
request : username, email, password 
response : id
username and email should be unique if not -> 400

- POST users/login
request : username, password
response : token
if the password was wrong -> 401

- GET users/{id}/get 
response : username
if the id was wrong -> 400

** Other APIs need token -> header name : auth
