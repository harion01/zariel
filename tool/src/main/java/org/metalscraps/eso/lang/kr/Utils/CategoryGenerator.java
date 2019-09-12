package org.metalscraps.eso.lang.kr.Utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.metalscraps.eso.lang.kr.bean.CategoryCSV;
import org.metalscraps.eso.lang.kr.bean.UpdateCSV;
import org.metalscraps.eso.lang.kr.bean.PO;
import org.metalscraps.eso.lang.kr.config.AppConfig;
import org.metalscraps.eso.lang.kr.config.AppWorkConfig;
import org.metalscraps.eso.lang.kr.config.FileNames;
import org.metalscraps.eso.lang.kr.config.SourceToMapConfig;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;


public class CategoryGenerator {
    private PoConverter PC = new PoConverter();
    private final AppWorkConfig appWorkConfig;

    public PoConverter getPC() {
        return PC;
    }

    public void setPC(PoConverter PC) {
        this.PC = PC;
    }

    public AppWorkConfig getAppWorkConfig() {
        return appWorkConfig;
    }

    public ArrayList<PO> getSourceList() {
        return sourceList;
    }

    public HashMap<String, CategoryCSV> getCategoryMap() {
        return CategoryMap;
    }

    public void setCategoryMap(HashMap<String, CategoryCSV> categoryMap) {
        CategoryMap = categoryMap;
    }

    public HashSet<CategoryCSV> getCategorizedCSV() {
        return CategorizedCSV;
    }

    public void setCategorizedCSV(HashSet<CategoryCSV> categorizedCSV) {
        CategorizedCSV = categorizedCSV;
    }

    private final ArrayList<PO> sourceList = new ArrayList<>();
    public HashMap<String, CategoryCSV> CategoryMap = new HashMap<>();
    private HashSet<CategoryCSV> CategorizedCSV = new HashSet<>();
    public CategoryGenerator(AppWorkConfig appWorkConfig) {
        this.appWorkConfig = appWorkConfig;
    }

    public void GenCategory(){
        if(CategoryMap .size() ==0){
            GenCategoryConfigMap(appWorkConfig.getZanataCategoryConfigDirectory().toString()+"\\IndexMatch.txt");
        }

        System.out.println("Select Csv file for generate category.");
        HashMap<String, PO> CSVMap = GetSelectedCSVMap();
        GenSubCategory(CSVMap);
        GenMainCategory(CSVMap);
        for(CategoryCSV oneCSV : CategorizedCSV){
            if("book".equals(oneCSV.getZanataFileName())){
                GenBookSubCategory(oneCSV);
                break;
            }
        }

        for(CategoryCSV oneCSV : CategorizedCSV) {
            if ("set".equals(oneCSV.getZanataFileName())) {
                GenSetSubCategory(oneCSV);
                break;
            }
        }

    }


    public void GenMainCategory(HashMap<String, PO> CSVMap){

        ParseMainCategorizedCSV(CSVMap);
    }

    public void SplitCategoryByUpdate(ArrayList<CategoryCSV> fullCSVList){

        ArrayList<UpdateCSV> updateCSVList = ParseUpdateID();

        ArrayList<CategoryCSV> splitCsvList = new ArrayList<>();
        for(CategoryCSV originCSV : fullCSVList) {
            System.out.println("origin csv name ["+originCSV.getZanataFileName()+"] po count ["+originCSV.getPODataMap().size()+"]");
            ArrayList<CategoryCSV> subsplitList = new ArrayList<>();
            for(UpdateCSV oneUpdate : updateCSVList){
                CategoryCSV splitCsv = new CategoryCSV();
                splitCsv.setZanataFileName(oneUpdate.getUpdateName()+"-"+originCSV.getZanataFileName());
                splitCsv.setType(originCSV.getType());
                splitCsv.setLinkCount(originCSV.getLinkCount());
                HashMap<String, PO> splitDataMap = new HashMap<>();
                for(String id : oneUpdate.getPoIndexList()){
                    PO po = originCSV.getPODataMap().get(id);
                    if(po != null){
                        splitDataMap.put(id,po);
                        originCSV.getPODataMap().remove(id);
                    }
                }
                splitCsv.setPODataMap(splitDataMap);
                subsplitList.add(splitCsv);
            }
            System.out.println("origin csv name ["+originCSV.getZanataFileName()+"] po count ["+originCSV.getPODataMap().size()+"]");
            for(CategoryCSV subCsv : subsplitList){
                System.out.println("subsplit csv name ["+subCsv.getZanataFileName()+"] po count ["+subCsv.getPODataMap().size()+"]");
            }

            splitCsvList.addAll(subsplitList);
        }

        this.CategorizedCSV.addAll(splitCsvList);
    }

