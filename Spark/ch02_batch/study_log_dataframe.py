from pyspark.sql import SparkSession
from pyspark.sql.types import *
import os
from pyspark.sql.functions import to_timestamp
from pyspark.sql.functions import (
    col,
    max,
    mean,
    min,
    hour,
    minute,
    date_trunc,
    collect_set,
    count,
)


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
        SparkSession.builder.master("local").appName("log dataframe ex").getOrCreate()
    )

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

    from_file = True
    df = load_data(ss, from_file, fields, data_path)

    # df.show()

    # df.printSchema()

    # a) 컬럼 변환
    # a-1) 현재 latency 컬럼의 단위는 milliSeconds인데, seconds단위인 latency_seconds 컬럼 새로 만들기

    def milliseconds_to_seconds(n):
        return n / 1000

    df = df.withColumn("latency_seconds", milliseconds_to_seconds(df.latency))

    # a-2) StringType 로 받은 timestamp 컬럼을 TimestampType으로 변경

    df = df.withColumn("timestamp", to_timestamp(df.timestamp))
    # df.show()
    # df.printSchema()

    # b) filter
    # b-1) status_code = 400, endpoint = "/users"인 row만 필터링
    df_filterd = df.filter((df.status_code == "400") & (df.endpoint == "/users"))
    # df.status_code=="400" & df.endpoint == "/users" 이런 식으로 괄호 없으면 안됩니다
    # df_filterd.show()
    # df_filterd.printSchema()

    # c) group by
    # c-1) method, endpoint 별 latency의 최댓값, 최솟값, 평균값

    group_cols = ["method", "endpoint"]

    # df.groupBy(group_cols)\
    #     .agg(max("latency").alias("max_latency"),
    #         min("latency").alias("min_latency"),
    #         mean("latency").alias("mean_latency")).show()

    # c-2) 분 단위, 중복 제거한 ip list, 개수 뽑기

    group_cols = ["hour", "minute"]

    df.withColumn(
        "hour",
        hour(date_trunc("hour", df.timestamp)),
    ).withColumn(
        "minute", minute(date_trunc("minute", df.timestamp))
    ).groupby(group_cols).agg(
        collect_set("ip").alias("ip_list"), count("ip").alias("ip_cnt")
    ).sort(
        group_cols
    ).show()

    while True:
        pass
