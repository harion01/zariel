package org.metalscraps.eso.lang.kr.Utils;
import org.junit.Test;
import org.metalscraps.eso.lang.kr.bean.PO;
import org.metalscraps.eso.lang.kr.config.AppConfig;
import org.metalscraps.eso.lang.kr.config.SourceToMapConfig;

import java.io.File;
import java.util.HashMap;


public class UtilTest {
    @Test
    public void ParseTest() {
        File file = new File("C:\\Users\\hario\\OneDrive\\Documents\\Elder Scrolls Online\\EsoKR\\PO_0901\\Weapon-test.po");

        SourceToMapConfig cfg = new SourceToMapConfig();
        cfg.setFile(file);
        cfg.setPattern(AppConfig.POPattern);
        cfg.setIsFillEmptyTrg(false);
        HashMap<String, PO> POMap =  Utils.sourceToMap(cfg);

        for(PO po : POMap.values()){
            System.out.println();
            System.out.println();
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
