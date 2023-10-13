FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install openjdk-17-jdk -y

COPY . /todolist

RUN apt-get install maven -y

RUN mvn clean install

EXPOSE 8080

COPY --from=build /todolist/target/todolist-0.0.1-SNAPSHOT.jar /todolist.jar

ENTRYPOINT ["java", "-jar", "/todolist.jar"]