package fr.uvsq.cprog.collex;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Text User Interface for DNS operations.
 * Handles user input parsing and command creation.
 */
public class DnsTUI {
  
  private static final Pattern IP_PATTERN = Pattern.compile(
      "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$");
  
  private final Scanner scanner;
  private final Dns dns;
  
  /**
   * Creates a new DNS TUI.
   *
   * @param dns the DNS service
   */
  public DnsTUI(Dns dns) {
    this.dns = dns;
    this.scanner = new Scanner(System.in);
  }
  
  /**
   * Creates a DNS TUI with custom scanner for testing.
   *
   * @param dns the DNS service
   * @param scanner the scanner to use for input
   */
  public DnsTUI(Dns dns, Scanner scanner) {
    this.dns = dns;
    this.scanner = scanner;
  }
  
  /**
   * Reads the next command from user input and creates the appropriate command object.
   *
   * @return the command object to execute
   * @throws DnsException if the command cannot be parsed
   */
  public Commande nextCommande() throws DnsException {
    System.out.print("> ");
    
    if (!scanner.hasNextLine()) {
      return new CommandeQuitter();
    }
    
    String input = scanner.nextLine().trim();
    
    if (input.isEmpty()) {
      throw new DnsException("Commande vide");
    }
    
    return parseCommande(input);
  }
  
  /**
   * Parses a command string and creates the appropriate command object.
   *
   * @param input the command string
   * @return the command object
   * @throws DnsException if the command cannot be parsed
   */
  private Commande parseCommande(String input) throws DnsException {
    String[] parts = input.split("\\s+");
    
    // Quit commands
    if (parts[0].equalsIgnoreCase("quit") || parts[0].equalsIgnoreCase("exit")) {
      return new CommandeQuitter();
    }
    
    // Help command
    if (parts[0].equalsIgnoreCase("aide") || parts[0].equalsIgnoreCase("help")) {
      return new CommandeAide();
    }
    
    // List command: ls [-a] domain
    if (parts[0].equalsIgnoreCase("ls")) {
      return parseListCommand(parts);
    }
    
    // Add command: add ip machine_name
    if (parts[0].equalsIgnoreCase("add")) {
      return parseAddCommand(parts);
    }
    
    // Single argument commands (IP address or machine name)
    if (parts.length == 1) {
      return parseSingleArgumentCommand(parts[0]);
    }
    
    throw new DnsException("Commande non reconnue: " + input);
  }
  
  /**
   * Parses a list command.
   *
   * @param parts the command parts
   * @return the list command
   * @throws DnsException if the command is invalid
   */
  private Commande parseListCommand(String[] parts) throws DnsException {
    if (parts.length < 2) {
      throw new DnsException("Usage: ls [-a] <domaine>");
    }
    
    boolean sortByIp = false;
    String domain;
    
    if (parts.length == 3 && parts[1].equals("-a")) {
      sortByIp = true;
      domain = parts[2];
    } else if (parts.length == 2) {
      domain = parts[1];
    } else {
      throw new DnsException("Usage: ls [-a] <domaine>");
    }
    
    return new CommandeListeDomaine(dns, domain, sortByIp);
  }
  
  /**
   * Parses an add command.
   *
   * @param parts the command parts
   * @return the add command
   * @throws DnsException if the command is invalid
   */
  private Commande parseAddCommand(String[] parts) throws DnsException {
    if (parts.length != 3) {
      throw new DnsException("Usage: add <adresse_ip> <nom_machine>");
    }
    
    return new CommandeAjouterEntree(dns, parts[1], parts[2]);
  }
  
  /**
   * Parses a single argument command (IP or machine name lookup).
   *
   * @param argument the argument
   * @return the appropriate search command
   * @throws DnsException if the argument is invalid
   */
  private Commande parseSingleArgumentCommand(String argument) throws DnsException {
    if (isIpAddress(argument)) {
      return new CommandeRechercheParIp(dns, argument);
    } else if (isMachineName(argument)) {
      return new CommandeRechercheParNom(dns, argument);
    } else {
      throw new DnsException("Format invalide: " + argument 
          + " (doit être une adresse IP ou un nom de machine qualifié)");
    }
  }
  
  /**
   * Checks if a string is a valid IP address format.
   *
   * @param str the string to check
   * @return true if it's an IP address format
   */
  private boolean isIpAddress(String str) {
    return IP_PATTERN.matcher(str).matches();
  }
  
  /**
   * Checks if a string is a valid machine name format.
   *
   * @param str the string to check
   * @return true if it's a machine name format
   */
  private boolean isMachineName(String str) {
    return str.contains(".") && !isIpAddress(str);
  }
  
  /**
   * Displays a result to the user.
   *
   * @param result the result to display
   */
  public void affiche(String result) {
    if (result != null && !result.isEmpty()) {
      System.out.println(result);
    }
  }
  
  /**
   * Displays an error message to the user.
   *
   * @param error the error message
   */
  public void afficheErreur(String error) {
    System.err.println("ERREUR: " + error);
  }
  
  /**
   * Closes the scanner.
   */
  public void fermer() {
    scanner.close();
  }
}
