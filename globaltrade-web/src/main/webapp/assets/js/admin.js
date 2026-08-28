document.addEventListener('DOMContentLoaded', () => {

    const getContextPath = () => {
        const path = window.location.pathname;
        const secondSlash = path.indexOf('/', 1);
        return (secondSlash !== -1 ? path.substring(0, secondSlash) : '') + '/logistics/api';
    };

    const API_BASE_URL = getContextPath();

    // --- AUTHENTICATED FETCH & TOKEN REFRESH ENGINE ---
    let isRefreshing = false;
    let refreshSubscribers = [];

    function clearSession() {
        localStorage.removeItem('access_token');
        localStorage.removeItem('refresh_token');
        localStorage.removeItem('username');
        localStorage.removeItem('roles');
        window.location.href = 'signin.html';
    }

    function onRefreshed(newToken) {
        refreshSubscribers.forEach(cb => cb(newToken));
        refreshSubscribers = [];
    }

    function addRefreshSubscriber(cb) {
        refreshSubscribers.push(cb);
    }

    async function refreshAccessToken() {
        const refreshToken = localStorage.getItem('refresh_token');

        if (!refreshToken) {
            clearSession();
            throw new Error('No refresh token available');
        }

        try {
            const response = await fetch(`${API_BASE_URL}/auth/refresh`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ refreshToken: refreshToken })
            });

            if (!response.ok) {
                clearSession();
                throw new Error('Refresh token invalid or expired');
            }

            const data = await response.json();

            const newAccessToken = data.access || data.accessToken;
            const newRefreshToken = data.refresh || data.refreshToken || refreshToken;

            localStorage.setItem('access_token', newAccessToken);
            localStorage.setItem('refresh_token', newRefreshToken);
            if (data.username) localStorage.setItem('username', data.username);
            if (data.roles) localStorage.setItem('roles', JSON.stringify(data.roles));

            return newAccessToken;
        } catch (err) {
            clearSession();
            throw err;
        }
    }

    async function authenticatedFetch(url, options = {}) {
        options.headers = options.headers || {};
        let accessToken = localStorage.getItem('access_token');

        if (!accessToken) {
            clearSession();
            return new Response(JSON.stringify({ message: "Unauthenticated" }), { status: 401 });
        }

        options.headers['Authorization'] = `Bearer ${accessToken}`;
        if (!options.headers['Content-Type'] && !(options.body instanceof FormData)) {
            options.headers['Content-Type'] = 'application/json';
        }

        let response = await fetch(url, options);

        if (response.status === 401) {
            if (!isRefreshing) {
                isRefreshing = true;

                try {
                    const newAccessToken = await refreshAccessToken();
                    isRefreshing = false;
                    onRefreshed(newAccessToken);

                    options.headers['Authorization'] = `Bearer ${newAccessToken}`;
                    return await fetch(url, options);
                } catch (err) {
                    isRefreshing = false;
                    return response;
                }
            } else {
                return new Promise((resolve) => {
                    addRefreshSubscriber((newToken) => {
                        options.headers['Authorization'] = `Bearer ${newToken}`;
                        resolve(fetch(url, options));
                    });
                });
            }
        }

        if (response.status === 403) {
            showAlert("Access forbidden: You do not have permission to execute this request.", "error");
        }

        return response;
    }

    // Export globally for cross-script utilization
    window.authenticatedFetch = authenticatedFetch;

    // Helper to display ValidationErrorResponse fields
    function showValidationErrorAlert(errorData, alertElem, msgElem, detailElem, fieldElem = null, statusElem = null) {
        if (!alertElem) return;

        let message = 'Validation Error';
        let detail = 'State transition rejected by validation rules.';
        let field = '';
        let status = '';

        // 1. Handle Array of Errors directly: [{ field, message }, ...]
        if (Array.isArray(errorData)) {
            message = 'Multiple Validation Errors';
            detail = errorData.map(err => {
                const f = err.field ? `[${err.field}]: ` : '';
                return `${f}${err.message || err.detail || 'Invalid input'}`;
            }).join(' | ');
            status = errorData[0]?.status || '';

            // 2. Handle Object containing an errors array: { message: "...", errors: [...] }
        } else if (errorData && Array.isArray(errorData.errors)) {
            message = errorData.message || 'Validation Failure';
            detail = errorData.errors.map(err => {
                const f = err.field ? `[${err.field}]: ` : '';
                return `${f}${err.message || err.detail || 'Invalid input'}`;
            }).join(' | ');
            status = errorData.status || '';

            // 3. Handle standard single error object
        } else if (errorData && typeof errorData === 'object') {
            message = errorData.message || 'Validation Error';
            detail = errorData.detail || errorData.message || 'State transition rejected by validation rules.';
            field = errorData.field || '';
            status = errorData.status || '';
        } else if (typeof errorData === 'string') {
            detail = errorData;
        }

        // Populate UI elements
        if (msgElem) msgElem.textContent = message;
        if (detailElem) detailElem.textContent = detail;

        if (fieldElem) {
            if (field) {
                fieldElem.textContent = `Field: ${field}`;
                fieldElem.classList.remove('hidden');
            } else {
                fieldElem.classList.add('hidden');
            }
        }

        if (statusElem) {
            if (status) {
                statusElem.textContent = `HTTP ${status}`;
                statusElem.classList.remove('hidden');
            } else {
                statusElem.classList.add('hidden');
            }
        }

        alertElem.classList.remove('hidden');
    }

    // Core Elements
    const adminSidebar = document.getElementById('adminSidebar');
    const mainWrapper = document.getElementById('mainWrapper');
    const sidebarToggle = document.getElementById('sidebarToggle');
    const toggleIcon = document.getElementById('toggleIcon');
    const sidebarTexts = document.querySelectorAll('.sidebar-text');

    const sidebarButtons = document.querySelectorAll('.sidebar-btn');
    const sectionPanes = document.querySelectorAll('.section-pane');
    const currentPageTitle = document.getElementById('currentPageTitle');
    const currentPageSubtitle = document.getElementById('currentPageSubtitle');

    const usernameDisplay = document.getElementById('usernameDisplay');
    const userAvatar = document.getElementById('userAvatar');
    const roleBadge = document.getElementById('roleBadge');

    const signOutBtn = document.getElementById('signOutBtn');
    const refreshDataBtn = document.getElementById('refreshDataBtn');

    const dashLoader = document.getElementById('dashLoader');
    const dashAlert = document.getElementById('dashAlert');
    const dashAlertIcon = document.getElementById('dashAlertIcon');
    const dashAlertMessage = document.getElementById('dashAlertMessage');

    // Modals
    const orderDetailsModal = document.getElementById('orderDetailsModal');
    const closeOrderModalBtn = document.getElementById('closeOrderModalBtn');
    const modalOrderCloseFooterBtn = document.getElementById('modalOrderCloseFooterBtn');
    const modalOrderLoader = document.getElementById('modalOrderLoader');
    const modalOrderContent = document.getElementById('modalOrderContent');

    const shipmentDetailsModal = document.getElementById('shipmentDetailsModal');
    const closeShipmentModalBtn = document.getElementById('closeShipmentModalBtn');
    const modalShipmentCloseFooterBtn = document.getElementById('modalShipmentCloseFooterBtn');
    const modalShipmentLoader = document.getElementById('modalShipmentLoader');
    const modalShipmentContent = document.getElementById('modalShipmentContent');

    const inventoryDetailsModal = document.getElementById('inventoryDetailsModal');
    const closeInventoryModalBtn = document.getElementById('closeInventoryModalBtn');
    const modalInventoryCloseFooterBtn = document.getElementById('modalInventoryCloseFooterBtn');
    const modalInventoryLoader = document.getElementById('modalInventoryLoader');
    const modalInventoryContent = document.getElementById('modalInventoryContent');

    const grnDetailsModal = document.getElementById('grnDetailsModal');
    const closeGrnModalBtn = document.getElementById('closeGrnModalBtn');
    const modalGrnCloseFooterBtn = document.getElementById('modalGrnCloseFooterBtn');
    const modalGrnLoader = document.getElementById('modalGrnLoader');
    const modalGrnContent = document.getElementById('modalGrnContent');

    const vendorDetailsModal = document.getElementById('vendorDetailsModal');
    const closeVendorModalBtn = document.getElementById('closeVendorModalBtn');
    const modalVendorCloseFooterBtn = document.getElementById('modalVendorCloseFooterBtn');
    const modalVendorLoader = document.getElementById('modalVendorLoader');
    const modalVendorContent = document.getElementById('modalVendorContent');

    // Customer Modal Elements
    const customerDetailsModal = document.getElementById('customerDetailsModal');
    const closeCustomerModalBtn = document.getElementById('closeCustomerModalBtn');
    const modalCustomerCloseFooterBtn = document.getElementById('modalCustomerCloseFooterBtn');
    const modalCustomerLoader = document.getElementById('modalCustomerLoader');
    const modalCustomerContent = document.getElementById('modalCustomerContent');

    // Employee Modal Elements
    const employeeDetailsModal = document.getElementById('employeeDetailsModal');
    const closeEmployeeModalBtn = document.getElementById('closeEmployeeModalBtn');
    const modalEmployeeCloseFooterBtn = document.getElementById('modalEmployeeCloseFooterBtn');
    const modalEmployeeLoader = document.getElementById('modalEmployeeLoader');
    const modalEmployeeContent = document.getElementById('modalEmployeeContent');

    // Register Employee Modal Elements
    const registerEmployeeModal = document.getElementById('registerEmployeeModal');
    const openRegisterEmployeeModalBtn = document.getElementById('openRegisterEmployeeModalBtn');
    const closeRegisterEmployeeModalBtn = document.getElementById('closeRegisterEmployeeModalBtn');
    const cancelRegisterEmployeeBtn = document.getElementById('cancelRegisterEmployeeBtn');
    const submitRegisterEmployeeBtn = document.getElementById('submitRegisterEmployeeBtn');
    const registerEmployeeForm = document.getElementById('registerEmployeeForm');
    const regEmpCountryId = document.getElementById('regEmpCountryId');
    const regEmpBtnSpinner = document.getElementById('regEmpBtnSpinner');
    const regEmpBtnIcon = document.getElementById('regEmpBtnIcon');
    const registerEmployeeModalAlert = document.getElementById('registerEmployeeModalAlert');
    const registerEmployeeAlertMessage = document.getElementById('registerEmployeeAlertMessage');
    const registerEmployeeAlertDetail = document.getElementById('registerEmployeeAlertDetail');
    const registerEmployeeAlertStatus = document.getElementById('registerEmployeeAlertStatus');

    // Vendor Status Activation/Deactivation Modal Elements
    const vendorStatusModal = document.getElementById('vendorStatusModal');
    const closeVendorStatusModalBtn = document.getElementById('closeVendorStatusModalBtn');
    const vendorStatusModalDismissBtn = document.getElementById('vendorStatusModalDismissBtn');
    const confirmVendorStatusBtn = document.getElementById('confirmVendorStatusBtn');
    const confirmVendorStatusBtnText = document.getElementById('confirmVendorStatusBtnText');
    const vendorStatusBtnSpinner = document.getElementById('vendorStatusBtnSpinner');
    const vendorStatusBtnIcon = document.getElementById('vendorStatusBtnIcon');
    const vendorStatusModalAlert = document.getElementById('vendorStatusModalAlert');
    const vendorStatusModalAlertMessage = document.getElementById('vendorStatusModalAlertMessage');
    const vendorStatusModalAlertField = document.getElementById('vendorStatusModalAlertField');
    const vendorStatusModalAlertDetail = document.getElementById('vendorStatusModalAlertDetail');
    const vendorStatusModalAlertStatus = document.getElementById('vendorStatusModalAlertStatus');

    // Cancel Order Modal Elements
    const cancelOrderModal = document.getElementById('cancelOrderModal');
    const closeCancelModalBtn = document.getElementById('closeCancelModalBtn');
    const cancelModalDismissBtn = document.getElementById('cancelModalDismissBtn');
    const confirmCancelOrderBtn = document.getElementById('confirmCancelOrderBtn');
    const cancelBtnSpinner = document.getElementById('cancelBtnSpinner');
    const cancelBtnIcon = document.getElementById('cancelBtnIcon');
    const cancelModalAlert = document.getElementById('cancelModalAlert');
    const cancelModalAlertMessage = document.getElementById('cancelModalAlertMessage');
    const cancelModalAlertField = document.getElementById('cancelModalAlertField');
    const cancelModalAlertDetail = document.getElementById('cancelModalAlertDetail');
    const cancelModalAlertStatus = document.getElementById('cancelModalAlertStatus');

    // Ship Shipment Modal Elements
    const shipConfirmModal = document.getElementById('shipConfirmModal');
    const closeShipConfirmModalBtn = document.getElementById('closeShipConfirmModalBtn');
    const shipModalDismissBtn = document.getElementById('shipModalDismissBtn');
    const confirmShipShipmentBtn = document.getElementById('confirmShipShipmentBtn');
    const shipBtnSpinner = document.getElementById('shipBtnSpinner');
    const shipBtnIcon = document.getElementById('shipBtnIcon');
    const shipModalAlert = document.getElementById('shipModalAlert');
    const shipModalAlertMessage = document.getElementById('shipModalAlertMessage');
    const shipModalAlertField = document.getElementById('shipModalAlertField');
    const shipModalAlertDetail = document.getElementById('shipModalAlertDetail');
    const shipModalAlertStatus = document.getElementById('shipModalAlertStatus');

    // Update Tracking Modal Elements
    const updateTrackingModal = document.getElementById('updateTrackingModal');
    const closeTrackingModalBtn = document.getElementById('closeTrackingModalBtn');
    const trackingModalDismissBtn = document.getElementById('trackingModalDismissBtn');
    const submitTrackingUpdateBtn = document.getElementById('submitTrackingUpdateBtn');
    const trackingForm = document.getElementById('trackingForm');
    const trackingBtnSpinner = document.getElementById('trackingBtnSpinner');
    const trackingBtnIcon = document.getElementById('trackingBtnIcon');
    const trackingModalAlert = document.getElementById('trackingModalAlert');
    const trackingModalAlertMessage = document.getElementById('trackingModalAlertMessage');
    const trackingModalAlertField = document.getElementById('trackingModalAlertField');
    const trackingModalAlertDetail = document.getElementById('trackingModalAlertDetail');
    const trackingModalAlertStatus = document.getElementById('trackingModalAlertStatus');

    // Create GRN Modal Elements
    const createGrnModal = document.getElementById('createGrnModal');
    const openAddGrnModalBtn = document.getElementById('openAddGrnModalBtn');
    const closeCreateGrnModalBtn = document.getElementById('closeCreateGrnModalBtn');
    const cancelCreateGrnModalBtn = document.getElementById('cancelCreateGrnModalBtn');
    const submitCreateGrnBtn = document.getElementById('submitCreateGrnBtn');
    const createGrnForm = document.getElementById('createGrnForm');
    const createGrnCompanySelect = document.getElementById('createGrnCompanySelect');
    const createGrnVendorSelect = document.getElementById('createGrnVendorSelect');
    const createGrnWarehouseSelect = document.getElementById('createGrnWarehouseSelect');
    const grnItemsTableBody = document.getElementById('grnItemsTableBody');
    const addGrnItemRowBtn = document.getElementById('addGrnItemRowBtn');
    const grnModalItemCounter = document.getElementById('grnModalItemCounter');
    const grnModalGrandTotal = document.getElementById('grnModalGrandTotal');
    const createGrnSpinner = document.getElementById('createGrnSpinner');
    const createGrnIcon = document.getElementById('createGrnIcon');
    const createGrnModalAlert = document.getElementById('createGrnModalAlert');
    const createGrnModalAlertMessage = document.getElementById('createGrnModalAlertMessage');
    const createGrnModalAlertField = document.getElementById('createGrnModalAlertField');
    const createGrnModalAlertDetail = document.getElementById('createGrnModalAlertDetail');
    const createGrnModalAlertStatus = document.getElementById('createGrnModalAlertStatus');

    let availableProductsCache = [];

    let revenueChartInstance = null;
    let orderStatusChartInstance = null;
    let monthlyOrdersChartInstance = null;
    let shipmentStatusChartInstance = null;

    let orderToCancelId = null;
    let shipmentToShipId = null;
    let shipmentToUpdateTrackingId = null;
    let vendorToUpdateAction = null;

    // Collapsible Sidebar Controller
    let isCollapsed = false;

    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', () => {
            isCollapsed = !isCollapsed;

            if (isCollapsed) {
                adminSidebar.classList.remove('w-64');
                adminSidebar.classList.add('w-20');
                mainWrapper.classList.remove('ml-64');
                mainWrapper.classList.add('ml-20');
                toggleIcon.className = 'fa-solid fa-angles-right text-sm';
                sidebarTexts.forEach(el => el.classList.add('hidden'));
            } else {
                adminSidebar.classList.remove('w-20');
                adminSidebar.classList.add('w-64');
                mainWrapper.classList.remove('ml-20');
                mainWrapper.classList.add('ml-64');
                toggleIcon.className = 'fa-solid fa-angles-left text-sm';
                sidebarTexts.forEach(el => el.classList.remove('hidden'));
            }
            window.dispatchEvent(new Event('resize'));
        });
    }

    // Status Badging
    function getStatusBadgeHtml(statusStr) {
        const rawStatus = (statusStr || '').toUpperCase();
        let displayLabel = rawStatus.charAt(0) + rawStatus.slice(1).toLowerCase().replace(/_/g, ' ');

        switch (rawStatus) {
            case 'CONFIRMED':
            case 'DELIVERED':
            case 'AVAILABLE':
            case 'ACTIVE':
            case 'IN_STOCK':
            case 'RECEIVED':
                return `<span class="px-2.5 py-1 bg-emerald-100 text-emerald-800 border border-emerald-300 rounded text-xs font-semibold inline-flex items-center gap-1.5"><i class="fa-solid fa-circle-check text-[8px] text-emerald-600"></i> ${displayLabel}</span>`;
            case 'CANCELLED':
            case 'DISCONTINUED':
            case 'OUT_OF_STOCK':
            case 'DAMAGED':
            case 'EXPIRED':
            case 'INACTIVE':
            case 'TERMINATED':
            case 'BLOCKED':
            case 'BLACKLISTED':
                return `<span class="px-2.5 py-1 bg-red-100 text-red-800 border border-red-300 rounded text-xs font-semibold inline-flex items-center gap-1.5"><i class="fa-solid fa-circle-xmark text-[8px] text-red-600"></i> ${displayLabel}</span>`;
            case 'PENDING':
            case 'RESERVED':
            case 'LOW_STOCK':
            case 'QUARANTINED':
            case 'DRAFT':
            case 'SUSPENDED':
            case 'UNDER_REVIEW':
                return `<span class="px-2.5 py-1 bg-amber-100 text-amber-800 border border-amber-300 rounded text-xs font-semibold inline-flex items-center gap-1.5"><i class="fa-solid fa-triangle-exclamation text-[8px] text-amber-600"></i> ${displayLabel}</span>`;
            case 'PROCESSING':
            case 'SHIPPED':
            case 'IN_TRANSIT':
            case 'OUT_FOR_DELIVERY':
                return `<span class="px-2.5 py-1 bg-blue-100 text-blue-800 border border-blue-300 rounded text-xs font-semibold inline-flex items-center gap-1.5"><i class="fa-solid fa-spinner spinner text-[8px] text-blue-600"></i> ${displayLabel}</span>`;
            default:
                return `<span class="px-2.5 py-1 bg-zinc-100 text-zinc-800 border border-zinc-300 rounded text-xs font-semibold inline-flex items-center gap-1.5">${displayLabel}</span>`;
        }
    }

    function getKycBadgeHtml(isVerified) {
        if (isVerified) {
            return `<span class="px-2.5 py-1 bg-emerald-100 text-emerald-800 border border-emerald-300 rounded text-xs font-semibold inline-flex items-center gap-1.5"><i class="fa-solid fa-shield-check text-[10px] text-emerald-600"></i> Verified</span>`;
        }
        return `<span class="px-2.5 py-1 bg-zinc-100 text-zinc-700 border border-zinc-300 rounded text-xs font-semibold inline-flex items-center gap-1.5"><i class="fa-solid fa-shield-halved text-[10px] text-zinc-400"></i> Unverified</span>`;
    }

    function getStockBadge(item) {
        if (item.outOfStock) {
            return `<span class="px-2.5 py-1 bg-red-100 text-red-800 border border-red-300 rounded text-xs font-semibold inline-flex items-center gap-1.5"><i class="fa-solid fa-circle-xmark text-[8px] text-red-600"></i> Out of Stock</span>`;
        }
        if (item.lowStock) {
            return `<span class="px-2.5 py-1 bg-amber-100 text-amber-800 border border-amber-300 rounded text-xs font-semibold inline-flex items-center gap-1.5"><i class="fa-solid fa-triangle-exclamation text-[8px] text-amber-600"></i> Low Stock</span>`;
        }
        return `<span class="px-2.5 py-1 bg-emerald-100 text-emerald-800 border border-emerald-300 rounded text-xs font-semibold inline-flex items-center gap-1.5"><i class="fa-solid fa-circle-check text-[8px] text-emerald-600"></i> Optimal</span>`;
    }

    function formatAddressHtml(addr) {
        if (!addr) return `<span class="text-zinc-400 italic">No address details available.</span>`;

        const countryName = (typeof addr.country === 'object' && addr.country !== null) ? (addr.country.name || 'N/A') : (addr.country || '');
        const line1 = addr.line1 || addr.streetAddress || '';
        const line2 = addr.line2 || '';
        const city = addr.city || '';
        const district = addr.district || '';
        const province = addr.stateProvince || addr.state || '';
        const postal = addr.postalCode || '';

        return `
            <div class="font-bold text-black text-xs">${line1}</div>
            ${line2 ? `<div class="text-zinc-700">${line2}</div>` : ''}
            <div class="text-zinc-600">${city}${district && district !== city ? `, ${district}` : ''}</div>
            <div class="text-zinc-600">${province} ${postal}</div>
            <div class="text-zinc-500 font-semibold pt-1 border-t border-zinc-100 mt-1">${countryName}</div>
        `;
    }

    // Auth Session Guard
    function checkAdminSession() {
        const token = localStorage.getItem('access_token');
        const username = localStorage.getItem('username');
        const rolesRaw = localStorage.getItem('roles');

        if (!token) {
            window.location.href = 'signin.html';
            return false;
        }

        let roles = [];
        if (rolesRaw) {
            try { roles = JSON.parse(rolesRaw); } catch (e) { roles = [rolesRaw]; }
        }

        const isAdmin = roles.some(r => {
            const roleStr = typeof r === 'string' ? r : r.roleType || r.name || '';
            return roleStr.toUpperCase() === 'ADMIN';
        });

        if (!isAdmin) {
            window.location.href = 'dashboard.html';
            return false;
        }

        if (username) {
            if (usernameDisplay) usernameDisplay.textContent = username;
            if (userAvatar) userAvatar.textContent = username.charAt(0).toUpperCase();
        }

        if (roleBadge) roleBadge.textContent = 'Admin';
        return true;
    }

    if (!checkAdminSession()) return;

    // Order Details Modal Controllers
    function openOrderModal() {
        if (orderDetailsModal) orderDetailsModal.classList.remove('hidden');
    }

    function closeOrderModal() {
        if (orderDetailsModal) orderDetailsModal.classList.add('hidden');
    }

    if (closeOrderModalBtn) closeOrderModalBtn.addEventListener('click', closeOrderModal);
    if (modalOrderCloseFooterBtn) modalOrderCloseFooterBtn.addEventListener('click', closeOrderModal);
    if (orderDetailsModal) {
        orderDetailsModal.addEventListener('click', (e) => {
            if (e.target === orderDetailsModal) closeOrderModal();
        });
    }

    // Vendor Details Modal Controllers
    function openVendorModal() {
        if (vendorDetailsModal) vendorDetailsModal.classList.remove('hidden');
    }

    function closeVendorModal() {
        if (vendorDetailsModal) vendorDetailsModal.classList.add('hidden');
    }

    if (closeVendorModalBtn) closeVendorModalBtn.addEventListener('click', closeVendorModal);
    if (modalVendorCloseFooterBtn) modalVendorCloseFooterBtn.addEventListener('click', closeVendorModal);
    if (vendorDetailsModal) {
        vendorDetailsModal.addEventListener('click', (e) => {
            if (e.target === vendorDetailsModal) closeVendorModal();
        });
    }

    // Vendor Inspection Fetcher
    async function fetchAndDisplayVendorDetails(vendorId) {
        if (!vendorId || vendorId === 'undefined') return;

        openVendorModal();
        if (modalVendorLoader) modalVendorLoader.classList.remove('hidden');
        if (modalVendorContent) modalVendorContent.classList.add('hidden');

        try {
            const response = await authenticatedFetch(`${API_BASE_URL}/vendors/${vendorId}`, {
                method: 'GET'
            });

            if (!response.ok) throw new Error(`HTTP Error (${response.status})`);

            const vendorDetails = await response.json();
            renderVendorDetailsModal(vendorDetails);

        } catch (err) {
            showAlert(`Unable to fetch vendor details: ${err.message}`, 'error');
            closeVendorModal();
        } finally {
            if (modalVendorLoader) modalVendorLoader.classList.add('hidden');
            if (modalVendorContent) modalVendorContent.classList.remove('hidden');
        }
    }

    // Render Vendors Table
    function renderVendorsTable(vendors) {
        const tbody = document.getElementById('vendorsTableBody');
        if (!tbody) return;
        tbody.innerHTML = '';

        if (!vendors || vendors.length === 0) {
            tbody.innerHTML = `<tr><td colspan="6" class="py-8 text-center text-zinc-400">No matching vendor records found.</td></tr>`;
            return;
        }

        vendors.forEach(v => {
            const tr = document.createElement('tr');
            tr.className = 'hover:bg-zinc-50/80 cursor-pointer transition-colors';

            const vId = v.id || v.vendorId;
            tr.setAttribute('data-vendor-id', vId);

            const rawStatus = (v.status || '').toUpperCase();
            const isActive = rawStatus === 'ACTIVE';
            const isInactive = rawStatus === 'INACTIVE';
            const score = v.performanceScore != null ? v.performanceScore : 'N/A';

            tr.innerHTML = `
                <td class="py-3.5 px-5 font-bold text-black btn-vendor-details">${v.vendorNumber || 'N/A'}</td>
                <td class="py-3.5 px-5 font-bold text-zinc-800 btn-vendor-details">${v.vendorName || (v.contactFirstName ? `${v.contactFirstName} ${v.contactLastName || ''}`.trim() : 'N/A')}</td>
                <td class="py-3.5 px-5 text-zinc-600 btn-vendor-details">${v.companyName || (v.company ? v.company.name : 'N/A')}</td>
                <td class="py-3.5 px-5 text-center font-bold text-blue-700 btn-vendor-details">${score}</td>
                <td class="py-3.5 px-5 btn-vendor-details vendor-status-cell">${getStatusBadgeHtml(rawStatus)}</td>
                <td class="py-3.5 px-5 text-center action-cells">
                    <div class="flex items-center justify-center gap-1.5">
                        <button class="btn-view-vendor p-1.5 rounded-lg bg-zinc-100 hover:bg-zinc-200 text-zinc-700 border border-zinc-200 transition-colors" title="View Vendor Details">
                            <i class="fa-solid fa-eye text-xs pointer-events-none"></i>
                        </button>
                        <button class="btn-activate-vendor p-1.5 rounded-lg border transition-colors ${
                isActive
                    ? 'bg-zinc-50 text-zinc-300 border-zinc-200 cursor-not-allowed'
                    : 'bg-emerald-50 hover:bg-emerald-100 text-emerald-600 border-emerald-200'
            }" title="${isActive ? 'Vendor is already active' : 'Activate Vendor'}" ${isActive ? 'disabled' : ''}>
                            <i class="fa-solid fa-circle-check text-xs pointer-events-none"></i>
                        </button>
                        <button class="btn-deactivate-vendor p-1.5 rounded-lg border transition-colors ${
                isInactive
                    ? 'bg-zinc-50 text-zinc-300 border-zinc-200 cursor-not-allowed'
                    : 'bg-red-50 hover:bg-red-100 text-red-600 border-red-200'
            }" title="${isInactive ? 'Vendor is already inactive' : 'Deactivate Vendor'}" ${isInactive ? 'disabled' : ''}>
                            <i class="fa-solid fa-circle-xmark text-xs pointer-events-none"></i>
                        </button>
                    </div>
                </td>
            `;

            tr.addEventListener('click', (e) => {
                if (e.target.closest('.action-cells')) return;
                if (vId) fetchAndDisplayVendorDetails(vId);
            });

            const viewBtn = tr.querySelector('.btn-view-vendor');
            if (viewBtn) {
                viewBtn.addEventListener('click', (e) => {
                    e.stopPropagation();
                    if (vId) fetchAndDisplayVendorDetails(vId);
                });
            }

            const activateBtn = tr.querySelector('.btn-activate-vendor');
            if (activateBtn && !isActive) {
                activateBtn.addEventListener('click', (e) => {
                    e.stopPropagation();
                    openVendorStatusModal(v, 'ACTIVE');
                });
            }

            const deactivateBtn = tr.querySelector('.btn-deactivate-vendor');
            if (deactivateBtn && !isInactive) {
                deactivateBtn.addEventListener('click', (e) => {
                    e.stopPropagation();
                    openVendorStatusModal(v, 'INACTIVE');
                });
            }

            tbody.appendChild(tr);
        });
    }

    function renderVendorDetailsModal(data) {
        document.getElementById('modalVendorNumber').textContent = `Vendor ${data.vendorNumber || 'N/A'}`;
        document.getElementById('modalVendorStatusBadge').innerHTML = getStatusBadgeHtml(data.status);

        const joinedDateStr = data.joinedAt ? new Date(data.joinedAt).toLocaleString() : 'N/A';
        document.getElementById('modalVendorMeta').textContent = `Joined: ${joinedDateStr}`;

        document.getElementById('modalVendorTotalGRNs').textContent = (data.totalGRNs || 0).toLocaleString();

        const purchasedValue = data.totalPurchasedValue != null ? parseFloat(data.totalPurchasedValue) : 0;
        document.getElementById('modalVendorTotalValue').textContent = `$${purchasedValue.toLocaleString(undefined, { minimumFractionDigits: 2 })}`;

        const score = data.performanceScore != null ? data.performanceScore : 'N/A';
        document.getElementById('modalVendorScore').textContent = `${score} / 100`;

        const vId = data.vendorId || data.id;
        document.getElementById('modalVendorIdBadge').textContent = vId ? vId.substring(0, 8) : 'N/A';
        document.getElementById('modalVendorContactName').textContent = `${data.contactFirstName || ''} ${data.contactLastName || ''}`.trim() || 'N/A';
        document.getElementById('modalVendorEmail').textContent = data.email || 'N/A';
        document.getElementById('modalVendorMobile1').textContent = data.mobile1 || 'N/A';
        document.getElementById('modalVendorMobile2').textContent = data.mobile2 || 'N/A';

        document.getElementById('modalVendorCompanyName').textContent = data.companyName || (data.company ? data.company.name : 'N/A');
        document.getElementById('modalVendorCompanyId').textContent = data.companyId || 'N/A';

        const lastGrnStr = data.lastGRNDate ? new Date(data.lastGRNDate).toLocaleString() : 'No Receipts Logged';
        document.getElementById('modalVendorLastGRNDate').textContent = lastGrnStr;
    }

    function updateVendorTableRow(updatedVendor) {
        const vId = updatedVendor.id || updatedVendor.vendorId;
        const row = document.querySelector(`tr[data-vendor-id="${vId}"]`);
        if (!row) return;

        const isActive = updatedVendor.status === 'ACTIVE';
        const isInactive = updatedVendor.status === 'INACTIVE';

        const statusCell = row.querySelector('.vendor-status-cell');
        if (statusCell) {
            statusCell.innerHTML = getStatusBadgeHtml(updatedVendor.status);
        }

        const activateBtn = row.querySelector('.btn-activate-vendor');
        if (activateBtn) {
            activateBtn.disabled = isActive;
            activateBtn.className = `btn-activate-vendor p-1.5 rounded-lg border transition-colors ${
                isActive
                    ? 'bg-zinc-50 text-zinc-300 border-zinc-200 cursor-not-allowed'
                    : 'bg-emerald-50 hover:bg-emerald-100 text-emerald-600 border-emerald-200'
            }`;
            activateBtn.title = isActive ? 'Vendor is already active' : 'Activate Vendor';

            const newActivateBtn = activateBtn.cloneNode(true);
            activateBtn.parentNode.replaceChild(newActivateBtn, activateBtn);
            if (!isActive) {
                newActivateBtn.addEventListener('click', (e) => {
                    e.stopPropagation();
                    openVendorStatusModal(updatedVendor, 'ACTIVE');
                });
            }
        }

        const deactivateBtn = row.querySelector('.btn-deactivate-vendor');
        if (deactivateBtn) {
            deactivateBtn.disabled = isInactive;
            deactivateBtn.className = `btn-deactivate-vendor p-1.5 rounded-lg border transition-colors ${
                isInactive
                    ? 'bg-zinc-50 text-zinc-300 border-zinc-200 cursor-not-allowed'
                    : 'bg-red-50 hover:bg-red-100 text-red-600 border-red-200'
            }`;
            deactivateBtn.title = isInactive ? 'Vendor is already inactive' : 'Deactivate Vendor';

            const newDeactivateBtn = deactivateBtn.cloneNode(true);
            deactivateBtn.parentNode.replaceChild(newDeactivateBtn, deactivateBtn);
            if (!isInactive) {
                newDeactivateBtn.addEventListener('click', (e) => {
                    e.stopPropagation();
                    openVendorStatusModal(updatedVendor, 'INACTIVE');
                });
            }
        }
    }

    function openVendorStatusModal(vendor, targetStatus) {
        const vId = vendor.vendorId || vendor.id;
        if (!vId) return;

        vendorToUpdateAction = { id: vId, targetStatus: targetStatus };

        const modalHeader = document.getElementById('vendorStatusModalHeader');
        const iconBox = document.getElementById('vendorStatusModalIconBox');
        const headerIcon = document.getElementById('vendorStatusModalHeaderIcon');
        const titleElem = document.getElementById('vendorStatusModalTitle');
        const subtitleElem = document.getElementById('vendorStatusModalSubtitle');
        const helpText = document.getElementById('vendorStatusModalHelpText');

        document.getElementById('vendorStatusModalRef').textContent = vendor.vendorNumber || 'N/A';
        document.getElementById('vendorStatusModalName').textContent = vendor.vendorName || `${vendor.contactFirstName || ''} ${vendor.contactLastName || ''}`.trim() || 'N/A';
        document.getElementById('vendorStatusModalCompany').textContent = vendor.companyName || (vendor.company ? vendor.company.name : 'N/A');
        document.getElementById('vendorStatusModalCurrent').innerHTML = getStatusBadgeHtml(vendor.status);

        if (targetStatus === 'ACTIVE') {
            modalHeader.className = 'px-6 py-4 border-b border-zinc-200 text-white flex items-center justify-between shrink-0 bg-emerald-600';
            iconBox.className = 'w-9 h-9 rounded-lg bg-emerald-700 border border-emerald-500 flex items-center justify-center shrink-0 text-white';
            headerIcon.className = 'fa-solid fa-circle-check text-base';
            titleElem.textContent = 'Activate Vendor Account';
            subtitleElem.textContent = 'Enable purchase ordering and stock intake';
            confirmVendorStatusBtn.className = 'bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs px-4 py-2 rounded-lg transition-all shadow-sm flex items-center gap-2';
            confirmVendorStatusBtnText.textContent = 'Activate Vendor';
            helpText.innerHTML = 'Activating this vendor allows inbound GRN processing and active purchase contracts.';
        } else {
            modalHeader.className = 'px-6 py-4 border-b border-zinc-200 text-white flex items-center justify-between shrink-0 bg-red-600';
            iconBox.className = 'w-9 h-9 rounded-lg bg-red-700 border border-red-500 flex items-center justify-center shrink-0 text-white';
            headerIcon.className = 'fa-solid fa-circle-xmark text-base';
            titleElem.textContent = 'Deactivate Vendor Account';
            subtitleElem.textContent = 'Suspend ordering and active inventory supply';
            confirmVendorStatusBtn.className = 'bg-red-600 hover:bg-red-700 text-white font-bold text-xs px-4 py-2 rounded-lg transition-all shadow-sm flex items-center gap-2';
            confirmVendorStatusBtnText.textContent = 'Deactivate Vendor';
            helpText.innerHTML = 'Deactivating this vendor will set status to <span class="font-semibold text-red-600">INACTIVE</span> and reject new inbound GRNs.';
        }

        if (vendorStatusModalAlert) vendorStatusModalAlert.classList.add('hidden');
        if (vendorStatusModal) vendorStatusModal.classList.remove('hidden');
    }

    function closeVendorStatusModal() {
        vendorToUpdateAction = null;
        if (vendorStatusModal) vendorStatusModal.classList.add('hidden');
    }

    if (closeVendorStatusModalBtn) closeVendorStatusModalBtn.addEventListener('click', closeVendorStatusModal);
    if (vendorStatusModalDismissBtn) vendorStatusModalDismissBtn.addEventListener('click', closeVendorStatusModal);
    if (vendorStatusModal) {
        vendorStatusModal.addEventListener('click', (e) => {
            if (e.target === vendorStatusModal) closeVendorStatusModal();
        });
    }

    if (confirmVendorStatusBtn) {
        confirmVendorStatusBtn.addEventListener('click', async () => {
            if (!vendorToUpdateAction || !vendorToUpdateAction.id) return;

            const { id: vendorId, targetStatus } = vendorToUpdateAction;

            confirmVendorStatusBtn.disabled = true;
            if (vendorStatusBtnSpinner) vendorStatusBtnSpinner.classList.remove('hidden');
            if (vendorStatusBtnIcon) vendorStatusBtnIcon.classList.add('hidden');
            if (vendorStatusModalAlert) vendorStatusModalAlert.classList.add('hidden');

            const endpointPath = targetStatus === 'ACTIVE'
                ? `${API_BASE_URL}/vendors/activate/${vendorId}`
                : `${API_BASE_URL}/vendors/deactivate/${vendorId}`;

            try {
                const response = await authenticatedFetch(endpointPath, {
                    method: 'PUT'
                });

                if (response.ok) {
                    const updatedVendor = await response.json();
                    closeVendorStatusModal();
                    showAlert(`Vendor status updated to ${updatedVendor.status}.`, 'info');

                    updateVendorTableRow(updatedVendor);
                    fetchAdminDashboard();
                } else {
                    const errorData = await response.json();
                    showValidationErrorAlert(
                        errorData,
                        vendorStatusModalAlert,
                        vendorStatusModalAlertMessage,
                        vendorStatusModalAlertDetail,
                        vendorStatusModalAlertField,
                        vendorStatusModalAlertStatus
                    );
                }
            } catch (err) {
                showValidationErrorAlert(
                    { message: "Network error encountered", detail: err.message },
                    vendorStatusModalAlert,
                    vendorStatusModalAlertMessage,
                    vendorStatusModalAlertDetail,
                    vendorStatusModalAlertField,
                    vendorStatusModalAlertStatus
                );
            } finally {
                confirmVendorStatusBtn.disabled = false;
                if (vendorStatusBtnSpinner) vendorStatusBtnSpinner.classList.add('hidden');
                if (vendorStatusBtnIcon) vendorStatusBtnIcon.classList.remove('hidden');
            }
        });
    }

    // State for Vendors Table Pagination & Query
    let vendorQueryState = {
        search: '',
        companyId: '',
        status: '',
        sortBy: 'vendorNumber',
        direction: 'ASC',
        page: 0,
        size: 20
    };

    let vendorSearchDebounceTimer = null;

    async function fetchAdminVendors() {
        const tbody = document.getElementById('vendorsTableBody');
        if (!tbody) return;

        tbody.innerHTML = `<tr><td colspan="6" class="py-8 text-center text-zinc-500"><i class="fa-solid fa-spinner spinner mr-2"></i>Loading vendor directory...</td></tr>`;

        try {
            const queryParams = new URLSearchParams({
                sortBy: vendorQueryState.sortBy,
                direction: vendorQueryState.direction,
                page: vendorQueryState.page,
                size: vendorQueryState.size
            });

            if (vendorQueryState.search.trim()) queryParams.append('search', vendorQueryState.search.trim());
            if (vendorQueryState.companyId) queryParams.append('companyId', vendorQueryState.companyId);
            if (vendorQueryState.status) queryParams.append('status', vendorQueryState.status);

            const response = await authenticatedFetch(`${API_BASE_URL}/vendors?${queryParams.toString()}`, {
                method: 'GET'
            });

            if (!response.ok) throw new Error(`Failed to load vendors (${response.status})`);

            const data = await response.json();

            renderVendorsTable(data.content || []);
            updateVendorPaginationControls(data);

        } catch (err) {
            tbody.innerHTML = `<tr><td colspan="6" class="py-6 text-center text-red-600">Failed to load vendors. ${err.message}</td></tr>`;
        }
    }

    function updateVendorPaginationControls(pageData) {
        const totalElements = pageData.totalElements || 0;
        const totalPages = pageData.totalPages || 0;
        const currentPage = pageData.page || 0;
        const pageSize = pageData.size || 20;

        const countElem = document.getElementById('vendorsTotalCount');
        const infoElem = document.getElementById('vendorsPageInfo');
        const textElem = document.getElementById('vendorsPaginationText');
        const prevBtn = document.getElementById('vendorPrevPageBtn');
        const nextBtn = document.getElementById('vendorNextPageBtn');

        if (countElem) countElem.textContent = totalElements.toLocaleString();
        if (infoElem) infoElem.textContent = `${totalPages > 0 ? currentPage + 1 : 0} / ${totalPages}`;

        const startItem = totalElements === 0 ? 0 : currentPage * pageSize + 1;
        const endItem = Math.min((currentPage + 1) * pageSize, totalElements);
        if (textElem) textElem.textContent = `Showing ${startItem} to ${endItem} of ${totalElements} entries`;

        if (prevBtn) prevBtn.disabled = currentPage <= 0;
        if (nextBtn) nextBtn.disabled = currentPage >= totalPages - 1 || totalPages === 0;
    }

    function initVendorTableEvents() {
        const searchInput = document.getElementById('vendorSearchInput');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                clearTimeout(vendorSearchDebounceTimer);
                vendorSearchDebounceTimer = setTimeout(() => {
                    vendorQueryState.search = e.target.value;
                    vendorQueryState.page = 0;
                    fetchAdminVendors();
                }, 300);
            });
        }

        const companyFilter = document.getElementById('vendorCompanyFilter');
        if (companyFilter) {
            companyFilter.addEventListener('change', (e) => {
                vendorQueryState.companyId = e.target.value;
                vendorQueryState.page = 0;
                fetchAdminVendors();
            });
        }

        const statusFilter = document.getElementById('vendorStatusFilter');
        if (statusFilter) {
            statusFilter.addEventListener('change', (e) => {
                vendorQueryState.status = e.target.value;
                vendorQueryState.page = 0;
                fetchAdminVendors();
            });
        }

        const pageSizeSelect = document.getElementById('vendorPageSize');
        if (pageSizeSelect) {
            pageSizeSelect.addEventListener('change', (e) => {
                vendorQueryState.size = parseInt(e.target.value, 10);
                vendorQueryState.page = 0;
                fetchAdminVendors();
            });
        }

        const sortableHeaders = document.querySelectorAll('.vendor-sortable-header');
        sortableHeaders.forEach(header => {
            header.addEventListener('click', () => {
                const field = header.getAttribute('data-sort');
                if (vendorQueryState.sortBy === field) {
                    vendorQueryState.direction = vendorQueryState.direction === 'ASC' ? 'DESC' : 'ASC';
                } else {
                    vendorQueryState.sortBy = field;
                    vendorQueryState.direction = 'ASC';
                }

                sortableHeaders.forEach(h => {
                    const icon = h.querySelector('i');
                    if (icon) icon.className = 'fa-solid fa-sort text-zinc-400 ml-1';
                });
                const currentIcon = header.querySelector('i');
                if (currentIcon) {
                    currentIcon.className = vendorQueryState.direction === 'ASC'
                        ? 'fa-solid fa-sort-up text-black ml-1'
                        : 'fa-solid fa-sort-down text-black ml-1';
                }

                fetchAdminVendors();
            });
        });

        const prevBtn = document.getElementById('vendorPrevPageBtn');
        const nextBtn = document.getElementById('vendorNextPageBtn');

        if (prevBtn) {
            prevBtn.addEventListener('click', () => {
                if (vendorQueryState.page > 0) {
                    vendorQueryState.page--;
                    fetchAdminVendors();
                }
            });
        }

        if (nextBtn) {
            nextBtn.addEventListener('click', () => {
                vendorQueryState.page++;
                fetchAdminVendors();
            });
        }
    }

    async function loadVendorDropdownOptions() {
        try {
            const companyResponse = await authenticatedFetch(`${API_BASE_URL}/grn/companies`);
            if (companyResponse.ok) {
                const companies = await companyResponse.json();
                const companySelect = document.getElementById('vendorCompanyFilter');
                if (companySelect) {
                    companySelect.innerHTML = `<option value="">All Companies</option>`;
                    companies.forEach(c => {
                        companySelect.innerHTML += `<option value="${c.id}">${c.name}</option>`;
                    });
                }
            }
        } catch (err) {
            console.error('Failed to load vendor company dropdown options:', err);
        }
    }

    // --- CUSTOMER MODULE ---
    let customerQueryState = {
        search: '',
        companyId: '',
        customerType: '',
        status: '',
        kycVerified: null,
        sortBy: 'customerNumber',
        direction: 'ASC',
        page: 0,
        size: 20
    };

    let customerSearchDebounceTimer = null;

    function openCustomerModal() {
        if (customerDetailsModal) customerDetailsModal.classList.remove('hidden');
    }

    function closeCustomerModal() {
        if (customerDetailsModal) customerDetailsModal.classList.add('hidden');
    }

    if (closeCustomerModalBtn) closeCustomerModalBtn.addEventListener('click', closeCustomerModal);
    if (modalCustomerCloseFooterBtn) modalCustomerCloseFooterBtn.addEventListener('click', closeCustomerModal);
    if (customerDetailsModal) {
        customerDetailsModal.addEventListener('click', (e) => {
            if (e.target === customerDetailsModal) closeCustomerModal();
        });
    }

    async function loadCustomerDropdownOptions() {
        try {
            const response = await authenticatedFetch(`${API_BASE_URL}/admin/customers/companies`);
            if (response.ok) {
                const companies = await response.json();
                const companySelect = document.getElementById('customerCompanyFilter');
                if (companySelect) {
                    companySelect.innerHTML = `<option value="">All Companies</option>`;
                    companies.forEach(c => {
                        companySelect.innerHTML += `<option value="${c.id}">${c.name}</option>`;
                    });
                }
            }
        } catch (err) {
            console.error('Failed to load customer company dropdowns:', err);
        }
    }

    async function fetchAndDisplayCustomerDetails(customerId) {
        if (!customerId) return;

        openCustomerModal();
        if (modalCustomerLoader) modalCustomerLoader.classList.remove('hidden');
        if (modalCustomerContent) modalCustomerContent.classList.add('hidden');

        try {
            const response = await authenticatedFetch(`${API_BASE_URL}/admin/customers/${customerId}`, {
                method: 'GET'
            });

            if (!response.ok) throw new Error(`HTTP Error (${response.status})`);

            const details = await response.json();
            renderCustomerDetailsModal(details);

        } catch (err) {
            showAlert(`Unable to fetch customer details: ${err.message}`, 'error');
            closeCustomerModal();
        } finally {
            if (modalCustomerLoader) modalCustomerLoader.classList.add('hidden');
            if (modalCustomerContent) modalCustomerContent.classList.remove('hidden');
        }
    }

    function renderCustomerDetailsModal(data) {
        document.getElementById('modalCustomerNumber').textContent = `Customer ${data.customerNumber || 'N/A'}`;
        document.getElementById('modalCustomerStatusBadge').innerHTML = getStatusBadgeHtml(data.status);
        document.getElementById('modalCustomerKycBadge').innerHTML = getKycBadgeHtml(data.kycVerified);

        const joinedStr = data.joinedAt ? new Date(data.joinedAt).toLocaleString() : 'N/A';
        document.getElementById('modalCustomerMeta').textContent = `Joined: ${joinedStr}`;

        document.getElementById('modalCustTotalOrders').textContent = (data.totalOrders || 0).toLocaleString();

        const totalValue = data.totalOrderValue != null ? parseFloat(data.totalOrderValue) : 0;
        document.getElementById('modalCustTotalValue').textContent = `$${totalValue.toLocaleString(undefined, { minimumFractionDigits: 2 })}`;

        const lastOrderStr = data.lastOrderDate ? new Date(data.lastOrderDate).toLocaleString() : 'No Orders Executed';
        document.getElementById('modalCustLastOrderDate').textContent = lastOrderStr;

        document.getElementById('modalCustomerIdBadge').textContent = data.customerId ? data.customerId.substring(0, 8) : 'N/A';
        document.getElementById('modalCustFullName').textContent = `${data.firstName || ''} ${data.lastName || ''}`.trim() || 'N/A';
        document.getElementById('modalCustEmailAddr').textContent = data.email || 'N/A';
        document.getElementById('modalCustPhone1').textContent = data.mobile1 || 'N/A';
        document.getElementById('modalCustPhone2').textContent = data.mobile2 || 'N/A';

        document.getElementById('modalCustCompanyName').textContent = data.companyName || 'N/A';
        document.getElementById('modalCustCompanyId').textContent = data.companyId || 'N/A';
        document.getElementById('modalCustTypeTag').textContent = data.customerType || 'N/A';
    }

    async function fetchAdminCustomers() {
        const tbody = document.getElementById('customersTableBody');
        if (!tbody) return;

        tbody.innerHTML = `<tr><td colspan="9" class="py-8 text-center text-zinc-500"><i class="fa-solid fa-spinner spinner mr-2"></i>Loading customer records...</td></tr>`;

        try {
            const queryParams = new URLSearchParams({
                sortBy: customerQueryState.sortBy,
                direction: customerQueryState.direction,
                page: customerQueryState.page,
                size: customerQueryState.size
            });

            if (customerQueryState.search.trim()) queryParams.append('search', customerQueryState.search.trim());
            if (customerQueryState.companyId) queryParams.append('companyId', customerQueryState.companyId);
            if (customerQueryState.customerType) queryParams.append('customerType', customerQueryState.customerType);
            if (customerQueryState.status) queryParams.append('status', customerQueryState.status);
            if (customerQueryState.kycVerified !== null) queryParams.append('kycVerified', customerQueryState.kycVerified);

            const response = await authenticatedFetch(`${API_BASE_URL}/admin/customers?${queryParams.toString()}`, {
                method: 'GET'
            });

            if (!response.ok) throw new Error(`Failed to load customers (${response.status})`);

            const data = await response.json();

            renderCustomersTable(data.content || []);
            updateCustomerPaginationControls(data);

        } catch (err) {
            tbody.innerHTML = `<tr><td colspan="9" class="py-6 text-center text-red-600">Failed to load customer list. ${err.message}</td></tr>`;
        }
    }

    function renderCustomersTable(customers) {
        const tbody = document.getElementById('customersTableBody');
        if (!tbody) return;
        tbody.innerHTML = '';

        if (customers.length === 0) {
            tbody.innerHTML = `<tr><td colspan="9" class="py-8 text-center text-zinc-400">No matching customer records found.</td></tr>`;
            return;
        }

        customers.forEach(cust => {
            const tr = document.createElement('tr');
            tr.className = 'hover:bg-zinc-50/80 cursor-pointer transition-colors';
            const custId = cust.customerId || cust.id;
            tr.setAttribute('data-customer-id', custId);

            const fullName = `${cust.firstName || ''} ${cust.lastName || ''}`.trim() || 'N/A';

            tr.innerHTML = `
                <td class="py-3.5 px-5 font-bold text-black">${cust.customerNumber || 'N/A'}</td>
                <td class="py-3.5 px-5 font-bold text-zinc-800">${fullName}</td>
                <td class="py-3.5 px-5 text-zinc-600">${cust.companyName || 'N/A'}</td>
                <td class="py-3.5 px-5 text-zinc-600">${cust.email || 'N/A'}</td>
                <td class="py-3.5 px-5 text-zinc-600">${cust.mobile1 || 'N/A'}</td>
                <td class="py-3.5 px-5 font-semibold text-indigo-700">${cust.customerType || 'N/A'}</td>
                <td class="py-3.5 px-5">${getStatusBadgeHtml(cust.status)}</td>
                <td class="py-3.5 px-5 text-center">${getKycBadgeHtml(cust.kycVerified)}</td>
                <td class="py-3.5 px-5 text-center action-cells">
                    <button class="btn-view-customer p-1.5 rounded-lg bg-zinc-100 hover:bg-zinc-200 text-zinc-700 border border-zinc-200 transition-colors" title="View Customer Details">
                        <i class="fa-solid fa-eye text-xs pointer-events-none"></i>
                    </button>
                </td>
            `;

            tr.addEventListener('click', (e) => {
                if (e.target.closest('.action-cells')) return;
                if (custId) fetchAndDisplayCustomerDetails(custId);
            });

            const viewBtn = tr.querySelector('.btn-view-customer');
            if (viewBtn) {
                viewBtn.addEventListener('click', (e) => {
                    e.stopPropagation();
                    if (custId) fetchAndDisplayCustomerDetails(custId);
                });
            }

            tbody.appendChild(tr);
        });
    }

    function updateCustomerPaginationControls(pageData) {
        const totalElements = pageData.totalElements || 0;
        const totalPages = pageData.totalPages || 0;
        const currentPage = pageData.page || 0;
        const pageSize = pageData.size || 20;

        const countElem = document.getElementById('customersTotalCount');
        const infoElem = document.getElementById('customersPageInfo');
        const textElem = document.getElementById('customersPaginationText');
        const prevBtn = document.getElementById('customerPrevPageBtn');
        const nextBtn = document.getElementById('customerNextPageBtn');

        if (countElem) countElem.textContent = totalElements.toLocaleString();
        if (infoElem) infoElem.textContent = `${totalPages > 0 ? currentPage + 1 : 0} / ${totalPages}`;

        const startItem = totalElements === 0 ? 0 : currentPage * pageSize + 1;
        const endItem = Math.min((currentPage + 1) * pageSize, totalElements);
        if (textElem) textElem.textContent = `Showing ${startItem} to ${endItem} of ${totalElements} entries`;

        if (prevBtn) prevBtn.disabled = currentPage <= 0;
        if (nextBtn) nextBtn.disabled = currentPage >= totalPages - 1 || totalPages === 0;
    }

    function initCustomerTableEvents() {
        const searchInput = document.getElementById('customerSearchInput');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                clearTimeout(customerSearchDebounceTimer);
                customerSearchDebounceTimer = setTimeout(() => {
                    customerQueryState.search = e.target.value;
                    customerQueryState.page = 0;
                    fetchAdminCustomers();
                }, 300);
            });
        }

        const companyFilter = document.getElementById('customerCompanyFilter');
        if (companyFilter) {
            companyFilter.addEventListener('change', (e) => {
                customerQueryState.companyId = e.target.value;
                customerQueryState.page = 0;
                fetchAdminCustomers();
            });
        }

        const typeFilter = document.getElementById('customerTypeFilter');
        if (typeFilter) {
            typeFilter.addEventListener('change', (e) => {
                customerQueryState.customerType = e.target.value;
                customerQueryState.page = 0;
                fetchAdminCustomers();
            });
        }

        const statusFilter = document.getElementById('customerStatusFilter');
        if (statusFilter) {
            statusFilter.addEventListener('change', (e) => {
                customerQueryState.status = e.target.value;
                customerQueryState.page = 0;
                fetchAdminCustomers();
            });
        }

        const kycFilter = document.getElementById('customerKycFilter');
        if (kycFilter) {
            kycFilter.addEventListener('change', (e) => {
                const val = e.target.value;
                customerQueryState.kycVerified = val === '' ? null : (val === 'true');
                customerQueryState.page = 0;
                fetchAdminCustomers();
            });
        }

        const pageSizeSelect = document.getElementById('customerPageSize');
        if (pageSizeSelect) {
            pageSizeSelect.addEventListener('change', (e) => {
                customerQueryState.size = parseInt(e.target.value, 10);
                customerQueryState.page = 0;
                fetchAdminCustomers();
            });
        }

        const sortableHeaders = document.querySelectorAll('.customer-sortable-header');
        sortableHeaders.forEach(header => {
            header.addEventListener('click', () => {
                const field = header.getAttribute('data-sort');
                if (customerQueryState.sortBy === field) {
                    customerQueryState.direction = customerQueryState.direction === 'ASC' ? 'DESC' : 'ASC';
                } else {
                    customerQueryState.sortBy = field;
                    customerQueryState.direction = 'ASC';
                }

                sortableHeaders.forEach(h => {
                    const icon = h.querySelector('i');
                    if (icon) icon.className = 'fa-solid fa-sort text-zinc-400 ml-1';
                });
                const currentIcon = header.querySelector('i');
                if (currentIcon) {
                    currentIcon.className = customerQueryState.direction === 'ASC'
                        ? 'fa-solid fa-sort-up text-black ml-1'
                        : 'fa-solid fa-sort-down text-black ml-1';
                }

                fetchAdminCustomers();
            });
        });

        const prevBtn = document.getElementById('customerPrevPageBtn');
        const nextBtn = document.getElementById('customerNextPageBtn');

        if (prevBtn) {
            prevBtn.addEventListener('click', () => {
                if (customerQueryState.page > 0) {
                    customerQueryState.page--;
                    fetchAdminCustomers();
                }
            });
        }

        if (nextBtn) {
            nextBtn.addEventListener('click', () => {
                customerQueryState.page++;
                fetchAdminCustomers();
            });
        }
    }

    // --- EMPLOYEE MODULE ---
    let employeeQueryState = {
        search: '',
        department: '',
        status: '',
        sortBy: 'employeeNumber',
        direction: 'ASC',
        page: 0,
        size: 20
    };

    let employeeSearchDebounceTimer = null;

    function openEmployeeModal() {
        if (employeeDetailsModal) employeeDetailsModal.classList.remove('hidden');
    }

    function closeEmployeeModal() {
        if (employeeDetailsModal) employeeDetailsModal.classList.add('hidden');
    }

    if (closeEmployeeModalBtn) closeEmployeeModalBtn.addEventListener('click', closeEmployeeModal);
    if (modalEmployeeCloseFooterBtn) modalEmployeeCloseFooterBtn.addEventListener('click', closeEmployeeModal);
    if (employeeDetailsModal) {
        employeeDetailsModal.addEventListener('click', (e) => {
            if (e.target === employeeDetailsModal) closeEmployeeModal();
        });
    }

    async function loadRegisterCountriesOptions() {
        if (!regEmpCountryId) return;
        try {
            const response = await authenticatedFetch(`${API_BASE_URL}/data/countries`, {
                method: 'GET'
            });
            if (response.ok) {
                const countries = await response.json();
                regEmpCountryId.innerHTML = `<option value="">-- Choose Country --</option>`;
                countries.forEach(c => {
                    const cId = c.id || c.countryId;
                    regEmpCountryId.innerHTML += `<option value="${cId}">${c.name || c.countryName}</option>`;
                });
            }
        } catch (err) {
            console.error('Failed to load countries for employee registration:', err);
        }
    }

    async function openRegisterEmployeeModal() {
        if (registerEmployeeForm) registerEmployeeForm.reset();
        if (registerEmployeeModalAlert) registerEmployeeModalAlert.classList.add('hidden');

        if (registerEmployeeModal) registerEmployeeModal.classList.remove('hidden');
        await loadRegisterCountriesOptions();
    }

    function closeRegisterEmployeeModal() {
        if (registerEmployeeModal) registerEmployeeModal.classList.add('hidden');
    }

    if (openRegisterEmployeeModalBtn) openRegisterEmployeeModalBtn.addEventListener('click', openRegisterEmployeeModal);
    if (closeRegisterEmployeeModalBtn) closeRegisterEmployeeModalBtn.addEventListener('click', closeRegisterEmployeeModal);
    if (cancelRegisterEmployeeBtn) cancelRegisterEmployeeBtn.addEventListener('click', closeRegisterEmployeeModal);

    if (registerEmployeeModal) {
        registerEmployeeModal.addEventListener('click', (e) => {
            if (e.target === registerEmployeeModal) closeRegisterEmployeeModal();
        });
    }

    if (submitRegisterEmployeeBtn) {
        submitRegisterEmployeeBtn.addEventListener('click', async (e) => {
            e.preventDefault();

            const payload = {
                firstName: document.getElementById('regEmpFirstName').value.trim(),
                lastName: document.getElementById('regEmpLastName').value.trim(),
                email: document.getElementById('regEmpEmail').value.trim(),
                mobile1: document.getElementById('regEmpMobile1').value.trim() || null,
                mobile2: document.getElementById('regEmpMobile2').value.trim() || null,
                department: document.getElementById('regEmpDepartment').value || null,
                username: document.getElementById('regEmpUsername').value.trim(),
                password: document.getElementById('regEmpPassword').value,
                countryId: document.getElementById('regEmpCountryId').value || null,
                addressLine1: document.getElementById('regEmpAddressLine1').value.trim(),
                addressLine2: document.getElementById('regEmpAddressLine2').value.trim() || null,
                addressLine3: document.getElementById('regEmpAddressLine3').value.trim() || null,
                city: document.getElementById('regEmpCity').value.trim(),
                district: document.getElementById('regEmpDistrict').value.trim() || null,
                stateProvince: document.getElementById('regEmpStateProvince').value.trim() || null,
                postalCode: document.getElementById('regEmpPostalCode').value.trim(),
                roleType: document.getElementById('regEmpRoleType').value || null
            };

            submitRegisterEmployeeBtn.disabled = true;
            if (regEmpBtnSpinner) regEmpBtnSpinner.classList.remove('hidden');
            if (regEmpBtnIcon) regEmpBtnIcon.classList.add('hidden');
            if (registerEmployeeModalAlert) registerEmployeeModalAlert.classList.add('hidden');

            try {
                const response = await authenticatedFetch(`${API_BASE_URL}/employee/register`, {
                    method: 'POST',
                    body: JSON.stringify(payload)
                });

                if (response.ok) {
                    closeRegisterEmployeeModal();
                    showAlert("Employee registered successfully.", "info");
                    fetchAdminEmployees();
                } else {
                    const errorData = await response.json();
                    showValidationErrorAlert(
                        errorData,
                        registerEmployeeModalAlert,
                        registerEmployeeAlertMessage,
                        registerEmployeeAlertDetail,
                        null,
                        registerEmployeeAlertStatus
                    );
                }
            } catch (err) {
                showValidationErrorAlert(
                    { message: "Network error encountered", detail: err.message },
                    registerEmployeeModalAlert,
                    registerEmployeeAlertMessage,
                    registerEmployeeAlertDetail,
                    null,
                    registerEmployeeAlertStatus
                );
            } finally {
                submitRegisterEmployeeBtn.disabled = false;
                if (regEmpBtnSpinner) regEmpBtnSpinner.classList.add('hidden');
                if (regEmpBtnIcon) regEmpBtnIcon.classList.remove('hidden');
            }
        });
    }

    async function fetchAndDisplayEmployeeDetails(employeeId) {
        if (!employeeId) return;

        openEmployeeModal();
        if (modalEmployeeLoader) modalEmployeeLoader.classList.remove('hidden');
        if (modalEmployeeContent) modalEmployeeContent.classList.add('hidden');

        try {
            const response = await authenticatedFetch(`${API_BASE_URL}/employee/${employeeId}`, {
                method: 'GET'
            });

            if (!response.ok) {
                const errData = await response.json();
                throw new Error(errData.message || `HTTP Error (${response.status})`);
            }

            const employeeDetails = await response.json();
            renderEmployeeDetailsModal(employeeDetails);

        } catch (err) {
            showAlert(`Unable to fetch employee details: ${err.message}`, 'error');
            closeEmployeeModal();
        } finally {
            if (modalEmployeeLoader) modalEmployeeLoader.classList.add('hidden');
            if (modalEmployeeContent) modalEmployeeContent.classList.remove('hidden');
        }
    }

    function renderEmployeeDetailsModal(data) {
        document.getElementById('modalEmployeeNumber').textContent = `Employee ${data.employeeNumber || 'N/A'}`;
        document.getElementById('modalEmployeeStatusBadge').innerHTML = getStatusBadgeHtml(data.status);

        const createdStr = data.createdAt ? new Date(data.createdAt).toLocaleString() : 'N/A';
        document.getElementById('modalEmployeeMeta').textContent = `Created: ${createdStr}`;

        document.getElementById('modalEmployeeIdBadge').textContent = data.employeeId ? data.employeeId.substring(0, 8) : 'N/A';
        document.getElementById('modalEmpFullName').textContent = `${data.firstName || ''} ${data.lastName || ''}`.trim() || 'N/A';
        document.getElementById('modalEmpEmail').textContent = data.email || 'N/A';
        document.getElementById('modalEmpMobile').textContent = data.mobile1 || 'N/A';
        document.getElementById('modalEmpDepartment').textContent = data.department || 'N/A';

        document.getElementById('modalEmpUsername').textContent = data.username || 'N/A';
        document.getElementById('modalEmpUserId').textContent = data.userId || 'N/A';

        const rolesContainer = document.getElementById('modalEmpRolesContainer');
        if (rolesContainer) {
            rolesContainer.innerHTML = '';
            const roles = data.roles || [];
            if (roles.length === 0) {
                rolesContainer.innerHTML = `<span class="text-zinc-400 italic">No roles assigned</span>`;
            } else {
                roles.forEach(role => {
                    rolesContainer.innerHTML += `<span class="bg-black text-white text-[10px] px-2 py-0.5 rounded font-mono font-bold">${role}</span>`;
                });
            }
        }

        const addrContainer = document.getElementById('modalEmpAddressContainer');
        if (addrContainer) {
            addrContainer.innerHTML = formatAddressHtml(data.address);
        }
    }

    async function fetchAdminEmployees() {
        const tbody = document.getElementById('employeesTableBody');
        if (!tbody) return;

        tbody.innerHTML = `<tr><td colspan="7" class="py-8 text-center text-zinc-500"><i class="fa-solid fa-spinner spinner mr-2"></i>Loading employee records...</td></tr>`;

        try {
            const queryParams = new URLSearchParams({
                sortBy: employeeQueryState.sortBy,
                direction: employeeQueryState.direction,
                page: employeeQueryState.page,
                size: employeeQueryState.size
            });

            if (employeeQueryState.search.trim()) queryParams.append('search', employeeQueryState.search.trim());
            if (employeeQueryState.department) queryParams.append('department', employeeQueryState.department);
            if (employeeQueryState.status) queryParams.append('status', employeeQueryState.status);

            const response = await authenticatedFetch(`${API_BASE_URL}/employee?${queryParams.toString()}`, {
                method: 'GET'
            });

            if (!response.ok) throw new Error(`Failed to load employees (${response.status})`);

            const data = await response.json();

            renderEmployeesTable(data.content || []);
            updateEmployeePaginationControls(data);

        } catch (err) {
            tbody.innerHTML = `<tr><td colspan="7" class="py-6 text-center text-red-600">Failed to load employees. ${err.message}</td></tr>`;
        }
    }

    function renderEmployeesTable(employees) {
        const tbody = document.getElementById('employeesTableBody');
        if (!tbody) return;
        tbody.innerHTML = '';

        if (!employees || employees.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" class="py-8 text-center text-zinc-400">No matching employee records found.</td></tr>`;
            return;
        }

        employees.forEach(emp => {
            const tr = document.createElement('tr');
            tr.className = 'hover:bg-zinc-50/80 cursor-pointer transition-colors';
            const empId = emp.employeeId || emp.id;
            tr.setAttribute('data-employee-id', empId);

            const fullName = `${emp.firstName || ''} ${emp.lastName || ''}`.trim() || 'N/A';
            const rawStatus = (emp.status || '').toUpperCase();

            tr.innerHTML = `
                <td class="py-3.5 px-5 font-bold text-black">${emp.employeeNumber || 'N/A'}</td>
                <td class="py-3.5 px-5 font-bold text-zinc-800">${fullName}</td>
                <td class="py-3.5 px-5 text-zinc-600">${emp.email || 'N/A'}</td>
                <td class="py-3.5 px-5 text-zinc-600">${emp.mobile || 'N/A'}</td>
                <td class="py-3.5 px-5 font-semibold text-indigo-700">${emp.roleType || 'N/A'}</td>
                <td class="py-3.5 px-5">${getStatusBadgeHtml(rawStatus)}</td>
                <td class="py-3.5 px-5 text-center action-cells">
                    <button class="btn-view-employee p-1.5 rounded-lg bg-zinc-100 hover:bg-zinc-200 text-zinc-700 border border-zinc-200 transition-colors" title="View Employee Details">
                        <i class="fa-solid fa-eye text-xs pointer-events-none"></i>
                    </button>
                </td>
            `;

            tr.addEventListener('click', (e) => {
                if (e.target.closest('.action-cells')) return;
                if (empId) fetchAndDisplayEmployeeDetails(empId);
            });

            const viewBtn = tr.querySelector('.btn-view-employee');
            if (viewBtn) {
                viewBtn.addEventListener('click', (e) => {
                    e.stopPropagation();
                    if (empId) fetchAndDisplayEmployeeDetails(empId);
                });
            }

            tbody.appendChild(tr);
        });
    }

    function updateEmployeePaginationControls(pageData) {
        const totalElements = pageData.totalElements || 0;
        const totalPages = pageData.totalPages || 0;
        const currentPage = pageData.page || 0;
        const pageSize = pageData.size || 20;

        const countElem = document.getElementById('employeesTotalCount');
        const infoElem = document.getElementById('employeesPageInfo');
        const textElem = document.getElementById('employeesPaginationText');
        const prevBtn = document.getElementById('employeePrevPageBtn');
        const nextBtn = document.getElementById('employeeNextPageBtn');

        if (countElem) countElem.textContent = totalElements.toLocaleString();
        if (infoElem) infoElem.textContent = `${totalPages > 0 ? currentPage + 1 : 0} / ${totalPages}`;

        const startItem = totalElements === 0 ? 0 : currentPage * pageSize + 1;
        const endItem = Math.min((currentPage + 1) * pageSize, totalElements);
        if (textElem) textElem.textContent = `Showing ${startItem} to ${endItem} of ${totalElements} entries`;

        if (prevBtn) prevBtn.disabled = currentPage <= 0;
        if (nextBtn) nextBtn.disabled = currentPage >= totalPages - 1 || totalPages === 0;
    }

    function initEmployeeTableEvents() {
        const searchInput = document.getElementById('employeeSearchInput');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                clearTimeout(employeeSearchDebounceTimer);
                employeeSearchDebounceTimer = setTimeout(() => {
                    employeeQueryState.search = e.target.value;
                    employeeQueryState.page = 0;
                    fetchAdminEmployees();
                }, 300);
            });
        }

        const deptFilter = document.getElementById('employeeDepartmentFilter');
        if (deptFilter) {
            deptFilter.addEventListener('change', (e) => {
                employeeQueryState.department = e.target.value;
                employeeQueryState.page = 0;
                fetchAdminEmployees();
            });
        }

        const statusFilter = document.getElementById('employeeStatusFilter');
        if (statusFilter) {
            statusFilter.addEventListener('change', (e) => {
                employeeQueryState.status = e.target.value;
                employeeQueryState.page = 0;
                fetchAdminEmployees();
            });
        }

        const pageSizeSelect = document.getElementById('employeePageSize');
        if (pageSizeSelect) {
            pageSizeSelect.addEventListener('change', (e) => {
                employeeQueryState.size = parseInt(e.target.value, 10);
                employeeQueryState.page = 0;
                fetchAdminEmployees();
            });
        }

        const sortableHeaders = document.querySelectorAll('.employee-sortable-header');
        sortableHeaders.forEach(header => {
            header.addEventListener('click', () => {
                const field = header.getAttribute('data-sort');
                if (employeeQueryState.sortBy === field) {
                    employeeQueryState.direction = employeeQueryState.direction === 'ASC' ? 'DESC' : 'ASC';
                } else {
                    employeeQueryState.sortBy = field;
                    employeeQueryState.direction = 'ASC';
                }

                sortableHeaders.forEach(h => {
                    const icon = h.querySelector('i');
                    if (icon) icon.className = 'fa-solid fa-sort text-zinc-400 ml-1';
                });
                const currentIcon = header.querySelector('i');
                if (currentIcon) {
                    currentIcon.className = employeeQueryState.direction === 'ASC'
                        ? 'fa-solid fa-sort-up text-black ml-1'
                        : 'fa-solid fa-sort-down text-black ml-1';
                }

                fetchAdminEmployees();
            });
        });

        const prevBtn = document.getElementById('employeePrevPageBtn');
        const nextBtn = document.getElementById('employeeNextPageBtn');

        if (prevBtn) {
            prevBtn.addEventListener('click', () => {
                if (employeeQueryState.page > 0) {
                    employeeQueryState.page--;
                    fetchAdminEmployees();
                }
            });
        }

        if (nextBtn) {
            nextBtn.addEventListener('click', () => {
                employeeQueryState.page++;
                fetchAdminEmployees();
            });
        }
    }

    // Cancel Order Modal Controller
    function openCancelOrderModal(order) {
        orderToCancelId = order.orderId || order.id;
        if (!orderToCancelId) return;

        document.getElementById('cancelModalOrderRef').textContent = order.orderNumber || 'N/A';
        document.getElementById('cancelModalCustName').textContent = order.customerName || 'N/A';
        document.getElementById('cancelModalTotalAmount').textContent = `$${parseFloat(order.totalAmount || 0).toFixed(2)}`;
        document.getElementById('cancelModalCurrentStatus').innerHTML = getStatusBadgeHtml(order.status || order.orderStatus);

        if (cancelModalAlert) cancelModalAlert.classList.add('hidden');
        if (cancelOrderModal) cancelOrderModal.classList.remove('hidden');
    }

    function closeCancelOrderModal() {
        orderToCancelId = null;
        if (cancelOrderModal) cancelOrderModal.classList.add('hidden');
    }

    if (closeCancelModalBtn) closeCancelModalBtn.addEventListener('click', closeCancelOrderModal);
    if (cancelModalDismissBtn) cancelModalDismissBtn.addEventListener('click', closeCancelOrderModal);
    if (cancelOrderModal) {
        cancelOrderModal.addEventListener('click', (e) => {
            if (e.target === cancelOrderModal) closeCancelOrderModal();
        });
    }

    if (confirmCancelOrderBtn) {
        confirmCancelOrderBtn.addEventListener('click', async () => {
            if (!orderToCancelId) return;

            confirmCancelOrderBtn.disabled = true;
            if (cancelBtnSpinner) cancelBtnSpinner.classList.remove('hidden');
            if (cancelBtnIcon) cancelBtnIcon.classList.add('hidden');
            if (cancelModalAlert) cancelModalAlert.classList.add('hidden');

            try {
                const response = await authenticatedFetch(`${API_BASE_URL}/orders/cancel/${orderToCancelId}`, {
                    method: 'POST'
                });

                if (response.ok) {
                    closeCancelOrderModal();
                    showAlert("Order has been successfully cancelled and stock released.", "info");
                    fetchAdminOrders();
                    fetchAdminDashboard();
                } else {
                    const errorData = await response.json();
                    showValidationErrorAlert(
                        errorData,
                        cancelModalAlert,
                        cancelModalAlertMessage,
                        cancelModalAlertDetail,
                        cancelModalAlertField,
                        cancelModalAlertStatus
                    );
                }
            } catch (err) {
                showValidationErrorAlert(
                    { message: "Network error encountered", detail: err.message },
                    cancelModalAlert,
                    cancelModalAlertMessage,
                    cancelModalAlertDetail,
                    cancelModalAlertField,
                    cancelModalAlertStatus
                );
            } finally {
                confirmCancelOrderBtn.disabled = false;
                if (cancelBtnSpinner) cancelBtnSpinner.classList.add('hidden');
                if (cancelBtnIcon) cancelBtnIcon.classList.remove('hidden');
            }
        });
    }

    // Ship Shipment Modal Controller
    function openShipConfirmModal(shipment) {
        shipmentToShipId = shipment.shipmentId || shipment.id;
        if (!shipmentToShipId) return;

        document.getElementById('shipModalShipmentRef').textContent = shipment.shipmentNumber || 'N/A';
        document.getElementById('shipModalOrderRef').textContent = shipment.orderNumber || 'N/A';
        document.getElementById('shipModalWarehouseName').textContent = shipment.warehouseName || 'Main Hub';
        document.getElementById('shipModalCurrentStatus').innerHTML = getStatusBadgeHtml(shipment.status);

        if (shipModalAlert) shipModalAlert.classList.add('hidden');
        if (shipConfirmModal) shipConfirmModal.classList.remove('hidden');
    }

    function closeShipConfirmModal() {
        shipmentToShipId = null;
        if (shipConfirmModal) shipConfirmModal.classList.add('hidden');
    }

    if (closeShipConfirmModalBtn) closeShipConfirmModalBtn.addEventListener('click', closeShipConfirmModal);
    if (shipModalDismissBtn) shipModalDismissBtn.addEventListener('click', closeShipConfirmModal);
    if (shipConfirmModal) {
        shipConfirmModal.addEventListener('click', (e) => {
            if (e.target === shipConfirmModal) closeShipConfirmModal();
        });
    }

    if (confirmShipShipmentBtn) {
        confirmShipShipmentBtn.addEventListener('click', async () => {
            if (!shipmentToShipId) return;

            confirmShipShipmentBtn.disabled = true;
            if (shipBtnSpinner) shipBtnSpinner.classList.remove('hidden');
            if (shipBtnIcon) shipBtnIcon.classList.add('hidden');
            if (shipModalAlert) shipModalAlert.classList.add('hidden');

            try {
                const response = await authenticatedFetch(`${API_BASE_URL}/shipments/ship/${shipmentToShipId}`, {
                    method: 'PUT'
                });

                if (response.ok) {
                    closeShipConfirmModal();
                    showAlert("Shipment dispatched successfully and stock deducted.", "info");
                    fetchAdminShipments();
                    fetchAdminDashboard();
                } else {
                    const errorData = await response.json();
                    showValidationErrorAlert(
                        errorData,
                        shipModalAlert,
                        shipModalAlertMessage,
                        shipModalAlertDetail,
                        shipModalAlertField,
                        shipModalAlertStatus
                    );
                }
            } catch (err) {
                showValidationErrorAlert(
                    { message: "Network error encountered", detail: err.message },
                    shipModalAlert,
                    shipModalAlertMessage,
                    shipModalAlertDetail,
                    shipModalAlertField,
                    shipModalAlertStatus
                );
            } finally {
                confirmShipShipmentBtn.disabled = false;
                if (shipBtnSpinner) shipBtnSpinner.classList.add('hidden');
                if (shipBtnIcon) shipBtnIcon.classList.remove('hidden');
            }
        });
    }

    // Update Tracking Modal Controller
    function openUpdateTrackingModal(shipment) {
        shipmentToUpdateTrackingId = shipment.shipmentId || shipment.id;
        if (!shipmentToUpdateTrackingId) return;

        document.getElementById('trackingModalShipmentRef').textContent = shipment.shipmentNumber || 'N/A';
        document.getElementById('trackingModalCurrentStatus').innerHTML = getStatusBadgeHtml(shipment.status);

        if (trackingForm) trackingForm.reset();
        if (trackingModalAlert) trackingModalAlert.classList.add('hidden');
        if (updateTrackingModal) updateTrackingModal.classList.remove('hidden');
    }

    function closeUpdateTrackingModal() {
        shipmentToUpdateTrackingId = null;
        if (updateTrackingModal) updateTrackingModal.classList.add('hidden');
    }

    if (closeTrackingModalBtn) closeTrackingModalBtn.addEventListener('click', closeUpdateTrackingModal);
    if (trackingModalDismissBtn) trackingModalDismissBtn.addEventListener('click', closeUpdateTrackingModal);
    if (updateTrackingModal) {
        updateTrackingModal.addEventListener('click', (e) => {
            if (e.target === updateTrackingModal) closeUpdateTrackingModal();
        });
    }

    if (submitTrackingUpdateBtn) {
        submitTrackingUpdateBtn.addEventListener('click', async (e) => {
            e.preventDefault();
            if (!shipmentToUpdateTrackingId) return;

            const status = document.getElementById('trackingStatusSelect').value;
            const location = document.getElementById('trackingLocationInput').value.trim();
            const description = document.getElementById('trackingDescriptionInput').value.trim();

            if (!status || !location || !description) {
                showValidationErrorAlert(
                    { message: "Validation Failure", detail: "Please complete all mandatory tracking parameters.", field: "Required Inputs" },
                    trackingModalAlert,
                    trackingModalAlertMessage,
                    trackingModalAlertDetail,
                    trackingModalAlertField,
                    trackingModalAlertStatus
                );
                return;
            }

            submitTrackingUpdateBtn.disabled = true;
            if (trackingBtnSpinner) trackingBtnSpinner.classList.remove('hidden');
            if (trackingBtnIcon) trackingBtnIcon.classList.add('hidden');
            if (trackingModalAlert) trackingModalAlert.classList.add('hidden');

            const payload = {
                status: status,
                description: description,
                location: location
            };

            try {
                const response = await authenticatedFetch(`${API_BASE_URL}/shipments/tracking/${shipmentToUpdateTrackingId}`, {
                    method: 'POST',
                    body: JSON.stringify(payload)
                });

                if (response.ok) {
                    closeUpdateTrackingModal();
                    showAlert("Shipment tracking log recorded successfully.", "info");
                    fetchAdminShipments();
                    fetchAdminDashboard();
                } else {
                    const errorData = await response.json();
                    showValidationErrorAlert(
                        errorData,
                        trackingModalAlert,
                        trackingModalAlertMessage,
                        trackingModalAlertDetail,
                        trackingModalAlertField,
                        trackingModalAlertStatus
                    );
                }
            } catch (err) {
                showValidationErrorAlert(
                    { message: "Network error encountered", detail: err.message },
                    trackingModalAlert,
                    trackingModalAlertMessage,
                    trackingModalAlertDetail,
                    trackingModalAlertField,
                    trackingModalAlertStatus
                );
            } finally {
                submitTrackingUpdateBtn.disabled = false;
                if (trackingBtnSpinner) trackingBtnSpinner.classList.add('hidden');
                if (trackingBtnIcon) trackingBtnIcon.classList.remove('hidden');
            }
        });
    }

    async function fetchAndDisplayOrderDetails(orderId) {
        if (!orderId) return;

        openOrderModal();
        if (modalOrderLoader) modalOrderLoader.classList.remove('hidden');
        if (modalOrderContent) modalOrderContent.classList.add('hidden');

        try {
            const response = await authenticatedFetch(`${API_BASE_URL}/admin/orders/${orderId}`, {
                method: 'GET'
            });

            if (!response.ok) throw new Error(`HTTP Error (${response.status})`);

            const orderDetails = await response.json();
            renderOrderDetailsModal(orderDetails);

        } catch (err) {
            showAlert(`Unable to fetch order details: ${err.message}`, 'error');
            closeOrderModal();
        } finally {
            if (modalOrderLoader) modalOrderLoader.classList.add('hidden');
            if (modalOrderContent) modalOrderContent.classList.remove('hidden');
        }
    }

    function renderOrderDetailsModal(data) {
        const numElem = document.getElementById('modalOrderNumber');
        const badgeElem = document.getElementById('modalOrderStatusBadge');
        const metaElem = document.getElementById('modalOrderMeta');
        const headerPaidElem = document.getElementById('modalHeaderPaidAmount');

        const totalFormatted = data.totalAmount != null ? `$${parseFloat(data.totalAmount).toLocaleString(undefined, { minimumFractionDigits: 2 })}` : '$0.00';

        if (numElem) numElem.textContent = `Order ${data.orderNumber || 'N/A'}`;
        if (badgeElem) badgeElem.innerHTML = getStatusBadgeHtml(data.status);
        if (metaElem) {
            const created = data.createdAt ? new Date(data.createdAt).toLocaleString() : 'N/A';
            metaElem.textContent = `Created: ${created}`;
        }
        if (headerPaidElem) headerPaidElem.textContent = totalFormatted;

        if (data.customer) {
            const c = data.customer;
            document.getElementById('modalCustName').textContent = `${c.firstName || ''} ${c.lastName || ''}`.trim() || 'N/A';
            document.getElementById('modalCustId').textContent = c.id || data.customerId || 'N/A';
            document.getElementById('modalCustUsername').textContent = c.username || 'N/A';
            document.getElementById('modalCustEmail').textContent = c.email || 'N/A';
            document.getElementById('modalCustMobile1').textContent = c.mobile1 || 'N/A';
            document.getElementById('modalCustMobile2').textContent = c.mobile2 || 'N/A';

            const custRefNo = c.customerNumber || (c.id ? `CUS-${c.id.substring(0, 3).toUpperCase()}` : 'CUS-001');
            document.getElementById('modalCustomerNumberBadge').textContent = custRefNo;
        }

        const addrContainer = document.getElementById('modalShippingAddress');
        if (addrContainer) {
            addrContainer.innerHTML = formatAddressHtml(data.shippingAddress);
        }

        const itemsBody = document.getElementById('modalOrderItemsBody');
        const itemCountBadge = document.getElementById('modalItemCountBadge');
        if (itemsBody) {
            itemsBody.innerHTML = '';
            const items = data.items || [];
            if (itemCountBadge) itemCountBadge.textContent = `${items.length} Items`;

            if (items.length === 0) {
                itemsBody.innerHTML = `<tr><td colspan="6" class="py-4 text-center text-zinc-400">No items found for this order.</td></tr>`;
            } else {
                items.forEach(item => {
                    const tr = document.createElement('tr');
                    tr.className = 'hover:bg-zinc-50/80 transition-colors';
                    tr.innerHTML = `
                        <td class="py-2.5 px-4 font-bold text-black">${item.productName || 'N/A'}</td>
                        <td class="py-2.5 px-4 text-zinc-600">${item.warehouseName || 'Main Hub'}</td>
                        <td class="py-2.5 px-4 text-zinc-500">${item.inventoryNumber || 'N/A'}</td>
                        <td class="py-2.5 px-4 text-center font-bold text-black">${item.quantity}</td>
                        <td class="py-2.5 px-4 text-right text-zinc-600">$${parseFloat(item.unitPrice || 0).toFixed(2)}</td>
                        <td class="py-2.5 px-4 text-right font-extrabold text-black">$${parseFloat(item.subtotal || 0).toFixed(2)}</td>
                    `;
                    itemsBody.appendChild(tr);
                });
            }
        }

        const shipmentsContainer = document.getElementById('modalShipmentsContainer');
        if (shipmentsContainer) {
            shipmentsContainer.innerHTML = '';
            const shipments = data.shipments || [];

            if (shipments.length === 0) {
                shipmentsContainer.innerHTML = `<div class="p-4 bg-white border border-zinc-200 rounded-xl text-center text-zinc-400">No shipments generated for this order yet.</div>`;
            } else {
                shipments.forEach(s => {
                    const card = document.createElement('div');
                    card.className = 'bg-white border border-zinc-200 rounded-xl p-4 shadow-sm space-y-3 cursor-pointer hover:border-black transition-colors';

                    const shippedAt = s.shippedAt ? new Date(s.shippedAt).toLocaleString() : 'N/A';
                    const estDelivery = s.estimatedDeliveryDate ? new Date(s.estimatedDeliveryDate).toLocaleDateString() : 'N/A';

                    let trackingHtml = '';
                    const trackingList = s.tracking || [];
                    if (trackingList.length === 0) {
                        trackingHtml = `<p class="text-zinc-400 italic">No tracking updates logged for this shipment.</p>`;
                    } else {
                        trackingHtml = `
                            <div class="space-y-2 pl-2 border-l-2 border-zinc-200">
                                ${trackingList.map(t => `
                                    <div class="relative pl-3">
                                        <div class="absolute -left-[11px] top-1.5 w-2 h-2 rounded-full bg-black"></div>
                                        <div class="flex justify-between text-xs">
                                            <strong class="text-black">${t.status || 'Update'}</strong>
                                            <span class="text-zinc-400">${t.trackingTimestamp ? new Date(t.trackingTimestamp).toLocaleString() : ''}</span>
                                        </div>
                                        <div class="text-zinc-600">${t.description || ''}</div>
                                        <div class="text-zinc-400 text-[11px]"><i class="fa-solid fa-location-arrow text-[9px] mr-1"></i>${t.location || 'Hub'}</div>
                                    </div>
                                `).join('')}
                            </div>
                        `;
                    }

                    card.innerHTML = `
                        <div class="flex items-center justify-between border-b border-zinc-100 pb-2">
                            <div class="flex items-center gap-2">
                                <span class="font-bold text-black">Shipment #${s.shipmentNumber || 'N/A'}</span>
                                ${getStatusBadgeHtml(s.status)}
                            </div>
                            <span class="text-zinc-500 text-xs">Hub: <strong>${s.warehouseName || 'Main'}</strong></span>
                        </div>

                        <div class="grid grid-cols-2 gap-2 text-zinc-600 text-xs">
                            <div>Shipped At: <strong class="text-black">${shippedAt}</strong></div>
                            <div>Est. Delivery: <strong class="text-black">${estDelivery}</strong></div>
                        </div>

                        <div class="pt-2 border-t border-zinc-100">
                            <div class="font-bold text-black text-xs mb-2">Tracking Logs</div>
                            ${trackingHtml}
                        </div>
                    `;

                    card.addEventListener('click', () => {
                        closeOrderModal();
                        fetchAndDisplayShipmentDetails(s.shipmentId || s.id);
                    });

                    shipmentsContainer.appendChild(card);
                });
            }
        }
    }

    // Shipment Modal Controller
    function openShipmentModal() {
        if (shipmentDetailsModal) shipmentDetailsModal.classList.remove('hidden');
    }

    function closeShipmentModal() {
        if (shipmentDetailsModal) shipmentDetailsModal.classList.add('hidden');
    }

    if (closeShipmentModalBtn) closeShipmentModalBtn.addEventListener('click', closeShipmentModal);
    if (modalShipmentCloseFooterBtn) modalShipmentCloseFooterBtn.addEventListener('click', closeShipmentModal);
    if (shipmentDetailsModal) {
        shipmentDetailsModal.addEventListener('click', (e) => {
            if (e.target === shipmentDetailsModal) closeShipmentModal();
        });
    }

    async function fetchAndDisplayShipmentDetails(shipmentId) {
        if (!shipmentId) return;

        openShipmentModal();
        if (modalShipmentLoader) modalShipmentLoader.classList.remove('hidden');
        if (modalShipmentContent) modalShipmentContent.classList.add('hidden');

        try {
            const response = await authenticatedFetch(`${API_BASE_URL}/admin/shipments/${shipmentId}`, {
                method: 'GET'
            });

            if (!response.ok) throw new Error(`HTTP Error (${response.status})`);

            const details = await response.json();
            renderShipmentDetailsModal(details);

        } catch (err) {
            showAlert(`Unable to fetch shipment details: ${err.message}`, 'error');
            closeShipmentModal();
        } finally {
            if (modalShipmentLoader) modalShipmentLoader.classList.add('hidden');
            if (modalShipmentContent) modalShipmentContent.classList.remove('hidden');
        }
    }

    function renderShipmentDetailsModal(data) {
        document.getElementById('modalShipmentNumber').textContent = `Shipment #${data.shipmentNumber || 'N/A'}`;
        document.getElementById('modalShipmentStatusBadge').innerHTML = getStatusBadgeHtml(data.status);
        document.getElementById('modalShipmentMeta').textContent = `Order Ref: ${data.orderNumber || 'N/A'} | Warehouse: ${data.warehouseName || 'N/A'}`;

        document.getElementById('modalRouteDistance').textContent = data.routeDistanceKm != null ? `${data.routeDistanceKm} km` : 'N/A';
        document.getElementById('modalRouteHours').textContent = data.routeEstimatedHours != null ? `${data.routeEstimatedHours} hrs` : 'N/A';
        document.getElementById('modalRouteName').textContent = data.routeName || 'Standard Route';

        const riskScore = data.routeRiskScore != null ? data.routeRiskScore : 0;
        let riskBadgeClass = 'bg-emerald-100 text-emerald-800 border-emerald-300';
        if (riskScore > 60) riskBadgeClass = 'bg-red-100 text-red-800 border-red-300';
        else if (riskScore > 30) riskBadgeClass = 'bg-amber-100 text-amber-800 border-amber-300';

        document.getElementById('modalRouteRiskScore').innerHTML = `<span class="px-2 py-0.5 border rounded font-bold text-xs ${riskBadgeClass}">Score: ${riskScore}</span>`;

        document.getElementById('modalOriginAddress').innerHTML = formatAddressHtml(data.originAddress);
        document.getElementById('modalDestinationAddress').innerHTML = formatAddressHtml(data.destinationAddress);

        const itemsBody = document.getElementById('modalShipmentItemsBody');
        const itemsCount = document.getElementById('modalShipmentItemsCount');
        if (itemsBody) {
            itemsBody.innerHTML = '';
            const items = data.items || [];
            if (itemsCount) itemsCount.textContent = `${items.length} Items`;

            if (items.length === 0) {
                itemsBody.innerHTML = `<tr><td colspan="4" class="py-4 text-center text-zinc-400">No items in this shipment manifest.</td></tr>`;
            } else {
                items.forEach(item => {
                    const tr = document.createElement('tr');
                    tr.className = 'hover:bg-zinc-50/80 transition-colors';
                    tr.innerHTML = `
                        <td class="py-2.5 px-4 font-bold text-black">${item.productName || 'N/A'}</td>
                        <td class="py-2.5 px-4 text-zinc-500 font-mono text-[11px]">${item.orderItemId || 'N/A'}</td>
                        <td class="py-2.5 px-4 text-center font-bold text-black">${item.quantity}</td>
                        <td class="py-2.5 px-4 text-right">${getStatusBadgeHtml(item.status)}</td>
                    `;
                    itemsBody.appendChild(tr);
                });
            }
        }

        const timelineContainer = document.getElementById('modalShipmentTrackingTimeline');
        if (timelineContainer) {
            timelineContainer.innerHTML = '';
            const trackingList = data.tracking || [];

            if (trackingList.length === 0) {
                timelineContainer.innerHTML = `<p class="text-zinc-400 italic">No tracking updates recorded for this shipment yet.</p>`;
            } else {
                trackingList.forEach(t => {
                    const timeStr = t.trackingTimestamp ? new Date(t.trackingTimestamp).toLocaleString() : 'N/A';
                    const div = document.createElement('div');
                    div.className = 'relative pl-4 border-l-2 border-zinc-200 pb-2 last:pb-0';
                    div.innerHTML = `
                        <div class="absolute -left-[5px] top-1.5 w-2 h-2 rounded-full bg-black"></div>
                        <div class="flex items-center justify-between text-xs">
                            <strong class="text-black">${t.status || 'Update'}</strong>
                            <span class="text-zinc-400 text-[11px]">${timeStr}</span>
                        </div>
                        <div class="text-zinc-600 text-xs mt-0.5">${t.description || ''}</div>
                        <div class="text-zinc-500 text-[11px] mt-0.5"><i class="fa-solid fa-location-dot mr-1"></i>${t.location || 'Checkpoint'}</div>
                    `;
                    timelineContainer.appendChild(div);
                });
            }
        }
    }

    // Inventory Modal Controller
    function openInventoryModal() {
        if (inventoryDetailsModal) inventoryDetailsModal.classList.remove('hidden');
    }

    function closeInventoryModal() {
        if (inventoryDetailsModal) inventoryDetailsModal.classList.add('hidden');
    }

    if (closeInventoryModalBtn) closeInventoryModalBtn.addEventListener('click', closeInventoryModal);
    if (modalInventoryCloseFooterBtn) modalInventoryCloseFooterBtn.addEventListener('click', closeInventoryModal);
    if (inventoryDetailsModal) {
        inventoryDetailsModal.addEventListener('click', (e) => {
            if (e.target === inventoryDetailsModal) closeInventoryModal();
        });
    }

    async function fetchAndDisplayInventoryDetails(inventoryId) {
        if (!inventoryId) return;

        openInventoryModal();
        if (modalInventoryLoader) modalInventoryLoader.classList.remove('hidden');
        if (modalInventoryContent) modalInventoryContent.classList.add('hidden');

        try {
            const response = await authenticatedFetch(`${API_BASE_URL}/admin/inventory/${inventoryId}`, {
                method: 'GET'
            });

            if (!response.ok) throw new Error(`HTTP Error (${response.status})`);

            const details = await response.json();
            renderInventoryDetailsModal(details);

        } catch (err) {
            showAlert(`Unable to fetch inventory details: ${err.message}`, 'error');
            closeInventoryModal();
        } finally {
            if (modalInventoryLoader) modalInventoryLoader.classList.add('hidden');
            if (modalInventoryContent) modalInventoryContent.classList.remove('hidden');
        }
    }

    function renderInventoryDetailsModal(data) {
        document.getElementById('modalInventoryNumber').textContent = `Inventory #${data.inventoryNumber || 'N/A'}`;
        document.getElementById('modalInventoryStatusBadge').innerHTML = getStockBadge(data);
        document.getElementById('modalInventoryEnumBadge').innerHTML = getStatusBadgeHtml(data.status);
        document.getElementById('modalInventoryMeta').textContent = `Product: ${data.productName || (data.product ? data.product.title : 'N/A')} | Warehouse: ${data.warehouseName || (data.warehouse ? data.warehouse.name : 'N/A')}`;

        const buyingPrice = data.buyingPrice != null ? parseFloat(data.buyingPrice) : 0;
        const sellingPrice = data.sellingPrice != null ? parseFloat(data.sellingPrice) : 0;
        const margin = sellingPrice - buyingPrice;

        document.getElementById('modalInvBuyingPrice').textContent = `$${buyingPrice.toFixed(2)}`;
        document.getElementById('modalInvSellingPrice').textContent = `$${sellingPrice.toFixed(2)}`;
        document.getElementById('modalInvMargin').textContent = `$${margin.toFixed(2)}`;

        document.getElementById('modalInvProductName').textContent = data.productName || (data.product ? data.product.title : 'N/A');
        document.getElementById('modalInvProductId').textContent = data.productId || (data.product ? data.product.id : 'N/A');

        document.getElementById('modalInvWarehouseName').textContent = data.warehouseName || (data.warehouse ? data.warehouse.name : 'N/A');
        document.getElementById('modalInvWarehouseId').textContent = data.warehouseId || (data.warehouse ? data.warehouse.id : 'N/A');

        document.getElementById('modalInvVendorName').textContent = data.vendorName || (data.vendor ? `${data.vendor.contactFirstName || ''} ${data.vendor.contactLastName || ''}`.trim() : 'N/A');
        document.getElementById('modalInvCountry').textContent = data.country || (data.vendor && data.vendor.company ? data.vendor.company.country : 'N/A');

        const createdDate = data.createdAt ? new Date(data.createdAt).toLocaleString() : 'N/A';
        document.getElementById('modalInvCreatedAt').textContent = `GRN Logged: ${createdDate}`;

        document.getElementById('modalInvTotalQty').textContent = (data.quantity || 0).toLocaleString();
        document.getElementById('modalInvReservedQty').textContent = (data.reservedQuantity || 0).toLocaleString();
        document.getElementById('modalInvAvailableQty').textContent = (data.availableQuantity || 0).toLocaleString();
        document.getElementById('modalInvReorderLevel').textContent = (data.reorderLevel || 0).toLocaleString();
    }

    // Goods Receive Note (GRN) Modal Controller
    function openGrnModal() {
        if (grnDetailsModal) grnDetailsModal.classList.remove('hidden');
    }

    function closeGrnModal() {
        if (grnDetailsModal) grnDetailsModal.classList.add('hidden');
    }

    if (closeGrnModalBtn) closeGrnModalBtn.addEventListener('click', closeGrnModal);
    if (modalGrnCloseFooterBtn) modalGrnCloseFooterBtn.addEventListener('click', closeGrnModal);
    if (grnDetailsModal) {
        grnDetailsModal.addEventListener('click', (e) => {
            if (e.target === grnDetailsModal) closeGrnModal();
        });
    }

    async function fetchAndDisplayGrnDetails(grnId) {
        if (!grnId) return;

        openGrnModal();
        if (modalGrnLoader) modalGrnLoader.classList.remove('hidden');
        if (modalGrnContent) modalGrnContent.classList.add('hidden');

        try {
            const response = await authenticatedFetch(`${API_BASE_URL}/grn/${grnId}`, {
                method: 'GET'
            });

            if (!response.ok) throw new Error(`HTTP Error (${response.status})`);

            const details = await response.json();
            renderGrnDetailsModal(details);

        } catch (err) {
            showAlert(`Unable to fetch GRN details: ${err.message}`, 'error');
            closeGrnModal();
        } finally {
            if (modalGrnLoader) modalGrnLoader.classList.add('hidden');
            if (modalGrnContent) modalGrnContent.classList.remove('hidden');
        }
    }

    function renderGrnDetailsModal(data) {
        document.getElementById('modalGrnNumber').textContent = `GRN ${data.grnNumber || 'N/A'}`;
        document.getElementById('modalGrnStatusBadge').innerHTML = getStatusBadgeHtml(data.status);

        const receivedStr = data.receivedAt ? new Date(data.receivedAt).toLocaleString() : 'N/A';
        document.getElementById('modalGrnMeta').textContent = `Received At: ${receivedStr}`;

        document.getElementById('modalGrnVendorName').textContent = data.vendorName || 'N/A';
        document.getElementById('modalGrnVendorId').textContent = data.vendorId || 'N/A';

        document.getElementById('modalGrnWarehouseName').textContent = data.warehouseName || 'N/A';
        document.getElementById('modalGrnWarehouseId').textContent = data.warehouseId || 'N/A';

        const itemsBody = document.getElementById('modalGrnItemsBody');
        const itemCountBadge = document.getElementById('modalGrnItemCountBadge');
        const totalHeaderCost = document.getElementById('modalHeaderGrnTotalCost');

        let grandTotalCost = 0;

        if (itemsBody) {
            itemsBody.innerHTML = '';
            const items = data.items || [];
            if (itemCountBadge) itemCountBadge.textContent = `${items.length} Items`;

            if (items.length === 0) {
                itemsBody.innerHTML = `<tr><td colspan="5" class="py-4 text-center text-zinc-400">No items registered in this GRN record.</td></tr>`;
            } else {
                items.forEach(item => {
                    const unitCost = item.unitCost != null ? parseFloat(item.unitCost) : 0;
                    const totalCost = item.totalCost != null ? parseFloat(item.totalCost) : (unitCost * (item.quantity || 0));
                    grandTotalCost += totalCost;

                    const tr = document.createElement('tr');
                    tr.className = 'hover:bg-zinc-50/80 transition-colors';
                    tr.innerHTML = `
                        <td class="py-2.5 px-4 font-mono text-zinc-500 text-[11px]">${item.id ? item.id.substring(0, 8) : 'N/A'}</td>
                        <td class="py-2.5 px-4 font-bold text-black">${item.productName || 'N/A'}</td>
                        <td class="py-2.5 px-4 text-center font-bold text-black">${item.quantity || 0}</td>
                        <td class="py-2.5 px-4 text-right text-zinc-600">$${unitCost.toFixed(2)}</td>
                        <td class="py-2.5 px-4 text-right font-extrabold text-black">$${totalCost.toFixed(2)}</td>
                    `;
                    itemsBody.appendChild(tr);
                });
            }
        }

        if (totalHeaderCost) {
            totalHeaderCost.textContent = `$${grandTotalCost.toLocaleString(undefined, { minimumFractionDigits: 2 })}`;
        }
    }

    // State for GRN Pagination & Filters
    let grnQueryState = {
        search: '',
        companyId: '',
        warehouseId: '',
        status: '',
        sortBy: 'receivedAt',
        direction: 'DESC',
        page: 0,
        size: 10
    };

    let grnSearchDebounceTimer = null;

    async function loadGrnDropdownOptions() {
        try {
            const whResponse = await authenticatedFetch(`${API_BASE_URL}/data/warehouses`);
            if (whResponse.ok) {
                const warehouses = await whResponse.json();
                const whSelect = document.getElementById('grnWarehouseFilter');
                if (whSelect) {
                    whSelect.innerHTML = `<option value="">All Warehouses</option>`;
                    warehouses.forEach(w => {
                        whSelect.innerHTML += `<option value="${w.id}">${w.name}</option>`;
                    });
                }
            }

            const companyResponse = await authenticatedFetch(`${API_BASE_URL}/grn/companies`);
            if (companyResponse.ok) {
                const companies = await companyResponse.json();
                const companySelect = document.getElementById('grnCompanyFilter');
                if (companySelect) {
                    companySelect.innerHTML = `<option value="">All Companies</option>`;
                    companies.forEach(c => {
                        companySelect.innerHTML += `<option value="${c.id}">${c.name}</option>`;
                    });
                }
            }
        } catch (err) {
            console.error('Failed to load GRN dropdown options:', err);
        }
    }

    async function populateCreateGrnFormDropdowns() {
        try {
            const compResponse = await authenticatedFetch(`${API_BASE_URL}/grn/companies`);
            if (compResponse.ok) {
                const companies = await compResponse.json();
                createGrnCompanySelect.innerHTML = `<option value="">-- Choose Company --</option>`;
                companies.forEach(c => {
                    createGrnCompanySelect.innerHTML += `<option value="${c.id}">${c.name}</option>`;
                });
            }

            const whResponse = await authenticatedFetch(`${API_BASE_URL}/data/warehouses`);
            if (whResponse.ok) {
                const warehouses = await whResponse.json();
                createGrnWarehouseSelect.innerHTML = `<option value="">-- Select Warehouse --</option>`;
                warehouses.forEach(w => {
                    createGrnWarehouseSelect.innerHTML += `<option value="${w.id}">${w.name}</option>`;
                });
            }

            const prodResponse = await authenticatedFetch(`${API_BASE_URL}/data/products`);
            if (prodResponse.ok) {
                availableProductsCache = await prodResponse.json();
            }
        } catch (err) {
            console.error('Failed to load Create GRN form options:', err);
        }
    }

    if (createGrnCompanySelect) {
        createGrnCompanySelect.addEventListener('change', async (e) => {
            const companyId = e.target.value;
            createGrnVendorSelect.innerHTML = `<option value="">-- Loading Vendors... --</option>`;
            createGrnVendorSelect.disabled = true;

            if (!companyId) {
                createGrnVendorSelect.innerHTML = `<option value="">-- Select Company First --</option>`;
                return;
            }

            try {
                const response = await authenticatedFetch(`${API_BASE_URL}/grn/vendors/${companyId}`);
                if (response.ok) {
                    const vendors = await response.json();
                    if (vendors.length === 0) {
                        createGrnVendorSelect.innerHTML = `<option value="">No Active Vendors for this Company</option>`;
                    } else {
                        createGrnVendorSelect.innerHTML = `<option value="">-- Select Vendor --</option>`;
                        vendors.forEach(v => {
                            const name = `${v.name || ''}`.trim();
                            createGrnVendorSelect.innerHTML += `<option value="${v.id}">${name} (${v.id})</option>`;
                        });
                        createGrnVendorSelect.disabled = false;
                    }
                }
            } catch (err) {
                createGrnVendorSelect.innerHTML = `<option value="">Error Loading Vendors</option>`;
            }
        });
    }

    function addGrnItemRow() {
        if (!grnItemsTableBody) return;

        const tr = document.createElement('tr');
        tr.className = 'grn-item-row hover:bg-zinc-50/80 transition-colors';

        let productOptions = `<option value="">-- Select Product --</option>`;
        availableProductsCache.forEach(p => {
            productOptions += `<option value="${p.id}">${p.name || p.title}</option>`;
        });

        tr.innerHTML = `
        <td class="py-2.5 px-3">
            <select class="item-product-id w-full bg-zinc-50 border border-zinc-200 rounded-lg px-2.5 py-1.5 text-xs text-zinc-800 focus:outline-none focus:border-black" required>
                ${productOptions}
            </select>
        </td>
        <td class="py-2.5 px-3">
            <input type="number" min="1" value="1" class="item-qty w-full text-center bg-zinc-50 border border-zinc-200 rounded-lg px-2.5 py-1.5 text-xs font-bold text-black focus:outline-none focus:border-black" required>
        </td>
        <td class="py-2.5 px-3">
            <input type="number" step="0.01" min="0" placeholder="0.00" class="item-unit-cost w-full text-right bg-zinc-50 border border-zinc-200 rounded-lg px-2.5 py-1.5 text-xs text-black focus:outline-none focus:border-black" required>
        </td>
        <td class="py-2.5 px-3">
            <input type="number" step="0.01" min="0" placeholder="0.00" class="item-selling-price w-full text-right bg-zinc-50 border border-zinc-200 rounded-lg px-2.5 py-1.5 text-xs text-black focus:outline-none focus:border-black" required>
        </td>
        <td class="py-2.5 px-3 text-center">
            <button type="button" class="btn-remove-row p-1.5 rounded-lg text-red-600 hover:bg-red-50 border border-transparent hover:border-red-200 transition-colors" title="Remove Line">
                <i class="fa-solid fa-trash text-xs pointer-events-none"></i>
            </button>
        </td>
    `;

        tr.querySelector('.item-qty').addEventListener('input', updateCreateGrnCalculations);
        tr.querySelector('.item-unit-cost').addEventListener('input', updateCreateGrnCalculations);
        tr.querySelector('.btn-remove-row').addEventListener('click', () => {
            tr.remove();
            updateCreateGrnCalculations();
        });

        grnItemsTableBody.appendChild(tr);
        updateCreateGrnCalculations();
    }

    function updateCreateGrnCalculations() {
        const rows = document.querySelectorAll('.grn-item-row');
        let grandTotal = 0;

        rows.forEach(r => {
            const qty = parseFloat(r.querySelector('.item-qty').value) || 0;
            const cost = parseFloat(r.querySelector('.item-unit-cost').value) || 0;
            grandTotal += (qty * cost);
        });

        if (grnModalItemCounter) grnModalItemCounter.textContent = `${rows.length} ${rows.length === 1 ? 'Line' : 'Lines'}`;
        if (grnModalGrandTotal) grnModalGrandTotal.textContent = `$${grandTotal.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
    }

    async function openCreateGrnModal() {
        if (createGrnForm) createGrnForm.reset();
        if (grnItemsTableBody) grnItemsTableBody.innerHTML = '';
        if (createGrnModalAlert) createGrnModalAlert.classList.add('hidden');
        if (createGrnVendorSelect) {
            createGrnVendorSelect.disabled = true;
            createGrnVendorSelect.innerHTML = `<option value="">-- Select Company First --</option>`;
        }

        if (createGrnModal) createGrnModal.classList.remove('hidden');

        await populateCreateGrnFormDropdowns();
        addGrnItemRow();
    }

    function closeCreateGrnModal() {
        if (createGrnModal) createGrnModal.classList.add('hidden');
    }

    if (openAddGrnModalBtn) openAddGrnModalBtn.addEventListener('click', openCreateGrnModal);
    if (closeCreateGrnModalBtn) closeCreateGrnModalBtn.addEventListener('click', closeCreateGrnModal);
    if (cancelCreateGrnModalBtn) cancelCreateGrnModalBtn.addEventListener('click', closeCreateGrnModal);
    if (addGrnItemRowBtn) addGrnItemRowBtn.addEventListener('click', addGrnItemRow);
    if (createGrnModal) {
        createGrnModal.addEventListener('click', (e) => {
            if (e.target === createGrnModal) closeCreateGrnModal();
        });
    }

    if (submitCreateGrnBtn) {
        submitCreateGrnBtn.addEventListener('click', async (e) => {
            e.preventDefault();

            const vendorId = createGrnVendorSelect.value;
            const warehouseId = createGrnWarehouseSelect.value;

            const rows = document.querySelectorAll('.grn-item-row');
            const items = [];

            rows.forEach(r => {
                const productId = r.querySelector('.item-product-id').value;
                const quantityVal = r.querySelector('.item-qty').value;
                const unitCostVal = r.querySelector('.item-unit-cost').value;
                const sellingPriceVal = r.querySelector('.item-selling-price').value;

                items.push({
                    productId: productId || null,
                    quantity: quantityVal !== "" ? parseInt(quantityVal, 10) : null,
                    unitCost: unitCostVal !== "" ? parseFloat(unitCostVal) : null,
                    sellingPrice: sellingPriceVal !== "" ? parseFloat(sellingPriceVal) : null
                });
            });

            submitCreateGrnBtn.disabled = true;
            if (createGrnSpinner) createGrnSpinner.classList.remove('hidden');
            if (createGrnIcon) createGrnIcon.classList.add('hidden');
            if (createGrnModalAlert) createGrnModalAlert.classList.add('hidden');

            try {
                const response = await authenticatedFetch(`${API_BASE_URL}/grn/create`, {
                    method: 'POST',
                    body: JSON.stringify({
                        vendorId: vendorId || null,
                        warehouseId: warehouseId || null,
                        items: items
                    })
                });

                if (response.ok) {
                    closeCreateGrnModal();
                    showAlert("Goods Receive Note created and inventory restocked successfully.", "info");
                    fetchAdminGRNs();
                    fetchAdminDashboard();
                } else {
                    const errorData = await response.json();
                    showValidationErrorAlert(
                        errorData,
                        createGrnModalAlert,
                        createGrnModalAlertMessage,
                        createGrnModalAlertDetail,
                        createGrnModalAlertField,
                        createGrnModalAlertStatus
                    );
                }
            } catch (err) {
                showValidationErrorAlert(
                    { message: "Network error encountered", detail: err.message },
                    createGrnModalAlert,
                    createGrnModalAlertMessage,
                    createGrnModalAlertDetail,
                    createGrnModalAlertField,
                    createGrnModalAlertStatus
                );
            } finally {
                submitCreateGrnBtn.disabled = false;
                if (createGrnSpinner) createGrnSpinner.classList.add('hidden');
                if (createGrnIcon) createGrnIcon.classList.remove('hidden');
            }
        });
    }

    async function fetchAdminGRNs() {
        const tbody = document.getElementById('grnTableBody');
        if (!tbody) return;

        tbody.innerHTML = `<tr><td colspan="7" class="py-8 text-center text-zinc-500"><i class="fa-solid fa-spinner spinner mr-2"></i>Loading Goods Receive Notes...</td></tr>`;

        try {
            const queryParams = new URLSearchParams({
                sortBy: grnQueryState.sortBy,
                direction: grnQueryState.direction,
                page: grnQueryState.page,
                size: grnQueryState.size
            });

            if (grnQueryState.search.trim()) queryParams.append('search', grnQueryState.search.trim());
            if (grnQueryState.companyId) queryParams.append('companyId', grnQueryState.companyId);
            if (grnQueryState.warehouseId) queryParams.append('warehouseId', grnQueryState.warehouseId);
            if (grnQueryState.status) queryParams.append('status', grnQueryState.status);

            const response = await authenticatedFetch(`${API_BASE_URL}/grn?${queryParams.toString()}`, {
                method: 'GET'
            });

            if (!response.ok) throw new Error(`Failed to load GRN list (${response.status})`);

            const data = await response.json();

            renderGrnTable(data.content || []);
            updateGrnPaginationControls(data);

        } catch (err) {
            tbody.innerHTML = `<tr><td colspan="7" class="py-6 text-center text-red-600">Failed to load GRN records. ${err.message}</td></tr>`;
        }
    }

    function renderGrnTable(grns) {
        const tbody = document.getElementById('grnTableBody');
        if (!tbody) return;
        tbody.innerHTML = '';

        if (grns.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" class="py-8 text-center text-zinc-400">No matching GRN records found.</td></tr>`;
            return;
        }

        grns.forEach(grn => {
            const tr = document.createElement('tr');
            tr.className = 'hover:bg-zinc-50/80 cursor-pointer transition-colors';

            const receivedAtStr = grn.receivedAt ? new Date(grn.receivedAt).toLocaleString() : 'N/A';
            const companyCountryStr = `${grn.vendorCompany || 'N/A'} (${grn.country || 'N/A'})`;

            tr.innerHTML = `
                <td class="py-3.5 px-5 font-bold text-black">${grn.grnNumber || 'N/A'}</td>
                <td class="py-3.5 px-5 font-bold text-zinc-800">${grn.vendorName || 'N/A'}</td>
                <td class="py-3.5 px-5 text-zinc-600">${companyCountryStr}</td>
                <td class="py-3.5 px-5 text-zinc-700 font-medium">${grn.warehouseName || 'Main Hub'}</td>
                <td class="py-3.5 px-5 text-center font-bold text-black">${grn.itemCount || 0}</td>
                <td class="py-3.5 px-5">${getStatusBadgeHtml(grn.status)}</td>
                <td class="py-3.5 px-5 text-right text-zinc-500 text-xs">${receivedAtStr}</td>
            `;

            tr.addEventListener('click', () => {
                const gId = grn.grnId || grn.id;
                if (gId) fetchAndDisplayGrnDetails(gId);
            });

            tbody.appendChild(tr);
        });
    }

    function updateGrnPaginationControls(pageData) {
        const totalElements = pageData.totalElements || 0;
        const totalPages = pageData.totalPages || 0;
        const currentPage = pageData.page || 0;
        const pageSize = pageData.size || 10;

        const countElem = document.getElementById('grnTotalCount');
        const infoElem = document.getElementById('grnPageInfo');
        const textElem = document.getElementById('grnPaginationText');
        const prevBtn = document.getElementById('grnPrevPageBtn');
        const nextBtn = document.getElementById('grnNextPageBtn');

        if (countElem) countElem.textContent = totalElements.toLocaleString();
        if (infoElem) infoElem.textContent = `${totalPages > 0 ? currentPage + 1 : 0} / ${totalPages}`;

        const startItem = totalElements === 0 ? 0 : currentPage * pageSize + 1;
        const endItem = Math.min((currentPage + 1) * pageSize, totalElements);
        if (textElem) textElem.textContent = `Showing ${startItem} to ${endItem} of ${totalElements} entries`;

        if (prevBtn) prevBtn.disabled = currentPage <= 0;
        if (nextBtn) nextBtn.disabled = currentPage >= totalPages - 1 || totalPages === 0;
    }

    function initGrnTableEvents() {
        const searchInput = document.getElementById('grnSearchInput');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                clearTimeout(grnSearchDebounceTimer);
                grnSearchDebounceTimer = setTimeout(() => {
                    grnQueryState.search = e.target.value;
                    grnQueryState.page = 0;
                    fetchAdminGRNs();
                }, 300);
            });
        }

        const companyFilter = document.getElementById('grnCompanyFilter');
        if (companyFilter) {
            companyFilter.addEventListener('change', (e) => {
                grnQueryState.companyId = e.target.value;
                grnQueryState.page = 0;
                fetchAdminGRNs();
            });
        }

        const warehouseFilter = document.getElementById('grnWarehouseFilter');
        if (warehouseFilter) {
            warehouseFilter.addEventListener('change', (e) => {
                grnQueryState.warehouseId = e.target.value;
                grnQueryState.page = 0;
                fetchAdminGRNs();
            });
        }

        const statusFilter = document.getElementById('grnStatusFilter');
        if (statusFilter) {
            statusFilter.addEventListener('change', (e) => {
                grnQueryState.status = e.target.value;
                grnQueryState.page = 0;
                fetchAdminGRNs();
            });
        }

        const pageSizeSelect = document.getElementById('grnPageSize');
        if (pageSizeSelect) {
            pageSizeSelect.addEventListener('change', (e) => {
                grnQueryState.size = parseInt(e.target.value, 10);
                grnQueryState.page = 0;
                fetchAdminGRNs();
            });
        }

        const sortableHeaders = document.querySelectorAll('.grn-sortable-header');
        sortableHeaders.forEach(header => {
            header.addEventListener('click', () => {
                const field = header.getAttribute('data-sort');
                if (grnQueryState.sortBy === field) {
                    grnQueryState.direction = grnQueryState.direction === 'ASC' ? 'DESC' : 'ASC';
                } else {
                    grnQueryState.sortBy = field;
                    grnQueryState.direction = 'ASC';
                }

                sortableHeaders.forEach(h => {
                    const icon = h.querySelector('i');
                    if (icon) icon.className = 'fa-solid fa-sort text-zinc-400 ml-1';
                });
                const currentIcon = header.querySelector('i');
                if (currentIcon) {
                    currentIcon.className = grnQueryState.direction === 'ASC'
                        ? 'fa-solid fa-sort-up text-black ml-1'
                        : 'fa-solid fa-sort-down text-black ml-1';
                }

                fetchAdminGRNs();
            });
        });

        const prevBtn = document.getElementById('grnPrevPageBtn');
        const nextBtn = document.getElementById('grnNextPageBtn');

        if (prevBtn) {
            prevBtn.addEventListener('click', () => {
                if (grnQueryState.page > 0) {
                    grnQueryState.page--;
                    fetchAdminGRNs();
                }
            });
        }

        if (nextBtn) {
            nextBtn.addEventListener('click', () => {
                grnQueryState.page++;
                fetchAdminGRNs();
            });
        }
    }

    // Dashboard Analytics Overview
    async function fetchAdminDashboard() {
        showLoader(true);
        clearAlert();

        try {
            const response = await authenticatedFetch(`${API_BASE_URL}/admin/dashboard`, {
                method: 'GET'
            });

            if (!response.ok) throw new Error(`Server status (${response.status})`);

            const data = await response.json();

            renderSummaryMetrics(data.summary);
            renderCharts(data.monthlyRevenue, data.orderStatus, data.monthlyOrders, data.shipmentStatus);
            renderLowStockTable(data.lowStockItems);
            renderRecentOrdersTable(data.recentOrders);
            renderActiveShipmentsTable(data.activeShipments);

        } catch (err) {
            showAlert(err.message || "Failed to load dashboard analytics.", "error");
        } finally {
            showLoader(false);
        }
    }

    function renderSummaryMetrics(summary) {
        if (!summary) return;

        const rev = document.getElementById('metricTotalRevenue');
        const ord = document.getElementById('metricTotalOrders');
        const ship = document.getElementById('metricActiveShipments');
        const cust = document.getElementById('metricTotalCustomers');
        const vend = document.getElementById('metricTotalVendors');
        const prod = document.getElementById('metricTotalProducts');
        const low = document.getElementById('metricLowStockItems');

        const totalRev = summary.totalRevenue != null ? parseFloat(summary.totalRevenue).toLocaleString(undefined, { minimumFractionDigits: 2 }) : '0.00';

        if (rev) rev.textContent = `$${totalRev}`;
        if (ord) ord.textContent = (summary.totalOrders || 0).toLocaleString();
        if (ship) ship.textContent = (summary.activeShipments || 0).toLocaleString();
        if (cust) cust.textContent = (summary.totalCustomers || 0).toLocaleString();
        if (vend) vend.textContent = (summary.totalVendors || 0).toLocaleString();
        if (prod) prod.textContent = (summary.totalProducts || 0).toLocaleString();
        if (low) low.textContent = (summary.lowStockItems || 0).toLocaleString();
    }

    function renderCharts(monthlyRevenue = [], orderStatus = [], monthlyOrders = [], shipmentStatus = []) {
        const statusColors = {
            'CONFIRMED': '#10b981',
            'DELIVERED': '#059669',
            'CANCELLED': '#ef4444',
            'PENDING': '#f59e0b',
            'PROCESSING': '#3b82f6',
            'SHIPPED': '#8b5cf6'
        };

        const defaultPalette = ['#10b981', '#3b82f6', '#f59e0b', '#8b5cf6', '#ef4444', '#06b6d4'];

        const revenueElem = document.getElementById('monthlyRevenueChart');
        if (revenueElem) {
            const ctx = revenueElem.getContext('2d');
            if (revenueChartInstance) revenueChartInstance.destroy();

            revenueChartInstance = new Chart(ctx, {
                type: 'line',
                data: {
                    labels: monthlyRevenue.map(m => m.month),
                    datasets: [{
                        label: 'Revenue ($)',
                        data: monthlyRevenue.map(m => m.amount),
                        borderColor: '#10b981',
                        backgroundColor: 'rgba(16, 185, 129, 0.12)',
                        fill: true,
                        tension: 0.35,
                        pointBackgroundColor: '#059669',
                        pointBorderColor: '#ffffff',
                        borderWidth: 3
                    }]
                },
                options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false } } }
            });
        }

        const orderStatusElem = document.getElementById('orderStatusPieChart');
        if (orderStatusElem) {
            const ctx = orderStatusElem.getContext('2d');
            if (orderStatusChartInstance) orderStatusChartInstance.destroy();

            const orderColors = orderStatus.map((item, idx) => statusColors[item.status] || defaultPalette[idx % defaultPalette.length]);

            orderStatusChartInstance = new Chart(ctx, {
                type: 'pie',
                data: {
                    labels: orderStatus.map(s => s.status),
                    datasets: [{
                        data: orderStatus.map(s => s.count),
                        backgroundColor: orderColors,
                        borderWidth: 2,
                        borderColor: '#ffffff'
                    }]
                },
                options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'bottom' } } }
            });
        }

        const monthlyOrdersElem = document.getElementById('monthlyOrdersChart');
        if (monthlyOrdersElem) {
            const ctx = monthlyOrdersElem.getContext('2d');
            if (monthlyOrdersChartInstance) monthlyOrdersChartInstance.destroy();

            monthlyOrdersChartInstance = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: monthlyOrders.map(m => m.month),
                    datasets: [{
                        label: 'Order Volume',
                        data: monthlyOrders.map(m => m.count),
                        backgroundColor: '#3b82f6',
                        borderRadius: 6
                    }]
                },
                options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false } } }
            });
        }

        const shipmentStatusElem = document.getElementById('shipmentStatusDoughnutChart');
        if (shipmentStatusElem) {
            const ctx = shipmentStatusElem.getContext('2d');
            if (shipmentStatusChartInstance) shipmentStatusChartInstance.destroy();

            const freightColors = shipmentStatus.map((item, idx) => statusColors[item.status] || defaultPalette[idx % defaultPalette.length]);

            shipmentStatusChartInstance = new Chart(ctx, {
                type: 'doughnut',
                data: {
                    labels: shipmentStatus.map(s => s.status),
                    datasets: [{
                        data: shipmentStatus.map(s => s.count),
                        backgroundColor: freightColors,
                        borderWidth: 2,
                        borderColor: '#ffffff'
                    }]
                },
                options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'bottom' } } }
            });
        }
    }

    function renderLowStockTable(items = []) {
        const tbody = document.getElementById('lowStockTableBody');
        if (!tbody) return;
        tbody.innerHTML = items.length === 0 ? `<tr><td colspan="4" class="py-4 text-center text-zinc-400">All inventory stock levels optimal.</td></tr>` : '';
        items.forEach(item => {
            const tr = document.createElement('tr');
            tr.className = 'hover:bg-zinc-50/80 transition-colors cursor-pointer';
            tr.innerHTML = `
                <td class="py-3 px-5 font-bold text-black">${item.productTitle || (item.product ? item.product.title : 'N/A')}</td>
                <td class="py-3 px-5 text-zinc-500">${item.warehouseName || (item.warehouse ? item.warehouse.name : 'Hub')}</td>
                <td class="py-3 px-5 text-right font-bold text-red-600">${item.availableQuantity}</td>
                <td class="py-3 px-5 text-right text-zinc-600">${item.reorderLevel}</td>
            `;
            tr.addEventListener('click', () => {
                const invId = item.inventoryId || item.id;
                if (invId) fetchAndDisplayInventoryDetails(invId);
            });
            tbody.appendChild(tr);
        });
    }

    function renderRecentOrdersTable(orders = []) {
        const tbody = document.getElementById('recentOrdersTableBody');
        if (!tbody) return;
        tbody.innerHTML = orders.length === 0 ? `<tr><td colspan="4" class="py-4 text-center text-zinc-400">No recent orders recorded.</td></tr>` : '';
        orders.forEach(ord => {
            const tr = document.createElement('tr');
            tr.className = 'hover:bg-zinc-50/80 transition-colors cursor-pointer';
            tr.innerHTML = `
                <td class="py-3 px-5 font-bold text-black">${ord.orderNumber || 'N/A'}</td>
                <td class="py-3 px-5 text-zinc-500">${ord.createdAt ? new Date(ord.createdAt).toLocaleDateString() : 'N/A'}</td>
                <td class="py-3 px-5 font-bold text-black">$${parseFloat(ord.totalAmount || 0).toFixed(2)}</td>
                <td class="py-3 px-5">${getStatusBadgeHtml(ord.status)}</td>
            `;
            tr.addEventListener('click', () => {
                if (ord.orderId || ord.id) fetchAndDisplayOrderDetails(ord.orderId || ord.id);
            });
            tbody.appendChild(tr);
        });
    }

    function renderActiveShipmentsTable(shipments = []) {
        const tbody = document.getElementById('activeShipmentsTableBody');
        if (!tbody) return;
        tbody.innerHTML = shipments.length === 0 ? `<tr><td colspan="5" class="py-6 text-center text-zinc-400">No active freight shipments in transit.</td></tr>` : '';
        shipments.forEach(s => {
            const tr = document.createElement('tr');
            tr.className = 'hover:bg-zinc-50/80 transition-colors cursor-pointer';
            tr.innerHTML = `
                <td class="py-3.5 px-6 font-bold text-black">${s.shipmentNumber || 'N/A'}</td>
                <td class="py-3.5 px-6 text-zinc-500">${s.orderNumber || 'N/A'}</td>
                <td class="py-3.5 px-6 font-medium">${s.warehouseName || 'Main Hub'}</td>
                <td class="py-3.5 px-6">${getStatusBadgeHtml(s.status)}</td>
                <td class="py-3.5 px-6 font-bold text-black">${s.estimatedDeliveryDate ? new Date(s.estimatedDeliveryDate).toLocaleDateString() : 'N/A'}</td>
            `;
            tr.addEventListener('click', () => {
                const sId = s.shipmentId || s.id;
                if (sId) fetchAndDisplayShipmentDetails(sId);
            });
            tbody.appendChild(tr);
        });
    }

    // Orders Controller
    let orderQueryState = {
        search: '',
        status: '',
        sortBy: 'createdAt',
        direction: 'DESC',
        page: 0,
        size: 20
    };

    let searchDebounceTimer = null;

    async function fetchAdminOrders() {
        const tbody = document.getElementById('ordersTableBody');
        if (!tbody) return;

        tbody.innerHTML = `<tr><td colspan="8" class="py-8 text-center text-zinc-500"><i class="fa-solid fa-spinner spinner mr-2"></i>Loading order records...</td></tr>`;

        try {
            const queryParams = new URLSearchParams({
                sortBy: orderQueryState.sortBy,
                direction: orderQueryState.direction,
                page: orderQueryState.page,
                size: orderQueryState.size
            });

            if (orderQueryState.search.trim()) queryParams.append('search', orderQueryState.search.trim());
            if (orderQueryState.status) queryParams.append('status', orderQueryState.status);

            const response = await authenticatedFetch(`${API_BASE_URL}/admin/orders?${queryParams.toString()}`, {
                method: 'GET'
            });

            if (!response.ok) throw new Error(`Failed to load orders (${response.status})`);

            const data = await response.json();

            renderOrdersTable(data.content || []);
            updateOrderPaginationControls(data);

        } catch (err) {
            tbody.innerHTML = `<tr><td colspan="8" class="py-6 text-center text-red-600">Failed to load orders. ${err.message}</td></tr>`;
        }
    }

    function renderOrdersTable(orders) {
        const tbody = document.getElementById('ordersTableBody');
        if (!tbody) return;
        tbody.innerHTML = '';

        if (orders.length === 0) {
            tbody.innerHTML = `<tr><td colspan="8" class="py-8 text-center text-zinc-400">No matching orders found.</td></tr>`;
            return;
        }

        orders.forEach(order => {
            const tr = document.createElement('tr');
            tr.className = 'hover:bg-zinc-50/80 transition-colors cursor-pointer';

            const formattedDate = order.createdAt ? new Date(order.createdAt).toLocaleString() : 'N/A';
            const formattedAmount = order.totalAmount != null ? `$${parseFloat(order.totalAmount).toFixed(2)}` : '$0.00';
            const rawStatus = (order.status || order.orderStatus || '').toUpperCase();
            const isCancelDisabled = rawStatus === 'CANCELLED' || rawStatus === 'SHIPPED' || rawStatus === 'DELIVERED';

            tr.innerHTML = `
                <td class="py-3.5 px-5 font-bold text-black btn-order-details">${order.orderNumber || 'N/A'}</td>
                <td class="py-3.5 px-5 btn-order-details">
                    <div class="font-bold text-black">${order.customerName || 'N/A'}</div>
                    <div class="text-[11px] text-zinc-500">${order.customerEmail || ''}</div>
                </td>
                <td class="py-3.5 px-5 btn-order-details">${getStatusBadgeHtml(rawStatus)}</td>
                <td class="py-3.5 px-5 text-center font-bold text-zinc-700 btn-order-details">${order.itemCount || 0}</td>
                <td class="py-3.5 px-5 text-center font-bold text-zinc-700 btn-order-details">${order.shipmentCount || 0}</td>
                <td class="py-3.5 px-5 text-right font-extrabold text-black btn-order-details">${formattedAmount}</td>
                <td class="py-3.5 px-5 text-right text-zinc-500 text-xs btn-order-details">${formattedDate}</td>
                <td class="py-3.5 px-5 text-center action-cells">
                    <div class="flex items-center justify-center gap-1.5">
                        <button class="btn-view-order p-1.5 rounded-lg bg-zinc-100 hover:bg-zinc-200 text-zinc-700 border border-zinc-200 transition-colors" title="View Order Details">
                            <i class="fa-solid fa-eye text-xs pointer-events-none"></i>
                        </button>
                        <button class="btn-cancel-order p-1.5 rounded-lg border transition-colors ${
                isCancelDisabled
                    ? 'bg-zinc-50 text-zinc-300 border-zinc-200 cursor-not-allowed'
                    : 'bg-red-50 hover:bg-red-100 text-red-600 border-red-200'
            }" title="${isCancelDisabled ? 'Cannot cancel in current state' : 'Cancel Order'}" ${isCancelDisabled ? 'disabled' : ''}>
                            <i class="fa-solid fa-ban text-xs pointer-events-none"></i>
                        </button>
                    </div>
                </td>
            `;

            const orderId = order.orderId || order.id;
            tr.addEventListener('click', (e) => {
                if (e.target.closest('.action-cells')) return;
                if (orderId) fetchAndDisplayOrderDetails(orderId);
            });

            const viewBtn = tr.querySelector('.btn-view-order');
            if (viewBtn) {
                viewBtn.addEventListener('click', (e) => {
                    e.stopPropagation();
                    if (orderId) fetchAndDisplayOrderDetails(orderId);
                });
            }

            const cancelBtn = tr.querySelector('.btn-cancel-order');
            if (cancelBtn && !isCancelDisabled) {
                cancelBtn.addEventListener('click', (e) => {
                    e.stopPropagation();
                    openCancelOrderModal(order);
                });
            }

            tbody.appendChild(tr);
        });
    }

    function updateOrderPaginationControls(pageData) {
        const totalElements = pageData.totalElements || 0;
        const totalPages = pageData.totalPages || 0;
        const currentPage = pageData.page || 0;
        const pageSize = pageData.size || 20;

        const ordersTotalCount = document.getElementById('ordersTotalCount');
        const ordersPageInfo = document.getElementById('ordersPageInfo');
        const ordersPaginationText = document.getElementById('ordersPaginationText');
        const prevBtn = document.getElementById('prevPageBtn');
        const nextBtn = document.getElementById('nextPageBtn');

        if (ordersTotalCount) ordersTotalCount.textContent = totalElements.toLocaleString();
        if (ordersPageInfo) ordersPageInfo.textContent = `${totalPages > 0 ? currentPage + 1 : 0} / ${totalPages}`;

        const startItem = totalElements === 0 ? 0 : currentPage * pageSize + 1;
        const endItem = Math.min((currentPage + 1) * pageSize, totalElements);
        if (ordersPaginationText) ordersPaginationText.textContent = `Showing ${startItem} to ${endItem} of ${totalElements} entries`;

        if (prevBtn) prevBtn.disabled = currentPage <= 0;
        if (nextBtn) nextBtn.disabled = currentPage >= totalPages - 1 || totalPages === 0;
    }

    function initOrdersTableEvents() {
        const searchInput = document.getElementById('orderSearchInput');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                clearTimeout(searchDebounceTimer);
                searchDebounceTimer = setTimeout(() => {
                    orderQueryState.search = e.target.value;
                    orderQueryState.page = 0;
                    fetchAdminOrders();
                }, 300);
            });
        }

        const statusFilter = document.getElementById('orderStatusFilter');
        if (statusFilter) {
            statusFilter.addEventListener('change', (e) => {
                orderQueryState.status = e.target.value;
                orderQueryState.page = 0;
                fetchAdminOrders();
            });
        }

        const pageSizeSelect = document.getElementById('orderPageSize');
        if (pageSizeSelect) {
            pageSizeSelect.addEventListener('change', (e) => {
                orderQueryState.size = parseInt(e.target.value, 10);
                orderQueryState.page = 0;
                fetchAdminOrders();
            });
        }

        const sortableHeaders = document.querySelectorAll('.sortable-header');
        sortableHeaders.forEach(header => {
            header.addEventListener('click', () => {
                const field = header.getAttribute('data-sort');
                if (orderQueryState.sortBy === field) {
                    orderQueryState.direction = orderQueryState.direction === 'ASC' ? 'DESC' : 'ASC';
                } else {
                    orderQueryState.sortBy = field;
                    orderQueryState.direction = 'ASC';
                }

                sortableHeaders.forEach(h => {
                    const icon = h.querySelector('i');
                    if (icon) icon.className = 'fa-solid fa-sort text-zinc-400 ml-1';
                });
                const currentIcon = header.querySelector('i');
                if (currentIcon) {
                    currentIcon.className = orderQueryState.direction === 'ASC'
                        ? 'fa-solid fa-sort-up text-black ml-1'
                        : 'fa-solid fa-sort-down text-black ml-1';
                }

                fetchAdminOrders();
            });
        });

        const prevBtn = document.getElementById('prevPageBtn');
        const nextBtn = document.getElementById('nextPageBtn');

        if (prevBtn) {
            prevBtn.addEventListener('click', () => {
                if (orderQueryState.page > 0) {
                    orderQueryState.page--;
                    fetchAdminOrders();
                }
            });
        }

        if (nextBtn) {
            nextBtn.addEventListener('click', () => {
                orderQueryState.page++;
                fetchAdminOrders();
            });
        }
    }

    // Shipments Controller
    let shipmentQueryState = {
        search: '',
        status: '',
        warehouseId: null,
        sortBy: 'shippedAt',
        direction: 'DESC',
        page: 0,
        size: 20
    };

    let shipmentSearchDebounceTimer = null;

    async function fetchAdminShipments() {
        const tbody = document.getElementById('shipmentsTableBody');
        if (!tbody) return;

        tbody.innerHTML = `<tr><td colspan="8" class="py-8 text-center text-zinc-500"><i class="fa-solid fa-spinner spinner mr-2"></i>Loading shipment records...</td></tr>`;

        try {
            const queryParams = new URLSearchParams({
                sortBy: shipmentQueryState.sortBy,
                direction: shipmentQueryState.direction,
                page: shipmentQueryState.page,
                size: shipmentQueryState.size
            });

            if (shipmentQueryState.search.trim()) queryParams.append('search', shipmentQueryState.search.trim());
            if (shipmentQueryState.status) queryParams.append('status', shipmentQueryState.status);
            if (shipmentQueryState.warehouseId) queryParams.append('warehouseId', shipmentQueryState.warehouseId);

            const response = await authenticatedFetch(`${API_BASE_URL}/admin/shipments?${queryParams.toString()}`, {
                method: 'GET'
            });

            if (!response.ok) throw new Error(`Failed to load shipments (${response.status})`);

            const data = await response.json();

            renderShipmentsTable(data.content || []);
            updateShipmentPaginationControls(data);

        } catch (err) {
            tbody.innerHTML = `<tr><td colspan="8" class="py-6 text-center text-red-600">Failed to load shipments. ${err.message}</td></tr>`;
        }
    }

    function renderShipmentsTable(shipments) {
        const tbody = document.getElementById('shipmentsTableBody');
        if (!tbody) return;
        tbody.innerHTML = '';

        if (shipments.length === 0) {
            tbody.innerHTML = `<tr><td colspan="8" class="py-8 text-center text-zinc-400">No matching shipment records found.</td></tr>`;
            return;
        }

        shipments.forEach(s => {
            const tr = document.createElement('tr');
            tr.className = 'hover:bg-zinc-50/80 transition-colors';

            const shippedAtStr = s.shippedAt ? new Date(s.shippedAt).toLocaleString() : 'N/A';
            const estDeliveryStr = s.estimatedDeliveryDate ? new Date(s.estimatedDeliveryDate).toLocaleDateString() : 'N/A';
            const deliveredAtStr = s.deliveredAt ? new Date(s.deliveredAt).toLocaleString() : 'N/A';
            const rawStatus = (s.status || '').toUpperCase();

            const isPending = rawStatus === 'PENDING';
            const isTerminal = rawStatus === 'DELIVERED' || rawStatus === 'CANCELLED';

            let actionButtonsHtml = '';

            if (isPending) {
                actionButtonsHtml = `
                    <button class="btn-ship-shipment p-1.5 rounded-lg bg-blue-50 hover:bg-blue-100 text-blue-600 border border-blue-200 transition-colors" title="Dispatch / Ship Freight">
                        <i class="fa-solid fa-paper-plane text-xs pointer-events-none"></i>
                    </button>
                `;
            } else if (!isTerminal) {
                actionButtonsHtml = `
                    <button class="btn-update-tracking p-1.5 rounded-lg bg-zinc-900 hover:bg-zinc-800 text-white border border-zinc-900 transition-colors" title="Update Logistics Tracking Checkpoint">
                        <i class="fa-solid fa-route text-xs pointer-events-none"></i>
                    </button>
                `;
            } else {
                actionButtonsHtml = `
                    <button class="p-1.5 rounded-lg bg-zinc-50 text-zinc-300 border border-zinc-200 cursor-not-allowed" disabled title="No active status actions available">
                        <i class="fa-solid fa-lock text-xs pointer-events-none"></i>
                    </button>
                `;
            }

            tr.innerHTML = `
                <td class="py-3.5 px-5 font-bold text-black cursor-pointer btn-shipment-details">${s.shipmentNumber || 'N/A'}</td>
                <td class="py-3.5 px-5 font-bold text-zinc-700 cursor-pointer btn-shipment-details">${s.orderNumber || 'N/A'}</td>
                <td class="py-3.5 px-5 text-zinc-700 cursor-pointer btn-shipment-details">${s.warehouseName || 'Main Hub'}</td>
                <td class="py-3.5 px-5 cursor-pointer btn-shipment-details">${getStatusBadgeHtml(s.status)}</td>
                <td class="py-3.5 px-5 text-zinc-500 text-xs cursor-pointer btn-shipment-details">${shippedAtStr}</td>
                <td class="py-3.5 px-5 text-zinc-700 text-xs font-semibold cursor-pointer btn-shipment-details">${estDeliveryStr}</td>
                <td class="py-3.5 px-5 text-right text-zinc-500 text-xs cursor-pointer btn-shipment-details">${deliveredAtStr}</td>
                <td class="py-3.5 px-5 text-center action-cells">
                    <div class="flex items-center justify-center gap-1.5">
                        <button class="btn-view-shipment p-1.5 rounded-lg bg-zinc-100 hover:bg-zinc-200 text-zinc-700 border border-zinc-200 transition-colors" title="Inspect Shipment Telemetry">
                            <i class="fa-solid fa-eye text-xs pointer-events-none"></i>
                        </button>
                        ${actionButtonsHtml}
                    </div>
                </td>
            `;

            const sId = s.shipmentId || s.id;
            tr.addEventListener('click', (e) => {
                if (e.target.closest('.action-cells')) return;
                if (sId) fetchAndDisplayShipmentDetails(sId);
            });

            const viewBtn = tr.querySelector('.btn-view-shipment');
            if (viewBtn) {
                viewBtn.addEventListener('click', (e) => {
                    e.stopPropagation();
                    if (sId) fetchAndDisplayShipmentDetails(sId);
                });
            }

            const shipBtn = tr.querySelector('.btn-ship-shipment');
            if (shipBtn) {
                shipBtn.addEventListener('click', (e) => {
                    e.stopPropagation();
                    openShipConfirmModal(s);
                });
            }

            const trackingBtn = tr.querySelector('.btn-update-tracking');
            if (trackingBtn) {
                trackingBtn.addEventListener('click', (e) => {
                    e.stopPropagation();
                    openUpdateTrackingModal(s);
                });
            }

            tbody.appendChild(tr);
        });
    }

    function updateShipmentPaginationControls(pageData) {
        const totalElements = pageData.totalElements || 0;
        const totalPages = pageData.totalPages || 0;
        const currentPage = pageData.page || 0;
        const pageSize = pageData.size || 20;

        const countElem = document.getElementById('shipmentsTotalCount');
        const infoElem = document.getElementById('shipmentsPageInfo');
        const textElem = document.getElementById('shipmentsPaginationText');
        const prevBtn = document.getElementById('shipmentPrevPageBtn');
        const nextBtn = document.getElementById('shipmentNextPageBtn');

        if (countElem) countElem.textContent = totalElements.toLocaleString();
        if (infoElem) infoElem.textContent = `${totalPages > 0 ? currentPage + 1 : 0} / ${totalPages}`;

        const startItem = totalElements === 0 ? 0 : currentPage * pageSize + 1;
        const endItem = Math.min((currentPage + 1) * pageSize, totalElements);
        if (textElem) textElem.textContent = `Showing ${startItem} to ${endItem} of ${totalElements} entries`;

        if (prevBtn) prevBtn.disabled = currentPage <= 0;
        if (nextBtn) nextBtn.disabled = currentPage >= totalPages - 1 || totalPages === 0;
    }

    function initShipmentsTableEvents() {
        const searchInput = document.getElementById('shipmentSearchInput');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                clearTimeout(shipmentSearchDebounceTimer);
                shipmentSearchDebounceTimer = setTimeout(() => {
                    shipmentQueryState.search = e.target.value;
                    shipmentQueryState.page = 0;
                    fetchAdminShipments();
                }, 300);
            });
        }

        const statusFilter = document.getElementById('shipmentStatusFilter');
        if (statusFilter) {
            statusFilter.addEventListener('change', (e) => {
                shipmentQueryState.status = e.target.value;
                shipmentQueryState.page = 0;
                fetchAdminShipments();
            });
        }

        const pageSizeSelect = document.getElementById('shipmentPageSize');
        if (pageSizeSelect) {
            pageSizeSelect.addEventListener('change', (e) => {
                shipmentQueryState.size = parseInt(e.target.value, 10);
                shipmentQueryState.page = 0;
                fetchAdminShipments();
            });
        }

        const sortableHeaders = document.querySelectorAll('.shipment-sortable-header');
        sortableHeaders.forEach(header => {
            header.addEventListener('click', () => {
                const field = header.getAttribute('data-sort');
                if (shipmentQueryState.sortBy === field) {
                    shipmentQueryState.direction = shipmentQueryState.direction === 'ASC' ? 'DESC' : 'ASC';
                } else {
                    shipmentQueryState.sortBy = field;
                    shipmentQueryState.direction = 'ASC';
                }

                sortableHeaders.forEach(h => {
                    const icon = h.querySelector('i');
                    if (icon) icon.className = 'fa-solid fa-sort text-zinc-400 ml-1';
                });
                const currentIcon = header.querySelector('i');
                if (currentIcon) {
                    currentIcon.className = shipmentQueryState.direction === 'ASC'
                        ? 'fa-solid fa-sort-up text-black ml-1'
                        : 'fa-solid fa-sort-down text-black ml-1';
                }

                fetchAdminShipments();
            });
        });

        const prevBtn = document.getElementById('shipmentPrevPageBtn');
        const nextBtn = document.getElementById('shipmentNextPageBtn');

        if (prevBtn) {
            prevBtn.addEventListener('click', () => {
                if (shipmentQueryState.page > 0) {
                    shipmentQueryState.page--;
                    fetchAdminShipments();
                }
            });
        }

        if (nextBtn) {
            nextBtn.addEventListener('click', () => {
                shipmentQueryState.page++;
                fetchAdminShipments();
            });
        }
    }

    // Inventory Controller
    let inventoryQueryState = {
        search: '',
        warehouseId: '',
        productId: '',
        lowStock: null,
        outOfStock: null,
        sortBy: 'inventoryNumber',
        direction: 'ASC',
        page: 0,
        size: 20
    };

    let inventorySearchDebounceTimer = null;

    async function loadInventoryDropdownOptions() {
        try {
            const whResponse = await authenticatedFetch(`${API_BASE_URL}/data/warehouses`);
            if (whResponse.ok) {
                const warehouses = await whResponse.json();
                const whSelect = document.getElementById('inventoryWarehouseFilter');
                if (whSelect) {
                    whSelect.innerHTML = `<option value="">All Warehouses</option>`;
                    warehouses.forEach(w => {
                        whSelect.innerHTML += `<option value="${w.id}">${w.name}</option>`;
                    });
                }
            }

            const prodResponse = await authenticatedFetch(`${API_BASE_URL}/data/products`);
            if (prodResponse.ok) {
                const products = await prodResponse.json();
                const prodSelect = document.getElementById('inventoryProductFilter');
                if (prodSelect) {
                    prodSelect.innerHTML = `<option value="">All Products</option>`;
                    products.forEach(p => {
                        prodSelect.innerHTML += `<option value="${p.id}">${p.name || p.title}</option>`;
                    });
                }
            }
        } catch (err) {
            console.error('Failed to load filter dropdown options:', err);
        }
    }

    async function fetchAdminInventory() {
        const tbody = document.getElementById('inventoryTableBody');
        if (!tbody) return;

        tbody.innerHTML = `<tr><td colspan="9" class="py-8 text-center text-zinc-500"><i class="fa-solid fa-spinner spinner mr-2"></i>Loading inventory records...</td></tr>`;

        try {
            const queryParams = new URLSearchParams({
                sortBy: inventoryQueryState.sortBy,
                direction: inventoryQueryState.direction,
                page: inventoryQueryState.page,
                size: inventoryQueryState.size
            });

            if (inventoryQueryState.search.trim()) queryParams.append('search', inventoryQueryState.search.trim());
            if (inventoryQueryState.warehouseId) queryParams.append('warehouseId', inventoryQueryState.warehouseId);
            if (inventoryQueryState.productId) queryParams.append('productId', inventoryQueryState.productId);
            if (inventoryQueryState.lowStock !== null) queryParams.append('lowStock', inventoryQueryState.lowStock);
            if (inventoryQueryState.outOfStock !== null) queryParams.append('outOfStock', inventoryQueryState.outOfStock);

            const response = await authenticatedFetch(`${API_BASE_URL}/admin/inventory?${queryParams.toString()}`, {
                method: 'GET'
            });

            if (!response.ok) throw new Error(`Failed to load inventory (${response.status})`);

            const data = await response.json();

            renderInventoryTable(data.content || []);
            updateInventoryPaginationControls(data);

        } catch (err) {
            tbody.innerHTML = `<tr><td colspan="9" class="py-6 text-center text-red-600">Failed to load inventory. ${err.message}</td></tr>`;
        }
    }

    function renderInventoryTable(items) {
        const tbody = document.getElementById('inventoryTableBody');
        if (!tbody) return;
        tbody.innerHTML = '';

        if (items.length === 0) {
            tbody.innerHTML = `<tr><td colspan="9" class="py-8 text-center text-zinc-400">No matching inventory records found.</td></tr>`;
            return;
        }

        items.forEach(item => {
            const tr = document.createElement('tr');
            tr.className = 'hover:bg-zinc-50/80 cursor-pointer transition-colors';

            const invId = item.id || item.inventoryId;
            const productName = item.productName || (item.product ? item.product.title : 'N/A');
            const warehouseName = item.warehouseName || (item.warehouse ? item.warehouse.name : 'Main Hub');

            tr.innerHTML = `
                <td class="py-3.5 px-5 font-bold text-black">${item.inventoryNumber || 'N/A'}</td>
                <td class="py-3.5 px-5 font-bold text-zinc-800">${productName}</td>
                <td class="py-3.5 px-5 text-zinc-600">${warehouseName}</td>
                <td class="py-3.5 px-5">${getStatusBadgeHtml(item.status)}</td>
                <td class="py-3.5 px-5 text-right font-bold text-black">${item.quantity != null ? item.quantity : 0}</td>
                <td class="py-3.5 px-5 text-right font-bold text-amber-600">${item.reservedQuantity != null ? item.reservedQuantity : 0}</td>
                <td class="py-3.5 px-5 text-right font-extrabold text-emerald-600">${item.availableQuantity != null ? item.availableQuantity : 0}</td>
                <td class="py-3.5 px-5 text-center">${getStockBadge(item)}</td>
                <td class="py-3.5 px-5 text-center action-cells">
                    <div class="flex items-center justify-center gap-1.5">
                        <button class="btn-view-inventory p-1.5 rounded-lg bg-zinc-100 hover:bg-zinc-200 text-zinc-700 border border-zinc-200 transition-colors" title="View Inventory Details">
                            <i class="fa-solid fa-eye text-xs pointer-events-none"></i>
                        </button>
                    </div>
                </td>
            `;

            tr.addEventListener('click', (e) => {
                if (e.target.closest('.action-cells')) return;
                if (invId) fetchAndDisplayInventoryDetails(invId);
            });

            const viewBtn = tr.querySelector('.btn-view-inventory');
            if (viewBtn) {
                viewBtn.addEventListener('click', (e) => {
                    e.stopPropagation();
                    if (invId) fetchAndDisplayInventoryDetails(invId);
                });
            }

            tbody.appendChild(tr);
        });
    }

    function updateInventoryPaginationControls(pageData) {
        const totalElements = pageData.totalElements || 0;
        const totalPages = pageData.totalPages || 0;
        const currentPage = pageData.page || 0;
        const pageSize = pageData.size || 20;

        const countElem = document.getElementById('inventoryTotalCount');
        const infoElem = document.getElementById('inventoryPageInfo');
        const textElem = document.getElementById('inventoryPaginationText');
        const prevBtn = document.getElementById('inventoryPrevPageBtn');
        const nextBtn = document.getElementById('inventoryNextPageBtn');

        if (countElem) countElem.textContent = totalElements.toLocaleString();
        if (infoElem) infoElem.textContent = `${totalPages > 0 ? currentPage + 1 : 0} / ${totalPages}`;

        const startItem = totalElements === 0 ? 0 : currentPage * pageSize + 1;
        const endItem = Math.min((currentPage + 1) * pageSize, totalElements);
        if (textElem) textElem.textContent = `Showing ${startItem} to ${endItem} of ${totalElements} entries`;

        if (prevBtn) prevBtn.disabled = currentPage <= 0;
        if (nextBtn) nextBtn.disabled = currentPage >= totalPages - 1 || totalPages === 0;
    }

    function initInventoryTableEvents() {
        const searchInput = document.getElementById('inventorySearchInput');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                clearTimeout(inventorySearchDebounceTimer);
                inventorySearchDebounceTimer = setTimeout(() => {
                    inventoryQueryState.search = e.target.value;
                    inventoryQueryState.page = 0;
                    fetchAdminInventory();
                }, 300);
            });
        }

        const warehouseFilter = document.getElementById('inventoryWarehouseFilter');
        if (warehouseFilter) {
            warehouseFilter.addEventListener('change', (e) => {
                inventoryQueryState.warehouseId = e.target.value;
                inventoryQueryState.page = 0;
                fetchAdminInventory();
            });
        }

        const productFilter = document.getElementById('inventoryProductFilter');
        if (productFilter) {
            productFilter.addEventListener('change', (e) => {
                inventoryQueryState.productId = e.target.value;
                inventoryQueryState.page = 0;
                fetchAdminInventory();
            });
        }

        const stockAlertFilter = document.getElementById('inventoryStockAlertFilter');
        if (stockAlertFilter) {
            stockAlertFilter.addEventListener('change', (e) => {
                const val = e.target.value;
                if (val === 'low') {
                    inventoryQueryState.lowStock = true;
                    inventoryQueryState.outOfStock = null;
                } else if (val === 'out') {
                    inventoryQueryState.lowStock = null;
                    inventoryQueryState.outOfStock = true;
                } else {
                    inventoryQueryState.lowStock = null;
                    inventoryQueryState.outOfStock = null;
                }
                inventoryQueryState.page = 0;
                fetchAdminInventory();
            });
        }

        const pageSizeSelect = document.getElementById('inventoryPageSize');
        if (pageSizeSelect) {
            pageSizeSelect.addEventListener('change', (e) => {
                inventoryQueryState.size = parseInt(e.target.value, 10);
                inventoryQueryState.page = 0;
                fetchAdminInventory();
            });
        }

        const sortableHeaders = document.querySelectorAll('.inventory-sortable-header');
        sortableHeaders.forEach(header => {
            header.addEventListener('click', () => {
                const field = header.getAttribute('data-sort');
                if (inventoryQueryState.sortBy === field) {
                    inventoryQueryState.direction = inventoryQueryState.direction === 'ASC' ? 'DESC' : 'ASC';
                } else {
                    inventoryQueryState.sortBy = field;
                    inventoryQueryState.direction = 'ASC';
                }

                sortableHeaders.forEach(h => {
                    const icon = h.querySelector('i');
                    if (icon) icon.className = 'fa-solid fa-sort text-zinc-400 ml-1';
                });
                const currentIcon = header.querySelector('i');
                if (currentIcon) {
                    currentIcon.className = inventoryQueryState.direction === 'ASC'
                        ? 'fa-solid fa-sort-up text-black ml-1'
                        : 'fa-solid fa-sort-down text-black ml-1';
                }

                fetchAdminInventory();
            });
        });

        const prevBtn = document.getElementById('inventoryPrevPageBtn');
        const nextBtn = document.getElementById('inventoryNextPageBtn');

        if (prevBtn) {
            prevBtn.addEventListener('click', () => {
                if (inventoryQueryState.page > 0) {
                    inventoryQueryState.page--;
                    fetchAdminInventory();
                }
            });
        }

        if (nextBtn) {
            nextBtn.addEventListener('click', () => {
                inventoryQueryState.page++;
                fetchAdminInventory();
            });
        }
    }

    // Section Switching Navigation
    function switchSection(targetId) {
        sectionPanes.forEach(pane => pane.classList.add('hidden'));

        sidebarButtons.forEach(btn => {
            if (btn.getAttribute('data-target') === targetId) {
                btn.classList.add('bg-zinc-900', 'text-white', 'border', 'border-zinc-800');
                btn.classList.remove('text-zinc-400');
            } else {
                btn.classList.remove('bg-zinc-900', 'text-white', 'border', 'border-zinc-800');
                btn.classList.add('text-zinc-400');
            }
        });

        const selectedPane = document.getElementById(targetId);
        if (selectedPane) selectedPane.classList.remove('hidden');

        if (targetId === 'secOrders') {
            fetchAdminOrders();
        } else if (targetId === 'secShipments') {
            fetchAdminShipments();
        } else if (targetId === 'secInventory') {
            fetchAdminInventory();
        } else if (targetId === 'secGRN') {
            fetchAdminGRNs();
        } else if (targetId === 'secVendors') {
            fetchAdminVendors();
        } else if (targetId === 'secCustomers') {
            fetchAdminCustomers();
        } else if (targetId === 'secEmployees') {
            fetchAdminEmployees();
        }

        switch (targetId) {
            case 'secDashboard':
                if (currentPageTitle) currentPageTitle.textContent = 'Executive Control Center';
                if (currentPageSubtitle) currentPageSubtitle.textContent = 'Global system metrics, orders & financial records';
                break;
            case 'secOrders':
                if (currentPageTitle) currentPageTitle.textContent = 'Order Management System';
                if (currentPageSubtitle) currentPageSubtitle.textContent = 'Filter, sort, and inspect customer transactions';
                break;
            case 'secShipments':
                if (currentPageTitle) currentPageTitle.textContent = 'Logistics & Shipments';
                if (currentPageSubtitle) currentPageSubtitle.textContent = 'Active freight tracking and route dispatch control';
                break;
            case 'secInventory':
                if (currentPageTitle) currentPageTitle.textContent = 'Warehouse Stock & Inventory';
                if (currentPageSubtitle) currentPageSubtitle.textContent = 'Stock replenishment and reorder points';
                break;
            case 'secGRN':
                if (currentPageTitle) currentPageTitle.textContent = 'Goods Receive Notes (GRN)';
                if (currentPageSubtitle) currentPageSubtitle.textContent = 'Inbound inventory arrivals and vendor supply manifests';
                break;
            case 'secProducts':
                if (currentPageTitle) currentPageTitle.textContent = 'Product Catalog';
                if (currentPageSubtitle) currentPageSubtitle.textContent = 'Product catalog entries and updates';
                break;
            case 'secCustomers':
                if (currentPageTitle) currentPageTitle.textContent = 'Customer Directory';
                if (currentPageSubtitle) currentPageSubtitle.textContent = 'Registered customer accounts, company ties, and lifecycle metrics';
                break;
            case 'secVendors':
                if (currentPageTitle) currentPageTitle.textContent = 'Vendor Network & Supplier Operations';
                if (currentPageSubtitle) currentPageSubtitle.textContent = 'Supplier directory, performance ratings, and status lifecycles';
                break;
            case 'secEmployees':
                if (currentPageTitle) currentPageTitle.textContent = 'Employee Management';
                if (currentPageSubtitle) currentPageSubtitle.textContent = 'Staff catalog, departments & system role permissions';
                break;
        }
    }

    sidebarButtons.forEach(btn => {
        btn.addEventListener('click', () => switchSection(btn.getAttribute('data-target')));
    });

    if (refreshDataBtn) refreshDataBtn.addEventListener('click', fetchAdminDashboard);

    if (signOutBtn) {
        signOutBtn.addEventListener('click', () => {
            clearSession();
        });
    }

    function showLoader(visible) {
        if (!dashLoader) return;
        dashLoader.classList.toggle('hidden', !visible);
    }

    function showAlert(message, type = 'error') {
        if (!dashAlert || !dashAlertMessage || !dashAlertIcon) return;
        dashAlertMessage.textContent = message;
        dashAlert.className = 'p-4 rounded-xl text-xs border flex items-center gap-3 shadow-sm bg-black text-white border-zinc-900';
        dashAlertIcon.className = type === 'error' ? 'fa-solid fa-triangle-exclamation text-white' : 'fa-solid fa-circle-info text-white';
        dashAlert.classList.remove('hidden');
    }

    function clearAlert() {
        if (dashAlert) dashAlert.classList.add('hidden');
    }

    // Initialize View Controls & Data Requests
    initOrdersTableEvents();
    initShipmentsTableEvents();
    initInventoryTableEvents();
    initGrnTableEvents();
    initVendorTableEvents();
    initCustomerTableEvents();
    initEmployeeTableEvents();
    loadInventoryDropdownOptions();
    loadGrnDropdownOptions();
    loadVendorDropdownOptions();
    loadCustomerDropdownOptions();
    fetchAdminDashboard();
    switchSection('secDashboard');
});