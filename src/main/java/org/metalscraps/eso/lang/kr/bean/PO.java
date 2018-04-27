package org.metalscraps.eso.lang.kr.bean;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.metalscraps.eso.lang.kr.config.AppConfig;
import org.metalscraps.eso.lang.kr.config.FileNames;

import javax.annotation.Nullable;

/**
 * Created by 안병길 on 2018-01-18.
 * Whya5448@gmail.com
 */

@Data
public class PO implements Comparable {

	public enum POWrapType {
		WRAP_ALL,
		WRAP_SOURCE,
		WRAP_TARGET
	}

	public PO(String id, String source, String target, FileNames fileName) {
		source = source.replaceAll("\"\n\"", "");
		target = target.replaceAll("\"\n\"", "");

		this.id = id;
		this.source = source;
		this.target = target;
		this.fileName = fileName;

		String[] ids = id.split("-");
		id1 = Integer.parseInt(ids[0]);
		id2 = Integer.parseInt(ids[1]);
		id3 = Integer.parseInt(ids[2]);

		if(target.equals("")) this.target = source;
		else if(source.equals("")) this.source = target;
	}

	private Integer id1, id2, id3;

	@Setter(AccessLevel.PUBLIC) @Getter(AccessLevel.PUBLIC)
	private String id, source, target;
	private FileNames fileName;

	@Setter(AccessLevel.PUBLIC) @Getter(AccessLevel.PUBLIC)
	private boolean fuzzy = false;

	public PO wrap(@Nullable String prefix, @Nullable String suffix, POWrapType wrapType) {

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
	public String toString() { return toCSV(new ToCSVConfig(false, false, false)); }

	public String toCSV(ToCSVConfig toCSVConfig) {
		if(toCSVConfig.isRemoveComment()) target = target.replaceAll(AppConfig.englishTitlePattern, "$1");
		return "\""+id+"\",\""+(toCSVConfig.isWriteSource()?source:"")+"\",\""+(isFuzzy()?source:((toCSVConfig.isWriteFileName()?fileName.getShortName()+"_"+id3+"_":"")+target))+"\"\n";

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