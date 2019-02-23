package george.fullstack.demo.springandangular.controller;

import george.fullstack.demo.springandangular.entity.Company;
import george.fullstack.demo.springandangular.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/companies")
//todo  remove @CrossOrigin when build for prod angular -  into the /dist folder
//todo add authentication in version 3
@CrossOrigin(origins = "http://localhost:4200")
public class CompanyController {

    private CompanyService service;

    @Autowired
    public CompanyController(CompanyService service) {
        this.service = service;
    }


    @GetMapping()
    public List<Company> getAllCompanies() {

        return service.findAllCompanies();
    }

    @GetMapping("{id}/id")
    public Company getCompanyById(@PathVariable String id) {
//todo (?) check format exception - already caught in Global Handler

        long companyId = Long.valueOf(id);

        return service.findById(companyId);
    }

    @GetMapping("{name}/name")
    public Company getCompanyByName(@PathVariable String name) {

        return service.findByName(name);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Company createCompany(@RequestBody Company company) {

        return service.createCompany(company);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCompany(Company company) {

        service.updateCompany(company);
    }

    @DeleteMapping("{id}/id")
    public void deleteCompanyById(@PathVariable String id) {
        long companyId = Long.valueOf(id);

        service.deleteCompanyById(companyId);
    }
}
