import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegxTest {
    public static void test() {
        String lineText = "";
        String pattern = "(?<=(public\\s|protected\\s|private\\s)?(static\\s)?(final/s)?(void/s|(.+)?\\s)?).+?\\(.*?\\)(\\s)*\\{";

        boolean isMatch = Pattern.matches(pattern, lineText);
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(lineText);

        System.out.println("字符串中是否包含了 'runoob' 子字符串? " + isMatch);
    }
}
