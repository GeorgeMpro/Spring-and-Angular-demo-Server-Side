package george.fullstack.demo.springandangular.service;

import george.fullstack.demo.springandangular.entity.Company;

import java.util.List;

public interface CompanyService {

    List<Company> findAllCompanies();

    Company createCompany(Company company);

    Company findById(long id);

    Company findByName(String name);

    void deleteCompanyById(long id);

    void updateCompany(Company theCoupon);
}
