/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.database.accessors;

import android.content.Context;
import android.text.TextUtils;
import cm.aptoide.pt.database.BuildConfig;
import cm.aptoide.pt.database.schedulers.RealmSchedulers;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import java.util.List;
import lombok.Cleanup;
import rx.Observable;

/**
 * Created by sithengineer on 16/05/16.
 *
 * This is the main class responsible to offer {@link Realm} database instances
 */
public final class Database {

  public static final int SCHEMA_VERSION = 8078; // if you bump this value, also add changes to the
  //private static final String TAG = Database.class.getName();
  private static final String KEY = "KRbjij20wgVyUFhMxm2gUHg0s1HwPUX7DLCp92VKMCt";
  private static final String DB_NAME = "aptoide.realm.db";
  private static final String DB_NAME_E = "aptoide_mobile.db";

  private static boolean isInitialized = false;

  //
  // Static methods
  //
  private static Realm INSTANCE;

  protected Database() {
  }

  private static String extract(String str) {
    return TextUtils.substring(str, str.lastIndexOf('.'), str.length());
  }

  public static void initialize(Context context) {
    if (isInitialized) return;

    //StringBuilder strBuilder = new StringBuilder(KEY);
    //strBuilder.append(extract(cm.aptoide.pt.model.BuildConfig.APPLICATION_ID));
    //strBuilder.append(extract(cm.aptoide.pt.utils.BuildConfig.APPLICATION_ID));
    //strBuilder.append(extract(BuildConfig.APPLICATION_ID));
    //strBuilder.append(extract(cm.aptoide.pt.preferences.BuildConfig.APPLICATION_ID));
    //byte[] key = strBuilder.toString().substring(0, 64).getBytes();

    // TODO
    // migration to an encrypted db
    //
    //if(isOldVersion()) {
    //  RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).name(DB_NAME_E)
    //      .encryptionKey(strBuilder.toString().substring(0, 64).getBytes())
    //      .schemaVersion(SCHEMA_VERSION)
    //      .migration(MIGRATION)
    //      .build();
    //  Realm instance = Realm.getInstance(realmConfig);
    //  try {
    //    instance.writeEncryptedCopyTo(new File(instance.getPath() + DB_NAME_E), key);
    //  } catch (IOException e) {
    //    e.printStackTrace();
    //  }
    //}

    // Beware this is the app context
    // So always use a unique name
    // Always use explicit modules in library projects
    RealmConfiguration realmConfig;
    if (BuildConfig.DEBUG) {
      //realmConfig = new RealmConfiguration.Builder(context).name(DB_NAME_E)
          //.encryptionKey(key)
      realmConfig = new RealmConfiguration.Builder(context).name(DB_NAME)
          .schemaVersion(SCHEMA_VERSION)
          .migration(new RealmToRealmDatabaseMigration())
          .build();
    } else {
      //realmConfig = new RealmConfiguration.Builder(context).name(DB_NAME_E)
          //.encryptionKey(key)
      realmConfig = new RealmConfiguration.Builder(context).name(DB_NAME)
          //.encryptionKey(strBuilder.toString().substring(0, 64).getBytes()) // FIXME: 30/08/16 sithengineer activate DB encryption
          .schemaVersion(SCHEMA_VERSION).migration(new RealmToRealmDatabaseMigration()).build();
    }

    //if (BuildConfig.DELETE_DB) {
    //  Realm.deleteRealm(realmConfig);
    //}
    Realm.setDefaultConfiguration(realmConfig);
    isInitialized = true;
  }

  protected static Realm get() {
    if (!isInitialized) {
      throw new IllegalStateException("You need to call Database.initialize(Context) first");
    }
    return Realm.getDefaultInstance();
  }

  /**
   * this code is expected to run on only a single thread, so no synchronizing primitives were used
   *
   * @return singleton Realm instance
   */
  private static Realm getInternal() {
    if (!isInitialized) {
      throw new IllegalStateException("You need to call Database.initialize(Context) first");
    }

    if (INSTANCE == null) {
      INSTANCE = Realm.getDefaultInstance();
    }

    return INSTANCE;
  }

  //
  // Instance methods
  //

  private Observable<Realm> getRealm() {
    return Observable.just(null)
        .observeOn(RealmSchedulers.getScheduler())
        .map(something -> Database.getInternal());
  }

  <E extends RealmObject> Observable<List<E>> copyFromRealm(RealmResults<E> results) {
    return Observable.just(results)
        .filter(data -> data.isLoaded())
        .map(realmObjects -> Database.getInternal().copyFromRealm(realmObjects));
  }

