package org.metalscraps.eso.lang.kr.Utils;

import org.apache.commons.io.FileUtils;
import org.metalscraps.eso.lang.kr.bean.PO;
import org.metalscraps.eso.lang.kr.config.AppConfig;
import org.metalscraps.eso.lang.kr.config.AppWorkConfig;
import org.metalscraps.eso.lang.kr.config.SourceToMapConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class PoConverter {
    private AppWorkConfig appWorkConfig;

    public void setAppWorkConfig(AppWorkConfig appWorkConfig) {
        this.appWorkConfig = appWorkConfig;
    }

    public void filterNewPO() {
        Collection<File> fileList = FileUtils.listFiles(appWorkConfig.getPODirectory(), new String[]{"po"}, false);
        ArrayList<PO> LtransList = new ArrayList<>();

        for (File file : fileList) {
            SourceToMapConfig cfg = new SourceToMapConfig();
            cfg.setFile(file);
            cfg.setPattern(AppConfig.POPattern);
            ArrayList<PO> fileItems = new ArrayList<>(Utils.sourceToMap(cfg).values());
            System.out.println("target : " + file);
            for (PO oneItem : fileItems) {
                if (oneItem.getSource().equals(oneItem.getTarget())) {
                    oneItem.setTarget("");
                    LtransList.add(oneItem);
                }
            }
            System.out.println("target size: " + LtransList.size());

            String outputName = file.getName() + "_conv.po";
            this.makePOFile(outputName, LtransList);
            LtransList.clear();
        }
    }


    private void makePOFile(String filename ,ArrayList<PO> poList) {

        StringBuilder sb = new StringBuilder();
        System.out.println("po file making... file : "+appWorkConfig.getPODirectory()+"\\"+filename);
        File file = new File( appWorkConfig.getPODirectory()+"\\"+filename);
        for(PO p : poList) {
            if(p.isFuzzy()){
                sb.append("#, fuzzy\n");
            }
            sb.append("msgctxt \"").append(p.getId()).append("\"\nmsgid \"").append(p.getSource()).append("\"\nmsgstr \"").append(p.getTarget()).append("\"\n\n");
        }

        try {
            FileUtils.writeStringToFile(file, sb.toString(), AppConfig.CHARSET);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
