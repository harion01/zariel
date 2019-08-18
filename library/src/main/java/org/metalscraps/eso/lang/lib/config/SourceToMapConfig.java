package org.metalscraps.eso.lang.lib.config;


import ch.qos.logback.core.html.HTMLLayoutBase;
import org.metalscraps.eso.lang.lib.bean.PO;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Created by 안병길 on 2018-01-20.
 * Whya5448@gmail.com
 */


public class SourceToMapConfig {

	public void setFile(File file) {
		this.file = file;
	}

	public void setKeyGroup(int keyGroup) {
		this.keyGroup = keyGroup;
	}

	public void setProcessText(boolean processText) {
		this.processText = processText;
	}

	public void setProcessItemName(boolean processItemName) {
		this.processItemName = processItemName;
	}

	public void setAddFileNameToTitle(boolean addFileNameToTitle) {
		this.addFileNameToTitle = addFileNameToTitle;
	}

	public void setToLowerCase(boolean toLowerCase) {
		this.toLowerCase = toLowerCase;
	}

	public void setRemoveComment(boolean removeComment) {
		this.removeComment = removeComment;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public void setPoWrapType(PO.POWrapType poWrapType) {
		this.poWrapType = poWrapType;
	}

	private File file = null;
	private int keyGroup = 2;

	public File getFile() {
		return file;
	}

	public int getKeyGroup() {
		return keyGroup;
	}

	public boolean isProcessText() {
		return processText;
	}

	public boolean isProcessItemName() {
		return processItemName;
	}

	public boolean isAddFileNameToTitle() {
		return addFileNameToTitle;
	}

	public boolean isToLowerCase() {
		return toLowerCase;
	}

	public boolean isRemoveComment() {
		return removeComment;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public PO.POWrapType getPoWrapType() {
		return poWrapType;
	}

	private boolean
			processText = true,
			processItemName = true,
			addFileNameToTitle = false,
			toLowerCase = false,
			removeComment;
	private String prefix, suffix;
	private Pattern pattern = null;
	private PO.POWrapType poWrapType = PO.POWrapType.WRAP_ALL;


}
