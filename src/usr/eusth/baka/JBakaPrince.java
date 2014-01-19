package usr.eusth.baka;

import usr.eusth.baka.commands.Command;
import usr.eusth.baka.commands.ConvertCommand;

public class JBakaPrince
{
	public static void main(String[] args) {
		CmdOptions options = new CmdOptions(args);

		Command command = null;
		if(options.isConverter) {
			command = new ConvertCommand();
		} else if(options.isParser) {

		}

		if(command != null) {
			if(command.isValid(options)) {
				command.execute(options);
			} else {
				System.out.println("INVALID OPTIONS");
			}
		} else {
			System.out.println("COMMAND NOT IMPLEMENTED");
		}


		//File path = new File("http://www.google.ch/file.json");

		//System.out.println(path.toFile().getAbsoluteFile());
	}
		//args = new string[] { @"parse", "Kamisama_no_Memochou", "-v", "2", "-c" };
		//args = new string[] { @"convert", "http://www.zomg.ch/baka/Config/hantsuki.json" };
		//args = new string[] { @"E:\Dev\prince\hakomari1.json", "-c", "-s", "stylesheet.css" };
		// args = new string[] { @"convert", @"E:\Dev\prince\tests\Accel World1Return of Princess Snow Black978-4-04-867517-8.json" };
//
//		Command command = null;
//		String commandName;
//		boolean showHelp = false;
//
//		OptionSet p = new OptionSet {{"f", "force re-download of files", v => {Helper.Caching = v == null;}}, {"h|help", "show this message and exit", v => showHelp = v != null}};
//
//
//		java.util.ArrayList<String> extra;
//		try
//		{
//			extra = p.Parse(args);
//		}
//		catch (OptionException e)
//		{
//			System.out.print("bakaprince: ");
//			System.out.println(e.getMessage());
//			System.out.println("Try `bakaprince --help' for more information.");
//			return;
//		}
//
//		// Show help if necessary
//		if (extra.isEmpty())
//		{
//			ShowHelp(p);
//		}
//
//		// Validate arguments
//		for (String arg : extra)
//		{
//			commandName = extra.First();
//			switch (commandName)
//			{
//				case "parse":
//					command = new ParseCommand();
//
//					break;
//				case "convert":
//					command = new ConvertCommand();
//					break;
//			}
//			if (command == null)
//			{
//				continue;
//			}
//			else
//			{
//				break;
//			}
//		}
//
//		if (showHelp || extra.size() < 2)
//		{
//			ShowHelp(command != null ? command.Options : p);
//		}
//
//		if (command != null)
//		{
//			java.util.ArrayList<String> newArgs = command.Options.Parse(extra);
//
//			try
//			{
//				command.Execute(newArgs.Skip(1).ToArray());
//			}
//			catch (FileNotFoundException e)
//			{
//				System.out.println(e.getMessage());
//			}
//			catch (WebException e)
//			{
//				System.out.println(e.getMessage());
//			}
//		}
//		else
//		{
//			ShowHelp(p);
//		}
//
//	}
//
//	private static void ShowHelp(OptionSet p)
//	{
//		System.out.println("Usage: bakaprince [OPTIONS]+ [parse|convert] Config-path");
//		System.out.println("create a PDF from a Baka-Tsuki project.");
//		System.out.println();
//		System.out.println("Options:");
//		p.WriteOptionDescriptions(Console.Out);
//
//		System.exit(0);
//	}
}