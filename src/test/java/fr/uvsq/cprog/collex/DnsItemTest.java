package fr.uvsq.cprog.collex;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for DnsItem class.
 */
public class DnsItemTest {
  
  @Test
  public void testConstructorWithObjects() {
    AdresseIP ip = new AdresseIP("192.168.1.1");
    NomMachine nom = new NomMachine("www.uvsq.fr");
    
    DnsItem item = new DnsItem(ip, nom);
    
    assertEquals(ip, item.getAdresseIp());
    assertEquals(nom, item.getNomMachine());
    assertEquals("uvsq.fr", item.getDomaine());
  }
  
  @Test
  public void testConstructorWithStrings() {
    DnsItem item = new DnsItem("192.168.1.1", "www.uvsq.fr");
    
    assertEquals("192.168.1.1", item.getAdresseIp().getAdresse());
    assertEquals("www.uvsq.fr", item.getNomMachine().getNomComplet());
    assertEquals("uvsq.fr", item.getDomaine());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testNullIpAddress() {
    NomMachine nom = new NomMachine("www.uvsq.fr");
    new DnsItem(null, nom);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testNullMachineName() {
    AdresseIP ip = new AdresseIP("192.168.1.1");
    new DnsItem(ip, null);
  }
  
  @Test
  public void testAppartientAuDomaine() {
    DnsItem item = new DnsItem("192.168.1.1", "www.uvsq.fr");
    
    assertTrue(item.appartientAuDomaine("uvsq.fr"));
    assertTrue(item.appartientAuDomaine("UVSQ.FR"));
    assertTrue(item.appartientAuDomaine("  uvsq.fr  "));
    assertFalse(item.appartientAuDomaine("google.com"));
    assertFalse(item.appartientAuDomaine(null));
  }
  
  @Test
  public void testEquals() {
    DnsItem item1 = new DnsItem("192.168.1.1", "www.uvsq.fr");
    DnsItem item2 = new DnsItem("192.168.1.1", "www.uvsq.fr");
    DnsItem item3 = new DnsItem("192.168.1.2", "www.uvsq.fr");
    DnsItem item4 = new DnsItem("192.168.1.1", "mail.uvsq.fr");
    
    assertEquals(item1, item2);
    assertNotEquals(item1, item3);
    assertNotEquals(item1, item4);
    assertNotEquals(item1, null);
    assertNotEquals(item1, "not a dns item");
  }
  
  @Test
  public void testHashCode() {
    DnsItem item1 = new DnsItem("192.168.1.1", "www.uvsq.fr");
    DnsItem item2 = new DnsItem("192.168.1.1", "www.uvsq.fr");
    
    assertEquals(item1.hashCode(), item2.hashCode());
  }
  
  @Test
  public void testToString() {
    DnsItem item = new DnsItem("192.168.1.1", "www.uvsq.fr");
    assertEquals("192.168.1.1 www.uvsq.fr", item.toString());
  }
  
  @Test
  public void testToDisplayString() {
    DnsItem item = new DnsItem("192.168.1.1", "www.uvsq.fr");
    assertEquals("192.168.1.1 www.uvsq.fr", item.toDisplayString());
  }
  
  @Test
  public void testCompareTo() {
    DnsItem item1 = new DnsItem("192.168.1.1", "aaa.domain.com");
    DnsItem item2 = new DnsItem("192.168.1.2", "bbb.domain.com");
    DnsItem item3 = new DnsItem("192.168.1.2", "aaa.domain.com");
    DnsItem item4 = new DnsItem("192.168.1.1", "aaa.domain.com");
    
    // Compare by machine name first
    assertTrue(item1.compareTo(item2) < 0);
    assertTrue(item2.compareTo(item1) > 0);
    
    // Same machine name, compare by IP
    assertTrue(item1.compareTo(item3) < 0);
    
    // Identical items
    assertEquals(0, item1.compareTo(item4));
    
    // Null comparison
    assertTrue(item1.compareTo(null) > 0);
  }
}
