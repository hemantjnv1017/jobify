const historyBody = document.getElementById("history-body");
const statusEl = document.getElementById("history-status");
const refreshBtn = document.getElementById("refresh-btn");

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;");
}

function formatDateTime(value) {
    if (!value) {
        return "—";
    }
    return new Date(value).toLocaleString(undefined, {
        dateStyle: "medium",
        timeStyle: "short"
    });
}

function showStatus(kind, message) {
    statusEl.hidden = false;
    statusEl.className = `status ${kind}`;
    statusEl.textContent = message;
}

function clearStatus() {
    statusEl.hidden = true;
    statusEl.textContent = "";
}

function statusClass(status) {
    return status === "SENT" ? "badge ok" : "badge err";
}

function renderHistory(items) {
    if (!items.length) {
        historyBody.innerHTML = `
            <tr class="placeholder-row">
                <td colspan="5">No mail sent yet. <a href="/">Send your first application</a>.</td>
            </tr>
        `;
        return;
    }

    historyBody.innerHTML = items.map((item) => {
        const cc = item.cc?.length ? item.cc.join(", ") : "—";
        const error = item.errorMessage
            ? `<span class="error-note" title="${escapeHtml(item.errorMessage)}">${escapeHtml(item.errorMessage)}</span>`
            : "";
        return `
            <tr>
                <td>
                    <time datetime="${escapeHtml(item.createdTime || "")}">${formatDateTime(item.createdTime)}</time>
                    ${item.sentTime ? `<span class="sub">Sent ${formatDateTime(item.sentTime)}</span>` : ""}
                </td>
                <td>
                    <strong>${escapeHtml(item.hrName)}</strong>
                    <span class="sub">${escapeHtml(item.hrEmail)}</span>
                </td>
                <td>${escapeHtml(item.role)}</td>
                <td>
                    <span class="${statusClass(item.status)}">${escapeHtml(item.status)}</span>
                    ${error}
                </td>
                <td>${escapeHtml(cc)}</td>
            </tr>
        `;
    }).join("");
}

async function loadHistory() {
    refreshBtn.disabled = true;
    clearStatus();

    try {
        const response = await fetch("/api/hr/mail/history");
        const payload = await response.json().catch(() => []);
        if (!response.ok) {
            throw new Error(payload.error || "Could not load mail history.");
        }
        renderHistory(Array.isArray(payload) ? payload : []);
    } catch (error) {
        historyBody.innerHTML = `
            <tr class="placeholder-row">
                <td colspan="5">Could not load history.</td>
            </tr>
        `;
        showStatus("err", error.message);
    } finally {
        refreshBtn.disabled = false;
    }
}

refreshBtn.addEventListener("click", loadHistory);
loadHistory();
