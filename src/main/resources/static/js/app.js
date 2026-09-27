/**
 * TURFERS - CLIENT APPLICATION JAVASCRIPT
 * Matching the exact simple UI and workflows from reference screenshots
 */

const API_BASE = '/api';

// Global State
let state = {
    user: null,
    token: localStorage.getItem('turf_token') || null,
    authRoleTarget: 'USER', // 'ADMIN' or 'USER'
    authMode: 'LOGIN',      // 'LOGIN' or 'REGISTER'
    currentTurf: null,
    turfs: [],
    selectedSlotsToBook: [], // array of { slotId, time, price, turfId }
    selectedBookingDate: new Date().toISOString().split('T')[0]
};

// Available standard default slots (06:00 to 22:00)
const STANDARD_SLOTS = [
    "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", 
    "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", 
    "18:00", "19:00", "20:00", "21:00", "22:00"
];

// Initialize on load
document.addEventListener('DOMContentLoaded', () => {
    initDates();
    checkExistingSession();
    setupFormListeners();
});

function initDates() {
    const today = new Date().toISOString().split('T')[0];
    const nextWeek = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0];
    
    const adminHistDate = document.getElementById('adminHistoryDatePicker');
    if (adminHistDate) adminHistDate.value = today;

    const schedStart = document.getElementById('schedStartDate');
    if (schedStart) schedStart.value = today;

    const schedEnd = document.getElementById('schedEndDate');
    if (schedEnd) schedEnd.value = nextWeek;

    const userDate = document.getElementById('userBookingDatePicker');
    if (userDate) userDate.value = today;
}

