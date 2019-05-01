package org.metalscraps.eso.lang.lib.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.metalscraps.eso.lang.lib.bean.ID;
import org.metalscraps.eso.lang.lib.bean.PO;
import org.metalscraps.eso.lang.lib.bean.ToCSVConfig;
import org.metalscraps.eso.lang.lib.config.AppConfig;
import org.metalscraps.eso.lang.lib.config.AppWorkConfig;
import org.metalscraps.eso.lang.lib.config.SourceToMapConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

@SuppressWarnings({"WeakerAccess", "ResultOfMethodCallIgnored", "unused"})
public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    private static HashMap<String, String> versionMap = new HashMap<>();
    private static HashMap<String, ArrayList<String>> projectMap = new HashMap<>();
    public static String getLatestVersion(String projectName) {
        if(versionMap.containsKey(projectName)) return versionMap.get(projectName);
        HttpRequest request = getDefaultRestClient(AppConfig.ZANATA_DOMAIN+"rest/projects/p/"+projectName);

        JsonNode jsonNode = getBodyFromHTTPsRequest(request);
        String[] serverVer = null;
        for (JsonNode node : jsonNode.get("iterations")) {
            if(node.get("status").toString().equalsIgnoreCase("\"ACTIVE\"") ) { // && temp > version
                String[] tempVer = node.get("id").asText().split("\\.");
                if(serverVer == null) {
                    serverVer = tempVer;
                    continue;
                }
                for(int i=0; i<3; i++) {
                    if(Integer.parseInt(tempVer[i]) > Integer.parseInt(serverVer[i])) {
                        serverVer = tempVer;
                        break;
                    }
                }
            }
        }
        if(serverVer == null) serverVer = new String[] {"0.0.0"};
        versionMap.put(projectName, String.join(".", serverVer));
        return versionMap.get(projectName);
    }

    public static JsonNode getBodyFromHTTPsRequest(HttpRequest request){

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = null;
        try { response = client.send(request, HttpResponse.BodyHandlers.ofString()); }
        catch (IOException | InterruptedException e) { e.printStackTrace(); }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;

        try { jsonNode = objectMapper.readTree(Objects.requireNonNull(response).body()); }
        catch (IOException e) { e.printStackTrace(); }
        return jsonNode;
    }

    public static ArrayList<String> getFileNames(String projectName){
        ArrayList<String> fileNames = new ArrayList<>();
        var request = getDefaultRestClient(AppConfig.ZANATA_DOMAIN+"rest/projects/p/"+ projectName +"/iterations/i/" +Utils.getLatestVersion(projectName)+"/r");
        JsonNode jsonNode = getBodyFromHTTPsRequest(request);

        for (Iterator<JsonNode> it = jsonNode.elements(); it.hasNext(); ) {
            JsonNode node = it.next();
            String Trim = node.get("name").toString().replaceAll("^\"|\"$", "");
            fileNames.add(Trim);
        }

        return fileNames;
    }

    public static void downloadPOs(AppWorkConfig appWorkConfig){
        LocalTime timeTaken = LocalTime.now();
        downloadPO(appWorkConfig, "ESO-item");
        downloadPO(appWorkConfig, "ESO-skill");
        downloadPO(appWorkConfig, "ESO-system");
        downloadPO(appWorkConfig, "ESO-book");
        downloadPO(appWorkConfig, "ESO-story");
        logger.info("총 " + timeTaken.until(LocalTime.now(), ChronoUnit.SECONDS) + "초");
    }

    public static void downloadPO(AppWorkConfig appWorkConfig, String projectName) {

        //final String url = AppConfig.ZANATA_DOMAIN+"rest/file/translation/"+projectName+"/"+Utils.getLatestVersion(projectName)+"/ko/po?docId=";
        final String url = AppConfig.ZANATA_DOMAIN+"rest/file/translation/"+projectName+"/1.0/ko/po?docId=";
        final File PODirectory = new File(appWorkConfig.getBaseDirectory() + "/PO_" + appWorkConfig.getToday());
        appWorkConfig.setPODirectory(PODirectory);

        File fPO = null;
        try {

            ArrayList<String > fileNames = getFileNames(projectName);

            for (String fileName : fileNames) {

                // 우리가 사용하는 데이터 아님.
                if (fileName.equals("00_EsoUI_Client") || fileName.equals("00_EsoUI_Pregame")) continue;

                LocalTime ltStart = LocalTime.now();
                String fileURL = url+fileName;
                fileURL = fileURL.replace(" ", "%20");
                logger.trace("download zanata file  ["+fileName+"] to local ["+PODirectory.getAbsolutePath()+"/"+fileName+".po] ");

                fPO = new File(PODirectory.getAbsolutePath() + "/" + fileName + ".po");
                if (!fPO.exists() || fPO.length() <= 0) FileUtils.writeStringToFile(fPO, IOUtils.toString(new URL(fileURL), AppConfig.CHARSET), AppConfig.CHARSET);
                fPO = null;

                LocalTime ltEnd = LocalTime.now();
                logger.trace(" " + ltStart.until(ltEnd, ChronoUnit.SECONDS) + "초");
            }

        } catch (IOException e) {
            if(e.getMessage().contains("Premature EOF")) {
                logger.warn("EOF 재시도");
                if(fPO.exists()) fPO.delete();
                try { Thread.sleep(1800000); } catch (InterruptedException e1) {  e1.printStackTrace(); }
                Utils.downloadPO(appWorkConfig, projectName);
            }
            else e.printStackTrace();
        } catch (Exception e) { logger.error(e.getMessage()); e.printStackTrace(); }

    }

    public static long CRC32(File f) {
        Checksum crc = new CRC32();
        if(!f.exists() || f.length() <= 0) return -1;

        try(BufferedInputStream in = new BufferedInputStream(new FileInputStream(f))) {
            byte[] buffer = new byte[32768];
            int length;
            while ((length = in.read(buffer)) >= 0) crc.update(buffer, 0, length);
        } catch (IOException e) { e.printStackTrace(); }
        return crc.getValue();
    }


    public static void processRun(File baseDirectory, String command) throws IOException, InterruptedException { processRun(baseDirectory, command, ProcessBuilder.Redirect.INHERIT); }

    public static void processRun(File baseDirectory, String command, ProcessBuilder.Redirect redirect) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder()
                .directory(baseDirectory)
                .command((command).split("\\s+"))
                .redirectError(redirect)
                .redirectOutput(redirect);
        pb.start().waitFor();
    }

    public static void makeCSV(File file, ToCSVConfig toCSVConfig, ArrayList<PO> poList) {
        StringBuilder sb = new StringBuilder("\"Location\",\"Source\",\"Target\"\n");
        for (PO p : poList) {
            sb.append(p.toCSV(toCSVConfig));
        }
        try {
            FileUtils.writeStringToFile(file, sb.toString(), AppConfig.CHARSET);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void makeGoogleSpreadCSV(File file, ToCSVConfig toCSVConfig, ArrayList<PO> poList) {
        StringBuilder sb = new StringBuilder("\"Location\",\"Source\",\"Target\"\n");
        for (PO p : poList) {
            sb.append(p.toGoogleSpreadCSV(toCSVConfig));
        }
        try {
            FileUtils.writeStringToFile(file, sb.toString(), AppConfig.CHARSET);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void convertKO_PO_to_CN(AppWorkConfig appWorkConfig) {

        Collection<File> fileList = FileUtils.listFiles(appWorkConfig.getPODirectory(), new String[]{"po"}, false);

        try {
            for (File file : fileList) {
                File po2 = new File(file.getAbsolutePath() + "2");
                if(!po2.exists() || po2.length() <= 0) FileUtils.write(po2, Utils.KOToCN(FileUtils.readFileToString(file, AppConfig.CHARSET)), AppConfig.CHARSET);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void convertCNtoKO(AppWorkConfig appWorkConfig) {

        Collection<File> fileList = FileUtils.listFiles(appWorkConfig.getPODirectory(), new String[]{"txt"}, false);
        for(File file : fileList){
            System.out.println(file.getPath());
        }


        try {
            for (File file : fileList) {
                File po2 = new File(file.getAbsolutePath() + "2");
                if(!po2.exists() || po2.length() <= 0) FileUtils.write(po2, Utils.CNtoKO(FileUtils.readFileToString(file, AppConfig.CHARSET)), AppConfig.CHARSET);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String KOToCN(String string) {
        char[] c = string.toCharArray();
        for(int i=0; i < c.length; i++) if (c[i] >= 0xAC00 && c[i] <= 0xEA00) c[i] -= 0x3E00;
        return new String(c);
    }

    public static String CNtoKO(String string) {
        char[] c = string.toCharArray();
        for(int i=0; i < c.length; i++) if (c[i] >= 0x6E00 && c[i] <= 0xAC00) c[i] += 0x3E00;
        return new String(c);
    }


    public static void replaceStringFromMap(StringBuilder stringBuilder, Map<String, ?> map) {
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            String key = entry.getKey();
            Object rawValue = entry.getValue();
            String value = rawValue instanceof PO ? ((PO) rawValue).getTarget() : rawValue instanceof String ? (String) rawValue : key;

            int start = stringBuilder.indexOf(key, 0);
            while (start > -1) {
                int end = start + key.length();
                int nextSearchStart = start + value.length();
                stringBuilder.replace(start, end, value);
                start = stringBuilder.indexOf(key, nextSearchStart);
            }
        }
    }

    public static HashMap<String, PO> sourceToMap(SourceToMapConfig config) {

        HashMap<String, PO> poMap = new HashMap<>();
        String fileName = FilenameUtils.getBaseName(config.getFile().getName());
        String source = parseSourceToMap(config);

        Matcher m = config.getPattern().matcher(source);
        boolean isPOPattern = config.getPattern() == (AppConfig.POPattern);
        while (m.find()) {
            PO po = new PO(m.group(2), m.group(6), m.group(7), config.isFillEmptyTrg()).wrap(config.getPrefix(), config.getSuffix(), config.getPoWrapType());
            //po.setFileName(FileNames.fromString(fileName));
            po.setStringFileName(fileName);
            if(isPOPattern && m.group(1) != null && m.group(1).equals("#, fuzzy")) po.setFuzzy(true);
            poMap.put(m.group(config.getKeyGroup()), po);
        }
        return poMap;
    }

    public static ArrayList<String> CustomSourceToArray(File file) throws  Exception {
        System.out.println("target : "+file.getName());
        ArrayList<String> indexArr = new ArrayList<>();
        String source = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        //System.out.println("source : "+source);
        String id = null;

        //update1
        //Pattern CSVPattern = Pattern.compile("(.*)`(.*)`(.*)`(.*)`", Pattern.MULTILINE);
        //update5,6,7,8
        //Pattern CSVPattern = Pattern.compile("(.*), \"", Pattern.MULTILINE);
        // id = subid[0]+"-"+subid[1]+"-"+subid[2];
        //update9,10,11
        //"21337012","0","3331","6743691","
        //Pattern CSVPattern = Pattern.compile("\"(.*)\",\"(.*)\",\"(.*)\",\"(.*)\",\"", Pattern.MULTILINE);
        //update 12~ curr
        Pattern CSVPattern = Pattern.compile("\"()(([\\d]+?)-([\\d]+?)-([\\d]+?))\",\"([\\s\\S]*?)\",\"([\\s\\S]*?)\"\n", Pattern.MULTILINE);
        //Pattern CategoryConfig = Pattern.compile ("FileName:(.*)((\\r\\n)|(\\n))isDuplicate:(.*)((\\r\\n)|(\\n))type:(.*)((\\r\\n)|(\\n))indexLinkCount:(.*)((\\r\\n)|(\\n))index:(.*)((\\r\\n)|(\\n))", Pattern.MULTILINE );

        Matcher m = CSVPattern.matcher(source);
        int count = 0;
        int tenk =0;
        while (m.find()) {
            //System.out.println("group 1 ["+m.group(1)+"] group 2 ["+m.group(2)+"] group 3 ["+m.group(3)+"]");
            //System.out.println("group 1 ["+m.group(1)+"] ");
            id = m.group(1)+"-"+m.group(2)+"-"+m.group(3);
            count++;

            if(count == 10000){
                tenk++;
                System.out.println("tenk["+tenk+"]");
                count = 0;
            }

            indexArr.add(id);
        }
        System.out.println("parse end");

        return indexArr;
    }

    public static ArrayList<PO> sourceToArray(SourceToMapConfig config){
        ArrayList<PO> poArray = new ArrayList<>();
        String fileName = FilenameUtils.getBaseName(config.getFile().getName());
        String source = parseSourceToMap(config);
        Matcher m = config.getPattern().matcher(source);
        boolean isPOPattern = config.getPattern() == (AppConfig.POPattern);
        while (m.find()) {
            PO po = new PO(m.group(2), m.group(6), m.group(7)).wrap(config.getPrefix(), config.getSuffix(), config.getPoWrapType());
            //po.setFileName(FileNames.fromString(fileName));
            po.setStringFileName(fileName);
            if(isPOPattern && m.group(1) != null && m.group(1).equals("#, fuzzy")) po.setFuzzy(true);
            poArray.add(po);
        }
        return poArray;
    }

    private static String parseSourceToMap(SourceToMapConfig config) {

        String source = null;
        try {
            source = FileUtils.readFileToString(config.getFile(), AppConfig.CHARSET);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (config.isToLowerCase()) source = Objects.requireNonNull(source).toLowerCase();
        //System.out.println("origin ["+source+"]");

        if (config.isProcessText()) {
            if (config.isProcessItemName()) source = Objects.requireNonNull(source).replaceAll("\\^[\\w]+", ""); // 아이템 명 뒤의 기호 수정

            source = convertEscape(source);
        }
        //System.out.println("parsed ["+source+"]");
        return source;
    }

    public static String convertEscape(String origin){
        return origin.replaceAll("\\\\(?!n)", "\\\\\\\\") // tip.pot-41714900-0-307 기타 등등 \ 이스케이프 문자 \n 제외하고 전부 \\로 이중 이스케이프
                .replaceAll("\\\\n", "\"\n\"\\\\n")  // "Lorem Ipsum is\nsimply" => "Lorem Ipsum is" + \n + "\\\\\nsimply" -> \n 자나타에서 \n 파싱 안됨
                .replaceAll("\\\\\"", "\"")
                .replaceAll("\\\\\"", "\"");
    }

    private void processRun(String command, File directory) throws IOException, InterruptedException { processRun(command, ProcessBuilder.Redirect.INHERIT, directory); }

    private void processRun(String command, @SuppressWarnings("SameParameterValue") ProcessBuilder.Redirect redirect, File directory) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder()
                .directory(directory)
                .command((command).split("\\s+"))
                .redirectError(redirect)
                .redirectOutput(redirect);
        pb.start().waitFor();
    }

    public static Properties setConfig(Path appPath, Path configPath, Map<String, String> config) {

        logger.info("앱 설정 폴더 확인");
        if(!appPath.toFile().exists()) {
            logger.info("폴더 존재하지 않음 생성.");
            if(appPath.toFile().mkdirs()) logger.info(appPath.toString() + "생성 성공");
            else {
                logger.error("설정 폴더 생성 실패. 앱 종료");
                System.exit(0);
            }
        }

        logger.info("설정 파일 확인");

        if(!configPath.toFile().exists()) {
            logger.info("설정 존재하지 않음 생성.");
            try {
                if(configPath.toFile().createNewFile()) logger.info(appPath.toString() + "생성 성공");
                else {
                    logger.error("설정 생성 실패. 앱 종료");
                    System.exit(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("설정 생성 실패. 앱 종료");
                System.exit(0);
            }
        }

        Properties properties = new Properties();
        try {
            if (Files.exists(configPath) && Files.size(configPath) > 0) try(var fis = new FileInputStream(configPath.toFile())) { properties.load(fis); } catch (Exception e) { e.printStackTrace(); }
            else try(var fos = new FileOutputStream(configPath.toFile())) {
                logger.info("설정 데이터 없음. 초기화");
                for(var entry : config.entrySet()) properties.setProperty(entry.getKey(), entry.getValue());
                properties.store(fos, "init");
            } catch (Exception e) { e.printStackTrace(); }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }


    public static void storeConfig(Path configPath, Properties properties) {
        try(var fos = new FileOutputStream(configPath.toFile())) {
            properties.store(fos, String.valueOf(new Date().getTime()));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static HttpRequest getDefaultRestClient(String domain) {
        return HttpRequest.newBuilder().uri(URI.create(domain)).header("Accept","application/json").build();
    }

    public static ArrayList<String> getDocuments(String projectName) {
        var list = projectMap.get(projectName);
        if(list.size() == 0) list.addAll(getFileNames(projectName));
        return list;
    }

    public static HashMap<String, ArrayList<String>> getProjectMap() {
        if(projectMap.size() == 0) {
            logger.info("rest/projects");
            var request = getDefaultRestClient(AppConfig.ZANATA_DOMAIN+"rest/projects");

            JsonNode jsonNode = getBodyFromHTTPsRequest(request);

            for(var x : jsonNode) {
                var id = String.valueOf(x.get("id")).replace("\"","");
                if(id.startsWith("ESO-") && String.valueOf(x.get("status")).equals("\"ACTIVE\"")) projectMap.put(id, getFileNames(id));
            }
        }

        for(var x : projectMap.keySet()) getDocuments(x);
        return projectMap;
    }

    public static String getProjectNameByDocument(ID id) throws Exception {
        if(!id.isFileNameHead()) throw new ID.NotFileNameHead();
        for(var x : getProjectMap().entrySet())
            for(var y : x.getValue())
                if(y.equalsIgnoreCase(id.getHead())) {
                    id.setHead(y);
                    return x.getKey();
                }
        throw new Exception("프로젝트 못찾음 /" + id.toString());
    }

    public static ArrayList<PO> getMergedPO(Collection<File> fileList) {
        ArrayList<PO> sourceList = new ArrayList<>();

        for (File file : fileList) {
            String fileName = FilenameUtils.getBaseName(file.getName());
            // pregame 쪽 데이터
            if (fileName.equals("00_EsoUI_Client") || fileName.equals("00_EsoUI_Pregame")) continue;
            sourceList.addAll(Utils.sourceToMap(new SourceToMapConfig().setFile(file).setPattern(AppConfig.POPattern)).values());
            logger.trace(file.toString());
        }
        sourceList.sort(PO.comparator);
        return sourceList;
    }

    public static void makeCSVwithLog(File file, ToCSVConfig csvConfig, ArrayList<PO> sourceList) {
        LocalTime timeTaken = LocalTime.now();
        Utils.makeCSV(file, csvConfig, sourceList);
        logger.info(file.getName() + timeTaken.until(LocalTime.now(), ChronoUnit.SECONDS) + "초");
    }

    public static void makeGoogleSpreadCSVwithLog(File file, ToCSVConfig csvConfig, ArrayList<PO> sourceList) {
        LocalTime timeTaken = LocalTime.now();
        Utils.makeGoogleSpreadCSV(file, csvConfig, sourceList);
        logger.info(file.getName() + timeTaken.until(LocalTime.now(), ChronoUnit.SECONDS) + "초");
    }

}
