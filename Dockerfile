FROM hseeberger/scala-sbt:8u212_1.2.8_2.13.0

RUN mkdir -p /gamemodule

ADD GameModule /gamemodule

FROM hseeberger/scala-sbt:8u212_1.2.8_2.13.0

RUN mkdir -p /playermodule

ADD PlayerModule /playermodule

WORKDIR /GameModule

RUN sbt run