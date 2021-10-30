import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

final class GitCommand
{
    public static final String[] CMD_GIT_VERSION              = {"git", "--version"};
    public static final String[] CMD_GIT_HASH_LATEST_COMMIT   = {"git", "log", "-1", "--pretty=format:%H", ""};
    public static final String[] CMD_GIT_CHECKOUT_COMMIT      = {"git", "checkout", ""};
    public static final String[] CMD_GIT_TIME_LATEST_COMMIT   = {"git", "log", "-1", "--format=%cd"};
    public static final String[] CMD_GIT_LATEST_COMITTER      = {"git", "log", "-1", "--format='%cn <%ce>'"};
    public static final String[] CMD_GIT_CLONE                = {"git", "clone", "", ""};
    public static final String FORMAT_TIMESTAMP_BEFORE        = "--before=\"%s\"";
    public static final String FORMAT_TIMESTAMP_AFTER         = "--after=\"%s\"";

    public static String[] cmdGitClone(String link, String path)
    {
        String[] copy = CMD_GIT_CLONE;
        copy[2] = link;
        copy[3] = path;
        return copy;
    }

    public static String getTimestampFrom(  boolean before, int year, int month, int day,
                                            int hour, int minute, int seconds, int nanoSeconds, ZoneId zoneId)
    {
        String formatted = ZonedDateTime.of(year, month, day, hour, minute, seconds, nanoSeconds, zoneId)
                            .format(DateTimeFormatter.ISO_DATE_TIME);
        return before ?
                String.format(FORMAT_TIMESTAMP_BEFORE, formatted) :
                String.format(FORMAT_TIMESTAMP_AFTER, formatted);
    }

    public static String[] getCmdGitHashLatestCommit(String timestamp)
    {
        String[] copy = CMD_GIT_HASH_LATEST_COMMIT;
        copy[4] = timestamp;
        return copy;
    }

    public static String[] getCmdGitCheckoutCommit(String hash)
    {
        String[] copy = CMD_GIT_CHECKOUT_COMMIT;
        copy[2] = hash;
        return copy;
    }
}
