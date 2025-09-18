# watermark?
"""
처리된 데이터에서 쿼리에 의해 검색된 최대 event time보다 뒤쳐지는 event time의 동적인 임계값.
즉 스래쉬홀드(threshold). 어떤 조건을 만족하는지 여부를 판단하기 위해 정해놓은 기준 점수나 수치.

해당 임계값 이전 시점의 event time을 가지는 event는 쿼리 집계 대상에서 제외됨.
watermark 사용하면, spark 쿼리 결과를 계산하고 유지해야 하는 상태 정보의 양을 줄일 수 있음
왜냐면 일부 데이터를 포함하지 않는 거니까.
"""

import os
from pyspark import SparkContext
from pyspark.sql import SparkSession
import pyspark.sql.functions as F

if __name__ == "__main__":

    ss: SparkSession = (
        SparkSession.builder.appName("watermarks_ex").master("local[2]").getOrCreate()
    )

    def read_from_socket():
        # socket 통해서 읽을 때는 option에서 schema 를 지정하는 것이 불가능 하다.
        lines = (
            ss.readStream.format("socket")
            .option("host", "localhost")
            .option("port", 50010)
            .load()
        )

        cols = ["time", "count"]
        df = lines.withColumn(
            cols[0], F.split(lines["value"], ",").getItem(0)
        ).withColumn(cols[1], F.split(lines["value"], ",").getItem(1))

        # .withWatermark("time","10 seconds")
        aggr_df = (
            df.select(F.to_timestamp(F.col("time")).alias("time"), F.col("count"))
            .withWatermark(
                "time", "10 minutes"
            )  ## watermark 적용할 컬럼 이름, treshold 값.
            .groupBy(
                F.window(F.col("time"), "5 minutes").alias("time")
            )  # window 기반 goupby 연산
            .agg(F.sum("count").alias("total_cnt"))
            .select(
                F.col("time").getField("start").alias("start"),
                F.col("time").getField("end").alias("end"),
                F.col("total_cnt"),
            )
        )

        aggr_df.writeStream.format("console").outputMode(
            "update"
        ).start().awaitTermination()

    read_from_socket()
    # sample data row 하나하나 넣어보자.

"""
watermark : 10 min

2023-04-16 09:08:00,1 -> 08:58,  watermark = 08:55 ~ 09:00
2023-04-16 08:59:00,1 -> 08:58,  watermark = 08:55 ~ 09:00
2023-04-16 08:45:00,1 -> x
2023-04-16 08:54:00,1 -> x
2023-04-16 08:55:00,1 -> 08:58,  watermark = 08:55 ~ 09:00
2023-04-16 09:19:00,1 -> 09:09,  watermark = 09:05 ~ 09:10
2023-04-16 09:11:00,1 -> 09:09,  watermark = 09:05 ~ 09:10
2023-04-16 09:07:00,1 -> 09:09,  watermark = 09:05 ~ 09:10
2023-04-16 09:04:00,1 - > x

watermark 보다 작다면 집계가 되지 x
"""
