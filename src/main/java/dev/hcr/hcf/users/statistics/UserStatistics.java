package dev.hcr.hcf.users.statistics;

import dev.hcr.hcf.users.User;

import java.util.HashMap;
import java.util.Map;

public class UserStatistics {
    private final User user;
    private final Map<String, Object> statisticKeyMapping;

    public UserStatistics(User user) {
        this.user = user;
        statisticKeyMapping = new HashMap<>();
    }

    public User getUser() {
        return user;
    }

    public void set(String key, Object value) {
        statisticKeyMapping.put(key, value);
    }

    public Map<String, Object> getStatisticKeyMapping() {
        return statisticKeyMapping;
    }
}
