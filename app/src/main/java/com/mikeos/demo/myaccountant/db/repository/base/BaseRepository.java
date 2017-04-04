package com.mikeos.demo.myaccountant.db.repository.base;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;

import com.mikeos.demo.myaccountant.MyAcApplication;
import com.mikeos.demo.myaccountant.db.AppContentProvider;
import com.mikeos.demo.myaccountant.db.specs.RepositorySpecification;
import com.mikeos.demo.myaccountant.model.DbModel;

import nl.qbusict.cupboard.CupboardFactory;
import nl.qbusict.cupboard.ProviderCompartment;
import nl.qbusict.cupboard.QueryResultIterable;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;

/**
 * Created on 28.03.17.
 */

public class BaseRepository<T extends DbModel<T>> implements Repository<T> {

    @Override
    public Observable<T> query(long id, Class<T> tClass) {
        Uri uri = getUriForId(id, tClass);
        return MyAcApplication.getBriteProvider()
                .createQuery(uri, null, null, null, null, false).map(query -> {
                    Cursor cursor = query.run();
                    T model = DbModel.fromCursor(cursor, tClass);
                    if (cursor != null) {
                        cursor.close();
                    }
                    return model;
                });
    }

    @Override
    public Observable<T> get(long id, Class<T> tClass) {
        return wrap(() -> getProviderCompartment().query(getUriForId(id, tClass), tClass).get(), null);
    }

    @Override
    public Observable<QueryResultIterable<T>> getIterable(RepositorySpecification<T> specification, Class<T> tClass) {
        return Observable.create(subscriber -> {
            ProviderCompartment.QueryBuilder<T> builder = getProviderCompartment()
                    .query(AppContentProvider.getUriHelper().getUri(tClass), tClass);
            RepositorySpecification<T> s = specification != null ? specification : v -> v;
            QueryResultIterable<T> query = s.transformQuery(builder).query();
            subscriber.onNext(query);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<T> create(T item) {
        return wrap(() -> item, t -> {
            Uri put = getProviderCompartment().put(t.getUri(), t);
            t.setId(Long.valueOf(put.getLastPathSegment()));
        });
    }

    @Override
    public Observable<T> update(T item) {
        return wrap(() -> item, t -> getProviderCompartment().update(t.getUri(), t.buildContentValues()));
    }

    @Override
    public Observable<T> remove(T item) {
        return wrap(() -> item, t -> getProviderCompartment().delete(item.getUri(), item));
    }

    private Uri getUriForId(long id, Class<T> tClass) {
        return ContentUris.withAppendedId(AppContentProvider.getUriHelper().getUri(tClass), id);
    }

    private Observable<T> wrap(Func0<T> itemGetter, Action1<T> action) {
        return Observable.create(subscriber -> {
            T model = itemGetter.call();
            if (action != null) {
                action.call(model);
            }
            subscriber.onNext(model);
            subscriber.onCompleted();
        });
    }

    private ProviderCompartment getProviderCompartment() {
        return MyAcApplication.cupboard();
    }
}
