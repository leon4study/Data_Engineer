package org.de.mongodb.delete;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.de.mongodb.model.Product;

import java.time.format.DateTimeFormatter;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.fields;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class Main {
    public static void main(String[] args) {
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry codecRegistry = fromRegistries(getDefaultCodecRegistry(),fromProviders(pojoCodecProvider));

        String uri = "mongodb://localhost:27017";
        MongoClient mongoClient = MongoClients.create(uri); // 이 객체 하나가 하나의 connection을 의미함.
        MongoDatabase mongoDatabase = mongoClient.getDatabase("de_mongodb").withCodecRegistry(codecRegistry);
        MongoCollection<Product> collection =  mongoDatabase.getCollection("products", Product.class);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // delete 는 지금 이 상태에서는 transaction이 없기 때문에 롤백이 안된다. 그래서 delete는 주의해야되고 그럴 때 빛을 발하는 게
        // 자바의 타입시스템으로 된 조건들이다 (gte, lt, regex 등)
        DeleteResult deleteResult = collection.deleteMany(fields(regex("name","shoes"),
                gte("price",10000), //10000 <= price < 20000
                lt("price", 20000)
                )
        );

        if (deleteResult.wasAcknowledged()) {
            System.out.println("deleted : " + deleteResult.getDeletedCount());
        }
    }
}
