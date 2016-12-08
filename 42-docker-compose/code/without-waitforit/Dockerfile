FROM openjdk:8
MAINTAINER "Shekhar Gulati"
ENV APP_DIR /app
ADD taskman.jar $APP_DIR/
WORKDIR $APP_DIR
EXPOSE 8080
CMD ["java","-jar","taskman.jar","--spring.profiles.active=docker"]
