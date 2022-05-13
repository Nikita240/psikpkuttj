# syntax = docker/dockerfile:experimental

# ******************************************************************************

# gRPC build container
FROM ubuntu:kinetic as gprc_build

WORKDIR /opt/grpc

RUN apt update && DEBIAN_FRONTEND="noninteractive" apt install --no-install-recommends -y  \
    ca-certificates \
    cmake \
    build-essential \
    autoconf \
    libtool \
    pkg-config \
    git \
	# Cleanup apt stuff
	&& rm -rf /var/lib/apt/lists/* && \
	apt autoremove -y && \
	apt-get autoclean -y

RUN git clone --recurse-submodules -b v1.45.0 --depth 1 --shallow-submodules https://github.com/grpc/grpc .

ENV INSTALL_DIR="/opt/grpc/install"

# Build gprc
RUN mkdir -p cmake/build \
    && cd cmake/build \
    && cmake -DgRPC_INSTALL=ON \
      -DgRPC_BUILD_TESTS=OFF \
      -DCMAKE_INSTALL_PREFIX=$INSTALL_DIR \
      ../.. \
    && make -j \
    && make install \
    && cd ../..

# Build examples
RUN cd examples/cpp/helloworld \
    && mkdir -p cmake/build \
    && cd cmake/build \
    && cmake -DCMAKE_INSTALL_PREFIX=$INSTALL_DIR ../.. \
    && make -j \
    && cd ../..

# ******************************************************************************

# hello world test client
FROM ubuntu:kinetic as hello_world_client

WORKDIR /opt/client

ENV PATH="${PATH}:/opt/client/bin"

COPY --from=gprc_build /opt/grpc/install /usr/local
COPY --from=gprc_build /opt/grpc/examples/cpp/helloworld/cmake/build ./bin

CMD "greeter_client"

# ******************************************************************************

# server build container
FROM ubuntu:kinetic as nikitadb_build

WORKDIR /opt/nikitadb

RUN apt update && DEBIAN_FRONTEND="noninteractive" apt install --no-install-recommends -y  \
    cmake \
    build-essential \
	# Cleanup apt stuff
	&& rm -rf /var/lib/apt/lists/* && \
	apt autoremove -y && \
	apt-get autoclean -y

COPY --from=gprc_build /opt/grpc/install /usr/local

COPY ./nikitadb ./
COPY ./protos ../protos

RUN mkdir -p build \
    && cd build \
    && cmake .. \
    && make -j \
    && cd ..

ENV PATH="${PATH}:/opt/nikitadb/build"

EXPOSE 50051

CMD "greeter_server"
