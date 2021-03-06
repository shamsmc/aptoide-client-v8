package cm.aptoide.pt.v8engine.timeline.view.displayable;

import android.content.Context;
import cm.aptoide.pt.model.v7.timeline.AggregatedSocialInstall;
import cm.aptoide.pt.model.v7.timeline.MinimalCard;
import cm.aptoide.pt.model.v7.timeline.UserSharerTimeline;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.timeline.SocialRepository;
import cm.aptoide.pt.v8engine.timeline.TimelineAnalytics;
import cm.aptoide.pt.v8engine.timeline.view.ShareCardCallback;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import java.util.Date;
import java.util.List;

import static cm.aptoide.pt.v8engine.analytics.Analytics.AppsTimeline.BLANK;

/**
 * Created by jdandrade on 11/05/2017.
 */

public class AggregatedSocialInstallDisplayable extends CardDisplayable {

  public static final String CARD_TYPE_NAME = "AGGREGATED_SOCIAL_INSTALL";
  private List<MinimalCard> minimalCardList;
  private List<UserSharerTimeline> sharers;
  private SocialRepository socialRepository;
  private long appStoreId;
  private long appId;
  private String appIcon;
  private String appName;
  private String packageName;
  private float appRatingAverage;
  private DateCalculator dateCalculator;
  private Date date;
  private String abTestingURL;
  private TimelineAnalytics timelineAnalytics;

  public AggregatedSocialInstallDisplayable() {
  }

  public AggregatedSocialInstallDisplayable(AggregatedSocialInstall card, long appId,
      String packageName, String appName, String appIcon, String abTestingURL, Date date,
      TimelineAnalytics timelineAnalytics, SocialRepository socialRepository,
      DateCalculator dateCalculator) {
    super(card, timelineAnalytics);
    this.minimalCardList = card.getMinimalCardList();
    this.sharers = card.getSharers();
    this.socialRepository = socialRepository;
    this.timelineAnalytics = timelineAnalytics;
    this.appStoreId = card.getApp()
        .getStore()
        .getId();
    this.dateCalculator = dateCalculator;
    this.date = date;
    this.appIcon = appIcon;
    this.appName = appName;
    this.appRatingAverage = card.getApp()
        .getStats()
        .getRating()
        .getAvg();
    this.abTestingURL = abTestingURL;
    this.packageName = packageName;
    this.appId = appId;
  }

  public static Displayable from(AggregatedSocialInstall aggregatedSocialInstall,
      TimelineAnalytics timelineAnalytics, SocialRepository socialRepository,
      DateCalculator dateCalculator) {

    String abTestingURL = null;

    if (aggregatedSocialInstall.getAb() != null
        && aggregatedSocialInstall.getAb()
        .getConversion() != null
        && aggregatedSocialInstall.getAb()
        .getConversion()
        .getUrl() != null) {
      abTestingURL = aggregatedSocialInstall.getAb()
          .getConversion()
          .getUrl();
    }

    return new AggregatedSocialInstallDisplayable(aggregatedSocialInstall,
        aggregatedSocialInstall.getApp()
            .getId(), aggregatedSocialInstall.getApp()
        .getPackageName(), aggregatedSocialInstall.getApp()
        .getName(), aggregatedSocialInstall.getApp()
        .getIcon(), abTestingURL, aggregatedSocialInstall.getDate(), timelineAnalytics,
        socialRepository, dateCalculator);
  }

  @Override
  public void share(String cardId, boolean privacyResult, ShareCardCallback shareCardCallback) {
    socialRepository.share(getTimelineCard().getCardId(), getAppStoreId(), privacyResult,
        shareCardCallback,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, SHARE, getPackageName(), BLANK,
            BLANK));
  }

  @Override public void share(String cardId, ShareCardCallback shareCardCallback) {
    socialRepository.share(getTimelineCard().getCardId(), getAppStoreId(), shareCardCallback,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, SHARE, getPackageName(), BLANK,
            BLANK));
  }

  @Override public void like(Context context, String cardType, int rating) {
    socialRepository.like(getTimelineCard().getCardId(), cardType, "", rating,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, LIKE, getPackageName(), BLANK, BLANK));
  }

  @Override public void like(Context context, String cardId, String cardType, int rating) {
    socialRepository.like(cardId, cardType, "", rating,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, LIKE, getPackageName(), BLANK, BLANK));
  }

  public List<MinimalCard> getMinimalCardList() {
    return minimalCardList;
  }

  public List<UserSharerTimeline> getSharers() {
    return sharers;
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_social_timeline_aggregated_social_install;
  }

  public String getTimeSinceLastUpdate(Context context) {
    return dateCalculator.getTimeSinceDate(context, date);
  }

  public void sendOpenAppEvent() {
    timelineAnalytics.sendOpenAppEvent(CARD_TYPE_NAME, TimelineAnalytics.SOURCE_APTOIDE,
        getPackageName());
  }

  public String getTimeSinceLastUpdate(Context context, Date date) {
    return dateCalculator.getTimeSinceDate(context, date);
  }

  public String getAppIcon() {
    return appIcon;
  }

  public String getAppName() {
    return appName;
  }

  public float getAppRatingAverage() {
    return appRatingAverage;
  }

  public String getAbTestingURL() {
    return abTestingURL;
  }

  public String getPackageName() {
    return packageName;
  }

  public long getAppId() {
    return appId;
  }

  public long getAppStoreId() {
    return appStoreId;
  }

  public String getCardHeaderNames() {
    StringBuilder headerNamesStringBuilder = new StringBuilder();
    List<UserSharerTimeline> firstSharers = getSharers().subList(0, 2);
    for (UserSharerTimeline user : firstSharers) {
      headerNamesStringBuilder.append(user.getStore()
          .getName())
          .append(", ");
    }
    headerNamesStringBuilder.setLength(headerNamesStringBuilder.length() - 2);
    return headerNamesStringBuilder.toString();
  }
}
