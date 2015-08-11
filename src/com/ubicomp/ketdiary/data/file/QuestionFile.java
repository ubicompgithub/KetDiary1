package com.ubicomp.ketdiary.data.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class of saving the Question file
 * 
 * @author Andy Chen
 */
public class QuestionFile {

	private File file;
	private BufferedWriter writer;
	private File directory;

	/**
	 * Constructor
	 * 
	 * @param directory
	 *            of the detection
	 */
	public QuestionFile(File directory) {
		this.directory = directory;
	}
	/**
	 * write the questionnaire result
	 * 
	 * @param emotion
	 *            Emotion index of the user
	 * @param craving
	 *            craving index of the user
	 */
	public void write(int day, int timeslot, int type, int items, int impact, String description) {
		String a = day + "\t" + timeslot + "\t" + type + "\t" + items + "\t" + impact + "\t" + description;
		file = new File(directory, "question.txt");
		try {
			writer = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			writer = null;
		}
		try {
			writer.write(a);
		} catch (IOException e) {
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
			}
		}
	}

}
