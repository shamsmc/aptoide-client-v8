package cm.aptoide.pt.shareappsandroid;

import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by filipegoncalves on 09-02-2017.
 */

public class TransferRecordManager {

  private ApplicationsManager applicationsManager;

  public TransferRecordManager(ApplicationsManager applicationsManager) {
    this.applicationsManager = applicationsManager;
  }

  public void deleteAllApps(DeleteAppsListener listener, List<HighwayTransferRecordItem> listOfApps) {
    List<HighwayTransferRecordItem> toRemoveList = findAppsToRemove(listOfApps);
    if (toRemoveList != null) {
      listOfApps.remove(toRemoveList);
      listener.onDeleteAllApps(toRemoveList);
    }
  }

  private List<HighwayTransferRecordItem> findAppsToRemove(List<HighwayTransferRecordItem> listOfApps) {
    List<HighwayTransferRecordItem> toRemoveList = new ArrayList<>();
    for (int i = 0; i < listOfApps.size(); i++) {
      if (listOfApps.get(i).isSent() || listOfApps.get(i)
          .isReceived()) {//no isSending or need resend
        listOfApps.get(i).setDeleted(true);
        toRemoveList.add(listOfApps.get(i));
        if (listOfApps.get(i).isReceived()) {
          String tmpFilePath = listOfApps.get(i).getFilePath();
          System.out.println("GOing to delete this filepath : " + tmpFilePath);
          applicationsManager.deleteAppFile(tmpFilePath);
        }
      }
    }
    return toRemoveList;
  }

  public void deleteAppFile(String filePath) {
    applicationsManager.deleteAppFile(filePath);
  }

  public void installApp(String filePath) {
    applicationsManager.installApp(filePath);
  }

  public App convertTransferRecordItemToApp(HighwayTransferRecordItem item) {
    App app = applicationsManager.convertTransferRecordItemToApp(item);
    return app;
  }

  public HighwayTransferRecordItem readApkArchive(String appName, String filePath, boolean needReSend) {
    HighwayTransferRecordItem item = applicationsManager.readApkArchive(appName, filePath, needReSend);
    return item;
  }

  @Nullable public HighwayTransferRecordItem startedSending(String appName, String packageName,
      boolean needReSend, boolean isSent) {
    HighwayTransferRecordItem item =
        applicationsManager.startedSending(appName, packageName, needReSend, isSent);
    if (item != null) {
      return item;
    } else {
      return null;
    }
  }

  public interface DeleteAppsListener {

    void onDeleteAllApps(List<HighwayTransferRecordItem> toRemoveList);
  }
}
