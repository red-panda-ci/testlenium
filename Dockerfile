FROM maven:3.6.1-jdk-8-slim

# Install Java.
RUN \
  apt-get update && \
  apt-get install -y git curl jq && \
  apt-get clean && \
  rm -rf /var/lib/apt/lists/*

WORKDIR /home

ENV JAVA_TOOL_OPTIONS "-Dfile.encoding=UTF8"

