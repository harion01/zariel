package org.metalscraps.eso.lang.tool.Utils;


import org.junit.Test;
import org.metalscraps.eso.lang.lib.bean.PO;
import org.metalscraps.eso.lang.lib.config.AppWorkConfig;
import org.metalscraps.eso.lang.lib.util.Utils;
import org.metalscraps.eso.lang.tool.bean.CategoryCSV;

import javax.swing.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CategoryGeneratorTest {

    @Test
    public void genMainCategory() {
        AppWorkConfig appWorkConfig = new AppWorkConfig();
        JFileChooser jFileChooser = new JFileChooser();
        Path workDir = Utils.getESODir().resolve("EsoKR");
        jFileChooser.setCurrentDirectory(workDir.toFile());
        appWorkConfig.setBaseDirectoryToPath(workDir);
        appWorkConfig.setZanataCategoryConfigDirectoryToPath(appWorkConfig.getBaseDirectoryToPath().resolve("ZanataCategory"));

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
        var workDir = Utils.getESODir().resolve("EsoKR");
        jFileChooser.setCurrentDirectory(workDir.toFile());
        appWorkConfig.setBaseDirectoryToPath(workDir);
        appWorkConfig.setZanataCategoryConfigDirectoryToPath(appWorkConfig.getBaseDirectoryToPath().resolve("ZanataCategory"));

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
    public void parseMainCategorizedCSV() {
    }

    @Test
    public void genSubCategory() {
        AppWorkConfig appWorkConfig = new AppWorkConfig();
        JFileChooser jFileChooser = new JFileChooser();
        var workDir = Utils.getESODir().resolve("EsoKR");
        jFileChooser.setCurrentDirectory(workDir.toFile());
        appWorkConfig.setBaseDirectoryToPath(workDir);
        appWorkConfig.setZanataCategoryConfigDirectoryToPath(appWorkConfig.getBaseDirectoryToPath().resolve("ZanataCategory"));

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