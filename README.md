# tilo


Create admin

curl -XPUT 'http://localhost:9200/users/user/admin@yourorganization.com' -d '{
    "id" : "admin@yourorganization.com",
    "name" : "Administrator",
    "roles" : ["administrator"],
    "password" : "admin"
}'
