package fr.uvsq.cprog.collex;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents an IP address with validation and comparison capabilities.
 */
public class AdresseIP implements Comparable<AdresseIP> {
  
  private static final Pattern IP_PATTERN = Pattern.compile(
      "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$");
  
  private final String adresse;
  
  /**
   * Creates a new IP address.
   * 
   * @param adresse the IP address string
   * @throws IllegalArgumentException if the IP address format is invalid
   */
  public AdresseIP(String adresse) {
    if (adresse == null || adresse.trim().isEmpty()) {
      throw new IllegalArgumentException("L'adresse IP ne peut pas être vide");
    }
    
    String trimmedAddress = adresse.trim();
    if (!IP_PATTERN.matcher(trimmedAddress).matches()) {
      throw new IllegalArgumentException("Format d'adresse IP invalide: " + trimmedAddress);
    }
    
    this.adresse = trimmedAddress;
  }
  
  /**
   * Returns the IP address string.
   * 
   * @return the IP address
   */
  public String getAdresse() {
    return adresse;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    AdresseIP other = (AdresseIP) obj;
    return Objects.equals(adresse, other.adresse);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(adresse);
  }
  
  @Override
  public String toString() {
    return adresse;
  }
  
  @Override
  public int compareTo(AdresseIP other) {
    if (other == null) {
      return 1;
    }
    
    String[] thisParts = this.adresse.split("\\.");
    String[] otherParts = other.adresse.split("\\.");
    
    for (int i = 0; i < 4; i++) {
      int thisOctet = Integer.parseInt(thisParts[i]);
      int otherOctet = Integer.parseInt(otherParts[i]);
      
      int comparison = Integer.compare(thisOctet, otherOctet);
      if (comparison != 0) {
        return comparison;
      }
    }
    
    return 0;
  }
}
