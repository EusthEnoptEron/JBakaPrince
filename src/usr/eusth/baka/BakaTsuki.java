package usr.eusth.baka;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import usr.eusth.baka.pdf.Image;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Simon on 14/01/18.
 */
public class BakaTsuki {
	public static final String DOCUMENT_ROOT = "http://www.baka-tsuki.org";
	public static final String BASE_PATH = DOCUMENT_ROOT + "/project";
	public static final String API_PATH  = BASE_PATH + "/api.php";
	public static int logLevel = 1;


	public static final int LOG_DEBUG = 0;
	public static final int LOG_INFO = 1;
	public static final int LOG_ERROR = 2;


	public static String getAbsolute(String uri) {
		if(uri.startsWith("http")) return uri;
		if(uri.startsWith("/")) return DOCUMENT_ROOT + uri;
		else return BASE_PATH + "/" + uri;
	}
	public static String join( List<String> list , String replacement  ) {
		StringBuilder b = new StringBuilder();
		for( String item: list ) {
			b.append( replacement ).append( item );
		}
		if(list.size() > 0)
			return b.toString().substring( replacement.length() );
		else
			return b.toString();
	}

	public static Gson getGson() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Image.class, new Image.ImageDeserializer());
		return builder.create();
	}

	public static String getApiPath(String[] args) {
		List<String> config = new ArrayList<>();
		config.add("format=json");
		config.addAll(Arrays.asList(args));

		return API_PATH + "?" + join(config, "&");
	}

	public static JsonObject call(String[] args) {
		List<String> config = new ArrayList<>();
		config.add("format=json");
		config.addAll(Arrays.asList(args));

		try {
			URL api = new URL(API_PATH + "?" + join(config, "&"));
			JsonParser parser = new JsonParser();
			try(InputStream in = api.openStream();
			    InputStreamReader r = new InputStreamReader(in)) {
				return parser.parse(r).getAsJsonObject();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URL getResource(String resource)  {
		try {
			return new URL(BakaTsuki.class.getProtectionDomain().getCodeSource().getLocation(), resource);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	public static InputStream getResourceAsStream(String resource) throws IOException {
		return getResource(resource).openStream();
	}

	public static String getUrl(String name) {
		return BASE_PATH + "/index.php?title=" + name;
	}


	public static void info(String msg) {
		if(logLevel <= LOG_INFO)
			System.out.println("INFO: " + msg);
	}

	public static void error(String msg) {
		if(logLevel <= LOG_ERROR )
			System.err.println("ERROR: " + msg);
	}

	public static void debug(String msg) {
		if(logLevel <= LOG_DEBUG)
			System.out.println("DEBUG: " + msg);
	}
}
