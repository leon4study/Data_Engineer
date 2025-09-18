import os
from pyspark import SparkContext
from pyspark.sql import SparkSession
import pyspark.sql.functions as F

if __name__ == "__main__":
    base_dir = os.path.dirname(os.path.abspath(__file__))
    data_dir = os.path.join(base_dir, "data")

    ss: SparkSession = (
        SparkSession.builder.master("local[2]")
        .appName("processing_time_window_ex")
        .getOrCreate()
    )

    # processing time을 기준으로 agg할 예정
    # socket에서 문자열 받아서 processing time 기준 window 연산 수행
    def agg_by_prcessing_time():

        # 터미널에서 문자열로 받아서 해보려는 건 각 윈도우 별로 문자열 캐릭터 카운트가 몇개인지 세려는 것.
        lines_char_cnt_by_window_df = (
            ss.readStream.format("socket")
            .option("port", 50010)
            .option("host", "localhost")
            .load()
            .select(
                F.col("value"),
                F.current_timestamp().alias(
                    "processingTime"
                ),  # 프로세싱 타임 기준 자체가 스파크에서 처리를 하는 시간이니
                # spark 들어온 시점의 current time을 processing time이라고 생각
            )
            .groupBy(F.window(F.col("processingTime"), "5 seconds").alias("window"))
            .agg(F.sum(F.length(F.col("value"))).alias("char_cnt"))
            .select(
                F.col("window").getField("start").alias("start"),
                F.col("window").getField("end").alias("end"),
                F.col("char_cnt"),
            )
        )

        lines_char_cnt_by_window_df.writeStream.format("console").outputMode(
            "complete"
        ).start().awaitTermination()
    
    agg_by_prcessing_time()


# processing time은 보통 current_timestamp를 사용하고
# 이전에 했던 event time같은 경우는 그 소스 데이터에 timestamp 타입의 속성을 가지고 이벤트 타임으로 간주함.