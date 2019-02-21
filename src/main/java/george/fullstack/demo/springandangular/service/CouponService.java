package george.fullstack.demo.springandangular.service;

import george.fullstack.demo.springandangular.entity.Coupon;

import java.util.List;


public interface CouponService {

    List<Coupon> findAllCoupons();

    Coupon createCoupon(Coupon coupon);

    Coupon findById(long id);

    Coupon findByName(String name);

    void deleteCouponById(long id);

    void updateCoupon(Coupon theCoupon);
}
