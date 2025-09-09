#!/bin/bash
# init-hive.sh
# Hive 스키마 초기화

HIVE_BIN="/usr/bin/schematool"
DB_TYPE="mysql"

echo "Initializing Hive schema..."
$HIVE_BIN -dbType $DB_TYPE -initSchema

if [[ $? -ne 0 ]]; then
    echo "Hive schema initialization failed. Exiting..."
    exit 1
fi

echo "Hive schema initialized successfully."
