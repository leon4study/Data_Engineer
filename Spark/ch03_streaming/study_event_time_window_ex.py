import os
from pyspark import SparkContext
import pyspark.sql.functions as F
from pyspark.sql import SparkSession
from pyspark.sql.types import (
    StructField,
    StructType,
    StringType,
    IntegerType,
    TimestampType,
)


if __name__ == "__main__":
    base_dir = os.path.dirname(os.path.abspath(__file__))
    data_dir = os.path.join(base_dir, "data")

    ss: SparkSession = (
        SparkSession.builder.master("local[2]")
        .appName("evnt_T_window_ex")
        .getOrCreate()
    )

    domain_traffic_schema = StructType(
        [
            StructField("id", StringType(), False),
            StructField("domain", StringType(), False),
            StructField("count", IntegerType(), False),
            StructField("time", TimestampType(), False),
        ]
    )

    def read_traffics_from_socket():
        return (
            ss.readStream.format("socket")
            .option("host", "localhost")
            .option("port", 50010)
            .load()
            .select(F.from_json(F.col("value"), domain_traffic_schema).alias("traffic"))
            .selectExpr("traffic.*")
        )

    # traffics_df 까지 처리하면
    """
    traffic
    {id: "1", domain: "com.com", count: 1, time: ... }
    """

    # selectExpr(traffic.*)는 StructType 안의 모든 필드를 풀어서 개별 컬럼으로 가져오라는 뜻입니다.
    # 결과적으로 원래 traffic이라는 struct 컬럼 안의 각 필드가 독립적인 컬럼으로 변환됩니다.
    """
    id	domain	count	time
    1	com.com	1	    2023-04-15 23:50:15
    """

    # Q) sliding window를 사용해서 trafic 카운트들을 집계
    def aggregate_traffic_count_by_sliding_window():
        traffics_df = read_traffics_from_socket()

        window_by_hours = (
            traffics_df.groupby(
                F.window(
                    F.col("time"),
                    windowDuration="2 hours",  # windowduration = window각각의 크기를 의미.
                    slideDuration="1 hour",  # slideDuration = 각 duration간의 갭 차이 의미
                ).alias(
                    "time"
                )  # 2시간 크기의 윈도우를 1시간 간격으로 슬라이딩 하면서 count 합계를 구함
            )
            .agg(F.sum("count").alias("total_cnt"))
            .select(
                F.col("time").getField("start").alias("start"),
                F.col("time").getField("end").alias("end"),
                F.col("total_cnt"),
            )
            .orderBy(F.col("start"))
        )  # 최종 출력은 start, end, total_cnt

        window_by_hours.writeStream.format("console").outputMode(
            "complete"
        ).start().awaitTermination()

    # aggregate_traffic_count_by_sliding_window()

    # 텀블링 윈도우는 사실상 window duration이랑 slide duration이 같아서
    # window간에 겹치는 구간이 발생하지 않는 윈도우를 의미하는 것.
    # 즉, 텀블링 윈도우는 크게는 슬라이딩 윈도우의 하나라고 볼 수 있음.

    # Q) tumbling window
    def agg_traffic_cnt_by_tumbling_window():
        traffics_df = read_traffics_from_socket()
        window_by_hours = (
            traffics_df.groupby(F.window(F.col("time"), "1 hour").alias("time"))
            .agg(F.sum("count").alias("total_cnt"))
            .select(
                F.col("time").getField("start").alias("start"),
                F.col("time").getField("end").alias("end"),
                F.col("total_cnt"),
            )
            .orderBy(F.col("start"))
        )

        window_by_hours.writeStream.format("console").outputMode(
            "complete"
        ).start().awaitTermination()

    # agg_traffic_cnt_by_tumbling_window()

    """
    매 시간마다, traffic이 가장 많은 도메인을 출력.
    이전까지와의 차이점이 있다면, 이전에는 그냥 전체의 윈도우별 전체 카운드만 계산했었지만, 
    여기서는 각 윈도우의 도메인 별 트레픽의 최댓값을 구하는 것.
    """

    def read_traffics_from_file():
        return ss.readStream.schema(domain_traffic_schema).json(data_dir + "/traffics")

    def find_largest_fraffic_domain_per_hour():
        traffics_df = read_traffics_from_file()

        # 차이가 있다면 groupby 앞에 도메인을 붙여주면 됨.
        largest_traffic_domain = (
            traffics_df.groupby(
                F.col("domain"), F.window(F.col("time"), "1 hour").alias("hour")
            )
            .agg(F.sum("count").alias("total_cnt"))
            .select(
                F.col("hour").getField("start").alias("start"),
                F.col("hour").getField("end").alias("end"),
                F.col("domain"),
                F.col("total_cnt"),
            )
            .orderBy(F.col("total_cnt"),ascending=False)
        )

        largest_traffic_domain.writeStream.format("console").outputMode(
            "complete"
        ).start().awaitTermination()

    find_largest_fraffic_domain_per_hour()
