FROM postgres:13.9-alpine3.16
COPY create-databases.sh /docker-entrypoint-initdb.d/
RUN chmod +x /docker-entrypoint-initdb.d/create-databases.sh
