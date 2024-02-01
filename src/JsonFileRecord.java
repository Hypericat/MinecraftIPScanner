import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;


public class JsonFileRecord {
    IP ip;
    File dir;
    String versionName = "";
    int versionProtocol = -1;
    int playerMax = -1;
    int onlinePlayersCount = -1;
    List<Player> allPlayers = new ArrayList<>();
    List<Player> onlinePlayers = new ArrayList<>();
    String descText = "";
    String favicon = "";
    boolean enforcesSecureChat = false;
    boolean hasForgeData = false;
    boolean previewsChat = false;
    Gson gson;
    String ignorePrefix = "%";
    String usedFile = "used";
    String archivedFile = "archive";
    public JsonFileRecord(File path) {
        ip = new IP(path.getName());
        gson = new Gson();
        dir = path;
        File bestFile = null;
        Double bestScore = 0d;
        for (File file : Objects.requireNonNull(path.listFiles())) {
            if (file.getName().startsWith(ignorePrefix)) {
                handleIgnorePrefix(file);
                continue;
            }
            if (!file.getName().endsWith(".json")) {
                file.renameTo(new File(file.getAbsolutePath() + ".json"));
            }
            try {
                double score = Double.parseDouble(file.getName().replaceAll(".json", ""));
                if (score > bestScore) {
                    bestFile = file;
                    bestScore = score;
                }
            } catch (Exception ignore) {

            }
            HashMap<String, Object> json;
            try {
                json = readJson(file, gson);
            } catch (Exception e) {
                return;
            }
            setArchived(ip.getArchived());
            try {
                if (json.containsKey("players")) {
                    HashMap<String, Object> playersMap = gson.fromJson(json.get("players").toString(), new TypeToken<HashMap<String, Object>>() {
                    }.getType());
                    if (playersMap != null) {
                        if (playersMap.containsKey("sample")) {
                            List<HashMap<String, String>> sampleList = gson.fromJson(playersMap.get("sample").toString(), new TypeToken<List<HashMap<String, String>>>() {
                            }.getType());
                            ;
                            for (HashMap<String, String> map : sampleList) {
                                if (map.containsKey("name") && map.containsKey("id")) {
                                    //May contain dupes but is intended for now to count frequency
                                    allPlayers.add(new Player(map.get("name"), map.get("id")));
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {

            }
        }
        initCurrentData(Objects.requireNonNull(bestFile));
    }
    public int parseNum(String string) {
        try {
            return (int) Double.parseDouble(string);
        } catch (Exception e) {
            return -1;
        }
    }
    public void initCurrentData(File file) {
        HashMap<String, Object> json;
        try {
            json = readJson(file, gson);
        } catch (Exception e) {
            return;
        }
        if (json.containsKey("enforceSecureChat"))
            enforcesSecureChat = Boolean.parseBoolean(json.get("enforcesSecureChat").toString());
        if (json.containsKey("forgeData")) hasForgeData = true;
        if (json.containsKey("favicon"))
            favicon = (json.get("favicon").toString());
        if (json.containsKey("previewsChat"))
            previewsChat = Boolean.parseBoolean(json.get("previewsChat").toString());
        try {
            if (json.containsKey("players")) {
                HashMap<String, Object> playersMap = gson.fromJson(json.get("players").toString(), new TypeToken<HashMap<String, Object>>() {
                }.getType());
                if (playersMap != null) {
                    if (playersMap.containsKey("max")) {
                        playerMax = parseNum(playersMap.get("max").toString());
                    }
                    if (playersMap.containsKey("online")) {
                        onlinePlayersCount = parseNum(playersMap.get("online").toString());
                    }
                    if (playersMap.containsKey("sample")) {
                        List<HashMap<String, String>> sampleList = gson.fromJson(playersMap.get("sample").toString(), new TypeToken<List<HashMap<String, String>>>() {
                        }.getType());
                        ;
                        for (HashMap<String, String> map : sampleList) {
                            if (map.containsKey("name") && map.containsKey("id")) {
                                onlinePlayers.add(new Player(map.get("name"), map.get("id")));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {}
        try {
            if (json.containsKey("description")) {
                StringBuilder text = new StringBuilder(json.get("description").toString());
                text.insert(6, "\"");
                text.insert(text.length() - 1, "\"");
                try {
                    HashMap<String, Object> descMap = gson.fromJson(text.toString(), new TypeToken<HashMap<String, Object>>() {
                    }.getType());
                    if (descMap != null) {
                        if (descMap.containsKey("text")) {
                            descText = descMap.get("text").toString();
                        }
                    }
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {}
        try {
            if (json.containsKey("version")) {
                try {
                    StringBuilder text = new StringBuilder(json.get("version").toString());
                    text.insert(text.toString().startsWith("{protocol") ? 10 : 6, "\"");
                    text.insert(text.toString().startsWith("{protocol") ? (text.indexOf("name") - 2) : (text.indexOf("protocol") - 2), "\"");
                    HashMap<String, Object> versionMap = gson.fromJson(text.toString(), new TypeToken<HashMap<String, Object>>() {
                    }.getType());
                    if (versionMap != null) {
                        if (versionMap.containsKey("name")) {
                            versionName = versionMap.get("name").toString();
                        }
                        if (versionMap.containsKey("protocol")) {
                            versionProtocol = parseNum(versionMap.get("protocol").toString());
                        }
                    }
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {}
    }
    public HashMap<String, Object> readJson(File path, Gson gson) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
            Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
            return gson.fromJson(bufferedReader, type);
        }
    }
    public void handleIgnorePrefix(File file) {

    }
    public boolean isArchived() {
        if (!dir.exists()) return false;
        return (new File(dir.getAbsolutePath() + "\\" + archivedFile)).exists();
    }
    public void setArchived(boolean archive) {
        if (archive) setArchived();
        else removeArchived();
    }
    public void setArchived() {
        if (!dir.exists()) return;
        if (isArchived()) return;
        FileHandler.createAndTestFile(new File(dir.getAbsolutePath() + "\\" + archivedFile));
    }
    public void removeArchived() {
        if (!dir.exists()) return;
        File file = (new File(dir.getAbsolutePath() + "\\" + archivedFile));
        if (file.exists()) file.delete();
    }
    public boolean isUsed() {
        return (new File(dir.getAbsolutePath() + "\\" + usedFile)).exists();
    }
    public void setUsed(boolean used) {
        if (used) setUsed();
        else removeUsed();
    }
    public void setUsed() {
        if (isUsed()) return;
        FileHandler.createAndTestFile(new File(dir.getAbsolutePath() + "\\" + usedFile));
    }
    public void removeUsed() {
        File file = (new File(dir.getAbsolutePath() + "\\" + usedFile));
        if (file.exists()) file.delete();
    }
    public IP getIp() {
        return ip;
    }
    public String getVersionName() {
        return versionName;
    }
    public boolean getPreviewsChat() {
        return previewsChat;
    }

    public int getVersionProtocol() {
        return versionProtocol;
    }

    public int getPlayerMax() {
        return playerMax;
    }
    public int getOnlinePlayersCount() {
        return onlinePlayersCount;
    }
    public List<Player> getAllPlayers() {
        return allPlayers;
    }

    public List<Player> getOnlinePlayers() {
        return onlinePlayers;
    }

    public String getDescText() {
        return descText;
    }

    public String getFavicon() {
        return favicon;
    }

    public boolean isEnforcesSecureChat() {
        return enforcesSecureChat;
    }
}
