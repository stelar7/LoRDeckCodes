package no.stelar7.lor.utils;

public class Utilities
{
    public static String padLeft(String input, String val, int length)
    {
        StringBuilder sb = new StringBuilder(input);
        while (sb.length() < length)
        {
            sb.insert(0, val);
        }
        
        return sb.toString();
    }
}
