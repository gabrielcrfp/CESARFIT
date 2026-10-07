'use strict';

/* =========================================================
   Utilidades: API, avisos, formatacao
   ========================================================= */

async function api(metodo, url, corpo) {
  const opcoes = { method: metodo, headers: {} };
  const token = sessionStorage.getItem('gymflow.token');
  if (token) opcoes.headers['X-Sessao'] = token;
  if (corpo !== undefined) {
    opcoes.headers['Content-Type'] = 'application/json';
    opcoes.body = JSON.stringify(corpo);
  }
  const resposta = await fetch(url, opcoes);
  if (resposta.status === 401 && !url.endsWith('/api/acesso/login')) {
    if (window.encerrarSessao) window.encerrarSessao('Sua sessão expirou. Entre novamente.');
    throw new Error('Sessão expirada');
  }
  if (resposta.status === 204) return null;
  const texto = await resposta.text();
  let dados = null;
  try { dados = texto ? JSON.parse(texto) : null; } catch (e) { dados = null; }
  if (!resposta.ok) {
    throw new Error((dados && (dados.erro || dados.motivo)) || 'Falha na comunicação com o servidor');
  }
  return dados;
}

function avisar(mensagem, erro) {
  const caixa = document.getElementById('aviso');
  caixa.textContent = mensagem;
  caixa.className = 'aviso visivel' + (erro ? ' erro' : '');
  clearTimeout(avisar.timer);
  avisar.timer = setTimeout(() => { caixa.className = 'aviso'; }, 4500);
}

async function tentar(acao) {
  try {
    await acao();
  } catch (e) {
    avisar(e.message, true);
  }
}

