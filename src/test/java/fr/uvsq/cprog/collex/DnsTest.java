package fr.uvsq.cprog.collex;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for Dns class.
 */
public class DnsTest {
  
  private static final String TEST_DB_FILE = "test_dns_database.txt";
  private Dns dns;
  
  @Before
  public void setUp() throws Exception {
    // Create test database file
    createTestDatabase();
    dns = new Dns(TEST_DB_FILE);
  }
  
  @After
  public void tearDown() throws Exception {
    // Clean up test file
    Path testPath = Paths.get(TEST_DB_FILE);
    if (Files.exists(testPath)) {
      Files.delete(testPath);
    }
  }
  
  private void createTestDatabase() throws IOException {
    String content = "www.uvsq.fr 193.51.31.90\n" +
                    "ecampus.uvsq.fr 193.51.25.12\n" +
                    "poste.uvsq.fr 193.51.31.154\n" +
                    "mail.google.com 172.217.20.5\n";
    Files.write(Paths.get(TEST_DB_FILE), content.getBytes());
  }
  
  @Test
  public void testLoadDatabase() throws Exception {
    assertEquals(4, dns.size());
  }
  
  @Test
  public void testGetItemByIp() throws Exception {
    AdresseIP ip = new AdresseIP("193.51.31.90");
    DnsItem item = dns.getItem(ip);
    
    assertNotNull(item);
    assertEquals("www.uvsq.fr", item.getNomMachine().getNomComplet());
    assertEquals("193.51.31.90", item.getAdresseIp().getAdresse());
  }
  
  @Test
  public void testGetItemByIpNotFound() throws Exception {
    AdresseIP ip = new AdresseIP("1.2.3.4");
    DnsItem item = dns.getItem(ip);
    
    assertNull(item);
  }
  
  @Test
  public void testGetItemByIpNull() throws Exception {
    DnsItem item = dns.getItem((AdresseIP) null);
    assertNull(item);
  }
  
  @Test
  public void testGetItemByMachineName() throws Exception {
    NomMachine nom = new NomMachine("www.uvsq.fr");
    DnsItem item = dns.getItem(nom);
    
    assertNotNull(item);
    assertEquals("www.uvsq.fr", item.getNomMachine().getNomComplet());
    assertEquals("193.51.31.90", item.getAdresseIp().getAdresse());
  }
  
  @Test
  public void testGetItemByMachineNameNotFound() throws Exception {
    NomMachine nom = new NomMachine("notfound.test.com");
    DnsItem item = dns.getItem(nom);
    
    assertNull(item);
  }
  
  @Test
  public void testGetItemByMachineNameNull() throws Exception {
    DnsItem item = dns.getItem((NomMachine) null);
    assertNull(item);
  }
  
  @Test
  public void testGetItemsByDomain() throws Exception {
    List<DnsItem> items = dns.getItems("uvsq.fr");
    
    assertEquals(3, items.size());
    // Should be sorted by machine name
    assertEquals("ecampus.uvsq.fr", items.get(0).getNomMachine().getNomComplet());
    assertEquals("poste.uvsq.fr", items.get(1).getNomMachine().getNomComplet());
    assertEquals("www.uvsq.fr", items.get(2).getNomMachine().getNomComplet());
  }
  
  @Test
  public void testGetItemsByDomainEmpty() throws Exception {
    List<DnsItem> items = dns.getItems("notfound.com");
    assertTrue(items.isEmpty());
  }
  
  @Test
  public void testGetItemsByDomainNull() throws Exception {
    List<DnsItem> items = dns.getItems(null);
    assertTrue(items.isEmpty());
  }
  
  @Test
  public void testGetItemsSortedByIp() throws Exception {
    List<DnsItem> items = dns.getItemsSortedByIp("uvsq.fr");
    
    assertEquals(3, items.size());
    // Should be sorted by IP address
    assertEquals("193.51.25.12", items.get(0).getAdresseIp().getAdresse());
    assertEquals("193.51.31.90", items.get(1).getAdresseIp().getAdresse());
    assertEquals("193.51.31.154", items.get(2).getAdresseIp().getAdresse());
  }
  
