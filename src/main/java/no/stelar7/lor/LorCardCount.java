package no.stelar7.lor;

import java.util.Objects;

public class LorCardCount
{
    private final String cardCode;
    private final int    count;
    
    public LorCardCount(String cardCode, int count)
    {
        this.cardCode = cardCode;
        this.count = count;
    }
    
    public LorCardCount(String setString, String factionString, String numberString, int count)
    {
        this(setString + factionString + numberString, count);
    }
    
    public String getCardCode()
    {
        return cardCode;
    }
    
    public int getCount()
    {
        return count;
    }
    
    public int getSet()
    {
        return Integer.parseInt(cardCode.substring(0, 2));
    }
    
    public String getFaction()
    {
        return cardCode.substring(2, 4);
    }
    
    public int getId()
    {
        return Integer.parseInt(cardCode.substring(4, 7));
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        LorCardCount that = (LorCardCount) o;
        return count == that.count &&
               Objects.equals(cardCode, that.cardCode);
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(cardCode, count);
    }
    
    @Override
    public String toString()
    {
        return "LorCardCount{" +
               "cardCode='" + cardCode + '\'' +
               ", count=" + count +
               '}';
    }
}
