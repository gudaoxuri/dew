FROM maven:3.6-jdk-8

# ----------------- Add git client

RUN apt-get install git

# ----------------- Add node
RUN curl -sL https://deb.nodesource.com/setup_8.x | bash - \
    && apt-get install -y nodejs

# Use a custom settings.xml file
# "/opt/maven/settings.xml" will be mounted by kubernetes ConfigMap in the CI/CD process
RUN mkdir -p /opt/maven/ \
    && echo -e '<?xml version="1.0" encoding="UTF-8"?>\n<settings>\n</settings>' > /opt/maven/settings.xml \
    && sed -i 's/\${CLASSWORLDS_LAUNCHER} "$@"/\${CLASSWORLDS_LAUNCHER} -s \/opt\/maven\/settings.xml "$@"/g' /usr/share/maven/bin/mvn

CMD ["sh"]
