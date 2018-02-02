package ru.proshik.applepriceparcer.service;

import ru.proshik.applepriceparcer.exception.DatabaseException;
import ru.proshik.applepriceparcer.model2.Shop;
import ru.proshik.applepriceparcer.model2.UserSubscriptions;
import ru.proshik.applepriceparcer.storage.Database2;

import java.util.*;

public class SubscriberService {

    private Database2 db;

    public SubscriberService(Database2 db) {
        this.db = db;
    }

    public Map<Shop, List<String>> subscriptionsSubscribers() throws DatabaseException {
        Map<Shop, List<String>> result = new HashMap<>();

        Map<String, UserSubscriptions> subscribers = db.allSubscribers();
        for (Map.Entry<String, UserSubscriptions> entry : subscribers.entrySet()) {
            for (Shop shop : entry.getValue().getShops()) {
                result.computeIfAbsent(shop, p -> new ArrayList<>()).add(entry.getKey());
            }
        }

        return result;
    }

    public Set<Shop> userSubscriptions(String userId) throws DatabaseException {
        UserSubscriptions userSubscriptions = db.getUserSubscriptions(userId);

        if (userSubscriptions != null) {
            return userSubscriptions.getShops();
        } else {
            return Collections.emptySet();
        }
    }

    public void addSubscription(String userId, Shop shop) throws DatabaseException {
        db.addSubscription(userId, shop);
    }

    public boolean removeSubscription(String userId, Shop shop) throws DatabaseException {
        return db.removeSubscription(userId, shop);
    }
}
