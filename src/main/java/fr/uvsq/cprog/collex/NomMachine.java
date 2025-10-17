package fr.uvsq.cprog.collex;

import java.util.Objects;

/**
 * Represents a qualified machine name with domain separation capabilities.
 */
public class NomMachine implements Comparable<NomMachine> {
  
  private final String nomComplet;
  
  /**
   * Creates a new machine name.
   * 
   * @param nomComplet the fully qualified machine name
   * @throws IllegalArgumentException if the name is invalid
   */
  public NomMachine(String nomComplet) {
    if (nomComplet == null || nomComplet.trim().isEmpty()) {
      throw new IllegalArgumentException("Le nom de machine ne peut pas être vide");
    }
    
    String trimmedName = nomComplet.trim();
    if (!trimmedName.contains(".")) {
      throw new IllegalArgumentException("Le nom doit être qualifié (contenir au moins un point)");
    }
    
    this.nomComplet = trimmedName.toLowerCase();
  }
  
  /**
   * Returns the complete qualified name.
   * 
   * @return the full machine name
   */
  public String getNomComplet() {
    return nomComplet;
  }
  
  /**
   * Returns the machine name part (before the first dot).
   * 
   * @return the machine name
   */
  public String getNomMachine() {
    int firstDot = nomComplet.indexOf('.');
    return nomComplet.substring(0, firstDot);
  }
  
  /**
   * Returns the domain name part (after the first dot).
   * 
   * @return the domain name
   */
  public String getNomDomaine() {
    int firstDot = nomComplet.indexOf('.');
    return nomComplet.substring(firstDot + 1);
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    NomMachine other = (NomMachine) obj;
    return Objects.equals(nomComplet, other.nomComplet);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(nomComplet);
  }
  
  @Override
  public String toString() {
    return nomComplet;
  }
  
  @Override
  public int compareTo(NomMachine other) {
    if (other == null) {
      return 1;
    }
    return this.nomComplet.compareTo(other.nomComplet);
  }
}
