FROM openjdk:8-jre

ADD ./target/scala-2.12/service-p2p.jar app.jar

ENV xms=256m

ENV xmx=256m

ENTRYPOINT exec java -server -Xms$xms -Xmx$xmx -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -jar app.jar
