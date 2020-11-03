package slack;

import fat.DirEntry;
import fat.FAT16;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SlackSpaceSeacher {

    private FAT16 fat16;
    private int startDataCluster;
    private int sectorPerCluster;
    private int sectorSize;
    private List<Long> allocateTable;
    private int clusterSize;
    private List<SlackSpace> slackSpaces;

    public SlackSpaceSeacher(FAT16 fat16) {
        this.fat16 = fat16;
        startDataCluster = fat16.getStartClusterArea();
        sectorPerCluster = fat16.getBootSector().getNumClustersInSector();
        sectorSize = fat16.getBootSector().getSectorSize();
        clusterSize = sectorSize * sectorPerCluster;
        allocateTable = fat16.getAllocateTable();
        slackSpaces = new ArrayList<>();
    }

    public List<SlackSpace> find() {
        List<DirEntry> rootEntries = fat16.getRootDir().getEntriesList();
        for (DirEntry entry : rootEntries) {
            search(entry, slackSpaces);
        }
        return slackSpaces;
    }

    private void search(DirEntry entry, List<SlackSpace> slackSpaces) {
        Pattern pattern = Pattern.compile("^\\.{1,2} *$");
        Matcher matcher = pattern.matcher(entry.getFileName());
        int clusterAddress;

        if (!matcher.find()) {
            if (entry.getFileAttributes().contains(DirEntry.DIR)) {

                int currentPosition = (startDataCluster + (entry.getFirstCluster() - 2) * sectorPerCluster) * sectorSize;
                while (fat16.getImageBytes()[currentPosition] != 0) {
                    DirEntry curEntry = new DirEntry(Arrays.copyOfRange(
                            fat16.getImageBytes(), currentPosition, currentPosition + 32));
                    search(curEntry, slackSpaces);
                    currentPosition += 32;
                }

            } else if (!entry.getFileAttributes().contains(DirEntry.VOLUME_NAME) &&
                    (entry.getFileSize() / sectorSize) % sectorPerCluster != 0) {
                System.out.println("Slack space was found in file: ");
                System.out.println(entry);

                clusterAddress = entry.getFirstCluster();
//                    0xFFF8=65528
                int i = 0;
                while (i < allocateTable.size()) {
                    if (allocateTable.get(clusterAddress) <= 65528)
                        clusterAddress = (int) (long) allocateTable.get(clusterAddress);
                    else break;
                }
                if (clusterAddress!=0) {
                    slackSpaces.add(createSlackSpace(clusterAddress));
                }
            }
        }
    }

    private SlackSpace createSlackSpace(int clusterAddress) {
        int sectorAddress=fat16.getStartClusterArea()+(clusterAddress-2)*sectorPerCluster;
        byte[] slackBytes=Arrays.copyOfRange(
                fat16.getImageBytes(),
                sectorAddress*sectorSize,
                (sectorAddress+sectorPerCluster)*sectorSize
        );
        for (int i=0; i<sectorPerCluster;i++) {
            if (slackBytes[i*fat16.getBootSector().getSectorSize()]==0) {
                return new SlackSpace(sectorAddress+i,sectorPerCluster-i);
            }
        }
        return null;
    }
}

