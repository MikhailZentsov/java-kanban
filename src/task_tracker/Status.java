package task_tracker;

import java.util.HashMap;
import java.util.Map;

enum Status {

    NEW,
    IN_PROGRESS,
    DONE;

    private static final Map<String, Status> LOOKUP_MAP = new HashMap<>();

    static {
        for (Status status : values()) {
            LOOKUP_MAP.put(status.name(), status);
        }
    }

    public static Status getStatusByName(String name) {
        if (!LOOKUP_MAP.containsKey(name)) return Status.NEW;
        return LOOKUP_MAP.get(name);
    }
}
