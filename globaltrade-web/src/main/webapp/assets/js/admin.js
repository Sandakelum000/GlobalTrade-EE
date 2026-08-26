document.addEventListener('DOMContentLoaded', () => {

    const getContextPath = () => {
        const path = window.location.pathname;
        const secondSlash = path.indexOf('/', 1);
        return (secondSlash !== -1 ? path.substring(0, secondSlash) : '') + '/logistics/api';
    };

    const API_BASE_URL = getContextPath();

    // Elements
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

    // Modal DOM References
    const orderDetailsModal = document.getElementById('orderDetailsModal');
    const closeOrderModalBtn = document.getElementById('closeOrderModalBtn');
    const modalOrderCloseFooterBtn = document.getElementById('modalOrderCloseFooterBtn');
    const modalOrderLoader = document.getElementById('modalOrderLoader');
    const modalOrderContent = document.getElementById('modalOrderContent');

    let revenueChartInstance = null;
    let orderStatusChartInstance = null;
    let monthlyOrdersChartInstance = null;
    let shipmentStatusChartInstance = null;

    // ==========================================
    // 1. Collapsible Sidebar Controller
    // ==========================================
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

    // Color Badging (Clear Sentence-Case Rendering)
    function getStatusBadgeHtml(statusStr) {
        const rawStatus = (statusStr || '').toUpperCase();
        let displayLabel = rawStatus.charAt(0) + rawStatus.slice(1).toLowerCase();

        switch (rawStatus) {
            case 'CONFIRMED':
            case 'DELIVERED':
                return `<span class="px-2.5 py-1 bg-emerald-100 text-emerald-800 border border-emerald-300 rounded text-xs font-semibold inline-flex items-center gap-1.5"><i class="fa-solid fa-circle-check text-[8px] text-emerald-600"></i> ${displayLabel}</span>`;
            case 'CANCELLED':
                return `<span class="px-2.5 py-1 bg-red-100 text-red-800 border border-red-300 rounded text-xs font-semibold inline-flex items-center gap-1.5"><i class="fa-solid fa-circle-xmark text-[8px] text-red-600"></i> ${displayLabel}</span>`;
            case 'PENDING':
                return `<span class="px-2.5 py-1 bg-amber-100 text-amber-800 border border-amber-300 rounded text-xs font-semibold inline-flex items-center gap-1.5"><i class="fa-solid fa-clock text-[8px] text-amber-600"></i> ${displayLabel}</span>`;
            case 'PROCESSING':
            case 'SHIPPED':
            case 'IN_TRANSIT':
                return `<span class="px-2.5 py-1 bg-blue-100 text-blue-800 border border-blue-300 rounded text-xs font-semibold inline-flex items-center gap-1.5"><i class="fa-solid fa-spinner spinner text-[8px] text-blue-600"></i> ${displayLabel}</span>`;
            default:
                return `<span class="px-2.5 py-1 bg-zinc-100 text-zinc-800 border border-zinc-300 rounded text-xs font-semibold inline-flex items-center gap-1.5">${displayLabel}</span>`;
        }
    }

    // ==========================================
    // 2. Auth Session Guard
    // ==========================================
    function checkAdminSession() {
        const token = localStorage.getItem('access_token');
        const username = localStorage.getItem('username');
        const rolesRaw = localStorage.getItem('roles');

        if (!token) {
            window.location.href = 'signin.html';
            return null;
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
            return null;
        }

        if (username) {
            if (usernameDisplay) usernameDisplay.textContent = username;
            if (userAvatar) userAvatar.textContent = username.charAt(0).toUpperCase();
        }

        if (roleBadge) roleBadge.textContent = 'Admin';
        return token;
    }

    const authToken = checkAdminSession();

    // ==========================================
    // 3. Order Details Modal Logic
    // ==========================================
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

    async function fetchAndDisplayOrderDetails(orderId) {
        if (!orderId || !authToken) return;

        openOrderModal();
        if (modalOrderLoader) modalOrderLoader.classList.remove('hidden');
        if (modalOrderContent) modalOrderContent.classList.add('hidden');

        try {
            const response = await fetch(`${API_BASE_URL}/admin/orders/${orderId}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${authToken}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) throw new Error(`HTTP Error (${response.status})`);

            const orderDetails = await response.json(); // Consumes AdminOrderDetailsResponse
            renderOrderDetailsModal(orderDetails);

        } catch (err) {
            console.error('Failed to fetch order details:', err);
            showAlert(`Unable to fetch order details: ${err.message}`, 'error');
            closeOrderModal();
        } finally {
            if (modalOrderLoader) modalOrderLoader.classList.add('hidden');
            if (modalOrderContent) modalOrderContent.classList.remove('hidden');
        }
    }

    function renderOrderDetailsModal(data) {
        // Order Header & Prominent Total Paid Amount
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

        // Customer Details & Custom References
        if (data.customer) {
            const c = data.customer;
            document.getElementById('modalCustName').textContent = `${c.firstName || ''} ${c.lastName || ''}`.trim() || 'N/A';
            document.getElementById('modalCustId').textContent = c.id || data.customerId || 'N/A';
            document.getElementById('modalCustUsername').textContent = c.username || 'N/A';
            document.getElementById('modalCustEmail').textContent = c.email || 'N/A';
            document.getElementById('modalCustMobile1').textContent = c.mobile1 || 'N/A';
            document.getElementById('modalCustMobile2').textContent = c.mobile2 || 'N/A';

            // Custom Customer Ref Number (e.g. CUS-001)
            const custRefNo = c.customerNumber || (c.id ? `CUS-${c.id.substring(0, 3).toUpperCase()}` : 'CUS-001');
            document.getElementById('modalCustomerNumberBadge').textContent = custRefNo;
        }

        // Detailed Shipping Address Parser
        const addrContainer = document.getElementById('modalShippingAddress');
        if (addrContainer) {
            if (data.shippingAddress) {
                const a = data.shippingAddress;

                // Extract Country Name if nested object or raw string
                const countryName = (typeof a.country === 'object' && a.country !== null) ? (a.country.name || 'N/A') : (a.country || '');
                const line1 = a.line1 || a.streetAddress || '';
                const line2 = a.line2 || '';
                const city = a.city || '';
                const district = a.district || '';
                const province = a.stateProvince || a.state || '';
                const postal = a.postalCode || '';

                addrContainer.innerHTML = `
                    <div class="font-bold text-black text-xs">${line1}</div>
                    ${line2 ? `<div class="text-zinc-700">${line2}</div>` : ''}
                    <div class="text-zinc-600">${city}${district && district !== city ? `, ${district}` : ''}</div>
                    <div class="text-zinc-600">${province} ${postal}</div>
                    <div class="text-zinc-500 font-semibold pt-1 border-t border-zinc-100 mt-1">${countryName}</div>
                `;
            } else {
                addrContainer.innerHTML = `<span class="text-zinc-400 italic">No shipping address attached.</span>`;
            }
        }

        // Purchased Items
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
                    tr.className = 'hover:bg-zinc-100/50';
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

        // Shipments and Tracking Logs
        const shipmentsContainer = document.getElementById('modalShipmentsContainer');
        if (shipmentsContainer) {
            shipmentsContainer.innerHTML = '';
            const shipments = data.shipments || [];

            if (shipments.length === 0) {
                shipmentsContainer.innerHTML = `<div class="p-4 bg-white border border-zinc-200 rounded-xl text-center text-zinc-400">No shipments generated for this order yet.</div>`;
            } else {
                shipments.forEach(s => {
                    const card = document.createElement('div');
                    card.className = 'bg-white border border-zinc-200 rounded-xl p-4 shadow-sm space-y-3';

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

                    shipmentsContainer.appendChild(card);
                });
            }
        }
    }

    // ==========================================
    // 4. Admin Dashboard Analytics Overview
    // ==========================================
    async function fetchAdminDashboard() {
        if (!authToken) return;

        showLoader(true);
        clearAlert();

        try {
            const response = await fetch(`${API_BASE_URL}/admin/dashboard`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${authToken}`,
                    'Content-Type': 'application/json'
                }
            });

            if (response.status === 401 || response.status === 403) {
                localStorage.clear();
                window.location.href = 'signin.html';
                return;
            }

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
            tr.className = 'hover:bg-zinc-100 transition-colors';
            tr.innerHTML = `
                <td class="py-3 px-5 font-bold text-black">${item.productTitle || 'N/A'}</td>
                <td class="py-3 px-5 text-zinc-500">${item.warehouseName || 'Hub'}</td>
                <td class="py-3 px-5 text-right font-bold text-red-600">${item.availableQuantity}</td>
                <td class="py-3 px-5 text-right text-zinc-600">${item.reorderLevel}</td>
            `;
            tbody.appendChild(tr);
        });
    }

    function renderRecentOrdersTable(orders = []) {
        const tbody = document.getElementById('recentOrdersTableBody');
        if (!tbody) return;
        tbody.innerHTML = orders.length === 0 ? `<tr><td colspan="4" class="py-4 text-center text-zinc-400">No recent orders recorded.</td></tr>` : '';
        orders.forEach(ord => {
            const tr = document.createElement('tr');
            tr.className = 'hover:bg-zinc-100 transition-colors cursor-pointer';
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
            tr.className = 'hover:bg-zinc-100 transition-colors';
            tr.innerHTML = `
                <td class="py-3.5 px-6 font-bold text-black">${s.shipmentNumber || 'N/A'}</td>
                <td class="py-3.5 px-6 text-zinc-500">${s.orderNumber || 'N/A'}</td>
                <td class="py-3.5 px-6 font-medium">${s.warehouseName || 'Main Hub'}</td>
                <td class="py-3.5 px-6">${getStatusBadgeHtml(s.status)}</td>
                <td class="py-3.5 px-6 font-bold text-black">${s.estimatedDeliveryDate ? new Date(s.estimatedDeliveryDate).toLocaleDateString() : 'N/A'}</td>
            `;
            tbody.appendChild(tr);
        });
    }

    // ==========================================
    // 5. Dedicated Orders Table Controller
    // ==========================================
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
        if (!tbody || !authToken) return;

        tbody.innerHTML = `<tr><td colspan="7" class="py-8 text-center text-zinc-500"><i class="fa-solid fa-spinner spinner mr-2"></i>Loading order records...</td></tr>`;

        try {
            const queryParams = new URLSearchParams({
                sortBy: orderQueryState.sortBy,
                direction: orderQueryState.direction,
                page: orderQueryState.page,
                size: orderQueryState.size
            });

            if (orderQueryState.search.trim()) queryParams.append('search', orderQueryState.search.trim());
            if (orderQueryState.status) queryParams.append('status', orderQueryState.status);

            const response = await fetch(`${API_BASE_URL}/admin/orders?${queryParams.toString()}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${authToken}`,
                    'Content-Type': 'application/json'
                }
            });

            if (response.status === 401 || response.status === 403) {
                localStorage.clear();
                window.location.href = 'signin.html';
                return;
            }

            if (!response.ok) throw new Error(`Failed to load orders (${response.status})`);

            const data = await response.json(); // Consumes PageResponse<AdminOrderListResponse>

            renderOrdersTable(data.content || []);
            updateOrderPaginationControls(data);

        } catch (err) {
            tbody.innerHTML = `<tr><td colspan="7" class="py-6 text-center text-red-600">Failed to load orders. ${err.message}</td></tr>`;
        }
    }

    function renderOrdersTable(orders) {
        const tbody = document.getElementById('ordersTableBody');
        if (!tbody) return;
        tbody.innerHTML = '';

        if (orders.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" class="py-8 text-center text-zinc-400">No matching orders found.</td></tr>`;
            return;
        }

        orders.forEach(order => {
            const tr = document.createElement('tr');
            tr.className = 'hover:bg-zinc-100/80 cursor-pointer transition-colors';

            const formattedDate = order.createdAt ? new Date(order.createdAt).toLocaleString() : 'N/A';
            const formattedAmount = order.totalAmount != null ? `$${parseFloat(order.totalAmount).toFixed(2)}` : '$0.00';

            tr.innerHTML = `
                <td class="py-3.5 px-5 font-bold text-black">${order.orderNumber || 'N/A'}</td>
                <td class="py-3.5 px-5">
                    <div class="font-bold text-black">${order.customerName || 'N/A'}</div>
                    <div class="text-[11px] text-zinc-500">${order.customerEmail || ''}</div>
                </td>
                <td class="py-3.5 px-5">${getStatusBadgeHtml(order.status)}</td>
                <td class="py-3.5 px-5 text-center font-bold text-zinc-700">${order.itemCount || 0}</td>
                <td class="py-3.5 px-5 text-center font-bold text-zinc-700">${order.shipmentCount || 0}</td>
                <td class="py-3.5 px-5 text-right font-extrabold text-black">${formattedAmount}</td>
                <td class="py-3.5 px-5 text-right text-zinc-500 text-xs">${formattedDate}</td>
            `;

            tr.addEventListener('click', () => {
                const orderId = order.orderId || order.id;
                if (orderId) {
                    fetchAndDisplayOrderDetails(orderId);
                }
            });

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

    // ==========================================
    // 6. Navigation Control & Routing
    // ==========================================
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
            case 'secProducts':
                if (currentPageTitle) currentPageTitle.textContent = 'Product Catalog';
                if (currentPageSubtitle) currentPageSubtitle.textContent = 'Product catalog entries and updates';
                break;
            case 'secCustomers':
                if (currentPageTitle) currentPageTitle.textContent = 'Customer Directory';
                if (currentPageSubtitle) currentPageSubtitle.textContent = 'Registered customer accounts and details';
                break;
            case 'secVendors':
                if (currentPageTitle) currentPageTitle.textContent = 'Vendor Network';
                if (currentPageSubtitle) currentPageSubtitle.textContent = 'Supplier accounts and fulfillment logs';
                break;
        }
    }

    sidebarButtons.forEach(btn => {
        btn.addEventListener('click', () => switchSection(btn.getAttribute('data-target')));
    });

    if (refreshDataBtn) refreshDataBtn.addEventListener('click', fetchAdminDashboard);

    if (signOutBtn) {
        signOutBtn.addEventListener('click', () => {
            localStorage.clear();
            window.location.href = 'signin.html';
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

    // Initialize View & Event Controls
    initOrdersTableEvents();
    fetchAdminDashboard();
    switchSection('secDashboard');
});