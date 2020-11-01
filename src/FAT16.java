import java.util.Arrays;

public class FAT16 {

    private BootSector bootSector;
    private RootDir rootDir;

    FAT16(byte[] imageBytes) {
        bootSector=new BootSector(Arrays.copyOfRange(imageBytes, 0, 512));
        int shift=(bootSector.getNumOfHiddenSectors()+1+bootSector.getNumAllocateTables()*bootSector.getNumSectorsPerFAT())*512;
        rootDir=new RootDir(Arrays.copyOfRange(imageBytes, shift, shift+bootSector.getMaxNumRootEntries()*32));
    }

    public BootSector getBootSector() {
        return bootSector;
    }

    public RootDir getRootDir() {
        return rootDir;
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
