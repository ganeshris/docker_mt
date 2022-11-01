*****************************************
Below is the script to copy reporsitry
*****************************************
#!/bin/bash
PRJ_NAME=gitclone
GIT_USER=admin123
GIT_PASS=admin123
GIT_URL_FROM=http://13.126.217.36:31633/admin123/test1.git
GIT_URL_TO=http://13.126.217.36:31633/admin123/test3.git
docker build -t dind .
echo IMAGE_NAME=$GIT_URL_TO
