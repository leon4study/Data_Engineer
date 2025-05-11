package org.de.spring.jpa.ex.repository;

import org.de.spring.jpa.ex.entity.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Long> {
    //CrudRepository 말고도 다른 타입의 repository가 있는데, Repository 보다 사용성이 좋은 CrudRepository로 사용.
    //CrudRepository<Product, Long> 에 Product는 이걸로 IOR 대상이 되는 테이블을 나타내는 엔티티를 여기다 줘. 여기 오는 클래스는 엔티티가 붙어있는 클래스만 올 수 있다.
    //CrudRepository<Product, Long> 여기서 사용하는 key 값의 타입은 Long이다. (ID Long으로 했으니까.)


    List<Product> findTop10ByOrderByPriceDesc();
    List<Product> findByPriceGreaterThanOrderByPriceDesc(int price);

    @Modifying // 아래 UPDATE니까 modifying이라는 annotion붙어있어야 함.
    @Transactional // 여기에 붙여도 되고 이 함수를 호출하는 블럭에서 @Transactional을 호출해도 된다
    @Query("UPDATE product p SET p.price = p.price + :plus where id = :id")
    int plusPrice(Long id, int plus);
}