// Session Check
async function checkExistingSession() {
    if (!state.token) {
        showLanding();
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/auth/me`, {
            headers: { 'Authorization': `Bearer ${state.token}` }
        });
        if (res.ok) {
            const user = await res.json();
            state.user = user;
            if (user.role === 'ROLE_OWNER' || user.role === 'ROLE_ADMIN') {
                openAdminPortal();
            } else {
                openUserPortal();
            }
        } else {
            logout();
        }
    } catch (e) {
        logout();
    }
}

// Navigation between landing and portals
function goHome() {
    if (state.user) {
        if (state.user.role === 'ROLE_OWNER' || state.user.role === 'ROLE_ADMIN') {
            showAdminTab('WELCOME');
        } else {
            showUserTab('WELCOME');
        }
    } else {
        showLanding();
    }
}

function showLanding() {
    document.getElementById('landingView').style.display = 'flex';
    document.getElementById('adminPortalView').style.display = 'none';
    document.getElementById('userPortalView').style.display = 'none';
    document.getElementById('topHeader').style.display = 'none';
}

function openAdminPortal() {
    document.getElementById('landingView').style.display = 'none';
    document.getElementById('userPortalView').style.display = 'none';
    document.getElementById('adminPortalView').style.display = 'flex';
    document.getElementById('topHeader').style.display = 'flex';
    document.getElementById('welcomeUserText').textContent = `Welcome, ${state.user ? state.user.name : 'admin'}`;
    showAdminTab('WELCOME');
    loadAdminTurfDetails();
}

function openUserPortal() {
    document.getElementById('landingView').style.display = 'none';
    document.getElementById('adminPortalView').style.display = 'none';
    document.getElementById('userPortalView').style.display = 'flex';
    document.getElementById('topHeader').style.display = 'flex';
    document.getElementById('welcomeUserText').textContent = `Welcome, ${state.user ? state.user.name : 'user'}`;
    showUserTab('WELCOME');
    loadUserProfile();
}

function logout() {
    state.user = null;
    state.token = null;
    localStorage.removeItem('turf_token');
    showLanding();
    showToast('Logged out');
}

// Auth Modal
function openAuthModal(role) {
    state.authRoleTarget = role;
    document.getElementById('authModalTitle').textContent = role === 'ADMIN' ? 'Admin' : 'User';
    
    // Clear inputs so user can type their real email/password
    document.getElementById('simpleLoginUser').value = '';
    document.getElementById('simpleLoginPass').value = '';
    document.getElementById('simpleRegName').value = '';
    document.getElementById('simpleRegEmail').value = '';
    document.getElementById('simpleRegPass').value = '';
    document.getElementById('simpleRegPhone').value = '';

    setAuthMode('LOGIN');
    document.getElementById('authModal').classList.add('active');
}

function fillDemoCreds(email, pass) {
    document.getElementById('simpleLoginUser').value = email;
    document.getElementById('simpleLoginPass').value = pass;
}

function closeAuthModal() {
    document.getElementById('authModal').classList.remove('active');
}

function setAuthMode(mode) {
    state.authMode = mode;
    const tabLogin = document.getElementById('authTabLogin');
    const tabReg = document.getElementById('authTabRegister');
    const formLogin = document.getElementById('simpleLoginForm');
    const formReg = document.getElementById('simpleRegisterForm');

    if (mode === 'LOGIN') {
        tabLogin.classList.add('active');
        tabReg.classList.remove('active');
        formLogin.style.display = 'block';
        formReg.style.display = 'none';
    } else {
        tabReg.classList.add('active');
        tabLogin.classList.remove('active');
        formLogin.style.display = 'none';
        formReg.style.display = 'block';
    }
}

async function handleSimpleLogin(e) {
    e.preventDefault();
    const email = document.getElementById('simpleLoginUser').value.trim();
    const password = document.getElementById('simpleLoginPass').value;

    try {
        const res = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        const data = await res.json();
        if (res.ok) {
            state.token = data.token;
            state.user = data;
            localStorage.setItem('turf_token', data.token);
            closeAuthModal();
            showToast(`Welcome ${data.name}`);
            
            if (data.role === 'ROLE_OWNER' || data.role === 'ROLE_ADMIN') {
                openAdminPortal();
            } else {
                openUserPortal();
            }
        } else {
            showToast(data.message || 'Login failed');
        }
    } catch (err) {
        showToast('Login request error');
    }
}

async function handleSimpleRegister(e) {
    e.preventDefault();
    const name = document.getElementById('simpleRegName').value.trim();
    const email = document.getElementById('simpleRegEmail').value.trim();
    const password = document.getElementById('simpleRegPass').value;
    const phone = document.getElementById('simpleRegPhone').value.trim();
    const role = state.authRoleTarget === 'ADMIN' ? 'ROLE_OWNER' : 'ROLE_CUSTOMER';

    try {
        const res = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password, phone, role })
        });
        const data = await res.json();
        if (res.ok) {
            state.token = data.token;
            state.user = data;
            localStorage.setItem('turf_token', data.token);
            closeAuthModal();
            showToast(`Registered successfully`);
            
            if (role === 'ROLE_OWNER' || role === 'ROLE_ADMIN') {
                openAdminPortal();
            } else {
                openUserPortal();
            }
        } else {
            showToast(data.message || 'Registration failed');
        }
    } catch (err) {
        showToast('Registration error');
    }
}

// =========================================================
// ADMIN PORTAL TAB CONTROLS
// =========================================================
function showAdminTab(tab) {
    document.querySelectorAll('#adminPortalView .sidebar-btn').forEach(b => b.classList.remove('active'));
    document.getElementById('adminWelcomeTab').style.display = 'none';
    document.getElementById('adminInfoTab').style.display = 'none';
    document.getElementById('adminScheduleTab').style.display = 'none';
    document.getElementById('adminHistoryTab').style.display = 'none';

    if (tab === 'WELCOME') {
        document.getElementById('adminWelcomeTab').style.display = 'flex';
    } else if (tab === 'INFO') {
        document.getElementById('adminBtnInfo').classList.add('active');
        document.getElementById('adminInfoTab').style.display = 'block';
        switchAdminInfoSubTab('BASIC');
    } else if (tab === 'SCHEDULE') {
        document.getElementById('adminBtnSchedule').classList.add('active');
        document.getElementById('adminScheduleTab').style.display = 'block';
        loadAdminScheduleView();
    } else if (tab === 'HISTORY') {
        document.getElementById('adminBtnHistory').classList.add('active');
        document.getElementById('adminHistoryTab').style.display = 'block';
        loadAdminHistory();
    }
}

function switchAdminInfoSubTab(sub) {
    if (sub === 'BASIC') {
        document.getElementById('subTabBasicDetails').classList.add('active');
        document.getElementById('subTabSlotTimings').classList.remove('active');
        document.getElementById('adminBasicDetailsSec').style.display = 'block';
        document.getElementById('adminSlotTimingsSec').style.display = 'none';
    } else {
        document.getElementById('subTabSlotTimings').classList.add('active');
        document.getElementById('subTabBasicDetails').classList.remove('active');
        document.getElementById('adminBasicDetailsSec').style.display = 'none';
        document.getElementById('adminSlotTimingsSec').style.display = 'block';
        renderAdminSlotBadges();
    }
}

function renderAdminSlotBadges() {
    const grid = document.getElementById('adminSlotBadgeGrid');
    grid.innerHTML = STANDARD_SLOTS.map(t => `<div class="slot-badge-btn">${t}</div>`).join('');
}

function loadAdminScheduleView() {
    const grid = document.getElementById('adminScheduleSlotsGrid');
    grid.innerHTML = STANDARD_SLOTS.map(t => `<div class="slot-badge-btn">${t}</div>`).join('');
}

async function loadAdminTurfDetails() {
    try {
        const res = await fetch(`${API_BASE}/turfs`);
        if (res.ok) {
            const turfs = await res.json();
            if (turfs.length > 0) {
                state.currentTurf = turfs[0];
                document.getElementById('adminTurfName').value = state.currentTurf.name;
                document.getElementById('adminTurfLocation').value = state.currentTurf.address + ', ' + state.currentTurf.city;
                document.getElementById('adminPriceWithoutLights').value = state.currentTurf.priceWithoutLights || 1500;
                document.getElementById('adminPriceWithLights').value = state.currentTurf.priceWithLights || 2000;
                document.getElementById('adminOpenTime').value = state.currentTurf.openTime || "06:00";
                document.getElementById('adminCloseTime').value = state.currentTurf.closeTime || "23:00";
            }
        }
    } catch (e) {
        console.warn(e);
    }
}

async function handleAdminSaveTurf(e) {
    e.preventDefault();
    if (!state.currentTurf) return;

    const payload = {
        name: document.getElementById('adminTurfName').value,
        sportType: state.currentTurf.sportType || 'Football',
        address: document.getElementById('adminTurfLocation').value,
        city: state.currentTurf.city || 'City',
        pricePerHour: parseFloat(document.getElementById('adminPriceWithoutLights').value),
        openTime: document.getElementById('adminOpenTime').value,
        closeTime: document.getElementById('adminCloseTime').value,
        imageUrl: state.currentTurf.imageUrl,
        amenities: state.currentTurf.amenities
    };

    try {
        const res = await fetch(`${API_BASE}/turfs/${state.currentTurf.id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${state.token}`
            },
            body: JSON.stringify(payload)
        });
        if (res.ok) {
            showToast('Turf details updated successfully');
        } else {
            showToast('Failed to update turf details');
        }
    } catch (err) {
        showToast('Error saving turf details');
    }
}

async function handleAdminScheduleSubmit(e) {
    e.preventDefault();
    if (!state.currentTurf) return;

    const startDate = document.getElementById('schedStartDate').value;
    const endDate = document.getElementById('schedEndDate').value;
    const priceWithoutLights = parseFloat(document.getElementById('schedPriceWithoutLights').value);

    try {
        const res = await fetch(`${API_BASE}/slots/generate`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${state.token}`
            },
            body: JSON.stringify({
                turfId: state.currentTurf.id,
                startDate,
                endDate,
                slotDurationMinutes: 60,
                customPrice: priceWithoutLights
            })
        });
        if (res.ok) {
            showToast('Slots scheduled successfully for selected date range');
        } else {
            showToast('Error scheduling slots');
        }
    } catch (e) {
        showToast('Request failed');
    }
}

