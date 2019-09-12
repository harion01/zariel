package org.metalscraps.eso.lang.kr.config;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.metalscraps.eso.lang.kr.Utils.CategoryGenerator;
import org.metalscraps.eso.lang.kr.bean.CategoryCSV;
import org.metalscraps.eso.lang.kr.bean.PO;
import org.metalscraps.eso.lang.kr.Utils.Utils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class CSVmergeTest {
    public static AppWorkConfig appWorkConfig;
    public static CategoryGenerator CG;
    @BeforeClass
    public static void setLang(){
        appWorkConfig = new AppWorkConfig();
        JFileChooser jFileChooser = new JFileChooser();
        File workDir = new File(jFileChooser.getCurrentDirectory().getAbsolutePath()+"/Elder Scrolls Online/EsoKR");

        jFileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) { return f.isDirectory(); }
            @Override
            public String getDescription() { return "작업 폴더 설정"; }
        });
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jFileChooser.setMultiSelectionEnabled(false);
        jFileChooser.setCurrentDirectory(workDir);
        appWorkConfig.setBaseDirectory(workDir);
        appWorkConfig.setZanataCategoryConfigDirectory(new File(appWorkConfig.getBaseDirectory()+"/ZanataCategory"));
        appWorkConfig.setPODirectory(new File(appWorkConfig.getBaseDirectory()+"/PO_"+appWorkConfig.getToday()));
        CG = new CategoryGenerator(appWorkConfig);

    }

    @Test
    public void mergeCSV() {

        AppWorkConfig appWorkConfig = new AppWorkConfig();
        JFileChooser jFileChooser = new JFileChooser();
        File workDir = new File(jFileChooser.getCurrentDirectory().getAbsolutePath() + "/Elder Scrolls Online/EsoKR");
        jFileChooser.setCurrentDirectory(workDir);
        appWorkConfig.setBaseDirectory(workDir);
        appWorkConfig.setZanataCategoryConfigDirectory(new File(appWorkConfig.getBaseDirectory() + "/ZanataCategory"));

        CategoryGenerator CG = new CategoryGenerator(appWorkConfig);
        HashMap<String, PO> CSVMap = CG.GetSelectedCSVMap();

        CSVmerge merge = new CSVmerge();
        HashMap<String, PO> targetCSV = new HashMap<>();
        File file = new File("C:\\Users\\hario\\OneDrive\\Documents\\Elder Scrolls Online\\EsoKR\\mergeTest\\test.po");
        SourceToMapConfig cfg = new SourceToMapConfig();
        cfg.setFile(file);
        cfg.setPattern(AppConfig.POPattern);
        targetCSV.putAll(Utils.sourceToMap(cfg));
        System.out.println("zanata po parsed [" + file + "] ");

        merge.MergePO(CSVMap, targetCSV, false, false);

        for(PO po : CSVMap.values()) {
            poPrint(po);
        }
    }

    public void poPrint(PO po){
        System.out.println("Is fuzzy: "+po.isFuzzy());
        System.out.println("ID : "+po.getId());
        System.out.println("Source : "+po.getSource());
        System.out.println("Target: "+po.getTarget());
    }
}