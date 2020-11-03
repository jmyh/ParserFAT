package fat;

import service.ParserHelper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DirEntry {

    public final static LocalDateTime START_TIME = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
    public final static String READ_ONLY="read_only";
    public final static String HIDDEN="hidden";
    public final static String SYSTEM="system";
    public final static String VOLUME_NAME="volume_name";
    public final static String DIR="dir";
    public final static String ARCHIVE="archive";

    private String fileName;
    private String fileExt;
    private List<String> fileAttributes=new ArrayList<>();
    private LocalDateTime creationDateTime;
    private LocalDateTime lastAccessDate;
    private LocalDateTime lastWriteDateTime;
    private int firstCluster;
    private long fileSize;

    public DirEntry(byte[] entryBytes) {
        int currentEntryPosition = 0;
        fileName = new String(Arrays.copyOfRange(entryBytes, currentEntryPosition, currentEntryPosition + 8));
        fileExt = new String(Arrays.copyOfRange(entryBytes, currentEntryPosition + 8, currentEntryPosition + 11));

        long attributeFlag = ParserHelper.byteArray2Int(Arrays.copyOfRange(entryBytes, currentEntryPosition + 11, currentEntryPosition + 12));
        if ((attributeFlag & 1) == 1) fileAttributes.add(READ_ONLY);
        if ((attributeFlag & 2) == 2) fileAttributes.add(HIDDEN);
        if ((attributeFlag & 4) == 4) fileAttributes.add(SYSTEM);
        if ((attributeFlag & 8) == 8) fileAttributes.add(VOLUME_NAME);
        if ((attributeFlag & 16) == 16) fileAttributes.add(DIR);
        if ((attributeFlag & 32) == 32) fileAttributes.add(ARCHIVE);

        long milliSeconds = ParserHelper.byteArray2Int(Arrays.copyOfRange(entryBytes, currentEntryPosition + 13, currentEntryPosition + 14)) * 10;
        byte[] byteTime = Arrays.copyOfRange(entryBytes, currentEntryPosition + 14, currentEntryPosition + 16);
        byte[] byteDate = Arrays.copyOfRange(entryBytes, currentEntryPosition + 16, currentEntryPosition + 18);
        creationDateTime = ParserHelper.byteArray2DateTime(byteTime,byteDate, (int) milliSeconds);

        byteDate = Arrays.copyOfRange(entryBytes, currentEntryPosition + 18, currentEntryPosition + 20);
        lastAccessDate = ParserHelper.byteArray2DateTime(null,byteDate, 0);

        byteTime = Arrays.copyOfRange(entryBytes, currentEntryPosition + 22, currentEntryPosition + 24);
        byteDate = Arrays.copyOfRange(entryBytes, currentEntryPosition + 24, currentEntryPosition + 26);
        lastWriteDateTime = ParserHelper.byteArray2DateTime(byteTime,byteDate, 0);

        firstCluster = (int) ParserHelper.byteArray2Int(Arrays.copyOfRange(entryBytes, currentEntryPosition + 26, currentEntryPosition + 28));
        fileSize = ParserHelper.byteArray2Int(Arrays.copyOfRange(entryBytes, currentEntryPosition + 28, currentEntryPosition + 32));
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileExt() {
        return fileExt;
    }

    public List<String> getFileAttributes() {
        return fileAttributes;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public LocalDateTime getLastAccessDate() {
        return lastAccessDate;
    }

    public LocalDateTime getLastWriteDateTime() {
        return lastWriteDateTime;
    }

    public int getFirstCluster() {
        return firstCluster;
    }

    public long getFileSize() {
        return fileSize;
    }

    @Override
    public String toString() {
        return "File name: " + fileName + "\n" +
                "\tFile extension: '" + fileExt + '\'' + "\n" +
                "\tFile attribute: " + fileAttributes + "\n" +
                "\tCreation date/time: " + creationDateTime + "\n" +
                "\tLast access date: " + lastAccessDate + "\n" +
                "\tLast write timestamp: " + lastWriteDateTime + "\n" +
                "\tFirst cluster: " + firstCluster + "\n" +
                "\tFile size: " + fileSize/1024+"\n\n";
    }
}
