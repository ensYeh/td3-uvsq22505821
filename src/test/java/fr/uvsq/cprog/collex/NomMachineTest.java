package fr.uvsq.cprog.collex;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for NomMachine class.
 */
public class NomMachineTest {
  
  @Test
  public void testValidMachineNames() {
    NomMachine nom1 = new NomMachine("www.uvsq.fr");
    assertEquals("www.uvsq.fr", nom1.getNomComplet());
    assertEquals("www", nom1.getNomMachine());
    assertEquals("uvsq.fr", nom1.getNomDomaine());
  }
  
  @Test
  public void testCaseInsensitive() {
    NomMachine nom = new NomMachine("WWW.UVSQ.FR");
    assertEquals("www.uvsq.fr", nom.getNomComplet());
    assertEquals("www", nom.getNomMachine());
    assertEquals("uvsq.fr", nom.getNomDomaine());
  }
  
  @Test
  public void testWithSpaces() {
    NomMachine nom = new NomMachine("  www.uvsq.fr  ");
    assertEquals("www.uvsq.fr", nom.getNomComplet());
  }
  
  @Test
  public void testMultipleDots() {
    NomMachine nom = new NomMachine("mail.server.company.com");
    assertEquals("mail.server.company.com", nom.getNomComplet());
    assertEquals("mail", nom.getNomMachine());
    assertEquals("server.company.com", nom.getNomDomaine());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testNullName() {
    new NomMachine(null);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testEmptyName() {
    new NomMachine("");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testBlankName() {
    new NomMachine("   ");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testNameWithoutDot() {
    new NomMachine("localhost");
  }
  
  @Test
  public void testEquals() {
    NomMachine nom1 = new NomMachine("www.uvsq.fr");
    NomMachine nom2 = new NomMachine("WWW.UVSQ.FR");
    NomMachine nom3 = new NomMachine("mail.uvsq.fr");
    
    assertEquals(nom1, nom2);
    assertNotEquals(nom1, nom3);
    assertNotEquals(nom1, null);
    assertNotEquals(nom1, "www.uvsq.fr");
  }
  
  @Test
  public void testHashCode() {
    NomMachine nom1 = new NomMachine("www.uvsq.fr");
    NomMachine nom2 = new NomMachine("WWW.UVSQ.FR");
    
    assertEquals(nom1.hashCode(), nom2.hashCode());
  }
  
  @Test
  public void testToString() {
    NomMachine nom = new NomMachine("www.uvsq.fr");
    assertEquals("www.uvsq.fr", nom.toString());
  }
  
  @Test
  public void testCompareTo() {
    NomMachine nom1 = new NomMachine("aaa.domain.com");
    NomMachine nom2 = new NomMachine("bbb.domain.com");
    NomMachine nom3 = new NomMachine("aaa.domain.com");
    
    assertTrue(nom1.compareTo(nom2) < 0);
    assertTrue(nom2.compareTo(nom1) > 0);
    assertEquals(0, nom1.compareTo(nom3));
    assertTrue(nom1.compareTo(null) > 0);
  }
}
