package fr.uvsq.cprog.collex;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * Unit tests for command classes.
 */
public class CommandeTest {
  
  private static final String TEST_DB_FILE = "test_commande_dns.txt";
  private Dns dns;
  
  @Before
  public void setUp() throws Exception {
    // Create test database
    String content = "www.uvsq.fr 193.51.31.90\n" +
                    "ecampus.uvsq.fr 193.51.25.12\n" +
                    "poste.uvsq.fr 193.51.31.154\n" +
                    "mail.google.com 172.217.20.5\n";
    Files.write(Paths.get(TEST_DB_FILE), content.getBytes());
    
    dns = new Dns(TEST_DB_FILE);
  }
  
  @After
  public void tearDown() throws Exception {
    Files.deleteIfExists(Paths.get(TEST_DB_FILE));
  }
  
  @Test
  public void testCommandeRechercheParIpSuccess() throws Exception {
    Commande cmd = new CommandeRechercheParIp(dns, "193.51.31.90");
    String result = cmd.execute();
    assertEquals("www.uvsq.fr", result);
  }
  
  @Test
  public void testCommandeRechercheParIpNotFound() throws Exception {
    Commande cmd = new CommandeRechercheParIp(dns, "1.2.3.4");
    String result = cmd.execute();
    assertTrue(result.contains("Aucune machine trouvée"));
  }
  
  @Test(expected = DnsException.class)
  public void testCommandeRechercheParIpInvalid() throws Exception {
    Commande cmd = new CommandeRechercheParIp(dns, "invalid.ip");
    cmd.execute();
  }
  
  @Test
  public void testCommandeRechercheParNomSuccess() throws Exception {
    Commande cmd = new CommandeRechercheParNom(dns, "www.uvsq.fr");
    String result = cmd.execute();
    assertEquals("193.51.31.90", result);
  }
  
  @Test
  public void testCommandeRechercheParNomNotFound() throws Exception {
    Commande cmd = new CommandeRechercheParNom(dns, "notfound.test.com");
    String result = cmd.execute();
    assertTrue(result.contains("Aucune adresse IP trouvée"));
  }
  
  @Test(expected = DnsException.class)
  public void testCommandeRechercheParNomInvalid() throws Exception {
    Commande cmd = new CommandeRechercheParNom(dns, "invalidname");
    cmd.execute();
  }
  
  @Test
  public void testCommandeListeDomaineSuccess() throws Exception {
    Commande cmd = new CommandeListeDomaine(dns, "uvsq.fr", false);
    String result = cmd.execute();
    
    assertTrue(result.contains("www.uvsq.fr"));
    assertTrue(result.contains("ecampus.uvsq.fr"));
    assertTrue(result.contains("poste.uvsq.fr"));
    assertFalse(result.contains("mail.google.com"));
    
    // Check ordering (by machine name)
    String[] lines = result.split("\n");
    assertTrue(lines[0].contains("ecampus.uvsq.fr")); // alphabetically first
  }
  
  @Test
  public void testCommandeListeDomaineSortedByIp() throws Exception {
    Commande cmd = new CommandeListeDomaine(dns, "uvsq.fr", true);
    String result = cmd.execute();
    
    // Check ordering (by IP address)
    String[] lines = result.split("\n");
    assertTrue(lines[0].contains("193.51.25.12")); // ecampus
    assertTrue(lines[1].contains("193.51.31.90"));  // www
    assertTrue(lines[2].contains("193.51.31.154")); // poste
  }
  
  @Test
  public void testCommandeListeDomaineNotFound() throws Exception {
    Commande cmd = new CommandeListeDomaine(dns, "notfound.com", false);
    String result = cmd.execute();
    assertTrue(result.contains("Aucune machine trouvée"));
  }
  
  @Test(expected = DnsException.class)
  public void testCommandeListeDomaineEmpty() throws Exception {
    Commande cmd = new CommandeListeDomaine(dns, "", false);
    cmd.execute();
  }
  
  @Test(expected = DnsException.class)
  public void testCommandeListeDomaineNull() throws Exception {
    Commande cmd = new CommandeListeDomaine(dns, null, false);
    cmd.execute();
  }
  
  @Test
  public void testCommandeAjouterEntreeSuccess() throws Exception {
    int initialSize = dns.size();
    
    Commande cmd = new CommandeAjouterEntree(dns, "10.0.0.1", "test.uvsq.fr");
    String result = cmd.execute();
    
    assertTrue(result.contains("ajoutée avec succès"));
    assertEquals(initialSize + 1, dns.size());
  }
  
  @Test(expected = DnsException.class)
  public void testCommandeAjouterEntreeDuplicateIp() throws Exception {
    Commande cmd = new CommandeAjouterEntree(dns, "193.51.31.90", "duplicate.uvsq.fr");
    cmd.execute();
  }
  
  @Test(expected = DnsException.class)
  public void testCommandeAjouterEntreeDuplicateName() throws Exception {
    Commande cmd = new CommandeAjouterEntree(dns, "10.0.0.2", "www.uvsq.fr");
    cmd.execute();
  }
  
  @Test(expected = DnsException.class)
  public void testCommandeAjouterEntreeInvalidIp() throws Exception {
    Commande cmd = new CommandeAjouterEntree(dns, "invalid.ip", "test.uvsq.fr");
    cmd.execute();
  }
  
  @Test(expected = DnsException.class)
  public void testCommandeAjouterEntreeInvalidName() throws Exception {
    Commande cmd = new CommandeAjouterEntree(dns, "10.0.0.3", "invalidname");
    cmd.execute();
  }
  
  @Test
  public void testCommandeQuitter() throws Exception {
    Commande cmd = new CommandeQuitter();
    String result = cmd.execute();
    assertEquals("quit", result);
  }
}
