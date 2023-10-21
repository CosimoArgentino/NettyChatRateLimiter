#!/usr/bin/env bash

function ncWithFile(){
    server=$1
    port=$2
    fileNc=$3
    output=$(nc -v $server $port < $fileNc)
    echo -e "$output"
}

function clientRun(){
    SERVER=$1
    PORT=$2

    # Client
    ncWithFile $SERVER $PORT in.txt > out.txt
    cat out.txt
}

function testDiff(){

    echo -n "Test "

    diff ok.txt out.txt 1> diff.txt

    if [ -s diff.txt ]; then
        echo "OK"
    else
        echo "NOK - Check diff file $FILE_DIFF"
    fi

}