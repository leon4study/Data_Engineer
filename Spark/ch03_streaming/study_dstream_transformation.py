import os
from pyspark import SparkContext, RDD
from pyspark.sql import SparkSession
from pyspark.streaming import DStream, StreamingContext


from collections import namedtuple


if __name__ == "__main__":

    base_dir = os.path.dirname(os.path.abspath(__file__))
    data_dir = os.path.join(base_dir, "data")
    stocks_data_dir = os.path.join(data_dir, "stocks")

    columns = ["Ticker", "Date", "Open", "High", "Low", "Close", "AdjClose", "Volume"]

    # namedtuple 는 뭐냐
    # 일반 튜플처럼 불변(immutable)이지만, 각 요소를 이름으로 접근할 수 있다는 점에서 편리합니다
    #
    Finance = namedtuple("Finace", columns)

    sc: SparkContext = (
        SparkSession.builder.master("local[2]")
        .appName("DStream transformation ex")
        .getOrCreate()
        .sparkContext
    )

    ssc = StreamingContext(sc, 5)

    def read_finance() -> DStream[Finance]:

        # 1. map 읽어 와서 finace 형태로 변환하는 map

        def parse(line: str):
            arr = line.split(",")
            return Finance(*arr)
            # Finace에 arr을 풀어서 각 필드에 넣어주기 위해서 사용 *arr
            # 이런 식으로 들어감
            # Finace(Ticker='NVDA', Date='2023.1.6', Open='144.7400055', High='150.1000061', Low='140.3399963', Close='148.5899963', AdjClose='148.5647583', Volume='40504400')

        return ssc.socketTextStream("localhost", 50010).map(parse)

    finance_stream: DStream[Finance] = read_finance()
    finance_stream.pprint()

    # filter
    def filter_nvda():
        finance_stream.filter(lambda f: f.Ticker == "NVDA").pprint()

    # filter_nvda()

    def filter_volume():
        finance_stream.filter(lambda f: int(f.Volume) > 100000000)

    # filter_volume()

    # DS에서 reduce by, group by를 할 때는 grouping을 하는 단위가 각 micro batch가 되는 특성이 있다.
    # 그것만 조금 주의해주면 됨.
    # reduce by, gruop by
    def count_dates_ticker():
        finance_stream.map(lambda f: (f.Ticker, 1)).reduceByKey(
            lambda a, b: a + b
        ).pprint()

    # count_dates_ticker()

    def group_by_dates_volume():
        finance_stream.map(lambda f: (f.Date, int(f.Volume))).groupByKey().mapValues(
            sum
        ).pprint()
    group_by_dates_volume()

    #foreach RDD 각 micro batch RDD마다 해당 데이터를 Json으로 저장하는 함수 구현
    def save_to_json():
        def foreach_func(rdd: RDD ):
            if rdd.isEmpty():
                print("RDD is empty")
                return
            df = rdd.toDF(columns)
            output_path = stocks_data_dir +"/outputs"
            n_files = len(os.listdir(output_path))
            full_path = f"{output_path}/finance-{n_files}.json"
            df.write.json(full_path)
            print(f"num-partitions => {df.rdd.getNumPartitions()}")
            print("write complted")
    
        finance_stream.foreachRDD(foreach_func)

    save_to_json()

    ssc.start()
    ssc.awaitTermination()
