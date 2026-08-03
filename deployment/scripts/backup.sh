#!/bin/bash

docker exec mysql mysqldump -uroot -proot --all-databases > backup.sql