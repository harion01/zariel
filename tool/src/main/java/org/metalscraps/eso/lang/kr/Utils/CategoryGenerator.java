package org.metalscraps.eso.lang.kr.Utils;


import org.apache.commons.io.FilenameUtils;
import org.metalscraps.eso.lang.kr.bean.CategoryCSV;
import org.metalscraps.eso.lang.lib.bean.PO;
import org.metalscraps.eso.lang.lib.config.AppConfig;
import org.metalscraps.eso.lang.lib.config.AppWorkConfig;
import org.metalscraps.eso.lang.lib.config.SourceToMapConfig;
import org.metalscraps.eso.lang.lib.util.Utils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;


public class CategoryGenerator {
    private PoConverter PC = new PoConverter();
    private final AppWorkConfig appWorkConfig;
    private final ArrayList<PO> sourceList = new ArrayList<>();

    public CategoryGenerator(AppWorkConfig appWorkConfig) {
        this.appWorkConfig = appWorkConfig;
    }

    public void GenSkillCategory(){
        System.out.println("Select Csv file for generate category.");
        HashMap<String, PO> CSVMap = GetSelectedCSVMap();
        HashMap<String, String> PoMap = new HashMap<>();

        for(PO po : CSVMap.values()) {
            PoMap.put(po.getId(), po.getSource());
        }


        boolean skillret;
        ArrayList<CategoryCSV> SkillCSV = new ArrayList<>();

        WebCrawler wc = new WebCrawler();
        skillret = wc.GetUESPChampionSkill(SkillCSV);
        skillret = wc.GetUESPSkillTree(SkillCSV);

        if(skillret){
            System.out.println("SkillCSV Size : "+SkillCSV.size());
            for(CategoryCSV oneCSV : SkillCSV){
                System.out.println("=========================================");
                System.out.println("Category : "+oneCSV.getCategory());
                for(String index: oneCSV.getPoIndexList() ){
                    System.out.println(index+" "+PoMap.get(index));
                }
            }
        }

    }

    public HashMap<String, PO> GetSelectedCSVMap() {
        // EsoExtractData.exe depot/eso.mnf export -a 0
        // EsoExtractData.exe -l en_0124.lang -p

        LinkedList<File> fileLinkedList = new LinkedList<>();
        HashMap<String, PO> map = new HashMap<>();

        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setMultiSelectionEnabled(false);
        jFileChooser.setCurrentDirectory(appWorkConfig.getBaseDirectory());
        jFileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return FilenameUtils.getExtension(f.getName()).equals("csv") | f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "*.csv";
            }
        });

        while (jFileChooser.showOpenDialog(null) != JFileChooser.CANCEL_OPTION) {
            jFileChooser.setCurrentDirectory(jFileChooser.getSelectedFile());
            fileLinkedList.add(jFileChooser.getSelectedFile());
        }

        if (fileLinkedList.size() == 0){
            System.out.println("no file selected!");
            return map;
        }

        SourceToMapConfig sourceToMapConfig = new SourceToMapConfig();
        sourceToMapConfig.setPattern(AppConfig.CSVPattern);
        for (File file : fileLinkedList) {
            System.out.println(file);
            sourceToMapConfig.setFile(file);
            map.putAll(Utils.sourceToMap(sourceToMapConfig));
        }



        return map;
    }

}