    public ArrayList<UpdateCSV>  ParseUpdateID() {
        ArrayList<UpdateCSV> returnCSV = new ArrayList<>();

        LinkedList<File> fileLinkedList = new LinkedList<>();
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

        //old version input
        System.out.println("select update id files");
        while (jFileChooser.showOpenDialog(null) != JFileChooser.CANCEL_OPTION) {
            jFileChooser.setCurrentDirectory(jFileChooser.getSelectedFile());
            fileLinkedList.add(jFileChooser.getSelectedFile());
        }

        if (fileLinkedList.size() == 0){
            System.out.println("no file selected!");
            return null;
        }

        for(File file : fileLinkedList) {
            ArrayList<String> idArr = new ArrayList<>();
            System.out.println("version file [" + file.getPath() + "]");
            String updateID = null;
            try {
                updateID = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            } catch (Exception ex){
                ex.printStackTrace();
            }

            String[] idSplit = updateID.split("\n");
            Collections.addAll(idArr, idSplit);
            String updateName = file.getName().substring(0,file.getName().indexOf("_"));
            UpdateCSV upCSV = new UpdateCSV();
            upCSV.setUpdateName(updateName);
            upCSV.setPoIndexList(idArr);
            returnCSV.add(upCSV);
        }

        return returnCSV;
    }


