package slack;

import fat.FAT16;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

public class SlackSpaceWriter {

    private List<SlackSpace> slackSpaces;
    private FAT16 fat16;
    private int sectorSize;

    public SlackSpaceWriter(FAT16 fat16, List<SlackSpace> slackSpaces) {
        this.slackSpaces=slackSpaces;
        this.fat16=fat16;
        sectorSize=fat16.getBootSector().getSectorSize();
    }

    public void write(String data, Path pathToOutputFile) throws IOException {
        byte[] insertBytes=data.getBytes();
        Random rand=new Random();
        int index=rand.nextInt(slackSpaces.size());
        SlackSpace slackSpace=slackSpaces.get(index);
        byte[] resultBytes=new byte[fat16.getImageBytes().length];

        System.arraycopy(
                fat16.getImageBytes(),
                0,
                resultBytes,
                0,slackSpace.getSectorStartAddress()*sectorSize
        );

        System.arraycopy(
                insertBytes,
                0,
                resultBytes,
                slackSpace.getSectorStartAddress()*sectorSize,
                insertBytes.length
        );

        System.arraycopy(
                fat16.getImageBytes(),
                slackSpace.getSectorStartAddress()*sectorSize+insertBytes.length,
                resultBytes,
                slackSpace.getSectorStartAddress()*sectorSize+insertBytes.length,
                resultBytes.length-slackSpace.getSectorStartAddress()*sectorSize-insertBytes.length
        );

        Files.write(pathToOutputFile,resultBytes);
    }
}
