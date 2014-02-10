package no.ntnu.idi.wikiviews.aux;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class AuxIO {

	protected static final Logger LOGGER = Logger.getLogger(AuxIO.class.getName());

	public static List<String> readFileLines(String path) throws FileNotFoundException {
		Scanner input = new Scanner(new FileInputStream(path));

		ArrayList<String> lines = new ArrayList<String>();
		while (input.hasNextLine()) {
			String line = input.nextLine();
			lines.add(line);
		}
		LOGGER.info(String.format("%d lines loaded from file %s", lines.size(), path));

		return lines;
	}

	public static void writeInt(String path, int value) throws IOException {
		FileWriter f = new FileWriter(path);
		f.write(new Integer(value).toString() + "\n");
		f.flush();
		f.close();
	}

	public static int readInt(String path) throws IOException {
		FileInputStream f = new FileInputStream(path);
		Scanner s = new Scanner(f);
		int val = Integer.parseInt(s.nextLine());
		f.close();
		return val;
	}

	public static String readLine(String path) throws IOException {
		FileInputStream f = new FileInputStream(path);
		Scanner s = new Scanner(f);
		if (!s.hasNextLine()) {
			throw new EOFException();
		}
		String line = s.nextLine();
		f.close();
		return line.trim();
	}
}
