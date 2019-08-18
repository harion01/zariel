package org.metalscraps.eso.lang.kr.bean;


import java.util.ArrayList;

public class UpdateCSV {
    public String getUpdateName() {
        return updateName;
    }

    public void setUpdateName(String updateName) {
        this.updateName = updateName;
    }

    public ArrayList<String> getPoIndexList() {
        return PoIndexList;
    }

    public void setPoIndexList(ArrayList<String> poIndexList) {
        PoIndexList = poIndexList;
    }

    private String updateName;
    ArrayList<String> PoIndexList = new ArrayList<>();
}
