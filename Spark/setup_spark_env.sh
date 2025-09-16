#!/bin/bash

# Conda 환경 활성화
source ~/miniconda3/etc/profile.d/conda.sh
conda activate leo4study

# Java 17 설정 (alias 내용을 그대로 넣음)
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH=$JAVA_HOME/bin:$PATH

# 실행할 Python 파일 인자 받기
python "$@"


