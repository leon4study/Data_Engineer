from datetime import datetime
from typing import List
from pyspark import SparkContext, RDD
from pyspark.sql import SparkSession
import os



if __name__ == '__main__':
    base_dir = os.path.dirname(os.path.abspath(__file__))
    data_path = os.path.join(base_dir, "data/log.txt")

    print(f"script dir: {base_dir}")
    print(f"data path: {data_path}")

    ss = SparkSession.builder \
        .master("local") \
        .appName("rdd examples ver") \
        .getOrCreate()

    sc : SparkContext = ss.sparkContext   # ✅ SparkContext 가져오기

    log_rdd = sc.textFile(data_path)

    # print(f"count of RDD ==> {log_rdd.count()}")
    # log_rdd.foreach(lambda v: print(v))


    # a)map
    # a-1) log.txt 각 행을 list[str] 받아오기

    def parse_line(row: str):
        return row.strip().split(" | ")
    
    parsed_log_rdd : RDD[List[str]] = log_rdd.map(parse_line)
    # parsed_log_rdd.foreach(print)

    # b) filter
    # b-1) status code = 404 인 log만 filtering.
    
    def get_only_404(row : List[str]) -> bool:
        status_code = row[-1]
        return status_code == '404'
    
    # rdd_404 = parsed_log_rdd.filter(get_only_404)
    # rdd_404.foreach(print)


    # b-2) status code 가 정상인 경우 2xx인 경우만 filtering
    def get_only_2xx(row : List[str]) -> bool:
        status_code = row[-1]
        return status_code.startswith('2')
    
    # rdd_normal = parsed_log_rdd.filter(get_only_2xx)
    # rdd_normal.foreach(print)


    # b-3) post, /playbooks api인 로그 필터링

    def get_post_request_and_playbooks_api(row : List[str])->bool:
        request,api, _ = row[2].replace("\"", "").split()
        return request == "POST" and api =="/playbooks"
    
    # rdd_post_playbooks = parsed_log_rdd.filter(get_post_request_and_playbooks_api)
    # rdd_post_playbooks.foreach(print)


    def get_post_request_and_playbooks_api2(row : List[str])->bool:
        log = row[2].replace("\"", "")
        return log.startswith("POST") and "/playbooks" in log
    
    # c) reduce
    # c) API method (POST /GET / PUT/ DELETE ) 별 개수 출력
    def extract_api_method(row: list[str]):
        api_method= row[2].replace("\"","").split()[0]
        return api_method,1
    
    # rdd_count_by_api_method = parsed_log_rdd.map(extract_api_method)\
    #     .reduceByKey(lambda c1, c2 : c1+c2).sortByKey() ## key 별로 정렬도 해보자
    
    # rdd_count_by_api_method.foreach(print)


    #c-2) 분 단위 별 요청 횟수 출력
    def extract_hour_and_minute(row: List[str]) -> tuple[str,int]:
        timestamp = row[1].replace("[","").replace("]","")
        date_format = "%d/%b/%Y:%H:%M:%S"
        date_time_obj = datetime.strptime(timestamp, date_format)
        return f"{date_time_obj.hour}:{date_time_obj.minute}", 1

    # rdd_cnt_by_h_and_m = parsed_log_rdd.map(extract_hour_and_minute)\
    #     .reduceByKey(lambda c1, c2:c1+c2).sortByKey()
    # rdd_cnt_by_h_and_m.foreach(print)


    # d) group by
    # d) status code, api method별 , ip list

    def extract_cols(row :List[str]) -> tuple [str,str,str]:
        ip = row[0]
        status_code = row[-1]
        api_method = row[2].replace("\"", "").split()[0]

        return status_code, api_method, ip
    
    ## group by key 사용하려면 map을 하나 더 만들어야 함.
    result = parsed_log_rdd.map(extract_cols)\
        .map(lambda x : ((x[0],x[1]), x[2]))\
        .groupByKey().mapValues(list)
    
    #result.foreach(print)

    # reduce by key

    result2 = parsed_log_rdd.map(extract_cols)\
        .map(lambda x: (( x[0],x[1]), x[2]))\
        .reduceByKey(lambda i1, i2 : f"{i1},{i2}")\
        .map(lambda row: (row[0], row[1].split(",")))
    
    result2.collect()

    while True:
        pass

