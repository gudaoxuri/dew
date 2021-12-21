FROM maven:3.6.3-jdk-11

# ----------------- Add git client

RUN apt-get install git

# ----------------- Add node
RUN curl -sL https://deb.nodesource.com/setup_12.x | bash - \
    && apt-get install -y nodejs

# ----------------- Add maven agent
ARG MAVEN_AGENT_URL=https://oss.sonatype.org/content/repositories/public/group/idealworld/dew/dew-maven-agent/3.0.0-Beta3/dew-maven-agent-3.0.0-Beta3.jar

RUN mkdir -p /opt/maven/ \
    && mkdir -p /opt/jar/ \
    && curl -o /opt/jar/dew-maven-agent.jar $MAVEN_AGENT_URL

# Use a custom settings.xml file
# "/opt/maven/settings.xml" will be mounted by kubernetes ConfigMap in the CI/CD process
RUN echo '<?xml version="1.0" encoding="UTF-8"?>\n<settings>\n</settings>' > /opt/maven/settings.xml \
    && sed -i 's/\${CLASSWORLDS_LAUNCHER} "$@"/\${CLASSWORLDS_LAUNCHER} -s \/opt\/maven\/settings.xml "$@"/g' /usr/share/maven/bin/mvn

ENV MAVEN_OPTS="-javaagent:/opt/jar/dew-maven-agent.jar -Dmaven.repo.local=.m2 -Dorg.apache.maven.user-settings=/opt/maven/settings.xml"

CMD ["sh"]
