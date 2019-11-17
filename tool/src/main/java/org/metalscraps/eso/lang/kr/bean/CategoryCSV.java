package org.metalscraps.eso.lang.kr.bean;

import java.util.ArrayList;
import java.util.HashMap;

public class CategoryCSV {

	public String getZanataFileName() {
		return zanataFileName;
	}

	public void setZanataFileName(String zanataFileName) {
		this.zanataFileName = zanataFileName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLinkCount() {
		return linkCount;
	}

	public void setLinkCount(int linkCount) {
		this.linkCount = linkCount;
	}

	public ArrayList<String> getPoIndexList() {
		return PoIndexList;
	}

	public void setPoIndexList(ArrayList<String> poIndexList) {
		PoIndexList = poIndexList;
	}

	public HashMap<String, PO> getPODataMap() {
		return PODataMap;
	}

	public void setPODataMap(HashMap<String, PO> PODataMap) {
		this.PODataMap = PODataMap;
	}

	private String zanataFileName;
	private String type;
	private int linkCount;
	ArrayList<String> PoIndexList = new ArrayList<>();
	HashMap<String, PO> PODataMap = new HashMap<>();

	public void addPoIndex(String Index){
		PoIndexList.add(Index);
	}
	public void putPoData(String index, PO po){
		PODataMap.put(index, po);
	}

	public void removePoIndex(String index){
		PoIndexList.remove(index);
	}
	public void removePoData(String index){
		PODataMap.remove(index);
	}

	public void addPoList(ArrayList<String> targetIndexList){
		PoIndexList.addAll(targetIndexList);
	}

	public void addPoMap(HashMap<String, PO> targetPODataMap ){
		PODataMap.putAll(targetPODataMap);
	}
}