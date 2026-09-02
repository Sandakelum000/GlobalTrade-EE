document.addEventListener('DOMContentLoaded', () => {

    const getContextPath = () => {
        const path = window.location.pathname;
        const secondSlash = path.indexOf('/', 1);
        return (secondSlash !== -1 ? path.substring(0, secondSlash) : '') + '/logistics/api';
    };

    const API_BASE_URL = getContextPath();

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
            window.location.href = 'forbidden.html';
            return response;
        }

        return response;
    }

    // Helper to display ValidationErrorResponse fields
    function showValidationErrorAlert(errorData, alertElem, msgElem, detailElem, fieldElem = null, statusElem = null) {
        if (!alertElem) return;

        let message = 'Validation Error';
        let detail = 'State transition rejected by validation rules.';
        let field = '';
        let status = '';

        if (Array.isArray(errorData)) {
            message = 'Multiple Validation Errors';
            detail = errorData.map(err => {
                const f = err.field ? `[${err.field}]: ` : '';
                return `${f}${err.message || err.detail || 'Invalid input'}`;
            }).join(' | ');
            status = errorData[0]?.status || '';
        } else if (errorData && Array.isArray(errorData.errors)) {
            message = errorData.message || 'Validation Failure';
            detail = errorData.errors.map(err => {
                const f = err.field ? `[${err.field}]: ` : '';
                return `${f}${err.message || err.detail || 'Invalid input'}`;
            }).join(' | ');
            status = errorData.status || '';
        } else if (errorData && typeof errorData === 'object') {
            message = errorData.message || 'Validation Error';
            detail = errorData.detail || errorData.message || 'State transition rejected by validation rules.';
            field = errorData.field || '';
            status = errorData.status || '';
        } else if (typeof errorData === 'string') {
            detail = errorData;
        }

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

    // Header & User UI Elements
    const usernameDisplay = document.getElementById('usernameDisplay');
    const userAvatar = document.getElementById('userAvatar');
    const roleBadge = document.getElementById('roleBadge');
    const signOutBtn = document.getElementById('signOutBtn');
    const refreshDataBtn = document.getElementById('refreshDataBtn');
    const dashAlert = document.getElementById('dashAlert');
    const dashAlertIcon = document.getElementById('dashAlertIcon');
    const dashAlertMessage = document.getElementById('dashAlertMessage');

    // Modals
    const shipmentDetailsModal = document.getElementById('shipmentDetailsModal');
    const closeShipmentModalBtn = document.getElementById('closeShipmentModalBtn');
    const modalShipmentCloseFooterBtn = document.getElementById('modalShipmentCloseFooterBtn');
    const modalShipmentLoader = document.getElementById('modalShipmentLoader');
    const modalShipmentContent = document.getElementById('modalShipmentContent');

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

    let shipmentToShipId = null;
    let shipmentToUpdateTrackingId = null;

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
            case 'INACTIVE':
                return `<span class="px-2.5 py-1 bg-red-100 text-red-800 border border-red-300 rounded text-xs font-semibold inline-flex items-center gap-1.5"><i class="fa-solid fa-circle-xmark text-[8px] text-red-600"></i> ${displayLabel}</span>`;
            case 'PENDING':
            case 'LOW_STOCK':
            case 'QUARANTINED':
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

    // Session Guard
    function checkManagerSession() {
        const token = localStorage.getItem('access_token');
        const username = localStorage.getItem('username');

        if (!token) {
            window.location.href = 'signin.html';
            return false;
        }

        if (username) {
            if (usernameDisplay) usernameDisplay.textContent = username;
            if (userAvatar) userAvatar.textContent = username.charAt(0).toUpperCase();
        }

        if (roleBadge) roleBadge.textContent = 'Manager';
        return true;
    }

    if (!checkManagerSession()) return;

    // --- SHIPMENT MODAL CONTROLLERS ---
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

    // --- SHIP SHIPMENT MODAL ---
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
                    fetchManagerShipments();
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

    // --- UPDATE TRACKING MODAL ---
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
                    showAlert("Pre-dispatch tracking log recorded successfully.", "info");
                    fetchManagerShipments();
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

    // --- SHIPMENTS QUERY & DATA FETCH ENGINE ---
    let shipmentQueryState = {
        search: '',
        status: '',
        warehouseId: '',
        sortBy: 'shippedAt',
        direction: 'DESC',
        page: 0,
        size: 20
    };

    let shipmentSearchDebounceTimer = null;

    async function loadWarehouseDropdownOptions() {
        try {
            const whResponse = await authenticatedFetch(`${API_BASE_URL}/data/warehouses`);
            if (whResponse.ok) {
                const warehouses = await whResponse.json();
                const whSelect = document.getElementById('shipmentWarehouseFilter');
                if (whSelect) {
                    whSelect.innerHTML = `<option value="">All Warehouses</option>`;
                    warehouses.forEach(w => {
                        whSelect.innerHTML += `<option value="${w.id}">${w.name}</option>`;
                    });
                }
            }
        } catch (err) {
            console.error('Failed to load warehouse dropdown options:', err);
        }
    }

    async function fetchManagerShipments() {
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

            const isPendingOrProcessing = rawStatus === 'PENDING' || rawStatus === 'PROCESSING';
            const isDispatchedOrTerminal = rawStatus === 'SHIPPED' || rawStatus === 'IN_TRANSIT' || rawStatus === 'OUT_FOR_DELIVERY' || rawStatus === 'DELIVERED' || rawStatus === 'CANCELLED';

            let actionButtonsHtml = '';

            // Managers can log updates prior to dispatch
            if (isPendingOrProcessing) {
                actionButtonsHtml = `
                    <button class="btn-update-tracking p-1.5 rounded-lg bg-zinc-900 hover:bg-zinc-800 text-white border border-zinc-900 transition-colors" title="Update Pre-Dispatch Checkpoint">
                        <i class="fa-solid fa-route text-xs pointer-events-none"></i>
                    </button>
                    <button class="btn-ship-shipment p-1.5 rounded-lg bg-blue-50 hover:bg-blue-100 text-blue-600 border border-blue-200 transition-colors" title="Dispatch / Ship Freight">
                        <i class="fa-solid fa-paper-plane text-xs pointer-events-none"></i>
                    </button>
                `;
            } else if (isDispatchedOrTerminal) {
                actionButtonsHtml = `
                    <button class="p-1.5 rounded-lg bg-zinc-50 text-zinc-300 border border-zinc-200 cursor-not-allowed" disabled title="Shipment dispatched. Pre-dispatch updates locked.">
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
                    fetchManagerShipments();
                }, 300);
            });
        }

        const warehouseFilter = document.getElementById('shipmentWarehouseFilter');
        if (warehouseFilter) {
            warehouseFilter.addEventListener('change', (e) => {
                shipmentQueryState.warehouseId = e.target.value;
                shipmentQueryState.page = 0;
                fetchManagerShipments();
            });
        }

        const statusFilter = document.getElementById('shipmentStatusFilter');
        if (statusFilter) {
            statusFilter.addEventListener('change', (e) => {
                shipmentQueryState.status = e.target.value;
                shipmentQueryState.page = 0;
                fetchManagerShipments();
            });
        }

        const pageSizeSelect = document.getElementById('shipmentPageSize');
        if (pageSizeSelect) {
            pageSizeSelect.addEventListener('change', (e) => {
                shipmentQueryState.size = parseInt(e.target.value, 10);
                shipmentQueryState.page = 0;
                fetchManagerShipments();
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

                fetchManagerShipments();
            });
        });

        const prevBtn = document.getElementById('shipmentPrevPageBtn');
        const nextBtn = document.getElementById('shipmentNextPageBtn');

        if (prevBtn) {
            prevBtn.addEventListener('click', () => {
                if (shipmentQueryState.page > 0) {
                    shipmentQueryState.page--;
                    fetchManagerShipments();
                }
            });
        }

        if (nextBtn) {
            nextBtn.addEventListener('click', () => {
                shipmentQueryState.page++;
                fetchManagerShipments();
            });
        }
    }

    // Helper Alerts
    function showAlert(message, type = 'error') {
        if (!dashAlert || !dashAlertMessage || !dashAlertIcon) return;
        dashAlertMessage.textContent = message;
        dashAlert.className = 'p-4 rounded-xl text-xs border flex items-center gap-3 shadow-sm bg-black text-white border-zinc-900';
        dashAlertIcon.className = type === 'error' ? 'fa-solid fa-triangle-exclamation text-white' : 'fa-solid fa-circle-info text-white';
        dashAlert.classList.remove('hidden');
    }

    if (refreshDataBtn) refreshDataBtn.addEventListener('click', fetchManagerShipments);

    if (signOutBtn) {
        signOutBtn.addEventListener('click', () => {
            clearSession();
        });
    }

    // Initialization
    initShipmentsTableEvents();
    loadWarehouseDropdownOptions();
    fetchManagerShipments();
});