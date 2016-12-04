package script;

import net.fornwall.jelf.ElfFile;
import net.fornwall.jelf.ElfSectionHeader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by matio on 28.11.2016.
 */
public class Script {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println(" Nie dostałem pliku \n jesli chcesz uzyc skryptu dla konkretnego pliku wpisz :\n java PathMeSolver <file> startAddress replaceBytes...");
           // System.exit(0);
        }
        String fileName = args[0];
        String addresStrng = args[1];
        long address = Long.decode(addresStrng);
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(fileName,"r");
            System.out.println(randomAccessFile.toString());
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
            System.out.println(closestName);
            System.out.println(addresToWrite);
            RandomAccessFile raf = new RandomAccessFile(Script.class.getResource("").getPath() + "/patch_me_bin_orig", "rw");
            raf.seek(addresToWrite);
            System.out.println(raf.read());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
