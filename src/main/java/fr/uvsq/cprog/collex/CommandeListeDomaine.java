package fr.uvsq.cprog.collex;

import java.util.List;

/**
 * Command to list all machines in a domain.
 */
public class CommandeListeDomaine implements Commande {
  
  private final Dns dns;
  private final String domaine;
  private final boolean trierParIp;
  
  /**
   * Creates a new list domain command.
   *
   * @param dns the DNS service
   * @param domaine the domain name
   * @param trierParIp true to sort by IP address, false to sort by machine name
   */
  public CommandeListeDomaine(Dns dns, String domaine, boolean trierParIp) {
    this.dns = dns;
    this.domaine = domaine;
    this.trierParIp = trierParIp;
  }
  
  @Override
  public String execute() throws DnsException {
    if (domaine == null || domaine.trim().isEmpty()) {
      throw new DnsException("Le nom de domaine ne peut pas être vide");
    }
    
    List<DnsItem> items;
    if (trierParIp) {
      items = dns.getItemsSortedByIp(domaine);
    } else {
      items = dns.getItems(domaine);
    }
    
    if (items.isEmpty()) {
      return "Aucune machine trouvée dans le domaine: " + domaine;
    }
    
    StringBuilder result = new StringBuilder();
    for (DnsItem item : items) {
      result.append(item.toDisplayString()).append("\n");
    }
    
    // Remove the last newline
    if (result.length() > 0) {
      result.setLength(result.length() - 1);
    }
    
    return result.toString();
  }
}
