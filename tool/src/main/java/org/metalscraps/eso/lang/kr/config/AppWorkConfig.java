package org.metalscraps.eso.lang.kr.config;

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

	public File getZanataCategoryConfigDirectory() {
		return ZanataCategoryConfigDirectory;
	}

	public void setZanataCategoryConfigDirectory(File zanataCategoryConfigDirectory) {
		ZanataCategoryConfigDirectory = zanataCategoryConfigDirectory;
	}

	public String getToday() {
		return today;
	}

	public String getTodayWithYear() {
		return todayWithYear;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	private File baseDirectory, PODirectory, ZanataCategoryConfigDirectory;
	private final String today, todayWithYear;
	private LocalDateTime dateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
}
