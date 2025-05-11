package org.de.mongodb.read;

import com.mongodb.client.*;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.de.mongodb.model.Product;

import java.time.format.DateTimeFormatter;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Sorts.descending;
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


        //MongoCursor<Product>  cursor = collection.find(eq("price",10000)).cursor();
        MongoCursor<Product>  cursor = collection.find(regex("name","shoes"))
                //.projection(include("name")) // arrigation 연산할 때, 집계정보만 필요할 때, 모든 컬럼이 필요하진 않으니 DB opration qngk wnfduwna.
                .sort(descending("price"))
                .cursor();
        while (cursor.hasNext()){
            System.out.println(cursor.next());
        }
    }
}
