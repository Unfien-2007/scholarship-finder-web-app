const state = { token: localStorage.getItem('scholarshipToken'), role: localStorage.getItem('scholarshipRole') };
const grid = document.querySelector('#scholarshipGrid');
const feedback = document.querySelector('#feedback');

function setFeedback(message, isError = false) {
    feedback.textContent = message;
    feedback.classList.toggle('error', isError);
}

function headers(json = false) {
    const result = json ? { 'Content-Type': 'application/json' } : {};
    if (state.token) result.Authorization = `Bearer ${state.token}`;
    return result;
}

async function readBody(response) {
    const text = await response.text();
    if (!text.trim()) return null;
    try {
        return JSON.parse(text);
    } catch (error) {
        return { message: text };
    }
}

function messageFor(body, fallback) {
    if (body?.fields) {
        return Object.entries(body.fields).map(([field, message]) => `${field}: ${message}`).join(' ');
    }
    return body?.error || body?.message || fallback;
}

async function loadScholarships() {
    const params = new URLSearchParams();
    const level = document.querySelector('#educationLevel').value;
    const location = document.querySelector('#location').value.trim();
    if (level) params.set('educationLevel', level);
    if (location) params.set('location', location);
    setFeedback('');
    try {
        const response = await fetch(`/api/scholarships?${params}`, { headers: headers() });
        if (!response.ok) throw new Error('Could not load scholarships.');
        const scholarships = await readBody(response) || [];
        document.querySelector('#resultCount').textContent = `${scholarships.length} opportunit${scholarships.length === 1 ? 'y' : 'ies'}`;
        renderScholarships(scholarships);
    } catch (error) {
        setFeedback(error.message, true);
    }
}

async function loadMatches() {
    setFeedback('Finding scholarships for your saved profile...');
    try {
        const response = await fetch('/api/scholarships/matches/me', { headers: headers() });
        const body = await readBody(response);
        if (!response.ok) throw new Error(body?.message || body?.error || 'Could not find profile matches.');
        document.querySelector('#resultCount').textContent = `${body.length} profile match${body.length === 1 ? '' : 'es'}`;
        renderScholarships(body);
        setFeedback('Showing scholarships matched to your profile.');
    } catch (error) { setFeedback(error.message, true); }
}

function renderScholarships(scholarships) {
    if (!scholarships.length) {
        grid.innerHTML = '<div class="empty">No open scholarships match those filters yet.</div>';
        return;
    }
    grid.innerHTML = scholarships.map(scholarship => `
        <article class="scholarship-card">
            <button class="scholarship-link" data-view="${scholarship.id}" type="button" aria-label="View ${escapeHtml(scholarship.title)} requirements">
            <span class="card-meta">${formatLevel(scholarship.educationLevel)} · ${escapeHtml(scholarship.location)}</span>
            <h3>${escapeHtml(scholarship.title)}</h3>
            <p class="provider">${escapeHtml(scholarship.provider)}</p>
            <div class="card-details"><span>${formatMoney(scholarship.amount)}</span><span>Closes ${scholarship.deadline}</span></div>
            </button>
            ${state.role === 'ROLE_STUDENT' ? `<button class="button button-dark card-action" data-apply="${scholarship.id}">Apply now</button>` : ''}
        </article>`).join('');
    grid.querySelectorAll('[data-view]').forEach(button => button.addEventListener('click', () => showDetails(button.dataset.view)));
    grid.querySelectorAll('[data-apply]').forEach(button => button.addEventListener('click', () => apply(button.dataset.apply)));
}

async function showDetails(id) {
    try {
        const response = await fetch(`/api/scholarships/${id}`);
        if (!response.ok) throw new Error('Could not load scholarship details.');
        const scholarship = await readBody(response);
        if (!scholarship) throw new Error('Scholarship details were empty.');
        document.querySelector('#detailMeta').textContent = `${formatLevel(scholarship.educationLevel)} · ${scholarship.location}`;
        document.querySelector('#detailTitle').textContent = scholarship.title;
        document.querySelector('#detailProvider').textContent = scholarship.provider;
        document.querySelector('#detailAmount').textContent = formatMoney(scholarship.amount);
        document.querySelector('#detailDeadline').textContent = `Closes ${scholarship.deadline}`;
        document.querySelector('#detailPeriod').textContent = scholarship.applicationPeriod;
        document.querySelector('#detailBenefits').textContent = scholarship.benefits;
        document.querySelector('#detailRequirements').textContent = scholarship.requirements;
        const applyButton = document.querySelector('#detailApply');
        applyButton.classList.toggle('hidden', state.role !== 'ROLE_STUDENT');
        applyButton.onclick = () => apply(scholarship.id);
        document.querySelector('#detailDialog').classList.remove('hidden');
    } catch (error) { setFeedback(error.message, true); }
}

