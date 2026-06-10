
/* =============================================
   TASK MANAGER — validation.js
   Validação de formulários + interatividade
   ============================================= */

'use strict';

// ─── Utilitários ─────────────────────────────

const $ = (sel, ctx = document) => ctx.querySelector(sel);
const $$ = (sel, ctx = document) => [...ctx.querySelectorAll(sel)];

function showError(fieldEl, message) {
  const group = fieldEl.closest('.form-group');
  if (!group) return;
  group.classList.add('error');
  const err = group.querySelector('.field-error');
  if (err) err.textContent = message;
}

function clearError(fieldEl) {
  const group = fieldEl.closest('.form-group');
  if (!group) return;
  group.classList.remove('error');
}

function clearAllErrors(formEl) {
  $$('.form-group.error', formEl).forEach(g => g.classList.remove('error'));
}

// ─── Toast ───────────────────────────────────

function showToast(message, type = 'success', duration = 3000) {
  let container = $('.toast-container');
  if (!container) {
    container = document.createElement('div');
    container.className = 'toast-container';
    document.body.appendChild(container);
  }

  const icon = type === 'success'
    ? '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7"/></svg>'
    : '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"/></svg>';

  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.innerHTML = `${icon}<span>${message}</span>`;
  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(20px)';
    toast.style.transition = 'all .3s ease';
    setTimeout(() => toast.remove(), 300);
  }, duration);
}

// ─── Regras de validação ──────────────────────

const validators = {
  required: (val) => val.trim() !== '' || 'Este campo é obrigatório.',

  minLength: (min) => (val) =>
    val.trim().length >= min || `Mínimo de ${min} caracteres.`,

  maxLength: (max) => (val) =>
    val.trim().length <= max || `Máximo de ${max} caracteres.`,

  email: (val) =>
    /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val.trim()) || 'E-mail inválido.',

  password: (val) =>
    val.length >= 6 || 'A senha deve ter no mínimo 6 caracteres.',

  strongPassword: (val) => {
    if (val.length < 8) return 'Mínimo de 8 caracteres.';
    if (!/[A-Z]/.test(val)) return 'Inclua ao menos uma letra maiúscula.';
    if (!/[0-9]/.test(val)) return 'Inclua ao menos um número.';
    return true;
  },

  noScript: (val) => {
    const dangerous = /<script|<\/script|javascript:|on\w+\s*=/i;
    return !dangerous.test(val) || 'Conteúdo não permitido detectado.';
  },

  noSQLInjection: (val) => {
    const sql = /('|--|;|DROP\s+TABLE|INSERT\s+INTO|SELECT\s+\*|DELETE\s+FROM)/i;
    return !sql.test(val) || 'Caracteres não permitidos detectados.';
  },
};

function validate(value, rules) {
  for (const rule of rules) {
    const result = rule(value);
    if (result !== true) return result;
  }
  return true;
}

// ─── FORMULÁRIO DE LOGIN ──────────────────────

