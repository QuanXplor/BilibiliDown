FROM ubuntu:23.10 as builder1
ENV LINK_X86_64=https://github.com/nICEnnnnnnnLee/FFmpeg-Builds/releases/download/autobuild-2022-11-28-14-31/ffmpeg-N-109250-gbecbb22eb0-linux64-lgpl.tar.xz
ENV LINK_AMD64=https://github.com/nICEnnnnnnnLee/FFmpeg-Builds/releases/download/autobuild-2022-11-28-14-31/ffmpeg-N-109250-gbecbb22eb0-linux64-lgpl.tar.xz
ENV LINK_AARCH64=https://johnvansickle.com/ffmpeg/releases/ffmpeg-release-arm64-static.tar.xz
ENV LINK_ARM64=https://johnvansickle.com/ffmpeg/releases/ffmpeg-release-arm64-static.tar.xz
WORKDIR /opt
RUN apt update && apt install -y wget xz-utils && \
wget $(env| grep LINK_`uname -m | tr a-z  A-Z` |cut -d "=" -f 2) && \
tar -xf *.xz &&\
rm -rf *.xz &&\
find ./ -name ffmpeg -exec mv {} /opt \; && \
chmod +x /opt/ffmpeg

FROM ubuntu:23.10 as builder
ENV GITHUB_API_URL=https://api.github.com/repos/nICEnnnnnnnLee/BilibiliDown/releases/latest
RUN apt update && apt install -y wget unzip zip curl jq
RUN curl https://api.github.com/repos/nICEnnnnnnnLee/BilibiliDown/releases/latest | jq '.assets[].browser_download_url' | grep -P 'BilibiliDown\.v[\.|\d]+\.release.zip"'|awk '{gsub("\"","",$0);system("wget -O /opt/release.zip "$0)}'
RUN mkdir /opt/bilibili && cd /opt/bilibili && unzip /opt/release.zip

FROM superdk/novnc-core
RUN apt update && apt install -y openjdk-11-jre
COPY --from=builder /opt/bilibili /opt/bilibili
COPY --from=builder1 /opt/ffmpeg /opt/bilibili
ENV LANG C.UTF-8
WORKDIR /opt/bilibili
CMD ["java","-Dfile.encoding=utf-8","-Dhttps.protocols=TLSv1.2","-jar","launch.jar"]