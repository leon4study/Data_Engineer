#!/bin/bash

# Conda 환경 활성화
source ~/miniconda3/etc/profile.d/conda.sh
conda activate leo4study

# Java 17 설정 (alias 내용을 그대로 넣음)
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH=$JAVA_HOME/bin:$PATH

# PySpark Python 버전 통일
export PYSPARK_PYTHON=/opt/homebrew/Caskroom/miniconda/base/envs/leo4study/bin/python
export PYSPARK_DRIVER_PYTHON=/opt/homebrew/Caskroom/miniconda/base/envs/leo4study/bin/python

# 실행할 Python 파일 인자 받기
if [ $# -eq 0 ]; then
    echo "Spark 환경 설정 완료 (python 파일 실행 안 함)"
    # 여기서 바로 Spark shell 들어가고 싶으면:
    # exec pyspark
else
    exec python "$@"
fi
