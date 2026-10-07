'use strict';

/* =========================================================
   Login, sessao e area do aluno
   Usa api(), avisar(), esc() e dataBR() de app.js
   ========================================================= */

const CHAVE_TOKEN = 'gymflow.token';
const CHAVE_PERFIL = 'gymflow.perfil';
const CHAVE_NOME = 'gymflow.nome';

const ICONES_GRUPO = {
  PEITO: '🫁', COSTAS: '🔙', PERNAS: '🦵', OMBROS: '🏋️', BRACOS: '💪',
  CORE: '🧘', CARDIO: '🏃'
};
const OBJETIVOS = { HIPERTROFIA: 'Hipertrofia', EMAGRECIMENTO: 'Emagrecimento', CONDICIONAMENTO: 'Condicionamento' };

let areaAtual = null;

/* Toda chamada a /api leva o token da sessao, mesmo se o app.js estiver em cache. */
const fetchOriginal = window.fetch.bind(window);
window.fetch = (url, opcoes = {}) => {
  const token = sessionStorage.getItem(CHAVE_TOKEN);
  if (token && String(url).startsWith('/api')) {
    const cabecalhos = new Headers(opcoes.headers || {});
    cabecalhos.set('X-Sessao', token);
    opcoes = { ...opcoes, headers: cabecalhos, credentials: 'same-origin' };
  }
  return fetchOriginal(url, opcoes);
};

function sessaoExpirou(mensagem) {
  return /sess[aã]o expirad/i.test(String(mensagem || ''));
}

/* ---------- telas ---------- */

function mostrar(tela) {
  document.getElementById('tela-login').classList.toggle('oculto', tela !== 'login');
  document.getElementById('visao-aluno').classList.toggle('oculto', tela !== 'aluno');
  document.getElementById('visao-equipe').classList.toggle('oculto', tela !== 'equipe');
  document.body.classList.add('modo-vidro');
  window.scrollTo(0, 0);
}

function guardarSessao(sessao) {
  sessionStorage.setItem(CHAVE_TOKEN, sessao.token);
  sessionStorage.setItem(CHAVE_PERFIL, sessao.perfil);
  sessionStorage.setItem(CHAVE_NOME, sessao.nome || '');
}

function entrarNaVisao(perfil) {
  if (perfil === 'EQUIPE') {
    mostrar('equipe');
    api('GET', '/api/painel').then(() => window.iniciarEquipe()).catch((e) => {
      window.encerrarSessao(sessaoExpirou(e.message) ? 'Sua sessão expirou. Entre novamente.' : traduzir(e.message));
    });
  } else {
    mostrar('aluno');
    carregarAreaDoAluno();
  }
}

window.encerrarSessao = (mensagem) => {
  sessionStorage.removeItem(CHAVE_TOKEN);
  sessionStorage.removeItem(CHAVE_PERFIL);
  sessionStorage.removeItem(CHAVE_NOME);
  areaAtual = null;
  document.getElementById('form-login').reset();
  document.getElementById('erro-login').textContent = mensagem || '';
  mostrar('login');
  setTimeout(() => document.getElementById('login-numero').focus(), 50);
};

async function sair() {
  try { await api('POST', '/api/acesso/logout'); } catch (e) { /* sessao ja pode ter expirado */ }
  window.encerrarSessao();
}

/* ---------- login ---------- */

document.getElementById('form-login').addEventListener('submit', async (evento) => {
  evento.preventDefault();
  const numero = document.getElementById('login-numero').value.trim();
  const senha = document.getElementById('login-senha').value;
  const erro = document.getElementById('erro-login');
  const botao = document.getElementById('btn-entrar');
  const cartao = evento.currentTarget;

  if (!numero || !senha) {
    erro.textContent = 'Informe o e-mail (ou acesso da equipe) e a senha.';
    tremer(cartao);
    return;
  }
  erro.textContent = '';
  botao.disabled = true;
  botao.classList.add('carregando');
  try {
    const sessao = await api('POST', '/api/acesso/login', { numero, senha });
    guardarSessao(sessao);
    entrarNaVisao(sessao.perfil);
  } catch (e) {
    erro.textContent = traduzir(e.message);
    tremer(cartao);
  } finally {
    botao.disabled = false;
    botao.classList.remove('carregando');
  }
});

document.getElementById('btn-ver-senha').addEventListener('click', (evento) => {
  const campo = document.getElementById('login-senha');
  const mostrando = campo.type === 'text';
  campo.type = mostrando ? 'password' : 'text';
  evento.currentTarget.setAttribute('aria-pressed', String(!mostrando));
  evento.currentTarget.setAttribute('aria-label', mostrando ? 'Mostrar senha' : 'Ocultar senha');
});

