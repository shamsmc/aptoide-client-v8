package cm.aptoide.pt.v8engine.billing.methods.braintree;

import cm.aptoide.pt.v8engine.billing.Transaction;

public class BraintreeTransaction extends Transaction {

  private final String token;

  public BraintreeTransaction(int productId, String payerId, Status status, int paymentMethodId,
      String token) {
    super(productId, payerId, status, paymentMethodId);
    this.token = token;
  }

  public String getToken() {
    return token;
  }
}
