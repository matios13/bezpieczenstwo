package script;

import com.sun.deploy.util.ArrayUtil;
import net.fornwall.jelf.ElfFile;
import net.fornwall.jelf.ElfSectionHeader;

import javax.sound.midi.SysexMessage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by matio on 28.11.2016.
 */
public class Script {

    static List<Byte> bytesInLittleEndian;
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println(" Nie dostałem pliku \n jesli chcesz uzyc skryptu dla konkretnego pliku wpisz :\n java PathMeSolver <file> startAddress replaceBytes...");
            System.exit(0);
        }
        String fileName = args[0];
        String addresStrng = args[1];
        long address = Long.decode(addresStrng);
        try {
            File file = new File(fileName);
            ElfFile elfFile = ElfFile.fromFile(file);
            long closest = 0;
            String closestName = "";
            long offset = 0;
            for (int i = 0; i < elfFile.num_sh; i++) {
                ElfSectionHeader sh = elfFile.getSectionHeader(i);
                if (sh.address <= address && sh.address > closest) {
                    closest = sh.address;
                    closestName = sh.getName();
                    offset = sh.section_offset;
                }
            }
            long addresToWrite = address - closest + offset;
            RandomAccessFile raf = new RandomAccessFile(Script.class.getResource("").getPath() + "/patch_me_bin_orig", "rw");
            raf.seek(addresToWrite);
            parameterToLittleEndian(args);
           raf.write( asArray(bytesInLittleEndian));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static byte[]asArray(List<Byte> bytes){
        byte[] result = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            result[i] = bytes.get(i);
        }
        return result;
    }
    private static void parameterToLittleEndian(String args[]){
        bytesInLittleEndian=new ArrayList<>();
        for (int i = 2; i < args.length; i++) {
            if (!args[i].startsWith("0x")) {
                System.err.println("Przyjmuje tylko liczby zapisane w formacie szesntastkowym 0x...");
                System.exit(0);
            }
            int lenght = args[i].length();
            if(lenght>10){
                System.err.println("Zbyt dlugi parametr");
                System.exit(0);
            }
            boolean addZero = false;
            if(lenght>6&&lenght<9)
                addZero=true;
            for(int j=lenght- 1; j > 1; j -= 2) {
                String substring = args[i].substring(j - 1, j + 1);
                substring = substring.replace("x","0");
                byte singleByte = (byte) Integer.parseInt(substring, 16);
                bytesInLittleEndian.add(singleByte);
            }
            if(addZero){
                bytesInLittleEndian.add(new Byte("48"));
                bytesInLittleEndian.add(new Byte("48"));
            }
        }
    }
}
