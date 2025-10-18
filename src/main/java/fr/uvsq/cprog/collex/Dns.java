package fr.uvsq.cprog.collex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * DNS service that manages DNS entries with file persistence.
 */
public class Dns {
  
  private final List<DnsItem> dnsItems;
  private final String databaseFilename;
  
  /**
   * Creates a new DNS service and loads the database from the properties file.
   *
   * @throws DnsException if the database cannot be loaded
   */
  public Dns() throws DnsException {
    this.dnsItems = new ArrayList<>();
    this.databaseFilename = loadDatabaseFilename();
    loadDatabase();
  }
  
  /**
   * Creates a DNS service with a specific database filename for testing.
   *
   * @param databaseFilename the database file name
   * @throws DnsException if the database cannot be loaded
   */
  public Dns(String databaseFilename) throws DnsException {
    this.dnsItems = new ArrayList<>();
    this.databaseFilename = databaseFilename;
    loadDatabase();
  }
  
  /**
   * Loads the database filename from the properties file.
   *
   * @return the database filename
   * @throws DnsException if properties cannot be loaded
   */
  private String loadDatabaseFilename() throws DnsException {
    try {
      Properties props = new Properties();
      props.load(getClass().getClassLoader().getResourceAsStream("dns.properties"));
      String filename = props.getProperty("database.filename");
      if (filename == null || filename.trim().isEmpty()) {
        throw new DnsException("Nom de fichier de base de données non configuré");
      }
      return filename.trim();
    } catch (IOException e) {
      throw new DnsException("Impossible de charger le fichier de propriétés: " + e.getMessage());
    } catch (NullPointerException e) {
      throw new DnsException("Fichier de propriétés dns.properties introuvable");
    }
  }
  
  /**
   * Loads the DNS database from the file.
   *
   * @throws DnsException if the database cannot be loaded
   */
  private void loadDatabase() throws DnsException {
    Path dbPath = Paths.get(databaseFilename);
    
    if (!Files.exists(dbPath)) {
      // Create empty file if it doesn't exist
      try {
        Files.createFile(dbPath);
        return;
      } catch (IOException e) {
        throw new DnsException("Impossible de créer le fichier de base de données: " + e.getMessage());
      }
    }
    
    try {
      List<String> lines = Files.readAllLines(dbPath);
      dnsItems.clear();
      
      for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
        String line = lines.get(lineNumber).trim();
        if (line.isEmpty() || line.startsWith("#")) {
          continue; // Skip empty lines and comments
        }
        
        String[] parts = line.split("\\s+");
        if (parts.length != 2) {
          throw new DnsException("Format invalide ligne " + (lineNumber + 1) + ": " + line);
        }
        
        try {
          DnsItem item = new DnsItem(parts[1], parts[0]); // IP, machine name
          dnsItems.add(item);
        } catch (IllegalArgumentException e) {
          throw new DnsException("Données invalides ligne " + (lineNumber + 1) + ": " + e.getMessage());
        }
      }
    } catch (IOException e) {
      throw new DnsException("Impossible de lire le fichier de base de données: " + e.getMessage());
    }
  }
  
  /**
   * Saves the current DNS database to the file.
   *
   * @throws DnsException if the database cannot be saved
   */
  private void saveDatabase() throws DnsException {
    try {
      List<String> lines = dnsItems.stream()
          .map(item -> item.getNomMachine().getNomComplet() + " " + item.getAdresseIp().getAdresse())
          .collect(Collectors.toList());
      
      Path dbPath = Paths.get(databaseFilename);
      Files.write(dbPath, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      throw new DnsException("Impossible de sauvegarder le fichier de base de données: " + e.getMessage());
    }
  }
  
  /**
   * Returns a DNS item by IP address.
   *
   * @param adresseIp the IP address to search for
   * @return the DNS item if found, null otherwise
   */
  public DnsItem getItem(AdresseIP adresseIp) {
    if (adresseIp == null) {
      return null;
    }
    
    return dnsItems.stream()
        .filter(item -> item.getAdresseIp().equals(adresseIp))
        .findFirst()
        .orElse(null);
  }
  
  /**
   * Returns a DNS item by machine name.
   *
   * @param nomMachine the machine name to search for
   * @return the DNS item if found, null otherwise
   */
  public DnsItem getItem(NomMachine nomMachine) {
    if (nomMachine == null) {
      return null;
    }
    
    return dnsItems.stream()
        .filter(item -> item.getNomMachine().equals(nomMachine))
        .findFirst()
        .orElse(null);
  }
  
  /**
   * Returns all DNS items for a specific domain.
   *
   * @param domaine the domain name
   * @return list of DNS items in the domain, sorted by machine name
   */
  public List<DnsItem> getItems(String domaine) {
    if (domaine == null || domaine.trim().isEmpty()) {
      return new ArrayList<>();
    }
    
    List<DnsItem> domainItems = dnsItems.stream()
        .filter(item -> item.appartientAuDomaine(domaine))
        .collect(Collectors.toList());
    
    Collections.sort(domainItems);
    return domainItems;
  }
  
  /**
   * Returns all DNS items for a specific domain, sorted by IP address.
   *
   * @param domaine the domain name
   * @return list of DNS items in the domain, sorted by IP address
   */
  public List<DnsItem> getItemsSortedByIp(String domaine) {
    if (domaine == null || domaine.trim().isEmpty()) {
      return new ArrayList<>();
    }
    
    List<DnsItem> domainItems = dnsItems.stream()
        .filter(item -> item.appartientAuDomaine(domaine))
        .collect(Collectors.toList());
    
    domainItems.sort((item1, item2) -> item1.getAdresseIp().compareTo(item2.getAdresseIp()));
    return domainItems;
  }
  
  /**
   * Adds a new DNS item to the database.
   *
   * @param adresseIp the IP address
   * @param nomMachine the machine name
   * @throws DnsException if the item cannot be added or already exists
   */
  public void addItem(AdresseIP adresseIp, NomMachine nomMachine) throws DnsException {
    if (adresseIp == null) {
      throw new DnsException("L'adresse IP ne peut pas être nulle");
    }
    if (nomMachine == null) {
      throw new DnsException("Le nom de machine ne peut pas être nul");
    }
    
    // Check if IP already exists
    if (getItem(adresseIp) != null) {
      throw new DnsException("ERREUR : L'adresse IP existe déjà !");
    }
    
    // Check if machine name already exists
    if (getItem(nomMachine) != null) {
      throw new DnsException("ERREUR : Le nom de machine existe déjà !");
    }
    
    DnsItem newItem = new DnsItem(adresseIp, nomMachine);
    dnsItems.add(newItem);
    saveDatabase();
  }
  
  /**
   * Adds a new DNS item to the database using string parameters.
   *
   * @param adresseIp the IP address string
   * @param nomMachine the machine name string
   * @throws DnsException if the item cannot be added or already exists
   */
  public void addItem(String adresseIp, String nomMachine) throws DnsException {
    try {
      AdresseIP ip = new AdresseIP(adresseIp);
      NomMachine nom = new NomMachine(nomMachine);
      addItem(ip, nom);
    } catch (IllegalArgumentException e) {
      throw new DnsException("Données invalides: " + e.getMessage());
    }
  }
  
  /**
   * Returns the number of DNS items in the database.
   *
   * @return the number of items
   */
  public int size() {
    return dnsItems.size();
  }
  
  /**
   * Returns all DNS items in the database.
   *
   * @return unmodifiable list of all DNS items
   */
  public List<DnsItem> getAllItems() {
    return Collections.unmodifiableList(new ArrayList<>(dnsItems));
  }
}
