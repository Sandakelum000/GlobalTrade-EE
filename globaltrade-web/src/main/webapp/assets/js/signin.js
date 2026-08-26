document.addEventListener('DOMContentLoaded', () => {
    // Dynamic Context Path Resolution
    const getContextPath = () => {
        const path = window.location.pathname;
        const secondSlash = path.indexOf('/', 1);
        return (secondSlash !== -1 ? path.substring(0, secondSlash) : '') + '/logistics/api';
    };

    const API_BASE_URL = getContextPath();

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

    // Password Visibility Toggle
    if (togglePasswordBtn) {
        togglePasswordBtn.addEventListener('click', () => {
            const isPassword = passwordInput.getAttribute('type') === 'password';
            passwordInput.setAttribute('type', isPassword ? 'text' : 'password');
            toggleIcon.className = isPassword ? 'fa-regular fa-eye-slash' : 'fa-regular fa-eye';
        });
    }

    // Role-based Navigation Helper
    function redirectUserBasedOnRole(roles) {
        let rolesList = [];
        if (Array.isArray(roles)) {
            rolesList = roles;
        } else if (typeof roles === 'string') {
            try {
                rolesList = JSON.parse(roles);
            } catch (e) {
                rolesList = [roles];
            }
        }

        const isAdmin = rolesList.some(r => {
            const roleStr = typeof r === 'string' ? r : r.roleType || r.name || '';
            return roleStr.toUpperCase() === 'ADMIN';
        });

        if (isAdmin) {
            window.location.href = 'admin.html';
        } else {
            window.location.href = 'dashboard.html';
        }
    }

    // Fixed Refresh Access Token
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

        // Handle both property names (refresh or refreshToken) safely
        const newAccessToken = data.access || data.accessToken;
        const newRefreshToken = data.refresh || data.refreshToken || refreshToken;

        localStorage.setItem('access_token', newAccessToken);
        localStorage.setItem('refresh_token', newRefreshToken);
        if (data.username) localStorage.setItem('username', data.username);
        if (data.roles) localStorage.setItem('roles', JSON.stringify(data.roles));

        return newAccessToken;
    }

    // Authenticated Fetch Wrapper
    async function authenticatedFetch(url, options = {}) {
        options.headers = options.headers || {};
        let accessToken = localStorage.getItem('access_token');

        if (accessToken) {
            options.headers['Authorization'] = `Bearer ${accessToken}`;
        }

        let response = await fetch(url, options);

        if (response.status === 401) {
            try {
                const newAccessToken = await refreshAccessToken();
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

    // Automated Active Session Check
    async function checkExistingSession() {
        const accessToken = localStorage.getItem('access_token');
        const refreshToken = localStorage.getItem('refresh_token');

        if (accessToken || refreshToken) {
            try {
                const response = await authenticatedFetch(`${API_BASE_URL}/auth/me`);
                if (response.ok) {
                    const user = localStorage.getItem('username') || 'User';
                    const roles = localStorage.getItem('roles');
                    showAlert(`Active session detected for ${user}. Redirecting...`, 'success');
                    setTimeout(() => {
                        redirectUserBasedOnRole(roles);
                    }, 800);
                }
            } catch (err) {
                clearSession();
            }
        }
    }

    checkExistingSession();

    // Login Form Submit Handler
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
            const response = await fetch(`${API_BASE_URL}/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, password })
            });

            const data = await response.json();

            if (response.ok) {
                const accessToken = data.access || data.accessToken;
                const refreshToken = data.refresh || data.refreshToken;

                localStorage.setItem('access_token', accessToken);
                localStorage.setItem('refresh_token', refreshToken);
                localStorage.setItem('username', data.username);
                localStorage.setItem('roles', JSON.stringify(data.roles));

                showAlert(`Authentication verified. Welcome back, ${data.username}!`, 'success');

                setTimeout(() => {
                    redirectUserBasedOnRole(data.roles);
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

    window.authenticatedFetch = authenticatedFetch;

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
        alertContainer.className = 'mb-6 p-4 rounded-xl text-xs font-mono border flex items-center gap-3 transition-all duration-300';

        if (type === 'error') {
            alertContainer.classList.add('bg-zinc-50', 'border-red-200', 'text-red-600');
            alertIcon.className = 'fa-solid fa-triangle-exclamation text-red-500';
        } else if (type === 'success') {
            alertContainer.classList.add('bg-zinc-50', 'border-emerald-200', 'text-emerald-700');
            alertIcon.className = 'fa-solid fa-circle-check text-emerald-500';
        }

        alertContainer.classList.remove('hidden');
    }
});