package fr.uvsq.cprog.collex;

/**
 * Command to display help information.
 */
public class CommandeAide implements Commande {
  
  @Override
  public String execute() throws DnsException {
    StringBuilder help = new StringBuilder();
    help.append("Commandes disponibles:\n");
    help.append("  aide                     - Affiche cette aide\n");
    help.append("  <adresse_ip>            - Recherche par adresse IP\n");
    help.append("  <nom_machine>           - Recherche par nom de machine\n");
    help.append("  ls <domaine>            - Liste les entrées d'un domaine\n");
    help.append("  ls -a <domaine>         - Liste les entrées d'un domaine triées par IP\n");
    help.append("  add <ip> <nom_machine>  - Ajoute une nouvelle entrée\n");
    help.append("  quit | exit             - Quitte l'application\n");
    return help.toString();
  }
}
