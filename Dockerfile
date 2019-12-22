FROM openjdk:8-jdk-alpine

EXPOSE 8086
EXPOSE 4001

#COPY ./img2019_08_22_12_43_07_911099.jpg /tmp/img.jpg

CMD java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=4001,suspend=n -jar /nexign/pipe-events-provider-0.0.1-SNAPSHOT.jar
COPY ./archive2.zip /tmp/archive2.zip
COPY ./background.jpg /tmp/background.jpg
COPY ./background1.jpg /tmp/background1.jpg
ADD ./target/pipe-events-provider-0.0.1-SNAPSHOT.jar /nexign/pipe-events-provider-0.0.1-SNAPSHOT.jar
