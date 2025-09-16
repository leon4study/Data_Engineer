from pyspark.sql import SparkSession
from pyspark.sql.types import *
import os


def load_data(ss: SparkSession, from_file, schema, data_path):
    if from_file:
        return ss.read.schema(schema).csv(data_path + "/log.csv")

    log_data_inmemory = [
        ["130.31.184.234", "2023-02-26 04:15:21", "PATCH", "/users", "400", 61],
        ["28.252.170.12", "2023-02-26 04:15:21", "GET", "/events", "401", 73],
        ["180.97.92.48", "2023-02-26 04:15:22", "POST", "/parsers", "503", 17],
        ["73.218.61.17", "2023-02-26 04:16:22", "DELETE", "/lists", "201", 91],
        ["24.15.193.50", "2023-02-26 04:17:23", "PUT", "/auth", "400", 24],
    ]
    return ss.createDataFrame(log_data_inmemory, schema)


if __name__ == "__main__":
    base_dir = os.path.dirname(os.path.abspath(__file__))
    data_path = os.path.join(base_dir, "data")

    ss: SparkSession = (
        SparkSession.builder.master("local").appName("log sql ex").getOrCreate()
    )

    from_file = False

    fields = StructType(
        [
            StructField("ip", StringType(), False),
            StructField("timestamp", StringType(), False),
            StructField("method", StringType(), False),
            StructField("endpoint", StringType(), False),
            StructField("status_code", StringType(), False),
            StructField("latency", IntegerType(), False),  # 단위 : milliseconds
        ]
    )

    table_name = "log_data"

    load_data(ss, from_file, fields, data_path).createOrReplaceTempView(table_name)

    ss.sql(f"SELECT * FROM {table_name}").show()
    ss.sql(f"SELECT * FROM {table_name}").printSchema()

    # a) 컬럼 반환
    # a-1) 현재 latency 컬럼의 단위는 milliseconds인데, seconds단위인 latency seconds 커럼을 새로 만들기
    ss.sql(
        f"""
        SELECT *, latency / 1000 AS latency_seconds
        FROM {table_name}
    """
    ).show()

    # a-2) StringType으로 받은 timestamp 컬럼을, TimestampTpe으로 변경
    ss.sql(
        f"""
        SELECT ip, TIMESTAMP(timestamp) AS timestamp, method, endpoint, status_code, latency, latency
        FROM {table_name}
    """
    ).printSchema()
