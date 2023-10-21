#!/usr/bin/env bash

SCRIPT_DIR=$(dirname "$0")

source server.sh
source client.sh

# Server

seed=1
port=$(randomPort)
serverStart $seed $port

# Client

server=0.0.0.0
clientRun $server $port

killProcessAtPort $port

#testDiff