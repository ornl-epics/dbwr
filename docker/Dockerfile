FROM alpine/git:latest as source_fetch

RUN git clone https://github.com/fermi-ad/dbwr.git /dbwr
ARG GIT_TAG=fnal
RUN cd /dbwr && git checkout ${GIT_TAG}

RUN git clone https://github.com/ornl-epics/pvws.git /pvws
RUN cd /pvws && git checkout main

FROM maven:3.8.7-eclipse-temurin-17 as maven_builder

COPY --from=source_fetch /pvws /pvws
COPY --from=source_fetch /dbwr /dbwr

RUN cd /dbwr && mvn clean package
RUN cd /pvws && mvn clean package

FROM tomcat:9.0-jdk17

ARG PORT_NUMBER=8080

# Keep image up to date
RUN apt-get update && apt-get install -y net-tools && \
    rm -rf /var/lib/apt/lists/*

COPY --from=maven_builder /dbwr/target/dbwr.war ${CATALINA_HOME}/webapps
COPY --from=maven_builder /pvws/target/pvws.war ${CATALINA_HOME}/webapps

RUN mkdir /displays && \
    ln -s /usr/local/epics/Config/CSS/ /displays

COPY ./setenv.sh ${CATALINA_HOME}/bin
RUN chmod +x ${CATALINA_HOME}/bin/setenv.sh
RUN sed -i.bak -e "s|Connector port=\"8080\"|Connector port=\"${PORT_NUMBER}\"|g" \
  -e 's|Server port="8005" shutdown="SHUTDOWN"|Server port="-1" shutdown="SHUTDOWN"|g' \
   ${CATALINA_HOME}/conf/server.xml
