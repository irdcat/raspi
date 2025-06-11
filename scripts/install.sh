#!/bin/sh

command -v python3 >/dev/null 2>&1 || {
    echo >&2 "Python 3 is required!"
}

command -v helm >/dev/null 2>&1 || {
    echo >&2 "Helm is required!"
}

python3 install.py $@