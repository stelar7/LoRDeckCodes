import no.stelar7.lor.*;
import org.junit.*;

import java.io.InputStream;
import java.util.*;

public class TestLorDeck
{
    
    @Test
    public void testEncodingRecommendedDecks()
    {
        InputStream file = TestLorDeck.class.getClassLoader().getResourceAsStream("testdata.txt");
        if (file == null)
        {
            throw new RuntimeException("Unable to load test file");
        }
        
        List<String>             codes = new ArrayList<>();
        List<List<LorCardCount>> decks = new ArrayList<>();
        
        try (Scanner scanner = new Scanner(file))
        {
            while (scanner.hasNextLine())
            {
                String line = scanner.nextLine();
                codes.add(line);
                
                List<LorCardCount> deck = new ArrayList<>();
                while (scanner.hasNextLine() && !(line = scanner.nextLine()).equalsIgnoreCase(""))
                {
                    String[] parts = line.split(":");
                    deck.add(new LorCardCount(parts[1], Integer.parseInt(parts[0])));
                }
                decks.add(deck);
            }
        }
        
        for (int i = 0; i < decks.size(); i++)
        {
            String encoded = LoRDeck.encode(decks.get(i));
            Assert.assertEquals("Decks are not equal", codes.get(i), encoded);
            
            List<LorCardCount> decoded = LoRDeck.decode(encoded);
            Assert.assertTrue("Did not produce same deck when re-coded", checkSameDeck(decks.get(i), decoded));
        }
    }
    
    @Test
    public void testDecodeEncode()
    {
        String DECK_CODE = "CEBAIAIFB4WDANQIAEAQGDAUDAQSIJZUAIAQCAIEAEAQKBIA";
        
        List<LorCardCount> deck   = LoRDeck.decode(DECK_CODE);
        String             result = LoRDeck.encode(deck);
        
        Assert.assertEquals("Did not transform code to match!", DECK_CODE, result);
    }
    
    
    @Test
    public void testSmallDeck()
    {
        List<LorCardCount> deck = new ArrayList<>()
        {{
            add(new LorCardCount("01DE002", 3));
        }};
        
        String             code    = LoRDeck.encode(deck);
        List<LorCardCount> decoded = LoRDeck.decode(code);
        
        Assert.assertTrue("Did not produce same deck when re-coded", checkSameDeck(deck, decoded));
    }
    
    @Test
    public void testLargeDeck()
    {
        List<LorCardCount> deck = new ArrayList<>()
        {{
            add(new LorCardCount("01DE002", 3));
            add(new LorCardCount("01DE003", 3));
            add(new LorCardCount("01DE004", 3));
            add(new LorCardCount("01DE005", 3));
            add(new LorCardCount("01DE006", 3));
            add(new LorCardCount("01DE007", 3));
            add(new LorCardCount("01DE008", 3));
            add(new LorCardCount("01DE009", 3));
            add(new LorCardCount("01DE010", 3));
            add(new LorCardCount("01DE011", 3));
            add(new LorCardCount("01DE012", 3));
            add(new LorCardCount("01DE013", 3));
            add(new LorCardCount("01DE014", 3));
            add(new LorCardCount("01DE015", 3));
            add(new LorCardCount("01DE016", 3));
            add(new LorCardCount("01DE017", 3));
            add(new LorCardCount("01DE018", 3));
            add(new LorCardCount("01DE019", 3));
            add(new LorCardCount("01DE020", 3));
            add(new LorCardCount("01DE021", 3));
        }};
        
        String             code    = LoRDeck.encode(deck);
        List<LorCardCount> decoded = LoRDeck.decode(code);
        
        Assert.assertTrue("Did not produce same deck when re-coded", checkSameDeck(deck, decoded));
    }
    
    
    @Test
    public void testMoreThan3Small()
    {
        List<LorCardCount> deck = new ArrayList<>()
        {{
            add(new LorCardCount("01DE002", 4));
        }};
        
        String             code    = LoRDeck.encode(deck);
        List<LorCardCount> decoded = LoRDeck.decode(code);
        
        Assert.assertTrue("Did not produce same deck when re-coded", checkSameDeck(deck, decoded));
    }
    
    @Test
    public void testMoreThan3Large()
    {
        List<LorCardCount> deck = new ArrayList<>()
        {{
            add(new LorCardCount("01DE002", 3));
            add(new LorCardCount("01DE003", 3));
            add(new LorCardCount("01DE004", 3));
            add(new LorCardCount("01DE005", 3));
            add(new LorCardCount("01DE006", 4));
            add(new LorCardCount("01DE007", 5));
            add(new LorCardCount("01DE008", 6));
            add(new LorCardCount("01DE009", 7));
            add(new LorCardCount("01DE010", 8));
            add(new LorCardCount("01DE011", 9));
            add(new LorCardCount("01DE012", 3));
            add(new LorCardCount("01DE013", 3));
            add(new LorCardCount("01DE014", 3));
            add(new LorCardCount("01DE015", 3));
            add(new LorCardCount("01DE016", 3));
            add(new LorCardCount("01DE017", 3));
            add(new LorCardCount("01DE018", 3));
            add(new LorCardCount("01DE019", 3));
            add(new LorCardCount("01DE020", 3));
            add(new LorCardCount("01DE021", 3));
        }};
        
        String             code    = LoRDeck.encode(deck);
        List<LorCardCount> decoded = LoRDeck.decode(code);
        
        Assert.assertTrue("Did not produce same deck when re-coded", checkSameDeck(deck, decoded));
    }
    
