package fat;

import fat.BootSector;
import service.ParserHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FAT16 {

    private BootSector bootSector;
    private RootDir rootDir;
    private List<Long> allocateTable;
    int startAllocateTable;
    int startRootDir;
    int startClusterArea;
    byte[] imageBytes;


    public FAT16(byte[] imageBytes) {
        this.imageBytes=imageBytes;
        bootSector=new BootSector(Arrays.copyOfRange(imageBytes, 0, 512));

        startAllocateTable=bootSector.getNumReservedSectors();
        allocateTable=calculateAllocateTable(Arrays.copyOfRange(
                imageBytes,
                 startAllocateTable*bootSector.getSectorSize(),
                (startAllocateTable+bootSector.getNumSectorsPerFAT())*bootSector.getSectorSize()
        ));

        startRootDir=(bootSector.getNumOfHiddenSectors()+1+bootSector.getNumAllocateTables()*bootSector.getNumSectorsPerFAT());
        rootDir=new RootDir(Arrays.copyOfRange(
                imageBytes,
                startRootDir*bootSector.getSectorSize(),
                startRootDir*bootSector.getSectorSize()+bootSector.getMaxNumRootEntries()*32)
        );
        startClusterArea=startRootDir+(bootSector.getMaxNumRootEntries()*32)/bootSector.getSectorSize();
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public BootSector getBootSector() {
        return bootSector;
    }

    public RootDir getRootDir() {
        return rootDir;
    }

    public List<Long> getAllocateTable() {
        return allocateTable;
    }

    public int getStartAllocateTable() {
        return startAllocateTable;
    }

    public int getStartRootDir() {
        return startRootDir;
    }

    public int getStartClusterArea() {
        return startClusterArea;
    }

    private List<Long> calculateAllocateTable(byte[] allocateTableBytes) {
        List<Long> allocateTable=new ArrayList<>();
        for (int i = 0; i < allocateTableBytes.length; i+=2) {
            allocateTable.add(ParserHelper.byteArray2Int(Arrays.copyOfRange(allocateTableBytes,i,i+2)));
        }
        return allocateTable;
    }

    @Override
    public String toString() {
        StringBuilder fatInfo=new StringBuilder();
        int currentPosition = bootSector.getNumOfHiddenSectors() + 1;
        for (int i = 0; i < bootSector.getNumAllocateTables(); i++) {
            fatInfo.append("FAT ")
                    .append(i)
                    .append(": ")
                    .append(currentPosition)
                    .append(" → ")
                    .append(currentPosition + bootSector.getNumSectorsPerFAT() - 1)
                    .append("\n");

            currentPosition += bootSector.getNumSectorsPerFAT();
        }
        fatInfo.append("Data area: ").append(currentPosition).append(" → ").append(bootSector.getNumSectors()).append("\n");
        fatInfo.append("\tRoot directory: ")
                .append(currentPosition)
                .append(" → ")
                .append(currentPosition + bootSector.getMaxNumRootEntries() * 32 / bootSector.getSectorSize() - 1)
                .append("\n");
        currentPosition += bootSector.getMaxNumRootEntries() * 32 / bootSector.getSectorSize();
        fatInfo.append("\tCluster area: ").append(currentPosition).append(" → ").append(bootSector.getNumSectors());

        return "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~File system information~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n\n"
                + bootSector +
                "\n\nFile system layout in sectors:\n"+
                "Reserved: " + bootSector.getNumOfHiddenSectors() + " → " + (bootSector.getNumReservedSectors() - 1)+"\n"+
                "\tBoot sector: " + bootSector.getNumOfHiddenSectors()+"\n"+
                fatInfo+
                "\n\nRoot directory structure:\n"+rootDir;
    }
}
