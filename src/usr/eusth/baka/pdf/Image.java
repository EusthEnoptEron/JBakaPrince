package usr.eusth.baka.pdf;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import usr.eusth.baka.BakaTsuki;
import usr.eusth.baka.Cache;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;

/**
 * Created by Simon on 14/01/18.
 */
public class Image {
	// Online
	private String url;
	// Local
	private File path;
	private String id;
	private float width;
	private float height;
	private boolean sashie;

	private static final float SHEET_WIDTH = 5.8f;
	private static final float SHEET_HEIGHT = 8.3f;


	public Image(String url) {
		this.url = url = url.replace(" ", "%20");
		id = "i" + Cache.getResourceId(url);

		try( InputStream stream = Cache.fetch(url) ) {
			BufferedImage image = ImageIO.read(stream);

			if(image.getWidth() > image.getHeight()) {
				// Landscape
				width = SHEET_HEIGHT;
				height = SHEET_HEIGHT / image.getWidth() * image.getHeight();
			} else {
				// Portrait
				width = SHEET_WIDTH;
				height = SHEET_WIDTH / image.getWidth() * image.getHeight();
			}

		} catch (IOException e) {
			width = 0;
			height = 0;
		}

		path = Cache.getResourcePath(url).getAbsoluteFile();
	}


	public String getHtml() {
		String classes = "";
		String html = "";

		if(sashie) {
			classes += "sashie";
		}
		if(sashie && width > height) {
			classes += " landscape";
		}

		if(sashie && width < height) {
			html += "<div class=\"sashie-wrapper\">";
		}

		html += String.format("<div class=\"image %s %s\"></div>", id, classes);

		if(sashie && width < height) {
			html += "</div>";
		}

		html += getStyle();

		return html;
	}

	public String getStyle() {
		return String.format("<style type=\"text/css\">%s</style>", getRules());
	}

	public String getRules() {

			return String.format("@page p%1$s {\n" +
					"                size: %2$sin %3$sin;\n" +
					"                margin: 0;\n" +
					"\n" +
					"    @top-center {\n" +
					"       content: normal;\n" +
					"    }\n" +
					"\n" +
					"    @bottom-center {\n" +
					"        content: normal;\n" +
					"    }\n" +
					"        }\n" +
					"\n" +
					".%1$s {\n" +
					"    background-image: url(%4$s);\n" +
					"    page: %5$s;\n" +
					"        width: %2$sin;\n" +
					"        height: %3$sin;\n" +
					" }\n" +
					"\n" +
					".sashie-wrapper .%1$s {\n" +
					"    top: %6$sin;\n" +
					"}", id, width, height, "file://" + path.toURI().getPath(),
					(sashie && height > width) ? "auto" : "p" + id,
					(SHEET_HEIGHT - height) / 2);

	}


	public void setSashie(boolean sashie) {
		this.sashie = sashie;
	}


	public static class ImageDeserializer implements JsonDeserializer<Image> {
		@Override
		public Image deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			return new Image(BakaTsuki.getAbsolute(jsonElement.getAsString()));
		}
	}
}
