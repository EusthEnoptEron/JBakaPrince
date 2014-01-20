package usr.eusth.baka.commands;

import usr.eusth.baka.BakaTsuki;
import usr.eusth.baka.CmdOptions;
import usr.eusth.baka.bakatsuki.Project;
import usr.eusth.baka.bakatsuki.Volume;
import usr.eusth.baka.pdf.Config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Simon on 14/01/19.
 */
public class ParseCommand implements Command {
	@Override
	public void execute(CmdOptions options) {
		Project project = new Project(options.params[0]);
		File output;
		if(options.output != null) {
			output = new File(options.output);
		} else {
			output = new File(".");
		}

		if(options.volume == null) {

		} else {
			for(Volume v: project.getVolumes()) {
				if(v.getTitle().equals(options.volume)) {
					BakaTsuki.info("Found volume! Parsing...");
					Config config = v.getConfig(project);

					if(options.output == null)
						output = new File(output, config.getProposedFileName() );

					try(FileWriter writer = new FileWriter(output)) {
						writer.write(config.toJSON().toString());
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}
			}
		}
		for(Config conf:  project.getConfigs()) {
			BakaTsuki.info("Parsing \""+conf.getTitle()+"\"...");
			File path = new File(output, conf.getProposedFileName());

			try(FileWriter writer = new FileWriter(path)) {
				writer.write(conf.toJSON().toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean isValid(CmdOptions options) {
		return options.params.length > 0;
	}

}