  <E extends RealmObject> Observable<E> copyFromRealm(E object) {
    return Observable.just(object)
        .filter(data -> data.isLoaded())
        .map(realmObject -> Database.getInternal().copyFromRealm(realmObject));
  }

  <E extends RealmObject> Observable<E> findFirst(RealmQuery<E> query) {
    return Observable.just(query.findFirst())
        .filter(realmObject -> realmObject != null)
        .flatMap(realmObject -> realmObject.<E>asObservable().unsubscribeOn(
            RealmSchedulers.getScheduler()))
        .flatMap(realmObject -> copyFromRealm(realmObject))
        .defaultIfEmpty(null);
  }

  <E extends RealmObject> Observable<List<E>> findAsList(RealmQuery<E> query) {
    return Observable.just(query.findAll())
        .filter(realmObject -> realmObject != null)
        .flatMap(realmObject -> realmObject.<E>asObservable().unsubscribeOn(
            RealmSchedulers.getScheduler()))
        .flatMap(realmObject -> copyFromRealm(realmObject))
        .defaultIfEmpty(null);
  }

  public Observable<Long> count(Class clazz) {
    return getRealm().flatMap(realm -> Observable.just(realm.where(clazz).count())
        .unsubscribeOn(RealmSchedulers.getScheduler()));
  }

  public <E extends RealmObject> Observable<List<E>> getAll(Class<E> clazz) {
    return getRealm().flatMap(
        realm -> realm.where(clazz).findAll().<List<E>>asObservable().unsubscribeOn(
            RealmSchedulers.getScheduler())).flatMap(results -> copyFromRealm(results));
  }

  public <E extends RealmObject> Observable<E> get(Class<E> clazz, String key, String value) {
    return getRealm().map(realm -> realm.where(clazz).equalTo(key, value))
        .flatMap(query -> findFirst(query));
  }

  public <E extends RealmObject> Observable<E> get(Class<E> clazz, String key, Integer value) {
    return getRealm().map(realm -> realm.where(clazz).equalTo(key, value))
        .flatMap(query -> findFirst(query));
  }

  public <E extends RealmObject> Observable<E> get(Class<E> clazz, String key, Long value) {
    return getRealm().map(realm -> realm.where(clazz).equalTo(key, value))
        .flatMap(query -> findFirst(query));
  }

  public <E extends RealmObject> Observable<List<E>> getAsList(Class<E> clazz, String key,
      String value) {
    return getRealm().map(realm -> realm.where(clazz).equalTo(key, value))
        .flatMap(query -> findAsList(query));
  }

  public <E extends RealmObject> Observable<List<E>> getAsList(Class<E> clazz, String key,
      Integer value) {
    return getRealm().map(realm -> realm.where(clazz).equalTo(key, value))
        .flatMap(query -> findAsList(query));
  }

  public <E extends RealmObject> Observable<List<E>> getAsList(Class<E> clazz, String key,
      Long value) {
    return getRealm().map(realm -> realm.where(clazz).equalTo(key, value))
        .flatMap(query -> findAsList(query));
  }

  public <E extends RealmObject> void delete(Class<E> clazz, String key, String value) {
    @Cleanup Realm realm = get();
    E first = realm.where(clazz).equalTo(key, value).findFirst();
    if (first != null) {
      realm.beginTransaction();
      first.deleteFromRealm();
      realm.commitTransaction();
    }
  }

  public <E extends RealmObject> void delete(Class<E> clazz, String key, Integer value) {
    @Cleanup Realm realm = get();
    E first = realm.where(clazz).equalTo(key, value).findFirst();
    if (first != null) {
      realm.beginTransaction();
      first.deleteFromRealm();
      realm.commitTransaction();
    }
  }

  public <E extends RealmObject> void delete(Class<E> clazz, String key, Long value) {
    @Cleanup Realm realm = get();
    E first = realm.where(clazz).equalTo(key, value).findFirst();
    if (first != null) {
      realm.beginTransaction();
      first.deleteFromRealm();
      realm.commitTransaction();
    }
  }

  public <E extends RealmObject> void deleteAll(Class<E> clazz) {
    @Cleanup Realm realm = get();
    realm.beginTransaction();
    realm.delete(clazz);
    realm.commitTransaction();
  }

  public <E extends RealmObject> void insertAll(List<E> objects) {
    @Cleanup Realm realm = get();
    realm.beginTransaction();
    realm.insertOrUpdate(objects);
    realm.commitTransaction();
  }

  public <E extends RealmObject> void insert(E object) {
    @Cleanup Realm realm = get();
    realm.beginTransaction();
    realm.insertOrUpdate(object);
    realm.commitTransaction();
  }
}
