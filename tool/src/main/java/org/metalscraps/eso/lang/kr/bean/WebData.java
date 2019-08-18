package org.metalscraps.eso.lang.kr.bean;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.metalscraps.eso.lang.kr.config.WebPageNames;

import java.util.ArrayList;
import java.util.HashMap;

public class WebData{
	public WebData(){

	}

	public String getItemFileName() {
		return ItemFileName;
	}

	public void setItemFileName(String itemFileName) {
		ItemFileName = itemFileName;
	}

	public String getItemURL() {
		return ItemURL;
	}

	public void setItemURL(String itemURL) {
		ItemURL = itemURL;
	}

	public WebPageNames getPageName() {
		return PageName;
	}

	public void setPageName(WebPageNames pageName) {
		PageName = pageName;
	}

	public Document getHTML() {
		return HTML;
	}

	public void setHTML(Document HTML) {
		this.HTML = HTML;
	}

	public Elements getWebTables() {
		return WebTables;
	}

	public void setWebTables(Elements webTables) {
		WebTables = webTables;
	}

	public HashMap<String, ArrayList<String>> getBookMap() {
		return bookMap;
	}

	public void setBookMap(HashMap<String, ArrayList<String>> bookMap) {
		this.bookMap = bookMap;
	}

	private String ItemFileName, ItemURL;
	private WebPageNames PageName;
	private Document HTML;
	private Elements WebTables = null;
	private HashMap<String, ArrayList<String>> bookMap = null;

	public void addWebTable(Element table){
		if(this.WebTables == null){
			this.WebTables = new Elements();
		}
		this.WebTables.add(table);
	}

	public void putBookMap(String category, ArrayList<String> titles){
		if(this.bookMap == null){
			this.bookMap = new HashMap<>();
		}
		bookMap.put(category, titles);
	}
}