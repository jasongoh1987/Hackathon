package com.example.videosubtitling.translation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Base64;
import android.util.Xml;

import com.example.videosubtitling.common.DebugLogger;

public class BingTranslation {

	public static final String MALAY = "ms";
	public static final String ENGLISH = "en";

	// NULL PARSER_REQUIRED_NAMESPACE will match any name space with any name
	private static final String PARSER_REQUIRED_NAMESPACE = null;

	private String parse(InputStream in) throws XmlPullParserException,
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

	/**
	 * Read <entry> tag enclosed in <feed> tag from the parser
	 * 
	 * @param parser
	 *            Parser to read from
	 * @return translation string, or empty list if no entry found
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private String readFeed(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		String entry = null;

		parser.require(XmlPullParser.START_TAG, PARSER_REQUIRED_NAMESPACE,
				"feed");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("entry")) {
				String translatedText = readEntry(parser);
				if (translatedText != null) {
					entry = translatedText;
				}
			} else {
				skip(parser);
			}
		}
		return entry;
	}

	/**
	 * Read <content> tag enclosed in <entry> tag from the parser
	 * 
	 * @param parser
	 *            Parser to read from
	 * @return Text for found translation, or null if not found
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
	 * @return Text for found translation, or null if not found
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
	 * Read <d:Text> tag enclosed in <m:properties> tag from the parser
	 * 
	 * @param parser
	 *            Parser to read from
	 * @return Text of url translation if found, else null
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private String readMediaUrl(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String translatedText = null;
		parser.require(XmlPullParser.START_TAG, PARSER_REQUIRED_NAMESPACE,
				"m:properties");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();

			if (name.equals("d:Text")) {
				translatedText = readTranslation(parser);
			} else {
				skip(parser);
			}
		}
		return translatedText;
	}

	/**
	 * Read translation inside the <d:Text> tag from the parser
	 * 
	 * @param parser
	 *            Parser to read from
	 * @return Text enclosed in the <d:Text> tag or null if the enclosed text
	 *         doesn't exist
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private String readTranslation(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, PARSER_REQUIRED_NAMESPACE,
				"d:Text");
		String translatedText = readText(parser);
		parser.require(XmlPullParser.END_TAG, PARSER_REQUIRED_NAMESPACE,
				"d:Text");
		return translatedText;
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
	 * Get translation for input text from one language to the other language
	 * 
	 * @param textToTranslate
	 *            the text to translate
	 * @param fromLanguageCode
	 *            the language which is translating from
	 * @param toLanguageCode
	 *            the language to translate to
	 * @return String which is translated
	 */
	public String getTranslation(String textToTranslate,
			String fromLanguageCode, String toLanguageCode) {
		String translatedString = textToTranslate;
		textToTranslate = textToTranslate.replaceAll(" ", "%20");
		String accountKey = "/wKRwjudV0InQ4JXDnYinF/5OWCEzhvVq8p3+/ajzpU";
		String bingTranslateUrl = "https://api.datamarket.azure.com/Bing/MicrosoftTranslator/v1/Translate?Text=%27"
				+ textToTranslate
				+ "%27&To=%27"
				+ toLanguageCode
				+ "%27&From=%27" + fromLanguageCode + "%27";

		byte[] accountKeyBytes = Base64.encode(
				(accountKey + ":" + accountKey).getBytes(), Base64.URL_SAFE
						| Base64.NO_WRAP);
		String accountKeyEnc = new String(accountKeyBytes);
		URL url;
		HttpsURLConnection conn = null;

		try {
			url = new URL(bingTranslateUrl);
			conn = (HttpsURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(15000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("Authorization", "Basic " + accountKeyEnc);

			try {
				translatedString = parse(conn.getInputStream());
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}

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
		return translatedString;
	}
}
