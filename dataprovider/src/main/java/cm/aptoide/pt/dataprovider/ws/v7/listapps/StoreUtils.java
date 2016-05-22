/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 23/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.listapps;

import java.util.LinkedList;
import java.util.List;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.networkclient.interfaces.ErrorRequestListener;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import cm.aptoide.pt.utils.IconSizeUtils;
import io.realm.Realm;
import lombok.Cleanup;

/**
 * Created by neuro on 11-05-2016.
 */
@Deprecated
public class StoreUtils {

	@Deprecated
	public static List<Store> getSubscribedStores() {
		LinkedList<Store> stores = new LinkedList<>();

		stores.add(new Store().setAppearance(new Store.Appearance("default", "void"))
				.setName("apps")
				.setId(15)
				.setAvatar("http://pool.img.aptoide" +
						".com/apps/815872daa4e7a55f93cb3692aff65e31_ravatar" + ".jpg"));

		return stores;
	}

	public static void subscribeStore(GetStoreMetaRequest getStoreMetaRequest, SuccessRequestListener<GetStoreMeta>
			successRequestListener, ErrorRequestListener errorRequestListener) {
		getStoreMetaRequest.execute(getStoreMeta->{

			if (BaseV7Response.Info.Status.OK.equals(getStoreMeta.getInfo().getStatus())) {

				@Cleanup Realm realm = Database.get(DataProvider.getContext());

				cm.aptoide.pt.database.realm.Store store = new cm.aptoide.pt.database.realm.Store();

				Store storeData = getStoreMeta.getData();
				store.setStoreId(storeData.getId());
				store.setStoreName(storeData.getName());
				store.setDownloads(storeData.getStats().getDownloads());

				String sizeString = IconSizeUtils.generateSizeStringAvatar(DataProvider.getContext());

				String avatar = storeData.getAvatar();

				if (avatar != null) {
					String[] splittedUrl = avatar.split("\\.(?=[^\\.]+$)");
					avatar = splittedUrl[0] + "_" + sizeString + "." + splittedUrl[1];
				}

				store.setIconPath(avatar);
				store.setTheme(storeData.getAppearance().getTheme());

				if (isPrivateCredentialsSet(getStoreMetaRequest)) {
					store.setUsername(getStoreMetaRequest.getBody().getStoreUser());
					store.setPasswordSha1(getStoreMetaRequest.getBody().getStorePassSha1());
				}

				// TODO: 18-05-2016 neuro private ainda na ta
				if (AptoideAccountManager.isLoggedIn()) {
					AptoideAccountManager.subscribeStore(storeData.getName());
				}

				realm.beginTransaction();
				realm.copyToRealmOrUpdate(store);
				realm.commitTransaction();

				successRequestListener.onSuccess(getStoreMeta);
			}
		}, errorRequestListener::onError);
	}

	private static boolean isPrivateCredentialsSet(GetStoreMetaRequest getStoreMetaRequest) {
		return getStoreMetaRequest.getBody().getStoreUser() != null && getStoreMetaRequest.getBody()
				.getStorePassSha1() != null;
	}
}
