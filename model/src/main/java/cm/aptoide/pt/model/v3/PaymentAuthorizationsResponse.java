/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 15/11/2016.
 */

package cm.aptoide.pt.model.v3;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by marcelobenites on 15/11/16.
 */
@Data @EqualsAndHashCode(callSuper = true) public class PaymentAuthorizationsResponse
    extends BaseV3Response {

  @JsonProperty("authorizations") private List<PaymentAuthorizationResponse> authorizations;

  @Data public static class PaymentAuthorizationResponse {

    @JsonProperty("paymentTypeId") private int paymentId;
    @JsonProperty("url") private String url;
    @JsonProperty("successUrl") private String successUrl;
    @JsonProperty("authorizationStatus") private String authorizationStatus;
  }
}