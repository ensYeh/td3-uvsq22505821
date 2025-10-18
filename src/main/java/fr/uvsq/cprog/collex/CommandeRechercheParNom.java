package fr.uvsq.cprog.collex;

/**
 * Command to search for an IP address by machine name.
 */
public class CommandeRechercheParNom implements Commande {
  
  private final Dns dns;
  private final String nomMachine;
  
  /**
   * Creates a new search by machine name command.
   *
   * @param dns the DNS service
   * @param nomMachine the machine name to search for
   */
  public CommandeRechercheParNom(Dns dns, String nomMachine) {
    this.dns = dns;
    this.nomMachine = nomMachine;
  }
  
  @Override
  public String execute() throws DnsException {
    try {
      NomMachine nom = new NomMachine(nomMachine);
      DnsItem item = dns.getItem(nom);
      
      if (item == null) {
        return "Aucune adresse IP trouvée pour la machine: " + nomMachine;
      }
      
      return item.getAdresseIp().getAdresse();
    } catch (IllegalArgumentException e) {
      throw new DnsException("Nom de machine invalide: " + e.getMessage());
    }
  }
}
