FROM gradle:6.8

COPY . /home/gradle/

RUN pwd && gradle clean && gradle build
