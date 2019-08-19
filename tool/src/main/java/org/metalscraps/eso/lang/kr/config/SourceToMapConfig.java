package org.metalscraps.eso.lang.kr.config;
import org.metalscraps.eso.lang.kr.bean.PO;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Created by 안병길 on 2018-01-20.
 * Whya5448@gmail.com
 */

public class SourceToMapConfig {

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public int getKeyGroup() {
		return keyGroup;
	}

	public void setKeyGroup(int keyGroup) {
		this.keyGroup = keyGroup;
	}

	public boolean isProcessText() {
		return processText;
	}

	public void setProcessText(boolean processText) {
		this.processText = processText;
	}

	public boolean isProcessItemName() {
		return processItemName;
	}

	public void setProcessItemName(boolean processItemName) {
		this.processItemName = processItemName;
	}

	public boolean isAddFileNameToTitle() {
		return addFileNameToTitle;
	}

	public void setAddFileNameToTitle(boolean addFileNameToTitle) {
		this.addFileNameToTitle = addFileNameToTitle;
	}

	public boolean isToLowerCase() {
		return toLowerCase;
	}

	public void setToLowerCase(boolean toLowerCase) {
		this.toLowerCase = toLowerCase;
	}

	public boolean isFillEmptyTrg() {
		return isFillEmptyTrg;
	}

	public void setFillEmptyTrg(boolean fillEmptyTrg) {
		isFillEmptyTrg = fillEmptyTrg;
	}

	public boolean isRemoveComment() {
		return removeComment;
	}

	public void setRemoveComment(boolean removeComment) {
		this.removeComment = removeComment;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public PO.POWrapType getPoWrapType() {
		return poWrapType;
	}

	public void setPoWrapType(PO.POWrapType poWrapType) {
		this.poWrapType = poWrapType;
	}

	private File file = null;
	private int keyGroup = 2;
	private boolean
			processText = true,
			processItemName = true,
			addFileNameToTitle = false,
			toLowerCase = false,
			isFillEmptyTrg = true,
			removeComment;
	private String prefix, suffix;
	private Pattern pattern = null;
	private PO.POWrapType poWrapType = PO.POWrapType.WRAP_ALL;

	public void setIsFillEmptyTrg(boolean set){
		this.isFillEmptyTrg = set;
	}
}
