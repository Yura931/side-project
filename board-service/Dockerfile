FROM openjdk:17-ea-slim
VOLUME /tmp
COPY build/libs/board-service-1.0.jar BoardService.jar
ENTRYPOINT ["java","-jar","BoardService.jar"]