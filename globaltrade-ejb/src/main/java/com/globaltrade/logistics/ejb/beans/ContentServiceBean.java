package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.dto.common.CompanyResponse;
import com.globaltrade.logistics.core.dto.common.CountryResponse;
import com.globaltrade.logistics.core.entity.common.Country;
import com.globaltrade.logistics.core.entity.company.Company;
import com.globaltrade.logistics.core.service.ContentService;
import com.globaltrade.logistics.ejb.repository.ContentRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;

@Stateless
public class ContentServiceBean implements ContentService {

    @Inject
    private ContentRepository contentRepository;

    @Override
    public List<CountryResponse> getCountries() {
        List<Country> countryList = contentRepository.getCountries();
        return countryList.stream()
                .map(country -> new CountryResponse(
                        country.getId(),
                        country.getName()
                )).toList();
    }

    @Override
    public List<CompanyResponse> getCompaniesByCountryId(UUID countryId) {
        List<Company> companyList = contentRepository.getCompaniesByCountryId(countryId);
        return companyList.stream()
                .map(company -> new CompanyResponse(
                        company.getId(),
                        company.getName() +" "+company.getAddress().getCity()+" "+company.getAddress().getStateProvince()
                )).toList();
    }

}
