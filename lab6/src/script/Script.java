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
        String fileName = "/patch_me_bin_orig";
        String addresStrng = "0x80483f0";
        long address = Long.decode(addresStrng);
        try {
            ElfFile elfFile = ElfFile.fromFile(new File(Script.class.getResource("").getPath() + "/patch_me_bin_orig"));
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
