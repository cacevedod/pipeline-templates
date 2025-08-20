FROM jenkins/jenkins:lts

USER root

RUN apt-get update \
    && apt-get install -y docker.io \
    && apt-get install -y python3 python3-pip python3-venv \
    && apt-get install -y python3-full
# && rm -rf /var/lib/apt/lists/* # Activar si se quiere que no se instale nada por fuera del Dockerfile

COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli -f /usr/share/jenkins/ref/plugins.txt

USER jenkins