async function loadAdminHistory() {
    const tbody = document.getElementById('adminHistoryTableBody');
    tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;">Loading history...</td></tr>';

    try {
        const date = document.getElementById('adminHistoryDatePicker').value || new Date().toISOString().split('T')[0];
        const turfId = state.currentTurf ? state.currentTurf.id : 1;

        // Fetch slots and all bookings for this turf
        const [slotsRes, bookingsRes] = await Promise.all([
            fetch(`${API_BASE}/slots/turf/${turfId}?date=${date}`),
            fetch(`${API_BASE}/bookings/all`, {
                headers: { 'Authorization': `Bearer ${state.token}` }
            })
        ]);

        const slots = slotsRes.ok ? await slotsRes.json() : [];
        const allBookings = bookingsRes.ok ? await bookingsRes.json() : [];

        if (slots.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;">No slots found for this date.</td></tr>';
            return;
        }

        tbody.innerHTML = slots.map(slot => {
            // Find if there is a confirmed booking on this date, slot time, and turf
            const booking = allBookings.find(b => 
                b.turf.id === turfId && 
                b.bookingDate === date && 
                b.startTime === slot.startTime && 
                b.status !== 'CANCELLED'
            );

            const isBooked = slot.status === 'BOOKED' || !!booking;
            const statusText = isBooked ? 'Booked' : (slot.status === 'BLOCKED' ? 'Not Scheduled' : 'Available');
            const customerName = booking ? booking.user.name : '-';
            const customerMobile = booking ? (booking.user.phone || '-') : '-';
            const priceVal = booking ? booking.totalAmount : slot.price;

            return `
                <tr>
                    <td style="font-weight: 600;">${slot.startTime}</td>
                    <td>${priceVal.toFixed(0)}</td>
                    <td style="font-weight: 700; color: ${isBooked ? '#ef4444' : (slot.status === 'BLOCKED' ? '#6b7280' : '#16a34a')};">
                        ${statusText}
                    </td>
                    <td><strong style="color: ${isBooked ? '#1e293b' : '#94a3b8'};">${customerName}</strong></td>
                    <td style="color: ${isBooked ? '#1e293b' : '#94a3b8'};">${customerMobile}</td>
                </tr>
            `;
        }).join('');
    } catch (e) {
        tbody.innerHTML = '<tr><td colspan="5" style="text-align:center; color: #ef4444;">Failed to load booking history</td></tr>';
    }
}

