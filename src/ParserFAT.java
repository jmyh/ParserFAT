import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

//https://www.file-recovery.com/recovery-FAT-BOOT-sector-bios.htm
//http://www.maverick-os.dk/FileSystemFormats/FAT16_FileSystem.html#:~:text=The%20FAT16%20file%20system%20uses,volumes%20are%2004h%20and%2006h.

/**
 * Парсит образ файловой системы и выводит ее параметры в консоль
 * Проверено только на FAT 16
 */
public class ParserFAT {
    public static void main(String[] args) throws IOException {
        String fileName = "adams.dd";
        byte[] array = Files.readAllBytes(Paths.get(fileName));

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~File system information~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

        String oemName = new String(Arrays.copyOfRange(array, 3, 11));
        System.out.println("03\t8\tOEM Name: " + oemName);

        int sectorSize = byteArray2Int(Arrays.copyOfRange(array, 11, 13));
        System.out.println("0B\t2\tSector size: " + sectorSize);

        int numClustersIntoSector = byteArray2Int(Arrays.copyOfRange(array, 13, 14));
        System.out.println("0D\t1\tCluster size: " + numClustersIntoSector * sectorSize);

        int numReservedSectors = byteArray2Int(Arrays.copyOfRange(array, 14, 16));
        System.out.println("0E\t2\tNumber of reserved sectors: " + numReservedSectors);

        int numAllocateTables = byteArray2Int(Arrays.copyOfRange(array, 16, 17));
        System.out.println("10\t1\tNumber of file allocation tables (FATs): " + numAllocateTables);

        int maxNumRootEntries = byteArray2Int(Arrays.copyOfRange(array, 17, 19));
        System.out.println("11\t2\tThe total number of file name entries: " + maxNumRootEntries);

        int numSmallSectors = byteArray2Int(Arrays.copyOfRange(array, 19, 21));
        int numLargeSectors = byteArray2Int(Arrays.copyOfRange(array, 32, 36));
        int numSectors=((numSmallSectors == 0) ? numLargeSectors : numSmallSectors);
        System.out.println("\t\tNumber of sectors: " + numSectors);

        String mediaType = DatatypeConverter.printHexBinary(Arrays.copyOfRange(array, 21, 22));
        System.out.println("15\t1\tMedia type: " + mediaType);

        int numSectorsPerFAT = byteArray2Int(Arrays.copyOfRange(array, 22, 24));
        System.out.println("16\t2\tSectors per FAT: " + numSectorsPerFAT);

        int numSectorsPerTrack = byteArray2Int(Arrays.copyOfRange(array, 24, 26));
        System.out.println("18\t2\tSectors per Track: " + numSectorsPerTrack);

        int numOfHeads = byteArray2Int(Arrays.copyOfRange(array, 26, 28));
        System.out.println("1A\t2\tNumber of Heads: " + numOfHeads);

        int numOfHiddenSectors = byteArray2Int(Arrays.copyOfRange(array, 28, 32));
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
        System.out.println("Reserved: "+numOfHiddenSectors+" → "+(numReservedSectors-1));
        System.out.println("\tBoot sector: "+numOfHiddenSectors);
        int currentPosition=numOfHiddenSectors+1;
        for (int i=0;i<numAllocateTables;i++) {
            System.out.println("FAT "+i+": "+currentPosition+" → "+(currentPosition+numSectorsPerFAT-1));
            currentPosition+=numSectorsPerFAT;
        }
        System.out.println("Data area: "+currentPosition+" → "+numSectors);
        System.out.println("\tRoot directory: "+currentPosition+" → "+(currentPosition+maxNumRootEntries*32/sectorSize-1));
        currentPosition+=maxNumRootEntries*32/sectorSize;
        System.out.println("\tCluster area: "+currentPosition+" → "+numSectors);
    }

    private static int byteArray2Int(byte[] byteArray) {
        int result = 0;
        for (int i = byteArray.length - 1; i >= 0; i--) {
            result += byteArray[i] << i * 8;
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
}