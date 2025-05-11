package org.de.mongodb.create;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertManyResult;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.de.mongodb.model.Product;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class Main {
    public static void main(String[] args) {

        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        // 이게 없으면 Can't find a codec for class org.de.mongodb.model.Product. 에러메세지 나옴.
        // 실제로 내가 클래스에 매핑한다고 했는데, 이게 진짜 자동으로 되는 게 아니라 이걸 해석할 수 있는 정보를 몽고디비 드라이버한테 알려줘야 함.
        // 그걸 코덱이라고 함.
        CodecRegistry codecRegistry = fromRegistries(getDefaultCodecRegistry(),fromProviders(pojoCodecProvider));


        String uri = "mongodb://localhost:27017";
        MongoClient mongoclient = MongoClients.create(uri); // 이 객체 하나가 하나의 connection을 의미함.
        MongoDatabase mongoDatabase = mongoclient.getDatabase("de_mongodb").withCodecRegistry(codecRegistry);
        MongoCollection<Product> collection =  mongoDatabase.getCollection("products", Product.class);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<Product> products = new ArrayList<>();
        products.add(new Product(null, "shoes1", LocalDateTime.parse("2022-08-01 01:00:00", formatter),
                "This is shoes1", 10000));

        products.add(new Product(null, "shoes2", LocalDateTime.parse("2022-08-01 02:00:00", formatter),
                "This is shoes2", 20000));

        products.add(new Product(null, "shoes3", LocalDateTime.parse("2022-08-01 03:00:00", formatter),
                "This is shoes3", 30000));

        products.add(new Product(null, "shoes4", LocalDateTime.parse("2022-08-01 04:00:00", formatter),
                "This is shoes4", 40000));

        products.add(new Product(null, "shoes5", LocalDateTime.parse("2022-08-01 05:00:00", formatter),
                "This is shoes5", 50000));

        products.add(new Product(null, "shoes6", LocalDateTime.parse("2022-08-01 06:00:00", formatter),
                "This is shoes6", 60000));

        products.add(new Product(null, "backpack", LocalDateTime.parse("2022-08-02 04:00:00", formatter),
                "This is backpack", 70000));

        products.add(new Product(null, "shirt", LocalDateTime.parse("2022-08-03 05:00:00", formatter),
                "This is shirt", 80000));

        products.add(new Product(null, "glasses", LocalDateTime.parse("2022-08-04 06:00:00", formatter),
                "This is glasses", 100000));

        InsertManyResult insertManyResult = collection.insertMany(products);
        // wasAcknowledged() 메서드는 클라이언트가 보낸 insertMany 요청에 대해 서버가 성공적으로 응답(acknowledge)했는지를 확인한다.
        // true이면 서버가 요청을 수신하고 처리했음을 의미한다.
        // 여기서의 "acknowledge"는 네트워크 통신에서 사용하는 ACK(acknowledgement) 개념과 유사하며,
        // 예를 들어 TCP 3-way handshake에서 SYN → SYN-ACK → ACK처럼 클라이언트가 요청을 보내고
        // 서버가 이를 수신했음을 알리는 메커니즘과 동일한 의미를 갖는다.
        if (insertManyResult.wasAcknowledged()){
            System.out.println("insert ids : " + insertManyResult.getInsertedIds());
        }
    }
}
