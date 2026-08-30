document.addEventListener('DOMContentLoaded', () => {
    // Dynamic Context Path Resolver
    const getContextPath = () => {
        const path = window.location.pathname;
        const secondSlash = path.indexOf('/', 1);
        return (secondSlash !== -1 ? path.substring(0, secondSlash) : '') + '/logistics/api';
    };

    const API_BASE_URL = getContextPath();

    // DOM Elements - Navigation & Session
    const navAuthContainer = document.getElementById('navAuthContainer');
    const heroRegisterBtn = document.getElementById('heroRegisterBtn');
    const registerNavBtn = document.getElementById('registerNavBtn');

    // DOM Elements - Modal & Tabs
    const registerModal = document.getElementById('registerModal');
    const closeModalBtn = document.getElementById('closeModalBtn');
    const tabCustomerBtn = document.getElementById('tabCustomerBtn');
    const tabVendorBtn = document.getElementById('tabVendorBtn');

    // DOM Elements - Form & Inputs
    const registerForm = document.getElementById('registerForm');
    const customerTypeField = document.getElementById('customerTypeField');
    const countrySelect = document.getElementById('countrySelect');
    const companySelect = document.getElementById('companyId');

    const regSubmitBtn = document.getElementById('regSubmitBtn');
    const regBtnText = document.getElementById('regBtnText');
    const regBtnSpinner = document.getElementById('regBtnSpinner');

    const modalAlert = document.getElementById('modalAlert');
    const modalAlertIcon = document.getElementById('modalAlertIcon');
    const modalAlertMessage = document.getElementById('modalAlertMessage');

    const validationErrorSummary = document.getElementById('validationErrorSummary');
    const validationErrorList = document.getElementById('validationErrorList');

    // State Variable ('CUSTOMER' | 'VENDOR')
    let activeTab = 'CUSTOMER';

    // ==========================================
    // 1. Session & Navigation Setup
    // ==========================================
    function checkActiveSession() {
        const accessToken = localStorage.getItem('access_token');
        const username = localStorage.getItem('username');

        if (accessToken && username) {
            // Replace navigation items with direct Dashboard entry point
            navAuthContainer.innerHTML = `
                <a href="dashboard.html" class="bg-slate-900 hover:bg-slate-800 text-white text-xs font-mono font-semibold px-4 py-2 rounded-xl transition-all shadow-sm flex items-center gap-2">
                    <i class="fa-solid fa-gauge-high text-slate-300"></i>
                    <span>Dashboard (${username})</span>
                </a>
            `;

            if (heroRegisterBtn) {
                heroRegisterBtn.innerHTML = `
                    <span>Go to Dashboard</span>
                    <i class="fa-solid fa-arrow-right text-xs"></i>
                `;
                heroRegisterBtn.onclick = () => { window.location.href = 'customer.html'; };
            }
        } else {
            if (heroRegisterBtn) {
                heroRegisterBtn.onclick = openModal;
            }
        }
    }

    // ==========================================
    // 2. Data Fetching (Countries & Companies)
    // ==========================================
    async function loadPartnershipCountries() {
        const countriesLoading = document.getElementById('countriesLoading');
        const countriesContainer = document.getElementById('countriesContainer');

        try {
            const response = await fetch(`${API_BASE_URL}/data/countries`);
            if (!response.ok) throw new Error('Failed to load countries');

            const countries = await response.json(); // List<CountryResponse>

            // Render on landing page
            countriesContainer.innerHTML = '';
            countrySelect.innerHTML = '<option value="">-- Choose Country --</option>';

            countries.forEach(c => {
                // Populate Landing Page Network Badges
                const badge = document.createElement('span');
                badge.className = 'px-3 py-1.5 bg-white border border-slate-200 text-slate-700 font-mono text-xs rounded-xl shadow-sm flex items-center gap-2';
                badge.innerHTML = `<i class="fa-solid fa-location-dot text-slate-400"></i>${c.name}`;
                countriesContainer.appendChild(badge);

                // Populate Modal Select
                const option = document.createElement('option');
                option.value = c.id;
                option.textContent = c.name;
                countrySelect.appendChild(option);
            });

            countriesLoading.classList.add('hidden');
            countriesContainer.classList.remove('hidden');

        } catch (err) {
            console.error('Error loading countries:', err);
            countriesLoading.textContent = 'Unable to load partner country data.';
        }
    }

    countrySelect.addEventListener('change', async () => {
        const countryId = countrySelect.value;
        companySelect.innerHTML = '<option value="">Loading companies...</option>';
        companySelect.disabled = true;

        if (!countryId) {
            companySelect.innerHTML = '<option value="">-- Select Country First --</option>';
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/data/${countryId}/companies`);
            if (!response.ok) throw new Error('Failed to load companies');

            const companies = await response.json(); // List<CompanyResponse>
            companySelect.innerHTML = '<option value="">-- Select Company --</option>';

            if (companies.length === 0) {
                companySelect.innerHTML = '<option value="">No companies registered in country</option>';
                return;
            }

            companies.forEach(comp => {
                const opt = document.createElement('option');
                opt.value = comp.id;
                opt.textContent = comp.name;
                companySelect.appendChild(opt);
            });

            companySelect.disabled = false;
        } catch (err) {
            console.error('Error loading companies:', err);
            companySelect.innerHTML = '<option value="">Error fetching companies</option>';
        }
    });

    // ==========================================
    // 3. Modal & Tab Controller
    // ==========================================
    function openModal() {
        clearErrors();
        registerModal.classList.remove('hidden');
    }

    function closeModal() {
        registerModal.classList.add('hidden');
    }

    function switchTab(targetTab) {
        activeTab = targetTab;
        clearErrors();

        if (activeTab === 'CUSTOMER') {
            tabCustomerBtn.className = 'flex-1 py-2 text-xs font-semibold rounded-lg transition-all bg-white text-slate-900 shadow-sm';
            tabVendorBtn.className = 'flex-1 py-2 text-xs font-semibold rounded-lg transition-all text-slate-600 hover:text-slate-900';
            customerTypeField.classList.remove('hidden');
            regBtnText.textContent = 'Complete Customer Registration';
        } else {
            tabVendorBtn.className = 'flex-1 py-2 text-xs font-semibold rounded-lg transition-all bg-white text-slate-900 shadow-sm';
            tabCustomerBtn.className = 'flex-1 py-2 text-xs font-semibold rounded-lg transition-all text-slate-600 hover:text-slate-900';
            customerTypeField.classList.add('hidden');
            regBtnText.textContent = 'Complete Vendor Registration';
        }
    }

    if (registerNavBtn) registerNavBtn.addEventListener('click', openModal);
    if (closeModalBtn) closeModalBtn.addEventListener('click', closeModal);
    tabCustomerBtn.addEventListener('click', () => switchTab('CUSTOMER'));
    tabVendorBtn.addEventListener('click', () => switchTab('VENDOR'));

    // ==========================================
    // 4. Form Submission & Exception Handling
    // ==========================================
    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        clearErrors();

        const endpoint = activeTab === 'CUSTOMER' ? `${API_BASE_URL}/customer/register` : `${API_BASE_URL}/vendor/register`;

        // Payload Construction matching DTO structures
        let payload = {};

        if (activeTab === 'CUSTOMER') {
            payload = {
                firstName: document.getElementById('firstName').value.trim(),
                lastName: document.getElementById('lastName').value.trim(),
                email: document.getElementById('email').value.trim(),
                mobile1: document.getElementById('mobile1').value.trim(),
                mobile2: document.getElementById('mobile2').value.trim() || null,
                customerType: document.getElementById('customerType').value,
                username: document.getElementById('regUsername').value.trim(),
                password: document.getElementById('regPassword').value,
                companyId: companySelect.value || null
            };
        } else {
            // Vendor Registration Request Mapping
            payload = {
                contactFirstName: document.getElementById('firstName').value.trim(),
                contactLastName: document.getElementById('lastName').value.trim(),
                email: document.getElementById('email').value.trim(),
                mobile1: document.getElementById('mobile1').value.trim(),
                mobile2: document.getElementById('mobile2').value.trim() || null,
                username: document.getElementById('regUsername').value.trim(),
                password: document.getElementById('regPassword').value,
                companyId: companySelect.value || null
            };
        }

        setLoading(true);

        try {
            const response = await fetch(endpoint, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const data = await response.json();

            if (response.status === 201) {
                showModalAlert(`Registration successful for ${data.username || 'partner'}. Redirecting to sign in...`, 'success');
                setTimeout(() => {
                    window.location.href = 'signin.html';
                }, 1500);
            } else if (response.status === 400 && Array.isArray(data)) {
                // Parse JAX-RS ConstraintViolationExceptionMapper list: ValidationErrorResponse
                showValidationErrors(data);
            } else {
                showModalAlert(data.error || 'Registration failed. Check inputs.', 'error');
            }

        } catch (err) {
            console.error('Registration API Error:', err);
            showModalAlert('Unable to reach server. Please try again.', 'error');
        } finally {
            setLoading(false);
        }
    });

    // ==========================================
    // 5. UI Helpers
    // ==========================================
    function setLoading(isLoading) {
        regSubmitBtn.disabled = isLoading;
        if (isLoading) {
            regBtnText.textContent = 'Processing...';
            regBtnSpinner.classList.remove('hidden');
        } else {
            regBtnText.textContent = activeTab === 'CUSTOMER' ? 'Complete Customer Registration' : 'Complete Vendor Registration';
            regBtnSpinner.classList.add('hidden');
        }
    }

    function showModalAlert(message, type = 'error') {
        modalAlertMessage.textContent = message;
        modalAlert.className = 'mb-4 p-4 rounded-xl text-xs font-mono border flex items-center gap-3';

        if (type === 'error') {
            modalAlert.classList.add('bg-red-50', 'border-red-200', 'text-red-700');
            modalAlertIcon.className = 'fa-solid fa-triangle-exclamation text-red-500';
        } else if (type === 'success') {
            modalAlert.classList.add('bg-emerald-50', 'border-emerald-200', 'text-emerald-700');
            modalAlertIcon.className = 'fa-solid fa-circle-check text-emerald-500';
        }

        modalAlert.classList.remove('hidden');
    }

    function showValidationErrors(errorsList) {
        validationErrorList.innerHTML = '';
        errorsList.forEach(err => {
            const li = document.createElement('li');
            li.textContent = `[${err.field}]: ${err.detail}`;
            validationErrorList.appendChild(li);
        });
        validationErrorSummary.classList.remove('hidden');
    }

    function clearErrors() {
        modalAlert.classList.add('hidden');
        validationErrorSummary.classList.add('hidden');
        validationErrorList.innerHTML = '';
    }

    // Initialize Page
    checkActiveSession();
    loadPartnershipCountries();
});