document.getElementById('btn-sair-aluno').addEventListener('click', sair);
document.getElementById('btn-sair-equipe').addEventListener('click', sair);

function tremer(elemento) {
  elemento.classList.remove('tremer');
  void elemento.offsetWidth;
  elemento.classList.add('tremer');
}

/* Mensagens do servidor vem sem acento; deixamos mais legiveis na tela. */
function traduzir(texto) {
  return String(texto || '')
    .replace('E-mail, matricula ou senha invalidos', 'E-mail, matrícula ou senha inválidos.')
    .replace('Numero da matricula ou senha invalidos', 'Número da matrícula ou senha inválidos.')
    .replace('Informe o e-mail (ou matricula) e a senha', 'Informe o e-mail (ou acesso da equipe) e a senha.')
    .replace('Matricula cancelada: procure a recepcao', 'Matrícula cancelada: procure a recepção.')
    .replace('Check-in de hoje ja foi registrado', 'O check-in de hoje já foi feito.')
    .replace('Check-in exige matricula ativa', 'O check-in exige matrícula ativa.')
    .replace('Senha atual incorreta', 'A senha atual está incorreta.')
    .replace('A nova senha deve ser diferente da atual', 'A nova senha deve ser diferente da atual.')
    .replace('A nova senha deve ter pelo menos 6 caracteres', 'A nova senha deve ter pelo menos 6 caracteres.');
}

/* ---------- area do aluno ---------- */

async function carregarAreaDoAluno() {
  const treino = document.getElementById('cartao-treino');
  treino.innerHTML = '<p class="vazio-vidro">Carregando seus dados…</p>';
  try {
    areaAtual = await api('GET', '/api/area-aluno');
    desenharArea();
  } catch (e) {
    if (sessaoExpirou(e.message)) {
      window.encerrarSessao('Sua sessão expirou. Entre novamente.');
      return;
    }
    document.getElementById('cartao-fogo').innerHTML = '';
    document.getElementById('cartao-perfil').innerHTML = '';
    treino.innerHTML = `<h2>Não foi possível carregar sua área</h2>
      <p class="vazio-vidro">${esc(traduzir(e.message))}</p>
      <button class="botao-vidro fantasma" id="btn-recarregar-aluno">Tentar de novo</button>`;
    document.getElementById('btn-recarregar-aluno').addEventListener('click', carregarAreaDoAluno);
  }
}

function desenharArea() {
  const { aluno } = areaAtual;
  document.getElementById('saudacao-aluno').innerHTML = `Olá, <strong>${esc(aluno.nome.split(' ')[0])}</strong>`;
  desenharFogo();
  desenharPerfil();
  desenharTreino();
}

function desenharFogo() {
  const f = areaAtual.frequencia;
  const acesa = f.sequenciaAtual > 0;
  const dias = f.sequenciaAtual === 1 ? 'dia seguido' : 'dias seguidos';
  const matriculaAtiva = areaAtual.matricula && areaAtual.matricula.status === 'ATIVA';

  document.getElementById('cartao-fogo').innerHTML = `
    <h2>Minha frequência</h2>
    <p class="legenda">${acesa ? 'Não deixe o fogo apagar: volte amanhã.' : 'Faça o check-in para acender o fogo.'}</p>
    <div class="fogo-topo">
      <span class="fogo-grande ${acesa ? '' : 'apagado'}" id="fogo-grande" aria-hidden="true">🔥</span>
      <div>
        <div class="numero-sequencia">${f.sequenciaAtual}</div>
        <div class="texto-sequencia">${dias}</div>
      </div>
    </div>
    <div class="semana" aria-label="Presenças desta semana">
      ${f.semana.map((d, i) => `
        <div class="dia ${d.presente ? 'presente' : ''} ${d.hoje ? 'hoje' : ''}" style="--i:${i}"
             title="${dataBR(d.data)}${d.presente ? ' — presente' : ''}">
          <span>${esc(d.rotulo)}</span>
          <span class="chama" aria-hidden="true">🔥</span>
          <span>${d.data.slice(8)}</span>
        </div>`).join('')}
    </div>
    <div class="numeros-fogo">
      <div><strong>${f.maiorSequencia}</strong><span>Maior sequência</span></div>
      <div><strong>${f.presencasNoMes}</strong><span>Presenças no mês</span></div>
      <div><strong>${f.totalDePresencas}</strong><span>Total de presenças</span></div>
    </div>
    <button class="botao-vidro primario-vidro" id="btn-check-in" ${f.presenteHoje || !matriculaAtiva ? 'disabled' : ''}>
      <span>${f.presenteHoje ? 'Check-in de hoje feito ✓' : (matriculaAtiva ? 'Fazer check-in de hoje' : 'Check-in exige matrícula ativa')}</span>
    </button>`;

  document.getElementById('btn-check-in').addEventListener('click', fazerCheckIn);
}

