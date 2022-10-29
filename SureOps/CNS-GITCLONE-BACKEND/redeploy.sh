*****************************************
Below are shellscript contents
*****************************************
#!/bin/bash
PRJ_NAME=cns-gitclone-backend
DOCKER_USER=farhan23432
DOCKER_PASS=Farhan@23432
DOCKER_URL=https://index.docker.io/v1/
RUN_PORT=8083
DOCKER_TAG=$(date +%s)
docker system prune --all --force
docker build -t $DOCKER_USER/$PRJ_NAME:$DOCKER_TAG --build-arg BUILD_ID=$DOCKER_TAG .
docker login --username=$DOCKER_USER --password=$DOCKER_PASS $DOCKER_URL
docker push $DOCKER_USER/$PRJ_NAME:$DOCKER_TAG
#docker run --name dev-$PRJ_NAME-$DOCKER_TAG -p $RUN_PORT:8087 -itd $DOCKER_USER/$PRJ_NAME:$DOCKER_TAG
docker logout $DOCKER_URL
#docker system prune -a -f
echo BUILD_ID=$DOCKER_TAG
IMAGE_URL=$DOCKER_USER/$PRJ_NAME:$DOCKER_TAG
#DEPLOY_FILE=cns-gitclone-backend-deployment.yaml
DEPLOYMENT=cns-gitclone-backend
NAMESPACE=cns-test
#sed -i -e 's,IMAGE_NAME,'"$IMAGE_URL"',g' $DEPLOY_FILE
kubectl set image deployment $DEPLOYMENT -n $NAMESPACE $DEPLOYMENT=$IMAGE_URL
#kubectl apply -f $DEPLOY_FILE
echo IMAGE_NAME=$IMAGE_URL
