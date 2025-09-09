#!/bin/bash
# install-mariadb-driver.sh
# 모든 노드에 JDBC 드라이버 설치

JAR_S3_PATH="s3://leo4study/jdbc/mariadb-java-client-3.4.1.jar"
DEST_PATH="/usr/lib/hive/lib/"

echo "Downloading MariaDB JDBC driver from S3..."
aws s3 cp $JAR_S3_PATH $DEST_PATH

if [[ $? -ne 0 ]]; then
    echo "Failed to download JDBC driver. Exiting..."
    exit 1
fi

echo "JDBC driver installed to $DEST_PATH."
