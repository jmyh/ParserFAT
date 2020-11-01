import java.time.LocalDateTime;
import java.time.ZoneOffset;


public class ParserHelper {

    public static long byteArray2Int(byte[] byteArray) {
        long result = 0L;
        for (int i = byteArray.length - 1; i >= 0; i--) {
            result += (byteArray[i] >= 0) ? (long) byteArray[i] << i * 8 :(long) (256 + byteArray[i]) << i * 8;

        }
        return result;
    }

    public static void reverseArray(byte[] originalArray) {
        for (int i = 0; i < originalArray.length / 2; i++) {
            byte temp = originalArray[i];
            originalArray[i] = originalArray[originalArray.length - i - 1];
            originalArray[originalArray.length - i - 1] = temp;
        }
    }

    public static LocalDateTime byteArray2DateTime(byte[] byteTime, byte[] byteDate, int milliseconds) {
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
