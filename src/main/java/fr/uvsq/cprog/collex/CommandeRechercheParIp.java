package fr.uvsq.cprog.collex;

/**
 * Command to search for a machine name by IP address.
 */
public class CommandeRechercheParIp implements Commande {
  
  private final Dns dns;
  private final String adresseIp;
  
  /**
   * Creates a new search by IP command.
   *
   * @param dns the DNS service
   * @param adresseIp the IP address to search for
   */
  public CommandeRechercheParIp(Dns dns, String adresseIp) {
    this.dns = dns;
    this.adresseIp = adresseIp;
  }
  
  @Override
  public String execute() throws DnsException {
    try {
      AdresseIP ip = new AdresseIP(adresseIp);
      DnsItem item = dns.getItem(ip);
      
      if (item == null) {
        return "Aucune machine trouvée pour l'adresse IP: " + adresseIp;
      }
      
      return item.getNomMachine().getNomComplet();
    } catch (IllegalArgumentException e) {
      throw new DnsException("Adresse IP invalide: " + e.getMessage());
    }
  }
}
