package week22.regex;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static week22.regex.GaddafiSpellingMatcher.match;

public class GaddafiSpellingMatcherTest {

    @Test
    public void shouldMatchGadaffi() throws Exception {
        assertTrue(match("Gadaffi"));
    }

    @Test
    public void shouldMatchGadafi() throws Exception {
        assertTrue(match("Gadafi"));
    }

    @Test
    public void shouldMatchGadafy() throws Exception {
        assertTrue(match("Gadafy"));
    }

    @Test
    public void shouldMatchGaddafiAndGaddafy() throws Exception {
        assertTrue(match("Gaddafi"));
        assertTrue(match("Gaddafy"));
    }

    @Test
    public void shouldMatchGaddhafiAndGadhafi() throws Exception {
        assertTrue(match("Gaddhafi"));
        assertTrue(match("Gadhafi"));
    }

    @Test
    public void shouldMatchGathafi() throws Exception {
        assertTrue(match("Gathafi"));
    }

    @Test
    public void shouldMatchGhadaffi_Ghadafi_Ghaddafi_Ghaddafy() throws Exception {
        assertTrue(match("Ghadaffi"));
        assertTrue(match("Ghadafi"));
        assertTrue(match("Ghaddafi"));
        assertTrue(match("Ghaddafy"));
    }

    @Test
    public void shouldMatchGheddafi() throws Exception {
        assertTrue(match("Gheddafi"));
    }

    @Test
    public void shouldMatchSpellingsStartingWithK() throws Exception {
        assertTrue(match("Kadaffi"));
        assertTrue(match("Kadafi"));
        assertTrue(match("Kaddafi"));
        assertTrue(match("Kadhafi"));
        assertTrue(match("Khadaffy"));
        assertTrue(match("Khadafy"));
        assertTrue(match("Khaddafi"));
    }

    @Test
    public void shouldMatchKazzafi() throws Exception {
        assertTrue(match("Kazzafi"));
    }

    @Test
    public void shouldMatchStartingWithQ() throws Exception {
        assertTrue(match("Qadafi"));
        assertTrue(match("Qaddafi"));
        assertTrue(match("Qadhafi"));
        assertTrue(match("Qadthafi"));
        assertTrue(match("Qathafi"));
    }

    @Test
    public void shouldMatchQadhdhafi() throws Exception {
        assertTrue(match("Qadhdhafi"));
    }

    @Test
    public void shouldMatchQuathafi() throws Exception {
        assertTrue(match("Quathafi"));
    }

    @Test
    public void shouldMatchQudhafi() throws Exception {
        assertTrue(match("Qudhafi"));
    }

    @Test
    public void shouldMatchKadafiWithSingleQoute() throws Exception {
        assertTrue(match("Kad'afi"));
    }
}