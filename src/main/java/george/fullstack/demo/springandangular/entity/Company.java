package george.fullstack.demo.springandangular.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.gson.annotations.Expose;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
// todo update schema - when production
@Table(name = "company", schema = "test_coupon_system")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", length = 45)
    @Expose
    private long id;

    @Column(name = "name", unique = true, nullable = false, length = 45)
    @Expose
    private String name;

    @Column(name = "email", unique = true, nullable = false, length = 45)
    @Expose
    private String email;

    @Column(name = "password", nullable = false, length = 45)
    @Expose
    private String password;

    @OneToMany(fetch = FetchType.EAGER,
            mappedBy = "company",
            orphanRemoval = true,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH}
    )
    @JsonManagedReference
    private List<Coupon> coupons;


    /**
     * Convenience method for adding Coupons to coupons field.<br>
     * Sets Coupon's Company field to this company.
     *
     * @param tempCoupon- Coupon to add to coupons field
     */
    //todo (?) does too many things at once. Add utility
    public void addCoupon(Coupon tempCoupon) {
        if (coupons == null) {
            coupons = new ArrayList<>();
        }
        coupons.add(tempCoupon);
        tempCoupon.setCompany(this);
    }

    public Company() {
    }

    public Company(String name, String email, String password, List<Coupon> coupons) {
        this.name = name;
        this.email = email;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public void setCoupons(List<Coupon> coupons) {
        this.coupons = coupons;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", coupons=" + coupons +
                '}';
    }
}

