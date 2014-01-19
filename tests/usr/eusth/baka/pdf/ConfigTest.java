package usr.eusth.baka.pdf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.princexml.Prince;
import org.junit.Test;
import usr.eusth.baka.BakaTsuki;
import usr.eusth.baka.PrinceDocument;

import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Simon on 14/01/18.
 */
public class ConfigTest {
	@Test
	public void testSerialization() throws Exception {
		Config config = new Config(this.getClass().getClassLoader().getResource("hakomari5.json"));
		PrinceDocument doc = new PrinceDocument(config);
		doc.Create("test.pdf");
	}
}