async function fazerCheckIn(evento) {
  const botao = evento.currentTarget;
  botao.disabled = true;
  botao.classList.add('carregando');
  try {
    areaAtual = await api('POST', '/api/area-aluno/presencas');
    desenharFogo();
    faiscas(document.getElementById('fogo-grande'));
    avisar(`Check-in feito! Sequência de ${areaAtual.frequencia.sequenciaAtual} ${areaAtual.frequencia.sequenciaAtual === 1 ? 'dia' : 'dias'}.`);
  } catch (e) {
    avisar(traduzir(e.message), true);
    botao.disabled = false;
    botao.classList.remove('carregando');
  }
}

function faiscas(origem) {
  if (!origem || matchMedia('(prefers-reduced-motion: reduce)').matches) return;
  const r = origem.getBoundingClientRect();
  for (let i = 0; i < 14; i++) {
    const f = document.createElement('span');
    f.className = 'faisca';
    f.textContent = i % 3 === 0 ? '✨' : '🔥';
    f.style.left = `${r.left + r.width / 2 - 10}px`;
    f.style.top = `${r.top + r.height / 2 - 10}px`;
    const angulo = (Math.PI * 2 * i) / 14;
    const distancia = 70 + Math.random() * 60;
    f.style.setProperty('--dx', `${Math.cos(angulo) * distancia}px`);
    f.style.setProperty('--dy', `${Math.sin(angulo) * distancia - 30}px`);
    f.style.setProperty('--rot', `${Math.random() * 360}deg`);
    document.body.appendChild(f);
    setTimeout(() => f.remove(), 1000);
  }
}

function desenharPerfil() {
  const { aluno, matricula } = areaAtual;
  const iniciais = aluno.nome.split(' ').filter(Boolean).slice(0, 2).map(p => p[0]).join('').toUpperCase();
  const linhaMatricula = matricula ? `
      <div><dt>Nº da matrícula</dt><dd>${esc(matricula.id)}</dd></div>
      <div><dt>Plano</dt><dd>${esc(matricula.planoNome)} · ${esc(matricula.valorMensal)}</dd></div>
      <div><dt>Situação</dt><dd><span class="selo ${esc(matricula.status)}">${esc(matricula.status.charAt(0) + matricula.status.slice(1).toLowerCase())}</span></dd></div>
      <div><dt>Vigência</dt><dd>${dataBR(matricula.inicio)} a ${dataBR(matricula.fim)}</dd></div>`
    : '<div><dt>Matrícula</dt><dd>Nenhuma</dd></div>';

  document.getElementById('cartao-perfil').innerHTML = `
    <div class="perfil-topo">
      <div class="avatar" aria-hidden="true">${esc(iniciais)}</div>
      <div><h2>${esc(aluno.nome)}</h2><p>${esc(aluno.idade)} anos · ${esc(OBJETIVOS[aluno.objetivo] || aluno.objetivo)}</p></div>
    </div>
    <dl class="dados">
      ${linhaMatricula}
      <div><dt>E-mail</dt><dd>${esc(aluno.email)}</dd></div>
      <div><dt>Telefone</dt><dd>${esc(aluno.telefone)}</dd></div>
      <div><dt>Nascimento</dt><dd>${dataBR(aluno.dataDeNascimento)}</dd></div>
    </dl>
    <button class="botao-vidro fantasma" id="btn-trocar-senha">Trocar senha</button>`;

  document.getElementById('btn-trocar-senha').addEventListener('click', () => {
    document.getElementById('form-senha').reset();
    document.getElementById('janela-senha').showModal();
  });
}

