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

    window.authenticatedFetch = authenticatedFetch;

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

    const dashLoader = document.getElementById('dashLoader');
    const dashAlert = document.getElementById('dashAlert');
    const dashAlertIcon = document.getElementById('dashAlertIcon');
    const dashAlertMessage = document.getElementById('dashAlertMessage');

    const marketplaceGrid = document.getElementById('marketplaceGrid');
    const marketplaceSearchInput = document.getElementById('marketplaceSearchInput');
    const refreshInventoryBtn = document.getElementById('refreshInventoryBtn');
    const cartItemsContainer = document.getElementById('cartItemsContainer');
    const emptyCartNotice = document.getElementById('emptyCartNotice');
    const cartCountBadge = document.getElementById('cartCountBadge');
    const cartSubtotal = document.getElementById('cartSubtotal');
    const cartGrandTotal = document.getElementById('cartGrandTotal');
    const placeOrderSubmitBtn = document.getElementById('placeOrderSubmitBtn');

    const productModal = document.getElementById('productModal');
    const closeProductModalBtn = document.getElementById('closeProductModalBtn');
    const modalProductName = document.getElementById('modalProductName');
    const modalWarehouseName = document.getElementById('modalWarehouseName');
    const modalAvailableQty = document.getElementById('modalAvailableQty');
    const modalSellingPrice = document.getElementById('modalSellingPrice');
    const modalQtyInput = document.getElementById('modalQtyInput');
    const modalQtyMinus = document.getElementById('modalQtyMinus');
    const modalQtyPlus = document.getElementById('modalQtyPlus');
    const modalQtyError = document.getElementById('modalQtyError');
    const modalQtyErrorText = document.getElementById('modalQtyErrorText');
    const modalAddToCartBtn = document.getElementById('modalAddToCartBtn');

    let availableInventoryList = [];
    let activeCart = [];
    let selectedInventoryItem = null;
    let ordersChartInstance = null;
    let spendingChartInstance = null;


    function getStatusBadgeHtml(statusStr) {
        const rawStatus = (statusStr || '').toUpperCase();
        let displayLabel = rawStatus.charAt(0) + rawStatus.slice(1).toLowerCase().replace(/_/g, ' ');

        switch (rawStatus) {
            case 'CONFIRMED':
            case 'DELIVERED':
            case 'ACTIVE':
            case 'PAID':
                return `<span class="px-2.5 py-1 bg-emerald-100 text-emerald-800 border border-emerald-200 rounded-lg text-xs font-semibold inline-flex items-center gap-1.5"><i class="fa-solid fa-circle-check text-[8px] text-emerald-600"></i> ${displayLabel}</span>`;
            case 'CANCELLED':
            case 'OUT_OF_STOCK':
            case 'FAILED':
            case 'REJECTED':
                return `<span class="px-2.5 py-1 bg-rose-100 text-rose-800 border border-rose-200 rounded-lg text-xs font-semibold inline-flex items-center gap-1.5"><i class="fa-solid fa-circle-xmark text-[8px] text-rose-600"></i> ${displayLabel}</span>`;
            case 'PENDING':
            case 'RESERVED':
            case 'UNDER_REVIEW':
                return `<span class="px-2.5 py-1 bg-amber-100 text-amber-800 border border-amber-200 rounded-lg text-xs font-semibold inline-flex items-center gap-1.5"><i class="fa-solid fa-clock text-[8px] text-amber-600"></i> ${displayLabel}</span>`;
            case 'PROCESSING':
            case 'SHIPPED':
            case 'IN_TRANSIT':
            case 'OUT_FOR_DELIVERY':
                return `<span class="px-2.5 py-1 bg-blue-100 text-blue-800 border border-blue-200 rounded-lg text-xs font-semibold inline-flex items-center gap-1.5"><i class="fa-solid fa-truck-fast text-[8px] text-blue-600"></i> ${displayLabel}</span>`;
            default:
                return `<span class="px-2.5 py-1 bg-slate-100 text-slate-800 border border-slate-200 rounded-lg text-xs font-semibold inline-flex items-center gap-1.5">${displayLabel}</span>`;
        }
    }

    if (typeof payhere !== 'undefined') {
        payhere.onCompleted = function onCompleted(orderId) {
            showAlert("Payment processed successfully! Updating dashboard...", "info");
            setTimeout(() => {
                loadDashboardData();
            }, 1500);
        };

        payhere.onDismissed = function onDismissed() {
            showAlert("Payment session was cancelled.", "error");
        };

        payhere.onError = function onError(error) {
            showAlert("Payment error: " + error, "error");
        };
    }

    function checkAuthSession() {
        const token = localStorage.getItem('access_token');
        const username = localStorage.getItem('username');

        if (!token) {
            clearSession();
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

    async function loadDashboardData() {
        if (!authToken) return;

        showLoader(true);
        clearAlert();

        try {
            const response = await authenticatedFetch(`${API_BASE_URL}/customer/dashboard`, {
                method: 'GET'
            });

            if (!response.ok) throw new Error(`Dashboard error (${response.status})`);

            const data = await response.json();

            renderSummaryMetrics(data.summary);
            renderCharts(data.monthlyOrders, data.monthlySpending);
            renderOrderStatusCards(data.orderStatus);
            renderRecentOrdersTable(data.recentOrders);
            renderShipmentStatusCards(data.shipmentStatus);
            renderShipmentsTable(data.shipments);

        } catch (err) {
            showAlert(err.message || 'Error communicating with logistics service.', 'error');
        } finally {
            showLoader(false);
        }
    }

    async function fetchInventory() {
        if (!authToken) return;

        if (marketplaceGrid) {
            marketplaceGrid.innerHTML = `
                <div class="col-span-full py-12 text-center text-slate-400">
                    <i class="fa-solid fa-spinner animate-spin text-2xl text-indigo-600 mb-2"></i>
                    <p class="text-xs font-semibold">Loading marketplace inventory...</p>
                </div>
            `;
        }

        try {
            const response = await authenticatedFetch(`${API_BASE_URL}/customer/inventory`, {
                method: 'GET'
            });

            if (!response.ok) throw new Error('Failed to retrieve inventory.');

            availableInventoryList = await response.json();
            renderMarketplaceGrid(availableInventoryList);

        } catch (err) {
            console.error('Inventory fetch error:', err);
            if (marketplaceGrid) {
                marketplaceGrid.innerHTML = `
                    <div class="col-span-full py-12 text-center text-rose-500">
                        <i class="fa-solid fa-triangle-exclamation text-2xl mb-2"></i>
                        <p class="text-xs font-semibold">Failed to load marketplace catalog.</p>
                    </div>
                `;
            }
        }
    }

    function renderMarketplaceGrid(items) {
        if (!marketplaceGrid) return;
        marketplaceGrid.innerHTML = '';

        if (!items || items.length === 0) {
            marketplaceGrid.innerHTML = `
                <div class="col-span-full py-12 text-center text-slate-400 bg-white border border-slate-200/80 rounded-2xl">
                    <i class="fa-solid fa-box-open text-3xl mb-2 text-slate-300"></i>
                    <p class="text-xs font-semibold">No stock items available in marketplace.</p>
                </div>
            `;
            return;
        }

        items.forEach(item => {
            const card = document.createElement('div');
            card.className = 'bg-white border border-slate-200/80 rounded-2xl p-5 shadow-sm hover:shadow-md transition-all flex flex-col justify-between space-y-4 group';

            const priceFormatted = item.sellingPrice != null ? `$${parseFloat(item.sellingPrice).toFixed(2)}` : '$0.00';
            const inStock = item.availableQuantity > 0;

            card.innerHTML = `
                <div class="space-y-2">
                    <div class="flex items-start justify-between gap-2">
                        <span class="text-[10px] font-bold uppercase tracking-wider text-indigo-600 bg-indigo-50 px-2 py-0.5 rounded-md">
                            <i class="fa-solid fa-warehouse text-[9px] mr-1"></i>${item.warehouseName || 'Hub Center'}
                        </span>
                        <span class="text-xs font-extrabold ${inStock ? 'text-emerald-600 bg-emerald-50' : 'text-rose-600 bg-rose-50'} px-2 py-0.5 rounded-md">
                            ${inStock ? `${item.availableQuantity} Left` : 'Out of Stock'}
                        </span>
                    </div>
                    <h4 class="text-sm font-extrabold text-slate-900 group-hover:text-indigo-600 transition-colors">${item.productName || 'N/A'}</h4>
                </div>

                <div class="pt-3 border-t border-slate-100 flex items-center justify-between">
                    <div>
                        <span class="text-[10px] text-slate-400 block font-semibold uppercase">Unit Price</span>
                        <span class="text-base font-black text-slate-900">${priceFormatted}</span>
                    </div>
                    <button class="select-product-btn bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-bold px-3.5 py-2 rounded-xl transition-all shadow-md shadow-indigo-500/20 ${!inStock ? 'opacity-50 cursor-not-allowed' : ''}" ${!inStock ? 'disabled' : ''}>
                        Select Item
                    </button>
                </div>
            `;

            const selectBtn = card.querySelector('.select-product-btn');
            if (selectBtn && inStock) {
                selectBtn.addEventListener('click', () => openProductModal(item));
            }

            marketplaceGrid.appendChild(card);
        });
    }

    if (marketplaceSearchInput) {
        marketplaceSearchInput.addEventListener('input', (e) => {
            const query = e.target.value.toLowerCase().trim();
            const filtered = availableInventoryList.filter(item =>
                (item.productName && item.productName.toLowerCase().includes(query)) ||
                (item.warehouseName && item.warehouseName.toLowerCase().includes(query))
            );
            renderMarketplaceGrid(filtered);
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
        hideModalError();
        if (productModal) productModal.classList.remove('hidden');
    }

    function hideModalError() {
        if (modalQtyError) modalQtyError.classList.add('hidden');
    }

    function showModalError(msg) {
        if (modalQtyError && modalQtyErrorText) {
            modalQtyErrorText.textContent = msg;
            modalQtyError.classList.remove('hidden');
        }
    }

    function validateQuantityInput(qty, availableMax) {
        if (isNaN(qty) || qty === null || qty === undefined) {
            return { valid: false, message: 'Please specify a numeric quantity.' };
        }
        if (!Number.isInteger(qty)) {
            return { valid: false, message: 'Quantity must be a whole number.' };
        }
        if (qty <= 0) {
            return { valid: false, message: 'Quantity must be greater than zero.' };
        }
        if (qty > availableMax) {
            return { valid: false, message: `Quantity cannot exceed available stock (${availableMax}).` };
        }
        return { valid: true };
    }

    if (closeProductModalBtn) {
        closeProductModalBtn.addEventListener('click', () => {
            if (productModal) productModal.classList.add('hidden');
        });
    }

    if (modalQtyMinus) {
        modalQtyMinus.addEventListener('click', () => {
            let current = parseInt(modalQtyInput.value) || 1;
            if (current > 1) {
                modalQtyInput.value = current - 1;
                hideModalError();
            }
        });
    }

    if (modalQtyPlus) {
        modalQtyPlus.addEventListener('click', () => {
            let current = parseInt(modalQtyInput.value) || 1;
            const max = selectedInventoryItem ? selectedInventoryItem.availableQuantity : 999;
            if (current < max) {
                modalQtyInput.value = current + 1;
                hideModalError();
            } else {
                showModalError(`Maximum available stock reached (${max}).`);
            }
        });
    }

    if (modalQtyInput) {
        modalQtyInput.addEventListener('input', () => {
            hideModalError();
        });
    }

    if (modalAddToCartBtn) {
        modalAddToCartBtn.addEventListener('click', () => {
            if (!selectedInventoryItem) return;

            const qtyVal = parseFloat(modalQtyInput.value);
            const validation = validateQuantityInput(qtyVal, selectedInventoryItem.availableQuantity);

            if (!validation.valid) {
                showModalError(validation.message);
                return;
            }

            const qty = parseInt(qtyVal, 10);
            const existingIndex = activeCart.findIndex(c => c.inventoryId === selectedInventoryItem.inventoryId);

            if (existingIndex > -1) {
                const newTotalQty = activeCart[existingIndex].quantity + qty;
                const totalValidation = validateQuantityInput(newTotalQty, selectedInventoryItem.availableQuantity);
                if (!totalValidation.valid) {
                    showModalError(`Combined cart quantity exceeds available stock (${selectedInventoryItem.availableQuantity}).`);
                    return;
                }
                activeCart[existingIndex].quantity = newTotalQty;
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
            div.className = 'p-3 bg-slate-50 rounded-xl text-xs flex items-center justify-between gap-2 border border-slate-200/80';
            div.innerHTML = `
                <div class="flex-1 min-w-0">
                    <span class="font-extrabold text-slate-900 block truncate">${item.productName}</span>
                    <span class="text-[11px] text-slate-500 block font-semibold">$${item.sellingPrice.toFixed(2)} × ${item.quantity}</span>
                </div>
                <span class="font-black text-slate-900">$${itemTotal.toFixed(2)}</span>
                <button data-index="${index}" class="remove-cart-item text-slate-400 hover:text-rose-600 p-1 transition-colors">
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
            placeOrderSubmitBtn.innerHTML = `<i class="fa-solid fa-spinner animate-spin"></i> Submitting Order...`;

            const payload = {
                items: activeCart.map(item => ({
                    inventoryId: item.inventoryId,
                    quantity: item.quantity
                }))
            };

            try {
                const response = await authenticatedFetch(`${API_BASE_URL}/orders/create`, {
                    method: 'POST',
                    body: JSON.stringify(payload)
                });

                if (!response.ok) throw new Error('Order creation failed.');

                const createdOrder = await response.json();

                activeCart = [];
                renderCartSummary();
                showAlert(`Order submitted! Reference: ${createdOrder.orderNumber || 'Success'}`, 'info');

                loadDashboardData();
                switchSection('secOrders');

            } catch (err) {
                console.error('Create order error:', err);
                showAlert('Failed to place order. Check stock availability.', 'error');
            } finally {
                placeOrderSubmitBtn.disabled = false;
                placeOrderSubmitBtn.innerHTML = `<i class="fa-solid fa-paper-plane"></i><span>Submit Purchase Order</span>`;
            }
        });
    }

    async function initiatePayHereCheckout(orderId) {
        if (!orderId) {
            showAlert("Invalid Order ID for payment.", "error");
            return;
        }

        if (typeof payhere === 'undefined') {
            showAlert("PayHere SDK failed to load.", "error");
            return;
        }

        showLoader(true);

        try {
            const response = await authenticatedFetch(`${API_BASE_URL}/checkouts/customer-checkouts`, {
                method: 'POST',
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

    function renderRecentOrdersTable(orders = []) {
        const tbody = document.getElementById('recentOrdersTableBody');
        if (!tbody) return;
        tbody.innerHTML = '';

        if (orders.length === 0) {
            tbody.innerHTML = `<tr><td colspan="5" class="py-8 text-center text-slate-400">No order history available.</td></tr>`;
            return;
        }

        orders.forEach(ord => {
            const tr = document.createElement('tr');
            tr.className = 'hover:bg-slate-50/80 transition-colors';

            const createdDate = ord.createdAt ? new Date(ord.createdAt).toLocaleString() : 'N/A';
            const formattedAmount = ord.totalAmount != null ? `$${parseFloat(ord.totalAmount).toFixed(2)}` : '$0.00';
            const statusUpper = ord.status ? ord.status.toUpperCase() : '';
            const isPending = statusUpper === 'PENDING';

            tr.innerHTML = `
                <td class="py-3.5 px-6 font-extrabold text-slate-900">
                    ${ord.orderNumber || 'N/A'}
                    <span class="block text-[10px] text-slate-400 font-normal">${ord.orderId ? ord.orderId.substring(0, 8) : ''}</span>
                </td>
                <td class="py-3.5 px-6 font-medium text-slate-600">${createdDate}</td>
                <td class="py-3.5 px-6 font-black text-slate-900">${formattedAmount}</td>
                <td class="py-3.5 px-6">${getStatusBadgeHtml(ord.status)}</td>
                <td class="py-3.5 px-6 text-center">
                    <div class="flex items-center justify-center gap-2">
                        ${isPending ? `
                            <button class="pay-now-btn px-3 py-1.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg text-xs font-bold uppercase transition-all shadow-md shadow-indigo-500/20 flex items-center gap-1" data-orderid="${ord.orderId}">
                                <i class="fa-solid fa-credit-card text-[10px]"></i> Pay
                            </button>
                            <button class="cancel-order-btn px-2.5 py-1.5 border border-slate-200 text-slate-600 hover:bg-rose-50 hover:text-rose-600 hover:border-rose-200 rounded-lg text-xs font-bold uppercase transition-all" data-orderid="${ord.orderId}">
                                Cancel
                            </button>
                        ` : `<span class="text-xs text-slate-400 font-semibold">—</span>`}
                    </div>
                </td>
            `;
            tbody.appendChild(tr);
        });

        document.querySelectorAll('.pay-now-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const orderId = e.currentTarget.getAttribute('data-orderid');
                initiatePayHereCheckout(orderId);
            });
        });

        document.querySelectorAll('.cancel-order-btn').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                const orderId = e.currentTarget.getAttribute('data-orderid');
                if (!orderId) return;

                if (!confirm('Are you sure you want to cancel this order?')) return;

                try {
                    const response = await authenticatedFetch(`${API_BASE_URL}/orders/calcel/${orderId}`, {
                        method: 'POST'
                    });

                    if (!response.ok) throw new Error('Failed to cancel order.');

                    showAlert('Order has been cancelled.', 'info');
                    loadDashboardData();

                } catch (err) {
                    console.error('Cancel order error:', err);
                    showAlert('Unable to cancel this order.', 'error');
                }
            });
        });
    }

    function switchSection(targetId) {
        sectionPanes.forEach(pane => pane.classList.add('hidden'));

        sidebarButtons.forEach(btn => {
            if (btn.getAttribute('data-target') === targetId) {
                btn.classList.add('bg-indigo-600', 'text-white', 'shadow-md', 'shadow-indigo-500/20');
                btn.classList.remove('text-slate-400');
            } else {
                btn.classList.remove('bg-indigo-600', 'text-white', 'shadow-md', 'shadow-indigo-500/20');
                btn.classList.add('text-slate-400');
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
                if (currentPageSubtitle) currentPageSubtitle.textContent = 'Real-time telemetry and operational status';
                break;
            case 'secInventory':
                if (currentPageTitle) currentPageTitle.textContent = 'Inventory Marketplace';
                if (currentPageSubtitle) currentPageSubtitle.textContent = 'Browse products and build purchase orders';
                break;
            case 'secOrders':
                if (currentPageTitle) currentPageTitle.textContent = 'Orders Management';
                if (currentPageSubtitle) currentPageSubtitle.textContent = 'Status breakdown and historical order records';
                break;
            case 'secShipments':
                if (currentPageTitle) currentPageTitle.textContent = 'Shipments & Tracking';
                if (currentPageSubtitle) currentPageSubtitle.textContent = 'Route analytics and freight delivery telemetry';
                break;
            case 'secPayments':
                if (currentPageTitle) currentPageTitle.textContent = 'Payment History';
                if (currentPageSubtitle) currentPageSubtitle.textContent = 'Transaction ledger and verified payment receipts';
                break;
            case 'secProfile':
                if (currentPageTitle) currentPageTitle.textContent = 'Profile Settings';
                if (currentPageSubtitle) currentPageSubtitle.textContent = 'Account parameters and authorization context';
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
            clearSession();
        });
    }

    function renderSummaryMetrics(summary) {
        if (!summary) return;
        const totalOrd = document.getElementById('metricTotalOrders');
        const pendingOrd = document.getElementById('metricPendingOrders');
        const actShip = document.getElementById('metricActiveShipments');
        const delShip = document.getElementById('metricDeliveredShipments');
        const totalSpent = document.getElementById('metricTotalSpent');

        if (totalOrd) totalOrd.textContent = (summary.totalOrders || 0).toLocaleString();
        if (pendingOrd) pendingOrd.textContent = (summary.pendingOrders || 0).toLocaleString();
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
                    datasets: [{
                        label: 'Orders',
                        data: monthlyOrders.map(m => m.count),
                        backgroundColor: '#6366f1',
                        borderRadius: 8,
                        hoverBackgroundColor: '#4f46e5'
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: { legend: { display: false } },
                    scales: {
                        x: { grid: { display: false } },
                        y: { grid: { color: '#f1f5f9' }, beginAtZero: true }
                    }
                }
            });
        }

        const spendingChartElem = document.getElementById('monthlySpendingChart');
        if (spendingChartElem) {
            const spendingCtx = spendingChartElem.getContext('2d');
            if (spendingChartInstance) spendingChartInstance.destroy();

            const gradient = spendingCtx.createLinearGradient(0, 0, 0, 250);
            gradient.addColorStop(0, 'rgba(16, 185, 129, 0.25)');
            gradient.addColorStop(1, 'rgba(16, 185, 129, 0.0)');

            spendingChartInstance = new Chart(spendingCtx, {
                type: 'line',
                data: {
                    labels: monthlySpending.map(m => m.month),
                    datasets: [{
                        label: 'Spent ($)',
                        data: monthlySpending.map(m => m.amount),
                        borderColor: '#10b981',
                        backgroundColor: gradient,
                        fill: true,
                        tension: 0.35,
                        pointBackgroundColor: '#059669',
                        pointBorderColor: '#ffffff',
                        pointHoverRadius: 6,
                        borderWidth: 3
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: { legend: { display: false } },
                    scales: {
                        x: { grid: { display: false } },
                        y: { grid: { color: '#f1f5f9' }, beginAtZero: true }
                    }
                }
            });
        }
    }

    function renderOrderStatusCards(orderStatusList = []) {
        const container = document.getElementById('orderStatusCardsContainer');
        if (!container) return;
        container.innerHTML = '';

        const statusMap = {
            'PENDING': { bg: 'bg-amber-50', text: 'text-amber-700', border: 'border-amber-200' },
            'CONFIRMED': { bg: 'bg-emerald-50', text: 'text-emerald-700', border: 'border-emerald-200' },
            'PROCESSING': { bg: 'bg-blue-50', text: 'text-blue-700', border: 'border-blue-200' },
            'SHIPPED': { bg: 'bg-indigo-50', text: 'text-indigo-700', border: 'border-indigo-200' },
            'DELIVERED': { bg: 'bg-teal-50', text: 'text-teal-700', border: 'border-teal-200' },
            'CANCELLED': { bg: 'bg-rose-50', text: 'text-rose-700', border: 'border-rose-200' }
        };

        orderStatusList.forEach(item => {
            const key = (item.status || '').toUpperCase();
            const config = statusMap[key] || { bg: 'bg-slate-50', text: 'text-slate-700', border: 'border-slate-200' };

            const card = document.createElement('div');
            card.className = `p-4 border ${config.border} ${config.bg} rounded-2xl shadow-sm flex flex-col justify-between`;
            card.innerHTML = `
                <div class="text-[10px] font-bold uppercase tracking-wider ${config.text}">${item.status}</div>
                <div class="text-2xl font-black ${config.text} mt-1">${item.count}</div>
            `;
            container.appendChild(card);
        });
    }

    function renderShipmentStatusCards(shipmentStatusList = []) {
        const container = document.getElementById('shipmentStatusCardsContainer');
        if (!container) return;
        container.innerHTML = '';

        const shipmentMap = {
            'PENDING': { bg: 'bg-amber-50', text: 'text-amber-700', border: 'border-amber-200' },
            'PROCESSING': { bg: 'bg-blue-50', text: 'text-blue-700', border: 'border-blue-200' },
            'SHIPPED': { bg: 'bg-indigo-50', text: 'text-indigo-700', border: 'border-indigo-200' },
            'IN_TRANSIT': { bg: 'bg-violet-50', text: 'text-violet-700', border: 'border-violet-200' },
            'OUT_FOR_DELIVERY': { bg: 'bg-cyan-50', text: 'text-cyan-700', border: 'border-cyan-200' },
            'DELIVERED': { bg: 'bg-emerald-50', text: 'text-emerald-700', border: 'border-emerald-200' },
            'CANCELLED': { bg: 'bg-rose-50', text: 'text-rose-700', border: 'border-rose-200' }
        };

        shipmentStatusList.forEach(item => {
            const key = (item.status || '').toUpperCase();
            const config = shipmentMap[key] || { bg: 'bg-slate-50', text: 'text-slate-700', border: 'border-slate-200' };

            const card = document.createElement('div');
            card.className = `p-3 border ${config.border} ${config.bg} rounded-2xl shadow-sm flex flex-col justify-between`;
            card.innerHTML = `
                <div class="text-[9px] font-bold uppercase tracking-wider ${config.text} truncate">${item.status}</div>
                <div class="text-xl font-black ${config.text} mt-0.5">${item.count}</div>
            `;
            container.appendChild(card);
        });
    }

    function renderShipmentsTable(shipments = []) {
        const tbody = document.getElementById('shipmentsTableBody');
        if (!tbody) return;
        tbody.innerHTML = '';

        if (shipments.length === 0) {
            tbody.innerHTML = `<tr><td colspan="6" class="py-8 text-center text-slate-400">No active shipments logged.</td></tr>`;
            return;
        }

        shipments.forEach(s => {
            const tr = document.createElement('tr');
            tr.className = 'hover:bg-slate-50/80 transition-colors';
            const estDate = s.estimatedDeliveryDate ? new Date(s.estimatedDeliveryDate).toLocaleDateString() : 'N/A';

            tr.innerHTML = `
                <td class="py-3.5 px-6">
                    <span class="font-extrabold text-slate-900 block">${s.shipmentNumber || 'N/A'}</span>
                    <span class="text-[10px] text-slate-400 block">Order: ${s.orderNumber || 'N/A'}</span>
                </td>
                <td class="py-3.5 px-6 font-medium text-slate-700">${s.warehouseName || 'Hub Center'}</td>
                <td class="py-3.5 px-6">
                    <span class="block font-bold text-slate-900">${s.routeName || 'Direct Route'}</span>
                    <span class="text-[10px] text-slate-500 block">${s.routeDistanceKm || 0} km | ${s.routeEstimatedHours || 0} hrs</span>
                </td>
                <td class="py-3.5 px-6">${getStatusBadgeHtml(s.status)}</td>
                <td class="py-3.5 px-6 font-extrabold text-slate-900">${estDate}</td>
                <td class="py-3.5 px-6 text-slate-600 font-medium">${s.latestLocation || 'In Transit'}</td>
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
        if (type === 'error') {
            dashAlert.className = 'p-4 rounded-2xl text-xs font-semibold border flex items-center gap-3 shadow-md bg-rose-50 text-rose-800 border-rose-200';
            dashAlertIcon.className = 'fa-solid fa-triangle-exclamation text-rose-600 text-base';
        } else {
            dashAlert.className = 'p-4 rounded-2xl text-xs font-semibold border flex items-center gap-3 shadow-md bg-emerald-50 text-emerald-800 border-emerald-200';
            dashAlertIcon.className = 'fa-solid fa-circle-check text-emerald-600 text-base';
        }
        dashAlert.classList.remove('hidden');
    }

    function clearAlert() {
        if (dashAlert) dashAlert.classList.add('hidden');
    }

    loadDashboardData();
    switchSection('secDashboard');
});