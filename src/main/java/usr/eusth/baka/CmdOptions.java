package usr.eusth.baka;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simon on 14/01/19.
 */
public class CmdOptions {
	public boolean isParser = false;
	public boolean isConverter = false;
	public boolean isLister = false;
	public String volume = null;
	public boolean noCache = false;
	public boolean verbose = false;

	public String princePath = null;
	public String output = null;
	public String styleSheet = null;

	public String[] params;
	public CmdOptions(String[] args) {
		List<String> params = new ArrayList<>();

		try {
			for(int i = 0; i < args.length; i++)  {
				String arg = args[i];

				if(arg.startsWith("-")) {
					switch (arg) {
						case "-volume":
						case "--volume":
							volume = args[++i];
							break;
						case "-v":
						case "--verbose":
							verbose = true;
							break;
						case "--nocache":
						case "-f":
							noCache = true;
							break;
						case "-p":
							princePath = args[++i];
							break;
						case "-o":
							output = args[++i];
							break;
						case "-s":
							styleSheet = args[++i];
							break;
						case "-h":
						case "--help":
							printHelp();
							System.exit(0);
							break;
					}
				} else {
					switch(arg) {
						case "parse":
							isParser = true;
							break;
						case "convert":
							isConverter = true;
							break;
						case "list":
							isLister = true;
							break;
						default:
							params.add(arg);
							break;
					}
				}

			}
		} catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("INVALID ARGUMENTS");
			System.out.println("-----------------");
			printHelp();
			System.exit(0);
		}

		this.params = params.toArray(new String[params.size()]);
	}

	void printHelp() {
		System.out.println("Usage: bakaprince [OPTIONS]+ [parse|convert] [volume-name|json-file]\n" +
				"Create a PDF from a Baka-Tsuki volume.\n" +
				"\n" +
				"Options:\n" +
				"  -f, --nocache              force re-download of files\n" +
				"  -h, --help                 show this message and exit\n" +
				"\n" +
				"  parse:\n" +
				"  -o                         output path\n" +
				"  --volume                   volume name\n" +
				"\n" +
				"  convert:\n" +
				"  -p                         the PATH where PrinceXML is located. Leave away\n" +
				"                               to find it automatically.\n" +
				"  -o                         where to write the resulting PDF\n" +
				"  -s                         specify an additional stylesheet to use");
	}

}
