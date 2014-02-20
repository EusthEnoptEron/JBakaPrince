package usr.eusth.baka.commands;

import usr.eusth.baka.BakaTsuki;
import usr.eusth.baka.CmdOptions;
import usr.eusth.baka.PrinceDocument;
import usr.eusth.baka.pdf.Config;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Simon on 14/01/19.
 */
public class ConvertCommand implements Command {
	@Override
	public void execute(CmdOptions options) {
		try {
			PrinceDocument doc;
			String input = options.params[0];
			Config config;

			if(input.startsWith("http")) {
				config = new Config(new URL(input));
			} else {
				config = new Config(new File(input).toURI().toURL());
			}
			BakaTsuki.info("Converting " + config.getTitle());


			if(options.princePath == null) {
				doc = new PrinceDocument(config);
			} else {
				doc = new PrinceDocument(config, options.princePath);
			}

			String output = options.output == null
				? config.getTitle().replaceAll("\\W", "_") + ".pdf"
				: options.output;

			doc.create(output);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isValid(CmdOptions options) {
		if(options.params.length == 0) return false;
		return true;
	}


}
