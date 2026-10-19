'use strict';

/* =========================================================
   Novas funcionalidades da EQUIPE
   (catalogo de exercicios, solicitacoes dos alunos, equipamentos,
    avisos de vencimento, cancelamento com justificativa, estorno, feedbacks)
   Usa api(), tentar(), avisar(), esc(), dataBR(), abrirJanela(), fecharJanela(),
   estado e recarregarTudo() de app.js.
   ========================================================= */

const NOME_GRUPO = {
  PEITO: 'Peito', COSTAS: 'Costas', PERNAS: 'Pernas', OMBROS: 'Ombros',
  BRACOS: 'Braços', CORE: 'Core', CARDIO: 'Cardio'
};
const NOME_SITUACAO = {
  SOLICITADA: 'Solicitada', NOVO_HORARIO_PROPOSTO: 'Novo horário proposto', CONFIRMADA: 'Confirmada',
  CANCELADA: 'Cancelada', PENDENTE: 'Pendente', APROVADA: 'Aprovada', REJEITADA: 'Rejeitada',
  ABERTO: 'Aberto', EM_ATENDIMENTO: 'Em atendimento', CONCLUIDO: 'Concluído',
  ABERTA: 'Aberta', EM_ANDAMENTO: 'Em andamento', RESOLVIDA: 'Resolvida',
  PROCEDENTE: 'Procedente', IMPROCEDENTE: 'Improcedente'
};
const SITUACAO_COR = {
  SOLICITADA: 'VENCIDA', NOVO_HORARIO_PROPOSTO: 'VENCIDA', PENDENTE: 'VENCIDA', ABERTO: 'VENCIDA',
  ABERTA: 'VENCIDA', EM_ATENDIMENTO: 'MENSAL', EM_ANDAMENTO: 'MENSAL',
  CONFIRMADA: 'ATIVA', APROVADA: 'ATIVA', CONCLUIDO: 'ATIVA', RESOLVIDA: 'ATIVA', PROCEDENTE: 'ATIVA',
  CANCELADA: 'CANCELADA', REJEITADA: 'CANCELADA', IMPROCEDENTE: 'CANCELADA'
};

function situacao(status) {
  return `<span class="etiqueta ${SITUACAO_COR[status] || ''}">${esc(NOME_SITUACAO[status] || status)}</span>`;
}

function dataHoraBR(iso) {
  if (!iso) return '—';
  const [data, hora = ''] = String(iso).split('T');
  return `${dataBR(data)} ${hora.slice(0, 5)}`.trim();
}

function cabecalhoJanela(titulo) {
  return `<h2>${esc(titulo)} <button class="fechar" data-acao="fechar-janela" aria-label="Fechar">×</button></h2>`;
}

/** Abre uma janela com um formulario e executa a acao ao enviar. */
function janelaComFormulario(titulo, dica, camposHtml, rotulo, aoEnviar) {
  abrirJanela(`${cabecalhoJanela(titulo)}
    ${dica ? `<p class="dica">${dica}</p>` : ''}
    <form id="form-janela" novalidate>${camposHtml}
      <button type="submit" class="primario">${esc(rotulo)}</button>
    </form>`);
  document.getElementById('form-janela').addEventListener('submit', (evento) => {
    evento.preventDefault();
    const form = evento.target;
    tentar(async () => {
      await aoEnviar(form);
      fecharJanela();
    });
  });
}

/* ---------- acoes por delegacao (data-x) ---------- */

const ACOES_EXTRA = {};

document.addEventListener('click', (evento) => {
  const botao = evento.target.closest('[data-x]');
  if (!botao) return;
  const acao = ACOES_EXTRA[botao.dataset.x];
  if (acao) tentar(() => acao(Number(botao.dataset.id), botao.dataset));
});

/* =========================================================
   MATRICULAS: detalhes, cancelamento com justificativa e estorno
   ========================================================= */

