package usr.eusth.baka.bakatsuki;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import usr.eusth.baka.pdf.Config;
import usr.eusth.baka.pdf.Image;
import usr.eusth.baka.pdf.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simon on 14/01/19.
 */
public class Volume {
	private final List<Chapter> chapters = new ArrayList<>();
	private IllustrationsPage illustrationsPage = null;
	String title = "";
	private static int counter = 1;

	public Volume(Element ul) {
		Element header = ul;

		while(!header.tagName().equals("body")) {
			Element candidate = findHeader(header);
			if(candidate == null) {
				header = header.parent();
			} else {
				header = candidate;
				break;
			}
		}

		if(!header.tagName().equals("body")) {

			title = header.select(".headline, .mw-headline").text().replaceAll("\\(.*$", "").trim();
		} else {
			title = "Unknown Volume " + counter++;
		}

		for(Element el : ul.select("li")) {
			Element link = el.select("a:not(.new)").first();
			if(link == null) continue;

			String name = link.attr("href").replaceAll("^.+title=", "");

			if(name.contains("Illustrations") && illustrationsPage == null) {
				illustrationsPage = new IllustrationsPage(name);
			} else {
				chapters.add(new Chapter(name, link.text()));
			}
		}

	}

	private Element findHeader(Element el) {
		do {
			if(isHeader(el))
				return el;
			else
				el = el.previousElementSibling();
		} while(el != null);

		return null;
	}

	public String getTitle() {
		return title.trim();
	}

	public Config getConfig(Project project) {
		Config conf = new Config();
		conf.setProject(project.url);
		conf.setTitle(project.getName().trim() + " - " + this.title);

		if(illustrationsPage != null) {
			for(Image img : illustrationsPage.getImages()) {
				conf.getImages().add(img);
			}
		}


		for(Chapter chapter: chapters) {
			Page p = new Page();
			p.setName(chapter.getName());
			p.setTitle(chapter.title);

			if(chapter.startsWithTitle())
				p.setNotitle(true);

			conf.getPages().add(p);
		}

		return conf;
	}

	private boolean isHeader(Element el) {
		if(el == null) return false;
		return el.tagName().equals("h2") || el.tagName().equals("h3") || el.tagName().equals("h4");
	}


	public class Chapter extends BakaPage {
		String title;
		public Chapter(String name, String label) {
			super(name);
			title = label;
		}

		public boolean startsWithTitle() {
			Document document = Jsoup.parseBodyFragment(getContent());

			Element firstNode = document.body().children().first();
			if(firstNode == null) return false;
			if(firstNode.tagName().equals("div") && firstNode.attr("id") != null && firstNode.attr("id").equals("toc")) {
				firstNode = firstNode.nextElementSibling();
			}

			return isHeader(firstNode);
		}
	}

}
