package org.metalscraps.eso.lang.lib.config;


import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Created by 안병길 on 2018-01-24.
 * Whya5448@gmail.com
 */

public class AppWorkConfig {

	public AppWorkConfig() {
		this.today = dateTime.format(DateTimeFormatter.ofPattern("MMdd"));
		this.todayWithYear = dateTime.format(DateTimeFormatter.ofPattern("yyMMdd"));
	}


	private File baseDirectory, PODirectory;

	public String getToday() {
		return today;
	}

	private final String today, todayWithYear;

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	private LocalDateTime dateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));


	public File getBaseDirectory() {
		return baseDirectory;
	}

	public void setBaseDirectory(File baseDirectory) {
		this.baseDirectory = baseDirectory;
	}

	public File getPODirectory() {
		return PODirectory;
	}

	public void setPODirectory(File PODirectory) {
		this.PODirectory = PODirectory;
	}

	public String getTodayWithYear() {
		return todayWithYear;
	}


}
