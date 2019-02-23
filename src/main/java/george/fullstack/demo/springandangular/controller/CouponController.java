package george.fullstack.demo.springandangular.controller;

import george.fullstack.demo.springandangular.entity.Coupon;
import george.fullstack.demo.springandangular.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coupons/")
//todo  remove @CrossOrigin when build for prod angular -  into the /dist folder
//todo add authentication in version 3
@CrossOrigin(origins = "http://localhost:4200")
public class CouponController {

    private CouponService service;


    @Autowired
    public CouponController(CouponService service) {
        this.service = service;
    }

    @GetMapping()
    public List<Coupon> getAllCoupons() {

        return service.findAllCoupons();
    }

    @GetMapping("{name}/name")
    public Coupon getCouponByName(@PathVariable(value = "name") String name) {

        return service.findByName(name);
    }

    @GetMapping("{id}/id")
//    todo(?) update to custom format exception
    public Coupon getCouponById(@PathVariable(value = "id") String id) {
        long couponId = Long.valueOf(id);

        return service.findById(couponId);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
//    todo note - when created needs a "location" url where to find the newly created item
    public Coupon createCoupon(@RequestBody Coupon coupon) {

        return service.createCoupon(coupon);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCoupon(@RequestBody Coupon coupon) {

        service.updateCoupon(coupon);
    }

    @DeleteMapping("/{id}/id")
    public void deleteCouponById(@PathVariable(value = "id") long id) {

        service.deleteCouponById(id);
    }
}