// =========================================================
// USER PORTAL TAB CONTROLS
// =========================================================
function showUserTab(tab) {
    document.querySelectorAll('#userPortalView .sidebar-btn').forEach(b => b.classList.remove('active'));
    document.getElementById('userWelcomeTab').style.display = 'none';
    document.getElementById('userProfileTab').style.display = 'none';
    document.getElementById('userBookSlotTab').style.display = 'none';
    document.getElementById('userHistoryTab').style.display = 'none';

    if (tab === 'WELCOME') {
        document.getElementById('userWelcomeTab').style.display = 'flex';
    } else if (tab === 'PROFILE') {
        document.getElementById('userBtnProfile').classList.add('active');
        document.getElementById('userProfileTab').style.display = 'block';
        loadUserProfile();
    } else if (tab === 'BOOK_SLOT') {
        document.getElementById('userBtnBookSlot').classList.add('active');
        document.getElementById('userBookSlotTab').style.display = 'block';
        loadUserTurfsAndSlots();
    } else if (tab === 'HISTORY') {
        document.getElementById('userBtnHistory').classList.add('active');
        document.getElementById('userHistoryTab').style.display = 'block';
        loadUserHistory();
    }
}

async function loadUserProfile() {
    if (!state.user) return;
    document.getElementById('userProfileName').value = state.user.name || 'User 1';
    document.getElementById('userProfilePhone').value = state.user.phone || '9888888888';
    document.getElementById('userProfileEmail').value = state.user.email || 'user1@test.com';
}

