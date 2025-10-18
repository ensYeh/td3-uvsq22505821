package fr.uvsq.cprog.collex;

/**
 * Exception thrown for DNS-related errors.
 */
public class DnsException extends Exception {
  
  /**
   * Creates a new DNS exception with a message.
   *
   * @param message the error message
   */
  public DnsException(String message) {
    super(message);
  }
  
  /**
   * Creates a new DNS exception with a message and cause.
   *
   * @param message the error message
   * @param cause the underlying cause
   */
  public DnsException(String message, Throwable cause) {
    super(message, cause);
  }
}
