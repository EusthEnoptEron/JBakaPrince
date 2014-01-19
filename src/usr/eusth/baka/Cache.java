package usr.eusth.baka;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Date;

/**
 * Created by Simon on 14/01/18.
 */
public class Cache {
	public static boolean noCache = false;

	public static String getResourceId(String url) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(url.getBytes("UTF8"));
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException|UnsupportedEncodingException e) {
			return url;
		}
	}

	public static File getResourcePath(String url) {
		File path = new File(System.getProperty("java.io.tmpdir"), "bakaprince");
		String id = getResourceId(url);

		return new File(path, id.substring(0, 2) + "/" + id.substring(0, 4) + id);
	}

	/**
	 * Fetches the contents of an URL
	 * @param uri
	 * @param threshold when the cache is older than this threshold, we will fetch the new version
	 * @return
	 */
	public static BufferedInputStream fetch(String uri, Date threshold) {
		File resourcePath = getResourcePath(uri);

		if(!noCache && resourcePath.exists() && (threshold == null || resourcePath.lastModified() > threshold.getTime()) ) {
			System.out.println("Fetching " + uri + " from cache");
			try {
				return new BufferedInputStream(new FileInputStream(resourcePath));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Fetching " + uri);
			try {
				URL url = new URL(uri);
				if(!resourcePath.getParentFile().exists()) {
					resourcePath.getParentFile().mkdirs();
				}
				try(InputStream in = url.openStream();
				    OutputStream out = new FileOutputStream(resourcePath)
				) {
					byte[] buffer = new byte[1024 * 1024];
					int len;
					while ((len = in.read(buffer)) != -1) {
						out.write(buffer, 0, len);
					}
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
				return fetch(uri);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static BufferedInputStream fetch(String uri) {
		return fetch(uri, null);
	}
}
