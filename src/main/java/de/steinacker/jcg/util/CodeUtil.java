/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.util;

import de.steinacker.jcg.model.QualifiedName;
import de.steinacker.jcg.model.Type;
import de.steinacker.jcg.model.TypeSymbol;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class CodeUtil {
    private static final int TAB_WIDTH = 4;     // TODO: Konfigurieren!
    private static int COMMENT_LINE_WIDTH = 80; // TODO: Konfigurieren!
    private static String FILE_COMMENT =        // TODO: Konfigurieren!
            "Copyright (c) 2010 by Guido Steinacker\n" +
                    "This file is generated using JCG.";


    public static CharSequence fileComment() {
        return toBlockComment(FILE_COMMENT, COMMENT_LINE_WIDTH);
    }

    public static String printExtends(final Type type) {
        final TypeSymbol superClass = type.getSuperClass();
        if (type.getKind() == Type.Kind.CLASS
                && !superClass.equals(QualifiedName.valueOf("java.lang.Object"))) {
            return  new StringBuilder("extends ")
                    .append(superClass.toString())
                    .toString();
        } else {
            return "";
        }
    }

    public static String printImplements(final Type type) {
        final StringBuilder sb = new StringBuilder();
        final List<TypeSymbol> interfaces = type.getImplementedInterfaces();
        if (interfaces.size() > 0) {
            if (type.getKind() == Type.Kind.INTERFACE)
                sb.append("extends");
            else
                sb.append("implements");
            sb.append(" ").append(interfaces.get(0).toString());
            if (interfaces.size() > 1) {
                for (int i=1,n=interfaces.size(); i<n; ++i)
                    sb.append(", ").append(interfaces.get(i).toString());
            }
        }
        return sb.toString();
    }

    public static CharSequence toJavaDocComment(final String text, final int lineWidth) {
        final List<String> lines = splitCommentIntoLines(text, lineWidth);
        final StringBuilder comment = new StringBuilder();
        comment.append("/**");
        for (final String line : lines) {
            comment.append("\n * ").append(line);
        }
        comment.append("\n */");
        return comment.toString();
    }

    public static CharSequence toBlockComment(final String text, final int lineWidth) {
        final List<String> lines = splitCommentIntoLines(text, lineWidth);
        final StringBuilder comment = new StringBuilder();
        comment.append("/*");
        for (final String line : lines) {
            comment.append("\n * ").append(line);
        }
        comment.append("\n */");
        return comment.toString();
    }

    public static CharSequence indent(final String text, final int steps) {
        if (text != null) {
            final String s = text.replaceAll("\r", "");
            final String[] lines = s.split("\n");
            final StringBuilder sb = new StringBuilder();
            boolean firstLine = true;
            for (final String line : lines) {
                if (!firstLine)
                    sb.append("\n");
                else
                    firstLine = false;
                for (int i=0, len=steps * TAB_WIDTH; i<len; ++i)
                    sb.append(' ');
                sb.append(line);
            }
            return sb;
        } else {
            return "";
        }
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
