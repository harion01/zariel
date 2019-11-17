package org.metalscraps.eso.lang.kr.config;

import org.metalscraps.eso.lang.kr.bean.CategoryCSV;
import org.metalscraps.eso.lang.kr.bean.PO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CSVmerge {
    public HashMap<String, CategoryCSV> getMergedCSV() {
        return mergedCSV;
    }

    public void setMergedCSV(HashMap<String, CategoryCSV> mergedCSV) {
        this.mergedCSV = mergedCSV;
    }

    private HashMap<String, CategoryCSV> mergedCSV = new HashMap<>();

    public void MergeCSV (HashSet<CategoryCSV> CategorizedClientCSV, HashMap<String, PO> targetPO, boolean isJapMerge){
        for(CategoryCSV oneCSV : CategorizedClientCSV){
            System.out.println("Merge oncCSV. name ["+oneCSV.getZanataFileName());
            HashMap<String, PO> clientPO = oneCSV.getPODataMap();
            boolean oldTag = false;
            if(oneCSV.getType().equals("item") || oneCSV.getType().equals("skill") ) {
                oldTag = true;
            }
            MergePO(clientPO, targetPO, isJapMerge, oldTag);

            if("book".equals(oneCSV.getType()) || "story".equals(oneCSV.getType())) {
                OverwriteDuplicate(oneCSV);
            }

        }
    }


    public void MergePO(HashMap<String, PO> CategorizedClientPO, HashMap<String, PO> FullPO, boolean isJap, boolean setOLDtag){
        for(String index : CategorizedClientPO.keySet()){
            PO basePO = CategorizedClientPO.get(index);
            //System.out.println(index + "] po ["+ basePO);

            PO targetPO = FullPO.get(index);
            if(targetPO == null){
                System.out.println("no index in target:"+index);
                continue;
            } else {
                if(basePO.getSource().equals(targetPO.getSource())) {
                    basePO.setTarget(targetPO.getTarget());
                    basePO.setFuzzy(targetPO.isFuzzy());
                } else {
                    if(isJap) {
                        basePO.setTarget(targetPO.getSource());
                    } else {
                        System.out.println("source data changed. index :"+index);
                        basePO.setFuzzy(true);
                        basePO.setTarget(targetPO.getTarget());
                    }
                }
                basePO.setStringFileName(targetPO.getStringFileName());
                basePO.setNewData(false);
            }
        }
    }

    private void OverwriteDuplicate(CategoryCSV CategorizedCSV) {
        HashMap<String, PO> poMap = CategorizedCSV.getPODataMap();
        HashMap<String, PO> translatedPoMap = new HashMap<>();
        ArrayList<PO> nonTransPoList = new ArrayList<>();

        for(PO po : poMap.values()){
            if(po.getTarget() == null || "".equals(po.getTarget())){
                nonTransPoList.add(po);
            }else {
                translatedPoMap.put(po.getSource(), po);
            }
        }

        for(PO po : nonTransPoList){
            PO sameSource = translatedPoMap.get(po.getSource());
            if(sameSource != null){
                po.setTarget(sameSource.getTarget());
            }
        }

    }

}
