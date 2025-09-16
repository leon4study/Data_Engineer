from pyspark import SparkContext, RDD
from pyspark.sql import SparkSession
import os, sys

if __name__ == "__main__":
    script_path = os.path.abspath(sys.argv[0])
    script_dir = os.path.dirname(script_path)
    os.chdir(script_dir)

    ss: SparkSession = SparkSession.builder \
        .master("local") \
        .appName("wordCount RDD ver") \
        .getOrCreate()
    sc: SparkContext = ss.sparkContext

    # load data
    text_file: RDD[str] = sc.textFile("data/words.txt")

    # transformations
    counts = text_file.flatMap(lambda line: line.split(" ")) \
        .map(lambda word: (word, 1)) \
        .reduceByKey(lambda count1, count2: count1 + count2)

    # action
    output = counts.collect()

    # show result
    for (word, count) in output:
        print("%s: %i" % (word, count))
