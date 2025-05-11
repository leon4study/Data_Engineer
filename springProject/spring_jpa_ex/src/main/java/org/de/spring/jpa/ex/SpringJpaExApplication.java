package org.de.spring.jpa.ex;

import lombok.extern.slf4j.Slf4j;
import org.de.spring.jpa.ex.entity.Product;
import org.de.spring.jpa.ex.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
@Slf4j
public class SpringJpaExApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringJpaExApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(ProductRepository productRepository){
        return args -> {
            productRepository.save(new Product(101L, "product101", LocalDateTime.now(),"content101",101));
            productRepository.save(new Product(102L, "product102", LocalDateTime.now(),"content102",102));

            log.info("--- all records ---");
            for (Product product : productRepository.findAll()) {
                log.info(product.toString());
            }

            log.info("find by id = 101");
            productRepository.findById(101L).ifPresent(it -> log.info(it.toString()));

            log.info("find top 10 order by price desc");
            for (Product product : productRepository.findTop10ByOrderByPriceDesc()) {
                log.info(product.toString());
            }

            log.info("find by price greater than 10000");
            for (Product product : productRepository.findByPriceGreaterThanOrderByPriceDesc(10000)){
                log.info(product.toString());
            }

            log.info("plus price + 10000 where id = 101 : "+ productRepository.plusPrice(101L,10000));
            log.info("find by id = 101");
            productRepository.findById(101L).ifPresent(it -> log.info(it.toString()));
        };
    }

}
