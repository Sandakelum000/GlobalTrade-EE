package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.dto.common.CompanyResponse;
import com.globaltrade.logistics.core.dto.common.CountryResponse;
import jakarta.ejb.Local;

import java.util.List;
import java.util.UUID;

@Local
public interface ContentService {
    List<CountryResponse>  getCountries();
    List<CompanyResponse> getCompaniesByCountryId(UUID countryId);
}