function desenharTreino() {
  const ficha = areaAtual.ficha;
  const alvo = document.getElementById('cartao-treino');
  if (!ficha) {
    alvo.innerHTML = `<h2>Treino da semana</h2>
      <p class="vazio-vidro">Você ainda não tem ficha de treino. Peça ao instrutor para montar a sua.</p>`;
    return;
  }
  const total = ficha.itens.length;
  const feitos = total - ficha.pendentes;
  const pct = total ? Math.round((feitos / total) * 100) : 0;
  const proximo = ficha.itens.find(i => !i.concluido);

  alvo.innerHTML = `
    <div class="treino-topo">
      <div>
        <h2>Treino da semana</h2>
        <p class="legenda" style="margin:0">Foco em ${esc(OBJETIVOS[ficha.objetivo] || ficha.objetivo)} · ficha válida até ${dataBR(ficha.validaAte)}</p>
      </div>
      <div class="progresso">
        <div class="progresso-rotulo"><span>${feitos} de ${total} exercícios</span><span>${pct}%</span></div>
        <div class="trilho"><span id="barra-progresso"></span></div>
      </div>
    </div>
    <ul class="exercicios">
      ${ficha.itens.map((item, i) => `
        <li class="exercicio ${item.concluido ? 'feito' : ''} ${proximo && proximo.posicao === item.posicao ? 'proximo' : ''}" style="--i:${i}">
          <span class="grupo-icone" aria-hidden="true">${ICONES_GRUPO[item.grupoMuscular] || '🏋️'}</span>
          <div>
            <h3>${esc(item.exercicio)}</h3>
            <p>${esc(nomeDoGrupo(item.grupoMuscular))} · descanso de ${esc(item.descansoEmSegundos)}s</p>
          </div>
          <div class="carga-vidro">
            <span><b>${esc(item.series)}</b> séries</span>
            <span><b>${esc(item.repeticoes)}</b> repetições</span>
          </div>
          <div class="acao-item">
            ${item.concluido
              ? '<span class="check">Feito ✓</span>'
              : `<button class="botao-vidro" data-posicao="${item.posicao}">Concluir</button>`}
          </div>
        </li>`).join('')}
    </ul>`;

  requestAnimationFrame(() => {
    const barra = document.getElementById('barra-progresso');
    if (barra) barra.style.width = `${pct}%`;
  });
}

function nomeDoGrupo(grupo) {
  const texto = String(grupo || '').replace('_', ' ').toLowerCase().replace('bracos', 'braços');
  return texto.charAt(0).toUpperCase() + texto.slice(1);
}

document.getElementById('cartao-treino').addEventListener('click', async (evento) => {
  const botao = evento.target.closest('button[data-posicao]');
  if (!botao || !areaAtual || !areaAtual.ficha) return;
  botao.disabled = true;
  try {
    areaAtual = await api('POST',
      `/api/area-aluno/fichas/${areaAtual.ficha.id}/itens/${botao.dataset.posicao}/conclusao`);
    desenharTreino();
    const restantes = areaAtual.ficha.pendentes;
    avisar(restantes ? `Boa! Faltam ${restantes} exercício${restantes > 1 ? 's' : ''}.` : 'Treino concluído! 🔥');
  } catch (e) {
    avisar(traduzir(e.message), true);
    botao.disabled = false;
  }
});

/* ---------- trocar senha ---------- */

document.getElementById('btn-fechar-senha').addEventListener('click', () => {
  document.getElementById('janela-senha').close();
});

document.getElementById('form-senha').addEventListener('submit', async (evento) => {
  evento.preventDefault();
  const dados = new FormData(evento.currentTarget);
  const novaSenha = dados.get('novaSenha');
  if (novaSenha.length < 6) {
    avisar('A nova senha deve ter pelo menos 6 caracteres.', true);
    return;
  }
  if (novaSenha !== dados.get('confirmacao')) {
    avisar('As senhas novas não conferem.', true);
    return;
  }
  try {
    await api('PUT', '/api/area-aluno/senha', { senhaAtual: dados.get('senhaAtual'), novaSenha });
    document.getElementById('janela-senha').close();
    avisar('Senha alterada.');
  } catch (e) {
    avisar(traduzir(e.message), true);
  }
});

/* ---------- efeitos do vidro ---------- */

/* reflexo do vidro segue o ponteiro */
document.addEventListener('pointermove', (evento) => {
  if (!document.body.classList.contains('modo-vidro')) return;
  const vidro = evento.target.closest && evento.target.closest('.vidro');
  if (!vidro) return;
  const r = vidro.getBoundingClientRect();
  vidro.style.setProperty('--mx', `${evento.clientX - r.left}px`);
  vidro.style.setProperty('--my', `${evento.clientY - r.top}px`);
});

/* gota de agua nos botoes */
document.addEventListener('pointerdown', (evento) => {
  const botao = evento.target.closest && evento.target.closest('.botao-vidro');
  if (!botao || botao.disabled) return;
  const r = botao.getBoundingClientRect();
  const tamanho = Math.max(r.width, r.height);
  const gota = document.createElement('span');
  gota.className = 'gota';
  gota.style.width = gota.style.height = `${tamanho}px`;
  gota.style.left = `${evento.clientX - r.left - tamanho / 2}px`;
  gota.style.top = `${evento.clientY - r.top - tamanho / 2}px`;
  botao.appendChild(gota);
  setTimeout(() => gota.remove(), 600);
});

/* ---------- inicio ---------- */

(function iniciar() {
  const token = sessionStorage.getItem(CHAVE_TOKEN);
  const perfil = sessionStorage.getItem(CHAVE_PERFIL);
  if (token && perfil) {
    entrarNaVisao(perfil);
  } else {
    window.encerrarSessao();
  }
})();
