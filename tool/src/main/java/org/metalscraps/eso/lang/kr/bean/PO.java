package org.metalscraps.eso.lang.kr.bean;

import org.metalscraps.eso.lang.kr.config.FileNames;

import java.util.Comparator;
import java.util.Objects;

/**
 * Created by 안병길 on 2018-01-18.
 * Whya5448@gmail.com
 */

public class PO implements Comparable<PO> {

	public static Comparator<PO> getComparator() {
		return comparator;
	}

	public static void setComparator(Comparator<PO> comparator) {
		PO.comparator = comparator;
	}

	public Integer getId1() {
		return id1;
	}

	public void setId1(Integer id1) {
		this.id1 = id1;
	}

	public Integer getId2() {
		return id2;
	}

	public void setId2(Integer id2) {
		this.id2 = id2;
	}

	public Integer getId3() {
		return id3;
	}

	public void setId3(Integer id3) {
		this.id3 = id3;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getStringFileName() {
		return stringFileName;
	}

	public void setStringFileName(String stringFileName) {
		this.stringFileName = stringFileName;
	}

	public boolean isFuzzy() {
		return fuzzy;
	}

	public void setFuzzy(boolean fuzzy) {
		this.fuzzy = fuzzy;
	}

	public FileNames getFileName() {
		return fileName;
	}

	public void setFileName(FileNames fileName) {
		this.fileName = fileName;
	}

	public static Comparator<PO> comparator = (o1, o2) -> {
        if(!Objects.equals(o1.getId1(), o2.getId1())) return o1.getId1() - o2.getId1();
        if(!Objects.equals(o1.getId2(), o2.getId2())) return o1.getId2() - o2.getId2();
        return o1.getId3() - o2.getId3();
    };

	public enum POWrapType {
		WRAP_ALL,
		WRAP_SOURCE,
		WRAP_TARGET
	}

	public PO(String id, String source, String target) {

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

	public PO(String id, String source, String target, boolean isFillEmptyTrg) {

		this.id = id;
		this.source = source;
		this.target = target;

		String[] ids = id.split("-");
		id1 = Integer.parseInt(ids[0]);
		id2 = Integer.parseInt(ids[1]);
		id3 = Integer.parseInt(ids[2]);

		if(isFillEmptyTrg) {
			if (target.equals("")) this.target = source;
			else if (source.equals("")) this.source = target;
		}
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
	public String toString() { return toCSV(new ToCSVConfig()); }

	public String toCSV(ToCSVConfig toCSVConfig) {
		String translatedMsg = "";

		if (toCSVConfig.isWriteFileName()) {
			translatedMsg = (stringFileName + "_" + id2 + "_" + id3 + "_" + target);
		} else if (toCSVConfig.isBeta()){
			translatedMsg = target;
		} else {
			translatedMsg = target;
			if(isFuzzy() || target.contains("-G-")){
				translatedMsg = source;
			}
		}

		return "\""+id+"\",\""+(toCSVConfig.isWriteSource()?source:"")+"\",\""+translatedMsg+"\"\n";

	}

	public String toGoogleSpreadCSV(ToCSVConfig toCSVConfig) {
		String translatedMsg = "";

		if (toCSVConfig.isWriteFileName()) {
			translatedMsg = (stringFileName + "_" + id2 + "_" + id3 + "_" + target);
		} else if (toCSVConfig.isBeta()){
			translatedMsg = target;
		} else {
			translatedMsg = target;
			if(isFuzzy() || target.contains("-G-")){
				translatedMsg = source;
			}
		}

		return id+"\t"+(toCSVConfig.isWriteSource()?source:"")+"\t"+translatedMsg+"\n";

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

	public StringBuilder toPOT() {
		StringBuilder sb = new StringBuilder("\n\n#: ").append(getId());
		sb
				.append("\nmsgctxt \"").append(getId()).append("\"")
				.append("\nmsgid \"").append(getSource()).append("\"")
				.append("\nmsgstr \"\"");
		return sb;
	}

	public StringBuilder toTranslatedPO() {
		StringBuilder sb = new StringBuilder("\n\n#: ").append(getId());
		if (isFuzzy()) sb.append("\n#, fuzzy");
		sb
				.append("\nmsgctxt \"").append(getId()).append("\"")
				.append("\nmsgid \"").append(getSource()).append("\"")
				.append("\nmsgstr \"").append(getTarget()).append("\"");
		return sb;
	}

	@Override
	public int compareTo(PO o) {
		String src = Integer.toString(o.id2) + o.id3;
		String trg = Integer.toString(this.id2) + this.id3;
		if (src.equals(trg))
			return this.id1.compareTo(o.id1);
		else
			return src.compareTo(trg);
	}
}