async function apply(id) {
    try {
        const response = await fetch(`/api/applications/${id}`, { method: 'POST', headers: headers() });
        const body = await readBody(response);
        if (!response.ok) throw new Error(body?.error || body?.message || 'Application could not be submitted.');
        setFeedback('Application submitted. Good luck!');
    } catch (error) { setFeedback(error.message, true); }
}

function formatLevel(level) { return level.replaceAll('_', ' ').toLowerCase().replace(/\b\w/g, letter => letter.toUpperCase()); }
function formatMoney(amount) { return new Intl.NumberFormat('en-PH', { style: 'currency', currency: 'PHP', maximumFractionDigits: 0 }).format(amount); }
function escapeHtml(value) { return String(value).replace(/[&<>'"]/g, character => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;' })[character]); }

function updateSession() {
    const label = document.querySelector('#sessionLabel');
    const loggedIn = Boolean(state.token);
    label.textContent = loggedIn ? `${state.role === 'ROLE_ADMIN' ? 'Administrator' : 'Student'} session` : 'Browsing as guest';
    document.querySelector('#loginButton').classList.toggle('hidden', loggedIn);
    document.querySelector('#logoutButton').classList.toggle('hidden', !loggedIn);
    document.querySelector('#adminPanel').classList.toggle('hidden', state.role !== 'ROLE_ADMIN');
    document.querySelector('#matchButton').classList.toggle('hidden', state.role !== 'ROLE_STUDENT');
    document.querySelector('#profileButton').classList.toggle('hidden', state.role !== 'ROLE_STUDENT');
}

document.querySelector('#searchForm').addEventListener('submit', event => { event.preventDefault(); loadScholarships(); });
document.querySelector('#matchButton').addEventListener('click', loadMatches);
document.querySelector('#loginButton').addEventListener('click', () => document.querySelector('#loginDialog').classList.remove('hidden'));
document.querySelector('#closeDialog').addEventListener('click', () => document.querySelector('#loginDialog').classList.add('hidden'));
document.querySelector('#closeDetail').addEventListener('click', () => document.querySelector('#detailDialog').classList.add('hidden'));
document.querySelector('#signupButton').addEventListener('click', () => { document.querySelector('#loginDialog').classList.add('hidden'); document.querySelector('#signupDialog').classList.remove('hidden'); });
document.querySelector('#closeSignup').addEventListener('click', () => document.querySelector('#signupDialog').classList.add('hidden'));
document.querySelector('#logoutButton').addEventListener('click', () => { localStorage.clear(); state.token = null; state.role = null; updateSession(); loadScholarships(); });
document.querySelector('#loginForm').addEventListener('submit', async event => {
    event.preventDefault();
    const form = new FormData(event.target);
    const errorBox = document.querySelector('#loginError');
    errorBox.textContent = '';
    try {
        const response = await fetch('/api/auth/login', { method: 'POST', headers: headers(true), body: JSON.stringify({ email: form.get('email'), password: form.get('password') }) });
        const body = await readBody(response);
        if (!response.ok) throw new Error(messageFor(body, 'Invalid email or password.'));
        if (!body?.token) throw new Error('Login response did not include a token.');
        state.token = body.token; state.role = body.role;
        localStorage.setItem('scholarshipToken', state.token); localStorage.setItem('scholarshipRole', state.role);
        document.querySelector('#loginDialog').classList.add('hidden'); updateSession();
        loadScholarships();
    } catch (error) { errorBox.textContent = error.message; }
});
document.querySelector('#signupForm').addEventListener('submit', async event => {
    event.preventDefault();
    const form = new FormData(event.target);
    const errorBox = document.querySelector('#signupError');
    errorBox.textContent = '';
    const profile = Object.fromEntries(form);
    profile.gwa = Number(profile.gwa);
    try {
        const response = await fetch('/api/auth/signup', { method: 'POST', headers: headers(true), body: JSON.stringify(profile) });
        const body = await readBody(response);
        if (!response.ok) throw new Error(messageFor(body, `Signup failed (HTTP ${response.status}). Restart the server and try again.`));
        if (!body?.token) throw new Error('Signup response did not include a token.');
        state.token = body.token; state.role = body.role;
        localStorage.setItem('scholarshipToken', state.token); localStorage.setItem('scholarshipRole', state.role);
        document.querySelector('#signupDialog').classList.add('hidden'); updateSession(); loadMatches();
    } catch (error) { errorBox.textContent = error.message; }
});
document.querySelector('#adminForm').addEventListener('submit', async event => {
    event.preventDefault();
    const values = Object.fromEntries(new FormData(event.target));
    values.amount = Number(values.amount); values.minimumGwa = values.minimumGwa ? Number(values.minimumGwa) : null;
    const response = await fetch('/api/admin/scholarships', { method: 'POST', headers: headers(true), body: JSON.stringify(values) });
    if (!response.ok) { setFeedback('The scholarship could not be published.', true); return; }
    event.target.reset(); setFeedback('Scholarship published successfully.'); loadScholarships();
});

updateSession();
loadScholarships();
