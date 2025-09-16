import os
from pyspark.sql import SparkSession
import pyspark.sql.functions as F


if __name__ == "__main__":
    base_dir = os.path.dirname(os.path.abspath(__file__))
    data_dir = os.path.join(base_dir, "data")

    ss: SparkSession = (
        SparkSession.builder.master("local[2]")
        .appName("streaming df join example")
        .getOrCreate()
    )

    authors = ss.read.option("inferSchema", True).json(data_dir + "/authors.json")
    books = ss.read.option("inferSchema", True).json(data_dir + "/books.json")

    # 1. join (고정된 데이터니까 static, static) 조인하는 상황
    def join_static_with_static():
        auths_books_df = authors.join(books, authors["book_id"] == books["id"], "inner")
        auths_books_df.show()

    # 2. join (static, stream)

    def join_stream_with_static():
        streamed_books = (
            ss.readStream.format("socket")
            .option("host", "localhost")
            .option("port", 50010)
            .load()
            .select(F.from_json(F.col("value"), books.schema).alias("book"))
            .selectExpr(
                # "book.id as id, book.name as name, book.year as year" 이렇게 넘겨주면 안되고 문자열 여러 개 해야됨.
                # python list로도 가능.
                "book.id as id",
                "book.name as name",
                "book.year as year",
            )  # stream으로 읽어온 형태를 json으로 parsing 해줘야 함
        )
        """
        안 하면 이렇게 됨.
        +----------------+
        |            book|
        +----------------+
        |{1, Kluge, 2008}|
        +----------------+
        """

        authors_books_df = authors.join(
            streamed_books,
            authors["book_id"] == streamed_books["id"],
            "inner",
            # streamed_books, authors["book_id"] == streamed_books["id"], "right"
            # streamed_books, authors["book_id"] == streamed_books["id"], "left" 는 error 발생.
            # left join시, 왼쪽이 static이고, 오른쪽이 streaming 이어서 error 발생.
            # streaming 쪽이 데이터가 무한하다고 가정하기 때문에 join 할 때, 무한한 데이터가 static데이터와 join시 성능부담이 크기 때문
        )

        authors_books_df.writeStream.format("console").outputMode(
            "append"
        ).start().awaitTermination()

    # 3. join (stream, stream)

    def join_stream_with_stream():
        streamed_books = (
            ss.readStream.format("socket")
            .option("host", "localhost")
            .option("port", "50010")
            .load()
            .select(F.from_json(F.col("value"), books.schema).alias("book"))
            .selectExpr("book.id as id", "book.name as name", "book.year as year")
        )

        streamed_authors = (
            ss.readStream.format("socket")
            .option("host", "localhost")
            .option("port", 50011)  # 다른 포트로 파싱
            .load()
            .select(F.from_json(F.col("value"), authors.schema).alias("author"))
            .selectExpr(
                "author.id as id", "author.name as name", "author.book_id as book_id"
            )
        )

        # join : PER BATCH
        authors_books_df = streamed_authors.join(
            streamed_books,
            streamed_authors["book_id"] == streamed_books["id"],
            "inner",  ## stream stream에서는 inner 밖에 안됨
        )

        authors_books_df.writeStream.format("console").outputMode(
            "append"
        ).start().awaitTermination()
        # stream stream join 에서는 append 말고 다른 Outputmoded 안됨.

    # join_static_with_static()
    # join_stream_with_static()
    join_stream_with_stream()
