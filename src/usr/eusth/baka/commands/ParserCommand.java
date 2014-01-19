package usr.eusth.baka.commands;

import usr.eusth.baka.CmdOptions;
import usr.eusth.baka.bakatsuki.Project;
import usr.eusth.baka.pdf.Config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Simon on 14/01/19.
 */
public class ParserCommand implements Command {
	@Override
	public void execute(CmdOptions options) {
		Project project = new Project(options.project);
		File outputDir = new File(options.output == null
				? "."
				: options.output);

		for(Config conf:  project.getConfigs()) {
			File path = new File(outputDir, conf.getTitle().replaceAll("\\W", "_") + ".json");

			try(FileWriter writer = new FileWriter(path)) {
				System.out.println("Writing " + path + "...");
				writer.write(conf.toJSON().toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean isValid(CmdOptions options) {
		return options.project != null;
	}
}
