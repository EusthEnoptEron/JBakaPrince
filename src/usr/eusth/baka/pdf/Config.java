package usr.eusth.baka.pdf;

import com.google.gson.*;
import usr.eusth.baka.BakaTsuki;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Simon on 14/01/18.
 */
public class Config {
	private String location;
	private HashMap<String, List<String>> contributors = new HashMap<>();

	private String title;
	private String project;

	private List<String> styleSheets = new ArrayList<>();

	private List<Image> images = new ArrayList<>();
	private List<Page> pages = new ArrayList<>();
	private String baseUrl;

	private JsonObject config;
	private URL base;


	public void setTitle(String title) {
		this.title = title;
	}

	public void setProject(String project) {
		this.project = project;
	}


	public Config() {

	}
	public Config(URL url) {
		base = url;

		try(InputStream in = url.openStream()) {
			JsonParser parser = new JsonParser();
			config = parser.parse(new InputStreamReader(in)).getAsJsonObject();

			parseImages();
			parsePages();
			parseStyleSheets();

			parseContributorList("authors");
			parseContributorList("artists");
			parseContributorList("translators");
			parseContributorList("editors");


			if (config.has("title"))
			{
				title = config.get("title").getAsString();
			}
			if (config.has("volume"))
			{
				project = config.get("volume").getAsString();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseContributorList(String key) {
		// Init
		if (!contributors.containsKey(key))
		{
			contributors.put(key, new ArrayList<String>());
		}

		List<String> list = contributors.get(key);

		if (config.has(key) && config.get(key).isJsonArray())
		{
			for(JsonElement el: config.get(key).getAsJsonArray()) {
				list.add(el.getAsString());
			}
		}
	}

	private void parseStyleSheets() {
		if (config.has("stylesheets"))
		{
			for(JsonElement el: config.get("stylesheets").getAsJsonArray()) {
				try {
					styleSheets.add(new URL(base, el.getAsString()).toString());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void parsePages() {
		if (config.has("pages") && config.get("pages").isJsonArray()) {

			for(JsonElement pageOptions : config.get("pages").getAsJsonArray())
			{
				Page page = new Page();

				if(config.has("defaults") && config.get("defaults").isJsonObject()) {
					page.applyConfig(config.get("defaults").getAsJsonObject());
				}


				if (pageOptions.isJsonPrimitive())
				{
					page.setName(pageOptions.getAsString());
				}
				else if(pageOptions.isJsonObject())
				{
					page.applyConfig(pageOptions.getAsJsonObject());
				}

				pages.add(page);
			}

			JsonObject res = getPagesStats();
			SimpleDateFormat mwParser = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'");
			mwParser.setTimeZone(TimeZone.getTimeZone("UTC"));

			for (Map.Entry<String, JsonElement> entry : res.entrySet()) {
				JsonObject stats = entry.getValue().getAsJsonObject();

				for(Page page: pages) {
					String fullName = page.getFullName(false);
					String fullName2 = stats.get("title").getAsString();
					if(page.getFullName(false).equals(stats.get("title").getAsString())) {
						try {
							page.setChangeDate(mwParser.parse(stats.get("touched").getAsString()));
							break;
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	private JsonObject getPagesStats() {
		if(pages.size() == 0) return new JsonObject();

		String[] conf = new String[4];
		conf[0] = "action=query";
		conf[1] = "prop=info";
		conf[2] = "redirects=1";

		String titles = "titles=";
		for(Page page: pages) {
			titles += page.getFullName(true) + "|";
		}
		if(pages.size() > 0) titles = titles.substring(0, titles.length() - 1);

		conf[3] = titles;
		return BakaTsuki.call(conf).getAsJsonObject("query").getAsJsonObject("pages");

	}

	private void parseImages() {
		if (config.has("images") && config.get("images").isJsonArray())
		{
			for(JsonElement image : config.get("images").getAsJsonArray()) {
				try {
					images.add(new Image(new URL(base, image.getAsString()).toString()));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public List<String> getStyleSheets() {
		return styleSheets;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public List<Image> getImages() {
		return images;
	}

	public List<Page> getPages() {
		return pages;
	}

	public String getTitle() {
		return title;
	}

	public HashMap<String, List<String>> getContributors() {
		return contributors;
	}

	public String getProject() {
		return project;
	}

	public JsonObject toJSON() {
		JsonObject obj = new JsonObject();
		Gson gson = new Gson();

		obj.addProperty("title", title);
		obj.addProperty("volume", project);

		// Add contributors
		for(String key: contributors.keySet()) {
			if(!contributors.get(key).isEmpty())
				obj.add(key, gson.toJsonTree(contributors.get(key)));
		}

		JsonArray jsonPages = new JsonArray();
		obj.add("pages", jsonPages);
		for(Page page: pages) {
			jsonPages.add(page.toJSON());
		}

		JsonArray jsonImages = new JsonArray();
		obj.add("images", jsonImages);
		for(Image image: images) {
			jsonImages.add(image.toJSON());
		}



		return obj;
	}

	public String getProposedFileName() {
		return getTitle().replaceAll("\\W", "_") + ".json";
	}
}