function esc(texto) {
  return String(texto ?? '').replace(/[&<>"']/g, c =>
    ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
}

function dataBR(iso) {
  if (!iso) return '—';
  const [a, m, d] = iso.split('-');
  return `${d}/${m}/${a}`;
}

function hojeISO() {
  const d = new Date();
  d.setMinutes(d.getMinutes() - d.getTimezoneOffset());
  return d.toISOString().slice(0, 10);
}

function adiar(fn, ms = 300) {
  let t;
  return (...args) => { clearTimeout(t); t = setTimeout(() => fn(...args), ms); };
}

const NOME_STATUS = { ATIVA: 'Ativa', VENCIDA: 'Vencida', CANCELADA: 'Cancelada' };
const NOME_OBJETIVO = { HIPERTROFIA: 'Hipertrofia', EMAGRECIMENTO: 'Emagrecimento', CONDICIONAMENTO: 'Condicionamento' };

/* =========================================================
   Estado
   ========================================================= */

const estado = {
  alunos: [],            // todos os alunos (seletores)
  planos: [],            // Semanal e Mensal vindos do servidor
  matriculas: [],        // matriculas exibidas na tabela (com filtros)
  editandoAlunoId: null,
  editandoMatriculaId: null,
  ficha: null
};

/* =========================================================
   Abas
   ========================================================= */

document.getElementById('abas').addEventListener('click', (evento) => {
  const botao = evento.target.closest('.aba[data-alvo]');
  if (!botao) return;
  abrirAba(botao.dataset.alvo);
});

function abrirAba(alvo) {
  document.querySelectorAll('.aba').forEach(b => b.classList.toggle('ativa', b.dataset.alvo === alvo));
  document.querySelectorAll('.secao').forEach(s => s.classList.toggle('ativa', s.id === alvo));
  tentar(() => aoAbrir(alvo));
}

function abaAtual() {
  const ativa = document.querySelector('.aba.ativa');
  return ativa ? ativa.dataset.alvo : 'painel';
}

async function aoAbrir(alvo) {
  if (alvo === 'painel') return carregarPainel();
  if (alvo === 'alunos') return carregarAlunos();
  if (alvo === 'matriculas') return Promise.all([carregarAlunos(), carregarMatriculas()]);
  if (alvo === 'treinos') return carregarAlunos().then(listarFichas);
  if (alvo === 'eventos') return carregarEventos();
}

/** Recarrega tudo que depende de alunos/matriculas apos qualquer alteracao. */
async function recarregarTudo() {
  await carregarAlunos();
  await Promise.all([carregarMatriculas(), carregarPainel()]);
  if (abaAtual() === 'eventos') await carregarEventos();
  if (abaAtual() === 'treinos') await listarFichas();
}

/* =========================================================
   PAINEL / DASHBOARD  (Integrante 6)
   ========================================================= */

async function carregarPainel() {
  const p = await api('GET', '/api/painel');
  document.getElementById('indicadores').innerHTML = `
    <div class="indicador"><span>Alunos cadastrados</span><strong>${p.totalDeAlunos}</strong></div>
    <div class="indicador verde"><span>Matrículas ativas</span><strong>${p.matriculasAtivas}</strong></div>
    <div class="indicador"><span>Ativas no Plano Semanal</span><strong>${p.semanaisAtivas}</strong></div>
    <div class="indicador"><span>Ativas no Plano Mensal</span><strong>${p.mensaisAtivas}</strong></div>
    <div class="indicador verde"><span>Receita das matrículas ativas</span><strong>${esc(p.receitaDasAtivas)}</strong></div>
    <div class="indicador ambar"><span>Matrículas vencidas</span><strong>${p.matriculasVencidas}</strong></div>
    <div class="indicador vermelho"><span>Matrículas canceladas</span><strong>${p.matriculasCanceladas}</strong></div>
    <div class="indicador ambar"><span>Alunos sem matrícula ativa</span><strong>${p.alunosSemMatriculaAtiva}</strong></div>
    <div class="indicador"><span>Presenças no mês</span><strong>${p.presencasNoMes}</strong></div>
    <div class="indicador verde"><span>Fichas ativas</span><strong>${p.fichasAtivas}</strong></div>
    <div class="indicador ambar"><span>Em atraso</span><strong>${p.matriculasEmAtraso}</strong></div>`;
  const alvo = document.getElementById('ultimas-matriculas');
  if (!p.ultimasMatriculas.length) {
    alvo.innerHTML = '<p class="vazio">Nenhuma matrícula cadastrada ainda.</p>';
    return;
  }
  alvo.innerHTML = `<table><thead><tr>
      <th>#</th><th>Aluno</th><th>Plano</th><th>Valor</th><th>Situação</th></tr></thead><tbody>
    ${p.ultimasMatriculas.map(m => `<tr>
      <td>${m.id}</td><td>${esc(m.alunoNome)}</td>
      <td><span class="etiqueta ${m.plano}">${esc(m.planoNome)}</span></td>
      <td>${esc(m.valorMensal)}</td>
      <td><span class="etiqueta ${m.status}">${NOME_STATUS[m.status]}</span></td></tr>`).join('')}
    </tbody></table>`;
}

/* =========================================================
   ALUNOS  (Integrantes 1 e 2)
   ========================================================= */

async function carregarAlunos() {
  estado.alunos = await api('GET', '/api/alunos');
  preencherSeletores();
  const busca = document.getElementById('busca-aluno').value.trim();
  const lista = busca
    ? await api('GET', `/api/alunos?busca=${encodeURIComponent(busca)}`)
    : estado.alunos;
  desenharTabelaAlunos(lista, busca);
}

function desenharTabelaAlunos(lista, busca) {
  const alvo = document.getElementById('tabela-alunos');
  document.getElementById('contador-alunos').textContent =
    `${lista.length} de ${estado.alunos.length} aluno(s)`;
  if (!lista.length) {
    alvo.innerHTML = busca
      ? '<p class="vazio">Nenhum aluno encontrado para esta pesquisa.</p>'
      : '<p class="vazio">Nenhum aluno cadastrado. Use o formulário ao lado.</p>';
    return;
  }
  alvo.innerHTML = `<table><thead><tr>
      <th>Nome</th><th>CPF</th><th>Telefone</th><th>Matrícula</th><th></th></tr></thead><tbody>
    ${lista.map(a => `<tr class="${estado.editandoAlunoId === a.id ? 'em-edicao' : ''}">
      <td>${esc(a.nome)}<div class="contador">${esc(a.email)}</div></td>
      <td>${esc(a.cpf)}</td>
      <td>${esc(a.telefone)}</td>
      <td>${a.possuiMatriculaAtiva ? '<span class="etiqueta ATIVA">Ativa</span>' : '<span class="contador">sem matrícula ativa</span>'}</td>
      <td class="acoes-tabela">
        <button class="btn-mini" data-acao="ver-aluno" data-id="${a.id}">Ver</button>
        <button class="btn-mini" data-acao="editar-aluno" data-id="${a.id}">Editar</button>
        <button class="btn-mini perigo" data-acao="excluir-aluno" data-id="${a.id}">Excluir</button>
      </td></tr>`).join('')}
    </tbody></table>`;
}

function preencherSeletores() {
  document.querySelectorAll('.seletor-aluno').forEach(seletor => {
    const anterior = seletor.value;
    seletor.innerHTML = estado.alunos.length
      ? '<option value="">Selecione o aluno</option>' +
        estado.alunos.map(a => `<option value="${a.id}">${esc(a.nome)} · ${esc(a.cpf)}</option>`).join('')
      : '<option value="">Cadastre um aluno primeiro</option>';
    if (anterior && estado.alunos.some(a => String(a.id) === anterior)) seletor.value = anterior;
  });
}

document.getElementById('busca-aluno').addEventListener('input', adiar(() => tentar(carregarAlunos)));

function dadosDoFormAluno(form) {
  const dados = {
    nome: form.nome.value.trim(),
    cpf: form.cpf.value.trim(),
    telefone: form.telefone.value.trim(),
    email: form.email.value.trim(),
    dataDeNascimento: form.dataDeNascimento.value || null,
    objetivo: form.objetivo.value
  };
  if (dados.nome.length < 3) throw new Error('Informe o nome do aluno (mínimo 3 letras)');
  if (dados.cpf.replace(/\D/g, '').length !== 11) throw new Error('CPF deve conter 11 dígitos');
  const fone = dados.telefone.replace(/\D/g, '').length;
  if (fone < 10 || fone > 11) throw new Error('Telefone inválido: informe DDD + número');
  if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(dados.email)) throw new Error('Informe um e-mail válido');
  if (!dados.dataDeNascimento) throw new Error('Informe a data de nascimento');
  return dados;
}

document.getElementById('form-aluno').addEventListener('submit', (evento) => {
  evento.preventDefault();
  const form = evento.target;
  tentar(async () => {
    const dados = dadosDoFormAluno(form);
    if (estado.editandoAlunoId) {
      const aluno = await api('PUT', `/api/alunos/${estado.editandoAlunoId}`, dados);
      sairEdicaoAluno();
      avisar(`Dados de ${aluno.nome} atualizados.`);
    } else {
      const aluno = await api('POST', '/api/alunos', dados);
      form.reset();
      avisar(`${aluno.nome} cadastrado.`);
    }
    await recarregarTudo();
  });
});

function editarAluno(id) {
  const a = estado.alunos.find(x => x.id === id);
  if (!a) throw new Error('Aluno não encontrado');
  const form = document.getElementById('form-aluno');
  form.nome.value = a.nome;
  form.cpf.value = a.cpf;
  form.telefone.value = a.telefone;
  form.email.value = a.email;
  form.dataDeNascimento.value = a.dataDeNascimento;
  form.objetivo.value = a.objetivo;
  estado.editandoAlunoId = id;
  document.getElementById('titulo-form-aluno').textContent = 'Editar aluno';
  const aviso = document.getElementById('aviso-edicao-aluno');
  aviso.textContent = `Editando: ${a.nome}`;
  aviso.classList.remove('oculto');
  document.getElementById('btn-salvar-aluno').textContent = 'Salvar alterações';
  document.getElementById('btn-cancelar-aluno').classList.remove('oculto');
  form.cpf.readOnly = true;
  form.cpf.setAttribute('aria-readonly', 'true');
  form.nome.focus();
  return carregarAlunos();
}

function sairEdicaoAluno() {
  estado.editandoAlunoId = null;
  document.getElementById('form-aluno').reset();
  document.getElementById('titulo-form-aluno').textContent = 'Cadastrar aluno';
  document.getElementById('aviso-edicao-aluno').classList.add('oculto');
  document.getElementById('btn-salvar-aluno').textContent = 'Cadastrar aluno';
  document.getElementById('btn-cancelar-aluno').classList.add('oculto');
  document.querySelector('#form-aluno [name=cpf]').readOnly = false;
  document.querySelector('#form-aluno [name=cpf]').removeAttribute('aria-readonly');
}

document.getElementById('btn-cancelar-aluno').addEventListener('click', () => {
  sairEdicaoAluno();
  tentar(carregarAlunos);
});

async function excluirAluno(id) {
  const a = estado.alunos.find(x => x.id === id);
  if (!a) throw new Error('Aluno não encontrado');
  if (a.possuiMatriculaAtiva) {
    throw new Error(`${a.nome} possui matrícula ativa. Cancele ou exclua a matrícula antes de excluir o aluno.`);
  }
  let mensagem = `Tem certeza que deseja excluir este aluno?\n\n${a.nome} — ${a.cpf}`;
  if (a.totalDeMatriculas > 0) {
    mensagem += `\n\nAtenção: o histórico dele (${a.totalDeMatriculas} matrícula(s) encerrada(s) e fichas de treino) também será removido.`;
  }
  if (!confirm(mensagem)) return;
  await api('DELETE', `/api/alunos/${id}`);
  if (estado.editandoAlunoId === id) sairEdicaoAluno();
  avisar(`${a.nome} excluído.`);
  await recarregarTudo();
}

async function verAluno(id) {
  const d = await api('GET', `/api/alunos/${id}`);
  const a = d.aluno;
  abrirJanela(`
    <h2>${esc(a.nome)} <button class="fechar" data-acao="fechar-janela" aria-label="Fechar">×</button></h2>
    <div class="cartao">
      <div class="linha-dado"><span>CPF</span><strong>${esc(a.cpf)}</strong></div>
      <div class="linha-dado"><span>Telefone</span><span>${esc(a.telefone)}</span></div>
      <div class="linha-dado"><span>E-mail</span><span>${esc(a.email)}</span></div>
      <div class="linha-dado"><span>Nascimento</span><span>${dataBR(a.dataDeNascimento)} (${a.idade} anos)</span></div>
      <div class="linha-dado"><span>Objetivo</span><span>${NOME_OBJETIVO[a.objetivo] || a.objetivo}</span></div>
    </div>
    <h3>Matrículas (${d.matriculas.length})</h3>
    ${d.matriculas.length ? d.matriculas.map(m => `
      <div class="cartao">
        <div class="linha-dado"><span><span class="etiqueta ${m.plano}">${esc(m.planoNome)}</span></span>
          <span class="etiqueta ${m.status}">${NOME_STATUS[m.status]}</span></div>
        <div class="linha-dado"><span>Valor</span><strong>${esc(m.valorMensal)}</strong></div>
        <div class="linha-dado"><span>Vigência</span><span>${dataBR(m.inicio)} até ${dataBR(m.fim)}</span></div>
        <div class="linha-dado"><span>Total pago</span><span>${esc(m.totalPago)}</span></div>
      </div>`).join('') : '<p class="vazio">Este aluno ainda não possui matrícula.</p>'}`);
}

/* =========================================================
   MATRICULAS  (Integrantes 3, 4 e 5)
   ========================================================= */

async function carregarPlanos() {
  estado.planos = await api('GET', '/api/planos');
  const alvo = document.getElementById('lista-planos');
  alvo.innerHTML = estado.planos.map(p => `
    <label class="plano" data-codigo="${p.codigo}">
      <input type="radio" name="plano" value="${p.codigo}">
      <span class="nome">${esc(p.nome)}</span>
      <span class="modalidade">${esc(p.duracao)}</span>
      <span class="valor">${esc(p.valor)}</span>
    </label>`).join('');
  alvo.querySelectorAll('input[name=plano]').forEach(radio =>
    radio.addEventListener('change', atualizarValorCalculado));
  atualizarValorCalculado();
}

function planoSelecionado() {
  const marcado = document.querySelector('#lista-planos input[name=plano]:checked');
  return marcado ? marcado.value : null;
}

/** Calculo automatico do valor conforme o plano escolhido (Integrante 3). */
function atualizarValorCalculado() {
  const codigo = planoSelecionado();
  document.querySelectorAll('#lista-planos .plano').forEach(l =>
    l.dataset.escolhido = String(l.dataset.codigo === codigo));
  const plano = estado.planos.find(p => p.codigo === codigo);
  document.getElementById('valor-calculado').textContent = plano ? plano.valor : '—';
}

function marcarPlano(codigo) {
  document.querySelectorAll('#lista-planos input[name=plano]').forEach(r => { r.checked = r.value === codigo; });
  atualizarValorCalculado();
}

function filtrosMatricula() {
  const params = new URLSearchParams();
  const busca = document.getElementById('busca-matricula').value.trim();
  const plano = document.getElementById('filtro-plano').value;
  const status = document.getElementById('filtro-status').value;
  if (busca) params.set('busca', busca);
  if (plano) params.set('plano', plano);
  if (status) params.set('status', status);
  return params.toString();
}

async function carregarMatriculas() {
  const qs = filtrosMatricula();
  estado.matriculas = await api('GET', '/api/matriculas' + (qs ? `?${qs}` : ''));
  desenharTabelaMatriculas(Boolean(qs));
}

function desenharTabelaMatriculas(filtrado) {
  const alvo = document.getElementById('tabela-matriculas');
  document.getElementById('contador-matriculas').textContent = `${estado.matriculas.length} matrícula(s)`;
  if (!estado.matriculas.length) {
    alvo.innerHTML = filtrado
      ? '<p class="vazio">Nenhuma matrícula encontrada com estes filtros.</p>'
      : '<p class="vazio">Nenhuma matrícula cadastrada. Use o formulário ao lado.</p>';
    return;
  }
  alvo.innerHTML = `<table><thead><tr>
      <th>#</th><th>Aluno</th><th>Plano</th><th>Valor</th><th>Vigência</th><th>Situação</th><th></th></tr></thead><tbody>
    ${estado.matriculas.map(m => `<tr class="${estado.editandoMatriculaId === m.id ? 'em-edicao' : ''}">
      <td>${m.id}</td>
      <td>${esc(m.alunoNome)}</td>
      <td><span class="etiqueta ${m.plano}">${esc(m.planoNome)}</span></td>
      <td><strong>${esc(m.valorMensal)}</strong></td>
      <td>${dataBR(m.inicio)}<div class="contador">até ${dataBR(m.fim)}</div></td>
      <td><span class="etiqueta ${m.status}">${NOME_STATUS[m.status]}</span><div class="contador financeiro-${m.statusFinanceiro}">${m.statusFinanceiro === 'REGULARIZADA' ? 'Financeiro regularizado' : 'Financeiro pendente'}</div></td>
      <td class="acoes-tabela">
        <button class="btn-mini" data-acao="editar-matricula" data-id="${m.id}">Editar</button>
        ${m.status !== 'CANCELADA' ? `<button class="btn-mini" data-acao="pagar-matricula" data-id="${m.id}">Pagar</button>
        <button class="btn-mini" data-acao="renovar-matricula" data-id="${m.id}">Renovar</button>
        <button class="btn-mini perigo" data-acao="cancelar-matricula" data-id="${m.id}">Cancelar</button>` : ''}
        <button class="btn-mini perigo" data-acao="excluir-matricula" data-id="${m.id}">Excluir</button>
      </td></tr>`).join('')}
    </tbody></table>`;
}

document.getElementById('busca-matricula').addEventListener('input', adiar(() => tentar(carregarMatriculas)));
document.getElementById('filtro-plano').addEventListener('change', () => tentar(carregarMatriculas));
document.getElementById('filtro-status').addEventListener('change', () => tentar(carregarMatriculas));

document.getElementById('form-matricula').addEventListener('submit', (evento) => {
  evento.preventDefault();
  tentar(async () => {
    const alunoId = document.getElementById('aluno-matricula').value;
    const plano = planoSelecionado();
    const inicio = document.getElementById('inicio-matricula').value;
    if (!alunoId) throw new Error('Selecione o aluno da matrícula');
    if (!plano) throw new Error('Selecione um plano (Semanal ou Mensal)');
    if (!inicio) throw new Error('Informe a data de início');
    const corpo = { alunoId: Number(alunoId), plano, inicio };
    if (estado.editandoMatriculaId) {
      corpo.status = document.getElementById('status-matricula').value;
      const m = await api('PUT', `/api/matriculas/${estado.editandoMatriculaId}`, corpo);
      sairEdicaoMatricula();
      avisar(`Matrícula #${m.id} salva: ${m.planoNome} por ${m.valorMensal}.`);
    } else {
      const m = await api('POST', '/api/matriculas', corpo);
      limparFormMatricula();
      avisar(`Matrícula criada: ${m.alunoNome} no ${m.planoNome} por ${m.valorMensal}.`);
    }
    await recarregarTudo();
  });
});

function limparFormMatricula() {
  document.getElementById('aluno-matricula').value = '';
  marcarPlano(null);
  document.getElementById('inicio-matricula').value = hojeISO();
  document.getElementById('status-matricula').value = 'ATIVA';
}

function editarMatricula(id) {
  const m = estado.matriculas.find(x => x.id === id);
  if (!m) throw new Error('Matrícula não encontrada');
  estado.editandoMatriculaId = id;
  document.getElementById('aluno-matricula').value = String(m.alunoId);
  marcarPlano(m.plano);
  document.getElementById('inicio-matricula').value = m.inicio;
  document.getElementById('status-matricula').value = m.status;
  document.getElementById('campo-status').classList.remove('oculto');
  document.getElementById('titulo-form-matricula').textContent = `Editar matrícula #${id}`;
  const aviso = document.getElementById('aviso-edicao-matricula');
  aviso.textContent = `Atual: ${m.planoNome} · ${m.valorMensal}. Ao trocar o plano, o valor é recalculado e salvo.`;
  aviso.classList.remove('oculto');
  document.getElementById('btn-salvar-matricula').textContent = 'Salvar alterações';
  document.getElementById('btn-cancelar-matricula').classList.remove('oculto');
  desenharTabelaMatriculas(Boolean(filtrosMatricula()));
}

function sairEdicaoMatricula() {
  estado.editandoMatriculaId = null;
  limparFormMatricula();
  document.getElementById('campo-status').classList.add('oculto');
  document.getElementById('titulo-form-matricula').textContent = 'Nova matrícula';
  document.getElementById('aviso-edicao-matricula').classList.add('oculto');
  document.getElementById('btn-salvar-matricula').textContent = 'Matricular';
  document.getElementById('btn-cancelar-matricula').classList.add('oculto');
}

document.getElementById('btn-cancelar-matricula').addEventListener('click', () => {
  sairEdicaoMatricula();
  desenharTabelaMatriculas(Boolean(filtrosMatricula()));
});

async function excluirMatricula(id) {
  const m = estado.matriculas.find(x => x.id === id);
  if (!m) throw new Error('Matrícula não encontrada');
  if (!confirm(`Tem certeza que deseja excluir esta matrícula?\n\n#${m.id} — ${m.alunoNome} · ${m.planoNome} · ${m.valorMensal}`)) return;
  await api('DELETE', `/api/matriculas/${id}`);
  if (estado.editandoMatriculaId === id) sairEdicaoMatricula();
  avisar(`Matrícula #${id} excluída.`);
  await recarregarTudo();
}

async function renovarMatricula(id) {
  const m = await api('POST', `/api/matriculas/${id}/renovacoes`);
  avisar(`Matrícula renovada até ${dataBR(m.fim)}.`);
  await recarregarTudo();
}

function cancelarMatricula(id) {
  const m = estado.matriculas.find(x => x.id === id);
  if (!m) throw new Error('Matrícula não encontrada');
  abrirJanela(`
    <h2>Cancelar matrícula <button class="fechar" data-acao="fechar-janela" aria-label="Fechar">×</button></h2>
    <p class="dica">${esc(m.alunoNome)} · ${esc(m.planoNome)} · Esta ação registra a data e a justificativa no histórico.</p>
    <form id="form-cancelamento" novalidate>
      <label>Justificativa*<textarea name="justificativa" required minlength="5" rows="4" placeholder="Informe o motivo do cancelamento"></textarea></label>
      <button type="submit" class="primario">Confirmar cancelamento</button>
    </form>`);
  document.getElementById('form-cancelamento').addEventListener('submit', evento => {
    evento.preventDefault();
    tentar(async () => {
      const justificativa = evento.target.justificativa.value.trim();
      if (justificativa.length < 5) throw new Error('Informe uma justificativa com pelo menos 5 caracteres');
      await api('POST', `/api/matriculas/${id}/cancelamento`, { justificativa });
      fecharJanela();
      avisar('Matrícula cancelada e justificativa registrada.');
      await recarregarTudo();
    });
  });
}

function pagarMatricula(id) {
  const m = estado.matriculas.find(x => x.id === id);
  if (!m) throw new Error('Matrícula não encontrada');
  abrirJanela(`
    <h2>Registrar pagamento <button class="fechar" data-acao="fechar-janela" aria-label="Fechar">×</button></h2>
    <p class="dica">${esc(m.alunoNome)} · ${esc(m.planoNome)} · total já pago: ${esc(m.totalPago)}</p>
    <form id="form-pagamento" novalidate>
      <label>Valor (mínimo ${esc(m.valorMensal)})<input name="valor" type="text" value="${m.valorNumerico.toFixed(2).replace('.', ',')}"></label>
      <div class="duas-colunas"><label>Parcela<input name="numeroParcela" type="number" min="1" step="1" placeholder="Ex.: 1"></label>
      <label>Vencimento<input name="vencimento" type="date"></label></div>
      <label>Forma de pagamento
        <select name="forma"><option value="PIX">PIX</option><option value="CARTAO">Cartão</option><option value="DINHEIRO">Dinheiro</option></select>
      </label>
      <button type="submit" class="primario">Confirmar pagamento</button>
    </form>`);
  document.getElementById('form-pagamento').addEventListener('submit', (evento) => {
    evento.preventDefault();
    const form = evento.target;
    tentar(async () => {
      const valor = form.valor.value.trim();
      if (!valor) throw new Error('Informe o valor do pagamento');
      const numeroParcela = form.numeroParcela.value ? Number(form.numeroParcela.value) : null;
      const vencimento = form.vencimento.value || null;
      await api('POST', `/api/matriculas/${id}/pagamentos`, {
        valor, formaDePagamento: form.forma.value, numeroParcela, vencimento
      });
      fecharJanela();
      avisar('Pagamento registrado.');
      await recarregarTudo();
    });
  });
}

/* =========================================================
   Acoes das tabelas e janela (delegacao de eventos)
   ========================================================= */

const ACOES = {
  'ver-aluno': verAluno,
  'editar-aluno': editarAluno,
  'excluir-aluno': excluirAluno,
  'editar-matricula': editarMatricula,
  'excluir-matricula': excluirMatricula,
  'renovar-matricula': renovarMatricula,
  'cancelar-matricula': cancelarMatricula,
  'pagar-matricula': pagarMatricula,
  'fechar-janela': fecharJanela
};

document.addEventListener('click', (evento) => {
  const botao = evento.target.closest('[data-acao]');
  if (!botao) return;
  const acao = ACOES[botao.dataset.acao];
  if (!acao) return;
  tentar(() => acao(Number(botao.dataset.id)));
});

function abrirJanela(html) {
  const janela = document.getElementById('janela');
  document.getElementById('conteudo-janela').innerHTML = html;
  if (!janela.open) janela.showModal();
}

function fecharJanela() {
  const janela = document.getElementById('janela');
  if (janela.open) janela.close();
}

/* =========================================================
   TREINOS
   ========================================================= */

document.getElementById('aluno-treino').addEventListener('change', () => tentar(listarFichas));

document.getElementById('btn-gerar-ficha').addEventListener('click', () => tentar(async () => {
  const id = document.getElementById('aluno-treino').value;
  if (!id) throw new Error('Selecione um aluno');
  estado.ficha = await api('POST', `/api/treinos/fichas?alunoId=${id}`);
  desenharFicha();
  avisar(`Ficha de ${estado.ficha.objetivo.toLowerCase()} gerada.`);
}));

async function listarFichas() {
  const seletor = document.getElementById('aluno-treino');
  if (!seletor.value) {
    estado.ficha = null;
    document.getElementById('ficha').innerHTML = '<p class="vazio">Selecione um aluno.</p>';
    return;
  }
  const fichas = await api('GET', `/api/treinos/fichas?alunoId=${seletor.value}`);
  estado.ficha = fichas[0] || null;
  desenharFicha();
}

function desenharFicha() {
  const alvo = document.getElementById('ficha');
  const ficha = estado.ficha;
  if (!ficha) {
    alvo.innerHTML = '<p class="vazio">Nenhuma ficha ativa. Gere uma ao lado (o aluno precisa de matrícula ativa).</p>';
    return;
  }
  const proximo = ficha.itens.find(i => !i.concluido);
  alvo.innerHTML = `
    <div class="cartao">
      <h3>${esc(ficha.objetivo)}</h3>
      <div class="linha-dado"><span>Válida até</span><span>${dataBR(ficha.validaAte)}</span></div>
      <div class="linha-dado"><span>Itens pendentes</span><strong>${ficha.pendentes} de ${ficha.itens.length}</strong></div>
    </div>
    ${ficha.itens.map(i => `
      <div class="item ${i.concluido ? 'feito' : ''} ${proximo && proximo.posicao === i.posicao ? 'proximo' : ''}">
        <div>
          <div class="exercicio">${esc(i.exercicio)}</div>
          <div class="grupo">${esc(i.grupoMuscular)}</div>
        </div>
        <span class="carga">${i.series}x${i.repeticoes} · ${i.descansoEmSegundos}s</span>
        ${i.concluido ? '' : `<button class="secundario" data-posicao="${i.posicao}">Concluir</button>`}
      </div>`).join('')}`;
}

document.getElementById('ficha').addEventListener('click', (evento) => {
  const botao = evento.target.closest('button[data-posicao]');
  if (!botao) return;
  tentar(async () => {
    estado.ficha = await api('POST', `/api/treinos/fichas/${estado.ficha.id}/itens/${botao.dataset.posicao}/conclusao`);
    desenharFicha();
    const proximo = await api('GET', `/api/treinos/fichas/${estado.ficha.id}/proximo`);
    avisar(proximo ? `Próximo exercício: ${proximo.exercicio}.` : 'Treino concluído.');
  });
});

/* =========================================================
   EVENTOS
   ========================================================= */

document.getElementById('btn-eventos').addEventListener('click', () => tentar(carregarEventos));

async function carregarEventos() {
  const eventos = await api('GET', '/api/eventos');
  const alvo = document.getElementById('lista-eventos');
  if (!eventos.length) {
    alvo.innerHTML = '<p class="vazio">Nenhum evento ainda. Cadastre um aluno ou uma matrícula.</p>';
    return;
  }
  alvo.innerHTML = `<table><thead><tr>
      <th>Quando</th><th>Evento</th><th>Detalhe</th></tr></thead><tbody>
    ${eventos.map(e => `<tr>
      <td>${esc(e.momento)}</td><td>${esc(e.tipo)}</td><td>${esc(e.detalhe)}</td></tr>`).join('')}
    </tbody></table>`;
}

/* =========================================================
   Inicio
   ========================================================= */

/* A visao da equipe so carrega depois do login (ver acesso.js). */
window.iniciarEquipe = () => tentar(async () => {
  document.getElementById('inicio-matricula').value = hojeISO();
  await carregarPlanos();
  await recarregarTudo();
});
