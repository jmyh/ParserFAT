package fat;

import service.ParserHelper;

import javax.xml.bind.DatatypeConverter;
import java.util.Arrays;

public class BootSector {

    private String oemName;
    private int sectorSize;
    private int clusterSize;
    private int numClustersInSector;
    private int numReservedSectors;
    private int numAllocateTables;
    private int maxNumRootEntries;
    private int numSectors;
    private String mediaType;
    private int numSectorsPerFAT;
    private int numSectorsPerTrack;
    private int numOfHeads;
    private int numOfHiddenSectors;
    private String physicalDiskNum;
    private String reservedByte;
    private String extendBootSign;
    private String volumeID;
    private String volumeLabel;
    private String fileSystemType;

    BootSector(byte[] bootSectorBytes) {
        oemName = new String(Arrays.copyOfRange(bootSectorBytes, 3, 11));
        sectorSize = (int) ParserHelper.byteArray2Int(Arrays.copyOfRange(bootSectorBytes, 11, 13));
        numClustersInSector = (int) ParserHelper.byteArray2Int(Arrays.copyOfRange(bootSectorBytes, 13, 14));
        clusterSize=sectorSize*numClustersInSector;
        numReservedSectors = (int) ParserHelper.byteArray2Int(Arrays.copyOfRange(bootSectorBytes, 14, 16));
        numAllocateTables = (int) ParserHelper.byteArray2Int(Arrays.copyOfRange(bootSectorBytes, 16, 17));
        maxNumRootEntries = (int) ParserHelper.byteArray2Int(Arrays.copyOfRange(bootSectorBytes, 17, 19));
        int numSmallSectors = (int) ParserHelper.byteArray2Int(Arrays.copyOfRange(bootSectorBytes, 19, 21));
        int numLargeSectors = (int) ParserHelper.byteArray2Int(Arrays.copyOfRange(bootSectorBytes, 32, 36));
        numSectors = ((numSmallSectors == 0) ? numLargeSectors : numSmallSectors);
        mediaType = DatatypeConverter.printHexBinary(Arrays.copyOfRange(bootSectorBytes, 21, 22));
        numSectorsPerFAT = (int) ParserHelper.byteArray2Int(Arrays.copyOfRange(bootSectorBytes, 22, 24));
        numSectorsPerTrack = (int) ParserHelper.byteArray2Int(Arrays.copyOfRange(bootSectorBytes, 24, 26));
        numOfHeads = (int) ParserHelper.byteArray2Int(Arrays.copyOfRange(bootSectorBytes, 26, 28));
        numOfHiddenSectors = (int) ParserHelper.byteArray2Int(Arrays.copyOfRange(bootSectorBytes, 28, 32));
        physicalDiskNum = DatatypeConverter.printHexBinary(Arrays.copyOfRange(bootSectorBytes, 36, 37));
        reservedByte = DatatypeConverter.printHexBinary(Arrays.copyOfRange(bootSectorBytes, 37, 38));
        extendBootSign = DatatypeConverter.printHexBinary(Arrays.copyOfRange(bootSectorBytes, 38, 39));
        byte[] volumeIDArr = Arrays.copyOfRange(bootSectorBytes, 39, 43);
        ParserHelper.reverseArray(volumeIDArr);
        volumeID = DatatypeConverter.printHexBinary(volumeIDArr);
        volumeLabel = new String(Arrays.copyOfRange(bootSectorBytes, 43, 54));
        fileSystemType = new String(Arrays.copyOfRange(bootSectorBytes, 54, 62));
    }

    public String getOemName() {
        return oemName;
    }

    public int getSectorSize() {
        return sectorSize;
    }

    public int getClusterSize() {
        return clusterSize;
    }

    public int getNumClustersInSector() {
        return numClustersInSector;
    }

    public int getNumReservedSectors() {
        return numReservedSectors;
    }

    public int getNumAllocateTables() {
        return numAllocateTables;
    }

    public int getMaxNumRootEntries() {
        return maxNumRootEntries;
    }

    public int getNumSectors() {
        return numSectors;
    }

    public String getMediaType() {
        return mediaType;
    }

    public int getNumSectorsPerFAT() {
        return numSectorsPerFAT;
    }

    public int getNumSectorsPerTrack() {
        return numSectorsPerTrack;
    }

    public int getNumOfHeads() {
        return numOfHeads;
    }

    public int getNumOfHiddenSectors() {
        return numOfHiddenSectors;
    }

    public String getPhysicalDiskNum() {
        return physicalDiskNum;
    }

    public String getReservedByte() {
        return reservedByte;
    }

    public String getExtendBootSign() {
        return extendBootSign;
    }

    public String getVolumeID() {
        return volumeID;
    }

    public String getVolumeLabel() {
        return volumeLabel;
    }

    public String getFileSystemType() {
        return fileSystemType;
    }

    @Override
    public String toString() {
        return "fat.BootSector {" +
                "\n\t03\t8\tOEM Name: " + oemName +
                "\n\t0B\t2\tSector size: " + sectorSize +
                "\n\t0D\t1\tCluster size: " + clusterSize +
                "\n\t0E\t2\tNumber of sectors in cluster: " + numClustersInSector +
                "\n\t0E\t2\tNumber of reserved sectors: " + numReservedSectors +
                "\n\t10\t1\tNumber of file allocation tables (FATs): " + numAllocateTables +
                "\n\t11\t2\tThe total number of file name entries: " + maxNumRootEntries +
                "\n\t\t\tNumber of sectors: " + numSectors +
                "\n\t15\t1\tMedia type: " + mediaType +
                "\n\t16\t2\tSectors per FAT: " + numSectorsPerFAT +
                "\n\t18\t2\tSectors per Track: " + numSectorsPerTrack +
                "\n\t1A\t2\tNumber of Heads: " + numOfHeads +
                "\n\t1C\t4\tNumber of Hidden sectors: " + numOfHiddenSectors +
                "\n\t24\t1\tPhysical Disk Number: " + physicalDiskNum +
                "\n\t25\t2\tReserved byte: " + reservedByte +
                "\n\t26\t1\tExtended Boot Signature: " + extendBootSign +
                "\n\t27\t4\tVolume Serial Number: " + volumeID +
                "\n\t2B\t11\tVolume Label: " + volumeLabel +
                "\n\t36\t8\tFile System Type: " + fileSystemType +
                "\n}";
    }
}
