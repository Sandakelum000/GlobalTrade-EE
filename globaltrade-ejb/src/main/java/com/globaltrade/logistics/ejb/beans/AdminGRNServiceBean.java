package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.annotation.Audited;
import com.globaltrade.logistics.core.dto.admin.grn.AdminGRNDetailsResponse;
import com.globaltrade.logistics.core.dto.admin.grn.AdminGRNItemResponse;
import com.globaltrade.logistics.core.dto.admin.grn.AdminGRNListResponse;
import com.globaltrade.logistics.core.dto.admin.order.PageResponse;
import com.globaltrade.logistics.core.dto.common.CompanyResponse;
import com.globaltrade.logistics.core.dto.common.VendorOptionResponse;
import com.globaltrade.logistics.core.dto.grn.*;
import com.globaltrade.logistics.core.entity.audit.AuditAction;
import com.globaltrade.logistics.core.entity.grn.GRNItem;
import com.globaltrade.logistics.core.entity.grn.GRNStatus;
import com.globaltrade.logistics.core.entity.grn.GoodsReceiveNote;
import com.globaltrade.logistics.core.entity.product.Product;
import com.globaltrade.logistics.core.entity.vendor.Vendor;
import com.globaltrade.logistics.core.entity.warehouse.Inventory;
import com.globaltrade.logistics.core.entity.warehouse.Warehouse;
import com.globaltrade.logistics.core.exception.GRNCreationException;
import com.globaltrade.logistics.core.exception.ResourceNotFoundException;
import com.globaltrade.logistics.core.service.AdminGRNService;
import com.globaltrade.logistics.core.service.NumberSequenceService;
import com.globaltrade.logistics.ejb.interceptor.AuditInterceptor;
import com.globaltrade.logistics.ejb.repository.*;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.transaction.Transactional;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Stateless
@Audited
@Interceptors(AuditInterceptor.class)
public class AdminGRNServiceBean implements AdminGRNService {

    private static final String SEQUENCE_KEY_GRN = "GRN";
    private static final String PREFIX_GRN = "GRN";
    private static final String SEQUENCE_KEY_INVENTORY = "INVENTORY";
    private static final String PREFIX_INVENTORY = "INV";
    private static final int WIDTH = 3;

    @Inject
    private GRNRepository grnRepository;
    @Inject
    private ProductRepository productRepository;
    @Inject
    private WarehouseRepository warehouseRepository;
    @Inject
    private VendorRepository vendorRepository;
    @Inject
    private InventoryRepository inventoryRepository;
    @Inject
    private NumberSequenceService numberSequenceService;

