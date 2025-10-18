package fr.uvsq.cprog.collex;

/**
 * Command to add a new DNS entry.
 */
public class CommandeAjouterEntree implements Commande {
  
  private final Dns dns;
  private final String adresseIp;
  private final String nomMachine;
  
  /**
   * Creates a new add entry command.
   *
   * @param dns the DNS service
   * @param adresseIp the IP address
   * @param nomMachine the machine name
   */
  public CommandeAjouterEntree(Dns dns, String adresseIp, String nomMachine) {
    this.dns = dns;
    this.adresseIp = adresseIp;
    this.nomMachine = nomMachine;
  }
  
  @Override
  public String execute() throws DnsException {
    dns.addItem(adresseIp, nomMachine);
    return "Entrée ajoutée avec succès: " + adresseIp + " " + nomMachine;
  }
}
