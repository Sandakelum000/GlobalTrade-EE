document.addEventListener('DOMContentLoaded', () => {

    const getContextPath = () => {
        const path = window.location.pathname;
        const secondSlash = path.indexOf('/', 1);
        return (secondSlash !== -1 ? path.substring(0, secondSlash) : '') + '/logistics/api';
    };

    const API_BASE_URL = getContextPath();

    // UI Elements
    const sidebarButtons = document.querySelectorAll('.sidebar-btn');
    const sectionPanes = document.querySelectorAll('.section-pane');
    const currentPageTitle = document.getElementById('currentPageTitle');
    const currentPageSubtitle = document.getElementById('currentPageSubtitle');

    const usernameDisplay = document.getElementById('usernameDisplay');
    const userAvatar = document.getElementById('userAvatar');
    const profileUsername = document.getElementById('profileUsername');
    const profileAvatar = document.getElementById('profileAvatar');
    const signOutBtn = document.getElementById('signOutBtn');
    const makeOrderBtn = document.getElementById('makeOrderBtn');

    // Loader & Alert
    const dashLoader = document.getElementById('dashLoader');
    const dashAlert = document.getElementById('dashAlert');
    const dashAlertIcon = document.getElementById('dashAlertIcon');
    const dashAlertMessage = document.getElementById('dashAlertMessage');

    // Inventory & Cart Elements
    const inventoryTableBody = document.getElementById('inventoryTableBody');
    const refreshInventoryBtn = document.getElementById('refreshInventoryBtn');
    const cartItemsContainer = document.getElementById('cartItemsContainer');
    const emptyCartNotice = document.getElementById('emptyCartNotice');
    const cartCountBadge = document.getElementById('cartCountBadge');
    const cartSubtotal = document.getElementById('cartSubtotal');
    const cartGrandTotal = document.getElementById('cartGrandTotal');
    const placeOrderSubmitBtn = document.getElementById('placeOrderSubmitBtn');

    // Modals
    const productModal = document.getElementById('productModal');
    const closeProductModalBtn = document.getElementById('closeProductModalBtn');
    const modalProductName = document.getElementById('modalProductName');
    const modalWarehouseName = document.getElementById('modalWarehouseName');
    const modalAvailableQty = document.getElementById('modalAvailableQty');
    const modalSellingPrice = document.getElementById('modalSellingPrice');
    const modalQtyInput = document.getElementById('modalQtyInput');
    const modalQtyMinus = document.getElementById('modalQtyMinus');
    const modalQtyPlus = document.getElementById('modalQtyPlus');
    const modalAddToCartBtn = document.getElementById('modalAddToCartBtn');

    // Application State
    let availableInventoryList = [];
    let activeCart = [];
    let selectedInventoryItem = null;
    let ordersChartInstance = null;
    let spendingChartInstance = null;

    // ==========================================
    // PayHere SDK Callbacks Configuration
    // ==========================================
    if (typeof payhere !== 'undefined') {
        payhere.onCompleted = function onCompleted(orderId) {
            console.log("PayHere Payment completed. Order ID: " + orderId);
            showAlert("Payment processed successfully! Updating records...", "info");
            setTimeout(() => {
                loadDashboardData();
            }, 1500);
        };

        payhere.onDismissed = function onDismissed() {
            console.log("PayHere Payment dismissed by user.");
            showAlert("Payment session was cancelled.", "error");
        };

        payhere.onError = function onError(error) {
            console.error("PayHere Error: " + error);
            showAlert("Payment error: " + error, "error");
        };
    }

    // ==========================================
    // 1. Authentication Check
    // ==========================================
    function checkAuthSession() {
        const token = localStorage.getItem('access_token');
        const username = localStorage.getItem('username');

        if (!token) {
            window.location.href = 'signin.html';
            return null;
        }

        if (username) {
            if (usernameDisplay) usernameDisplay.textContent = username;
            if (userAvatar) userAvatar.textContent = username.charAt(0).toUpperCase();
            if (profileUsername) profileUsername.textContent = username;
            if (profileAvatar) profileAvatar.textContent = username.charAt(0).toUpperCase();
        }

        return token;
    }

    const authToken = checkAuthSession();

    // ==========================================
    // 2. Main Dashboard Data Loader
    // ==========================================
    async function loadDashboardData() {
        if (!authToken) return;

        showLoader(true);
        clearAlert();

        try {
            const response = await fetch(`${API_BASE_URL}/customer/dashboard`, {
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

            if (!response.ok) throw new Error(`Dashboard error (${response.status})`);

            const data = await response.json();

            renderSummaryMetrics(data.summary);
            renderCharts(data.monthlyOrders, data.monthlySpending);
            renderOrderStatusCards(data.orderStatus);
            renderRecentOrdersTable(data.recentOrders);
            renderShipmentStatusCards(data.shipmentStatus);
            renderShipmentsTable(data.shipments);

        } catch (err) {
            showAlert(err.message || 'Error communicating with server.', 'error');
        } finally {
            showLoader(false);
        }
    }

    // ==========================================
    // 3. Inventory & Modal Handling
    // ==========================================
    async function fetchInventory() {
        if (!authToken) return;

        if (inventoryTableBody) {
            inventoryTableBody.innerHTML = `<tr><td colspan="5" class="py-6 text-center text-zinc-400 font-mono">Loading inventory stock...</td></tr>`;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/customer/inventory`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${authToken}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) throw new Error('Failed to retrieve inventory.');

            availableInventoryList = await response.json();
            renderInventoryTable(availableInventoryList);

        } catch (err) {
            console.error('Inventory fetch error:', err);
            if (inventoryTableBody) {
                inventoryTableBody.innerHTML = `<tr><td colspan="5" class="py-6 text-center text-red-600 font-mono">Failed to load available stock items.</td></tr>`;
            }
        }
    }

    function renderInventoryTable(items) {
        if (!inventoryTableBody) return;
        inventoryTableBody.innerHTML = '';

        if (!items || items.length === 0) {
            inventoryTableBody.innerHTML = `<tr><td colspan="5" class="py-6 text-center text-zinc-400">No inventory available.</td></tr>`;
            return;
        }

        items.forEach(item => {
            const tr = document.createElement('tr');
            tr.className = 'hover:bg-zinc-200/60 cursor-pointer transition-colors';

            const priceFormatted = item.sellingPrice != null ? `$${parseFloat(item.sellingPrice).toFixed(2)}` : '$0.00';

            tr.innerHTML = `
                <td class="py-3.5 px-5 font-bold text-black">${item.productName || 'N/A'}</td>
                <td class="py-3.5 px-5 text-zinc-500">${item.warehouseName || 'Main Center'}</td>
                <td class="py-3.5 px-5 text-right font-bold ${item.availableQuantity > 0 ? 'text-black' : 'text-red-500'}">${item.availableQuantity}</td>
                <td class="py-3.5 px-5 text-right font-bold text-black">${priceFormatted}</td>
                <td class="py-3.5 px-5 text-center">
                    <button class="px-2.5 py-1 bg-black text-white hover:bg-zinc-800 text-[10px] uppercase font-bold rounded">
                        Select
                    </button>
                </td>
            `;

            tr.addEventListener('click', () => openProductModal(item));
            inventoryTableBody.appendChild(tr);
        });
    }

    function openProductModal(item) {
        selectedInventoryItem = item;
        if (modalProductName) modalProductName.textContent = item.productName;
        if (modalWarehouseName) modalWarehouseName.textContent = item.warehouseName;
        if (modalAvailableQty) modalAvailableQty.textContent = item.availableQuantity;
        if (modalSellingPrice) modalSellingPrice.textContent = item.sellingPrice != null ? `$${parseFloat(item.sellingPrice).toFixed(2)}` : '$0.00';
        if (modalQtyInput) {
            modalQtyInput.value = 1;
            modalQtyInput.max = item.availableQuantity;
        }

        if (productModal) productModal.classList.remove('hidden');
    }

    if (closeProductModalBtn) {
        closeProductModalBtn.addEventListener('click', () => {
            if (productModal) productModal.classList.add('hidden');
        });
    }

    if (modalQtyMinus) {
        modalQtyMinus.addEventListener('click', () => {
            let current = parseInt(modalQtyInput.value) || 1;
            if (current > 1) modalQtyInput.value = current - 1;
        });
    }

    if (modalQtyPlus) {
        modalQtyPlus.addEventListener('click', () => {
            let current = parseInt(modalQtyInput.value) || 1;
            const max = selectedInventoryItem ? selectedInventoryItem.availableQuantity : 999;
            if (current < max) modalQtyInput.value = current + 1;
        });
    }

    if (modalAddToCartBtn) {
        modalAddToCartBtn.addEventListener('click', () => {
            if (!selectedInventoryItem) return;

            const qty = parseInt(modalQtyInput.value) || 1;
            const existingIndex = activeCart.findIndex(c => c.inventoryId === selectedInventoryItem.inventoryId);

            if (existingIndex > -1) {
                activeCart[existingIndex].quantity += qty;
            } else {
                activeCart.push({
                    inventoryId: selectedInventoryItem.inventoryId,
                    productName: selectedInventoryItem.productName,
                    sellingPrice: parseFloat(selectedInventoryItem.sellingPrice || 0),
                    quantity: qty,
                    availableQuantity: selectedInventoryItem.availableQuantity
                });
            }

            if (productModal) productModal.classList.add('hidden');
            renderCartSummary();
        });
    }

    if (refreshInventoryBtn) {
        refreshInventoryBtn.addEventListener('click', fetchInventory);
    }

    // ==========================================
    // 4. Cart & Order Submission
    // ==========================================
    function renderCartSummary() {
        if (!cartItemsContainer) return;
        cartItemsContainer.innerHTML = '';

        if (activeCart.length === 0) {
            if (emptyCartNotice) emptyCartNotice.classList.remove('hidden');
            if (cartCountBadge) cartCountBadge.textContent = '0 Items';
            if (cartSubtotal) cartSubtotal.textContent = '$0.00';
            if (cartGrandTotal) cartGrandTotal.textContent = '$0.00';
            if (placeOrderSubmitBtn) placeOrderSubmitBtn.disabled = true;
            return;
        }

        if (emptyCartNotice) emptyCartNotice.classList.add('hidden');
        let total = 0;
        let count = 0;

        activeCart.forEach((item, index) => {
            const itemTotal = item.sellingPrice * item.quantity;
            total += itemTotal;
            count += item.quantity;

            const div = document.createElement('div');
            div.className = 'p-3 bg-zinc-100 rounded-lg font-mono text-xs flex items-center justify-between gap-2 border border-zinc-200';
            div.innerHTML = `
                <div class="flex-1 min-w-0">
                    <span class="font-bold text-black block truncate">${item.productName}</span>
                    <span class="text-[10px] text-zinc-500 block">$${item.sellingPrice.toFixed(2)} × ${item.quantity}</span>
                </div>
                <span class="font-bold text-black">$${itemTotal.toFixed(2)}</span>
                <button data-index="${index}" class="remove-cart-item text-zinc-400 hover:text-red-600 px-1">
                    <i class="fa-solid fa-trash-can"></i>
                </button>
            `;
            cartItemsContainer.appendChild(div);
        });

        if (cartCountBadge) cartCountBadge.textContent = `${count} Items`;
        if (cartSubtotal) cartSubtotal.textContent = `$${total.toFixed(2)}`;
        if (cartGrandTotal) cartGrandTotal.textContent = `$${total.toFixed(2)}`;
        if (placeOrderSubmitBtn) placeOrderSubmitBtn.disabled = false;

        document.querySelectorAll('.remove-cart-item').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const idx = parseInt(e.currentTarget.getAttribute('data-index'));
                activeCart.splice(idx, 1);
                renderCartSummary();
            });
        });
    }

    if (placeOrderSubmitBtn) {
        placeOrderSubmitBtn.addEventListener('click', async () => {
            if (activeCart.length === 0) return;

            placeOrderSubmitBtn.disabled = true;
            placeOrderSubmitBtn.innerHTML = `<i class="fa-solid fa-spinner spinner"></i> Submitting...`;

            const payload = {
                items: activeCart.map(item => ({
                    inventoryId: item.inventoryId,
                    quantity: item.quantity
                }))
            };

            try {
                const response = await fetch(`${API_BASE_URL}/orders/create`, {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${authToken}`,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(payload)
                });

                if (!response.ok) throw new Error('Order creation failed.');

                const createdOrder = await response.json();

                activeCart = [];
                renderCartSummary();
                showAlert(`Order created! Reference: ${createdOrder.orderNumber || 'Success'}`, 'info');

                loadDashboardData();
                switchSection('secOrders');

            } catch (err) {
                console.error('Create order error:', err);
                showAlert('Failed to place order. Check stock availability.', 'error');
            } finally {
                placeOrderSubmitBtn.disabled = false;
                placeOrderSubmitBtn.innerHTML = `<i class="fa-solid fa-paper-plane"></i><span>Submit Order</span>`;
            }
        });
    }

    // ==========================================
    // 5. PayHere Payment Integration
    // ==========================================
    async function initiatePayHereCheckout(orderId) {
        if (!orderId) {
            showAlert("Invalid Order ID for payment.", "error");
            return;
        }

        if (typeof payhere === 'undefined') {
            showAlert("PayHere SDK is not loaded. Please check your HTML script tags.", "error");
            return;
        }

        showLoader(true);

        try {
            const response = await fetch(`${API_BASE_URL}/checkouts/customer-checkouts`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${authToken}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ orderId: orderId })
            });

            if (!response.ok) {
                const errText = await response.text();
                throw new Error(errText || `Checkout failed (${response.status})`);
            }

            const payHereDTO = await response.json();

            const paymentPayload = {
                "sandbox": payHereDTO.sandbox,
                "merchant_id": payHereDTO.merchant_id,
                "return_url": payHereDTO.return_url,
                "cancel_url": payHereDTO.cancel_url,
                "notify_url": payHereDTO.notify_url,
                "order_id": payHereDTO.order_id,
                "items": payHereDTO.items,
                "amount": payHereDTO.amount,
                "currency": payHereDTO.currency,
                "hash": payHereDTO.hash,
                "first_name": payHereDTO.first_name,
                "last_name": payHereDTO.last_name,
                "email": payHereDTO.email,
                "phone": payHereDTO.phone,
                "address": payHereDTO.address,
                "city": payHereDTO.city,
                "country": payHereDTO.country
            };

            payhere.startPayment(paymentPayload);

        } catch (err) {
            console.error("PayHere initiation error:", err);
            showAlert(err.message || "Failed to initiate PayHere payment.", "error");
        } finally {
            showLoader(false);
        }
    }

    // ==========================================
    // 6. Orders Table & Actions (Pay / Cancel)
    // ==========================================
    function renderRecentOrdersTable(orders = []) {
        const tbody = document.getElementById('recentOrdersTableBody');
        if (!tbody) return;
        tbody.innerHTML = '';

        if (orders.length === 0) {
            tbody.innerHTML = `<tr><td colspan="6" class="py-6 text-center text-zinc-400">No recent orders found.</td></tr>`;
            return;
        }

        orders.forEach(ord => {
            const tr = document.createElement('tr');
            tr.className = 'hover:bg-zinc-200/50 transition-colors';

            const createdDate = ord.createdAt ? new Date(ord.createdAt).toLocaleString() : 'N/A';
            const formattedAmount = ord.totalAmount != null ? `$${parseFloat(ord.totalAmount).toFixed(2)}` : '$0.00';
            const statusUpper = ord.status ? ord.status.toUpperCase() : '';
            const isCancelled = statusUpper === 'CANCELLED';
            const isPending = statusUpper === 'PENDING';

            tr.innerHTML = `
                <td class="py-3.5 px-6 font-bold text-black">${ord.orderNumber || 'N/A'}</td>
                <td class="py-3.5 px-6 text-[10px] text-zinc-400">${ord.orderId || ''}</td>
                <td class="py-3.5 px-6">${createdDate}</td>
                <td class="py-3.5 px-6 font-bold text-black">${formattedAmount}</td>
                <td class="py-3.5 px-6">
                    <span class="px-2.5 py-1 ${isCancelled ? 'bg-red-100 text-red-700' : 'bg-zinc-200 text-black'} rounded text-[10px] font-bold">
                        ${ord.status}
                    </span>
                </td>
                <td class="py-3.5 px-6 text-center flex items-center justify-center gap-2">
                    ${isPending ? `
                        <button class="pay-now-btn px-3 py-1 bg-black text-white hover:bg-zinc-800 rounded text-[10px] font-bold uppercase transition-all" data-orderid="${ord.orderId}">
                            Pay Now
                        </button>
                        <button class="cancel-order-btn px-2.5 py-1 border border-zinc-400 text-zinc-700 hover:bg-red-600 hover:text-white hover:border-red-600 rounded text-[10px] font-bold uppercase transition-all" data-orderid="${ord.orderId}">
                            Cancel
                        </button>
                    ` : `<span class="text-[10px] text-zinc-400">N/A</span>`}
                </td>
            `;
            tbody.appendChild(tr);
        });

        // Pay Now Button Event Listeners -> Triggers PayHere
        document.querySelectorAll('.pay-now-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const orderId = e.currentTarget.getAttribute('data-orderid');
                initiatePayHereCheckout(orderId);
            });
        });

        // Cancel Order Button Event Listeners -> Requests @POST @Path("/calcel/{orderId}")
        document.querySelectorAll('.cancel-order-btn').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                const orderId = e.currentTarget.getAttribute('data-orderid');
                if (!orderId) return;

                if (!confirm('Are you sure you want to cancel this order?')) return;

                try {
                    const response = await fetch(`${API_BASE_URL}/orders/calcel/${orderId}`, {
                        method: 'POST',
                        headers: {
                            'Authorization': `Bearer ${authToken}`,
                            'Content-Type': 'application/json'
                        }
                    });

                    if (!response.ok) throw new Error('Failed to cancel order.');

                    showAlert('Order has been successfully cancelled.', 'info');
                    loadDashboardData();

                } catch (err) {
                    console.error('Cancel order error:', err);
                    showAlert('Unable to cancel this order.', 'error');
                }
            });
        });
    }

    // ==========================================
    // 7. Section Navigation Controller
    // ==========================================
    function switchSection(targetId) {
        sectionPanes.forEach(pane => pane.classList.add('hidden'));

        sidebarButtons.forEach(btn => {
            if (btn.getAttribute('data-target') === targetId) {
                btn.classList.add('bg-black', 'text-white');
                btn.classList.remove('text-zinc-400');
            } else {
                btn.classList.remove('bg-black', 'text-white');
                btn.classList.add('text-zinc-400');
            }
        });

        const selectedPane = document.getElementById(targetId);
        if (selectedPane) selectedPane.classList.remove('hidden');

        if (targetId === 'secInventory') {
            fetchInventory();
        }

        switch (targetId) {
            case 'secDashboard':
                if (currentPageTitle) currentPageTitle.textContent = 'Dashboard Overview';
                if (currentPageSubtitle) currentPageSubtitle.textContent = 'Real-time metrics and operational status';
                break;
            case 'secInventory':
                if (currentPageTitle) currentPageTitle.textContent = 'Inventory Marketplace';
                if (currentPageSubtitle) currentPageSubtitle.textContent = 'Select stock items and configure new order quantities';
                break;
            case 'secOrders':
                if (currentPageTitle) currentPageTitle.textContent = 'Orders Management';
                if (currentPageSubtitle) currentPageSubtitle.textContent = 'Status breakdown and recently placed order records';
                break;
            case 'secShipments':
                if (currentPageTitle) currentPageTitle.textContent = 'Shipments & Tracking';
                if (currentPageSubtitle) currentPageSubtitle.textContent = 'Route analytics and delivery progress';
                break;
            case 'secPayments':
                if (currentPageTitle) currentPageTitle.textContent = 'Payment History';
                if (currentPageSubtitle) currentPageSubtitle.textContent = 'Receipts and transaction audit trails';
                break;
            case 'secProfile':
                if (currentPageTitle) currentPageTitle.textContent = 'Customer Profile';
                if (currentPageSubtitle) currentPageSubtitle.textContent = 'Account parameters and role access';
                break;
        }
    }

    sidebarButtons.forEach(btn => {
        btn.addEventListener('click', () => switchSection(btn.getAttribute('data-target')));
    });

    if (makeOrderBtn) {
        makeOrderBtn.addEventListener('click', () => switchSection('secInventory'));
    }

    if (signOutBtn) {
        signOutBtn.addEventListener('click', () => {
            localStorage.clear();
            window.location.href = 'signin.html';
        });
    }

    // ==========================================
    // 8. Render Helper Functions
    // ==========================================
    function renderSummaryMetrics(summary) {
        if (!summary) return;
        const totalOrd = document.getElementById('metricTotalOrders');
        const pendingOrd = document.getElementById('metricPendingOrders');
        const confOrd = document.getElementById('metricConfirmedOrders');
        const actShip = document.getElementById('metricActiveShipments');
        const delShip = document.getElementById('metricDeliveredShipments');
        const totalSpent = document.getElementById('metricTotalSpent');

        if (totalOrd) totalOrd.textContent = (summary.totalOrders || 0).toLocaleString();
        if (pendingOrd) pendingOrd.textContent = (summary.pendingOrders || 0).toLocaleString();
        if (confOrd) confOrd.textContent = (summary.confirmedOrders || 0).toLocaleString();
        if (actShip) actShip.textContent = (summary.activeShipments || 0).toLocaleString();
        if (delShip) delShip.textContent = (summary.deliveredShipments || 0).toLocaleString();

        const spent = summary.totalSpent != null ? parseFloat(summary.totalSpent).toLocaleString(undefined, { minimumFractionDigits: 2 }) : '0.00';
        if (totalSpent) totalSpent.textContent = `$${spent}`;
    }

    function renderCharts(monthlyOrders = [], monthlySpending = []) {
        const ordersChartElem = document.getElementById('monthlyOrdersChart');
        if (ordersChartElem) {
            const ordersCtx = ordersChartElem.getContext('2d');
            if (ordersChartInstance) ordersChartInstance.destroy();

            ordersChartInstance = new Chart(ordersCtx, {
                type: 'bar',
                data: {
                    labels: monthlyOrders.map(m => m.month),
                    datasets: [{ label: 'Orders', data: monthlyOrders.map(m => m.count), backgroundColor: '#000000', borderRadius: 4 }]
                },
                options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false } } }
            });
        }

        const spendingChartElem = document.getElementById('monthlySpendingChart');
        if (spendingChartElem) {
            const spendingCtx = spendingChartElem.getContext('2d');
            if (spendingChartInstance) spendingChartInstance.destroy();

            spendingChartInstance = new Chart(spendingCtx, {
                type: 'line',
                data: {
                    labels: monthlySpending.map(m => m.month),
                    datasets: [{ label: 'Spent ($)', data: monthlySpending.map(m => m.amount), borderColor: '#000000', backgroundColor: 'rgba(0,0,0,0.05)', fill: true, tension: 0.2 }]
                },
                options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false } } }
            });
        }
    }

    function renderOrderStatusCards(orderStatusList = []) {
        const container = document.getElementById('orderStatusCardsContainer');
        if (!container) return;
        container.innerHTML = '';
        orderStatusList.forEach(item => {
            const card = document.createElement('div');
            card.className = 'p-4 border border-zinc-200 rounded-xl bg-white shadow-sm';
            card.innerHTML = `<div class="text-[10px] font-mono font-bold uppercase text-zinc-500">${item.status}</div><div class="text-xl font-black text-black mt-1">${item.count}</div>`;
            container.appendChild(card);
        });
    }

    function renderShipmentStatusCards(shipmentStatusList = []) {
        const container = document.getElementById('shipmentStatusCardsContainer');
        if (!container) return;
        container.innerHTML = '';
        shipmentStatusList.forEach(item => {
            const card = document.createElement('div');
            card.className = 'p-3 border border-zinc-200 rounded-xl bg-white shadow-sm';
            card.innerHTML = `<div class="text-[9px] font-mono font-bold uppercase text-zinc-500 truncate">${item.status}</div><div class="text-lg font-black text-black mt-0.5">${item.count}</div>`;
            container.appendChild(card);
        });
    }

    function renderShipmentsTable(shipments = []) {
        const tbody = document.getElementById('shipmentsTableBody');
        if (!tbody) return;
        tbody.innerHTML = '';

        if (shipments.length === 0) {
            tbody.innerHTML = `<tr><td colspan="6" class="py-6 text-center text-zinc-400">No active shipments logged.</td></tr>`;
            return;
        }

        shipments.forEach(s => {
            const tr = document.createElement('tr');
            tr.className = 'hover:bg-zinc-200/50 transition-colors';
            const estDate = s.estimatedDeliveryDate ? new Date(s.estimatedDeliveryDate).toLocaleDateString() : 'N/A';

            tr.innerHTML = `
                <td class="py-3.5 px-6">
                    <span class="font-bold text-black block">${s.shipmentNumber || 'N/A'}</span>
                    <span class="text-[10px] text-zinc-400 block font-mono">Order: ${s.orderNumber || 'N/A'}</span>
                </td>
                <td class="py-3.5 px-6">${s.warehouseName || 'Hub'}</td>
                <td class="py-3.5 px-6">
                    <span class="block font-bold text-black">${s.routeName || 'Direct'}</span>
                    <span class="text-[10px] text-zinc-500 block">${s.routeDistanceKm || 0} km | ${s.routeEstimatedHours || 0} hrs</span>
                </td>
                <td class="py-3.5 px-6">
                    <span class="px-2.5 py-1 bg-black text-white rounded text-[10px] font-bold">${s.status}</span>
                </td>
                <td class="py-3.5 px-6 font-bold">${estDate}</td>
                <td class="py-3.5 px-6">${s.latestLocation || 'In Transit'}</td>
            `;
            tbody.appendChild(tr);
        });
    }

    function showLoader(visible) {
        if (!dashLoader) return;
        if (visible) dashLoader.classList.remove('hidden');
        else dashLoader.classList.add('hidden');
    }

    function showAlert(message, type = 'error') {
        if (!dashAlert || !dashAlertMessage || !dashAlertIcon) return;
        dashAlertMessage.textContent = message;
        dashAlert.className = 'p-4 rounded-xl text-xs font-mono border flex items-center gap-3 shadow-sm bg-black text-white border-zinc-900';
        dashAlertIcon.className = type === 'error' ? 'fa-solid fa-triangle-exclamation text-white' : 'fa-solid fa-circle-info text-white';
        dashAlert.classList.remove('hidden');
    }

    function clearAlert() {
        if (dashAlert) dashAlert.classList.add('hidden');
    }

    // Initialize application data
    loadDashboardData();
    switchSection('secDashboard');
});