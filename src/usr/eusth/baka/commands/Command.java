package usr.eusth.baka.commands;

import usr.eusth.baka.CmdOptions;

/**
 * Created by Simon on 14/01/19.
 */
public interface Command {
	void execute(CmdOptions options);
	boolean isValid(CmdOptions options);

}
