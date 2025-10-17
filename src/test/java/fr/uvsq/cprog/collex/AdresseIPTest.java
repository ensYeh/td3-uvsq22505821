package fr.uvsq.cprog.collex;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for AdresseIP class.
 */
public class AdresseIPTest {
  
  @Test
  public void testValidIpAddresses() {
    AdresseIP ip1 = new AdresseIP("192.168.1.1");
    assertEquals("192.168.1.1", ip1.getAdresse());
    
    AdresseIP ip2 = new AdresseIP("0.0.0.0");
    assertEquals("0.0.0.0", ip2.getAdresse());
    
    AdresseIP ip3 = new AdresseIP("255.255.255.255");
    assertEquals("255.255.255.255", ip3.getAdresse());
  }
  
  @Test
  public void testValidIpWithSpaces() {
    AdresseIP ip = new AdresseIP("  192.168.1.1  ");
    assertEquals("192.168.1.1", ip.getAdresse());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testNullAddress() {
    new AdresseIP(null);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testEmptyAddress() {
    new AdresseIP("");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testBlankAddress() {
    new AdresseIP("   ");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidIpFormat() {
    new AdresseIP("192.168.1");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidOctet() {
    new AdresseIP("192.168.1.256");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testNonNumericOctet() {
    new AdresseIP("192.168.1.abc");
  }
  
  @Test
  public void testEquals() {
    AdresseIP ip1 = new AdresseIP("192.168.1.1");
    AdresseIP ip2 = new AdresseIP("192.168.1.1");
    AdresseIP ip3 = new AdresseIP("192.168.1.2");
    
    assertEquals(ip1, ip2);
    assertNotEquals(ip1, ip3);
    assertNotEquals(ip1, null);
    assertNotEquals(ip1, "192.168.1.1");
  }
  
  @Test
  public void testHashCode() {
    AdresseIP ip1 = new AdresseIP("192.168.1.1");
    AdresseIP ip2 = new AdresseIP("192.168.1.1");
    
    assertEquals(ip1.hashCode(), ip2.hashCode());
  }
  
  @Test
  public void testToString() {
    AdresseIP ip = new AdresseIP("192.168.1.1");
    assertEquals("192.168.1.1", ip.toString());
  }
  
  @Test
  public void testCompareTo() {
    AdresseIP ip1 = new AdresseIP("192.168.1.1");
    AdresseIP ip2 = new AdresseIP("192.168.1.2");
    AdresseIP ip3 = new AdresseIP("192.168.2.1");
    AdresseIP ip4 = new AdresseIP("192.168.1.1");
    
    assertTrue(ip1.compareTo(ip2) < 0);
    assertTrue(ip2.compareTo(ip1) > 0);
    assertTrue(ip1.compareTo(ip3) < 0);
    assertEquals(0, ip1.compareTo(ip4));
    assertTrue(ip1.compareTo(null) > 0);
  }
}
