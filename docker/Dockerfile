FROM alpine/git:latest as source_fetch

RUN git clone https://github.com/ornl-epics/dbwr.git /dbwr
ARG GIT_TAG=main
RUN cd /dbwr && git checkout ${GIT_TAG}

FROM maven:3.8.7-eclipse-temurin-17 as maven_builder

COPY --from=source_fetch /dbwr /dbwr
RUN cd /dbwr && mvn clean package 

FROM tomcat:9.0-jdk17

ARG PORT_NUMBER=8080
COPY --from=maven_builder /dbwr/target/dbwr.war ${CATALINA_HOME}/webapps
COPY ./setenv.sh ${CATALINA_HOME}/bin
RUN sed -i.bak -e "s|Connector port=\"8080\"|Connector port=\"${PORT_NUMBER}\"|g" \
  -e 's|Server port="8005" shutdown="SHUTDOWN"|Server port="-1" shutdown="SHUTDOWN"|g' \
   ${CATALINA_HOME}/conf/server.xml
