def call() {
    sh '''
        IMAGE_TAG=`cat ~/IMAGE_TAG.txt`  
        aws ecr get-login-password --region ap-south-1 | docker login --username AWS --password-stdin 537984406465.dkr.ecr.ap-south-1.amazonaws.com
        docker push $REGISTRY/$REPOSITORY:$IMAGE_TAG
    '''
}