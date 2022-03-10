package br.com.alura.leilao.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Pagamento;
import br.com.alura.leilao.model.Usuario;

class GeradorDePagamentoTest {

	// instancia criada a classe testada;
	private GeradorDePagamento gerador;

	// mock feito para passar no leilao e não ir buscar no banco de dados;
	@Mock
	private PagamentoDao pagamento;
	
	@Captor
	private ArgumentCaptor<Pagamento> captor;
	
	@Mock
	private Clock clock;

	@BeforeEach
	public void beaforeEach() {
		MockitoAnnotations.initMocks(this);
		this.gerador = new GeradorDePagamento(pagamento,clock);
	}

	@Test
	void deveriaCriarPagamentoVencedor() {

		Leilao leilao = leilao();
		Lance vencedor = leilao.getLanceVencedor();
		
		LocalDate data = LocalDate.of(2020, 12, 7);
		Instant instant =data.atStartOfDay(ZoneId.systemDefault()).toInstant();
		
		Mockito.when(clock.instant()).thenReturn(instant);
		Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());
		
		gerador.gerarPagamento(vencedor);
		Mockito.verify(pagamento).salvar(captor.capture());
		
		Pagamento pagamento = captor.getValue();
		Assert.assertEquals(LocalDate.now().plusDays(1), pagamento.getVencimento());
		Assert.assertEquals(vencedor.getValor(), pagamento.getValor());
		Assert.assertFalse(pagamento.getPago());
		Assert.assertEquals(vencedor.getUsuario(), pagamento.getUsuario());
		Assert.assertEquals(leilao,pagamento.getLeilao());
	}

	private Leilao leilao() {

		Leilao leilao = new Leilao("celular", new BigDecimal("500"), new Usuario("fulano"));

		Lance segundo = new Lance(new Usuario("Ciclano"), new BigDecimal("900"));

		leilao.propoe(segundo);
		leilao.setLanceVencedor(segundo);


		return leilao;

	}

}
