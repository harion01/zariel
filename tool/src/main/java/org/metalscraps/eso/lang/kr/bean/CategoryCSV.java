package org.metalscraps.eso.lang.kr.bean;



import org.metalscraps.eso.lang.lib.bean.PO;

import java.util.ArrayList;


public class CategoryCSV {


	private String Category;
	private ArrayList<String> PoIndexList, CSVList = null;
	private ArrayList<PO> PODataList = null;

	public ArrayList<String> getPoIndexList() {
		return PoIndexList;
	}

	public void setPoIndexList(ArrayList<String> poIndexList) {
		PoIndexList = poIndexList;
	}

	public ArrayList<String> getCSVList() {
		return CSVList;
	}

	public void setCSVList(ArrayList<String> CSVList) {
		this.CSVList = CSVList;
	}

	public ArrayList<PO> getPODataList() {
		return PODataList;
	}

	public void setPODataList(ArrayList<PO> PODataList) {
		this.PODataList = PODataList;
	}


	public void addPoIndex(String Index){
		if(this.PoIndexList == null){
			this.PoIndexList = new ArrayList<>();
		}
		PoIndexList.add(Index);
	}

	public void addCSV(String oneCSVItem){
		if(this.PoIndexList == null){
			this.PoIndexList = new ArrayList<>();
		}
		PoIndexList.add(oneCSVItem);
	}

	public void addPoData(PO po){
		if(this.PODataList == null){
			this.PODataList = new ArrayList<>();
		}
		PODataList.add(po);
	}


	public String getCategory() {
		return Category;
	}

	public void setCategory(String category) {
		Category = category;
	}
}