    @Test
    public void testSingleCard40()
    {
        List<LorCardCount> deck = new ArrayList<>()
        {{
            add(new LorCardCount("01DE002", 40));
        }};
        
        String             code    = LoRDeck.encode(deck);
        List<LorCardCount> decoded = LoRDeck.decode(code);
        
        Assert.assertTrue("Did not produce same deck when re-coded", checkSameDeck(deck, decoded));
    }
    
    @Test
    public void testWorstCaseLength()
    {
        List<LorCardCount> deck = new ArrayList<>()
        {{
            add(new LorCardCount("01DE002", 4));
            add(new LorCardCount("01DE003", 4));
            add(new LorCardCount("01DE004", 4));
            add(new LorCardCount("01DE005", 4));
            add(new LorCardCount("01DE006", 4));
            add(new LorCardCount("01DE007", 5));
            add(new LorCardCount("01DE008", 6));
            add(new LorCardCount("01DE009", 7));
            add(new LorCardCount("01DE010", 8));
            add(new LorCardCount("01DE011", 9));
            add(new LorCardCount("01DE012", 4));
            add(new LorCardCount("01DE013", 4));
            add(new LorCardCount("01DE014", 4));
            add(new LorCardCount("01DE015", 4));
            add(new LorCardCount("01DE016", 4));
            add(new LorCardCount("01DE017", 4));
            add(new LorCardCount("01DE018", 4));
            add(new LorCardCount("01DE019", 4));
            add(new LorCardCount("01DE020", 4));
            add(new LorCardCount("01DE021", 4));
        }};
        
        String             code    = LoRDeck.encode(deck);
        List<LorCardCount> decoded = LoRDeck.decode(code);
        
        Assert.assertTrue("Did not produce same deck when re-coded", checkSameDeck(deck, decoded));
    }
    
    @Test
    public void testOrderDoesNotMatter()
    {
        List<LorCardCount> deck = new ArrayList<>()
        {{
            add(new LorCardCount("01DE002", 1));
            add(new LorCardCount("01DE003", 2));
            add(new LorCardCount("02DE003", 3));
        }};
        
        List<LorCardCount> deck2 = new ArrayList<>()
        {{
            add(new LorCardCount("01DE003", 2));
            add(new LorCardCount("02DE003", 3));
            add(new LorCardCount("01DE002", 1));
        }};
        
        String code  = LoRDeck.encode(deck);
        String code2 = LoRDeck.encode(deck2);
        
        Assert.assertEquals("Order matters?", code, code2);
    }
    
    @Test
    public void testOrderDoesNotMatterMoreThan3()
    {
        List<LorCardCount> deck = new ArrayList<>()
        {{
            add(new LorCardCount("01DE002", 4));
            add(new LorCardCount("01DE003", 2));
            add(new LorCardCount("02DE003", 3));
            add(new LorCardCount("01DE004", 5));
        }};
        
        List<LorCardCount> deck2 = new ArrayList<>()
        {{
            add(new LorCardCount("01DE004", 5));
            add(new LorCardCount("01DE003", 2));
            add(new LorCardCount("02DE003", 3));
            add(new LorCardCount("01DE002", 4));
        }};
        
        String code  = LoRDeck.encode(deck);
        String code2 = LoRDeck.encode(deck2);
        
        Assert.assertEquals("Order matters?", code, code2);
    }
    
    @Test
    public void testInvalidDecks()
    {
        List<LorCardCount> deck = new ArrayList<>()
        {
            {
                add(new LorCardCount("01DE02", 1));
            }
        };
        checkDeck(deck);
        
        deck = new ArrayList<>()
        {
            {
                add(new LorCardCount("01XX202", 1));
            }
        };
        checkDeck(deck);
        
        deck = new ArrayList<>()
        {
            {
                add(new LorCardCount("01DE002", 0));
            }
        };
        checkDeck(deck);
        
        deck = new ArrayList<>()
        {
            {
                add(new LorCardCount("01DE002", -1));
            }
        };
        checkDeck(deck);
    }
    
    @Test
    public void testInvalidCodes()
    {
        String badNot32 = "This is not a card code. Dont @me";
        String bad32    = "ABCDEFG";
        String empty    = "";
        
        checkCode(badNot32);
        checkCode(bad32);
        checkCode(empty);
    }
    
    private void checkCode(String code)
    {
        try
        {
            List<LorCardCount> deck = LoRDeck.decode(code);
            Assert.fail("Invalid code did not produce an error");
        } catch (IllegalArgumentException e)
        {
            // ok
        } catch (Exception e)
        {
            Assert.fail("Invalid code produced the wrong exception");
        }
    }
    
    
    private void checkDeck(List<LorCardCount> deck)
    {
        try
        {
            LoRDeck.encode(deck);
            Assert.fail("Invalid deck did not produce an error");
        } catch (IllegalArgumentException e)
        {
            // ok
        } catch (Exception e)
        {
            Assert.fail("Invalid deck produced the wrong exception");
        }
    }
    
    private boolean checkSameDeck(List<LorCardCount> a, List<LorCardCount> b)
    {
        if (a.size() != b.size())
        {
            return false;
        }
        
        for (LorCardCount bCard : b)
        {
            boolean found = false;
            for (LorCardCount aCard : a)
            {
                if (aCard.equals(bCard))
                {
                    found = true;
                    break;
                }
            }
            
            if (!found)
            {
                return false;
            }
            
        }
        return true;
    }
}
