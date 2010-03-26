/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class CodeUtil {
    private static int commentLineWidth = 80; // TODO: Konfigurieren!
    private static String fileComment =       // TODO: Konfigurieren!
            "Copyright (c) 2010 by Guido Steinacker\n" +
                    "This file is generated using JCG.";


    public static String fileComment() {
        return toBlockComment(fileComment, commentLineWidth);
    }

    public static String toJavaDocComment(final String text, final int lineWidth) {
        final List<String> lines = splitCommentIntoLines(text, lineWidth);
        final StringBuilder comment = new StringBuilder();
        comment.append("/**");
        for (final String line : lines) {
            comment.append("\n * ").append(line);
        }
        comment.append("\n */");
        return comment.toString();
    }

    public static String toBlockComment(final String text, final int lineWidth) {
        final List<String> lines = splitCommentIntoLines(text, lineWidth);
        final StringBuilder comment = new StringBuilder();
        comment.append("/*");
        for (final String line : lines) {
            comment.append("\n * ").append(line);
        }
        comment.append("\n */");
        return comment.toString();
    }

    /**
     * Formats a given text into a List of lines. Newlines inside the text are preserved.
     * Additional newlines are added if a line is <code>lineWidth</code> characters long.
     *
     * @param text
     * @param lineWidth
     * @return
     */
    private static List<String> splitCommentIntoLines(final String text, final int lineWidth) {
        final List<String> lines = new ArrayList<String>();
        final String[] wordsWithNewLines = text.replaceAll("\\r", "").split(" ");
        StringBuilder sb = new StringBuilder();
        boolean isFirstWord = true;
        for (final String wordWithNewLines : wordsWithNewLines) {
            final String[] words = wordWithNewLines.split("\\n");
            for (int i = 0; i < words.length; ++i) {
                final String word = words[i];
                final boolean newLine = i > 0
                        || sb.length() + word.length() > lineWidth
                        || sb.length() > 10 && word.length() > lineWidth;
                if (newLine) {
                    lines.add(sb.toString());
                    sb = new StringBuilder();
                    isFirstWord = true;
                }
                if (!isFirstWord)
                    sb.append(" ");
                sb.append(word);

                isFirstWord = false;

            }
        }
        if (sb.length() > 0)
            lines.add(sb.toString());
        return lines;
    }
}
