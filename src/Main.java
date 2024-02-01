import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    private static final String defaultIP1 = "70.55.172.10";
    private static String defaultIP = "20.0.0.0";
    private static final String defaultIP2 = "13.105.183.64";
    protected static final int port = 25565;
    protected static File logsPath;
    protected static int timeOut = 300;
    private static int threadCount = 1000;
    private static int lastVal;
    private static Window window;
    private static final IP ipAt = new IP();
    static List<JsonFileRecord> fileRecords = new ArrayList<>();
    static List<JComponent> ipLabels = new ArrayList<>();
    static List<IP> toUpdate;
    static List<Thread> toExecute = new ArrayList<>();

    public static void main(String[] args) {
        String path = System.getProperty("user.dir");
        logsPath = FileHandler.createAndTestDir(new File(path + "\\logs"));
        Util.print("Enter anything for scan mode. Press enter for gui");
        String input = Util.getInput();
        if (!input.isEmpty()) {
            runSearchMode(null);
            return;
        }
        runGui(logsPath);
    }
    public static void upDateRecords() {
        for (File file : Objects.requireNonNull(logsPath.listFiles())) {
            try {
                fileRecords.add(new JsonFileRecord(file));
            } catch (NullPointerException ignore) {}
        }
    }
    public static void runGui(File logsPath) {
        upDateRecords();
        Util.print("Starting GUI");
        //for (int i = 0; i < 10; i ++) {
        //    JsonFileRecord file = fileRecords.get(Util.getRandomNumber(0, fileRecords.size() - 1));
        //    Util.print(file.getDescText());
        //    Util.print(file.getFavicon());
        //    Util.print(file.getVersionName());
        //    Util.print(file.getVersionProtocol());
        //    Util.print(file.getIp().formatToString());
        //    Util.print(file.getAllPlayers().toString());
        //}


        window = new Window("MC Json Searcher");
        //Setup filter version filter checkboxes
        int counterY = 1;
        int spacing = 30;
        int x = 10;
        AtomicBoolean all = new AtomicBoolean(false);
        window.addCheckBox("All Servers Types", new Bound(x, (counterY++) * spacing, 200, 30), all);

        AtomicBoolean paper = new AtomicBoolean(true);
        window.addCheckBox("Paper Servers", new Bound(x, (counterY++) * spacing, 200, 30), paper);

        AtomicBoolean vanilla = new AtomicBoolean(true);
        window.addCheckBox("Vanilla Servers", new Bound(x, (counterY++) * spacing, 200, 30), vanilla);

        AtomicBoolean spiggot = new AtomicBoolean(true);
        window.addCheckBox("Spiggot Servers", new Bound(x, (counterY++) * spacing, 200, 30), spiggot);

        AtomicBoolean forge = new AtomicBoolean(false);
        window.addCheckBox("Forge Servers", new Bound(x, (counterY++) * spacing, 200, 30), forge);

        AtomicBoolean fabric = new AtomicBoolean(false);
        window.addCheckBox("Fabric Servers", new Bound(x, (counterY++) * spacing, 200, 30), fabric);

        AtomicBoolean velocity = new AtomicBoolean(false);
        window.addCheckBox("Velocity Servers", new Bound(x, (counterY++) * spacing, 200, 30), velocity);

        AtomicBoolean hasMotd = new AtomicBoolean(false);
        window.addCheckBox("Has MOTD", new Bound(x, (counterY++) * spacing, 200, 30), hasMotd);

        AtomicBoolean randomize = new AtomicBoolean(true);
        window.addCheckBox("Randomize Order", new Bound(x, (counterY++) * spacing, 200, 30), randomize);

        AtomicBoolean used = new AtomicBoolean(false);
        window.addCheckBox("Show Visited", new Bound(x, (counterY++) * spacing, 200, 30), used);

        AtomicBoolean archived = new AtomicBoolean(false);
        window.addCheckBox("Show Archived", new Bound(x, (counterY++) * spacing, 200, 30), archived);

        window.addLabel("Enter Total Player Min", new Bound(x, counterY++ * spacing, 200, 30));
        JTextField totalPlayerMin = window.addTextBox("", new Bound(x, counterY ++ * spacing, 120, 30));

        window.addLabel("Enter Total Player Max", new Bound(x, counterY++ * spacing, 200, 30));
        JTextField totalPlayerMax = window.addTextBox(new Bound(x, counterY ++ * spacing, 120, 30));

        window.addLabel("Enter Online Player Min", new Bound(x, counterY++ * spacing, 200, 30));
        JTextField onlinePlayerMin = window.addTextBox("", new Bound(x, counterY ++ * spacing, 120, 30));

        window.addLabel("Enter Online Player Max", new Bound(x, counterY++ * spacing, 200, 30));
        JTextField onlinePlayerMax = window.addTextBox(new Bound(x, counterY ++ * spacing, 120, 30));

        window.addLabel("Enter Version", new Bound(x, counterY++ * spacing, 200, 30));
        JTextField versionNumber = window.addTextBox(new Bound(x, counterY ++ * spacing, 120, 30));

        window.addLabel("Max Results", new Bound(x, counterY++ * spacing, 200, 30));
        JTextField maxResults = window.addTextBox(new Bound(x, counterY ++ * spacing, 120, 30));

        window.addButton("Search!", new Bound(x, (int) ((counterY += 2) * spacing / 3 * 2.75f + 10), 80, 30), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<JsonFileRecord> list = new ArrayList<>(fileRecords);
                List<JsonFileRecord> searchRecords = search(list, all, paper, vanilla, spiggot, forge, fabric, velocity, hasMotd, randomize, used, archived, totalPlayerMin, totalPlayerMax, onlinePlayerMin, onlinePlayerMax, versionNumber, maxResults);
                window.setVisible(false);
                for (JComponent jLabel : ipLabels) {
                    window.remove(jLabel);
                }
                ipLabels.clear();
                int x = 420;
                int y = 1;
                int spacing = 30;
                Util.print("Found " + searchRecords.size() + " compatible servers!");
                /*
                JPanel panel = new JPanel();
                panel.setBounds(x - 45, y, 900, 1000);
                JScrollPane scrollFrame = new JScrollPane(panel);
                panel.setAutoscrolls(true);
                panel.setAlignmentX(x);
                scrollFrame.setPreferredSize(new Dimension( 800,300));
                window.setStyling(panel);
                ipLabels.add(scrollFrame);
                 */
                for (JsonFileRecord record : searchRecords) {
                    JLabel label = window.addLabel("|IP : " + record.getIp().formatToString() + " |Version : " + record.getVersionName() + " |Player Count : " + record.getOnlinePlayersCount() + " |Desc : " + record.getDescText(), new Bound(x, y * spacing, 700, 30));
                    ipLabels.add((window.addButton("Copy", new Bound(x - 77, y * spacing + 5, 75, 20), new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(record.getIp().formatToString()), null);
                            window.setVisible(false);
                            resetCopy();
                            label.setForeground(Color.MAGENTA);
                            window.setVisible(true);
                        }
                    })));
                    ipLabels.add((window.addButton("Use", new Bound(x - 147, y * spacing + 5, 65, 20), new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            record.setUsed();
                        }
                    })));
                    ipLabels.add((window.addButton("Un", new Bound(x - 210, y * spacing + 5, 60, 20), new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            record.removeUsed();
                        }
                    })));
                    y++;
                    ipLabels.add(label);
                }
                //ipLabels.add(panel);
                //window.add(panel);
                //window.add(scrollFrame);
                window.setVisible(true);
            }
        });
        window.addButton("Update", new Bound(x, (int) ((counterY += 2) * spacing / 3 * 2.75f), 80, 30), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCurrentServers();
                upDateRecords();
            }
        });
        window.addButton("Copy To Clipboard", new Bound(x, (int) ((counterY += 2) * spacing / 3 * 2.75f), 140, 30), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringBuilder builder = new StringBuilder();
                for (JsonFileRecord record : fileRecords) {
                    builder.append(record.getIp().formatToString()).append("&");
                }
                if (String.valueOf(builder.charAt(builder.length() - 1)).equals("&")) {
                    builder.deleteCharAt(builder.length() - 1);
                }
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(builder.toString()), null);
            }
        });
        window.init();
    }
    public static void resetCopy() {
        for (JComponent component : ipLabels) {
            if (!(component instanceof JLabel)) continue;
            if (component.getForeground().getRGB() == Color.MAGENTA.getRGB()) component.setForeground(Color.WHITE);
        }
    }
    public static JComponent addToPanel(JComponent c, JPanel panel) {
        panel.add(c);
        return c;
    }
    public static void updateCurrentServers() {
        Util.print("Initiating search for found IPs");
        List<IP> toUpdate = new ArrayList<>();
        for (File file : Objects.requireNonNull(logsPath.listFiles())) {
            toUpdate.add(new IP(file.getName()));
        }
        System.out.println("Updating " + toUpdate.size() + " Servers");
        runSearchMode(toUpdate);
        Util.print("Finished reScanning all IPs");
    }
    public static List<JsonFileRecord> search(List<JsonFileRecord> fileRecords, AtomicBoolean all, AtomicBoolean paper, AtomicBoolean vanilla, AtomicBoolean spiggot, AtomicBoolean forge, AtomicBoolean fabric, AtomicBoolean velocity, AtomicBoolean hasMotd, AtomicBoolean randomize, AtomicBoolean used, AtomicBoolean archived, JTextField totalPlayerMin, JTextField totalPlayerMax, JTextField onlinePlayerMin, JTextField onlinePlayerMax, JTextField versionNumber, JTextField maxResults) {
        fileRecords = versionPass(fileRecords, all, paper, vanilla, spiggot, forge, fabric, velocity);
        fileRecords = versionNumberPass(fileRecords, versionNumber);
        if (randomize.get()) {
            Collections.shuffle(fileRecords);
        }
        List<JsonFileRecord> validRecords = new ArrayList<>();
        int maxResult = -1;
        if (!maxResults.getText().isEmpty())
            maxResult = (int) Double.parseDouble(maxResults.getText());
        for (JsonFileRecord record : fileRecords) {
            if (maxResult == 0) break;
            if (record.getDescText().isEmpty() && hasMotd.get()) continue;
            if (!onlinePlayerMin.getText().isEmpty() && record.getOnlinePlayersCount() < Double.parseDouble(onlinePlayerMin.getText())) continue;
            if (!totalPlayerMin.getText().isEmpty() && record.allPlayers.size() < Double.parseDouble(onlinePlayerMin.getText())) continue;
            if (!onlinePlayerMax.getText().isEmpty() && record.getOnlinePlayersCount() > Double.parseDouble(onlinePlayerMax.getText())) continue;
            if (!totalPlayerMax.getText().isEmpty() && record.allPlayers.size() < Double.parseDouble(totalPlayerMax.getText())) continue;
            if (record.isUsed() && used.get()) continue;
            if (record.isArchived() && archived.get()) continue;
            maxResult --;
            addToList(validRecords, record);
        }
        return validRecords;
    }
    public static boolean isValidFormat(String input) {
        if (!input.startsWith("1.")) return false;
        input = input.substring(2);
        if (!startsWithList(input, "1234567890")) return false;
        if (!startsWithList(input, "1234567890.")) return false;
        if (!startsWithList(input, "1234567890.")) return false;
        return true;
    }
    public static boolean startsWithList(String str1, String str2) {
        for (char cha : str2.toCharArray()) {
            if (str1.startsWith(String.valueOf(cha))) return true;
        }
        return false;
    }
    //1.8.9 1.8.x
    //1.20.4 1.20.x
    //1.5.6
    public static boolean compareVersions(String string1, String string2) {
        if (string1.equals(string2)) return true;
        if (string1.length() != string2.length()) return false;
        String sun = string1.substring(0, string1.length() - 2);
        if (!string2.startsWith(sun)) return false;
        if (!matchesChar(string1.charAt(string1.length() - 1), string2.charAt(string2.length() - 1))) return false;
        return true;
    }
    /*
            StringBuilder builder1 = new StringBuilder(string1);
        StringBuilder builder2 = new StringBuilder(string2);
        try {
            for (int i = 0; i < Math.min(string1.length(), string2.length()); i++) {
                if (!matchesChar(builder1.charAt(i), builder2.charAt(i))) return false;
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
     */
    public static boolean matchesChar(char str1, char str2) {
        if (String.valueOf(str1).equals(String.valueOf(str2))) return true;
        if (String.valueOf(str1).equals("x")) return true;
        return false;
    }

    public static void addToList(List<JsonFileRecord> list, JsonFileRecord object) {
        if (!list.contains(object)) list.add(object);
    }
    public static List<JsonFileRecord> versionNumberPass(List<JsonFileRecord> records, JTextField versionNumber) {
        if (versionNumber.getText().isEmpty()) return records;
        List<JsonFileRecord> validRecords = new ArrayList<>();
        outer : for (JsonFileRecord record : records) {
            String version = versionNumber.getText();
            if (record.getVersionName().equals(version)) addToList(validRecords, record);
            String[] versionArray = record.getVersionName().split(" ");
            for (String string : versionArray) {
                if (isValidFormat(string)) {
                    if (compareVersions(string, version)) {
                        addToList(validRecords, record);
                        continue outer;
                    }
                }
            }
        }
        return validRecords;
    }
    public static List<JsonFileRecord> versionPass(List<JsonFileRecord> fileRecords, AtomicBoolean all, AtomicBoolean paper, AtomicBoolean vanilla, AtomicBoolean spiggot, AtomicBoolean forge, AtomicBoolean fabric, AtomicBoolean velocity) {
        if (all.get()) return fileRecords;
        List<JsonFileRecord> validRecords = new ArrayList<>();
        for (JsonFileRecord record : fileRecords) {
            String version = record.getVersionName().toLowerCase();
            if (version.contains("fabric") && fabric.get())  {
                addToList(validRecords, record);
                continue;
            }
            if (version.contains("spiggot") && spiggot.get()) {
                addToList(validRecords, record);
                continue;
            }
            if (version.contains("paper") && paper.get()) {
                addToList(validRecords, record);
                continue;
            }
            if (version.contains("velocity") && velocity.get()) {
                addToList(validRecords, record);
                continue;
            }
            if (record.hasForgeData && forge.get()) {
                addToList(validRecords, record);
                continue;
            }
            if (vanilla.get() && !version.contains("velocity") && !version.contains("paper")) {
                boolean vanillaA = version.startsWith("1.");
                if (vanillaA) {
                    for (char cha : version.toCharArray()) {
                        if (!"1234567890. -x".contains(String.valueOf(cha))) {
                            vanillaA = false;
                            break;

                        }
                    }
                }
                if (vanillaA) {
                    addToList(validRecords, record);
                    continue;
                }
            }
        }
        return validRecords;
    }
    public static <E extends Throwable> void sneakyThrow(Throwable e) throws E {
        throw (E) e;
    }
    public static void runSearchMode(List<IP> toResearch) {
        if (toResearch == null) {
            Util.print("Syntax = arg1 : threadCount, arg2 : defaultIP, arg3 : timeout");
            threadCount = Integer.parseInt(Util.getInput("Enter thread count"));
        } else threadCount = 5000;
        if (toResearch == null) {
            defaultIP = Util.getInput("Enter default IP");
            try {
                timeOut = Integer.parseInt(Util.getInput("Enter timeout"));
            } catch (NumberFormatException ignore) {
            }
            Util.print("Attempting to load last scanned IP");
        }
        if (toResearch == null || toResearch.isEmpty()) {
            Util.print("Did not find last scanned IP defaulting to " + defaultIP);
            ipAt.setIp(defaultIP);
        } else {
            toUpdate = toResearch;
            Util.print("Scanning " + toUpdate.size() + " IPs");
        }


        Util.print("Starting Ping Initialization");
        lastVal = ipAt.getVal3() - 1;
        Util.print("Creating " + threadCount + " thread(s)");
        toExecute.clear();
        for (int i = 0; i < threadCount; i++) {
            toExecute.add(new Thread(new RunnablePing()));
        }
        for (Thread thread : toExecute) {
            thread.start();
        }
        while (!toUpdate.isEmpty()) {
            try {
                Thread.sleep(100);
            } catch (Exception ignore) {}
        }
        try {
            Thread.sleep(1000);
        } catch (Exception ignore) {}
        for (Thread thread : toExecute) {
            thread.stop();
        }
        toExecute.clear();
        toUpdate = null;
    }
    public static boolean isBiggerIP(String ip1, String ip2) {
        String[] ip = ip1.split(".");
        String[] ip3 = ip2.split(".");
        if (ip.length != 4) return false;
        if (ip3.length != 4) return false;
        int index = 0;
        for (String str : ip) {
            int i1 = Integer.parseInt(str);
            int i2 = Integer.parseInt(ip3[index]);
            if (i1 > i2) return true;
            if (i1 < i2) return false;
            index ++;
        }
        return false;
    }
    static synchronized IP getNextIP() {
        if (toUpdate == null) {
            ipAt.increment();
            if (ipAt.getVal3() > lastVal) {
                lastVal = ipAt.getVal3();
                System.out.println("Testing IP " + ipAt.formatToString());
                if (lastVal == 255) lastVal = 0;
            }
            return ipAt.clone();
        } else {
            if (!toUpdate.isEmpty()) {
                IP ip = toUpdate.get(0);
                toUpdate.remove(0);
                Util.print("Gave IP " + ip.formatToString() + "  IPs Left : " + toUpdate.size());
                return ip.clone();
            }
            return null;
        }
    }
}