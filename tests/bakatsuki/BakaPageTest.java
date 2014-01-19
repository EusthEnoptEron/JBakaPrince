package bakatsuki;

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
}
