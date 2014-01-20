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
 * Created by Simon on 14/01/20.
 */
public class ListCommand implements Command {
	@Override
	public void execute(CmdOptions options) {
		BakaTsuki.logLevel = BakaTsuki.LOG_ERROR;

		Project project = new Project(options.params[0]);

		for(Volume volume: project.getVolumes()) {
			System.out.println(volume.getTitle());
		}
	}

	@Override
	public boolean isValid(CmdOptions options) {
		return options.params.length > 0;
	}

}
