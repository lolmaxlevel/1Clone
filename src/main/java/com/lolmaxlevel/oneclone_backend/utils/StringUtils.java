package com.lolmaxlevel.oneclone_backend.utils;

public class StringUtils {
    public static String convertCyrilic(String message) {
        char[] abcCyr = {
                'а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м',
                'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы',
                'ь', 'э', 'ю', 'я',
                ' ', '/', '\\', ':', '*', '?', '\"', '<', '>', '|', '!',
                '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
                '.', ',', '(', ')', '-', '_', '+', '=', '[', ']', '{', '}',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

        StringBuilder builder = getStringBuilder(message, abcCyr);
        return builder.toString();
    }

    private static StringBuilder getStringBuilder(String message, char[] abcCyr) {
        String[] abcLat = {
                "a", "b", "v", "g", "d", "e", "e", "zh", "z", "i", "y", "k", "l", "m",
                "n", "o", "p", "r", "s", "t", "u", "f", "h", "c", "ch", "sh", "sha", "a", "i", "'", "e", "yu", "ya",
                " ", "/", "\\", ":", "*", "?", "\"", "<", ">", "|", "!",
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
                ".", ",", "(", ")", "-", "_", "+", "=", "[", "]", "{", "}",
                "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
                "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            for (int x = 0; x < abcCyr.length; x++) {
                if (message.charAt(i) == abcCyr[x]) {
                    builder.append(abcLat[x]);
                    break;
                }
            }
        }
        return builder;
    }
}
