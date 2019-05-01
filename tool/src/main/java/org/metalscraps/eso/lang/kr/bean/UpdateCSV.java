package org.metalscraps.eso.lang.kr.bean;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class UpdateCSV {
    @Setter(AccessLevel.PUBLIC) @Getter(AccessLevel.PUBLIC)
    private String updateName;

    @Setter(AccessLevel.PUBLIC) @Getter(AccessLevel.PUBLIC)
    ArrayList<String> PoIndexList = new ArrayList<>();
}
