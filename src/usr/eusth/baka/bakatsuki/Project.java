package usr.eusth.baka.bakatsuki;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import usr.eusth.baka.BakaTsuki;
import usr.eusth.baka.pdf.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simon on 14/01/19.
 */
public class Project extends BakaPage {
	List<Volume> volumes = new ArrayList<>();
	String url;

	public Project(String name) {
		super(name);
		url = BakaTsuki.getUrl(name);

		Document document = Jsoup.parseBodyFragment(getContent());
		for(Element el: document.body().select("a:contains(Illustrations)")) {
			Element ul = el.parents().not(":not(ul)").first();
			if(ul != null) {
				volumes.add(new Volume(ul));
			} else {
				System.out.println("Can't parse volume.");
			}
		}
	}


	public List<Config> getConfigs() {
		List<Config> list = new ArrayList<>();

		for(Volume v: volumes) {
			list.add(v.getConfig(this));
		}
		return list;
	}

}
