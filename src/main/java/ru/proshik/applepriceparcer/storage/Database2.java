package ru.proshik.applepriceparcer.storage;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import ru.proshik.applepriceparcer.exception.DatabaseException;
import ru.proshik.applepriceparcer.model2.Fetch;
import ru.proshik.applepriceparcer.model2.Shop;
import ru.proshik.applepriceparcer.model2.UserSubscriptions;
import ru.proshik.applepriceparcer.storage.serializer2.FetchListSerializer;
import ru.proshik.applepriceparcer.storage.serializer2.ShopSerializer;
import ru.proshik.applepriceparcer.storage.serializer2.UserSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database2 {

    private static final String SHOP_BUCKET = "shop";
    private static final String USER_SUBSCRIPTIONS_BUCKET = "userSubscriptions";

    private final String dbPath;

    public Database2(String dbPath) {
        this.dbPath = dbPath;
    }

    public List<Fetch> getFetches(Shop shop) throws DatabaseException {
        try (DB db = open()) {
            HTreeMap<Shop, List<Fetch>> map = createOrOpenShopBucket(db);
            return map.get(shop);
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    public void addFetch(Shop shop, Fetch fetch) throws DatabaseException {
        try (DB db = open()) {
            HTreeMap<Shop, List<Fetch>> map = createOrOpenShopBucket(db);
            List<Fetch> fetchList = map.get(shop);
            if (fetchList == null) {
                List<Fetch> a = new ArrayList<>();
                a.add(fetch);
                map.put(shop, a);
            } else {
                fetchList.add(fetch);
                map.put(shop, fetchList);
            }
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    public Map<String, UserSubscriptions> allSubscribers() throws DatabaseException {
        try (DB db = open()) {
            HTreeMap<String, UserSubscriptions> map = createOrOpenUserBucket(db);

            Map<String, UserSubscriptions> subscribers = new HashMap<>();
            for (HTreeMap.Entry<String, UserSubscriptions> entry : map.getEntries()) {
                subscribers.put(entry.getKey(), entry.getValue());
            }

            return subscribers;
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    public UserSubscriptions getUserSubscriptions(String userId) throws DatabaseException {
        try (DB db = open()) {
            HTreeMap<String, UserSubscriptions> map = createOrOpenUserBucket(db);
            UserSubscriptions userSubscriptions = map.get(userId);

            if (userSubscriptions == null) {
                return new UserSubscriptions();
            }

            return userSubscriptions;
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    public void addSubscription(String userId, Shop shop) throws DatabaseException {
        try (DB db = open()) {
            HTreeMap<String, UserSubscriptions> map = createOrOpenUserBucket(db);
            UserSubscriptions userSubscriptions = map.get(userId);

            if (userSubscriptions == null) {
                userSubscriptions = new UserSubscriptions();
            }

            userSubscriptions.getShops().add(shop);

            map.put(userId, userSubscriptions);
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    public boolean removeSubscription(String userId, Shop shop) throws DatabaseException {
        try (DB db = open()) {
            HTreeMap<String, UserSubscriptions> map = createOrOpenUserBucket(db);
            UserSubscriptions userSubscriptions = map.get(userId);

            if (userSubscriptions == null) {
                return false;
            }

            return userSubscriptions.getShops().remove(shop);
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    private DB open() {
        return DBMaker
                .fileDB(dbPath)
                .fileMmapEnable()
                .fileLockWait(3000)
                .make();
    }

    private HTreeMap<Shop, List<Fetch>> createOrOpenShopBucket(DB db) {
        return db.hashMap(SHOP_BUCKET)
                .keySerializer(new ShopSerializer())
                .valueSerializer(new FetchListSerializer())
                .createOrOpen();
    }

    private HTreeMap<String, UserSubscriptions> createOrOpenUserBucket(DB db) {
        return db.hashMap(USER_SUBSCRIPTIONS_BUCKET)
                .keySerializer(Serializer.STRING)
                .valueSerializer(new UserSerializer())
                .createOrOpen();
    }

}