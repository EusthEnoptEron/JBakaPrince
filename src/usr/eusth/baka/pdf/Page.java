package usr.eusth.baka.pdf;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import usr.eusth.baka.BakaTsuki;
import usr.eusth.baka.bakatsuki.BakaPage;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by Simon on 14/01/18.
 */
public class Page {
	// assumes the current class is called logger
	private final static Logger LOGGER = Logger.getLogger(Page.class.getName());

	private final static Page defaultPage = new Page();

	private String prefix = "";
	private String name = "";
	private String title = "";

	private boolean pagebreak = true;
	private boolean notitle = false;
	private boolean noheader = false;

	private String wiki = BakaTsuki.BASE_PATH;
	private boolean entrypicture = false;

	private boolean fetched = false;
	private String html = "";
	private List<Image> images = new ArrayList<Image>();
	private Date changeDate;

	public void setName(String name) {
		this.name = name;
	}

	public Page() {
	}

	public void setNotitle(boolean notitle) {
		this.notitle = notitle;
	}

	public void applyConfig(JsonObject values) {
		for (Map.Entry<String, JsonElement> entry : values.entrySet()) {
			switch (entry.getKey()) {
				case "prefix": prefix = entry.getValue().getAsString(); break;
				case "name": name = entry.getValue().getAsString(); break;
				case "title": title = entry.getValue().getAsString(); break;
				case "pagebreak": pagebreak = entry.getValue().getAsBoolean(); break;
				case "notitle": notitle = entry.getValue().getAsBoolean(); break;
				case "noheader": noheader = entry.getValue().getAsBoolean(); break;
				case "wiki": wiki = entry.getValue().getAsString(); break;
				case "entrypicture": entrypicture = entry.getValue().getAsBoolean(); break;
			}
		}
	}

	public String getHtml() {
		fetch();
		return html;
	}

	public List<Image> getImages() {
		return Collections.unmodifiableList(images);
	}
	public String getFullName(boolean forUrl) {
//		String fullname = prefix + name;
//		if(forUrl) {
//			try {
//				return URLEncoder.encode(fullname, "UTF-8");
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
//		} else {
//			try {
//				return URLDecoder.decode(fullname.replace("_", " "), "UTF-8");
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
//		}
		return prefix + name;
	}

	private void fetch() {
		if(!fetched) {
			fetched = true;

			if(title == null)
				title = name;

			BakaPage page = new BakaPage(getFullName(false), changeDate);

			html = prepareHtml(page.getContent());
		}
	}

	private String prepareHtml(String html) {
		BakaTsuki.info("Typesetting " + name + "...");
		// Make title
		if (!notitle)
		{
			html = "<h2>" + title + "</h2>" + html;
		}
		html = "<span class=\"invisible chapterstart\">" + (noheader ? "" : title) + "</span>" + html;

		// Make sure page break is set
		if (pagebreak)
		{
			html = "<span class=\"invisible pagebreak\"></span>" + html;
		}

		Document document = Jsoup.parseBodyFragment("<div class=\"content\">" + html + "</div>");
		Element dom = document.body().select(".content").first();

		// Remove next/prev table
		dom.select("table:contains(Back):contains(Return)").remove();


		// Find images
		for(Element a: dom.select("a.image")) {
			Element img = a.select("img").first();

			String src = img.attr("src").replace("/thumb", "");

			src = src.replaceFirst("[.](jpg|png|gif)/.+$", ".$1");
			// TODO: wiki
			Image image = new Image(BakaTsuki.getAbsolute(src));
			image.setSashie(true);

			Element node = a.parents().not(":not(.thumb)").first();
			if(node == null) node = a;


			if (images.size() == 0 && entrypicture)
			{
				// We can view it as a full-fledged image since we don't need to worry about text-flow
				image.setSashie(false);
				dom.before(image.getHtml());
			}
			else
			{
				node.before(image.getHtml());
				//node.After("<span class=\"image-stopper\"></span>");
			}

			node.remove();

			images.add(image);
		}

		// Catch references
		for (Element sup : dom.select("sup.reference"))
		{
			Element footnote = document.createElement("span").addClass("fn");
			Element oldFootnote = dom.select(sup.select("a").attr("href")).first();

			footnote.html(oldFootnote.select(".reference-text").html());

			oldFootnote.remove();
			sup.before(footnote).remove();
		}
		// Remove possible reference title
		Element references = dom.select(".references").first();
		if(references != null) {
			Element prevNode = references.previousElementSibling();

			if(prevNode.tagName().equals("h1") || prevNode.tagName().equals("h2") || prevNode.tagName().equals("h3") || prevNode.tagName().equals("h4")) {
				prevNode.remove();
			}
		}

		// Remove edit links
		dom.select(".editsection, .mw-editsection, #toc").remove();

		// Make smart quotes
		for(Element p : dom.select("p:contains(\"), p:contains('), li:contains(\"), li:contains(')")) {
			String pHtml = p.html();

			// Replace quotes
			int count = pHtml.length() - pHtml.replace("&quot;", "&quo").length();
			if (count % 2 == 0)
			{
				pHtml = pHtml.replaceAll("&quot;(.+?)&quot;", "“$1”");
			}
			else
			{
				System.out.println("NOTICE: possible quotes problem ("+pHtml.trim()+")");
			}

			// Replace single quotes (\b doesn't work)
			pHtml = pHtml.replaceAll("(?<!\\w)'(.+?)'(?!\\w)", "‘$1’");
			// Replace apostrophes
			pHtml = pHtml.replace("'", "’");

			p.html(pHtml);
		}

		// Parse Ruby
		for(Element rubySpan : dom.select("span > span > span")) {
			if(rubySpan.attr("style").contains("relative") && rubySpan.attr("style").contains("-50%")) {
				Element textSpan = rubySpan.parent().siblingElements().select("span").first();
				if(textSpan == null) continue;

				Element containerSpan = textSpan.parent();
				if(containerSpan == null) continue;

				if (containerSpan.attr("style").contains("nowrap"))
				{
					// Okay, this is ruby.
					Element ruby = document.createElement("ruby");
					ruby.html(textSpan.html());
					ruby.append("<rp>(</rp>");
					ruby.appendElement("rt").html(rubySpan.html());
					ruby.append("<rp>)</rp>");

					containerSpan.replaceWith(
							ruby
					);

				}
			}
		}

		// Hakomari specific
		for (Element star : dom.select("p:contains(✵)"))
		{
			star.html("<img src=\"" + (BakaTsuki.getResource("assets/blackstar.jpg").toString()) + "\">");
		}

		return document.body().html();

	}


	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}

	public Date getChangeDate() {
		return changeDate;
	}

	public JsonElement toJSON() {
		JsonObject obj = new JsonObject();
		String[] properties = new String[]{"prefix", "name", "title", "pagebreak", "notitle", "noheader", "wiki", "entrypicture"};

		for(String prop: properties) {
			try {
				Field field = this.getClass().getDeclaredField(prop);
				Object v1 = field.get(this);
				Object v2 = field.get(defaultPage);
				if(!field.get(this).equals(field.get(defaultPage))) {

					if(field.getType().equals(String.class)) {
						obj.addProperty(prop, (String)field.get(this));
					} else if(field.getType().equals(Boolean.TYPE)) {
						obj.addProperty(prop, (Boolean)field.get(this));
					} else if(field.getType().equals(Integer.TYPE)) {
						obj.addProperty(prop, (Integer) field.get(this));
					} else {
						System.out.println("Weve got a problem");
					}
				}
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return obj;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}


