#!/usr/bin/env bash

function randomPort(){
    rndPort=$((($RANDOM % 65535) + 1))
    echo $rndPort
}

function serverStart(){
    export SEED=$1
    export PORT=$2

    # Server (background)
    make -C ../ run &

    waitPort $PORT
}

function waitPort() {
    port=$1
    while ! nc -z localhost $port ; do sleep 1 ; done
}

function killProcessAtPort(){
    port=$1
    pid=$(lsof -t -i:$port)
    kill -9 $pid 2> /dev/null
}

