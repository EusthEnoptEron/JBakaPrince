package usr.eusth.baka.bakatsuki;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import usr.eusth.baka.BakaTsuki;
import usr.eusth.baka.Cache;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

/**
 * Created by Simon on 14/01/18.
 */
public class BakaPage {
	private String name;
	private Date changeDate = null;

	public BakaPage(String name, Date changeDate) {
		// TODO: wiki
		this(name);
		this.changeDate = changeDate;
	}

	public BakaPage(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getContent() {
		try {
			BakaTsuki.info("Fetching page \"" + name + "\"...");
			return fetchPage(new String[] {"action=parse", "redirects=1", "page=" + name})
					.getAsJsonObject("parse")
					.getAsJsonObject("text")
					.getAsJsonPrimitive("*")
					.getAsString();
		} catch(NullPointerException e) {
			BakaTsuki.error("Didn't find page: "+ name);
			return "";
		}

	}

	private JsonObject fetchPage(String[] args) {
		String url = BakaTsuki.getApiPath(args);

		try(InputStream stream = Cache.fetch(url, changeDate, false);
			InputStreamReader reader = new InputStreamReader(stream))
		{
			JsonParser parser = new JsonParser();
			return parser.parse(reader).getAsJsonObject();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

}
