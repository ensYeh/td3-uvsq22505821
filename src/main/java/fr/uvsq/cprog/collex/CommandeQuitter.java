package fr.uvsq.cprog.collex;

/**
 * Command to quit the application.
 */
public class CommandeQuitter implements Commande {
  
  @Override
  public String execute() throws DnsException {
    return "quit";
  }
}
