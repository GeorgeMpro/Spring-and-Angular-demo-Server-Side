package george.fullstack.demo.springandangular.service;

import george.fullstack.demo.springandangular.dao.CouponRepository;
import george.fullstack.demo.springandangular.entity.Coupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CouponServiceImpl implements CouponService {

    private CouponRepository repository;

    @Autowired
    public CouponServiceImpl(CouponRepository theRepository) {
        this.repository = theRepository;
    }

    @Override
    public List<Coupon> findAllCoupons() {

        return repository.findAll();
    }

    @Override
    public Coupon createCoupon(Coupon coupon) {
        long id = coupon.getId();
        String name = coupon.getName();
        String errorMessage = "Cannot create. Coupon with name value: " + name + " already exists";

        if (isDuplicateName(name) | idFound(id))
            throw new CouponAlreadyExist(errorMessage);

        return repository.save(coupon);
    }

    @Override
    public Coupon findById(long id) {
        String errorMessage = "Coupon not found. For id value: " + id;

        Optional<Coupon> opt = repository.findById(id);
        return opt.orElseThrow(() -> {
            throw new NoSuchCoupon(errorMessage);
        });
    }

    @Override
    public Coupon findByName(String name) {
        String message = "Coupon not found. For name value: " + name;
        Optional<Coupon> opt = repository.findByName(name);

        return opt.orElseThrow(() -> {
            throw new NoSuchCoupon(message);
        });
    }

    @Override
    public void deleteCouponById(long id) {
        String errorMessage = "Cannot Delete. Coupon not found. For id value: " + id;
        if (!idFound(id))
            throw new NoSuchCoupon(errorMessage);

        repository.deleteById(id);
    }

    @Override
    public void updateCoupon(Coupon theCoupon) {
        long id = theCoupon.getId();
        String cannotFindErrorMessage = "Cannot Update. Coupon not found. For id value " + id;
        String duplicateErrorMessage = "Cannot update. Coupon with name value " + theCoupon.getName() + " already exists.";
        Optional<Coupon> opt = repository.findById(id);

        if (!opt.isPresent())
            throw new NoSuchCoupon(cannotFindErrorMessage);

        try {
            repository.save(theCoupon);
        } catch (DataIntegrityViolationException e) {
            throw new CouponAlreadyExist(duplicateErrorMessage);
        }
    }

    // todo (?) create util class for entity optional is present checks
    private boolean idFound(long id) {
        Optional<Coupon> opt = repository.findById(id);
        return opt.isPresent();
    }

    private boolean isDuplicateName(String name) {
        Optional<Coupon> optionalCoupon = repository.findByName(name);
        return optionalCoupon.isPresent();
    }

    public static class CouponAlreadyExist extends RuntimeException {
        public CouponAlreadyExist(String message) {
            super(message);
        }
    }

    public static class NoSuchCoupon extends RuntimeException {
        public NoSuchCoupon(String message) {
            super(message);
        }
    }
}
