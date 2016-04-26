/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/04/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppsUpdatesRequest;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppsVersionsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetStore;
import cm.aptoide.pt.model.v7.ListSearchApps;
import cm.aptoide.pt.model.v7.listapp.ListAppVersions;
import cm.aptoide.pt.model.v7.listapp.ListAppsUpdates;
import cm.aptoide.pt.networkclient.WebService;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by neuro on 19-04-2016.
 */
public abstract class V7<U> extends WebService<V7.Interfaces, U> {

	protected V7() {
		super(Interfaces.class);
	}

	@Override
	protected String getBaseHost() {
		return "http://ws75.aptoide.com/api/7/";
	}

	public interface Interfaces {

		@POST("getStore")
		Observable<GetStore> getStore(@Body GetStoreRequest.Body body);

		@POST("getApp")
		Observable<GetApp> getApp(@Body GetAppRequest.Body body);

		@POST("listAppsUpdates")
		Observable<ListAppsUpdates> listAppsUpdates(@Body ListAppsUpdatesRequest.Body body);

		@POST("listAppsVersions")
		Observable<ListAppVersions> listAppVersions(@Body ListAppsVersionsRequest.Body body);

		@POST("listSearchApps")
		Observable<ListSearchApps> listSearchApps(@Body ListSearchAppsRequest.Body body);

		// dummy, apagar
		@GET
		Observable<Object> testeGet(@Url String str);

	}
}
