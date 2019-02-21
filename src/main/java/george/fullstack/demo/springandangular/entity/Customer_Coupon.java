package george.fullstack.demo.springandangular.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Class used in verifying customer coupon ownership.
 */
@Entity
@Table(name = "customer_coupon")
public class Customer_Coupon implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "customer_id", length = 45)
    private long customer_id;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "coupon_id", length = 45)
    private long coupon_id;

    public Customer_Coupon() {
    }

    public long getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(long customer_id) {
        this.customer_id = customer_id;
    }

    public long getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(long coupon_id) {
        this.coupon_id = coupon_id;
    }
}