    public void GenCategoryConfigMap(String indexFileName){
        File file = new File(indexFileName);
        String fileString = "";
        String localIndex = "";

        try {
            fileString =  FileUtils.readFileToString(file, AppConfig.CHARSET);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Matcher matcher = AppConfig.CategoryConfig.matcher(fileString);
        while(matcher.find()){
            localIndex = matcher.group(17);
            String[] localIndexList  = localIndex.split(",");

            CategoryCSV categoryCSV = new CategoryCSV();
            categoryCSV.setZanataFileName(matcher.group(1));
            categoryCSV.setType(matcher.group(9));
            categoryCSV.setLinkCount(Integer.parseInt(matcher.group(13)));
            for(String index : localIndexList) {
                categoryCSV.addPoIndex(index);
                CategoryMap.put(index, categoryCSV);
            }
        }

        CategoryCSV UndefinedCategoryCSV = new CategoryCSV();
        UndefinedCategoryCSV.setZanataFileName("Undefined");
        UndefinedCategoryCSV.setType("system");
        UndefinedCategoryCSV.setLinkCount(0);
        CategoryMap.put("Undefined", UndefinedCategoryCSV);
    }

    public HashMap<String, PO> GetSelectedOldCSVMap() {
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

    public void ParseMainCategorizedCSV(HashMap<String, PO> CSVMap){
        for(PO onePO : CSVMap.values()){
            CategoryCSV categoryCSV = this.getCategoryMap().get(onePO.getId1().toString());
            if(categoryCSV == null){
                categoryCSV = this.getCategoryMap().get("Undefined");
            }
            onePO.setFileName(FileNames.fromString(categoryCSV.getZanataFileName()));
            categoryCSV.putPoData(onePO.getId(), onePO);
        }

        this.CategorizedCSV.addAll(this.getCategoryMap().values());
    }

    public void GenSubCategory(HashMap<String, PO> MainPoMap){
        ArrayList<CategoryCSV> CategorizedSkillCsvList = new ArrayList<>();
        boolean genRet = GenSkillCategory(CategorizedSkillCsvList);
        if(genRet){
            System.out.println("SkillCSV Size : "+CategorizedSkillCsvList.size());
            for(CategoryCSV oneCSV : CategorizedSkillCsvList){
                for(String index: oneCSV.getPoIndexList() ) {
                    categorizePOMapData(MainPoMap, oneCSV, index);
                }
            }
        }

        for(CategoryCSV oneCSV : CategorizedSkillCsvList) {
            oneCSV.setType("skill");
            CategorizedCSV.add(oneCSV);
        }

        ArrayList<CategoryCSV> CategorizedItemCsvList = new ArrayList<>();
        genRet = GetItemCategory(CategorizedItemCsvList);
        if(genRet){
            System.out.println("ItemCSV Size : "+CategorizedItemCsvList.size());
            for(CategoryCSV oneCSV : CategorizedItemCsvList){
                for(String index: oneCSV.getPoIndexList() ){
                    categorizePOMapData(MainPoMap, oneCSV, index);
                }
            }
        }

        for(CategoryCSV oneCSV : CategorizedItemCsvList) {
            oneCSV.setType("item");
            CategorizedCSV.add(oneCSV);
        }

    }

    private void categorizePOMapData(HashMap<String, PO> MainPoMap, CategoryCSV oneCSV, String index) {
        PO poData = MainPoMap.get(index);
        if(poData != null) {
            if(poData.getFileName() == null) {
                poData.setFileName(FileNames.fromString(oneCSV.getZanataFileName()));
            }
            oneCSV.putPoData(index, poData);
            MainPoMap.remove(index);
        }
    }

    public boolean GenSkillCategory(ArrayList<CategoryCSV> SkillCSV){
        boolean wbCrawlRet;
        WebCrawler wc = new WebCrawler();
        try {
            if(!wc.GetUESPChampionSkill(SkillCSV)){
                throw new Exception("ChampionSkill gen fail");
            }
            if(!wc.GetUESPSkillTree(SkillCSV)){
                throw new Exception("Skill tree gen fail");
            }
            wbCrawlRet = true;
        }catch(Exception ex){
            wbCrawlRet = false;
        }
        return wbCrawlRet;
    }


    private void GenBookSubCategory(CategoryCSV oneCSV) {
        WebCrawler wc = new WebCrawler();
        ArrayList<CategoryCSV> CategorizedBookCsvList = GenUESPBookSubCategory( wc.GetUESPBookMap(), oneCSV);
        this.CategorizedCSV.addAll(CategorizedBookCsvList);
    }


    private void
    GenSetSubCategory(CategoryCSV setCSV) {
        ArrayList<PO> setName = new ArrayList<>();
        for(PO po : setCSV.getPODataMap().values()) {
            if(po.getId1() == 38727365) {
                setName.add(po);
            }
        }

        for(CategoryCSV oneCsv : this.getCategorizedCSV()){
            if(oneCsv.getZanataFileName().equals("skill")){
                GenSetSkillCategory(setName, oneCsv);
                break;
            }
        }

    }

    private void GenSetSkillCategory(ArrayList<PO> setName, CategoryCSV skillCSV) {
        CategoryCSV setSkillCSV = new CategoryCSV();
        setSkillCSV.setZanataFileName("SetItemSkill");
        setSkillCSV.setType("skill");

        System.out.println("gen set skill start. skill size ["+skillCSV.getPODataMap().size()+"]");

        HashMap<String, String> skillNameMap = new HashMap<>();
        for(PO po : skillCSV.getPODataMap().values()){
            if(po.getId1() == 198758357){
                skillNameMap.put(po.getSource(), po.getId());
            }
        }

        ArrayList<String> skillNameArr = new ArrayList<>();
        for(PO namePO : setName){
            String skillNameID = skillNameMap.get(namePO.getSource());
            if(skillNameID != null){
                skillNameArr.add(skillNameID);
            }else {
                System.out.println("no id for ["+ namePO.getSource() +"]");
            }
        }

        HashMap<String, PO> skillFullMap = skillCSV.getPODataMap();
        for(String skillNameID : skillNameArr){
            //System.out.println("skillName id["+skillNameID+"]");
            String skillDescID = skillNameID.replaceAll("198758357", "132143172");
            PO namePO = skillFullMap.get(skillNameID);
            PO descPO = skillFullMap.get(skillDescID);
            if(namePO != null && descPO !=null){
                setSkillCSV.addPoIndex(skillNameID);
                setSkillCSV.addPoIndex(skillDescID);
                setSkillCSV.putPoData(skillNameID, namePO);
                setSkillCSV.putPoData(skillDescID, descPO);
                skillFullMap.remove(skillNameID);
                skillFullMap.remove(skillDescID);
            }
        }

        System.out.println("Set skill item size ["+setSkillCSV.getPODataMap().size()+"]");
        System.out.println("gen set skill end. skill size ["+skillCSV.getPODataMap().size()+"]");

        this.CategorizedCSV.add(setSkillCSV);
    }



    private ArrayList<CategoryCSV> GenUESPBookSubCategory(HashMap<String, ArrayList<String>> BookNameMap, CategoryCSV BookCSV){
        ArrayList<CategoryCSV> bookList = new ArrayList<>();
        HashMap<String, PO> BookPOMap = BookCSV.getPODataMap();
        HashMap<String, PO> SourcePOMap = new HashMap<>();
        for(PO po : BookPOMap.values()) {
            if(po.getId1() == 51188213) {
                SourcePOMap.put(po.getSource(), po);
            }
        }

        CategoryCSV motifCSV = GenCraftMotif(BookPOMap, SourcePOMap, BookCSV);
        bookList.add(motifCSV);

        for(String bookCategory : BookNameMap.keySet()){
            CategoryCSV subCSV = new CategoryCSV();
            subCSV.setZanataFileName(bookCategory);
            subCSV.setType("book");
            subCSV.setLinkCount(BookCSV.getLinkCount());
            subCSV.setPoIndexList(BookCSV.getPoIndexList());
            for(String title : BookNameMap.get(bookCategory)){
                if(title.contains((" ("))) {
                    title = title.substring(0, title.indexOf(" ("));
                }
                PO po = SourcePOMap.get(title);
                if(po == null) {
                    title = title.replace("\"", "\"\"");
                    title = title.replace("No. ", "#");
                    title = title.replace(" — ", "—");
                    PO containPo = getContainPO(title, SourcePOMap);
                    if(containPo == null){
                        System.out.println("title ["+title+"]");
                        continue;
                    }else {
                        po = containPo;
                    }
                }


                ArrayList<String> indexList = getLinkedIndexList(BookCSV, po.getId());
                for(String index : indexList){
                    PO subPO = BookPOMap.get(index);
                    if(subPO != null){
                        BookPOMap.remove(index);
                        subCSV.putPoData(index, subPO);
                    }
                }
            }
            bookList.add(subCSV);
        }

        return bookList;
    }

    private PO getContainPO(String title, HashMap<String, PO> sourcePOMap) {
        PO containPO = null;

        for(String SourceTitle : sourcePOMap.keySet()){
            if(SourceTitle.contains(title)){
                containPO = sourcePOMap.get(SourceTitle);
                //System.out.println("Contina title ["+ SourceTitle+"]");
                break;
            }
        }

        return containPO;
    }

    private CategoryCSV GenCraftMotif(HashMap<String, PO> BookPOMap, HashMap<String, PO> SourcePOMap, CategoryCSV BookCSV){
        CategoryCSV motifCSV = new CategoryCSV();
        motifCSV.setZanataFileName("Craft Motifs");
        motifCSV.setType("book");
        motifCSV.setLinkCount(BookCSV.getLinkCount());
        motifCSV.setPoIndexList(BookCSV.getPoIndexList());
        ArrayList<String> title = new ArrayList<>();
        for(String name : SourcePOMap.keySet()){
            if(name.contains("Crafting Motif")){
                title.add(name);
            }
        }

        for(String oneTitle : title){
            PO titlePO = SourcePOMap.get(oneTitle);
            SourcePOMap.remove(oneTitle);
            ArrayList<String> indexList = getLinkedIndexList(BookCSV, titlePO.getId());
            for(String index : indexList) {
                PO bookPO = BookPOMap.get(index);
                if(bookPO != null) {
                    motifCSV.putPoData(bookPO.getId(), bookPO);
                    BookPOMap.remove(index);
                }
            }
        }

        return motifCSV;
    }


    private ArrayList<String> getLinkedIndexList(CategoryCSV CSV, String originFullIndex ){
        ArrayList<String> LinkedMainIndex = CSV.getPoIndexList();
        String originTailIndex = originFullIndex.substring(originFullIndex.indexOf("-"));
        ArrayList<String> LinkedFullIndex = new ArrayList<>();
        for(String MainIndex : LinkedMainIndex){
            LinkedFullIndex.add(MainIndex+originTailIndex);
        }
        return LinkedFullIndex;
    }



    private boolean GetItemCategory(ArrayList<CategoryCSV> ItemCSV){
        boolean wbCrawlRet;
        WebCrawler wc = new WebCrawler();
        try {
            if(!wc.GetUESPItemCategory(ItemCSV)){
                throw new Exception("ChampionSkill gen fail");
            }
            wbCrawlRet = true;
        }catch(Exception ex){
            wbCrawlRet = false;
        }
        return wbCrawlRet;
    }

}