    @Override
    @Audited(
            action = AuditAction.CREATE,
            entity = "GoodsReceiveNote"
    )
    @Transactional(Transactional.TxType.REQUIRED)
    @RolesAllowed({"ADMIN"})
    public GRNRegistrationResponse createGRN(@NonNull GRNRegistrationRequest request) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new GRNCreationException("GRN must contain at least one item");
        }

        Vendor vendor = vendorRepository.findById(request.vendorId())
                .orElseThrow(() -> new GRNCreationException("Vendor not found: " + request.vendorId()));

        Warehouse warehouse = warehouseRepository.getWarehouseById(request.warehouseId())
                .orElseThrow(() -> new GRNCreationException("Warehouse not found: " + request.warehouseId()));

        String nextGRNNumber = numberSequenceService.next(SEQUENCE_KEY_GRN, PREFIX_GRN, WIDTH);


        GoodsReceiveNote grn = GoodsReceiveNote.builder()
                .grnNumber(nextGRNNumber)
                .vendor(vendor)
                .warehouse(warehouse)
                .status(GRNStatus.RECEIVED)
                .receivedAt(LocalDateTime.now())
                .build();

        for (GRNItemRequest grnItemRequest : request.items()) {
            Product product = productRepository.findProductById(grnItemRequest.productId())
                    .orElseThrow(() -> new GRNCreationException("Product not found: " + grnItemRequest.productId()));

            GRNItem grnItem = GRNItem.builder()
                    .product(product)
                    .quantity(grnItemRequest.quantity())
                    .unitCost(grnItemRequest.unitCost())
                    .build();

            grn.addItem(grnItem);
        }
        grnRepository.save(grn);

        if (grn.getItems().size() != request.items().size()) {
            throw new GRNCreationException("GRN item count does not match registration request");
        }

        for(int i = 0; i < grn.getItems().size(); i++) {
            GRNItem grnItem = grn.getItems().get(i);
            GRNItemRequest grnItemRequest = request.items().get(i);

            createInventoryStock(warehouse,grnItem.getProduct(),grnItem,grnItemRequest.sellingPrice());
        }
        return toResponse(grn);
    }

    private void createInventoryStock(Warehouse warehouse, Product product, GRNItem grnItem, BigDecimal sellingPrice) {
        String nextInventoryNumber =
                numberSequenceService.next(SEQUENCE_KEY_INVENTORY, PREFIX_INVENTORY, WIDTH);
        Inventory inventory = Inventory.builder()
                .inventoryNumber(nextInventoryNumber)
                .warehouse(warehouse)
                .product(product)
                .grnItem(grnItem)
                .quantity(grnItem.getQuantity())
                .sellingPrice(sellingPrice)
                .build();

        inventoryRepository.save(inventory);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    @Audited(
            action = AuditAction.CANCEL,
            entity = "GoodsReceiveNote"
    )
    public GRNRegistrationResponse cancelGRN(UUID grnId) {
        GoodsReceiveNote grn = grnRepository.findByGrnId(grnId)
                .orElseThrow(() -> new IllegalArgumentException("Grn not found: " + grnId));

        grn.cancel();
        return toResponse(grn);
    }

    @Override
    @RolesAllowed({"ADMIN"})
    @Transactional(Transactional.TxType.SUPPORTS)
    public PageResponse<AdminGRNListResponse> getGRNs(String search, UUID companyId, UUID warehouseId, GRNStatus status, String sortBy, String direction, int page, int size) {
        if (page < 0) {
            throw new ResourceNotFoundException("Page cannot be negative");
        }

        if (size < 1 || size > 100) {
            throw new ResourceNotFoundException("Page size must be between 1 and 100");
        }

        List<GoodsReceiveNote> grns =
                grnRepository.findGRNs(search, companyId, warehouseId, status, sortBy, direction, page, size);

        long totalElements = grnRepository.countGRNs(search, companyId, warehouseId, status);

        List<AdminGRNListResponse> content = grns.stream()
                        .map(this::toListResponse)
                        .toList();

        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new PageResponse<>(
                content,
                page,
                size,
                totalElements,
                totalPages
        );
    }

    @Override
    @RolesAllowed({"ADMIN"})
    @Transactional(Transactional.TxType.SUPPORTS)
    public AdminGRNDetailsResponse getGRNDetails(UUID grnId) {
        if (grnId == null) {
            throw new ResourceNotFoundException("GRN ID cannot be null");
        }

        GoodsReceiveNote grn = grnRepository.findDetailsById(grnId)
                        .orElseThrow(() -> new ResourceNotFoundException("GRN " + grnId + " not found"));

        return toDetailsResponse(grn);
    }

    @Override
    @RolesAllowed({"ADMIN"})
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<CompanyResponse> getVendorCompanyOptions() {
        return grnRepository.findVendorCompanies().stream()
                .map(company -> new CompanyResponse(
                        company.getId(),
                        company.getName()
                )).toList();
    }

    @Override
    @RolesAllowed({"ADMIN"})
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<VendorOptionResponse> getVendorsByCompanyId(UUID companyId) {
        return vendorRepository.findByCompanyId(companyId)
                .stream()
                .map(vendor -> new VendorOptionResponse(
                        vendor.getId(),
                        vendor.getContactFirstName() +" "+vendor.getContactLastName()
                )).toList();
    }

    private GRNRegistrationResponse toResponse(@NonNull GoodsReceiveNote grn) {
        List<GRNItemResponse> grnItemResponseList = grn.getItems()
                .stream()
                .map(grnItem -> new GRNItemResponse(
                        grnItem.getId(),
                        grnItem.getProduct().getProductNumber(),
                        grnItem.getProduct().getTitle(),
                        grnItem.getQuantity(),
                        grnItem.getUnitCost(),
                        grnItem.getTotalCost()
                )).toList();

        return new GRNRegistrationResponse(
                grn.getId(),
                grn.getGrnNumber(),
                grn.getVendor().getVendorNumber(),
                grn.getVendor().getContactFirstName() + " " + grn.getVendor().getContactLastName(),
                grn.getWarehouse().getName(),
                null,
                grn.getStatus().name(),
                calculateTotalCost(grn),
                grnItemResponseList);

    }

    private BigDecimal calculateTotalCost(@NonNull GoodsReceiveNote grn) {
        return grn.getItems()
                .stream()
                .map(GRNItem::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private AdminGRNListResponse toListResponse(GoodsReceiveNote grn) {

        int itemCount = grn.getItems() == null ? 0 : grn.getItems().size();

        return new AdminGRNListResponse(
                grn.getId(),
                grn.getGrnNumber(),
                grn.getVendor().getContactFirstName() +" "+grn.getVendor().getContactLastName(),
                grn.getVendor().getCompany().getName(),
                grn.getVendor().getCompany().getAddress().getCountry().getName(),

                grn.getWarehouse().getName(),
                itemCount,
                grn.getStatus(),
                grn.getReceivedAt()
        );
    }

    private AdminGRNDetailsResponse toDetailsResponse(GoodsReceiveNote grn) {

        List<AdminGRNItemResponse> items = grn.getItems()
                        .stream()
                        .map(item ->
                                new AdminGRNItemResponse(
                                        item.getId(),
                                        item.getProduct().getId(),
                                        item.getProduct().getTitle(),
                                        item.getQuantity(),
                                        item.getUnitCost(),
                                        item.getTotalCost()
                                )
                        )
                        .toList();

        return new AdminGRNDetailsResponse(
                grn.getId(),
                grn.getGrnNumber(),

                grn.getVendor().getId(),
                grn.getVendor().getContactFirstName()+" "+grn.getVendor().getContactFirstName(),

                grn.getWarehouse().getId(),
                grn.getWarehouse().getName(),

                grn.getStatus(),
                grn.getReceivedAt(),

                items
        );
    }

}
