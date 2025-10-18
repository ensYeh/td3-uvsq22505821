package fr.uvsq.cprog.collex;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * Unit tests for DnsTUI class.
 */
public class DnsTUITest {
  
  private static final String TEST_DB_FILE = "test_tui_dns.txt";
  private Dns dns;
  private DnsTUI tui;
  private ByteArrayOutputStream outContent;
  private ByteArrayOutputStream errContent;
  private PrintStream originalOut;
  private PrintStream originalErr;
  
  @Before
  public void setUp() throws Exception {
    // Create test database
    String content = "www.uvsq.fr 193.51.31.90\n" +
                    "ecampus.uvsq.fr 193.51.25.12\n" +
                    "mail.google.com 172.217.20.5\n";
    Files.write(Paths.get(TEST_DB_FILE), content.getBytes());
    
    dns = new Dns(TEST_DB_FILE);
    
    // Capture output
    originalOut = System.out;
    originalErr = System.err;
    outContent = new ByteArrayOutputStream();
    errContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
  }
  
  @After
  public void tearDown() throws Exception {
    // Restore output
    System.setOut(originalOut);
    System.setErr(originalErr);
    
    // Clean up
    Files.deleteIfExists(Paths.get(TEST_DB_FILE));
    if (tui != null) {
      tui.fermer();
    }
  }
  
  @Test
  public void testNextCommandeSearchByIp() throws Exception {
    tui = createTuiWithInput("193.51.31.90");
    
    Commande cmd = tui.nextCommande();
    assertTrue(cmd instanceof CommandeRechercheParIp);
    
    String result = cmd.execute();
    assertEquals("www.uvsq.fr", result);
  }
  
  @Test
  public void testNextCommandeSearchByMachineName() throws Exception {
    tui = createTuiWithInput("www.uvsq.fr");
    
    Commande cmd = tui.nextCommande();
    assertTrue(cmd instanceof CommandeRechercheParNom);
    
    String result = cmd.execute();
    assertEquals("193.51.31.90", result);
  }
  
  @Test
  public void testNextCommandeListDomain() throws Exception {
    tui = createTuiWithInput("ls uvsq.fr");
    
    Commande cmd = tui.nextCommande();
    assertTrue(cmd instanceof CommandeListeDomaine);
    
    String result = cmd.execute();
    assertTrue(result.contains("www.uvsq.fr"));
    assertTrue(result.contains("ecampus.uvsq.fr"));
  }
  
  @Test
  public void testNextCommandeListDomainSortedByIp() throws Exception {
    tui = createTuiWithInput("ls -a uvsq.fr");
    
    Commande cmd = tui.nextCommande();
    assertTrue(cmd instanceof CommandeListeDomaine);
    
    String result = cmd.execute();
    String[] lines = result.split("\n");
    assertTrue(lines.length >= 2);
    // Should be sorted by IP
    assertTrue(lines[0].contains("193.51.25.12"));
    assertTrue(lines[1].contains("193.51.31.90"));
  }
  
  @Test
  public void testNextCommandeAddEntry() throws Exception {
    tui = createTuiWithInput("add 10.0.0.1 test.uvsq.fr");
    
    Commande cmd = tui.nextCommande();
    assertTrue(cmd instanceof CommandeAjouterEntree);
    
    String result = cmd.execute();
    assertTrue(result.contains("ajoutée avec succès"));
  }
  
  @Test
  public void testNextCommandeQuit() throws Exception {
    tui = createTuiWithInput("quit");
    
    Commande cmd = tui.nextCommande();
    assertTrue(cmd instanceof CommandeQuitter);
  }
  
  @Test
  public void testNextCommandeExit() throws Exception {
    tui = createTuiWithInput("exit");
    
    Commande cmd = tui.nextCommande();
    assertTrue(cmd instanceof CommandeQuitter);
  }
  
  @Test(expected = DnsException.class)
  public void testNextCommandeEmpty() throws Exception {
    tui = createTuiWithInput("\n");
    tui.nextCommande();
  }
  
  @Test(expected = DnsException.class)
  public void testNextCommandeInvalid() throws Exception {
    tui = createTuiWithInput("invalid command");
    tui.nextCommande();
  }
  
  @Test(expected = DnsException.class)
  public void testNextCommandeInvalidFormat() throws Exception {
    tui = createTuiWithInput("invalidformat");
    tui.nextCommande();
  }
  
  @Test(expected = DnsException.class)
  public void testListCommandMissingDomain() throws Exception {
    tui = createTuiWithInput("ls");
    tui.nextCommande();
  }
  
  @Test(expected = DnsException.class)
  public void testAddCommandWrongArguments() throws Exception {
    tui = createTuiWithInput("add 10.0.0.1");
    tui.nextCommande();
  }
  
  @Test
  public void testAffiche() {
    tui = createTuiWithInput("");
    tui.affiche("Test message");
    
    String output = outContent.toString();
    assertTrue(output.contains("Test message"));
  }
  
  @Test
  public void testAfficheEmpty() {
    tui = createTuiWithInput("");
    tui.affiche("");
    tui.affiche(null);
    
    String output = outContent.toString();
    // Should not contain any content from empty/null messages
    assertEquals("", output.trim());
  }
  
  @Test
  public void testAfficheErreur() {
    tui = createTuiWithInput("");
    tui.afficheErreur("Test error");
    
    String errorOutput = errContent.toString();
    assertTrue(errorOutput.contains("ERREUR: Test error"));
  }
  
  @Test
  public void testCaseInsensitiveCommands() throws Exception {
    tui = createTuiWithInput("QUIT");
    Commande cmd = tui.nextCommande();
    assertTrue(cmd instanceof CommandeQuitter);
    
    tui = createTuiWithInput("LS uvsq.fr");
    cmd = tui.nextCommande();
    assertTrue(cmd instanceof CommandeListeDomaine);
    
    tui = createTuiWithInput("ADD 10.0.0.2 test2.uvsq.fr");
    cmd = tui.nextCommande();
    assertTrue(cmd instanceof CommandeAjouterEntree);
  }
  
  private DnsTUI createTuiWithInput(String input) {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
    Scanner scanner = new Scanner(inputStream);
    return new DnsTUI(dns, scanner);
  }
}
