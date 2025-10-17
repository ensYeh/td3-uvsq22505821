package fr.uvsq.cprog.collex;

import java.util.Objects;

/**
 * Represents a DNS entry that associates an IP address with a machine name.
 */
public class DnsItem implements Comparable<DnsItem> {
  
  private final AdresseIP adresseIp;
  private final NomMachine nomMachine;
  
  /**
   * Creates a new DNS item.
   * 
   * @param adresseIp the IP address
   * @param nomMachine the machine name
   * @throws IllegalArgumentException if either parameter is null
   */
  public DnsItem(AdresseIP adresseIp, NomMachine nomMachine) {
    if (adresseIp == null) {
      throw new IllegalArgumentException("L'adresse IP ne peut pas être nulle");
    }
    if (nomMachine == null) {
      throw new IllegalArgumentException("Le nom de machine ne peut pas être nul");
    }
    
    this.adresseIp = adresseIp;
    this.nomMachine = nomMachine;
  }
  
  /**
   * Creates a new DNS item from string representations.
   * 
   * @param adresseIp the IP address string
   * @param nomMachine the machine name string
   * @throws IllegalArgumentException if parameters are invalid
   */
  public DnsItem(String adresseIp, String nomMachine) {
    this(new AdresseIP(adresseIp), new NomMachine(nomMachine));
  }
  
  /**
   * Returns the IP address.
   * 
   * @return the IP address
   */
  public AdresseIP getAdresseIp() {
    return adresseIp;
  }
  
  /**
   * Returns the machine name.
   * 
   * @return the machine name
   */
  public NomMachine getNomMachine() {
    return nomMachine;
  }
  
  /**
   * Returns the domain name.
   * 
   * @return the domain name
   */
  public String getDomaine() {
    return nomMachine.getNomDomaine();
  }
  
  /**
   * Checks if this DNS item belongs to the specified domain.
   * 
   * @param domaine the domain to check
   * @return true if this item belongs to the domain
   */
  public boolean appartientAuDomaine(String domaine) {
    if (domaine == null) {
      return false;
    }
    return getDomaine().equalsIgnoreCase(domaine.trim());
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    DnsItem other = (DnsItem) obj;
    return Objects.equals(adresseIp, other.adresseIp) 
        && Objects.equals(nomMachine, other.nomMachine);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(adresseIp, nomMachine);
  }
  
  @Override
  public String toString() {
    return adresseIp + " " + nomMachine;
  }
  
  /**
   * Returns a formatted string for display purposes.
   * 
   * @return formatted string with IP and machine name
   */
  public String toDisplayString() {
    return adresseIp.getAdresse() + " " + nomMachine.getNomComplet();
  }
  
  @Override
  public int compareTo(DnsItem other) {
    if (other == null) {
      return 1;
    }
    
    // Compare by machine name first
    int nameComparison = this.nomMachine.compareTo(other.nomMachine);
    if (nameComparison != 0) {
      return nameComparison;
    }
    
    // If names are equal, compare by IP address
    return this.adresseIp.compareTo(other.adresseIp);
  }
}
