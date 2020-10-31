import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

//https://www.file-recovery.com/recovery-FAT-BOOT-sector-bios.htm
//http://www.maverick-os.dk/FileSystemFormats/FAT16_FileSystem.html#:~:text=The%20FAT16%20file%20system%20uses,volumes%20are%2004h%20and%2006h.

/**
 * Парсит образ файловой системы и выводит ее параметры в консоль
 * Проверено только на FAT 16
 */
public class ParserFAT {

    private final static LocalDateTime START_TIME = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);

    public static void main(String[] args) throws IOException {
        String fileName = "adams.dd";
        byte[] array = Files.readAllBytes(Paths.get(fileName));

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~File system information~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

        String oemName = new String(Arrays.copyOfRange(array, 3, 11));
        System.out.println("03\t8\tOEM Name: " + oemName);

        long sectorSize = byteArray2Int(Arrays.copyOfRange(array, 11, 13));
        System.out.println("0B\t2\tSector size: " + sectorSize);

        long numClustersIntoSector = byteArray2Int(Arrays.copyOfRange(array, 13, 14));
        System.out.println("0D\t1\tCluster size: " + numClustersIntoSector * sectorSize);

        long numReservedSectors = byteArray2Int(Arrays.copyOfRange(array, 14, 16));
        System.out.println("0E\t2\tNumber of reserved sectors: " + numReservedSectors);

        long numAllocateTables = byteArray2Int(Arrays.copyOfRange(array, 16, 17));
        System.out.println("10\t1\tNumber of file allocation tables (FATs): " + numAllocateTables);

        long maxNumRootEntries = byteArray2Int(Arrays.copyOfRange(array, 17, 19));
        System.out.println("11\t2\tThe total number of file name entries: " + maxNumRootEntries);

        long numSmallSectors = byteArray2Int(Arrays.copyOfRange(array, 19, 21));
        long numLargeSectors = byteArray2Int(Arrays.copyOfRange(array, 32, 36));
        long numSectors = ((numSmallSectors == 0) ? numLargeSectors : numSmallSectors);
        System.out.println("\t\tNumber of sectors: " + numSectors);

        String mediaType = DatatypeConverter.printHexBinary(Arrays.copyOfRange(array, 21, 22));
        System.out.println("15\t1\tMedia type: " + mediaType);

        long numSectorsPerFAT = byteArray2Int(Arrays.copyOfRange(array, 22, 24));
        System.out.println("16\t2\tSectors per FAT: " + numSectorsPerFAT);

        long numSectorsPerTrack = byteArray2Int(Arrays.copyOfRange(array, 24, 26));
        System.out.println("18\t2\tSectors per Track: " + numSectorsPerTrack);

        long numOfHeads = byteArray2Int(Arrays.copyOfRange(array, 26, 28));
        System.out.println("1A\t2\tNumber of Heads: " + numOfHeads);

        int numOfHiddenSectors = (int) byteArray2Int(Arrays.copyOfRange(array, 28, 32));
        System.out.println("1C\t4\tNumber of Hidden sectors: " + numOfHiddenSectors);

        String physicalDiskNum = DatatypeConverter.printHexBinary(Arrays.copyOfRange(array, 36, 37));
        System.out.println("24\t1\tPhysical Disk Number: " + physicalDiskNum);

        String reservedByte = DatatypeConverter.printHexBinary(Arrays.copyOfRange(array, 37, 38));
        System.out.println("25\t2\tReserved byte: " + reservedByte);

        String extenBootSign = DatatypeConverter.printHexBinary(Arrays.copyOfRange(array, 38, 39));
        System.out.println("26\t1\tExtended Boot Signature: " + extenBootSign);

        byte[] volumeIDArr = Arrays.copyOfRange(array, 39, 43);
        reverseArray(volumeIDArr);
        String volumeID = DatatypeConverter.printHexBinary(volumeIDArr);
        System.out.println("27\t4\tVolume Serial Number: " + volumeID);

        String volumeLabel = new String(Arrays.copyOfRange(array, 43, 54));
        System.out.println("2B\t11\tVolume Label: " + volumeLabel);

        String fileSystemType = new String(Arrays.copyOfRange(array, 54, 62));
        System.out.println("36\t8\tFile System Type : " + fileSystemType);

        System.out.println("\nFile system layout in sectors:");
        System.out.println("Reserved: " + numOfHiddenSectors + " → " + (numReservedSectors - 1));
        System.out.println("\tBoot sector: " + numOfHiddenSectors);
        int currentPosition = numOfHiddenSectors + 1;
        for (int i = 0; i < numAllocateTables; i++) {
            System.out.println("FAT " + i + ": " + currentPosition + " → " + (currentPosition + numSectorsPerFAT - 1));
            currentPosition += numSectorsPerFAT;
        }
        System.out.println("Data area: " + currentPosition + " → " + numSectors);
        int rootDirPosition = currentPosition;
//        int rootDirSize=maxNumRootEntries*32; //в байтах
        System.out.println("\tRoot directory: " + currentPosition + " → " + (currentPosition + maxNumRootEntries * 32 / sectorSize - 1));
        currentPosition += maxNumRootEntries * 32 / sectorSize;
        System.out.println("\tCluster area: " + currentPosition + " → " + numSectors);

        System.out.println("\n\nRoot directory structure:");
        int currentEntryPosition = rootDirPosition * 512;
        for (int i = 0; i < maxNumRootEntries; i++) {
            if (array[(currentEntryPosition)] == 0) break;

            fileName = new String(Arrays.copyOfRange(array, currentEntryPosition, currentEntryPosition + 8));
            System.out.println((i + 1) + ") File name: " + fileName);

            String fileExt = new String(Arrays.copyOfRange(array, currentEntryPosition + 8, currentEntryPosition + 11));
            System.out.println("\tFile extension: '" + fileExt + "'");

            long attributeFlag = byteArray2Int(Arrays.copyOfRange(array, currentEntryPosition + 11, currentEntryPosition + 12));
            StringBuilder fileAttribute = new StringBuilder();
            if ((attributeFlag & 1) == 1) fileAttribute.append("read_only ");
            if ((attributeFlag & 2) == 2) fileAttribute.append("hidden ");
            if ((attributeFlag & 4) == 4) fileAttribute.append("system ");
            if ((attributeFlag & 8) == 8) fileAttribute.append("volume_name ");
            if ((attributeFlag & 16) == 16) fileAttribute.append("dir ");
            if ((attributeFlag & 32) == 32) fileAttribute.append("archive_flag ");
            System.out.println("\tFile attribute: '" + fileAttribute + "'");

            long milliSeconds = byteArray2Int(Arrays.copyOfRange(array, currentEntryPosition + 13, currentEntryPosition + 14)) * 10;
            byte[] byteTime = Arrays.copyOfRange(array, currentEntryPosition + 14, currentEntryPosition + 16);
            byte[] byteDate = Arrays.copyOfRange(array, currentEntryPosition + 16, currentEntryPosition + 18);
            LocalDateTime creationDateTime = byteArray2DateTime(byteTime,byteDate, (int) milliSeconds);
            if (!creationDateTime.equals(START_TIME)) {
                System.out.println("\tCreation date/time: " + creationDateTime);
            }

            byteDate = Arrays.copyOfRange(array, currentEntryPosition + 18, currentEntryPosition + 20);
            LocalDateTime lastAccessDate = byteArray2DateTime(null,byteDate, 0);
            if (!lastAccessDate.equals(START_TIME)) {
                System.out.println("\tLast access date: " + lastAccessDate);
            }

            byteTime = Arrays.copyOfRange(array, currentEntryPosition + 22, currentEntryPosition + 24);
            byteDate = Arrays.copyOfRange(array, currentEntryPosition + 24, currentEntryPosition + 26);
            LocalDateTime lastWriteDateTime = byteArray2DateTime(byteTime,byteDate, 0);
            if (!lastWriteDateTime.equals(START_TIME)) {
                System.out.println("\tLast write timestamp: " + lastWriteDateTime);
            }

            long firstCluster = byteArray2Int(Arrays.copyOfRange(array, currentEntryPosition + 26, currentEntryPosition + 28));
            System.out.println("\tFirst cluster: " + firstCluster);

            long fileSize = byteArray2Int(Arrays.copyOfRange(array, currentEntryPosition + 28, currentEntryPosition + 32));
            if (fileSize != 0) System.out.println("\tFile size: " + fileSize/1024 + " Kb");
            currentEntryPosition += 32;
        }
    }

    private static long byteArray2Int(byte[] byteArray) {
        long result = 0L;
        for (int i = byteArray.length - 1; i >= 0; i--) {
            result += (byteArray[i] >= 0) ? (long) byteArray[i] << i * 8 :(long) (256 + byteArray[i]) << i * 8;

        }
        return result;
    }

    private static void reverseArray(byte[] originalArray) {
        for (int i = 0; i < originalArray.length / 2; i++) {
            byte temp = originalArray[i];
            originalArray[i] = originalArray[originalArray.length - i - 1];
            originalArray[originalArray.length - i - 1] = temp;
        }
    }

    private static LocalDateTime byteArray2DateTime(byte[] byteTime,byte[] byteDate, int milliseconds) {
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);

        long days=byteArray2Int(byteDate)& 31;
        long months=(byteArray2Int(byteDate) >> 5) & 15;
        long years=byteArray2Int(byteDate) >> 9;

        dateTime=dateTime.plusDays(days)
                .plusMonths(months)
                .plusYears(years);

        if (byteTime!=null) {
            long seconds=(byteArray2Int(byteTime)& 31)*2;
            long minutes=(byteArray2Int(byteTime) >> 5) & 63;
            long hours=byteArray2Int(byteTime)>>11;

            dateTime=dateTime.plusSeconds(seconds)
                    .plusMinutes(minutes)
                    .plusHours(hours);
        }
        return dateTime;
    }
}