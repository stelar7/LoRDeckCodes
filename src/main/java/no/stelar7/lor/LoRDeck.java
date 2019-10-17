package no.stelar7.lor;


import java.util.*;
import java.util.stream.Collectors;

public class LoRDeck
{
    private static Map<String, Integer> factionIds = new HashMap<>()
    {{
        put("DE", 0);
        put("FR", 1);
        put("IO", 2);
        put("NX", 3);
        put("PZ", 4);
        put("SI", 5);
    }};
    
    private static Map<Integer, String> IdFactions = new HashMap<>()
    {{
        put(0, "DE");
        put(1, "FR");
        put(2, "IO");
        put(3, "NX");
        put(4, "PZ");
        put(5, "SI");
    }};
    
    public static List<LorCardCount> decode(String code)
    {
        List<LorCardCount> result = new ArrayList<>();
        List<Byte>         bytes  = new ArrayList<>(Arrays.asList(Base32.decodeBoxed(code)));
        
        int firstByte = bytes.remove(0);
        int format    = firstByte >>> 4;
        int version   = firstByte & 0xF;
        
        int MAX_KNOWN_VERSION = 1;
        if (version > MAX_KNOWN_VERSION)
        {
            throw new IllegalArgumentException("The provided code requires a higher version of this library; please update");
        }
        
        for (int i = 3; i > 0; i--)
        {
            int groupCount = VarInt.pop(bytes);
            
            for (int j = 0; j < groupCount; j++)
            {
                int itemCount = VarInt.pop(bytes);
                int set       = VarInt.pop(bytes);
                int faction   = VarInt.pop(bytes);
                
                for (int k = 0; k < itemCount; k++)
                {
                    int    card          = VarInt.pop(bytes);
                    String setString     = padLeft(String.valueOf(set), 2);
                    String factionString = IdFactions.get(faction);
                    String cardString    = padLeft(String.valueOf(card), 3);
                    
                    result.add(new LorCardCount(setString, factionString, cardString, i));
                }
            }
        }
        
        while (bytes.size() > 0)
        {
            int count   = VarInt.pop(bytes);
            int set     = VarInt.pop(bytes);
            int faction = VarInt.pop(bytes);
            int number  = VarInt.pop(bytes);
            
            String setString     = padLeft(String.valueOf(set), 2);
            String factionString = IdFactions.get(faction);
            String numberString  = padLeft(String.valueOf(number), 3);
            
            result.add(new LorCardCount(setString, factionString, numberString, count));
        }
        
        result.sort(Comparator.comparing(LorCardCount::getCount).reversed().thenComparing(LorCardCount::getCardCode));
        
        return result;
        
    }
    
    private static String padLeft(String input, int length)
    {
        return "0".repeat(length).substring(input.length()) + input;
    }
    
    public static String encode(List<LorCardCount> cards)
    {
        List<Integer> result = new ArrayList<>();
        if (!isValidDeck(cards))
        {
            throw new IllegalArgumentException("The deck provided contains invalid card codes");
        }
        
        // format and version
        result.add(17);
        
        Map<Integer, List<LorCardCount>> counts   = cards.stream().collect(Collectors.groupingBy(LorCardCount::getCount));
        List<List<LorCardCount>>         grouped3 = groupByFactionAndSetSorted(counts.getOrDefault(3, new ArrayList<>()));
        List<List<LorCardCount>>         grouped2 = groupByFactionAndSetSorted(counts.getOrDefault(2, new ArrayList<>()));
        List<List<LorCardCount>>         grouped1 = groupByFactionAndSetSorted(counts.getOrDefault(1, new ArrayList<>()));
        List<LorCardCount> nOfs = counts.entrySet().stream()
                                        .filter(e -> e.getKey() > 3)
                                        .flatMap(e -> e.getValue().stream())
                                        .collect(Collectors.toList());
        
        result.addAll(encodeGroup(grouped3));
        result.addAll(encodeGroup(grouped2));
        result.addAll(encodeGroup(grouped1));
        result.addAll(encodeNofs(nOfs));
        
        return Base32.encodeBoxed(result);
    }
    
    private static List<Integer> encodeNofs(List<LorCardCount> nOfs)
    {
        List<Integer> result = new ArrayList<>();
        
        for (LorCardCount card : nOfs)
        {
            int faction = factionIds.get(card.getFaction());
            
            result.addAll(VarInt.get(card.getCount()));
            result.addAll(VarInt.get(card.getSet()));
            result.addAll(VarInt.get(faction));
            result.addAll(VarInt.get(card.getId()));
        }
        
        return result;
    }
    
    private static List<Integer> encodeGroup(List<List<LorCardCount>> group)
    {
        List<Integer> result = new ArrayList<>(VarInt.get(group.size()));
        
        for (List<LorCardCount> list : group)
        {
            result.addAll(VarInt.get(list.size()));
            LorCardCount first = list.get(0);
            
            int faction = factionIds.get(first.getFaction());
            result.addAll(VarInt.get(first.getSet()));
            result.addAll(VarInt.get(faction));
            
            for (LorCardCount card : list)
            {
                result.addAll(VarInt.get(card.getId()));
            }
        }
        
        return result;
    }
    
    private static boolean isValidDeck(List<LorCardCount> cards)
    {
        for (LorCardCount card : cards)
        {
            if (card.getCardCode().length() != 7)
            {
                return false;
            }
            
            try
            {
                // set and card id
                Integer.parseInt(card.getCardCode().substring(0, 2));
                Integer.parseInt(card.getCardCode().substring(4, 7));
            } catch (NumberFormatException e)
            {
                return false;
            }
            
            String faction = card.getCardCode().substring(2, 4);
            if (!factionIds.containsKey(faction))
            {
                return false;
            }
            
            
            if (card.getCount() < 1)
            {
                return false;
            }
        }
        
        return true;
    }
    
    private static List<List<LorCardCount>> groupByFactionAndSetSorted(List<LorCardCount> cards)
    {
        List<List<LorCardCount>> result = new ArrayList<>();
        
        while (cards.size() > 0)
        {
            List<LorCardCount> set = new ArrayList<>();
            
            LorCardCount first = cards.remove(0);
            set.add(first);
            
            for (int i = cards.size() - 1; i >= 0; i--)
            {
                LorCardCount compare = cards.get(i);
                
                if (first.getSet() == compare.getSet() && first.getFaction().equalsIgnoreCase(compare.getFaction()))
                {
                    set.add(compare);
                    cards.remove(i);
                }
            }
            
            result.add(set);
        }
        
        // sort outer list by size, then by inner list code, then sort inner list by code
        Comparator<List<LorCardCount>> c  = Comparator.comparing(List::size);
        Comparator<List<LorCardCount>> c2 = Comparator.comparing((List<LorCardCount> a) -> a.get(0).getCardCode());
        result.sort(c.thenComparing(c2));
        for (List<LorCardCount> group : result)
        {
            group.sort(Comparator.comparing(LorCardCount::getCardCode));
        }
        
        return result;
    }
}
