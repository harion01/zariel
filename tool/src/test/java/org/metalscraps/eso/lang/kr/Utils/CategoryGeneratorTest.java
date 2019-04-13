package org.metalscraps.eso.lang.kr.Utils;


import org.junit.Test;
import org.metalscraps.eso.lang.kr.bean.CategoryCSV;
import org.metalscraps.eso.lang.lib.bean.PO;
import org.metalscraps.eso.lang.lib.config.AppWorkConfig;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CategoryGeneratorTest {

    @Test
    public void genMainCategory() {
        AppWorkConfig appWorkConfig = new AppWorkConfig();
        JFileChooser jFileChooser = new JFileChooser();
        File workDir = new File(jFileChooser.getCurrentDirectory().getAbsolutePath()+"/Elder Scrolls Online/EsoKR");
        jFileChooser.setCurrentDirectory(workDir);
        appWorkConfig.setBaseDirectory(workDir);
        appWorkConfig.setZanataCategoryConfigDirectory(new File(appWorkConfig.getBaseDirectory()+"/ZanataCategory"));

        CategoryGenerator CG = new CategoryGenerator(appWorkConfig);
        CG.GenCategory();
        HashMap<String, CategoryCSV> testMap = CG.getCategoryMap();
        System.out.println("========== Category Map info ===========");
        for(String index : testMap.keySet()){
            CategoryCSV item = testMap.get(index);
            System.out.println("index ["+index+"] filename ["+item.getZanataFileName()+"] ");
        }
        System.out.println("========== Category Map info ===========");


        System.out.println("========== Category Set info ===========");
        HashSet<CategoryCSV> testSet = CG.getCategorizedCSV();
        int totalPoCount = 0;
        for(CategoryCSV csv : testSet){
            printCategory(csv);
            totalPoCount = totalPoCount + csv.getPODataMap().size();
        }
        System.out.println("========== Category Set info. total po count ["+totalPoCount+"] ===========");


    }

    public void printCategory(CategoryCSV item){
        System.out.println("Category file name ["+item.getZanataFileName()+"] type ["+item.getType()+"] indexLinkCount ["+item.getLinkCount() +
                "] index ["+item.getPoIndexList().size()+"] po count [" +item.getPODataMap().size()+ "]" );
    }


    @Test
    public void getSelectedCSVMap() {
        AppWorkConfig appWorkConfig = new AppWorkConfig();
        JFileChooser jFileChooser = new JFileChooser();
        File workDir = new File(jFileChooser.getCurrentDirectory().getAbsolutePath()+"/Elder Scrolls Online/EsoKR");
        jFileChooser.setCurrentDirectory(workDir);
        appWorkConfig.setBaseDirectory(workDir);
        appWorkConfig.setZanataCategoryConfigDirectory(new File(appWorkConfig.getBaseDirectory()+"/ZanataCategory"));

        CategoryGenerator CG = new CategoryGenerator(appWorkConfig);
        HashMap<String, PO> CSVMap = CG.GetSelectedCSVMap();

        for(String key : CSVMap.keySet()) {
            PO po = CSVMap.get(key);
            System.out.println("key [" + key+"] , filename ["+po.getFileName()+"] source ["+po.getSource()+"] target ["+po.getTarget()+"]");
        }
    }


    @Test
    public void genCategoryConfigMap() {
    }

    @Test
    public void parseCSV() {
        AppWorkConfig appWorkConfig = new AppWorkConfig();
        JFileChooser jFileChooser = new JFileChooser();
        File workDir = new File(jFileChooser.getCurrentDirectory().getAbsolutePath()+"/Elder Scrolls Online/EsoKR");
        jFileChooser.setCurrentDirectory(workDir);
        appWorkConfig.setBaseDirectory(workDir);
        appWorkConfig.setZanataCategoryConfigDirectory(new File(appWorkConfig.getBaseDirectory()+"/ZanataCategory"));

        CategoryGenerator CG = new CategoryGenerator(appWorkConfig);
        if(CG.CategoryMap.size() ==0){
            CG.GenCategoryConfigMap(appWorkConfig.getZanataCategoryConfigDirectory().toString()+"\\IndexMatch.txt");
        }

        System.out.println("Select Csv file for generate category.");
        HashMap<String, PO> CSVMap = CG.GetSelectedCSVMap();


    }

    @Test
    public void genSubCategory() {
        AppWorkConfig appWorkConfig = new AppWorkConfig();
        JFileChooser jFileChooser = new JFileChooser();
        File workDir = new File(jFileChooser.getCurrentDirectory().getAbsolutePath()+"/Elder Scrolls Online/EsoKR");
        jFileChooser.setCurrentDirectory(workDir);
        appWorkConfig.setBaseDirectory(workDir);
        appWorkConfig.setZanataCategoryConfigDirectory(new File(appWorkConfig.getBaseDirectory()+"/ZanataCategory"));

        CategoryGenerator CG = new CategoryGenerator(appWorkConfig);
        ArrayList<CategoryCSV> CategorizedSkillCsvList = new ArrayList<>();
        CG.GenSkillCategory(CategorizedSkillCsvList);
        for(CategoryCSV oneCSV : CategorizedSkillCsvList){
            System.out.println("------------------------------------------");
            System.out.println("file name : "+oneCSV.getZanataFileName());
            for(String key: oneCSV.getPoIndexList()){
                System.out.println("index : "+key);
            }
            System.out.println("------------------------------------------");
        }

    }
}