package george.fullstack.demo.springandangular.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", length = 45)
    private long id;

    @Column(name = "name", unique = true, nullable = false, length = 45)
    private String name;

    @Column(name = "email", unique = true, nullable = false, length = 45)
    private String email;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            name = "customer_coupon",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "coupon_id")
    )
    private List<Coupon> coupons;

    public Customer() {
    }

    public Customer(String name, String email, List<Coupon> coupons) {
        this.name = name;
        this.email = email;
        this.coupons = coupons;
    }

    public void setCoupons(List<Coupon> coupons) {
        this.coupons = coupons;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Coupon> getCoupons() {
        return coupons;
    }

    /**
     * Convenience method for adding coupons to coupon list.<br>
     * Will initialize list if not initialized.
     *
     * @param coupon - coupon to add to list
     */
    public void addCoupon(Coupon coupon) {
        if (coupons == null) {
            coupons = new ArrayList<>();
        }
        coupons.add(coupon);

    }


    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
