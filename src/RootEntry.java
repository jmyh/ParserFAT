import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;

public class RootEntry {

    private final static LocalDateTime START_TIME = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);

    private String fileName;
    private String fileExt;
    private StringBuilder fileAttributes=new StringBuilder();
    private LocalDateTime creationDateTime;
    private LocalDateTime lastAccessDate;
    private LocalDateTime lastWriteDateTime;
    private long firstCluster;
    private long fileSize;

    RootEntry(byte[] entryBytes) {
        int currentEntryPosition = 0;
        fileName = new String(Arrays.copyOfRange(entryBytes, currentEntryPosition, currentEntryPosition + 8));
        fileExt = new String(Arrays.copyOfRange(entryBytes, currentEntryPosition + 8, currentEntryPosition + 11));

        long attributeFlag = ParserHelper.byteArray2Int(Arrays.copyOfRange(entryBytes, currentEntryPosition + 11, currentEntryPosition + 12));
        if ((attributeFlag & 1) == 1) fileAttributes.append("read_only ");
        if ((attributeFlag & 2) == 2) fileAttributes.append("hidden ");
        if ((attributeFlag & 4) == 4) fileAttributes.append("system ");
        if ((attributeFlag & 8) == 8) fileAttributes.append("volume_name ");
        if ((attributeFlag & 16) == 16) fileAttributes.append("dir ");
        if ((attributeFlag & 32) == 32) fileAttributes.append("archive_flag ");

        long milliSeconds = ParserHelper.byteArray2Int(Arrays.copyOfRange(entryBytes, currentEntryPosition + 13, currentEntryPosition + 14)) * 10;
        byte[] byteTime = Arrays.copyOfRange(entryBytes, currentEntryPosition + 14, currentEntryPosition + 16);
        byte[] byteDate = Arrays.copyOfRange(entryBytes, currentEntryPosition + 16, currentEntryPosition + 18);
        creationDateTime = ParserHelper.byteArray2DateTime(byteTime,byteDate, (int) milliSeconds);

        byteDate = Arrays.copyOfRange(entryBytes, currentEntryPosition + 18, currentEntryPosition + 20);
        lastAccessDate = ParserHelper.byteArray2DateTime(null,byteDate, 0);

        byteTime = Arrays.copyOfRange(entryBytes, currentEntryPosition + 22, currentEntryPosition + 24);
        byteDate = Arrays.copyOfRange(entryBytes, currentEntryPosition + 24, currentEntryPosition + 26);
        lastWriteDateTime = ParserHelper.byteArray2DateTime(byteTime,byteDate, 0);

        firstCluster = ParserHelper.byteArray2Int(Arrays.copyOfRange(entryBytes, currentEntryPosition + 26, currentEntryPosition + 28));
        fileSize = ParserHelper.byteArray2Int(Arrays.copyOfRange(entryBytes, currentEntryPosition + 28, currentEntryPosition + 32));
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileExt() {
        return fileExt;
    }

    public StringBuilder getFileAttributes() {
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

    public long getFirstCluster() {
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
                "\tLast write timestamp: " + firstCluster + "\n" +
                "\tFile size: " + fileSize/1024+" Kb";
    }
}
