package slack;

public class SlackSpace {

    private int sectorStartAddress;
    private int numEmptySectors;

    public SlackSpace(int sectorStartAddress, int numEmptySectors) {
        this.sectorStartAddress = sectorStartAddress;
        this.numEmptySectors = numEmptySectors;
    }

    public int getSectorStartAddress() {
        return sectorStartAddress;
    }

    public int getNumEmptySectors() {
        return numEmptySectors;
    }

    @Override
    public String toString() {
        return "SlackSpace{" +
                "sectorStartAddress=" + sectorStartAddress +
                ", numEmptySectors=" + numEmptySectors +
                '}';
    }
}
