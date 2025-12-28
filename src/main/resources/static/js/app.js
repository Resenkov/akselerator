const SmartParkAuth = (() => {
    const tokenKey = "smartpark.jwt";

    const storeToken = (token) => {
        if (token) {
            localStorage.setItem(tokenKey, token);
        }
    };

    const getToken = () => localStorage.getItem(tokenKey);

    const clearToken = () => localStorage.removeItem(tokenKey);

    const showResult = (containerId, contentId, data) => {
        const container = document.getElementById(containerId);
        const content = document.getElementById(contentId);
        if (!container || !content) return;
        container.classList.remove("hidden");
        content.textContent = JSON.stringify(data, null, 2);
    };

    const handleAuthResponse = async (response) => {
        const data = await response.json().catch(() => ({}));
        if (response.ok && data.token) {
            storeToken(data.token);
        }
        return data;
    };

    const bindLogin = () => {
        const form = document.getElementById("login-form");
        if (!form) return;
        form.addEventListener("submit", async (event) => {
            event.preventDefault();
            const payload = Object.fromEntries(new FormData(form));
            const response = await fetch("/api/auth/login", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(payload)
            });
            const data = await handleAuthResponse(response);
            showResult("login-result", "login-response", data);
        });

        const button = document.getElementById("go-dashboard");
        if (button) {
            button.addEventListener("click", () => {
                window.location.href = "/dashboard";
            });
        }
    };

    const bindRegister = () => {
        const form = document.getElementById("register-form");
        if (!form) return;
        form.addEventListener("submit", async (event) => {
            event.preventDefault();
            const payload = Object.fromEntries(new FormData(form));
            const response = await fetch("/api/auth/register", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(payload)
            });
            const data = await handleAuthResponse(response);
            showResult("register-result", "register-response", data);
        });

        const button = document.getElementById("go-dashboard");
        if (button) {
            button.addEventListener("click", () => {
                window.location.href = "/dashboard";
            });
        }
    };

    const bindDashboard = () => {
        const tokenDisplay = document.getElementById("token-display");
        if (tokenDisplay) {
            const token = getToken();
            tokenDisplay.textContent = token ?? "Токен не найден";
        }

        const form = document.getElementById("booking-form");
        if (form) {
            form.addEventListener("submit", async (event) => {
                event.preventDefault();
                const formData = new FormData(form);
                const endpoint = formData.get("endpoint");
                const payloadText = formData.get("payload");
                let payload = {};
                try {
                    payload = payloadText ? JSON.parse(payloadText) : {};
                } catch (error) {
                    showResult("booking-response", "booking-response", {
                        error: "Невалидный JSON",
                        details: error.message
                    });
                    return;
                }

                const response = await fetch(endpoint, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": getToken() ? `Bearer ${getToken()}` : ""
                    },
                    body: JSON.stringify(payload)
                });

                const data = await response.json().catch(() => ({}));
                const result = {
                    status: response.status,
                    ok: response.ok,
                    data
                };
                const block = document.getElementById("booking-response");
                if (block) {
                    block.textContent = JSON.stringify(result, null, 2);
                }
            });
        }

        const logoutButton = document.getElementById("logout");
        if (logoutButton) {
            logoutButton.addEventListener("click", () => {
                clearToken();
                window.location.href = "/login";
            });
        }
    };

    return {
        bindLogin,
        bindRegister,
        bindDashboard
    };
})();