  @Test
  public void testAddItemSuccess() throws Exception {
    int initialSize = dns.size();
    
    dns.addItem("10.0.0.1", "test.uvsq.fr");
    
    assertEquals(initialSize + 1, dns.size());
    
    AdresseIP ip = new AdresseIP("10.0.0.1");
    DnsItem item = dns.getItem(ip);
    assertNotNull(item);
    assertEquals("test.uvsq.fr", item.getNomMachine().getNomComplet());
    
    // Verify it was saved to file
    Dns reloadedDns = new Dns(TEST_DB_FILE);
    assertEquals(initialSize + 1, reloadedDns.size());
    assertNotNull(reloadedDns.getItem(ip));
  }
  
  @Test
  public void testAddItemWithObjects() throws Exception {
    int initialSize = dns.size();
    
    AdresseIP ip = new AdresseIP("10.0.0.2");
    NomMachine nom = new NomMachine("test2.uvsq.fr");
    
    dns.addItem(ip, nom);
    
    assertEquals(initialSize + 1, dns.size());
    assertNotNull(dns.getItem(ip));
  }
  
  @Test(expected = DnsException.class)
  public void testAddItemDuplicateIp() throws Exception {
    dns.addItem("193.51.31.90", "duplicate.uvsq.fr");
  }
  
  @Test(expected = DnsException.class)
  public void testAddItemDuplicateName() throws Exception {
    dns.addItem("10.0.0.3", "www.uvsq.fr");
  }
  
  @Test(expected = DnsException.class)
  public void testAddItemNullIp() throws Exception {
    NomMachine nom = new NomMachine("test.uvsq.fr");
    dns.addItem(null, nom);
  }
  
  @Test(expected = DnsException.class)
  public void testAddItemNullName() throws Exception {
    AdresseIP ip = new AdresseIP("10.0.0.4");
    dns.addItem(ip, null);
  }
  
  @Test(expected = DnsException.class)
  public void testAddItemInvalidIp() throws Exception {
    dns.addItem("invalid.ip", "test.uvsq.fr");
  }
  
  @Test(expected = DnsException.class)
  public void testAddItemInvalidName() throws Exception {
    dns.addItem("10.0.0.5", "invalidname");
  }
  
  @Test
  public void testGetAllItems() throws Exception {
    List<DnsItem> allItems = dns.getAllItems();
    assertEquals(4, allItems.size());
    
    // Should be unmodifiable
    try {
      allItems.add(new DnsItem("1.1.1.1", "test.com"));
      fail("Should throw UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      // Expected
    }
  }
  
  @Test
  public void testEmptyDatabaseFile() throws Exception {
    // Create empty file
    String emptyFile = "empty_test.txt";
    Files.write(Paths.get(emptyFile), new byte[0]);
    
    try {
      Dns emptyDns = new Dns(emptyFile);
      assertEquals(0, emptyDns.size());
    } finally {
      Files.deleteIfExists(Paths.get(emptyFile));
    }
  }
  
  @Test
  public void testDatabaseWithComments() throws Exception {
    String commentFile = "comment_test.txt";
    String content = "# This is a comment\n" +
                    "www.test.com 1.2.3.4\n" +
                    "\n" +  // empty line
                    "# Another comment\n" +
                    "mail.test.com 1.2.3.5\n";
    Files.write(Paths.get(commentFile), content.getBytes());
    
    try {
      Dns commentDns = new Dns(commentFile);
      assertEquals(2, commentDns.size());
    } finally {
      Files.deleteIfExists(Paths.get(commentFile));
    }
  }
  
  @Test(expected = DnsException.class)
  public void testInvalidDatabaseFormat() throws Exception {
    String invalidFile = "invalid_test.txt";
    String content = "invalid line format\n";
    Files.write(Paths.get(invalidFile), content.getBytes());
    
    try {
      new Dns(invalidFile);
    } finally {
      Files.deleteIfExists(Paths.get(invalidFile));
    }
  }
}
