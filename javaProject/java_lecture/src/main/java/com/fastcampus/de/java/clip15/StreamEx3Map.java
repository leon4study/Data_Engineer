package com.fastcampus.de.java.clip15;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class StreamEx3Map {
    static class Sale {
        String fruitName;
        int price;
        float discount;

        public Sale(String fruitName, int price, float discount) {
            this.fruitName = fruitName;
            this.price = price;
            this.discount = discount;
        }
    }

    public static void main(String[] args) {
        List<Sale> saleList =
                Arrays.asList(new Sale("Apple", 5000, 0.05f), new Sale("Grape", 3000, 0.1f),
                        new Sale("Orange", 4000, 0.2f), new Sale("Tangerine", 2000, 0));

        Stream<Sale> saleStream = saleList.stream();
        saleStream.map(sale -> Pair.of(sale.fruitName, sale.price * (1 - sale.discount))).forEach(
                pair -> System.out.println(pair.getLeft() + " 실 구매가 : " + pair.getRight()));
    }
}
