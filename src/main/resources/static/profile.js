const token = localStorage.getItem('scholarshipToken');
const role = localStorage.getItem('scholarshipRole');
const state = { profile: null, matches: [], applications: [], tab: 'matches' };

function headers(json = false) {
    const result = json ? { 'Content-Type': 'application/json' } : {};
    if (token) result.Authorization = `Bearer ${token}`;
    return result;
}

async function readBody(response) {
    const text = await response.text();
    if (!text.trim()) return null;
    try { return JSON.parse(text); } catch (error) { return { message: text }; }
}

function messageFor(body, fallback) {
    if (body?.fields) return Object.entries(body.fields).map(([field, message]) => `${field}: ${message}`).join(' ');
    return body?.error || body?.message || fallback;
}

async function request(url, options = {}) {
    const response = await fetch(url, { ...options, headers: { ...headers(Boolean(options.body)), ...(options.headers || {}) } });
    const body = await readBody(response);
    if (!response.ok) throw new Error(messageFor(body, `Request failed (HTTP ${response.status}).`));
    return body;
}

function formatLevel(level) { return level.replaceAll('_', ' ').toLowerCase().replace(/\b\w/g, letter => letter.toUpperCase()); }
function formatMoney(amount) { return new Intl.NumberFormat('en-PH', { style: 'currency', currency: 'PHP', maximumFractionDigits: 0 }).format(amount); }
function escapeHtml(value) { return String(value).replace(/[&<>'"]/g, character => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;' })[character]); }
function setFeedback(selector, message, error = false) { const element = document.querySelector(selector); element.textContent = message; element.classList.toggle('error', error); }

async function loadDashboard() {
    if (!token || role !== 'ROLE_STUDENT') {
        document.querySelector('#profileGate').classList.remove('hidden');
        return;
    }
    document.querySelector('#profileContent').classList.remove('hidden');
    try {
        const [profile, matches, applications] = await Promise.all([
            request('/api/me'), request('/api/scholarships/matches/me'), request('/api/applications/me')
        ]);
        state.profile = profile; state.matches = matches || []; state.applications = applications || [];
        fillProfile(profile); renderCurrentTab();
    } catch (error) { setFeedback('#applicationFeedback', error.message, true); }
}

function fillProfile(profile) {
    const form = document.querySelector('#profileForm');
    form.elements.fullName.value = profile.fullName || '';
    form.elements.email.value = profile.email || '';
    form.elements.gwa.value = profile.gwa ?? '';
    form.elements.educationLevel.value = profile.educationLevel || 'UNDERGRADUATE';
    form.elements.location.value = profile.location || '';
    form.elements.incomeBracket.value = profile.incomeBracket || 'LOW';
    document.querySelector('#welcomeTitle').textContent = profile.fullName ? `Welcome, ${profile.fullName}` : 'My profile';
}

function applicationFor(scholarshipId) { return state.applications.find(application => application.scholarshipId === scholarshipId); }

function renderCurrentTab() {
    document.querySelector('#matchCount').textContent = state.matches.length;
    const list = document.querySelector('#scholarshipList');
    let items;
    if (state.tab === 'matches') {
        items = state.matches.map(scholarship => {
            const application = applicationFor(scholarship.id);
            return renderMatch(scholarship, application);
        });
    } else if (state.tab === 'pending') {
        items = state.applications.filter(application => application.status === 'PENDING').map(renderApplication);
    } else {
        items = state.applications.filter(application => ['APPROVED', 'REJECTED', 'RESULT_RECEIVED'].includes(application.status)).map(renderApplication);
    }
    list.innerHTML = items.length ? items.join('') : `<div class="empty">No scholarships in this view yet.</div>`;
    list.querySelectorAll('[data-apply]').forEach(button => button.addEventListener('click', () => apply(button.dataset.apply)));
    list.querySelectorAll('[data-status-action]').forEach(button => button.addEventListener('click', () => updateStatus(button.dataset.statusAction, button.dataset.status)));
}

