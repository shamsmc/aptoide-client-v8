/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 18/08/2016.
 */

package cm.aptoide.pt.database.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class PaymentConfirmation extends RealmObject {

  public static final String PRODUCT_ID = "productId";
  public static final String PAYER_ID = "payerId";

  @PrimaryKey private int productId;
  @Required private String payerId;
  @Required private String status;

  private int paymentMethodId;

  private String paymentConfirmationId;

  public PaymentConfirmation() {
  }

  public PaymentConfirmation(String localMetadata, int productId, String status, String payerId,
      int paymentMethodId) {
    this.paymentConfirmationId = localMetadata;
    this.status = status;
    this.productId = productId;
    this.payerId = payerId;
    this.paymentMethodId = paymentMethodId;
  }

  public String getPayerId() {
    return payerId;
  }

  public String getLocalMetadata() {
    return paymentConfirmationId;
  }

  public int getProductId() {
    return productId;
  }

  public String getStatus() {
    return status;
  }

  public int getPaymentMethodId() {
    return paymentMethodId;
  }
}