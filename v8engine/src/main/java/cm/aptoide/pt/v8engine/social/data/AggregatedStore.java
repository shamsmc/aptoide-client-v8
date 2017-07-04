package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.model.v7.timeline.MinimalCard;
import cm.aptoide.pt.v8engine.social.data.publisher.Poster;
import java.util.Date;
import java.util.List;

/**
 * Created by jdandrade on 30/06/2017.
 */

public class AggregatedStore extends StoreLatestApps {
  private final List<Poster> posters;
  private final List<MinimalCard> minimalCards;

  public AggregatedStore(String cardId, List<Poster> posters, List<MinimalCard> minimalCards,
      Long storeId, String storeName, String storeAvatar, String storeTheme, int subscribers,
      int appsNumber, Date latestUpdate, List<App> apps, String abUrl, CardType cardType) {
    super(cardId, storeId, storeName, storeAvatar, storeTheme, subscribers, appsNumber,
        latestUpdate, apps, abUrl, false, cardType);
    this.posters = posters;
    this.minimalCards = minimalCards;
  }

  public List<MinimalCard> getMinimalCards() {
    return minimalCards;
  }

  public List<Poster> getPosters() {
    return posters;
  }

  @Override public String getCardId() {
    throw new RuntimeException("Aggregated cards have NO card id");
  }
}
