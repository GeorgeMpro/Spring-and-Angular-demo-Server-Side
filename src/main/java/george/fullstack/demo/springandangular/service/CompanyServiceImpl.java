package george.fullstack.demo.springandangular.service;

import george.fullstack.demo.springandangular.dao.CompanyRepository;
import george.fullstack.demo.springandangular.entity.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyServiceImpl implements CompanyService {

    private CompanyRepository repository;

    @Autowired
    public CompanyServiceImpl(CompanyRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Company> findAllCompanies() {
        return repository.findAll();
    }

    @Override
    public Company createCompany(Company company) {
        long id = company.getId();
        String name = company.getName();
        String email = company.getEmail();
        String errorMessage = "Cannot create. Company with this id, name or email already exists";

        if (nameIsDuplicate(name) | emailIsDuplicate(email) | idFound(id))
            throw new CompanyAlreadyExist(errorMessage);

        return repository.save(company);
    }

    @Override
    public Company findById(long id) {
        String errorMessage = "Company not found. For id value: " + id;
        Optional<Company> opt = repository.findById(id);

        return opt.orElseThrow(() -> new NoSuchCompany(errorMessage));

    }

    @Override
    public Company findByName(String name) {
        String errorMessage = "Company not found. For name value " + name;
        Optional<Company> opt = repository.findByName(name);

        return opt.orElseThrow(() -> new NoSuchCompany(errorMessage));
    }

    @Override
    public void updateCompany(Company company) {
        long id = company.getId();
        String cannotFindByIdErrorMessage = "Cannot Update. Company not found. For id value " + id;
        String duplicateErrorMessage = "Cannot update. Company with name or email values already exists.";

        if (!idFound(id))
            throw new NoSuchCompany(cannotFindByIdErrorMessage);

        try {
            repository.save(company);
        } catch (DataIntegrityViolationException e) {
            throw new CompanyAlreadyExist(duplicateErrorMessage);
        }
    }

    @Override
    public void deleteCompanyById(long id) {
        String errorMessage = "Cannot Delete. Company not found. For id value: " + id;
        if (!idFound(id))
            throw new NoSuchCompany(errorMessage);

        repository.deleteById(id);
    }

    private boolean nameIsDuplicate(String name) {
        Optional<Company> opt = repository.findByName(name);
        return opt.isPresent();
    }

    private boolean emailIsDuplicate(String email) {
        Optional<Company> opt = repository.findByEmail(email);
        return opt.isPresent();
    }

    private boolean idFound(long id) {
        Optional<Company> opt = repository.findById(id);
        return opt.isPresent();
    }

    public static class CompanyAlreadyExist extends RuntimeException {
        public CompanyAlreadyExist(String message) {
            super(message);
        }
    }

    public static class NoSuchCompany extends RuntimeException {
        public NoSuchCompany(String message) {
            super(message);
        }
    }
}
