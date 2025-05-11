package org.de.mongodb.aggregation;

import com.mongodb.client.*;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.de.mongodb.model.Product;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.gt;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class Main {
    public static void main(String[] args) {

        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry codecRegistry = fromRegistries(getDefaultCodecRegistry(),fromProviders(pojoCodecProvider));


        String uri = "mongodb://localhost:27017";
        MongoClient mongoClient = MongoClients.create(uri); // 이 객체 하나가 하나의 connection을 의미함.
        MongoDatabase mongoDatabase = mongoClient.getDatabase("de_mongodb").withCodecRegistry(codecRegistry);
        //MongoCollection<Product> collection =  mongoDatabase.getCollection("products", Product.class);
        MongoCollection<Document> collection =  mongoDatabase.getCollection("products"); //여기서는 pojo product.class tkdydgotjs dksehla

        //collection.aggregate(
        //MongoCursor cursor =  collection.aggregate(
        MongoCursor<Document> cursor =  collection.aggregate(
                Arrays.asList(
                        Aggregates.match(gt("price", 10000)),
                        Aggregates.group("$name", Accumulators.avg("avg_price","$price")),// 그냥 하면 안되고 필드에 조건을 걸려면 달러를 붙여야 함.
                        // id 주어지는 파라미터에는 필드 이름 앞에 $ 붙여야 해당 필드 찾을 수 있다.
                        Aggregates.sort(Sorts.descending("avg_price"))
                )
        //);
        ).iterator();

        // 왜 이렇게 하냐? 우리가 그냥 컬렉션에서 값만, 컬렉션에 있는 데이터만 가져올 거면
        // Pojo 모델을 바꿔서 Product로 받는 게 편하다. 그런데 이건 가져오는 게 아니라 데이터를 기반으로 해서
        // 집계를 할 건데 집계를 할 떄 우리 Avg_Price 라는 값으로 평균 값을 저장할래. 이럴 것이다.
        // 그런데 이 avg price 는 Product 모델에 없죠? 그렇다고 집계 결과인데 원래 모델에 포함하기도 또 애매하고.
        // 그래서 엄밀히 말하면 이 집계 Aggregation의 결과는 새로운 Document다. Product Document가 아니라.
        // 이거를 이제 Pojo 로 정하기 전에는 자유롭게 쓸 수 있는 건 Document 이니 Document로 만들었다.

        // Aggregation 결과는 Product 모델에 없는 avg_price 필드를 포함하므로,
        // 기존 POJO로 매핑하기 애매하다. 따라서 자유로운 구조인 Document로 처리한다.

        while (cursor.hasNext()) {
            System.out.println(cursor.next());
        }

    }
}
