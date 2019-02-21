package george.fullstack.demo.springandangular.dao;

import george.fullstack.demo.springandangular.entity.Coupon;
import george.fullstack.demo.springandangular.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByName(String name);

    /**
     * Method for retrieving {@link Coupon} already owned by the customer.
     *
     * @param customerId
     * @return list of owned coupons
     */
    @Query("SELECT c FROM Coupon c" +
            " INNER JOIN Customer_Coupon cc ON  c.id=cc.coupon_id " +
            " WHERE cc.customer_id=?1")
    List<Coupon> getCouponsOwnedByThisCustomer(long customerId);
}
