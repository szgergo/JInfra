FROM openjdk:11-jre-slim
RUN mkdir app
WORKDIR /app
RUN groupadd -r roadmappio && useradd -r -s /bin/false -g roadmappio roadmappio
RUN chown -R roadmappio:roadmappio /app
USER roadmappio
CMD ["tail", "-f", "/dev/null"]