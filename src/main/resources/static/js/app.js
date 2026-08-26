const form = document.getElementById("mail-form");
const hrName = document.getElementById("hrName");
const email = document.getElementById("email");
const role = document.getElementById("role");
const ccInput = document.getElementById("cc-input");
const ccAdd = document.getElementById("cc-add");
const ccList = document.getElementById("cc-list");
const statusEl = document.getElementById("form-status");
const sendBtn = document.getElementById("send-btn");
const subjectPreview = document.getElementById("subject-preview");

const cc = [];

function renderSubject() {
    const value = role.value.trim() || "your role";
    subjectPreview.innerHTML =
        `Application for <em>${escapeHtml(value)}</em> — Java &amp; Spring Boot | FinTech &amp; Payments | Immediate Joiner`;
}

function escapeHtml(value) {
    return value
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;");
}

function renderCc() {
    ccList.hidden = cc.length === 0;
    ccList.innerHTML = "";
    cc.forEach((address, index) => {
        const item = document.createElement("li");
        item.textContent = address;
        const remove = document.createElement("button");
        remove.type = "button";
        remove.setAttribute("aria-label", `Remove ${address}`);
        remove.textContent = "×";
        remove.addEventListener("click", () => {
            cc.splice(index, 1);
            renderCc();
        });
        item.appendChild(remove);
        ccList.appendChild(item);
    });
}

function addCc() {
    const address = ccInput.value.trim();
    if (!address) {
        return;
    }
    if (!ccInput.checkValidity()) {
        ccInput.reportValidity();
        return;
    }
    if (!cc.includes(address)) {
        cc.push(address);
        renderCc();
    }
    ccInput.value = "";
    ccInput.focus();
}

function showStatus(kind, message) {
    statusEl.hidden = false;
    statusEl.className = `status ${kind}`;
    statusEl.textContent = message;
}

role.addEventListener("input", renderSubject);
ccAdd.addEventListener("click", addCc);
ccInput.addEventListener("keydown", (event) => {
    if (event.key === "Enter") {
        event.preventDefault();
        addCc();
    }
});

form.addEventListener("submit", async (event) => {
    event.preventDefault();
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    sendBtn.disabled = true;
    showStatus("ok", "Sending…");

    try {
        const response = await fetch("/api/hr/mail", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                email: email.value.trim(),
                hrName: hrName.value.trim(),
                role: role.value.trim(),
                cc
            })
        });
        const payload = await response.json().catch(() => ({}));
        if (!response.ok) {
            throw new Error(payload.error || "Could not send mail.");
        }
        showStatus("ok", `Sent to ${payload.to}${cc.length ? ` (CC: ${cc.join(", ")})` : ""}.`);
    } catch (error) {
        showStatus("err", error.message);
    } finally {
        sendBtn.disabled = false;
    }
});

renderSubject();
renderCc();
