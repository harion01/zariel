package org.metalscraps.eso.lang.lib.bean;

import org.metalscraps.eso.lang.lib.config.AppConfig;
import org.metalscraps.eso.lang.lib.config.FileNames;

/**
 * Created by 안병길 on 2018-01-18.
 * Whya5448@gmail.com
 */


public class PO implements Comparable {

	public boolean isFuzzy() {
		return fuzzy;
	}

	public void setFuzzy(boolean fuzzy) {
		this.fuzzy = fuzzy;
	}

	public void setStringFileName(String stringFileName) {
		this.stringFileName = stringFileName;
	}

	public FileNames getFileName() {
		return fileName;
	}

	public void setFileName(FileNames fileName) {
		this.fileName = fileName;
	}

	public String getId() {
		return this.id;
	}

	public String getSource() {
		return this.source;
	}

	public String getTarget() {
		return this.target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public int getId1() {
		return id1;
	}

	public void setId1(int id1) {
		this.id1 = id1;
	}


	public enum POWrapType {
		WRAP_ALL,
		WRAP_SOURCE,
		WRAP_TARGET
	}

	public PO(String id, String source, String target) {
		source = source.replaceAll("\"\n\"", "");
		target = target.replaceAll("\"\n\"", "");

		this.id = id;
		this.source = source;
		this.target = target;

		String[] ids = id.split("-");
		id1 = Integer.parseInt(ids[0]);
		id2 = Integer.parseInt(ids[1]);
		id3 = Integer.parseInt(ids[2]);

		if(target.equals("")) this.target = source;
		else if(source.equals("")) this.source = target;
	}

	private Integer id1, id2, id3;
	private String id, source, target, stringFileName;
	private boolean fuzzy = false;
	private FileNames fileName;


	public boolean modifyDoubleQuart(){
		this.source  =source.replace("\"\"", "\"");
		this.target  =target.replace("\"\"", "\"");
		return true;
	}

	public PO wrap(String prefix, String suffix, POWrapType wrapType) {

		if (prefix == null) prefix = "";
		if (suffix == null) suffix = "";

		if(wrapType == POWrapType.WRAP_ALL) {
			if(!source.equals("")) source = prefix + source + suffix;
			if(!target.equals("")) target = prefix + target + suffix;
		} else if(wrapType == POWrapType.WRAP_SOURCE && !source.equals("")) source = prefix + source + suffix;
		else if(wrapType == POWrapType.WRAP_TARGET && !target.equals("")) target = prefix + target + suffix;

		return this;
	}

	@Override
	public String toString() { return toCSV(new ToCSVConfig(false, false, false, false)); }


	public String toCSV(ToCSVConfig toCSVConfig) {
		String t = "";

		if(toCSVConfig.isRemoveComment()) target = target.replaceAll(AppConfig.englishTitlePattern, "$1");

		if(toCSVConfig.isWriteFileName()) t = (stringFileName +  "_" + id2 + "_" + id3 + "_" + target);
		else if(!toCSVConfig.isBeta() && isFuzzy() && target.contains("-G-")) t = source;
		else t = target;

		return "\""+id+"\",\""+(toCSVConfig.isWriteSource()?source:"")+"\",\""+t+"\"\n";

	}



	public StringBuilder toPO() {
		StringBuilder sb = new StringBuilder("\n\n#: ").append(getId());
		if(isFuzzy()) sb.append("\n#, fuzzy");
		sb
				.append("\nmsgctxt \"").append(getId()).append("\"")
				.append("\nmsgid \"").append(getSource()).append("\"")
				.append("\nmsgstr \"\"");
		return sb;
	}




	@Override
	public int compareTo(Object o) {
		PO x = (PO) o;
		PO t = this;
		if(t.id1.equals(x.id1)) {
			if(t.id2.equals(x.id2)) return t.id3.compareTo(x.id3);
			else return t.id2.compareTo(x.id2);
		} else return t.id1.compareTo(x.id1);

	}

}