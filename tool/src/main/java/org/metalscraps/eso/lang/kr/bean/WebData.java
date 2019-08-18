package org.metalscraps.eso.lang.kr.bean;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.metalscraps.eso.lang.kr.config.WebPageNames;


public class WebData{
	public WebData(){

	}


	private WebPageNames PageName;
	private Document HTML;
	private Elements WebTables = null;


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


	public void addWebTable(Element table){
		if(this.WebTables == null){
			this.WebTables = new Elements();
		}
		this.WebTables.add(table);
	}
}