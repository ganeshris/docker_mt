#************* Stage 1 - git pull **************
FROM alpine/git:v2.30.1 AS code
RUN mkdir repo
WORKDIR /repo
RUN git clone http://13.126.217.36:31633/admin123/test1.git
WORKDIR /repo/test1
RUN rm -rf .git
RUN ls

#************* Stage 2 - git push **************
FROM alpine/git:v2.30.1 AS push
RUN mkdir repo1
WORKDIR /repo1
RUN git clone http://13.126.217.36:31633/admin123/test3.git
COPY --from=code /repo/test1 /repo1/test3
WORKDIR /repo1/test3
RUN ls
RUN git config --global user.email ganeshk@dekatc.com
RUN git config --global user.name admin123
RUN git add .
RUN git commit -m "testing"
RUN git push http://admin123:admin123@13.126.217.36:31633/admin123/test3.git
WORKDIR /
RUN rm -rf /repo1