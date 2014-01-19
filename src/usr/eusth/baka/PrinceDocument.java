package usr.eusth.baka;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.princexml.Prince;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import usr.eusth.baka.pdf.Config;
import usr.eusth.baka.pdf.Image;
import usr.eusth.baka.pdf.Page;

import java.io.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Simon on 14/01/18.
 */
public class PrinceDocument {
//	private Prince prince;
//	private PDF.Config conf;
	private Prince prince;
	private Config config;

	public PrinceDocument(Config config) {
		// Find prince path
		String[] paths = System.getenv("PATH").split(";|:");
		String princePath = null;
		for(String path : paths) {
			File winBin = new File(path, "prince.exe");
			File linBin = new File(path, "prince");
			if(winBin.exists()) {
				princePath = winBin.getAbsolutePath();
			} else if(linBin.exists()) {
				princePath = linBin.getAbsolutePath();
			}

			if(princePath != null) break;
		}

		if(princePath == null) throw new RuntimeException("Couldn't find prince binary!");
		init(config, princePath);
	}

	public PrinceDocument(Config config, String princePath) {
		init(config, princePath);
	}

	private void init(Config config, String princePath) {
		prince = new Prince(princePath);


		// We are dealing with Html
		prince.setHTML(true);

		// Add default stylesheets
		prince.addStyleSheet(BakaTsuki.getResource("assets/mediawiki.css").toString());
		prince.addStyleSheet(BakaTsuki.getResource("assets/book.css").toString());
		prince.addStyleSheet(BakaTsuki.getResource("assets/ruby.css").toString());

		// Add additional stylesheets
		for (String path : config.getStyleSheets())
		{
			prince.addStyleSheet(path);
		}

		this.config = config;
	}

	public void create(String path) {
		// Set base url to the Wiki URL (for images that we didn't catch, etc.)
		prince.setBaseURL(config.getBaseUrl());
		File output = new File(path).getAbsoluteFile();
		File temp   = new File(path + ".pdf").getAbsoluteFile();

		if(!output.getParentFile().exists())
			output.getParentFile().mkdirs();

		// Init builder
		StringBuilder htmlBuilder = new StringBuilder();
		initBuilder(htmlBuilder);

		// Compile color images
		System.out.println("Creating color pages...");
		for (Image image : config.getImages())
		{
			htmlBuilder.append(image.getHtml());
		}


		System.out.println("Compiling chapters...");
		// Compile Html
		for (Page page : config.getPages())
		{
			htmlBuilder.append(page.getHtml());
		}

		closeBuilder(htmlBuilder);

		System.out.println(String.format("Writing PDF to %s...", path));


		try ( InputStream in = new ByteArrayInputStream(htmlBuilder.toString().getBytes("UTF-8"));
		      OutputStream out = new FileOutputStream(temp))
		{
			prince.convert(in, out);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Cleaning...");
		moveDisclaimer(temp, output);

		temp.delete();

		System.out.println("Et voilà -- your PDF is ready.");
	}

	private void moveDisclaimer(File input, File output) {
		try {
			PdfReader reader = new PdfReader(input.getAbsolutePath());
			com.itextpdf.text.Document document = new com.itextpdf.text.Document(reader.getPageSizeWithRotation(1));

			PdfCopy writer = new PdfCopy(document, new FileOutputStream(output));
			document.open();

			PdfImportedPage page = null;
			PdfImportedPage disclaimer = null;
			int imageCount = config.getImages().size();
			for(int i = 1; i <= reader.getNumberOfPages(); i++) {
				page = writer.getImportedPage(reader, i);
				if(i ==  1) disclaimer = page;
				else {
					writer.addPage(page);

					if(i == imageCount + 1) {
						writer.addPage(disclaimer);
					}
				}
			}

			document.close();
			writer.close();
			reader.close();

		} catch (IOException | DocumentException e) {
			e.printStackTrace();
		}
	}

	private void initBuilder(StringBuilder builder) {
		builder.append(
			String.format("<html>\n" +
					"    <head>\n" +
					"        <title>%s</title>\n" +
					"        <meta name=\\\"author\\\" content=\\\"%s\\\"/>\n" +
					"        <meta name=\\\"generator\\\" content=\\\"BakaPrince\\\"/>\n" +
					"    </head>\n" +
					"<body>",
			config.getTitle().replace("\"", "\\\""),
			BakaTsuki.join(config.getContributors().get("authors"), ", ").replace("\"", "\\\"")));

		// Add disclaimer
		appendDisclaimer(builder);
	}


	private void closeBuilder(StringBuilder builder) {
		builder.append("</body></html>");
	}

	private void appendDisclaimer(StringBuilder builder) {

		try {
			Document doc = Jsoup.parse(BakaTsuki.getResourceAsStream("assets/disclaimer.html"), "UTF-8", "");
			Element table = doc.select("table#contributors").first();

			// Append header row
			if(!config.getTitle().equals(""))
				table.append("<tr><th colspan='2' class='header'></th></tr>").select(".header").first().text(config.getTitle());

			HashMap<String, List<String>> contributors = config.getContributors();
			Set<String> keys = contributors.keySet();
			for(String key : keys)
			{
				List<String> names = contributors.get(key);
				key = key.substring(0, 1).toUpperCase() + key.substring(1);
				if (names.size() == 1)
				{
					key = key.substring(0, key.length() - 1);
				}
				else if (names.size() == 0)
				{
					continue;
				}

				Element tr = doc.createElement("tr");
				tr.appendElement("th").text(key);
				tr.appendElement("td").text(BakaTsuki.join(names, ", "));

				table.appendChild(tr);
			}


			if(!config.getProject().equals("")) {
				table.append("<tr><th>Project page</th><td></td></tr>");
				table.select("tr").last().select("td").append(String.format("<a href='%1$s'>%1$s</a>", config.getProject()));
			}

			DateFormat formatter = DateFormat.getDateInstance();
			table.append(String.format("<tr><th>PDF creation date</th><td>%s</td>", formatter.format(new Date())));
			builder.append(doc.body().html());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
