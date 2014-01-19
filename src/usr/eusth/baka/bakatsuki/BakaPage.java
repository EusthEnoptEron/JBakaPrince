package usr.eusth.baka.bakatsuki;

import com.google.gson.*;
import usr.eusth.baka.BakaTsuki;
import usr.eusth.baka.Cache;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

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

	public String getContent() {
		return fetchPage(new String[] {"action=parse", "page=" + name})
				.getAsJsonObject("parse")
				.getAsJsonObject("text")
				.getAsJsonPrimitive("*")
				.getAsString();
	}

	private JsonObject fetchPage(String[] args) {
		String url = BakaTsuki.getApiPath(args);

		try(InputStream stream = Cache.fetch(url, changeDate);
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
