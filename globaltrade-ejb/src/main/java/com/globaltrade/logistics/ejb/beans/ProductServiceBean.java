package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.dto.product.ProductRegistrationRequest;
import com.globaltrade.logistics.core.dto.product.ProductRegistrationResponse;
import com.globaltrade.logistics.core.entity.product.Product;
import com.globaltrade.logistics.core.exception.ProductAlreadyExistsException;
import com.globaltrade.logistics.core.service.NumberSequenceService;
import com.globaltrade.logistics.core.service.ProductService;
import com.globaltrade.logistics.ejb.repository.ProductRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.springframework.lang.NonNull;

@Stateless
public class ProductServiceBean implements ProductService {

    private static final String SEQUENCE_KEY = "PRODUCT";
    private static final String PREFIX = "PR";
    private static final int WIDTH = 3;

    @Inject
    private ProductRepository productRepository;

    @Inject
    private NumberSequenceService numberSequenceService;

    @Override
    @RolesAllowed({"ADMIN"})
    @Transactional(Transactional.TxType.REQUIRED)
    public ProductRegistrationResponse registerProduct(@NonNull ProductRegistrationRequest request) {

        if (productRepository.existsByTitle(request.title())) {
            throw new ProductAlreadyExistsException(request.title());
        }

        String nextProductNumber = numberSequenceService.next(
                ProductServiceBean.SEQUENCE_KEY,
                ProductServiceBean.PREFIX,
                ProductServiceBean.WIDTH
        );

        Product product = Product.builder()
                .title(request.title())
                .productNumber(nextProductNumber)
                .description(request.description())
                .reorderLevel(request.reorderLevel())
                .build();

        productRepository.save(product);

        return new ProductRegistrationResponse(
                product.getId(),
                product.getProductNumber(),
                product.getTitle(),
                product.getDescription(),
                product.getReorderLevel()
        );
    }

}
