package com.google.jstestdriver.util;

import junit.framework.TestCase;

public class RetryTest extends TestCase {
  
  RetryableThatThrowsExceptionOnFirst2Tries retryable;
  
  @Override
  protected void setUp() throws Exception {
    retryable = new RetryableThatThrowsExceptionOnFirst2Tries();
  }

  public void testRetryablePassesOnRetry() {   
    // This shouldn't throw an exception since the Retryable should fail on the
    // first two tries but pass on the third try
    new Retry(3).retry(retryable);
    // Retryable should have been called 3 times
    assertEquals(3, retryable.count);
  }

  public void testExceptionIsThrownIfRetryableFailsOnAllTries() {
    // Exception should be thrown since the Retryable should fail on both tries
    try {
      new Retry(2).retry(retryable);
      fail("Expecting exception to be thrown");
    } catch (Exception e) {
      // Exception should be thrown
      assertTrue(e.getMessage().startsWith("Failed after 2 tries"));
    }
    
    // Retryable should have been called 2 times
    assertEquals(2, retryable.count);
  }
  
  private static class RetryableThatThrowsExceptionOnFirst2Tries implements Retry.Retryable<Void> {
    public int count = 0;

    public Void run() throws Exception {
      // Throw an exception the first two times this method is called
      if (++count <= 2) {
        throw new RuntimeException();
      }
      return null;
    }
  };
}