ACOES['cancelar-matricula'] = (id) => {
  const m = estado.matriculas.find(x => x.id === id);
  janelaComFormulario('Cancelar matrícula',
    `${esc(m ? m.alunoNome : '')} · a data da solicitação (${dataBR(hojeISO())}) será registrada.
     Se faltarem menos de 30 dias para o fim do contrato (${m ? dataBR(m.fim) : ''}), é cobrada a taxa de uma mensalidade.`,
    `<label>Justificativa* (mín. 10 caracteres)<textarea name="texto" rows="4" maxlength="500"></textarea></label>`,
    'Confirmar cancelamento',
    async (form) => {
      const texto = form.texto.value.trim();
      if (texto.length < 10) throw new Error('Informe a justificativa (mínimo de 10 caracteres)');
      const cancelada = await api('POST', `/api/matriculas/${id}/cancelamento`, { texto });
      avisar(`Matrícula cancelada. Taxa de cancelamento: ${cancelada.taxaDeCancelamento}.`);
      await recarregarTudo();
    });
};

ACOES['detalhar-matricula'] = async (id) => {
  const m = await api('GET', `/api/matriculas/${id}`);
  const pagamentos = m.pagamentos.length
    ? `<table><thead><tr><th>Data</th><th>Valor</th><th>Forma</th><th>Situação</th><th></th></tr></thead><tbody>
      ${m.pagamentos.map(p => `<tr>
        <td>${dataBR(p.data)}</td><td><strong>${esc(p.valor)}</strong></td><td>${esc(p.formaDePagamento)}</td>
        <td>${p.estornado
          ? `<span class="etiqueta CANCELADA">Estornado</span><div class="contador">${dataBR(p.dataDoEstorno)} · ${esc(p.motivoDoEstorno)}</div>`
          : '<span class="etiqueta ATIVA">Pago</span>'}</td>
        <td>${p.estornado ? '' : `<button class="btn-mini perigo" data-x="estornar" data-id="${m.id}" data-indice="${p.indice}">Estornar</button>`}</td>
      </tr>`).join('')}</tbody></table>`
    : '<p class="vazio">Nenhum pagamento registrado.</p>';
  abrirJanela(`${cabecalhoJanela(`Matrícula #${m.id}`)}
    <p class="dica">${esc(m.alunoNome)} · ${esc(m.planoNome)} · ${esc(m.valorMensal)} · ${dataBR(m.inicio)} a ${dataBR(m.fim)}</p>
    <p><span class="etiqueta ${m.status}">${NOME_STATUS[m.status]}</span> · Total pago: <strong>${esc(m.totalPago)}</strong></p>
    ${m.justificativaDoCancelamento ? `<div class="cartao"><strong>Cancelamento solicitado em ${dataBR(m.dataDaSolicitacaoDeCancelamento)}</strong>
      <p class="dica">${esc(m.justificativaDoCancelamento)}</p>
      <p>Taxa de cancelamento: <strong>${esc(m.taxaDeCancelamento || 'R$ 0,00')}</strong></p></div>` : ''}
    <h3>Pagamentos</h3>${pagamentos}`);
};

ACOES_EXTRA.estornar = (id, dados) => {
  janelaComFormulario('Estornar pagamento', 'O valor deixa de contar no total pago. Um pagamento só pode ser estornado uma vez.',
    `<label>Motivo do estorno* (mín. 10 caracteres)<textarea name="texto" rows="3" maxlength="300"></textarea></label>`,
    'Confirmar estorno',
    async (form) => {
      const texto = form.texto.value.trim();
      if (texto.length < 10) throw new Error('Informe o motivo do estorno (mínimo de 10 caracteres)');
      await api('POST', `/api/matriculas/${id}/pagamentos/${dados.indice}/estorno`, { texto });
      avisar('Pagamento estornado.');
      await recarregarTudo();
    });
};

/* ---------- avisos de vencimento ---------- */

async function carregarAvisos() {
  const avisos = await api('GET', '/api/notificacoes');
  const alvo = document.getElementById('lista-avisos');
  alvo.innerHTML = avisos.length
    ? avisos.slice(0, 6).map(a => `<div class="cartao aviso-item">
        <strong>${esc(a.alunoNome)}</strong> · vence ${dataBR(a.vencimento)}
        <div class="contador">Enviado ${dataHoraBR(a.enviadaEm)} · ${a.lida ? 'lido pelo aluno' : 'não lido'}</div></div>`).join('')
    : '<p class="vazio">Nenhum aviso enviado ainda.</p>';
}

document.getElementById('btn-verificar-vencimentos').addEventListener('click', () => tentar(async () => {
  const r = await api('POST', '/api/notificacoes/verificacao');
  avisar(r.avisosEnviados ? `${r.avisosEnviados} aviso(s) de vencimento enviado(s).` : 'Nenhum aviso novo: ninguém para avisar hoje.');
  await carregarAvisos();
}));

/* =========================================================
   CATALOGO DE EXERCICIOS
   ========================================================= */

estado.exercicios = [];
estado.editandoExercicioId = null;

async function carregarExercicios() {
  estado.exercicios = await api('GET', '/api/exercicios');
  desenharExercicios();
}

function desenharExercicios() {
  const busca = document.getElementById('busca-exercicio').value.trim().toLowerCase();
  const grupo = document.getElementById('filtro-grupo').value;
  const lista = estado.exercicios.filter(e =>
    (!grupo || e.grupoMuscular === grupo) &&
    (!busca || e.nome.toLowerCase().includes(busca) || (e.equipamento || '').toLowerCase().includes(busca)));
  const ativos = estado.exercicios.filter(e => e.ativo).length;
  document.getElementById('contador-exercicios').textContent = `${ativos} ativo(s) de ${estado.exercicios.length}`;
  const alvo = document.getElementById('tabela-exercicios');
  if (!lista.length) {
    alvo.innerHTML = '<p class="vazio">Nenhum exercício encontrado.</p>';
    return;
  }
  alvo.innerHTML = `<table><thead><tr><th>Exercício</th><th>Grupo</th><th>Equipamento</th><th>Situação</th><th></th></tr></thead><tbody>
    ${lista.map(e => `<tr class="${estado.editandoExercicioId === e.id ? 'em-edicao' : ''} ${e.ativo ? '' : 'inativo'}">
      <td><strong>${esc(e.nome)}</strong>${e.usadoEmFichas ? '<div class="contador">usado em fichas</div>' : ''}</td>
      <td>${esc(NOME_GRUPO[e.grupoMuscular] || e.grupoMuscular)}</td>
      <td>${esc(e.equipamento || 'Peso corporal')}</td>
      <td><span class="etiqueta ${e.ativo ? 'ATIVA' : 'CANCELADA'}">${e.ativo ? 'Ativo' : 'Inativo'}</span></td>
      <td class="acoes-tabela">
        <button class="btn-mini" data-x="editar-exercicio" data-id="${e.id}">Editar</button>
        ${e.ativo
          ? `<button class="btn-mini" data-x="desativar-exercicio" data-id="${e.id}">Desativar</button>`
          : `<button class="btn-mini" data-x="reativar-exercicio" data-id="${e.id}">Reativar</button>`}
        ${e.usadoEmFichas ? '' : `<button class="btn-mini perigo" data-x="excluir-exercicio" data-id="${e.id}">Excluir</button>`}
      </td></tr>`).join('')}
    </tbody></table>`;
}

document.getElementById('busca-exercicio').addEventListener('input', adiar(desenharExercicios, 150));
document.getElementById('filtro-grupo').addEventListener('change', desenharExercicios);

document.getElementById('form-exercicio').addEventListener('submit', (evento) => {
  evento.preventDefault();
  const form = evento.target;
  tentar(async () => {
    const corpo = { nome: form.nome.value.trim(), grupoMuscular: form.grupoMuscular.value, equipamento: form.equipamento.value.trim() };
    if (corpo.nome.length < 3) throw new Error('Informe o nome do exercício (mínimo de 3 caracteres)');
    if (estado.editandoExercicioId) {
      await api('PUT', `/api/exercicios/${estado.editandoExercicioId}`, corpo);
      avisar('Exercício atualizado.');
    } else {
      await api('POST', '/api/exercicios', corpo);
      avisar('Exercício incluído no catálogo.');
    }
    sairEdicaoExercicio();
    await carregarExercicios();
  });
});

function sairEdicaoExercicio() {
  estado.editandoExercicioId = null;
  document.getElementById('form-exercicio').reset();
  document.getElementById('titulo-form-exercicio').textContent = 'Novo exercício';
  document.getElementById('btn-salvar-exercicio').textContent = 'Incluir no catálogo';
  document.getElementById('btn-cancelar-exercicio').classList.add('oculto');
  document.getElementById('aviso-edicao-exercicio').classList.add('oculto');
  desenharExercicios();
}

document.getElementById('btn-cancelar-exercicio').addEventListener('click', sairEdicaoExercicio);

ACOES_EXTRA['editar-exercicio'] = (id) => {
  const e = estado.exercicios.find(x => x.id === id);
  if (!e) return;
  const form = document.getElementById('form-exercicio');
  estado.editandoExercicioId = id;
  form.nome.value = e.nome;
  form.grupoMuscular.value = e.grupoMuscular;
  form.equipamento.value = e.equipamento || '';
  document.getElementById('titulo-form-exercicio').textContent = 'Editar exercício';
  document.getElementById('btn-salvar-exercicio').textContent = 'Salvar alterações';
  document.getElementById('btn-cancelar-exercicio').classList.remove('oculto');
  const aviso = document.getElementById('aviso-edicao-exercicio');
  aviso.textContent = e.usadoEmFichas
    ? `Editando "${e.nome}". Já usado em fichas: nome e grupo não podem mudar.`
    : `Editando "${e.nome}".`;
  aviso.classList.remove('oculto');
  desenharExercicios();
  form.nome.focus();
};

ACOES_EXTRA['desativar-exercicio'] = async (id) => {
  await api('POST', `/api/exercicios/${id}/desativacao`);
  avisar('Exercício desativado: não entra mais em fichas novas.');
  await carregarExercicios();
};

ACOES_EXTRA['reativar-exercicio'] = async (id) => {
  await api('POST', `/api/exercicios/${id}/reativacao`);
  avisar('Exercício reativado.');
  await carregarExercicios();
};

ACOES_EXTRA['excluir-exercicio'] = async (id) => {
  const e = estado.exercicios.find(x => x.id === id);
  if (!confirm(`Excluir "${e ? e.nome : 'exercício'}" do catálogo?`)) return;
  await api('DELETE', `/api/exercicios/${id}`);
  avisar('Exercício excluído.');
  await carregarExercicios();
};

/* =========================================================
   SOLICITACOES DOS ALUNOS
   ========================================================= */

estado.solicitacoes = { avaliacoes: [], atendimentos: [], substituicoes: [], contestacoes: [] };

async function buscarSolicitacoes() {
  const [avaliacoes, atendimentos, substituicoes, contestacoes] = await Promise.all([
    api('GET', '/api/avaliacoes'), api('GET', '/api/atendimentos'),
    api('GET', '/api/substituicoes'), api('GET', '/api/contestacoes')]);
  estado.solicitacoes = { avaliacoes, atendimentos, substituicoes, contestacoes };
  return estado.solicitacoes;
}

function pendencias(s) {
  return {
    avaliacoes: s.avaliacoes.filter(a => a.status === 'SOLICITADA').length,
    atendimentos: s.atendimentos.filter(a => a.status !== 'CONCLUIDO').length,
    substituicoes: s.substituicoes.filter(a => a.status === 'PENDENTE').length,
    contestacoes: s.contestacoes.filter(a => a.status === 'ABERTA').length
  };
}

async function contarPendencias() {
  const p = pendencias(await buscarSolicitacoes());
  const total = p.avaliacoes + p.atendimentos + p.substituicoes + p.contestacoes;
  const bolinha = document.getElementById('bolinha-solicitacoes');
  bolinha.textContent = total;
  bolinha.classList.toggle('oculto', total === 0);
}

async function carregarSolicitacoes() {
  const s = await buscarSolicitacoes();
  const p = pendencias(s);
  document.getElementById('indicadores-solicitacoes').innerHTML = `
    <div class="indicador ambar"><span>Avaliações a responder</span><strong>${p.avaliacoes}</strong></div>
    <div class="indicador ambar"><span>Atendimentos em aberto</span><strong>${p.atendimentos}</strong></div>
    <div class="indicador ambar"><span>Substituições pendentes</span><strong>${p.substituicoes}</strong></div>
    <div class="indicador vermelho"><span>Contestações em análise</span><strong>${p.contestacoes}</strong></div>`;
  const total = p.avaliacoes + p.atendimentos + p.substituicoes + p.contestacoes;
  document.getElementById('bolinha-solicitacoes').textContent = total;
  document.getElementById('bolinha-solicitacoes').classList.toggle('oculto', total === 0);

  tabela('tabela-avaliacoes', s.avaliacoes, ['Aluno', 'Pedido para', 'Agendada', 'Situação'], a => `
    <td>${esc(a.alunoNome)}</td><td>${dataHoraBR(a.dataDesejada)}</td>
    <td>${a.dataAgendada ? dataHoraBR(a.dataAgendada) : (a.dataProposta ? `proposta: ${dataHoraBR(a.dataProposta)}` : '—')}</td>
    <td>${situacao(a.status)}</td>
    <td class="acoes-tabela">${a.status === 'SOLICITADA'
      ? `<button class="btn-mini" data-x="confirmar-avaliacao" data-id="${a.id}">Confirmar</button>
         <button class="btn-mini" data-x="propor-avaliacao" data-id="${a.id}">Propor horário</button>` : ''}</td>`);

  tabela('tabela-atendimentos', s.atendimentos, ['Aluno', 'Pedido', 'Instrutor', 'Situação'], a => `
    <td>${esc(a.alunoNome)}<div class="contador">${dataHoraBR(a.abertaEm)}</div></td>
    <td>${a.exercicio ? `<strong>${esc(a.exercicio)}</strong><br>` : ''}${esc(a.motivo)}
      ${a.orientacao ? `<div class="contador">Orientação: ${esc(a.orientacao)}</div>` : ''}</td>
    <td>${esc(a.instrutor || '—')}</td><td>${situacao(a.status)}</td>
    <td class="acoes-tabela">${a.status === 'ABERTO'
      ? `<button class="btn-mini" data-x="assumir-atendimento" data-id="${a.id}">Assumir</button>`
      : a.status === 'EM_ATENDIMENTO'
        ? `<button class="btn-mini" data-x="concluir-atendimento" data-id="${a.id}">Concluir</button>` : ''}</td>`);

  tabela('tabela-substituicoes', s.substituicoes, ['Aluno', 'Exercício', 'Motivo', 'Situação'], a => `
    <td>${esc(a.alunoNome)}</td>
    <td><strong>${esc(a.exercicioAtual)}</strong>${a.exercicioNovo ? ` → ${esc(a.exercicioNovo)}` : ''}
      <div class="contador">${esc(NOME_GRUPO[a.grupoMuscular] || '')}</div></td>
    <td>${esc(a.motivo)}${a.resposta ? `<div class="contador">Resposta: ${esc(a.resposta)}</div>` : ''}</td>
    <td>${situacao(a.status)}</td>
    <td class="acoes-tabela">${a.status === 'PENDENTE'
      ? `<button class="btn-mini" data-x="aprovar-substituicao" data-id="${a.id}">Aprovar</button>
         <button class="btn-mini perigo" data-x="rejeitar-substituicao" data-id="${a.id}">Rejeitar</button>` : ''}</td>`);

  tabela('tabela-contestacoes', s.contestacoes, ['Aluno', 'Cobrança', 'Motivo', 'Situação'], a => `
    <td>${esc(a.alunoNome)}<div class="contador">matrícula #${a.matriculaId}</div></td>
    <td><strong>${esc(a.valor)}</strong></td>
    <td>${esc(a.motivo)}${a.parecer ? `<div class="contador">Parecer: ${esc(a.parecer)}</div>` : ''}</td>
    <td>${situacao(a.status)}</td>
    <td class="acoes-tabela">${a.status === 'ABERTA'
      ? `<button class="btn-mini" data-x="analisar-contestacao" data-id="${a.id}">Analisar</button>` : ''}</td>`);
}

function tabela(alvoId, lista, colunas, linha) {
  const alvo = document.getElementById(alvoId);
  alvo.innerHTML = lista.length
    ? `<table><thead><tr>${colunas.map(c => `<th>${c}</th>`).join('')}<th></th></tr></thead>
       <tbody>${lista.map(item => `<tr>${linha(item)}</tr>`).join('')}</tbody></table>`
    : '<p class="vazio">Nenhuma solicitação.</p>';
}

async function depoisDeResponder(mensagem) {
  avisar(mensagem);
  await carregarSolicitacoes();
}

ACOES_EXTRA['confirmar-avaliacao'] = (id) => janelaComFormulario('Confirmar avaliação física',
  'O horário pedido pelo aluno será confirmado.',
  `<label>Observação (opcional)<input name="texto" type="text" maxlength="300" placeholder="Venha com roupa leve"></label>`,
  'Confirmar', async (form) => {
    await api('POST', `/api/avaliacoes/${id}/confirmacao`, { texto: form.texto.value.trim() });
    await depoisDeResponder('Avaliação confirmada.');
  });

ACOES_EXTRA['propor-avaliacao'] = (id) => janelaComFormulario('Propor outro horário',
  'Funcionamento: 6h às 21h, em até 60 dias. O aluno aceita ou cancela pela área dele.',
  `<label>Novo horário* (horários redondos: :00 ou :30)<input name="data" type="datetime-local" step="1800"></label>
   <label>Observação (opcional)<input name="obs" type="text" maxlength="300"></label>`,
  'Enviar proposta', async (form) => {
    if (!form.data.value) throw new Error('Informe o novo horário');
    await api('POST', `/api/avaliacoes/${id}/proposta`, { novaDataHora: form.data.value, observacao: form.obs.value.trim() });
    await depoisDeResponder('Proposta de horário enviada ao aluno.');
  });

ACOES_EXTRA['assumir-atendimento'] = async (id) => {
  await api('POST', `/api/atendimentos/${id}/assuncao`);
  await depoisDeResponder('Atendimento assumido.');
};

ACOES_EXTRA['concluir-atendimento'] = (id) => janelaComFormulario('Concluir atendimento', null,
  `<label>Orientação dada ao aluno* (mín. 10 caracteres)<textarea name="texto" rows="4" maxlength="500"></textarea></label>`,
  'Concluir', async (form) => {
    await api('POST', `/api/atendimentos/${id}/conclusao`, { texto: form.texto.value.trim() });
    await depoisDeResponder('Atendimento concluído.');
  });

ACOES_EXTRA['aprovar-substituicao'] = async (id) => {
  const s = estado.solicitacoes.substituicoes.find(x => x.id === id);
  const catalogo = await api('GET', '/api/exercicios');
  const opcoes = catalogo.filter(e => e.ativo && e.grupoMuscular === s.grupoMuscular
    && e.nome.toLowerCase() !== s.exercicioAtual.toLowerCase());
  if (!opcoes.length) throw new Error('Não há outro exercício ativo deste grupo no catálogo');
  janelaComFormulario('Aprovar substituição',
    `${esc(s.alunoNome)} · trocar <strong>${esc(s.exercicioAtual)}</strong> (${esc(NOME_GRUPO[s.grupoMuscular] || '')}). Séries e repetições são mantidas.`,
    `<label>Exercício substituto*<select name="exercicio">${opcoes.map(e =>
      `<option value="${e.id}">${esc(e.nome)}${e.equipamento ? ` · ${esc(e.equipamento)}` : ''}</option>`).join('')}</select></label>
     <label>Observação (opcional)<input name="obs" type="text" maxlength="300"></label>`,
    'Aprovar e trocar na ficha', async (form) => {
      await api('POST', `/api/substituicoes/${id}/aprovacao`, { exercicioId: Number(form.exercicio.value), observacao: form.obs.value.trim() });
      await depoisDeResponder('Substituição aprovada: a ficha do aluno foi atualizada.');
    });
};

ACOES_EXTRA['rejeitar-substituicao'] = (id) => janelaComFormulario('Rejeitar substituição', null,
  `<label>Justificativa* (mín. 10 caracteres)<textarea name="texto" rows="3" maxlength="300"></textarea></label>`,
  'Rejeitar', async (form) => {
    await api('POST', `/api/substituicoes/${id}/rejeicao`, { texto: form.texto.value.trim() });
    await depoisDeResponder('Substituição rejeitada.');
  });

ACOES_EXTRA['analisar-contestacao'] = (id) => janelaComFormulario('Analisar contestação',
  'Se for procedente, o pagamento contestado é estornado automaticamente.',
  `<label>Resultado*<select name="resultado"><option value="false">Improcedente</option><option value="true">Procedente (estornar)</option></select></label>
   <label>Parecer* (mín. 10 caracteres)<textarea name="texto" rows="4" maxlength="500"></textarea></label>`,
  'Registrar análise', async (form) => {
    await api('POST', `/api/contestacoes/${id}/analise`, { procedente: form.resultado.value === 'true', parecer: form.texto.value.trim() });
    await depoisDeResponder('Análise registrada.');
    await recarregarTudo();
  });

/* =========================================================
   EQUIPAMENTOS
   ========================================================= */

estado.ocorrencias = [];

async function carregarOcorrencias() {
  estado.ocorrencias = await api('GET', '/api/ocorrencias');
  desenharOcorrencias();
}

function desenharOcorrencias() {
  const filtro = document.getElementById('filtro-ocorrencia').value;
  const lista = estado.ocorrencias.filter(o => !filtro || o.status === filtro);
  const pendentes = estado.ocorrencias.filter(o => o.status !== 'RESOLVIDA').length;
  document.getElementById('contador-ocorrencias').textContent = `${pendentes} pendente(s)`;
  tabela('tabela-ocorrencias', lista, ['Equipamento', 'Problema', 'Atualizada', 'Situação'], o => `
    <td><strong>${esc(o.equipamento)}</strong><div class="contador">aberta ${dataHoraBR(o.abertaEm)}</div></td>
    <td>${esc(o.descricao)}${o.solucao ? `<div class="contador">Solução: ${esc(o.solucao)}</div>` : ''}</td>
    <td>${dataHoraBR(o.atualizadaEm)}</td>
    <td>${situacao(o.status)}</td>
    <td class="acoes-tabela">${o.status === 'ABERTA'
      ? `<button class="btn-mini" data-x="iniciar-ocorrencia" data-id="${o.id}">Iniciar</button>`
      : o.status === 'EM_ANDAMENTO'
        ? `<button class="btn-mini" data-x="resolver-ocorrencia" data-id="${o.id}">Resolver</button>` : ''}</td>`);
}

document.getElementById('filtro-ocorrencia').addEventListener('change', desenharOcorrencias);

document.getElementById('form-ocorrencia').addEventListener('submit', (evento) => {
  evento.preventDefault();
  const form = evento.target;
  tentar(async () => {
    const corpo = { equipamento: form.equipamento.value.trim(), descricao: form.descricao.value.trim() };
    if (!corpo.equipamento) throw new Error('Informe o equipamento');
    if (corpo.descricao.length < 10) throw new Error('Descreva o problema (mínimo de 10 caracteres)');
    await api('POST', '/api/ocorrencias', corpo);
    form.reset();
    avisar('Ocorrência registrada.');
    await carregarOcorrencias();
  });
});

ACOES_EXTRA['iniciar-ocorrencia'] = async (id) => {
  await api('POST', `/api/ocorrencias/${id}/andamento`);
  avisar('Ocorrência em andamento.');
  await carregarOcorrencias();
};

ACOES_EXTRA['resolver-ocorrencia'] = (id) => janelaComFormulario('Resolver ocorrência', null,
  `<label>O que foi feito?* (mín. 5 caracteres)<textarea name="texto" rows="3" maxlength="500"></textarea></label>`,
  'Marcar como resolvida', async (form) => {
    await api('POST', `/api/ocorrencias/${id}/resolucao`, { texto: form.texto.value.trim() });
    avisar('Ocorrência resolvida.');
    await carregarOcorrencias();
  });

/* =========================================================
   FEEDBACKS POS-TREINO (consulta na aba Treinos)
   ========================================================= */

async function carregarFeedbacks(alunoId) {
  const alvo = document.getElementById('feedbacks-aluno');
  const lista = await api('GET', `/api/alunos/${alunoId}/feedbacks`);
  alvo.innerHTML = lista.length
    ? `<table><thead><tr><th>Data</th><th>Esforço</th><th>Exercícios</th><th>Observação</th></tr></thead><tbody>
      ${lista.map(f => `<tr><td>${dataBR(f.data)}</td><td><strong>${f.esforco}/10</strong></td>
        <td>${f.exerciciosConcluidos}/${f.totalDeExercicios}</td><td>${esc(f.observacao || '—')}</td></tr>`).join('')}
      </tbody></table>`
    : '<p class="vazio">Este aluno ainda não registrou feedbacks.</p>';
}
