package george.fullstack.demo.springandangular.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", length = 45)
    private long id;

    @Column(name = "name", unique = true, nullable = false, length = 45)
    private String name;

    @Column(name = "description", length = 100)
    private String description;

    @Column(name = "imageLocation")
    private String imageLocation;

    @Column(name = "amount")
    private long amount;

    @Column(name = "startDate", length = 45)
    private LocalDate startDate;

    @Column(name = "endDate", length = 45)
    private LocalDate endDate;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "company_id")
    @JsonBackReference
    private Company company;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            name = "customer_coupon",
            joinColumns = @JoinColumn(name = "coupon_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id")
    )
    @JsonIgnore
    private List<Customer> customers;

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public Coupon() {
    }

    public Coupon(String name, String description, String imageLocation, long amount, LocalDate startDate, LocalDate endDate, Company company, List<Customer> customers) {
        this.name = name;
        this.description = description;
        this.imageLocation = imageLocation;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.company = company;
        this.customers = customers;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @Override
    public String toString() {
        return "Coupon{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", imageLocation='" + imageLocation + '\'' +
                ", amount=" + amount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }

    /**
     * Convenience method for adding customer to customer list.<br>
     * Initialize list if not initialized.
     */
    public void addCustomer(Customer customer) {

        if (customers == null) {
            customers = new ArrayList<>();
        }
        customers.add(customer);
    }
}
