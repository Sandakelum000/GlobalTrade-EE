document.addEventListener('DOMContentLoaded', () => {
    // Exact context path matching AuthResource mappings (/globaltrade/logistics/api/auth/login)
    const API_BASE_URL = '/globaltrade/logistics/api';

    // DOM Elements
    const loginForm = document.getElementById('loginForm');
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    const togglePasswordBtn = document.getElementById('togglePasswordBtn');
    const toggleIcon = document.getElementById('toggleIcon');

    const submitBtn = document.getElementById('submitBtn');
    const btnText = document.getElementById('btnText');
    const btnIcon = document.getElementById('btnIcon');
    const btnSpinner = document.getElementById('btnSpinner');

    const alertContainer = document.getElementById('alertContainer');
    const alertIcon = document.getElementById('alertIcon');
    const alertMessage = document.getElementById('alertMessage');

    // ==========================================
    // 1. Password Visibility Toggle
    // ==========================================
    togglePasswordBtn.addEventListener('click', () => {
        const isPassword = passwordInput.getAttribute('type') === 'password';
        passwordInput.setAttribute('type', isPassword ? 'text' : 'password');
        toggleIcon.className = isPassword ? 'fa-regular fa-eye-slash' : 'fa-regular fa-eye';
    });

    // ==========================================
    // 2. Token Management & API Interceptor
    // ==========================================

    /**
     * Calls POST /auth/refresh using RefreshRequest DTO payload {"refreshToken": "..."}
     * Maps to AuthResource.refresh(@Valid @NotNull RefreshRequest request)
     */
    async function refreshAccessToken() {
        const refreshToken = localStorage.getItem('refresh_token');

        if (!refreshToken) {
            throw new Error('No refresh token available');
        }

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

        // AuthResource response structure: { access, refresh, username, roles }
        localStorage.setItem('access_token', data.access);
        localStorage.setItem('refresh_token', data.refresh);
        localStorage.setItem('username', data.username);
        localStorage.setItem('roles', JSON.stringify(data.roles));

        return data.access;
    }

    /**
     * Custom fetch wrapper for authenticated endpoints.
     * Automatically attaches Authorization: Bearer <token> and handles 401 recovery via refresh.
     */
    async function authenticatedFetch(url, options = {}) {
        options.headers = options.headers || {};
        let accessToken = localStorage.getItem('access_token');

        if (accessToken) {
            options.headers['Authorization'] = `Bearer ${accessToken}`;
        }

        let response = await fetch(url, options);

        // If AuthMechanism returns 401 (Unauthorized/Expired JWT)
        if (response.status === 401) {
            try {
                const newAccessToken = await refreshAccessToken();

                // Retry original request with freshly acquired access token
                options.headers['Authorization'] = `Bearer ${newAccessToken}`;
                response = await fetch(url, options);
            } catch (error) {
                console.warn('Session refresh failed:', error.message);
            }
        }

        return response;
    }

    function clearSession() {
        localStorage.removeItem('access_token');
        localStorage.removeItem('refresh_token');
        localStorage.removeItem('username');
        localStorage.removeItem('roles');
    }

    // ==========================================
    // 3. Automated Active Session Check
    // ==========================================
    async function checkExistingSession() {
        const accessToken = localStorage.getItem('access_token');
        const refreshToken = localStorage.getItem('refresh_token');

        if (accessToken || refreshToken) {
            try {
                const response = await authenticatedFetch(`${API_BASE_URL}/auth/me`);

                if (response.ok) {
                    const user = localStorage.getItem('username') || 'User';
                    showAlert(`Active session detected for ${user}. Redirecting...`, 'success');
                    setTimeout(() => {
                        window.location.href = '/globaltrade/logistics/dashboard.html';
                    }, 800);
                }
            } catch (err) {
                clearSession();
            }
        }
    }

    // Run session check on page load
    checkExistingSession();

    // ==========================================
    // 4. Form Submission & POST /auth/login
    // ==========================================
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const username = usernameInput.value.trim();
        const password = passwordInput.value.trim();

        if (!username || !password) {
            showAlert('Username and password are required.', 'error');
            return;
        }

        setLoading(true);

        try {
            // Direct POST call matching AuthResource @Path("/login")
            const response = await fetch(`${API_BASE_URL}/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, password })
            });

            const data = await response.json();

            if (response.ok) {
                // Backend maps: { "access": ..., "refreshToken": ..., "username": ..., "roles": ... }
                localStorage.setItem('access_token', data.access);
                localStorage.setItem('refresh_token', data.refreshToken);
                localStorage.setItem('username', data.username);
                localStorage.setItem('roles', JSON.stringify(data.roles));

                showAlert(`Authentication verified. Welcome back, ${data.username}!`, 'success');

                setTimeout(() => {
                    window.location.href = '/globaltrade/logistics/dashboard.html';
                }, 1000);

            } else {
                const errorMessage = data.error || 'Authentication failed. Please verify credentials.';
                showAlert(errorMessage, 'error');
            }
        } catch (error) {
            showAlert('Unable to reach GlobalTrade Auth API. Check server connection.', 'error');
            console.error('Login Error:', error);
        } finally {
            setLoading(false);
        }
    });

    // Expose authenticatedFetch globally for other modules in your project
    window.authenticatedFetch = authenticatedFetch;

    // ==========================================
    // 5. UI Helpers (Clean Alert Logic & Autofill Prevention)
    // ==========================================
    function setLoading(isLoading) {
        submitBtn.disabled = isLoading;
        if (isLoading) {
            btnText.textContent = 'Authenticating...';
            btnIcon.classList.add('hidden');
            btnSpinner.classList.remove('hidden');
        } else {
            btnText.textContent = 'Sign In';
            btnIcon.classList.remove('hidden');
            btnSpinner.classList.add('hidden');
        }
    }

    function showAlert(message, type = 'error') {
        alertMessage.textContent = message;

        // Base class reset without trailing space issues
        alertContainer.className = 'mb-6 p-4 rounded-xl text-xs font-mono border flex items-center gap-3 transition-all duration-300';

        if (type === 'error') {
            alertContainer.classList.add('bg-gray-50', 'border-red-200', 'text-red-600');
            alertIcon.className = 'fa-solid fa-triangle-exclamation text-red-500';
        } else if (type === 'success') {
            alertContainer.classList.add('bg-gray-50', 'border-emerald-200', 'text-emerald-700');
            alertIcon.className = 'fa-solid fa-circle-check text-emerald-500';
        }

        // Explicitly unhide the element
        alertContainer.classList.remove('hidden');
    }
});