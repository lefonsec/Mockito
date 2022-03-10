import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.model.Leilao;

public class HelloWordMokito {

	@Test
	void hello() {
		LeilaoDao mockDao = mock(LeilaoDao.class);
		List<Leilao> todos = mockDao.buscarTodos();
		Assert.assertTrue(todos.isEmpty());
		
	}
}
