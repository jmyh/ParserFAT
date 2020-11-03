package run;

import fat.FAT16;
import slack.SlackSpace;
import slack.SlackSpaceSeacher;
import slack.SlackSpaceWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

//https://www.file-recovery.com/recovery-FAT-BOOT-sector-bios.htm
//http://www.maverick-os.dk/FileSystemFormats/FAT16_FileSystem.html#:~:text=The%20FAT16%20file%20system%20uses,volumes%20are%2004h%20and%2006h.

/**
 * Парсит образ файловой системы и выводит ее параметры в консоль
 * Проверено только на FAT 16
 */
public class ParserFAT {

    private static String DATA = "|This is       |" +
            "|not slack     |" +
            "|space, keep   |" +
            "|looking...    |" +
            "     /          " +
            "(\\__/)          " +
            "(='.'=)         " +
            "(\")_(\")       ";

    public static void main(String[] args) throws IOException {
        String fileName = "adams.dd";
        byte[] array = Files.readAllBytes(Paths.get(fileName));

        FAT16 fat16 = new FAT16(array);
        System.out.print(fat16);

        SlackSpaceSeacher seacher = new SlackSpaceSeacher(fat16);
        List<SlackSpace> slackSpaces = seacher.find();
        System.out.println(slackSpaces);

        SlackSpaceWriter writer = new SlackSpaceWriter(fat16, slackSpaces);
        writer.write(DATA, Paths.get("adams_writen.dd"));
    }
}