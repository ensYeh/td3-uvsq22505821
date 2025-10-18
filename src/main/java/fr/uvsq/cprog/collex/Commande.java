package fr.uvsq.cprog.collex;

/**
 * Command interface following the Command pattern.
 * Encapsulates a request as an object to allow parameterization and queuing.
 */
public interface Commande {
  
  /**
   * Executes the command.
   *
   * @return the result of the command execution
   * @throws DnsException if an error occurs during execution
   */
  String execute() throws DnsException;
}