function renderMatch(scholarship, application) {
    const action = application?.status === 'WITHDRAWN'
        ? `<button class="button button-dark" data-apply="${scholarship.id}" type="button">Apply again</button>`
        : application
        ? `<span class="status status-${application.status.toLowerCase()}">${formatStatus(application.status)}</span>`
        : `<button class="button button-dark" data-apply="${scholarship.id}" type="button">Apply</button>`;
    return `<article class="profile-scholarship">
        <div><span class="card-meta">${formatLevel(scholarship.educationLevel)} · ${escapeHtml(scholarship.location)}</span><h3>${escapeHtml(scholarship.title)}</h3><p>${escapeHtml(scholarship.provider)}</p><div class="profile-benefits">${escapeHtml(scholarship.benefits)}</div></div>
        <div class="profile-row"><span>${formatMoney(scholarship.amount)} · Closes ${scholarship.deadline}</span>${action}</div>
    </article>`;
}

function renderApplication(application) {
    const actions = application.status === 'PENDING'
        ? `<div class="application-actions"><button class="button button-quiet" data-status-action="${application.id}" data-status="WITHDRAWN" type="button">Withdraw</button><button class="button button-accent" data-status-action="${application.id}" data-status="RESULT_RECEIVED" type="button">Record result received</button></div>`
        : '';
    return `<article class="profile-scholarship"><div><span class="card-meta">Application status</span><h3>${escapeHtml(application.scholarshipTitle)}</h3><p>Applied ${new Date(application.appliedAt).toLocaleDateString('en-PH')}</p></div><div class="application-state"><span class="status status-${application.status.toLowerCase()}">${formatStatus(application.status)}</span>${actions}</div></article>`;
}

function formatStatus(status) {
    if (status === 'APPROVED') return 'Approved';
    if (status === 'REJECTED') return 'Not selected';
    if (status === 'WITHDRAWN') return 'Withdrawn';
    if (status === 'RESULT_RECEIVED') return 'Result received';
    return 'Pending review';
}

async function apply(id) {
    try {
        await request(`/api/applications/${id}`, { method: 'POST' });
        setFeedback('#applicationFeedback', 'Application submitted and moved to Pending.');
        const applications = await request('/api/applications/me');
        state.applications = applications || []; renderCurrentTab();
    } catch (error) { setFeedback('#applicationFeedback', error.message, true); }
}

async function updateStatus(applicationId, status) {
    try {
        await request(`/api/applications/${applicationId}/status`, { method: 'PATCH', body: JSON.stringify({ status }) });
        state.applications = await request('/api/applications/me');
        renderCurrentTab();
        setFeedback('#applicationFeedback', status === 'WITHDRAWN' ? 'Application withdrawn.' : 'Result recorded.');
    } catch (error) { setFeedback('#applicationFeedback', error.message, true); }
}

document.querySelectorAll('[data-tab]').forEach(tab => tab.addEventListener('click', () => {
    document.querySelectorAll('[data-tab]').forEach(item => item.classList.remove('active'));
    tab.classList.add('active'); state.tab = tab.dataset.tab; renderCurrentTab();
}));
document.querySelector('#profileForm').addEventListener('submit', async event => {
    event.preventDefault();
    const form = new FormData(event.target); const profile = Object.fromEntries(form); profile.gwa = Number(profile.gwa);
    try {
        const updated = await request('/api/me', { method: 'PUT', body: JSON.stringify(profile) });
        state.profile = updated; fillProfile(updated);
        state.matches = await request('/api/scholarships/matches/me');
        renderCurrentTab(); setFeedback('#profileFeedback', 'Profile saved. Matches updated.');
    } catch (error) { setFeedback('#profileFeedback', error.message, true); }
});
document.querySelector('#logoutButton').addEventListener('click', () => { localStorage.clear(); window.location.href = '/'; });

loadDashboard();
