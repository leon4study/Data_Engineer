from pyspark.sql import SparkSession
import pyspark.sql.functions as F
from pyspark.sql.types import StructType, StructField, StringType, IntegerType
import os

if __name__ == "__main__":
    print("main_started")
    base_dir = os.path.dirname(os.path.abspath(__file__))
    data_path = os.path.join(base_dir, "data")

    ss: SparkSession = (
        SparkSession.builder.master("local[2]")
        .appName("streaming df examples")
        .getOrCreate()
    )

    schemas = StructType(
        [
            StructField("ip", StringType(), False),
            StructField("timestamp", StringType(), False),
            StructField("method", StringType(), False),
            StructField("endpoint", StringType(), False),
            StructField("status_code", StringType(), False),
            StructField("latency", IntegerType(), False),
        ]
    )

    def hello_streaming():
        print("hello_streaming is running", flush=True)
        lines = (
            ss.readStream.format("socket")
            .option("host", "localhost")
            .option(
                "port", 50010
            )  ## 12345 이미 다른 애가 먹고 있으면 안됨. 50010으로 변경
            .load()
        )

        lines.writeStream.format("console").outputMode("append").trigger(
            processingTime="2 seconds"
        ).start().awaitTermination()

    def read_from_socket():
        print("read_from_socket is running", flush=True)
        lines = (
            ss.readStream.format("socket")
            .option("host", "localhost")
            .option("port", 50010)
            .load()
        )

        cols = ["ip", "timestamp", "method", "endpoint", "status_code", "latency"]

        # filter : status_code = 400, endpoint = /users

        df = (
            lines.withColumn(cols[0], F.split(lines["value"], ",").getItem(0))
            .withColumn(cols[1], F.split(lines["value"], ",").getItem(1))
            .withColumn(cols[2], F.split(lines["value"], ",").getItem(2))
            .withColumn(cols[3], F.split(lines["value"], ",").getItem(3))
            .withColumn(cols[4], F.split(lines["value"], ",").getItem(4))
            .withColumn(cols[5], F.split(lines["value"], ",").getItem(5))
        )

        # df = df.filter((df.status_code == "400") & (df.endpoint == "/users"))

        # group by : method, endpoint 별 latency의 최대, 최소, 평균값

        # group_cols = ["method","endpoint"]
        # df = df.groupBy(group_cols).agg(
        #     F.max("latency").alias("max_latency"),
        #     F.min("latency").alias("min_latency"),
        #     F.mean("latency").alias("mean_latency")
        #     )

        """
        위 goupby를 실행하면 에러가 뜨는데 그 이유는 아래와 같다.
        새로 들어온 완전한 데이터만 출력할 때 사용 가능하지만, 
        집계 연산(예: groupBy, count, sum)이 들어가면 append 모드는 동작하지 않습니다.
        This output mode is not supported for streaming aggregations 
        without watermark on streaming DataFrames/DataSets. SQLSTATE: 42KDE
        append output모드는 watermark 없이는 streaming연산시, agg연산이 지원되지 않는다.
        특정시점 (watermark시점) 이전의 데이터는 집계대상에 포함되지 않도록 필터링을 건다. 고 알면 됨.
        stream 데이터는 무한히 데이터가 들어온다는 것을 전제로 함. 어느 시점 이후의 데이터를 가져올거야. 라는 조건이 없다면
        무한히 많은 데이터를 가져올 가능성이 생기기 때문에 spark app에 큰 부담이 됨. 따라서 제약을 두었다.
        """

        df.writeStream.format("console").outputMode("append").trigger(
            processingTime="2 seconds"
        ).start().awaitTermination()

    # 스트리밍에서 특정 소켓이나 다른 것 말고도, 파일로부터도 데이터를 읽어올 수 있다.
    def read_from_files():
        logs_df = (
            ss.readStream.format("csv")
            .option("header", "false")
            .schema(schemas)
            .load(data_path + "/logs")
            # 즉, “특정 CSV 파일만 스트리밍으로 계속 읽겠다”는 요구는 스트리밍 설계와 맞지 않습니다
            # 스트리밍용 파일 소스는 일반적으로 폴더 단위 감시가 권장됨. 특정 파일만 반복 읽고 싶으면 batch 모드로 처리
            # .load(data_path + "/logs/streaming_log.csv") 이렇게 하면 안되고 해당 파일의 디렉토리 넣어야 함
            # .load(data_path + "/logs/streaming_log_*.csv")  # logs/streaming_log_로 시작하는 모든 CSV

        )

        logs_df.writeStream.format("console").outputMode("append").trigger(
            processingTime="2 seconds"
        ).start().awaitTermination()

    # hello_streaming()
    # read_from_socket()
    read_from_files()
