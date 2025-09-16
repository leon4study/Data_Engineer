import os
from pyspark import SparkContext
from pyspark.sql import SparkSession


def load_data_from_file(sc : SparkContext, data_path):

    user_visits = sc.textFile(data_path+"/user_visits.txt").map(lambda v : v.split(","))
    user_names = sc.textFile(data_path+"/user_names.txt").map(lambda v : v.split(","))
    return user_visits, user_names



def load_data_from_memory(sc: SparkContext):

    # [user_id, visits]
    user_visits = [
        (1, 10),
        (2, 27),
        (3, 2),
        (4, 5),
        (5, 88),
        (6, 1),
        (7, 5)
    ]


    user_names = [
        (1, "Andrew"),
        (2, "Chris"),
        (3, "John"),
        (4, "Bob"),
        (6, "Ryan"),
        (7, "Mali"),
        (8, "Tony"),
    ]

    # parallelize : Python의 로컬 데이터(리스트, range, 배열 등)를 Spark의 RDD(Resilient Distributed Dataset)로 변환
    return sc.parallelize(user_visits), sc.parallelize(user_names)



if __name__ == '__main__':
    base_dir = os.path.dirname(os.path.abspath(__file__))
    data_path = os.path.join(base_dir, "data")

    ss: SparkSession = SparkSession.builder\
            .master("local").appName("rdd join ex")\
            .getOrCreate()

    sc: SparkContext = ss.sparkContext

    user_visits_rdd, user_names_rdd = load_data_from_file(sc, data_path)

    #print(user_names_rdd)
    #print(user_visits_rdd)
    #print(user_names_rdd.take(5))
    #print(user_visits_rdd.take(5))

    # a) Chris 의 방문횟수를 출력하고 싶은데?
    joined_rdd = user_names_rdd.join(user_visits_rdd).sortByKey()
    print(joined_rdd.take(5))
    # 결과 # [(1, ('Andrew', 10)), (2, ('Chris', 27)), (3, ('John', 2)), (4, ('Bob', 5)), (6, ('Ryan', 1))]

    result = joined_rdd.filter(lambda row : row[1][0] == "Chris").collect()
    print(result)

    # b) inner, left outer, right outer, full outer join
    inner = user_names_rdd.join(user_visits_rdd).sortByKey()
    print(f"inner ==> : {inner.collect()}")
    # [(1, ('Andrew', 10)), (2, ('Chris', 27)), (3, ('John', 2)), (4, ('Bob', 5)), (6, ('Ryan', 1)), (7, ('Mali', 5))]

    right_outer = user_names_rdd.rightOuterJoin(user_visits_rdd).sortByKey()
    print(f"right outer ==> : {right_outer.collect()}")
    # [(1, ('Andrew', 10)), (2, ('Chris', 27)), (3, ('John', 2)), (4, ('Bob', 5)), (5, (None, 88)), (6, ('Ryan', 1)), (7, ('Mali', 5))]


    # 보통 left join이 가지고 있는 거 전부 가지고, null 은 채우고.
    left_outer = user_names_rdd.leftOuterJoin(user_visits_rdd).sortByKey()
    print(f"left outer ==> : {left_outer.collect()}")
    # [(1, ('Andrew', 10)), (2, ('Chris', 27)), (3, ('John', 2)), (4, ('Bob', 5)), (6, ('Ryan', 1)), (7, ('Mali', 5)), (8, ('Tony', None))]

    full_outer = user_names_rdd.fullOuterJoin(user_visits_rdd).sortByKey()
    print(f"full outer ==> : {full_outer.collect()}")
    # [(1, ('Andrew', 10)), (2, ('Chris', 27)), (3, ('John', 2)), (4, ('Bob', 5)), (5, (None, 88)), (6, ('Ryan', 1)), (7, ('Mali', 5)), (8, ('Tony', None))]