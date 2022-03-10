package br.com.alura.leilao.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;

class FinalizarLeilaoServiceTest {

	//instancia criada a classe testada;
	private FinalizarLeilaoService service;

	//mock feito para passar no leilao e não ir buscar no banco de dados;
	@Mock
	private LeilaoDao leidaoDao;
	
	@Mock
	private EnviadorDeEmails enviador;

	@BeforeEach
	public void beaforeEach() {
		MockitoAnnotations.initMocks(this);
		this.service = new FinalizarLeilaoService(leidaoDao,enviador);
	}

	@Test
	void deveriaFinalizarLeilao() {
		//lista criada para passar no mock; 
		List<Leilao> leiloes = leiloes();

		//comportamento do mock;
		Mockito.when(leidaoDao.buscarLeiloesExpirados()).thenReturn(leiloes);

		//metodo que ta sendo testado;
		service.finalizarLeiloesExpirados();
		
		Leilao leilao = leiloes.get(0);
		Assert.assertTrue(leilao.isFechado());
		Assert.assertEquals(new BigDecimal("900"), leilao.getLanceVencedor().getValor());
		
		Mockito.verify(leidaoDao).salvar(leilao);

	}
	
	@Test
	void deveriaEnviarEmaail() {
		List<Leilao> leiloes = leiloes();

		Mockito.when(leidaoDao.buscarLeiloesExpirados()).thenReturn(leiloes);

		service.finalizarLeiloesExpirados();
		Leilao leilao = leiloes.get(0);
		Lance lanceVencedor = leilao.getLanceVencedor();
		
		Mockito.verify(enviador).enviarEmailVencedorLeilao(lanceVencedor);;

	}
	
	@Test
	void naoDeveriaEnviarEmaail() {
		List<Leilao> leiloes = leiloes();

		Mockito.when(leidaoDao.buscarLeiloesExpirados()).thenReturn(leiloes);
		
		Mockito.when(leidaoDao.salvar(Mockito.any())).thenThrow(RuntimeException.class);

		try {
			service.finalizarLeiloesExpirados();
			Mockito.verifyNoInteractions(enviador);


		}catch(Exception e ) {
			
		}
		

	}

	private List<Leilao> leiloes() {
		List<Leilao> lista = new ArrayList<Leilao>();

		Leilao leilao = new Leilao("celular", new BigDecimal("500"), new Usuario("fulano"));
		Lance primeiro = new Lance(new Usuario("Beltrano"), new BigDecimal("600"));
		Lance segundo = new Lance(new Usuario("Ciclano"), new BigDecimal("900"));

		leilao.propoe(primeiro);
		leilao.propoe(segundo);

		lista.add(leilao);

		return lista;

	}

}
