package com.high4resto.comptabilite.utils;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Formatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class TextUtil {
	/*
	 * Create a java function like String.indexOf but with regex and start index and
	 * end index
	 */
	public static int indexOf(String entry, String regex, int startIndex, int endIndex) {
		int index = -1;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(entry);
		if (matcher.find(startIndex) && matcher.start() <= endIndex) {
			index = matcher.start();
		}
		return index;
	}

	/* Create a java function like String.indexOf but with regex and start index */
	public static int indexOf(String entry, String regex, int startIndex) {
		int index = -1;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(entry);
		if (matcher.find(startIndex)) {
			index = matcher.start();
		}
		return index;
	}
	
	 // Get random string[length]
	public static String getRandomString(int length)
	{
		String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder pass = new StringBuilder();
		for(int x=0;x<length;x++)
		{
			int i = (int)Math.floor(Math.random() * 62);
			pass.append(chars.charAt(i));
		}
		return pass.toString();
	}

	/* Create a java function like String.indexOf but with regex */
	public static int indexOf(String entry, String regex) {
		int index = -1;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(entry);
		if (matcher.find()) {
			index = matcher.start();
		}
		return index;
	}

	public static double evaluateArithmeticExpression(String expression)
	{
		Expression e=new ExpressionBuilder(expression).build();
		return e.evaluate();
	}

	/* A function for find index of the most nested parenthesis ( */
	static int indexOfMostNestedRightParenthesis(String entry) {
		int index = -1;
		int count = 0;
		int max = 0;
		for (int i = 0; i < entry.length(); i++) {
			if (entry.charAt(i) == '(') {
				count++;
			} else if (entry.charAt(i) == ')') {
				count--;
			}
			if (count > max) {
				max = count;
				index = i;
			}
		}
		return index;
	}

	/* A function for find index of the most nested parenthesis ( */
	static int indexOfMostNestedLeftParenthesis(String entry) {
		int index = -1;
		int count = 0;
		int max = 0;
		for (int i = entry.length() - 1; i >= 0; i--) {
			if (entry.charAt(i) == ')') {
				count++;
			} else if (entry.charAt(i) == '(') {
				count--;
			}
			if (count > max) {
				max = count;
				index = i;
			}
		}
		return index;
	}

	// Compare 2 strings with Levenshtein distance
	public static int compareWithLevenshtein(String entry1, String entry2) {

		int[][] distance = new int[entry1.length() + 1][entry2.length() + 1];

		for (int i = 0; i <= entry1.length(); i++)
			distance[i][0] = i;
		for (int j = 1; j <= entry2.length(); j++)
			distance[0][j] = j;

		for (int i = 1; i <= entry1.length(); i++)
			for (int j = 1; j <= entry2.length(); j++)
				distance[i][j] = minimum(
						distance[i - 1][j] + 1,
						distance[i][j - 1] + 1,
						distance[i - 1][j - 1]
								+ ((entry1.charAt(i - 1) == entry2.charAt(j - 1)) ? 0
										: 1));

		return distance[entry1.length()][entry2.length()];
	}

	// Generate Sha1 hash from a string
	public static String generateSha1(String entry) {
		String sha1 = "";
		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(entry.getBytes("UTF-8"));
			sha1 = byteArray2Hex(crypt.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sha1;
	}

	// Compare 2 strings with Jaro distance
	public static Double compareWithJaro(final CharSequence left, final CharSequence right) {
		final double defaultScalingFactor = 0.1;
		final double percentageRoundValue = 100.0;

		if (left == null || right == null) {
			throw new IllegalArgumentException("Strings must not be null");
		}

		int[] mtp = matches(left, right);
		double m = mtp[0];
		if (m == 0) {
			return 0D;
		}
		double j = ((m / left.length() + m / right.length() + (m - mtp[1]) / m)) / 3;
		double jw = j < 0.7D ? j : j + Math.min(defaultScalingFactor, 1D / mtp[3]) * mtp[2] * (1D - j);
		return Math.round(jw * percentageRoundValue) / percentageRoundValue;
	}

	protected static int[] matches(final CharSequence first, final CharSequence second) {
		CharSequence max, min;
		if (first.length() > second.length()) {
			max = first;
			min = second;
		} else {
			max = second;
			min = first;
		}
		int range = Math.max(max.length() / 2 - 1, 0);
		int[] matchIndexes = new int[min.length()];
		Arrays.fill(matchIndexes, -1);
		boolean[] matchFlags = new boolean[max.length()];
		int matches = 0;
		for (int mi = 0; mi < min.length(); mi++) {
			char c1 = min.charAt(mi);
			for (int xi = Math.max(mi - range, 0), xn = Math.min(mi + range + 1, max.length()); xi < xn; xi++) {
				if (!matchFlags[xi] && c1 == max.charAt(xi)) {
					matchIndexes[mi] = xi;
					matchFlags[xi] = true;
					matches++;
					break;
				}
			}
		}
		char[] ms1 = new char[matches];
		char[] ms2 = new char[matches];
		for (int i = 0, si = 0; i < min.length(); i++) {
			if (matchIndexes[i] != -1) {
				ms1[si] = min.charAt(i);
				si++;
			}
		}
		for (int i = 0, si = 0; i < max.length(); i++) {
			if (matchFlags[i]) {
				ms2[si] = max.charAt(i);
				si++;
			}
		}
		int transpositions = 0;
		for (int mi = 0; mi < ms1.length; mi++) {
			if (ms1[mi] != ms2[mi]) {
				transpositions++;
			}
		}
		int prefix = 0;
		for (int mi = 0; mi < min.length(); mi++) {
			if (first.charAt(mi) == second.charAt(mi)) {
				prefix++;
			} else {
				break;
			}
		}
		return new int[] { matches, transpositions / 2, prefix, max.length() };
	}

	private static int minimum(int a, int b, int c) {
		return Math.min(Math.min(a, b), c);
	}

	// Create SHAsum from byte array
	public static String SHAsum(byte[] convertme) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		return byteArray2Hex(md.digest(convertme));
	}

	private static String byteArray2Hex(final byte[] hash) {
		try (Formatter formatter = new Formatter()) {
			for (byte b : hash) {
				formatter.format("%02x", b);
			}
			return formatter.toString();
		}
	}

	// Convert pdf to text
	public static String getTextFromPDF(byte[] content) throws IOException {
		PDDocument doc = PDDocument.load(content);
		String stripper = new PDFTextStripper().getText(doc);
		if (doc != null)
			doc.close();
		return stripper;
	}


}
