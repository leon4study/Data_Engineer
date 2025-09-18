import os
from pyspark import SparkContext
from pyspark.sql import SparkSession
from pyspark.streaming import StreamingContext, DStream


# 우선 기본적으로 DStream과 RDD의 interface는 거의 동일하다고 보면 됨.
# DStream의 구현도 RDD로 이루어져 있음
# RDD에서 쓰이는 Map Filter, FlatMap 등 DStream에서도 전부 사용이 가능함.

# APPL 일 별 최고가 DStream으로 가져오는 실습

if __name__ == "__main__":
    base_dir = os.path.dirname(os.path.abspath(__file__))
    data_dir = os.path.join(base_dir, "data")

    sc: SparkContext = (
        SparkSession.builder.master("local[2]")  # 스레드 2개 사용
        .appName("DStream ex")
        .getOrCreate()
        .sparkContext
    )

    ssc = StreamingContext(sc, 5)  # DStreaming 하려면 StreamingContext라는 게 필요함.
    # 여기들어가는 5는 batch duration이라는 파라미터인데 5초마다 micro batch를 돌린다는 뜻으로 이해하면 됨


    def read_from_socket():
        socket_stream: DStream[str] = ssc.socketTextStream("localhost",50010)

        # stream에서 한 줄로 여러 개의 단어들이 들어왔다고 하면 단어들을 띄어쓰기 단위로 스플릿 해서 한 줄로 펴서 보여주겠다.
        word_stream: DStream[str] = socket_stream.flatMap(lambda line: line.split(" "))

        word_stream.pprint() #콘솔에서 어떻게 나오는지 확인 가능

        ssc.start()
        ssc.awaitTermination()

    
    #read_from_socket()

    def read_from_file():
        stocks_file_path = data_dir+"/stocks"
        text_stream: DStream[str]= ssc.textFileStream(stocks_file_path) 
        text_stream.pprint()
        ssc.start()
        ssc.awaitTermination()
        ## 이런 식으로 하면 파일이 있음에도 불구하고 일단 이 마이크로 배치에는 들어가지 않는 것을 확인할 수가 있다.
        ## 새롭게 데이터가 추가되는 경우에만 DStream에 micro batch를 한다고 이해를 하면 됨
        ## 자동으로 이러한 파일을 생성하는 간단한 스크립트를 돌려보자. 
        ## study_create_new_file.py

    
    read_from_file()