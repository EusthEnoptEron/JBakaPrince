package usr.eusth.baka.pdf;

import org.junit.Test;
import usr.eusth.baka.PrinceDocument;

/**
 * Created by Simon on 14/01/18.
 */
public class ConfigTest {
	@Test
	public void testSerialization() throws Exception {
		Config config = new Config(this.getClass().getClassLoader().getResource("hakomari5.json"));
		PrinceDocument doc = new PrinceDocument(config);
		doc.create("test.pdf");
	}
}
