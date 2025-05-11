package org.de.mongodb.transaction;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.de.mongodb.model.Product;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class Main {
    public static void main(String[] args) {
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry codecRegistry = fromRegistries(getDefaultCodecRegistry(),fromProviders(pojoCodecProvider));

        String uri = "mongodb://localhost:27017/?replicaSet=rs1";
        MongoClient mongoClient = MongoClients.create(uri); // 이 객체 하나가 하나의 connection을 의미함.
        MongoDatabase mongoDatabase = mongoClient.getDatabase("de_mongodb").withCodecRegistry(codecRegistry);
        //MongoCollection<Product> collection =  mongoDatabase.getCollection("products", Product.class);

        ClientSession clientSession = mongoClient.startSession();

        TransactionOptions transactionOptions = TransactionOptions.builder()
                                                                    .readPreference(ReadPreference.primary())
                                                                    .readConcern(ReadConcern.LOCAL)
                                                                    .writeConcern(WriteConcern.MAJORITY)
                                                                    .build();

//        TransactionBody transactionBody = () -> {
        clientSession.startTransaction(transactionOptions);
            MongoCollection<Document> coll1 = mongoDatabase.getCollection("foo");
            MongoCollection<Document> coll2 = mongoDatabase.getCollection("bar");

            Map<String, Object> input1 = new HashMap<>();
            int id = new Random().nextInt();
            int value = new Random().nextInt();
            System.out.println("Test input : "+ id+ " : "+ value);
            input1.put("_id", id);
            input1.put("field", value);

            Map<String, Object> input2 = new HashMap<>();
            input2.put("_id", id+1);
            input2.put("field", value+1);

            coll1.insertOne(clientSession, new Document(input1));
            coll1.insertOne(clientSession, new Document(input2));
            coll2.insertOne(clientSession, new Document(input1));
            clientSession.abortTransaction(); // rollback

            clientSession.startTransaction(transactionOptions);
            coll1.insertOne(clientSession, new Document(input1)); // 아이디가 겹침. 에러나면서 종료.
            // 트렌젝션으로 처리했으니까 롤백돼서 앞에 것들도 다 취소 되어서 하나도 들어가지 않았음.
            clientSession.commitTransaction();
//            return "inserted into collection in different databases";
//        };
//        clientSession.withTransaction(transactionBody, transactionOptions);
    }
}
