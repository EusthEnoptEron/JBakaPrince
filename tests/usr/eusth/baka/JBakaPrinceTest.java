package usr.eusth.baka;

import org.junit.Test;
import usr.eusth.baka.commands.Command;
import usr.eusth.baka.commands.ListCommand;
import usr.eusth.baka.commands.ParseCommand;
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

	@Test
	public void testParser() throws Exception {
		CmdOptions opts = new CmdOptions(new String[] {"parse", "Boku_wa_tomodachi_ga_sukunai", "-volume", "Volume 9", "-o", "lol.json"} );
		Command cmd = new ParseCommand();
		cmd.execute(opts);

	}

	@Test
	public void testList() throws Exception {
		CmdOptions opts = new CmdOptions(new String[] {"list",  "Boku_wa_tomodachi_ga_sukunai"} );
		Command cmd = new ListCommand();
		cmd.execute(opts);
	}
}
