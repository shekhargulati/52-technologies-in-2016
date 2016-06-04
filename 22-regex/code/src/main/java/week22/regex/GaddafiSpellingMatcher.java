package week22.regex;

import ru.lanwen.verbalregex.VerbalExpression;

public class GaddafiSpellingMatcher {

    public static boolean match(final String spelling) {
        VerbalExpression verbalExpression = VerbalExpression
                .regex()
                .startOfLine()
                .anyOf("GKQ")
                .maybe(VerbalExpression.regex().anyOf("uh"))
                .maybe(VerbalExpression.regex().any("ae"))
                .anyOf("tdz'").count(1, 2)
                .maybe("h")
                .maybe("dh")
                .then("a")
                .then("f").count(1, 2)
                .anyOf("iy")
                .endOfLine()
                .build();
        return verbalExpression.test(spelling);
    }

}