async function handleUserProfileSave(e) {
    e.preventDefault();
    const name = document.getElementById('userProfileName').value.trim();
    const phone = document.getElementById('userProfilePhone').value.trim();
    const email = document.getElementById('userProfileEmail').value.trim();

    try {
        const res = await fetch(`${API_BASE}/auth/profile`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${state.token}`
            },
            body: JSON.stringify({ name, phone, email })
        });
        if (res.ok) {
            const data = await res.json();
            state.user.name = data.name;
            state.user.phone = data.phone;
            state.user.email = data.email;
            document.getElementById('welcomeUserText').textContent = `Welcome, ${data.name}`;
            showToast('Profile saved successfully');
        } else {
            showToast('Failed to save profile');
        }
    } catch (e) {
        showToast('Error saving profile');
    }
}

// User Book Slot Tab
async function loadUserTurfsAndSlots() {
    const list = document.getElementById('userTurfSlotsList');
    list.innerHTML = '<div style="padding: 20px; text-align: center;">Loading turfs and slots...</div>';
    state.selectedSlotsToBook = [];

    const date = document.getElementById('userBookingDatePicker').value || new Date().toISOString().split('T')[0];
    state.selectedBookingDate = date;

    try {
        const turfsRes = await fetch(`${API_BASE}/turfs`);
        const turfs = turfsRes.ok ? await turfsRes.json() : [];
        state.turfs = turfs;

        if (turfs.length === 0) {
            list.innerHTML = '<div style="padding: 20px; text-align: center;">No turfs found.</div>';
            return;
        }

        let html = '';
        for (const turf of turfs) {
            const slotsRes = await fetch(`${API_BASE}/slots/turf/${turf.id}?date=${date}`);
            const slots = slotsRes.ok ? await slotsRes.json() : [];

            html += `
                <div class="user-turf-row">
                    <div class="user-turf-name">${turf.name}</div>
                    <div class="user-turf-slots">
                        ${slots.map(s => {
                            const isBooked = s.status === 'BOOKED' || s.status === 'BLOCKED';
                            return `
                                <div class="slot-badge-btn ${isBooked ? 'booked' : ''}" 
                                     id="slot_btn_${s.id}"
                                     onclick="toggleSlotSelection(${s.id}, '${s.startTime}', ${s.price}, ${turf.id}, '${turf.name}', '${s.status}')">
                                    ${s.startTime}
                                </div>
                            `;
                        }).join('')}
                    </div>
                    <div>
                        <button class="btn-action" style="background: #e2e8f0; font-weight: 700;" onclick="openConfirmBookingModal(${turf.id})">Book</button>
                    </div>
                </div>
            `;
        }

        list.innerHTML = html;
    } catch (e) {
        list.innerHTML = '<div style="padding: 20px; text-align: center; color: red;">Failed to load turfs.</div>';
    }
}

function toggleSlotSelection(slotId, time, price, turfId, turfName, status) {
    if (status === 'BOOKED' || status === 'BLOCKED') {
        showToast('This slot is already booked');
        return;
    }

    const btn = document.getElementById(`slot_btn_${slotId}`);
    const index = state.selectedSlotsToBook.findIndex(s => s.slotId === slotId);

    if (index > -1) {
        state.selectedSlotsToBook.splice(index, 1);
        btn.classList.remove('selected');
    } else {
        state.selectedSlotsToBook.push({ slotId, time, price, turfId, turfName });
        btn.classList.add('selected');
    }
}

function openConfirmBookingModal(turfId) {
    const slotsForThisTurf = state.selectedSlotsToBook.filter(s => s.turfId === turfId);
    if (slotsForThisTurf.length === 0) {
        showToast('Please select at least 1 green time slot first');
        return;
    }

    const tbody = document.getElementById('confirmSlotTableBody');
    let total = 0;
    tbody.innerHTML = slotsForThisTurf.map(s => {
        total += s.price;
        return `
            <tr>
                <td><strong>${s.time}</strong></td>
                <td>${s.price.toFixed(0)}</td>
            </tr>
        `;
    }).join('');

    document.getElementById('confirmTotalAmount').textContent = total.toFixed(0);
    document.getElementById('confirmBookingModal').classList.add('active');
}

function closeConfirmModal() {
    document.getElementById('confirmBookingModal').classList.remove('active');
}

async function submitSlotBooking() {
    if (!state.token) {
        showToast('Please login to book');
        return;
    }

    if (state.selectedSlotsToBook.length === 0) {
        showToast('No slots selected');
        return;
    }

    try {
        for (const item of state.selectedSlotsToBook) {
            await fetch(`${API_BASE}/bookings`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${state.token}`
                },
                body: JSON.stringify({
                    turfId: item.turfId,
                    slotId: item.slotId,
                    paymentMethod: 'ONLINE'
                })
            });
        }

        closeConfirmModal();
        showToast('Slot booking confirmed successfully!');
        state.selectedSlotsToBook = [];
        showUserTab('HISTORY');
    } catch (e) {
        showToast('Booking submission failed');
    }
}

// User History Tab
async function loadUserHistory() {
    const tbody = document.getElementById('userHistoryTableBody');
    tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;">Loading history...</td></tr>';

    try {
        const res = await fetch(`${API_BASE}/bookings/my`, {
            headers: { 'Authorization': `Bearer ${state.token}` }
        });
        if (res.ok) {
            const bookings = await res.json();
            if (bookings.length === 0) {
                tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;">No booking records found.</td></tr>';
                return;
            }

            tbody.innerHTML = bookings.map(b => `
                <tr>
                    <td><strong>${b.turf.name}</strong></td>
                    <td>${b.bookingDate}</td>
                    <td>${b.startTime}</td>
                    <td>${b.totalAmount.toFixed(0)}</td>
                    <td style="font-weight: 600; color: ${b.status === 'CONFIRMED' ? '#16a34a' : '#ef4444'};">
                        ${b.status === 'CONFIRMED' ? 'Booked' : b.status}
                    </td>
                </tr>
            `).join('');
        }
    } catch (e) {
        tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;">Failed to load user history</td></tr>';
    }
}

// Event Listeners Setup
function setupFormListeners() {
    document.getElementById('adminTurfDetailsForm')?.addEventListener('submit', handleAdminSaveTurf);
    document.getElementById('adminScheduleForm')?.addEventListener('submit', handleAdminScheduleSubmit);
    document.getElementById('userProfileForm')?.addEventListener('submit', handleUserProfileSave);
}

// Simple Toast
function showToast(msg) {
    const bar = document.getElementById('toastBar');
    bar.textContent = msg;
    bar.style.display = 'block';
    setTimeout(() => {
        bar.style.display = 'none';
    }, 3000);
}