function initLoginForm() {
  const form = $('#loginForm');
  if (!form) return;

  const emailInput = $('#loginEmail', form);
  const passInput  = $('#loginPassword', form);

  // Live validation ao sair do campo
  emailInput?.addEventListener('blur', () => {
    const err = validate(emailInput.value, [validators.required, validators.email]);
    if (err !== true) showError(emailInput, err);
    else clearError(emailInput);
  });

  emailInput?.addEventListener('input', () => clearError(emailInput));
  passInput?.addEventListener('input',  () => clearError(passInput));

  form.addEventListener('submit', (e) => {
    e.preventDefault();
    clearAllErrors(form);
    let valid = true;

    // Sanitização básica (XSS)
    const emailVal = emailInput.value.replace(/[<>"']/g, '');
    const passVal  = passInput.value;

    const emailErr = validate(emailVal, [validators.required, validators.email]);
    if (emailErr !== true) { showError(emailInput, emailErr); valid = false; }

    const passErr = validate(passVal, [validators.required, validators.password]);
    if (passErr !== true) { showError(passInput, passErr); valid = false; }

    if (!valid) return;

    // Feedback visual de loading
    const btn = form.querySelector('.btn-primary');
    btn?.classList.add('loading');

    // ⚡ Aqui o form será submetido ao Servlet
    // Por enquanto, simulamos para dev:
    setTimeout(() => {
      btn?.classList.remove('loading');
      form.submit(); // descomentar quando Servlet estiver pronto
    }, 800);
  });
}

// ─── FORMULÁRIO DE CADASTRO ───────────────────

function initCadastroForm() {
  const form = $('#cadastroForm');
  if (!form) return;

  const nomeInput     = $('#cadastroNome', form);
  const emailInput    = $('#cadastroEmail', form);
  const passInput     = $('#cadastroPassword', form);
  const confirmInput  = $('#cadastroConfirm', form);

  // Força da senha
  passInput?.addEventListener('input', () => {
    updatePasswordStrength(passInput.value);
    clearError(passInput);
  });

  function updatePasswordStrength(pass) {
    const fill  = $('#strengthFill');
    const label = $('#strengthLabel');
    if (!fill || !label) return;

    let score = 0;
    if (pass.length >= 8)        score++;
    if (/[A-Z]/.test(pass))      score++;
    if (/[0-9]/.test(pass))      score++;
    if (/[^A-Za-z0-9]/.test(pass)) score++;

    const levels = [
      { pct: '0%',   bg: 'transparent', text: '' },
      { pct: '25%',  bg: '#C0392B', text: 'Fraca' },
      { pct: '50%',  bg: '#B7791F', text: 'Regular' },
      { pct: '75%',  bg: '#2B7A9E', text: 'Boa' },
      { pct: '100%', bg: '#3D6B5E', text: 'Forte' },
    ];

    fill.style.width      = levels[score].pct;
    fill.style.background = levels[score].bg;
    label.textContent     = levels[score].text;
  }

  // Live validations
  emailInput?.addEventListener('blur', () => {
    const err = validate(emailInput.value, [validators.required, validators.email]);
    if (err !== true) showError(emailInput, err);
    else clearError(emailInput);
  });

  confirmInput?.addEventListener('input', () => {
    clearError(confirmInput);
    if (passInput.value && confirmInput.value && passInput.value !== confirmInput.value) {
      showError(confirmInput, 'As senhas não coincidem.');
    }
  });

  [nomeInput, emailInput, passInput].forEach(el => {
    el?.addEventListener('input', () => clearError(el));
  });

  form.addEventListener('submit', (e) => {
    e.preventDefault();
    clearAllErrors(form);
    let valid = true;

    // Sanitização
    const nome  = nomeInput.value.replace(/[<>"']/g, '').trim();
    const email = emailInput.value.replace(/[<>"']/g, '').trim();
    const pass  = passInput.value;
    const conf  = confirmInput.value;

    // Validações com anti-injeção
    const nomeErr = validate(nome, [validators.required, validators.minLength(3), validators.noScript, validators.noSQLInjection]);
    if (nomeErr !== true) { showError(nomeInput, nomeErr); valid = false; }

    const emailErr = validate(email, [validators.required, validators.email]);
    if (emailErr !== true) { showError(emailInput, emailErr); valid = false; }

    const passErr = validate(pass, [validators.required, validators.strongPassword]);
    if (passErr !== true) { showError(passInput, passErr); valid = false; }

    if (pass !== conf) { showError(confirmInput, 'As senhas não coincidem.'); valid = false; }

    if (!valid) return;

    const btn = form.querySelector('.btn-primary');
    btn?.classList.add('loading');

    setTimeout(() => {
      btn?.classList.remove('loading');
      form.submit();
    }, 800);
  });
}

// ─── DASHBOARD ───────────────────────────────

function initDashboard() {
  if (!$('.dashboard-wrapper')) return;

  // ── Modal nova tarefa ──
  const modalOverlay  = $('#taskModal');
  const openModalBtn  = $('#openModalBtn');
  const closeModalBtn = $('#closeModal');
  const taskForm      = $('#taskForm');

  openModalBtn?.addEventListener('click', () => {
    modalOverlay?.classList.add('open');
    setTimeout(() => $('#taskTitle', taskForm)?.focus(), 150);
  });

  closeModalBtn?.addEventListener('click', closeModal);
  modalOverlay?.addEventListener('click', (e) => {
    if (e.target === modalOverlay) closeModal();
  });

  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') closeModal();
  });

  function closeModal() {
    modalOverlay?.classList.remove('open');
    taskForm?.reset();
    clearAllErrors(taskForm);
    // Resetar para modo "nova tarefa"
    const modalTitle = $('#modalTitle');
    if (modalTitle) modalTitle.textContent = 'Nova Tarefa';
    const editId = $('#editTaskId');
    if (editId) editId.value = '';
  }

  // ── Validação do form de tarefa ──
  taskForm?.addEventListener('submit', (e) => {
    e.preventDefault();
    clearAllErrors(taskForm);
    let valid = true;

    const titleInput = $('#taskTitle', taskForm);
    const titleVal   = titleInput?.value.replace(/[<>"']/g, '').trim();

    const titleErr = validate(titleVal, [
      validators.required,
      validators.minLength(2),
      validators.maxLength(100),
      validators.noScript,
      validators.noSQLInjection,
    ]);

    if (titleErr !== true) { showError(titleInput, titleErr); valid = false; }

    if (!valid) return;

    const btn = taskForm.querySelector('.btn-primary');
    btn?.classList.add('loading');

    setTimeout(() => {
      btn?.classList.remove('loading');
      taskForm.submit();
    }, 600);
  });

  // ── Filtros ──
  const filterTabs = $$('.filter-tab');
  filterTabs.forEach(tab => {
    tab.addEventListener('click', () => {
      filterTabs.forEach(t => t.classList.remove('active'));
      tab.classList.add('active');
      filterTasks(tab.dataset.filter);
    });
  });

  function filterTasks(filter) {
    const cards = $$('.task-card');
    cards.forEach(card => {
      const isDone = card.classList.contains('done');
      if (filter === 'all')        card.style.display = '';
      else if (filter === 'active')    card.style.display = isDone ? 'none' : '';
      else if (filter === 'done')  card.style.display = isDone ? '' : 'none';
    });
  }

  // ── Busca local ──
  const searchInput = $('#taskSearch');
  searchInput?.addEventListener('input', () => {
    const term = searchInput.value.toLowerCase().trim();
    $$('.task-card').forEach(card => {
      const title = card.querySelector('.task-title')?.textContent.toLowerCase() || '';
      card.style.display = title.includes(term) ? '' : 'none';
    });
  });

  // ── Checkbox concluir (client-side preview) ──
  document.addEventListener('change', (e) => {
    if (e.target.matches('.check-task')) {
      const card = e.target.closest('.task-card');
      if (card) card.classList.toggle('done', e.target.checked);
    }
  });

  // ── Editar tarefa: preencher modal ──
  document.addEventListener('click', (e) => {
    const editBtn = e.target.closest('.edit-task-btn');
    if (!editBtn) return;

    const card = editBtn.closest('.task-card');
    const id    = editBtn.dataset.id    || '';
    const title = editBtn.dataset.title || '';
    const desc  = editBtn.dataset.desc  || '';
    const prio  = editBtn.dataset.prio  || 'MEDIA';

    const modalTitle = $('#modalTitle');
    if (modalTitle) modalTitle.textContent = 'Editar Tarefa';

    const editIdInput = $('#editTaskId');
    if (editIdInput) editIdInput.value = id;

    const titleInput = $('#taskTitle', taskForm);
    if (titleInput) titleInput.value = title;

    const descInput = $('#taskDesc', taskForm);
    if (descInput) descInput.value = desc;

    const prioInput = $('#taskPriority', taskForm);
    if (prioInput) prioInput.value = prio;

    modalOverlay?.classList.add('open');
  });
}

// ─── Mostrar/ocultar senha ────────────────────

function initPasswordToggles() {
  $$('.toggle-password').forEach(btn => {
    btn.addEventListener('click', () => {
      const targetId = btn.dataset.target;
      const input = document.getElementById(targetId);
      if (!input) return;

      const isPass = input.type === 'password';
      input.type   = isPass ? 'text' : 'password';
      btn.innerHTML = isPass
        ? '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 4.411m0 0L21 21"/></svg>'
        : '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/><path stroke-linecap="round" stroke-linejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/></svg>';
    });
  });
}

// ─── Flash messages (do servidor via JSP) ─────

function initFlashMessages() {
  const alerts = $$('.alert[data-auto-dismiss]');
  alerts.forEach(alert => {
    const delay = parseInt(alert.dataset.autoDismiss) || 4000;
    setTimeout(() => {
      alert.style.transition = 'opacity .4s ease';
      alert.style.opacity = '0';
      setTimeout(() => alert.remove(), 400);
    }, delay);
  });
}

// ─── Boot ─────────────────────────────────────

document.addEventListener('DOMContentLoaded', () => {
  initLoginForm();
  initCadastroForm();
  initDashboard();
  initPasswordToggles();
  initFlashMessages();
});
