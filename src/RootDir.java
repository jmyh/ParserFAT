import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RootDir {

    private List<RootEntry> entriesList=new ArrayList<>();

    RootDir(byte[] rootDirBytes) {
        int currentPosition=0;
        for(int i=0;i<rootDirBytes.length/32;i++) {
            if (rootDirBytes[(currentPosition)] == 0) break;
            entriesList.add(new RootEntry(Arrays.copyOfRange(rootDirBytes, currentPosition, currentPosition + 32)));
            currentPosition+=32;
        }
    }

    @Override
    public String toString() {
        StringBuilder rootDirInfo=new StringBuilder();
        for (int i = 0; i < entriesList.size(); i++) {
            rootDirInfo.append(i + 1).append(") ").append(entriesList.get(i)).append("\n\n");
        }
        return rootDirInfo.toString();
    }
}
