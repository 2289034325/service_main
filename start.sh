#!/bin/bash

java -jar app.jar --spring.profiles.active=$1 --spring.datasource.url="jdbc:mysql://$2:3306/ava?useUnicode=true&characterEncoding=utf8&useSSL=false" --spring.datasource.username=$3 --spring.datasource.password=$4
