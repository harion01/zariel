package org.metalscraps.eso.lang.lib.bean;



/**
 * Created by 안병길 on 2018-01-24.
 * Whya5448@gmail.com
 */
public class ToCSVConfig {
	private boolean writeSource = false, writeFileName = false, removeComment = false, beta =false;

	public ToCSVConfig(){
	}

	public ToCSVConfig(boolean writeSource, boolean writeFileName, boolean removeComment, boolean beta) {
		this.writeSource = writeSource;
		this.writeFileName = writeFileName;
		this.removeComment = removeComment;
		this.beta = beta;
	}

	public boolean isWriteSource() {
		return writeSource;
	}

	public void setWriteSource(boolean writeSource) {
		this.writeSource = writeSource;
	}


	public boolean isWriteFileName() {
		return writeFileName;
	}

	public void setWriteFileName(boolean writeFileName) {
		this.writeFileName = writeFileName;
	}

	public boolean isRemoveComment() {
		return removeComment;
	}

	public void setRemoveComment(boolean removeComment) {
		this.removeComment = removeComment;
	}

	public boolean isBeta() {
		return beta;
	}

	public void setBeta(boolean beta) {
		this.beta = beta;
	}
}
