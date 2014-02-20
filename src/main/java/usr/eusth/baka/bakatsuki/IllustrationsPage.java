package usr.eusth.baka.bakatsuki;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import usr.eusth.baka.BakaTsuki;
import usr.eusth.baka.pdf.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simon on 14/01/19.
 */
public class IllustrationsPage extends BakaPage {
	public IllustrationsPage(String name) {
		super(name);
	}

	public List<Image> getImages() {
		List<Image> images = new ArrayList<Image>();
		Document document = Jsoup.parseBodyFragment(getContent());

		for(Element img : document.body().select("img")) {
			try {
				URL src = new URL(BakaTsuki.getAbsolute(img.attr("src")));

				if (isColorful(src)) {
					String url = src.toString();
					url = url.replace("images/thumb", "images");
					url = url.substring(0, url.lastIndexOf('/'));

					images.add(Image.makeUnloadedImage(url));
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}

		return images;
	}

	private boolean isColorful(URL img) {
		try {
			BufferedImage image = ImageIO.read(img);
			float saturation = 0;
			int threshold = 100;
			if(image.getType() == BufferedImage.TYPE_BYTE_GRAY || image.getType() == BufferedImage.TYPE_USHORT_GRAY)
				return false;

			for (int x = 0; x < image.getWidth(); x++)
			{
				for (int y = 0; y < image.getHeight(); y++)
				{
					Color col = new Color(image.getRGB(x, y));

					float[] hsb = Color.RGBtoHSB(col.getRed(), col.getGreen(), col.getBlue(), null);

					saturation += hsb[1];

					if (saturation > threshold) {
						return true;
					}

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}




}
