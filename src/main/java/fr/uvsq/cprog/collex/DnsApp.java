package fr.uvsq.cprog.collex;

import java.util.Scanner;

/**
 * Main DNS application class that coordinates the user interface and command execution.
 * This class implements the main application loop, handling user input and displaying results.
 */
public class DnsApp {
  
  private final DnsTUI tui;
  private final Dns dns;
  
  /**
   * Creates a new DNS application with the specified DNS service and user interface.
   *
   * @param dns the DNS service for data operations
   * @param tui the text user interface for user interaction
   */
  public DnsApp(Dns dns, DnsTUI tui) {
    this.dns = dns;
    this.tui = tui;
  }
  
  /**
   * Runs the main application loop.
   * Continuously prompts for commands, executes them, and displays results until quit.
   */
  public void run() {
    System.out.println("DNS Simulator - Tapez 'aide' pour voir les commandes disponibles");
    
    boolean continuer = true;
    while (continuer) {
      try {
        Commande commande = tui.nextCommande();
        String resultat = commande.execute();
        
        if (resultat != null && !resultat.trim().isEmpty()) {
          tui.affiche(resultat);
        }
        
        // Check if it's a quit command
        if (commande instanceof CommandeQuitter) {
          continuer = false;
        }
        
      } catch (DnsException e) {
        tui.affiche("ERREUR : " + e.getMessage());
      } catch (Exception e) {
        tui.affiche("ERREUR inattendue : " + e.getMessage());
      }
    }
  }
  
  /**
   * Main entry point of the application.
   * Creates the DNS service, user interface, and starts the application.
   *
   * @param args command line arguments (not used)
   */
  public static void main(String[] args) {
    try {
      // Create DNS service and load database
      Dns dns = new Dns();
      
      // Create user interface
      DnsTUI tui = new DnsTUI(dns);
      
      // Create and run application
      DnsApp app = new DnsApp(dns, tui);
      app.run();
      
    } catch (Exception e) {
      System.err.println("Erreur lors du démarrage de l'application : " + e.getMessage());
      System.exit(1);
    }
  }
}
