FROM openjdk:latest

# Install Java.
RUN \
  apt-get update && \
  apt-get install -y git curl maven jq && \
  apt-get clean && \
  rm -rf /var/lib/apt/lists/* && \
  rm -rf /var/cache/oracle-jdk8-installer

WORKDIR /home

ENV JAVA_TOOL_OPTIONS "-Dfile.encoding=UTF8"
