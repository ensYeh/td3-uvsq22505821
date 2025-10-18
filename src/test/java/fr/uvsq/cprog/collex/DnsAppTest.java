package fr.uvsq.cprog.collex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for DnsApp.
 */
public class DnsAppTest {
  
  private DnsApp app;
  private Dns dns;
  private ByteArrayOutputStream outputStream;
  private PrintStream originalOut;
  
  @Before
  public void setUp() throws Exception {
    // Setup test database file
    String testData = "www.test.fr 192.168.1.1\n" +
                     "mail.test.fr 192.168.1.2\n" +
                     "ftp.example.com 10.0.0.1\n";
    Files.write(Paths.get("dns_database.txt"), testData.getBytes());
    
    // Create DNS service
    dns = new Dns();
    
    // Capture output
    originalOut = System.out;
    outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));
  }
  
  @After
  public void tearDown() throws IOException {
    System.setOut(originalOut);
    Files.deleteIfExists(Paths.get("dns_database.txt"));
  }
  
  private DnsApp createAppWithInput(String input) {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
    Scanner scanner = new Scanner(inputStream);
    DnsTUI tui = new DnsTUI(dns, scanner);
    return new DnsApp(dns, tui);
  }
  
  @Test
  public void testConstructor() {
    DnsTUI tui = new DnsTUI(dns);
    DnsApp app = new DnsApp(dns, tui);
    assertNotNull(app);
  }
  
  @Test
  public void testRunWithQuitCommand() {
    app = createAppWithInput("quit\n");
    app.run();
    
    String output = outputStream.toString();
    assertTrue(output.contains("DNS Simulator"));
  }
  
  @Test
  public void testRunWithSearchCommand() {
    app = createAppWithInput("www.test.fr\nquit\n");
    app.run();
    
    String output = outputStream.toString();
    assertTrue(output.contains("192.168.1.1"));
  }
  
  @Test
  public void testRunWithInvalidCommand() {
    app = createAppWithInput("commande.invalide\nquit\n");
    app.run();
    
    String output = outputStream.toString();
    assertTrue(output.contains("DNS Simulator"));
  }
  
  @Test
  public void testRunWithListCommand() {
    app = createAppWithInput("ls test.fr\nquit\n");
    app.run();
    
    String output = outputStream.toString();
    assertTrue(output.contains("mail.test.fr"));
    assertTrue(output.contains("www.test.fr"));
  }
  
  @Test
  public void testRunWithAddCommand() {
    app = createAppWithInput("add 192.168.1.100 new.test.fr\nls test.fr\nquit\n");
    app.run();
    
    String output = outputStream.toString();
    assertTrue(output.contains("new.test.fr"));
  }
  
  @Test
  public void testRunWithHelpCommand() {
    app = createAppWithInput("aide\nquit\n");
    app.run();
    
    String output = outputStream.toString();
    assertTrue(output.contains("DNS Simulator"));
  }
  
  @Test
  public void testRunWithEmptyInput() {
    app = createAppWithInput("\nquit\n");
    app.run();
    
    String output = outputStream.toString();
    assertTrue(output.contains("ERREUR"));
  }
  
  @Test
  public void testRunWithReverseSearch() {
    app = createAppWithInput("192.168.1.1\nquit\n");
    app.run();
    
    String output = outputStream.toString();
    assertTrue(output.contains("www.test.fr"));
  }
  
  @Test
  public void testRunWithMultipleCommands() {
    app = createAppWithInput("www.test.fr\n192.168.1.2\nls test.fr\nquit\n");
    app.run();
    
    String output = outputStream.toString();
    assertTrue(output.contains("192.168.1.1"));
    assertTrue(output.contains("mail.test.fr"));
    assertTrue(output.contains("www.test.fr"));
  }
  
  @Test
  public void testRunWithDuplicateAddCommand() {
    app = createAppWithInput("add 192.168.1.1 duplicate.test.fr\nquit\n");
    app.run();
    
    String output = outputStream.toString();
    assertTrue(output.contains("ERREUR"));
    assertTrue(output.contains("existe déjà"));
  }
  
  @Test
  public void testMainMethod() {
    // Test that main method can be called without throwing exceptions
    // We'll redirect stdin to provide quit command
    ByteArrayInputStream inputStream = new ByteArrayInputStream("quit\n".getBytes());
    System.setIn(inputStream);
    
    try {
      DnsApp.main(new String[]{});
      // If we reach here, main executed successfully
      assertTrue(true);
    } catch (Exception e) {
      fail("Main method should not throw exceptions: " + e.getMessage());
    } finally {
      // Restore original System.in
      System.setIn(System.in);
    }
  }
}
