package org.de.mongodb.model;

import lombok.*;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Product {
    @BsonId //이렇게 하면 아래 필드는 _id 로 되는 유니크 필드랑 매핑되는 값이다 라고 인지할 수 있음
    ObjectId id;
    String name;
    @BsonProperty("updated_at")
    LocalDateTime updatedAt; // java.sql 타임스탬프 썼었는데 그대로 써도 된다.
    String contents;
    int price;
}
