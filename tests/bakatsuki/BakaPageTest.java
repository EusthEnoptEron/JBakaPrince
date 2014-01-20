package bakatsuki;

import com.google.gson.JsonPrimitive;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.Test;
import usr.eusth.baka.bakatsuki.BakaPage;

import static org.junit.Assert.assertTrue;

/**
 * Created by Simon on 14/01/18.
 */
public class BakaPageTest {
	@Test
	public void canLoadPage() throws Exception {
		BakaPage page = new BakaPage("Utsuro_no_Hako:Volume2_Prologue");

		assertTrue(page.getContent().contains("I already know"));
	}


	@Test
	public void testName() throws Exception {
		BakaPage page = new BakaPage("Boku_wa_tomodachi_ga_sukunai");
		String content = page.getContent();
		Element el = Jsoup.parseBodyFragment(content);
		String text = el.select("a:contains(Comedy King)").text();
		String primitive = new JsonPrimitive(text).getAsString();

	}
}
