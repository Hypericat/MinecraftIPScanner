import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class IP implements Cloneable {
    private byte val1;
    private byte val2;
    private byte val3;
    private byte val4;
    private boolean shouldArchive = false;
    public IP() {

    }
    public IP(String ip) {
        this(ip.split("\\."));
    }
    public void setIp(String ip) {
        setIp(ip.split("\\."));
    }
    public void setIp(String[] ip) {
        setIp(ip[0], ip[1], ip[2], ip[3]);
    }
    public void setIp(String val1, String val2, String val3, String val4) {
        setIp((byte) (Short.parseShort(val1) + Byte.MIN_VALUE),(byte) (Short.parseShort(val2) + Byte.MIN_VALUE),(byte) (Short.parseShort(val3) + Byte.MIN_VALUE),(byte) (Short.parseShort(val4) + Byte.MIN_VALUE));
    }
    public void setIp(byte val1, byte val2, byte val3, byte val4) {
        this.val1 = val1;
        this.val2 = val2;
        this.val3 = val3;
        this.val4 = val4;
    }
    public IP(String[] ip) {
        this(ip[0], ip[1], ip[2], ip[3]);
    }
    public IP(String val1, String val2, String val3, String val4) {
        this((byte) (Short.parseShort(val1) + Byte.MIN_VALUE),(byte) (Short.parseShort(val2) + Byte.MIN_VALUE),(byte) (Short.parseShort(val3) + Byte.MIN_VALUE),(byte) (Short.parseShort(val4) + Byte.MIN_VALUE));
    }
    public IP increment() {
        val4 += 1;
        if (val4 == -128) {
            val3 += 1;
        } else {
            return this;
        }
        if (val3 == -128) {
            val2 += 1;
        } else {
            return this;
        }
        if (val2 == -128) {
            val1 += 1;
        }
        //Check to see if in ip range of 127.0.0.0 - 127.255.255.255 which are all for testing
        if (val1 == -1) val1 += 1;
        //Add 1 to the val1 since the first number should never be 0
        if (val1 == -128) val1 += 1;
        return this;
    }
    public IP(byte val1, byte val2, byte val3, byte val4) {
        this.val1 = val1;
        this.val2 = val2;
        this.val3 = val3;
        this.val4 = val4;
    }
    public String formatToString() {
        return (format(val1) + "." + format(val2) + "." + format(val3) + "." + format(val4));
    }
    public short getVal(int index) {
        switch (index) {
            case (0): return format(val1);
            case (1): return format(val2);
            case (2): return format(val3);
            case (3): return format(val4);
            default: return -1;
        }
    }
    public short format(byte val) {
        return (short) (val - Byte.MIN_VALUE);
    }
    public short getVal1() {
        return getVal(0);
    }
    public short getVal2() {
        return getVal(1);
    }
    public short getVal3() {
        return getVal(2);
    }
    public short getVal4() {
        return getVal(3);
    }

    @Override
    public IP clone() {
        return new IP(val1, val2, val3, val4);
    }
    public void ping(File logsPath, int timeOut, int port) {
        //Util.print("Pinging " + this.formatToString());
        byte[] handshake;
        try {
            handshake = getHandshake(port);
        } catch (IOException e) {
            Util.print("Failed to generate handshake for " + this.formatToString());
            return;
        }
        Optional<String> result;
        try {
            result = sendPacketHandshake(handshake, new Socket(), port, timeOut);
        } catch (IOException e) {
            //Util.print("Failed to send packet handshake for ip " + this.formatToString());
            this.shouldArchive = true;
            return;
        }
        if (result.isEmpty()) {
            Util.print("Handshake packet returned with error");
            this.shouldArchive = true;
            return;
        }
        String json = result.get();
        if (json.equals("Timeout")) {
            Util.print("IP address " + this.formatToString() + " timeout or closed port");
            this.shouldArchive = true;
            return;
        }
        File dir = FileHandler.createAndTestDir(new File(logsPath + "\\" + this.formatToString()));
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            try {
                if (file.isFile() && FileHandler.readFromFile(file).get(0).equals(json)) {
                    Util.print("Found open server but data was already recorded : " + this.formatToString());
                    file.renameTo(new File(file.getParent() + "\\" + System.currentTimeMillis()));
                    return;
                }
            } catch (IndexOutOfBoundsException e) {}
        }
        File jsonFile = FileHandler.createAndTestFile(new File(dir.getAbsoluteFile() + "\\" + System.currentTimeMillis() + ".json"));
        FileHandler.overWriteToFile(jsonFile, json);
        Util.print("Successfully saved data for ip " + this.formatToString());
    }
    public boolean getArchived() {
        return this.shouldArchive;
    }
    public Optional<String> sendPacketHandshake(byte[] buf, Socket socket, int port, int timeOut) throws IOException {
        InetSocketAddress address = new InetSocketAddress(this.formatToString(), port);
        try {
            socket.setSoTimeout(timeOut);
            socket.connect(address, timeOut);
        } catch (ConnectException e) {
            return "Timeout".describeConstable();
        }
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        DataInputStream input = new DataInputStream(socket.getInputStream());
        //C->S : Set Handshake state
        //Send Packet Length
        writeVarInt(output, buf.length);
        output.write(buf);

        //C->S
        output.writeByte(0x01); //Size of packet
        output.writeByte(0x00); //Packet ID

        //S->C
        int size = readVarInt(input);
        int packetId = readVarInt(input);

        if (packetId == -1) {
            Util.print("Invalid packet id of -1");
            return Optional.empty();
        }
        if (packetId != 0x00) {
            Util.print("Invalid response packet was given at " + this.formatToString());
            return Optional.empty();
        }
        int length = readVarInt(input);
        if (length < 1) {
            Util.print("Invalid json length of less than 1");
            return Optional.empty();
        }
        byte[] in = new byte[length];
        input.readFully(in); //read json
        socket.close();
        String json = new String(in);
        return Optional.of(json);
    }
    public byte[] getHandshake(int port) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream handshake =  new DataOutputStream(buffer);
        handshake.writeByte(0x00);
        writeVarInt(handshake, 765);
        writeString(handshake, this.formatToString(), Charset.defaultCharset());
        handshake.writeShort(port);
        writeVarInt(handshake, 1);
        return buffer.toByteArray();
    }
    public static void writeString(DataOutputStream out, String string, Charset charset) throws IOException {
        byte [] bytes = string.getBytes(charset);
        writeVarInt(out, bytes.length);
        out.write(bytes);
    }
    public static void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                out.writeByte(paramInt);
                return;
            }

            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }
    public static int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5) throw new RuntimeException("VarInt too big");
            if ((k & 0x80) != 128) break;
        }
        return i;
    }





}
