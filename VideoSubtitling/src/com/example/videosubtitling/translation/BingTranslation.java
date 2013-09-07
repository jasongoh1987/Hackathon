package com.example.videosubtitling.translation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Base64;
import android.util.Xml;

import com.example.videosubtitling.common.DebugLogger;

public class BingTranslation {
	// NULL PARSER_REQUIRED_NAMESPACE will match any name space with any name
	private static final String PARSER_REQUIRED_NAMESPACE = null;

	private List<Entry> parse(InputStream in) throws XmlPullParserException,
			IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readFeed(parser);
		} finally {
			in.close();
		}
	}

	public static class Entry {
		private final String mLink;

		private Entry(String link) {
			this.mLink = link;
		}

		public String getLink() {
			return mLink;
		}
	}

	/**
	 * Read <entry> tag enclosed in <feed> tag from the parser
	 * 
	 * @param parser
	 *            Parser to read from
	 * @return List of Entries(Entry) populated with the url link, or empty list
	 *         if no entry found
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private List<Entry> readFeed(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		List<Entry> entries = new ArrayList<Entry>();

		parser.require(XmlPullParser.START_TAG, PARSER_REQUIRED_NAMESPACE,
				"feed");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("entry")) {
				String link = readEntry(parser);
				if (link != null) {
					entries.add(new Entry(link));
				}
			} else {
				skip(parser);
			}
		}
		return entries;
	}

	/**
	 * Read <content> tag enclosed in <entry> tag from the parser
	 * 
	 * @param parser
	 *            Parser to read from
	 * @return Text for found url link, or null if not found
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private String readEntry(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, PARSER_REQUIRED_NAMESPACE,
				"entry");
		String entry = null;
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("content")) {
				entry = readProperties(parser);
			} else {
				skip(parser);
			}

		}
		return entry;
	}

	/**
	 * Read <m:properties> tag enclosed in <content> tag from parser
	 * 
	 * @param parser
	 *            Parser to read from
	 * @return Text for found link, or null if not found
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private String readProperties(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String entry = null;
		parser.require(XmlPullParser.START_TAG, PARSER_REQUIRED_NAMESPACE,
				"content");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();

			if (name.equals("m:properties")) {
				entry = readMediaUrl(parser);
			} else {
				skip(parser);
			}
		}
		return entry;
	}

	/**
	 * Read <d:MediaUrl> tag enclosed in <m:properties> tag from the parser
	 * 
	 * @param parser
	 *            Parser to read from
	 * @return Text of url link if found, else null
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private String readMediaUrl(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String link = null;
		parser.require(XmlPullParser.START_TAG, PARSER_REQUIRED_NAMESPACE,
				"m:properties");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();

			if (name.equals("d:MediaUrl")) {
				link = readLink(parser);
			} else {
				skip(parser);
			}
		}
		return link;
	}

	/**
	 * Read link inside the <d:MediaUrl> tag from the parser
	 * 
	 * @param parser
	 *            Parser to read from
	 * @return Text enclosed in the <d:MediaUrl> tag or null if the enclosed
	 *         text doesn't exist
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private String readLink(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, PARSER_REQUIRED_NAMESPACE,
				"d:MediaUrl");
		String link = readText(parser);
		parser.require(XmlPullParser.END_TAG, PARSER_REQUIRED_NAMESPACE,
				"d:MediaUrl");
		return link;
	}

	/**
	 * Read text from parser
	 * 
	 * @param parser
	 *            Parser to read from
	 * @return Text in the next parse line, return null if the next parse item
	 *         is not text or it is in an empty tag
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private String readText(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String result = null;
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}

	/**
	 * Get number of required entries of images url based on serach text
	 * 
	 * @param textToTranslate
	 *            search text for the image search query
	 * @param numberOfEntry
	 *            number of entries required
	 * @return List of image url entries based on search text with length of the
	 *         input numberOfEntry
	 */
	public List<Entry> getTranslation(String textToTranslate, int numberOfEntry) {
		List<Entry> imageLinkList = null;
		textToTranslate = textToTranslate.replaceAll(" ", "%20");
		String accountKey = "/wKRwjudV0InQ4JXDnYinF/5OWCEzhvVq8p3+/ajzpU";
		String query = "?Query=%27";
		String bingImageSearchBasedUrl = "https://api.datamarket.azure.com/Bing/MicrosoftTranslator/v1/Translate?Text=%27No%27&To=%27ms%27&From=%27en%27";

		byte[] accountKeyBytes = Base64.encode(
				(accountKey + ":" + accountKey).getBytes(), Base64.URL_SAFE
						| Base64.NO_WRAP);
		String accountKeyEnc = new String(accountKeyBytes);
		URL url;
		HttpsURLConnection conn = null;

		try {
			url = new URL(bingImageSearchBasedUrl);
			conn = (HttpsURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(15000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("Authorization", "Basic " + accountKeyEnc);

			InputStream input = conn.getInputStream();

			System.out.println(convertStreamToString(input));
			// try {
			// imageLinkList = parse(conn.getInputStream());
			//
			// for (Entry entry : imageLinkList) {
			// DebugLogger.log(BingTranslation.class, entry.getLink());
			// }
			// } catch (XmlPullParserException e) {
			// e.printStackTrace();
			// }

			conn.disconnect();

		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			try {
				DebugLogger.log(BingTranslation.class,
						"status code: " + conn.getResponseCode());
				InputStream error = conn.getErrorStream();
				if (error != null) {
					BufferedReader errorReader = new BufferedReader(
							new InputStreamReader(error));
					String line;
					DebugLogger.log(BingTranslation.class, "error: ");
					while ((line = errorReader.readLine()) != null) {
						DebugLogger.log(BingTranslation.class, line);
					}
				}

			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return imageLinkList;
	}

	static String convertStreamToString(InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}
}
