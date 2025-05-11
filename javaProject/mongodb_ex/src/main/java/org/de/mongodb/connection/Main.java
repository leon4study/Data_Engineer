package org.de.mongodb.connection;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class Main {
    public static void main(String[] args) {
        String uri = "mongodb://localhost:27017";
        MongoClient mongoclient = MongoClients.create(uri); // 이 객체 하나가 하나의 connection을 의미함.
        MongoDatabase mongoDatabase = mongoclient.getDatabase("test2");
        MongoCollection<Document> collection =  mongoDatabase.getCollection("movies");
        Document document = collection.find(eq("title", "The Favourite")).first();
        System.out.println(document.toJson());
    }
}
