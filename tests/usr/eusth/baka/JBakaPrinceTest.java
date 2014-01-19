package usr.eusth.baka;

import org.junit.Test;
import usr.eusth.baka.pdf.Config;

/**
 * Created by Simon on 14/01/19.
 */
public class JBakaPrinceTest {
	@Test
	public void testSerialization() throws Exception {
		Config config = new Config(this.getClass().getClassLoader().getResource("hakomari5.json"));
		String json = config.toJSON().toString();

	}
}
