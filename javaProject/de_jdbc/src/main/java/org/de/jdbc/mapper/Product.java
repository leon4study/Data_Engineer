package org.de.jdbc.mapper;

import java.time.LocalDateTime;

public class Product {
    int id;
    String name;
    LocalDateTime updated_at;
    String contents;
    int price;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public String getContents() {
        return contents;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public Product(int id, String name, LocalDateTime updated_at, String contents, int price) {
        this.id = id;
        this.name = name;
        this.updated_at = updated_at;
        this.contents = contents;
        this.price = price;
    }

    // 표준 입출력에 넣으면 toString이 자동으로 출력된다
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", updated_at=" + updated_at +
                ", contents='" + contents + '\'' +
                ", price=" + price +
                '}';
    }
}
