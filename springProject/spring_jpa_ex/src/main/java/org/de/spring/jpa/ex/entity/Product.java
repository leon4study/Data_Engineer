package org.de.spring.jpa.ex.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name ="product")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name = "updated_at", columnDefinition = "DATE")
    private LocalDateTime updatedAt;
    private String contents;
    private int price;
}
