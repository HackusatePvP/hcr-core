package dev.hcr.hcf.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Duration {

	private final long value;

	public Duration(long value) {
		this.value = value;
	}

	public static Duration fromString(String source) {
		if (source.equalsIgnoreCase("perm") || source.equalsIgnoreCase("permanent")) {
			return new Duration(Integer.MAX_VALUE);
		}
		long total = 0L;
		boolean found = false;
		Matcher matcher = Pattern.compile("\\d+\\D+").matcher(source);
		while (matcher.find()) {
			String string = matcher.group();
			long value = Long.parseLong(string.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0]);
			String type = string.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[1];
			switch (type) {
				case "s":
					total += value;
					found = true;
					break;
				case "m":
					total += value * 60;
					found = true;
					break;
				case "h":
					total += value * 60 * 60;
					found = true;
					break;
				case "d":
					total += value * 60 * 60 * 24;
					found = true;
					break;
				case "w":
					total += value * 60 * 60 * 24 * 7;
					found = true;
					break;
				case "M":
					total += value * 60 * 60 * 24 * 30;
					found = true;
					break;
				case "y":
					total += value * 60 * 60 * 24 * 365;
					found = true;
					break;
			}
		}
		return new Duration(!found ? -1 : total * 1000);
	}

	public long getValue() {
		return value;
	}
}